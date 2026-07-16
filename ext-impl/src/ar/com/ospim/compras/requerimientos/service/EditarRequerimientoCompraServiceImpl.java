package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.text.Normalizer;
import java.util.Locale;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class EditarRequerimientoCompraServiceImpl {

    private static final Log _log =
            LogFactoryUtil.getLog(EditarRequerimientoCompraServiceImpl.class);


    private static final Pattern DIACRITICOS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final String SQL_GUARDAR_REQUERIMIENTO =
            "{ ? = call compras.guardar_requerimiento(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

    private static final String SQL_GUARDAR_REQUERIMIENTO_DETALLE =
            "{call compras.guardar_requerimiento_detalle("
                    + "?,?,?,?,?,?,?,?,?,?,?,?,?"
                    + ")}";

    private static final String SQL_BORRAR_REQUERIMIENTO_DETALLE =
            "{ call compras.borrar_requerimiento_detalle(?,?) }";

    private static final String SQL_BORRAR_REQUERIMIENTO =
            "{ call compras.borrar_requerimiento(?,?) }";

    private static final String SQL_CAMBIAR_ESTADO =
            "{ call compras.cambiar_estado_requerimiento(?,?,?) }";

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

    /*
     * Las excepciones de esta clase terminan siendo mostradas por la capa
     * de acción. Por eso se distingue explícitamente entre errores aptos
     * para el usuario y fallas técnicas que solo deben quedar en el log.
     */
    private static final class MensajeUsuarioException extends Exception {

        private MensajeUsuarioException(String mensaje) {
            super(mensaje);
        }

        private MensajeUsuarioException(
                String mensaje,
                Throwable causa) {

            super(mensaje, causa);
        }
    }

    public int guardarRequerimientoCompra(
            RequerimientoCompra requerimiento,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            validarRequerimientoParaGuardar(
                    requerimiento
            );

            if (requerimiento.getIdRequerimientoCompra() > 0) {
                RequerimientoCompra actual =
                        BusquedaRequerimientoCompraServiceUtil
                                .getRequerimientoCompra(
                                        requerimiento
                                                .getIdRequerimientoCompra()
                                );

                if (actual == null) {
                    throw errorUsuario(
                            "El requerimiento que intenta editar ya no est? disponible."
                    );
                }

                if (!actual.puedeEditarEstructura()) {
                    throw errorUsuario(
                            "El requerimiento ya no puede modificarse porque "
                                    + "no se encuentra en estado PENDIENTE."
                    );
                }
            }

            con = obtenerConexionGuardarRequerimiento();

            if (con == null) {
                throw new SQLException(
                        "No se obtuvo una conexión para guardar el requerimiento."
                );
            }

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

            int idGuardado = stmt.getInt(1);

            if (stmt.wasNull()
                    || idGuardado <= 0) {

                throw new SQLException(
                        "La función de guardado devolvi? un identificador inválido."
                );
            }

            return idGuardado;

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "guardar el requerimiento de compra",
                    "No se pudo guardar el requerimiento de compra. "
                            + "Revise los datos e intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + obtenerIdRequerimientoSeguro(requerimiento)
                            + ", usuario=" + usuario
            );

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

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        Integer idRequerimiento = null;

        try {
            if (detalle == null) {
                throw errorUsuario(
                        "Debe informar el detalle del requerimiento."
                );
            }

            idRequerimiento =
                    getIdRequerimientoDetalle(
                            detalle
                    );

            RequerimientoCompra requerimiento =
                    validarRequerimientoDetalle(
                            idRequerimiento
                    );

            RequerimientoCompraDetalle detallePersistido =
                    obtenerDetallePersistido(
                            requerimiento,
                            detalle.getIdInt()
                    );

            prepararDetalleParaGuardar(
                    requerimiento,
                    detallePersistido,
                    detalle
            );

            validarDetalleParaGuardar(
                    requerimiento,
                    detalle
            );

            con = obtenerConexionGuardarDetalle();

            if (con == null) {
                throw new SQLException(
                        "No se obtuvo una conexión para guardar el detalle."
                );
            }

            stmt = con.prepareCall(
                    SQL_GUARDAR_REQUERIMIENTO_DETALLE
            );

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

            stmt.setString(
                    3,
                    emptyToNull(
                            detalle.getTipoItemNormalizado()
                    )
            );

            setNullableInteger(
                    stmt,
                    4,
                    detalle.getIdPrestacion()
            );

            setNullableInteger(
                    stmt,
                    5,
                    detalle.getIdTipoNomenclador()
            );

            stmt.setString(
                    6,
                    emptyToNull(
                            detalle.getCodigoNomenclador()
                    )
            );

            stmt.setString(
                    7,
                    emptyToNull(
                            detalle.getDescripcionNomenclador()
                    )
            );

            setNullableInteger(
                    stmt,
                    8,
                    detalle.getIdMedicamento()
            );

            setNullableInteger(
                    stmt,
                    9,
                    detalle.getTroquel()
            );

            stmt.setString(
                    10,
                    emptyToNull(
                            detalle.getNombreMedicamento()
                    )
            );

            setNullableInteger(
                    stmt,
                    11,
                    detalle.getCantidad()
            );

            stmt.setString(
                    12,
                    emptyToNull(
                            detalle.getObservaciones()
                    )
            );

            stmt.setString(
                    13,
                    emptyToNull(usuario)
            );

            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException(
                        "La función de guardado no devolvi? el identificador del detalle."
                );
            }

            int idDetalleGuardado =
                    rs.getInt(1);

            if (rs.wasNull()
                    || idDetalleGuardado <= 0) {

                throw new SQLException(
                        "La función de guardado devolvi? un identificador de detalle inválido."
                );
            }

            if (rs.next()) {
                throw new SQLException(
                        "La función de guardado devolvi? más de un resultado."
                );
            }

            return idDetalleGuardado;

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "guardar el detalle del requerimiento",
                    "No se pudo guardar el detalle del requerimiento. "
                            + "Revise la información e intente nuevamente.",
                    e,
                    construirContextoDetalle(
                            detalle,
                            idRequerimiento,
                            usuario
                    )
            );

        } finally {
            cerrar(rs);

            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    public void borrarDetalle(
            int idDetalle,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            if (idDetalle <= 0) {
                throw errorUsuario(
                        "Debe informar el detalle que desea quitar."
                );
            }

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(
                    SQL_BORRAR_REQUERIMIENTO_DETALLE
            );
            stmt.setInt(1, idDetalle);
            stmt.setString(2, emptyToNull(usuario));
            stmt.execute();

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "quitar el detalle del requerimiento",
                    "No se pudo quitar el detalle del requerimiento. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idDetalle=" + idDetalle
                            + ", usuario=" + usuario
            );

        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void borrarRequerimientoCompra(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra que desea eliminar."
                );
            }

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(
                    SQL_BORRAR_REQUERIMIENTO
            );
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setString(2, emptyToNull(usuario));
            stmt.execute();

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "eliminar el requerimiento de compra",
                    "No se pudo eliminar el requerimiento de compra. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", usuario=" + usuario
            );

        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public void cambiarEstado(
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            if (!WebKeysCompras.esEstadoValido(idEstadoNuevo)) {
                throw errorUsuario(
                        "El estado seleccionado no es válido."
                );
            }

            RequerimientoCompra requerimientoActual =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    idRequerimientoCompra
                            );

            if (requerimientoActual == null) {
                throw errorUsuario(
                        "El requerimiento ya no est? disponible."
                );
            }

            if (!WebKeysCompras.validarTransicionEstado(
                    requerimientoActual.getEstado(),
                    idEstadoNuevo
            )) {
                throw errorUsuario(
                        "El requerimiento no puede pasar al estado seleccionado "
                                + "desde su estado actual."
                );
            }

            con = ConnectionHelper.getConnection();
            stmt = con.prepareCall(SQL_CAMBIAR_ESTADO);
            stmt.setInt(1, idRequerimientoCompra);
            stmt.setInt(2, idEstadoNuevo);
            stmt.setString(3, emptyToNull(usuario));
            stmt.execute();

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "actualizar el estado del requerimiento",
                    "No se pudo actualizar el estado del requerimiento. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", idEstadoNuevo=" + idEstadoNuevo
                            + ", usuario=" + usuario
            );

        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    public NotificacionCotizacionResultado enviarACotizar(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        try {
            RequerimientoCompra requerimiento =
                    validarRequerimientoParaEnviarACotizar(
                            idRequerimientoCompra
                    );

            NotificacionCotizacionResultado resultado =
                    NotificarCotizacionPrestadorServiceUtil
                            .notificarPrestadores(
                                    requerimiento
                                            .getIdRequerimientoCompra(),
                                    usuario,
                                    companyId
                            );

            if (resultado == null) {
                throw new IllegalStateException(
                        "El proceso de notificación no devolvi? resultado."
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

                throw new IllegalStateException(
                        "Estado inesperado al confirmar el envío: "
                                + estadoFinal
                );
            }

            return resultado;

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "enviar el requerimiento a cotizar",
                    "No se pudo enviar el requerimiento a cotizar. "
                            + "Verifique los datos e intente nuevamente.",
                    e,
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", companyId=" + companyId
                            + ", usuario=" + usuario
            );
        }
    }

    public NotificacionCotizacionResultado reintentarNotificacionesCotizacion(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            RequerimientoCompra requerimiento =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    idRequerimientoCompra
                            );

            if (requerimiento == null) {
                throw errorUsuario(
                        "El requerimiento ya no est? disponible."
                );
            }

            if (!requerimiento.puedeReintentarNotificaciones()) {
                throw errorUsuario(
                        "Las notificaciones solo pueden reenviarse mientras "
                                + "el requerimiento est? en estado A COTIZAR."
                );
            }

            if (!BusquedaRequerimientoCompraServiceUtil
                    .hayPrestadoresPendientesNotificacion(
                            idRequerimientoCompra
                    )) {

                return new NotificacionCotizacionResultado();
            }

            return NotificarCotizacionPrestadorServiceUtil
                    .notificarPrestadores(
                            idRequerimientoCompra,
                            usuario,
                            companyId
                    );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "reenviar las notificaciones de cotización",
                    "No se pudieron reenviar las notificaciones pendientes. "
                            + "Intente nuevamente.",
                    e,
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", companyId=" + companyId
                            + ", usuario=" + usuario
            );
        }
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

    /*
     * Se conserva este método por compatibilidad con consumidores existentes.
     * La función SQL determina de forma atómica si corresponde guardar un
     * avance o pasar el requerimiento a COTIZADO.
     */
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

    private void validarRequerimientoParaGuardar(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null) {
            throw errorUsuario(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {

            throw errorUsuario(
                    "Debe seleccionar el sector del requerimiento."
            );
        }

        validarPorcentaje(
                requerimiento.getCargoOspim(),
                "El cargo de OSPIM"
        );
        validarPorcentaje(
                requerimiento.getCargoTercerizadora(),
                "El cargo de la tercerizadora"
        );

        int cargoOspim =
                requerimiento.getCargoOspim() != null
                        ? requerimiento.getCargoOspim().intValue()
                        : 0;

        int cargoTercerizadora =
                requerimiento.getCargoTercerizadora() != null
                        ? requerimiento
                          .getCargoTercerizadora()
                          .intValue()
                        : 0;

        if (cargoOspim + cargoTercerizadora > 100) {
            throw errorUsuario(
                    "La suma de los cargos de OSPIM y la tercerizadora "
                            + "no puede superar el 100 %."
            );
        }

        if (cargoTercerizadora > 0
                && WebKeysCompras.isEmpty(
                requerimiento.getIdTercerizadora()
        )) {
            throw errorUsuario(
                    "Debe seleccionar una tercerizadora cuando su cargo "
                            + "es mayor que cero."
            );
        }

        if (requerimiento.isRequiereAfiliado()) {
            if (WebKeysCompras.isEmpty(
                    requerimiento.getAfiliadoCuilTitular()
            )) {
                throw errorUsuario(
                        "Debe informar el CUIL del titular afiliado."
                );
            }

            if (requerimiento.getAfiliadoInt() == null
                    || requerimiento
                    .getAfiliadoInt()
                    .intValue() < 0) {

                throw errorUsuario(
                        "Debe informar el integrante del grupo familiar."
                );
            }
        }
    }

    private void validarDetalleParaGuardar(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle) throws Exception {

        if (detalle == null) {
            throw errorUsuario(
                    "Debe informar el detalle del requerimiento."
            );
        }

        Integer idRequerimiento =
                getIdRequerimientoDetalle(detalle);

        if (idRequerimiento == null
                || idRequerimiento.intValue() <= 0) {

            throw errorUsuario(
                    "Primero debe guardar los datos generales del requerimiento."
            );
        }

        if (detalle.getCantidad() == null) {
            detalle.setCantidad(
                    Integer.valueOf(1)
            );
        }

        if (detalle.getCantidad().intValue() <= 0) {
            throw errorUsuario(
                    "La cantidad debe ser mayor que cero."
            );
        }

        String tipoItem =
                detalle.getTipoItemNormalizado();

        if (WebKeysCompras.isEmpty(tipoItem)) {
            throw errorUsuario(
                    "Debe seleccionar el tipo de ?tem."
            );
        }

        if (!RequerimientoCompraDetalle
                .TIPO_ITEM_NOMENCLADOR
                .equals(tipoItem)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_MEDICAMENTO
                .equals(tipoItem)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_OBSERVACION
                .equals(tipoItem)) {

            throw errorUsuario(
                    "El tipo de ?tem seleccionado no es válido."
            );
        }

        if (RequerimientoCompraDetalle
                .TIPO_ITEM_NOMENCLADOR
                .equals(tipoItem)) {

            validarDetalleNomencladorParaGuardar(
                    requerimiento,
                    detalle
            );
        }

        if (RequerimientoCompraDetalle
                .TIPO_ITEM_MEDICAMENTO
                .equals(tipoItem)) {

            validarDetalleMedicamentoParaGuardar(
                    detalle
            );
        }

        if (RequerimientoCompraDetalle
                .TIPO_ITEM_OBSERVACION
                .equals(tipoItem)) {

            validarDetalleObservacionParaGuardar(
                    detalle
            );
        }

        if (detalle.getPrecioUnitarioEstimado() != null
                && detalle
                .getPrecioUnitarioEstimado()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw errorUsuario(
                    "El precio unitario no puede ser negativo."
            );
        }

        if (detalle.getPrecioTotalEstimadoInformado() != null
                && detalle
                .getPrecioTotalEstimadoInformado()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw errorUsuario(
                    "El precio total no puede ser negativo."
            );
        }
    }

    private void validarDetalleNomencladorParaGuardar(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle) throws Exception {

        if (detalle.getIdPrestacion() == null
                || detalle.getIdPrestacion().intValue() <= 0) {

            throw errorUsuario(
                    "Debe seleccionar una prestacion del nomenclador."
            );
        }

        if (detalle.getIdTipoNomenclador() == null
                || detalle.getIdTipoNomenclador().intValue() <= 0) {

            throw errorUsuario(
                    "Debe seleccionar el tipo de nomenclador."
            );
        }

        if (WebKeysCompras.isEmpty(
                detalle.getCodigoNomenclador()
        )) {
            throw errorUsuario(
                    "La prestacion seleccionada no tiene un codigo valido."
            );
        }

        if (WebKeysCompras.isEmpty(
                detalle.getDescripcionNomenclador()
        )) {
            throw errorUsuario(
                    "La prestacion seleccionada no tiene una descripcion valida."
            );
        }

        /*
         * Un detalle NOMENCLADOR no puede contener informacion
         * tecnica correspondiente a un medicamento.
         */
        if (detalle.getIdMedicamento() != null
                || detalle.getTroquel() != null
                || !WebKeysCompras.isEmpty(
                detalle.getNombreMedicamento()
        )) {

            throw errorUsuario(
                    "Los datos recibidos no corresponden a una prestacion. "
                            + "Actualice la pantalla y vuelva a seleccionarla."
            );
        }

        /*
         * Se recupera nuevamente la prestacion canonica.
         *
         * Los identificadores, el tipo de nomenclador, la marca ReinLiq,
         * el codigo, la descripcion y el estado activo no se confian
         * a los valores enviados desde la pantalla.
         */
        Nomenclador nomenclador =
                obtenerNomencladorCanonico(
                        detalle.getIdPrestacion().intValue()
                );

        if (nomenclador == null
                || nomenclador.getId_prestacion()
                != detalle.getIdPrestacion().intValue()
                || nomenclador.getBaja_fecha() != null) {

            throw errorUsuario(
                    "La prestacion seleccionada ya no existe "
                            + "o no esta activa. Vuelva a seleccionarla."
            );
        }

        int idTipoNomencladorCanonico =
                nomenclador.getId_tipo_nomenclador();

        if (idTipoNomencladorCanonico <= 0) {
            throw errorUsuario(
                    "La prestacion seleccionada no tiene un "
                            + "tipo de nomenclador valido."
            );
        }

        /*
         * El tipo recibido desde la pantalla debe coincidir con
         * el tipo real persistido para la prestacion.
         */
        if (idTipoNomencladorCanonico
                != detalle.getIdTipoNomenclador().intValue()) {

            throw errorUsuario(
                    "La prestacion seleccionada no corresponde al "
                            + "tipo de nomenclador actual. "
                            + "Vuelva a seleccionarla."
            );
        }

        String sectorDescripcion =
                requerimiento != null
                        ? requerimiento.getSectorDescripcion()
                        : null;

        String sector =
                WebKeysCompras
                        .normalizarSectorCompra(
                                sectorDescripcion
                        );

        if (WebKeysCompras.isEmpty(
                sector
        )) {
            throw errorUsuario(
                    "No se pudo determinar el sector del requerimiento."
            );
        }

        /*
         * Replica literal de la matriz utilizada por
         * Reclamos Prestacionales:
         *
         * FARMACIA:
         *     tipo 9.
         *
         * DISCAPACIDAD:
         *     marca ReinLiq 6 o codigo 431003.
         *
         * ODONTOLOGIA:
         *     tipo 1.
         *
         * PRESTACIONES MEDICAS:
         *     cualquier tipo distinto de 1 y 9.
         *
         * MONOTRIBUTO:
         *     cualquier tipo positivo distinto de 9.
         */
        boolean nomencladorValido =
                WebKeysCompras
                        .esNomencladorValidoParaSectorCompras(
                                sector,
                                idTipoNomencladorCanonico,
                                nomenclador
                                        .getMarcaReintegroLiquidacion(),
                                nomenclador.getCodigo()
                        );

        if (!nomencladorValido) {
            if ("FARMACIA".equals(
                    sector
            )) {
                throw errorUsuario(
                        "Para Farmacia debe seleccionar una "
                                + "prestacion del nomenclador tipo 9."
                );
            }

            if ("DISCAPACIDAD".equals(
                    sector
            )) {
                throw errorUsuario(
                        "Para Discapacidad debe seleccionar una "
                                + "prestacion con marca ReinLiq 6 "
                                + "o el codigo 431003."
                );
            }

            if ("ODONTOLOGIA".equals(
                    sector
            )) {
                throw errorUsuario(
                        "Para Odontologia debe seleccionar una "
                                + "prestacion del nomenclador tipo 1."
                );
            }

            if ("PRESTACIONES MEDICAS".equals(
                    sector
            )) {
                throw errorUsuario(
                        "Prestaciones Medicas no admite "
                                + "prestaciones del nomenclador tipo 1 o 9."
                );
            }

            if ("MONOTRIBUTO".equals(
                    sector
            )) {
                throw errorUsuario(
                        "Monotributo no admite prestaciones "
                                + "del nomenclador tipo 9."
                );
            }

            throw errorUsuario(
                    "La prestacion seleccionada no corresponde "
                            + "al sector del requerimiento."
            );
        }

        /*
         * Aunque codigo y descripcion llegaron desde la pantalla,
         * deben coincidir con los datos canonicos de la prestacion.
         */
        validarTextoTecnico(
                "codigo de nomenclador",
                detalle.getCodigoNomenclador(),
                nomenclador.getCodigo()
        );

        validarTextoTecnico(
                "descripcion de nomenclador",
                detalle.getDescripcionNomenclador(),
                nomenclador.getDescripcion()
        );

        /*
         * Finalmente se reemplazan los textos recibidos por los
         * valores canonicos obtenidos desde autorizaciones.nomenclador.
         */
        detalle.setCodigoNomenclador(
                emptyToNull(
                        nomenclador.getCodigo()
                )
        );

        detalle.setDescripcionNomenclador(
                emptyToNull(
                        nomenclador.getDescripcion()
                )
        );
    }

    private void validarDetalleMedicamentoParaGuardar(
            RequerimientoCompraDetalle detalle) throws Exception {

        /*
         * MEDICAMENTO queda admitido exclusivamente para
         * registros históricos ya persistidos.
         */
        if (detalle.getIdInt() <= 0) {
            throw errorUsuario(
                    "No se pueden crear nuevos detalles "
                            + "de tipo MEDICAMENTO en Compras."
            );
        }

        if (detalle.getIdMedicamento() == null
                || detalle.getIdMedicamento().intValue() <= 0) {

            throw errorUsuario(
                    "El detalle histórico de medicamento "
                            + "no conserva un identificador válido."
            );
        }

        if (WebKeysCompras.isEmpty(
                detalle.getNombreMedicamento()
        )) {
            throw errorUsuario(
                    "El detalle histórico de medicamento "
                            + "no conserva su descripción."
            );
        }

        if (detalle.getIdPrestacion() != null
                || detalle.getIdTipoNomenclador() != null
                || !WebKeysCompras.isEmpty(
                detalle.getCodigoNomenclador()
        )
                || !WebKeysCompras.isEmpty(
                detalle.getDescripcionNomenclador()
        )) {

            throw errorUsuario(
                    "El detalle histórico de medicamento "
                            + "contiene datos técnicos incompatibles."
            );
        }
    }

    private void validarDetalleObservacionParaGuardar(
            RequerimientoCompraDetalle detalle) throws Exception {

        if (WebKeysCompras.isEmpty(
                detalle.getObservaciones()
        )) {
            throw errorUsuario(
                    "Debe informar las Observaciones del detalle."
            );
        }

        if (detalle.getIdPrestacion() != null
                || detalle.getIdTipoNomenclador() != null
                || !WebKeysCompras.isEmpty(detalle.getCodigoNomenclador())
                || !WebKeysCompras.isEmpty(detalle.getDescripcionNomenclador())
                || detalle.getIdMedicamento() != null
                || detalle.getTroquel() != null
                || !WebKeysCompras.isEmpty(detalle.getNombreMedicamento())) {

            throw errorUsuario(
                    "Un detalle de Observación no puede contener "
                            + "datos de código o medicamento."
            );
        }
    }

    private RequerimientoCompra validarRequerimientoDetalle(
            Integer idRequerimiento) throws Exception {

        if (idRequerimiento == null
                || idRequerimiento.intValue() <= 0) {
            throw errorUsuario(
                    "Primero debe guardar los datos generales del requerimiento."
            );
        }

        RequerimientoCompra requerimiento =
                obtenerRequerimientoDetalle(
                        idRequerimiento.intValue()
                );

        if (requerimiento == null
                || !requerimiento.puedeEditarEstructura()) {
            throw errorUsuario(
                    "Los detalles ya no pueden modificarse porque el requerimiento no se encuentra PENDIENTE."
            );
        }

        if (requerimiento.getSectorId() == null
                || requerimiento.getSectorId().intValue() <= 0) {
            throw errorUsuario(
                    "El requerimiento no tiene un sector válido. Actualice sus datos antes de continuar."
            );
        }

        return requerimiento;
    }

    private void prepararDetalleParaGuardar(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detallePersistido,
            RequerimientoCompraDetalle detalle) throws Exception {

        if (detalle == null) {
            throw errorUsuario(
                    "Debe informar el detalle del requerimiento."
            );
        }

        /*
         * El único caso en el que MEDICAMENTO sigue siendo válido
         * es la edición controlada de una fila histórica.
         *
         * Los datos técnicos se reconstruyen desde la base:
         * nunca se confía en los ocultos del navegador.
         */
        if (detallePersistido != null
                && detallePersistido.esMedicamento()) {

            String tipoRecibido =
                    detalle.getTipoItemNormalizado();

            if (!WebKeysCompras.isEmpty(tipoRecibido)
                    && !RequerimientoCompraDetalle
                    .TIPO_ITEM_MEDICAMENTO
                    .equals(tipoRecibido)) {

                throw errorUsuario(
                        "El detalle histórico de medicamento "
                                + "no puede convertirse directamente "
                                + "a otro tipo."
                );
            }

            detalle.setTipoItem(
                    RequerimientoCompraDetalle
                            .TIPO_ITEM_MEDICAMENTO
            );

            detalle.setIdPrestacion(null);
            detalle.setIdTipoNomenclador(null);
            detalle.setCodigoNomenclador(null);
            detalle.setDescripcionNomenclador(null);

            detalle.setIdMedicamento(
                    detallePersistido
                            .getIdMedicamento()
            );

            detalle.setTroquel(
                    detallePersistido
                            .getTroquel()
            );

            detalle.setNombreMedicamento(
                    detallePersistido
                            .getNombreMedicamento()
            );

            detalle.setCodigoItem(
                    detallePersistido
                            .getCodigoItemVisible()
            );

            detalle.setDescripcionItem(
                    detallePersistido
                            .getDescripcionItemVisible()
            );

            return;
        }

        boolean sectorObservacion =
                WebKeysCompras
                        .esSectorDetalleObservacionCompras(
                                requerimiento != null
                                        ? requerimiento
                                          .getSectorDescripcion()
                                        : null
                        );

        if (sectorObservacion) {
            if (detallePersistido != null
                    && !detallePersistido.esObservacion()) {

                throw errorUsuario(
                        "El detalle existente no corresponde al sector "
                                + "seleccionado. Debe quitarlo antes de "
                                + "cambiar el sector."
                );
            }

            String tipoRecibido =
                    detalle.getTipoItemNormalizado();

            if (!WebKeysCompras.isEmpty(tipoRecibido)
                    && !RequerimientoCompraDetalle
                    .TIPO_ITEM_OBSERVACION
                    .equals(tipoRecibido)) {

                throw errorUsuario(
                        "El sector seleccionado requiere un detalle "
                                + "de OBSERVACION."
                );
            }

            detalle.setTipoItem(
                    RequerimientoCompraDetalle
                            .TIPO_ITEM_OBSERVACION
            );
            detalle.setIdPrestacion(null);
            detalle.setIdTipoNomenclador(null);
            detalle.setCodigoNomenclador(null);
            detalle.setDescripcionNomenclador(null);
            detalle.setIdMedicamento(null);
            detalle.setTroquel(null);
            detalle.setNombreMedicamento(null);
            detalle.setCodigoItem(null);
            detalle.setDescripcionItem(null);

            return;
        }

        Integer filtroTipoNomenclador =
                WebKeysCompras
                        .getFiltroTipoNomencladorCompras(
                                requerimiento != null
                                        ? requerimiento
                                          .getSectorDescripcion()
                                        : null
                        );

        if (filtroTipoNomenclador == null) {
            throw errorUsuario(
                    "El sector seleccionado no tiene configurado "
                            + "un nomenclador para Compras."
            );
        }

        if (detallePersistido != null
                && detallePersistido.esObservacion()) {

            throw errorUsuario(
                    "El detalle existente no corresponde al sector "
                            + "seleccionado. Debe quitarlo antes de "
                            + "cambiar el sector."
            );
        }

        String tipoRecibido =
                detalle.getTipoItemNormalizado();

        if (!WebKeysCompras.isEmpty(tipoRecibido)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_NOMENCLADOR
                .equals(tipoRecibido)) {

            throw errorUsuario(
                    "Los detalles nuevos de Compras deben "
                            + "utilizar NOMENCLADOR."
            );
        }

        detalle.setTipoItem(
                RequerimientoCompraDetalle
                        .TIPO_ITEM_NOMENCLADOR
        );

        detalle.setIdMedicamento(null);
        detalle.setTroquel(null);
        detalle.setNombreMedicamento(null);

        detalle.setCodigoItem(
                detalle.getCodigoNomenclador()
        );

        detalle.setDescripcionItem(
                detalle.getDescripcionNomenclador()
        );
    }

    private RequerimientoCompraDetalle obtenerDetallePersistido(
            RequerimientoCompra requerimiento,
            int idDetalle) throws Exception {

        if (idDetalle <= 0) {
            return null;
        }

        if (requerimiento == null) {
            throw errorUsuario(
                    "No se pudo validar el requerimiento del detalle."
            );
        }

        List<RequerimientoCompraDetalle> detalles =
                requerimiento.getDetalles();

        if (detalles != null) {
            for (int i = 0;
                 i < detalles.size();
                 i++) {

                RequerimientoCompraDetalle persistido =
                        detalles.get(i);

                if (persistido != null
                        && persistido.getIdInt() == idDetalle
                        && persistido.getIdRequerimientoCompra()
                        == requerimiento
                        .getIdRequerimientoCompra()) {

                    return persistido;
                }
            }
        }

        throw errorUsuario(
                "El detalle que intenta modificar ya no existe "
                        + "o no pertenece al requerimiento."
        );
    }

    protected RequerimientoCompra obtenerRequerimientoDetalle(
            int idRequerimiento) throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .getRequerimientoCompra(
                        idRequerimiento
                );
    }

    protected Connection obtenerConexionGuardarDetalle()
            throws Exception {

        return ConnectionHelper.getConnection();
    }

    protected Nomenclador obtenerNomencladorCanonico(
            int idPrestacion) throws Exception {

        return NomencladorServiceUtil.buscarNomencladorPorId(
                idPrestacion
        );
    }

    protected Medicamento obtenerMedicamentoCanonico(
            int idMedicamento) throws Exception {

        return BusquedaMedicamentoServiceUtil.getMedicamento(
                idMedicamento
        );
    }

    private void validarTextoTecnico(
            String campo,
            String recibido,
            String canonico) throws Exception {

        String recibidoNormalizado =
                normalizarTextoTecnico(recibido);
        String canonicoNormalizado =
                normalizarTextoTecnico(canonico);

        if (recibidoNormalizado == null
                || canonicoNormalizado == null
                || !recibidoNormalizado.equalsIgnoreCase(
                canonicoNormalizado
        )) {
            throw errorUsuario(
                    "El " + campo
                            + " cambi? o ya no coincide con la información actual. Vuelva a seleccionarlo."
            );
        }
    }

    private String normalizarTextoTecnico(String value) {
        String result = emptyToNull(value);

        return result == null
                ? null
                : result.replaceAll("\\s+", " ");
    }

    private String normalizarClave(String value) {
        String result = normalizarTextoTecnico(value);

        if (result == null) {
            return "";
        }

        result = Normalizer.normalize(
                result,
                Normalizer.Form.NFD
        );
        result = DIACRITICOS.matcher(result).replaceAll("");

        return result.toUpperCase(Locale.ROOT);
    }

    private RequerimientoCompra validarRequerimientoParaEnviarACotizar(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw errorUsuario("Debe informar el requerimiento de compra.");
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            throw errorUsuario("No se encontr? el requerimiento de compra informado.");
        }

        if (!requerimiento.puedeEnviarACotizar()) {
            throw errorUsuario("El requerimiento solo puede enviarse a cotizar mientras est? PENDIENTE.");
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {
            throw errorUsuario("Debe seleccionar el sector antes de enviar el requerimiento a cotizar.");
        }

        if (!requerimiento.tieneDetalles()) {
            throw errorUsuario("Debe agregar al menos un detalle antes de enviar el requerimiento a cotizar.");
        }

        if (requerimiento.isRequiereAfiliado()
                && !requerimiento.tieneAfiliadoInformado()) {
            throw errorUsuario("Debe completar los datos del afiliado antes de enviar el requerimiento a cotizar.");
        }

        return requerimiento;
    }

    private GuardadoCotizacionResultado guardarCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Integer idPrestadorAdjudicado = null;

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            validarDetallesCotizacionRecibidos(
                    detalles
            );

            idPrestadorAdjudicado =
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
             * La base sigue siendo la validación definitiva. Esta comprobación
             * previa evita que una situación esperable termine mostrando una
             * excepción técnica de PostgreSQL en la interfaz.
             */
            if (debeValidarPresupuestoParaCotizado(
                    idPrestadorAdjudicado,
                    preciosUnitarios
            )) {
                validarTienePresupuestoActivoDelPrestador(
                        idRequerimientoCompra,
                        idPrestadorAdjudicado.intValue()
                );
            }

            con = ConnectionHelper.getConnection();

            if (con == null) {
                throw new SQLException(
                        "No se obtuvo una conexión para guardar la cotización."
                );
            }

            stmt = con.prepareStatement(
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

            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException(
                        "La función de cotización no devolvi? el estado final."
                );
            }

            int estadoFinal =
                    rs.getInt(1);

            if (rs.wasNull()
                    || (estadoFinal
                    != WebKeysCompras.ESTADO_A_COTIZAR
                    && estadoFinal
                    != WebKeysCompras.ESTADO_COTIZADO)) {

                throw new SQLException(
                        "La función de cotización devolvi? un estado inválido: "
                                + estadoFinal
                );
            }

            return new GuardadoCotizacionResultado(
                    estadoFinal
                            == WebKeysCompras.ESTADO_COTIZADO,
                    estadoFinal
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "guardar la cotización",
                    "No se pudo guardar la cotización. "
                            + "Revise los datos e intente nuevamente.",
                    e,
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", idPrestadorAdjudicado="
                            + idPrestadorAdjudicado
                            + ", cantidadDetalles="
                            + (detalles != null ? detalles.size() : 0)
                            + ", usuario=" + usuario
            );

        } finally {
            cerrar(rs);

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

            throw errorUsuario(
                    "No se recibieron los detalles de la cotización. Actualice la pantalla y vuelva a intentarlo."
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

                throw errorUsuario(
                        "Uno de los detalles ya no es válido. Actualice la pantalla y vuelva a intentarlo."
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

            throw errorUsuario(
                    "No se recibieron los precios de la cotización. Actualice la pantalla y vuelva a intentarlo."
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

                throw errorUsuario(
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

            throw errorUsuario(
                    "La cotización no contiene detalles para guardar."
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

                throw errorUsuario(
                        "Los detalles recibidos no coinciden con el requerimiento. Actualice la pantalla y vuelva a intentarlo."
                );
            }

            Integer idDetalle =
                    Integer.valueOf(
                            detalle.getIdInt()
                    );

            if (!idsRecibidos.add(idDetalle)) {
                throw errorUsuario(
                        "La cotización contiene un detalle repetido. Actualice la pantalla y vuelva a intentarlo."
                );
            }

            if (detalle.getPrecioUnitarioEstimado() != null
                    && detalle
                    .getPrecioUnitarioEstimado()
                    .compareTo(BigDecimal.ZERO) < 0) {

                throw errorUsuario(
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

                throw errorUsuario(
                        "Debe seleccionar el mismo prestador adjudicado para todos los detalles."
                );
            }
        }

        return idPrestadorAdjudicado;

    }

    private Integer getIdRequerimientoDetalle(
            RequerimientoCompraDetalle detalle) {

        if (detalle == null) {
            return null;
        }

        if (detalle.getIdRequerimiento() != null
                && detalle
                .getIdRequerimiento()
                .intValue() > 0) {

            return detalle.getIdRequerimiento();
        }

        if (detalle.getIdRequerimientoCompra() > 0) {
            return Integer.valueOf(
                    detalle.getIdRequerimientoCompra()
            );
        }

        return null;
    }

    private void validarPorcentaje(
            Integer value,
            String label) throws Exception {

        int parsed =
                value != null
                        ? value.intValue()
                        : 0;

        if (parsed < 0
                || parsed > 100) {

            throw errorUsuario(
                    label + " debe estar entre 0 y 100 %."
            );
        }
    }

    private void setNullableInteger(
            CallableStatement stmt,
            int index,
            Integer value) throws SQLException {

        if (stmt == null) {
            throw new SQLException(
                    "No se inform? el CallableStatement."
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

    public int registrarPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            validarPresupuestoParaRegistrar(
                    presupuesto
            );

            con = ConnectionHelper.getConnection();

            if (con == null) {
                throw new SQLException(
                        "No se obtuvo una conexión para registrar el presupuesto."
                );
            }

            stmt = con.prepareCall(
                    SQL_REGISTRAR_PRESUPUESTO
            );

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

                throw new SQLException(
                        "La función de presupuesto devolvi? un identificador inválido."
                );
            }

            return idRequerimientoPresupuesto;

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "registrar el presupuesto",
                    "No se pudo registrar el presupuesto. "
                            + "Vuelva a seleccionar el archivo e intente nuevamente.",
                    e,
                    construirContextoPresupuesto(
                            presupuesto,
                            usuario
                    )
            );

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
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            if (idRequerimientoPresupuesto <= 0) {
                throw errorUsuario(
                        "Debe informar el presupuesto que desea quitar."
                );
            }

            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

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
            throw manejarErrorOperacion(
                    "quitar el presupuesto",
                    "No se pudo quitar el presupuesto. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto
                            + ", idRequerimiento="
                            + idRequerimientoCompra
                            + ", usuario=" + usuario
            );

        } finally {
            ConnectionHelper.cerrar(
                    stmt,
                    con
            );
        }
    }

    public boolean reactivarPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;

        try {
            if (idRequerimientoPresupuesto <= 0) {
                throw errorUsuario(
                        "Debe informar el presupuesto que desea reactivar."
                );
            }

            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

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
            throw manejarErrorOperacion(
                    "reactivar el presupuesto",
                    "No se pudo reactivar el presupuesto. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto
                            + ", idRequerimiento="
                            + idRequerimientoCompra
            );

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
            throw errorUsuario(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        if (presupuesto.getIdRequerimiento() == null
                || presupuesto
                .getIdRequerimiento()
                .intValue() <= 0) {

            throw errorUsuario(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (presupuesto.getIdPrestador() == null
                || presupuesto
                .getIdPrestador()
                .intValue() <= 0) {

            throw errorUsuario(
                    "Debe informar el prestador del presupuesto."
            );
        }

        if (presupuesto.getDlGroupId() == null
                || presupuesto
                .getDlGroupId()
                .longValue() <= 0L) {

            throw errorUsuario(
                    "No se pudo identificar correctamente el archivo del presupuesto. Vuelva a seleccionarlo."
            );
        }

        if (presupuesto.getDlFolderId() == null
                || presupuesto
                .getDlFolderId()
                .longValue() < 0L) {

            throw errorUsuario(
                    "No se pudo identificar correctamente la carpeta del presupuesto. Vuelva a seleccionar el archivo."
            );
        }

        if (presupuesto.getDlFileEntryId() == null
                || presupuesto
                .getDlFileEntryId()
                .longValue() <= 0L) {

            throw errorUsuario(
                    "No se pudo identificar correctamente el archivo del presupuesto. Vuelva a seleccionarlo."
            );
        }

        if (WebKeysCompras.isEmpty(
                presupuesto.getNombreOriginal()
        )) {
            throw errorUsuario(
                    "El archivo del presupuesto no tiene un nombre válido. Vuelva a seleccionarlo."
            );
        }

        if (WebKeysCompras.isEmpty(
                presupuesto.getNombrePersistido()
        )) {
            throw errorUsuario(
                    "No se pudo registrar correctamente el archivo del presupuesto. Vuelva a seleccionarlo."
            );
        }

        if (WebKeysCompras.isEmpty(
                presupuesto.getTitulo()
        )) {
            throw errorUsuario(
                    "El archivo del presupuesto no tiene un título válido. Vuelva a seleccionarlo."
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
                throw new SQLException(
                        "La confirmación del envío no devolvi? el estado final."
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
        Set<Throwable> visitados =
                new HashSet<Throwable>();

        while (actual != null
                && visitados.add(actual)) {

            if (actual instanceof SQLException) {
                return (SQLException) actual;
            }

            actual = actual.getCause();
        }

        return null;
    }

    private void validarTienePresupuestoActivoDelPrestador(
            int idRequerimientoCompra,
            int idPrestadorAdjudicado) throws Exception {

        if (idPrestadorAdjudicado <= 0) {
            throw errorUsuario(
                    "Debe seleccionar un prestador adjudicado válido."
            );
        }

        List<RequerimientoCompraPresupuesto> presupuestos =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPresupuestos(
                                idRequerimientoCompra
                        );

        if (presupuestos != null) {
            for (int i = 0;
                 i < presupuestos.size();
                 i++) {

                RequerimientoCompraPresupuesto presupuesto =
                        presupuestos.get(i);

                if (presupuesto == null
                        || !esPresupuestoActivoDeTercerizadora(
                        presupuesto
                )) {

                    continue;
                }

                Integer idPrestadorPresupuesto =
                        presupuesto.getIdPrestador();

                if (idPrestadorPresupuesto != null
                        && idPrestadorPresupuesto.intValue()
                        == idPrestadorAdjudicado) {

                    return;
                }
            }
        }

        throw errorUsuario(
                "Para cerrar la cotización, primero cargue un presupuesto activo del prestador adjudicado."
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

    private MensajeUsuarioException errorUsuario(
            String mensaje) {

        return new MensajeUsuarioException(
                mensaje
        );
    }

    private MensajeUsuarioException errorUsuario(
            String mensaje,
            Throwable causa) {

        return new MensajeUsuarioException(
                mensaje,
                causa
        );
    }

    /**
     * ?nico punto de salida para fallas técnicas de esta implementación.
     * Los detalles quedan en el log y hacia la interfaz solo se propagan
     * mensajes breves, accionables y sin información interna de PostgreSQL.
     */
    private Exception manejarErrorOperacion(
            String operacion,
            String mensajePredeterminado,
            Exception error,
            String contexto) {

        MensajeUsuarioException errorUsuario =
                buscarMensajeUsuarioException(
                        error
                );

        if (errorUsuario != null) {
            return errorUsuario;
        }

        SQLException sqlException =
                buscarSQLException(
                        error
                );

        registrarErrorTecnico(
                operacion,
                contexto,
                error,
                sqlException
        );

        if (sqlException != null) {
            String mensajeSql =
                    traducirSQLException(
                            sqlException
                    );

            if (!WebKeysCompras.isEmpty(mensajeSql)) {
                return errorUsuario(
                        mensajeSql,
                        error
                );
            }
        }

        return errorUsuario(
                mensajePredeterminado,
                error
        );
    }

    private MensajeUsuarioException buscarMensajeUsuarioException(
            Throwable error) {

        Throwable actual = error;
        Set<Throwable> visitados =
                new HashSet<Throwable>();

        while (actual != null
                && visitados.add(actual)) {

            if (actual instanceof MensajeUsuarioException) {
                return (MensajeUsuarioException) actual;
            }

            actual = actual.getCause();
        }

        return null;
    }

    private void registrarErrorTecnico(
            String operacion,
            String contexto,
            Throwable error,
            SQLException sqlException) {

        StringBuilder mensaje =
                new StringBuilder();

        mensaje.append(
                "Error técnico al "
        );
        mensaje.append(
                operacion
        );
        mensaje.append('.');

        if (!WebKeysCompras.isEmpty(contexto)) {
            mensaje.append(' ');
            mensaje.append(contexto);
        }

        if (sqlException != null) {
            mensaje.append(
                    ", SQLState="
            );
            mensaje.append(
                    sqlException.getSQLState()
            );
            mensaje.append(
                    ", errorCode="
            );
            mensaje.append(
                    sqlException.getErrorCode()
            );
        }

        _log.error(
                mensaje.toString(),
                error
        );

        if (sqlException != null
                && sqlException.getNextException() != null
                && sqlException.getNextException()
                != sqlException) {

            _log.error(
                    "Excepción SQL encadenada al "
                            + operacion
                            + ".",
                    sqlException.getNextException()
            );
        }
    }

    private String traducirSQLException(
            SQLException error) {

        String textoCompleto =
                obtenerTextoCompletoSQLException(
                        error
                );

        String clave =
                normalizarClave(
                        textoCompleto
                );

        if (clave.contains(
                "DEBE EXISTIR UN PRESUPUESTO ACTIVO DEL PRESTADOR ADJUDICADO"
        )) {
            return "Para cerrar la cotización, primero cargue un "
                    + "presupuesto activo del prestador adjudicado.";
        }

        if (clave.contains(
                "EL PRESTADOR ADJUDICADO NO FUE NOTIFICADO CORRECTAMENTE"
        )) {
            return "El prestador seleccionado no fue notificado correctamente "
                    + "para este requerimiento. Verifique el envío antes de adjudicarlo.";
        }

        if (clave.contains(
                "LA COTIZACION SOLO PUEDE GUARDARSE EN ESTADO A COTIZAR"
        )) {
            return "La cotización ya no puede modificarse porque el "
                    + "requerimiento no se encuentra en estado A COTIZAR.";
        }

        if (clave.contains(
                "NO EXISTE EL REQUERIMIENTO ACTIVO INFORMADO"
        )) {
            return "El requerimiento ya no est? disponible o fue dado de baja.";
        }

        if (clave.contains(
                "LA COTIZACION DEBE INFORMAR EXACTAMENTE TODOS LOS DETALLES ACTIVOS"
        )
                || clave.contains(
                "LA LISTA DE DETALLES FUE MANIPULADA O PERTENECE A OTRO REQUERIMIENTO"
        )
                || clave.contains(
                "LA CANTIDAD DE DETALLES Y PRECIOS NO COINCIDE"
        )) {
            return "Los detalles del requerimiento cambiaron mientras editaba "
                    + "la cotización. Actualice la pantalla y vuelva a intentarlo.";
        }

        if (clave.contains(
                "LA COTIZACION CONTIENE IDENTIFICADORES DE DETALLE INVALIDOS"
        )
                || clave.contains(
                "LA COTIZACION CONTIENE DETALLES DUPLICADOS"
        )) {
            return "La cotización contiene detalles inválidos o repetidos. "
                    + "Actualice la pantalla y vuelva a intentarlo.";
        }

        if (clave.contains(
                "LOS PRECIOS UNITARIOS DEBEN SER NULOS O MAYORES O IGUALES QUE CERO"
        )) {
            return "Los precios unitarios deben ser iguales o mayores que cero.";
        }

        if (clave.contains(
                "EL REQUERIMIENTO NO CONTIENE DETALLES ACTIVOS"
        )) {
            return "El requerimiento no tiene detalles activos para cotizar.";
        }

        if (clave.contains(
                "EL PRESTADOR ADJUDICADO DEBE SER MAYOR QUE CERO"
        )) {
            return "Debe seleccionar un prestador adjudicado válido.";
        }

        if (clave.contains(
                "NO SE PUDO ACTUALIZAR EL DETALLE"
        )) {
            return "No se pudo actualizar uno de los detalles. "
                    + "Actualice la pantalla y vuelva a intentarlo.";
        }

        String sqlState =
                error.getSQLState();

        if ("23505".equals(sqlState)) {
            return "Ya existe un registro con los mismos datos.";
        }

        if ("23503".equals(sqlState)) {
            return "No se pudo completar la operación porque uno de los "
                    + "datos relacionados ya no existe o est? siendo utilizado.";
        }

        if ("23502".equals(sqlState)) {
            return "Falta completar un dato obligatorio.";
        }

        if ("23514".equals(sqlState)) {
            return "Uno de los datos ingresados no cumple las condiciones permitidas.";
        }

        if ("22001".equals(sqlState)) {
            return "Uno de los textos ingresados supera la longitud permitida.";
        }

        if ("22003".equals(sqlState)) {
            return "Uno de los valores numéricos est? fuera del rango permitido.";
        }

        if ("22P02".equals(sqlState)) {
            return "Uno de los datos ingresados tiene un formato inválido.";
        }

        if ("22007".equals(sqlState)
                || "22008".equals(sqlState)) {
            return "Una de las fechas ingresadas no es válida.";
        }

        if ("40001".equals(sqlState)
                || "40P01".equals(sqlState)) {
            return "La información cambi? mientras se procesaba la operación. "
                    + "Actualice la pantalla y vuelva a intentarlo.";
        }

        if ("57014".equals(sqlState)) {
            return "La operación demor? más de lo esperado. Intente nuevamente.";
        }

        if (sqlState != null
                && sqlState.startsWith("08")) {
            return "No se pudo comunicar con la base de datos. "
                    + "Intente nuevamente en unos instantes.";
        }

        /*
         * PostgreSQL utiliza estados de clase P para excepciones generadas
         * desde funciones PL/pgSQL. Si el mensaje principal es funcional y
         * no contiene datos técnicos, se conserva sin exponer Where, Detail,
         * nombres de funciones ni líneas internas.
         */
        if (sqlState != null
                && sqlState.startsWith("P")) {

            String mensajeFuncion =
                    extraerMensajePrincipalPostgreSql(
                            error
                    );

            if (esMensajeSqlAptoParaUsuario(
                    mensajeFuncion
            )) {
                return asegurarPuntoFinal(
                        mensajeFuncion
                );
            }
        }

        return null;
    }

    private String extraerMensajePrincipalPostgreSql(
            SQLException error) {

        SQLException actual = error;
        Set<SQLException> visitadas =
                new HashSet<SQLException>();

        while (actual != null
                && visitadas.add(actual)) {

            String mensaje =
                    emptyToNull(
                            actual.getMessage()
                    );

            if (mensaje != null) {
                String[] lineas =
                        mensaje.split("[\\r\\n]+");

                for (int i = 0;
                     i < lineas.length;
                     i++) {

                    String linea =
                            emptyToNull(
                                    lineas[i]
                            );

                    if (linea == null) {
                        continue;
                    }

                    String claveLinea =
                            normalizarClave(
                                    linea
                            );

                    if (claveLinea.startsWith("WHERE:")
                            || claveLinea.startsWith("DETAIL:")
                            || claveLinea.startsWith("HINT:")
                            || claveLinea.startsWith("CONTEXT:")) {
                        continue;
                    }

                    if (claveLinea.startsWith("ERROR:")) {
                        linea = linea.substring(
                                linea.indexOf(':') + 1
                        ).trim();
                    }

                    if (!WebKeysCompras.isEmpty(linea)) {
                        return linea;
                    }
                }
            }

            actual = actual.getNextException();
        }

        return null;
    }

    private boolean esMensajeSqlAptoParaUsuario(
            String mensaje) {

        if (WebKeysCompras.isEmpty(mensaje)
                || mensaje.length() > 500) {
            return false;
        }

        String clave =
                normalizarClave(
                        mensaje
                );

        return !clave.contains("PL/PGSQL")
                && !clave.contains("PLPGSQL")
                && !clave.contains("ORG.POSTGRESQL")
                && !clave.contains("SQLSTATE")
                && !clave.contains("JDBC")
                && !clave.contains("RAISE")
                && !clave.contains("FUNCION ")
                && !clave.contains("FUNCTION ")
                && !clave.contains("LINEA ")
                && !clave.contains("LINE ")
                && !clave.contains("CONSTRAINT")
                && !clave.contains("SELECT ")
                && !clave.contains("INSERT ")
                && !clave.contains("UPDATE ")
                && !clave.contains("DELETE ")
                && !clave.contains("CALL ");
    }

    private String asegurarPuntoFinal(
            String mensaje) {

        String limpio =
                emptyToNull(
                        mensaje
                );

        if (limpio == null) {
            return null;
        }

        char ultimo =
                limpio.charAt(
                        limpio.length() - 1
                );

        if (ultimo == '.'
                || ultimo == '!'
                || ultimo == '?') {
            return limpio;
        }

        return limpio + ".";
    }

    private String obtenerTextoCompletoSQLException(
            SQLException error) {

        StringBuilder texto =
                new StringBuilder();

        SQLException actual = error;
        Set<SQLException> visitadas =
                new HashSet<SQLException>();

        while (actual != null
                && visitadas.add(actual)) {

            if (!WebKeysCompras.isEmpty(
                    actual.getMessage()
            )) {
                if (texto.length() > 0) {
                    texto.append(' ');
                }

                texto.append(
                        actual.getMessage()
                );
            }

            actual = actual.getNextException();
        }

        return texto.toString();
    }

    private int obtenerIdRequerimientoSeguro(
            RequerimientoCompra requerimiento) {

        return requerimiento != null
                ? requerimiento.getIdRequerimientoCompra()
                : 0;
    }

    private String construirContextoDetalle(
            RequerimientoCompraDetalle detalle,
            Integer idRequerimiento,
            String usuario) {

        if (detalle == null) {
            return "idRequerimiento=" + idRequerimiento
                    + ", detalle=null"
                    + ", usuario=" + usuario;
        }

        return "idDetalle=" + detalle.getId()
                + ", idRequerimiento=" + idRequerimiento
                + ", tipoItem=" + detalle.getTipoItemNormalizado()
                + ", idPrestacion=" + detalle.getIdPrestacion()
                + ", idTipoNomenclador=" + detalle.getIdTipoNomenclador()
                + ", codigoNomenclador=" + detalle.getCodigoNomenclador()
                + ", idMedicamento=" + detalle.getIdMedicamento()
                + ", troquel=" + detalle.getTroquel()
                + ", cantidad=" + detalle.getCantidad()
                + ", usuario=" + usuario;
    }

    private String construirContextoPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) {

        if (presupuesto == null) {
            return "presupuesto=null, usuario=" + usuario;
        }

        return "idRequerimiento="
                + presupuesto.getIdRequerimiento()
                + ", idPrestador="
                + presupuesto.getIdPrestador()
                + ", dlFileEntryId="
                + presupuesto.getDlFileEntryId()
                + ", usuario=" + usuario;
    }

}