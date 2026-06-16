package ar.com.ospim.compras.requerimientos.service;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsKeys;

import javax.mail.internet.InternetAddress;

public class CotizacionPrestadorMailHelper {

    public void enviar(long companyId,
                       String emailDestino,
                       String asunto,
                       String cuerpo) throws Exception {

        validarEmailDestino(emailDestino);

        String fromAddress = PrefsPropsUtil.getString(
                companyId,
                PropsKeys.ADMIN_EMAIL_FROM_ADDRESS
        );

        String fromName = PrefsPropsUtil.getString(
                companyId,
                PropsKeys.ADMIN_EMAIL_FROM_NAME
        );

        if (isEmpty(fromAddress)) {
            throw new Exception(
                    "No esta configurado el remitente de email del portal: "
                            + PropsKeys.ADMIN_EMAIL_FROM_ADDRESS
            );
        }

        if (isEmpty(fromName)) {
            fromName = "OSPIM";
        }

        InternetAddress from = new InternetAddress(
                fromAddress,
                fromName
        );

        InternetAddress to = new InternetAddress(
                emailDestino.trim()
        );

        MailMessage mailMessage = new MailMessage(
                from,
                to,
                asunto,
                cuerpo,
                false
        );

        MailServiceUtil.sendEmail(mailMessage);
    }

    private void validarEmailDestino(String emailDestino)
            throws Exception {

        if (isEmpty(emailDestino)) {
            throw new Exception(
                    "Debe informar email destino."
            );
        }

        if (emailDestino.indexOf("@") < 0
                || emailDestino.indexOf(".") < 0) {

            throw new Exception(
                    "Email destino invalido: "
                            + emailDestino
            );
        }
    }

    private boolean isEmpty(String value) {
        return value == null
                || value.trim().length() == 0;
    }
}