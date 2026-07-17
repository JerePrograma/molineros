from pathlib import Path
import re

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


def regex_once(path, pattern, replacement, label):
    text = read(path)
    updated, count = re.subn(pattern, replacement, text, count=1, flags=re.S)
    if count != 1:
        raise SystemExit("%s: se esperaba 1 coincidencia y se encontraron %s" % (label, count))
    write(path, updated)


VIEW_DIR = "ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/"
VIEW = VIEW_DIR + "view_reclamo.jsp"
BASE_JS = VIEW_DIR + "view_reclamo.js"
AFILIADO = VIEW_DIR + "view_reclamo_afiliado_diagnostico.jspf"
PRESTACIONES = VIEW_DIR + "view_reclamo_prestaciones.jspf"
SEGUIMIENTO = VIEW_DIR + "view_reclamo_seguimiento_cierre.jspf"
CONFIG = VIEW_DIR + "view_reclamo_configuracion.jspf"
WORKFLOW = ".github/workflows/reclamo-prestacional-p0-contract.yml"
P0_CONTRACT = "ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java"
INITIAL_CONTRACT = "ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java"
RELEASE = "scripts/reclamos/validar_release_reclamos.sh"
VERSION_OLD = "20260716-p0-4"
VERSION_NEW = "20260717-initial-state-1"

# 1. Namespace y diagnóstico disponibles aun si el bloque de configuración legacy falla.
replace_once(
    VIEW,
    '<%@ include\n\tfile="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>\n<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf" %>',
    '<%@ include\n\tfile="/html/portlet/autorizaciones/reclamos_prestacionales/init.jsp"%>\n<script type="text/javascript">\nwindow.ReclamoPrestacionalNamespace = \'<portlet:namespace />\';\nwindow.ReclamoPrestacionalAssetError = function(nombre) {\n    if (window.console && window.console.error) {\n        window.console.error(\n                "RECLAMO_PRESTACIONAL_ASSET_ERROR: " + nombre\n        );\n    }\n};\n</script>\n<%@ include file="/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jspf" %>',
    "bootstrap namespace independiente"
)

view = read(VIEW)
if view.count(VERSION_OLD) != 4:
    raise SystemExit("versionado de assets: se esperaban 4 referencias antiguas")
view = view.replace(VERSION_OLD, VERSION_NEW)
write(VIEW, view)

replace_once(
    VIEW,
    '<script type="text/javascript" src="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js?v=' + VERSION_NEW + '"></script>',
    '<script type="text/javascript"\n\tsrc="<%= request.getContextPath() %>/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js?v=' + VERSION_NEW + '"\n\tonerror="window.ReclamoPrestacionalAssetError(\'view_reclamo.js\');"></script>\n<script type="text/javascript">\nif (!window.ReclamoPrestacionalViewBootstrapOk) {\n    window.ReclamoPrestacionalAssetError(\'view_reclamo.js/bootstrap\');\n}\n</script>',
    "diagnóstico del asset principal"
)

# 2. Bootstrap defensivo: el render inicial no debe abortar por config.values ausente.
replace_once(
    BASE_JS,
    '(function(window, jQuery) {\nvar reclamoPrestacionalViewConfig = window.ReclamoPrestacionalViewConfig || {};\nvar reclamoPrestacionalNamespace = reclamoPrestacionalViewConfig.namespace || "";\n\nvar popupMD;',
    '(function(window, jQuery) {\nvar reclamoPrestacionalConfigDisponible = !!window.ReclamoPrestacionalViewConfig;\nvar reclamoPrestacionalViewConfig = window.ReclamoPrestacionalViewConfig || {};\nreclamoPrestacionalViewConfig.values = jQuery.extend({\n\tcantPrestaciones: 0,\n\tcasoVinculado: "0",\n\thasReclamo: false,\n\treclamoCerrado: false,\n\ttipoGestionCierre: 0,\n\tidObservacionMedica: 0,\n\ttieneResolucion: false,\n\tesEdicion: false,\n\tesAlta: false,\n\tesBorradorCompras: false,\n\tidReclamo: 0,\n\tcantRevisiones: 0,\n\tdebitoTercerizadora: false,\n\tcodigoCie10Presente: false,\n\tcaiNamespace: false\n}, reclamoPrestacionalViewConfig.values || {});\nreclamoPrestacionalViewConfig.urls = reclamoPrestacionalViewConfig.urls || {};\nreclamoPrestacionalViewConfig.messages = reclamoPrestacionalViewConfig.messages || {};\nvar reclamoPrestacionalNamespace = reclamoPrestacionalViewConfig.namespace ||\n\t\twindow.ReclamoPrestacionalNamespace || "";\n\nif (!reclamoPrestacionalConfigDisponible && window.console && window.console.error) {\n\twindow.console.error("RECLAMO_PRESTACIONAL_CONFIG_AUSENTE");\n}\n\nvar popupMD;',
    "configuración JS defensiva"
)

