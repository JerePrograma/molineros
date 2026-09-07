package ar.com.ospim.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mchange.v2.c3p0.PooledDataSource;
import ar.com.ospim.autorizaciones.services.EquipoInterdisciplinarioServiceImpl;
import ar.com.ospim.util.ConnectionHelper;

/** Dobles JDBC sin conexiones reales: prueba la implementacion del servicio. */
public class EquipoInterDictamenLecturaTest implements InvocationHandler {
    private String sql;
    private String column;
    private int id;
    private int tipo;
    private boolean failQuery;
    private boolean userQuery;
    private int rows;
    private int closed;

    public static void main(String[] args) throws Exception {
        EquipoInterDictamenLecturaTest mock = new EquipoInterDictamenLecturaTest();
        Field field = ConnectionHelper.class.getDeclaredField("datasource");
        field.setAccessible(true);
        Object previous = field.get(null);
        field.set(null, mock.proxy(PooledDataSource.class));
        int failed = 0;
        try {
            EquipoInterdisciplinarioServiceImpl service = new EquipoInterdisciplinarioServiceImpl();
            String[] empty = service.getDictamenesDelEquipoInterdisciplinario(321);
            boolean allEmpty = empty.length == 6;
            for (int i = 0; i < empty.length; i++) { allEmpty = allEmpty && "".equals(empty[i]); }
            if (!allEmpty || mock.id != 321 || mock.closed != 2
                    || !"{call autorizaciones.equipo_interdisciplinario_dictamenes_by_id(?)}".equals(mock.sql)) {
                failed++;
                System.out.println("FALLO seis dictamenes vacios / contrato / cierre");
            }
            mock.failQuery = true;
            boolean rejected = false;
            try { service.getDictamenesDelEquipoInterdisciplinario(321); }
            catch (RuntimeException e) { rejected = e.getCause() instanceof SQLException; }
            if (!rejected) {
                failed++;
                System.out.println("FALLO lectura fallida no rechazada");
            }
            mock.failQuery = false;
            mock.userQuery = true;
            String user = service.getUsuarioUltimaModificacionDictamen(321, 5);
            if (!"USUARIO <>&".equals(user) || mock.id != 321 || mock.tipo != 5
                    || !"usuario_modificacion".equals(mock.column)
                    || !"{call autorizaciones.equipo_interdisciplinario_usuario_dictamen_by_id(?,?)}".equals(mock.sql)) {
                failed++;
                System.out.println("FALLO contrato usuario ultima modificacion");
            }
            mock.failQuery = true;
            if (!"".equals(service.getUsuarioUltimaModificacionDictamen(321, 5))) {
                failed++;
                System.out.println("FALLO fallback usuario informativo");
            }
        } finally {
            field.set(null, previous);
        }
        System.out.println("EquipoInterDictamenLecturaTest: PASO=" + (4 - failed) + " FALLO=" + failed);
        if (failed != 0) { throw new AssertionError("Lectura dictamen: " + failed); }
    }

    private Object proxy(Class type) {
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        if ("getNumBusyConnections".equals(name)) { return Integer.valueOf(0); }
        if ("getConnection".equals(name)) { return proxy(Connection.class); }
        if ("prepareCall".equals(name)) {
            sql = (String) args[0];
            rows = 0;
            return proxy(CallableStatement.class);
        }
        if ("setInt".equals(name)) {
            if (((Integer) args[0]).intValue() == 1) { id = ((Integer) args[1]).intValue(); }
            else { tipo = ((Integer) args[1]).intValue(); }
            return null;
        }
        if ("executeQuery".equals(name)) {
            if (failQuery) { throw new SQLException("Fallo simulado de lectura"); }
            return proxy(ResultSet.class);
        }
        if ("next".equals(name)) { return Boolean.valueOf(userQuery && rows++ == 0); }
        if ("getString".equals(name)) { column = (String) args[0]; return "USUARIO <>&"; }
        if ("close".equals(name)) { closed++; return null; }
        throw new AssertionError("Operacion JDBC no autorizada en doble: " + name);
    }
}