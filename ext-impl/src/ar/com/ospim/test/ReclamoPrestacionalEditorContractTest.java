package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del editor de prestaciones y sus guards AJAX. */
public final class ReclamoPrestacionalEditorContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset LATIN_1 = Charset.forName("ISO-8859-1");

    private ReclamoPrestacionalEditorContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String dir =
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/";

        String view = leer(dir + "view_reclamo.jsp", UTF_8);
        String editorPatch = leer(
                dir + "view_reclamo_editor_patch.js",
                UTF_8
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
                "view_reclamo_editor_patch.js?v=20260723-editor-dom-clean-1"
        );
        assertBefore(
                "editor cargado antes del P0 general",
                view,
                "view_reclamo_editor_patch.js?v=20260723-editor-dom-clean-1",
                "view_reclamo_p0_patch.js?v=20260717-legacy-flows-1"
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
                "No se pudo cargar el editor de la prestaci\u00f3n."
        );
        assertContains(
                "API de diagnostico expuesta",
                editorPatch,
                "window.ReclamoPrestacionalEditorPatch"
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
