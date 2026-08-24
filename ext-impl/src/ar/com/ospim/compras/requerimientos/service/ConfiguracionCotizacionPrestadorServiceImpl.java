package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.TipoPrestadorSector;
import ar.com.ospim.util.ConnectionHelper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia de configuración sector/tipo de prestador.
 *
 * No contiene validaciones funcionales. La validación y normalización de
 * parámetros pertenece al Helper que administra la configuración.
 */
public class ConfiguracionCotizacionPrestadorServiceImpl {

    private static final String SQL_LISTAR_TIPOS =
            "{call compras.listar_configuracion_prestador_tipo_cotizacion(?)}";

    private static final String SQL_DESACTIVAR_TIPOS =
            "{call compras.desactivar_tipos_prestador_sector(?,?)}";

    private static final String SQL_GUARDAR_TIPO =
            "{call compras.guardar_sector_tipo_prestador_cotizacion(?,?,?,?,?)}";

    private static final String SQL_GUARDAR_TIPO_LEGACY =
            "{call compras.guardar_sector_tipo_prestador(?,?,?,?)}";

    public List<TipoPrestadorSector> listarTiposPrestadorSector(
            int idSector) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<TipoPrestadorSector> resultado =
                new ArrayList<TipoPrestadorSector>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_TIPOS);
            stmt.setInt(1, idSector);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapTipoPrestadorSector(rs));
            }

            return resultado;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void guardarConfiguracion(
            int idSector,
            List<TipoPrestadorSector> tiposSeleccionados,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmtDesactivar = null;
        CallableStatement stmtGuardar = null;

        try {
            con = ConnectionHelper.getConnectionForTransaction();

            stmtDesactivar = con.prepareCall(SQL_DESACTIVAR_TIPOS);
            stmtDesactivar.setInt(1, idSector);
            stmtDesactivar.setString(2, usuario);
            stmtDesactivar.execute();

            if (tiposSeleccionados != null
                    && !tiposSeleccionados.isEmpty()) {

                stmtGuardar = con.prepareCall(SQL_GUARDAR_TIPO);

                for (int i = 0; i < tiposSeleccionados.size(); i++) {
                    TipoPrestadorSector tipo = tiposSeleccionados.get(i);

                    stmtGuardar.clearParameters();
                    stmtGuardar.setInt(1, idSector);
                    stmtGuardar.setInt(2, tipo.getIdTipoPrestacion());
                    stmtGuardar.setInt(3, tipo.getIdTipoPrestador());
                    stmtGuardar.setBoolean(4, true);
                    stmtGuardar.setString(5, usuario);
                    stmtGuardar.execute();
                }
            }

            con.commit();
        } catch (Exception e) {
            ConnectionHelper.rollback(con);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmtGuardar);
            ConnectionHelper.cerrar(stmtDesactivar);
            ConnectionHelper.cerrar(con);
        }
    }

    public void guardarConfiguracion(
            int idSector,
            int[] idsTiposSeleccionados,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmtDesactivar = null;
        CallableStatement stmtGuardar = null;

        try {
            con = ConnectionHelper.getConnectionForTransaction();

            stmtDesactivar = con.prepareCall(SQL_DESACTIVAR_TIPOS);
            stmtDesactivar.setInt(1, idSector);
            stmtDesactivar.setString(2, usuario);
            stmtDesactivar.execute();

            if (idsTiposSeleccionados != null
                    && idsTiposSeleccionados.length > 0) {

                stmtGuardar = con.prepareCall(SQL_GUARDAR_TIPO_LEGACY);

                for (int i = 0; i < idsTiposSeleccionados.length; i++) {
                    stmtGuardar.clearParameters();
                    stmtGuardar.setInt(1, idSector);
                    stmtGuardar.setInt(2, idsTiposSeleccionados[i]);
                    stmtGuardar.setBoolean(3, true);
                    stmtGuardar.setString(4, usuario);
                    stmtGuardar.execute();
                }
            }

            con.commit();
        } catch (Exception e) {
            ConnectionHelper.rollback(con);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmtGuardar);
            ConnectionHelper.cerrar(stmtDesactivar);
            ConnectionHelper.cerrar(con);
        }
    }

    private TipoPrestadorSector mapTipoPrestadorSector(
            ResultSet rs) throws Exception {

        TipoPrestadorSector tipo = new TipoPrestadorSector();
        tipo.setIdTipoPrestacion(rs.getInt("id_tipo_prestacion"));
        tipo.setTipoPrestacionDescripcion(
                rs.getString("tipo_cotizacion")
        );
        tipo.setIdTipoPrestador(rs.getInt("id_tipo_prestador"));
        tipo.setDescripcion(rs.getString("descripcion"));
        tipo.setActivo(rs.getBoolean("activo"));
        return tipo;
    }

    private void cerrar(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }
}
