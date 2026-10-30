package ar.com.ospim.util;
import java.sql.Connection;
import java.sql.Statement;
public class ConnectionHelper {
    public static int aperturas;
    public static Connection getConnection() throws Exception {
        aperturas++;
        throw new Exception("La prueba no permite abrir otra conexion.");
    }
    public static Connection getConnectionForTransaction() throws Exception {
        return getConnection();
    }
    public static void cerrar(Statement s) { try { if (s != null) s.close(); } catch (Exception e) { throw new RuntimeException(e); } }
    public static void cerrar(Connection c) { try { if (c != null) c.close(); } catch (Exception e) { throw new RuntimeException(e); } }
    public static void cerrar(Statement s, Connection c) { cerrar(s); cerrar(c); }
}
