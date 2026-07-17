from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]


def read(path):
    return (ROOT / path).read_text(encoding="utf-8")


def write(path, text):
    (ROOT / path).write_text(text, encoding="utf-8", newline="")


def replace_once(path, old, new, label):
    text = read(path)
    count = text.count(old)
    if count != 1:
        raise SystemExit("%s: se esperaba 1 coincidencia y se encontraron %s" % (label, count))
    write(path, text.replace(old, new, 1))


DIR = "ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/"
VIEW = DIR + "view_reclamo.jsp"
INITIAL_JS = DIR + "view_reclamo_initial_state.js"
AFILIADO = DIR + "view_reclamo_afiliado_diagnostico.jspf"
PRESTACIONES = DIR + "view_reclamo_prestaciones.jspf"
SEGUIMIENTO = DIR + "view_reclamo_seguimiento_cierre.jspf"
CONFIG = DIR + "view_reclamo_configuracion.jspf"
WORKFLOW = ".github/workflows/reclamo-prestacional-p0-contract.yml"
P0 = "ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java"
CONTRACT = "ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java"
RELEASE = "scripts/reclamos/validar_release_reclamos.sh"
OLD = "20260716-p0-4"
NEW = "20260717-initial-state-1"

# Namespace independiente del bloque de configuración generado por el JSPF.
replace_once(
    VIEW,
    '<%@ include\n\tfile="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>\n<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf" %>',
    '<%@ include\n\tfile="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>\n<script type="text/javascript">\nwindow.ReclamoPrestacionalNamespace = \'<portlet:namespace />\';\nwindow.ReclamoPrestacionalAssetError = function(nombre) {\n    if (window.console && window.console.error) {\n        window.console.error("RECLAMO_PRESTACIONAL_ASSET_ERROR: " + nombre);\n    }\n};\n</script>\n<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf" %>',
    "namespace independiente"
)

view = read(VIEW)
if view.count(OLD) != 4:
    raise SystemExit("Se esperaban cuatro assets legacy versionados")
view = view.replace(OLD, NEW)
write(VIEW, view)

replace_once(
    VIEW,
    '<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js?v=' + NEW + '"></script>',
    '<script type="text/javascript"\n\tsrc="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_initial_state.js?v=' + NEW + '"\n\tonerror="window.ReclamoPrestacionalAssetError(\'view_reclamo_initial_state.js\');"></script>\n<script type="text/javascript"\n\tsrc="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js?v=' + NEW + '"\n\tonerror="window.ReclamoPrestacionalAssetError(\'view_reclamo.js\');"></script>\n<script type="text/javascript">\nif (!window.ReclamoPrestacionalInitialStateOk) {\n    window.ReclamoPrestacionalAssetError(\'view_reclamo_initial_state.js/bootstrap\');\n}\n</script>',
    "conexión de capa inicial"
)

initial_js = r'''(function(window, jQuery) {
"use strict";

var configDisponible = !!window.ReclamoPrestacionalViewConfig;
var config = window.ReclamoPrestacionalViewConfig || {};
config.values = jQuery.extend({
    cantPrestaciones: 0,
    casoVinculado: "0",
    hasReclamo: false,
    reclamoCerrado: false,
    tipoGestionCierre: 0,
    idObservacionMedica: 0,
    tieneResolucion: false,
    esEdicion: false,
    esAlta: false,
    esBorradorCompras: false,
    idReclamo: 0,
    cantRevisiones: 0,
    debitoTercerizadora: false,
    codigoCie10Presente: false,
    caiNamespace: false
}, config.values || {});
config.urls = config.urls || {};
config.messages = config.messages || {};
config.namespace = config.namespace || window.ReclamoPrestacionalNamespace || "";
window.ReclamoPrestacionalViewConfig = config;

var namespace = config.namespace;

function campo(sufijo) {
    return jQuery("#" + namespace + sufijo);
}

function sectorNoSeleccionado() {
    var sector = campo("sector");
    return !sector.length || sector.prop("selectedIndex") <= 0 || !sector.val();
}

function normalizarBuscadoresSinSector() {
    if (!sectorNoSeleccionado()) {
        return;
    }
    campo("busqueda_prestaciones").hide();
    campo("busqueda_farmacia").hide();
    campo("nom_seleccionado").val("0");
}

function aplicarEstadoInicial() {
    campo("divResultadoActualizarOK").hide();
    campo("lista_prestaciones_asociadas").hide();
    campo("lista_contactos_reclamo").hide();

    if (config.values.esBorradorCompras) {
        campo("datos_prestacion_ingreso").hide();
        campo("datos_edicion_prestacion").show();
    } else {
        campo("datos_edicion_prestacion").hide();
        campo("datos_prestacion_ingreso").show();
    }

    campo("Cierre_Reclamo_Div").toggle(
            !!config.values.reclamoCerrado
    );
    normalizarBuscadoresSinSector();
}

if (!configDisponible && window.console && window.console.error) {
    window.console.error("RECLAMO_PRESTACIONAL_CONFIG_AUSENTE");
}

aplicarEstadoInicial();

jQuery(function() {
    /* Se ejecuta después de los ready handlers legacy. */
    window.setTimeout(aplicarEstadoInicial, 0);
});

jQuery(document).on(
        "change",
        "#" + namespace + "sector, #" + namespace + "tipopedido",
        function() {
            window.setTimeout(normalizarBuscadoresSinSector, 0);
        }
);

window.ReclamoPrestacionalInitialStateOk = true;
})(window, jQuery);
'''
write(INITIAL_JS, initial_js)

