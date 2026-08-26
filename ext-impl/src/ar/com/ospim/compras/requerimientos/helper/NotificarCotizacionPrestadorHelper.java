package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.NotificarCotizacionPrestadorServiceImpl;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;

import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class NotificarCotizacionPrestadorHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    NotificarCotizacionPrestadorHelper.class
            );

    private static final boolean REDIRECCION_QA_HABILITADA =
            true;

    private static final String EMAIL_DESTINO_QA =
            "acomas@ospim.org.ar";

    /*
     * El nombre histórico de la configuración se conserva por compatibilidad.
     * Los destinatarios se envian actualmente como BCC.
     */
    private static final String EMAIL_COPIA_COTIZACION =
            TraeListasServiceUtil.getSystemConfig(
                    "REQUERIMIENTO_EMAIL_CC"
            );

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"
            );

    private static final Pattern SENSITIVE_DETAIL_PATTERN =
            Pattern.compile(
                    "(?i)(password|passwd|pwd|token|secret|"
                            + "api[_-]?key|authorization)"
                            + "\\s*[:=]\\s*\\S+"
            );

    private final CotizacionPrestadorMailHelper mailHelper =
            new CotizacionPrestadorMailHelper();

    private final NotificarCotizacionPrestadorServiceImpl persistence =
            new NotificarCotizacionPrestadorServiceImpl();

    public NotificacionCotizacionResultado notificarPrestadores(
            int idRequerimientoCompra,
            String usuario,
            long companyId,
            ServiceContext serviceContext)
            throws Exception {

        validarParametros(
                idRequerimientoCompra,
                companyId
        );

        RequerimientoCompra requerimiento =
                getRequerimientoCompra(
                        idRequerimientoCompra
                );

        validarRequerimiento(
                requerimiento
        );

        List<PrestadorCotizacion> candidatos =
                listarPrestadoresCandidatos(
                        idRequerimientoCompra
                );

        NotificacionCotizacionResultado resultado =
                new NotificacionCotizacionResultado();

        resultado.setTotalCandidatos(
                candidatos.size()
        );

        cargarDiagnosticoCandidatosConControl(
                requerimiento,
                resultado
        );

        if (candidatos.isEmpty()) {

            if (_log.isInfoEnabled()) {
                _log.info(
                        "No hay prestadores pendientes de notificación. "
                                + "idRequerimiento="
                                + idRequerimientoCompra
                                + ", habilitados="
                                + resultado.getPrestadoresHabilitados()
                                + ", compatiblesSector="
                                + resultado
                                .getPrestadoresCompatiblesSector()
                                + ", bloqueadosEstadoPrevio="
                                + resultado
                                .getPrestadoresBloqueadosEstadoPrevio()
                );
            }

            return resultado;
        }

        /*
         * Existe por lo menos un prestador procesable.
         *
         * El pedido que se enviara por correo debe poder
         * persistirse en Document Library.
         */
        DocumentoLibraryComprasHelper
                .validarContextoDocumentLibrary(
                        serviceContext
                );

        /*
         * El PDF se genera una única vez.
         *
         * El mismo byte[] se persiste y posteriormente
         * se adjunta al correo.
         */
        byte[] pedidoPresupuestoPdf =
                generarPedidoPresupuestoPdf(
                        idRequerimientoCompra
                );

        String nombrePedidoPresupuestoPdf =
                "PedidoPresupuesto_"
                        + idRequerimientoCompra
                        + ".pdf";

        List<OrdenMedicaAdjunta> ordenesMedicasAdjuntas =
                recuperarOrdenesMedicasAdjuntas(
                        idRequerimientoCompra,
                        companyId
                );

        for (int i = 0;
             i < candidatos.size();
             i++) {

            procesarPrestador(
                    requerimiento,
                    candidatos.get(i),
                    usuario,
                    companyId,
                    serviceContext,
                    resultado,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf,
                    ordenesMedicasAdjuntas
            );
        }

        if (resultado.getPendientesSinClasificar() > 0) {

            _log.error(
                    "El proceso de notificación finalizo "
                            + "con candidatos sin clasificar. "
                            + "idRequerimiento="
                            + idRequerimientoCompra
                            + ", candidatos="
                            + resultado.getTotalCandidatos()
                            + ", procesados="
                            + resultado.getTotalProcesados()
                            + ", pendientesSinClasificar="
                            + resultado.getPendientesSinClasificar()
            );
        }

        return resultado;
    }

    private void procesarPrestador(
            RequerimientoCompra requerimiento,
            PrestadorCotizacion prestador,
            String usuario,
            long companyId,
            ServiceContext serviceContext,
            NotificacionCotizacionResultado resultado,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<OrdenMedicaAdjunta> ordenesMedicasAdjuntas) {

        if (prestador == null) {

            _log.error(
                    "La consulta de prestadores candidatos "
                            + "devolvio un elemento nulo. "
                            + "idRequerimiento="
                            + requerimiento
                            .getIdRequerimientoCompra()
            );

            registrarResultado(
                    resultado,
                    null,
                    null,
                    null,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "VALIDACION",
                    "No se pudo preparar la notificacion. "
                            + "Contacte a Sistemas.",
                    false
            );

            return;
        }

        int idRequerimiento =
                requerimiento
                        .getIdRequerimientoCompra();

        int idPrestador =
                prestador
                        .getIdPrestador();

        if (_log.isInfoEnabled()) {

            _log.info(
                    "Procesando candidato de cotizacion. "
                            + "idRequerimiento="
                            + idRequerimiento
                            + ", sector="
                            + requerimiento.getIdSector()
                            + ", idPrestador="
                            + idPrestador
                            + ", idTipoPrestador="
                            + prestador.getIdTipoPrestador()
            );
        }

        ReservaCotizacionPrestador reserva;

        /*
         * ==========================================================
         * 1. RESERVA EXCLUSIVA
         * ==========================================================
         */
        try {

            reserva =
                    reservarCotizacionPrestador(
                            idRequerimiento,
                            idPrestador,
                            usuario
                    );

        } catch (Exception e) {

            _log.error(
                    "No se pudo reservar la notificacion "
                            + "de cotizacion. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    e
            );

            /*
             * No se utiliza prestador.getEmail().
             *
             * Los destinatarios reales deben provenir exclusivamente
             * de la reserva, cuya fuente canonica es:
             *
             * prestad_contacto_e -> contacto_e
             */
            registrarResultado(
                    resultado,
                    prestador,
                    null,
                    null,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "RESERVA",
                    "No se pudo iniciar el envio. "
                            + "Contacte a Sistemas antes de reintentar.",
                    false
            );

            return;
        }

        if (reserva == null) {

            _log.error(
                    "La funcion de reserva no devolvio resultado. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );

            registrarResultado(
                    resultado,
                    prestador,
                    null,
                    null,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "RESERVA",
                    "No se pudo iniciar el envio. "
                            + "Contacte a Sistemas antes de reintentar.",
                    false
            );

            return;
        }

        if (!reserva.isReservado()) {

            String motivo =
                    construirMotivoReservaNoOtorgada(
                            reserva
                    );

            if (_log.isInfoEnabled()) {

                _log.info(
                        "Prestador omitido porque no se obtuvo "
                                + "la reserva exclusiva. "
                                + "idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                                + ", estadoObservado="
                                + reserva.getEstadoEnvio()
                                + ", motivoCodigo="
                                + reserva.getMotivoCodigo()
                                + ", motivo="
                                + motivo
                );
            }

            registrarResultado(
                    resultado,
                    prestador,
                    reserva.getEmailDestino(),
                    null,
                    NotificacionCotizacionDetalle.RESULTADO_OMITIDO,
                    "RESERVA",
                    motivo,
                    false
            );

            return;
        }

        /*
         * Desde aca la fila queda PROCESANDO y esta ejecucion
         * posee la reserva exclusiva.
         *
         * email_destino contiene 0..N destinatarios separados
         * por punto y coma.
         */
        String emailsReservados =
                normalizarEmail(
                        reserva.getEmailDestino()
                );

        String[] emailsReales =
                resolverEmailsPrestador(
                        emailsReservados
                );

        boolean modoTemporal =
                redireccionQaHabilitada();

        boolean prestadorSinEmail =
                emailsReales == null
                        || emailsReales.length == 0;

        /*
         * En modo QA la ausencia de email real es solamente
         * una advertencia porque ningun correo sera enviado
         * al prestador.
         */
        boolean emailRealInvalidoAdvertido =
                modoTemporal
                        && prestadorSinEmail;

        /*
         * ==========================================================
         * 2. VALIDACION DE LOS DESTINATARIOS REALES
         * ==========================================================
         *
         * En produccion, si no existe ningun contacto electronico
         * valido asociado al prestador, no se consulta otra fuente.
         */
        if (!modoTemporal
                && prestadorSinEmail) {

            String errorTecnico =
                    "El prestador no posee contactos electronicos "
                            + "vigentes y validos asociados mediante "
                            + "prestad_contacto_e.";

            _log.warn(
                    "No se intentara enviar la cotizacion porque "
                            + "el prestador no posee emails habilitados "
                            + "obtenidos mediante prestad_contacto_e. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            WebKeysCompras.ENVIO_EMAIL_INVALIDO,
                            errorTecnico,
                            usuario
                    );

            String motivoUsuario;

            if (persistido) {

                motivoUsuario =
                        "El prestador no tiene un email "
                                + "habilitado registrado.";

            } else {

                motivoUsuario =
                        "El prestador no tiene un email habilitado "
                                + "y el resultado no pudo registrarse. "
                                + "Contacte a Sistemas.";
            }

            registrarResultado(
                    resultado,
                    prestador,
                    null,
                    null,
                    persistido
                            ? NotificacionCotizacionDetalle
                              .RESULTADO_EMAIL_INVALIDO
                            : NotificacionCotizacionDetalle
                              .RESULTADO_ERROR,
                    persistido
                            ? "VALIDACION_EMAIL"
                            : "PERSISTENCIA",
                    motivoUsuario,
                    false
            );

            return;
        }

        if (modoTemporal
                && prestadorSinEmail) {

            _log.warn(
                    "El prestador no posee emails habilitados "
                            + "asociados mediante prestad_contacto_e. "
                            + "El envio continuara exclusivamente "
                            + "al destinatario temporal de QA. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );
        }

        /*
         * En produccion devuelve exactamente los emails reales.
         *
         * En modo temporal devuelve exclusivamente EMAIL_DESTINO_QA.
         * Nunca se generan N copias hacia QA.
         */
        String[] emailsDestino =
                resolverEmailsDestino(
                        emailsReales,
                        modoTemporal
                );

        /*
         * Aunque la funcion SQL filtra las direcciones,
         * se valida nuevamente el contrato antes de SMTP.
         */
        boolean destinoEfectivoValido =
                emailsDestino != null
                        && emailsDestino.length > 0;

        if (destinoEfectivoValido) {

            for (int i = 0;
                 i < emailsDestino.length;
                 i++) {

                if (!esEmailValido(
                        emailsDestino[i]
                )) {

                    destinoEfectivoValido =
                            false;

                    break;
                }
            }
        }

        String emailsRealesDetalle =
                unirEmails(
                        emailsReales
                );

        String emailsDestinoDetalle =
                unirEmails(
                        emailsDestino
                );

        /*
         * ==========================================================
         * 2.1 VALIDACION DEL DESTINO EFECTIVO
         * ==========================================================
         */
        if (!destinoEfectivoValido) {

            String errorTecnico;

            if (modoTemporal) {

                errorTecnico =
                        "El email destino temporal de QA "
                                + "es inexistente o invalido.";

            } else {

                errorTecnico =
                        "Uno o mas emails destino efectivos "
                                + "del prestador son inexistentes "
                                + "o invalidos.";
            }

            _log.warn(
                    "No se intentara enviar la cotizacion "
                            + "porque el conjunto de destinatarios "
                            + "efectivos es invalido. "
                            + "modoTemporal="
                            + modoTemporal
                            + ", cantidadDestinatarios="
                            + (
                            emailsDestino != null
                                    ? emailsDestino.length
                                    : 0
                    )
                            + ", idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            WebKeysCompras.ENVIO_EMAIL_INVALIDO,
                            errorTecnico,
                            usuario
                    );

            String motivoUsuario;

            if (modoTemporal) {

                motivoUsuario =
                        "El destinatario configurado para las pruebas "
                                + "no es valido. Contacte a Sistemas.";

            } else {

                motivoUsuario =
                        "El prestador no tiene destinatarios "
                                + "de email validos registrados.";
            }

            if (!persistido) {

                motivoUsuario +=
                        " El resultado tampoco pudo registrarse.";
            }

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    persistido
                            ? NotificacionCotizacionDetalle
                              .RESULTADO_EMAIL_INVALIDO
                            : NotificacionCotizacionDetalle
                              .RESULTADO_ERROR,
                    persistido
                            ? "VALIDACION_EMAIL"
                            : "PERSISTENCIA",
                    motivoUsuario,
                    emailRealInvalidoAdvertido
            );

            return;
        }

        if (modoTemporal
                && _log.isInfoEnabled()) {

            _log.info(
                    "Modo temporal de notificacion activo. "
                            + "El correo sera redirigido "
                            + "exclusivamente al destinatario fijo de QA. "
                            + "cantidadEmailsReales="
                            + (
                            emailsReales != null
                                    ? emailsReales.length
                                    : 0
                    )
                            + ", idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
            );
        }

        /*
         * ==========================================================
         * 3. CONSERVAR EL PEDIDO EXACTO ANTES DEL ENVIO
         * ==========================================================
         *
         * El pedido debe conservarse antes del correo.
         *
         * El mismo byte[] que se persiste es el que luego
         * se adjunta al mensaje.
         */
        try {

            registrarPedidoCotizacionActual(
                    idRequerimiento,
                    idPrestador,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf,
                    usuario,
                    serviceContext
            );

        } catch (Exception e) {

            String detalleError =
                    construirDetalleError(
                            e
                    );

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            WebKeysCompras.ENVIO_ERROR,
                            detalleError,
                            usuario
                    );

            _log.error(
                    "No se pudo conservar el pedido de cotizacion "
                            + "antes de enviar el correo. "
                            + "El envio fue cancelado. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
                            + ", estadoErrorPersistido="
                            + persistido,
                    e
            );

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "DOCUMENTO_PEDIDO_COTIZACION",
                    "No se pudo conservar el pedido de cotizacion. "
                            + "No se envio el correo. "
                            + "Contacte a Sistemas.",
                    emailRealInvalidoAdvertido
            );

            return;
        }

        /*
         * ==========================================================
         * 4. ENVIO DEL CORREO
         * ==========================================================
         *
         * Existe un unico MimeMessage para el prestador.
         *
         * En produccion contiene todos los emails habilitados
         * como destinatarios TO.
         *
         * En QA contiene solamente EMAIL_DESTINO_QA.
         */
        try {

            String asunto =
                    construirAsunto(
                            requerimiento
                    );

            String cuerpo =
                    construirCuerpo(
                            requerimiento,
                            prestador
                    );

            /*
             * pedidoPresupuestoPdf es exactamente el mismo byte[]
             * que se conservo documentalmente.
             */
            enviarMail(
                    companyId,
                    emailsDestino,
                    asunto,
                    cuerpo,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf,
                    ordenesMedicasAdjuntas
            );

        } catch (Exception e) {

            String detalleError =
                    construirDetalleError(
                            e
                    );

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            WebKeysCompras.ENVIO_ERROR,
                            detalleError,
                            usuario
                    );

            String motivoUsuario;

            if (persistido) {

                motivoUsuario =
                        "El correo no pudo enviarse. "
                                + "Contacte a Sistemas "
                                + "antes de reintentar.";

            } else {

                motivoUsuario =
                        "El correo no pudo enviarse y el resultado "
                                + "no pudo registrarse. "
                                + "Contacte a Sistemas "
                                + "antes de reintentar.";
            }

            _log.error(
                    "Fallo el envio de la cotizacion. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
                            + ", cantidadDestinatarios="
                            + emailsDestino.length
                            + ", estadoErrorPersistido="
                            + persistido,
                    e
            );

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "ENVIO",
                    motivoUsuario,
                    emailRealInvalidoAdvertido
            );

            return;
        }

        /*
         * ==========================================================
         * 5. CONFIRMACION DEL ENVIO
         * ==========================================================
         */
        boolean enviadoPersistido =
                finalizarConControl(
                        idRequerimiento,
                        idPrestador,
                        WebKeysCompras.ENVIO_ENVIADO,
                        null,
                        usuario
                );

        if (!enviadoPersistido) {

            String motivoUsuario =
                    "El correo fue aceptado, pero el resultado "
                            + "del envio no pudo confirmarse. "
                            + "No reintente hasta verificarlo "
                            + "con Sistemas.";

            _log.error(
                    "El servicio de correo acepto el mensaje, "
                            + "pero no se pudo persistir ENVIADO. "
                            + "La fila puede permanecer PROCESANDO. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
                            + ", cantidadDestinatarios="
                            + emailsDestino.length
            );

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "PERSISTENCIA",
                    motivoUsuario,
                    emailRealInvalidoAdvertido
            );

            return;
        }

        /*
         * ==========================================================
         * 6. RESULTADO EXITOSO
         * ==========================================================
         */
        String motivoExito;

        if (modoTemporal) {

            motivoExito =
                    "Correo enviado al destinatario temporal "
                            + "de QA y resultado confirmado.";

            if (emailRealInvalidoAdvertido) {

                motivoExito +=
                        " El prestador no posee un email "
                                + "habilitado y debe revisarse.";
            }

        } else {

            motivoExito =
                    "Correo enviado a todos los emails habilitados "
                            + "del prestador y resultado confirmado.";
        }

        registrarResultado(
                resultado,
                prestador,
                emailsRealesDetalle,
                emailsDestinoDetalle,
                NotificacionCotizacionDetalle.RESULTADO_ENVIADO,
                "FINALIZADO",
                motivoExito,
                emailRealInvalidoAdvertido
        );

        if (_log.isInfoEnabled()) {

            _log.info(
                    "Cotizacion enviada y finalizada. "
                            + "idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento
                            + ", sector="
                            + requerimiento.getIdSector()
                            + ", idTipoPrestador="
                            + prestador.getIdTipoPrestador()
                            + ", modoTemporal="
                            + modoTemporal
                            + ", cantidadEmailsReales="
                            + (
                            emailsReales != null
                                    ? emailsReales.length
                                    : 0
                    )
                            + ", cantidadDestinatariosEfectivos="
                            + emailsDestino.length
                            + ", estadoEnvio=ENVIADO"
                            + ", ordenesMedicasAdjuntas="
                            + (
                            ordenesMedicasAdjuntas != null
                                    ? ordenesMedicasAdjuntas.size()
                                    : 0
                    )
            );
        }
    }

    protected RequerimientoCompraPedidoCotizacion registrarPedidoCotizacionActual(
            int idRequerimiento,
            int idPrestador,
            byte[] contenido,
            String nombreOriginal,
            String usuario,
            ServiceContext serviceContext)
            throws Exception {

        if (idRequerimiento <= 0) {

            throw new Exception(
                    "Debe informar el requerimiento "
                            + "del pedido de cotización."
            );
        }

        if (idPrestador <= 0) {

            throw new Exception(
                    "Debe informar el prestador "
                            + "del pedido de cotización."
            );
        }

        if (contenido == null
                || contenido.length == 0) {

            throw new Exception(
                    "El pedido de cotización generado "
                            + "no contiene datos."
            );
        }

        if (WebKeysCompras.isEmpty(
                nombreOriginal
        )) {

            throw new Exception(
                    "El pedido de cotización generado "
                            + "no posee un nombre válido."
            );
        }

        DocumentoLibraryComprasHelper
                .validarContextoDocumentLibrary(
                        serviceContext
                );

        DocumentoComprasCreado documento =
                null;

        try {
            documento =
                    DocumentoLibraryComprasHelper
                            .crearPedidoCotizacion(
                                    idRequerimiento,
                                    idPrestador,
                                    contenido,
                                    nombreOriginal,
                                    serviceContext
                            );

            if (documento == null
                    || documento.getGroupId() <= 0L
                    || documento.getFolderId() <= 0L
                    || documento.getFileEntryId() <= 0L
                    || WebKeysCompras.isEmpty(
                    documento.getUuid()
            )
                    || WebKeysCompras.isEmpty(
                    documento.getNombrePersistido()
            )
                    || WebKeysCompras.isEmpty(
                    documento.getTitulo()
            )) {

                throw new Exception(
                        "Document Library no devolvió una identidad "
                                + "válida para el pedido de cotización."
                );
            }

            RequerimientoCompraPedidoCotizacion asociacion =
                    new RequerimientoCompraPedidoCotizacion();

            asociacion.setIdRequerimiento(
                    Integer.valueOf(
                            idRequerimiento
                    )
            );

            asociacion.setIdPrestador(
                    Integer.valueOf(
                            idPrestador
                    )
            );

            asociacion.setDlGroupId(
                    Long.valueOf(
                            documento.getGroupId()
                    )
            );

            asociacion.setDlFolderId(
                    Long.valueOf(
                            documento.getFolderId()
                    )
            );

            asociacion.setDlFileEntryId(
                    Long.valueOf(
                            documento.getFileEntryId()
                    )
            );

            asociacion.setDlFileUuid(
                    documento.getUuid()
            );

            asociacion.setNombreOriginal(
                    nombreOriginal
            );

            asociacion.setNombrePersistido(
                    documento.getNombrePersistido()
            );

            asociacion.setTitulo(
                    documento.getTitulo()
            );

            int intento =
                    persistence
                            .registrarPedidoCotizacionDocumento(
                                    asociacion,
                                    normalizarUsuario(
                                            usuario
                                    )
                            );

            if (intento <= 0) {

                throw new Exception(
                        "No se obtuvo el intento asociado "
                                + "al pedido de cotización."
                );
            }

            asociacion.setIntento(
                    Integer.valueOf(
                            intento
                    )
            );

            return asociacion;

        } catch (Exception errorRegistro) {

            if (documento != null) {

                try {
                    DocumentoLibraryComprasHelper
                            .eliminarDocumentoCreado(
                                    documento
                            );

                } catch (Exception cleanupError) {

                    _log.error(
                            "No se pudo compensar el pedido "
                                    + "de cotización creado "
                                    + "antes de fallar su asociación. "
                                    + "idRequerimiento="
                                    + idRequerimiento
                                    + ", idPrestador="
                                    + idPrestador
                                    + ", fileEntryId="
                                    + documento.getFileEntryId(),
                            cleanupError
                    );
                }
            }

            throw errorRegistro;
        }
    }

    protected byte[] generarPedidoPresupuestoPdf(
            int idRequerimientoCompra) throws Exception {

        return new PdfServlet()
                .crearRequerimientoCompraComoAdjunto(
                        idRequerimientoCompra
                );
    }

    /**
     * Contrato canónico para el flujo actual: recupera todas las Órdenes
     * médicas activas del requerimiento.
     */
    protected List<OrdenMedicaAdjunta> recuperarOrdenesMedicasAdjuntas(
            int idRequerimientoCompra,
            long companyId) throws Exception {

        List<OrdenMedicaAdjunta> resultado =
                new ArrayList<OrdenMedicaAdjunta>();

        List<RequerimientoCompraPresupuesto> ordenesMedicas =
                getOrdenesMedicas(
                        idRequerimientoCompra
                );

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            if (_log.isInfoEnabled()) {
                _log.info(
                        "El requerimiento no posee Orden médica activa; "
                                + "se conserva el envío histórico con PDF. "
                                + "idRequerimiento="
                                + idRequerimientoCompra
                );
            }

            return resultado;
        }

        Set<Long> fileEntryIds =
                new HashSet<Long>();

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            RequerimientoCompraPresupuesto ordenMedica =
                    ordenesMedicas.get(i);

            DocumentoLibraryComprasHelper.validarRelacionOrdenMedica(
                    ordenMedica,
                    idRequerimientoCompra
            );

            long fileEntryId =
                    ordenMedica
                            .getDlFileEntryId()
                            .longValue();

            if (!fileEntryIds.add(
                    Long.valueOf(fileEntryId)
            )) {
                throw new Exception(
                        "El requerimiento contiene más de una "
                                + "Orden médica activa asociada al mismo "
                                + "documento de Document Library."
                );
            }

            DLFileEntry entry =
                    getFileEntryOrdenMedica(
                            fileEntryId
                    );

            DocumentoLibraryComprasHelper
                    .validarIdentidadOrdenMedicaPersistida(
                            ordenMedica,
                            entry,
                            companyId
                    );

            DocumentoLibraryComprasHelper.OrdenMedicaContenido documento =
                    DocumentoLibraryComprasHelper
                            .leerOrdenMedicaValidada(
                                    entry,
                                    ordenMedica.getNombreOriginal()
                            );

            resultado.add(
                    crearOrdenMedicaAdjunta(
                            documento.getContenido(),
                            documento.getNombreOriginal(),
                            documento.getContentType()
                    )
            );
        }

        return resultado;
    }

    /**
     * Contrato legacy conservado para tests y subclases existentes.
     *
     * Devuelve exclusivamente la primera Orden médica, reproduciendo el
     * comportamiento histórico. El flujo productivo actual no utiliza este
     * método para enviar cotizaciones.
     */
    protected OrdenMedicaAdjunta recuperarOrdenMedicaAdjunta(
            int idRequerimientoCompra,
            long companyId) throws Exception {

        RequerimientoCompraPresupuesto ordenMedica =
                getOrdenMedica(
                        idRequerimientoCompra
                );

        if (ordenMedica == null) {
            return null;
        }

        DocumentoLibraryComprasHelper.validarRelacionOrdenMedica(
                ordenMedica,
                idRequerimientoCompra
        );

        DLFileEntry entry =
                getFileEntryOrdenMedica(
                        ordenMedica
                                .getDlFileEntryId()
                                .longValue()
                );

        DocumentoLibraryComprasHelper
                .validarIdentidadOrdenMedicaPersistida(
                        ordenMedica,
                        entry,
                        companyId
                );

        DocumentoLibraryComprasHelper.OrdenMedicaContenido documento =
                DocumentoLibraryComprasHelper
                        .leerOrdenMedicaValidada(
                                entry,
                                ordenMedica.getNombreOriginal()
                        );

        return crearOrdenMedicaAdjunta(
                documento.getContenido(),
                documento.getNombreOriginal(),
                documento.getContentType()
        );
    }

    protected OrdenMedicaAdjunta crearOrdenMedicaAdjunta(
            byte[] contenido,
            String nombreOriginal,
            String contentType) {

        return new OrdenMedicaAdjunta(
                contenido,
                nombreOriginal,
                contentType
        );
    }

    protected List<RequerimientoCompraPresupuesto> getOrdenesMedicas(
            int idRequerimientoCompra) throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .listarOrdenesMedicas(
                        idRequerimientoCompra
                );
    }

    protected RequerimientoCompraPresupuesto getOrdenMedica(
            int idRequerimientoCompra) throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .getOrdenMedica(
                        idRequerimientoCompra
                );
    }

    protected DLFileEntry getFileEntryOrdenMedica(
            long fileEntryId) throws Exception {

        return DLFileEntryLocalServiceUtil.getDLFileEntry(
                fileEntryId
        );
    }

    /**
     * Firma legacy conservada. La regla canónica vive en
     * DocumentoLibraryComprasHelper.
     */
    protected String validarContenidoOrdenMedica(
            byte[] contenido,
            String nombreOriginal) throws Exception {

        return DocumentoLibraryComprasHelper
                .validarContenidoOrdenMedica(
                        contenido,
                        nombreOriginal
                );
    }

    protected RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .getRequerimientoCompra(
                        idRequerimientoCompra
                );
    }

    protected List<PrestadorCotizacion> listarPrestadoresCandidatos(
            int idRequerimientoCompra) throws Exception {

        return persistence.listarPrestadoresCandidatos(
                idRequerimientoCompra
        );
    }

    private void cargarDiagnosticoCandidatosConControl(
            RequerimientoCompra requerimiento,
            NotificacionCotizacionResultado resultado) {

        try {
            cargarDiagnosticoCandidatos(
                    requerimiento,
                    resultado
            );

        } catch (Exception e) {
            _log.warn(
                    "No se pudo calcular el diagnóstico "
                            + "de prestadores candidatos. "
                            + "idRequerimiento="
                            + (
                            requerimiento != null
                                    ? requerimiento
                                      .getIdRequerimientoCompra()
                                    : 0
                    ),
                    e
            );
        }
    }

    protected void cargarDiagnosticoCandidatos(
            RequerimientoCompra requerimiento,
            NotificacionCotizacionResultado resultado)
            throws Exception {

        if (requerimiento == null || resultado == null) {
            return;
        }

        CotizacionPrestadorDiagnostico diagnostico =
                persistence.diagnosticarPrestadores(
                        requerimiento.getIdRequerimientoCompra()
                );

        if (diagnostico != null) {
            resultado.setPrestadoresHabilitados(
                    diagnostico.getPrestadoresHabilitados()
            );
            resultado.setPrestadoresCompatiblesSector(
                    diagnostico.getPrestadoresCompatiblesSector()
            );
            resultado.setPrestadoresBloqueadosEstadoPrevio(
                    diagnostico.getPrestadoresBloqueadosEstadoPrevio()
            );
        }

        if (_log.isInfoEnabled()) {
            _log.info(
                    "Diagnóstico de prestadores candidatos. "
                            + "idRequerimiento="
                            + requerimiento.getIdRequerimientoCompra()
                            + ", sector="
                            + requerimiento.getIdSector()
                            + ", candidatos="
                            + resultado.getTotalCandidatos()
                            + ", habilitados="
                            + resultado.getPrestadoresHabilitados()
                            + ", compatiblesSector="
                            + resultado.getPrestadoresCompatiblesSector()
                            + ", bloqueadosEstadoPrevio="
                            + resultado.getPrestadoresBloqueadosEstadoPrevio()
            );
        }
    }

    protected ReservaCotizacionPrestador reservarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario) throws Exception {

        return persistence.reservarCotizacionPrestador(
                idRequerimientoCompra,
                idPrestador,
                normalizarUsuario(usuario)
        );
    }

    protected boolean registrarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario) throws Exception {

        ReservaCotizacionPrestador reserva =
                reservarCotizacionPrestador(
                        idRequerimientoCompra,
                        idPrestador,
                        usuario
                );

        return reserva != null
                && reserva.isReservado();
    }

    protected FinalizacionCotizacionPrestador finalizarCotizacionPrestadorConDetalle(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario) throws Exception {

        return persistence.finalizarCotizacionPrestador(
                idRequerimiento,
                idPrestador,
                estado,
                truncar(error, 4000),
                normalizarUsuario(usuario)
        );
    }

    protected boolean finalizarCotizacionPrestador(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error) throws Exception {

        FinalizacionCotizacionPrestador finalizacion =
                finalizarCotizacionPrestadorConDetalle(
                        idRequerimiento,
                        idPrestador,
                        estado,
                        error,
                        "sistema"
                );

        return finalizacion != null
                && finalizacion.isActualizado();
    }

    /**
     * Contrato canónico del envío actual: un único correo con 0..N Órdenes
     * médicas adicionales.
     */
    protected void enviarMail(
            long companyId,
            String[] emails,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<OrdenMedicaAdjunta> ordenesMedicas)
            throws Exception {

        List<CotizacionPrestadorMailHelper.AdjuntoOrdenMedica> adjuntos =
                new ArrayList<CotizacionPrestadorMailHelper.AdjuntoOrdenMedica>();

        for (int i = 0;
             ordenesMedicas != null
                     && i < ordenesMedicas.size();
             i++) {

            OrdenMedicaAdjunta ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null) {

                throw new Exception(
                        "Se encontró una Orden médica adjunta inválida."
                );
            }

            adjuntos.add(
                    new CotizacionPrestadorMailHelper.AdjuntoOrdenMedica(
                            ordenMedica.getContenido(),
                            ordenMedica.getNombreOriginal(),
                            ordenMedica.getContentType()
                    )
            );
        }

        mailHelper.enviar(
                emails,
                resolverEmailsCopiaCotizacion(),
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                adjuntos
        );
    }

    private boolean finalizarConControl(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario) {

        try {
            FinalizacionCotizacionPrestador finalizacion =
                    finalizarCotizacionPrestadorConDetalle(
                            idRequerimiento,
                            idPrestador,
                            estado,
                            error,
                            usuario
                    );

            if (finalizacion == null) {
                _log.error(
                        "La finalización no devolvió resultado. "
                                + "estadoSolicitado="
                                + estado
                                + ", idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );

                return false;
            }

            if (!finalizacion.isActualizado()) {
                _log.error(
                        "No se pudo persistir el estado final "
                                + "de la cotización. "
                                + "estadoSolicitado="
                                + estado
                                + ", estadoAnterior="
                                + finalizacion.getEstadoAnterior()
                                + ", estadoActual="
                                + finalizacion.getEstadoActual()
                                + ", motivo="
                                + finalizacion.getMotivo()
                                + ", idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );

                return false;
            }

            return true;

        } catch (Exception persistenciaError) {
            _log.error(
                    "Error persistiendo el estado final "
                            + "de la cotización. "
                            + "estadoSolicitado="
                            + estado
                            + ", idPrestador="
                            + idPrestador
                            + ", idRequerimiento="
                            + idRequerimiento,
                    persistenciaError
            );

            return false;
        }
    }

    private void registrarResultado(
            NotificacionCotizacionResultado resultado,
            PrestadorCotizacion prestador,
            String emailReal,
            String emailDestino,
            String tipoResultado,
            String etapa,
            String motivo,
            boolean emailRealInvalidoAdvertido) {

        if (resultado == null) {
            return;
        }

        NotificacionCotizacionDetalle detalle =
                new NotificacionCotizacionDetalle();

        if (prestador != null) {
            detalle.setIdPrestador(
                    prestador.getIdPrestador()
            );

            detalle.setPrestador(
                    prestador.getDescripcion()
            );
        }

        detalle.setEmailReal(
                normalizarEmail(emailReal)
        );

        detalle.setEmailDestino(
                normalizarEmail(emailDestino)
        );

        detalle.setResultado(tipoResultado);
        detalle.setEtapa(etapa);
        detalle.setMotivo(
                truncar(motivo, 1000)
        );

        detalle.setEmailRealInvalidoAdvertido(
                emailRealInvalidoAdvertido
        );

        resultado.agregarDetalle(detalle);
    }

    private String construirMotivoReservaNoOtorgada(
            ReservaCotizacionPrestador reserva) {

        if (reserva == null) {
            return "No se obtuvo la reserva de procesamiento.";
        }

        if (WebKeysCompras.ENVIO_ENVIADO.equals(
                reserva.getEstadoEnvio()
        )) {
            return "El prestador ya habia sido notificado. "
                    + "No se realizo un reenvio.";
        }

        if (WebKeysCompras.ENVIO_PROCESANDO.equals(
                reserva.getEstadoEnvio()
        )) {
            return "El prestador ya estaba siendo procesado "
                    + "por otra ejecución.";
        }

        if (!WebKeysCompras.isEmpty(
                reserva.getMotivoDescripcion()
        )) {
            return truncar(
                    reserva.getMotivoDescripcion(),
                    1000
            );
        }

        return "No se obtuvo la reserva de procesamiento.";
    }

    protected boolean redireccionQaHabilitada() {
        return REDIRECCION_QA_HABILITADA;
    }

    private String[] resolverEmailsCopiaCotizacion() {

        String configuracion =
                EMAIL_COPIA_COTIZACION != null
                        ? EMAIL_COPIA_COTIZACION
                        : "";

        String[] emails =
                configuracion.split(
                        ";",
                        -1
                );

        for (int i = 0; i < emails.length; i++) {
            emails[i] =
                    emails[i] != null
                            ? emails[i].trim()
                            : "";
        }

        if (_log.isInfoEnabled()) {
            _log.info(
                    "Destinatarios BCC de cotización resueltos. "
                            + "cantidadBcc="
                            + emails.length
                            + ", modoTemporal="
                            + redireccionQaHabilitada()
            );
        }

        return emails;
    }

    private String construirAsunto(
            RequerimientoCompra requerimiento) {

        return "Solicitud de cotización - Requerimiento #"
                + requerimiento
                .getIdRequerimientoCompra();
    }

    private String construirCuerpo(
            RequerimientoCompra requerimiento,
            PrestadorCotizacion prestador) {

        StringBuilder sb =
                new StringBuilder();

        sb.append("Estimado prestador");

        if (!WebKeysCompras.isEmpty(
                prestador.getDescripcion()
        )) {
            sb.append(" ");
            sb.append(
                    prestador.getDescripcionVisible()
            );
        }

        sb.append(",\n\n");

        sb.append(
                "OSPIM solicita cotización para el "
                        + "siguiente requerimiento de compra:"
        );

        sb.append("\n\n");

        appendDetalles(
                sb,
                requerimiento
        );

        if (!WebKeysCompras.isEmpty(
                requerimiento.getObservaciones()
        )) {
            sb.append("\nDetalle / observaciones:\n");

            sb.append(
                    requerimiento
                            .getObservacionesVisible()
            );

            sb.append("\n");
        }

        sb.append("\n");

        sb.append("Requerimiento: # ");
        sb.append(
                requerimiento
                        .getIdRequerimientoCompra()
        );
        sb.append("\n");

        sb.append("Sector: ");
        sb.append(
                requerimiento
                        .getSectorDescripcionVisible()
        );
        sb.append("\n");

        if (!WebKeysCompras.isEmpty(
                requerimiento.getAltaFechaAsString()
        )) {
            sb.append("Fecha: ");
            sb.append(
                    requerimiento.getAltaFechaAsString()
            );
            sb.append("\n");
        }

        sb.append(
                "\nPor favor responder este correo "
                        + "informando disponibilidad, "
                        + "plazo de entrega "
                        + "e importe de cotización a "
                        + "kfernandez@ospim.org.ar."
        );

        sb.append(
                "\n\nLos presupuestos se deben presentar en formato .PDF, "
                        + "fijando como plazo límite de entrega "
                        + "las próximas 48 horas, "
                        + "con horario tope de recepción a las 18:00 hs."
        );

        return sb.toString();
    }

    private void appendDetalles(
            StringBuilder sb,
            RequerimientoCompra requerimiento) {

        List<RequerimientoCompraDetalle> detalles =
                requerimiento.getDetalles();

        if (detalles == null
                || detalles.isEmpty()) {

            return;
        }

        sb.append("\nItems:\n");

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle =
                    detalles.get(i);

            sb.append("- ");

            String tipoItem =
                    detalle.getTipoItemNormalizado();

            String codigoItem =
                    detalle.getCodigoItemVisible();

            String descripcionItem =
                    detalle.getDescripcionItemVisible();

            if (!WebKeysCompras.isEmpty(tipoItem)
                    && !"NOMENCLADOR".equalsIgnoreCase(
                    tipoItem.trim()
            )) {

                sb.append(tipoItem);
                sb.append(" | ");
            }

            if (!WebKeysCompras.isEmpty(codigoItem)) {
                sb.append(codigoItem);
                sb.append(" - ");
            }

            if (!WebKeysCompras.isEmpty(descripcionItem)) {
                sb.append(
                        descripcionItem
                );
            } else {
                sb.append(
                        "Item sin descripción"
                );
            }

            sb.append(" | Cantidad: ");
            sb.append(
                    detalle.getCantidadString()
            );

            if (!WebKeysCompras.isEmpty(
                    detalle.getObservaciones()
            )) {
                sb.append(" | Descripción: ");

                sb.append(
                        detalle.getObservacionesVisible()
                );
            }

            sb.append("\n");
        }
    }

    private void validarParametros(
            int idRequerimientoCompra,
            long companyId) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (companyId <= 0) {
            throw new Exception(
                    "No se pudo determinar la empresa del portal."
            );
        }
    }

    private void validarRequerimiento(
            RequerimientoCompra requerimiento)
            throws Exception {

        if (requerimiento == null) {
            throw new Exception(
                    "No se encontró el requerimiento de compra."
            );
        }

        if (requerimiento.getIdSector() == null
                || requerimiento
                .getIdSector()
                .intValue() <= 0) {

            throw new Exception(
                    "El requerimiento no tiene sector informado."
            );
        }

        if (requerimiento.getEstado()
                != WebKeysCompras.ESTADO_PENDIENTE
                && requerimiento.getEstado()
                != WebKeysCompras.ESTADO_A_COTIZAR) {

            throw new Exception(
                    "El requerimiento no se encuentra "
                            + "en estado PENDIENTE o A COTIZAR."
            );
        }
    }

    private boolean esEmailValido(
            String email) {

        String emailNormalizado =
                normalizarEmail(email);

        return emailNormalizado != null
                && EMAIL_PATTERN
                .matcher(emailNormalizado)
                .matches();
    }

    private String normalizarEmail(
            String email) {

        if (email == null) {
            return null;
        }

        String resultado =
                email.trim();

        return resultado.length() > 0
                ? resultado
                : null;
    }

    private String construirDetalleError(
            Exception e) {

        if (e == null) {
            return "Error no informado.";
        }

        String mensaje =
                e.getMessage();

        if (mensaje == null
                || mensaje.trim().length() == 0) {

            mensaje =
                    e.getClass().getName();

        } else {
            mensaje =
                    e.getClass().getName()
                            + ": "
                            + mensaje.trim();
        }

        mensaje =
                SENSITIVE_DETAIL_PATTERN
                        .matcher(mensaje)
                        .replaceAll("$1=<omitido>");

        mensaje =
                mensaje
                        .replace('\r', ' ')
                        .replace('\n', ' ')
                        .replace('\t', ' ');

        return truncar(
                mensaje,
                4000
        );
    }

    private String truncar(
            String value,
            int maxLength) {

        if (value == null) {
            return null;
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(
                0,
                maxLength
        );
    }

    private String normalizarUsuario(
            String usuario) {

        if (usuario == null
                || usuario.trim().length() == 0) {

            return "sistema";
        }

        return truncar(
                usuario.trim(),
                100
        );
    }

    protected static final class OrdenMedicaAdjunta {

        private final byte[] contenido;
        private final String nombreOriginal;
        private final String contentType;

        protected OrdenMedicaAdjunta(
                byte[] contenido,
                String nombreOriginal,
                String contentType) {

            this.contenido = contenido;
            this.nombreOriginal = nombreOriginal;
            this.contentType = contentType;
        }

        protected byte[] getContenido() {
            return contenido;
        }

        protected String getNombreOriginal() {
            return nombreOriginal;
        }

        protected String getContentType() {
            return contentType;
        }
    }

    private String[] resolverEmailsPrestador(
            String emailsConcatenados) {

        String normalizado =
                normalizarEmail(
                        emailsConcatenados
                );

        if (normalizado == null) {
            return new String[0];
        }

        String[] candidatos =
                normalizado.split(
                        ";"
                );

        List<String> resultado =
                new ArrayList<String>();

        Set<String> encontrados =
                new HashSet<String>();

        for (int i = 0;
             i < candidatos.length;
             i++) {

            String email =
                    normalizarEmail(
                            candidatos[i]
                    );

            if (email == null) {
                continue;
            }

            if (!esEmailValido(
                    email
            )) {
                continue;
            }

            String clave =
                    email.toLowerCase();

            if (encontrados.add(
                    clave
            )) {

                resultado.add(
                        email
                );
            }
        }

        return resultado.toArray(
                new String[
                        resultado.size()
                        ]
        );
    }

    private String[] resolverEmailsDestino(
            String[] emailsReales,
            boolean modoTemporal) {

        if (modoTemporal) {

            String emailQa =
                    normalizarEmail(
                            EMAIL_DESTINO_QA
                    );

            if (emailQa == null) {
                return new String[0];
            }

            return new String[] {
                    emailQa
            };
        }

        return emailsReales != null
                ? emailsReales
                : new String[0];
    }

    private String unirEmails(
            String[] emails) {

        if (emails == null
                || emails.length == 0) {

            return null;
        }

        StringBuilder sb =
                new StringBuilder();

        for (int i = 0;
             i < emails.length;
             i++) {

            if (i > 0) {
                sb.append(";");
            }

            sb.append(
                    emails[i]
            );
        }

        return sb.toString();
    }


}
