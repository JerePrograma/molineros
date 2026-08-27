package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.mail.MailUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class CotizacionPrestadorMailHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    CotizacionPrestadorMailHelper.class
            );

    public void enviar(
            String emailDestino,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf)
            throws Exception {

        enviar(
                emailDestino,
                null,
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf
        );
    }

    public void enviar(
            String emailDestino,
            String[] emailsCopia,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf)
            throws Exception {

        enviarInterno(
                new String[] {
                        emailDestino
                },
                emailsCopia,
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                null
        );
    }

    /**
     * Contrato legacy de una única Orden médica.
     */
    public void enviar(
            String emailDestino,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            byte[] ordenMedica,
            String nombreOrdenMedica,
            String contentTypeOrdenMedica)
            throws Exception {

        enviar(
                emailDestino,
                null,
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                ordenMedica,
                nombreOrdenMedica,
                contentTypeOrdenMedica
        );
    }

    /**
     * Contrato legacy de una única Orden médica + BCC.
     */
    public void enviar(
            String emailDestino,
            String[] emailsCopia,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            byte[] ordenMedica,
            String nombreOrdenMedica,
            String contentTypeOrdenMedica)
            throws Exception {

        validarOrdenMedica(
                ordenMedica,
                nombreOrdenMedica,
                contentTypeOrdenMedica
        );

        List<AdjuntoOrdenMedica> ordenesMedicas =
                new ArrayList<AdjuntoOrdenMedica>(1);

        ordenesMedicas.add(
                new AdjuntoOrdenMedica(
                        ordenMedica,
                        nombreOrdenMedica,
                        contentTypeOrdenMedica
                )
        );

        enviarInterno(
                new String[] {
                        emailDestino
                },
                emailsCopia,
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                ordenesMedicas
        );
    }

    /**
     * Contrato canónico: un único mensaje SMTP con 0..N Órdenes médicas.
     */
    public void enviar(
            String emailDestino,
            String[] emailsCopia,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<AdjuntoOrdenMedica> ordenesMedicas)
            throws Exception {

        enviarInterno(
                new String[] {
                        emailDestino
                },
                emailsCopia,
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                ordenesMedicas
        );
    }

    public void enviar(
            String[] emailsDestino,
            String[] emailsCopia,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<AdjuntoOrdenMedica> ordenesMedicas)
            throws Exception {

        enviarInterno(
                emailsDestino,
                emailsCopia,
                asunto,
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                ordenesMedicas
        );
    }

    private void enviarInterno(
            String[] emailsDestino,
            String[] emailsCopia,
            String asunto,
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<AdjuntoOrdenMedica> ordenesMedicas)
            throws Exception {

        validarParametros(
                emailsDestino,
                emailsCopia,
                asunto,
                cuerpo
        );

        ReportesAutomaticosConfiguracion configuracion =
                ReportesServiceUtil.getConfiguracion();

        if (configuracion == null) {
            throw new Exception(
                    "No se encontró la configuración "
                            + "de correo de reportes automáticos."
            );
        }

        Properties propiedades =
                new Properties();

        propiedades.putAll(
                MailUtils.getMailServerProperties()
        );

        propiedades.setProperty(
                "mail.smtp.starttls.enable",
                "true"
        );

        propiedades.setProperty(
                "mail.smtp.starttls.required",
                "true"
        );

        propiedades.setProperty(
                "mail.smtp.ssl.protocols",
                "TLSv1.2"
        );

        String usuarioSmtp =
                normalizar(
                        propiedades.getProperty(
                                "mail.smtp.user"
                        )
                );

        String password =
                configuracion.getPass();

        String remitente =
                normalizar(
                        configuracion.getMailFrom()
                );

        if (usuarioSmtp == null) {
            throw new Exception(
                    "La configuración SMTP no tiene "
                            + "usuario informado."
            );
        }

        if (isEmpty(password)) {
            throw new Exception(
                    "La configuración de correo "
                            + "no tiene contraseña informada."
            );
        }

        if (remitente == null) {
            remitente = usuarioSmtp;
        }

        validarEmail(
                remitente,
                "remitente"
        );

        Session session =
                Session.getInstance(
                        propiedades,
                        null
                );

        session.setDebug(
                Boolean.parseBoolean(
                        propiedades.getProperty(
                                "mail.debug",
                                "false"
                        )
                )
        );

        MimeMessage mensaje =
                new MimeMessage(session);

        mensaje.setFrom(
                new InternetAddress(
                        remitente,
                        true
                )
        );

        agregarDestinatariosPrincipales(
                mensaje,
                emailsDestino
        );

        agregarDestinatariosCopia(
                mensaje,
                emailsCopia
        );

        mensaje.setSubject(
                asunto,
                "UTF-8"
        );

        Multipart multipart =
                construirMultipart(
                        cuerpo,
                        pedidoPresupuestoPdf,
                        nombrePedidoPresupuestoPdf,
                        ordenesMedicas
                );

        mensaje.setContent(
                multipart
        );

        mensaje.setSentDate(
                new Date()
        );

        Transport transport = null;
        boolean enviado = false;

        try {
            transport =
                    session.getTransport("smtp");

            transport.connect(
                    usuarioSmtp,
                    password
            );

            transport.sendMessage(
                    mensaje,
                    mensaje.getAllRecipients()
            );

            enviado = true;

            if (_log.isDebugEnabled()) {
                _log.debug(
                        "Correo de cotización aceptado por el servidor SMTP. "
                                + "ordenesMedicasAdjuntas="
                                + (
                                ordenesMedicas != null
                                        ? ordenesMedicas.size()
                                        : 0
                        )
                );
            }

        } catch (Exception e) {
            _log.error(
                    "Falló el envío SMTP de cotización.",
                    e
            );

            throw e;

        } finally {
            cerrarTransport(
                    transport,
                    enviado
            );
        }
    }

    /**
     * Contrato legacy conservado.
     */
    protected Multipart construirMultipart(
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            byte[] ordenMedica,
            String nombreOrdenMedica,
            String contentTypeOrdenMedica)
            throws Exception {

        List<AdjuntoOrdenMedica> ordenesMedicas =
                null;

        boolean incluyeOrdenMedica =
                ordenMedica != null
                        || nombreOrdenMedica != null
                        || contentTypeOrdenMedica != null;

        if (incluyeOrdenMedica) {
            validarOrdenMedica(
                    ordenMedica,
                    nombreOrdenMedica,
                    contentTypeOrdenMedica
            );

            ordenesMedicas =
                    new ArrayList<AdjuntoOrdenMedica>(1);

            ordenesMedicas.add(
                    new AdjuntoOrdenMedica(
                            ordenMedica,
                            nombreOrdenMedica,
                            contentTypeOrdenMedica
                    )
            );
        }

        return construirMultipart(
                cuerpo,
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf,
                ordenesMedicas
        );
    }

    protected Multipart construirMultipart(
            String cuerpo,
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf,
            List<AdjuntoOrdenMedica> ordenesMedicas)
            throws Exception {

        if (cuerpo == null) {
            throw new Exception(
                    "Debe informar el cuerpo del correo."
            );
        }

        validarPedidoPresupuesto(
                pedidoPresupuestoPdf,
                nombrePedidoPresupuestoPdf
        );

        validarOrdenesMedicas(
                ordenesMedicas
        );

        MimeBodyPart parteTexto =
                new MimeBodyPart();

        parteTexto.setText(
                cuerpo,
                "UTF-8"
        );

        Multipart multipart =
                new MimeMultipart();

        multipart.addBodyPart(
                parteTexto
        );

        multipart.addBodyPart(
                crearParteAdjunto(
                        pedidoPresupuestoPdf,
                        nombrePedidoPresupuestoPdf,
                        "application/pdf"
                )
        );

        for (int i = 0;
             ordenesMedicas != null && i < ordenesMedicas.size();
             i++) {

            AdjuntoOrdenMedica ordenMedica =
                    ordenesMedicas.get(i);

            multipart.addBodyPart(
                    crearParteAdjunto(
                            ordenMedica.getContenido(),
                            ordenMedica.getNombre(),
                            ordenMedica.getContentType()
                    )
            );
        }

        return multipart;
    }

    private MimeBodyPart crearParteAdjunto(
            byte[] contenido,
            String nombre,
            String contentType) throws Exception {

        MimeBodyPart parte =
                new MimeBodyPart();

        parte.setDataHandler(
                new DataHandler(
                        new AdjuntoDataSource(
                                contenido,
                                nombre,
                                contentType
                        )
                )
        );

        parte.setFileName(
                MimeUtility.encodeText(
                        nombre,
                        "UTF-8",
                        null
                )
        );

        return parte;
    }

    private void validarParametros(
            String[] emailsDestino,
            String[] emailsCopia,
            String asunto,
            String cuerpo)
            throws Exception {

        validarEmailsDestino(
                emailsDestino
        );

        validarEmailsCopia(
                emailsCopia
        );

        if (isEmpty(asunto)) {
            throw new Exception(
                    "Debe informar el asunto del correo."
            );
        }

        if (cuerpo == null) {
            throw new Exception(
                    "Debe informar el cuerpo del correo."
            );
        }

    }

    private void validarEmailsDestino(
            String[] emailsDestino)
            throws Exception {

        if (emailsDestino == null
                || emailsDestino.length == 0) {

            throw new Exception(
                    "Debe informar al menos un email destino."
            );
        }

        for (int i = 0;
             i < emailsDestino.length;
             i++) {

            if (isEmpty(
                    emailsDestino[i]
            )) {

                throw new Exception(
                        "Existe un email destino vacío."
                );
            }

            validarEmail(
                    emailsDestino[i],
                    "destino"
            );
        }
    }

    private void validarPedidoPresupuesto(
            byte[] pedidoPresupuestoPdf,
            String nombrePedidoPresupuestoPdf)
            throws Exception {

        if (pedidoPresupuestoPdf == null
                || pedidoPresupuestoPdf.length == 0) {

            throw new Exception(
                    "El pedido de presupuesto PDF "
                            + "no fue generado."
            );
        }

        if (isEmpty(nombrePedidoPresupuestoPdf)
                || !nombrePedidoPresupuestoPdf
                .toLowerCase(Locale.ENGLISH)
                .endsWith(".pdf")) {

            throw new Exception(
                    "El nombre del adjunto PDF "
                            + "no es válido."
            );
        }
    }

    private void validarOrdenesMedicas(
            List<AdjuntoOrdenMedica> ordenesMedicas)
            throws Exception {

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            return;
        }

        for (int i = 0; i < ordenesMedicas.size(); i++) {
            AdjuntoOrdenMedica ordenMedica =
                    ordenesMedicas.get(i);

            if (ordenMedica == null) {
                throw new Exception(
                        "Existe una Orden médica adjunta inválida."
                );
            }

            validarOrdenMedica(
                    ordenMedica.getContenido(),
                    ordenMedica.getNombre(),
                    ordenMedica.getContentType()
            );
        }
    }

    private void validarOrdenMedica(
            byte[] ordenMedica,
            String nombreOrdenMedica,
            String contentTypeOrdenMedica)
            throws Exception {

        if (ordenMedica == null
                || ordenMedica.length == 0) {

            throw new Exception(
                    "La Orden médica no fue recuperada."
            );
        }

        if (isEmpty(nombreOrdenMedica)
                || !nombreOrdenMedica.equals(
                nombreOrdenMedica.trim()
        )
                || nombreOrdenMedica.indexOf("..") >= 0
                || nombreOrdenMedica.indexOf('/') >= 0
                || nombreOrdenMedica.indexOf('\\') >= 0
                || nombreOrdenMedica.matches(
                ".*\\p{Cntrl}.*"
        )) {

            throw new Exception(
                    "El nombre original de la Orden médica no es válido."
            );
        }

        /*
         * Vuelve a comprobar el contenido real antes de construir
         * el MimeBodyPart. Esto también protege los overloads legacy
         * que pueden ser invocados sin pasar previamente por el flujo
         * normal de recuperación desde Document Library.
         */
        String contentTypeValidado =
                DocumentoLibraryComprasHelper
                        .validarContenidoOrdenMedica(
                                ordenMedica,
                                nombreOrdenMedica
                        );

        if (!contentTypeValidado.equals(
                contentTypeOrdenMedica
        )) {

            throw new Exception(
                    "El tipo MIME de la Orden médica "
                            + "no coincide con su contenido real."
            );
        }
    }

    private void validarEmail(
            String email,
            String tipo) throws Exception {

        try {
            InternetAddress direccion =
                    new InternetAddress(
                            email.trim(),
                            true
                    );

            direccion.validate();

        } catch (Exception e) {
            throw new Exception(
                    "Email "
                            + tipo
                            + " inválido: "
                            + email,
                    e
            );
        }
    }

    private void cerrarTransport(
            Transport transport,
            boolean enviado) {

        if (transport == null) {
            return;
        }

        try {
            transport.close();

        } catch (Exception e) {
            if (enviado) {
                _log.warn(
                        "El correo fue enviado, pero falló "
                                + "el cierre de la conexión SMTP.",
                        e
                );

            } else {
                _log.debug(
                        "Falló el cierre de la conexión SMTP.",
                        e
                );
            }
        }
    }

    private String normalizar(
            String value) {

        if (value == null) {
            return null;
        }

        String resultado =
                value.trim();

        return resultado.length() > 0
                ? resultado
                : null;
    }

    private boolean isEmpty(
            String value) {

        return normalizar(value) == null;
    }

    private void validarEmailsCopia(
            String[] emailsCopia) throws Exception {

        if (emailsCopia == null
                || emailsCopia.length == 0) {

            return;
        }

        for (int i = 0;
             i < emailsCopia.length;
             i++) {

            if (isEmpty(emailsCopia[i])) {
                throw new Exception(
                        "Existe un email de copia vacío."
                );
            }

            validarEmail(
                    emailsCopia[i],
                    "de copia"
            );
        }
    }

    private void agregarDestinatariosCopia(
            MimeMessage mensaje,
            String[] emailsCopia) throws Exception {

        if (emailsCopia == null
                || emailsCopia.length == 0) {

            return;
        }

        for (int i = 0;
             i < emailsCopia.length;
             i++) {

            mensaje.addRecipient(
                    Message.RecipientType.BCC,
                    new InternetAddress(
                            emailsCopia[i].trim(),
                            true
                    )
            );
        }
    }

    public static final class AdjuntoOrdenMedica {

        private final byte[] contenido;
        private final String nombre;
        private final String contentType;

        public AdjuntoOrdenMedica(
                byte[] contenido,
                String nombre,
                String contentType) {

            this.contenido = contenido;
            this.nombre = nombre;
            this.contentType = contentType;
        }

        public byte[] getContenido() {
            return contenido;
        }

        public String getNombre() {
            return nombre;
        }

        public String getContentType() {
            return contentType;
        }
    }

    private static final class AdjuntoDataSource
            implements DataSource {

        private final byte[] contenido;
        private final String nombre;
        private final String contentType;

        private AdjuntoDataSource(
                byte[] contenido,
                String nombre,
                String contentType) {

            this.contenido =
                    contenido;

            this.nombre =
                    nombre;

            this.contentType =
                    contentType;
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(
                    contenido
            );
        }

        public OutputStream getOutputStream()
                throws IOException {

            throw new IOException(
                    "El adjunto es de solo lectura."
            );
        }

        public String getContentType() {
            return contentType;
        }

        public String getName() {
            return nombre;
        }
    }

    private void agregarDestinatariosPrincipales(
            MimeMessage mensaje,
            String[] emailsDestino)
            throws Exception {

        for (int i = 0;
             emailsDestino != null
                     && i < emailsDestino.length;
             i++) {

            mensaje.addRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(
                            emailsDestino[i].trim(),
                            true
                    )
            );
        }
    }
}