# Render inicial fail-closed desde servidor.
replace_once(
    AFILIADO,
    '<div id="<portlet:namespace />divResultadoActualizarOK">',
    '<div id="<portlet:namespace />divResultadoActualizarOK" style="display:none;">',
    "mensaje de domicilio"
)
replace_once(
    PRESTACIONES,
    '<div id="<portlet:namespace />busqueda_farmacia" align="left" width="80%">',
    '<div id="<portlet:namespace />busqueda_farmacia" align="left" width="80%" style="display:none;">',
    "buscador farmacia"
)
replace_once(
    PRESTACIONES,
    '<div id="<portlet:namespace />busqueda_prestaciones" align="left" width="80%">',
    '<div id="<portlet:namespace />busqueda_prestaciones" align="left" width="80%" style="display:none;">',
    "buscador prestaciones"
)
replace_once(
    PRESTACIONES,
    '<div id="<portlet:namespace />datos_edicion_prestacion" align="left" width="95%">',
    '<div id="<portlet:namespace />datos_edicion_prestacion" align="left" width="95%"\n\t\tstyle="<%= esBorradorCompras ? \"\" : \"display:none;\" %>">',
    "editor Compras"
)
replace_once(
    PRESTACIONES,
    '<div id="<portlet:namespace />datos_prestacion_ingreso">',
    '<div id="<portlet:namespace />datos_prestacion_ingreso"\n\t\tstyle="<%= esBorradorCompras ? \"display:none;\" : \"\" %>">',
    "ingreso manual"
)
replace_once(
    PRESTACIONES,
    'style="height: 120px; overflow: scroll; overflow-x: hidden;">\n\t\t<span style="background-color: #d4d9d5; font-size: 135%"><b><%= caso_vinculado%>.</b>',
    'style="display:none; height: 120px; overflow: scroll; overflow-x: hidden;">\n\t\t<span style="background-color: #d4d9d5; font-size: 135%"><b><%= caso_vinculado%>.</b>',
    "prestaciones asociadas"
)
replace_once(
    SEGUIMIENTO,
    '<div id="<portlet:namespace />lista_contactos_reclamo" align="center"\n\t\t\tstyle="height: 160px; overflow: scroll; overflow-x: hidden;">',
    '<div id="<portlet:namespace />lista_contactos_reclamo" align="center"\n\t\t\tstyle="display:none; height: 160px; overflow: scroll; overflow-x: hidden;">',
    "contactos CRM"
)
replace_once(
    SEGUIMIENTO,
    '<div id="<portlet:namespace />Cierre_Reclamo_Div">',
    '<div id="<portlet:namespace />Cierre_Reclamo_Div"\n\t\t\t\t\t\tstyle="<%= existeReclamoPersistido && reclamoprestacional.getEstado() == 3 ? \"\" : \"display:none;\" %>">',
    "cierre de reclamo"
)

