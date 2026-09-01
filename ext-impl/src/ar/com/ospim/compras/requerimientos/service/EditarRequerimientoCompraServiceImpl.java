package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;
import ar.com.ospim.util.ConnectionHelper;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Persistencia de edición de Requerimientos de Compra.
 *
 * Sólo abre/cierra conexiones, invoca funciones PostgreSQL, parametriza
 * valores y recupera los resultados técnicos de esas funciones.
 */
public class EditarRequerimientoCompraServiceImpl {

    private static final String SQL_GUARDAR_REQUERIMIENTO =
            "{ ? = call compras.guardar_requerimiento(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_GUARDAR_REQUERIMIENTO_DETALLE =
            "{call compras.guardar_requerimiento_detalle_clasificado(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

    private static final String SQL_BORRAR_REQUERIMIENTO_DETALLE =
            "{call compras.borrar_requerimiento_detalle(?,?)}";

    private static final String SQL_CAMBIAR_ESTADO =
            "{call compras.cambiar_estado_requerimiento(?,?,?)}";

    private static final String SQL_REGISTRAR_PRESUPUESTO =
            "{ ? = call compras.registrar_requerimiento_presupuesto(?,?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_REGISTRAR_COTIZACION_EMPRESA =
            "{ ? = call compras.registrar_requerimiento_presupuesto(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_REGISTRAR_ORDEN_MEDICA =
            "{ ? = call compras.registrar_requerimiento_orden_medica(?,?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_BAJA_PRESUPUESTO =
            "{ ? = call compras.baja_requerimiento_presupuesto(?,?,?) }";

    private static final String SQL_REACTIVAR_PRESUPUESTO =
            "{ ? = call compras.reactivar_requerimiento_presupuesto(?,?) }";

    private static final String SQL_BAJA_COTIZACION_EMPRESA =
            "{ ? = call compras.baja_cotizacion_empresa_requerimiento(?,?,?) }";

    private static final String SQL_REACTIVAR_COTIZACION_EMPRESA =
            "{ ? = call compras.reactivar_cotizacion_empresa_requerimiento(?,?) }";

    private static final String SQL_CONFIRMAR_ENVIO_A_COTIZAR =
            "{ ? = call compras.confirmar_envio_a_cotizar(?,?) }";

    private static final String SQL_CONFIRMAR_ORDEN_COMPRA =
            "{ ? = call compras.confirmar_orden_compra_requerimiento(?,?) }";

    private static final String SQL_GUARDAR_COTIZACION =
            "{ ? = call compras.guardar_cotizacion_requerimiento_call(?,?,?,?,?,?,?) }";

    public Transaccion abrirTransaccion() throws Exception {
        Connection con = ConnectionHelper.getConnectionForTransaction();

        if (con == null) {
            throw new SQLException(
                    "No se obtuvo una conexión transaccional para Compras."
            );
        }

        return new Transaccion(this, con);
    }

