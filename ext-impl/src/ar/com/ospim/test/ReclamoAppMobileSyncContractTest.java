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
