package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.util.ConnectionHelper;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
            "{call compras.listar_relaciones_reclamo_prestacional_batch(?,?)}";

    private static final String SQL_GET_RELACION_POR_RECLAMO =
            "{call compras.listar_relaciones_reclamo_prestacional_por_reclamo(?,?)}";

    private static final String SQL_BLOQUEAR_REQUERIMIENTO =
            "{call compras.bloquear_requerimiento_reclamo_prestacional(?)}";

    private static final String SQL_GET_ESTADO_REQUERIMIENTO_FOR_UPDATE =
            "{call compras.get_estado_requerimiento_for_update(?)}";

    private static final String SQL_GUARDAR_OBSERVACION_ESTADO_RECLAMO =
            "UPDATE autorizaciones.reclamos_prestacionales_estados "
                    + "SET observacion = ? "
                    + "WHERE id_reclamo_prestacional = ? "
                    + "AND id_estado = ? AND baja_fecha IS NULL";

    public Timestamp bloquearReclamoPrestacional(
            Connection con, int idReclamoPrestacional) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(
                    "SELECT baja_fecha FROM autorizaciones.reclamos_prestacionales "
                            + "WHERE id = ? FOR UPDATE");
            stmt.setInt(1, idReclamoPrestacional);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No se encontro el Reclamo Prestacional a bloquear.");
            }
            return rs.getTimestamp("baja_fecha");
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public List<RequerimientoCompraReclamoPrestacional> obtenerVinculosPorRPForUpdate(
            Connection con, int idReclamoPrestacional) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraReclamoPrestacional> resultado =
                new ArrayList<RequerimientoCompraReclamoPrestacional>();
        try {
            stmt = con.prepareStatement(
                    "SELECT * FROM compras.requerimiento_reclamo_prestacional "
                            + "WHERE id_reclamo_prestacional = ? "
                            + "ORDER BY id_requerimiento FOR UPDATE");
            stmt.setInt(1, idReclamoPrestacional);
            rs = stmt.executeQuery();
            while (rs.next()) {
                resultado.add(mapRelacion(rs));
            }
            return resultado;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public int guardarBajaVinculo(
            Connection con, RequerimientoCompraReclamoPrestacional relacion,
            Timestamp fechaBaja, String usuario, String estadoEsperado) throws Exception {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(
                    "UPDATE compras.requerimiento_reclamo_prestacional "
                            + "SET estado = ?, baja_fecha = ?, baja_usr = ?, token_reserva = ?, "
                            + "modi_fecha = now(), modi_usr = ? "
                            + "WHERE id_requerimiento = ? AND id_reclamo_prestacional = ? "
                            + "AND estado = ?");
            stmt.setString(1, relacion.getEstado());
            stmt.setTimestamp(2, fechaBaja);
            stmt.setString(3, usuario);
            stmt.setString(4, relacion.getTokenReserva());
            stmt.setString(5, usuario);
            stmt.setInt(6, relacion.getIdRequerimientoCompra());
            stmt.setInt(7, relacion.getIdReclamoPrestacionalInt());
            stmt.setString(8, estadoEsperado);
            return stmt.executeUpdate();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

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

    public boolean reservarCreacion(Connection con,int id,String token,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper()
                .reservarCreacionEnTransaccion(RequerimientoCompraReclamoPrestacionalTransaccion.sobreConexion(this,con),id,token,usuario);
    }

    public boolean finalizarCreacion(Connection con,int id,String token,int reclamo,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper()
                .finalizarCreacionEnTransaccion(RequerimientoCompraReclamoPrestacionalTransaccion.sobreConexion(this,con),id,token,reclamo,usuario);
    }

    public boolean finalizarCreacion(int id,String token,int reclamo,String usuario)throws Exception {
        new ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper().finalizarCreacion(id,token,reclamo,usuario);return true;
    }

    public boolean liberarReserva(int id,String token,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper().liberarReserva(id,token,usuario);
    }

    public boolean marcarErrorPosteriorAlInsert(int id,String token,int reclamo,String error,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper().marcarErrorPosteriorAlInsert(id,token,reclamo,error,usuario);
    }

    public java.util.Map<String,Object> obtenerCabeceraReserva(Connection con,int id)throws Exception {
        PreparedStatement stmt=null;ResultSet rs=null;
        try{
            stmt=con.prepareStatement("SELECT estado,baja_fecha,afiliado_cuil_titular,afiliado_int FROM compras.requerimiento WHERE id_requerimiento=? FOR UPDATE");
            stmt.setInt(1,id);rs=stmt.executeQuery();if(!rs.next())return null;
            java.util.Map<String,Object> f=new java.util.HashMap<String,Object>();int estado=rs.getInt("estado");f.put("estado",rs.wasNull()?null:Integer.valueOf(estado));
            f.put("baja",rs.getTimestamp("baja_fecha"));f.put("cuil",rs.getString("afiliado_cuil_titular"));int integrante=rs.getInt("afiliado_int");
            f.put("integrante",rs.wasNull()?null:Integer.valueOf(integrante));return f;
        }finally{closeQuietly(rs);ConnectionHelper.cerrar(stmt);}
    }

    public int insertarReserva(Connection con,int id,String estado,String token,String usuario)throws Exception {
        usuario=new EditarRequerimientoCompraServiceImpl().resolverUsuarioAuditoria(con,usuario);PreparedStatement stmt=null;
        try{
            stmt=con.prepareStatement("INSERT INTO compras.requerimiento_reclamo_prestacional "
                    + "(id_requerimiento,estado,token_reserva,reserva_fecha,alta_fecha,alta_usr) VALUES (?,?,?,now(),now(),?) "
                    + "ON CONFLICT (id_requerimiento) DO NOTHING");
            stmt.setInt(1,id);stmt.setString(2,estado);stmt.setString(3,token);stmt.setString(4,usuario);return stmt.executeUpdate();
        }finally{ConnectionHelper.cerrar(stmt);}
    }

    public int actualizarVinculo(Connection con,int id,String anterior,String token,int reclamo,
            String nuevo,String error,boolean limpiarToken,String usuario)throws Exception {
        usuario=new EditarRequerimientoCompraServiceImpl().resolverUsuarioAuditoria(con,usuario);PreparedStatement stmt=null;
        try{
            stmt=con.prepareStatement("UPDATE compras.requerimiento_reclamo_prestacional SET id_reclamo_prestacional=?,estado=?,ultimo_error=?,"
                    + "modi_fecha=now(),modi_usr=?"+(limpiarToken?",token_reserva=NULL":"")
                    + " WHERE id_requerimiento=? AND estado=? AND token_reserva IS NOT DISTINCT FROM ?");
            stmt.setInt(1,reclamo);stmt.setString(2,nuevo);stmt.setString(3,error);stmt.setString(4,usuario);stmt.setInt(5,id);
            stmt.setString(6,anterior);stmt.setString(7,token);return stmt.executeUpdate();
        }finally{ConnectionHelper.cerrar(stmt);}
    }

    public int eliminarReserva(Connection con,int id,String estado,String token)throws Exception {
        PreparedStatement stmt=null;
        try{
            stmt=con.prepareStatement("DELETE FROM compras.requerimiento_reclamo_prestacional WHERE id_requerimiento=? "
                    + "AND estado=? AND id_reclamo_prestacional IS NULL AND token_reserva=?");
            stmt.setInt(1,id);stmt.setString(2,estado);stmt.setString(3,token);return stmt.executeUpdate();
        }finally{ConnectionHelper.cerrar(stmt);}
    }

    public boolean bloquearRequerimiento(
            Connection con,
            int idRequerimientoCompra) throws Exception {

        CallableStatement advisory=null;PreparedStatement stmt=null;ResultSet rs=null;
        try{
            advisory=con.prepareCall(SQL_BLOQUEAR_REQUERIMIENTO);advisory.setInt(1,idRequerimientoCompra);advisory.execute();
            String[] locks={"SELECT rp.id FROM autorizaciones.reclamos_prestacionales rp JOIN compras.requerimiento_reclamo_prestacional rr "
                    + "ON rr.id_reclamo_prestacional=rp.id WHERE rr.id_requerimiento=? ORDER BY rp.id FOR UPDATE OF rp",
                    "SELECT id_requerimiento FROM compras.requerimiento_reclamo_prestacional WHERE id_requerimiento=? FOR UPDATE",
                    "SELECT id_requerimiento FROM compras.requerimiento WHERE id_requerimiento=? FOR UPDATE"};
            for(int i=0;i<locks.length;i++){
                stmt=con.prepareStatement(locks[i]);stmt.setInt(1,idRequerimientoCompra);rs=stmt.executeQuery();while(rs.next())rs.getInt(1);
                closeQuietly(rs);rs=null;ConnectionHelper.cerrar(stmt);stmt=null;
            }return true;
        }finally{closeQuietly(rs);ConnectionHelper.cerrar(stmt);ConnectionHelper.cerrar(advisory);}
    }

    public int getEstadoRequerimientoForUpdate(
            Connection con,
            int idRequerimientoCompra) throws Exception {

        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareCall(
                    SQL_GET_ESTADO_REQUERIMIENTO_FOR_UPDATE
            );
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt);
        }
    }

    public void cambiarEstado(
            Connection con,
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        int anterior = getEstadoRequerimientoForUpdate(con, idRequerimientoCompra);
        if (new EditarRequerimientoCompraServiceImpl().persistirEstado(con,
                idRequerimientoCompra, anterior, idEstadoNuevo, usuario, null, false) != 1) {
            throw new SQLException("El requerimiento cambio mientras se vinculaba el Reclamo Prestacional.");
        }
    }

    public void guardarObservacionEstadoReclamo(
            Connection con,
            int idReclamoPrestacional,
            int idEstado,
            String observacion) throws Exception {

        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(SQL_GUARDAR_OBSERVACION_ESTADO_RECLAMO);
            stmt.setString(1, observacion);
            stmt.setInt(2, idReclamoPrestacional);
            stmt.setInt(3, idEstado);

            if (stmt.executeUpdate() != 1) {
                throw new Exception(
                        "No se pudo registrar la observacion inicial "
                                + "del Reclamo Prestacional."
                );
            }
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
        relacion.setBajaFecha(rs.getTimestamp("baja_fecha"));
        relacion.setBajaUsr(rs.getString("baja_usr"));

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