    public int guardarRequerimientoCompra(
            RequerimientoCompra requerimiento,
            String usuario) throws Exception {

        Connection con = null;

        try {
            con = ConnectionHelper.getConnection();
            return guardarRequerimientoCompra(
                    con,
                    requerimiento,
                    usuario
            );
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public int guardarRequerimientoCompra(
            Connection con,
            RequerimientoCompra requerimiento,
            String usuario) throws Exception {

        CallableStatement stmt = null;

        try {
            stmt = con.prepareCall(SQL_GUARDAR_REQUERIMIENTO);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, requerimiento.getId());
            stmt.setString(3, requerimiento.getAfiliadoCuilTitular());
            setNullableInteger(stmt, 4, requerimiento.getAfiliadoInt());
            setNullableInteger(stmt, 5, requerimiento.getAfiliadoIdOspim());
            stmt.setString(6, requerimiento.getAfiliadoNombre());
            stmt.setString(7, requerimiento.getAfiliadoApellido());
            stmt.setString(8, requerimiento.getAfiliadoDocumentoTipo());
            stmt.setString(9, requerimiento.getAfiliadoDocumentoNro());
            stmt.setString(10, requerimiento.getAfiliadoDireccion());
            stmt.setString(11, requerimiento.getAfiliadoLocalidad());
            stmt.setString(12, requerimiento.getAfiliadoProvincia());
            stmt.setString(13, requerimiento.getAfiliadoCelular());
            stmt.setString(14, requerimiento.getAfiliadoTelefono());
            stmt.setString(15, requerimiento.getAfiliadoEmail());
            setNullableInteger(stmt, 16, requerimiento.getIdSector());
            setNullableInteger(stmt, 17, requerimiento.getCargoOspim());
            setNullableInteger(stmt, 18, requerimiento.getCargoTercerizadora());
            stmt.setString(19, requerimiento.getIdTercerizadora());
            stmt.setBoolean(20, requerimiento.isRecupero());
            stmt.setBoolean(21, requerimiento.isSurge());
            stmt.setBoolean(22, requerimiento.isLegales());
            stmt.setString(23, requerimiento.getObservaciones());
            stmt.setString(24, usuario);

            stmt.execute();
            return stmt.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public int registrarOrdenMedica(
            Connection con,
            int idRequerimiento,
            OrdenMedicaValidada ordenMedica,
            DocumentoComprasCreado documento,
            String usuario) throws Exception {

        CallableStatement stmt = null;

        try {
            stmt = con.prepareCall(SQL_REGISTRAR_ORDEN_MEDICA);
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimiento);
            stmt.setLong(3, documento.getGroupId());
            stmt.setLong(4, documento.getFolderId());
            stmt.setLong(5, documento.getFileEntryId());
            stmt.setString(6, documento.getUuid());
            stmt.setString(7, ordenMedica.getNombreOriginal());
            stmt.setString(8, documento.getNombrePersistido());
            stmt.setString(9, WebKeysCompras.TITULO_ORDEN_MEDICA);
            stmt.setDate(10, ordenMedica.getFechaDocumento());

            if (ordenMedica.getNumeroReceta() == null) {
                stmt.setNull(11, Types.VARCHAR);
            } else {
                stmt.setString(
                        11,
                        ordenMedica.getNumeroReceta()
                );
            }

            stmt.setString(12, usuario);
            stmt.execute();

            return stmt.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public int guardarDetalle(
            RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GUARDAR_REQUERIMIENTO_DETALLE);

            setNullableInteger(stmt, 1, detalle.getId());

            setNullableInteger(
                    stmt,
                    2,
                    detalle.getIdRequerimiento()
            );
            stmt.setString(3, detalle.getTipoItem());
            setNullableInteger(stmt, 4, detalle.getIdPrestacion());
            setNullableInteger(stmt, 5, detalle.getIdTipoNomenclador());
            stmt.setString(6, detalle.getCodigoNomenclador());
            stmt.setString(7, detalle.getDescripcionNomenclador());
            setNullableInteger(stmt, 8, detalle.getIdMedicamento());
            setNullableInteger(stmt, 9, detalle.getTroquel());
            stmt.setString(10, detalle.getNombreMedicamento());
            setNullableInteger(stmt, 11, detalle.getCantidad());
            stmt.setString(12, detalle.getObservaciones());
            setNullableInteger(
                    stmt,
                    13,
                    detalle.getIdTipoPrestacion()
            );
            stmt.setString(14, usuario);

            rs = stmt.executeQuery();

            return rs.next()
                    ? rs.getInt(1)
                    : 0;
        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarDetalle(
            int idDetalle,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BORRAR_REQUERIMIENTO_DETALLE);
            stmt.setInt(1, idDetalle);
            stmt.setString(2, usuario);
            stmt.execute();
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarRequerimientoCompra(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        cambiarEstado(
                idRequerimientoCompra,
                WebKeysCompras.ESTADO_ANULADO,
                usuario
        );
    }

    public void cambiarEstado(
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        Connection con = null;

        try {
            con = ConnectionHelper.getConnection();
            cambiarEstado(
                    con,
                    idRequerimientoCompra,
                    idEstadoNuevo,
                    usuario
            );
        } finally {
            ConnectionHelper.cerrar(con);
        }
    }

    public void cambiarEstado(
            Connection con,
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        CallableStatement stmt = null;

        try {
            stmt = con.prepareCall(SQL_CAMBIAR_ESTADO);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idEstadoNuevo);
            stmt.setString(3, usuario);
            stmt.execute();
        } finally {
            ConnectionHelper.cerrar(stmt);
        }
    }

    public int guardarCotizacion(
            int idRequerimientoCompra,
            Integer[] idsDetalle,
            BigDecimal[] preciosUnitarios,
            Integer idPrestadorAdjudicado,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                construirArrayEnterosPostgreSql(idsDetalle),
                construirArrayNumericosPostgreSql(preciosUnitarios),
                "{}",
                idPrestadorAdjudicado,
                null,
                usuario
        );
    }

    public int guardarCotizacion(
            int idRequerimientoCompra,
            Integer[] idsDetalle,
            BigDecimal[] preciosUnitarios,
            Integer[] idsDetalleEliminados,
            Integer idPrestadorAdjudicado,
            boolean surge,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                construirArrayEnterosPostgreSql(idsDetalle),
                construirArrayNumericosPostgreSql(preciosUnitarios),
                construirArrayEnterosPostgreSql(idsDetalleEliminados),
                idPrestadorAdjudicado,
                Boolean.valueOf(surge),
                usuario
        );
    }

    public int guardarCotizacion(
            int idRequerimientoCompra,
            String idsDetalle,
            String preciosUnitarios,
            String idsDetalleEliminados,
            Integer idPrestadorAdjudicado,
            Boolean surge,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GUARDAR_COTIZACION);
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setString(3, idsDetalle);
            stmt.setString(4, preciosUnitarios);
            stmt.setString(5, idsDetalleEliminados);
            setNullableInteger(stmt, 6, idPrestadorAdjudicado);
            if (surge == null) {
                stmt.setNull(7, Types.BOOLEAN);
            } else {
                stmt.setBoolean(7, surge.booleanValue());
            }
            stmt.setString(8, usuario);
            stmt.execute();

            return stmt.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int registrarPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) throws Exception {

        if (presupuesto.isCotizacionEmpresa()) {
            return registrarCotizacionEmpresa(
                    presupuesto,
                    usuario
            );
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_REGISTRAR_PRESUPUESTO);
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, presupuesto.getIdRequerimiento().intValue());
            stmt.setInt(3, presupuesto.getIdPrestador().intValue());
            stmt.setLong(4, presupuesto.getDlGroupId().longValue());
            stmt.setLong(5, presupuesto.getDlFolderId().longValue());
            stmt.setLong(6, presupuesto.getDlFileEntryId().longValue());
            stmt.setString(7, presupuesto.getDlFileUuid());
            stmt.setString(8, presupuesto.getNombreOriginal());
            stmt.setString(9, presupuesto.getNombrePersistido());
            stmt.setString(10, presupuesto.getTitulo());
            stmt.setString(
                    11,
                    presupuesto.getDescripcionPrestador()
            );
            stmt.setString(12, usuario);
            stmt.execute();

