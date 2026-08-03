package ar.com.ospim.test;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual para impedir que el cliente AppMobile legacy vuelva a
 * incorporar credenciales o autenticación paralela.
 */
public final class ClienteAppMobileLegacySecurityContractTest {

    private static final Charset ISO_8859_1 =
        Charset.forName("ISO-8859-1");
    private static final Charset LATIN_1 = Charset.forName("ISO-8859-1");

    private ClienteAppMobileLegacySecurityContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String legacy = leerLegacy(
                "ext-impl/src/ar/com/ospim/desarrolloAppMobile/beans/"
                        + "ClienteAppMobile.java"
        );
        String auth = leerLegacy(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoAppMobileAuthClient.java"
        );

        assertContains(
                "cliente legacy importa autenticación segura",
                legacy,
                "import ar.com.ospim.autorizaciones.services."
                        + "ReclamoAppMobileAuthClient;"
        );
        assertContains(
                "obtenerToken delega exclusivamente",
                legacy,
                "return ReclamoAppMobileAuthClient.obtenerToken();"
        );
        assertNotContains(
                "api key literal prohibida",
                legacy,
                "private static final String API_KEY"
        );
        assertNotContains(
                "email literal prohibido",
                legacy,
                "private static final String EMAIL"
        );
        assertNotContains(
                "password literal prohibido",
                legacy,
                "private static final String PASSWORD"
        );
        assertNotContains(
                "endpoint de login duplicado prohibido",
                legacy,
                "private static final String LOGIN_URL"
        );
        assertNotContains(
                "header de api key legacy prohibido",
                legacy,
                "post.addRequestHeader(\"api-key\", API_KEY)"
        );
        assertNotContains(
                "cuerpo de credenciales legacy prohibido",
                legacy,
                "String jsonBody = String.format"
        );
        assertNotContains(
                "respuesta de login legacy prohibida",
                legacy,
                "Error login AppMobile. Status:"
        );

        assertContains("host configurable", auth, "APP_HOST_WEBSERVICE");
        assertContains("api key configurable", auth, "APP_BACKOFFICE_API_KEY");
        assertContains("email configurable", auth, "APP_BACKOFFICE_EMAIL");
        assertContains("password configurable", auth, "APP_BACKOFFICE_PASSWORD");
        assertContains(
                "cliente seguro limita logs de respuesta",
                auth,
                "responseLength=\" + longitud(response)"
        );

        System.out.println("CONTRATO_CLIENTE_APPMOBILE_LEGACY_SEGURO_OK");
    }

    private static String leerLegacy(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        byte[] bytes = Files.readAllBytes(path);
        CharsetDecoder decoder = UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            return new String(bytes, LATIN_1);
        }
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
