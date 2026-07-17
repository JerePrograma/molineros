#!/usr/bin/env python3
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[2]
DIR = ROOT / "ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales"


def leer(nombre):
    return (DIR / nombre).read_bytes().decode("latin-1")


def exigir(condicion, mensaje):
    if not condicion:
        raise AssertionError(mensaje)


def codigo_scriptlets(jsp):
    bloques = re.findall(r"<%(?![@=!\-])([\s\S]*?)%>", jsp)
    codigo = "\n".join(bloques)
    codigo = re.sub(r"/\*[\s\S]*?\*/", "", codigo)
    codigo = re.sub(r"//[^\r\n]*", "", codigo)
    codigo = re.sub(r'"(?:\\.|[^"\\])*"', '""', codigo)
    codigo = re.sub(r"'(?:\\.|[^'\\])*'", "''", codigo)
    return codigo


def verificar_balance_java(jsp):
    profundidad = 0
    for caracter in codigo_scriptlets(jsp):
        if caracter == "{":
            profundidad += 1
        elif caracter == "}":
            profundidad -= 1
            exigir(
                profundidad >= 0,
                "datos_edicion_prestacion.jsp cierra un bloque Java inexistente",
            )
    exigir(
        profundidad == 0,
        "datos_edicion_prestacion.jsp deja bloques Java sin cerrar: %d"
        % profundidad,
    )


def main():
    editor = leer("datos_edicion_prestacion.jsp")
    initial = leer("view_reclamo_initial_state.js")
    cabecera = leer("view_reclamo_cabecera.jspf")
    view = leer("view_reclamo.jsp")

    exigir(
        "HtmlUtil.escapeJS" not in editor,
        "Liferay 5.2 no dispone de HtmlUtil.escapeJS",
    )
    exigir(
        "jQuery(function() {" in editor,
        "el editor no inicializa después de renderizar los campos",
    )
    exigir(
        'codigoSeguimiento_filtro_edit").val() || ""' in editor,
        "el editor no recupera el código desde el input renderizado",
    )
    verificar_balance_java(editor)

    exigir(
        'sector === "FARMACIA" && tipoPedido !== "EXCEPCION"' in initial,
        "la regla REINTEGRO + FARMACIA no usa el buscador de medicamentos",
    )
    exigir(
        'campo("sector").change(actualizarBuscadorPrestacion)' in initial,
        "falta el binding compatible con jQuery legacy para sector",
    )
    exigir(
        'campo("tipopedido").change(actualizarBuscadorPrestacion)' in initial,
        "falta el binding compatible con jQuery legacy para tipo de pedido",
    )
    exigir(
        "jQuery(document).on(" not in initial,
        "se reintrodujo una API no disponible en el jQuery de Liferay 5.2",
    )
    exigir(
        "actualizarBuscadorPrestacion(); } cambioTipoPedido()" in cabecera,
        "Tipo Pedido no invoca primero el selector seguro",
    )
    exigir(
        "actualizarBuscadorPrestacion(); } manejarTipoSector()" in cabecera,
        "Sector no invoca primero el selector seguro",
    )
    exigir(
        "view_reclamo_initial_state.js?v=20260717-initial-state-4" in view,
        "el cache key no fuerza la nueva capa de estado",
    )

    print("VERIFICACION_RECLAMO_EDITOR_JSP_OK")


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print("ERROR: %s" % exc, file=sys.stderr)
        sys.exit(1)
