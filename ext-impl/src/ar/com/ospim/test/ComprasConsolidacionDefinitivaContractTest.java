package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contrato ejecutable de la instalacion canonica del modulo Compras.
 */
public final class ComprasConsolidacionDefinitivaContractTest {

    private static final Charset LATIN1 = Charset.forName("ISO-8859-1");
    private static final File SQL_DIR = new File(
            "ext-impl/src/ar/com/ospim/compras/sql"
    );
    private static final File SCHEMA = new File(SQL_DIR, "compras_schema.sql");
    private static final File JAVA_DIR = new File(
            "ext-impl/src/ar/com/ospim/compras"
    );
    private static final File HTML_DIR = new File("ext-web/docroot/html");
    private static final File JSP_DIR = new File(
            HTML_DIR,
            "portlet/compras/requerimientos"
    );

    private static final Pattern FUNCTION = Pattern.compile(
            "(?is)CREATE\\s+(?:OR\\s+REPLACE\\s+)?FUNCTION\\s+"
                    + "((?:compras|autorizaciones)\\.[a-z0-9_]+)\\s*"
                    + "\\((.*?)\\)\\s*RETURNS"
    );
    private static final Pattern JDBC_CALL = Pattern.compile(
            "(?is)\\{\\s*(?:\\?\\s*=\\s*)?call\\s+"
                    + "((?:compras|autorizaciones)\\.[a-z0-9_]+)\\s*"
                    + "\\(([^}]*)\\)\\s*\\}"
    );
    private static final Pattern JSP_REFERENCE = Pattern.compile(
            "(?is)\"([^\"]+\\.jsp)\""
    );

    public static void main(String[] args) throws Exception {
        Map<String, Integer> functions = validarScriptCanonico();
        validarContratosJdbc(functions);
        validarNormalizacionYEncoding();
        validarStrutsTilesYJsp();
        System.out.println("COMPRAS_CONSOLIDACION_DEFINITIVA_OK");
    }

    private static Map<String, Integer> validarScriptCanonico()
            throws Exception {

        File[] sqlFiles = SQL_DIR.listFiles();
        check(sqlFiles != null, "No se pudo leer el directorio SQL");

        int files = 0;
        for (int i = 0; i < sqlFiles.length; i++) {
            if (sqlFiles[i].isFile()) {
                files++;
                check(
                        "compras_schema.sql".equals(sqlFiles[i].getName()),
                        "Existe un SQL incremental: " + sqlFiles[i].getName()
                );
            }
        }
        check(files == 1, "Debe existir un unico archivo SQL");

        String schema = read(SCHEMA);
        String compact = schema.replaceAll("(?s)^\\s*", "");
        check(compact.startsWith("--"), "El schema debe conservar su cabecera");
        check(schema.contains("BEGIN;"), "Falta BEGIN");
        check(!schema.matches(
                        "(?is).*\\bDROP\\s+(?:SCHEMA|TABLE|FUNCTION|TYPE|TRIGGER|CONSTRAINT)\\b.*"),
                "La instalacion canonica no puede contener operaciones DROP");
        check(!schema.matches("(?is).*\\bTRUNCATE\\b.*"),
                "La instalacion canonica no puede truncar datos");
        check(schema.contains("CREATE SCHEMA compras;"),
                "Falta crear el esquema");
        check(schema.trim().endsWith("COMMIT;"), "Falta COMMIT final");
        check(!schema.matches("(?is).*\\\\i\\s+.*"),
                "El schema no puede usar includes");

        Map<String, Integer> functions = new HashMap<String, Integer>();
        Matcher matcher = FUNCTION.matcher(schema);
        while (matcher.find()) {
            String name = matcher.group(1).toLowerCase();
            int arity = arity(matcher.group(2));
            check(!functions.containsKey(name),
                    "Funcion duplicada o sobrecargada: " + name);
            functions.put(name, Integer.valueOf(arity));
        }

        check(functions.containsKey(
                        "autorizaciones.busca_nomenclador_prest_med_compras"),
                "Falta la busqueda tecnica de prestaciones");
        check(!schema.contains("guardar_sector_tipo_prestador("),
                "Permanece el wrapper obsoleto de configuracion");
        check(functions.get("compras.registrar_requerimiento_orden_medica")
                        .intValue() == 11,
                "La Orden medica debe conservar solo su firma canonica");

        return functions;
    }

    private static void validarContratosJdbc(Map<String, Integer> functions)
            throws Exception {

        List<File> javaFiles = files(JAVA_DIR, ".java");
        for (int i = 0; i < javaFiles.size(); i++) {
            String source = read(javaFiles.get(i));
            source = source.replaceAll("\"\\s*\\+\\s*\"", "");
            Matcher matcher = JDBC_CALL.matcher(source);
            while (matcher.find()) {
                String function = matcher.group(1).toLowerCase();
                Integer expected = functions.get(function);
                check(expected != null,
                        "CallableStatement sin funcion SQL: " + function);
                int actual = questionMarks(matcher.group(2));
                check(expected.intValue() == actual,
                        "Firma incompatible para " + function
                                + ": Java=" + actual
                                + ", PostgreSQL=" + expected);
            }
        }
    }

