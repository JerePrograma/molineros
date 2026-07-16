package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual ejecutable sin dependencias de Liferay.
 *
 * Verifica que la capa de estabilización P0 permanezca conectada después de
 * cambios en el JSP legacy. No reemplaza pruebas funcionales con navegador.
 */
public final class ReclamoPrestacionalP0ContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private ReclamoPrestacionalP0ContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );
        String patch = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo_p0_patch.js"
        );

        assertContains(
                "snapshot antes del script legacy",
                view,
                "ReclamoPrestacionalBootstrapSnapshot"
        );
        assertBefore(
                "snapshot antes de legacy",
                view,
                "ReclamoPrestacionalBootstrapSnapshot",
                "view_reclamo.js?v="
        );
        assertBefore(
                "patch después de legacy",
                view,
                "view_reclamo.js?v=",
                "view_reclamo_p0_patch.js?v="
        );
        assertContains(
                "assets versionados",
                view,
                "?v=20260716-p0-1"
        );

        assertContains(
                "estado cerrado numérico",
                patch,
                "var ESTADO_CERRADO = \"3\""
        );
        assertContains(
                "gestión rechazo numérica",
                patch,
                "var GESTION_RECHAZADO = \"5\""
        );
        assertContains(
                "cierre espera revisión",
                patch,
                ").done(function(html)"
        );
        assertContains(
                "revisión fail closed",
                patch,
                "El reclamo no fue guardado ni cerrado"
        );
        assertContains(
                "flags enviados",
                patch,
                "chk_entramite: campo(\"chk_entramite\").is(\":checked\")"
        );
        assertContains(
                "restaura precarga",
                patch,
                "restaurarSeleccionInicial();"
        );
        assertContains(
                "editor reinicializado",
                patch,
                "editar_reclamosprestaciones"
        );
        assertContains(
                "doble submit bloqueado",
                patch,
                "if (submitEnCurso)"
        );
        assertNotContains(
                "selector textual cerrado prohibido",
                patch,
                "option[value='CERRADO']"
        );
        assertNotContains(
                "selector textual rechazado prohibido",
                patch,
                "option[value='RECHAZADO']"
        );

        System.out.println("CONTRATO_RECLAMO_PRESTACIONAL_P0_OK");
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
