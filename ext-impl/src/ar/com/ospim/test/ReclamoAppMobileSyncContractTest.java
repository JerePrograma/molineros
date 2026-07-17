package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual de la sincronización de bajas con AppMobile.
 */
public final class ReclamoAppMobileSyncContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private ReclamoAppMobileSyncContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String auth = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileAuthClient.java"
        );
        String client = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileSyncClient.java"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamosPrestacionesServiceUtil.java"
        );
        String outbox = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileOutboxService.java"
        );
        String directOutbox = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileOutboxDirectService.java"
        );
        String transactionalDelete = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoPrestacionalBajaTransaccionalService.java"
        );

        assertContains("host de autenticación configurable", auth,
                "APP_HOST_WEBSERVICE");
        assertContains("api key configurable", auth,
                "APP_BACKOFFICE_API_KEY");
        assertContains("email configurable", auth,
                "APP_BACKOFFICE_EMAIL");
        assertContains("password configurable", auth,
                "APP_BACKOFFICE_PASSWORD");
        assertContains("configuración leída por servicio", auth,
                "TraeListasServiceUtil.getSystemConfig(clave)");
        assertContains("credenciales en JSON", auth,
                "body.put(\"email\", email)");
        assertContains("password en JSON", auth,
                "body.put(\"password\", password)");
        assertContains("autenticación exige HTTP 200", auth,
                "if (status != 200)");
        assertContains("token obligatorio", auth,
                "json.optString(\"token\", \"\")");
        assertContains("conexión de login liberada", auth,
                "post.releaseConnection();");
        assertContains("login registra sólo longitud", auth,
                "responseLength=\" + longitud(response)");
        assertNotContains("login no registra cuerpo", auth,
                "respuesta=\" +");
        assertNotContains("email literal prohibido", auth,
                "private static final String EMAIL");
        assertNotContains("password literal prohibido", auth,
                "private static final String PASSWORD");
        assertNotContains("api key literal prohibida", auth,
                "private static final String API_KEY");

        assertContains("éxito limitado a HTTP 200/204", client,
                "status == 200 || status == 204");
        assertContains("respuesta exitosa explícita", client, "return true;");
        assertContains("respuesta fallida explícita", client, "return false;");
        assertContains("host desde configuración", client,
                "APP_HOST_WEBSERVICE");
        assertContains("sincronización registra sólo longitud", client,
                "responseLength=\" + longitud(response)");
        assertNotContains("sincronización no registra cuerpo", client,
                "respuesta=\" +");
        assertNotContains("sincronización no limita cuerpo para log", client,
                "limitar(response");
        assertContains("conexión liberada", client,
                "post.releaseConnection();");

        assertContains("baja usa autenticación segura", service,
                "ReclamoAppMobileAuthClient.obtenerToken()");
        assertNotContains("baja no usa autenticación legacy", service,
                "ClienteAppMobile.obtenerToken()");
        assertContains("worker usa autenticación segura", outbox,
                "ReclamoAppMobileAuthClient.obtenerToken()");
        assertNotContains("worker no usa autenticación legacy", outbox,
                "ClienteAppMobile.obtenerToken()");

        assertContains("servicio usa cliente confirmado", service,
                "ReclamoAppMobileSyncClient");
        assertContains("resultado de sincronización evaluado", service,
                "if (!sincronizado)");
        assertContains("HTTP no confirmado queda pendiente", service,
                "motivo=HTTP_NO_CONFIRMADO");
        assertContains("éxito sólo después de confirmación", service,
                "Anulación confirmada por AppMobile");
        assertNotContains("mensaje ambiguo de solicitud enviada", service,
                "Solicitud de anulación enviada a AppMobile");

        assertNotContains("cache concurrente de bajas eliminado", service,
                "ConcurrentHashMap<Integer, Long>");
        assertNotContains("ventana temporal eliminada", service,
                "BAJA_RECIENTE_TTL_MS");
        assertNotContains("registro temporal eliminado", service,
                "registrarBajaReciente(id)");
        assertNotContains("guard temporal eliminado", service,
                "if (esBajaReciente(id))");
        assertNotContains("limpieza temporal eliminada", service,
                "limpiarBajasRecientesExpiradas");

        assertContains("token nulo persistido", service,
                "registrarOutboxSeguro(");
        assertContains("error HTTP persistido", service,
                "HTTP_NO_CONFIRMADO");
        assertContains("excepción resumida en outbox", service,
                "EXCEPCION: ");
        assertContains("fallo de outbox visible", service,
                "RECLAMO_APP_OUTBOX_UNAVAILABLE");
        assertContains("confirmación pendiente visible", service,
                "RECLAMO_APP_OUTBOX_CONFIRM_PENDING");
        assertNotContains("baja antigua prohibida en utilitario", service,
                "getInstance().borrar(id, user.getScreenName());");

        assertBefore("baja transaccional antes del token", service,
                "ReclamoPrestacionalBajaTransaccionalService.borrar(",
                "ReclamoAppMobileAuthClient.obtenerToken()");
        assertBefore("HTTP antes de confirmar outbox", service,
                "boolean sincronizado = ReclamoAppMobileSyncClient",
                "confirmarOutboxSeguro(idReintegroApp.intValue(), \"AN\")");
        assertBefore("confirmación outbox antes de log de éxito", service,
                "confirmarOutboxSeguro(idReintegroApp.intValue(), \"AN\")",
                "Anulación confirmada por AppMobile");

        assertContains("stored procedure conservado", transactionalDelete,
                "autorizaciones.borra_reclamo_prestacional");
        assertContains("outbox usa misma conexión", transactionalDelete,
                "registrarOutboxEnTransaccion(");
        assertContains("motivo inicial persistido", transactionalDelete,
                "BAJA_LOCAL_CONFIRMADA");
        assertBefore("baja antes de outbox en la transacción",
                transactionalDelete,
                "resultado = baja.executeQuery();",
                "registrarOutboxEnTransaccion(");
        assertBefore("outbox antes del commit",
                transactionalDelete,
                "registrarOutboxEnTransaccion(",
                "con.commit();");
        assertContains("resultado cero bloquea baja", transactionalDelete,
                "resultado.getInt(1) == 0");
        assertContains("rollback de baja transaccional", transactionalDelete,
                "ConnectionHelper.rollback(con)");

        assertContains("confirmación por clave externa", directOutbox,
                "WHERE id_reintegro_app = ?");
        assertContains("confirmación sólo de pendientes", directOutbox,
                "AND procesado_en IS NULL");
        assertContains("estado procesado persistido", directOutbox,
                "estado_proceso = 'PROCESADO'");
        assertContains("confirmación transaccional", directOutbox,
                "con.commit();");
        assertContains("rollback de confirmación", directOutbox,
                "ConnectionHelper.rollback(con)");

        System.out.println("CONTRATO_SYNC_APPMOBILE_RECLAMO_OK");
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
