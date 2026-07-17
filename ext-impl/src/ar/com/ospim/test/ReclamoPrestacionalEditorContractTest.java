package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del editor de prestaciones y su estabilizador AJAX. */
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
        String editorPatch = leer(dir + "view_reclamo_editor_patch.js", UTF_8);
        String editorJsp = leer(dir + "datos_edicion_prestacion.jsp", LATIN_1);

        assertBefore(
                "editor cargado después del guard de pestañas",
                view,
                "view_reclamo_tab_guard.js?v=20260717-initial-state-1",
                "view_reclamo_editor_patch.js?v=20260717-initial-state-1"
        );
        assertBefore(
                "editor cargado antes del P0 general",
                view,
                "view_reclamo_editor_patch.js?v=20260717-initial-state-1",
                "view_reclamo_p0_patch.js?v=20260717-initial-state-1"
        );

        assertNotContains(
                "método no disponible en Liferay 5.2",
                editorJsp,
                "HtmlUtil.escapeJS"
        );
        assertContains(
                "inicialización post-render",
                editorJsp,
                "jQuery(function() {"
        );
        assertContains(
                "código leído desde el control renderizado",
                editorJsp,
                "codigoSeguimiento_filtro_edit\").val() || \"\""
        );
        assertContains(
                "wrapper del editor permanece abierto",
                editorJsp,
                "if (prestacionEnEdicion != null) {"
        );
        assertContains(
                "cierre final del wrapper",
                editorJsp,
                "<%\n}\n%>"
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
                "carga de letra asíncrona",
                editorPatch,
                "type: \"GET\""
        );
        assertNotContains(
                "AJAX síncrono prohibido",
                editorPatch,
                "async: false"
        );
        assertNotContains(
                "AJAX síncrono legacy prohibido",
                editorPatch,
                "async:false"
        );
        assertContains(
                "cálculo decimal localizado",
                editorPatch,
                ".replace(\",\", \".\")"
        );
        assertContains(
                "variables de cálculo encapsuladas",
                editorPatch,
                "var total = importe * cantidad"
        );
        assertContains(
                "etiqueta de edición",
                editorPatch,
                "Observación de edición:"
        );
        assertContains(
                "etiqueta de autorización",
                editorPatch,
                "Observación de autorización:"
        );
        assertContains(
                "etiqueta de rechazo",
                editorPatch,
                "Observación de rechazo:"
        );
        assertContains(
                "error visible de carga",
                editorPatch,
                "No se pudo cargar el editor de la prestación."
        );
        assertContains(
                "API de diagnóstico expuesta",
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
                    etiqueta + ": no se encontró [" + esperado + "]"
            );
        }
    }

    private static void assertNotContains(
            String etiqueta,
            String contenido,
            String prohibido) {

        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": se encontró [" + prohibido + "]"
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

        if (posPrimero < 0 || posSegundo < 0 || posPrimero >= posSegundo) {
            throw new AssertionError(
                    etiqueta + ": orden inválido entre ["
                            + primero + "] y [" + segundo + "]"
            );
        }
    }
}
