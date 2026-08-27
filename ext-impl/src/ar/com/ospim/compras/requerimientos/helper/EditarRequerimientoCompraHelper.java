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
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
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
import com.liferay.portal.service.ServiceContext;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Fuente canónica de reglas funcionales para alta, edición, detalles,
 * estados, cotización y asociaciones documentales del requerimiento.
 *
 * No abre conexiones ni conoce JDBC. Toda persistencia se delega en
 * EditarRequerimientoCompraServiceImpl.
 */
public class EditarRequerimientoCompraHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    EditarRequerimientoCompraHelper.class
            );

    public static final int MAX_ORDENES_MEDICAS_POR_CARGA = 20;

    /**
     * @deprecated Usar MAX_ORDENES_MEDICAS_POR_CARGA.
     */
    @Deprecated
    public static final int MAX_ORDENES_MEDICAS_POR_ALTA =
            MAX_ORDENES_MEDICAS_POR_CARGA;

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
                        "El requerimiento que intenta editar ya no está disponible."
                );
            }

            /*
             * Una vez que el requerimiento deja de estar PENDIENTE,
             * ninguno de sus datos estructurales puede modificarse.
             *
             * El flujo de cotización utiliza sus operaciones específicas
             * y no debe ingresar por este guardado de cabecera.
             */
            if (!actual.puedeEditarEstructura()) {
                throw errorUsuario(
                        "El requerimiento solo puede modificarse mientras "
                                + "se encuentra PENDIENTE."
                );
            }


            /*
             * ==========================================================
             * SECTOR INMUTABLE POST-ALTA
             * ==========================================================
             *
             * Si HTTP envia explicitamente otro sector, se rechaza.
             * Si no lo envia, o envia el mismo, siempre se restaura
             * el valor persistido como fuente autoritativa.
             */

            Integer idSectorActual =
                    actual.getIdSector();

            Integer idSectorRecibido =
                    requerimiento.getIdSector();

            if (idSectorRecibido != null
                    && idSectorRecibido.intValue() > 0
                    && !mismoInteger(
                    idSectorActual,
                    idSectorRecibido
            )) {

                throw errorUsuario(
                        "El sector del requerimiento no puede modificarse una vez creado."
                );
            }

            requerimiento.setIdSector(
                    idSectorActual
            );


            /*
             * ==========================================================
             * AFILIADO INMUTABLE POST-ALTA
             * ==========================================================
             *
             * CUIL e integrante conforman la identidad del afiliado
             * asociado al requerimiento.
             *
             * Los campos pueden no llegar desde HTTP porque la pantalla
             * los renderiza en solo lectura. Por eso solamente se compara
             * cada dato cuando fue efectivamente informado.
             *
             * Independientemente de lo recibido, antes de guardar se
             * restauran siempre los valores persistidos.
             */

            String afiliadoCuilActual =
                    actual.getAfiliadoCuilTitular();

            Integer afiliadoIntActual =
                    actual.getAfiliadoInt();

            String afiliadoCuilRecibido =
                    requerimiento.getAfiliadoCuilTitular();

            Integer afiliadoIntRecibido =
                    requerimiento.getAfiliadoInt();


            if (!WebKeysCompras.isEmpty(
                    afiliadoCuilRecibido
            )
                    && !mismoTexto(
                    afiliadoCuilActual,
                    afiliadoCuilRecibido
            )) {

                throw errorUsuario(
                        "El afiliado del requerimiento no puede modificarse una vez creado."
                );
            }


            if (afiliadoIntRecibido != null
                    && !mismoInteger(
                    afiliadoIntActual,
                    afiliadoIntRecibido
            )) {

                throw errorUsuario(
                        "El afiliado del requerimiento no puede modificarse una vez creado."
                );
            }


            requerimiento.setAfiliadoCuilTitular(
                    afiliadoCuilActual
            );

            requerimiento.setAfiliadoInt(
                    afiliadoIntActual
            );

            /*
             * El snapshot completo del afiliado también permanece inmutable.
             *
             * No se vuelve a consultar ni reconstruir desde los datos actuales
             * del padrón durante una edición del requerimiento.
             */
            requerimiento.setAfiliadoIdOspim(
                    actual.getAfiliadoIdOspim()
            );

            requerimiento.setAfiliadoNombre(
                    actual.getAfiliadoNombre()
            );

            requerimiento.setAfiliadoApellido(
                    actual.getAfiliadoApellido()
            );

            requerimiento.setAfiliadoDocumentoTipo(
                    actual.getAfiliadoDocumentoTipo()
            );

            requerimiento.setAfiliadoDocumentoNro(
                    actual.getAfiliadoDocumentoNro()
            );

            requerimiento.setAfiliadoDireccion(
                    actual.getAfiliadoDireccion()
            );

            requerimiento.setAfiliadoLocalidad(
                    actual.getAfiliadoLocalidad()
            );

            requerimiento.setAfiliadoProvincia(
                    actual.getAfiliadoProvincia()
            );

            requerimiento.setAfiliadoCelular(
                    actual.getAfiliadoCelular()
            );

            requerimiento.setAfiliadoTelefono(
                    actual.getAfiliadoTelefono()
            );

            requerimiento.setAfiliadoEmail(
                    actual.getAfiliadoEmail()
            );

            if (requerimiento.isLegales() != actual.isLegales()) {
                throw errorUsuario(
                        "La marca LEGALES sólo puede definirse durante el alta."
                );
            }

            requerimiento.setLegales(actual.getLegales());

            /*
             * Sector y afiliado ya fueron restaurados desde persistencia.
             *
             * El resto de los datos estructurales permanece editable
             * porque el requerimiento sigue PENDIENTE.
             */
            prepararRequerimientoParaGuardar(
                    requerimiento
            );

            validarRequerimientoParaGuardar(
                    requerimiento
            );


            int idGuardado =
                    persistence.guardarRequerimientoCompra(
                            requerimiento,
                            normalizarUsuario(usuario)
                    );

            if (idGuardado <= 0) {
                throw new IllegalStateException(
                        "El guardado no devolvió un identificador válido."
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
                        "La Orden médica obligatoria solo se registra "
                                + "durante el alta de un requerimiento nuevo."
                );
            }

            validarOrdenesMedicasParaAlta(ordenesMedicas);

            if (gestorDocumento == null) {
                throw new IllegalStateException(
                        "No se obtuvo el gestor documental de la Orden médica."
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
                        "El alta no devolvió un identificador válido."
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
                            "El registro de la Orden médica no devolvió "
                                    + "un identificador válido."
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
                                    + "con Orden médica. No se eliminarán "
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
                    "guardar el requerimiento nuevo con Orden médica",
                    "No se pudo guardar el requerimiento con su Orden médica. "
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

        validarOrdenesMedicasParaCarga(
                ordenesMedicas
        );
    }

    public void validarOrdenesMedicasParaCarga(
            List<OrdenMedicaValidada> ordenesMedicas) throws Exception {

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            throw errorUsuario(
                    "Debe seleccionar al menos una Orden médica "
                            + "e informar su fecha."
            );
        }

        if (ordenesMedicas.size()
                > MAX_ORDENES_MEDICAS_POR_CARGA) {

            throw errorUsuario(
                    "Se pueden registrar hasta "
                            + MAX_ORDENES_MEDICAS_POR_CARGA
                            + " Órdenes médicas por operación."
            );
        }

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            OrdenMedicaValidada ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null
                    || ordenMedica.getFechaDocumento() == null) {

                throw errorUsuario(
                        "Cada Orden médica debe tener "
                                + "informada su fecha."
                );
            }
        }
    }

    public void validarNuevoRequerimientoNoDuplicado(
            RequerimientoCompra requerimiento,
            List<Integer> idsPrestaciones,
            List<OrdenMedicaValidada> ordenesMedicas)
            throws Exception {

        /*
         * Regla exclusiva de alta.
         */
        if (requerimiento == null
                || requerimiento
                .getIdRequerimientoCompra() > 0) {

            return;
        }

        String cuilTitular =
                WebKeysCompras.trimToNull(
                        requerimiento
                                .getAfiliadoCuilTitular()
                );

        Integer integrante =
                requerimiento.getAfiliadoInt();

        /*
         * La ausencia del afiliado se valida posteriormente
         * mediante las reglas generales del requerimiento.
         *
         * Este método solamente controla duplicidad.
         */
        if (cuilTitular == null
                || integrante == null
                || integrante.intValue() < 0) {

            return;
        }

        if (idsPrestaciones == null
                || idsPrestaciones.isEmpty()) {

            return;
        }

        validarOrdenesMedicasParaCarga(
                ordenesMedicas
        );

        Set<Integer> prestacionesUnicas =
                new HashSet<Integer>();

        for (int i = 0;
             i < idsPrestaciones.size();
             i++) {

            Integer idPrestacion =
                    idsPrestaciones.get(i);

            if (idPrestacion != null
                    && idPrestacion.intValue() > 0) {

                prestacionesUnicas.add(
                        idPrestacion
                );
            }
        }

        if (prestacionesUnicas.isEmpty()) {
            return;
        }

        /*
         * java.sql.Date hereda de java.util.Date.
         *
         * Usando java.util.Date acá aceptamos tanto la fecha
         * validada de la Orden médica como las fechas leídas
         * posteriormente desde otros beans.
         */
        Set<java.util.Date> fechasUnicas =
                new HashSet<java.util.Date>();

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            OrdenMedicaValidada ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null
                    || ordenMedica.getFechaDocumento() == null) {

                continue;
            }

            fechasUnicas.add(
                    ordenMedica.getFechaDocumento()
            );
        }

        if (fechasUnicas.isEmpty()) {
            return;
        }

        for (Integer idPrestacion
                : prestacionesUnicas) {

            for (java.util.Date fechaOrdenMedica
                    : fechasUnicas) {

                boolean existeDuplicado =
                        BusquedaRequerimientoCompraServiceUtil
                                .existeRequerimientoDuplicado(
                                        cuilTitular,
                                        integrante.intValue(),
                                        idPrestacion.intValue(),
                                        fechaOrdenMedica,
                                        0
                                );

                if (existeDuplicado) {
                    throw errorUsuario(
                            "Ya existe un requerimiento de compra "
                                    + "para el mismo afiliado, la misma "
                                    + "prestación y la misma fecha "
                                    + "de Orden médica."
                    );
                }
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
                        "No se pudo compensar una Orden médica creada "
                                + "después del rollback. fileEntryId="
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

            validarTipoPrestacionParaGuardar(
                    requerimiento,
                    detallePersistido,
                    detalle
            );

            normalizarDetalleNuevo(
                    detalle
            );

            validarDetalleParaGuardar(
                    requerimiento,
                    detalle
            );

            /*
             * Un detalle NUEVO puede completar posteriormente
             * una combinación:
             *
             * afiliado + prestación + fecha de Orden médica
             *
             * que ya exista en otro requerimiento.
             *
             * No se ejecuta para una actualización de un detalle
             * persistido porque el requerimiento ya existe y esta
             * regla apunta a impedir generar una nueva combinación
             * duplicada mediante el agregado posterior de prestaciones.
             */
            if (detallePersistido == null) {
                validarDetalleNuevoNoGeneraDuplicado(
                        requerimiento,
                        detalle
                );
            }

            int idDetalleGuardado =
                    persistence.guardarDetalle(
                            detalle,
                            normalizarUsuario(
                                    usuario
                            )
                    );

            if (idDetalleGuardado <= 0) {
                throw new IllegalStateException(
                        "El guardado del detalle no devolvió un ID válido."
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
        }
    }

    private void validarDetalleNuevoNoGeneraDuplicado(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle)
            throws Exception {

        if (requerimiento == null
                || detalle == null) {

            return;
        }

        Integer idPrestacion =
                detalle.getIdPrestacion();

        /*
         * MEDICAMENTO y OBSERVACION no participan
         * en esta regla.
         */
        if (idPrestacion == null
                || idPrestacion.intValue() <= 0) {

            return;
        }

        if (!requerimiento
                .tieneAfiliadoInformado()) {

            return;
        }

        String cuilTitular =
                WebKeysCompras.trimToNull(
                        requerimiento
                                .getAfiliadoCuilTitular()
                );

        Integer integrante =
                requerimiento.getAfiliadoInt();

        if (cuilTitular == null
                || integrante == null
                || integrante.intValue() < 0) {

            return;
        }

        int idRequerimientoCompra =
                requerimiento
                        .getIdRequerimientoCompra();

        if (idRequerimientoCompra <= 0) {
            return;
        }

        List<RequerimientoCompraPresupuesto> ordenesMedicas =
                BusquedaRequerimientoCompraServiceUtil
                        .listarOrdenesMedicas(
                                idRequerimientoCompra
                        );

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            return;
        }

        /*
         * RequerimientoCompraPresupuesto#getFechaDocumento()
         * devuelve java.util.Date.
         *
         * Por eso deliberadamente NO usamos java.sql.Date
         * en la capa funcional.
         */
        Set<java.util.Date> fechasUnicas =
                new HashSet<java.util.Date>();

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            RequerimientoCompraPresupuesto ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null
                    || ordenMedica.getBajaFecha() != null
                    || ordenMedica.getFechaDocumento() == null) {

                continue;
            }

            fechasUnicas.add(
                    ordenMedica.getFechaDocumento()
            );
        }

        for (java.util.Date fechaOrdenMedica
                : fechasUnicas) {

            boolean existeDuplicado =
                    BusquedaRequerimientoCompraServiceUtil
                            .existeRequerimientoDuplicado(
                                    cuilTitular,
                                    integrante.intValue(),
                                    idPrestacion.intValue(),
                                    fechaOrdenMedica,
                                    idRequerimientoCompra
                            );

            if (existeDuplicado) {
                throw errorUsuario(
                        "Ya existe otro requerimiento de compra "
                                + "para el mismo afiliado, la misma "
                                + "prestación y la misma fecha "
                                + "de Orden médica."
                );
            }
        }
    }

    /**
     * Contrato canónico utilizado por los Actions: valida estado y pertenencia
     * antes de ejecutar el borrado persistente.
     */
    public void borrarDetalle(
            int idRequerimientoCompra,
            int idDetalle,
            String usuario) throws Exception {

        try {
            RequerimientoCompra requerimiento =
                    validarRequerimientoParaBorrarDetalle(
                            Integer.valueOf(
                                    idRequerimientoCompra
                            )
                    );

            obtenerDetallePersistido(
                    requerimiento,
                    idDetalle
            );

            /*
             * Validación anticipada para mensaje amigable.
             *
             * NO sustituye la validación atómica PostgreSQL.
             * getDetalles() contiene exclusivamente detalles activos.
             */
            if (requerimiento.isACotizar()
                    && requerimiento.getDetalles().size() <= 1) {

                throw errorUsuario(
                        "El requerimiento ENVIADO A COTIZAR "
                                + "debe conservar al menos una prestación."
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
                    "idRequerimiento=" + idRequerimientoCompra
                            + ", idDetalle=" + idDetalle
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
                        "El requerimiento ya no está disponible."
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
            long companyId,
            ServiceContext serviceContext) throws Exception {

        try {
            RequerimientoCompra requerimiento =
                    validarRequerimientoParaEnviarACotizar(
                            idRequerimientoCompra
                    );

            NotificacionCotizacionResultado resultado =
                    notificacionHelper.notificarPrestadores(
                            requerimiento
                                    .getIdRequerimientoCompra(),
                            usuario,
                            companyId,
                            serviceContext
                    );

            if (resultado == null) {
                throw new IllegalStateException(
                        "El proceso de notificación "
                                + "no devolvió resultado."
                );
            }

            int estadoFinal =
                    persistence.confirmarEnvioACotizar(
                            idRequerimientoCompra,
                            normalizarUsuario(
                                    usuario
                            )
                    );

            if (estadoFinal
                    != WebKeysCompras.ESTADO_PENDIENTE
                    && estadoFinal
                    != WebKeysCompras.ESTADO_A_COTIZAR) {

                throw new IllegalStateException(
                        "Estado inesperado al confirmar "
                                + "el envío: "
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
                    "idRequerimiento="
                            + idRequerimientoCompra
                            + ", companyId="
                            + companyId
                            + ", usuario="
                            + usuario
            );
        }
    }

    public NotificacionCotizacionResultado reintentarNotificacionesCotizacion(
            int idRequerimientoCompra,
            String usuario,
            long companyId,
            ServiceContext serviceContext)
            throws Exception {

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
                        "El requerimiento ya no está disponible."
                );
            }

            if (!requerimiento
                    .puedeReintentarNotificaciones()) {

                throw errorUsuario(
                        "Las notificaciones solo pueden reenviarse "
                                + "mientras el requerimiento esta en "
                                + "estado ENVIADO A COTIZAR."
                );
            }

            if (!BusquedaRequerimientoCompraServiceUtil
                    .hayPrestadoresPendientesNotificacion(
                            idRequerimientoCompra
                    )) {

                return new NotificacionCotizacionResultado();
            }

            return notificacionHelper
                    .notificarPrestadores(
                            idRequerimientoCompra,
                            usuario,
                            companyId,
                            serviceContext
                    );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "reenviar las notificaciones de cotización",
                    "No se pudieron reenviar las notificaciones pendientes. "
                            + "Intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + idRequerimientoCompra
                            + ", companyId="
                            + companyId
                            + ", usuario="
                            + usuario
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
                new ArrayList<Integer>(),
                obtenerSurgeActual(idRequerimientoCompra),
                usuario
        );
    }

    public GuardadoCotizacionResultado guardarAvanceCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            List<Integer> idsDetallesEliminados,
            boolean surge,
            String usuario) throws Exception {

        return guardarCotizacion(
                idRequerimientoCompra,
                detalles,
                idsDetallesEliminados,
                surge,
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
                new ArrayList<Integer>(),
                obtenerSurgeActual(idRequerimientoCompra),
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
                        "El registro del presupuesto no devolvió un ID válido."
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

    public boolean darDeBajaCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        try {
            if (idRequerimientoPresupuesto <= 0
                    || idRequerimientoCompra <= 0) {

                throw errorUsuario(
                        "Debe informar la cotización de Empresa "
                                + "y su requerimiento."
                );
            }

            return persistence.darDeBajaCotizacionEmpresa(
                    idRequerimientoPresupuesto,
                    idRequerimientoCompra,
                    normalizarUsuario(usuario)
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "quitar la cotización de Empresa",
                    "No se pudo quitar la cotización de Empresa. "
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

    public boolean reactivarCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        try {
            if (idRequerimientoPresupuesto <= 0
                    || idRequerimientoCompra <= 0) {

                throw errorUsuario(
                        "Debe informar la cotización de Empresa "
                                + "y su requerimiento."
                );
            }

            return persistence.reactivarCotizacionEmpresa(
                    idRequerimientoPresupuesto,
                    idRequerimientoCompra
            );

        } catch (Exception e) {
            throw manejarErrorOperacion(
                    "reactivar la cotización de Empresa",
                    "No se pudo reactivar la cotización de Empresa. "
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

        Integer idSector = requerimiento.getIdSector();

        if (idSector != null && idSector.intValue() > 0) {
            RequerimientoCompraSector sector =
                    BusquedaRequerimientoCompraServiceUtil
                            .getSector(idSector.intValue());

            if (sector == null || sector.getIdSector() <= 0) {
                throw errorUsuario(
                        "El sector seleccionado ya no existe o no está disponible."
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

        /*
         * El snapshot del afiliado se captura exclusivamente durante el alta.
         *
         * En requerimientos existentes la identidad y los datos asociados
         * al afiliado son inmutables.
         */
        if (requerimiento.getIdRequerimientoCompra() <= 0
                && requerimiento.tieneAfiliadoInformado()) {

            cargarSnapshotAfiliado(
                    requerimiento
            );
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
                "No se pudo obtener un único afiliado para guardar el requerimiento."
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
                "El tipo de item seleccionado no es válido."
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

private void validarTipoPrestacionParaGuardar(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle detallePersistido,
        RequerimientoCompraDetalle detalle) throws Exception {

    if (requerimiento == null
            || requerimiento.getIdSector() == null
            || requerimiento.getIdSector().intValue() <= 0
            || detalle == null) {

        throw errorUsuario(
                "No se pudo validar el tipo de prestación del detalle."
        );
    }

    List<TipoPrestacionCompra> tipos =
            BusquedaRequerimientoCompraServiceUtil
                    .listarTiposPrestacion();

    boolean sectorConCatalogo = false;
    boolean tipoValido = false;
    Integer idTipo = detalle.getIdTipoPrestacion();

    for (int i = 0; tipos != null && i < tipos.size(); i++) {
        TipoPrestacionCompra tipo = tipos.get(i);

        if (tipo == null
                || tipo.getIdSectorInt()
                != requerimiento.getIdSector().intValue()) {

            continue;
        }

        sectorConCatalogo = true;

        if (idTipo != null
                && tipo.getIdInt() == idTipo.intValue()) {

            tipoValido = true;
            detalle.setTipoPrestacionDescripcion(
                    tipo.getDescripcion()
            );
        }
    }

    if (idTipo == null) {
        if (detallePersistido != null
                && detallePersistido.getIdTipoPrestacion() != null) {

            throw errorUsuario(
                    "El tipo de prestación ya informado no puede quitarse."
            );
        }

        if (detallePersistido == null && sectorConCatalogo) {
            throw errorUsuario(
                    "Debe seleccionar el tipo de prestación."
            );
        }

        return;
    }

    if (!tipoValido) {
        throw errorUsuario(
                "El tipo de prestación no corresponde al sector "
                        + "del requerimiento."
        );
    }
}

private void validarDetalleNomencladorParaGuardar(
        RequerimientoCompra requerimiento,
        RequerimientoCompraDetalle detalle) throws Exception {

    if (detalle.getIdPrestacion() == null
            || detalle.getIdPrestacion().intValue() <= 0) {

        throw errorUsuario(
                "Debe seleccionar una prestación del nomenclador."
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
                "Los datos recibidos no corresponden a una prestación. "
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
                "La prestación seleccionada ya no existe "
                        + "o no está activa. Vuelva a seleccionarla."
        );
    }

    int idTipoNomencladorCanonico =
            nomenclador.getId_tipo_nomenclador();

    if (idTipoNomencladorCanonico <= 0
            || idTipoNomencladorCanonico
            != detalle.getIdTipoNomenclador().intValue()) {

        throw errorUsuario(
                "La prestación seleccionada no corresponde al "
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
            "código de nomenclador",
            detalle.getCodigoNomenclador(),
            nomenclador.getCodigo()
    );

    validarTextoTecnico(
            "descripción de nomenclador",
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
        return "Para Farmacia debe seleccionar una prestación "
                + "del nomenclador tipo 9.";
    }

    if ("DISCAPACIDAD".equals(sector)) {
        return "Para Discapacidad debe seleccionar una prestación "
                + "con marca ReinLiq 6 o el código 431003.";
    }

    if ("ODONTOLOGIA".equals(sector)) {
        return "Para Odontologia debe seleccionar una prestación "
                + "del nomenclador tipo 1.";
    }

    if ("PRESTACIONES MEDICAS".equals(sector)) {
        return "Para PRESTACIONES MÉDICAS debe seleccionar una "
                + "prestación de nomenclador tipo 2, 3, 4, 6 o 10.";
    }

    return "La prestación seleccionada no corresponde "
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
                "El detalle histórico de medicamento no conserva "
                        + "una referencia válida."
        );
    }

    if (detalle.getIdPrestacion() != null
            || detalle.getIdTipoNomenclador() != null
            || !WebKeysCompras.isEmpty(detalle.getCodigoNomenclador())
            || !WebKeysCompras.isEmpty(detalle.getDescripcionNomenclador())) {

        throw errorUsuario(
                "El detalle histórico de medicamento contiene "
                        + "datos técnicos incompatibles."
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
                "Los detalles ya no pueden modificarse porque el "
                        + "requerimiento no se encuentra PENDIENTE."
        );
    }

    if (requerimiento.getSectorId() == null
            || requerimiento.getSectorId().intValue() <= 0) {

        throw errorUsuario(
                "El requerimiento no tiene un sector válido."
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
                    "El detalle histórico de medicamento no puede "
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
                            + "del requerimiento. Actualice la pantalla "
                            + "e intente nuevamente."
            );
        }

        String tipoRecibido = detalle.getTipoItemNormalizado();

        if (!WebKeysCompras.isEmpty(tipoRecibido)
                && !RequerimientoCompraDetalle
                .TIPO_ITEM_OBSERVACION
                .equals(tipoRecibido)) {

            throw errorUsuario(
                    "El sector seleccionado requiere un detalle de OBSERVACIÓN."
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

        }

        if (detallePersistido != null
                && detallePersistido.esObservacion()) {

            throw errorUsuario(
                    "El detalle existente no corresponde al sector "
                            + "del requerimiento. Actualice la pantalla "
                            + "e intente nuevamente."
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
                            + " cambió o ya no coincide con la información actual. "
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
                    "No se encontró el requerimiento de compra informado."
            );
        }

        if (!requerimiento.puedeEnviarACotizar()) {
            throw errorUsuario(
                    "El requerimiento solo puede enviarse a cotizar "
                            + "mientras está PENDIENTE."
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
            List<Integer> idsDetallesEliminados,
            boolean surge,
            String usuario) throws Exception {

        Integer idPrestadorAdjudicado = null;

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            validarDetallesCotizacionRecibidos(detalles);
            validarIdsDetallesEliminados(
                    detalles,
                    idsDetallesEliminados
            );

            idPrestadorAdjudicado =
                    obtenerPrestadorAdjudicadoUnico(detalles);

            Integer[] idsDetalle = new Integer[detalles.size()];
            BigDecimal[] preciosUnitarios = new BigDecimal[detalles.size()];
            Integer[] idsEliminados =
                    idsDetallesEliminados.toArray(
                            new Integer[idsDetallesEliminados.size()]
                    );

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
                            idsEliminados,
                            idPrestadorAdjudicado,
                            surge,
                            normalizarUsuario(usuario)
                    );

            if (estadoFinal != WebKeysCompras.ESTADO_A_COTIZAR
                    && estadoFinal != WebKeysCompras.ESTADO_COTIZADO) {

                throw new IllegalStateException(
                        "La cotización devolvió un estado inválido: "
                                + estadoFinal
                );
            }

            return new GuardadoCotizacionResultado(
                    estadoFinal == WebKeysCompras.ESTADO_COTIZADO,
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
        }
    }

    private boolean obtenerSurgeActual(
            int idRequerimientoCompra) throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            throw errorUsuario(
                    "No se encontró el requerimiento de compra informado."
            );
        }

        return requerimiento.isSurge();
    }

    protected void validarIdsDetallesEliminados(
            List<RequerimientoCompraDetalle> detalles,
            List<Integer> idsDetallesEliminados) throws Exception {

        if (idsDetallesEliminados == null) {
            throw errorUsuario(
                    "No se recibió la lista de prestaciones eliminadas."
            );
        }

        Set<Integer> idsConservados = new HashSet<Integer>();

        for (int i = 0; detalles != null && i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);

            if (detalle != null && detalle.getIdInt() > 0) {
                idsConservados.add(Integer.valueOf(detalle.getIdInt()));
            }
        }

        Set<Integer> idsEliminados = new HashSet<Integer>();

        for (int i = 0; i < idsDetallesEliminados.size(); i++) {
            Integer id = idsDetallesEliminados.get(i);

            if (id == null
                    || id.intValue() <= 0
                    || idsConservados.contains(id)
                    || !idsEliminados.add(id)) {

                throw errorUsuario(
                        "La lista de prestaciones eliminadas fue manipulada."
                );
            }
        }
    }

    protected void validarDetallesCotizacionRecibidos(
            List<RequerimientoCompraDetalle> detalles) throws Exception {

        if (detalles == null || detalles.isEmpty()) {
            throw errorUsuario(
                    "La cotización no contiene detalles para guardar."
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
                        "La cotización contiene un detalle repetido. "
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
                    "Debe seleccionar un prestador adjudicado válido."
            );
        }

        List<RequerimientoCompraPresupuesto> presupuestos =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPresupuestos(idRequerimientoCompra);

        for (int i = 0;
             presupuestos != null && i < presupuestos.size();
             i++) {

            RequerimientoCompraPresupuesto presupuesto = presupuestos.get(i);

            if (presupuesto == null
                    || presupuesto.getBajaFecha() != null
                    || presupuesto.getIdPrestador() == null
                    || presupuesto.getIdPrestador().intValue() <= 0
                    || presupuesto.getDlFileEntryId() == null
                    || presupuesto.getDlFileEntryId().longValue() <= 0L) {
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
                "Para cerrar la cotización, primero cargue un presupuesto "
                        + "activo del prestador adjudicado."
        );
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

        if (presupuesto.getTipoDocumento() == null) {
            throw errorUsuario(
                    "Debe informar el tipo de documento del presupuesto."
            );
        }

        if (presupuesto.isPresupuestoPrestador()) {
            if (presupuesto.getIdPrestador() == null
                    || presupuesto.getIdPrestador().intValue() <= 0) {

                throw errorUsuario(
                        "Debe informar el prestador del presupuesto."
                );
            }

            if (!WebKeysCompras.isEmpty(presupuesto.getEmpresaCuit())
                    || !WebKeysCompras.isEmpty(
                            presupuesto.getEmpresaSucursal()
                    )
                    || !WebKeysCompras.isEmpty(
                            presupuesto.getDescripcionEmpresa()
                    )) {

                throw errorUsuario(
                        "Un presupuesto de prestador no puede asociarse "
                                + "a una Empresa."
                );
            }

        } else if (presupuesto.isCotizacionEmpresa()) {
            if (presupuesto.getIdPrestador() != null
                    || !WebKeysCompras.isEmpty(
                            presupuesto.getDescripcionPrestador()
                    )) {

                throw errorUsuario(
                        "Una cotización de Empresa no puede asociarse "
                                + "a un prestador."
                );
            }

            if (WebKeysCompras.isEmpty(presupuesto.getEmpresaCuit())
                    || presupuesto.getEmpresaCuit().length() > 11
                    || WebKeysCompras.isEmpty(
                            presupuesto.getEmpresaSucursal()
                    )
                    || presupuesto.getEmpresaSucursal().length() > 6
                    || WebKeysCompras.isEmpty(
                            presupuesto.getDescripcionEmpresa()
                    )
                    || presupuesto.getDescripcionEmpresa().length() > 200) {

                throw errorUsuario(
                        "La identidad de la Empresa de la cotización "
                                + "no es válida."
                );
            }

        } else {
            throw errorUsuario(
                    "El tipo de documento del presupuesto no es válido."
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
                    "El documento del presupuesto no conserva una identidad válida."
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
        mensajeLog.append("Error técnico al ");
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

        String contexto = "idRequerimiento="
                + presupuesto.getIdRequerimiento()
                + ", tipoDocumento="
                + presupuesto.getTipoDocumento()
                + ", idPrestador="
                + presupuesto.getIdPrestador();

        if (presupuesto.isCotizacionEmpresa()) {
            contexto += ", empresaCuit="
                    + presupuesto.getEmpresaCuit()
                    + ", empresaSucursal="
                    + presupuesto.getEmpresaSucursal();
        }

        return contexto
                + ", dlFileEntryId="
                + presupuesto.getDlFileEntryId()
                + ", usuario=" + usuario;
    }

    public int agregarOrdenesMedicasRequerimientoPendiente(
            int idRequerimientoCompra,
            List<OrdenMedicaValidada> ordenesMedicas,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario) throws Exception {

        EditarRequerimientoCompraServiceImpl.Transaccion transaccion =
                null;

        List<DocumentoComprasCreado> documentosCreados =
                new ArrayList<DocumentoComprasCreado>();

        try {
            if (idRequerimientoCompra <= 0) {
                throw errorUsuario(
                        "Debe informar el requerimiento de compra."
                );
            }

            RequerimientoCompra actual =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    idRequerimientoCompra
                            );

            if (actual == null) {
                throw errorUsuario(
                        "El requerimiento ya no está disponible."
                );
            }

            if (!actual.puedeEditarEstructura()) {
                throw errorUsuario(
                        "Solo pueden agregarse Órdenes médicas mientras "
                                + "el requerimiento se encuentra PENDIENTE."
                );
            }

            validarOrdenesMedicasParaCarga(
                    ordenesMedicas
            );

            if (gestorDocumento == null) {
                throw new IllegalStateException(
                        "No se obtuvo el gestor documental "
                                + "de la Orden médica."
                );
            }

            transaccion =
                    persistence.abrirTransaccion();

            for (int i = 0;
                 i < ordenesMedicas.size();
                 i++) {

                OrdenMedicaValidada ordenMedica =
                        ordenesMedicas.get(i);

                DocumentoComprasCreado documento =
                        gestorDocumento.crearOrdenMedica(
                                idRequerimientoCompra,
                                ordenMedica
                        );

                documentosCreados.add(
                        documento
                );

                int idDocumento =
                        transaccion.registrarOrdenMedica(
                                idRequerimientoCompra,
                                ordenMedica,
                                documento,
                                normalizarUsuario(usuario)
                        );

                if (idDocumento <= 0) {
                    throw new IllegalStateException(
                            "El registro de la Orden médica "
                                    + "no devolvió un identificador válido."
                    );
                }
            }

            transaccion.commit();

            return documentosCreados.size();

        } catch (Exception e) {
            boolean rollbackConfirmado =
                    transaccion == null;

            if (transaccion != null) {
                try {
                    transaccion.rollback();
                    rollbackConfirmado = true;

                } catch (Exception rollbackError) {
                    _log.error(
                            "No se pudo confirmar el rollback al agregar "
                                    + "Órdenes médicas a un requerimiento existente.",
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
                    "agregar Órdenes médicas al requerimiento",
                    "No se pudieron agregar las Órdenes médicas. "
                            + "Vuelva a seleccionar las imágenes "
                            + "e intente nuevamente.",
                    e,
                    "idRequerimiento="
                            + idRequerimientoCompra
                            + ", usuario="
                            + usuario
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

    private RequerimientoCompra validarRequerimientoParaBorrarDetalle(
            Integer idRequerimiento) throws Exception {

        if (idRequerimiento == null
                || idRequerimiento.intValue() <= 0) {

            throw errorUsuario(
                    "Debe informar el requerimiento del detalle."
            );
        }

        RequerimientoCompra requerimiento =
                obtenerRequerimientoDetalle(
                        idRequerimiento.intValue()
                );

        if (requerimiento == null) {
            throw errorUsuario(
                    "El requerimiento ya no está disponible."
            );
        }

        if (!requerimiento.puedeEliminarDetalle()) {
            throw errorUsuario(
                    "Los detalles solo pueden quitarse mientras "
                            + "el requerimiento se encuentra PENDIENTE "
                            + "o ENVIADO A COTIZAR."
            );
        }

        return requerimiento;
    }
}
