package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSeguimiento;
import ar.com.ospim.util.ConnectionHelper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/** JDBC adaptado de obtener/insertar/actualizarSuministro y su orden de bloqueos. */
public class RequerimientoCompraOrdenSeguimientoServiceImpl {
    private static final String COLUMNAS = "id_orden_compra,calificacion_proveedor,observaciones_proveedor,"
            + "factura_ingresada,numero_factura,fecha_factura,estado_liquidacion,liquidacion_auditada,"
            + "estado_pago,id_liquidacion,id_orden_pago,version_edicion,alta_fecha,alta_usr,modi_fecha,modi_usr";

    public RequerimientoCompraOrdenSeguimiento obtener(int idOrden) throws Exception {
        Connection con = null;
        try {
            con = ConnectionHelper.getConnection();
            if (con == null) throw new SQLException("No se pudo obtener la conexion de Compras.");
            return obtener(con, idOrden);
        } finally { ConnectionHelper.cerrar(con); }
    }

    private RequerimientoCompraOrdenSeguimiento obtener(Connection con, int idOrden)
            throws Exception {
        PreparedStatement stmt = con.prepareStatement("SELECT " + COLUMNAS
                + " FROM compras.requerimiento_orden_compra_seguimiento WHERE id_orden_compra=?");
        try {
            stmt.setInt(1, idOrden);
            ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) return null;
                RequerimientoCompraOrdenSeguimiento datos = new RequerimientoCompraOrdenSeguimiento();
                datos.setIdOrdenCompra(rs.getInt("id_orden_compra"));
                datos.setCalificacionProveedor(rs.getString("calificacion_proveedor"));
                datos.setObservacionesProveedor(rs.getString("observaciones_proveedor"));
                datos.setFacturaIngresada(rs.getString("factura_ingresada"));
                datos.setNumeroFactura(rs.getString("numero_factura"));
                datos.setFechaFactura(rs.getDate("fecha_factura"));
                datos.setEstadoLiquidacion(rs.getString("estado_liquidacion"));
                datos.setLiquidacionAuditada(rs.getBoolean("liquidacion_auditada"));
                datos.setEstadoPago(rs.getString("estado_pago"));
                int liquidacion = rs.getInt("id_liquidacion");
                datos.setIdLiquidacion(rs.wasNull() ? null : Integer.valueOf(liquidacion));
                int pago = rs.getInt("id_orden_pago");
                datos.setIdOrdenPago(rs.wasNull() ? null : Integer.valueOf(pago));
                datos.setVersionEdicion(rs.getInt("version_edicion"));
                datos.setAltaFecha(rs.getTimestamp("alta_fecha"));
                datos.setAltaUsr(rs.getString("alta_usr"));
                datos.setModiFecha(rs.getTimestamp("modi_fecha"));
                datos.setModiUsr(rs.getString("modi_usr"));
                return datos;
            } finally { rs.close(); }
        } finally { stmt.close(); }
    }

    public Transaccion abrirTransaccion() throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();
        if (con == null) throw new SQLException("No se pudo obtener la conexion de Compras.");
        return new Transaccion(this, con);
    }

    private int cargarCampos(PreparedStatement stmt, int posicion,
            RequerimientoCompraOrdenSeguimiento datos) throws Exception {
        stmt.setString(posicion++, datos.getCalificacionProveedor());
        stmt.setString(posicion++, datos.getObservacionesProveedor());
        stmt.setString(posicion++, datos.getFacturaIngresada());
        stmt.setString(posicion++, datos.getNumeroFactura());
        stmt.setDate(posicion++, datos.getFechaFactura() == null ? null
                : new java.sql.Date(datos.getFechaFactura().getTime()));
        stmt.setString(posicion++, datos.getEstadoLiquidacion());
        stmt.setBoolean(posicion++, datos.isLiquidacionAuditada());
        stmt.setString(posicion++, datos.getEstadoPago());
        if (datos.getIdLiquidacion() == null) stmt.setNull(posicion++, Types.INTEGER);
        else stmt.setInt(posicion++, datos.getIdLiquidacion().intValue());
        if (datos.getIdOrdenPago() == null) stmt.setNull(posicion++, Types.INTEGER);
        else stmt.setInt(posicion++, datos.getIdOrdenPago().intValue());
        return posicion;
    }

    public static class Transaccion {
        private final RequerimientoCompraOrdenSeguimientoServiceImpl service;
        private final Connection con;
        private Transaccion(RequerimientoCompraOrdenSeguimientoServiceImpl service, Connection con) {
            this.service = service;
            this.con = con;
        }
        private void bloquear(String sql, int id) throws Exception {
            PreparedStatement stmt = con.prepareStatement(sql);
            try {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                try { while (rs.next()) rs.getObject(1); }
                finally { rs.close(); }
            } finally { stmt.close(); }
        }

        public RequerimientoCompra bloquearYObtenerRequerimiento(int id) throws Exception {
            CallableStatement call = con.prepareCall("{call compras.bloquear_requerimiento_reclamo_prestacional(?)}");
            try { call.setInt(1, id); call.execute(); }
            finally { call.close(); }
            // Mismo orden que el seguimiento de suministro/comercial para evitar interbloqueos.
            bloquear("SELECT rp.id FROM autorizaciones.reclamos_prestacionales rp "
                    + "JOIN compras.requerimiento_reclamo_prestacional rr ON rr.id_reclamo_prestacional=rp.id "
                    + "WHERE rr.id_requerimiento=? ORDER BY rp.id FOR UPDATE OF rp", id);
            bloquear("SELECT id_requerimiento FROM compras.requerimiento_reclamo_prestacional "
                    + "WHERE id_requerimiento=? FOR UPDATE", id);
            bloquear("SELECT id_requerimiento FROM compras.requerimiento WHERE id_requerimiento=? FOR UPDATE", id);
            bloquear("SELECT id_detalle FROM compras.requerimiento_detalle WHERE id_requerimiento=? "
                    + "AND baja_fecha IS NULL ORDER BY id_detalle FOR UPDATE", id);
            bloquear("SELECT id_requerimiento_presupuesto FROM compras.requerimiento_presupuesto "
                    + "WHERE id_requerimiento=? ORDER BY id_requerimiento_presupuesto FOR UPDATE", id);
            bloquear("SELECT id_comparativa FROM compras.requerimiento_comparativa "
                    + "WHERE id_requerimiento=? ORDER BY id_comparativa FOR UPDATE", id);
            bloquear("SELECT cd.id_detalle FROM compras.requerimiento_comparativa_detalle cd "
                    + "JOIN compras.requerimiento_comparativa c ON c.id_comparativa=cd.id_comparativa "
                    + "WHERE c.id_requerimiento=? ORDER BY cd.id_detalle FOR UPDATE OF cd", id);
            bloquear("SELECT id_orden_compra FROM compras.requerimiento_orden_compra "
                    + "WHERE id_requerimiento=? FOR UPDATE", id);
            bloquear("SELECT od.id_orden_compra FROM compras.requerimiento_orden_compra_detalle od "
                    + "JOIN compras.requerimiento_orden_compra o ON o.id_orden_compra=od.id_orden_compra "
                    + "WHERE o.id_requerimiento=? ORDER BY od.orden FOR UPDATE OF od", id);
            PreparedStatement stmt = con.prepareStatement("SELECT id_requerimiento,estado,id_sector,baja_fecha "
                    + "FROM compras.requerimiento WHERE id_requerimiento=?");
            try {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (!rs.next()) return null;
                    RequerimientoCompra req = RequerimientoCompra.paraMapeo();
                    req.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));
                    req.setEstado(rs.getInt("estado"));
                    req.setSectorId(Integer.valueOf(rs.getInt("id_sector")));
                    req.setBajaFecha(rs.getTimestamp("baja_fecha"));
                    return req;
                } finally { rs.close(); }
            } finally { stmt.close(); }
        }

        public Map<String, Object> obtenerSector(int id) throws Exception {
            PreparedStatement stmt = con.prepareStatement("SELECT activo,baja_fecha FROM compras.sector_requerimiento "
                    + "WHERE id_sector=? FOR SHARE");
            try {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (!rs.next()) return null;
                    Map<String, Object> sector = new HashMap<String, Object>();
                    sector.put("activo", Boolean.valueOf(rs.getBoolean("activo")));
                    sector.put("baja", rs.getTimestamp("baja_fecha"));
                    return sector;
                } finally { rs.close(); }
            } finally { stmt.close(); }
        }

        public RequerimientoCompraOrden obtenerOrden(int idRequerimiento) throws Exception {
            PreparedStatement stmt = con.prepareStatement("SELECT id_orden_compra,id_requerimiento "
                    + "FROM compras.requerimiento_orden_compra WHERE id_requerimiento=?");
            try {
                stmt.setInt(1, idRequerimiento);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (!rs.next()) return null;
                    RequerimientoCompraOrden orden = new RequerimientoCompraOrden();
                    orden.setIdOrdenCompra(rs.getInt("id_orden_compra"));
                    orden.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));
                    return orden;
                } finally { rs.close(); }
            } finally { stmt.close(); }
        }

        public String getConfiguracionCompra(String clave) throws Exception {
            return BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave, con);
        }
        public RequerimientoCompraOrdenSeguimiento obtener(int idOrden) throws Exception {
            return service.obtener(con, idOrden);
        }
        public int insertar(RequerimientoCompraOrdenSeguimiento datos, String usuario) throws Exception {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO compras.requerimiento_orden_compra_seguimiento "
                    + "(id_orden_compra,calificacion_proveedor,observaciones_proveedor,factura_ingresada,numero_factura,"
                    + "fecha_factura,estado_liquidacion,liquidacion_auditada,estado_pago,id_liquidacion,id_orden_pago,alta_usr,version_edicion) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,1)");
            try {
                stmt.setInt(1, datos.getIdOrdenCompra());
                int posicion = service.cargarCampos(stmt, 2, datos);
                stmt.setString(posicion, usuario);
                return stmt.executeUpdate();
            } finally { stmt.close(); }
        }
        public int actualizar(RequerimientoCompraOrdenSeguimiento datos, String usuario) throws Exception {
            PreparedStatement stmt = con.prepareStatement("UPDATE compras.requerimiento_orden_compra_seguimiento "
                    + "SET calificacion_proveedor=?,observaciones_proveedor=?,factura_ingresada=?,numero_factura=?,"
                    + "fecha_factura=?,estado_liquidacion=?,liquidacion_auditada=?,estado_pago=?,id_liquidacion=?,id_orden_pago=?,"
                    + "modi_fecha=now(),modi_usr=?,version_edicion=version_edicion+1 WHERE id_orden_compra=? AND version_edicion=?");
            try {
                int posicion = service.cargarCampos(stmt, 1, datos);
                stmt.setString(posicion++, usuario);
                stmt.setInt(posicion++, datos.getIdOrdenCompra());
                stmt.setInt(posicion, datos.getVersionEdicion());
                return stmt.executeUpdate();
            } finally { stmt.close(); }
        }
        public void commit() throws Exception { con.commit(); }
        public void rollback() throws Exception { con.rollback(); }
        public void cerrar() { ConnectionHelper.cerrar(con); }
    }
}
