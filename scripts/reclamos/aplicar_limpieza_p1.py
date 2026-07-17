#!/usr/bin/env python3
from pathlib import Path
from typing import Tuple

ROOT = Path(__file__).resolve().parents[2]

ACTION = ROOT / "ext-impl/src/ar/com/ospim/autorizaciones/action/EditarReclamosEntryAction.java"
SERVICE = ROOT / "ext-impl/src/ar/com/ospim/autorizaciones/services/ReclamosPrestacionesServiceUtil.java"
EDITOR = ROOT / "ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp"


def require(condition: bool, message: str) -> None:
    if not condition:
        raise RuntimeError(message)


def read_source(path: Path) -> Tuple[str, str]:
    raw = path.read_bytes()
    for encoding in ("utf-8", "iso-8859-1"):
        try:
            return raw.decode(encoding), encoding
        except UnicodeDecodeError:
            continue
    raise RuntimeError(f"No se pudo detectar la codificación de {path}")


def write_source(path: Path, text: str, encoding: str) -> None:
    path.write_bytes(text.encode(encoding))


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    require(count == 1, f"{label}: se esperó 1 coincidencia y se encontraron {count}")
    return text.replace(old, new, 1)


def update_action() -> None:
    text, encoding = read_source(ACTION)

    start_marker = "\t\t\tif(cmd.equals(Constants.DELETE)){"
    end_marker = "\t\t\t\t  BusquedaReclamosPrestacionalesFiltro filtro = null ;"
    start = text.find(start_marker)
    end = text.find(end_marker, start)
    require(start >= 0 and end > start, "bloque DELETE legacy no encontrado")

    replacement = """\t\t\tif(cmd.equals(Constants.DELETE)){
\t\t\t\t
\t\t\t\ttry {
\t\t\t\t\tReclamosPrestacionesServiceUtil.borrar(idReclamoDeBuscador, user);
\t\t\t\t\t_log.info(\"Reclamo Prestacional dado de baja: \" + idReclamoDeBuscador);
\t\t\t    } catch (Exception e) {
\t\t\t        _log.error(
\t\t\t        \t\t\"Error eliminando Reclamo Prestacional \" + idReclamoDeBuscador,
\t\t\t        \t\te
\t\t\t        );
\t\t\t        SessionErrors.add(renderRequest, \"error-delete-reclamo\");
\t\t\t    }
\t\t\t\t
"""
    text = text[:start] + replacement + text[end:]

    delete_end = text.find(end_marker, start)
    delete_block = text[start:delete_end]
    require("ClienteAppMobile" not in delete_block, "quedó sincronización AppMobile dentro del DELETE")
    require("getReclamoPrestacional(idReclamoDeBuscador)" not in delete_block, "quedó relectura posterior a la baja")
    require(
        "ClienteAppMobile.actualizarEstadoReintegro(idExterno, codigoExterno, token)" in text,
        "se alteró la sincronización de estados PE/CE/RE fuera del DELETE",
    )
    write_source(ACTION, text, encoding)


def update_service() -> None:
    text, encoding = read_source(SERVICE)

    text = replace_once(text, "import java.util.Map;\n", "", "import Map")
    text = replace_once(
        text,
        "import java.util.concurrent.ConcurrentHashMap;\n",
        "",
        "import ConcurrentHashMap",
    )

    fields = """\tprivate static final long BAJA_RECIENTE_TTL_MS = 60000L;
\tprivate static final Map<Integer, Long> BAJAS_RECIENTES =
\t\t\tnew ConcurrentHashMap<Integer, Long>();
"""
    text = replace_once(text, fields, "", "campos BAJAS_RECIENTES")

    guard = """\t\tif (esBajaReciente(id)) {
\t\t\t_log.debug(\"Se omite relectura de Reclamo Prestacional dado de baja: \" + id);
\t\t\treturn null;
\t\t}
"""
    text = replace_once(text, guard, "", "guard esBajaReciente")
    text = replace_once(text, "\t\tregistrarBajaReciente(id);\n", "", "registro baja reciente")

    start_marker = "\tprivate static void registrarBajaReciente(int idReclamo) {"
    end_marker = "\tprivate static void registrarOutboxSeguro("
    start = text.find(start_marker)
    end = text.find(end_marker, start)
    require(start >= 0 and end > start, "helpers BAJAS_RECIENTES no encontrados")
    text = text[:start] + text[end:]

    require("BAJAS_RECIENTES" not in text, "quedó BAJAS_RECIENTES")
    require("esBajaReciente" not in text, "quedó esBajaReciente")
    require(
        "ReclamoPrestacionalBajaTransaccionalService.borrar" in text,
        "se perdió la baja transaccional",
    )
    write_source(SERVICE, text, encoding)


