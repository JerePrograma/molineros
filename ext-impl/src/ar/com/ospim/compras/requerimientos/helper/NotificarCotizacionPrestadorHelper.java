package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.NotificarCotizacionPrestadorServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.ServiceContext;
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

    private static final String CONFIG_REQUERIMIENTO_REDIRECCION_QA_HABILITADA =
            "REQUERIMIENTO_REDIRECCION_QA_HABILITADA";

    private static final String CONFIG_REQUERIMIENTO_EMAIL_CC =
            "REQUERIMIENTO_EMAIL_CC";

    private static final String CONFIG_REQUERIMIENTO_EMAIL_QA =
            "REQUERIMIENTO_EMAIL_QA";

    private static final String CONFIG_REQUERIMIENTO_EMAIL_RESPUESTA =
            "REQUERIMIENTO_EMAIL_RESPUESTA";

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
                                + ", compatiblesRubro="
                                + resultado
                                .getPrestadoresCompatiblesSector()
                                + ", bloqueadosEstadoPrevio="
                                + resultado
                                .getPrestadoresBloqueadosEstadoPrevio()
                );
            }

            return resultado;
        }

        DocumentoLibraryComprasHelper
                .validarContextoDocumentLibrary(
                        serviceContext
                );

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

        boolean copiaCotizacionEnviada =
                false;

        for (int i = 0;
             i < candidatos.size();
             i++) {

            copiaCotizacionEnviada =
                    procesarPrestador(
                            requerimiento,
                            candidatos.get(i),
                            usuario,
                            companyId,
                            serviceContext,
                            resultado,
                            pedidoPresupuestoPdf,
                            nombrePedidoPresupuestoPdf,
                            ordenesMedicasAdjuntas,
                            copiaCotizacionEnviada
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

    private boolean procesarPrestador(
            RequerimientoCompra requerimiento,
            PrestadorCotizacion prestador,
            String usuario,
            long companyId,
            ServiceContext serviceContext,
            NotificacionCotizacionResultado resultado,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<OrdenMedicaAdjunta> ordenesMedicasAdjuntas,
            boolean copiaCotizacionEnviada) {

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

            return copiaCotizacionEnviada;
        }

        int idRequerimiento =
                requerimiento
                        .getIdRequerimientoCompra();

        int idPrestador =
                prestador
                        .getIdPrestador();

        if (_log.isDebugEnabled()) {

            _log.debug(
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

            return copiaCotizacionEnviada;
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

            return copiaCotizacionEnviada;
        }

        if (!reserva.isReservado()) {

            String motivo =
                    construirMotivoReservaNoOtorgada(
                            reserva
                    );

            if (_log.isDebugEnabled()) {

                _log.debug(
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

            return copiaCotizacionEnviada;
        }

        /*
         * Desde aca la fila queda PROCESANDO y esta ejecucion
         * posee la reserva exclusiva.
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

        boolean emailRealInvalidoAdvertido =
                modoTemporal
                        && prestadorSinEmail;

        /*
         * ==========================================================
         * 2. VALIDACION DE LOS DESTINATARIOS REALES
         * ==========================================================
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

            return copiaCotizacionEnviada;
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

        String[] emailsDestino =
                resolverEmailsDestino(
                        emailsReales,
                        modoTemporal
                );

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

            return copiaCotizacionEnviada;
        }

        if (modoTemporal
                && _log.isDebugEnabled()) {

            _log.debug(
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

            return copiaCotizacionEnviada;
        }

        /*
         * La copia BCC se reserva por requerimiento.
         *
         * El boolean local evita consultas innecesarias durante
         * la misma ejecución. La base garantiza la exclusividad
         * entre ejecuciones y reintentos.
         */
        boolean incluirCopiaCotizacion =
                false;

        if (!copiaCotizacionEnviada) {

            try {

                incluirCopiaCotizacion =
                        NotificarCotizacionPrestadorServiceUtil
                                .reservarCopiaCotizacion(
                                        idRequerimiento
                                );

            } catch (Exception e) {

                _log.error(
                        "No se pudo reservar el envio de la copia "
                                + "de cotizacion. "
                                + "idRequerimiento="
                                + idRequerimiento,
                        e
                );
            }
        }

        /*
         * ==========================================================
         * 4. ENVIO DEL CORREO
         * ==========================================================
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

            enviarMail(
                    companyId,
                    emailsDestino,
                    asunto,
                    cuerpo,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf,
                    ordenesMedicasAdjuntas,
                    incluirCopiaCotizacion
            );

            if (incluirCopiaCotizacion) {

                copiaCotizacionEnviada =
                        true;
            }

        } catch (
                CotizacionPrestadorMailHelper
                        .EnvioParcialException e) {

            /*
             * Si el SMTP alcanzó a aceptar la copia BCC,
             * la reserva queda consumida.
             *
             * Si no fue aceptada, se libera para que el próximo
             * prestador o reintento pueda volver a intentar la copia.
             */
            if (incluirCopiaCotizacion) {

                if (e.isCopiaEnviada()) {

                    copiaCotizacionEnviada =
                            true;

                } else {

                    try {

                        NotificarCotizacionPrestadorServiceUtil
                                .liberarCopiaCotizacion(
                                        idRequerimiento
                                );

                    } catch (Exception liberarError) {

                        _log.error(
                                "No se pudo liberar la reserva "
                                        + "de copia de cotizacion. "
                                        + "idRequerimiento="
                                        + idRequerimiento,
                                liberarError
                        );
                    }
                }
            }

            String advertencia =
                    "ADVERTENCIA: envio parcial. "
                            + e.getDestinatariosEnviados()
                            + " de "
                            + e.getDestinatariosTotales()
                            + " destinatarios fueron aceptados.";

            boolean persistido =
                    finalizarConControl(
                            idRequerimiento,
                            idPrestador,
                            WebKeysCompras.ENVIO_ENVIADO,
                            advertencia,
                            usuario
                    );

            registrarResultado(
                    resultado,
                    prestador,
                    emailsRealesDetalle,
                    emailsDestinoDetalle,
                    persistido
                            ? NotificacionCotizacionDetalle
                            .RESULTADO_ENVIADO
                            : NotificacionCotizacionDetalle
                            .RESULTADO_ERROR,
                    persistido
                            ? "ENVIO_PARCIAL"
                            : "PERSISTENCIA",
                    persistido
                            ? advertencia
                            : "El envio parcial no pudo registrarse.",
                    emailRealInvalidoAdvertido
            );

            return copiaCotizacionEnviada;

        } catch (Exception e) {

            /*
             * El envío no fue confirmado.
             *
             * Si esta ejecución había reservado la copia BCC,
             * se libera para permitir otro intento.
             */
            if (incluirCopiaCotizacion) {

                try {

                    NotificarCotizacionPrestadorServiceUtil
                            .liberarCopiaCotizacion(
                                    idRequerimiento
                            );

                } catch (Exception liberarError) {

                    _log.error(
                            "No se pudo liberar la reserva "
                                    + "de copia de cotizacion. "
                                    + "idRequerimiento="
                                    + idRequerimiento,
                            liberarError
                    );
                }
            }

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

            return copiaCotizacionEnviada;
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

            return copiaCotizacionEnviada;
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

        return copiaCotizacionEnviada;
    }

    protected RequerimientoCompraPedidoCotizacion
    registrarPedidoCotizacionActual(
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
                    NotificarCotizacionPrestadorServiceUtil
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
            int idRequerimientoCompra)
            throws Exception {

        return new PdfServlet()
                .crearRequerimientoCompraComoAdjunto(
                        idRequerimientoCompra
                );
    }

    protected List<OrdenMedicaAdjunta>
    recuperarOrdenesMedicasAdjuntas(
            int idRequerimientoCompra,
            long companyId)
            throws Exception {

        List<OrdenMedicaAdjunta> resultado =
                new ArrayList<OrdenMedicaAdjunta>();

        List<RequerimientoCompraPresupuesto> ordenesMedicas =
                getOrdenesMedicas(
                        idRequerimientoCompra
                );

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            if (_log.isDebugEnabled()) {

                _log.debug(
                        "El requerimiento no posee un adjunto activo; "
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

            DocumentoLibraryComprasHelper
                    .validarRelacionOrdenMedica(
                            ordenMedica,
                            idRequerimientoCompra
                    );

            long fileEntryId =
                    ordenMedica
                            .getDlFileEntryId()
                            .longValue();

            if (!fileEntryIds.add(
                    Long.valueOf(
                            fileEntryId
                    )
            )) {

                throw new Exception(
                        "El requerimiento contiene más de un "
                                + "adjunto activo asociado al mismo "
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

            DocumentoLibraryComprasHelper
                    .OrdenMedicaContenido documento =
                    DocumentoLibraryComprasHelper
                            .leerOrdenMedicaValidada(
                                    entry,
                                    ordenMedica
                                            .getNombreOriginal()
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

    protected List<RequerimientoCompraPresupuesto>
    getOrdenesMedicas(
            int idRequerimientoCompra)
            throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .listarOrdenesMedicas(
                        idRequerimientoCompra
                );
    }

    protected DLFileEntry getFileEntryOrdenMedica(
            long fileEntryId)
            throws Exception {

        return DLFileEntryLocalServiceUtil
                .getDLFileEntry(
                        fileEntryId
                );
    }

    protected RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra)
            throws Exception {

        return BusquedaRequerimientoCompraServiceUtil
                .getRequerimientoCompra(
                        idRequerimientoCompra
                );
    }

    protected List<PrestadorCotizacion>
    listarPrestadoresCandidatos(
            int idRequerimientoCompra)
            throws Exception {

        return NotificarCotizacionPrestadorServiceUtil
                .listarPrestadoresCandidatos(
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

        if (requerimiento == null
                || resultado == null) {

            return;
        }

        CotizacionPrestadorDiagnostico diagnostico =
                NotificarCotizacionPrestadorServiceUtil
                        .diagnosticarPrestadores(
                                requerimiento
                                        .getIdRequerimientoCompra()
                        );

        if (diagnostico != null) {

            resultado.setPrestadoresHabilitados(
                    diagnostico
                            .getPrestadoresHabilitados()
            );

            resultado.setPrestadoresCompatiblesSector(
                    diagnostico
                            .getPrestadoresCompatiblesSector()
            );

            resultado.setPrestadoresBloqueadosEstadoPrevio(
                    diagnostico
                            .getPrestadoresBloqueadosEstadoPrevio()
            );
        }

        if (_log.isDebugEnabled()) {

            _log.debug(
                    "Diagnóstico de prestadores candidatos. "
                            + "idRequerimiento="
                            + requerimiento
                            .getIdRequerimientoCompra()
                            + ", sector="
                            + requerimiento.getIdSector()
                            + ", candidatos="
                            + resultado.getTotalCandidatos()
                            + ", habilitados="
                            + resultado.getPrestadoresHabilitados()
                            + ", compatiblesRubro="
                            + resultado
                            .getPrestadoresCompatiblesSector()
                            + ", bloqueadosEstadoPrevio="
                            + resultado
                            .getPrestadoresBloqueadosEstadoPrevio()
            );
        }
    }

    protected ReservaCotizacionPrestador
    reservarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario)
            throws Exception {

        return NotificarCotizacionPrestadorServiceUtil
                .reservarCotizacionPrestador(
                        idRequerimientoCompra,
                        idPrestador,
                        normalizarUsuario(
                                usuario
                        )
                );
    }

    protected FinalizacionCotizacionPrestador
    finalizarCotizacionPrestadorConDetalle(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario)
            throws Exception {

        return NotificarCotizacionPrestadorServiceUtil
                .finalizarCotizacionPrestador(
                        idRequerimiento,
                        idPrestador,
                        estado,
                        truncar(
                                error,
                                4000
                        ),
                        normalizarUsuario(
                                usuario
                        )
                );
    }

    private void enviarMail(
            long companyId,
            String[] emails,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<OrdenMedicaAdjunta> ordenesMedicas,
            boolean incluirCopiaCotizacion)
            throws Exception {

        List<CotizacionPrestadorMailHelper
                .AdjuntoOrdenMedica> adjuntos =
                new ArrayList<CotizacionPrestadorMailHelper
                        .AdjuntoOrdenMedica>();

        for (int i = 0;
             ordenesMedicas != null
                     && i < ordenesMedicas.size();
             i++) {

            OrdenMedicaAdjunta ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null) {

                throw new Exception(
                        "Se encontró un adjunto inválido."
                );
            }

            adjuntos.add(
                    new CotizacionPrestadorMailHelper
                            .AdjuntoOrdenMedica(
                            ordenMedica.getContenido(),
                            ordenMedica.getNombreOriginal(),
                            ordenMedica.getContentType()
                    )
            );
        }

        mailHelper.enviar(
                emails,
                incluirCopiaCotizacion
                        ? resolverEmailsCopiaCotizacion()
                        : new String[0],
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
                normalizarEmail(
                        emailReal
                )
        );

        detalle.setEmailDestino(
                normalizarEmail(
                        emailDestino
                )
        );

        detalle.setResultado(
                tipoResultado
        );

        detalle.setEtapa(
                etapa
        );

        detalle.setMotivo(
                truncar(
                        motivo,
                        1000
                )
        );

        detalle.setEmailRealInvalidoAdvertido(
                emailRealInvalidoAdvertido
        );

        resultado.agregarDetalle(
                detalle
        );
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

        String valor =
                TraeListasServiceUtil.getSystemConfig(
                        CONFIG_REQUERIMIENTO_REDIRECCION_QA_HABILITADA
                );

        if (valor == null) {

            return false;
        }

        return Boolean.parseBoolean(
                valor.trim()
        );
    }

    private String[] resolverEmailsCopiaCotizacion() {

        String configuracion =
                TraeListasServiceUtil.getSystemConfig(
                        CONFIG_REQUERIMIENTO_EMAIL_CC
                );

        String[] emails =
                resolverEmails(
                        configuracion
                );

        if (_log.isDebugEnabled()) {

            _log.debug(
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

        String emailRespuesta =
                resolverEmailRespuestaCotizacion();

        StringBuilder sb =
                new StringBuilder();

        sb.append(
                "Estimado prestador"
        );

        if (!WebKeysCompras.isEmpty(
                prestador.getDescripcion()
        )) {

            sb.append(
                    " "
            );

            sb.append(
                    prestador
                            .getDescripcionVisible()
            );
        }

        sb.append(
                ",\n\n"
        );

        sb.append(
                "OSPIM solicita cotización para el "
                        + "siguiente requerimiento de compra:"
        );

        sb.append(
                "\n\n"
        );

        appendDetalles(
                sb,
                requerimiento
        );

        if (!WebKeysCompras.isEmpty(
                requerimiento.getObservaciones()
        )) {

            sb.append(
                    "\nDetalle / observaciones:\n"
            );

            sb.append(
                    requerimiento
                            .getObservacionesVisible()
            );

            sb.append(
                    "\n"
            );
        }

        sb.append(
                "\n"
        );

        sb.append(
                "Requerimiento: # "
        );

        sb.append(
                requerimiento
                        .getIdRequerimientoCompra()
        );

        sb.append(
                "\n"
        );

        sb.append(
                "Sector: "
        );

        sb.append(
                requerimiento
                        .getSectorDescripcionVisible()
        );

        sb.append(
                "\n"
        );

        if (!WebKeysCompras.isEmpty(
                requerimiento
                        .getAltaFechaAsString()
        )) {

            sb.append(
                    "Fecha: "
            );

            sb.append(
                    requerimiento
                            .getAltaFechaAsString()
            );

            sb.append(
                    "\n"
            );
        }

        sb.append(
                "\nPor favor responder este correo "
                        + "informando disponibilidad, "
                        + "plazo de entrega "
                        + "e importe de cotización"
        );

        if (!WebKeysCompras.isEmpty(
                emailRespuesta
        )) {

            sb.append(
                    " a "
            );

            sb.append(
                    emailRespuesta
            );
        }

        sb.append(
                "."
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

        sb.append(
                "\nItems:\n"
        );

        for (int i = 0;
             i < detalles.size();
             i++) {

            RequerimientoCompraDetalle detalle =
                    detalles.get(i);

            sb.append(
                    "- "
            );

            String tipoItem =
                    detalle
                            .getTipoItemNormalizado();

            String codigoItem =
                    detalle
                            .getCodigoItemVisible();

            String descripcionItem =
                    detalle
                            .getDescripcionItemVisible();

            if (!WebKeysCompras.isEmpty(
                    tipoItem
            )
                    && !"NOMENCLADOR".equalsIgnoreCase(
                    tipoItem.trim()
            )) {

                sb.append(
                        tipoItem
                );

                sb.append(
                        " | "
                );
            }

            if (!WebKeysCompras.isEmpty(
                    codigoItem
            )) {

                sb.append(
                        codigoItem
                );

                sb.append(
                        " - "
                );
            }

            if (!WebKeysCompras.isEmpty(
                    descripcionItem
            )) {

                sb.append(
                        descripcionItem
                );

            } else {

                sb.append(
                        "Item sin descripción"
                );
            }

            sb.append(
                    " | Cantidad: "
            );

            sb.append(
                    detalle
                            .getCantidadString()
            );

            if (!WebKeysCompras.isEmpty(
                    detalle.getObservaciones()
            )) {

                sb.append(
                        " | Descripción: "
                );

                sb.append(
                        detalle
                                .getObservacionesVisible()
                );
            }

            sb.append(
                    "\n"
            );
        }
    }

    private void validarParametros(
            int idRequerimientoCompra,
            long companyId)
            throws Exception {

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
                normalizarEmail(
                        email
                );

        return emailNormalizado != null
                && EMAIL_PATTERN
                .matcher(
                        emailNormalizado
                )
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
                    e.getClass()
                            .getName();

        } else {

            mensaje =
                    e.getClass()
                            .getName()
                            + ": "
                            + mensaje.trim();
        }

        mensaje =
                SENSITIVE_DETAIL_PATTERN
                        .matcher(
                                mensaje
                        )
                        .replaceAll(
                                "$1=<omitido>"
                        );

        mensaje =
                mensaje
                        .replace(
                                '\r',
                                ' '
                        )
                        .replace(
                                '\n',
                                ' '
                        )
                        .replace(
                                '\t',
                                ' '
                        );

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

        if (value.length()
                <= maxLength) {

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
                || usuario.trim()
                .length() == 0) {

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

            this.contenido =
                    contenido;

            this.nombreOriginal =
                    nombreOriginal;

            this.contentType =
                    contentType;
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

        return resolverEmails(
                emailsConcatenados
        );
    }

    private String[] resolverEmailsDestino(
            String[] emailsReales,
            boolean modoTemporal) {

        if (modoTemporal) {

            String configuracionQa =
                    TraeListasServiceUtil
                            .getSystemConfig(
                                    CONFIG_REQUERIMIENTO_EMAIL_QA
                            );

            return resolverEmails(
                    configuracionQa
            );
        }

        return resolverEmails(
                emailsReales
        );
    }

    private String unirEmails(
            String[] emails) {

        String[] emailsValidos =
                resolverEmails(
                        emails
                );

        if (emailsValidos.length == 0) {

            return null;
        }

        StringBuilder sb =
                new StringBuilder();

        for (int i = 0;
             i < emailsValidos.length;
             i++) {

            if (i > 0) {

                sb.append(
                        ";"
                );
            }

            sb.append(
                    emailsValidos[i]
            );
        }

        return sb.toString();
    }

    private String resolverEmailRespuestaCotizacion() {

        String configuracion =
                TraeListasServiceUtil
                        .getSystemConfig(
                                CONFIG_REQUERIMIENTO_EMAIL_RESPUESTA
                        );

        String[] emails =
                resolverEmails(
                        configuracion
                );

        if (emails.length == 0) {

            _log.warn(
                    "No hay un email válido configurado en "
                            + CONFIG_REQUERIMIENTO_EMAIL_RESPUESTA
                            + ". Se continuará el envío "
                            + "sin informar un email de respuesta."
            );

            return null;
        }

        if (emails.length > 1) {

            _log.warn(
                    "Existe más de un email configurado en "
                            + CONFIG_REQUERIMIENTO_EMAIL_RESPUESTA
                            + ". Se utilizará únicamente el primero."
            );
        }

        return emails[0];
    }

    private String[] resolverEmails(
            String emailsConcatenados) {

        String normalizado =
                normalizarEmail(
                        emailsConcatenados
                );

        if (normalizado == null) {

            return new String[0];
        }

        return resolverEmails(
                normalizado.split(
                        ";",
                        -1
                )
        );
    }

    private String[] resolverEmails(
            String[] candidatos) {

        if (candidatos == null
                || candidatos.length == 0) {

            return new String[0];
        }

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

            /*
             * Tolera:
             *
             * correo@dominio.com;
             * correo@dominio.com;;
             * ;correo@dominio.com
             * espacios entre separadores
             */
            if (email == null) {

                continue;
            }

            /*
             * Un email inválido dentro de una lista no debe
             * inutilizar los demás destinatarios válidos.
             */
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
}