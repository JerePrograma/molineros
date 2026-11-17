import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.compras.requerimientos.helper.CotizacionPrestadorMailHelper;
import ar.com.ospim.compras.requerimientos.helper.CotizacionPrestadorMailHelper.AdjuntoOrdenMedica;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.mail.smtp.SMTPSendFailedException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;

/** JavaMail Provider de captura en JVM aislada: helper/MIME/catch reales, sin sockets. */
public class ComprasAutorizacionSmtpTransporteTest {
    private static int checks;
    private static int conexiones;
    private static int envios;
    private static int cierres;
    private static int solicitudesRed;
    private static String modo;
    private static final String[] DESTINOS = {"uno@example.invalid", "dos@example.invalid", "tres@example.invalid"};

    public static void main(String[] args) throws Exception {
        SecurityManager seguridadPrevia = System.getSecurityManager();
        System.setSecurityManager(new SecurityManager() {
            public void checkPermission(Permission permiso) { }
            public void checkPermission(Permission permiso, Object contexto) { }
            public void checkConnect(String host, int port) {
                solicitudesRed++;
                throw new SecurityException("Red bloqueada por la prueba SMTP aislada");
            }
            public void checkConnect(String host, int port, Object contexto) { checkConnect(host, port); }
            public void checkListen(int port) { throw new SecurityException("Sockets de escucha bloqueados"); }
        });
        try {
            probar("ACEPTADO", null);
            Exception rechazado = probar("RECHAZADO", SMTPSendFailedException.class);
            verificar(CotizacionPrestadorMailHelper.esRechazoSmtpConfirmado(rechazado), "Rechazo real del Provider clasificado");
            Exception parcial = probar("PARCIAL", CotizacionPrestadorMailHelper.EnvioParcialException.class);
            CotizacionPrestadorMailHelper.EnvioParcialException detalle = (CotizacionPrestadorMailHelper.EnvioParcialException) parcial;
            verificar(detalle.getDestinatariosEnviados() == 1 && detalle.getDestinatariosTotales() == 3, "Conserva destinatarios aceptados del parcial");
            verificar(!detalle.isCopiaEnviada(), "Sin BCC en la autorizacion");
            verificar(!CotizacionPrestadorMailHelper.esRechazoSmtpConfirmado(detalle), "Parcial no es rechazo total");
            Exception incierto = probar("INCIERTO", MessagingException.class);
            verificar(!CotizacionPrestadorMailHelper.esRechazoSmtpConfirmado(incierto), "IO de transporte conserva incertidumbre");
            probar("CIERRE_POST_ACEPTACION", null);
            Exception autenticacion = probar("AUTENTICACION_RECHAZADA", javax.mail.AuthenticationFailedException.class);
            verificar(CotizacionPrestadorMailHelper.esRechazoSmtpConfirmado(autenticacion), "Autenticacion rechazada antes de sendMessage");
            verificar(ReportesServiceUtil.consultas == 6, "Configuracion exclusivamente sintetica por llamada");
            new CotizacionPrestadorMailHelper().validarConfiguracionEnvio();
            verificar(ReportesServiceUtil.consultas == 7, "Preflight lee solo configuracion sintetica");
            probarConfiguracionInvalida("CONFIG_NULL", "configuraci");
            probarConfiguracionInvalida("CLAVE_VACIA", "contrase");
            probarConfiguracionInvalida("REMITENTE_INVALIDO", "remitente");
            verificar(solicitudesRed == 0, "El Provider no intento ninguna conexion de red");
            System.out.println("COMPRAS_AUTORIZACION_SMTP_TRANSPORTE_OK checks=" + checks
                    + " provider_captura=true sockets=0 transport_real_red=false configuracion_sintetica=true");
        } finally { System.setSecurityManager(seguridadPrevia); }
    }

    private static Exception probar(String escenario, Class tipoEsperado) throws Exception {
        modo = escenario;
        conexiones = 0;
        envios = 0;
        cierres = 0;
        List<AdjuntoOrdenMedica> adjuntos = new ArrayList<AdjuntoOrdenMedica>();
        adjuntos.add(new AdjuntoOrdenMedica(pdf("Original externo de prueba"), "original.pdf", "application/pdf"));
        Exception error = null;
        try {
            new CotizacionPrestadorMailHelper().enviar(DESTINOS, new String[0], "Autorizacion sintetica",
                    "Documentos de prueba", pdf("Pedido actual de prueba"), "pedido_actual.pdf", adjuntos);
        } catch (Exception e) { error = e; }
        verificar(tipoEsperado == null ? error == null : tipoEsperado.isInstance(error), "Resultado tipado " + escenario);
        verificar(conexiones == 1 && envios == ("AUTENTICACION_RECHAZADA".equals(escenario) ? 0 : 1) && cierres == 1, "Una conexion/captura/cierre del Provider " + escenario);
        System.out.println("SCENARIO=" + escenario + " connect=" + conexiones + " sendMessage=" + envios
                + " close=" + cierres + " exception=" + (error == null ? "none" : error.getClass().getSimpleName()) + " sockets=0");
        return error;
    }

