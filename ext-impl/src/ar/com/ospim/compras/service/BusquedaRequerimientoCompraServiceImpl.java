package ar.com.ospim.compras.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.beans.RequerimientoCompraFiltro;
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
            if (filtro == null) {
                filtro = new RequerimientoCompraFiltro();
            }

            String sql = "{call compras.buscar_requerimientos(?,?,?,?,?,?,?,?,?,?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);

            setNullableInteger(stmt, 1, filtro.getNumero());
            setNullableDate(stmt, 2, filtro.getFechaDesde());
            setNullableDate(stmt, 3, filtro.getFechaHasta());
            setNullableInteger(stmt, 4, filtro.getIdSector());
            setNullableInteger(stmt, 5, filtro.getIdEstado());
            stmt.setString(6, emptyToNull(filtro.getSolicitanteUsr()));
            stmt.setString(7, emptyToNull(filtro.getAfiliadoCuilTitular()));
            setNullableInteger(stmt, 8, filtro.getAfiliadoInte());
            stmt.setString(9, emptyToNull(filtro.getTipoArticulo()));
            stmt.setString(10, emptyToNull(filtro.getTexto()));

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
            String sql = "{call compras.get_requerimiento(?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);

            rs = stmt.executeQuery();

            if (rs.next()) {
                requerimiento = mapRequerimiento(rs);
                requerimiento.setDetalles(getDetalles(idRequerimientoCompra));
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

    public List<RequerimientoCompraDetalle> getDetalles(int idRequerimientoCompra) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();

        try {
            String sql = "{call compras.get_requerimiento_detalle(?)}";

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(sql);
            stmt.setInt(1, idRequerimientoCompra);

            rs = stmt.executeQuery();

            while (rs.next()) {
                detalles.add(mapDetalle(rs));
            }
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return detalles;
    }

    private RequerimientoCompra mapRequerimiento(ResultSet rs) throws Exception {
        RequerimientoCompra r = new RequerimientoCompra();

        r.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));
        r.setNumero(rs.getInt("numero"));

        r.setIdEstado(rs.getInt("id_estado"));
        r.setEstadoCodigo(rs.getString("estado_codigo"));
        r.setEstadoDescripcion(rs.getString("estado_descripcion"));

        int idSector = rs.getInt("id_sector");
        r.setIdSector(rs.wasNull() ? null : Integer.valueOf(idSector));
        r.setSectorCodigo(rs.getString("sector_codigo"));
        r.setSectorDescripcion(rs.getString("sector_descripcion"));

        r.setRequiereAfiliado(rs.getBoolean("requiere_afiliado"));
        r.setFechaSolicitud(rs.getDate("fecha_solicitud"));

        r.setSolicitanteUsr(rs.getString("solicitante_usr"));
        r.setSolicitanteNombre(rs.getString("solicitante_nombre"));

        r.setAfiliadoCuilTitular(rs.getString("afiliado_cuil_titular"));

        int afiliadoInte = rs.getInt("afiliado_inte");
        r.setAfiliadoInte(rs.wasNull() ? null : Integer.valueOf(afiliadoInte));

        r.setDescripcion(rs.getString("descripcion"));
        r.setObservaciones(rs.getString("observaciones"));

        r.setAltaFecha(rs.getTimestamp("alta_fecha"));
        r.setAltaUsr(rs.getString("alta_usr"));
        r.setModiFecha(rs.getTimestamp("modi_fecha"));
        r.setModiUsr(rs.getString("modi_usr"));
        r.setBajaFecha(rs.getTimestamp("baja_fecha"));
        r.setBajaUsr(rs.getString("baja_usr"));

        return r;
    }

    private RequerimientoCompraDetalle mapDetalle(ResultSet rs) throws Exception {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();

        d.setIdRequerimientoDetalle(rs.getInt("id_requerimiento_detalle"));
        d.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));
        d.setRenglon(rs.getInt("renglon"));
        d.setTipoArticulo(rs.getString("tipo_articulo"));
        d.setArticulo(rs.getString("articulo"));
        d.setCantidad(getBigDecimal(rs, "cantidad"));
        d.setUnidadMedida(rs.getString("unidad_medida"));
        d.setPrecioUnitarioEstimado(getNullableBigDecimal(rs, "precio_unitario_estimado"));
        d.setPrecioTotalEstimado(getNullableBigDecimal(rs, "precio_total_estimado"));
        d.setObservaciones(rs.getString("observaciones"));

        d.setAltaFecha(rs.getTimestamp("alta_fecha"));
        d.setAltaUsr(rs.getString("alta_usr"));
        d.setModiFecha(rs.getTimestamp("modi_fecha"));
        d.setModiUsr(rs.getString("modi_usr"));
        d.setBajaFecha(rs.getTimestamp("baja_fecha"));
        d.setBajaUsr(rs.getString("baja_usr"));

        return d;
    }

    private void setNullableInteger(CallableStatement stmt, int index, Integer value) throws Exception {
        if (value == null || value.intValue() < 0) {
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

    private BigDecimal getNullableBigDecimal(ResultSet rs, String column) throws Exception {
        return rs.getBigDecimal(column);
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
