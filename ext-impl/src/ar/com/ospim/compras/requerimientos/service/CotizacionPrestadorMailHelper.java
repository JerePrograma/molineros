package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.automatico.ReportesScheduler
        .ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.mail.MailUtils;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

public class CotizacionPrestadorMailHelper {

    public void enviar(
            String emailDestino,
            String asunto,
            String cuerpo) throws Exception {

        validarEmailDestino(emailDestino);

        ReportesAutomaticosConfiguracion configuracion =
                ReportesServiceUtil.getConfiguracion();

        if (configuracion == null) {
            throw new Exception(
                    "No se encontró la configuración "
                            + "de correo de reportes automáticos."
            );
        }

        String password =
                configuracion.getPass();

        if (isEmpty(password)) {
            throw new Exception(
                    "La configuración de correo "
                            + "no tiene contraseña informada."
            );
        }

        List<String> destinatarios =
                new ArrayList<String>();

        destinatarios.add(
                emailDestino.trim()
        );

        List<MimeBodyPart> adjuntos =
                new ArrayList<MimeBodyPart>();

        boolean enviado =
                MailUtils
                        .enviarMailGmailconAdjuntoYRespuesta(
                                configuracion.getMailFrom(),
                                password,
                                destinatarios,
                                asunto,
                                cuerpo,
                                adjuntos
                        );

        if (!enviado) {
            throw new Exception(
                    "El servicio SMTP existente "
                            + "no pudo enviar el correo."
            );
        }
    }

    private void validarEmailDestino(
            String emailDestino) throws Exception {

        if (isEmpty(emailDestino)) {
            throw new Exception(
                    "Debe informar email destino."
            );
        }

        try {
            InternetAddress direccion =
                    new InternetAddress(
                            emailDestino.trim(),
                            true
                    );

            direccion.validate();

        } catch (Exception e) {
            throw new Exception(
                    "Email destino inválido: "
                            + emailDestino,
                    e
            );
        }
    }

    private boolean isEmpty(String value) {
        return value == null
                || value.trim().length() == 0;
    }
}