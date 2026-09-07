package ar.com.ospim.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import ar.com.ospim.autorizaciones.action.EquipoInterBaseAction;
import ar.com.ospim.autorizaciones.action.EquipoInterBaseAction.DictamenConcurrenteException;

public class EquipoInterDictamenConcurrencyTest {
    private static final EquipoInterBaseAction ACTION = new EquipoInterBaseAction();
    private static Method resolver;
    private static int passed;
    private static int failed;

    public static void main(String[] args) throws Exception {
        resolver = EquipoInterBaseAction.class.getDeclaredMethod("resolverDictamen",
                new Class[] {String.class, String.class, String.class, String.class, Integer.TYPE});
        resolver.setAccessible(true);
        for (int tipo = 0; tipo < 6; tipo++) {
            check("sin modificar", "BASE", "BASE", "OTRO", "OTRO", false, tipo);
            check("modificado", "NUEVO", "BASE", "BASE", "NUEVO", false, tipo);
            check("mismo valor", "NUEVO", "BASE", "NUEVO", "NUEVO", false, tipo);
            check("conflicto", "NUEVO", "BASE", "OTRO", null, true, tipo);
            check("ausente", null, "BASE", "BASE", "BASE", false, tipo);
            check("disabled concurrente", null, "BASE", "OTRO", "OTRO", false, tipo);
            check("borrado explicito", "", "BASE", "BASE", "", false, tipo);
            check("borrado concurrente", "", "BASE", "OTRO", null, true, tipo);
            check("original ausente", "NUEVO", null, "BASE", null, true, tipo);
            check("original ausente BD vacia", "NUEVO", null, "", null, true, tipo);
            check("original ausente mismo valor", "BASE", null, "BASE", "BASE", false, tipo);
            check("vacios", "", "", null, "", false, tipo);
            check("saltos con edicion", "A\r\nNUEVO", "A\r\nB", "A\nB", "A\r\nNUEVO", false, tipo);
            check("saltos sin edicion", "A\r\nB", "A\nB", "OTRO", "OTRO", false, tipo);
            check("saltos mismo valor", "A\rB", "BASE", "A\nB", "A\nB", false, tipo);
            check("especiales", "\u00d1 \u00e1 <b> & \"\nNUEVO", "BASE", "BASE",
                    "\u00d1 \u00e1 <b> & \"\nNUEVO", false, tipo);
        }
        System.out.println("EquipoInterDictamenConcurrencyTest: PASO=" + passed + " FALLO=" + failed);
        if (failed != 0) { throw new AssertionError("Regresiones de dictamen: " + failed); }
    }

    private static void check(String caso, String ingresado, String original, String actual,
            String esperado, boolean conflicto, int tipo) throws Exception {
        boolean ok;
        try {
            String result = (String) resolver.invoke(ACTION,
                    new Object[] {ingresado, original, actual, "Dictamen", Integer.valueOf(tipo)});
            ok = !conflicto && esperado.equals(result);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (!(cause instanceof DictamenConcurrenteException)) { throw e; }
            DictamenConcurrenteException c = (DictamenConcurrenteException) cause;
            ok = conflicto && c.getTipoDictamen() == tipo
                    && actual.equals(c.getValorActualBD()) && ingresado.equals(c.getValorIngresado());
        }
        if (ok) { passed++; } else {
            failed++;
            System.out.println("FALLO " + caso + " tipo=" + tipo);
        }
    }
}