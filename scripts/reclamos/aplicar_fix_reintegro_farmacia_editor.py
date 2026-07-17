#!/usr/bin/env python3
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[2]


def leer_legacy(ruta):
    path = ROOT / ruta
    data = path.read_bytes()
    return path, data.decode("latin-1")


def guardar_legacy(path, contenido):
    path.write_bytes(contenido.encode("latin-1"))


def reemplazar_uno(contenido, anterior, nuevo, etiqueta):
    cantidad = contenido.count(anterior)
    if cantidad != 1:
        raise RuntimeError(f"{etiqueta}: se esperó 1 coincidencia y hubo {cantidad}")
    return contenido.replace(anterior, nuevo, 1)


# 1) El editor JSP tenía una inicialización antes de que existieran los campos,
#    invocaba HtmlUtil.escapeJS (inexistente en Liferay 5.2) y cerraba dos veces
#    el mismo if. Se elimina ese bloque temprano y se inicializa al final/ready.
editor_path, editor = leer_legacy(
    "ext-web/docroot/html/portlet/autorizaciones/"
    "reclamos_prestacionales/datos_edicion_prestacion.jsp"
)

bloque_temprano = re.compile(
    r'<script type="text/javascript">\s*'
    r'\(function\(window, jQuery\) \{.*?HtmlUtil\.escapeJS.*?'
    r'</script>\s*<%\s*\}\s*%>\s*',
    re.DOTALL,
)
editor, cantidad = bloque_temprano.subn("", editor, count=1)
if cantidad != 1:
    raise RuntimeError(
        "editor JSP: no se encontró exactamente una inicialización temprana rota"
    )

if "HtmlUtil.escapeJS" in editor:
    raise RuntimeError("editor JSP: persiste HtmlUtil.escapeJS")

inicializacion_editor = r'''
<script type="text/javascript">
jQuery(function() {
    var namespace = "<portlet:namespace />";
    var codigo = jQuery("#" + namespace + "codigoSeguimiento_filtro_edit").val() || "";

    jQuery("#" + namespace + "datos_edicion_prestacion").show();
    jQuery("#" + namespace + "codigoprestacion").val(codigo);

    <% if (prestacionEnEdicion.getId_prestacion() != 0) { %>
    var buscarNomenclador =
            window[namespace + "buscarNomencladorAutocompletar_edit"];
    if (codigo && typeof buscarNomenclador === "function") {
        window.setTimeout(buscarNomenclador, 0);
    }
    <% } else if (prestacionEnEdicion.getId_medicamento() != 0) { %>
    jQuery("#" + namespace + "troquel_edit").val(
            "<%= prestacionEnEdicion.getId_medicamento() %>"
    );
    <% } %>

    <% if (ocultarSeccional != null) { %>
    jQuery("#" + namespace + "Autorizado").hide();
    <% } %>
});
</script>

'''

cierre_final = re.compile(r'(<%\s*\}\s*%>\s*)\Z', re.DOTALL)
coincidencia = cierre_final.search(editor)
if not coincidencia:
    raise RuntimeError("editor JSP: no se encontró el cierre final del if")
editor = (
    editor[:coincidencia.start()]
    + inicializacion_editor
    + coincidencia.group(1)
)

guardar_legacy(editor_path, editor)

# 2) La vista debe reaccionar con la API disponible en el jQuery de Liferay 5.2.
initial_path, initial = leer_legacy(
    "ext-web/docroot/html/portlet/autorizaciones/"
    "reclamos_prestacionales/view_reclamo_initial_state.js"
)

binding_moderno = '''jQuery(document).on(
        "change",
        "#" + namespace + "sector, #" + namespace + "tipopedido",
        function() {
            window.setTimeout(mostrarBuscadorSegunSeleccion, 0);
        }
);
'''

binding_legacy = '''function actualizarBuscadorPrestacion() {
    mostrarBuscadorSegunSeleccion();
}

/* Compatible con el jQuery legacy incluido por Liferay 5.2. */
campo("sector").change(actualizarBuscadorPrestacion);
campo("tipopedido").change(actualizarBuscadorPrestacion);
window[namespace + "actualizarBuscadorPrestacion"] =
        actualizarBuscadorPrestacion;
'''

initial = reemplazar_uno(
    initial,
    binding_moderno,
    binding_legacy,
    "binding de sector/tipo de pedido",
)
guardar_legacy(initial_path, initial)

# 3) Los onchange inline invocan primero el selector seguro. Así REINTEGRO +
#    FARMACIA alterna aunque otra rutina legacy falle después.
cabecera_path, cabecera = leer_legacy(
    "ext-web/docroot/html/portlet/autorizaciones/"
    "reclamos_prestacionales/view_reclamo_cabecera.jspf"
)

cabecera = reemplazar_uno(
    cabecera,
    'onchange="cambioTipoPedido();manejarTipoPedidoCierre();"',
    'onchange="if (typeof <portlet:namespace />actualizarBuscadorPrestacion == \'function\') { <portlet:namespace />actualizarBuscadorPrestacion(); } cambioTipoPedido();manejarTipoPedidoCierre();"',
    "onchange de tipo pedido",
)

cabecera = reemplazar_uno(
    cabecera,
    'onchange="manejarTipoSector();"',
    'onchange="if (typeof <portlet:namespace />actualizarBuscadorPrestacion == \'function\') { <portlet:namespace />actualizarBuscadorPrestacion(); } manejarTipoSector();"',
    "onchange de sector",
)
guardar_legacy(cabecera_path, cabecera)

# 4) Fuerza al navegador a tomar la nueva capa de estado.
view_path, view = leer_legacy(
    "ext-web/docroot/html/portlet/autorizaciones/"
    "reclamos_prestacionales/view_reclamo.jsp"
)
view = reemplazar_uno(
    view,
    "view_reclamo_initial_state.js?v=20260717-initial-state-3",
    "view_reclamo_initial_state.js?v=20260717-initial-state-4",
    "cache key initial state",
)
guardar_legacy(view_path, view)

print("FIX_REINTEGRO_FARMACIA_EDITOR_APLICADO")
