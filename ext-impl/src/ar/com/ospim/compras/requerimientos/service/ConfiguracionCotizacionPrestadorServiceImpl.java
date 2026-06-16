package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.TipoPrestadorSector;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ConfiguracionCotizacionPrestadorServiceImpl {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    ConfiguracionCotizacionPrestadorServiceImpl.class
            );

    private static final String SQL_LISTAR_TIPOS =
            "SELECT id_tipo_prestador, descripcion, activo " +
                    "FROM compras.listar_tipos_prestador_sector(?)";

    private static final String SQL_DESACTIVAR_TIPOS =
            "{ call compras.desactivar_tipos_prestador_sector(?,?) }";

    private static final String SQL_GUARDAR_TIPO =
            "{ call compras.guardar_sector_tipo_prestador(?,?,?,?) }";

    public List<TipoPrestadorSector> listarTiposPrestadorSector(
            int idSector) throws Exception {

        validarSector(idSector);

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<TipoPrestadorSector> tipos =
                new ArrayList<TipoPrestadorSector>();

        try {
            con = obtenerConexion();

            stmt = con.prepareStatement(SQL_LISTAR_TIPOS);
            stmt.setInt(1, idSector);

            rs = stmt.executeQuery();

            while (rs.next()) {
                tipos.add(mapTipoPrestadorSector(rs));
            }

            return tipos;

        } catch (Exception e) {
            _log.error(
                    "Error listando tipos de prestador para sector "
                            + idSector,
                    e
            );

            throw e;

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void guardarConfiguracion(int idSector,
                                     int[] idsTiposSeleccionados,
                                     String usuario) throws Exception {

        validarSector(idSector);

        String usuarioNormalizado = normalizarUsuario(usuario);

        Set<Integer> idsNormalizados =
                normalizarIds(idsTiposSeleccionados);

        Connection con = null;
        CallableStatement stmtDesactivar = null;
        CallableStatement stmtGuardar = null;

        boolean autoCommitOriginal = true;

        try {
            con = obtenerConexion();

            autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false);

            stmtDesactivar = con.prepareCall(SQL_DESACTIVAR_TIPOS);
            stmtDesactivar.setInt(1, idSector);
            stmtDesactivar.setString(2, usuarioNormalizado);
            stmtDesactivar.execute();

            if (!idsNormalizados.isEmpty()) {
                stmtGuardar = con.prepareCall(SQL_GUARDAR_TIPO);

                for (Integer idTipo : idsNormalizados) {
                    stmtGuardar.clearParameters();

                    stmtGuardar.setInt(1, idSector);
                    stmtGuardar.setInt(2, idTipo.intValue());
                    stmtGuardar.setBoolean(3, true);
                    stmtGuardar.setString(4, usuarioNormalizado);

                    stmtGuardar.execute();
                }
            }

            con.commit();

            if (_log.isInfoEnabled()) {
                _log.info(
                        "Configuracion sector/tipo prestador guardada. "
                                + "idSector="
                                + idSector
                                + ", tiposActivos="
                                + idsNormalizados.size()
                                + ", usuario="
                                + usuarioNormalizado
                );
            }

        } catch (Exception e) {
            if (con != null) {
                ConnectionHelper.rollback(con);
            }

            _log.error(
                    "Error guardando configuracion de tipos de prestador. "
                            + "idSector="
                            + idSector,
                    e
            );

            throw e;

        } finally {
            ConnectionHelper.cerrar(stmtGuardar);
            ConnectionHelper.cerrar(stmtDesactivar);

            restaurarAutoCommit(con, autoCommitOriginal);
            ConnectionHelper.cerrar(con);
        }
    }

    private TipoPrestadorSector mapTipoPrestadorSector(ResultSet rs)
            throws Exception {

        TipoPrestadorSector tipo = new TipoPrestadorSector();

        tipo.setIdTipoPrestador(
                rs.getInt("id_tipo_prestador")
        );

        tipo.setDescripcion(
                rs.getString("descripcion")
        );

        tipo.setActivo(
                rs.getBoolean("activo")
        );

        return tipo;
    }

    private Connection obtenerConexion() throws Exception {
        Connection con = ConnectionHelper.getConnection();

        if (con == null) {
            throw new Exception(
                    "No se pudo obtener conexion a la base de datos."
            );
        }

        return con;
    }

    private void validarSector(int idSector) throws Exception {
        if (idSector <= 0) {
            throw new Exception(
                    "Debe informar el sector de compras."
            );
        }
    }

    private Set<Integer> normalizarIds(int[] ids) {
        Set<Integer> resultado =
                new LinkedHashSet<Integer>();

        if (ids == null) {
            return resultado;
        }

        for (int i = 0; i < ids.length; i++) {
            if (ids[i] > 0) {
                resultado.add(Integer.valueOf(ids[i]));
            }
        }

        return resultado;
    }

    private String normalizarUsuario(String usuario) {
        if (usuario == null || usuario.trim().length() == 0) {
            return "sistema";
        }

        return usuario.trim();
    }

    private void restaurarAutoCommit(Connection con,
                                     boolean autoCommitOriginal) {

        if (con == null) {
            return;
        }

        try {
            con.setAutoCommit(autoCommitOriginal);
        } catch (Exception e) {
            _log.warn(
                    "No se pudo restaurar autoCommit de la conexion.",
                    e
            );
        }
    }

    private void cerrar(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception e) {
            _log.debug(
                    "No se pudo cerrar ResultSet.",
                    e
            );
        }
    }
}