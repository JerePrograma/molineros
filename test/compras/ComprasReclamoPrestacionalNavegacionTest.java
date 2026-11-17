import ar.com.ospim.autorizaciones.action.EditarReclamosEntryAction;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/* Navegacion segun Liquidaciones/ReintegroActionUtil.getReintegroEntry:
 * una apertura reemplaza el estado temporal, sin persistir el abandonado.
 * Adaptacion del harness aislado ComprasDocumentosPermisoAltaTest.
 * Metodos productivos del helper/Action y requests simulados; sin DB ni red. */
public class ComprasReclamoPrestacionalNavegacionTest {
    private static final String USUARIO = "qa-reclamo";
    private static final String[] CLAVES_EDITOR = {
        WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA,
        "RECUPERACION_RECLAMO_PRESTACIONAL_COMPRA",
        WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION,
        WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION,
        WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION,
        WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION,
        WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION,
        WebKeysAutorizaciones.RECLAMO_NUEVO_ESTADO_OBS,
        WebKeysAutorizaciones.LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION,
        WebKeysAutorizaciones.RECLAMO_PRESTACION_CUENTA_SELECT,
        "RECLAMO_PRESTACION_ESTADO_ORIGINAL", "RECLAMOS_PROCESAR_IMAGENES",
        "rp.nuevoReclamo.nonce", "rp.nuevoReclamo.editor", "rp.nuevoReclamo.contextoCompra"
    };
    private static int checks;
    private static int escenarios;
    private static int fallidos;

    public static void main(String[] args) throws Exception {
        for (int caso = 0; caso < 25; caso++) {
            escenarios++;
            try {
                ejecutar(caso);
                System.out.println("PASS caso=" + caso);
            } catch (Throwable error) {
                fallidos++;
                System.out.println("FAIL caso=" + caso + " " + error.toString());
            }
        }
        System.out.println("escenarios=" + escenarios + " checks=" + checks + " fallidos=" + fallidos);
        if (fallidos != 0) { throw new AssertionError("Fallaron " + fallidos + " escenarios de navegacion"); }
    }

    private static void ejecutar(int caso) throws Exception {
        if (caso >= 10) { ejecutarSeleccion(caso - 10); return; }
        Sesion sesion = new Sesion();
        ReclamoPrestacional anterior = new ReclamoPrestacional();
        anterior.setId(caso == 0 ? 67428 : 0);
        ReclamoPrestacionalCompraContexto contextoAnterior = contexto(
                100, "nonce-anterior", caso == 2 ? 1L : System.currentTimeMillis());
        contextoAnterior.setReclamoEnEdicion(anterior);
        for (int i = 0; i < CLAVES_EDITOR.length; i++) {
            sesion.datos.put(CLAVES_EDITOR[i], new Object());
        }
        sesion.datos.put(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, anterior);
        sesion.datos.put(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, contextoAnterior);
        sesion.datos.put("rp.nuevoReclamo.editor", anterior);
        sesion.datos.put("rp.nuevoReclamo.contextoCompra", contextoAnterior);
        sesion.datos.put("otra.pantalla", "conservar");
        if (caso == 3) {
            sesion.datos.remove(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION);
            sesion.datos.remove(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA);
        }
        if (caso == 4) {
            sesion.datos.put(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, "formato-anterior");
            sesion.datos.put(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, "formato-anterior");
        }
        if (caso <= 4) {
            ReclamoPrestacionalCompraPrecargaHelper.prepararSesionParaConsulta(sesion.proxy, 200, 456);
            verificarLimpieza(sesion, null);
            comprobar(anterior.getId_reclamo() == (caso == 0 ? 67428 : 0), "No mutar reclamo anterior");
            return;
        }
        if (caso == 5) {
            ReclamoPrestacionalCompraContexto nuevo = contexto(200, "nonce-nuevo", System.currentTimeMillis());
            ReclamoPrestacionalCompraPrecargaHelper.reiniciarYRegistrarContextoDesdeCompras(sesion.proxy, nuevo);
            verificarLimpieza(sesion, nuevo);
            ReclamoPrestacional borrador = new ReclamoPrestacional();
            nuevo.setReclamoEnEdicion(borrador);
            sesion.datos.put(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, borrador);
            comprobar(ReclamoPrestacionalCompraPrecargaHelper.validarContextoEditor(
                    sesion.proxy, "nonce-nuevo", "compras", USUARIO, 0) == nuevo, "Contexto nuevo valido");
            rechazarContexto(sesion, "nonce-anterior", USUARIO, 0);
            rechazarContexto(sesion, "nonce-nuevo", "otro-usuario", 0);
            rechazarContexto(sesion, "nonce-nuevo", USUARIO, 456);
            comprobar(borrador.getId_reclamo() == 0, "Nuevo borrador mantiene ID cero");
            return;
        }
        if (caso == 6 || caso == 7) {
            Map<String, Object> copia = new HashMap<String, Object>(sesion.datos);
            boolean rechazo = false;
            try {
                ReclamoPrestacionalCompraPrecargaHelper.prepararSesionParaConsulta(
                        sesion.proxy, caso == 6 ? 0 : 200, caso == 7 ? 0 : 456);
            } catch (Exception esperado) { rechazo = true; }
            comprobar(rechazo, "ID invalido rechazado");
            comprobar(copia.equals(sesion.datos), "ID invalido no destruye sesion");
            return;
        }
        if (caso == 8) {
            Map<String, Object> copia = new HashMap<String, Object>(sesion.datos);
            boolean rechazo = false;
            try {
                ReclamoPrestacionalCompraPrecargaHelper.reiniciarYRegistrarContextoDesdeCompras(
                        sesion.proxy, contexto(200, "nonce-vencido", 1L));
            } catch (Exception esperado) { rechazo = true; }
            comprobar(rechazo, "Contexto vencido rechazado");
            comprobar(copia.equals(sesion.datos), "Contexto invalido no destruye sesion");
            return;
        }
        ReclamoPrestacionalCompraPrecargaHelper.limpiarEstadoEditorReclamoPrestacional(sesion.proxy);
        verificarLimpieza(sesion, null);
        ReclamoPrestacionalCompraPrecargaHelper.limpiarEstadoEditorReclamoPrestacional(sesion.proxy);
        verificarLimpieza(sesion, null);
    }