contract = r'''package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del estado inicial de Nuevo Reclamo Prestacional. */
public final class ReclamoPrestacionalInitialViewContractTest {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";

    private ReclamoPrestacionalInitialViewContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(DIR + "view_reclamo.jsp");
        String initial = leer(DIR + "view_reclamo_initial_state.js");
        String afiliado = leer(DIR + "view_reclamo_afiliado_diagnostico.jspf");
        String prestaciones = leer(DIR + "view_reclamo_prestaciones.jspf");
        String seguimiento = leer(DIR + "view_reclamo_seguimiento_cierre.jspf");
        String config = leer(DIR + "view_reclamo_configuracion.jspf");

        contiene(view, "namespace independiente", "window.ReclamoPrestacionalNamespace");
        contiene(view, "capa antes de legacy", "view_reclamo_initial_state.js?v=20260717-initial-state-1");
        antes(view, "view_reclamo_initial_state.js?v=", "view_reclamo.js?v=");
        contiene(view, "diagnóstico asset", "RECLAMO_PRESTACIONAL_ASSET_ERROR");
        contiene(initial, "defaults seguros", "config.values = jQuery.extend");
        contiene(initial, "preserva Compras", "config.values.esBorradorCompras");
        contiene(initial, "espera ready legacy", "window.setTimeout(aplicarEstadoInicial, 0)");
        contiene(initial, "sector vacío", "sector.prop(\"selectedIndex\") <= 0");
        contiene(initial, "marca ejecutada", "ReclamoPrestacionalInitialStateOk = true");

        contiene(afiliado, "mensaje oculto", "divResultadoActualizarOK\" style=\"display:none;\"");
        contiene(prestaciones, "farmacia oculta", "busqueda_farmacia\" align=\"left\" width=\"80%\" style=\"display:none;\"");
        contiene(prestaciones, "nomenclador oculto", "busqueda_prestaciones\" align=\"left\" width=\"80%\" style=\"display:none;\"");
        contiene(prestaciones, "editor Compras", "esBorradorCompras ? \"\" : \"display:none;\"");
        contiene(prestaciones, "ingreso normal", "esBorradorCompras ? \"display:none;\" : \"\"");
        contiene(prestaciones, "asociadas ocultas", "style=\"display:none; height: 120px;");
        contiene(seguimiento, "CRM oculto", "style=\"display:none; height: 160px;");
        contiene(seguimiento, "cierre persistido", "existeReclamoPersistido && reclamoprestacional.getEstado() == 3");
        contiene(config, "flag Compras", "esBorradorCompras: <%= esBorradorCompras %>");

        System.out.println("CONTRATO_RECLAMO_PRESTACIONAL_VISTA_INICIAL_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
    }

    private static void contiene(String contenido, String etiqueta, String esperado) {
        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(etiqueta + ": no se encontró [" + esperado + "]");
        }
    }

    private static void antes(String contenido, String primero, String segundo) {
        int a = contenido.indexOf(primero);
        int b = contenido.indexOf(segundo);
        if (a < 0 || b < 0 || a >= b) {
            throw new AssertionError("Orden inválido: " + primero + " / " + segundo);
        }
    }
}
'''
write(CONTRACT, contract)

# P0 contract mantiene el orden legacy/patch con el nuevo cache key.
p0 = read(P0)
if p0.count(OLD) != 3:
    raise SystemExit("P0: cantidad inesperada de cache keys")
write(P0, p0.replace(OLD, NEW))

workflow = read(WORKFLOW)
for anchor, addition in (
    ("      - 'ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java'\n",
     "      - 'ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java'\n"),
    ("      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js'\n",
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_initial_state.js'\n"
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_afiliado_diagnostico.jspf'\n"
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_prestaciones.jspf'\n"
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_seguimiento_cierre.jspf'\n"
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_configuracion.jspf'\n"),
):
    if workflow.count(anchor) != 2:
        raise SystemExit("Workflow: ancla inesperada " + anchor.strip())
    workflow = workflow.replace(anchor, anchor + addition)

compile_anchor = "            ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java \\\n"
if workflow.count(compile_anchor) != 1:
    raise SystemExit("Workflow: compile anchor")
workflow = workflow.replace(
    compile_anchor,
    compile_anchor + "            ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java \\\n",
    1
)
run_anchor = "      - name: Run P1 cleanup contract\n"
if workflow.count(run_anchor) != 1:
    raise SystemExit("Workflow: run anchor")
workflow = workflow.replace(
    run_anchor,
    "      - name: Run initial view contract\n"
    "        run: |\n"
    "          java -cp /tmp/rp-contracts \\\n"
    "            ar.com.ospim.test.ReclamoPrestacionalInitialViewContractTest\n\n"
    "      - name: Validate Reclamo JavaScript syntax\n"
    "        run: |\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_initial_state.js\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_tab_guard.js\n\n"
    + run_anchor,
    1
)
write(WORKFLOW, workflow)

replace_once(
    RELEASE,
    "  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java\n",
    "  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java\n"
    "  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java\n",
    "release source"
)
replace_once(
    RELEASE,
    "  ar.com.ospim.test.ReclamoPrestacionalP0ContractTest\n",
    "  ar.com.ospim.test.ReclamoPrestacionalP0ContractTest\n"
    "  ar.com.ospim.test.ReclamoPrestacionalInitialViewContractTest\n",
    "release class"
)
release = read(RELEASE)
release = release.replace(
    "  view_reclamo.js \\\n",
    "  view_reclamo_initial_state.js \\\n  view_reclamo.js \\\n",
    1
)
if release.count(OLD) != 1:
    raise SystemExit("Release: cache key inesperada")
release = release.replace(OLD, NEW)
release = release.replace("Assets p0-4 conectados", "Assets initial-state-1 conectados")
write(RELEASE, release)

print("NORMALIZACION_VISTA_INICIAL_V2_APLICADA")
