package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.GuardadoCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.GestorOrdenMedicaDocumento;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceImpl;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Fuente canonica de reglas funcionales para alta, edicion, detalles,
 * estados, cotizacion y asociaciones documentales del requerimiento.
 *
 * No abre conexiones ni conoce JDBC. Toda persistencia se delega en
 * EditarRequerimientoCompraServiceImpl.
 */
public class EditarRequerimientoCompraHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    EditarRequerimientoCompraHelper.class
            );

    public static final int MAX_ORDENES_MEDICAS_POR_ALTA = 20;

    private static final Pattern DIACRITICOS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private final EditarRequerimientoCompraServiceImpl persistence =
            new EditarRequerimientoCompraServiceImpl();

    private final NotificarCotizacionPrestadorHelper notificacionHelper =
            new NotificarCotizacionPrestadorHelper();

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

        try {
            if (requerimiento == null
                    || requerimiento.getIdRequerimientoCompra() <= 0) {

                throw errorUsuario(
                        "Debe informar el requerimiento de compra que desea editar."
                );
            }

            RequerimientoCompra actual =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    requerimiento
                                            .getIdRequerimientoCompra()
                            );

            if (actual == null) {
                throw errorUsuario(
                        "El requerimiento que intenta editar ya no esta disponible."
                );
            }

            RequerimientoCompra requerimientoPersistir;

            if (actual.puedeEditarEstructura()) {
                prepararRequerimientoParaGuardar(
                        requerimiento
                );

                validarRequerimientoParaGuardar(
                        requerimiento
                );

                requerimientoPersistir =
                        requerimiento;

            } else if (actual.puedeEditarSurge()) {
                /*
                 * En ENVIADO A COTIZAR la unica modificacion estructural
                 * habilitada es SURGE. Se ignoran deliberadamente los demas
                 * valores recibidos por HTTP y se parte del snapshot canonico
                 * recuperado de persistencia.
                 */
                actual.setSurge(
                        requerimiento.getSurge()
                );

                requerimientoPersistir =
                        actual;

            } else {
                throw errorUsuario(
                        "El requerimiento ya no permite modificar SURGE "
                                + "ni su estructura desde el estado actual."
                );
            }

            int idGuardado =
                    persistence.guardarRequerimientoCompra(
                            requerimientoPersistir,
                            normalizarUsuario(usuario)
                    );

            if (idGuardado <= 0) {
                throw new IllegalStateException(
                        "El guardado no devolvio un identificador valido."
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
        }
    }

    public int guardarNuevoRequerimientoCompraConOrdenMedica(
            RequerimientoCompra requerimiento,
            OrdenMedicaValidada ordenMedica,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario) throws Exception {

        List<OrdenMedicaValidada> ordenesMedicas =
                new ArrayList<OrdenMedicaValidada>(1);

        ordenesMedicas.add(ordenMedica);

        return guardarNuevoRequerimientoCompraConOrdenesMedicas(
                requerimiento,
                ordenesMedicas,
                gestorDocumento,
                usuario
        );
    }

    public int guardarNuevoRequerimientoCompraConOrdenesMedicas(
            RequerimientoCompra requerimiento,
            List<OrdenMedicaValidada> ordenesMedicas,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario) throws Exception {

        EditarRequerimientoCompraServiceImpl.Transaccion transaccion = null;

        List<DocumentoComprasCreado> documentosCreados =
                new ArrayList<DocumentoComprasCreado>();

        try {
            prepararRequerimientoParaGuardar(requerimiento);
            validarRequerimientoParaGuardar(requerimiento);

            if (requerimiento.getIdRequerimientoCompra() > 0) {
                throw errorUsuario(
                        "La Orden medica obligatoria solo se registra "
                                + "durante el alta de un requerimiento nuevo."
                );
            }

            validarOrdenesMedicasParaAlta(ordenesMedicas);

            if (gestorDocumento == null) {
                throw new IllegalStateException(
                        "No se obtuvo el gestor documental de la Orden medica."
                );
            }

            transaccion = persistence.abrirTransaccion();

            int idRequerimiento =
                    transaccion.guardarRequerimientoCompra(
                            requerimiento,
                            normalizarUsuario(usuario)
                    );

            if (idRequerimiento <= 0) {
                throw new IllegalStateException(
                        "El alta no devolvio un identificador valido."
                );
            }

            for (int i = 0; i < ordenesMedicas.size(); i++) {
                OrdenMedicaValidada ordenMedica =
                        ordenesMedicas.get(i);

                DocumentoComprasCreado documento =
                        gestorDocumento.crearOrdenMedica(
                                idRequerimiento,
                                ordenMedica
                        );

                documentosCreados.add(documento);

                int idDocumento =
                        transaccion.registrarOrdenMedica(
                                idRequerimiento,
                                ordenMedica,
                                documento,
                                normalizarUsuario(usuario)
                        );

                if (idDocumento <= 0) {
                    throw new IllegalStateException(
                            "El registro de la Orden medica no devolvio "
                                    + "un identificador valido."
                    );
                }
            }

            transaccion.commit();
            return idRequerimiento;

        } catch (Exception e) {
            boolean rollbackConfirmado = transaccion == null;

            if (transaccion != null) {
                try {
                    transaccion.rollback();
                    rollbackConfirmado = true;
                } catch (Exception rollbackError) {
                    _log.error(
                            "No se pudo confirmar el rollback del alta "
                                    + "con Orden medica. No se eliminaran "
                                    + "documentos ante un estado transaccional ambiguo.",
                            rollbackError
                    );
                }
            }

            if (rollbackConfirmado
                    && gestorDocumento != null
                    && !documentosCreados.isEmpty()) {

                compensarOrdenesMedicasCreadas(
                        documentosCreados,
                        gestorDocumento
                );
            }

            throw manejarErrorOperacion(
                    "guardar el requerimiento nuevo con Orden medica",
                    "No se pudo guardar el requerimiento con su Orden medica. "
                            + "Vuelva a seleccionar la imagen e intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + obtenerIdRequerimientoSeguro(requerimiento)
                            + ", usuario=" + usuario
                            + ", cantidadOrdenesMedicas="
                            + (ordenesMedicas != null
                            ? ordenesMedicas.size()
                            : 0)
            );

        } finally {
            if (transaccion != null) {
                transaccion.cerrar();
            }
        }
    }

    public void validarOrdenesMedicasParaAlta(
            List<OrdenMedicaValidada> ordenesMedicas) throws Exception {

        if (ordenesMedicas == null || ordenesMedicas.isEmpty()) {
            throw errorUsuario(
                    "Debe seleccionar la Orden medica e informar su fecha."
            );
        }

        if (ordenesMedicas.size() > MAX_ORDENES_MEDICAS_POR_ALTA) {
            throw errorUsuario(
                    "Se pueden registrar hasta "
                            + MAX_ORDENES_MEDICAS_POR_ALTA
                            + " Ordenes medicas por requerimiento."
            );
        }

        for (int i = 0; i < ordenesMedicas.size(); i++) {
            OrdenMedicaValidada ordenMedica = ordenesMedicas.get(i);

            if (ordenMedica == null
                    || ordenMedica.getFechaDocumento() == null) {

                throw errorUsuario(
                        "Debe seleccionar la Orden medica e informar su fecha."
                );
            }
        }
    }

    private void compensarOrdenesMedicasCreadas(
            List<DocumentoComprasCreado> documentosCreados,
            GestorOrdenMedicaDocumento gestorDocumento) {

        for (int i = documentosCreados.size() - 1; i >= 0; i--) {
            DocumentoComprasCreado documento = documentosCreados.get(i);

            if (documento == null) {
                continue;
            }

            try {
                gestorDocumento.eliminarDocumento(documento);
            } catch (Exception cleanupError) {
                _log.error(
                        "No se pudo compensar una Orden medica creada "
                                + "despues del rollback. fileEntryId="
                                + documento.getFileEntryId(),
                        cleanupError
                );
            }
        }
    }

    public int guardarDetalle(
            RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {

        Integer idRequerimiento = null;

        try {
            if (detalle == null) {
                throw errorUsuario(
                        "Debe informar el detalle del requerimiento."
                );
            }

            idRequerimiento = getIdRequerimientoDetalle(detalle);

            RequerimientoCompra requerimiento =
                    validarRequerimientoDetalle(idRequerimiento);

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

            normalizarDetalleNuevo(detalle);

            validarDetalleParaGuardar(
                    requerimiento,
                    detalle
            );

            int idDetalleGuardado =
                    persistence.guardarDetalle(
                            detalle,
                            normalizarUsuario(usuario)
                    );

            if (idDetalleGuardado <= 0) {
                throw new IllegalStateException(
                        "El guardado del detalle no devolvio un ID valido."
                );
            }

            return idDetalleGuardado;

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "guardar el detalle del requerimiento",
                    "No se pudo guardar el detalle del requerimiento. "
                            + "Revise la informacion e intente nuevamente.",
                    e,
                    construirContextoDetalle(
                            detalle,
                            idRequerimiento,
                            usuario
                    )
            );
        }
    }

    /**
     * Contrato legacy: conserva la firma historica para callers existentes.
     */
    public void borrarDetalle(
            int idDetalle,
            String usuario) throws Exception {

        try {
            if (idDetalle <= 0) {
                throw errorUsuario(
                        "Debe informar el detalle que desea quitar."
                );
            }

            persistence.borrarDetalle(
                    idDetalle,
                    normalizarUsuario(usuario)
            );
        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "quitar el detalle del requerimiento",
                    "No se pudo quitar el detalle del requerimiento. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idDetalle=" + idDetalle
                            + ", usuario=" + usuario
            );
        }
    }

    /**
     * Contrato canonico utilizado por los Actions: valida estado y pertenencia
     * antes de ejecutar el borrado persistente.
     */
    public void borrarDetalle(
            int idRequerimientoCompra,
            int idDetalle,
            String usuario) throws Exception {

        try {
            RequerimientoCompra requerimiento =
                    validarRequerimientoDetalle(
                            Integer.valueOf(idRequerimientoCompra)
                    );

            obtenerDetallePersistido(
                    requerimiento,
                    idDetalle
            );

            persistence.borrarDetalle(
                    idDetalle,
                    normalizarUsuario(usuario)
            );
        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "quitar el detalle del requerimiento",
                    "No se pudo quitar el detalle del requerimiento. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", idDetalle=" + idDetalle
                            + ", usuario=" + usuario
            );
        }
    }

    public void borrarRequerimientoCompra(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra que desea eliminar."
                );
            }

            persistence.borrarRequerimientoCompra(
                    idRequerimientoCompra,
                    normalizarUsuario(usuario)
            );
        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "eliminar el requerimiento de compra",
                    "No se pudo eliminar el requerimiento de compra. "
                            + "Actualice la pantalla e intente nuevamente.",
                    e,
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", usuario=" + usuario
            );
        }
    }

    public void cambiarEstado(
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            if (!WebKeysCompras.esEstadoValido(idEstadoNuevo)) {
                throw errorUsuario(
                        "El estado seleccionado no es valido."
                );
            }

            RequerimientoCompra requerimientoActual =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    idRequerimientoCompra
                            );

            if (requerimientoActual == null) {
                throw errorUsuario(
                        "El requerimiento ya no esta disponible."
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

            persistence.cambiarEstado(
                    idRequerimientoCompra,
                    idEstadoNuevo,
                    normalizarUsuario(usuario)
            );

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
                    notificacionHelper.notificarPrestadores(
                            requerimiento.getIdRequerimientoCompra(),
                            usuario,
                            companyId
                    );

            if (resultado == null) {
                throw new IllegalStateException(
                        "El proceso de notificacion no devolvio resultado."
                );
            }

            int estadoFinal =
                    persistence.confirmarEnvioACotizar(
                            idRequerimientoCompra,
                            normalizarUsuario(usuario)
                    );

            if (estadoFinal != WebKeysCompras.ESTADO_PENDIENTE
                    && estadoFinal != WebKeysCompras.ESTADO_A_COTIZAR) {

                throw new IllegalStateException(
                        "Estado inesperado al confirmar el envio: "
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
                        "El requerimiento ya no esta disponible."
                );
            }

            if (!requerimiento.puedeReintentarNotificaciones()) {
                throw errorUsuario(
                        "Las notificaciones solo pueden reenviarse mientras "
                                + "el requerimiento esta en estado ENVIADO A COTIZAR."
                );
            }

            if (!BusquedaRequerimientoCompraServiceUtil
                    .hayPrestadoresPendientesNotificacion(
                            idRequerimientoCompra
                    )) {

                return new NotificacionCotizacionResultado();
            }

            return notificacionHelper.notificarPrestadores(
                    idRequerimientoCompra,
                    usuario,
                    companyId
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "reenviar las notificaciones de cotizacion",
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

    public int registrarPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) throws Exception {

        try {
            validarPresupuestoParaRegistrar(presupuesto);

            int id =
                    persistence.registrarPresupuesto(
                            presupuesto,
                            normalizarUsuario(usuario)
                    );

            if (id <= 0) {
                throw new IllegalStateException(
                        "El registro del presupuesto no devolvio un ID valido."
                );
            }

            return id;

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
        }
    }

    public boolean darDeBajaPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

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

            return persistence.darDeBajaPresupuesto(
                    idRequerimientoPresupuesto,
                    idRequerimientoCompra,
                    normalizarUsuario(usuario)
            );

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
        }
    }

    public boolean reactivarPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

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

            return persistence.reactivarPresupuesto(
                    idRequerimientoPresupuesto,
                    idRequerimientoCompra
            );

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
        }
    }

    public void prepararRequerimientoParaGuardar(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null) {
            return;
        }

        preservarTercerizadoraExistenteSiNoCambioAfiliado(
                requerimiento
        );

        Integer idSector = requerimiento.getIdSector();

        if (idSector != null && idSector.intValue() > 0) {
            RequerimientoCompraSector sector =
                    BusquedaRequerimientoCompraServiceUtil
                            .getSector(idSector.intValue());

            if (sector == null || sector.getIdSector() <= 0) {
                throw errorUsuario(
                        "El sector seleccionado ya no existe o no esta disponible."
                );
            }

            requerimiento.setSectorDescripcion(
                    sector.getDescripcion()
            );
            requerimiento.setRequiereAfiliado(
                    sector.isRequiereAfiliado()
            );

            if (!sector.isRequiereAfiliado()) {
                aplicarReglaSectorSinAfiliado(requerimiento);
                return;
            }
        }

        if (requerimiento.tieneAfiliadoInformado()) {
            cargarSnapshotAfiliado(requerimiento);
        }

        int cargoTercerizadora =
                requerimiento.getCargoTercerizadora() != null
                        ? requerimiento.getCargoTercerizadora().intValue()
                        : 0;

        requerimiento.setRecupero(
                cargoTercerizadora > 0
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

        if (!requerimiento.isRequiereAfiliado()) {
            aplicarReglaSectorSinAfiliado(requerimiento);
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
                        ? requerimiento.getCargoTercerizadora().intValue()
                        : 0;

        if (cargoOspim + cargoTercerizadora != 100) {
            throw errorUsuario(
                    "La suma de los cargos de OSPIM y la tercerizadora "
                            + "debe ser exactamente 100 %."
            );
        }

        requerimiento.setRecupero(
                cargoTercerizadora > 0
        );

        if (requerimiento.isRequiereAfiliado()
                && cargoTercerizadora > 0
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
``````


        if (requerimiento.getAfiliadoInt() == null
                || requerimiento.getAfiliadoInt().intValue() < 0) {

            throw errorUsuario(
                    "Debe informar el integrante del grupo familiar."
            );
        }
    }
}

private void cargarSnapshotAfiliado(
        RequerimientoCompra requerimiento) throws Exception {

    List<Afiliado> afiliados =
            BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
                    requerimiento.getAfiliadoCuilTitular(),
                    requerimiento.getAfiliadoIntString(),
                    null,
                    null,
                    0,
                    null,
                    null,
                    WebKeysGlobal.ID_DEFAULT_ENTIDAD,
                    0,
                    0,
                    new BigDecimal(0)
            );

    if (afiliados == null || afiliados.size() != 1) {
        throw errorUsuario(
                "No se pudo obtener un unico afiliado para guardar el requerimiento."
        );
    }

    Afiliado afiliado = afiliados.get(0);

    requerimiento.setAfiliadoIdOspim(afiliado.getId_ospim());
    requerimiento.setAfiliadoNombre(afiliado.getNombre());
    requerimiento.setAfiliadoApellido(afiliado.getApellido());
    requerimiento.setAfiliadoDocumentoTipo(afiliado.getDocumento_tipo());
    requerimiento.setAfiliadoDocumentoNro(afiliado.getDocu_numero());
    requerimiento.setAfiliadoEmail(afiliado.getEmail());

    List<Domicilio> domicilios =
            BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(
                    requerimiento.getAfiliadoCuilTitular(),
                    requerimiento.getAfiliadoInt().intValue()
            );

    if ((domicilios == null || domicilios.isEmpty())
            && requerimiento.getAfiliadoInt().intValue() != 0) {

        domicilios =
                BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(
                        requerimiento.getAfiliadoCuilTitular(),
                        0
                );
    }

    if (domicilios == null || domicilios.isEmpty()) {
        return;
    }

    Domicilio domicilio = domicilios.get(0);

    requerimiento.setAfiliadoDireccion(
            formatearDireccion(domicilio)
    );
    requerimiento.setAfiliadoLocalidad(
            domicilio.getLocalidadAsString()
    );
    requerimiento.setAfiliadoProvincia(
            domicilio.getProvinciaAsString()
    );
    requerimiento.setAfiliadoCelular(
            formatearTelefono(
                    domicilio.getCod_area_celular(),
                    domicilio.getCelular()
            )
    );
    requerimiento.setAfiliadoTelefono(
            formatearTelefono(
                    domicilio.getCod_area_telefono(),
                    domicilio.getTelefono()
            )
    );
}