    /* Adapta ComprasPrimeraCargaVistaTest: Portal real con proxy reversible y
     * metodo privado productivo por reflexion. No ejecuta la baja ni usa JDBC. */
    private static void ejecutarSeleccion(int caso) throws Exception {
        final Sesion sesion = new Sesion();
        ReclamoPrestacional anterior = new ReclamoPrestacional();
        anterior.setId(caso == 7 || caso == 8 ? 0 : 60001);
        sesion.datos.put(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, anterior);
        sesion.datos.put(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION, new Object());
        sesion.datos.put("otra.pantalla", "conservar");
        final Map<String, String> parametros = new HashMap<String, String>();
        final Map<String, Object> atributos = new HashMap<String, Object>();
        parametros.put(Constants.CMD, Constants.DELETE);
        parametros.put("id_reclamosel", "60002");
        if (caso == 1 || caso == 12) { parametros.put(Constants.CMD, Constants.UPDATE); }
        if (caso == 3) { parametros.put("id_reclamosel", "0"); }
        if (caso == 4) { parametros.put("id_reclamosel", "-1"); }
        if (caso == 5) { parametros.put("id_reclamosel", "no-es-un-id"); }
        if (caso == 9) { parametros.put("origen", "compras"); }
        if (caso == 10) { parametros.put(Constants.CMD, Constants.VIEW); }
        if (caso == 11) { parametros.put(Constants.CMD, Constants.EDIT); }
        if (caso == 13) { parametros.put("id_reclamosel", "60001"); }
        if (caso == 6 || caso == 7 || caso == 8 || caso == 14) {
            ReclamoPrestacionalCompraContexto contexto = contexto(100, "nonce-valido", System.currentTimeMillis());
            contexto.setReclamoEnEdicion(anterior);
            sesion.datos.put(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, contexto);
            if (caso != 7) {
                parametros.put(WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE,
                        caso == 6 ? "nonce-invalido" : "nonce-valido");
            }
        }
        final User usuario = (User) Proxy.newProxyInstance(User.class.getClassLoader(),
                new Class[] {User.class}, new InvocationHandler() {
                    public Object invoke(Object objeto, Method metodo, Object[] args) {
                        if ("getScreenName".equals(metodo.getName())) { return USUARIO; }
                        throw new UnsupportedOperationException("Usuario inesperado: " + metodo.getName());
                    }
                });
        final HttpServletRequest http = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(), new Class[] {HttpServletRequest.class},
                new InvocationHandler() {
                    public Object invoke(Object objeto, Method metodo, Object[] args) {
                        if ("getSession".equals(metodo.getName())) { return sesion.proxy; }
                        throw new UnsupportedOperationException("HTTP inesperado: " + metodo.getName());
                    }
                });
        PortletRequest request = (PortletRequest) Proxy.newProxyInstance(
                PortletRequest.class.getClassLoader(),
                new Class[] {caso == 2 || caso == 12 ? ActionRequest.class : RenderRequest.class},
                new InvocationHandler() {
                    public Object invoke(Object objeto, Method metodo, Object[] args) {
                        String nombre = metodo.getName();
                        if ("getParameter".equals(nombre)) { return parametros.get(args[0]); }
                        if ("getAttribute".equals(nombre)) { return atributos.get(args[0]); }
                        if ("setAttribute".equals(nombre)) { atributos.put((String) args[0], args[1]); return null; }
                        throw new UnsupportedOperationException("Portlet inesperado: " + nombre);
                    }
                });
        Map<String, Object> copia = new HashMap<String, Object>(sesion.datos);
        Method resolver = EditarReclamosEntryAction.class.getDeclaredMethod("resolverContextoCompra",
                new Class[] {HttpSession.class, PortletRequest.class, User.class});
        resolver.setAccessible(true);
        Portal portalAnterior = PortalUtil.getPortal();
        Throwable rechazo = null;
        Object resultado = null;
        new PortalUtil().setPortal((Portal) Proxy.newProxyInstance(Portal.class.getClassLoader(),
                new Class[] {Portal.class}, new InvocationHandler() {
                    public Object invoke(Object objeto, Method metodo, Object[] args) {
                        if ("getUser".equals(metodo.getName())) { return usuario; }
                        if ("getHttpServletRequest".equals(metodo.getName())) { return http; }
                        throw new UnsupportedOperationException("Portal inesperado: " + metodo.getName());
                    }
                }));
        try {
            try {
                resultado = resolver.invoke(new EditarReclamosEntryAction(),
                        new Object[] {sesion.proxy, request, usuario});
            } catch (InvocationTargetException error) { rechazo = error.getCause(); }
        } finally { new PortalUtil().setPortal(portalAnterior); }
        comprobar(copia.equals(sesion.datos), "Seleccion conserva editor, listas y otras pantallas caso=" + caso);
        comprobar(anterior.getId_reclamo() == (caso == 7 || caso == 8 ? 0 : 60001),
                "Seleccion no cambia el ID del editor caso=" + caso);
        boolean permitido = caso == 0 || caso == 10 || caso == 11 || caso == 13;
        if (permitido) {
            comprobar(rechazo == null, "Seleccion explicita permitida caso=" + caso + " error=" + rechazo);
            comprobar(resultado == null, "Seleccion ordinaria no se convierte en precarga Compras");
            comprobar(Boolean.FALSE.equals(atributos.get("rp.view.restringirRecuperableCompras")),
                    "Seleccion ordinaria conserva vista sin contexto Compras");
        } else {
            comprobar(rechazo != null && rechazo.getClass() == Exception.class,
                    "Rechazo funcional de ID/editor/contexto caso=" + caso + " error=" + rechazo);
            if (caso == 1 || caso == 2 || caso == 3 || caso == 4 || caso == 12) {
                comprobar(rechazo.getMessage().indexOf("El formulario pertenece a otro Reclamo Prestacional.") >= 0,
                        "Mutacion/ID invalido conserva control de editor caso=" + caso);
            }
        }
    }

    private static ReclamoPrestacionalCompraContexto contexto(int id, String nonce, long fecha) {
        return new ReclamoPrestacionalCompraContexto(id, "", Integer.valueOf(1), USUARIO, fecha, nonce);
    }

    private static void verificarLimpieza(Sesion sesion, ReclamoPrestacionalCompraContexto nuevo) {
        for (int i = 0; i < CLAVES_EDITOR.length; i++) {
            Object esperado = CLAVES_EDITOR[i].equals(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA)
                    ? nuevo : null;
            comprobar(sesion.datos.get(CLAVES_EDITOR[i]) == esperado, "Sin residuo: " + CLAVES_EDITOR[i]);
        }
        comprobar("conservar".equals(sesion.datos.get("otra.pantalla")), "Conservar otros modulos");
        comprobar(sesion.datos.size() == (nuevo == null ? 1 : 2), "Solo contexto activo y otras pantallas");
    }

    private static void rechazarContexto(Sesion sesion, String nonce, String usuario, int id) throws Exception {
        boolean rechazo = false;
        try {
            ReclamoPrestacionalCompraPrecargaHelper.validarContextoEditor(sesion.proxy, nonce, "compras", usuario, id);
        } catch (Exception esperado) { rechazo = true; }
        comprobar(rechazo, "Rechazar formulario anterior/usuario/ID incorrecto");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        checks++;
        if (!condicion) { throw new AssertionError(mensaje); }
    }

    private static final class Sesion implements InvocationHandler {
        final Map<String, Object> datos = new HashMap<String, Object>();
        final HttpSession proxy = (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(), new Class[] {HttpSession.class}, this);
        public Object invoke(Object objeto, Method metodo, Object[] args) {
            String nombre = metodo.getName();
            if ("getAttribute".equals(nombre) || "getValue".equals(nombre)) { return datos.get(args[0]); }
            if ("setAttribute".equals(nombre) || "putValue".equals(nombre)) {
                if (args[1] == null) { datos.remove(args[0]); } else { datos.put((String) args[0], args[1]); }
                return null;
            }
            if ("removeAttribute".equals(nombre) || "removeValue".equals(nombre)) { return datos.remove(args[0]); }
            if ("getAttributeNames".equals(nombre)) { return Collections.enumeration(datos.keySet()); }
            if ("toString".equals(nombre)) { return "SesionQA"; }
            if ("hashCode".equals(nombre)) { return Integer.valueOf(System.identityHashCode(objeto)); }
            if ("equals".equals(nombre)) { return Boolean.valueOf(objeto == args[0]); }
            Class tipo = metodo.getReturnType();
            if (tipo == Boolean.TYPE) { return Boolean.FALSE; }
            if (tipo == Integer.TYPE) { return Integer.valueOf(0); }
            if (tipo == Long.TYPE) { return Long.valueOf(0L); }
            return null;
        }
    }
}
