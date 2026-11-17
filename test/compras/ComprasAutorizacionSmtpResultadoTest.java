import ar.com.ospim.compras.requerimientos.helper.CotizacionPrestadorMailHelper;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPSendFailedException;
import java.io.IOException;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;

/** Clasificacion conservadora con excepciones JavaMail reales, sin abrir transporte. */
public class ComprasAutorizacionSmtpResultadoTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        String[] comandos = {"MAIL FROM:<origen@example.invalid>", "RCPT TO:<destino@example.invalid>", "DATA", "."};
        int[] rechazos = {421, 450, 451, 500, 550, 554};
        for (int i = 0; i < comandos.length; i++) {
            for (int j = 0; j < rechazos.length; j++) {
                verificar(clasificar(smtp(comandos[i], rechazos[j], null, null)), "Respuesta negativa explicita en " + comandos[i]);
            }
        }
        verificar(clasificar(smtp(" data ", 554, null, null)), "Normaliza comando sin depender de mayusculas");
        verificar(!clasificar(null), "Null no acredita rechazo");
        verificar(!clasificar(new SendFailedException("Fallo generico")), "SendFailed sin respuesta no acredita rechazo");
        verificar(!clasificar(new IOException("Conexion interrumpida")), "IO incierto");
        verificar(!clasificar(smtp("DATA", 250, null, null)), "Aceptacion no es rechazo");
        verificar(!clasificar(smtp("DATA", 354, null, null)), "Respuesta intermedia no es rechazo");
        verificar(!clasificar(smtp("RSET", 550, null, null)), "RSET no acredita resultado de envio");
        verificar(!clasificar(smtp("QUIT", 421, null, null)), "QUIT no acredita resultado de envio");
        verificar(!clasificar(smtp(null, 550, null, null)), "Fase no identificada");
        Address[] aceptados = {new InternetAddress("destino@example.invalid")};
        verificar(!clasificar(smtp(".", 554, null, aceptados)), "Aceptados primarios impiden rechazo total");
        SendFailedException parcialAnidado = new SendFailedException("Aceptado parcial", null, aceptados, null, null);
        verificar(!clasificar(smtp(".", 554, parcialAnidado, null)), "Aceptados en cadena impiden rechazo total");
        verificar(!clasificar(smtp("DATA", 554, new IOException("Fallo de socket"), null)), "IO en cadena conserva incertidumbre");
        verificar(!clasificar(smtp("DATA", 554, new Exception("Causa desconocida"), null)), "Causa no identificada conserva incertidumbre");
        SMTPAddressFailedException rcpt = new SMTPAddressFailedException(new InternetAddress("destino@example.invalid"), "RCPT TO", 550, "Rechazo de destinatario");
        verificar(clasificar(smtp("RCPT TO:<destino@example.invalid>", 550, rcpt, null)), "Cadena SMTP negativa sin aceptados");
        verificar(!clasificar(rcpt), "Un fallo aislado RCPT no acredita resultado global");
        MessagingException envoltura = new MessagingException("Envoltura", smtp("DATA", 554, null, null));
        verificar(!clasificar(envoltura), "No desenvuelve generica para afirmar fase final");
        verificar(clasificar(new javax.mail.AuthenticationFailedException("Autenticacion rechazada")), "AuthFailed acredita rechazo antes de sendMessage");
        javax.mail.AuthenticationFailedException authIncierta = new javax.mail.AuthenticationFailedException("Autenticacion con causa IO");
        authIncierta.setNextException(new IOException("Conexion interrumpida"));
        verificar(!clasificar(authIncierta), "AuthFailed con IO anidada conserva incertidumbre");
        javax.mail.AuthenticationFailedException authConAceptados = new javax.mail.AuthenticationFailedException("Cadena inconsistente");
        authConAceptados.setNextException(parcialAnidado);
        verificar(!clasificar(authConAceptados), "AuthFailed con aceptados no acredita rechazo total");
        System.out.println("COMPRAS_AUTORIZACION_SMTP_RESULTADO_OK checks=" + checks + " transport=0 sockets=0");
    }

    private static SMTPSendFailedException smtp(String comando, int codigo, Exception causa, Address[] aceptados) {
        return new SMTPSendFailedException(comando, codigo, "Respuesta SMTP sintetica", causa, aceptados, null, null);
    }
    private static boolean clasificar(Exception error) { return CotizacionPrestadorMailHelper.esRechazoSmtpConfirmado(error); }
    private static void verificar(boolean ok, String mensaje) {
        checks++;
        if (!ok) throw new AssertionError(mensaje);
    }
}
