package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class BusquedaRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(BusquedaRequerimientoCompraServiceImpl.class);

    private static final String SQL_BUSCAR_REQUERIMIENTOS =
            "{call compras.buscar_requerimientos(?,?,?,?,?,?,?)}";

    private static final String SQL_GET_REQUERIMIENTO =
            "{call compras.get_requerimiento(?)}";

    private static final String SQL_GET_REQUERIMIENTO_DETALLE =
            "{call compras.get_requerimiento_detalle(?)}";

    private static final String SQL_LISTAR_ESTADOS =
            "{call compras.listar_estados_requerimiento()}";

    private static final String SQL_LISTAR_SECTORES =
            "{call compras.listar_sector_requerimiento()}";

    private static final String SQL_GET_ESTADO =
            "{call compras.get_estado_actual_requerimiento(?)}";

    private static final String SQL_GET_SECTOR =
            "{call compras.get_sector_requerimiento(?)}";

    private static final String SQL_BUSCAR_PRESTADORES_ENVIADOS =
            "SELECT id_prestador, descripcion, cuit, email, " +
                    "id_tipo_prestador, tipo_prestador " +
                    "FROM compras.buscar_prestadores_enviados(?, ?, ?)";

    private static final String SQL_LISTAR_PRESTADORES_ENVIADOS =
            "SELECT DISTINCT p.id_prestador, p.descripcion, p.cuit, " +
                    "p.contacto AS email, p.id_tipo_prestador, " +
                    "tp.descripcion AS tipo_prestador, " +
                    "rcp.estado_envio " +
                    "FROM compras.requerimiento r " +
                    "JOIN compras.requerimiento_cotizacion_prestador rcp " +
                    "  ON rcp.id_requerimiento = r.id_requerimiento " +
                    " AND rcp.estado_envio = ? " +
                    "JOIN public.prestador p " +
                    "  ON p.id_prestador = rcp.id_prestador " +
                    "LEFT JOIN trae_tipos_prestadores() tp " +
                    "  ON tp.id_tipo_prestador = p.id_tipo_prestador " +
                    "WHERE r.id_requerimiento = ? " +
                    "  AND r.estado IN (?, ?) " +
                    "ORDER BY p.descripcion, p.cuit, p.id_prestador " +
                    "LIMIT ?";

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

            setNullableInteger(stmt, 1, filtro.getIdEstado());
            setNullableInteger(stmt, 2, filtro.getIdSector());
            stmt.setString(3, emptyToNull(filtro.getAfiliadoCuilTitular()));
            setNullableInteger(stmt, 4, filtro.getAfiliadoInt());
            stmt.setString(5, emptyToNull(filtro.getIdTercerizadora()));
            setNullableBoolean(stmt, 6, filtro.getRecupero());
            stmt.setString(7, emptyToNull(filtro.getTexto()));

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
        return ar.com.ospim.compras.WebKeysCompras.listarEstados();
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

    public RequerimientoCompraEstado getEstado(int idRequerimientoCompra) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GET_ESTADO);
            stmt.setInt(1, idRequerimientoCompra);
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

    public List<PrestadorCotizacion> buscarPrestadoresEnviados(int idRequerimientoCompra,
                                                                String texto,
                                                                int limite) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (limite <= 0 || limite > 50) {
            limite = 20;
        }

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PrestadorCotizacion> prestadores = new ArrayList<PrestadorCotizacion>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(SQL_BUSCAR_PRESTADORES_ENVIADOS);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setString(2, emptyToNull(texto));
            stmt.setInt(3, limite);

            rs = stmt.executeQuery();

            while (rs.next()) {
                prestadores.add(mapPrestadorCotizacion(rs));
            }

            return prestadores;
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PrestadorCotizacion> prestadores =
                new ArrayList<PrestadorCotizacion>();

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareStatement(
                    SQL_LISTAR_PRESTADORES_ENVIADOS
            );
            stmt.setString(
                    1,
                    WebKeysCompras.ENVIO_ENVIADO
            );
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setInt(
                    3,
                    WebKeysCompras.ESTADO_A_COTIZAR
            );
            stmt.setInt(
                    4,
                    WebKeysCompras.ESTADO_COTIZADO
            );
            stmt.setInt(
                    5,
                    WebKeysCompras
                            .MAX_PRESTADORES_ENVIADOS_REQUERIMIENTO
                            + 1
            );

            rs = stmt.executeQuery();

            while (rs.next()) {
                prestadores.add(
                        mapPrestadorCotizacion(rs)
                );
            }

            if (prestadores.size()
                    > WebKeysCompras
                    .MAX_PRESTADORES_ENVIADOS_REQUERIMIENTO) {

                throw new Exception(
                        "El requerimiento supera el maximo de prestadores "
                                + "enviados permitido para esta pantalla."
                );
            }

            return prestadores;
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

        r.setId(getInteger(rs, "id"));

        r.setAltaFecha(rs.getTimestamp("alta_fecha"));
        r.setAltaUsr(getString(rs, "alta_usr"));
        r.setModiFecha(rs.getTimestamp("modi_fecha"));
        r.setModiUsr(getString(rs, "modi_usr"));
        r.setBajaFecha(rs.getTimestamp("baja_fecha"));
        r.setBajaUsr(getString(rs, "baja_usr"));

        r.setAfiliadoCuilTitular(getString(rs, "afiliado_cuil_titular"));
        r.setAfiliadoInt(getInteger(rs, "afiliado_int"));

        r.setAfiliadoNombre(getString(rs, "afiliado_nombre"));
        r.setAfiliadoApellido(getString(rs, "afiliado_apellido"));
        r.setAfiliadoNombreApellido(getString(rs, "afiliado_nombre_apellido"));
        r.setAfiliadoDocumentoTipo(getString(rs, "afiliado_documento_tipo"));
        r.setAfiliadoDocumentoNro(getString(rs, "afiliado_documento_nro"));
        r.setAfiliadoDocumento(getString(rs, "afiliado_documento"));
        r.setAfiliadoDireccion(getString(rs, "afiliado_direccion"));
        r.setAfiliadoLocalidad(getString(rs, "afiliado_localidad"));
        r.setAfiliadoProvincia(getString(rs, "afiliado_provincia"));
        r.setAfiliadoCelular(getString(rs, "afiliado_celular"));
        r.setAfiliadoTelefono(getString(rs, "afiliado_telefono"));
        r.setAfiliadoEmail(getString(rs, "afiliado_email"));

        r.setIdSector(getInteger(rs, "id_sector"));
        r.setSectorDescripcion(getString(rs, "sector_descripcion"));
        r.setRequiereAfiliado(getBoolean(rs, "requiere_afiliado"));

        r.setCargoOspim(getInteger(rs, "cargo_ospim"));
        r.setCargoTercerizadora(getInteger(rs, "cargo_tercerizadora"));
        r.setIdTercerizadora(getString(rs, "id_tercerizadora"));

        r.setRecupero(getBoolean(rs, "recupero"));
        r.setObservaciones(getString(rs, "observaciones"));

        r.setIdEstado(getInteger(rs, "id_estado"));
        r.setEstadoDescripcion(getString(rs, "estado_descripcion"));

        return r;
    }

    private RequerimientoCompraDetalle mapDetalle(ResultSet rs) throws Exception {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();

        d.setId(getInteger(rs, "id"));
        d.setIdRequerimiento(getInteger(rs, "id_requerimiento"));
        d.setIdArticulo(getInteger(rs, "id_articulo"));
        d.setArticulo(getString(rs, "articulo"));
        d.setCantidad(getInteger(rs, "cantidad"));
        d.setPrecioUnitarioEstimado(getNullableBigDecimal(rs, "precio_unitario_estimado"));
        d.setPrecioTotalEstimado(getNullableBigDecimal(rs, "precio_total_estimado"));
        d.setIdPrestador(getInteger(rs, "id_prestador"));
        d.setPrestadorCuit(getString(rs, "prestador_cuit"));
        d.setPrestadorRazonSocial(getString(rs, "prestador_razon_social"));
        d.setObservaciones(getString(rs, "observaciones"));

        return d;
    }

    private RequerimientoCompraEstado mapEstado(ResultSet rs) throws Exception {
        RequerimientoCompraEstado estado = new RequerimientoCompraEstado();

        estado.setId(getInteger(rs, "id"));
        estado.setDescripcion(getString(rs, "descripcion"));

        return estado;
    }

    private RequerimientoCompraSector mapSector(ResultSet rs) throws Exception {
        RequerimientoCompraSector sector = new RequerimientoCompraSector();

        sector.setId(getInteger(rs, "id"));
        sector.setDescripcion(getString(rs, "descripcion"));
        sector.setRequiereAfiliado(getBoolean(rs, "requiere_afiliado"));

        return sector;
    }

    private PrestadorCotizacion mapPrestadorCotizacion(ResultSet rs) throws Exception {
        PrestadorCotizacion prestador = new PrestadorCotizacion();

        prestador.setIdPrestador(getInteger(rs, "id_prestador") != null
                ? getInteger(rs, "id_prestador").intValue()
                : 0);
        prestador.setDescripcion(getString(rs, "descripcion"));
        prestador.setCuit(getString(rs, "cuit"));
        prestador.setEmail(getString(rs, "email"));
        prestador.setIdTipoPrestador(getInteger(rs, "id_tipo_prestador") != null
                ? getInteger(rs, "id_tipo_prestador").intValue()
                : 0);
        prestador.setTipoPrestador(getString(rs, "tipo_prestador"));
        prestador.setEstadoEnvio(getString(rs, "estado_envio"));

        return prestador;
    }

    private void setNullableInteger(CallableStatement stmt, int index, Integer value) throws Exception {
        if (value == null || value.intValue() < 0) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }

    private void setNullableBoolean(CallableStatement stmt, int index, Boolean value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.BOOLEAN);
        } else {
            stmt.setBoolean(index, value.booleanValue());
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

    private Integer getInteger(ResultSet rs, String column) throws Exception {
        if (!hasColumn(rs, column)) {
            return null;
        }

        int value = rs.getInt(column);
        return rs.wasNull() ? null : Integer.valueOf(value);
    }

    private Boolean getBoolean(ResultSet rs, String column) throws Exception {
        if (!hasColumn(rs, column)) {
            return null;
        }

        boolean value = rs.getBoolean(column);
        return rs.wasNull() ? null : Boolean.valueOf(value);
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
