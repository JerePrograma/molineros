package ar.com.ospim.util;
import java.sql.Connection;
import java.sql.Statement;
public class ConnectionHelper {
    public static int aperturas;
    public static int transacciones;
    public static Connection transaccionPreparada;
    public static Connection getConnection() throws Exception { aperturas++; throw new Exception("La prueba no permite abrir otra conexion."); }
    public static Connection getConnectionForTransaction() throws Exception {
        transacciones++; Connection c = transaccionPreparada; transaccionPreparada = null;
        if (c == null) throw new Exception("No hay conexion de prueba preparada."); return c;
    }
    public static void cerrar(Statement s) { try { if (s != null) s.close(); } catch (Exception e) { throw new RuntimeException(e); } }
    public static void cerrar(Connection c) { try { if (c != null) c.close(); } catch (Exception e) { throw new RuntimeException(e); } }
    public static void cerrar(Statement s, Connection c) { cerrar(s); cerrar(c); }
}
