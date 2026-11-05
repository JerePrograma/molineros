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
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeUtility;

/** Patron de ComprasComparativaSincronizacionTest: reglas reales con fronteras simuladas. */
public class ComprasAutorizacionEmailTest {
    private static int checks;
    private static final int ID = 1376;
    private static final String DESTINO = "COMPRAS_EMAIL_AUTORIZACION";
    private static final String QA = "REQUERIMIENTO_EMAIL_QA";
    private static final String REDIRECCION = "REQUERIMIENTO_REDIRECCION_QA_HABILITADA";
    private static final String ERROR_SMTP = "El requerimiento quedo PARA AUTORIZAR, pero no se pudo confirmar el envio del correo.";

    public static void main(String[] args) throws Exception {
        ConfiguracionComprasDatosFixture.instalar();
        probarMime();
        probarEnvio();
        probarConfiguracion();
        probarLimite();
        probarContexto();
        probarRelacion();
        probarFallos();
        System.out.println("COMPRAS_AUTORIZACION_EMAIL_OK checks=" + checks);
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

    private static void probarEnvio() throws Exception {
        Fixture f = new Fixture();
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(f.cambios == 1 && f.envios == 1 && f.estadoPersistido == 6, "Confirma el estado y envia una sola vez.");
        verificar(f.eventos.equals(Arrays.asList(new String[] {"pedido", "adjuntos", "cambiar", "correo"})), "Adjuntos antes del cambio; correo despues de confirmar.");
        verificar(Arrays.equals(f.destinos, new String[] {"autorizacion@example.invalid", "suplente@example.invalid", "tercera@example.invalid"}), "Resuelve ambos separadores configurables.");
        verificar("RP:68180-APELLIDO NOMBRE-DU:12345678-LOCALIDAD-PROVINCIA-ID:1376-COMPARATIVA".equals(f.asunto), "Asunto respeta el formato RP y los datos del afiliado en mayusculas.");
        verificar(f.pedidoEnviado == f.pedido && f.adjuntosEnviados == f.adjuntos, "Conserva documentos recuperados para el envio.");
        aprobado("ENVIO_UNICO_DESPUES_DE_CAMBIO_CONFIRMADO");
        fallo(f, contexto(), null);
        verificar(f.cambios == 1 && f.envios == 1, "Segundo intento rechazado por la transicion vigente sin reenviar.");
        aprobado("DOBLE_PETICION_NO_REENVIA");

        f = new Fixture();
        f.requerimiento.setSectorDescripcion("SISTEMAS");
        f.requerimiento.setRequiereAfiliado(false);
        f.requerimiento.setEstado(1);
        f.estadoPersistido = 1;
        f.configuracion.clear();
        f.relacion = null;
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(f.cambios == 1 && f.envios == 0, "Sector empresa conserva la transicion sin correo de RP.");
        verificar(f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "Sector empresa no recupera documentos de RP.");
        aprobado("EMPRESA_SIN_RP_CONSERVA_FLUJO");
    }

    private static void probarConfiguracion() throws Exception {
        String[] invalidas = {null, "", "   ", "direccion-invalida", "autorizacion@example.invalid;direccion-invalida", ";autorizacion@example.invalid", "autorizacion@example.invalid;", "autorizacion@example.invalid;;suplente@example.invalid", "autorizacion@example.invalid\r\nBcc: otro@example.invalid"};
        for (int i = 0; i < invalidas.length; i++) {
            Fixture f = new Fixture();
            f.configuracion.put(DESTINO, invalidas[i]);
            fallo(f, contexto(), null);
            verificar(f.cambios == 0 && f.envios == 0, "Configuracion invalida no cambia ni envia: caso " + i);
        }
        aprobado("CONFIGURACION_VACIA_INVALIDA_O_INYECCION_RECHAZADA");
        Fixture f = new Fixture();
        f.configuracion.put(REDIRECCION, " true ");
        f.configuracion.put(QA, "qa@example.invalid");
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(Arrays.equals(f.destinos, new String[] {"qa@example.invalid"}), "QA reemplaza destinos reales.");
        aprobado("REDIRECCION_QA_RESPETADA");
        f = new Fixture();
        f.configuracion.put(REDIRECCION, " true ");
        f.configuracion.put(QA, "invalido");
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "QA invalido no cae en destino real.");
        aprobado("QA_INVALIDO_FAIL_CLOSED");
    }