    private static void validarNormalizacionYEncoding() throws Exception {
        String schema = read(SCHEMA);
        String service = read(new File(
                JAVA_DIR,
                "requerimientos/service/EditarRequerimientoCompraServiceImpl.java"
        ));
        String webKeys = read(new File(JAVA_DIR, "WebKeysCompras.java"));
        String documentos = read(new File(
                JAVA_DIR,
                "requerimientos/documentos/DocumentoLibraryComprasHelper.java"
        ));
        String webXml = read(new File(
                "ext-web/docroot/WEB-INF/web.xml"
        ));
        String initCompras = read(new File(
                "ext-web/docroot/html/portlet/compras/init.jsp"
        ));
        List<File> archivosIso = files(JAVA_DIR, ".java");
        archivosIso.addAll(files(new File(
                "ext-web/docroot/html/portlet/compras"
        ), ".jsp"));
        archivosIso.add(SCHEMA);
        archivosIso.add(new File("ext-web/docroot/WEB-INF/web.xml"));
        for (int i = 0; i < archivosIso.size(); i++) {
            validarArchivoIso(archivosIso.get(i));
        }

        check(schema.contains("\\encoding LATIN1"),
                "psql no declara la lectura ISO-8859-1");
        check(schema.contains("'Prestaciones Médicas'"),
                "SQL no contiene la acentuacion final del sector");
        check(schema.contains(
                        "NULLIF(btrim(p_titulo), ''),\n"
                                + "            btrim(p_nombre_original)"),
                "SQL no resuelve el titulo tecnico opcional de la Orden medica");
        check(!schema.contains("'orden medica' THEN"),
                "SQL conserva una validacion artificial del titulo");
        check(webKeys.contains(
                        "TITULO_ORDEN_MEDICA =\n            \"Orden médica\";"),
                "Java no define el título canónico con tilde");
        check(webKeys.contains("Normalizer.Form.NFC"),
                "Java no normaliza texto de negocio a NFC");
        check(webKeys.contains("esTituloOrdenMedica("),
                "Java no compara el título de forma normalizada");
        check(service.contains(
                        "stmt.setString(9, WebKeysCompras.TITULO_ORDEN_MEDICA);"),
                "Java no envía el título canónico centralizado");
        check(documentos.contains("WebKeysCompras.esTituloOrdenMedica("),
                "Document Library no tolera el título histórico sin tilde");
        check(webXml.contains("<page-encoding>ISO-8859-1</page-encoding>"),
                "La aplicación web no fija ISO-8859-1 para Compras");
        check(webXml.startsWith(
                        "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"),
                "web.xml no declara su codificación ISO-8859-1");
        check(initCompras.contains(
                        "pageEncoding=\"ISO-8859-1\""),
                "La fuente JSP de Compras no declara ISO-8859-1");
        check(!initCompras.contains("contentType="),
                "Compras no debe contradecir el UTF-8 común de Liferay");
    }

    private static void validarArchivoIso(File file) throws Exception {
        byte[] bytes = Files.readAllBytes(file.toPath());
        check(bytes.length < 3
                        || (bytes[0] & 0xFF) != 0xEF
                        || (bytes[1] & 0xFF) != 0xBB
                        || (bytes[2] & 0xFF) != 0xBF,
                "El archivo ISO no puede tener BOM UTF-8: " + file);
        check(bytes.length < 2
                        || (bytes[0] & 0xFF) != 0xFF
                        || (bytes[1] & 0xFF) != 0xFE,
                "El archivo ISO no puede tener BOM UTF-16 LE: " + file);
        check(bytes.length < 2
                        || (bytes[0] & 0xFF) != 0xFE
                        || (bytes[1] & 0xFF) != 0xFF,
                "El archivo ISO no puede tener BOM UTF-16 BE: " + file);

        String source = new String(bytes, LATIN1);
        check(source.indexOf('\u00C3') < 0
                        && source.indexOf('\u00C2') < 0
                        && source.indexOf("\u00EF\u00BF\u00BD") < 0,
                "Se detectó mojibake o UTF-8 leído como ISO-8859-1: " + file);
    }

