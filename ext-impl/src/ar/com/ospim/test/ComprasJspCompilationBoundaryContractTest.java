package ar.com.ospim.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contrato estructural para mantener acotadas las unidades JSP de Compras.
 */
public final class ComprasJspCompilationBoundaryContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    private static final Path DOCROOT =
            Paths.get("ext-web", "docroot").toAbsolutePath().normalize();

    private static final Path TEST_SOURCE = Paths.get(
            "ext-impl",
            "src",
            "ar",
            "com",
            "ospim",
            "test",
            "ComprasJspCompilationBoundaryContractTest.java"
    ).toAbsolutePath().normalize();

    private static final int MAX_STATIC_BYTES = 60000;
    private static final int MAX_STATIC_NODES = 15;
    private static final int MAX_STATIC_DEPTH = 5;

    private static final String REQUIREMENTS =
            "/html/portlet/compras/requerimientos";
    private static final String PARTIALS = REQUIREMENTS + "/partials";
    private static final String RUNTIME = PARTIALS + "/runtime";

    private static final String ROOT_ALTA =
            REQUIREMENTS + "/requerimiento_alta.jsp";
    private static final String ROOT_EDICION =
            REQUIREMENTS + "/requerimiento_edicion.jsp";
    private static final String ROOT_VISTA =
            REQUIREMENTS + "/requerimiento_vista.jsp";
    private static final String LAYOUT_EDICION =
            PARTIALS + "/_layout_edicion.jsp";
    private static final String LAYOUT_VISTA =
            PARTIALS + "/_layout_vista.jsp";
    private static final String MODEL =
            PARTIALS + "/_modelo_requerimiento.jsp";
    private static final String PUBLISHER =
            PARTIALS + "/_publicar_contexto_requerimiento.jsp";
    private static final String RUNTIME_INIT =
            RUNTIME + "/_runtime_init.jsp";
    private static final String RUNTIME_JS_HELPER =
            RUNTIME + "/_runtime_js_helper.jsp";

    private static final String[] LARGE_STATIC_BODIES = new String[] {
        PARTIALS + "/_mensajes.jsp",
        PARTIALS + "/_datos_basicos.jsp",
        PARTIALS + "/_adjudicacion.jsp",
        PARTIALS + "/_orden_medica_alta.jsp",
        PARTIALS + "/_orden_medica_vista.jsp",
        PARTIALS + "/_botonera.jsp",
        PARTIALS + "/_scripts_edicion.jsp",
        PARTIALS + "/_scripts_vista.jsp"
    };

    private static final String[] RUNTIME_WRAPPERS = new String[] {
        RUNTIME + "/_mensajes_runtime.jsp",
        RUNTIME + "/_datos_basicos_runtime.jsp",
        RUNTIME + "/_adjudicacion_runtime.jsp",
        RUNTIME + "/_orden_medica_alta_runtime.jsp",
        RUNTIME + "/_orden_medica_vista_runtime.jsp",
        RUNTIME + "/_botonera_runtime.jsp",
        RUNTIME + "/_scripts_edicion_runtime.jsp",
        RUNTIME + "/_scripts_vista_runtime.jsp"
    };

    private static final String[] EDIT_RUNTIME_INCLUDES =
            RUNTIME_WRAPPERS;

    private static final String[] VIEW_RUNTIME_INCLUDES = new String[] {
        RUNTIME + "/_mensajes_runtime.jsp",
        RUNTIME + "/_datos_basicos_runtime.jsp",
        RUNTIME + "/_adjudicacion_runtime.jsp",
        RUNTIME + "/_orden_medica_vista_runtime.jsp",
        RUNTIME + "/_botonera_runtime.jsp",
        RUNTIME + "/_scripts_vista_runtime.jsp"
    };

    private static final Path[] BOM_CHECK_PATHS = new Path[] {
        webPath(ROOT_ALTA),
        webPath(ROOT_EDICION),
        webPath(ROOT_VISTA),
        webPath(LAYOUT_EDICION),
        webPath(LAYOUT_VISTA),
        webPath(MODEL),
        webPath(PUBLISHER),
        webPath(RUNTIME_INIT),
        webPath(RUNTIME_JS_HELPER),
        webPath(RUNTIME_WRAPPERS[0]),
        webPath(RUNTIME_WRAPPERS[1]),
        webPath(RUNTIME_WRAPPERS[2]),
        webPath(RUNTIME_WRAPPERS[3]),
        webPath(RUNTIME_WRAPPERS[4]),
        webPath(RUNTIME_WRAPPERS[5]),
        webPath(RUNTIME_WRAPPERS[6]),
        webPath(RUNTIME_WRAPPERS[7]),
        TEST_SOURCE
    };

    private static final Pattern STATIC_INCLUDE = Pattern.compile(
            "<%@\\s*include\\s+file\\s*=\\s*[\"']([^\"']+)[\"']"
                    + "[^%]*%>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private ComprasJspCompilationBoundaryContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Path altaPath = webPath(ROOT_ALTA);
        Path edicionPath = webPath(ROOT_EDICION);
        Path vistaPath = webPath(ROOT_VISTA);
        Path layoutEdicionPath = webPath(LAYOUT_EDICION);
        Path layoutVistaPath = webPath(LAYOUT_VISTA);

        String alta = readRequired(altaPath);
        String edicion = readRequired(edicionPath);
        String vista = readRequired(vistaPath);
        String layoutEdicion = readRequired(layoutEdicionPath);
        String layoutVista = readRequired(layoutVistaPath);

        assertRootContract(
                "alta",
                altaPath,
                alta,
                LAYOUT_EDICION
        );
        assertRootContract(
                "edicion",
                edicionPath,
                edicion,
                LAYOUT_EDICION
        );
        assertRootContract(
                "vista",
                vistaPath,
                vista,
                LAYOUT_VISTA
        );

        assertNoLargeStaticBodies(
                "layout edicion",
                layoutEdicionPath,
                layoutEdicion
        );
        assertNoLargeStaticBodies(
                "layout vista",
                layoutVistaPath,
                layoutVista
        );
        assertRuntimeIncludes(
                "layout edicion",
                layoutEdicion,
                EDIT_RUNTIME_INCLUDES
        );
        assertRuntimeIncludes(
                "layout vista",
                layoutVista,
                VIEW_RUNTIME_INCLUDES
        );

        for (int i = 0; i < RUNTIME_WRAPPERS.length; i++) {
            Path wrapper = webPath(RUNTIME_WRAPPERS[i]);
            readRequired(wrapper);
            assertStaticBoundary(RUNTIME_WRAPPERS[i], wrapper);
        }

        for (int i = 0; i < BOM_CHECK_PATHS.length; i++) {
            readRequired(BOM_CHECK_PATHS[i]);
            assertNoBom(BOM_CHECK_PATHS[i]);
        }

        assertStaticBoundary("alta", altaPath);
        assertStaticBoundary("edicion", edicionPath);
        assertStaticBoundary("vista", vistaPath);

        System.out.println("COMPRAS_JSP_COMPILATION_BOUNDARY_OK");
    }

    private static void assertRootContract(
            String name,
            Path rootPath,
            String jsp,
            String layoutPath) {

        int init = staticIncludePosition(
                rootPath,
                jsp,
                "/html/portlet/compras/init.jsp"
        );
        int model = staticIncludePosition(rootPath, jsp, MODEL);
        int publisher = staticIncludePosition(
                rootPath,
                jsp,
                PUBLISHER
        );
        int layout = staticIncludePosition(rootPath, jsp, layoutPath);

        assertTrue(name + " includes compras/init.jsp", init >= 0);
        assertTrue(name + " includes model", model >= 0);
        assertTrue(name + " includes context publisher", publisher >= 0);
        assertTrue(name + " includes layout", layout >= 0);
        assertTrue(
                name + " orders init, model, publisher and layout",
                init < model && model < publisher && publisher < layout
        );
    }

    private static void assertNoLargeStaticBodies(
            String name,
            Path layoutPath,
            String jsp) {

        for (int i = 0; i < LARGE_STATIC_BODIES.length; i++) {
            String body = LARGE_STATIC_BODIES[i];
            assertTrue(
                    name + " does not statically include " + body,
                    staticIncludePosition(layoutPath, jsp, body) < 0
            );
        }
    }

    private static void assertRuntimeIncludes(
            String name,
            String jsp,
            String[] expectedPaths) {

        for (int i = 0; i < expectedPaths.length; i++) {
            String path = expectedPaths[i];
            Pattern runtimeInclude = Pattern.compile(
                    "<jsp:include\\b[^>]*\\bpage\\s*=\\s*"
                            + "[\"']"
                            + Pattern.quote(path)
                            + "[\"'][^>]*/\\s*>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            );
            assertTrue(
                    name + " runtime includes " + path,
                    runtimeInclude.matcher(jsp).find()
            );
        }
    }

    private static int staticIncludePosition(
            Path source,
            String jsp,
            String expectedWebPath) {

        Path expected = webPath(expectedWebPath);
        Matcher matcher = STATIC_INCLUDE.matcher(jsp);

        while (matcher.find()) {
            Path actual = resolveStaticTarget(source, matcher.group(1));
            if (expected.equals(actual)) {
                return matcher.start();
            }
        }

        return -1;
    }

    private static void assertStaticBoundary(
            String name,
            Path rootPath) throws IOException {

        StaticMetrics metrics = new StaticMetrics();
        inspectStaticTree(
                rootPath,
                1,
                new ArrayDeque<Path>(),
                metrics
        );

        assertTrue(
                name + " known static bytes=" + metrics.bytes,
                metrics.bytes <= MAX_STATIC_BYTES
        );
        assertTrue(
                name + " known static nodes=" + metrics.nodes,
                metrics.nodes <= MAX_STATIC_NODES
        );
        assertTrue(
                name + " static depth=" + metrics.maxDepth,
                metrics.maxDepth <= MAX_STATIC_DEPTH
        );
    }

    private static void inspectStaticTree(
            Path path,
            int depth,
            Deque<Path> activeStack,
            StaticMetrics metrics) throws IOException {

        Path normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(DOCROOT)
                || !Files.isRegularFile(normalized)) {
            return;
        }

        if (activeStack.contains(normalized)) {
            throw new AssertionError(
                    "Static include cycle at "
                            + normalized
                            + " through "
                            + activeStack
            );
        }

        activeStack.addLast(normalized);
        try {
            byte[] bytes = Files.readAllBytes(normalized);
            String jsp = new String(bytes, LATIN1);

            metrics.bytes += bytes.length;
            metrics.nodes++;
            if (depth > metrics.maxDepth) {
                metrics.maxDepth = depth;
            }

            Matcher matcher = STATIC_INCLUDE.matcher(jsp);
            while (matcher.find()) {
                Path child = resolveStaticTarget(
                        normalized,
                        matcher.group(1)
                );
                if (child != null
                        && child.startsWith(DOCROOT)
                        && Files.isRegularFile(child)) {

                    inspectStaticTree(
                            child,
                            depth + 1,
                            activeStack,
                            metrics
                    );
                }
            }
        } finally {
            Path removed = activeStack.removeLast();
            assertTrue(
                    "active static stack order",
                    normalized.equals(removed)
            );
        }
    }

    private static Path resolveStaticTarget(
            Path source,
            String includePath) {

        Path target;
        if (includePath.startsWith("/")) {
            target = DOCROOT.resolve(includePath.substring(1));
        } else {
            target = source.getParent().resolve(includePath);
        }

        target = target.toAbsolutePath().normalize();
        if (!target.startsWith(DOCROOT)) {
            return null;
        }
        return target;
    }

    private static Path webPath(String path) {
        assertTrue("absolute web path: " + path, path.startsWith("/"));
        Path resolved = DOCROOT.resolve(path.substring(1))
                .toAbsolutePath()
                .normalize();
        assertTrue(
                "web path under docroot: " + path,
                resolved.startsWith(DOCROOT)
        );
        return resolved;
    }

    private static String readRequired(Path path) throws IOException {
        assertTrue(
                "required file exists: " + path,
                Files.isRegularFile(path)
        );
        return new String(Files.readAllBytes(path), LATIN1);
    }

    private static void assertNoBom(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        boolean utf8Bom = bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF;
        boolean utf16LeBom = bytes.length >= 2
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xFE;
        boolean utf16BeBom = bytes.length >= 2
                && (bytes[0] & 0xFF) == 0xFE
                && (bytes[1] & 0xFF) == 0xFF;

        assertTrue("no UTF-8 BOM: " + path, !utf8Bom);
        assertTrue("no UTF-16 LE BOM: " + path, !utf16LeBom);
        assertTrue("no UTF-16 BE BOM: " + path, !utf16BeBom);
    }

    private static void assertTrue(String description, boolean condition) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    private static final class StaticMetrics {
        private long bytes;
        private int nodes;
        private int maxDepth;
    }
}
