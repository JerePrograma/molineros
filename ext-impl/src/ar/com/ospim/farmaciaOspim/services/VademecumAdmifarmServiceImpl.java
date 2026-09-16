package ar.com.ospim.farmaciaOspim.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class VademecumAdmifarmServiceImpl implements VademecumAdmifarmService {

    private static final Log log =
            LogFactoryUtil.getLog(VademecumAdmifarmServiceImpl.class);

    public List<Object[]> getImportaciones(String tipo) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        boolean autoCommit = true;
        boolean transaccionIniciada = false;
        boolean transaccionTerminada = false;

        try {
            con = ConnectionHelper.getConnection();
            if (con == null) {
                throw new SQLException("No se obtuvo conexion a la base de datos.");
            }
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            transaccionIniciada = true;

            stmt = con.prepareCall(
                    "{? = call public.buscar_importaciones_vademecum_admifarm(?)}");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setString(2, tipo);
            stmt.execute();
            rs = (ResultSet) stmt.getObject(1);

            List<Object[]> importaciones = new ArrayList<Object[]>();
            while (rs.next()) {
                importaciones.add(new Object[] {
                        rs.getTimestamp("alta_fecha"),
                        Long.valueOf(rs.getLong("cantidad")) });
            }

            con.commit();
            transaccionTerminada = true;
            return importaciones;
        } finally {
            cerrar(con, stmt, rs, autoCommit,
                    transaccionIniciada, transaccionTerminada);
        }
    }

    public ImportacionVademecumAdmifarm importar(String tipo,
            List<Registro> registros) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        boolean autoCommit = true;
        boolean transaccionIniciada = false;
        boolean transaccionTerminada = false;

        try {
            con = ConnectionHelper.getConnection();
            if (con == null) {
                throw new SQLException("No se obtuvo conexion a la base de datos.");
            }
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            transaccionIniciada = true;

            stmt = con.prepareCall(
                    "{? = call public.importar_vademecum_admifarm(?, "
                            + "CAST(? AS numeric[]), CAST(? AS text[]), "
                            + "CAST(? AS text[]), CAST(? AS text[]), "
                            + "CAST(? AS text[]), CAST(? AS text[]), "
                            + "CAST(? AS text[]))}");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setString(2, tipo);
            cargarParametros(stmt, tipo, registros);
            stmt.execute();
            rs = (ResultSet) stmt.getObject(1);

            ImportacionVademecumAdmifarm importacion = cargarImportacion(rs, tipo);
            // Se conserva el contrato original: la carga devuelve la lista recibida.
            importacion.setRegistros(registros);
            con.commit();
            transaccionTerminada = true;
            return importacion;
        } finally {
            cerrar(con, stmt, rs, autoCommit,
                    transaccionIniciada, transaccionTerminada);
        }
    }

    public ImportacionVademecumAdmifarm getImportacion(String tipo,
            Timestamp fecha) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        boolean autoCommit = true;
        boolean transaccionIniciada = false;
        boolean transaccionTerminada = false;

        try {
            con = ConnectionHelper.getConnection();
            if (con == null) {
                throw new SQLException("No se obtuvo conexion a la base de datos.");
            }
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            transaccionIniciada = true;

            stmt = con.prepareCall(
                    "{? = call public.buscar_importacion_vademecum_admifarm(?, ?)}");
            stmt.registerOutParameter(1, Types.OTHER);
            stmt.setString(2, tipo);
            stmt.setTimestamp(3, fecha);
            stmt.execute();
            rs = (ResultSet) stmt.getObject(1);

            ImportacionVademecumAdmifarm importacion = cargarImportacion(rs, tipo);
            con.commit();
            transaccionTerminada = true;
            return importacion;
        } finally {
            cerrar(con, stmt, rs, autoCommit,
                    transaccionIniciada, transaccionTerminada);
        }
    }

    // Se envian parametros de texto y el CALL los convierte a arrays PostgreSQL.
    // No requiere createArrayOf ni APIs JDBC4 en la conexion del pool.
    private void cargarParametros(CallableStatement stmt,
            String tipo, List<Registro> registros) throws SQLException {
        int cantidadColumnas = ImportacionVademecumAdmifarm.getColumnas(tipo).length;
        if (registros == null || registros.isEmpty() || registros.size() > 65535) {
            throw new IllegalArgumentException(
                    "La importacion debe contener entre 1 y 65535 registros.");
        }

        int cantidad = registros.size();
        BigDecimal[] numeros = new BigDecimal[cantidad];
        String[][] valores = new String[6][cantidad];
        boolean ampliado = "ampliado".equals(tipo);

        for (int i = 0; i < cantidad; i++) {
            Registro registro = registros.get(i);
            if (registro == null || registro.getValores() == null
                    || registro.getValores().length != cantidadColumnas - 1) {
                throw new IllegalArgumentException(
                        "La cantidad de columnas no corresponde al tipo de Vademecum.");
            }

            String[] fila = registro.getValores();
            numeros[i] = registro.getRegistro();
            valores[0][i] = fila[0];                         // nombre
            valores[1][i] = fila[ampliado ? 1 : 2];         // presentacion
            valores[2][i] = fila[ampliado ? 2 : 3];         // accion
            valores[3][i] = fila[ampliado ? 3 : 1];         // monodroga
            valores[4][i] = fila[4];                       // laboratorio
            valores[5][i] = ampliado ? fila[5] : null;      // tipo_venta
        }

        stmt.setString(3, parametroNumerico(numeros));
        for (int i = 0; i < valores.length; i++) {
            stmt.setString(i + 4, parametroTexto(valores[i]));
        }
    }

    private String parametroNumerico(BigDecimal[] valores) {
        StringBuffer parametro = new StringBuffer("{");
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                parametro.append(',');
            }
            if (valores[i] == null) {
                parametro.append("NULL");
            } else {
                parametro.append(valores[i].toPlainString());
            }
        }
        parametro.append('}');
        return parametro.toString();
    }

    private String parametroTexto(String[] valores) {
        StringBuffer parametro = new StringBuffer("{");
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                parametro.append(',');
            }
            String valor = valores[i];
            if (valor == null) {
                parametro.append("NULL");
            } else {
                parametro.append('"');
                for (int c = 0; c < valor.length(); c++) {
                    char caracter = valor.charAt(c);
                    if (caracter == '"' || caracter == '\\') {
                        parametro.append('\\');
                    }
                    parametro.append(caracter);
                }
                parametro.append('"');
            }
        }
        parametro.append('}');
        return parametro.toString();
    }

    private ImportacionVademecumAdmifarm cargarImportacion(ResultSet rs,
            String tipo) throws SQLException {
        ImportacionVademecumAdmifarm importacion =
                new ImportacionVademecumAdmifarm();
        boolean ampliado = "ampliado".equals(tipo);

        while (rs.next()) {
            importacion.setFecha(rs.getTimestamp("fecha"));
            importacion.setFechaAnterior(rs.getTimestamp("fecha_anterior"));

            String[] valores;
            if (ampliado) {
                valores = new String[] {
                        rs.getString("nombre"),
                        rs.getString("presentacion"),
                        rs.getString("accion"),
                        rs.getString("monodroga"),
                        rs.getString("laboratorio"),
                        rs.getString("tipo_venta") };
            } else {
                valores = new String[] {
                        rs.getString("nombre"),
                        rs.getString("monodroga"),
                        rs.getString("presentacion"),
                        rs.getString("accion"),
                        rs.getString("laboratorio") };
            }

            Registro registro = new Registro(rs.getBigDecimal("registro"), valores);
            if (rs.getBoolean("anterior")) {
                importacion.getAnteriores().add(registro);
            } else {
                importacion.getRegistros().add(registro);
            }
        }

        if (importacion.getRegistros().isEmpty()) {
            throw new SQLException("La consulta no devolvio la importacion solicitada.");
        }
        return importacion;
    }

    private void cerrar(Connection con, CallableStatement stmt, ResultSet rs,
            boolean autoCommit, boolean transaccionIniciada,
            boolean transaccionTerminada) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.warn("No se pudo cerrar la consulta de Vademecum", e);
            }
        }
        ConnectionHelper.cerrar(stmt);

        if (con != null && transaccionIniciada) {
            if (!transaccionTerminada) {
                try {
                    con.rollback();
                    transaccionTerminada = true;
                } catch (SQLException e) {
                    log.error("Error revirtiendo Vademecum Admifarm", e);
                }
            }
            // No activar autoCommit si no se pudo confirmar el rollback.
            if (transaccionTerminada) {
                try {
                    con.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    log.error("Error restaurando la conexion de Vademecum", e);
                }
            }
        }
        ConnectionHelper.cerrar(con);
    }
}
