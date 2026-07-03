package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.text.Normalizer;
import java.util.Locale;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class EditarRequerimientoCompraServiceImpl {

    private static Log _log = LogFactoryUtil.getLog(EditarRequerimientoCompraServiceImpl.class);


    private static final Pattern DIACRITICOS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final String SQL_GUARDAR_REQUERIMIENTO =
            "{ ? = call compras.guardar_requerimiento(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_GUARDAR_REQUERIMIENTO_DETALLE =
            "{call compras.guardar_requerimiento_detalle(?,?,?,?,?,?)}";

    private static final String SQL_BORRAR_REQUERIMIENTO_DETALLE =
            "{ call compras.borrar_requerimiento_detalle(?,?) }";

    private static final String SQL_BORRAR_REQUERIMIENTO =
            "{ call compras.borrar_requerimiento(?,?) }";

    private static final String SQL_CAMBIAR_ESTADO =
            "{ call compras.cambiar_estado_requerimiento(?,?,?) }";

    private static final String SQL_GUARDAR_ARTICULO =
            "{ ? = call compras.guardar_articulo(?,?,?) }";

    private static final String SQL_BORRAR_ARTICULO =
            "{ call compras.borrar_articulo(?) }";

    private static final String SQL_REGISTRAR_PRESUPUESTO =
            "{ ? = call compras.registrar_requerimiento_presupuesto("
                    + "?,?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_BAJA_PRESUPUESTO =
            "{ ? = call compras.baja_requerimiento_presupuesto("
                    + "?,?,?) }";

    private static final String SQL_REACTIVAR_PRESUPUESTO =
            "{ ? = call compras.reactivar_requerimiento_presupuesto("
                    + "?,?) }";

    private static final String SQL_CONFIRMAR_ENVIO_A_COTIZAR =
            "{ ? = call compras.confirmar_envio_a_cotizar(?,?) }";

    private static final String SQL_GUARDAR_COTIZACION_REQUERIMIENTO =
            "SELECT compras.guardar_cotizacion_requerimiento("
                    + "?, "
                    + "CAST(? AS INTEGER[]), "
                    + "CAST(? AS NUMERIC[]), "
                    + "?, "
                    + "?"
                    + ")";

    private static final String SQL_LISTAR_ARTICULOS_CURSOR =
            "{ ? = call compras.listar_articulos_cursor(?,?) }";

    private static final String SQL_GET_ARTICULO_CURSOR =
            "{ ? = call compras.get_articulo_cursor(?) }";

    public int guardarRequerimientoCompra(
            RequerimientoCompra requerimiento,
            String usuario) throws Exception {

        validarRequerimientoParaGuardar(requerimiento);

        if (requerimiento.getIdRequerimientoCompra() > 0) {
            RequerimientoCompra actual =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    requerimiento
                                            .getIdRequerimientoCompra()
                            );

            if (actual == null) {
                throw new Exception(
                        "No se encontró el requerimiento de compra informado."
                );
            }

            if (!actual.puedeEditarEstructura()) {
                throw new Exception(
                        "Solo se puede editar la estructura de requerimientos "
                                + "en estado PENDIENTE."
                );
            }
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = obtenerConexionGuardarRequerimiento();

            stmt = con.prepareCall(
                    SQL_GUARDAR_REQUERIMIENTO
            );

            stmt.registerOutParameter(
                    1,
                    Types.INTEGER
            );

            setNullableInteger(
                    stmt,
                    2,
                    requerimiento.getId()
            );

            stmt.setString(
                    3,
                    emptyToNull(
                            requerimiento.getAfiliadoCuilTitular()
                    )
            );

            setNullableInteger(
                    stmt,
                    4,
                    requerimiento.getAfiliadoInt()
            );

            setNullableInteger(
                    stmt,
                    5,
                    requerimiento.getAfiliadoIdOspim()
            );

            stmt.setString(
                    6,
                    emptyToNull(
                            requerimiento.getAfiliadoNombre()
                    )
            );

            stmt.setString(
                    7,
                    emptyToNull(
                            requerimiento.getAfiliadoApellido()
                    )
            );

            stmt.setString(
                    8,
                    emptyToNull(
                            requerimiento.getAfiliadoDocumentoTipo()
                    )
            );

            stmt.setString(
                    9,
                    emptyToNull(
                            requerimiento.getAfiliadoDocumentoNro()
                    )
            );

            stmt.setString(
                    10,
                    emptyToNull(
                            requerimiento.getAfiliadoDireccion()
                    )
            );

            stmt.setString(
                    11,
                    emptyToNull(
                            requerimiento.getAfiliadoLocalidad()
                    )
            );

            stmt.setString(
                    12,
                    emptyToNull(
                            requerimiento.getAfiliadoProvincia()
                    )
            );

            stmt.setString(
                    13,
                    emptyToNull(
                            requerimiento.getAfiliadoCelular()
                    )
            );

            stmt.setString(
                    14,
                    emptyToNull(
                            requerimiento.getAfiliadoTelefono()
                    )
            );

            stmt.setString(
                    15,
                    emptyToNull(
                            requerimiento.getAfiliadoEmail()
                    )
            );

            setNullableInteger(
                    stmt,
                    16,
                    requerimiento.getIdSector()
            );

            setNullableInteger(
                    stmt,
                    17,
                    requerimiento.getCargoOspim()
            );

            setNullableInteger(
                    stmt,
                    18,
                    requerimiento.getCargoTercerizadora()
            );

            stmt.setString(
                    19,
                    emptyToNull(
                            requerimiento.getIdTercerizadora()
                    )
            );

            stmt.setBoolean(
                    20,
                    requerimiento.isRecupero()
            );

            stmt.setBoolean(
                    21,
                    requerimiento.isSurge()
            );

            stmt.setString(
                    22,
                    emptyToNull(
                            requerimiento.getObservaciones()
                    )
            );

            stmt.setString(
                    23,
                    emptyToNull(usuario)
            );

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    protected Connection obtenerConexionGuardarRequerimiento()
            throws Exception {

        return ConnectionHelper.getConnection();
    }

    public int guardarDetalle(
            RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {

        validarDetalleParaGuardar(detalle);

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        Integer idRequerimiento =
                getIdRequerimientoDetalle(detalle);

        try {
            con = ConnectionHelper.getConnection();

            if (con == null) {
                throw new SQLException(
                        "No se pudo obtener una conexión para guardar "
                                + "el detalle del requerimiento."
                );
            }

            stmt = con.prepareCall(
                    SQL_GUARDAR_REQUERIMIENTO_DETALLE
            );

            /*
             * La función recibe seis parámetros.
             *
             * Ya no existe registerOutParameter():
             * el INTEGER retornado se obtiene desde el ResultSet.
             */
            setNullableInteger(
                    stmt,
                    1,
                    detalle.getId()
            );

            setNullableInteger(
                    stmt,
                    2,
                    idRequerimiento
            );

            setNullableInteger(
                    stmt,
                    3,
                    detalle.getIdArticulo()
            );

            setNullableInteger(
                    stmt,
                    4,
                    detalle.getCantidad()
            );

            stmt.setString(
                    5,
                    emptyToNull(
                            detalle.getObservaciones()
                    )
            );

            stmt.setString(
                    6,
                    emptyToNull(usuario)
            );

            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException(
                        "La función compras.guardar_requerimiento_detalle "
                                + "no devolvió el identificador del detalle."
                );
            }

            int idDetalleGuardado =
                    rs.getInt(1);

            if (rs.wasNull()) {
                throw new SQLException(
                        "La función compras.guardar_requerimiento_detalle "
                                + "devolvió NULL."
                );
            }

            if (idDetalleGuardado <= 0) {
                throw new SQLException(
                        "La función compras.guardar_requerimiento_detalle "
                                + "devolvió un identificador inválido: "
                                + idDetalleGuardado
                );
            }

            /*
             * Una función escalar debe devolver exactamente una fila.
             */
            if (rs.next()) {
                throw new SQLException(
                        "La función compras.guardar_requerimiento_detalle "
                                + "devolvió más de un resultado."
                );
            }

            return idDetalleGuardado;

        } catch (Exception e) {
            SQLException sqlException =
                    buscarSQLException(e);

            String sqlState =
                    sqlException != null
                            ? sqlException.getSQLState()
                            : null;

            int errorCode =
                    sqlException != null
                            ? sqlException.getErrorCode()
                            : 0;

            _log.error(
                    "No se pudo guardar el detalle del requerimiento. "
                            + "idDetalle=" + detalle.getId()
                            + ", idRequerimiento=" + idRequerimiento
                            + ", idArticulo=" + detalle.getIdArticulo()
                            + ", cantidad=" + detalle.getCantidad()
                            + ", usuario=" + usuario
                            + ", SQLState=" + sqlState
                            + ", errorCode=" + errorCode,
                    e
            );

            if (sqlException != null
                    && sqlException.getNextException() != null
                    && sqlException.getNextException() != sqlException) {

                _log.error(
                        "Excepción SQL encadenada al guardar el detalle.",
                        sqlException.getNextException()
                );
            }

            throw e;

        } finally {
            cerrar(rs);

            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
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
            throw new Exception("Estado de requerimiento inválido.");
        }

        RequerimientoCompra requerimientoActual =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(idRequerimientoCompra);

        if (requerimientoActual == null) {
            throw new Exception(
                    "No se encontró el requerimiento de compra informado."
            );
        }

        if (!WebKeysCompras.validarTransicionEstado(
                requerimientoActual.getEstado(),
                idEstadoNuevo
        )) {
            throw new Exception(
                    "La transición de estado solicitada no es válida."
            );
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

    public NotificacionCotizacionResultado enviarACotizar(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        RequerimientoCompra requerimiento =
                validarRequerimientoParaEnviarACotizar(idRequerimientoCompra);

        NotificacionCotizacionResultado resultado =
                NotificarCotizacionPrestadorServiceUtil.notificarPrestadores(
                        requerimiento.getIdRequerimientoCompra(),
                        usuario,
                        companyId
                );

        if (resultado == null) {

            throw new Exception(
                    "El proceso de notificación no devolvió un resultado verificable."
            );
        }

        int estadoFinal =
                confirmarEnvioACotizar(
                        idRequerimientoCompra,
                        usuario
                );

        if (estadoFinal != WebKeysCompras.ESTADO_PENDIENTE
                && estadoFinal
                != WebKeysCompras.ESTADO_A_COTIZAR) {

            throw new Exception(
                    "La base devolvió un estado inesperado "
                            + "al confirmar el envío a cotizar: "
                            + estadoFinal
            );
        }

        return resultado;
    }

    public NotificacionCotizacionResultado reintentarNotificacionesCotizacion(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            throw new Exception("No se encontró el requerimiento de compra informado.");
        }

        if (!requerimiento.puedeReintentarNotificaciones()) {
            throw new Exception(
                    "Solo se pueden reintentar notificaciones de requerimientos en estado A COTIZAR."
            );
        }

        if (!BusquedaRequerimientoCompraServiceUtil
                .hayPrestadoresPendientesNotificacion(
                        idRequerimientoCompra
                )) {

            /*
             * Operación idempotente: una petición atrasada o duplicada no
             * vuelve a enviar correos cuando ya no quedan candidatos.
             */
            return new NotificacionCotizacionResultado();
        }

        return NotificarCotizacionPrestadorServiceUtil.notificarPrestadores(
                idRequerimientoCompra,
                usuario,
                companyId
        );
    }

    public GuardadoCotizacionResultado guardarAvanceCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                detalles,
                usuario
        );
    }

    public GuardadoCotizacionResultado cerrarCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                detalles,
                usuario
        );
    }

    public List<CompraArticulo> listarArticulos(Integer idSector, String texto) throws Exception {
        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        boolean autoCommitOriginal = true;

        List<CompraArticulo> articulos =
                new ArrayList<CompraArticulo>();

        try {
            con =
                    ConnectionHelper.getConnection();

            autoCommitOriginal =
                    con.getAutoCommit();

            con.setAutoCommit(false);

            stmt =
                    con.prepareCall(
                            SQL_LISTAR_ARTICULOS_CURSOR
                    );

            stmt.registerOutParameter(
                    1,
                    Types.OTHER
            );

            setNullableInteger(
                    stmt,
                    2,
                    idSector
            );

            stmt.setString(
                    3,
                    emptyToNull(texto)
            );

            stmt.execute();

            rs =
                    (ResultSet) stmt.getObject(1);

            while (rs.next()) {
                articulos.add(
                        mapearArticulo(rs)
                );
            }

            con.commit();

            return articulos;

        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception rollbackError) {
                    _log.error(
                            rollbackError
                    );
                }
            }

            throw e;

        } finally {
            cerrar(rs);
            cerrar(stmt);

            if (con != null) {
                try {
                    con.setAutoCommit(
                            autoCommitOriginal
                    );
                } catch (Exception ignored) {
                }

                try {
                    con.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public List<CompraArticulo> listarArticulosPorSector(int idSector) throws Exception {
        if (idSector <= 0) {
            throw new Exception("Debe informar el sector.");
        }

        return listarArticulos(Integer.valueOf(idSector), null);
    }

    public CompraArticulo getArticulo(int idArticulo) throws Exception {
        if (idArticulo <= 0) {
            throw new Exception("Debe informar el artículo.");
        }

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        boolean autoCommitOriginal = true;

        try {
            con =
                    ConnectionHelper.getConnection();

            autoCommitOriginal =
                    con.getAutoCommit();

            con.setAutoCommit(false);

            stmt =
                    con.prepareCall(
                            SQL_GET_ARTICULO_CURSOR
                    );

            stmt.registerOutParameter(
                    1,
                    Types.OTHER
            );

            stmt.setInt(
                    2,
                    idArticulo
            );

            stmt.execute();

            rs =
                    (ResultSet) stmt.getObject(1);

            CompraArticulo articulo = null;

            if (rs.next()) {
                articulo =
                        mapearArticulo(rs);
            }

            con.commit();

            return articulo;

        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception rollbackError) {
                    _log.error(
                            rollbackError
                    );
                }
            }

            throw e;

        } finally {
            cerrar(rs);
            cerrar(stmt);

            if (con != null) {
                try {
                    con.setAutoCommit(
                            autoCommitOriginal
                    );
                } catch (Exception ignored) {
                }

                try {
                    con.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public int guardarArticulo(Integer idArticulo,
                               Integer idSector,
                               String descripcion) throws Exception {

        String descripcionNormalizada = normalizarDescripcionArticulo(descripcion);

        CompraArticulo articulo = new CompraArticulo();
        articulo.setId(idArticulo);
        articulo.setIdSector(idSector);
        articulo.setDescripcion(descripcionNormalizada);

        validarArticuloParaGuardar(articulo);
        validarArticuloDuplicado(articulo);

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_GUARDAR_ARTICULO);
            stmt.registerOutParameter(1, Types.INTEGER);

            setNullableInteger(stmt, 2, idArticulo);
            setNullableInteger(stmt, 3, idSector);
            stmt.setString(4, emptyToNull(descripcionNormalizada));

            stmt.execute();

            return stmt.getInt(1);
        } catch (Exception e) {
            _log.error(e);
            throw e;
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarArticulo(int idArticulo) throws Exception {
        if (idArticulo <= 0) {
            throw new Exception("Debe informar el artículo.");
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_BORRAR_ARTICULO);
            stmt.setInt(1, idArticulo);

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

        int cargoOspim = requerimiento.getCargoOspim() != null
                ? requerimiento.getCargoOspim().intValue()
                : 0;

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

        Integer idRequerimiento = getIdRequerimientoDetalle(detalle);

        if (idRequerimiento == null || idRequerimiento.intValue() <= 0) {
            throw new Exception("Debe guardar primero la cabecera del requerimiento.");
        }

        if (detalle.getIdArticulo() == null || detalle.getIdArticulo().intValue() <= 0) {
            throw new Exception("Debe informar el artículo.");
        }

        if (detalle.getCantidad() == null) {
            detalle.setCantidad(Integer.valueOf(1));
        }

        if (detalle.getCantidad().intValue() <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
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

    private RequerimientoCompra validarRequerimientoParaEnviarACotizar(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            throw new Exception("No se encontró el requerimiento de compra informado.");
        }

        if (!requerimiento.puedeEnviarACotizar()) {
            throw new Exception("Solo se pueden enviar a cotizar requerimientos en estado PENDIENTE.");
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {
            throw new Exception("El requerimiento debe tener sector.");
        }

        if (!requerimiento.tieneDetalles()) {
            throw new Exception("El requerimiento debe tener al menos un detalle válido.");
        }

        if (requerimiento.isRequiereAfiliado()
                && !requerimiento.tieneAfiliadoInformado()) {
            throw new Exception("El sector requiere afiliado y el requerimiento no lo tiene informado.");
        }

        return requerimiento;
    }

    private GuardadoCotizacionResultado guardarCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        validarDetallesCotizacionRecibidos(
                detalles
        );

        Integer idPrestadorAdjudicado =
                obtenerPrestadorAdjudicadoUnico(
                        detalles
                );

        Integer[] idsDetalle =
                new Integer[detalles.size()];

        BigDecimal[] preciosUnitarios =
                new BigDecimal[detalles.size()];

        for (int i = 0;
             i < detalles.size();
             i++) {

            RequerimientoCompraDetalle detalle =
                    detalles.get(i);

            idsDetalle[i] =
                    Integer.valueOf(
                            detalle.getIdInt()
                    );

            preciosUnitarios[i] =
                    detalle.getPrecioUnitarioEstimado() != null
                            ? WebKeysCompras.normalizarImporte(
                            detalle.getPrecioUnitarioEstimado()
                    )
                            : null;
        }

        /*
         * Si la cotización está completa para pasar a COTIZADO,
         * debe existir al menos un archivo activo de presupuesto
         * cargado para una tercerizadora/prestador.
         *
         * Se valida antes de llamar a la función SQL para evitar
         * que la base cambie el estado y recién después falle Java.
         */
        if (debeValidarPresupuestoParaCotizado(
                idPrestadorAdjudicado,
                preciosUnitarios
        )) {
            validarTienePresupuestoActivoDeTercerizadora(
                    idRequerimientoCompra
            );
        }

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con =
                    ConnectionHelper.getConnection();

            stmt =
                    con.prepareStatement(
                            SQL_GUARDAR_COTIZACION_REQUERIMIENTO
                    );

            stmt.setInt(
                    1,
                    idRequerimientoCompra
            );

            /*
             * El pool c3p0 legacy no implementa Connection.createArrayOf().
             * Los arrays se envían como literales parametrizados y PostgreSQL
             * realiza el cast explícito definido en la sentencia.
             */
            stmt.setString(
                    2,
                    construirArrayEnterosPostgreSql(
                            idsDetalle
                    )
            );

            stmt.setString(
                    3,
                    construirArrayNumericosPostgreSql(
                            preciosUnitarios
                    )
            );

            if (idPrestadorAdjudicado == null) {
                stmt.setNull(
                        4,
                        Types.INTEGER
                );
            } else {
                stmt.setInt(
                        4,
                        idPrestadorAdjudicado.intValue()
                );
            }

            stmt.setString(
                    5,
                    emptyToNull(usuario)
            );

            rs =
                    stmt.executeQuery();

            if (!rs.next()) {
                throw new Exception(
                        "La base no devolvió el estado final "
                                + "de la cotización."
                );
            }

            int estadoFinal =
                    rs.getInt(1);

            if (rs.wasNull()
                    || (estadoFinal
                    != WebKeysCompras.ESTADO_A_COTIZAR
                    && estadoFinal
                    != WebKeysCompras.ESTADO_COTIZADO)) {

                throw new Exception(
                        "La base devolvió un estado final "
                                + "de cotización inválido."
                );
            }

            return new GuardadoCotizacionResultado(
                    estadoFinal
                            == WebKeysCompras.ESTADO_COTIZADO,
                    estadoFinal
            );

        } finally {
            cerrar(
                    rs
            );

            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    protected String construirArrayEnterosPostgreSql(
            Integer[] valores) throws Exception {

        if (valores == null
                || valores.length == 0) {

            throw new Exception(
                    "Debe informar los identificadores "
                            + "de detalle de la cotización."
            );
        }

        StringBuilder array =
                new StringBuilder();

        array.append('{');

        for (int i = 0; i < valores.length; i++) {
            Integer valor =
                    valores[i];

            if (valor == null
                    || valor.intValue() <= 0) {

                throw new Exception(
                        "La lista contiene un identificador "
                                + "de detalle inválido."
                );
            }

            if (i > 0) {
                array.append(',');
            }

            array.append(
                    valor.intValue()
            );
        }

        array.append('}');

        return array.toString();
    }

    protected String construirArrayNumericosPostgreSql(
            BigDecimal[] valores) throws Exception {

        if (valores == null
                || valores.length == 0) {

            throw new Exception(
                    "Debe informar los precios "
                            + "de la cotización."
            );
        }

        StringBuilder array =
                new StringBuilder();

        array.append('{');

        for (int i = 0; i < valores.length; i++) {
            BigDecimal valor =
                    valores[i];

            if (i > 0) {
                array.append(',');
            }

            /*
             * NULL sin comillas representa un elemento SQL nulo.
             * Esto permite guardar avances de cotización incompletos.
             */
            if (valor == null) {
                array.append("NULL");
                continue;
            }

            BigDecimal normalizado =
                    WebKeysCompras.normalizarImporte(
                            valor
                    );

            if (normalizado.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                throw new Exception(
                        "El precio unitario no puede ser negativo."
                );
            }

            array.append(
                    normalizado.toPlainString()
            );
        }

        array.append('}');

        return array.toString();
    }

    protected void validarDetallesCotizacionRecibidos(
            List<RequerimientoCompraDetalle> detalles)
            throws Exception {

        if (detalles == null
                || detalles.isEmpty()) {

            throw new Exception(
                    "Debe informar los detalles de la cotización."
            );
        }

        Set<Integer> idsRecibidos =
                new HashSet<Integer>();

        for (int i = 0;
             i < detalles.size();
             i++) {

            RequerimientoCompraDetalle detalle =
                    detalles.get(i);

            if (detalle == null
                    || detalle.getIdInt() <= 0) {

                throw new Exception(
                        "La lista de detalles de cotización fue manipulada."
                );
            }

            Integer idDetalle =
                    Integer.valueOf(
                            detalle.getIdInt()
                    );

            if (!idsRecibidos.add(idDetalle)) {
                throw new Exception(
                        "El detalle "
                                + idDetalle
                                + " fue informado más de una vez."
                );
            }

            if (detalle.getPrecioUnitarioEstimado() != null
                    && detalle
                    .getPrecioUnitarioEstimado()
                    .compareTo(BigDecimal.ZERO) < 0) {

                throw new Exception(
                        "El precio unitario no puede ser negativo."
                );
            }
        }

        obtenerPrestadorAdjudicadoUnico(
                detalles
        );
    }

    protected Integer obtenerPrestadorAdjudicadoUnico(
            List<RequerimientoCompraDetalle> detalles) throws Exception {

        Integer idPrestadorAdjudicado = null;

        for (int i = 0;
             detalles != null && i < detalles.size();
             i++) {

            RequerimientoCompraDetalle detalle =
                    detalles.get(i);

            if (detalle == null
                    || !detalle.tienePrestadorAdjudicado()) {
                continue;
            }

            Integer idPrestadorDetalle =
                    detalle.getIdPrestador();

            if (idPrestadorAdjudicado == null) {
                idPrestadorAdjudicado =
                        idPrestadorDetalle;
            } else if (idPrestadorAdjudicado.intValue()
                    != idPrestadorDetalle.intValue()) {

                throw new Exception(
                        "Debe seleccionar un único prestador adjudicado "
                                + "para todo el requerimiento."
                );
            }
        }

        return idPrestadorAdjudicado;

    }

    private void validarArticuloParaGuardar(CompraArticulo articulo) throws Exception {
        if (articulo == null) {
            throw new Exception("Debe informar el artículo.");
        }

        if (articulo.getIdSector() == null || articulo.getIdSector().intValue() <= 0) {
            throw new Exception("Debe informar el sector del artículo.");
        }

        if (WebKeysCompras.isEmpty(articulo.getDescripcion())) {
            throw new Exception("Debe informar la descripción del artículo.");
        }
    }

    private CompraArticulo mapearArticulo(ResultSet rs) throws Exception {
        CompraArticulo articulo = new CompraArticulo();

        articulo.setId(Integer.valueOf(rs.getInt("id")));
        articulo.setIdSector(Integer.valueOf(rs.getInt("id_sector")));
        articulo.setSectorDescripcion(rs.getString("sector_descripcion"));
        articulo.setDescripcion(rs.getString("descripcion"));

        return articulo;
    }

    private Integer getIdRequerimientoDetalle(RequerimientoCompraDetalle detalle) {
        if (detalle.getIdRequerimiento() != null && detalle.getIdRequerimiento().intValue() > 0) {
            return detalle.getIdRequerimiento();
        }

        if (detalle.getIdRequerimientoCompra() > 0) {
            return Integer.valueOf(detalle.getIdRequerimientoCompra());
        }

        return null;
    }

    private void validarPorcentaje(Integer value, String label) throws Exception {
        int parsed = value != null ? value.intValue() : 0;

        if (parsed < 0 || parsed > 100) {
            throw new Exception(label + " debe estar entre 0 y 100.");
        }
    }

    private void setNullableInteger(
            CallableStatement stmt,
            int index,
            Integer value) throws SQLException {

        if (stmt == null) {
            throw new SQLException(
                    "No se informó el CallableStatement."
            );
        }

        if (value == null) {
            stmt.setNull(
                    index,
                    Types.INTEGER
            );
        } else {
            stmt.setInt(
                    index,
                    value.intValue()
            );
        }
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        return value.trim();
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

    private void cerrar(Statement stmt) {
        if (stmt == null) {
            return;
        }

        try {
            stmt.close();
        } catch (Exception ignored) {
        }
    }

    private String normalizarDescripcionArticulo(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() == 0) {
            return null;
        }

        String normalizado = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalizado = DIACRITICOS.matcher(normalizado).replaceAll("");
        normalizado = normalizado.replaceAll("\\s+", " ");

        return normalizado.toUpperCase(Locale.ROOT).trim();
    }

    private void validarArticuloDuplicado(CompraArticulo articulo) throws Exception {
        if (articulo == null
                || articulo.getIdSector() == null
                || articulo.getIdSector().intValue() <= 0
                || WebKeysCompras.isEmpty(articulo.getDescripcion())) {

            return;
        }

        String descripcionNormalizada =
                normalizarDescripcionArticulo(articulo.getDescripcion());

        List<CompraArticulo> existentes =
                listarArticulos(
                        articulo.getIdSector(),
                        null
                );

        if (existentes == null) {
            return;
        }

        for (int i = 0; i < existentes.size(); i++) {
            CompraArticulo existente = existentes.get(i);

            if (existente == null) {
                continue;
            }

            if (articulo.getId() != null
                    && existente.getId() != null
                    && articulo.getId().intValue() == existente.getId().intValue()) {

                continue;
            }

            String descripcionExistente =
                    normalizarDescripcionArticulo(existente.getDescripcion());

            if (descripcionNormalizada != null
                    && descripcionNormalizada.equals(descripcionExistente)) {

                throw new Exception(
                        "Ya existe un artículo con la descripción '"
                                + descripcionNormalizada
                                + "' para el sector seleccionado."
                );
            }
        }
    }

    public int registrarPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario)
            throws Exception {

        validarPresupuestoParaRegistrar(
                presupuesto
        );

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();

            stmt = con.prepareCall(
                    SQL_REGISTRAR_PRESUPUESTO
            );

            /*
             * 1: valor de retorno.
             * 2..12: argumentos de la función.
             */
            stmt.registerOutParameter(
                    1,
                    Types.INTEGER
            );

            stmt.setInt(
                    2,
                    presupuesto
                            .getIdRequerimiento()
                            .intValue()
            );

            stmt.setInt(
                    3,
                    presupuesto
                            .getIdPrestador()
                            .intValue()
            );

            stmt.setLong(
                    4,
                    presupuesto
                            .getDlGroupId()
                            .longValue()
            );

            stmt.setLong(
                    5,
                    presupuesto
                            .getDlFolderId()
                            .longValue()
            );

            stmt.setLong(
                    6,
                    presupuesto
                            .getDlFileEntryId()
                            .longValue()
            );

            stmt.setString(
                    7,
                    emptyToNull(
                            presupuesto.getDlFileUuid()
                    )
            );

            stmt.setString(
                    8,
                    emptyToNull(
                            presupuesto.getNombreOriginal()
                    )
            );

            stmt.setString(
                    9,
                    emptyToNull(
                            presupuesto.getNombrePersistido()
                    )
            );

            stmt.setString(
                    10,
                    emptyToNull(
                            presupuesto.getTitulo()
                    )
            );

            stmt.setString(
                    11,
                    emptyToNull(
                            presupuesto
                                    .getDescripcionPrestador()
                    )
            );

            stmt.setString(
                    12,
                    emptyToNull(usuario)
            );

            stmt.execute();

            int idRequerimientoPresupuesto =
                    stmt.getInt(1);

            if (stmt.wasNull()
                    || idRequerimientoPresupuesto <= 0) {

                throw new Exception(
                        "La base de datos devolvió un identificador "
                                + "de presupuesto inválido."
                );
            }

            return idRequerimientoPresupuesto;
        } catch (Exception e) {
            _log.error(
                    "No se pudo registrar la asociación "
                            + "del presupuesto. "
                            + "idRequerimiento="
                            + presupuesto.getIdRequerimiento()
                            + ", idPrestador="
                            + presupuesto.getIdPrestador()
                            + ", dlFileEntryId="
                            + presupuesto.getDlFileEntryId(),
                    e
            );

            throw e;
        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    public boolean darDeBajaPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario)
            throws Exception {

        if (idRequerimientoPresupuesto <= 0) {
            throw new Exception(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();

            stmt = con.prepareCall(
                    SQL_BAJA_PRESUPUESTO
            );

            stmt.registerOutParameter(
                    1,
                    Types.BOOLEAN
            );

            stmt.setInt(
                    2,
                    idRequerimientoPresupuesto
            );

            stmt.setInt(
                    3,
                    idRequerimientoCompra
            );

            stmt.setString(
                    4,
                    emptyToNull(usuario)
            );

            stmt.execute();

            boolean resultado =
                    stmt.getBoolean(1);

            return !stmt.wasNull()
                    && resultado;
        } catch (Exception e) {
            _log.error(
                    "No se pudo dar de baja el presupuesto. "
                            + "idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto
                            + ", idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );

            throw e;
        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    public boolean reactivarPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra)
            throws Exception {

        if (idRequerimientoPresupuesto <= 0) {
            throw new Exception(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();

            stmt = con.prepareCall(
                    SQL_REACTIVAR_PRESUPUESTO
            );

            stmt.registerOutParameter(
                    1,
                    Types.BOOLEAN
            );

            stmt.setInt(
                    2,
                    idRequerimientoPresupuesto
            );

            stmt.setInt(
                    3,
                    idRequerimientoCompra
            );

            stmt.execute();

            boolean resultado =
                    stmt.getBoolean(1);

            return !stmt.wasNull()
                    && resultado;
        } catch (Exception e) {
            _log.error(
                    "No se pudo reactivar el presupuesto. "
                            + "idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto
                            + ", idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );

            throw e;
        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    private void validarPresupuestoParaRegistrar(
            RequerimientoCompraPresupuesto presupuesto)
            throws Exception {

        if (presupuesto == null) {
            throw new Exception(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        if (presupuesto.getIdRequerimiento() == null
                || presupuesto
                .getIdRequerimiento()
                .intValue() <= 0) {

            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (presupuesto.getIdPrestador() == null
                || presupuesto
                .getIdPrestador()
                .intValue() <= 0) {

            throw new Exception(
                    "Debe informar el prestador del presupuesto."
            );
        }

        if (presupuesto.getDlGroupId() == null
                || presupuesto
                .getDlGroupId()
                .longValue() <= 0L) {

            throw new Exception(
                    "El groupId del documento no es válido."
            );
        }

        if (presupuesto.getDlFolderId() == null
                || presupuesto
                .getDlFolderId()
                .longValue() < 0L) {

            throw new Exception(
                    "El folderId del documento no es válido."
            );
        }

        if (presupuesto.getDlFileEntryId() == null
                || presupuesto
                .getDlFileEntryId()
                .longValue() <= 0L) {

            throw new Exception(
                    "El fileEntryId del documento no es válido."
            );
        }

        if (WebKeysCompras.isEmpty(
                presupuesto.getNombreOriginal()
        )) {
            throw new Exception(
                    "El nombre original del presupuesto no es válido."
            );
        }

        if (WebKeysCompras.isEmpty(
                presupuesto.getNombrePersistido()
        )) {
            throw new Exception(
                    "El nombre persistido del presupuesto no es válido."
            );
        }

        if (WebKeysCompras.isEmpty(
                presupuesto.getTitulo()
        )) {
            throw new Exception(
                    "El título del presupuesto no es válido."
            );
        }
    }


    protected int confirmarEnvioACotizar(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            con = ConnectionHelper.getConnection();

            stmt = con.prepareCall(
                    SQL_CONFIRMAR_ENVIO_A_COTIZAR
            );

            stmt.registerOutParameter(
                    1,
                    Types.INTEGER
            );

            stmt.setInt(
                    2,
                    idRequerimientoCompra
            );

            stmt.setString(
                    3,
                    emptyToNull(usuario)
            );

            stmt.execute();

            int estadoFinal =
                    stmt.getInt(1);

            if (stmt.wasNull()) {
                throw new Exception(
                        "La base no devolvió el estado final "
                                + "del requerimiento."
                );
            }

            return estadoFinal;

        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    private SQLException buscarSQLException(Throwable throwable) {
        Throwable actual = throwable;

        while (actual != null) {
            if (actual instanceof SQLException) {
                return (SQLException) actual;
            }

            actual = actual.getCause();
        }

        return null;
    }

    private void validarTienePresupuestoActivoDeTercerizadora(
            int idRequerimientoCompra) throws Exception {

        List<RequerimientoCompraPresupuesto> presupuestos =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPresupuestos(
                                idRequerimientoCompra
                        );

        if (presupuestos == null
                || presupuestos.isEmpty()) {

            throw new Exception(
                    "Para pasar el requerimiento a COTIZADO debe cargar "
                            + "al menos un archivo de presupuesto en una tercerizadora."
            );
        }

        for (int i = 0; i < presupuestos.size(); i++) {
            RequerimientoCompraPresupuesto presupuesto =
                    presupuestos.get(i);

            if (esPresupuestoActivoDeTercerizadora(
                    presupuesto
            )) {
                return;
            }
        }

        throw new Exception(
                "Para pasar el requerimiento a COTIZADO debe existir "
                        + "al menos un archivo activo cargado en una tercerizadora."
        );
    }

    private boolean esPresupuestoActivoDeTercerizadora(
            RequerimientoCompraPresupuesto presupuesto) {

        if (presupuesto == null) {
            return false;
        }

        if (presupuesto.getBajaFecha() != null) {
            return false;
        }

        if (presupuesto.getIdPrestador() == null
                || presupuesto.getIdPrestador().intValue() <= 0) {

            return false;
        }

        if (presupuesto.getDlFileEntryId() == null
                || presupuesto.getDlFileEntryId().longValue() <= 0L) {

            return false;
        }

        return true;
    }

    private boolean debeValidarPresupuestoParaCotizado(
            Integer idPrestadorAdjudicado,
            BigDecimal[] preciosUnitarios) {

        if (idPrestadorAdjudicado == null
                || idPrestadorAdjudicado.intValue() <= 0) {

            return false;
        }

        if (preciosUnitarios == null
                || preciosUnitarios.length == 0) {

            return false;
        }

        for (int i = 0; i < preciosUnitarios.length; i++) {
            if (preciosUnitarios[i] == null) {
                return false;
            }
        }

        return true;
    }
}