private String formatearDireccion(Domicilio domicilio) {
    if (domicilio == null) {
        return null;
    }

    StringBuilder direccion = new StringBuilder();
    agregarParte(direccion, domicilio.getCalle());
    agregarParte(direccion, domicilio.getNumero());
    agregarParte(direccion, prefijar("Piso", domicilio.getPiso()));
    agregarParte(direccion, prefijar("Dto.", domicilio.getDepto()));
    agregarParte(direccion, prefijar("Of.", domicilio.getOficina()));
    return WebKeysCompras.trimToNull(direccion.toString());
}

private String formatearTelefono(
        String codigoArea,
        String numero) {

    StringBuilder telefono = new StringBuilder();
    agregarParte(telefono, codigoArea);
    agregarParte(telefono, numero);
    return WebKeysCompras.trimToNull(telefono.toString());
}

private String prefijar(
        String prefijo,
        String valor) {

    String normalizado = WebKeysCompras.trimToNull(valor);
    return normalizado != null
            ? prefijo + " " + normalizado
            : null;
}

private void agregarParte(
        StringBuilder destino,
        String valor) {

    String normalizado = WebKeysCompras.trimToNull(valor);

    if (normalizado == null) {
        return;
    }

    if (destino.length() > 0) {
        destino.append(' ');
    }

    destino.append(normalizado);
}

