package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual del scope de sesión por borrador.
 */
public final class ReclamoPrestacionalDraftScopeContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private ReclamoPrestacionalDraftScopeContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String scope = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/session/"
                        + "ReclamoPrestacionalDraftScope.java"
        );
        String guard = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo_tab_guard.js"
        );

        assertContains("parámetro compartido", scope,
                "PARAM_DRAFT_ID = \"reclamoDraftId\"");
        assertContains("cliente usa mismo parámetro", guard,
                "namespace + \"reclamoDraftId\"");
        assertContains("formato acotado", scope,
                "^[A-Za-z0-9_-]{8,80}$");
        assertContains("legacy compatible", scope,
                "LEGACY_DRAFT_ID = \"legacy\"");
        assertContains("clave namespaced", scope,
                "claveBase + \"::DRAFT::\" + scope");
        assertContains("resolver servlet", scope,
                "resolver(HttpServletRequest request)");
        assertContains("resolver portlet", scope,
                "resolver(PortletRequest request)");
        assertContains("session HTTP soportada", scope,
                "HttpSession session");
        assertContains("session portlet soportada", scope,
                "PortletSession session");
        assertContains("application scope explícito", scope,
                "PortletSession.APPLICATION_SCOPE");
        assertContains("null elimina", scope,
                "if (valor == null)");
        assertContains("draft inválido falla cerrado", scope,
                "Identificador de borrador de reclamo inválido.");
        assertContains("session nula falla cerrado", scope,
                "La sesión es obligatoria para el scope de borrador.");
        assertNotContains("sin valores arbitrarios en clave", scope,
                "claveBase + draftId");

        System.out.println("CONTRATO_DRAFT_SCOPE_RECLAMO_PRESTACIONAL_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
    }

    private static void assertContains(
            String etiqueta, String contenido, String esperado) {
        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontró [" + esperado + "]");
        }
    }

    private static void assertNotContains(
            String etiqueta, String contenido, String prohibido) {
        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": se encontró [" + prohibido + "]");
        }
    }
}
