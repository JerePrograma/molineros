package ar.com.ospim.compras.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraDetalle;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class EditarRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(EditarRequerimientoCompraServiceImpl.class);

    private static final String SQL_GUARDAR_REQUERIMIENTO =
            "{ ? = call compras.guardar_requerimiento(?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_GUARDAR_REQUERIMIENTO_DETALLE =
            "{ ? = call compras.guardar_requerimiento_detalle(?,?,?,?,?,?,?,?) }";

    private static final String SQL_BORRAR_REQUERIMIENTO_DETALLE =
            "{call compras.borrar_requerimiento_detalle(?,?)}";

    private static final String SQL_BORRAR_REQUERIMIENTO =
            "{call compras.borrar_requerimiento(?,?)}";

    private static final String SQL_CAMBIAR_ESTADO =
            "{call compras.cambiar_estado_requerimiento(?,?,?)}";

    public int guardarRequerimientoCompra(RequerimientoCompra requerimiento, String usuario) throws Exception {
        validarRequerimientoParaGuardar(requerimiento);

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GUARDAR_REQUERIMIENTO);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, requerimiento.getId());
            stmt.setString(3, emptyToNull(requerimiento.getAfiliadoCuilTitular()));
            setNullableInteger(stmt, 4, requerimiento.getAfiliadoInt());
            setNullableInteger(stmt, 5, requerimiento.getIdSector());
            setNullableInteger(stmt, 6, requerimiento.getCargoOspim());
            setNullableInteger(stmt, 7, requerimiento.getCargoTercerizadora());
            stmt.setString(8, emptyToNull(requerimiento.getIdTercerizadora()));
            stmt.setBoolean(9, requerimiento.isRecupero());
            stmt.setString(10, emptyToNull(requerimiento.getObservaciones()));
            stmt.setString(11, emptyToNull(usuario));

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int guardarDetalle(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        validarDetalleParaGuardar(detalle);

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GUARDAR_REQUERIMIENTO_DETALLE);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, detalle.getId());
            setNullableInteger(stmt, 3, detalle.getIdRequerimiento());
            stmt.setString(4, emptyToNull(detalle.getArticulo()));
            setNullableInteger(stmt, 5, detalle.getCantidad());
            setNullableBigDecimal(stmt, 6, detalle.getPrecioUnitarioEstimado());
            setNullableBigDecimal(stmt, 7, detalle.getPrecioTotalEstimadoInformado());
            stmt.setString(8, emptyToNull(detalle.getObservaciones()));
            stmt.setString(9, emptyToNull(usuario));

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarDetalle(int idDetalle, String usuario) throws Exception {
        if (idDetalle <= 0) {
            throw new Exception("Debe informar el detalle del requerimiento.");
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BORRAR_REQUERIMIENTO_DETALLE);
            stmt.setInt(1, idDetalle);
            stmt.setString(2, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarRequerimientoCompra(int idRequerimientoCompra, String usuario) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BORRAR_REQUERIMIENTO);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setString(2, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void cambiarEstado(int idRequerimientoCompra, int idEstadoNuevo, String usuario) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (!WebKeysCompras.esEstadoValido(idEstadoNuevo)) {
            throw new Exception("Estado de requerimiento invalido.");
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_CAMBIAR_ESTADO);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idEstadoNuevo);
            stmt.setString(3, emptyToNull(usuario));

            stmt.execute();
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private void validarRequerimientoParaGuardar(RequerimientoCompra requerimiento) throws Exception {
        if (requerimiento == null) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (requerimiento.getIdSector() == null || requerimiento.getIdSector().intValue() <= 0) {
            throw new Exception("Debe informar el sector.");
        }

        validarPorcentaje(requerimiento.getCargoOspim(), "Cargo OSPIM");
        validarPorcentaje(requerimiento.getCargoTercerizadora(), "Cargo tercerizadora");

        int cargoOspim = requerimiento.getCargoOspim() != null ? requerimiento.getCargoOspim().intValue() : 0;
        int cargoTercerizadora = requerimiento.getCargoTercerizadora() != null
                ? requerimiento.getCargoTercerizadora().intValue()
                : 0;

        if (cargoOspim + cargoTercerizadora > 100) {
            throw new Exception("La suma de cargos no puede superar 100.");
        }

        if (cargoTercerizadora > 0
                && WebKeysCompras.isEmpty(requerimiento.getIdTercerizadora())) {
            throw new Exception("Debe informar la tercerizadora cuando su cargo es mayor a cero.");
        }

        if (requerimiento.isRequiereAfiliado()) {
            if (WebKeysCompras.isEmpty(requerimiento.getAfiliadoCuilTitular())) {
                throw new Exception("Debe informar el CUIL titular del afiliado.");
            }

            if (requerimiento.getAfiliadoInt() == null || requerimiento.getAfiliadoInt().intValue() < 0) {
                throw new Exception("Debe informar el integrante del afiliado.");
            }
        }
    }

    private void validarDetalleParaGuardar(RequerimientoCompraDetalle detalle) throws Exception {
        if (detalle == null) {
            throw new Exception("Debe informar el detalle del requerimiento.");
        }

        if (detalle.getIdRequerimientoCompra() <= 0) {
            throw new Exception("Debe guardar primero la cabecera del requerimiento.");
        }

        if (detalle.getCantidad() == null) {
            detalle.setCantidad(Integer.valueOf(1));
        }

        if (detalle.getCantidad().intValue() <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
        }

        if (WebKeysCompras.isEmpty(detalle.getArticulo())) {
            throw new Exception("Debe informar el articulo.");
        }

        if (detalle.getPrecioUnitarioEstimado() != null
                && detalle.getPrecioUnitarioEstimado().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El precio unitario estimado no puede ser negativo.");
        }

        if (detalle.getPrecioTotalEstimadoInformado() != null
                && detalle.getPrecioTotalEstimadoInformado().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El precio total estimado no puede ser negativo.");
        }
    }

    private void validarPorcentaje(Integer value, String label) throws Exception {
        int parsed = value != null ? value.intValue() : 0;

        if (parsed < 0 || parsed > 100) {
            throw new Exception(label + " debe estar entre 0 y 100.");
        }
    }

    private void setNullableInteger(CallableStatement stmt, int index, Integer value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }

    private void setNullableBigDecimal(CallableStatement stmt, int index, BigDecimal value) throws Exception {
        if (value == null) {
            stmt.setNull(index, Types.NUMERIC);
        } else {
            stmt.setBigDecimal(index, value);
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return value.trim();
    }
}
