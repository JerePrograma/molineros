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
        String client = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileSyncClient.java"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamosPrestacionesServiceUtil.java"
        );
        String directOutbox = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileOutboxDirectService.java"
        );

        assertContains(
                "éxito limitado a HTTP 200/204",
                client,
                "status == 200 || status == 204"
        );
        assertContains(
                "respuesta exitosa explícita",
                client,
                "return true;"
        );
        assertContains(
                "respuesta fallida explícita",
                client,
                "return false;"
        );
        assertContains(
                "host desde configuración",
                client,
                "APP_HOST_WEBSERVICE"
        );
        assertContains(
                "respuesta externa limitada",
                client,
                "limitar(response, 1000)"
        );
        assertContains(
                "conexión liberada",
                client,
                "post.releaseConnection();"
        );

        assertContains(
                "servicio usa cliente confirmado",
                service,
                "ReclamoAppMobileSyncClient"
        );
        assertContains(
                "resultado de sincronización evaluado",
                service,
                "if (!sincronizado)"
        );
        assertContains(
                "HTTP no confirmado queda pendiente",
                service,
                "motivo=HTTP_NO_CONFIRMADO"
        );
        assertContains(
                "éxito sólo después de confirmación",
                service,
                "Anulación confirmada por AppMobile"
        );
        assertNotContains(
                "mensaje ambiguo de solicitud enviada",
                service,
                "Solicitud de anulación enviada a AppMobile"
        );

        assertContains(
                "registro concurrente de bajas",
                service,
                "new ConcurrentHashMap<Integer, Long>()"
        );
        assertContains(
                "ventana limitada de compatibilidad",
                service,
                "BAJA_RECIENTE_TTL_MS = 60000L"
        );
        assertContains(
                "baja registrada después de persistencia local",
                service,
                "registrarBajaReciente(id);"
        );
        assertBefore(
                "registro posterior a baja local",
                service,
                "getInstance().borrar(id, user.getScreenName());",
                "registrarBajaReciente(id);"
        );
        assertContains(
                "relectura inmediata suprimida",
                service,
                "if (esBajaReciente(id))"
        );
        assertContains(
                "relectura devuelve nulo",
                service,
                "Se omite relectura de Reclamo Prestacional dado de baja"
        );
        assertContains(
                "limpieza de marcas expiradas",
                service,
                "limpiarBajasRecientesExpiradas"
        );

        assertContains(
                "outbox registrada después de baja",
                service,
                "BAJA_LOCAL_CONFIRMADA"
        );
        assertContains(
                "token nulo persistido",
                service,
                "registrarOutboxSeguro("
        );
        assertContains(
                "error HTTP persistido",
                service,
                "HTTP_NO_CONFIRMADO"
        );
        assertContains(
                "excepción resumida en outbox",
                service,
                "EXCEPCION: "
        );
        assertContains(
                "fallo de outbox no revierte baja",
                service,
                "RECLAMO_APP_OUTBOX_UNAVAILABLE"
        );
        assertContains(
                "confirmación pendiente visible",
                service,
                "RECLAMO_APP_OUTBOX_CONFIRM_PENDING"
        );

        assertBefore(
                "baja antes de registrar outbox",
                service,
                "getInstance().borrar(id, user.getScreenName());",
                "BAJA_LOCAL_CONFIRMADA"
        );
        assertBefore(
                "outbox antes de obtener token",
                service,
                "BAJA_LOCAL_CONFIRMADA",
                "ClienteAppMobile.obtenerToken()"
        );
        assertBefore(
                "HTTP antes de confirmar outbox",
                service,
                "boolean sincronizado = ReclamoAppMobileSyncClient",
                "confirmarOutboxSeguro(idReintegroApp.intValue(), \"AN\")"
        );
        assertBefore(
                "confirmación outbox antes de log de éxito",
                service,
                "confirmarOutboxSeguro(idReintegroApp.intValue(), \"AN\")",
                "Anulación confirmada por AppMobile"
        );

        assertContains(
                "confirmación por clave externa",
                directOutbox,
                "WHERE id_reintegro_app = ?"
        );
        assertContains(
                "confirmación sólo de pendientes",
                directOutbox,
                "AND procesado_en IS NULL"
        );
        assertContains(
                "estado procesado persistido",
                directOutbox,
                "estado_proceso = 'PROCESADO'"
        );
        assertContains(
                "confirmación transaccional",
                directOutbox,
                "con.commit();"
        );
        assertContains(
                "rollback de confirmación",
                directOutbox,
                "ConnectionHelper.rollback(con)"
        );

        System.out.println("CONTRATO_SYNC_APPMOBILE_RECLAMO_OK");
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
