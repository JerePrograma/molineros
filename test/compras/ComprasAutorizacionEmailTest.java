import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.helper.CotizacionPrestadorMailHelper;
import ar.com.ospim.compras.requerimientos.helper.CotizacionPrestadorMailHelper.AdjuntoOrdenMedica;
import ar.com.ospim.compras.requerimientos.helper.NotificarAutorizacionCompraHelper;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeUtility;

/** Reglas productivas con fronteras simuladas; el correo no impide la transicion confirmada. */
public class ComprasAutorizacionEmailTest {
    private static int checks;
    private static final int ID = 1376;
    private static final String DESTINO = "COMPRAS_EMAIL_AUTORIZACION";
    private static final String QA = "REQUERIMIENTO_EMAIL_QA";
    private static final String REDIRECCION = "REQUERIMIENTO_REDIRECCION_QA_HABILITADA";
    private static final String MENSAJE_LOG = "No se pudo confirmar el correo de autorizacion. Estado PARA AUTORIZAR confirmado. id=" + ID;
    private static final CapturaLog LOGS = new CapturaLog();

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger(NotificarAutorizacionCompraHelper.class.getName());
        Level nivelAnterior = logger.getLevel();
        boolean propagar = logger.getUseParentHandlers();
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        logger.addHandler(LOGS);
        try {
            ConfiguracionComprasDatosFixture.instalar();
            probarMime();
            probarClaveVaciaNoBloquea();
            probarEnvio();
            probarConfiguracion();
            probarLimite();
            probarContexto();
            probarRelacion();
            probarFallos();
            System.out.println("COMPRAS_AUTORIZACION_EMAIL_OK checks=" + checks);
        } finally {
            logger.removeHandler(LOGS);
            logger.setLevel(nivelAnterior);
            logger.setUseParentHandlers(propagar);
        }
    }

    private static void probarMime() throws Exception {
        Fixture f = new Fixture();
        Multipart mime = new Correo().multipart("Texto de autorizacion", f.pedido, f.adjuntos);
        verificar(mime.getCount() == 6, "MIME: texto, pedido, comparativa, ficha RP y dos ordenes medicas.");
        verificar(mime.getBodyPart(0).getContent().equals("Texto de autorizacion"), "Conserva el cuerpo del correo.");
        comprobarParte(mime.getBodyPart(1), f.pedido);
        for (int i = 0; i < f.adjuntos.size(); i++) comprobarParte(mime.getBodyPart(i + 2), f.adjuntos.get(i));
        aprobado("MIME_REAL_NOMBRES_BYTES_TIPOS_Y_MULTIPLES_ORDENES");
    }

    private static void comprobarParte(BodyPart parte, AdjuntoOrdenMedica esperado) throws Exception {
        verificar(esperado.getNombre().equals(MimeUtility.decodeText(parte.getFileName())), "Nombre MIME " + esperado.getNombre());
        verificar(parte.getDataHandler().getContentType().equals(esperado.getContentType()), "Tipo MIME " + esperado.getNombre());
        InputStream entrada = parte.getInputStream();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try {
            byte[] bloque = new byte[1024];
            int n;
            while ((n = entrada.read(bloque)) != -1) salida.write(bloque, 0, n);
        } finally { entrada.close(); }
        verificar(Arrays.equals(esperado.getContenido(), salida.toByteArray()), "Bytes MIME " + esperado.getNombre());
    }

    private static void probarClaveVaciaNoBloquea() throws Exception {
        Fixture f = new Fixture();
        f.configuracion.put(DESTINO, "");
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "La clave vacia no envia a destinos alternativos.");
        verificar(f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "La clave vacia confirma el estado sin recuperar documentos.");
        aprobado("CLAVE_VACIA_NO_BLOQUEA_PARA_AUTORIZAR");
    }

    private static void probarEnvio() throws Exception {
        Fixture f = new Fixture();
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(f.cambios == 1 && f.envios == 1 && f.estadoPersistido == 6, "Confirma el estado y envia una sola vez.");
        verificar(f.eventos.equals(Arrays.asList(new String[] {"cambiar", "pedido", "adjuntos", "correo"})), "Confirma la transicion antes de preparar o enviar el correo.");
        verificar(Arrays.equals(f.destinos, new String[] {"autorizacion@example.invalid", "suplente@example.invalid", "tercera@example.invalid"}), "Resuelve ambos separadores configurables.");
        verificar("RP:68180-APELLIDO NOMBRE-DU:12345678-LOCALIDAD-PROVINCIA-ID:1376-COMPARATIVA".equals(f.asunto), "Asunto respeta el formato RP y los datos del afiliado en mayusculas.");
        verificar(f.pedidoEnviado == f.pedido && f.adjuntosEnviados == f.adjuntos, "Conserva documentos recuperados para el envio.");
        aprobado("ENVIO_UNICO_DESPUES_DE_CAMBIO_CONFIRMADO");
        f.configuracion.clear();
        LOGS.registros.clear();
        Exception error = fallo(f, null, "La transicion ya fue procesada.");
        verificar(LOGS.registros.isEmpty(), "El rechazo de doble peticion no intenta ni registra fallo de notificacion.");
        verificar(error == f.errorDuplicado, "La doble peticion conserva el error original de transicion.");
        verificar(f.cambios == 1 && f.envios == 1 && f.eventos.size() == 5 && "cambiar".equals(f.eventos.get(4)), "Segundo intento no prepara ni reenvia el correo.");
        aprobado("DOBLE_PETICION_RECHAZADA_ANTES_DE_PREPARAR_CORREO");

        f = new Fixture();
        f.requerimiento.setSectorDescripcion("SISTEMAS");
        f.requerimiento.setRequiereAfiliado(false);
        f.requerimiento.setEstado(1);
        f.estadoPersistido = 1;
        f.configuracion.clear();
        f.relacion = null;
        LOGS.registros.clear();
        f.enviarAAutorizar(ID, "usuario-prueba", null);
        verificar(LOGS.registros.isEmpty(), "Empresa sin RP conserva exito sin incidencia de correo.");
        verificar(f.cambios == 1 && f.envios == 0, "Empresa confirma sin contexto documental, RP ni correo configurado.");
        verificar(f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "Sector empresa no recupera documentos de RP.");
        aprobado("EMPRESA_SIN_RP_NI_CONTEXTO_CONSERVA_FLUJO");
    }

    private static void probarConfiguracion() throws Exception {
        String[] invalidas = {null, "", "   ", "direccion-invalida", "autorizacion@example.invalid;direccion-invalida", ";autorizacion@example.invalid", "autorizacion@example.invalid;", "autorizacion@example.invalid;;suplente@example.invalid", "autorizacion@example.invalid\r\nBcc: otro@example.invalid"};
        for (int i = 0; i < invalidas.length; i++) {
            Fixture f = new Fixture();
            f.configuracion.put(DESTINO, invalidas[i]);
            incidencia(f, contexto(), null);
            verificar(f.envios == 0 && f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "Destino invalido no prepara ni envia correo: caso " + i);
        }
        aprobado("CONFIGURACION_INVALIDA_SE_REGISTRA_SIN_BLOQUEAR_NI_ENVIAR");
        Fixture f = new Fixture();
        f.configuracion.put(REDIRECCION, " true ");
        f.configuracion.put(QA, "qa@example.invalid");
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(Arrays.equals(f.destinos, new String[] {"qa@example.invalid"}), "QA reemplaza destinos reales.");
        aprobado("REDIRECCION_QA_RESPETADA");
        f = new Fixture();
        f.configuracion.put(REDIRECCION, " true ");
        f.configuracion.put(QA, "invalido");
        incidencia(f, contexto(), null);
        verificar(f.envios == 0 && f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "QA invalido conserva estado sin caer en destino real.");
        aprobado("QA_INVALIDO_NO_ENVIA_A_DESTINO_REAL");
    }

    private static void probarLimite() throws Exception {
        String[] invalidos = {"invalido", "1.5", "0", "-1"};
        for (int i = 0; i < invalidos.length; i++) {
            Fixture f = new Fixture();
            f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", invalidos[i]);
            incidencia(f, contexto(), null);
            verificar(f.envios == 0, "Limite invalido no envia: " + invalidos[i]);
        }
        Fixture f = new Fixture();
        f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", "1");
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "Documentos que exceden el limite no se envian.");
        f = new Fixture();
        long total = f.pedido.getContenido().length;
        for (int i = 0; i < f.adjuntos.size(); i++) total += f.adjuntos.get(i).getContenido().length;
        f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", String.valueOf(total));
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "El total exacto del limite no se envia.");
        f = new Fixture();
        f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", "10000000");
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(f.cambios == 1 && f.envios == 1, "Limite suficiente permite todos los documentos.");
        aprobado("LIMITE_DOCUMENTAL_SE_REGISTRA_SIN_BLOQUEAR_ESTADO");
    }

    private static void probarContexto() throws Exception {
        Contexto sinUsuario = contexto();
        sinUsuario.usuario = null;
        Contexto sinChecker = contexto();
        sinChecker.checker = null;
        ThemeDisplay[] invalidos = {null, sinUsuario, sinChecker, contexto(0L, 10136L, 501L, 501L), contexto(1L, 0L, 501L, 501L), contexto(1L, 10136L, 501L, 999L)};
        for (int i = 0; i < invalidos.length; i++) {
            Fixture f = new Fixture();
            incidencia(f, invalidos[i], null);
            verificar(f.envios == 0 && f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "Contexto documental incompleto o ajeno no envia: caso " + i);
        }
        aprobado("CONTEXTO_DOCUMENTAL_INVALIDO_NO_REVIERTE_TRANSICION");
        // Estas lecturas ocurren despues del commit; la validacion central ya fue ejecutada por cambiarEstado.
        Fixture f = new Fixture();
        f.requerimiento.setBajaFecha(new Date());
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "Lectura postcommit inactiva no envia correo.");
        f = new Fixture();
        f.requerimiento.setId(Integer.valueOf(ID + 1));
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "Lectura postcommit de otro requerimiento no envia correo.");
        aprobado("LECTURA_POSTCOMMIT_INCONSISTENTE_SE_REGISTRA");
    }

    private static void probarRelacion() throws Exception {
        Fixture f = new Fixture();
        f.relacion = null;
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "No envia sin relacion de RP vigente.");
        f = new Fixture();
        f.relacion.setEstado(WebKeysCompras.VINCULO_RECLAMO_RESERVADO);
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "No envia con RP reservado.");
        f = new Fixture();
        f.relacion.setIdRequerimientoCompra(ID + 1);
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "No envia con relacion de otro requerimiento.");
        f = new Fixture();
        f.relacion.setBajaFecha(new Date());
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "No envia con relacion de baja.");
        aprobado("RELACION_DOCUMENTAL_INVALIDA_NO_REVIERTE_TRANSICION");
    }

    private static void probarFallos() throws Exception {
        Fixture f = new Fixture();
        f.fallaPedido = true;
        incidencia(f, contexto(), "Fallo DL pedido simulado.");
        verificar(f.envios == 0, "Pedido faltante no envia correo incompleto.");
        f = new Fixture();
        f.fallaAdjuntos = true;
        incidencia(f, contexto(), "Fallo DL adjuntos simulado.");
        verificar(f.envios == 0, "Fallo DL no envia correo incompleto.");
        aprobado("FALLOS_DOCUMENTALES_SE_REGISTRAN_SIN_REVERTIR_ESTADO");
        f = new Fixture();
        f.adjuntos.set(0, new AdjuntoOrdenMedica(new byte[] {1, 2, 3}, "comparativa.pdf", "application/pdf"));
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "MIME invalido no se envia aunque el estado este confirmado.");
        aprobado("MIME_INVALIDO_NO_ENVIA_NI_REVIERTE_ESTADO");
        f = new Fixture();
        f.fallaCambio = true;
        f.configuracion.clear();
        LOGS.registros.clear();
        Exception error = fallo(f, null, "Fallo de persistencia simulado.");
        verificar(LOGS.registros.isEmpty(), "Fallo central de persistencia no se confunde con fallo de correo.");
        verificar(error == f.errorCambio, "El fallo de transicion conserva la excepcion original.");
        verificar(f.cambios == 0 && f.envios == 0 && f.estadoPersistido == 4 && f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "Fallo transaccional no prepara ni envia.");
        aprobado("FALLO_PERSISTENCIA_PROPAGA_ERROR_Y_NO_PREPARA_CORREO");
        f = new Fixture();
        f.fallaSmtp = true;
        LogRecord registro = incidencia(f, contexto(), "detalle interno SMTP que no debe mostrarse");
        verificar(f.envios == 1, "SMTP se intento una vez despues del estado confirmado.");
        verificar(registro.getMessage().indexOf("detalle interno") < 0, "El texto del log identifica la incidencia sin incorporar el mensaje de la causa.");
        aprobado("FALLO_SMTP_LOGUEADO_CON_RETORNO_NORMAL_Y_ESTADO_CONFIRMADO");
    }

    /** Captura el LogFactory Jdk14 real; no agrega hooks a la clase productiva. */
    private static final class CapturaLog extends Handler {
        private final List<LogRecord> registros = new ArrayList<LogRecord>();
        public void publish(LogRecord registro) { registros.add(registro); }
        public void flush() { }
        public void close() { }
    }

    private static LogRecord incidencia(Fixture f, ThemeDisplay contexto, String causa) throws Exception {
        LOGS.registros.clear();
        f.enviarAAutorizar(ID, "usuario-prueba", contexto);
        verificar(f.cambios == 1 && f.estadoPersistido == 6, "El correo no debe impedir ni revertir la transicion a PARA AUTORIZAR.");
        verificar(LOGS.registros.size() == 1, "La incidencia del correo se registra una vez y el metodo retorna normalmente.");
        LogRecord registro = LOGS.registros.get(0);
        verificar(registro.getLevel().intValue() >= Level.SEVERE.intValue(), "La incidencia se registra como error.");
        verificar(MENSAJE_LOG.equals(registro.getMessage()), "El log informa id y estado confirmado con el texto esperado.");
        verificar(registro.getThrown() instanceof Exception, "El log conserva la excepcion original para el diagnostico.");
        if (causa != null) verificar(causa.equals(registro.getThrown().getMessage()), "El log conserva la causa original.");
        return registro;
    }
    private static Exception fallo(Fixture f, ThemeDisplay contexto, String mensaje) throws Exception {
        try { f.enviarAAutorizar(ID, "usuario-prueba", contexto); }
        catch (Exception error) {
            if (mensaje != null) verificar(mensaje.equals(error.getMessage()), "Mensaje esperado: " + mensaje + "; recibido: " + error.getMessage());
            return error;
        }
        throw new IllegalStateException("Debia rechazar el caso.");
    }
    private static Contexto contexto() { return contexto(1L, 10136L, 501L, 501L); }
    private static Contexto contexto(long empresa, long grupo, long usuario, long checkerUsuario) {
        return new Contexto(empresa, grupo,
                (User) modelo(User.class, "getUserId", usuario),
                (PermissionChecker) modelo(PermissionChecker.class, "getUserId", checkerUsuario));
    }

    /** ThemeDisplay setters consultan servicios del portal; la frontera offline provee solo getters. */
    private static final class Contexto extends ThemeDisplay {
        private final long empresa;
        private final long grupo;
        private User usuario;
        private PermissionChecker checker;
        Contexto(long empresa, long grupo, User usuario, PermissionChecker checker) {
            this.empresa = empresa;
            this.grupo = grupo;
            this.usuario = usuario;
            this.checker = checker;
        }
        public long getCompanyId() { return empresa; }
        public long getScopeGroupId() { return grupo; }
        public User getUser() { return usuario; }
        public PermissionChecker getPermissionChecker() { return checker; }
    }

    private static Object modelo(Class tipo, final String getter, final long valor) {
        return Proxy.newProxyInstance(tipo.getClassLoader(), new Class[] {tipo}, new InvocationHandler() {
            public Object invoke(Object proxy, Method metodo, Object[] argumentos) {
                if (getter.equals(metodo.getName())) return Long.valueOf(valor);
                if (metodo.getReturnType() == Boolean.TYPE) return Boolean.TRUE;
                if (metodo.getReturnType() == Long.TYPE) return Long.valueOf(0L);
                if (metodo.getReturnType() == Integer.TYPE) return Integer.valueOf(0);
                return null;
            }
        });
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

    private static byte[] png() throws Exception {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        BufferedImage imagen = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        imagen.setRGB(0, 0, 0x123456);
        ImageIO.write(imagen, "png", salida);
        return salida.toByteArray();
    }

    private static void verificar(boolean valor, String mensaje) {
        if (!valor) throw new IllegalStateException(mensaje);
        checks++;
    }
    private static void aprobado(String caso) { System.out.println("PASS " + caso); }

    private static final class Correo extends CotizacionPrestadorMailHelper {
        Multipart multipart(String cuerpo, AdjuntoOrdenMedica pedido, List<AdjuntoOrdenMedica> adjuntos) throws Exception {
            return construirMultipart(cuerpo, pedido.getContenido(), pedido.getNombre(), adjuntos);
        }
    }

    private static final class Fixture extends NotificarAutorizacionCompraHelper {
        RequerimientoCompra requerimiento = new RequerimientoCompra();
        RequerimientoCompraReclamoPrestacional relacion = new RequerimientoCompraReclamoPrestacional();
        Map<String, String> configuracion = new HashMap<String, String>();
        List<String> eventos = new ArrayList<String>();
        AdjuntoOrdenMedica pedido;
        List<AdjuntoOrdenMedica> adjuntos = new ArrayList<AdjuntoOrdenMedica>();
        AdjuntoOrdenMedica pedidoEnviado;
        List<AdjuntoOrdenMedica> adjuntosEnviados;
        String[] destinos;
        String asunto;
        int cambios;
        int envios;
        int estadoPersistido = 4;
        boolean fallaPedido;
        boolean fallaAdjuntos;
        boolean fallaCambio;
        boolean fallaSmtp;
        final Exception errorCambio = new Exception("Fallo de persistencia simulado.");
        final Exception errorDuplicado = new Exception("La transicion ya fue procesada.");

        Fixture() throws Exception {
            requerimiento.setId(Integer.valueOf(ID));
            requerimiento.setEstado(4);
            requerimiento.setSectorDescripcion("PRESTACIONES MEDICAS");
            requerimiento.setRequiereAfiliado(true);
            requerimiento.setAfiliadoApellido("Apellido");
            requerimiento.setAfiliadoNombre("Nombre");
            requerimiento.setAfiliadoDocumentoTipo("DU");
            requerimiento.setAfiliadoDocumentoNro("12345678");
            requerimiento.setAfiliadoLocalidad("Localidad");
            requerimiento.setAfiliadoProvincia("Provincia");
            relacion.setIdRequerimientoCompra(ID);
            relacion.setIdReclamoPrestacional(Integer.valueOf(68180));
            relacion.setEstado(WebKeysCompras.VINCULO_RECLAMO_VINCULADO);
            configuracion.put(DESTINO, "autorizacion@example.invalid, suplente@example.invalid; tercera@example.invalid");
            configuracion.put(REDIRECCION, "false");
            pedido = new AdjuntoOrdenMedica(pdf("Pedido de presupuesto"), "RequerimientoCompra_1376.pdf", "application/pdf");
            adjuntos.add(new AdjuntoOrdenMedica(pdf("Comparativa"), "68180-ComparativaCompra_1376.pdf", "application/pdf"));
            adjuntos.add(new AdjuntoOrdenMedica(pdf("Ficha imprimible del RP"), "RP 68180.pdf", "application/pdf"));
            adjuntos.add(new AdjuntoOrdenMedica(png(), "orden_medica_1.png", "image/png"));
            adjuntos.add(new AdjuntoOrdenMedica(pdf("Segunda orden medica"), "orden_medica_2.pdf", "application/pdf"));
        }
        protected RequerimientoCompra obtenerRequerimiento(int id) { return requerimiento; }
        protected String getConfiguracion(String clave) { return configuracion.get(clave); }
        protected RequerimientoCompraReclamoPrestacional obtenerRelacion(int id) { return relacion; }
        protected AdjuntoOrdenMedica recuperarPedido(int id, ThemeDisplay contexto) throws Exception {
            eventos.add("pedido");
            if (fallaPedido) throw new Exception("Fallo DL pedido simulado.");
            return pedido;
        }
        protected List<AdjuntoOrdenMedica> recuperarAdjuntos(int id, RequerimientoCompraReclamoPrestacional vinculo, ThemeDisplay contexto) throws Exception {
            eventos.add("adjuntos");
            if (fallaAdjuntos) throw new Exception("Fallo DL adjuntos simulado.");
            return adjuntos;
        }
        protected void cambiarEstado(int id, String usuario) throws Exception {
            eventos.add("cambiar");
            if (fallaCambio) throw errorCambio;
            if (estadoPersistido == 6) throw errorDuplicado;
            estadoPersistido = 6;
            cambios++;
        }
        protected void enviarCorreo(String[] destinatarios, String titulo, String cuerpo, AdjuntoOrdenMedica documentoPedido, List<AdjuntoOrdenMedica> documentos) throws Exception {
            eventos.add("correo");
            envios++;
            destinos = destinatarios;
            asunto = titulo;
            pedidoEnviado = documentoPedido;
            adjuntosEnviados = documentos;
            if (fallaSmtp) throw new Exception("detalle interno SMTP que no debe mostrarse");
        }
    }
}
