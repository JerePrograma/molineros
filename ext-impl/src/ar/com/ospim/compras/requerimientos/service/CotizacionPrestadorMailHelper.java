package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.automatico.ReportesScheduler
        .ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.mail.MailUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CotizacionPrestadorMailHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    CotizacionPrestadorMailHelper.class
            );

    public void enviar(
            String emailDestino,
            String asunto,
            String cuerpo) throws Exception {

        validarParametros(
                emailDestino,
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

        /*
         * Se crea una sesión SMTP independiente para evitar
         * reutilizar la Session global de otros módulos.
         */
        propiedades.setProperty(
                "mail.smtp.starttls.enable",
                "true"
        );

        propiedades.setProperty(
                "mail.smtp.starttls.required",
                "true"
        );

        /*
         * La conexión desde Java 8 falla durante el handshake
         * posterior a STARTTLS. Se restringe el cliente a TLS 1.2.
         */
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

        mensaje.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(
                        emailDestino.trim(),
                        true
                )
        );

        mensaje.setSubject(
                asunto,
                "UTF-8"
        );

        mensaje.setText(
                cuerpo,
                "UTF-8"
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

            if (_log.isInfoEnabled()) {
                _log.info(
                        "Correo de cotización enviado. "
                                + "emailDestino="
                                + emailDestino
                                + ", usuarioSmtp="
                                + usuarioSmtp
                                + ", remitente="
                                + remitente
                );
            }

        } catch (Exception e) {
            _log.error(
                    "Falló el envío SMTP de cotización. "
                            + "emailDestino="
                            + emailDestino
                            + ", usuarioSmtp="
                            + usuarioSmtp,
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

    private void validarParametros(
            String emailDestino,
            String asunto,
            String cuerpo) throws Exception {

        if (isEmpty(emailDestino)) {
            throw new Exception(
                    "Debe informar email destino."
            );
        }

        validarEmail(
                emailDestino,
                "destino"
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
                /*
                 * El mensaje ya fue aceptado por el SMTP.
                 * Un fallo de cierre no debe transformar
                 * el envío exitoso en ENVIO_ERROR.
                 */
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
}