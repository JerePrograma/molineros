import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import com.liferay.documentlibrary.service.DLLocalService;
import com.liferay.documentlibrary.service.DLLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import ar.com.ospim.compras.requerimientos.helper.CotizacionPrestadorMailHelper;
import ar.com.ospim.compras.requerimientos.helper.CotizacionPrestadorMailHelper.AdjuntoOrdenMedica;
import ar.com.ospim.compras.requerimientos.helper.NotificarAutorizacionCompraHelper;
import com.liferay.portal.model.User;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
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
    private static Fixture activo;

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger(NotificarAutorizacionCompraHelper.class.getName());
        Level nivelAnterior = logger.getLevel();
        boolean propagar = logger.getUseParentHandlers();
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        logger.addHandler(LOGS);
        Fronteras fronteras = new Fronteras();
        try {
            probarSeparadoresFinales();
            probarMime();
            probarClaveVaciaNoBloquea();
            probarEnvio();
            probarConfiguracion();
            probarLimite();
            probarContexto();
            probarRelacion();
            probarFallos();
            probarPresupuestos();
            System.out.println("COMPRAS_AUTORIZACION_EMAIL_OK checks=" + checks);
        } finally {
            fronteras.close();
            logger.removeHandler(LOGS);
            logger.setLevel(nivelAnterior);
            logger.setUseParentHandlers(propagar);
        }
    }

    private static void probarSeparadoresFinales() throws Exception {
        String[] valores = {"autorizacion@example.invalid;", "autorizacion@example.invalid;; ",
            "; autorizacion@example.invalid ;; suplente@example.invalid; ",
            "autorizacion@example.invalid;AUTORIZACION@example.invalid;"};
        for (int qa = 0; qa < 2; qa++) {
            for (int i = 0; i < valores.length; i++) {
                Fixture f = new Fixture();
                f.configuracion.put(REDIRECCION, qa == 1 ? "true" : "false");
                f.configuracion.put(qa == 1 ? QA : DESTINO, valores[i]);
                LOGS.registros.clear();
                f.enviarAAutorizar(ID, "usuario-prueba", contexto());
                verificar(f.envios == 1 && f.estadoPersistido == 6 && LOGS.registros.isEmpty(),
                        "Tolera separadores vacios sin perder el envio: QA=" + qa + " caso=" + i);
                verificar(f.destinos.length == (i == 2 ? 2 : 1), "Conserva destinatarios validos sin duplicar ni agregar vacios.");
            }
        }
        aprobado("SEPARADOR_FINAL_Y_VACIOS_TOLERADOS_EN_DESTINO_Y_QA");
    }

    private static void probarMime() throws Exception {
        Fixture f = new Fixture();
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        Multipart mime = new Correo().multipart(f.cuerpo, f.pedidoEnviado, f.adjuntosEnviados);
        verificar(mime.getCount() == 9, "MIME: texto y ocho adjuntos con dos presupuestos, sin Orden de Compra.");
        verificar(mime.getBodyPart(0).getContent().equals(f.cuerpo), "Conserva el cuerpo acentuado del correo.");
        comprobarParte(mime.getBodyPart(1), f.pedido);
        for (int i = 0; i < f.adjuntos.size(); i++) comprobarParte(mime.getBodyPart(i + 2), f.adjuntos.get(i));
        verificar(f.documentos.get(Long.valueOf(701)).leido && f.documentos.get(Long.valueOf(702)).leido,
                "Recupera los bytes reales de ambas cotizaciones por la frontera DL simulada.");
        comprobarParte(mime.getBodyPart(7), f.documentos.get(Long.valueOf(701)).adjuntoEsperado());
        comprobarParte(mime.getBodyPart(8), f.documentos.get(Long.valueOf(702)).adjuntoEsperado());
        aprobado("MIME_REAL_OCHO_ADJUNTOS_SIN_OC_CONSERVA_PRESUPUESTOS_NOMBRES_FORMATOS_Y_BYTES");
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
        verificar(f.eventos.equals(Arrays.asList(new String[] {"cambiar", "pedido", "adjuntos", "presupuestos", "correo"})), "Confirma la transicion antes de preparar o enviar el correo.");
        verificar(Arrays.equals(f.destinos, new String[] {"autorizacion@example.invalid", "suplente@example.invalid", "tercera@example.invalid"}), "Resuelve ambos separadores configurables.");
        verificar("RP:68180-APELLIDO NOMBRE-DU:12345678-LOCALIDAD-PROVINCIA-ID:1376-COMPARATIVA".equals(f.asunto), "Asunto respeta el formato RP y los datos del afiliado en mayusculas.");
        verificar(f.pedidoEnviado == f.pedido, "Conserva el pedido de presupuesto original.");
        verificar(f.adjuntosEnviados == f.adjuntos, "Conserva exactamente los documentos recuperados para el envio.");
        verificar(f.adjuntos.size() == 7 && f.listados == 1, "Comparativa, ficha RP, tres ordenes medicas y todos los presupuestos originales.");
        for (int i = 0; i < f.adjuntosEnviados.size(); i++) {
            verificar(f.adjuntosEnviados.get(i).getNombre().indexOf("OrdenCompra") < 0,
                    "El correo no genera ni adjunta Orden de Compra.");
        }
        verificar(f.cuerpo.indexOf("Orden de Compra") < 0, "El cuerpo no anuncia una OC antes de la autorizacion.");
        verificar(f.cuerpo.startsWith("Se envía a autorizar el requerimiento de compra Nro. " + ID),
                "El cuerpo usa Se envía y conserva el numero de compra.");
        verificar(f.cuerpo.indexOf("Reclamo Prestacional Nro. 68180") >= 0
                && f.cuerpo.indexOf("presupuestos cargados") >= 0, "El cuerpo conserva RP y anuncia todos los presupuestos cargados.");
        verificar(f.cuerpo.indexOf("Usuario:") < 0 && f.cuerpo.indexOf("usuario-prueba") < 0,
                "El cuerpo no informa el usuario que realizo la transicion.");
        aprobado("ENVIO_UNICO_DESPUES_DE_CAMBIO_CONFIRMADO");
        f.configuracion.clear();
        LOGS.registros.clear();
        Exception error = fallo(f, null, "La transicion ya fue procesada.");
        verificar(LOGS.registros.isEmpty(), "El rechazo de doble peticion no intenta ni registra fallo de notificacion.");
        verificar(error == f.errorDuplicado, "La doble peticion conserva el error original de transicion.");
        verificar(f.cambios == 1 && f.envios == 1 && f.eventos.size() == 6 && "cambiar".equals(f.eventos.get(5)) && f.listados == 1, "Segundo intento no prepara ni reenvia el correo.");
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
        verificar(f.cambios == 1 && f.envios == 0 && f.listados == 0, "Empresa confirma sin contexto documental, RP ni consultas de presupuestos.");
        verificar(f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "Sector empresa no recupera documentos de RP.");
        aprobado("EMPRESA_SIN_RP_NI_CONTEXTO_CONSERVA_FLUJO");
    }

    private static void probarConfiguracion() throws Exception {
        String[] invalidas = {null, "", "   ", "; ;;; ", ",,;", "direccion-invalida",
            "autorizacion@example.invalid;direccion-invalida;", "autorizacion@example.invalid\r\nBcc: otro@example.invalid"};
        for (int qa = 0; qa < 2; qa++) {
            for (int i = 0; i < invalidas.length; i++) {
                Fixture f = new Fixture();
                f.configuracion.put(REDIRECCION, qa == 1 ? "true" : "false");
                f.configuracion.put(qa == 1 ? QA : DESTINO, invalidas[i]);
                incidencia(f, contexto(), null);
                verificar(f.envios == 0 && f.listados == 0 && f.eventos.equals(Arrays.asList(new String[] {"cambiar"})),
                        "Destino invalido no prepara ni envia ni cae en destino real: QA=" + qa + " caso=" + i);
            }
        }
        aprobado("CONFIGURACION_INVALIDA_Y_SOLO_SEPARADORES_NO_ENVIA_EN_DESTINO_NI_QA");
        Fixture f = new Fixture();
        f.configuracion.put(DESTINO, "invalido");
        f.configuracion.put(REDIRECCION, " true ");
        f.configuracion.put(QA, "qa@example.invalid;");
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(Arrays.equals(f.destinos, new String[] {"qa@example.invalid"}), "QA con separador final reemplaza destinos reales incluso si no estan configurados correctamente.");
        aprobado("REDIRECCION_QA_RESPETADA_SIN_FALLBACK");
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
        for (DocumentoSimulado documento : f.documentos.values()) total += documento.contenido.length;
        f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", String.valueOf(total));
        incidencia(f, contexto(), null);
        verificar(f.envios == 0, "El total exacto del limite no se envia.");
        f = new Fixture();
        f.configuracion.put("GMAIL_SIZE_VALIDO_ADJUNTOS", String.valueOf(total + 1));
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
        verificar(f.cambios == 0 && f.envios == 0 && f.estadoPersistido == 4 && f.listados == 0 && f.eventos.equals(Arrays.asList(new String[] {"cambiar"})), "Fallo transaccional no prepara ni envia.");
        aprobado("FALLO_PERSISTENCIA_PROPAGA_ERROR_Y_NO_PREPARA_CORREO");
        f = new Fixture();
        f.fallaSmtp = true;
        LogRecord registro = incidencia(f, contexto(), "detalle interno SMTP que no debe mostrarse");
        verificar(f.envios == 1, "SMTP se intento una vez despues del estado confirmado.");
        verificar(registro.getMessage().indexOf("detalle interno") < 0, "El texto del log identifica la incidencia sin incorporar el mensaje de la causa.");
        aprobado("FALLO_SMTP_LOGUEADO_CON_RETORNO_NORMAL_Y_ESTADO_CONFIRMADO");
    }

    private static void probarPresupuestos() throws Exception {
        Fixture f = new Fixture();
        f.presupuestos.clear();
        f.enviarAAutorizar(ID, "usuario-prueba", contexto());
        verificar(f.envios == 1 && f.listados == 1 && f.adjuntosEnviados.size() == 5,
                "Una lista vacia no crea una regla nueva que bloquee la notificacion.");
        f = new Fixture();
        f.presupuestos = null;
        rechazarPresupuesto(f);
        f = new Fixture();
        f.fallaListado = true;
        incidencia(f, contexto(), "Listado de presupuestos simulado.");
        verificar(f.envios == 0, "Un fallo de listado no se interpreta como lista vacia.");
        for (int caso = 0; caso < 15; caso++) {
            f = new Fixture();
            RequerimientoCompraPresupuesto presupuesto = f.presupuestos.get(1);
            DocumentoSimulado documento = f.documentos.get(Long.valueOf(702));
            if (caso == 0) presupuesto.setIdRequerimiento(Integer.valueOf(ID + 1));
            if (caso == 1) presupuesto.setTipoDocumento(Integer.valueOf(2));
            if (caso == 2) presupuesto.setBajaFecha(new Date());
            if (caso == 3) presupuesto.setIdPrestador(null);
            if (caso == 4) presupuesto.setDlFileUuid("uuid-ajeno");
            if (caso == 5) presupuesto.setNombrePersistido("otro.pdf");
            if (caso == 6) documento.empresa = 99L;
            if (caso == 7) { documento.grupo = 99L; presupuesto.setDlGroupId(Long.valueOf(99L)); }
            if (caso == 8) documento.permitido = false;
            if (caso == 9) documento.contenido = new byte[] {1, 2, 3};
            if (caso == 10) documento.contenido = new byte[0];
            if (caso == 11) f.documentos.remove(Long.valueOf(702));
            if (caso == 12) presupuesto.setNombreOriginal("cotizacion.exe");
            if (caso == 13) presupuesto.setIdRequerimientoPresupuesto(Integer.valueOf(0));
            if (caso == 14) f.presupuestos.set(1, f.presupuestos.get(0));
            rechazarPresupuesto(f);
            if (caso <= 8 || caso == 11 || caso >= 13) verificar(!documento.leido,
                    "Identidad, relacion, sitio, permiso y duplicados se rechazan antes del contenido: caso=" + caso);
        }
        f = new Fixture();
        f.documentos.get(Long.valueOf(702)).fallaLectura = true;
        incidencia(f, contexto(), "Fallo DL segundo presupuesto simulado.");
        verificar(f.envios == 0 && f.estadoPersistido == 6
                && f.documentos.get(Long.valueOf(701)).leido,
                "El fallo del segundo presupuesto no envia el primero parcialmente ni revierte el estado.");
        aprobado("PRESUPUESTOS_REALES_IDENTIDAD_COMPRA_ACTIVO_VIEW_VERSION_CONTENIDO_Y_FALLOS");
    }

    private static void rechazarPresupuesto(Fixture f) throws Exception {
        incidencia(f, contexto(), null);
        verificar(f.envios == 0 && f.listados == 1 && f.estadoPersistido == 6,
                "Un presupuesto inconsistente evita el correo completo y conserva el estado confirmado.");
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

    private static Object modelo(final Class tipo, final String getter, final long valor) {
        return Proxy.newProxyInstance(tipo.getClassLoader(), new Class[] {tipo}, new InvocationHandler() {
            public Object invoke(Object proxy, Method metodo, Object[] argumentos) {
                if (getter.equals(metodo.getName())) return Long.valueOf(valor);
                if (tipo == PermissionChecker.class && "hasOwnerPermission".equals(metodo.getName())) return Boolean.FALSE;
                if (tipo == PermissionChecker.class && "hasPermission".equals(metodo.getName())) {
                    DocumentoSimulado documento = activo.documentos.get((Long) argumentos[2]);
                    verificar(documento != null && Long.valueOf(documento.grupo).equals(argumentos[0])
                            && DLFileEntry.class.getName().equals(argumentos[1]) && "VIEW".equals(argumentos[3]),
                            "Comprueba VIEW para la identidad DL antes de abrir el presupuesto.");
                    documento.vista = documento.permitido;
                    return Boolean.valueOf(documento.permitido);
                }
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

    /** Patron de ComprasComparativaRecuperacionTest y ComprasDocumentosPermisoAltaTest. */
    private static final class Fronteras implements InvocationHandler {
        private final ClassLoader portalAnterior = PortalClassLoaderUtil.getClassLoader();
        private final Field consultas = BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        private final Field entradas = DLFileEntryLocalServiceUtil.class.getDeclaredField("_service");
        private final Field archivos = DLLocalServiceUtil.class.getDeclaredField("_service");
        private final Object consultasAnteriores;
        private final Object entradasAnteriores;
        private final Object archivosAnteriores;
        Fronteras() throws Exception {
            PortalClassLoaderUtil.setClassLoader(ComprasAutorizacionEmailTest.class.getClassLoader());
            consultas.setAccessible(true); entradas.setAccessible(true); archivos.setAccessible(true);
            consultasAnteriores = consultas.get(null); entradasAnteriores = entradas.get(null); archivosAnteriores = archivos.get(null);
            consultas.set(null, new Lecturas());
            entradas.set(null, Proxy.newProxyInstance(DLFileEntryLocalService.class.getClassLoader(),
                    new Class[] {DLFileEntryLocalService.class}, this));
            archivos.set(null, Proxy.newProxyInstance(DLLocalService.class.getClassLoader(),
                    new Class[] {DLLocalService.class}, this));
        }
        void close() throws Exception {
            consultas.set(null, consultasAnteriores); entradas.set(null, entradasAnteriores); archivos.set(null, archivosAnteriores);
            activo = null;
            PortalClassLoaderUtil.setClassLoader(portalAnterior);
        }
        public Object invoke(Object proxy, Method metodo, Object[] argumentos) throws Throwable {
            String nombre = metodo.getName();
            DocumentoSimulado documento = null;
            if ("getDLFileEntry".equals(nombre)) documento = activo.documentos.get((Long) argumentos[0]);
            else if ("getFileEntry".equals(nombre) || "getFileAsStream".equals(nombre)) {
                int indice = "getFileEntry".equals(nombre) ? 1 : 2;
                for (DocumentoSimulado candidato : activo.documentos.values()) {
                    if (candidato.nombre.equals(argumentos[indice])) { documento = candidato; break; }
                }
            } else throw new UnsupportedOperationException("Frontera DL inesperada: " + nombre);
            if (documento == null) throw new com.liferay.portal.SystemException("Documento DL simulado no encontrado.");
            verificar(activo.estadoPersistido == 6, "La recuperacion documental ocurre despues de confirmar PARA AUTORIZAR.");
            if ("getFileAsStream".equals(nombre)) {
                verificar(argumentos.length == 4 && Long.valueOf(documento.empresa).equals(argumentos[0])
                        && Long.valueOf(documento.carpeta).equals(argumentos[1])
                        && Double.valueOf(2.5D).equals(argumentos[3]) && documento.vista,
                        "La lectura conserva empresa, carpeta, version vigente y VIEW previo.");
                if (documento.fallaLectura) throw new com.liferay.portal.SystemException("Fallo DL segundo presupuesto simulado.");
                documento.leido = true;
                return new ByteArrayInputStream(documento.contenido);
            }
            return documento.entry;
        }
    }

    private static final class Lecturas extends ConfiguracionComprasDatosFixture {
        public List<RequerimientoCompraPresupuesto> listarPresupuestos(int id) throws Exception {
            verificar(id == ID && activo.estadoPersistido == 6, "Lista cotizaciones de la misma compra despues del cambio de estado.");
            activo.listados++;
            activo.eventos.add("presupuestos");
            if (activo.fallaListado) throw new Exception("Listado de presupuestos simulado.");
            return activo.presupuestos;
        }
    }

    private static final class DocumentoSimulado implements InvocationHandler {
        final DLFileEntry entry;
        final long id;
        final long carpeta = 600L;
        final String nombre;
        final String uuid;
        final String nombreOriginal;
        long empresa = 1L;
        long grupo = 10136L;
        byte[] contenido;
        boolean permitido = true;
        boolean vista;
        boolean leido;
        boolean fallaLectura;
        DocumentoSimulado(RequerimientoCompraPresupuesto p, byte[] bytes) {
            id = p.getDlFileEntryId().longValue(); nombre = p.getNombrePersistido();
            uuid = p.getDlFileUuid(); nombreOriginal = p.getNombreOriginal(); contenido = bytes;
            entry = (DLFileEntry) Proxy.newProxyInstance(DLFileEntry.class.getClassLoader(), new Class[] {DLFileEntry.class}, this);
        }
        AdjuntoOrdenMedica adjuntoEsperado() { return new AdjuntoOrdenMedica(contenido, nombreOriginal, "application/pdf"); }
        public Object invoke(Object proxy, Method metodo, Object[] argumentos) throws Throwable {
            String n = metodo.getName();
            if ("getCompanyId".equals(n)) return Long.valueOf(empresa);
            if ("getGroupId".equals(n)) return Long.valueOf(grupo);
            if ("getFileEntryId".equals(n)) return Long.valueOf(id);
            if ("getFolderId".equals(n)) return Long.valueOf(carpeta);
            if ("getName".equals(n)) return nombre;
            if ("getUuid".equals(n)) return uuid;
            if ("getSize".equals(n)) return Integer.valueOf(contenido.length);
            if ("getVersion".equals(n)) return Double.valueOf(2.5D);
            if ("getUserId".equals(n)) return Long.valueOf(501L);
            throw new UnsupportedOperationException("Modelo DL inesperado: " + n);
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
        String cuerpo;
        int cambios;
        int envios;
        int estadoPersistido = 4;
        boolean fallaPedido;
        boolean fallaAdjuntos;
        boolean fallaCambio;
        boolean fallaSmtp;
        boolean fallaListado;
        int listados;
        List<RequerimientoCompraPresupuesto> presupuestos = new ArrayList<RequerimientoCompraPresupuesto>();
        Map<Long, DocumentoSimulado> documentos = new HashMap<Long, DocumentoSimulado>();
        final Exception errorCambio = new Exception("Fallo de persistencia simulado.");
        final Exception errorDuplicado = new Exception("La transicion ya fue procesada.");

        Fixture() throws Exception {
            activo = this;
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
            adjuntos.add(new AdjuntoOrdenMedica(pdf("Tercera orden medica"), "orden_medica_3.pdf", "application/pdf"));
            agregarPresupuesto(701L, 2624, "Cotizacion proveedor A (1).pdf");
            agregarPresupuesto(702L, 92967, "Presupuesto proveedor B - noviembre.pdf");
        }
        private void agregarPresupuesto(long id, int prestador, String nombre) throws Exception {
            RequerimientoCompraPresupuesto p = new RequerimientoCompraPresupuesto();
            p.setIdRequerimientoPresupuesto(Integer.valueOf((int) id));
            p.setIdRequerimiento(Integer.valueOf(ID));
            p.setTipoDocumento(Integer.valueOf(RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_PRESUPUESTO));
            p.setIdPrestador(Integer.valueOf(prestador));
            p.setNombreOriginal(nombre);
            p.setNombrePersistido("cotizacion-" + id + ".pdf");
            p.setDlGroupId(Long.valueOf(10136L));
            p.setDlFolderId(Long.valueOf(600L));
            p.setDlFileEntryId(Long.valueOf(id));
            p.setDlFileUuid("uuid-" + id);
            presupuestos.add(p);
            documentos.put(Long.valueOf(id), new DocumentoSimulado(p, pdf("Presupuesto del prestador " + prestador)));
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
            this.cuerpo = cuerpo;
            pedidoEnviado = documentoPedido;
            adjuntosEnviados = documentos;
            if (fallaSmtp) throw new Exception("detalle interno SMTP que no debe mostrarse");
        }
    }
}
