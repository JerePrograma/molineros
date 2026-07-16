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
}