            return stmt.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private int registrarCotizacionEmpresa(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_REGISTRAR_COTIZACION_EMPRESA);
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, presupuesto.getIdRequerimiento().intValue());
            stmt.setShort(3, presupuesto.getTipoDocumento().shortValue());
            stmt.setNull(4, Types.INTEGER);
            stmt.setString(5, presupuesto.getEmpresaCuit());
            stmt.setString(6, presupuesto.getEmpresaSucursal());
            stmt.setString(7, presupuesto.getDescripcionEmpresa());
            stmt.setLong(8, presupuesto.getDlGroupId().longValue());
            stmt.setLong(9, presupuesto.getDlFolderId().longValue());
            stmt.setLong(10, presupuesto.getDlFileEntryId().longValue());
            stmt.setString(11, presupuesto.getDlFileUuid());
            stmt.setString(12, presupuesto.getNombreOriginal());
            stmt.setString(13, presupuesto.getNombrePersistido());
            stmt.setString(14, presupuesto.getTitulo());
            stmt.setNull(15, Types.VARCHAR);
            stmt.setString(16, usuario);
            stmt.execute();

            return stmt.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean darDeBajaPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BAJA_PRESUPUESTO);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoPresupuesto);
            stmt.setInt(3, idRequerimientoCompra);
            stmt.setString(4, usuario);
            stmt.execute();

            boolean resultado = stmt.getBoolean(1);
            return !stmt.wasNull() && resultado;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean reactivarPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_REACTIVAR_PRESUPUESTO);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoPresupuesto);
            stmt.setInt(3, idRequerimientoCompra);
            stmt.execute();

            boolean resultado = stmt.getBoolean(1);
            return !stmt.wasNull() && resultado;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean darDeBajaCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BAJA_COTIZACION_EMPRESA);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoPresupuesto);
            stmt.setInt(3, idRequerimientoCompra);
            stmt.setString(4, usuario);
            stmt.execute();

            boolean resultado = stmt.getBoolean(1);
            return !stmt.wasNull() && resultado;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public boolean reactivarCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_REACTIVAR_COTIZACION_EMPRESA);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setInt(2, idRequerimientoPresupuesto);
            stmt.setInt(3, idRequerimientoCompra);
            stmt.execute();

            boolean resultado = stmt.getBoolean(1);
            return !stmt.wasNull() && resultado;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int confirmarEnvioACotizar(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_CONFIRMAR_ENVIO_A_COTIZAR);
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setString(3, usuario);
            stmt.execute();

            return stmt.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public int confirmarOrdenCompra(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_CONFIRMAR_ORDEN_COMPRA);
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, idRequerimientoCompra);
            stmt.setString(3, usuario);
            stmt.execute();

            return stmt.getInt(1);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    private String construirArrayEnterosPostgreSql(
            Integer[] valores) {

        if (valores == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        value.append('{');

        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                value.append(',');
            }

            Integer actual = valores[i];
            value.append(
                    actual != null
                            ? actual.toString()
                            : "NULL"
            );
        }

        value.append('}');
        return value.toString();
    }

    private String construirArrayNumericosPostgreSql(
            BigDecimal[] valores) {

        if (valores == null) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        value.append('{');

        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                value.append(',');
            }

            BigDecimal actual = valores[i];
            value.append(
                    actual != null
                            ? actual.toPlainString()
                            : "NULL"
            );
        }

        value.append('}');
        return value.toString();
    }

    public static final class Transaccion {

        private final EditarRequerimientoCompraServiceImpl service;
        private Connection con;

        private Transaccion(
                EditarRequerimientoCompraServiceImpl service,
                Connection con) {

            this.service = service;
            this.con = con;
        }

        public int guardarRequerimientoCompra(
                RequerimientoCompra requerimiento,
                String usuario) throws Exception {

            return service.guardarRequerimientoCompra(
                    con,
                    requerimiento,
                    usuario
            );
        }

        public int registrarOrdenMedica(
                int idRequerimiento,
                OrdenMedicaValidada ordenMedica,
                DocumentoComprasCreado documento,
                String usuario) throws Exception {

            return service.registrarOrdenMedica(
                    con,
                    idRequerimiento,
                    ordenMedica,
                    documento,
                    usuario
            );
        }

        public void commit() throws Exception {
            con.commit();
        }

        public void rollback() throws Exception {
            con.rollback();
        }

        public void cerrar() {
            Connection actual = con;
            con = null;
            ConnectionHelper.cerrar(actual);
        }
    }

    private void setNullableInteger(
            CallableStatement stmt,
            int index,
            Integer value) throws SQLException {

        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, value.intValue());
        }
    }


    private void cerrar(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (Exception ignored) {
        }
    }
}
