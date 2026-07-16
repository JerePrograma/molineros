package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual de timeouts para evitar requests indefinidos a AppMobile.
 */
public final class ReclamoAppMobileHttpTimeoutContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private ReclamoAppMobileHttpTimeoutContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String auth = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileAuthClient.java"
        );
        String sync = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileSyncClient.java"
        );

        validarCliente("autenticación", auth);
        validarCliente("sincronización", sync);

        System.out.println("CONTRATO_TIMEOUT_APPMOBILE_RECLAMO_OK");
    }

    private static void validarCliente(String nombre, String contenido) {
        assertContains(nombre + " connection timeout", contenido,
                "CONNECTION_TIMEOUT_MS = 3000");
        assertContains(nombre + " socket timeout", contenido,
                "SOCKET_TIMEOUT_MS = 7000");
        assertContains(nombre + " configura connection timeout", contenido,
                ".setConnectionTimeout(CONNECTION_TIMEOUT_MS)");
        assertContains(nombre + " configura socket timeout", contenido,
                ".setSoTimeout(SOCKET_TIMEOUT_MS)");
        assertBefore(nombre + " configura antes de ejecutar", contenido,
                "configurarTimeouts(httpClient);",
                "httpClient.executeMethod(post)");
        assertContains(nombre + " libera conexión", contenido,
                "post.releaseConnection();");
        assertNotContains(nombre + " no usa timeout infinito", contenido,
                "CONNECTION_TIMEOUT_MS = 0");
        assertNotContains(nombre + " no usa socket infinito", contenido,
                "SOCKET_TIMEOUT_MS = 0");
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
