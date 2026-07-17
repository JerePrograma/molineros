package ar.com.ospim.test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/** Valida estructura de scriptlets y preservación UTF-8 del editor JSP. */
public final class ReclamoPrestacionalJspStructureContractTest {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Path JSP = Paths.get(
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/datos_edicion_prestacion.jsp"
    );

    private ReclamoPrestacionalJspStructureContractTest() {
    }

    public static void main(String[] args) throws Exception {
        byte[] bytes = Files.readAllBytes(JSP);
        String contenido = decodificarUtf8Estricto(bytes);
        if (!Arrays.equals(bytes, contenido.getBytes(UTF_8))) {
            throw new AssertionError("El JSP no conserva una codificación UTF-8 estable");
        }
        if (contenido.indexOf("HtmlUtil.escapeJS") >= 0) {
            throw new AssertionError("El JSP usa HtmlUtil.escapeJS, ausente en Liferay 5.2");
        }

        String java = extraerScriptlets(contenido);
        validarLlaves(java);
        exigirUnaVez(contenido, "if (prestacionEnEdicion != null) {");

        System.out.println("CONTRATO_ESTRUCTURA_JSP_EDITOR_RECLAMO_OK");
    }

    private static String decodificarUtf8Estricto(byte[] bytes)
            throws CharacterCodingException {
        CharsetDecoder decoder = UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer chars = decoder.decode(ByteBuffer.wrap(bytes));
        return chars.toString();
    }

    private static String extraerScriptlets(String jsp) {
        StringBuilder java = new StringBuilder();
        int pos = 0;
        while (true) {
            int inicio = jsp.indexOf("<%", pos);
            if (inicio < 0) {
                break;
            }
            int fin = jsp.indexOf("%>", inicio + 2);
            if (fin < 0) {
                throw new AssertionError("Scriptlet JSP sin cierre desde posición " + inicio);
            }

            if (!jsp.startsWith("<%@", inicio)
                    && !jsp.startsWith("<%=", inicio)
                    && !jsp.startsWith("<%!", inicio)
                    && !jsp.startsWith("<%--", inicio)) {
                java.append(jsp.substring(inicio + 2, fin)).append('\n');
            }
            pos = fin + 2;
        }
        return java.toString();
    }

    private static void validarLlaves(String java) {
        int profundidad = 0;
        boolean cadena = false;
        boolean caracter = false;
        boolean lineaComentario = false;
        boolean bloqueComentario = false;
        boolean escape = false;

        for (int i = 0; i < java.length(); i++) {
            char actual = java.charAt(i);
            char siguiente = i + 1 < java.length() ? java.charAt(i + 1) : '\0';

            if (lineaComentario) {
                if (actual == '\n') {
                    lineaComentario = false;
                }
                continue;
            }
            if (bloqueComentario) {
                if (actual == '*' && siguiente == '/') {
                    bloqueComentario = false;
                    i++;
                }
                continue;
            }
            if (cadena || caracter) {
                if (escape) {
                    escape = false;
                } else if (actual == '\\') {
                    escape = true;
                } else if (cadena && actual == '"') {
                    cadena = false;
                } else if (caracter && actual == '\'') {
                    caracter = false;
                }
                continue;
            }
            if (actual == '/' && siguiente == '/') {
                lineaComentario = true;
                i++;
                continue;
            }
            if (actual == '/' && siguiente == '*') {
                bloqueComentario = true;
                i++;
                continue;
            }
            if (actual == '"') {
                cadena = true;
                continue;
            }
            if (actual == '\'') {
                caracter = true;
                continue;
            }
            if (actual == '{') {
                profundidad++;
            } else if (actual == '}') {
                profundidad--;
                if (profundidad < 0) {
                    throw new AssertionError("Llave de cierre sin apertura en scriptlets JSP");
                }
            }
        }

        if (cadena || caracter || bloqueComentario) {
            throw new AssertionError("Literal o comentario Java sin cierre en scriptlets JSP");
        }
        if (profundidad != 0) {
            throw new AssertionError("Llaves Java desbalanceadas en scriptlets JSP: " + profundidad);
        }
    }

    private static void exigirUnaVez(String contenido, String esperado) {
        int primero = contenido.indexOf(esperado);
        int segundo = primero < 0 ? -1 : contenido.indexOf(esperado, primero + esperado.length());
        if (primero < 0 || segundo >= 0) {
            throw new AssertionError("Se esperaba una única ocurrencia de [" + esperado + "]");
        }
    }
}
