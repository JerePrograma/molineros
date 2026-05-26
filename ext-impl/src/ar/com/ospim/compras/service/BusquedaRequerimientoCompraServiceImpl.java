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
import ar.com.ospim.compras.beans.RequerimientoCompraEstado;
import ar.com.ospim.compras.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.beans.RequerimientoCompraSector;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class BusquedaRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(BusquedaRequerimientoCompraServiceImpl.class);

    private static final String SQL_BUSCAR_REQUERIMIENTOS =
            "{call compras.buscar_requerimientos(?,?,?,?,?,?,?,?,?,?)}";

    private static final String SQL_GET_REQUERIMIENTO =
            "{call compras.get_requerimiento(?)}";

    private static final String SQL_GET_REQUERIMIENTO_DETALLE =
            "{call compras.get_requerimiento_detalle(?)}";

    private static final String SQL_LISTAR_ESTADOS =
            "{call compras.listar_requerimientos_estados()}";

    private static final String SQL_LISTAR_SECTORES =
            "{call compras.listar_requerimientos_sectores()}";

    private static final String SQL_GET_ESTADO =
            "{call compras.get_requerimiento_estado(?)}";

    private static final String SQL_GET_SECTOR =
            "{call compras.get_requerimiento_sector(?)}";

    public List<RequerimientoCompra> buscarRequerimientos(RequerimientoCompraFiltro filtro) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        List<RequerimientoCompra> requerimientos = new ArrayList<RequerimientoCompra>();

        try {
            if (filtro == null) {
                filtro = new RequerimientoCompraFiltro();
            }

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BUSCAR_REQUERIMIENTOS);

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
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_REQUERIMIENTO);
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
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_REQUERIMIENTO_DETALLE);
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

    public List<RequerimientoCompraEstado> listarEstados() throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        List<RequerimientoCompraEstado> estados = new ArrayList<RequerimientoCompraEstado>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_ESTADOS);
            rs = stmt.executeQuery();

            while (rs.next()) {
                estados.add(mapEstado(rs));
            }
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return estados;
    }

    public List<RequerimientoCompraSector> listarSectores() throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        List<RequerimientoCompraSector> sectores = new ArrayList<RequerimientoCompraSector>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_LISTAR_SECTORES);
            rs = stmt.executeQuery();

            while (rs.next()) {
                sectores.add(mapSector(rs));
            }
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }

        return sectores;
    }

    public RequerimientoCompraEstado getEstado(int idEstado) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_ESTADO);
            stmt.setInt(1, idEstado);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapEstado(rs);
            }

            return null;
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public RequerimientoCompraSector getSector(int idSector) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_SECTOR);
            stmt.setInt(1, idSector);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapSector(rs);
            }

            return null;
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private RequerimientoCompra mapRequerimiento(ResultSet rs) throws Exception {
        RequerimientoCompra r = new RequerimientoCompra();

        r.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));
        r.setNumero(rs.getInt("numero"));

        r.setIdEstado(rs.getInt("id_estado"));
        r.setEstadoCodigo(getString(rs, "estado_codigo"));
        r.setEstadoDescripcion(getString(rs, "estado_descripcion"));

        int idSector = rs.getInt("id_sector");
        r.setIdSector(rs.wasNull() ? null : Integer.valueOf(idSector));
        r.setSectorCodigo(getString(rs, "sector_codigo"));
        r.setSectorDescripcion(getString(rs, "sector_descripcion"));

        r.setRequiereAfiliado(rs.getBoolean("requiere_afiliado"));
        r.setFechaSolicitud(rs.getDate("fecha_solicitud"));

        r.setSolicitanteUsr(getString(rs, "solicitante_usr"));
        r.setSolicitanteNombre(getString(rs, "solicitante_nombre"));

        r.setAfiliadoCuilTitular(getString(rs, "afiliado_cuil_titular"));

        int afiliadoInte = rs.getInt("afiliado_inte");
        r.setAfiliadoInte(rs.wasNull() ? null : Integer.valueOf(afiliadoInte));

        r.setDescripcion(getString(rs, "descripcion"));
        r.setObservaciones(getString(rs, "observaciones"));

        r.setAltaFecha(rs.getTimestamp("alta_fecha"));
        r.setAltaUsr(getString(rs, "alta_usr"));
        r.setModiFecha(rs.getTimestamp("modi_fecha"));
        r.setModiUsr(getString(rs, "modi_usr"));
        r.setBajaFecha(rs.getTimestamp("baja_fecha"));
        r.setBajaUsr(getString(rs, "baja_usr"));

        return r;
    }

    private RequerimientoCompraDetalle mapDetalle(ResultSet rs) throws Exception {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();

        d.setIdRequerimientoDetalle(rs.getInt("id_requerimiento_detalle"));
        d.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));
        d.setRenglon(rs.getInt("renglon"));
        d.setTipoArticulo(getString(rs, "tipo_articulo"));
        d.setArticulo(getString(rs, "articulo"));
        d.setCantidad(getBigDecimal(rs, "cantidad"));
        d.setUnidadMedida(getString(rs, "unidad_medida"));
        d.setPrecioUnitarioEstimado(getNullableBigDecimal(rs, "precio_unitario_estimado"));
        d.setPrecioTotalEstimado(getNullableBigDecimal(rs, "precio_total_estimado"));
        d.setObservaciones(getString(rs, "observaciones"));

        d.setAltaFecha(rs.getTimestamp("alta_fecha"));
        d.setAltaUsr(getString(rs, "alta_usr"));
        d.setModiFecha(rs.getTimestamp("modi_fecha"));
        d.setModiUsr(getString(rs, "modi_usr"));
        d.setBajaFecha(rs.getTimestamp("baja_fecha"));
        d.setBajaUsr(getString(rs, "baja_usr"));

        return d;
    }

    private RequerimientoCompraEstado mapEstado(ResultSet rs) throws Exception {
        RequerimientoCompraEstado estado = new RequerimientoCompraEstado();

        estado.setIdEstado(rs.getInt("id_estado"));
        estado.setCodigo(getString(rs, "codigo"));
        estado.setDescripcion(getString(rs, "descripcion"));

        if (hasColumn(rs, "orden")) {
            int orden = rs.getInt("orden");
            estado.setOrden(rs.wasNull() ? null : Integer.valueOf(orden));
        }

        if (hasColumn(rs, "activo")) {
            estado.setActivo(rs.getBoolean("activo"));
        }

        return estado;
    }

    private RequerimientoCompraSector mapSector(ResultSet rs) throws Exception {
        RequerimientoCompraSector sector = new RequerimientoCompraSector();

        sector.setIdSector(rs.getInt("id_sector"));
        sector.setCodigo(getString(rs, "codigo"));
        sector.setDescripcion(getString(rs, "descripcion"));
        sector.setRequiereAfiliado(rs.getBoolean("requiere_afiliado"));

        if (hasColumn(rs, "activo")) {
            sector.setActivo(rs.getBoolean("activo"));
        }

        return sector;
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

    private String getString(ResultSet rs, String column) throws Exception {
        if (!hasColumn(rs, column)) {
            return null;
        }

        return rs.getString(column);
    }

    private BigDecimal getBigDecimal(ResultSet rs, String column) throws Exception {
        BigDecimal value = getNullableBigDecimal(rs, column);
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal getNullableBigDecimal(ResultSet rs, String column) throws Exception {
        if (!hasColumn(rs, column)) {
            return null;
        }

        return rs.getBigDecimal(column);
    }

    private boolean hasColumn(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (Exception e) {
            return false;
        }
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
