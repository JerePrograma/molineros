package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionDetalle;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.servlets.PdfServlet;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class NotificarCotizacionPrestadorServiceImpl {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    NotificarCotizacionPrestadorServiceImpl.class
            );

    /*
     * Modo temporal de QA.
     *
     * Mientras permanezca en true, todos los correos se envían
     * al destinatario fijo configurado.
     *
     * El email real del prestador igualmente se captura y valida.
     * Si es inválido, queda registrado como advertencia.
     */
    private static final boolean USAR_EMAIL_DESTINO_TEMPORAL = true;

    private static final String EMAIL_DESTINO_TEMPORAL =
            "acomas@ospim.org.ar";

    private static final String EMAIL_COPIA_COTIZACION = TraeListasServiceUtil.getSystemConfig("REQUERIMIENTO_EMAIL_CC");

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
            long companyId) throws Exception {

        validarParametros(
                idRequerimientoCompra,
                companyId
        );

        RequerimientoCompra requerimiento =
                getRequerimientoCompra(
                        idRequerimientoCompra
                );

        validarRequerimiento(requerimiento);

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
                        "No hay prestadores pendientes de notificacion. "
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
         * El PDF se genera antes de reservar al primer prestador.
         *
         * Si Jasper falla, no queda ninguna fila PROCESANDO
         * ni se realizan envíos parciales.
         */
        byte[] pedidoPresupuestoPdf =
                generarPedidoPresupuestoPdf(
                        idRequerimientoCompra
                );

        String nombrePedidoPresupuestoPdf =
                "PedidoPresupuesto_"
                        + idRequerimientoCompra
                        + ".pdf";

        /*
         * La Orden medica se recupera y valida completamente antes de
         * reservar al primer prestador. Una ausencia total corresponde al
         * contrato historico; una asociacion existente pero inconsistente
         * debe fallar cerrada sin dejar filas PROCESANDO.
         */
        OrdenMedicaAdjunta ordenMedicaAdjunta =
                recuperarOrdenMedicaAdjunta(
                        idRequerimientoCompra,
                        companyId
                );

        for (int i = 0; i < candidatos.size(); i++) {
            procesarPrestador(
                    requerimiento,
                    candidatos.get(i),
                    usuario,
                    companyId,
                    resultado,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf,
                    ordenMedicaAdjunta
            );
        }

        if (resultado.getPendientesSinClasificar() > 0) {
            _log.error(
                    "El proceso de notificacion finalizo "
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
            NotificacionCotizacionResultado resultado,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            OrdenMedicaAdjunta ordenMedicaAdjunta) {

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
                requerimiento.getIdRequerimientoCompra();

        int idPrestador =
                prestador.getIdPrestador();

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

        try {
            reserva =
                    reservarCotizacionPrestador(
                            idRequerimiento,
                            idPrestador,
                            usuario
                    );

        } catch (Exception e) {
            /*
             * El detalle técnico completo queda exclusivamente
             * en el log. No debe exponerse en la interfaz.
             */
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
                    prestador.getEmail(),
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
                    prestador.getEmail(),
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

        String emailReservadoNormalizado =
                normalizarEmail(
                        reserva.getEmailDestino()
                );

        String emailDestino =
                resolverEmailDestino(
                        emailReservadoNormalizado
                );

        boolean emailRealInvalido =
                !esEmailValido(
                        emailReservadoNormalizado
                );

        /*
         * En modo QA, un email real inválido no bloquea el envío,
         * porque se utiliza EMAIL_DESTINO_TEMPORAL.
         *
         * Sin embargo, queda registrado como advertencia.
         */
        boolean emailRealInvalidoAdvertido =
                USAR_EMAIL_DESTINO_TEMPORAL
                        && emailRealInvalido;

        if (USAR_EMAIL_DESTINO_TEMPORAL) {
            if (emailRealInvalido) {
                _log.warn(
                        "El email real reservado del prestador "
                                + "es inexistente o invalido. "
                                + "El envio continuara redirigido "
                                + "al destinatario temporal. "
                                + "idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );
            }

            if (_log.isInfoEnabled()) {
                _log.info(
                        "Modo temporal de notificacion activo. "
                                + "El correo sera redirigido "
                                + "al destinatario fijo de QA. "
                                + "idPrestador="
                                + idPrestador
                                + ", idRequerimiento="
                                + idRequerimiento
                );
            }

        } else if (emailRealInvalido) {
            String errorTecnico =
                    "El email real reservado del prestador "
                            + "es inexistente o invalido.";

            _log.warn(
                    "No se intentara enviar la cotizacion "
                            + "porque el email real es invalido. "
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
                                + "valido registrado.";
            } else {
                motivoUsuario =
                        "El prestador no tiene un email valido "
                                + "y el resultado no pudo registrarse. "
                                + "Contacte a Sistemas.";
            }

            registrarResultado(
                    resultado,
                    prestador,
                    emailReservadoNormalizado,
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

        /*
         * Esta validación contempla especialmente un error de
         * configuración del destinatario temporal.
         */
        if (!esEmailValido(emailDestino)) {
            String errorTecnico;

            if (USAR_EMAIL_DESTINO_TEMPORAL) {
                errorTecnico =
                        "El email destino temporal de QA "
                                + "es inexistente o invalido.";
            } else {
                errorTecnico =
                        "El email destino efectivo del prestador "
                                + "es inexistente o invalido.";
            }

            _log.warn(
                    "No se intentara enviar la cotizacion "
                            + "porque el destinatario efectivo "
                            + "es invalido. "
                            + "modoTemporal="
                            + USAR_EMAIL_DESTINO_TEMPORAL
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

            if (USAR_EMAIL_DESTINO_TEMPORAL) {
                motivoUsuario =
                        "El destinatario configurado para las pruebas "
                                + "no es valido. Contacte a Sistemas.";
            } else {
                motivoUsuario =
                        "El prestador no tiene un email "
                                + "valido registrado.";
            }

            if (!persistido) {
                motivoUsuario +=
                        " El resultado tampoco pudo registrarse.";
            }

            registrarResultado(
                    resultado,
                    prestador,
                    emailReservadoNormalizado,
                    emailDestino,
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

        /*
         * La llamada al servicio de correo debe ocurrir antes
         * de persistir ENVIADO.
         */
        try {
            String asunto = construirAsunto(requerimiento);
            String cuerpo = construirCuerpo(
                    requerimiento,
                    prestador
            );

            if (ordenMedicaAdjunta == null) {
                enviarMail(
                        companyId,
                        emailDestino,
                        asunto,
                        cuerpo,
                        pedidoPresupuestoPdf,
                        nombrePedidoPresupuestoPdf
                );
            } else {
                enviarMail(
                        companyId,
                        emailDestino,
                        asunto,
                        cuerpo,
                        pedidoPresupuestoPdf,
                        nombrePedidoPresupuestoPdf,
                        ordenMedicaAdjunta.getContenido(),
                        ordenMedicaAdjunta.getNombreOriginal(),
                        ordenMedicaAdjunta.getContentType()
                );
            }

        } catch (Exception e) {
            /*
             * El detalle técnico se utiliza para el log y para
             * registrar internamente el estado ERROR.
             *
             * La interfaz recibe solamente un mensaje operativo.
             */
            String detalleError =
                    construirDetalleError(e);

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
                            + ", estadoErrorPersistido="
                            + persistido,
                    e
            );

            registrarResultado(
                    resultado,
                    prestador,
                    emailReservadoNormalizado,
                    emailDestino,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "ENVIO",
                    motivoUsuario,
                    emailRealInvalidoAdvertido
            );

            return;
        }

        /*
         * El helper de correo aceptó el mensaje.
         * Recién ahora se intenta persistir ENVIADO.
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
            );

            registrarResultado(
                    resultado,
                    prestador,
                    emailReservadoNormalizado,
                    emailDestino,
                    NotificacionCotizacionDetalle.RESULTADO_ERROR,
                    "PERSISTENCIA",
                    motivoUsuario,
                    emailRealInvalidoAdvertido
            );

            return;
        }

        String motivoExito;

        if (USAR_EMAIL_DESTINO_TEMPORAL) {
            motivoExito =
                    "Correo enviado al destinatario temporal "
                            + "de QA y resultado confirmado.";

            if (emailRealInvalidoAdvertido) {
                motivoExito +=
                        " El email real del prestador "
                                + "debe revisarse.";
            }
        } else {
            motivoExito =
                    "Correo enviado y resultado confirmado.";
        }

        registrarResultado(
                resultado,
                prestador,
                emailReservadoNormalizado,
                emailDestino,
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
                            + USAR_EMAIL_DESTINO_TEMPORAL
                            + ", estadoEnvio=ENVIADO"
            );
        }
    }

    protected byte[] generarPedidoPresupuestoPdf(
            int idRequerimientoCompra) throws Exception {

        return new PdfServlet()
                .crearRequerimientoCompraComoAdjunto(
                        idRequerimientoCompra
                );
    }

    protected OrdenMedicaAdjunta recuperarOrdenMedicaAdjunta(
            int idRequerimientoCompra,
            long companyId) throws Exception {

        RequerimientoCompraPresupuesto ordenMedica =
                getOrdenMedica(
                        idRequerimientoCompra
                );

        /*
         * Los requerimientos historicos pueden no tener Orden medica.
         * El alta nueva atomica incorporada por Compras no puede producir
         * esa ausencia, por lo que no se utiliza una heuristica de fecha o ID.
         */
        if (ordenMedica == null) {
            if (_log.isInfoEnabled()) {
                _log.info(
                        "El requerimiento no posee Orden medica activa; "
                                + "se conserva el envio historico con PDF. "
                                + "idRequerimiento="
                                + idRequerimientoCompra
                );
            }

            return null;
        }

        validarRelacionOrdenMedica(
                ordenMedica,
                idRequerimientoCompra
        );

        DLFileEntry entry = getFileEntryOrdenMedica(
                ordenMedica.getDlFileEntryId().longValue()
        );

        validarIdentidadOrdenMedica(
                ordenMedica,
                entry,
                companyId
        );

        long maximoTamano = obtenerMaximoTamanoDocumento();

        if (entry.getSize() <= 0
                || entry.getSize() > maximoTamano) {

            throw new Exception(
                    "La Orden médica persistida tiene un tamaño inválido."
            );
        }

        byte[] contenido = leerOrdenMedica(
                entry,
                maximoTamano
        );

        String contentType = validarContenidoOrdenMedica(
                contenido,
                ordenMedica.getNombreOriginal()
        );

        return crearOrdenMedicaAdjunta(
                contenido,
                ordenMedica.getNombreOriginal(),
                contentType
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

    protected String validarContenidoOrdenMedica(
            byte[] contenido,
            String nombreOriginal) throws Exception {

        if (contenido == null || contenido.length == 0) {
            throw new Exception(
                    "La Orden médica persistida está vacía."
            );
        }

        validarNombreOriginalOrdenMedica(
                nombreOriginal
        );

        String nombreNormalizado =
                nombreOriginal.toLowerCase(Locale.ENGLISH);

        if (nombreNormalizado.endsWith(".png")) {
            if (contenido.length < 8
                    || (contenido[0] & 0xFF) != 0x89
                    || contenido[1] != 0x50
                    || contenido[2] != 0x4E
                    || contenido[3] != 0x47
                    || contenido[4] != 0x0D
                    || contenido[5] != 0x0A
                    || contenido[6] != 0x1A
                    || contenido[7] != 0x0A) {

                throw new Exception(
                        "La Orden médica PNG no conserva una firma válida."
                );
            }

            return "image/png";
        }

        if (nombreNormalizado.endsWith(".jpg")
                || nombreNormalizado.endsWith(".jpeg")) {

            if (contenido.length < 3
                    || (contenido[0] & 0xFF) != 0xFF
                    || (contenido[1] & 0xFF) != 0xD8
                    || (contenido[2] & 0xFF) != 0xFF) {

                throw new Exception(
                        "La Orden médica JPEG no conserva una firma válida."
                );
            }

            return "image/jpeg";
        }

        throw new Exception(
                "La Orden médica persistida no es JPEG/JPG ni PNG."
        );
    }

    private void validarRelacionOrdenMedica(
            RequerimientoCompraPresupuesto ordenMedica,
            int idRequerimientoCompra) throws Exception {

        if (ordenMedica.getIdRequerimiento() == null
                || ordenMedica.getIdRequerimiento().intValue()
                != idRequerimientoCompra
                || ordenMedica.getTipoDocumento() == null
                || ordenMedica.getTipoDocumento().intValue()
                != RequerimientoCompraPresupuesto
                        .TIPO_DOCUMENTO_ORDEN_MEDICA
                || ordenMedica.getIdPrestador() != null
                || !ordenMedica.isActivo()
                || ordenMedica.getFechaDocumento() == null
                || ordenMedica.getDlGroupId() == null
                || ordenMedica.getDlGroupId().longValue() <= 0L
                || ordenMedica.getDlFolderId() == null
                || ordenMedica.getDlFolderId().longValue() <= 0L
                || ordenMedica.getDlFileEntryId() == null
                || ordenMedica.getDlFileEntryId().longValue() <= 0L
                || WebKeysCompras.isEmpty(
                        ordenMedica.getDlFileUuid()
                )
                || WebKeysCompras.isEmpty(
                        ordenMedica.getNombrePersistido()
                )
                || WebKeysCompras.isEmpty(
                        ordenMedica.getNombreOriginal()
                )
                || !DocumentoLibraryComprasHelper
                        .TITULO_ORDEN_MEDICA.equals(
                                ordenMedica.getTitulo()
                        )) {

            throw new Exception(
                    "La asociación de la Orden médica activa es inconsistente."
            );
        }
    }

    private void validarIdentidadOrdenMedica(
            RequerimientoCompraPresupuesto ordenMedica,
            DLFileEntry entry,
            long companyId) throws Exception {

        boolean coincide = entry != null
                && entry.getCompanyId() == companyId
                && entry.getFileEntryId()
                == ordenMedica.getDlFileEntryId().longValue()
                && entry.getGroupId()
                == ordenMedica.getDlGroupId().longValue()
                && entry.getFolderId()
                == ordenMedica.getDlFolderId().longValue()
                && ordenMedica.getDlFileUuid().equals(
                entry.getUuid()
        )
                && ordenMedica.getNombrePersistido().equals(
                entry.getName()
        );

        if (!coincide) {
            throw new Exception(
                    "La Orden médica no coincide con su identidad en Document Library."
            );
        }
    }

    private byte[] leerOrdenMedica(
            DLFileEntry entry,
            long maximoTamano) throws Exception {

        InputStream input = null;

        try {
            input = DLFileEntryLocalServiceUtil.getFileAsStream(
                    entry.getCompanyId(),
                    entry.getUserId(),
                    entry.getFolderId(),
                    entry.getName(),
                    entry.getVersion()
            );

            if (input == null) {
                throw new Exception(
                        "Document Library no devolvió la Orden médica."
                );
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream(
                    entry.getSize()
            );
            byte[] buffer = new byte[8192];
            long total = 0L;
            int cantidad;

            while ((cantidad = input.read(buffer)) >= 0) {
                if (cantidad == 0) {
                    continue;
                }

                total += cantidad;

                if (total > maximoTamano) {
                    throw new Exception(
                            "La Orden médica supera dl.file.max.size."
                    );
                }

                output.write(buffer, 0, cantidad);
            }

            byte[] contenido = output.toByteArray();

            if (contenido.length != entry.getSize()) {
                throw new Exception(
                        "El tamaño leído de la Orden médica no coincide con Document Library."
                );
            }

            return contenido;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception closeError) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                                "No se pudo cerrar la lectura de la Orden médica.",
                                closeError
                        );
                    }
                }
            }
        }
    }

    private long obtenerMaximoTamanoDocumento() throws Exception {
        String valor = PropsUtil.get("dl.file.max.size");

        if (WebKeysCompras.isEmpty(valor)) {
            return Long.MAX_VALUE;
        }

        try {
            long maximo = Long.parseLong(valor.trim());
            return maximo > 0L ? maximo : Long.MAX_VALUE;
        } catch (NumberFormatException e) {
            throw new Exception(
                    "La configuración dl.file.max.size no es válida.",
                    e
            );
        }
    }

    private void validarNombreOriginalOrdenMedica(
            String nombreOriginal) throws Exception {

        if (WebKeysCompras.isEmpty(nombreOriginal)
                || nombreOriginal.length() > 255
                || !nombreOriginal.equals(nombreOriginal.trim())
                || nombreOriginal.indexOf("..") >= 0
                || nombreOriginal.indexOf('/') >= 0
                || nombreOriginal.indexOf('\\') >= 0
                || nombreOriginal.matches(".*\\p{Cntrl}.*")) {

            throw new Exception(
                    "El nombre original de la Orden médica es inválido."
            );
        }
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

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        List<PrestadorCotizacion> candidatos =
                new ArrayList<PrestadorCotizacion>();

        try {
            String sql =
                    "{call compras."
                            + "listar_prestadores_notificacion_cotizacion"
                            + "(?)}";

            con = obtenerConexion();
            stmt = con.prepareCall(sql);

            stmt.setInt(
                    1,
                    idRequerimientoCompra
            );

            rs = stmt.executeQuery();

            while (rs.next()) {
                candidatos.add(
                        mapPrestadorCotizacion(rs)
                );
            }

            return candidatos;

        } catch (Exception e) {
            _log.error(
                    "Error listando prestadores candidatos. "
                            + "idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );

            throw e;

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
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
                    "No se pudo calcular el diagnostico "
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

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql =
                    "{call compras."
                            + "diagnosticar_prestadores_"
                            + "notificacion_cotizacion"
                            + "(?)}";

            con = obtenerConexion();
            stmt = con.prepareCall(sql);

            stmt.setInt(
                    1,
                    requerimiento
                            .getIdRequerimientoCompra()
            );

            rs = stmt.executeQuery();

            if (rs.next()) {
                resultado.setPrestadoresHabilitados(
                        rs.getInt(
                                "prestadores_habilitados"
                        )
                );

                resultado.setPrestadoresCompatiblesSector(
                        rs.getInt(
                                "prestadores_compatibles_sector"
                        )
                );

                resultado.setPrestadoresBloqueadosEstadoPrevio(
                        rs.getInt(
                                "prestadores_bloqueados_estado_previo"
                        )
                );
            }

            if (_log.isInfoEnabled()) {
                _log.info(
                        "Diagnostico de prestadores candidatos. "
                                + "idRequerimiento="
                                + requerimiento
                                .getIdRequerimientoCompra()
                                + ", sector="
                                + requerimiento.getIdSector()
                                + ", candidatos="
                                + resultado.getTotalCandidatos()
                                + ", habilitados="
                                + resultado
                                .getPrestadoresHabilitados()
                                + ", compatiblesSector="
                                + resultado
                                .getPrestadoresCompatiblesSector()
                                + ", bloqueadosEstadoPrevio="
                                + resultado
                                .getPrestadoresBloqueadosEstadoPrevio()
                );
            }

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    protected ReservaCotizacionPrestador reservarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql =
                    "{call compras."
                            + "reservar_notificacion_"
                            + "cotizacion_prestador"
                            + "(?,?,?)}";

            con = obtenerConexion();
            stmt = con.prepareCall(sql);

            stmt.setInt(
                    1,
                    idRequerimientoCompra
            );

            stmt.setInt(
                    2,
                    idPrestador
            );

            stmt.setString(
                    3,
                    normalizarUsuario(usuario)
            );

            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new Exception(
                        "La funcion de reserva no devolvio filas."
                );
            }

            ReservaCotizacionPrestador reserva =
                    new ReservaCotizacionPrestador();

            reserva.setReservado(
                    rs.getBoolean("reservado")
            );

            reserva.setEstadoEnvio(
                    rs.getString("estado_envio")
            );

            reserva.setEmailDestino(
                    rs.getString("email_destino")
            );

            reserva.setMotivoCodigo(
                    rs.getString("motivo_codigo")
            );

            reserva.setMotivoDescripcion(
                    rs.getString("motivo_descripcion")
            );

            return reserva;

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    /*
     * Método conservado para compatibilidad con tests o
     * subclases que utilizaban la firma anterior.
     */
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

    protected FinalizacionCotizacionPrestador
    finalizarCotizacionPrestadorConDetalle(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql =
                    "{call compras."
                            + "finalizar_notificacion_"
                            + "cotizacion_prestador"
                            + "(?,?,?,?,?)}";

            con = obtenerConexion();
            stmt = con.prepareCall(sql);

            stmt.setInt(
                    1,
                    idRequerimiento
            );

            stmt.setInt(
                    2,
                    idPrestador
            );

            stmt.setString(
                    3,
                    estado
            );

            stmt.setString(
                    4,
                    truncar(error, 4000)
            );

            stmt.setString(
                    5,
                    normalizarUsuario(usuario)
            );

            rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new Exception(
                        "La funcion de finalizacion "
                                + "no devolvio filas."
                );
            }

            FinalizacionCotizacionPrestador finalizacion =
                    new FinalizacionCotizacionPrestador();

            finalizacion.setActualizado(
                    rs.getBoolean("actualizado")
            );

            finalizacion.setEstadoAnterior(
                    rs.getString("estado_anterior")
            );

            finalizacion.setEstadoActual(
                    rs.getString("estado_actual")
            );

            finalizacion.setMotivo(
                    rs.getString("motivo")
            );

            return finalizacion;

        } finally {
            cerrar(rs);
            ConnectionHelper.cerrar(stmt, con);
        }
    }

    /*
     * Método conservado para compatibilidad con tests o
     * código existente que utilizaba la firma anterior.
     */
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

    /*
     * companyId se conserva para no romper llamadas ni tests.
     *
     * El helper SMTP específico de Compras actualmente
     * no utiliza companyId.
     */
    protected void enviarMail(
            long companyId,
            String email,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf)
            throws Exception {

        mailHelper.enviar(
                email,
                resolverEmailsCopiaCotizacion(),
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf
        );
    }

    protected void enviarMail(
            long companyId,
            String email,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            byte[] ordenMedica,
            String nombreOrdenMedica,
            String contentTypeOrdenMedica)
            throws Exception {

        mailHelper.enviar(
                email,
                resolverEmailsCopiaCotizacion(),
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                ordenMedica,
                nombreOrdenMedica,
                contentTypeOrdenMedica
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
                        "La finalizacion no devolvio resultado. "
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
                                + "de la cotizacion. "
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
                            + "de la cotizacion. "
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

        /*
         * El motivo guardado en el resultado es apto para interfaz.
         * Las excepciones técnicas completas se registran en el log
         * o en la persistencia interna, pero no aquí.
         */
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
                    + "por otra ejecucion.";
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

    private String resolverEmailDestino(
            String emailReservado) {

        if (USAR_EMAIL_DESTINO_TEMPORAL) {
            return normalizarEmail(
                    EMAIL_DESTINO_TEMPORAL
            );
        }

        return normalizarEmail(emailReservado);
    }

    private String[] resolverEmailsCopiaCotizacion() {

        /*
         * Mientras el envío esté redirigido al destinatario temporal
         * de QA, no se incluyen las copias productivas.
         */
        if (USAR_EMAIL_DESTINO_TEMPORAL) {
            return new String[0];
        }

        String configuracion =
                EMAIL_COPIA_COTIZACION != null
                        ? EMAIL_COPIA_COTIZACION
                        : "";

        /*
         * El -1 conserva también elementos vacíos al final.
         *
         * Esto es deliberado: una configuración como
         * "uno@dominio.com;" o "uno@dominio.com;;dos@dominio.com"
         * debe llegar al MailHelper como inválida, en lugar de enviar
         * silenciosamente el correo con una lista incompleta.
         */
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

        return emails;
    }

    private String construirAsunto(
            RequerimientoCompra requerimiento) {

        return "Solicitud de cotizacion - Requerimiento #"
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
                "OSPIM solicita cotizacion para el "
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
                        "Item sin descripcion"
                );
            }

            sb.append(" | Cantidad: ");
            sb.append(
                    detalle.getCantidadString()
            );

            if (!WebKeysCompras.isEmpty(
                    detalle.getObservaciones()
            )) {
                sb.append(" | Descripcion: ");

                sb.append(
                        detalle.getObservacionesVisible()
                );
            }

            sb.append("\n");
        }
    }

    private PrestadorCotizacion mapPrestadorCotizacion(
            ResultSet rs) throws Exception {

        PrestadorCotizacion prestador =
                new PrestadorCotizacion();

        prestador.setIdPrestador(
                rs.getInt("id_prestador")
        );

        prestador.setDescripcion(
                rs.getString("descripcion")
        );

        prestador.setCuit(
                rs.getString("cuit")
        );

        prestador.setEmail(
                rs.getString("email")
        );

        prestador.setIdTipoPrestador(
                rs.getInt("id_tipo_prestador")
        );

        prestador.setTipoPrestador(
                rs.getString("tipo_prestador")
        );

        return prestador;
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
                    "No se encontro el requerimiento de compra."
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

    private Connection obtenerConexion()
            throws Exception {

        Connection con =
                ConnectionHelper.getConnection();

        if (con == null) {
            throw new Exception(
                    "No se pudo obtener conexion "
                            + "a la base de datos."
            );
        }

        return con;
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

    /*
     * Este método genera un detalle técnico para logs o
     * persistencia interna. Su resultado no debe mostrarse
     * directamente en la interfaz.
     */
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

    private void cerrar(
            ResultSet rs) {

        if (rs == null) {
            return;
        }

        try {
            rs.close();

        } catch (Exception e) {
            _log.debug(
                    "No se pudo cerrar ResultSet.",
                    e
            );
        }
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

    protected static class ReservaCotizacionPrestador {

        private boolean reservado;
        private String estadoEnvio;
        private String emailDestino;
        private String motivoCodigo;
        private String motivoDescripcion;

        public ReservaCotizacionPrestador() {
        }

        public boolean isReservado() {
            return reservado;
        }

        public void setReservado(
                boolean reservado) {

            this.reservado = reservado;
        }

        public String getEstadoEnvio() {
            return estadoEnvio;
        }

        public void setEstadoEnvio(
                String estadoEnvio) {

            this.estadoEnvio = estadoEnvio;
        }

        public String getEmailDestino() {
            return emailDestino;
        }

        public void setEmailDestino(
                String emailDestino) {

            this.emailDestino = emailDestino;
        }

        public String getMotivoCodigo() {
            return motivoCodigo;
        }

        public void setMotivoCodigo(
                String motivoCodigo) {

            this.motivoCodigo = motivoCodigo;
        }

        public String getMotivoDescripcion() {
            return motivoDescripcion;
        }

        public void setMotivoDescripcion(
                String motivoDescripcion) {

            this.motivoDescripcion =
                    motivoDescripcion;
        }
    }

    protected static class FinalizacionCotizacionPrestador {

        private boolean actualizado;
        private String estadoAnterior;
        private String estadoActual;
        private String motivo;

        public FinalizacionCotizacionPrestador() {
        }

        public boolean isActualizado() {
            return actualizado;
        }

        public void setActualizado(
                boolean actualizado) {

            this.actualizado = actualizado;
        }

        public String getEstadoAnterior() {
            return estadoAnterior;
        }

        public void setEstadoAnterior(
                String estadoAnterior) {

            this.estadoAnterior = estadoAnterior;
        }

        public String getEstadoActual() {
            return estadoActual;
        }

        public void setEstadoActual(
                String estadoActual) {

            this.estadoActual = estadoActual;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(
                String motivo) {

            this.motivo = motivo;
        }
    }
}
