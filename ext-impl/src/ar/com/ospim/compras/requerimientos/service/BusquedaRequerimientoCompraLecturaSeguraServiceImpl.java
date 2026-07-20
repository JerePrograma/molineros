package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Carga la cabecera y los detalles de un requerimiento en dos etapas.
 *
 * La implementacion historica solicitaba la conexion de detalles mientras
 * todavia conservaba abierta la conexion de cabecera. Con un pool pequeno o
 * saturado, la misma peticion podia quedar esperando indefinidamente una
 * segunda conexion. Esta clase libera completamente la cabecera antes de
 * solicitar los detalles.
 */
public final class BusquedaRequerimientoCompraLecturaSeguraServiceImpl {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    BusquedaRequerimientoCompraLecturaSeguraServiceImpl.class
            );

    private static final String SQL_GET_REQUERIMIENTO =
            "{call compras.get_requerimiento(?)}";

    private static final int QUERY_TIMEOUT_SEGUNDOS = 30;

    private final BusquedaRequerimientoCompraServiceImpl detalleService =
            new BusquedaRequerimientoCompraServiceImpl();

    public RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        RequerimientoCompra requerimiento =
                getCabeceraRequerimiento(idRequerimientoCompra);

        if (requerimiento == null) {
            return null;
        }

        /*
         * Esta llamada ocurre despues del finally de cabecera. Por lo tanto,
         * nunca compite con una conexion retenida por esta misma peticion.
         */
        requerimiento.setDetalles(
                detalleService.getDetalles(idRequerimientoCompra)
        );

        return requerimiento;
    }

    private RequerimientoCompra getCabeceraRequerimiento(
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();

            if (con == null) {
                throw new Exception(
                        "No se pudo obtener una conexion para leer el "
                                + "requerimiento de compra."
                );
            }

            stmt = con.prepareCall(SQL_GET_REQUERIMIENTO);
            stmt.setQueryTimeout(QUERY_TIMEOUT_SEGUNDOS);
            stmt.setInt(1, idRequerimientoCompra);
            rs = stmt.executeQuery();

            return rs.next() ? mapRequerimiento(rs) : null;

        } catch (Exception e) {
            _log.error(
                    "No se pudo recuperar la cabecera del requerimiento. "
                            + "idRequerimiento=" + idRequerimientoCompra,
                    e
            );
            throw e;

        } finally {
            closeQuietly(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private RequerimientoCompra mapRequerimiento(ResultSet rs)
            throws Exception {

        RequerimientoCompra r = new RequerimientoCompra();

        r.setId(getInteger(rs, "id"));
        r.setAltaFecha(rs.getTimestamp("alta_fecha"));
        r.setAltaUsr(getString(rs, "alta_usr"));
        r.setModiFecha(rs.getTimestamp("modi_fecha"));
        r.setModiUsr(getString(rs, "modi_usr"));
        r.setBajaFecha(rs.getTimestamp("baja_fecha"));
        r.setBajaUsr(getString(rs, "baja_usr"));

        r.setAfiliadoCuilTitular(
                getString(rs, "afiliado_cuil_titular")
        );
        r.setAfiliadoInt(getInteger(rs, "afiliado_int"));
        r.setAfiliadoIdOspim(getInteger(rs, "afiliado_id_ospim"));
        r.setAfiliadoNombre(getString(rs, "afiliado_nombre"));
        r.setAfiliadoApellido(getString(rs, "afiliado_apellido"));
        r.setAfiliadoNombreApellido(
                getString(rs, "afiliado_nombre_apellido")
        );
        r.setAfiliadoDocumentoTipo(
                getString(rs, "afiliado_documento_tipo")
        );
        r.setAfiliadoDocumentoNro(
                getString(rs, "afiliado_documento_nro")
        );
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
        r.setSurge(getBoolean(rs, "surge"));
        r.setObservaciones(getString(rs, "observaciones"));
        r.setIdEstado(getInteger(rs, "id_estado"));
        r.setEstadoDescripcion(getString(rs, "estado_descripcion"));

        return r;
    }

    private String getString(ResultSet rs, String column) throws Exception {
        return hasColumn(rs, column) ? rs.getString(column) : null;
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

    private boolean hasColumn(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (Exception e) {
            return false;
        }
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
