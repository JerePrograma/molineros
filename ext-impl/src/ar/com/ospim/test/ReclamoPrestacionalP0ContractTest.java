package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual ejecutable sin dependencias de Liferay.
 *
 * Verifica exclusivamente que la capa de estabilización P0 y la integración
 * focalizada de producción 7305 permanezcan conectadas después de cambios en
 * los JSP legacy. Las deudas históricas de ReclamosBaseAction se diagnostican
 * en un contrato separado y no bloqueante.
 */
public final class ReclamoPrestacionalP0ContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String JSP_DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";

    private ReclamoPrestacionalP0ContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(JSP_DIR + "view_reclamo.jsp");
        String init = leer(JSP_DIR + "init.jsp");
        String patch = leer(JSP_DIR + "view_reclamo_p0_patch.js");
        String produccion7305 = leer(
                JSP_DIR + "view_reclamo_produccion_7305_patch.js"
        );

        assertBefore(
                "patch P0 después de legacy",
                view,
                "view_reclamo.js?v=20260717-legacy-flows-1",
                "view_reclamo_p0_patch.js?v=20260717-legacy-flows-1"
        );
        assertBefore(
                "integración 7305 después de reglas vigentes",
                view,
                "view_reclamo_prestacion_rules_patch.js?v=20260720-recuperable-neutro-2",
                "view_reclamo_produccion_7305_patch.js?v=20260722-prod-7305-1"
        );
        assertContains(
                "vista conserva ensamblado segmentado",
                view,
                "view_reclamo.jspf"
        );
        assertContains(
                "normaliza fecha seccional vacía",
                view,
                "normalizarFechaOpcional(\"fechaseccional\")"
        );
        assertContains(
                "normaliza fecha de cierre vacía",
                view,
                "normalizarFechaOpcional(\"fechacierre\")"
        );
        assertContains(
                "intercepta submitForm",
                view,
                "window.submitForm = submitFormNormalizado"
        );
        assertContains(
                "submit compatible con jQuery legacy",
                view,
                ").submit(normalizarFechasOpcionales)"
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
                "rollback visual si falla revisión",
                patch,
                "restaurarEstadoCierre(estadoAnterior);"
        );
        assertContains(
                "advierte cierre parcial",
                patch,
                "La revisión fue registrada, pero el cierre del reclamo no se completó."
        );
        assertContains(
                "flags enviados",
                patch,
                "chk_entramite: campo(\"chk_entramite\").is(\":checked\")"
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
                "P0 no reimplementa Tipo Pedido x Sector",
                patch,
                "renderModoSector"
        );
        assertNotContains(
                "P0 no sobrescribe handler legacy",
                patch,
                "window.manejarTipoSector"
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

        assertContains(
                "lista autorizada incorporada",
                init,
                "listaRevisionEstadoAutorizado"
        );
        assertContains(
                "cache autorizado incorporado",
                init,
                "RECLAMOS_PRESTACIONALES_REVISION_ESTADO_AUTORIZADO_EN_SESION"
        );
        assertContains(
                "servicio autorizado incorporado",
                init,
                "getReclamosPrestacionalesRevisionEstadoAutorizado()"
        );

        assertContains(
                "campo de bloqueo por plan",
                produccion7305,
                "plan_reclamo_bloqueado"
        );
        assertContains(
                "campo de nombre de plan",
                produccion7305,
                "nombre_plan_reclamo_bloqueado"
        );
        assertContains(
                "plan Cobertura bloqueado",
                produccion7305,
                "planNormalizado === \"COBERTURA\""
        );
        assertContains(
                "plan Cobertura Total O bloqueado",
                produccion7305,
                "planNormalizado === \"COBERTURA TOTAL O\""
        );
        assertContains(
                "plan Cobertura Total M bloqueado",
                produccion7305,
                "planNormalizado === \"COBERTURA TOTAL M\""
        );
        assertContains(
                "monitoreo de plan de producción",
                produccion7305,
                "verificarPlanAfiliadoDelReclamo,\n                500"
        );
        assertContains(
                "guarda alta protegida",
                produccion7305,
                "namespace + \"saveReclamo\""
        );
        assertContains(
                "guarda edición protegida",
                produccion7305,
                "namespace + \"editaReclamo\""
        );
        assertContains(
                "alta de prestación valida emisión",
                produccion7305,
                "namespace + \"agregarPrestacion\""
        );
        assertContains(
                "edición de prestación valida emisión",
                produccion7305,
                "namespace + \"editarPrestacionSeleccionada\""
        );
        assertContains(
                "mensaje de fecha de producción",
                produccion7305,
                "La fecha de prestación no puede ser posterior a la fecha de emisión"
        );
        assertNotContains(
                "integración 7305 sin jQuery.on",
                produccion7305,
                ".on("
        );
        assertNotContains(
                "integración 7305 sin AJAX síncrono",
                produccion7305,
                "async: false"
        );
        assertNotContains(
                "integración 7305 sin funciones flecha",
                produccion7305,
                "=>"
        );
        assertNotContains(
                "integración 7305 sin let",
                produccion7305,
                "let "
        );
        assertNotContains(
                "integración 7305 sin const",
                produccion7305,
                "const "
        );
        assertNotContains(
                "integración 7305 sin fetch",
                produccion7305,
                "fetch("
        );

        assertSinMarcadores("init.jsp", init);
        assertSinMarcadores("view_reclamo.jsp", view);
        assertArchivoAusente(JSP_DIR + "init.jsp.mine");
        assertArchivoAusente(JSP_DIR + "init.jsp.r7295");
        assertArchivoAusente(JSP_DIR + "init.jsp.r7305");
        assertArchivoAusente(JSP_DIR + "view_reclamo.jsp.mine");
        assertArchivoAusente(JSP_DIR + "view_reclamo.jsp.r7295");
        assertArchivoAusente(JSP_DIR + "view_reclamo.jsp.r7305");

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

    private static void assertSinMarcadores(
            String etiqueta,
            String contenido) {

        assertNotContains(etiqueta + " sin inicio de conflicto", contenido, "<<<<<<<");
        assertNotContains(etiqueta + " sin base de conflicto", contenido, "|||||||");
        assertNotContains(etiqueta + " sin separador de conflicto", contenido, "=======");
        assertNotContains(etiqueta + " sin fin de conflicto", contenido, ">>>>>>>");
    }

    private static void assertArchivoAusente(String ruta) {
        if (Files.exists(Paths.get(ruta))) {
            throw new AssertionError(
                    "persiste artefacto SVN de conflicto: " + ruta
            );
        }
    }
}
