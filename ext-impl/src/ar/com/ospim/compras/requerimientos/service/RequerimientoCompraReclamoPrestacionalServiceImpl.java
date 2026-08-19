package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.util.ConnectionHelper;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia del vínculo Compras / Reclamo Prestacional.
 *
 * Sin validaciones funcionales ni orquestación de creación del RP.
 */
public class RequerimientoCompraReclamoPrestacionalServiceImpl {

    private static final String SQL_GET_RELACION =
            "{call compras.get_requerimiento_reclamo_prestacional(?)}";

    private static final String SQL_GET_RELACIONES_BATCH =
            "{call compras.listar_requerimientos_reclamo_prestacional_vinculados(?,?)}";

    private static final String SQL_GET_RELACION_POR_RECLAMO =
            "{call compras.get_requerimiento_por_reclamo_prestacional(?,?)}";

    private static final String SQL_RESERVAR =
            "{ ? = call compras.reservar_reclamo_prestacional(?,?,?) }";

    private static final String SQL_FINALIZAR =
            "{ ? = call compras.finalizar_reclamo_prestacional(?,?,?,?) }";

    private static final String SQL_LIBERAR =
            "{ ? = call compras.liberar_reserva_reclamo_prestacional(?,?,?) }";

    private static final String SQL_MARCAR_ERROR =
            "{ ? = call compras.marcar_error_reclamo_prestacional(?,?,?,?,?) }";

    private static final String SQL_BLOQUEAR_REQUERIMIENTO =
            "{ ? = call compras.bloquear_requerimiento_reclamo_prestacional(?) }";

    private static final String SQL_GET_ESTADO_REQUERIMIENTO_FOR_UPDATE =
            "{ ? = call compras.get_estado_requerimiento_for_update(?) }";

    private static final String SQL_CAMBIAR_ESTADO_REQUERIMIENTO =
            "{call compras.cambiar_estado_requerimiento(?,?,?)}";

    public RequerimientoCompraReclamoPrestacional obtenerPorRequerimiento(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;

        try {
            con = ConnectionHelper.getConnection();
            return obtenerPorRequerimiento(
                    con,
                    idRequerimientoCompra
            );
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public RequerimientoCompraReclamoPrestacional obtenerPorRequerimiento(
            Connection con,
            int idRequerimientoCompra) throws Exception {

        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareCall(SQL_GET_RELACION);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            return rs.next()
                    ? mapRelacion(rs)
                    : null;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<RequerimientoCompraReclamoPrestacional>
    listarPorReclamoPrestacional(
            int idReclamoPrestacional,
            String estado) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraReclamoPrestacional> resultado =
                new ArrayList<RequerimientoCompraReclamoPrestacional>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_RELACION_POR_RECLAMO);
            stmt.setInt(1, idReclamoPrestacional);
            stmt.setString(2, estado);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapRelacion(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<RequerimientoCompraReclamoPrestacional>
    listarVinculadasPorRequerimientos(
            String estado,
            List<Integer> idsRequerimientos) throws Exception {

        return listarVinculadasPorRequerimientos(
                estado,
                construirArrayEnterosPostgreSql(idsRequerimientos)
        );
    }

    public List<RequerimientoCompraReclamoPrestacional>
    listarVinculadasPorRequerimientos(
            String estado,
            String idsRequerimientosArray) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraReclamoPrestacional> resultado =
                new ArrayList<RequerimientoCompraReclamoPrestacional>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_RELACIONES_BATCH);
            stmt.setString(1, estado);
            stmt.setString(2, idsRequerimientosArray);
            rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(mapRelacion(rs));
            }

            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean reservarCreacion(
            Connection con,
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        CallableStatement stmt = null;

        try {
            stmt = con.prepareCall(SQL_RESERVAR);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setString(3, tokenReserva);
            stmt.setString(4, usuario);
            stmt.execute();

            return stmt.getBoolean(1);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public boolean finalizarCreacion(
            Connection con,
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String usuario) throws Exception {

        CallableStatement stmt = null;

        try {
            stmt = con.prepareCall(SQL_FINALIZAR);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setString(3, tokenReserva);
            stmt.setInt(4, idReclamoPrestacional);
            stmt.setString(5, usuario);
            stmt.execute();

            return stmt.getBoolean(1);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public boolean finalizarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String usuario) throws Exception {

        Connection con = null;

        try {
            con = ConnectionHelper.getConnection();
            return finalizarCreacion(
                    con,
                    idRequerimientoCompra,
                    tokenReserva,
                    idReclamoPrestacional,
                    usuario
            );
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public boolean liberarReserva(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LIBERAR);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setString(3, tokenReserva);
            stmt.setString(4, usuario);
            stmt.execute();

            return stmt.getBoolean(1);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean marcarErrorPosteriorAlInsert(
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String error,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_MARCAR_ERROR);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setString(3, tokenReserva);
            stmt.setInt(4, idReclamoPrestacional);
            stmt.setString(5, error);
            stmt.setString(6, usuario);
            stmt.execute();

            return stmt.getBoolean(1);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean bloquearRequerimiento(
            Connection con,
            int idRequerimientoCompra) throws Exception {

        CallableStatement stmt = null;

        try {
            stmt = con.prepareCall(SQL_BLOQUEAR_REQUERIMIENTO);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.execute();

            return stmt.getBoolean(1);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public int getEstadoRequerimientoForUpdate(
            Connection con,
            int idRequerimientoCompra) throws Exception {

        CallableStatement stmt = null;

        try {
            stmt = con.prepareCall(
                    SQL_GET_ESTADO_REQUERIMIENTO_FOR_UPDATE
            );
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.execute();

            return stmt.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public void cambiarEstado(
            Connection con,
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        CallableStatement stmt = null;

        try {
            stmt = con.prepareCall(
                    SQL_CAMBIAR_ESTADO_REQUERIMIENTO
            );
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idEstadoNuevo);
            stmt.setString(3, usuario);
            stmt.execute();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    private String construirArrayEnterosPostgreSql(
            List<Integer> valores) {

        if (valores == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        value.append('{');

        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) {
                value.append(',');
            }

            Integer actual = valores.get(i);
            value.append(
                    actual != null
                            ? actual.toString()
                            : "NULL"
            );
        }

        value.append('}');
        return value.toString();
    }

    private RequerimientoCompraReclamoPrestacional mapRelacion(
            ResultSet rs) throws Exception {

        RequerimientoCompraReclamoPrestacional relacion =
                new RequerimientoCompraReclamoPrestacional();

        relacion.setIdRequerimientoCompra(
                rs.getInt("id_requerimiento")
        );

        int idReclamo = rs.getInt("id_reclamo_prestacional");
        relacion.setIdReclamoPrestacional(
                rs.wasNull()
                        ? null
                        : Integer.valueOf(idReclamo)
        );

        relacion.setEstado(rs.getString("estado"));
        relacion.setTokenReserva(rs.getString("token_reserva"));
        relacion.setReservaFecha(rs.getTimestamp("reserva_fecha"));
        relacion.setUltimoError(rs.getString("ultimo_error"));
        relacion.setAltaFecha(rs.getTimestamp("alta_fecha"));
        relacion.setAltaUsr(rs.getString("alta_usr"));
        relacion.setModiFecha(rs.getTimestamp("modi_fecha"));
        relacion.setModiUsr(rs.getString("modi_usr"));

        return relacion;
    }

    private void closeQuietly(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }
}
