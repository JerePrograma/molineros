package ar.com.ospim.test;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Contrato byte a byte y funcional del ensamblado segmentado de view_reclamo. */
public final class ReclamoPrestacionalSegmentationContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset LATIN_1 = Charset.forName("ISO-8859-1");
    private static final String JSP_DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";
    private static final Path WEB_ROOT = Paths.get("ext-web/docroot");
    private static final String EXPECTED_SHA_256 =
            "1658a26ec69943f61e50f4b90b4f66db5794006ce9c67d87986dc730f638be7c";
    private static final int EXPECTED_BYTES = 83454;
    private static final int EXPECTED_LINES = 1944;

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
    private static final Pattern ASYNC_FALSE = Pattern.compile(
            "async\\s*:\\s*false"
    );

    private ReclamoPrestacionalSegmentationContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = readUtf8(Paths.get(JSP_DIR + "view_reclamo.jsp"));
        String assembler = readUtf8(Paths.get(JSP_DIR + "view_reclamo.jspf"));
        String context = readUtf8(Paths.get(JSP_DIR + "view_reclamo_contexto.jspf"));
        String start = readUtf8(Paths.get(JSP_DIR + "view_reclamo_inicio_formulario.jspf"));
        String header = readUtf8(Paths.get(JSP_DIR + "view_reclamo_cabecera.jspf"));
        String affiliate = readUtf8(
                Paths.get(JSP_DIR + "view_reclamo_afiliado_diagnostico.jspf")
        );
        String neutral = readUtf8(
                Paths.get(JSP_DIR + "view_reclamo_recuperable_neutro.jspf")
        );
        String services = readUtf8(Paths.get(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "ReclamoPrestacionalCompraPrecargaServiceUtil.java"
        ));
        String handoff = readUtf8(Paths.get(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "IniciarReclamoPrestacionalCompraAction.java"
        ));
        String contextBean = readLegacy(Paths.get(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "ReclamoPrestacionalCompraContexto.java"
        ));
        String baseJs = readLegacy(Paths.get(JSP_DIR + "view_reclamo.js"));
        String tabGuard = readUtf8(Paths.get(JSP_DIR + "view_reclamo_tab_guard.js"));
        String editorPatch = readUtf8(
                Paths.get(JSP_DIR + "view_reclamo_editor_patch.js")
        );
        String p0Patch = readUtf8(Paths.get(JSP_DIR + "view_reclamo_p0_patch.js"));
        String rulesPatch = readUtf8(
                Paths.get(JSP_DIR + "view_reclamo_prestacion_rules_patch.js")
        );

        verifyFragmentOrder(assembler);
        verifyAssemblerGlue(assembler);

        String expanded = expand(
                Paths.get(JSP_DIR + "view_reclamo.jspf"),
                new HashSet<Path>()
        );
        byte[] expandedBytes = expanded.getBytes(UTF_8);
        Path expandedPath = Paths.get(
                System.getProperty("java.io.tmpdir"),
                "view_reclamo.expandido.jspf"
        );
        Files.write(expandedPath, expandedBytes);

        equalsValue("SHA-256 del cuerpo canónico", EXPECTED_SHA_256,
                sha256(expandedBytes));
        equalsValue("tamaño del cuerpo canónico", EXPECTED_BYTES,
                expandedBytes.length);
        equalsValue("líneas del cuerpo canónico", EXPECTED_LINES,
                countByte(expandedBytes, (byte) '\n'));

        before(assembler, "tipoEdicionOriginalAntesDePrestaciones",
                "view_reclamo_recuperable_neutro.jspf");
        before(assembler, "view_reclamo_recuperable_neutro.jspf",
                "view_reclamo_prestaciones.jspf");
        before(assembler, "view_reclamo_prestaciones.jspf",
                "request.removeAttribute(\"tipoEdicion\")");
        contains(assembler, "default temporal sólo con prestación activa",
                "PRESTACION_EN_PROCESO_DE_EDICION");
        contains(assembler, "restaura únicamente el atributo agregado",
                "if (aplicarTipoEdicionComun)");

        occurrences(expanded, "<form name=\"<portlet:namespace />reclamo_fm\"", 1);
        occurrences(expanded, "</form>", 1);
        before(expanded, "String cmd =", "<form name=");
        before(expanded, "ReclamoPrestacional  reclamoprestacional", "<form name=");
        before(expanded, "boolean esBorradorCompras", "<form name=");
        before(expanded, "int idReclamoPantalla", "id=\"<portlet:namespace />id_reclamosel\"");

        contains(context, "CMD por atributo de backend",
                "String cmd = (String) request.getAttribute(Constants.CMD)");
        contains(context, "fallback CMD por parámetro legacy",
                "ParamUtil.getString(request, Constants.CMD, \"\")");
        before(context, "request.getAttribute(Constants.CMD)",
                "ParamUtil.getString(request, Constants.CMD, \"\")");
        contains(context, "borrador Compras sólo durante ADD",
                "boolean esBorradorCompras = esAlta && contextoCompras != null");
        contains(context, "bean de contexto comprobado por tipo",
                "instanceof ReclamoPrestacionalCompraContexto");
        contains(context, "nonce validado", "contextoCompras.coincideNonce(nonceCompras)");
        contains(context, "usuario validado", "contextoCompras.perteneceAUsuario");
        contains(context, "vigencia validada", "contextoCompras.estaVigente");
        contains(context, "sólo ID positivo es persistido",
                "reclamoprestacional.getId_reclamo() > 0");
        contains(context, "ID de alta y borrador en cero",
                "int idReclamoPantalla = esAlta || !existeReclamoPersistido");
        contains(context, "estado observado protegido ante bean nulo",
                "nuevoEstadoObservadoObj != null && reclamoprestacional != null");
        contains(context, "título de alta normal restaurado",
                ": \"Nuevo Reclamo Prestacional\"");
        contains(context, "título de Compras restaurado",
                "Nuevo Reclamo Prestacional - Requerimiento #");
        contains(header, "título dinámico renderizado sin tratarlo como clave",
                "<label><b><%= nroreclamo %></b></label>");

        contains(start, "hidden CMD", "name=\"<portlet:namespace /><%= Constants.CMD %>\"");
        contains(start, "hidden nonce", "PARAM_RECLAMO_PRESTACIONAL_NONCE");
        contains(start, "hidden ID del reclamo", "id=\"<portlet:namespace />id_reclamosel\"");
        contains(start, "hidden de acción de prestación",
                "id=\"<portlet:namespace />tipoaccionprestacion\"");
        contains(affiliate, "CUIL del bean temporal",
                "reclamoprestacional.getCuit_titular()");
        contains(affiliate, "integrante del bean temporal",
                "reclamoprestacional.getInte()");

        contains(neutral, "lista temporal neutralizada",
                "LISTADO_PRESTACIONES_RECLAMOS_EN_SESION");
        contains(neutral, "prestación activa neutralizada",
                "PRESTACION_EN_PROCESO_DE_EDICION");
        contains(neutral, "Recuperable servidor cero",
                "prestacionNeutra.setRecuperable(");
        contains(neutral, "Reconocido servidor cero",
                "prestacionNeutra.setReconocidoSSS(");
        contains(expanded, "porcentaje OSPIM visible",
                "Cargo OSPIM: <%= cargoOspimPorcentajeInicial %>%");
        contains(expanded, "porcentaje prestadora visible",
                "Cargo Prestadora: <%= cargoPrestadoraPorcentajeInicial %>%");

        contains(handoff, "inicio con nonce aleatorio", "UUID.randomUUID().toString()");
        contains(handoff, "navegación ADD", "Constants.ADD");
        contains(handoff, "origen Compras", "ORIGEN_COMPRAS");
        notContains(handoff, "requerimiento no se envía como id_reclamosel",
                "setParameter(\n                \"id_reclamosel\"");
        contains(contextBean, "vigencia de dos horas",
                "2L * 60L * 60L * 1000L");
        contains(contextBean, "comparación de nonce", "coincideNonce");
        contains(contextBean, "comparación de usuario", "perteneceAUsuario");
        contains(contextBean, "comparación de vigencia", "estaVigente");
        contains(services, "misma lista en cabecera", "reclamo.setPrestaciones(");
        contains(services, "primera prestación activa", "prestaciones.get(0)");
        contains(services, "OBSERVACION trazable", "\"ART-\" + detalle.getIdInt()");
        contains(services, "ID médico cero", "prestacion.setId_prestacion(");
        notContains(services, "no inventa ID de reclamo", "setId_reclamo(");
        contains(services, "Cargo OSPIM transferido", "prestacion.setCargo_ospim(");
        contains(services, "Cargo prestadora transferido", "prestacion.setCargo_ps(");

        contains(expanded, "config de alta", "esAlta: <%= esAlta %>");
        contains(expanded, "config de edición", "esEdicion: <%= esEdicion %>");
        contains(expanded, "config de borrador Compras",
                "esBorradorCompras: <%= esBorradorCompras %>");
        contains(expanded, "config de ID", "idReclamo: <%= idReclamoPantalla %>");

        before(view, "window.ReclamoPrestacionalNamespace",
                "ajaxNoBloqueante.__rpFiltroLetraNoBloqueante = true");
        before(view, "ajaxNoBloqueante.__rpFiltroLetraNoBloqueante = true",
                "ajaxAfiliadoNoBloqueante.__rpAfiliadoNoBloqueante = true");
        before(view, "ajaxAfiliadoNoBloqueante.__rpAfiliadoNoBloqueante = true",
                "window.ReclamoPrestacionalJQueryLoadOriginal");
        before(view, "window.ReclamoPrestacionalJQueryLoadOriginal",
                "view_reclamo.jspf");
        before(view, "view_reclamo.jspf", "view_reclamo.js?v=");
        before(view, "view_reclamo.js?v=", "view_reclamo_tab_guard.js?v=");
        before(view, "view_reclamo_tab_guard.js?v=", "view_reclamo_editor_patch.js?v=");
        before(view, "view_reclamo_editor_patch.js?v=", "view_reclamo_p0_patch.js?v=");
        before(view, "view_reclamo_p0_patch.js?v=",
                "view_reclamo_prestacion_rules_patch.js?v=");
        before(view, "view_reclamo_prestacion_rules_patch.js?v=",
                "normalizarFechasOpcionales");
        occurrences(view, "view_reclamo.js?v=", 1);
        occurrences(view, "view_reclamo_tab_guard.js?v=", 1);
        occurrences(view, "view_reclamo_editor_patch.js?v=", 1);
        occurrences(view, "view_reclamo_p0_patch.js?v=", 1);
        occurrences(view, "view_reclamo_prestacion_rules_patch.js?v=", 1);

        occurrences(baseJs, "function manejarTipoSector(){", 1);
        occurrences(baseJs, "function cambioTipoPedido(){", 1);
        notContains(p0Patch, "P0 no duplica Tipo Pedido/Sector", "renderModoSector");
        notContains(p0Patch, "P0 no reemplaza manejarTipoSector",
                "window.manejarTipoSector");
        contains(tabGuard, "draft hidden namespaced",
                "name: namespace + \"reclamoDraftId\"");
        contains(tabGuard, "draft propagado por AJAX", "jQuery.ajaxPrefilter");

        String allLegacyJs = view + baseJs + tabGuard + editorPatch + p0Patch + rulesPatch;
        notContains(allLegacyJs, "sin jQuery.on incompatible", ".on(");
        notContains(allLegacyJs, "sin funciones flecha", "=>");
        notContains(allLegacyJs, "sin fetch", "fetch(");
        notContains(allLegacyJs, "sin optional chaining", "?.");
        notContains(allLegacyJs, "sin let", "let ");
        notContains(allLegacyJs, "sin const", "const ");
        noAsyncFalse(editorPatch, "editor patch");
        noAsyncFalse(p0Patch, "P0 patch");
        noAsyncFalse(rulesPatch, "prestación rules patch");
        noAsyncFalse(tabGuard, "tab guard");

        verifyJspComments(expanded);
        verifyScriptletBraces(expanded);
        verifyEncodings();

        System.out.println("EXPANSION=" + expandedPath.toAbsolutePath());
        System.out.println("SHA256=" + sha256(expandedBytes));
        System.out.println("BYTES=" + expandedBytes.length);
        System.out.println("LINES=" + countByte(expandedBytes, (byte) '\n'));
        System.out.println("CONTRATO_SEGMENTACION_RECLAMO_PRESTACIONAL_OK");
    }

    private static void verifyFragmentOrder(String assembler) {
        Matcher matcher = LOCAL_INCLUDE.matcher(assembler);
        List<String> includes = new ArrayList<String>();
        while (matcher.find()) {
            String path = matcher.group(1);
            if (path.startsWith("/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/view_reclamo_")) {
                includes.add(path.substring(path.lastIndexOf('/') + 1));
            }
        }
        if (!includes.equals(Arrays.asList(FRAGMENTS))) {
            throw new AssertionError("Orden de fragmentos inválido: " + includes);
        }
    }

    private static void verifyAssemblerGlue(String assembler) {
        String glue = LOCAL_INCLUDE.matcher(assembler).replaceAll("");
        notContains(glue, "assembler sin HTML de negocio", "<form");
        notContains(glue, "assembler sin scripts", "<script");
        notContains(glue, "assembler sin handlers", "function ");
        contains(glue, "glue guarda atributo original",
                "tipoEdicionOriginalAntesDePrestaciones");
        contains(glue, "glue aplica default temporal", "aplicarTipoEdicionComun");
    }

    private static String expand(Path path, Set<Path> stack) throws Exception {
        Path normalized = path.toAbsolutePath().normalize();
        if (!stack.add(normalized)) {
            throw new AssertionError("Include circular: " + normalized);
        }

        String source = readUtf8(normalized);
        Matcher matcher = LOCAL_INCLUDE.matcher(source);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String include = matcher.group(1);
            String replacement = matcher.group(0);
            if (include.startsWith(
                    "/html/portlet/autorizaciones/reclamos_prestacionales/")) {
                replacement = expand(
                        WEB_ROOT.resolve(include.substring(1)),
                        stack
                );
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        stack.remove(normalized);
        return result.toString();
    }

    private static void verifyEncodings() throws Exception {
        readUtf8(Paths.get(JSP_DIR + "view_reclamo.jsp"));
        readUtf8(Paths.get(JSP_DIR + "view_reclamo.jspf"));
        for (String fragment : FRAGMENTS) {
            String text = readUtf8(Paths.get(JSP_DIR + fragment));
            notContains(text, "fragmento sin carácter de reemplazo: " + fragment,
                    "\uFFFD");
        }

        Path editorPath = Paths.get(JSP_DIR + "datos_edicion_prestacion.jsp");
        byte[] editorBytes = Files.readAllBytes(editorPath);
        String editor = new String(editorBytes, LATIN_1);
        if (!Arrays.equals(editorBytes, editor.getBytes(LATIN_1))) {
            throw new AssertionError("El editor no hace round-trip ISO-8859-1");
        }
        boolean invalidUtf8 = false;
        try {
            decodeStrict(editorBytes, UTF_8);
        } catch (CharacterCodingException expected) {
            invalidUtf8 = true;
        }
        if (!invalidUtf8) {
            throw new AssertionError("El editor legacy dejó de ser ISO-8859-1");
        }
    }

    private static String readUtf8(Path path) throws Exception {
        return decodeStrict(Files.readAllBytes(path), UTF_8);
    }

    private static String readLegacy(Path path) throws Exception {
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
                throw new AssertionError("Comentario JSP sin cierre en " + start);
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
                int end = jsp.indexOf("--%>", start + 4);
                if (end < 0) {
                    throw new AssertionError("Comentario JSP sin cierre");
                }
                position = end + 4;
                continue;
            }
            int end = jsp.indexOf("%>", start + 2);
            if (end < 0) {
                throw new AssertionError("Scriptlet JSP sin cierre en " + start);
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
            } else if (current == '/' && next == '*') {
                blockComment = true;
                i++;
            } else if (current == '"') {
                string = true;
            } else if (current == '\'') {
                character = true;
            } else if (current == '{') {
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth < 0) {
                    throw new AssertionError("Llave Java de cierre sin apertura");
                }
            }
        }
        if (depth != 0 || string || character || blockComment) {
            throw new AssertionError("Scriptlets Java desbalanceados: " + depth);
        }
    }

    private static void noAsyncFalse(String javascript, String label) {
        if (ASYNC_FALSE.matcher(javascript).find()) {
            throw new AssertionError(label + " contiene AJAX síncrono");
        }
    }

    private static String sha256(byte[] bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] value = digest.digest(bytes);
        StringBuilder result = new StringBuilder();
        for (byte item : value) {
            result.append(String.format("%02x", item & 0xff));
        }
        return result.toString();
    }

    private static int countByte(byte[] bytes, byte expected) {
        int count = 0;
        for (byte item : bytes) {
            if (item == expected) {
                count++;
            }
        }
        return count;
    }

    private static void contains(String content, String label, String expected) {
        if (content.indexOf(expected) < 0) {
            throw new AssertionError(label + ": no se encontró [" + expected + "]");
        }
    }

    private static void notContains(String content, String label, String forbidden) {
        if (content.indexOf(forbidden) >= 0) {
            throw new AssertionError(label + ": se encontró [" + forbidden + "]");
        }
    }

    private static void before(String content, String first, String second) {
        int firstPosition = content.indexOf(first);
        int secondPosition = content.indexOf(second);
        if (firstPosition < 0 || secondPosition < 0 || firstPosition >= secondPosition) {
            throw new AssertionError(
                    "Orden inválido entre [" + first + "] y [" + second + "]"
            );
        }
    }

    private static void occurrences(String content, String expected, int count) {
        int actual = 0;
        int position = 0;
        while ((position = content.indexOf(expected, position)) >= 0) {
            actual++;
            position += expected.length();
        }
        if (actual != count) {
            throw new AssertionError(
                    "Se esperaban " + count + " apariciones de [" + expected
                            + "] y se encontraron " + actual
            );
        }
    }

    private static void equalsValue(String label, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    label + ": esperado=" + expected + ", actual=" + actual
            );
        }
    }
}
