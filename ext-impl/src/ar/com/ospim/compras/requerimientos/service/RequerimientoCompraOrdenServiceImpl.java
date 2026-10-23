package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RequerimientoCompraOrdenServiceImpl {

    private static final Log _log = LogFactoryUtil.getLog(
            RequerimientoCompraOrdenServiceImpl.class);

    public RequerimientoCompraOrden obtenerPorRequerimiento(int idRequerimiento)
            throws Exception {
        Connection con = null;
        try {
            con = ConnectionHelper.getConnection();
            return obtenerPorRequerimiento(con, idRequerimiento);
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    private RequerimientoCompraOrden obtenerPorRequerimiento(Connection con,
            int idRequerimiento) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT * FROM compras.requerimiento_orden_compra "
                + "WHERE id_requerimiento = ?");
        RequerimientoCompraOrden orden = null;
        try {
            stmt.setInt(1, idRequerimiento);
            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    orden = new RequerimientoCompraOrden();
                    orden.setIdOrdenCompra(rs.getInt("id_orden_compra"));
                    orden.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));
                    orden.setAltaUsr(rs.getString("alta_usr"));
                    orden.setFechaEmision(rs.getDate("fecha_emision"));
                    orden.setFechaOrdenMedica(rs.getDate("fecha_orden_medica"));
                    orden.setFechaRecepcion(rs.getDate("fecha_recepcion"));
                    orden.setMoneda(rs.getString("moneda"));
                    orden.setProveedor(rs.getString("proveedor"));
                    orden.setProveedorCuit(rs.getString("proveedor_cuit"));
                    orden.setProveedorDomicilio(rs.getString("proveedor_domicilio"));
                    orden.setProveedorContacto(rs.getString("proveedor_contacto"));
                    orden.setProveedorEmail(rs.getString("proveedor_email"));
                    orden.setProveedorTelefono(rs.getString("proveedor_telefono"));
                    orden.setCondicion(rs.getString("condicion"));
                    orden.setFormaPago(rs.getString("forma_pago"));
                    orden.setPlazoPago(rs.getString("plazo_pago"));
                    orden.setEntrega(rs.getString("entrega"));
                    orden.setObservaciones(rs.getString("observaciones"));
                    orden.setConfeccionadoPor(rs.getString("confeccionado_por"));
                    orden.setTelefonoCompras(rs.getString("telefono_compras"));
                    orden.setSolicitante(rs.getString("solicitante"));
                    orden.setPlan(rs.getString("plan"));
                    orden.setIdReclamoPrestacional(rs.getInt("id_reclamo_prestacional"));
                    orden.setIva(rs.getBigDecimal("iva"));
                    orden.setIibb(rs.getBigDecimal("iibb"));
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
        if (orden == null) {
            return null;
        }
        stmt = con.prepareStatement(
                "SELECT * FROM compras.requerimiento_orden_compra_detalle "
                + "WHERE id_orden_compra = ? ORDER BY orden");
        try {
            stmt.setInt(1, orden.getIdOrdenCompra());
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();
                    detalle.setId(Integer.valueOf(rs.getInt("id_requerimiento_detalle")));
                    detalle.setIdRequerimiento(Integer.valueOf(idRequerimiento));
                    detalle.setCodigoItem(rs.getString("codigo_item"));
                    detalle.setDescripcionItem(rs.getString("descripcion_item"));
                    detalle.setCantidad(Integer.valueOf(rs.getInt("cantidad")));
                    detalle.setPrecioUnitarioEstimado(rs.getBigDecimal("precio_unitario"));
                    detalle.setPrecioTotalEstimado(rs.getBigDecimal("precio_total"));
                    orden.getDetalles().add(detalle);
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
        return orden;
    }

    public RequerimientoCompraOrden crear(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();
        if (con == null) {
            throw new SQLException("No se pudo obtener la conexion de Compras.");
        }
        try {
            bloquearCreacion(con, orden.getIdRequerimientoCompra(), usuario);
            RequerimientoCompraOrden existente = obtenerPorRequerimiento(
                    con, orden.getIdRequerimientoCompra());
            if (existente != null) {
                confirmar(con, orden.getIdRequerimientoCompra(), usuario);
                con.commit();
                return existente;
            }
            guardarCabecera(con, orden, usuario);
            int posicion = 0;
            for (RequerimientoCompraDetalle detalle : orden.getDetalles()) {
                guardarDetalle(con, orden.getIdOrdenCompra(), ++posicion, detalle);
            }
            confirmar(con, orden.getIdRequerimientoCompra(), usuario);
            RequerimientoCompraOrden persistida = obtenerPorRequerimiento(
                    con, orden.getIdRequerimientoCompra());
            con.commit();
            return persistida;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException rollbackError) {
                _log.error("No se pudo revertir la creacion de la Orden de Compra.", rollbackError);
            }
            throw e;
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    private void bloquearCreacion(Connection con, int idRequerimiento,
            String usuario) throws Exception {
        CallableStatement stmt = con.prepareCall(
                "{ ? = call compras.bloquear_creacion_orden_compra(?,?) }");
        try {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimiento);
            stmt.setString(3, usuario);
            stmt.execute();
        } finally {
            stmt.close();
        }
    }

    private void confirmar(Connection con, int idRequerimiento,
            String usuario) throws Exception {
        CallableStatement stmt = con.prepareCall(
                "{ ? = call compras.confirmar_orden_compra_requerimiento(?,?) }");
        try {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimiento);
            stmt.setString(3, usuario);
            stmt.execute();
            if (stmt.getInt(1) != WebKeysCompras.ESTADO_ORDEN_COMPRA) {
                throw new SQLException("No se confirmo la Orden de Compra.");
            }
        } finally {
            stmt.close();
        }
    }

    private void guardarCabecera(Connection con, RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO compras.requerimiento_orden_compra "
                + "(id_requerimiento, alta_usr, fecha_emision, fecha_orden_medica, fecha_recepcion, "
                + "moneda, proveedor, proveedor_cuit, proveedor_domicilio, proveedor_contacto, "
                + "proveedor_email, proveedor_telefono, condicion, forma_pago, plazo_pago, "
                + "entrega, observaciones, confeccionado_por, telefono_compras, solicitante, "
                + "plan, id_reclamo_prestacional, iva, iibb) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_orden_compra");
        try {
            stmt.setInt(1, orden.getIdRequerimientoCompra());
            stmt.setString(2, usuario);
            stmt.setDate(3, orden.getFechaEmision() == null ? null
                    : new java.sql.Date(orden.getFechaEmision().getTime()));
            stmt.setDate(4, orden.getFechaOrdenMedica() == null ? null
                    : new java.sql.Date(orden.getFechaOrdenMedica().getTime()));
            stmt.setDate(5, orden.getFechaRecepcion() == null ? null
                    : new java.sql.Date(orden.getFechaRecepcion().getTime()));
            stmt.setString(6, orden.getMoneda());
            stmt.setString(7, orden.getProveedor());
            stmt.setString(8, orden.getProveedorCuit());
            stmt.setString(9, orden.getProveedorDomicilio());
            stmt.setString(10, orden.getProveedorContacto());
            stmt.setString(11, orden.getProveedorEmail());
            stmt.setString(12, orden.getProveedorTelefono());
            stmt.setString(13, orden.getCondicion());
            stmt.setString(14, orden.getFormaPago());
            stmt.setString(15, orden.getPlazoPago());
            stmt.setString(16, orden.getEntrega());
            stmt.setString(17, orden.getObservaciones());
            stmt.setString(18, orden.getConfeccionadoPor());
            stmt.setString(19, orden.getTelefonoCompras());
            stmt.setString(20, orden.getSolicitante());
            stmt.setString(21, orden.getPlan());
            stmt.setInt(22, orden.getIdReclamoPrestacional());
            stmt.setBigDecimal(23, orden.getIva());
            stmt.setBigDecimal(24, orden.getIibb());
            ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) {
                    throw new SQLException("No se obtuvo el numero de Orden de Compra.");
                }
                orden.setIdOrdenCompra(rs.getInt(1));
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    private void guardarDetalle(Connection con, int idOrdenCompra,
            int posicion, RequerimientoCompraDetalle detalle) throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO compras.requerimiento_orden_compra_detalle "
                + "(id_orden_compra, orden, id_requerimiento_detalle, codigo_item, "
                + "descripcion_item, cantidad, precio_unitario, precio_total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            stmt.setInt(1, idOrdenCompra);
            stmt.setInt(2, posicion);
            stmt.setInt(3, detalle.getIdInt());
            stmt.setString(4, detalle.getCodigoItemVisible());
            stmt.setString(5, detalle.getDescripcionItemVisible());
            stmt.setInt(6, detalle.getCantidadInt());
            stmt.setBigDecimal(7, detalle.getPrecioUnitarioEstimado());
            stmt.setBigDecimal(8, detalle.getPrecioTotalEstimado());
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("No se pudo guardar el detalle de la Orden de Compra.");
            }
        } finally {
            stmt.close();
        }
    }
}