private void aplicarReglaSectorSinAfiliado(
        RequerimientoCompra requerimiento) {

    if (requerimiento == null) {
        return;
    }

    requerimiento.setAfiliadoCuilTitular(null);
    requerimiento.setAfiliadoInt(null);
    requerimiento.setAfiliadoIdOspim((Integer) null);

    if (requerimiento.getIdRequerimientoCompra() <= 0) {
        requerimiento.setIdTercerizadora(null);
    }

    requerimiento.setCargoOspim(Integer.valueOf(100));
    requerimiento.setCargoTercerizadora(Integer.valueOf(0));
    requerimiento.setRecupero(false);
}

private void preservarTercerizadoraExistenteSiNoCambioAfiliado(
        RequerimientoCompra requerimiento) throws Exception {

    if (requerimiento == null
            || requerimiento.getIdRequerimientoCompra() <= 0) {
        return;
    }

    RequerimientoCompra existente =
            BusquedaRequerimientoCompraServiceUtil
                    .getRequerimientoCompra(
                            requerimiento.getIdRequerimientoCompra()
                    );

    if (existente == null) {
        return;
    }

    boolean mismoAfiliado =
            mismoTexto(
                    existente.getAfiliadoCuilTitular(),
                    requerimiento.getAfiliadoCuilTitular()
            )
                    && mismoInteger(
                    existente.getAfiliadoInt(),
                    requerimiento.getAfiliadoInt()
            );

    if (mismoAfiliado
            && !WebKeysCompras.isEmpty(
            existente.getIdTercerizadora()
    )) {
        requerimiento.setIdTercerizadora(
                existente.getIdTercerizadora().trim().toUpperCase()
        );
    }
}

