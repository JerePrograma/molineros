import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.action.CambiarEstadoRequerimientoCompraAction;
import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraGuardadoToken;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

/** Adapta ComprasComparativaRecuperacionActionTest y el proxy Portal de ComprasReclamoPrestacionalNavegacionTest. */
public class ComprasAutorizacionRecuperacionActionTest {
    private static int checks;
    private static int lecturasRequerimiento;
    private static User usuario;

    public static void main(String[] args) throws Exception {
        Portal anterior = PortalUtil.getPortal();
        Field campo = BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        campo.setAccessible(true);
        Object consultasAnteriores = campo.get(null);
        Logger logger = Logger.getLogger(CambiarEstadoRequerimientoCompraAction.class.getName());
        Level nivelAnterior = logger.getLevel();
        logger.setLevel(Level.OFF);
        campo.set(null, new Lecturas());
        new PortalUtil().setPortal((Portal) Proxy.newProxyInstance(Portal.class.getClassLoader(),
                new Class[] {Portal.class}, new InvocationHandler() {
                    public Object invoke(Object objeto, Method metodo, Object[] args) {
                        if ("getUser".equals(metodo.getName())) return usuario;
                        if ("getCompanyId".equals(metodo.getName())) return Long.valueOf(1L);
                        if ("getPortletNamespace".equals(metodo.getName())) return "_autorizacion_prueba_";
                        throw new AssertionError("Frontera Portal inesperada: " + metodo.getName());
                    }
                }));
        try {
            comandos(); permisos(); tokens(); confirmacion();
            comprobar("ninguna lectura de requerimiento ni acceso al helper", lecturasRequerimiento == 0);
            System.out.println("COMPRAS_AUTORIZACION_RECUPERACION_ACTION_OK checks=" + checks);
            System.out.println("Action, permisos y token reales; rechazos previos a helper, DB y SMTP.");
        } finally {
            new PortalUtil().setPortal(anterior);
            campo.set(null, consultasAnteriores);
            logger.setLevel(nivelAnterior);
        }
    }

    private static void comandos() throws Exception {
        usuario = usuario("COTIZAR_COMPRAS");
        Peticion p = peticion(); token(p); p.parametros.put("estado_nuevo", "6");
        rechazar(p, "operaciones incompatibles", "mezcla con estado");
        RequerimientoCompraGuardadoToken.consumir((ActionRequest) p.proxy(),
                p.parametros.get(RequerimientoCompraGuardadoToken.PARAMETRO));
        p = peticion(); token(p); p.parametros.put("reintentar_notificaciones", "true");
        rechazar(p, "operaciones incompatibles", "mezcla con notificacion a prestadores");
    }

    private static void permisos() throws Exception {
        String[] denegados = {"VIEW_COMPRAS", "AUTORIZAR_COMPRAS", "ABM_COMPRAS", "COTIZAR_COMPRAS_OTRO", ""};
        for (int i = 0; i < denegados.length; i++) {
            usuario = usuario(denegados[i]); Peticion p = peticion(); token(p);
            rechazar(p, "No posee permisos para cotizar", "rol no autorizado " + i);
            RequerimientoCompraGuardadoToken.consumir((ActionRequest) p.proxy(),
                    p.parametros.get(RequerimientoCompraGuardadoToken.PARAMETRO));
        }
        usuario = null;
        rechazar(peticion(), "No posee permisos para cotizar", "sin usuario autenticado");
    }

