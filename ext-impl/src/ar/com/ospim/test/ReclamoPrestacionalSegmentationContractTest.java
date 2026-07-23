package ar.com.ospim.test;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Contrato semantico del ensamblado segmentado de view_reclamo. */
public final class ReclamoPrestacionalSegmentationContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset LATIN_1 = Charset.forName("ISO-8859-1");

    private static final String JSP_DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";

    private static final Path WEB_ROOT = Paths.get("ext-web/docroot");

    private static final String[] FRAGMENTS = {
        "view_reclamo_contexto.jspf",
        "view_reclamo_inicio_formulario.jspf",
        "view_reclamo_cabecera.jspf",
        "view_reclamo_afiliado_diagnostico.jspf",
        "view_reclamo_recuperable_neutro.jspf",
        "view_reclamo_prestaciones.jspf",
        "view_reclamo_seguimiento_cierre.jspf",
        "view_reclamo_acciones.jspf",
        "view_reclamo_configuracion.jspf"
    };

    private static final Pattern LOCAL_INCLUDE = Pattern.compile(
            "(?m)^[\\t ]*<%@\\s+include\\s+file=\"([^\"]+)\"\\s*%>"
                    + "[\\t ]*(?:\\r?\\n|$)"
    );

    private ReclamoPrestacionalSegmentationContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Path viewPath = Paths.get(JSP_DIR + "view_reclamo.jsp");
        Path assemblerPath = Paths.get(JSP_DIR + "view_reclamo.jspf");
        Path contextPath = Paths.get(JSP_DIR + "view_reclamo_contexto.jspf");
        Path headerPath = Paths.get(JSP_DIR + "view_reclamo_cabecera.jspf");
        Path configPath = Paths.get(JSP_DIR + "view_reclamo_configuracion.jspf");
        Path baseJsPath = Paths.get(JSP_DIR + "view_reclamo.js");
        Path tabGuardPath = Paths.get(JSP_DIR + "view_reclamo_tab_guard.js");
        Path editorPatchPath = Paths.get(JSP_DIR + "view_reclamo_editor_patch.js");
        Path p0PatchPath = Paths.get(JSP_DIR + "view_reclamo_p0_patch.js");
        Path rulesPatchPath = Paths.get(
                JSP_DIR + "view_reclamo_prestacion_rules_patch.js"
        );
        Path surgePatchPath = Paths.get(
                JSP_DIR + "view_reclamo_compras_surge_patch.js"
        );
        Path productionPatchPath = Paths.get(
                JSP_DIR + "view_reclamo_produccion_7305_patch.js"
        );

        String view = readText(viewPath);
        String assembler = readText(assemblerPath);
        String context = readText(contextPath);
        String header = readText(headerPath);
        String config = readText(configPath);
        String baseJs = readText(baseJsPath);
        String tabGuard = readText(tabGuardPath);
        String editorPatch = readText(editorPatchPath);
        String p0Patch = readText(p0PatchPath);
        String rulesPatch = readText(rulesPatchPath);
        String surgePatch = readText(surgePatchPath);
        String productionPatch = readText(productionPatchPath);

        verifyFragmentOrder(assembler);

        String expanded = expand(
                assemblerPath,
                new HashSet<Path>()
        );

        occurrences(expanded, "<form name=\"<portlet:namespace />reclamo_fm\"", 1);
        occurrences(expanded, "</form>", 1);
        before(expanded, "String cmd =", "<form name=");
        before(expanded, "boolean esBorradorCompras", "<form name=");
        verifyJspComments(expanded);
        verifyScriptletBraces(expanded);

        contains(context, "alta conserva titulo legacy",
                "String nroreclamo = \"Caso Nro 00000\";");
        contains(context, "requerimiento queda en campo inferior",
                "String opAsignadaalReclamo = esBorradorCompras");
        contains(context, "identificador de requerimiento",
                "\"Requerimiento #\"");
        notContains(context, "titulo principal sin requerimiento",
                "Nuevo Reclamo Prestacional - Requerimiento #");
        contains(context, "reclamo persistido conserva titulo",
                "nroreclamo =\"Reclamo Nro : \"");
        contains(context, "orden de pago preservada",
                "opAsignadaalReclamo =\"OP: \"");
        before(header, "<%= nroreclamo %>", "<%= opAsignadaalReclamo %>");

        contains(view, "lee context path una sola vez",
                "String reclamoPrestacionalContextPath = request.getContextPath();");
        contains(view, "normaliza contexto raiz",
                "\"/\".equals(reclamoPrestacionalContextPath)");
        contains(view, "define base unica de assets",
                "String reclamoPrestacionalAssetBase =");
        contains(view, "base termina en directorio de reclamos",
                "+ \"/html/portlet/autorizaciones/reclamos_prestacionales/\"");
        notContains(view, "sin ruta protocol-relative",
                "request.getContextPath() %>/html/");
        notContains(assembler, "assembler sin ruta protocol-relative",
                "request.getContextPath() %>/html/");
        occurrences(view, "src=\"<%= reclamoPrestacionalAssetBase %>", 6);
        occurrences(assembler, "src=\"<%= reclamoPrestacionalAssetBase %>", 1);
        occurrences(view, "onerror=\"window.ReclamoPrestacionalAssetError", 6);
        occurrences(assembler, "onerror=\"window.ReclamoPrestacionalAssetError", 1);

        before(view, "view_reclamo.jspf", "view_reclamo.js?v=");
        before(view, "view_reclamo.js?v=", "view_reclamo_tab_guard.js?v=");
        before(view, "view_reclamo_tab_guard.js?v=",
                "view_reclamo_editor_patch.js?v=");
        before(view, "view_reclamo_editor_patch.js?v=",
                "view_reclamo_p0_patch.js?v=");
        before(view, "view_reclamo_p0_patch.js?v=",
                "view_reclamo_prestacion_rules_patch.js?v=");
        before(view, "view_reclamo_prestacion_rules_patch.js?v=",
                "view_reclamo_produccion_7305_patch.js?v=");

        contains(config, "config expone contexto normalizado",
                "contextPath: '<%= reclamoPrestacionalContextPath %>'");
        contains(config, "config expone base de assets",
                "assetBase: '<%= reclamoPrestacionalAssetBase %>'");

        notContains(view, "operaciones normales sin console.warn", "console.warn");
        notContains(view, "sin aviso normal de filtro asincrono",
                "RECLAMO_PRESTACIONAL_FILTRO_LETRA_ASYNC");
        notContains(view, "sin aviso normal de afiliado asincrono",
                "RECLAMO_PRESTACIONAL_AFILIADO_ASYNC");
        contains(view, "errores reales siguen en consola", "console.error");
        contains(view, "error real de afiliado preservado",
                "RECLAMO_PRESTACIONAL_AFILIADO_ERROR");
        contains(view, "error real de asset preservado",
                "RECLAMO_PRESTACIONAL_ASSET_ERROR");

        contains(baseJs, "save queda exportado con namespace",
                "window[reclamoPrestacionalNamespace + \"saveReclamo\"]"
                        + " = reclamoPrestacional_saveReclamo;");
        contains(baseJs, "editar queda exportado con namespace",
                "window[reclamoPrestacionalNamespace + \"editaReclamo\"]"
                        + " = reclamoPrestacional_editaReclamo;");
        occurrences(baseJs, "function manejarTipoSector(){", 1);
        occurrences(baseJs, "function cambioTipoPedido(){", 1);

        String legacyJs = view
                + baseJs
                + tabGuard
                + editorPatch
                + p0Patch
                + rulesPatch
                + surgePatch
                + productionPatch;

        notContains(legacyJs, "sin funciones flecha", "=>");
        notContains(legacyJs, "sin optional chaining", "?.");
        notContains(legacyJs, "sin fetch", "fetch(");
        notContains(legacyJs, "sin let", "let ");
        notContains(legacyJs, "sin const", "const ");

        verifyEncoding(viewPath, true);
        verifyEncoding(assemblerPath, true);
        verifyEncoding(configPath, true);
        verifyEncoding(contextPath, false);
        verifyEncoding(headerPath, false);
        verifyEncoding(baseJsPath, false);
        verifyEncoding(tabGuardPath, false);
        verifyEncoding(editorPatchPath, false);
        verifyEncoding(p0PatchPath, false);
        verifyEncoding(rulesPatchPath, false);
        verifyEncoding(surgePatchPath, false);
        verifyEncoding(productionPatchPath, false);

        System.out.println("CONTRATO_SEGMENTACION_RECLAMO_PRESTACIONAL_OK");
    }

    private static void verifyFragmentOrder(String assembler) {
        Matcher matcher = LOCAL_INCLUDE.matcher(assembler);
        List<String> includes = new ArrayList<String>();

        while (matcher.find()) {
            String path = matcher.group(1);
            if (path.startsWith(
                    "/html/portlet/autorizaciones/"
                            + "reclamos_prestacionales/view_reclamo_"
            )) {
                includes.add(path.substring(path.lastIndexOf('/') + 1));
            }
        }

        if (!includes.equals(Arrays.asList(FRAGMENTS))) {
            throw new AssertionError(
                    "Orden de fragmentos invalido: " + includes
            );
        }
    }

    private static String expand(Path path, Set<Path> stack) throws Exception {
        Path normalized = path.toAbsolutePath().normalize();

        if (!stack.add(normalized)) {
            throw new AssertionError("Include circular: " + normalized);
        }

        String source = readText(normalized);
        Matcher matcher = LOCAL_INCLUDE.matcher(source);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String include = matcher.group(1);
            String replacement = matcher.group(0);

            if (include.startsWith(
                    "/html/portlet/autorizaciones/reclamos_prestacionales/"
            )) {
                replacement = expand(
                        WEB_ROOT.resolve(include.substring(1)),
                        stack
                );
            }

            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement(replacement)
            );
        }

        matcher.appendTail(result);
        stack.remove(normalized);
        return result.toString();
    }

    private static void verifyEncoding(Path path, boolean asciiRequired)
            throws Exception {

        byte[] bytes = Files.readAllBytes(path);

        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF) {

            throw new AssertionError("BOM UTF-8 no permitido: " + path);
        }

        String text = readText(path);
        notContains(text, "sin reemplazo Unicode en " + path, "\uFFFD");
        notContains(text, "sin mojibake A tilde en " + path, "\u00C3");
        notContains(text, "sin mojibake A circunflejo en " + path, "\u00C2");

        if (asciiRequired) {
            for (int i = 0; i < bytes.length; i++) {
                if ((bytes[i] & 0x80) != 0) {
                    throw new AssertionError(
                            "Archivo modificado no es ASCII/ISO-8859-1: "
                                    + path
                                    + " byte="
                                    + i
                    );
                }
            }
        }
    }

    private static String readText(Path path) throws Exception {
        byte[] bytes = Files.readAllBytes(path);

        try {
            return decodeStrict(bytes, UTF_8);
        } catch (CharacterCodingException expected) {
            return new String(bytes, LATIN_1);
        }
    }

    private static String decodeStrict(byte[] bytes, Charset charset)
            throws CharacterCodingException {

        CharsetDecoder decoder = charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        return decoder.decode(ByteBuffer.wrap(bytes)).toString();
    }

    private static void verifyJspComments(String jsp) {
        int position = 0;

        while (true) {
            int start = jsp.indexOf("<%--", position);
            if (start < 0) {
                return;
            }

            int end = jsp.indexOf("--%>", start + 4);
            if (end < 0) {
                throw new AssertionError(
                        "Comentario JSP sin cierre desde " + start
                );
            }

            position = end + 4;
        }
    }

    private static void verifyScriptletBraces(String jsp) {
        StringBuilder java = new StringBuilder();
        int position = 0;

        while (true) {
            int start = jsp.indexOf("<%", position);
            if (start < 0) {
                break;
            }

            if (jsp.startsWith("<%--", start)) {
                int endComment = jsp.indexOf("--%>", start + 4);
                if (endComment < 0) {
                    throw new AssertionError("Comentario JSP sin cierre");
                }
                position = endComment + 4;
                continue;
            }

            int end = jsp.indexOf("%>", start + 2);
            if (end < 0) {
                throw new AssertionError(
                        "Scriptlet JSP sin cierre desde " + start
                );
            }

            if (!jsp.startsWith("<%@", start)
                    && !jsp.startsWith("<%=", start)
                    && !jsp.startsWith("<%!", start)) {
                java.append(jsp.substring(start + 2, end)).append('\n');
            }

            position = end + 2;
        }

        verifyJavaBraces(java.toString());
    }

    private static void verifyJavaBraces(String java) {
        int depth = 0;
        boolean string = false;
        boolean character = false;
        boolean lineComment = false;
        boolean blockComment = false;
        boolean escape = false;

        for (int i = 0; i < java.length(); i++) {
            char current = java.charAt(i);
            char next = i + 1 < java.length() ? java.charAt(i + 1) : '\0';

            if (lineComment) {
                if (current == '\n') {
                    lineComment = false;
                }
                continue;
            }

            if (blockComment) {
                if (current == '*' && next == '/') {
                    blockComment = false;
                    i++;
                }
                continue;
            }

            if (string || character) {
                if (escape) {
                    escape = false;
                } else if (current == '\\') {
                    escape = true;
                } else if (string && current == '"') {
                    string = false;
                } else if (character && current == '\'') {
                    character = false;
                }
                continue;
            }

            if (current == '/' && next == '/') {
                lineComment = true;
                i++;
                continue;
            }

            if (current == '/' && next == '*') {
                blockComment = true;
                i++;
                continue;
            }

            if (current == '"') {
                string = true;
                continue;
            }

            if (current == '\'') {
                character = true;
                continue;
            }

            if (current == '{') {
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth < 0) {
                    throw new AssertionError(
                            "Llave de cierre sin apertura"
                    );
                }
            }
        }

        if (string || character || blockComment || depth != 0) {
            throw new AssertionError(
                    "Scriptlets Java desbalanceados. depth=" + depth
            );
        }
    }

    private static void contains(
            String text,
            String label,
            String expected) {

        if (text.indexOf(expected) < 0) {
            throw new AssertionError(
                    label + ": no se encontro [" + expected + "]"
            );
        }
    }

    private static void notContains(
            String text,
            String label,
            String forbidden) {

        if (text.indexOf(forbidden) >= 0) {
            throw new AssertionError(
                    label + ": se encontro [" + forbidden + "]"
            );
        }
    }

    private static void before(
            String text,
            String first,
            String second) {

        int firstPosition = text.indexOf(first);
        int secondPosition = text.indexOf(second);

        if (firstPosition < 0
                || secondPosition < 0
                || firstPosition >= secondPosition) {

            throw new AssertionError(
                    "Orden invalido entre ["
                            + first
                            + "] y ["
                            + second
                            + "]"
            );
        }
    }

    private static void occurrences(
            String text,
            String token,
            int expected) {

        int count = 0;
        int position = 0;

        while ((position = text.indexOf(token, position)) >= 0) {
            count++;
            position += token.length();
        }

        if (count != expected) {
            throw new AssertionError(
                    "Cantidad invalida para ["
                            + token
                            + "]: esperada="
                            + expected
                            + " actual="
                            + count
            );
        }
    }
}
