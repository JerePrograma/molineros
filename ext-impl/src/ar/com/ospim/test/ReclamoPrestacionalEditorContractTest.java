package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del editor de prestaciones y sus guards AJAX. */
public final class ReclamoPrestacionalEditorContractTest {

    private static final Charset ISO_8859_1 =
        Charset.forName("ISO-8859-1");
    private static final Charset LATIN_1 = Charset.forName("ISO-8859-1");

    private ReclamoPrestacionalEditorContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String dir =
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/";

        String view = leer(dir + "view_reclamo.jsp", LATIN_1);
        String editorPatch = leer(
                dir + "view_reclamo_editor_patch.js",
                LATIN_1
        );
        String p0Patch = leer(
                dir + "view_reclamo_p0_patch.js",
                LATIN_1
        );
        String cabecera = leer(
                dir + "view_reclamo_cabecera.jspf",
                LATIN_1
        );
        String seguimiento = leer(
                dir + "view_reclamo_seguimiento_cierre.jspf",
                LATIN_1
        );
        String inicioFormulario = leer(
                dir + "view_reclamo_inicio_formulario.jspf",
                LATIN_1
        );
        String configuracion = leer(
                dir + "view_reclamo_configuracion.jspf",
                LATIN_1
        );
        String editorJsp = leer(
                dir + "datos_edicion_prestacion.jsp",
                LATIN_1
        );
        String baseJs = leer(dir + "view_reclamo.js", LATIN_1);

        assertBefore(
                "editor cargado despues del guard de pestanas",
                view,
                "view_reclamo_tab_guard.js?v=20260717-initial-state-1",
                "view_reclamo_editor_patch.js?v=20260724-editor-buttons-2"
        );
        assertBefore(
                "editor cargado antes del P0 general",
                view,
                "view_reclamo_editor_patch.js?v=20260724-editor-buttons-2",
                "view_reclamo_p0_patch.js?v=20260723-popup-clean-2"
        );

        assertContains(
                "guard de XHR sincrono instalado",
                view,
                "ajaxNoBloqueante.__rpFiltroLetraNoBloqueante = true"
        );
        assertContains(
                "guard limitado al filtro de letra",
                view,
                ".indexOf(\"filtrarLetraComprobante\") >= 0"
        );
        assertContains(
                "filtro de letra forzado a asincrono",
                view,
                "opciones.async = true"
        );
        assertNotContains(
                "operacion normal sin advertencia",
                view,
                "RECLAMO_PRESTACIONAL_FILTRO_LETRA_ASYNC"
        );
        assertBefore(
                "guard activo antes del JSP legacy",
                view,
                "ajaxNoBloqueante.__rpFiltroLetraNoBloqueante = true",
                "view_reclamo.jspf"
        );

        assertContains(
                "guard del afiliado instalado",
                view,
                "ajaxAfiliadoNoBloqueante.__rpAfiliadoNoBloqueante = true"
        );
        assertContains(
                "permanencia del afiliado no bloqueante",
                view,
                "evalua_permanencia_afiliado"
        );
        assertContains(
                "observaciones del afiliado no bloqueantes",
                view,
                "tiene_observaciones_afiliado"
        );
        assertContains(
                "datos complementarios no bloqueantes",
                view,
                "buscar_afiliado_datos"
        );
        assertNotContains(
                "operaciones normales del afiliado sin advertencia",
                view,
                "RECLAMO_PRESTACIONAL_AFILIADO_ASYNC"
        );
        assertContains(
                "errores reales del afiliado conservados",
                view,
                "RECLAMO_PRESTACIONAL_AFILIADO_ERROR"
        );
        assertContains(
                "error real usa consola de error",
                view,
                "window.console.error"
        );
        assertContains(
                "loader de afiliados reemplazado",
                view,
                "jQuery.fn.load = loadAfiliadoSeguro"
        );
        assertContains(
                "loader limitado al buscador de autorizaciones",
                view,
                "struts_action=/autorizaciones/buscar_afiliados"
        );
        assertContains(
                "timeout explicito del buscador",
                view,
                "var TIMEOUT_AFILIADO_MS = 15000"
        );
        assertContains(
                "error visible del buscador",
                view,
                "No se pudo completar la busqueda de "
        );
        assertContains(
                "API de diagnostico del afiliado",
                view,
                "window.ReclamoPrestacionalAfiliadoSearchPatch"
        );

        assertContains(
                "base JS exporta grabacion namespaced",
                baseJs,
                "window[reclamoPrestacionalNamespace + \"saveReclamo\"]"
                        + " = reclamoPrestacional_saveReclamo;"
        );

