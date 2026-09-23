package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RequerimientoCompraComparativaServiceImpl {

    private static final Log _log = LogFactoryUtil.getLog(
            RequerimientoCompraComparativaServiceImpl.class);

    public List<RequerimientoCompraComparativa> listar(int idRequerimiento)
            throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        List<RequerimientoCompraComparativa> resultado =
                new ArrayList<RequerimientoCompraComparativa>();
        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    "SELECT c.*, rp.id_prestador AS prestador_id, p.descripcion "
                    + "FROM compras.listar_documentos_requerimiento(?, 1) rp "
                    + "JOIN public.prestador p ON p.id_prestador = rp.id_prestador "
                    + "LEFT JOIN compras.requerimiento_comparativa c "
                    + "ON c.id_requerimiento = rp.id_requerimiento "
                    + "AND c.id_prestador = rp.id_prestador "
                    + "ORDER BY rp.id_prestador");
            stmt.setInt(1, idRequerimiento);
            ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    RequerimientoCompraComparativa c = new RequerimientoCompraComparativa();
                    c.setIdComparativa(rs.getInt("id_comparativa"));
                    c.setIdRequerimiento(idRequerimiento);
                    c.setIdPrestador(rs.getInt("prestador_id"));
                    c.setPrestador(rs.getString("descripcion"));
                    if (c.getIdComparativa() > 0) {
                        c.setFechaPresupuesto(rs.getDate("fecha_presupuesto"));
                        c.setFormaPago(Integer.valueOf(rs.getInt("forma_pago")));
                        c.setPlazoEntrega(rs.getString("plazo_entrega"));
                        int validez = rs.getInt("validez_presupuesto");
                        c.setValidezPresupuesto(rs.wasNull() ? null : Integer.valueOf(validez));
                        c.setEnvio(rs.getString("envio"));
                        c.setIva(rs.getBigDecimal("iva"));
                        c.setIibb(rs.getBigDecimal("iibb"));
                    }
                    resultado.add(c);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            stmt = con.prepareStatement(
                    "SELECT id_detalle, id_prestacion, cantidad, importe_unitario "
                    + "FROM compras.requerimiento_comparativa_detalle "
                    + "WHERE id_comparativa = ? ORDER BY id_prestacion");
            for (RequerimientoCompraComparativa c : resultado) {
                if (c.getIdComparativa() == 0) {
                    continue;
                }
                stmt.setInt(1, c.getIdComparativa());
                rs = stmt.executeQuery();
                try {
                    while (rs.next()) {
                        RequerimientoCompraComparativaDetalle d =
                                new RequerimientoCompraComparativaDetalle();
                        d.setIdDetalle(rs.getInt("id_detalle"));
                        d.setIdPrestacion(rs.getInt("id_prestacion"));
                        int cantidad = rs.getInt("cantidad");
                        d.setCantidad(rs.wasNull() ? null : Integer.valueOf(cantidad));
                        d.setImporteUnitario(rs.getBigDecimal("importe_unitario"));
                        c.getDetalles().add(d);
                    }
                } finally {
                    rs.close();
                }
            }
            return resultado;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void guardar(List<RequerimientoCompraComparativa> comparativas,
            List<Integer> detallesVaciados, String usuario) throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();
        if (con == null) {
            throw new SQLException("No se pudo obtener la conexión de Compras.");
        }
        try {
            for (RequerimientoCompraComparativa c : comparativas) {
                guardarCabecera(con, c, usuario);
                for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
                    guardarDetalle(con, c.getIdComparativa(), d, usuario);
                }
            }
            PreparedStatement stmt = con.prepareStatement(
                    "DELETE FROM compras.requerimiento_comparativa_detalle WHERE id_detalle = ?");
            try {
                for (Integer id : detallesVaciados) {
                    stmt.setInt(1, id.intValue());
                    stmt.executeUpdate();
                }
            } finally {
                stmt.close();
            }
            con.commit();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException rollbackError) {
                _log.error("No se pudo revertir el guardado de la comparativa.", rollbackError);
            }
            throw e;
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    private void guardarCabecera(Connection con, RequerimientoCompraComparativa c,
            String usuario) throws Exception {
        boolean alta = c.getIdComparativa() == 0;
        PreparedStatement stmt = con.prepareStatement(alta
                ? "INSERT INTO compras.requerimiento_comparativa "
                    + "(fecha_presupuesto, forma_pago, plazo_entrega, validez_presupuesto, "
                    + "envio, iva, iibb, alta_usr, id_requerimiento, id_prestador) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_comparativa"
                : "UPDATE compras.requerimiento_comparativa SET fecha_presupuesto = ?, "
                    + "forma_pago = ?, plazo_entrega = ?, validez_presupuesto = ?, "
                    + "envio = ?, iva = ?, iibb = ?, modi_usr = ?, modi_fecha = now() "
                    + "WHERE id_requerimiento = ? AND id_prestador = ? AND id_comparativa = ?");
        try {
            stmt.setDate(1, c.getFechaPresupuesto() == null ? null
                    : new java.sql.Date(c.getFechaPresupuesto().getTime()));
            stmt.setInt(2, c.getFormaPago().intValue());
            stmt.setString(3, c.getPlazoEntrega());
            stmt.setObject(4, c.getValidezPresupuesto(), Types.INTEGER);
            stmt.setString(5, c.getEnvio());
            stmt.setBigDecimal(6, c.getIva());
            stmt.setBigDecimal(7, c.getIibb());
            stmt.setString(8, usuario);
            stmt.setInt(9, c.getIdRequerimiento());
            stmt.setInt(10, c.getIdPrestador());
            if (alta) {
                ResultSet rs = stmt.executeQuery();
                try {
                    if (!rs.next()) {
                        throw new SQLException("No se obtuvo la cabecera de la comparativa.");
                    }
                    c.setIdComparativa(rs.getInt(1));
                } finally {
                    rs.close();
                }
            } else {
                stmt.setInt(11, c.getIdComparativa());
                if (stmt.executeUpdate() != 1) {
                    throw new SQLException("No se pudo actualizar la cabecera de la comparativa.");
                }
            }
        } finally {
            stmt.close();
        }
    }

    private void guardarDetalle(Connection con, int idComparativa,
            RequerimientoCompraComparativaDetalle d, String usuario) throws Exception {
        boolean alta = d.getIdDetalle() == 0;
        PreparedStatement stmt = con.prepareStatement(alta
                ? "INSERT INTO compras.requerimiento_comparativa_detalle "
                    + "(cantidad, importe_unitario, alta_usr, id_comparativa, id_prestacion) "
                    + "VALUES (?, ?, ?, ?, ?)"
                : "UPDATE compras.requerimiento_comparativa_detalle SET cantidad = ?, "
                    + "importe_unitario = ?, modi_usr = ?, modi_fecha = now() "
                    + "WHERE id_comparativa = ? AND id_prestacion = ? AND id_detalle = ?");
        try {
            stmt.setObject(1, d.getCantidad(), Types.INTEGER);
            stmt.setBigDecimal(2, d.getImporteUnitario());
            stmt.setString(3, usuario);
            stmt.setInt(4, idComparativa);
            stmt.setInt(5, d.getIdPrestacion());
            if (!alta) {
                stmt.setInt(6, d.getIdDetalle());
            }
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("No se pudo guardar la prestación de la comparativa.");
            }
        } finally {
            stmt.close();
        }
    }
}