    private static void tokens() throws Exception {
        usuario = usuario("COTIZAR_COMPRAS");
        rechazar(peticion(), "No se proces", "token ausente");
        Peticion p = peticion(); p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO, "desconocido");
        rechazar(p, "No se proces", "token desconocido");
        Peticion otraSesion = peticion(); token(otraSesion); p = peticion();
        p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO,
                otraSesion.parametros.get(RequerimientoCompraGuardadoToken.PARAMETRO));
        rechazar(p, "No se proces", "token de otra sesion");
        p = peticion(); token(p);
        RequerimientoCompraGuardadoToken.consumir((ActionRequest) p.proxy(),
                p.parametros.get(RequerimientoCompraGuardadoToken.PARAMETRO));
        rechazar(p, "No se proces", "token ya consumido");
    }

    private static void confirmacion() throws Exception {
        usuario = usuario("COTIZAR_COMPRAS");
        String[] valores = {null, "", "NO", "true"};
        for (int i = 0; i < valores.length; i++) {
            Peticion p = peticion(); token(p);
            if (valores[i] == null) p.parametros.remove("confirmar_reintento_autorizacion");
            else p.parametros.put("confirmar_reintento_autorizacion", valores[i]);
            rechazar(p, "Debe verificar el envio anterior", "confirmacion invalida " + i);
            p.parametros.put("confirmar_reintento_autorizacion", "SI");
            rechazar(p, "No se proces", "confirmacion fallida consume token " + i);
        }
    }

    private static void rechazar(Peticion p, String mensaje, String caso) throws Exception {
        new CambiarEstadoRequerimientoCompraAction().processAction(null, null, null,
                (ActionRequest) p.proxy(),
                (ActionResponse) Proxy.newProxyInstance(ActionResponse.class.getClassLoader(),
                        new Class[] {ActionResponse.class}, p));
        Object error = p.atributos.get(WebKeysCompras.ERROR_PARA_ALERT);
        comprobar(caso + " rechazo especifico", error instanceof String && ((String) error).indexOf(mensaje) >= 0);
        comprobar(caso + " no confirma operacion", "false".equals(p.render.get("compras_guardado")));
        comprobar(caso + " informa error", "true".equals(p.render.get("compras_error")));
        comprobar(caso + " conserva requerimiento", "1458".equals(p.render.get("id_requerimiento_compra")));
        comprobar(caso + " conserva ruta", "/compras/ver_requerimiento".equals(p.render.get("struts_action")));
        comprobar(caso + " sin falso cambio estado", !SessionMessages.contains((PortletRequest) p.proxy(),
                "estado-requerimiento-compra-actualizado"));
        comprobar(caso + " sin falso exito SMTP", !SessionMessages.contains((PortletRequest) p.proxy(),
                "correo-autorizacion-compra-confirmado"));
        comprobar(caso + " sin lectura de datos", lecturasRequerimiento == 0);
    }

    private static Peticion peticion() {
        Peticion p = new Peticion(); p.parametros.put("id_requerimiento_compra", "1458");
        p.parametros.put("reintentar_autorizacion", "true");
        p.parametros.put("confirmar_reintento_autorizacion", "SI"); return p;
    }
    private static void token(Peticion p) {
        RequerimientoCompraGuardadoToken.publicar((RenderRequest) p.proxy());
        p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO, (String) p.atributos.get("COMPRAS_SAVE_TOKEN"));
    }
    private static User usuario(String nombre) {
        final List<Role> roles = new ArrayList<Role>();
        if (nombre.length() > 0) { Role rol = new RoleImpl(); rol.setName(nombre); roles.add(rol); }
        return new UserImpl() {
            public List<Role> getRoles() { return roles; }
            public String getScreenName() { return "recuperacion_action_fixture"; }
        };
    }
    private static class Lecturas extends ConfiguracionComprasDatosFixture {
        public RequerimientoCompra getRequerimientoCompra(int id) throws Exception {
            lecturasRequerimiento++;
            throw new AssertionError("La Action llego al helper antes de rechazar la solicitud.");
        }
    }
    private static class Peticion implements InvocationHandler {
        final Map<String, String> parametros = new HashMap<String, String>();
        final Map<String, String> render = new HashMap<String, String>();
        final Map<String, Object> atributos = new HashMap<String, Object>();
        final Map<String, Object> sesion = new HashMap<String, Object>();
        final PortletSession session = (PortletSession) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[] {PortletSession.class}, this);
        Peticion() {
            // Mismo contexto de forward usado por ComprasContactoAfiliadoTokenTest.
            com.liferay.portal.model.Portlet portlet = (com.liferay.portal.model.Portlet) Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class[] {com.liferay.portal.model.Portlet.class},
                    new InvocationHandler() {
                        public Object invoke(Object objeto, Method metodo, Object[] args) {
                            if ("getRootPortletId".equals(metodo.getName()) || "getPortletId".equals(metodo.getName())) {
                                return "autorizacion_prueba";
                            }
                            return null;
                        }
                    });
            atributos.put("javax.portlet.config", new com.liferay.portlet.PortletConfigImpl(portlet, null));
        }
        Object proxy() { return Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[] {ActionRequest.class, RenderRequest.class}, this); }
        public Object invoke(Object proxy, Method metodo, Object[] args) {
            String nombre = metodo.getName(); Map<String, Object> store = proxy instanceof PortletSession ? sesion : atributos;
            if ("getPortletSession".equals(nombre)) return session;
            if ("getAttribute".equals(nombre)) return store.get(args[0]);
            if ("setAttribute".equals(nombre)) { store.put((String) args[0], args[1]); return null; }
            if ("removeAttribute".equals(nombre)) { store.remove(args[0]); return null; }
            if ("getAttributeNames".equals(nombre)) return java.util.Collections.enumeration(store.keySet());
            if ("getParameter".equals(nombre)) return parametros.get(args[0]);
            if ("getParameterValues".equals(nombre)) { String valor = parametros.get(args[0]); return valor == null ? null : new String[] {valor}; }
            if ("getParameterNames".equals(nombre)) return java.util.Collections.enumeration(parametros.keySet());
            if ("getParameterMap".equals(nombre)) { Map<String, String[]> mapa = new HashMap<String, String[]>(); for (String clave : parametros.keySet()) mapa.put(clave, new String[] {parametros.get(clave)}); return mapa; }
            if ("setRenderParameter".equals(nombre)) { render.put((String) args[0], (String) args[1]); return null; }
            if (metodo.getReturnType() == Boolean.TYPE) return Boolean.FALSE;
            if (metodo.getReturnType() == Integer.TYPE) return Integer.valueOf(0);
            if (metodo.getReturnType() == Long.TYPE) return Long.valueOf(0L);
            return null;
        }
    }
    private static void comprobar(String caso, boolean condicion) { checks++; if (!condicion) throw new AssertionError(caso); }
}