        assertContains(
                "cabecera usa tabla legacy valida",
                cabecera,
                "<table class=\"lfr-table\""
        );
        assertContains(
                "cabecera conserva fecha OSPIM",
                cabecera,
                "dayParam=\"fechaospimDia\""
        );
        assertContains(
                "cabecera conserva fecha seccional",
                cabecera,
                "dayParam=\"fechaseccionalDia\""
        );
        assertContains(
                "cabecera conserva contratos de compras",
                cabecera,
                "id=\"<portlet:namespace />integracion\""
        );
        assertNotContains(
                "cabecera sin atributo width roto",
                cabecera,
                "width=\"33%  style="
        );
        assertNotContains(
                "cabecera sin celda con atributo fantasma",
                cabecera,
                "<td c width="
        );
        assertNotContains(
                "cabecera sin checkbox invalido",
                cabecera,
                "Unchecked"
        );
        assertNotContains(
                "cabecera sin tablas envolviendo selects",
                cabecera,
                "<td><table>"
        );

        assertContains(
                "formulario usa ancho disponible",
                inicioFormulario,
                "style=\"width:100%; box-sizing:border-box;\""
        );
        assertContains(
                "cabecera visual usa ancho flexible",
                configuracion,
                "width: 100%;"
        );
        assertNotContains(
                "cabecera visual sin ancho fijo",
                configuracion,
                "width: 1100px;"
        );
        assertContains(
                "revisiones sin alto vacio fijo",
                seguimiento,
                "max-height: 120px; overflow-y: auto;"
        );
        assertNotContains(
                "revisiones sin scroll permanente",
                seguimiento,
                "height: 120px; overflow: scroll;"
        );
        assertNotContains(
                "revisiones sin tabla envolvente",
                seguimiento,
                "<td colspan=\"10\"><liferay-util:include"
        );
        assertNotContains(
                "seguimiento sin checkbox invalido",
                seguimiento,
                "Unchecked"
        );
        assertNotContains(
                "seguimiento sin tabla fija de 600px",
                seguimiento,
                "width=\"600px\""
        );
        assertContains(
                "observacion medica completa cuatro columnas",
                seguimiento,
                "<td colspan=\"3\"><select name=\"<portlet:namespace/>observacion_medica\""
        );
        assertTokenBalance(
                "fieldset balanceados en seguimiento",
                seguimiento,
                "<fieldset",
                "</fieldset>"
        );
        assertTokenBalance(
                "filas balanceadas en seguimiento",
                seguimiento,
                "<tr",
                "</tr>"
        );
        assertTokenBalance(
                "celdas balanceadas en seguimiento",
                seguimiento,
                "<td",
                "</td>"
        );
        assertNotContains(
                "metodo no disponible en Liferay 5.2",
                editorJsp,
                "HtmlUtil.escapeJS"
        );
        assertContains(
                "inicializacion post-render",
                editorJsp,
                "jQuery(function() {"
        );
        assertContains(
                "wrapper del editor permanece abierto",
                editorJsp,
                "if (prestacionEnEdicion != null) {"
        );
        assertContains(
                "id de prestacion copiado a primitivo",
                editorJsp,
                "int idPrest ="
        );
        assertContains(
                "id de prestacion leido del modelo",
                editorJsp,
                "prestacionEnEdicion.getId_prestacion();"
        );
        assertContains(
                "id de medicamento copiado a primitivo",
                editorJsp,
                "int idMedic ="
        );
        assertContains(
                "id de medicamento leido del modelo",
                editorJsp,
                "prestacionEnEdicion.getId_medicamento();"
        );
        assertContains(
                "medicamento se inicializa solo sin prestacion",
                editorJsp,
                "prestacionEnEdicion.getId_prestacion() == 0"
        );
        assertContains(
                "medicamento exige identificador valido",
                editorJsp,
                "&& prestacionEnEdicion.getId_medicamento() != 0"
        );
        assertNotContains(
                "editor no abre nomenclador automaticamente",
                editorJsp,
                "var buscarNomenclador ="
        );
        assertNotContains(
                "editor no programa apertura automatica",
                editorJsp,
                "window.setTimeout("
        );
        assertNotContains(
                "id de prestacion sin comparacion nula",
                editorJsp,
                "getId_prestacion() != null"
        );
        assertNotContains(
                "id de prestacion sin intValue",
                editorJsp,
                "getId_prestacion().intValue()"
        );
        assertNotContains(
                "id de medicamento sin comparacion nula",
                editorJsp,
                "getId_medicamento() != null"
        );
        assertNotContains(
                "id de medicamento sin intValue",
                editorJsp,
                "getId_medicamento().intValue()"
        );
        assertContains(
                "boton editar con texto estable",
                editorJsp,
                "value=\"Editar Prestación\""
        );
        assertContains(
                "boton autorizar con texto estable",
                editorJsp,
                "value=\"Autoriza Prestación\""
        );
        assertContains(
                "boton rechazar con texto estable",
                editorJsp,
                "value=\"Rechaza Prestación\""
        );
        assertContains(
                "boton cancelar con identidad",
                editorJsp,
                "id=\"<portlet:namespace />btncancelar_prestacion\""
        );
        assertContains(
                "boton cancelar usa texto calculado seguro",
                editorJsp,
                "value=\"<%= HtmlUtil.escape(captionbotoncancelar) %>\""
        );
        assertNotContains(
                "boton editar no depende de clave libre de language",
                editorJsp,
                "value=\"<liferay-ui:message key=\"Editar Prestaci"
        );
        assertContains(
                "intercepta solamente el editor legacy",
                editorPatch,
                "var ENDPOINT_EDITOR = \"editar_reclamosprestaciones\""
        );
        assertContains(
                "reemplaza carga jQuery",
                editorPatch,
                "jQuery.fn.load = loadSeguro"
        );
        assertContains(
                "retira utilidades locales inseguras",
                editorPatch,
                "filtrarLetraComprobanteEdicion"
        );
        assertContains(
                "carga de letra asincrona",
                editorPatch,
                "type: \"GET\""
        );
        assertNotContains(
                "AJAX sincrono prohibido",
                editorPatch,
                "async: false"
        );
        assertNotContains(
                "AJAX sincrono legacy prohibido",
                editorPatch,
                "async:false"
        );
        assertContains(
                "error visible de carga",
                editorPatch,
                "No se pudo cargar el editor de la prestaci"
        );
        assertContains(
                "API de diagnostico expuesta",
                editorPatch,
                "window.ReclamoPrestacionalEditorPatch"
        );

