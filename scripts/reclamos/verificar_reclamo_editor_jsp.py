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
    legacy = leer("view_reclamo.js")
    patch = leer("view_reclamo_p0_patch.js")
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
        "function manejarTipoSector(){" in legacy,
        "falta el selector legacy Tipo Pedido por Sector",
    )
    exigir(
        "return sector == 'FARMACIA' && tipoPedido != 'EXCEPCION';" in legacy,
        "la regla productiva FARMACIA salvo EXCEPCION no está preservada",
    )
    exigir(
        "reclamoPrestacionalNamespace + \"busqueda_farmacia\").show()" in legacy,
        "el selector legacy no muestra Medicamento/Troquel para Farmacia",
    )
    exigir(
        "window.manejarTipoSector" not in patch,
        "el P0 vuelve a sobrescribir manejarTipoSector",
    )
    exigir(
        "window.cambioTipoPedido" not in patch,
        "el P0 vuelve a sobrescribir cambioTipoPedido",
    )
    exigir(
        "renderModoSector" not in patch,
        "el P0 vuelve a duplicar la matriz Tipo Pedido por Sector",
    )
    exigir(
        "cambioTipoPedido();manejarTipoPedidoCierre();" in cabecera,
        "Tipo Pedido no conserva el handler legacy",
    )
    exigir(
        "manejarTipoSector();" in cabecera,
        "Sector no conserva el handler legacy",
    )
    exigir(
        "view_reclamo_initial_state.js" not in view,
        "la vista todavía carga la segunda máquina de estado",
    )
    exigir(
        "view_reclamo.js?v=20260717-legacy-flows-1" in view,
        "el cache key no fuerza el JavaScript legacy corregido",
    )
    exigir(
        "view_reclamo_p0_patch.js?v=20260717-legacy-flows-1" in view,
        "el cache key no fuerza el P0 sin selector duplicado",
    )
    exigir(
        "jQuery(document).on(" not in view,
        "se reintrodujo una API no disponible en el jQuery de Liferay 5.2",
    )

    print("VERIFICACION_RECLAMO_EDITOR_JSP_OK")


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print("ERROR: %s" % exc, file=sys.stderr)
        sys.exit(1)
