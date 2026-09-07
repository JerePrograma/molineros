package ar.com.ospim.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.mchange.v2.c3p0.PooledDataSource;
import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.exceptions.DictamenConcurrenteException;
import ar.com.ospim.autorizaciones.services.EquipoInterdisciplinarioServiceImpl;
import ar.com.ospim.util.ConnectionHelper;

/** Simula cambios posteriores a la lectura Action, sin conectar a una BD. */
public class EquipoInterDictamenTransactionTest implements InvocationHandler {
    private String[] current;
    private List<String> events = new ArrayList<String>();
    private int connections;
    private int writes;
    private int commits;
    private int rollbacks;
    private boolean failRead;
    private boolean failWrite;
    private int row;
    private static int passed;
    private static int failed;

    public static void main(String[] args) throws Exception {
        for (int tipo = 0; tipo < 6; tipo++) {
        run("conflicto despues de lectura inicial", "OTRO", false, false, true, 0, false, tipo);
        run("editado sin concurrencia", "BASE0", false, false, false, 1, false, tipo);
        run("mismo valor concurrente", "NUEVO", false, false, false, 0, false, tipo);
        run("campos diferentes", "BASE0", false, false, false, 1, true, tipo);
        run("fallo lectura dentro transaccion", "BASE0", true, false, true, 0, false, tipo);
        run("fallo escritura rollback", "BASE0", false, true, true, 1, false, tipo);
        }
        System.out.println("EquipoInterDictamenTransactionTest: PASO=" + passed + " FALLO=" + failed);
        if (failed != 0) { throw new AssertionError("Transaccion dictamen: " + failed); }
    }

    private static void run(String name, String currentFirst, boolean failRead, boolean failWrite,
            boolean error, int writes, boolean otherField, int fieldIndex) throws Exception {
        EquipoInterDictamenTransactionTest mock = new EquipoInterDictamenTransactionTest();
        String[] original = new String[] {"BASE0", "BASE1", "BASE2", "BASE3", "BASE4", "BASE5"};
        String[] entered = (String[]) original.clone();
        entered[fieldIndex] = "NUEVO";
        mock.current = (String[]) original.clone();
        mock.current[fieldIndex] = "BASE0".equals(currentFirst) ? original[fieldIndex] : currentFirst;
        if (otherField) { mock.current[(fieldIndex + 1) % 6] = "OTRO CAMPO"; }
        mock.failRead = failRead;
        mock.failWrite = failWrite;
        EquipoInterdisciplinario equipo = new EquipoInterdisciplinario();
        equipo.setId(321);
        equipo.setDictamenes(entered);
        equipo.setDictamenesOrigianles(original);
        Field field = ConnectionHelper.class.getDeclaredField("datasource");
        field.setAccessible(true);
        Object previous = field.get(null);
        field.set(null, mock.proxy(PooledDataSource.class));
        Exception failure = null;
        try { new EquipoInterdisciplinarioServiceImpl().actualizar(equipo, "PRUEBA"); }
        catch (Exception e) { failure = e; }
        finally { field.set(null, previous); }
        boolean ok = (failure != null) == error && mock.connections == 1 && mock.writes == writes
                && mock.commits == (error ? 0 : 1) && mock.rollbacks == (error ? 1 : 0)
                && mock.events.indexOf("H") >= 0
                && mock.events.indexOf("R") > mock.events.indexOf("H");
        if (mock.writes > 0) { ok = ok && mock.events.indexOf("W") > mock.events.indexOf("R"); }
        if (!error && otherField) {
            ok = ok && "OTRO CAMPO".equals(equipo.getDictamen(EquipoInterdisciplinario.DICTAMENES.values()[(fieldIndex + 1) % 6]));
        }
        if (error && !failRead && !failWrite) {
            ok = ok && failure instanceof DictamenConcurrenteException;
            if (failure instanceof DictamenConcurrenteException) {
                DictamenConcurrenteException conflict = (DictamenConcurrenteException) failure;
                String[] labels = new String[] {"Psicolog\u00eda", "M\u00e9dico Auditor",
                        "Trabajadora Social", "Kinesiolog\u00eda", "Legales", "Equipo Interdisciplinario"};
                ok = ok && labels[fieldIndex].equals(conflict.getMessage())
                        && conflict.getTipoDictamen() == fieldIndex
                        && currentFirst.equals(conflict.getValorActualBD())
                        && "NUEVO".equals(conflict.getValorIngresado());
            }
        }
        if (ok) { passed++; } else { failed++; System.out.println("FALLO " + name + " campo=" + fieldIndex + " " + mock.events); }
    }

    private Object proxy(Class type) {
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("getNumBusyConnections".equals(name)) { return Integer.valueOf(0); }
        if ("getConnection".equals(name)) { connections++; return proxy(Connection.class); }
        if ("setAutoCommit".equals(name) || "close".equals(name)) { return null; }
        if ("commit".equals(name)) { commits++; events.add("C"); return null; }
        if ("rollback".equals(name)) { rollbacks++; events.add("B"); return null; }
        if ("next".equals(name)) { return Boolean.valueOf(++row < 6); }
        if ("getInt".equals(name)) { return Integer.valueOf(row); }
        if ("getString".equals(name)) { return current[row]; }
        if ("prepareCall".equals(name)) {
            final String sql = (String) args[0];
            return Proxy.newProxyInstance(CallableStatement.class.getClassLoader(),
                    new Class[] {CallableStatement.class}, new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String name = method.getName();
                    if (name.startsWith("set") || "close".equals(name)) { return null; }
                    if ("executeQuery".equals(name)) {
                        if (!sql.equals("{call autorizaciones.equipo_interdisciplinario_dictamenes_by_id(?)}")) {
                            throw new AssertionError("CALL de lectura inesperada");
                        }
                        events.add("R"); row = -1;
                        if (failRead) { throw new SQLException("Fallo lectura simulado"); }
                        return proxy(ResultSet.class);
                    }
                    if ("executeUpdate".equals(name)) {
                        if (sql.indexOf("update_datos_equipointerdisciplinario(") >= 0) { events.add("H"); }
                        else if (sql.indexOf("inserta_update_equipo_dictamen") >= 0) {
                            events.add("W"); writes++;
                            if (failWrite) { throw new SQLException("Fallo escritura simulado"); }
                        } else { throw new AssertionError("CALL de escritura inesperada"); }
                        return Integer.valueOf(1);
                    }
                    throw new AssertionError("Operacion no prevista: " + name);
                }
            });
        }
        throw new AssertionError("Operacion no prevista: " + name);
    }
}