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
                "SELECT * FROM compras.requerimiento_orden_compra_consulta "
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
                    orden.setModiFecha(rs.getTimestamp("modi_fecha"));
                    orden.setModiUsr(rs.getString("modi_usr"));
                    orden.setVersionEdicion(rs.getInt("version_edicion"));
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
                "SELECT * FROM compras.requerimiento_orden_compra_detalle_consulta "
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

    public RequerimientoCompraOrden actualizar(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();
        if (con == null) {
            throw new SQLException("No se pudo obtener la conexion de Compras.");
        }
        try {
            int idExistente = bloquearCreacion(con, orden.getIdRequerimientoCompra(), usuario);
            if (idExistente <= 0 || idExistente != orden.getIdOrdenCompra()
                    || orden.getVersionEdicion() < 0
                    || orden.getVersionEdicion() == Integer.MAX_VALUE) {
                throw new IllegalArgumentException("La Orden de Compra emitida ya no esta disponible.");
            }
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE compras.requerimiento_orden_compra "
                    + "SET condicion = ?, forma_pago = ?, plazo_pago = ?, entrega = ?, "
                    + "observaciones = ?, modi_fecha = now(), modi_usr = ?, "
                    + "version_edicion = version_edicion + 1 "
                    + "WHERE id_orden_compra = ? AND id_requerimiento = ? AND version_edicion = ?");
            try {
                stmt.setString(1, orden.getCondicion());
                stmt.setString(2, orden.getFormaPago());
                stmt.setString(3, orden.getPlazoPago());
                stmt.setString(4, orden.getEntrega());
                stmt.setString(5, orden.getObservaciones());
                stmt.setString(6, usuario);
                stmt.setInt(7, orden.getIdOrdenCompra());
                stmt.setInt(8, orden.getIdRequerimientoCompra());
                stmt.setInt(9, orden.getVersionEdicion());
                if (stmt.executeUpdate() != 1) {
                    throw new IllegalArgumentException(
                            "La Orden de Compra fue modificada por otro usuario. Actualice la pantalla.");
                }
            } finally {
                stmt.close();
            }
            RequerimientoCompraOrden persistida = obtenerPorRequerimiento(
                    con, orden.getIdRequerimientoCompra());
            con.commit();
            return persistida;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException rollbackError) {
                _log.error("No se pudo revertir la modificacion de la Orden de Compra.", rollbackError);
            }
            throw e;
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    private int bloquearCreacion(Connection con, int idRequerimiento,
            String usuario) throws Exception {
        CallableStatement stmt = con.prepareCall(
                "{ ? = call compras.bloquear_creacion_orden_compra(?,?) }");
        try {
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimiento);
            stmt.setString(3, usuario);
            stmt.execute();
            return stmt.getInt(1);
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

    private int obtenerPresupuestoAdjudicado(Connection con, int idRequerimiento)
            throws Exception {
        PreparedStatement stmt = con.prepareStatement(
                "SELECT rp.id_requerimiento_presupuesto "
                + "FROM compras.requerimiento_presupuesto rp "
                + "JOIN compras.requerimiento r ON r.id_requerimiento = rp.id_requerimiento "
                + "JOIN compras.sector_requerimiento sr ON sr.id_sector = r.id_sector "
                + "WHERE rp.id_requerimiento = ? AND rp.baja_fecha IS NULL "
                + "AND ((compras.normalizar_sector(sr.descripcion) IN ('RRHH', 'SISTEMAS') "
                + "AND rp.tipo_documento = 3 AND rp.empresa_adjudicada) "
                + "OR (compras.normalizar_sector(sr.descripcion) NOT IN ('RRHH', 'SISTEMAS') "
                + "AND rp.tipo_documento = 1 AND rp.id_prestador = ("
                + "SELECT min(d.id_prestador) FROM compras.requerimiento_detalle d "
                + "WHERE d.id_requerimiento = r.id_requerimiento AND d.baja_fecha IS NULL "
                + "HAVING count(DISTINCT d.id_prestador) = 1 "
                + "AND count(*) = count(d.id_prestador))))");
        try {
            stmt.setInt(1, idRequerimiento);
            ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "Debe existir una cotizacion adjudicada unica del requerimiento para emitir la Orden de Compra.");
                }
                int idPresupuesto = rs.getInt("id_requerimiento_presupuesto");
                if (idPresupuesto <= 0 || rs.next()) {
                    throw new IllegalArgumentException(
                            "Debe existir una cotizacion adjudicada unica del requerimiento para emitir la Orden de Compra.");
                }
                return idPresupuesto;
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    private void guardarCabecera(Connection con, RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        int idPresupuesto = obtenerPresupuestoAdjudicado(con,
                orden.getIdRequerimientoCompra());
        PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO compras.requerimiento_orden_compra "
                + "(id_requerimiento, id_presupuesto, alta_usr, fecha_emision, "
                + "condicion, forma_pago, plazo_pago, entrega, observaciones, iva, iibb) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_orden_compra");
        try {
            stmt.setInt(1, orden.getIdRequerimientoCompra());
            stmt.setInt(2, idPresupuesto);
            stmt.setString(3, usuario);
            stmt.setDate(4, orden.getFechaEmision() == null ? null
                    : new java.sql.Date(orden.getFechaEmision().getTime()));
            stmt.setString(5, orden.getCondicion());
            stmt.setString(6, orden.getFormaPago());
            stmt.setString(7, orden.getPlazoPago());
            stmt.setString(8, orden.getEntrega());
            stmt.setString(9, orden.getObservaciones());
            stmt.setBigDecimal(10, orden.getIva());
            stmt.setBigDecimal(11, orden.getIibb());
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
                + "(id_orden_compra, orden, id_requerimiento_detalle, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?, ?)");
        try {
            stmt.setInt(1, idOrdenCompra);
            stmt.setInt(2, posicion);
            stmt.setInt(3, detalle.getIdInt());
            stmt.setInt(4, detalle.getCantidadInt());
            stmt.setBigDecimal(5, detalle.getPrecioUnitarioEstimado());
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("No se pudo guardar el detalle de la Orden de Compra.");
            }
        } finally {
            stmt.close();
        }
    }
}
