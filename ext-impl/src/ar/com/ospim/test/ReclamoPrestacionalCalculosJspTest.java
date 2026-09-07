package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/** Ejecuta las funciones ES5 tomadas de las JSP reales mediante Nashorn de Java 8. */
public class ReclamoPrestacionalCalculosJspTest {
    public static void main(String[] args) throws Exception {
        String base = "ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/";
        String principal = leer(base + "view_reclamo.jsp");
        String edicion = leer(base + "datos_edicion_prestacion.jsp");
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        if (engine == null) { throw new AssertionError("Se requiere Nashorn del JDK 8 configurado."); }
        engine.eval("var valores = {}; function jQuery(id) { return { val: function(v) { if (arguments.length) { valores[id] = v; return this; } return valores[id]; } }; }");
        engine.eval(funcion(principal, "_rp_numeroReclamo"));
        String[][] casos = {
            { "calculatotal", "importe", "cantidad", "total" },
            { "calculatotalFC", "importeUnitarioFC", "cantidadFC", "importeFC" },
            { "calculatotalEdicion", "importeEdicion", "cantidadEdicion", "totalEdicion" },
            { "calculatotalFCEdicion", "importeUnitarioFC_edicion", "cantidadFC_edicion", "importeFC_edicion" }
        };
        String enorme = "1";
        for (int n = 0; n < 200; n++) { enorme += "0"; }
        String enormeRedondeo = enorme;
        for (int n = 200; n < 307; n++) { enormeRedondeo += "0"; }
        int pruebas = 0;
        for (int i = 0; i < casos.length; i++) {
            String[] caso = casos[i];
            engine.eval(funcion(i < 2 ? principal : edicion, caso[0]));
            probar(engine, caso, "12.50", "2", "25", true); pruebas++;
            probar(engine, caso, "12,50", "2", "25", true); pruebas++;
            probar(engine, caso, "2", "1,5", "3", true); pruebas++;
            probar(engine, caso, "0", "2", "0", true); pruebas++;
            probar(engine, caso, "", "2", "", false); pruebas++;
            probar(engine, caso, "12x", "2", "", false); pruebas++;
            probar(engine, caso, "1.2.3", "2", "", false); pruebas++;
            probar(engine, caso, "1.235", "1", "1.24", true); pruebas++;
            probar(engine, caso, enorme, enorme, "", false); pruebas++;
            probar(engine, caso, enormeRedondeo, "1", i == 2 ? enormeRedondeo : "", i == 2); pruebas++;
        }
        if (edicion.indexOf("function calculatotal()") >= 0) {
            throw new AssertionError("La edicion sobrescribe el calculo principal");
        }
        System.out.println("RP_CALCULOS_JSP_REALES_OK casos=" + pruebas);
    }
    private static String leer(String ruta) throws Exception {
        return new String(Files.readAllBytes(new File(ruta).toPath()), Charset.forName("ISO-8859-1"));
    }
    private static String funcion(String jsp, String nombre) {
        String fuente = jsp.replace("<portlet:namespace />", "_rp_").replace("<portlet:namespace/>", "_rp_");
        Matcher m = Pattern.compile("(?ms)^function " + Pattern.quote(nombre) + "\\s*\\([^)]*\\)\\s*\\{.*?^\\}").matcher(fuente);
        if (!m.find()) { throw new AssertionError("No existe funcion " + nombre); }
        return m.group();
    }
    private static void probar(ScriptEngine e, String[] c, String importe, String cantidad, String esperado, boolean valido) throws Exception {
        e.put("importePrueba", importe); e.put("cantidadPrueba", cantidad);
        e.eval("valores['#_rp_" + c[1] + "'] = importePrueba; valores['#_rp_" + c[2] + "'] = cantidadPrueba;");
        e.eval("valores['#_rp_" + c[3] + "'] = '123.45';");
        Object result = e.eval(c[0] + "()");
        String actual = String.valueOf(e.eval("valores['#_rp_" + c[3] + "']"));
        if (valido != Boolean.TRUE.equals(result)) { throw new AssertionError(c[0] + " aceptacion " + importe + ": " + result); }
        if (valido) {
            if (Double.parseDouble(actual) != Double.parseDouble(esperado)) { throw new AssertionError(c[0] + ": " + actual); }
        } else if (!esperado.equals(actual)) { throw new AssertionError(c[0] + " invalido: " + actual); }
    }
}