replace_once(
    BASE_JS,
    "jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').attr('readonly', true);\n\n\nvar addprestacion=false;",
    "jQuery('#' + reclamoPrestacionalNamespace + 'reconocidoSSS').attr('readonly', true);\nwindow.ReclamoPrestacionalViewBootstrapOk = true;\n\n\nvar addprestacion=false;",
    "marca de bootstrap ejecutado"
)

new_sector_function = r'''function manejarTipoSector(){
	var sectorControl = jQuery('#' + reclamoPrestacionalNamespace + 'sector');
	var sector = sectorControl.val();
	var tipoPedido = jQuery('#' + reclamoPrestacionalNamespace + 'tipopedido').val();

	try {
		/* El alta sin sector debe coincidir con producción: no muestra ningún buscador. */
		jQuery("#" + reclamoPrestacionalNamespace + "busqueda_prestaciones").hide();
		jQuery("#" + reclamoPrestacionalNamespace + "busqueda_farmacia").hide();
		jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val("0");

		jQuery('#' + reclamoPrestacionalNamespace + 'troquel').val("");
		jQuery('#' + reclamoPrestacionalNamespace + 'codigoSeguimiento_filtro').val("");
		jQuery('#' + reclamoPrestacionalNamespace + 'descripcionSeguimiento_filtro').val("");
		jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("");

		if (!sectorControl.length || sectorControl.prop("selectedIndex") <= 0 || !sector) {
			return;
		}

		jQuery("#" + reclamoPrestacionalNamespace + "busqueda_prestaciones").show();
		jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val("1");

		if (reclamoPrestacional_usaBuscadorMedicamentos()) {
			jQuery("#" + reclamoPrestacionalNamespace + "busqueda_farmacia").show();
			jQuery("#" + reclamoPrestacionalNamespace + "busqueda_prestaciones").hide();
			jQuery("#" + reclamoPrestacionalNamespace + "nom_seleccionado").val("2");
			return;
		}

		if (sector == 'FARMACIA' && tipoPedido == 'EXCEPCION') {
			/* Producción usa Código Presentado para EXCEPCION + FARMACIA. */
			jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("9");
			return;
		}

		if (sector == 'DISCAPACIDAD') {
			jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("8");
		} else if (sector == 'ODONTOLOGIA') {
			jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("1");
		} else if (sector == 'PRESTACIONES MEDICAS' || sector == 'LEGALES') {
			jQuery("#" + reclamoPrestacionalNamespace + "tipoNomencladorSeguimiento_filtro").val("0");
		}
	}
	catch (err) {
		alert('error manejarTipoSector() ');
	}
}
'''
regex_once(
    BASE_JS,
    r'function manejarTipoSector\(\)\{.*?\n\}\n\n\n\n\n\nfunction reclamoPrestacional_agregarRevision',
    new_sector_function + '\n\n\n\n\nfunction reclamoPrestacional_agregarRevision',
    "estado inicial por sector"
)

# 3. Estado inicial fail-closed desde JSP.
replace_once(
    AFILIADO,
    '<div id="<portlet:namespace />divResultadoActualizarOK">',
    '<div id="<portlet:namespace />divResultadoActualizarOK" style="display:none;">',
    "mensaje domicilio inicialmente oculto"
)