    private static void probarLimite() throws Exception {
        String[] invalidos = {"invalido", "1.5", "0", "-1"};
        for (int i = 0; i < invalidos.length; i++) {
            Fixture f = new Fixture();
            f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", invalidos[i]);
            fallo(f, contexto(), null);
            verificar(f.cambios == 0 && f.envios == 0, "Limite invalido no cambia ni envia: " + invalidos[i]);
        }
        Fixture f = new Fixture();
        f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", "1");
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "Documentos que exceden el limite abortan antes de confirmar.");
        f = new Fixture();
        long total = f.pedido.getContenido().length;
        for (int i = 0; i < f.adjuntos.size(); i++) total += f.adjuntos.get(i).getContenido().length;
        f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", String.valueOf(total));
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "El total exacto del limite tambien se rechaza.");
        f = new Fixture();
        f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", "10000000");
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(f.cambios == 1 && f.envios == 1, "Limite suficiente permite todos los documentos.");
        aprobado("LIMITE_DOCUMENTAL_CONFIGURABLE_ANTES_DEL_CAMBIO");
    }
    private static void probarContexto() throws Exception {
        Contexto sinUsuario = contexto();
        sinUsuario.usuario = null;
        Contexto sinChecker = contexto();
        sinChecker.checker = null;
        ThemeDisplay[] invalidos = {null, sinUsuario, sinChecker, contexto(0L, 10136L, 501L, 501L), contexto(1L, 0L, 501L, 501L), contexto(1L, 10136L, 501L, 999L)};
        for (int i = 0; i < invalidos.length; i++) {
            Fixture f = new Fixture();
            fallo(f, invalidos[i], null);
            verificar(f.cambios == 0 && f.envios == 0, "Contexto incompleto o ajeno no cambia ni envia: caso " + i);
        }
        aprobado("CONTEXTO_IDENTIDAD_EMPRESA_GRUPO_Y_PERMISOS");
        Fixture f = new Fixture();
        f.requerimiento.setBajaFecha(new Date());
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "Requerimiento inactivo rechazado.");
        f = new Fixture();
        f.requerimiento.setId(Integer.valueOf(ID + 1));
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "Requerimiento ajeno rechazado.");
        aprobado("REQUERIMIENTO_ACTIVO_E_IDENTIDAD");
    }

    private static void probarRelacion() throws Exception {
        Fixture f = new Fixture();
        f.relacion = null;
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "Exige relacion de RP.");
        f = new Fixture();
        f.relacion.setEstado(WebKeysCompras.VINCULO_RECLAMO_RESERVADO);
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "Exige RP vinculado.");
        f = new Fixture();
        f.relacion.setIdRequerimientoCompra(ID + 1);
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "Exige relacion del mismo requerimiento.");
        f = new Fixture();
        f.relacion.setBajaFecha(new Date());
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "Excluye relacion de baja.");
        aprobado("RELACION_RP_VIGENTE_DEL_MISMO_REQUERIMIENTO");
    }

    private static void probarFallos() throws Exception {
        Fixture f = new Fixture();
        f.fallaPedido = true;
        fallo(f, contexto(), "Fallo DL pedido simulado.");
        verificar(f.cambios == 0 && f.envios == 0, "Error de pedido aborta antes de cambiar.");
        f = new Fixture();
        f.fallaAdjuntos = true;
        fallo(f, contexto(), "Fallo DL adjuntos simulado.");
        verificar(f.cambios == 0 && f.envios == 0, "Error de adjuntos aborta antes de cambiar.");
        aprobado("FALLOS_DOCUMENTALES_NO_CAMBIAN_ESTADO");
        f = new Fixture();
        f.adjuntos.set(0, new AdjuntoOrdenMedica(new byte[] {1, 2, 3}, "comparativa.pdf", "application/pdf"));
        fallo(f, contexto(), null);
        verificar(f.cambios == 0 && f.envios == 0, "Contenido documental invalido aborta antes de cambiar.");
        aprobado("MIME_SE_VALIDA_ANTES_DE_CAMBIAR_ESTADO");
        f = new Fixture();
        f.fallaCambio = true;
        fallo(f, contexto(), "Fallo de persistencia simulado.");
        verificar(f.cambios == 0 && f.envios == 0 && f.estadoPersistido == 4, "Fallo transaccional no envia.");
        aprobado("FALLO_PERSISTENCIA_NO_ENVIA");
        f = new Fixture();
        f.fallaSmtp = true;
        Exception error = fallo(f, contexto(), ERROR_SMTP);
        verificar(f.cambios == 1 && f.envios == 1 && f.estadoPersistido == 6, "SMTP fallido conserva estado confirmado.");
        verificar(error.getMessage().indexOf("detalle interno") < 0, "No expone mensaje interno SMTP al usuario.");
        aprobado("FALLO_SMTP_INFORMA_ESTADO_Y_NO_EXPONE_CAUSA");
    }

    private static Exception fallo(Fixture f, ThemeDisplay contexto, String mensaje) throws Exception {
        try { f.enviarAAutorizar(ID, "usuario-prueba", contexto); }
        catch (Exception error) {
            if (mensaje != null) verificar(ERROR_SMTP.equals(mensaje) ? error.getMessage().startsWith(mensaje) : mensaje.equals(error.getMessage()), "Mensaje esperado: " + mensaje + "; recibido: " + error.getMessage());
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
            if (fallaCambio) throw new Exception("Fallo de persistencia simulado.");
            if (estadoPersistido == 6) throw new Exception("La transicion ya fue procesada.");
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
