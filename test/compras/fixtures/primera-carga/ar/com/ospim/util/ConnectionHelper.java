package ar.com.ospim.util;

import java.sql.Connection;
import java.sql.Statement;

/* Offline-only JDBC boundary. No driver, pool, network or live database. */
public class ConnectionHelper {
    public static Connection connection;
    public static int acquisitions;
    public static RuntimeException failure;

    public static Connection getConnection() {
        acquisitions++;
        if (failure != null) throw failure;
        if (connection == null) throw new IllegalStateException("No offline connection configured");
        return connection;
    }

    public static void cerrar(Statement statement, Connection value) {
        cerrar(statement);
        cerrar(value);
    }

    public static void cerrar(Statement statement) {
        if (statement != null) {
            try { statement.close(); }
            catch (Exception error) { throw new IllegalStateException(error); }
        }
    }

    public static void cerrar(Connection value) {
        if (value != null) {
            try { value.close(); }
            catch (Exception error) { throw new IllegalStateException(error); }
        }
    }
}