replace_once(
    PRESTACIONES,
    '<div id="<portlet:namespace />busqueda_farmacia" align="left" width="80%">',
    '<div id="<portlet:namespace />busqueda_farmacia" align="left" width="80%" style="display:none;">',
    "buscador farmacia inicialmente oculto"
)
replace_once(
    PRESTACIONES,
    '<div id="<portlet:namespace />busqueda_prestaciones" align="left" width="80%">',
    '<div id="<portlet:namespace />busqueda_prestaciones" align="left" width="80%" style="display:none;">',
    "buscador nomenclador inicialmente oculto"
)
replace_once(
    PRESTACIONES,
    '<div id="<portlet:namespace />datos_edicion_prestacion" align="left" width="95%">',
    '<div id="<portlet:namespace />datos_edicion_prestacion" align="left" width="95%"\n\t\tstyle="<%= esBorradorCompras ? \"\" : \"display:none;\" %>">',
    "editor condicionado por Compras"
)
replace_once(
    PRESTACIONES,
    '<div id="<portlet:namespace />datos_prestacion_ingreso">',
    '<div id="<portlet:namespace />datos_prestacion_ingreso"\n\t\tstyle="<%= esBorradorCompras ? \"display:none;\" : \"\" %>">',
    "ingreso manual condicionado por Compras"
)
replace_once(
    PRESTACIONES,
    'style="height: 120px; overflow: scroll; overflow-x: hidden;">\n\t\t<span style="background-color: #d4d9d5; font-size: 135%"><b><%= caso_vinculado%>.</b>',
    'style="display:none; height: 120px; overflow: scroll; overflow-x: hidden;">\n\t\t<span style="background-color: #d4d9d5; font-size: 135%"><b><%= caso_vinculado%>.</b>',
    "prestaciones asociadas inicialmente ocultas"
)

replace_once(
    SEGUIMIENTO,
    '<div id="<portlet:namespace />lista_contactos_reclamo" align="center"\n\t\t\tstyle="height: 160px; overflow: scroll; overflow-x: hidden;">',
    '<div id="<portlet:namespace />lista_contactos_reclamo" align="center"\n\t\t\tstyle="display:none; height: 160px; overflow: scroll; overflow-x: hidden;">',
    "lista CRM inicialmente oculta"
)
replace_once(
    SEGUIMIENTO,
    '<div id="<portlet:namespace />Cierre_Reclamo_Div">',
    '<div id="<portlet:namespace />Cierre_Reclamo_Div"\n\t\t\t\t\t\tstyle="<%= existeReclamoPersistido && reclamoprestacional.getEstado() == 3 ? \"\" : \"display:none;\" %>">',
    "cierre condicionado por estado persistido"
)

# 4. Contrato específico de estado inicial.
contract = r'''package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del render inicial de Nuevo Reclamo Prestacional. */
public final class ReclamoPrestacionalInitialViewContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";

    private ReclamoPrestacionalInitialViewContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(DIR + "view_reclamo.jsp");
        String js = leer(DIR + "view_reclamo.js");
        String afiliado = leer(DIR + "view_reclamo_afiliado_diagnostico.jspf");
        String prestaciones = leer(DIR + "view_reclamo_prestaciones.jspf");
        String seguimiento = leer(DIR + "view_reclamo_seguimiento_cierre.jspf");
        String configuracion = leer(DIR + "view_reclamo_configuracion.jspf");

        contiene(view, "namespace independiente", "window.ReclamoPrestacionalNamespace");
        contiene(view, "diagnóstico de asset", "RECLAMO_PRESTACIONAL_ASSET_ERROR");
        contiene(view, "versión de assets", "?v=20260717-initial-state-1");
        contiene(js, "fallback namespace", "window.ReclamoPrestacionalNamespace || \"\"");
        contiene(js, "defaults de configuración", "reclamoPrestacionalViewConfig.values = jQuery.extend");
        contiene(js, "marca bootstrap", "window.ReclamoPrestacionalViewBootstrapOk = true");
        contiene(js, "sector obligatorio antes de buscador", "sectorControl.prop(\"selectedIndex\") <= 0");

        contiene(afiliado, "éxito domicilio oculto", "divResultadoActualizarOK\" style=\"display:none;\"");
        contiene(prestaciones, "farmacia oculta", "busqueda_farmacia\" align=\"left\" width=\"80%\" style=\"display:none;\"");
        contiene(prestaciones, "nomenclador oculto", "busqueda_prestaciones\" align=\"left\" width=\"80%\" style=\"display:none;\"");
        contiene(prestaciones, "editor sólo para Compras", "esBorradorCompras ? \"\" : \"display:none;\"");
        contiene(prestaciones, "ingreso manual fuera de Compras", "esBorradorCompras ? \"display:none;\" : \"\"");
        contiene(prestaciones, "asociadas ocultas", "style=\"display:none; height: 120px;");
        contiene(seguimiento, "contactos ocultos", "style=\"display:none; height: 160px;");
        contiene(seguimiento, "cierre sólo persistido", "existeReclamoPersistido && reclamoprestacional.getEstado() == 3");
        contiene(configuracion, "flag Compras conectado", "esBorradorCompras: <%= esBorradorCompras %>");

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
}
'''
write(INITIAL_CONTRACT, contract)

