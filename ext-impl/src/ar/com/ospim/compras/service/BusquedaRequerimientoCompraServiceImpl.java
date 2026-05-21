package ar.com.ospim.compras.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraAdjunto;
import ar.com.ospim.compras.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.beans.RequerimientoCompraHistorial;
import ar.com.ospim.compras.beans.RequerimientoCompraItem;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class BusquedaRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(BusquedaRequerimientoCompraServiceImpl.class);

    public List<RequerimientoCompra> buscarRequerimientos(RequerimientoCompraFiltro filtro) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompra> requerimientos = new ArrayList<RequerimientoCompra>();

        try {
            String sql = "{call buscar_requerimientos_compra(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            setNullableInteger(stmt, 1, filtro.getNumero());
            setNullableDate(stmt, 2, filtro.getFechaDesde());
            setNullableDate(stmt, 3, filtro.getFechaHasta());
            setNullableInteger(stmt, 4, filtro.getSectorId());
            stmt.setString(5, emptyToNull(filtro.getSolicitanteUsr()));
            stmt.setString(6, emptyToNull(filtro.getEntidad()));
            setNullableInteger(stmt, 7, filtro.getPrioridad());
            setNullableInteger(stmt, 8, filtro.getEstado());
            stmt.setString(9, emptyToNull(filtro.getTexto()));

            stmt.setString(10, emptyToNull(filtro.getAfiliado()));
            stmt.setString(11, emptyToNull(filtro.getDni()));
            stmt.setString(12, emptyToNull(filtro.getDetalleRequerimiento()));
            setNullableInteger(stmt, 13, filtro.getRpNumero());

            Integer ordenCompra = filtro.getOrdenCompraNumero();
            if ((ordenCompra == null || ordenCompra.intValue() <= 0) && filtro.getIdOrdenCompra() != null) {
                ordenCompra = filtro.getIdOrdenCompra();
            }
            setNullableInteger(stmt, 14, ordenCompra);

            setNullableBoolean(stmt, 15, filtro.getRecupero());
            setNullableBoolean(stmt, 16, filtro.getCotizado());
            setNullableDate(stmt, 17, filtro.getFechaPedidoCotizacionDesde());
            setNullableDate(stmt, 18, filtro.getFechaPedidoCotizacionHasta());
            stmt.setString(19, emptyToNull(filtro.getLocalidad()));
            stmt.setString(20, emptyToNull(filtro.getProvincia()));

            rs = stmt.executeQuery();
            while (rs.next()) {
                requerimientos.add(mapRequerimiento(rs));
            }
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return requerimientos;
    }

    public RequerimientoCompra getRequerimientoCompra(int idRequerimientoCompra) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        RequerimientoCompra requerimiento = null;

        try {
            String sql = "{call get_requerimiento_compra(?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);

            rs = stmt.executeQuery();
            if (rs.next()) {
                requerimiento = mapRequerimiento(rs);
                requerimiento.setItems(getItems(idRequerimientoCompra));
                requerimiento.setHistorial(getHistorial(idRequerimientoCompra));
                requerimiento.setAdjuntos(getAdjuntos(idRequerimientoCompra));
            }
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return requerimiento;
    }

    public List<RequerimientoCompraItem> getItems(int idRequerimientoCompra) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraItem> items = new ArrayList<RequerimientoCompraItem>();

        try {
            String sql = "{call get_requerimiento_compra_items(?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);

            rs = stmt.executeQuery();
            while (rs.next()) {
                RequerimientoCompraItem item = new RequerimientoCompraItem();
                item.setIdItem(rs.getInt("id_item"));
                item.setIdRequerimientoCompra(rs.getInt("id_requerimiento_compra"));
                item.setDescripcion(rs.getString("descripcion"));
                item.setCantidad(getBigDecimal(rs, "cantidad"));
                item.setUnidadMedida(rs.getString("unidad_medida"));
                item.setImporteEstimado(getBigDecimal(rs, "importe_estimado"));
                item.setObservaciones(rs.getString("observaciones"));
                item.setEstado(rs.getInt("estado"));
                item.setBajaFecha(rs.getDate("baja_fecha"));
                item.setBajaUsr(rs.getString("baja_usr"));
                items.add(item);
            }
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return items;
    }

    public List<RequerimientoCompraHistorial> getHistorial(int idRequerimientoCompra) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraHistorial> historial = new ArrayList<RequerimientoCompraHistorial>();

        try {
            String sql = "{call get_requerimiento_compra_historial(?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);

            rs = stmt.executeQuery();
            while (rs.next()) {
                RequerimientoCompraHistorial h = new RequerimientoCompraHistorial();
                h.setIdHistorial(rs.getInt("id_historial"));
                h.setIdRequerimientoCompra(rs.getInt("id_requerimiento_compra"));

                int estadoAnterior = rs.getInt("estado_anterior");
                h.setEstadoAnterior(rs.wasNull() ? null : Integer.valueOf(estadoAnterior));

                h.setEstadoNuevo(rs.getInt("estado_nuevo"));
                h.setUsuario(rs.getString("usuario"));
                h.setFecha(rs.getTimestamp("fecha"));
                h.setComentario(rs.getString("comentario"));
                historial.add(h);
            }
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return historial;
    }

    public List<RequerimientoCompraAdjunto> getAdjuntos(int idRequerimientoCompra) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        List<RequerimientoCompraAdjunto> adjuntos = new ArrayList<RequerimientoCompraAdjunto>();

        try {
            String sql = "{call get_requerimiento_compra_adjuntos(?)}";
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);

            rs = stmt.executeQuery();
            while (rs.next()) {
                RequerimientoCompraAdjunto adjunto = new RequerimientoCompraAdjunto();
                adjunto.setIdAdjunto(rs.getInt("id_adjunto"));
                adjunto.setIdRequerimientoCompra(rs.getInt("id_requerimiento_compra"));

                long fileEntryId = rs.getLong("file_entry_id");
                adjunto.setFileEntryId(rs.wasNull() ? null : Long.valueOf(fileEntryId));

                adjunto.setNombreArchivo(rs.getString("nombre_archivo"));
                adjunto.setTipoArchivo(rs.getString("tipo_archivo"));
                adjunto.setAltaUsr(rs.getString("alta_usr"));
                adjunto.setAltaFecha(rs.getTimestamp("alta_fecha"));
                adjunto.setBajaFecha(rs.getTimestamp("baja_fecha"));
                adjunto.setBajaUsr(rs.getString("baja_usr"));
                adjuntos.add(adjunto);
            }
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return adjuntos;
    }

    private RequerimientoCompra mapRequerimiento(ResultSet rs) throws Exception {
        RequerimientoCompra r = new RequerimientoCompra();

        r.setIdRequerimientoCompra(rs.getInt("id_requerimiento_compra"));
        r.setNumero(rs.getInt("numero"));

        r.setAfiliado(rs.getString("afiliado"));
        r.setDni(rs.getString("dni"));

        int sectorId = rs.getInt("sector_id");
        r.setSectorId(rs.wasNull() ? null : Integer.valueOf(sectorId));
        r.setSectorDescripcion(rs.getString("sector_descripcion"));

        r.setSolicitanteUsr(rs.getString("solicitante_usr"));
        r.setEntidad(rs.getString("entidad"));
        r.setPrioridad(rs.getInt("prioridad"));
        r.setEstado(rs.getInt("estado"));

        r.setFechaAlta(rs.getTimestamp("fecha_alta"));
        r.setAltaUsr(rs.getString("alta_usr"));
        r.setFechaModi(rs.getTimestamp("fecha_modi"));
        r.setModiUsr(rs.getString("modi_usr"));
        r.setBajaFecha(rs.getTimestamp("baja_fecha"));
        r.setBajaUsr(rs.getString("baja_usr"));

        r.setFechaSolicitud(rs.getDate("fecha_solicitud"));
        r.setFechaNecesidad(rs.getDate("fecha_necesidad"));
        r.setFechaPedidoCotizacion(rs.getDate("fecha_pedido_cotizacion"));

        r.setDetalleRequerimiento(rs.getString("detalle_requerimiento"));
        r.setMotivo(rs.getString("motivo"));
        r.setObservaciones(rs.getString("observaciones"));

        r.setPedidosPresupuestos(rs.getString("pedidos_presupuestos"));
        r.setComparativa(rs.getString("comparativa"));

        r.setRpNumero(getNullableInteger(rs, "rp_numero"));
        r.setRpObservacion(rs.getString("rp_observacion"));

        r.setIdOrdenCompra(getNullableInteger(rs, "id_orden_compra"));
        r.setOrdenCompraNumero(getNullableInteger(rs, "orden_compra_numero"));
        r.setOrdenCompraObservacion(rs.getString("orden_compra_observacion"));

        r.setCargoOspim(rs.getString("cargo_ospim"));
        r.setCargoEnsalud(rs.getString("cargo_ensalud"));
        r.setRecupero(getNullableBoolean(rs, "recupero"));
        r.setCotizado(getNullableBoolean(rs, "cotizado"));

        r.setLocalidad(rs.getString("localidad"));
        r.setProvincia(rs.getString("provincia"));

        r.setImporteEstimadoTotal(getBigDecimal(rs, "importe_estimado_total"));

        return r;
    }

    private void setNullableInteger(CallableStatement stmt, int index, Integer value) throws Exception {
        if (value == null || value.intValue() <= 0) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }

    private void setNullableDate(CallableStatement stmt, int index, java.util.Date value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.DATE);
        } else {
            stmt.setDate(index, new java.sql.Date(value.getTime()));
        }
    }

    private void setNullableBoolean(CallableStatement stmt, int index, Boolean value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.BOOLEAN);
        } else {
            stmt.setBoolean(index, value.booleanValue());
        }
    }

    private Integer getNullableInteger(ResultSet rs, String column) throws Exception {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : Integer.valueOf(value);
    }

    private Boolean getNullableBoolean(ResultSet rs, String column) throws Exception {
        boolean value = rs.getBoolean(column);
        return rs.wasNull() ? null : Boolean.valueOf(value);
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return value.trim();
    }

    private BigDecimal getBigDecimal(ResultSet rs, String column) throws Exception {
        BigDecimal value = rs.getBigDecimal(column);
        return value != null ? value : BigDecimal.ZERO;
    }

    private void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ignored) {
            }
        }
    }
}
