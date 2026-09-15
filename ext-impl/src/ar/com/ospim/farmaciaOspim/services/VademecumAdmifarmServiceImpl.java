package ar.com.ospim.farmaciaOspim.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro;
import ar.com.ospim.farmaciaOspim.helper.VademecumAdmifarmHelper;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class VademecumAdmifarmServiceImpl {
    private static final Log log = LogFactoryUtil.getLog(VademecumAdmifarmServiceImpl.class);

    private String tabla(String tipo) {
        if ("ampliado".equals(tipo)) { return "public.vademecum_admifarm_ampliado"; }
        if ("pmo".equals(tipo)) { return "public.vademecum_admifarm_pmo"; }
        throw new IllegalArgumentException("El tipo de Vademecum no es valido.");
    }

    private String columnas(String tipo) {
        String[] nombres = ImportacionVademecumAdmifarm.getColumnas(tipo);
        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < nombres.length; i++) {
            if (i > 0) { sql.append(", "); }
            sql.append(nombres[i]);
        }
        return sql.toString();
    }

    public List<Object[]> getImportaciones(String tipo) throws Exception {
        String historico = tabla(tipo) + "_historico";
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement("SELECT alta_fecha, count(*) AS cantidad FROM "
                + historico + " WHERE alta_fecha IS NOT NULL GROUP BY alta_fecha ORDER BY alta_fecha DESC");
            rs = stmt.executeQuery();
            List<Object[]> resultado = new ArrayList<Object[]>();
            while (rs.next()) {
                resultado.add(new Object[] { rs.getTimestamp("alta_fecha"), Long.valueOf(rs.getLong("cantidad")) });
            }
            return resultado;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    // La misma transaccion conserva la foto completa y reemplaza el vigente.
    // El bloqueo se toma antes de elegir el ultimo historico, incluso con cargas simultaneas.
    public ImportacionVademecumAdmifarm importar(String tipo, List<Registro> registros) throws Exception {
        String vigente = tabla(tipo);
        if (registros == null || registros.isEmpty() || registros.size() > 65535) {
            throw new IllegalArgumentException("La importacion debe contener entre 1 y 65535 registros.");
        }
        VademecumAdmifarmHelper.indexar(registros);
        Connection con = null;
        boolean autoCommit = true;
        boolean transaccionTerminada = false;
        try {
            con = ConnectionHelper.getConnection();
            if (con == null) { throw new SQLException("No se obtuvo conexion a la base de datos."); }
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            ejecutar(con, "SET LOCAL lock_timeout = '15s'");
            ejecutar(con, "LOCK TABLE " + vigente + ", " + vigente
                + "_historico IN SHARE ROW EXCLUSIVE MODE");
            ImportacionVademecumAdmifarm resultado = new ImportacionVademecumAdmifarm();
            Timestamp anterior = fechaAnterior(con, tipo, null);
            List<Registro> anteriores = leer(con, tipo, anterior, anterior != null);
            VademecumAdmifarmHelper.indexar(anteriores);
            // Si aun no habia historico, se conserva el vigente antes de la primera carga.
            // Su fecha representa el momento de este respaldo inicial.
            if (anterior == null && !anteriores.isEmpty()) {
                anterior = nuevaFecha(con, tipo);
                insertar(con, tipo, anteriores, anterior);
            }
            Timestamp fecha = nuevaFecha(con, tipo);
            insertar(con, tipo, registros, fecha);
            ejecutar(con, "DELETE FROM " + vigente);
            insertar(con, tipo, registros, null);
            resultado.setFecha(fecha);
            resultado.setFechaAnterior(anterior);
            resultado.setAnteriores(anteriores);
            resultado.setRegistros(registros);
            con.commit();
            transaccionTerminada = true;
            return resultado;
        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); transaccionTerminada = true; }
                catch (SQLException rollback) { log.error("Error revirtiendo Vademecum Admifarm", rollback); }
            }
            throw e;
        } finally {
            if (con != null && transaccionTerminada) {
                try { con.setAutoCommit(autoCommit); }
                catch (SQLException e) { log.error("Error restaurando la conexion de Vademecum", e); }
            }
            ConnectionHelper.cerrar(con);
        }
    }

    public ImportacionVademecumAdmifarm getImportacion(String tipo, Timestamp fecha) throws Exception {
        tabla(tipo);
        if (fecha == null) { throw new IllegalArgumentException("Debe indicar la fecha de importacion."); }
        Connection con = null;
        try {
            con = ConnectionHelper.getConnection();
            ImportacionVademecumAdmifarm resultado = new ImportacionVademecumAdmifarm();
            resultado.setFecha(fecha);
            resultado.setRegistros(leer(con, tipo, fecha, true));
            if (resultado.getRegistros().isEmpty()) {
                throw new IllegalArgumentException("La importacion solicitada no existe.");
            }
            Timestamp anterior = fechaAnterior(con, tipo, fecha);
            resultado.setFechaAnterior(anterior);
            if (anterior != null) { resultado.setAnteriores(leer(con, tipo, anterior, true)); }
            return resultado;
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    private Timestamp fechaAnterior(Connection con, String tipo, Timestamp fecha) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT max(alta_fecha) FROM " + tabla(tipo)
                + "_historico" + (fecha == null ? "" : " WHERE alta_fecha < ?"));
            if (fecha != null) { stmt.setTimestamp(1, fecha); }
            rs = stmt.executeQuery();
            rs.next();
            return rs.getTimestamp(1);
        } finally { cerrar(rs); ConnectionHelper.cerrar(stmt); }
    }

    private Timestamp nuevaFecha(Connection con, String tipo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT greatest(clock_timestamp()::timestamp, "
                + "max(alta_fecha) + interval '1 microsecond') FROM " + tabla(tipo) + "_historico");
            rs = stmt.executeQuery();
            rs.next();
            return rs.getTimestamp(1);
        } finally { cerrar(rs); ConnectionHelper.cerrar(stmt); }
    }

    private List<Registro> leer(Connection con, String tipo, Timestamp fecha, boolean historico) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT " + columnas(tipo) + " FROM " + tabla(tipo)
                + (historico ? "_historico WHERE alta_fecha = ?" : "") + " ORDER BY registro");
            if (historico) { stmt.setTimestamp(1, fecha); }
            rs = stmt.executeQuery();
            List<Registro> resultado = new ArrayList<Registro>();
            int cantidad = ImportacionVademecumAdmifarm.getColumnas(tipo).length;
            while (rs.next()) {
                String[] valores = new String[cantidad - 1];
                for (int i = 1; i < cantidad; i++) { valores[i - 1] = rs.getString(i + 1); }
                resultado.add(new Registro(rs.getBigDecimal(1), valores));
            }
            return resultado;
        } finally { cerrar(rs); ConnectionHelper.cerrar(stmt); }
    }

    private void insertar(Connection con, String tipo, List<Registro> registros, Timestamp fecha) throws SQLException {
        int cantidad = ImportacionVademecumAdmifarm.getColumnas(tipo).length;
        StringBuffer parametros = new StringBuffer("?");
        for (int i = 1; i < cantidad; i++) { parametros.append(", ?"); }
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("INSERT INTO " + tabla(tipo) + (fecha == null ? "" : "_historico")
                + " (" + (fecha == null ? "" : "alta_fecha, ") + columnas(tipo) + ") VALUES ("
                + (fecha == null ? "" : "?, ") + parametros.toString() + ")");
            int pendientes = 0;
            for (Registro registro : registros) {
                if (registro.getValores() == null || registro.getValores().length != cantidad - 1) {
                    throw new IllegalArgumentException("La cantidad de columnas no corresponde al tipo de Vademecum.");
                }
                stmt.clearParameters();
                int posicion = 1;
                if (fecha != null) { stmt.setTimestamp(posicion++, fecha); }
                stmt.setBigDecimal(posicion++, registro.getRegistro());
                for (String valor : registro.getValores()) { stmt.setString(posicion++, valor); }
                stmt.addBatch();
                pendientes++;
                if (pendientes == 1000) { stmt.executeBatch(); stmt.clearBatch(); pendientes = 0; }
            }
            if (pendientes > 0) { stmt.executeBatch(); }
        } finally { ConnectionHelper.cerrar(stmt); }
    }

    private void ejecutar(Connection con, String sql) throws SQLException {
        Statement stmt = null;
        try { stmt = con.createStatement(); stmt.execute(sql); }
        finally { ConnectionHelper.cerrar(stmt); }
    }

    private void cerrar(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); }
            catch (SQLException e) { log.warn("No se pudo cerrar la consulta de Vademecum", e); }
        }
    }
}