# 5. Conecta contrato y validación sintáctica permanente.
workflow = read(WORKFLOW)
for anchor, addition in (
    ("      - 'ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java'\n",
     "      - 'ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java'\n"),
    ("      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js'\n",
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_afiliado_diagnostico.jspf'\n"
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_prestaciones.jspf'\n"
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_seguimiento_cierre.jspf'\n"
     "      - 'ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_configuracion.jspf'\n"),
):
    count = workflow.count(anchor)
    if count != 2:
        raise SystemExit("workflow paths: se esperaban 2 anclas y se encontraron %s" % count)
    workflow = workflow.replace(anchor, anchor + addition)

workflow = workflow.replace(
    "            ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java \\\n",
    "            ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java \\\n"
    "            ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java \\\n",
    1
)
workflow = workflow.replace(
    "      - name: Run P1 cleanup contract\n",
    "      - name: Run initial view contract\n"
    "        run: |\n"
    "          java -cp /tmp/rp-contracts \\\n"
    "            ar.com.ospim.test.ReclamoPrestacionalInitialViewContractTest\n\n"
    "      - name: Validate Reclamo JavaScript syntax\n"
    "        run: |\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_p0_patch.js\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_editor_patch.js\n"
    "          node --check ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo_tab_guard.js\n\n"
    "      - name: Run P1 cleanup contract\n",
    1
)
write(WORKFLOW, workflow)

# P0 contract y release gate usan el nuevo cache key.
p0 = read(P0_CONTRACT)
if p0.count(VERSION_OLD) != 3:
    raise SystemExit("P0 contract: cantidad inesperada de versiones antiguas")
write(P0_CONTRACT, p0.replace(VERSION_OLD, VERSION_NEW))

release = read(RELEASE)
replace_once(
    RELEASE,
    "  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java\n",
    "  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalP0ContractTest.java\n"
    "  ext-impl/src/ar/com/ospim/test/ReclamoPrestacionalInitialViewContractTest.java\n",
    "release contract source"
)
replace_once(
    RELEASE,
    "  ar.com.ospim.test.ReclamoPrestacionalP0ContractTest\n",
    "  ar.com.ospim.test.ReclamoPrestacionalP0ContractTest\n"
    "  ar.com.ospim.test.ReclamoPrestacionalInitialViewContractTest\n",
    "release contract class"
)
release = read(RELEASE)
if release.count(VERSION_OLD) != 1:
    raise SystemExit("release gate: versión antigua inesperada")
release = release.replace(VERSION_OLD, VERSION_NEW)
release = release.replace("Assets p0-4 conectados", "Assets initial-state-1 conectados")
write(RELEASE, release)

# Guardas finales.
for path in (VIEW, BASE_JS, AFILIADO, PRESTACIONES, SEGUIMIENTO, CONFIG, WORKFLOW, P0_CONTRACT, INITIAL_CONTRACT, RELEASE):
    if not (ROOT / path).is_file():
        raise SystemExit("Falta archivo final: " + path)

print("NORMALIZACION_VISTA_INICIAL_APLICADA")