private boolean mismoTexto(String a, String b) {
    String aa = a != null ? a.trim() : "";
    String bb = b != null ? b.trim() : "";
    return aa.equalsIgnoreCase(bb);
}

private boolean mismoInteger(Integer a, Integer b) {
    if (a == null && b == null) {
        return true;
    }

    return a != null
            && b != null
            && a.intValue() == b.intValue();
}

private void validarDetalleParaGuardar(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle detalle) throws Exception {

    if (detalle == null) {
        throw errorUsuario(
                "Debe informar el detalle del requerimiento."
        );
    }

    Integer idRequerimiento = getIdRequerimientoDetalle(detalle);

    if (idRequerimiento == null
            || idRequerimiento.intValue() <= 0) {

        throw errorUsuario(
                "Primero debe guardar los datos generales del requerimiento."
        );
    }

    if (detalle.getCantidad() == null) {
        detalle.setCantidad(Integer.valueOf(1));
    }

    if (detalle.getCantidad().intValue() <= 0) {
        throw errorUsuario(
                "La cantidad debe ser mayor que cero."
        );
    }

    String tipoItem = detalle.getTipoItemNormalizado();

    if (!RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR.equals(tipoItem)
            && !RequerimientoCompraDetalle.TIPO_ITEM_MEDICAMENTO.equals(tipoItem)
            && !RequerimientoCompraDetalle.TIPO_ITEM_OBSERVACION.equals(tipoItem)) {

        throw errorUsuario(
                "El tipo de item seleccionado no es valido."
        );
    }

    if (RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR.equals(tipoItem)) {
        validarDetalleNomencladorParaGuardar(
                requerimiento,
                detalle
        );
    } else if (RequerimientoCompraDetalle.TIPO_ITEM_MEDICAMENTO.equals(tipoItem)) {
        validarDetalleMedicamentoParaGuardar(detalle);
    } else {
        validarDetalleObservacionParaGuardar(detalle);
    }

    if (detalle.getPrecioUnitarioEstimado() != null
            && detalle.getPrecioUnitarioEstimado()
            .compareTo(BigDecimal.ZERO) < 0) {

        throw errorUsuario(
                "El precio unitario no puede ser negativo."
        );
    }

    if (detalle.getPrecioTotalEstimadoInformado() != null
            && detalle.getPrecioTotalEstimadoInformado()
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

    if (idTipoNomencladorCanonico <= 0
            || idTipoNomencladorCanonico
            != detalle.getIdTipoNomenclador().intValue()) {

        throw errorUsuario(
                "La prestacion seleccionada no corresponde al "
                        + "tipo de nomenclador actual. Vuelva a seleccionarla."
        );
    }

    String sector =
            WebKeysCompras.normalizarSectorCompra(
                    requerimiento != null
                            ? requerimiento.getSectorDescripcion()
                            : null
            );

    if (WebKeysCompras.isEmpty(sector)) {
        throw errorUsuario(
                "No se pudo determinar el sector del requerimiento."
        );
    }

    if (!WebKeysCompras.esNomencladorValidoParaSectorCompras(
            sector,
            idTipoNomencladorCanonico,
            nomenclador.getMarcaReintegroLiquidacion(),
            nomenclador.getCodigo()
    )) {
        throw errorUsuario(
                mensajeNomencladorInvalido(sector)
        );
    }

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

    detalle.setCodigoNomenclador(
            emptyToNull(nomenclador.getCodigo())
    );
    detalle.setDescripcionNomenclador(
            emptyToNull(nomenclador.getDescripcion())
    );
    detalle.setCodigoItem(
            detalle.getCodigoNomenclador()
    );
    detalle.setDescripcionItem(
            detalle.getDescripcionNomenclador()
    );
}

private String mensajeNomencladorInvalido(String sector) {
    if ("FARMACIA".equals(sector)) {
        return "Para Farmacia debe seleccionar una prestacion "
                + "del nomenclador tipo 9.";
    }

    if ("DISCAPACIDAD".equals(sector)) {
        return "Para Discapacidad debe seleccionar una prestacion "
                + "con marca ReinLiq 6 o el codigo 431003.";
    }

    if ("ODONTOLOGIA".equals(sector)) {
        return "Para Odontologia debe seleccionar una prestacion "
                + "del nomenclador tipo 1.";
    }

    if ("PRESTACIONES MEDICAS".equals(sector)) {
        return "Para PRESTACIONES MEDICAS debe seleccionar una "
                + "prestacion de nomenclador tipo 2, 3, 4, 6 o 10.";
    }

    return "La prestacion seleccionada no corresponde "
            + "al sector del requerimiento.";
}

private void validarDetalleMedicamentoParaGuardar(
        RequerimientoCompraDetalle detalle) throws Exception {

    if (detalle.getIdInt() <= 0) {
        throw errorUsuario(
                "No se pueden crear nuevos detalles de tipo MEDICAMENTO en Compras."
        );
    }

    if (detalle.getIdMedicamento() == null
            || detalle.getIdMedicamento().intValue() <= 0
            || WebKeysCompras.isEmpty(
            detalle.getNombreMedicamento()
    )) {

        throw errorUsuario(
                "El detalle historico de medicamento no conserva "
                        + "una referencia valida."
        );
    }

    if (detalle.getIdPrestacion() != null
            || detalle.getIdTipoNomenclador() != null
            || !WebKeysCompras.isEmpty(detalle.getCodigoNomenclador())
            || !WebKeysCompras.isEmpty(detalle.getDescripcionNomenclador())) {

        throw errorUsuario(
                "El detalle historico de medicamento contiene "
                        + "datos tecnicos incompatibles."
        );
    }
}

private void validarDetalleObservacionParaGuardar(
        RequerimientoCompraDetalle detalle) throws Exception {

    if (WebKeysCompras.isEmpty(detalle.getObservaciones())) {
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
                "Un detalle de Observacion no puede contener "
                        + "datos de codigo o medicamento."
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
                "Los detalles ya no pueden modificarse porque el "
                        + "requerimiento no se encuentra PENDIENTE."
        );
    }

    if (requerimiento.getSectorId() == null
            || requerimiento.getSectorId().intValue() <= 0) {

        throw errorUsuario(
                "El requerimiento no tiene un sector valido."
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

    if (detallePersistido != null
            && detallePersistido.esMedicamento()) {

        String tipoRecibido = detalle.getTipoItemNormalizado();

        if (!WebKeysCompras.isEmpty(tipoRecibido)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_MEDICAMENTO
                .equals(tipoRecibido)) {

            throw errorUsuario(
                    "El detalle historico de medicamento no puede "
                            + "convertirse directamente a otro tipo."
            );
        }

        detalle.setTipoItem(
                RequerimientoCompraDetalle.TIPO_ITEM_MEDICAMENTO
        );
        detalle.setIdPrestacion(null);
        detalle.setIdTipoNomenclador(null);
        detalle.setCodigoNomenclador(null);
        detalle.setDescripcionNomenclador(null);
        detalle.setIdMedicamento(detallePersistido.getIdMedicamento());
        detalle.setTroquel(detallePersistido.getTroquel());
        detalle.setNombreMedicamento(detallePersistido.getNombreMedicamento());
        detalle.setCodigoItem(detallePersistido.getCodigoItemVisible());
        detalle.setDescripcionItem(detallePersistido.getDescripcionItemVisible());
        return;
    }

    boolean sectorObservacion =
            WebKeysCompras.esSectorDetalleObservacionCompras(
                    requerimiento != null
                            ? requerimiento.getSectorDescripcion()
                            : null
            );

    if (sectorObservacion) {
        if (detallePersistido != null
                && !detallePersistido.esObservacion()) {

            throw errorUsuario(
                    "El detalle existente no corresponde al sector "
                            + "seleccionado. Debe quitarlo antes de cambiar el sector."
            );
        }

        String tipoRecibido = detalle.getTipoItemNormalizado();

        if (!WebKeysCompras.isEmpty(tipoRecibido)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_OBSERVACION
                .equals(tipoRecibido)) {

            throw errorUsuario(
                    "El sector seleccionado requiere un detalle de OBSERVACION."
            );
        }

        detalle.setTipoItem(
                RequerimientoCompraDetalle.TIPO_ITEM_OBSERVACION
        );
        limpiarReferenciaTecnica(detalle);
        detalle.setCodigoItem(null);
        detalle.setDescripcionItem(null);
        return;
    }

    Integer filtroTipoNomenclador =
            WebKeysCompras.getFiltroTipoNomencladorCompras(
                    requerimiento != null
                            ? requerimiento.getSectorDescripcion()
                            : null
            );

    if (filtroTipoNomenclador == null) {
        throw errorUsuario(
                "El sector seleccionado no tiene configurado "
                        + "un nomenclador para Compras."
        );
``````

        }

        if (detallePersistido != null
                && detallePersistido.esObservacion()) {

            throw errorUsuario(
                    "El detalle existente no corresponde al sector "
                            + "seleccionado. Debe quitarlo antes de cambiar el sector."
            );
        }

        String tipoRecibido = detalle.getTipoItemNormalizado();

        if (!WebKeysCompras.isEmpty(tipoRecibido)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_NOMENCLADOR
                .equals(tipoRecibido)) {

            throw errorUsuario(
                    "Los detalles nuevos de Compras deben utilizar NOMENCLADOR."
            );
        }

        detalle.setTipoItem(
                RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR
        );
        detalle.setIdMedicamento(null);
        detalle.setTroquel(null);
        detalle.setNombreMedicamento(null);
        detalle.setCodigoItem(detalle.getCodigoNomenclador());
        detalle.setDescripcionItem(detalle.getDescripcionNomenclador());
    }

    private void limpiarReferenciaTecnica(
            RequerimientoCompraDetalle detalle) {

        detalle.setIdPrestacion(null);
        detalle.setIdTipoNomenclador(null);
        detalle.setCodigoNomenclador(null);
        detalle.setDescripcionNomenclador(null);
        detalle.setIdMedicamento(null);
        detalle.setTroquel(null);
        detalle.setNombreMedicamento(null);
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
            for (int i = 0; i < detalles.size(); i++) {
                RequerimientoCompraDetalle persistido = detalles.get(i);

                if (persistido != null
                        && persistido.getIdInt() == idDetalle
                        && persistido.getIdRequerimientoCompra()
                        == requerimiento.getIdRequerimientoCompra()) {

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
                .getRequerimientoCompra(idRequerimiento);
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

    public String normalizarTextoCarga(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() == 0) {
            return null;
        }

        String normalizado =
                Normalizer.normalize(
                        value,
                        Normalizer.Form.NFD
                );

        normalizado =
                DIACRITICOS.matcher(normalizado).replaceAll("");

        return normalizado.toUpperCase(Locale.ROOT).trim();
    }

    private void normalizarDetalleNuevo(
            RequerimientoCompraDetalle detalle) {

        if (detalle == null || detalle.getIdInt() > 0) {
            return;
        }

        detalle.setObservaciones(
                normalizarTextoCarga(
                        detalle.getObservaciones()
                )
        );
    }

    private void validarTextoTecnico(
            String campo,
            String recibido,
            String canonico) throws Exception {

        String recibidoNormalizado = normalizarTextoTecnico(recibido);
        String canonicoNormalizado = normalizarTextoTecnico(canonico);

        if (recibidoNormalizado == null
                || canonicoNormalizado == null
                || !recibidoNormalizado.equalsIgnoreCase(
                canonicoNormalizado
        )) {
            throw errorUsuario(
                    "El " + campo
                            + " cambio o ya no coincide con la informacion actual. "
                            + "Vuelva a seleccionarlo."
            );
        }
    }

    private String normalizarTextoTecnico(String value) {
        String result = emptyToNull(value);
        return result == null
                ? null
                : result.replaceAll("\\s+", " ");
    }

    private RequerimientoCompra validarRequerimientoParaEnviarACotizar(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw errorUsuario(
                    "Debe informar el requerimiento de compra."
            );
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            throw errorUsuario(
                    "No se encontro el requerimiento de compra informado."
            );
        }

        if (!requerimiento.puedeEnviarACotizar()) {
            throw errorUsuario(
                    "El requerimiento solo puede enviarse a cotizar "
                            + "mientras esta PENDIENTE."
            );
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {
            throw errorUsuario(
                    "Debe seleccionar el sector antes de enviar "
                            + "el requerimiento a cotizar."
            );
        }

        if (!requerimiento.tieneDetalles()) {
            throw errorUsuario(
                    "Debe agregar al menos un detalle antes de enviar "
                            + "el requerimiento a cotizar."
            );
        }

        if (requerimiento.isRequiereAfiliado()
                && !requerimiento.tieneAfiliadoInformado()) {
            throw errorUsuario(
                    "Debe completar los datos del afiliado antes de enviar "
                            + "el requerimiento a cotizar."
            );
        }

        return requerimiento;
    }

    private GuardadoCotizacionResultado guardarCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        Integer idPrestadorAdjudicado = null;

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            validarDetallesCotizacionRecibidos(detalles);

            idPrestadorAdjudicado =
                    obtenerPrestadorAdjudicadoUnico(detalles);

            Integer[] idsDetalle = new Integer[detalles.size()];
            BigDecimal[] preciosUnitarios = new BigDecimal[detalles.size()];

            for (int i = 0; i < detalles.size(); i++) {
                RequerimientoCompraDetalle detalle = detalles.get(i);

                idsDetalle[i] = Integer.valueOf(detalle.getIdInt());
                preciosUnitarios[i] =
                        detalle.getPrecioUnitarioEstimado() != null
                                ? WebKeysCompras.normalizarImporte(
                                detalle.getPrecioUnitarioEstimado()
                        )
                                : null;
            }

            if (debeValidarPresupuestoParaCotizado(
                    idPrestadorAdjudicado,
                    preciosUnitarios
            )) {
                validarTienePresupuestoActivoDelPrestador(
                        idRequerimientoCompra,
                        idPrestadorAdjudicado.intValue()
                );
            }

            int estadoFinal =
                    persistence.guardarCotizacion(
                            idRequerimientoCompra,
                            idsDetalle,
                            preciosUnitarios,
                            idPrestadorAdjudicado,
                            normalizarUsuario(usuario)
                    );

            if (estadoFinal != WebKeysCompras.ESTADO_A_COTIZAR
                    && estadoFinal != WebKeysCompras.ESTADO_COTIZADO) {

                throw new IllegalStateException(
                        "La cotizacion devolvio un estado invalido: "
                                + estadoFinal
                );
            }

            return new GuardadoCotizacionResultado(
                    estadoFinal == WebKeysCompras.ESTADO_COTIZADO,
                    estadoFinal
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "guardar la cotizacion",
                    "No se pudo guardar la cotizacion. "
                            + "Revise los datos e intente nuevamente.",
                    e,
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", idPrestadorAdjudicado="
                            + idPrestadorAdjudicado
                            + ", cantidadDetalles="
                            + (detalles != null ? detalles.size() : 0)
                            + ", usuario=" + usuario
            );
        }
    }

    protected void validarDetallesCotizacionRecibidos(
            List<RequerimientoCompraDetalle> detalles) throws Exception {

        if (detalles == null || detalles.isEmpty()) {
            throw errorUsuario(
                    "La cotizacion no contiene detalles para guardar."
            );
        }

        Set<Integer> idsRecibidos = new HashSet<Integer>();

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);

            if (detalle == null || detalle.getIdInt() <= 0) {
                throw errorUsuario(
                        "Los detalles recibidos no coinciden con el requerimiento. "
                                + "Actualice la pantalla y vuelva a intentarlo."
                );
            }

            Integer idDetalle = Integer.valueOf(detalle.getIdInt());

            if (!idsRecibidos.add(idDetalle)) {
                throw errorUsuario(
                        "La cotizacion contiene un detalle repetido. "
                                + "Actualice la pantalla y vuelva a intentarlo."
                );
            }

            if (detalle.getPrecioUnitarioEstimado() != null
                    && detalle.getPrecioUnitarioEstimado()
                    .compareTo(BigDecimal.ZERO) < 0) {

                throw errorUsuario(
                        "El precio unitario no puede ser negativo."
                );
            }
        }

        obtenerPrestadorAdjudicadoUnico(detalles);
    }

    protected Integer obtenerPrestadorAdjudicadoUnico(
            List<RequerimientoCompraDetalle> detalles) throws Exception {

        Integer idPrestadorAdjudicado = null;

        for (int i = 0;
             detalles != null && i < detalles.size();
             i++) {

            RequerimientoCompraDetalle detalle = detalles.get(i);

            if (detalle == null
                    || !detalle.tienePrestadorAdjudicado()) {
                continue;
            }

            Integer idPrestadorDetalle = detalle.getIdPrestador();

            if (idPrestadorAdjudicado == null) {
                idPrestadorAdjudicado = idPrestadorDetalle;
            } else if (idPrestadorAdjudicado.intValue()
                    != idPrestadorDetalle.intValue()) {

                throw errorUsuario(
                        "Debe seleccionar el mismo prestador adjudicado "
                                + "para todos los detalles."
                );
            }
        }

        return idPrestadorAdjudicado;
    }

    private void validarTienePresupuestoActivoDelPrestador(
            int idRequerimientoCompra,
            int idPrestadorAdjudicado) throws Exception {

        if (idPrestadorAdjudicado <= 0) {
            throw errorUsuario(
                    "Debe seleccionar un prestador adjudicado valido."
            );
        }

        List<RequerimientoCompraPresupuesto> presupuestos =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPresupuestos(idRequerimientoCompra);

        for (int i = 0;
             presupuestos != null && i < presupuestos.size();
             i++) {

            RequerimientoCompraPresupuesto presupuesto = presupuestos.get(i);

            if (!esPresupuestoActivoDeTercerizadora(presupuesto)) {
                continue;
            }

            Integer idPrestadorPresupuesto = presupuesto.getIdPrestador();

            if (idPrestadorPresupuesto != null
                    && idPrestadorPresupuesto.intValue()
                    == idPrestadorAdjudicado) {
                return;
            }
        }

        throw errorUsuario(
                "Para cerrar la cotizacion, primero cargue un presupuesto "
                        + "activo del prestador adjudicado."
        );
    }

    private boolean esPresupuestoActivoDeTercerizadora(
            RequerimientoCompraPresupuesto presupuesto) {

        return presupuesto != null
                && presupuesto.getBajaFecha() == null
                && presupuesto.getIdPrestador() != null
                && presupuesto.getIdPrestador().intValue() > 0
                && presupuesto.getDlFileEntryId() != null
                && presupuesto.getDlFileEntryId().longValue() > 0L;
    }

    private boolean debeValidarPresupuestoParaCotizado(
            Integer idPrestadorAdjudicado,
            BigDecimal[] preciosUnitarios) {

        if (idPrestadorAdjudicado == null
                || idPrestadorAdjudicado.intValue() <= 0
                || preciosUnitarios == null
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

    private void validarPresupuestoParaRegistrar(
            RequerimientoCompraPresupuesto presupuesto) throws Exception {

        if (presupuesto == null) {
            throw errorUsuario(
                    "Debe informar el presupuesto del requerimiento."
            );
        }

        if (presupuesto.getIdRequerimiento() == null
                || presupuesto.getIdRequerimiento().intValue() <= 0) {
            throw errorUsuario(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (presupuesto.getIdPrestador() == null
                || presupuesto.getIdPrestador().intValue() <= 0) {
            throw errorUsuario(
                    "Debe informar el prestador del presupuesto."
            );
        }

        if (presupuesto.getDlGroupId() == null
                || presupuesto.getDlGroupId().longValue() <= 0L
                || presupuesto.getDlFolderId() == null
                || presupuesto.getDlFolderId().longValue() < 0L
                || presupuesto.getDlFileEntryId() == null
                || presupuesto.getDlFileEntryId().longValue() <= 0L) {

            throw errorUsuario(
                    "No se pudo identificar correctamente el documento "
                            + "del presupuesto. Vuelva a seleccionarlo."
            );
        }

        if (WebKeysCompras.isEmpty(presupuesto.getNombreOriginal())
                || WebKeysCompras.isEmpty(presupuesto.getNombrePersistido())
                || WebKeysCompras.isEmpty(presupuesto.getTitulo())) {

            throw errorUsuario(
                    "El documento del presupuesto no conserva una identidad valida."
            );
        }
    }

    protected int confirmarEnvioACotizar(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        return persistence.confirmarEnvioACotizar(
                idRequerimientoCompra,
                normalizarUsuario(usuario)
        );
    }

    private Integer getIdRequerimientoDetalle(
            RequerimientoCompraDetalle detalle) {

        if (detalle == null) {
            return null;
        }

        if (detalle.getIdRequerimiento() != null
                && detalle.getIdRequerimiento().intValue() > 0) {
            return detalle.getIdRequerimiento();
        }

        return detalle.getIdRequerimientoCompra() > 0
                ? Integer.valueOf(detalle.getIdRequerimientoCompra())
                : null;
    }

    private void validarPorcentaje(
            Integer value,
            String label) throws Exception {

        int parsed = value != null
                ? value.intValue()
                : 0;

        if (parsed < 0 || parsed > 100) {
            throw errorUsuario(
                    label + " debe estar entre 0 y 100 %."
            );
        }
    }

    private String emptyToNull(String value) {
        return WebKeysCompras.trimToNull(value);
    }

    private String normalizarUsuario(String usuario) {
        String value = WebKeysCompras.trimToNull(usuario);
        return value != null
                ? value
                : "sistema";
    }

    private MensajeUsuarioException errorUsuario(String mensaje) {
        return new MensajeUsuarioException(mensaje);
    }

    private MensajeUsuarioException errorUsuario(
            String mensaje,
            Throwable causa) {

        return new MensajeUsuarioException(
                mensaje,
                causa
        );
    }

    private Exception manejarErrorOperacion(
            String operacion,
            String mensajePredeterminado,
            Exception error,
            String contexto) {

        MensajeUsuarioException funcional =
                buscarMensajeUsuarioException(error);

        if (funcional != null) {
            return funcional;
        }

        StringBuilder mensajeLog = new StringBuilder();
        mensajeLog.append("Error tecnico al ");
        mensajeLog.append(operacion);
        mensajeLog.append('.');

        if (!WebKeysCompras.isEmpty(contexto)) {
            mensajeLog.append(' ');
            mensajeLog.append(contexto);
        }

        _log.error(
                mensajeLog.toString(),
                error
        );

        return errorUsuario(
                mensajePredeterminado,
                error
        );
    }

    private MensajeUsuarioException buscarMensajeUsuarioException(
            Throwable error) {

        Throwable actual = error;
        Set<Throwable> visitados = new HashSet<Throwable>();

        while (actual != null && visitados.add(actual)) {
            if (actual instanceof MensajeUsuarioException) {
                return (MensajeUsuarioException) actual;
            }

            actual = actual.getCause();
        }

        return null;
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
                + ", idMedicamento=" + detalle.getIdMedicamento()
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