        assertContains(
                "seleccion de nomenclador sin cierre interno",
                editorPatch,
                "window.seleccionaCamposNm = asignarNomencladorSeguro;"
        );
        assertContains(
                "seleccion y cierre coordinados una sola vez",
                editorPatch,
                "window.pasarParametrosAParentNm = function("
        );
        assertContains(
                "cierre protegido contra reentrada",
                editorPatch,
                "__rpCierreNomencladorSeguro"
        );
        assertContains(
                "busqueda inicial bloqueada",
                editorPatch,
                "var busquedaInicialBloqueada = true;"
        );
        assertContains(
                "limpieza focalizada de residuos del editor",
                editorPatch,
                "function limpiarResiduosEditor(contenedor)"
        );
        assertContains(
                "reparacion defensiva de botones del editor",
                editorPatch,
                "function repararBotonesEditor(contenedor)"
        );
        assertContains(
                "boton actualiza atributo value visible",
                editorPatch,
                "control.attr(\"value\", texto);"
        );
        assertContains(
                "boton actualiza propiedad value visible",
                editorPatch,
                "control.val(texto);"
        );
        assertContains(
                "botonera elimina campos text vacios",
                editorPatch,
                "if (tipo === \"text\" && esTextoVacio(control.val()))"
        );
        assertNotContains(
                "editor sin estilo ancho agregado",
                editorJsp,
                "style=\"width: 100%; border-collapse: separate; border-spacing: 3px;\""
        );
        assertContains(
                "limpieza no elimina botones funcionales",
                editorPatch,
                "var tipoVisualVacio = tipo === \"text\";"
        );
        assertContains(
                "boton cancelar recibe identidad estable",
                editorPatch,
                "namespace + \"btncancelar_prestacion\""
        );
        assertBefore(
                "botones reparados antes de limpiar residuos",
                editorPatch,
                "repararBotonesEditor(contenedor);",
                "limpiarResiduosEditor(contenedor);"
        );
        assertNotContains(
                "reparacion sin apertura automatica del popup",
                editorPatch,
                "window.setTimeout(buscarNomenclador, 0)"
        );
        assertNotContains(
                "P0 sin apertura automatica del popup",
                p0Patch,
                "buscarNomencladorAutocompletar_edit"
        );
        assertContains(
                "P0 conserva codigo tecnico sin buscar",
                p0Patch,
                "asignar(\"codigoprestacion\", codigo);"
        );

        System.out.println("CONTRATO_EDITOR_RECLAMO_PRESTACIONAL_OK");
    }

    private static String leer(String ruta, Charset charset) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), charset);
    }

    private static void assertContains(
            String etiqueta,
            String contenido,
            String esperado) {

        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontro [" + esperado + "]"
            );
        }
    }

    private static void assertNotContains(
            String etiqueta,
            String contenido,
            String prohibido) {

        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": se encontro [" + prohibido + "]"
            );
        }
    }

    private static void assertTokenBalance(
            String etiqueta,
            String contenido,
            String apertura,
            String cierre) {

        int cantidadAperturas = contar(contenido, apertura);
        int cantidadCierres = contar(contenido, cierre);

        if (cantidadAperturas != cantidadCierres) {
            throw new AssertionError(
                    etiqueta
                            + ": aperturas="
                            + cantidadAperturas
                            + ", cierres="
                            + cantidadCierres
            );
        }
    }

    private static int contar(String contenido, String token) {
        int cantidad = 0;
        int posicion = 0;

        while (true) {
            posicion = contenido.indexOf(token, posicion);

            if (posicion < 0) {
                return cantidad;
            }

            cantidad++;
            posicion += token.length();
        }
    }

    private static void assertBefore(
            String etiqueta,
            String contenido,
            String primero,
            String segundo) {

        int posPrimero = contenido.indexOf(primero);
        int posSegundo = contenido.indexOf(segundo);

        if (posPrimero < 0
                || posSegundo < 0
                || posPrimero >= posSegundo) {

            throw new AssertionError(
                    etiqueta + ": orden invalido entre ["
                            + primero
                            + "] y ["
                            + segundo
                            + "]"
            );
        }
    }
}
