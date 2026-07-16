package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual del estabilizador del editor de prestaciones.
 *
 * Se ejecuta sin dependencias de Liferay y protege la carga AJAX frente al
 * JavaScript inválido que todavía existe en el fragmento legacy.
 */
public final class ReclamoPrestacionalEditorContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private ReclamoPrestacionalEditorContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );
        String editorPatch = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo_editor_patch.js"
        );

        assertBefore(
                "editor cargado después del legacy",
                view,
                "view_reclamo.js?v=20260716-p0-3",
                "view_reclamo_editor_patch.js?v=20260716-p0-3"
        );
        assertBefore(
                "editor cargado antes del P0 general",
                view,
                "view_reclamo_editor_patch.js?v=20260716-p0-3",
                "view_reclamo_p0_patch.js?v=20260716-p0-3"
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
                "retira inicialización rota",
                editorPatch,
                "prestacionEnEdicion != null"
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

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
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