    private static void validarStrutsTilesYJsp() throws Exception {
        Element struts = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File("ext-web/docroot/WEB-INF/struts-config.xml"))
                .getDocumentElement();
        Element tiles = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File("ext-web/docroot/WEB-INF/tiles-defs.xml"))
                .getDocumentElement();

        String tilesSource = read(new File(
                "ext-web/docroot/WEB-INF/tiles-defs.xml"
        ));
        check(!tilesSource.contains(
                        "requerimiento_compra_detalle_componente.jsp"),
                "Tiles contiene una ruta de Compras concatenada a otro JSP");

        Set<String> allTileNames = new HashSet<String>();
        Set<String> tileNames = new HashSet<String>();
        Set<File> roots = new HashSet<File>();
        NodeList definitions = tiles.getElementsByTagName("definition");
        for (int i = 0; i < definitions.getLength(); i++) {
            Element definition = (Element) definitions.item(i);
            String name = definition.getAttribute("name");
            allTileNames.add(name);
            if (name.startsWith("portlet.compras")) {
                check(tileNames.add(name), "Tile duplicado: " + name);
                NodeList puts = definition.getElementsByTagName("put");
                for (int j = 0; j < puts.getLength(); j++) {
                    Element put = (Element) puts.item(j);
                    if ("portlet_content".equals(put.getAttribute("name"))) {
                        File jsp = jspFile(put.getAttribute("value"), null);
                        check(jsp.isFile(), "JSP inexistente en Tiles: " + jsp);
                        roots.add(jsp.getCanonicalFile());
                    }
                }
            }
        }

        Set<String> actionPaths = new HashSet<String>();
        NodeList actions = struts.getElementsByTagName("action");
        for (int i = 0; i < actions.getLength(); i++) {
            Element action = (Element) actions.item(i);
            String path = action.getAttribute("path");
            if (!path.startsWith("/compras/")) {
                continue;
            }
            check(actionPaths.add(path), "Mapping Struts duplicado: " + path);
            String type = action.getAttribute("type");
            if (type.length() > 0) {
                File actionClass = new File(
                        "ext-impl/src/" + type.replace('.', '/') + ".java"
                );
                check(actionClass.isFile(), "Action inexistente: " + type);
            }

            NodeList children = action.getChildNodes();
            Set<String> forwards = new HashSet<String>();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child instanceof Element
                        && "forward".equals(child.getNodeName())) {
                    Element forward = (Element) child;
                    String name = forward.getAttribute("name");
                    String target = forward.getAttribute("path");
                    check(forwards.add(name),
                            "Forward duplicado en " + path + ": " + name);
                    check(allTileNames.contains(target),
                            "Forward sin Tile en " + path + ": " + target);
                }
            }
        }
        check(actionPaths.size() == 23,
                "Se esperaban 23 mappings de Compras");
        check(tileNames.size() == 17,
                "Se esperaban 17 Tiles de Compras");

        Set<File> reachable = new HashSet<File>();
        List<File> pending = new ArrayList<File>(roots);
        while (!pending.isEmpty()) {
            File current = pending.remove(pending.size() - 1).getCanonicalFile();
            if (!reachable.add(current)) {
                continue;
            }
            Matcher refs = JSP_REFERENCE.matcher(read(current));
            while (refs.find()) {
                String reference = refs.group(1);
                if (reference.indexOf("<%") >= 0) {
                    continue;
                }
                File target = jspFile(reference, current.getParentFile());
                if (target.getCanonicalPath().startsWith(
                        JSP_DIR.getCanonicalPath())) {
                    check(target.isFile(),
                            "Referencia a JSP inexistente: " + reference);
                    pending.add(target.getCanonicalFile());
                }
            }
        }

        List<File> allJsp = files(JSP_DIR, ".jsp");
        for (int i = 0; i < allJsp.size(); i++) {
            check(reachable.contains(allJsp.get(i).getCanonicalFile()),
                    "JSP huerfano: " + allJsp.get(i));
        }
    }

    private static File jspFile(String reference, File parent) {
        if (reference.startsWith("/html/")) {
            return new File(HTML_DIR, reference.substring(6));
        }
        if (reference.startsWith("/")) {
            return new File(HTML_DIR, reference.substring(1));
        }
        return new File(parent, reference);
    }

    private static List<File> files(File root, String extension) {
        List<File> result = new ArrayList<File>();
        File[] children = root.listFiles();
        if (children == null) {
            return result;
        }
        for (int i = 0; i < children.length; i++) {
            if (children[i].isDirectory()) {
                result.addAll(files(children[i], extension));
            } else if (children[i].getName().endsWith(extension)) {
                result.add(children[i]);
            }
        }
        return result;
    }

    private static String read(File file) throws Exception {
        return new String(Files.readAllBytes(file.toPath()), LATIN1);
    }

    private static int arity(String arguments) {
        String trimmed = arguments.trim();
        return trimmed.length() == 0 ? 0 : trimmed.split(",").length;
    }

    private static int questionMarks(String arguments) {
        int count = 0;
        for (int i = 0; i < arguments.length(); i++) {
            if (arguments.charAt(i) == '?') {
                count++;
            }
        }
        return count;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private ComprasConsolidacionDefinitivaContractTest() {
    }
}
