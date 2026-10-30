package ar.com.ospim.compras.requerimientos.helper;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import ar.com.ospim.compras.WebKeysCompras;

public final class TextoCompraHelper {
    private static final Pattern DIACRITICOS_TEXTO_COMPRAS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern ESPACIOS_TEXTO_COMPRAS = Pattern.compile("\\s+");

    private TextoCompraHelper() {
    }

    public static String normalizarSectorCompra(
            String value) {

        return normalizarClaveTexto(value);
    }

    public static String normalizarTexto(
            String value) {

        if (value == null) {
            return "";
        }

        return Normalizer.normalize(
                value,
                Normalizer.Form.NFC
        ).trim();
    }

    public static String normalizarClaveTexto(
            String value) {

        String texto = normalizarTexto(value);

        if (texto.length() == 0) {
            return "";
        }

        String normalizado =
                Normalizer.normalize(
                        texto,
                        Normalizer.Form.NFD
                );

        normalizado =
                DIACRITICOS_TEXTO_COMPRAS
                        .matcher(normalizado)
                        .replaceAll("");

        normalizado =
                ESPACIOS_TEXTO_COMPRAS
                        .matcher(normalizado)
                        .replaceAll(" ");

        return normalizado.toUpperCase(Locale.ROOT).trim();
    }

    public static boolean esTituloOrdenMedica(
            String value) {

        return normalizarClaveTexto(WebKeysCompras.TITULO_ORDEN_MEDICA).equals(
                normalizarClaveTexto(value)
        );
    }

    public static String getBooleanDescripcion(
            Boolean value) {

        if (value == null) {
            return "";
        }

        return value.booleanValue()
                ? "Sí"
                : "No";
    }

    public static boolean isEmpty(String value) {
        return value == null
                || value.trim().length() == 0;
    }

    public static String trimToNull(String value) {
        String normalizado = normalizarTexto(value);

        if (normalizado.length() == 0) {
            return null;
        }

        return normalizado;
    }
}
