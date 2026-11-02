package ar.com.ospim.util;
import java.sql.Connection;
import java.sql.Statement;
public class ConnectionHelper {
    public static Connection lectura;
    public static Connection getConnection() throws Exception {
        if (lectura == null) throw new Exception("No hay conexion de lectura de prueba.");
        return lectura;
    }
    public static Connection getConnectionForTransaction() throws Exception { throw new Exception("Sin escritura de prueba."); }
    public static void cerrar(Statement s) { try { if (s != null) s.close(); } catch (Exception e) { throw new RuntimeException(e); } }
    public static void cerrar(Connection c) { try { if (c != null) c.close(); } catch (Exception e) { throw new RuntimeException(e); } }
    public static void cerrar(Statement s, Connection c) { cerrar(s); cerrar(c); }
}