def update_editor() -> None:
    text, encoding = read_source(EDITOR)

    start_marker = "if(prestacionEnEdicion != null  ){"
    end_marker = "\t    <input   type=\"hidden\""
    start = text.find(start_marker)
    end = text.find(end_marker, start)
    require(start >= 0 and end > start, "fragmento JavaScript inválido no encontrado")

    replacement = """if (prestacionEnEdicion != null) {
\tString codigoPrestacionEdicion = Validator.isNotNull(prestacionEnEdicion.getCodigoPrestacion())
\t\t\t? prestacionEnEdicion.getCodigoPrestacion()
\t\t\t: \"\";
\tString descripcionPrestacionEdicion = Validator.isNotNull(prestacionEnEdicion.getDescripcion())
\t\t\t? prestacionEnEdicion.getDescripcion()
\t\t\t: \"\";
%>
<script type=\"text/javascript\">
(function(window, jQuery) {
\t\"use strict\";

\tvar namespace = \"<portlet:namespace />\";
\tvar codigo = \"<%= HtmlUtil.escapeJS(codigoPrestacionEdicion) %>\";
\tvar descripcion = \"<%= HtmlUtil.escapeJS(descripcionPrestacionEdicion) %>\";

\tjQuery(\"#\" + namespace + \"datos_edicion_prestacion\").show();
\tjQuery(\"#\" + namespace + \"codigoprestacion\").val(codigo);
\tjQuery(\"#\" + namespace + \"idRegistro\").val(\"<%= prestacionEnEdicion.getIdRegistro() %>\");

\t<% if (prestacionEnEdicion.getId_prestacion() != 0) { %>
\tjQuery(\"#\" + namespace + \"codigoSeguimiento_filtro_edit\").val(codigo);
\tjQuery(\"#\" + namespace + \"descripcionSeguimiento_filtro_edit\").val(descripcion);

\tvar buscarNomenclador = window[namespace + \"buscarNomencladorAutocompletar_edit\"];
\tif (typeof buscarNomenclador === \"function\") {
\t\tbuscarNomenclador();
\t}
\t<% } else if (prestacionEnEdicion.getId_medicamento() != 0) { %>
\tjQuery(\"#\" + namespace + \"troquel_edit\").val(\"<%= prestacionEnEdicion.getId_medicamento() %>\");
\t<% } %>

\t<% if (ocultarSeccional != null) { %>
\tjQuery(\"#\" + namespace + \"Autorizado\").hide();
\t<% } %>
})(window, jQuery);
</script>
<%
}
%>
"""
    text = text[:start] + replacement + text[end:]

    require("HtmlUtil.escapeJS" in text, "el editor no escapa valores JavaScript")
    require("if (prestacionEnEdicion != null) {\n%>\n\n<script" not in text, "persistió el script anidado")
    require(text.count("jQuery(\"#<portlet:namespace />idRegistro\").val") == 0, "persistió inicialización duplicada")
    write_source(EDITOR, text, encoding)


def main() -> None:
    update_action()
    update_service()
    update_editor()
    print("RECLAMOS_P1_CLEANUP_APPLIED")


if __name__ == "__main__":
    main()
