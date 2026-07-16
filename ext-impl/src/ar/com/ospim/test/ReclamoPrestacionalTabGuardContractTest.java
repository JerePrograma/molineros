package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual de la mitigación cliente para edición simultánea.
 *
 * No representa aislamiento backend: protege el navegador y propaga un
 * draftId para la migración posterior de las claves de sesión.
 */
public final class ReclamoPrestacionalTabGuardContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private ReclamoPrestacionalTabGuardContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );
        String guard = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo_tab_guard.js"
        );

        assertBefore("guard después del legacy", view,
                "view_reclamo.js?v=20260716-p0-4",
                "view_reclamo_tab_guard.js?v=20260716-p0-4");
        assertBefore("guard antes de parches de edición", view,
                "view_reclamo_tab_guard.js?v=20260716-p0-4",
                "view_reclamo_editor_patch.js?v=20260716-p0-4");

        assertContains("draft persistido por pestaña", guard,
                "window.sessionStorage.getItem(SESSION_KEY)");
        assertContains("instancia única por carga", guard,
                "var pageInstanceId = generarId(\"page\")");
        assertContains("lease incluye instancia", guard,
                "instanceId: pageInstanceId");
        assertContains("lease compartido entre pestañas", guard,
                "window.localStorage.setItem(STORAGE_KEY");
        assertContains("heartbeat acotado", guard,
                "var HEARTBEAT_MS = 5000");
        assertContains("lease expirable", guard,
                "var LEASE_MS = 20000");
        assertContains("propiedad por instancia", guard,
                "lease.instanceId !== pageInstanceId");
        assertContains("detecta draft duplicado", guard,
                "lease.draftId === draftId");
        assertContains("regenera draft duplicado", guard,
                "guardarDraftId(generarId(\"rp\"))");
        assertContains("libera sólo lease propio", guard,
                "lease.instanceId === pageInstanceId");
        assertContains("controles bloqueados", guard,
                "control.prop(\"disabled\", true)");
        assertContains("restaura estado original", guard,
                "rpTabGuardDisabled");
        assertContains("toma de control explícita", guard,
                "Tomar control en esta pestaña");
        assertContains("mensaje de prevención", guard,
                "Para evitar sobrescribir datos");
        assertContains("hidden de draft", guard,
                "name: namespace + \"reclamoDraftId\"");
        assertContains("propagación AJAX", guard,
                "jQuery.ajaxPrefilter");
        assertContains("parámetro namespaced", guard,
                "var nombre = namespace + \"reclamoDraftId\"");
        assertContains("actualiza fragmentos dinámicos", guard,
                "jQuery(document).ajaxComplete");
        assertContains("reacciona a otra pestaña", guard,
                "window.addEventListener(\"storage\"");
        assertContains("libera al cerrar", guard,
                "window.addEventListener(\"beforeunload\"");
        assertContains("API diagnóstica", guard,
                "window.ReclamoPrestacionalTabGuard");
        assertContains("expone instancia diagnóstica", guard,
                "getInstanceId");
        assertNotContains("sin bloqueo indefinido", guard,
                "while (true)");

        System.out.println("CONTRATO_TAB_GUARD_RECLAMO_PRESTACIONAL_OK");
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
                            + primero + "] y [" + segundo + "]");
        }
    }
}