    private static void probarConfiguracionInvalida(String configuracion, String causaEsperada) throws Exception {
        ReportesServiceUtil.modo = configuracion;
        conexiones = 0;
        envios = 0;
        cierres = 0;
        Exception preflight = null;
        try { new CotizacionPrestadorMailHelper().validarConfiguracionEnvio(); }
        catch (Exception e) { preflight = e; }
        verificar(preflight != null && preflight.getMessage().indexOf(causaEsperada) >= 0, "Preflight rechaza " + configuracion);
        verificar(conexiones == 0 && envios == 0 && cierres == 0, "Preflight invalido no crea transporte " + configuracion);
        Exception envio = null;
        try {
            new CotizacionPrestadorMailHelper().enviar(DESTINOS, new String[0], "Autorizacion sintetica", "Prueba",
                    pdf("Pedido de prueba"), "pedido_actual.pdf", new ArrayList<AdjuntoOrdenMedica>());
        } catch (Exception e) { envio = e; }
        verificar(envio != null && envio.getMessage().equals(preflight.getMessage()), "Envio conserva validacion exacta " + configuracion);
        verificar(conexiones == 0 && envios == 0 && cierres == 0, "Envio invalido no crea transporte " + configuracion);
        System.out.println("SCENARIO=PREFLIGHT_" + configuracion + " connect=0 sendMessage=0 close=0 sockets=0");
        ReportesServiceUtil.modo = "VALIDO";
    }

    private static byte[] pdf(String texto) throws Exception {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document();
        PdfWriter.getInstance(documento, salida);
        documento.open();
        documento.add(new Paragraph(texto));
        documento.close();
        return salida.toByteArray();
    }

    private static void verificar(boolean ok, String mensaje) {
        checks++;
        if (!ok) throw new AssertionError(mensaje);
    }

    public static class TransporteCaptura extends Transport {
        public TransporteCaptura(Session session, URLName url) { super(session, url); }
        public void connect(String usuario, String clave) throws MessagingException {
            // JavaMail Service intenta DNS para pedir nuevas credenciales al fallar AUTH.
            // La captura de rechazo se inyecta en connect para no accionar ese fallback.
            if ("AUTENTICACION_RECHAZADA".equals(modo)) {
                protocolConnect(null, -1, usuario, clave);
                return;
            }
            super.connect(usuario, clave);
        }
        protected boolean protocolConnect(String host, int puerto, String usuario, String clave) throws MessagingException {
            conexiones++;
            verificar("fixture-sin-secreto".equals(clave), "Solo usa secreto sintetico");
            verificar("true".equals(session.getProperty("mail.smtp.starttls.required")), "Conserva requisito TLS");
            verificar("true".equals(session.getProperty("mail.smtp.sendpartial")), "Conserva politica sendpartial");
            if ("AUTENTICACION_RECHAZADA".equals(modo)) throw new javax.mail.AuthenticationFailedException("Autenticacion rechazada sintetica");
            return true;
        }
        public void sendMessage(Message mensaje, Address[] destinos) throws MessagingException {
            envios++;
            verificar(destinos.length == 3, "Todos los destinatarios sinteticos");
            for (int i = 0; i < destinos.length; i++) verificar(destinos[i].toString().endsWith("@example.invalid"), "Nunca destinatarios reales");
            try {
                verificar("Autorizacion sintetica".equals(mensaje.getSubject()), "Asunto preservado");
                Multipart mime = (Multipart) mensaje.getContent();
                verificar(mime.getCount() == 3, "MIME real con cuerpo, pedido y original");
                verificar("pedido_actual.pdf".equals(mime.getBodyPart(1).getFileName()), "Nombre pedido MIME");
                verificar("original.pdf".equals(mime.getBodyPart(2).getFileName()), "Nombre original MIME");
                verificar(mime.getBodyPart(1).getInputStream().read() == '%', "Bytes reales pedido MIME");
            } catch (IOException e) { throw new MessagingException("MIME de captura invalido", e); }
            if ("RECHAZADO".equals(modo)) {
                throw new SMTPSendFailedException(".", 554, "Rechazo SMTP sintetico", null, null, destinos, null);
            }
            if ("PARCIAL".equals(modo)) {
                throw new SMTPSendFailedException(".", 250, "Entrega parcial sintetica", null,
                        new Address[] {destinos[0]}, new Address[] {destinos[1], destinos[2]}, null);
            }
            if ("INCIERTO".equals(modo)) throw new MessagingException("Resultado incierto sintetico", new IOException("Conexion interrumpida sintetica"));
        }
        public synchronized void close() throws MessagingException {
            cierres++;
            if ("CIERRE_POST_ACEPTACION".equals(modo)) throw new MessagingException("Fallo de cierre sintetico despues de aceptacion");
            super.close();
        }
    }
}
