import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.crm.action.ActualizaDomicilioAfiliadoAction;
import ar.com.ospim.compras.requerimientos.action.ActualizarContactoAfiliadoCompraAction;
import ar.com.ospim.compras.requerimientos.action.ActualizarContactoAfiliadoCompraToken;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.apache.struts.action.ActionForward;
import com.liferay.portal.struts.ActionConstants;

/** Fronteras de sesion y lectura simuladas; token, permiso y validacion OC reales.
 * Referencias: ComprasOrdenHelperTest y validarPermisoReclamoPrestacional de
 * Autorizaciones/DescargarDocumentoCompraReclamoAction. No ejecuta CRM ni DB. */
public class ComprasContactoAfiliadoTokenTest {
    private static final String CUIL = "20111111112";
    private static int checks;

    public static void main(String[] args) throws Exception {
        ComprasFuenteDatosFixture datos = new ComprasFuenteDatosFixture();
        datos.cargarConfiguracionPrueba();
        datos.instalar();
        contextoSuministro();
        contextoAbm();
        permisos();
        estadoVigente();
        contactoFamiliar();
        respuestaActualizacion();
        System.out.println("COMPRAS_CONTACTO_AFILIADO_OFFLINE_OK checks=" + checks);
        System.out.println("Token/permiso/Helper productivos; sin guardar contactos, DB, CRM ni UI.");
    }

    private static void contextoSuministro() throws Exception {
        Sesion sesion = new Sesion();
        Peticion p = new Peticion(sesion);
        String token = publicar(p, requerimiento(40), true);
        igual("publicacion token no vacio", Boolean.TRUE, Boolean.valueOf(token != null && token.length() > 0));
        suministro("contexto correcto", p, CUIL, 1, 40, false);
        p.parametros.put("id_requerimiento_compra", "999");
        suministro("id confiable viene de la sesion", p, CUIL, 1, 40, false);
        suministro("CUIL distinto", p, "20222222223", 1, 0, true);
        suministro("integrante distinto", p, CUIL, 2, 0, true);
        p.parametros.remove(ActualizarContactoAfiliadoCompraToken.PARAM_TOKEN);
        suministro("token omitido", p, CUIL, 1, 0, true);
        p.parametros.put(ActualizarContactoAfiliadoCompraToken.PARAM_TOKEN, "inexistente");
        suministro("token desconocido", p, CUIL, 1, 0, true);
        Peticion otraSesion = new Peticion(new Sesion());
        otraSesion.parametros.put(ActualizarContactoAfiliadoCompraToken.PARAM_TOKEN, token);
        suministro("token de otra sesion", otraSesion, CUIL, 1, 0, true);
        p.parametros.put(ActualizarContactoAfiliadoCompraToken.PARAM_TOKEN, token);
        suministro("error ajeno no consume contexto valido", p, CUIL, 1, 40, false);

        Peticion segunda = new Peticion(sesion);
        String token2 = publicar(segunda, requerimiento(41), true);
        igual("dos pestanas tienen token distinto", Boolean.FALSE, Boolean.valueOf(token.equals(token2)));
        suministro("primera pestana conserva requerimiento", p, CUIL, 1, 40, false);
        suministro("segunda pestana conserva requerimiento", segunda, CUIL, 1, 41, false);

        Peticion sinId = new Peticion(new Sesion());
        publicar(sinId, requerimiento(0), true);
        suministro("contexto suministro sin requerimiento persistido", sinId, CUIL, 1, 0, true);
    }

    private static void contextoAbm() throws Exception {
        Peticion p = new Peticion(new Sesion());
        publicar(p, requerimiento(40), false);
        ActualizarContactoAfiliadoCompraToken.validar(p.request(), CUIL, 1);
        igual("token historico ABM conserva validar", Boolean.TRUE, Boolean.TRUE);
        suministro("token generico anterior no habilita GESTION", p, CUIL, 1, 0, true);
        boolean rechazo = false;
        try {
            ActualizarContactoAfiliadoCompraToken.vincular(p.request(), "20222222223", 2);
        } catch (Exception e) {
            validarRechazo(e); rechazo = true;
        }
        igual("ABM no reasigna requerimiento persistido", Boolean.TRUE, Boolean.valueOf(rechazo));
        ActualizarContactoAfiliadoCompraToken.vincular(p.request(), CUIL, 1);
        ActualizarContactoAfiliadoCompraToken.validar(p.request(), CUIL, 1);
        igual("ABM conserva vinculo original", Boolean.TRUE, Boolean.TRUE);

        RequerimientoCompra alta = requerimiento(0);
        alta.setAfiliadoCuilTitular(null); alta.setAfiliadoInt(null);
        Peticion nuevo = new Peticion(new Sesion());
        publicar(nuevo, alta, false);
        ActualizarContactoAfiliadoCompraToken.vincular(nuevo.request(), CUIL, 1);
        ActualizarContactoAfiliadoCompraToken.validar(nuevo.request(), CUIL, 1);
        igual("ABM puede vincular afiliado al alta", Boolean.TRUE, Boolean.TRUE);
        suministro("bind ABM no convierte origen en suministro", nuevo, CUIL, 1, 0, true);
    }

    private static void permisos() throws Exception {
        Modelo modelo = new Modelo();
        Accion accion = new Accion(modelo);
        User gestion = usuario(new String[] { ConfiguracionComprasHelper.getRolGestionCompras() });
        User abm = usuario(new String[] { ConfiguracionComprasHelper.getRolAbmCompras() });
        User ambos = usuario(new String[] { ConfiguracionComprasHelper.getRolAbmCompras(),
                ConfiguracionComprasHelper.getRolGestionCompras() });
        Peticion p = new Peticion(new Sesion());
        publicar(p, requerimiento(40), true);
        p.parametros.put("cmd", "view");
        permiso("GESTION abre contacto de OC", accion, p, gestion, CUIL, 1, false);
        igual("GESTION valida requerimiento de token", Integer.valueOf(40), Integer.valueOf(modelo.ultimoId));
        p.parametros.put("cmd", "save");
        permiso("GESTION autoriza guardar mismo contacto", accion, p, gestion, CUIL, 1, false);
        p.parametros.put("id_requerimiento_compra", "999");
        permiso("parametro ajeno no suplanta contexto", accion, p, gestion, CUIL, 1, false);
        igual("parametro ajeno no dirige consulta", Integer.valueOf(40), Integer.valueOf(modelo.ultimoId));
        p.parametros.put("cmd", "bind");
        int lecturas = modelo.lecturas;
        permiso("GESTION no vincula otro afiliado", accion, p, gestion, CUIL, 1, true);
        igual("bind GESTION rechazado antes de leer OC", Integer.valueOf(lecturas), Integer.valueOf(modelo.lecturas));
        p.parametros.put("cmd", "save");
        permiso("GESTION rechaza otro CUIL", accion, p, gestion, "20222222223", 1, true);
        permiso("GESTION rechaza otro integrante", accion, p, gestion, CUIL, 2, true);
        permiso("anonimo sin acceso", accion, p, null, CUIL, 1, true);
        permiso("rol VIEW no obtiene permiso de contacto", accion, p,
                usuario(new String[] { ConfiguracionComprasHelper.getRolViewCompras() }), CUIL, 1, true);
        permiso("nombre de rol parecido no autoriza", accion, p,
                usuario(new String[] { ConfiguracionComprasHelper.getRolGestionCompras() + "_OTRO" }), CUIL, 1, true);
        Peticion generico = new Peticion(new Sesion());
        publicar(generico, requerimiento(40), false);
        generico.parametros.put("cmd", "save");
        permiso("GESTION no usa token ABM", accion, generico, gestion, CUIL, 1, true);
        Peticion sinToken = new Peticion(new Sesion());
        permiso("GESTION requiere token", accion, sinToken, gestion, CUIL, 1, true);

        lecturas = modelo.lecturas;
        sinToken.parametros.put("cmd", "bind");
        permiso("rama ABM de permisos conserva bind", accion, sinToken, abm, CUIL, 1, false);
        permiso("multirrol conserva facultades ABM", accion, sinToken, ambos, CUIL, 1, false);
        igual("rama ABM no requiere validar una OC", Integer.valueOf(lecturas), Integer.valueOf(modelo.lecturas));
    }

    private static void estadoVigente() throws Exception {
        Modelo m = new Modelo();
        Accion accion = new Accion(m);
        User gestion = usuario(new String[] { ConfiguracionComprasHelper.getRolGestionCompras() });
        Peticion p = new Peticion(new Sesion());
        publicar(p, requerimiento(40), true); p.parametros.put("cmd", "save");
        permiso("estado vigente inicial", accion, p, gestion, CUIL, 1, false);
        m.r.setEstado(ConfiguracionComprasHelper.getEstadoAutorizado());
        permiso("OC pierde estado emitido despues de render", accion, p, gestion, CUIL, 1, true);
        m.r.setEstado(ConfiguracionComprasHelper.getEstadoOrdenCompra());
        m.r.setBajaFecha(new Date());
        permiso("requerimiento dado de baja despues de render", accion, p, gestion, CUIL, 1, true);
        m.r.setBajaFecha(null);
        m.r.setAfiliadoCuilTitular("20222222223");
        permiso("afiliado cambia despues de render", accion, p, gestion, CUIL, 1, true);
        m.r.setAfiliadoCuilTitular(CUIL); m.r.setAfiliadoInt(Integer.valueOf(2));
        permiso("integrante cambia despues de render", accion, p, gestion, CUIL, 1, true);
        m.r.setAfiliadoInt(null);
        permiso("identidad actual incompleta", accion, p, gestion, CUIL, 1, true);
        m.r.setAfiliadoInt(Integer.valueOf(1));
        m.orden.setIdRequerimientoCompra(41);
        permiso("OC pertenece a otro requerimiento", accion, p, gestion, CUIL, 1, true);
        m.orden.setIdRequerimientoCompra(40); m.orden.setIdOrdenCompra(0);
        permiso("OC no persistida", accion, p, gestion, CUIL, 1, true);
        m.orden = null;
        permiso("OC eliminada despues de render", accion, p, gestion, CUIL, 1, true);
        m.r.setIdRequerimientoCompra(41);
        permiso("lectura no devuelve requerimiento solicitado", accion, p, gestion, CUIL, 1, true);
        m.r = null;
        permiso("requerimiento ya no existe", accion, p, gestion, CUIL, 1, true);
        m.falla = true;
        permiso("fallo lectura no autoriza contacto", accion, p, gestion, CUIL, 1, true);
        m.falla = false;
        boolean rechazo = false;
        int lecturas = m.lecturas;
        try {
            m.validarAfiliadoContactoOrden(0, CUIL, 1);
        } catch (Exception e) {
            validarRechazo(e); rechazo = true;
        }
        igual("Helper rechaza ID no informado", Boolean.TRUE, Boolean.valueOf(rechazo));
        igual("ID invalido sin consulta", Integer.valueOf(lecturas), Integer.valueOf(m.lecturas));
    }

    private static void contactoFamiliar() throws Exception {
        Accion accion = new Accion(new Modelo());
        Peticion p = new Peticion(new Sesion());
        p.parametros.put("inte", "7");
        boolean rechazo = false;
        try {
            accion.integranteContacto(p.request());
        } catch (IllegalStateException e) {
            validarRechazo(e); rechazo = true;
        }
        igual("contacto sin afiliado validado falla cerrado", Boolean.TRUE, Boolean.valueOf(rechazo));
        p.atributos.put("COMPRAS_CONTACTO_AFILIADO_VALIDADO", "afiliado no validado");
        rechazo = false;
        try {
            accion.integranteContacto(p.request());
        } catch (IllegalStateException e) {
            validarRechazo(e); rechazo = true;
        }
        igual("contexto de otro tipo no autoriza contacto", Boolean.TRUE, Boolean.valueOf(rechazo));

        Afiliado afiliado = new Afiliado();
        p.atributos.put("COMPRAS_CONTACTO_AFILIADO_VALIDADO", afiliado);
        // El popup CRM ofrece contacto propio para titular, conyuge y concubino.
        int[] propios = { WebKeysAfiliados.PARENTESCO_DEFAULT,
                WebKeysAfiliados.CONYUGE_DEFAULT, WebKeysAfiliados.CONCUBINO_DEFAULT };
        for (int i = 0; i < propios.length; i++) {
            afiliado.setId_parentesco(propios[i]);
            igual("parentesco con contacto propio " + propios[i], Integer.valueOf(7),
                    Integer.valueOf(accion.integranteContacto(p.request())));
        }
        int[] familiares = { WebKeysAfiliados.HIJO_MENOR, WebKeysAfiliados.HIJO_MAYOR,
                WebKeysAfiliados.HIJO_MENOR_CONYUGE, WebKeysAfiliados.MENOR_BAJO_GUARDA };
        for (int i = 0; i < familiares.length; i++) {
            afiliado.setId_parentesco(familiares[i]);
            igual("contacto familiar del titular " + familiares[i], Integer.valueOf(0),
                    Integer.valueOf(accion.integranteContacto(p.request())));
        }
        igual("resolver contacto no altera identidad solicitada", "7", p.parametros.get("inte"));
        RequerimientoCompra r = requerimiento(40); r.setAfiliadoInt(Integer.valueOf(7));
        publicar(p, r, true);
        suministro("token conserva integrante del requerimiento", p, CUIL, 7, 40, false);
        CrmPrueba crm = new CrmPrueba();
        igual("CRM ajeno a Compras mantiene integrante recibido", Integer.valueOf(7),
                Integer.valueOf(crm.integranteContacto(p.request())));
    }

    private static void respuestaActualizacion() throws Exception {
        com.liferay.portal.util.Portal anterior = com.liferay.portal.util.PortalUtil.getPortal();
        com.liferay.portal.util.Portal contenedor = (com.liferay.portal.util.Portal)Proxy.newProxyInstance(
                ComprasContactoAfiliadoTokenTest.class.getClassLoader(),
                new Class[] { com.liferay.portal.util.Portal.class }, new InvocationHandler() {
                    public Object invoke(Object objeto, Method metodo, Object[] args) {
                        if ("getPortletNamespace".equals(metodo.getName())) return "_" + args[0] + "_";
                        throw new UnsupportedOperationException("Operacion Portal inesperada: " + metodo.getName());
                    }
                });
        com.liferay.portal.util.PortalUtil portalUtil = new com.liferay.portal.util.PortalUtil();
        portalUtil.setPortal(contenedor);
        try {
            Accion accion = new Accion(new Modelo());
            ActionForward popup = new ActionForward("/popup.jsp");
            Peticion p = new Peticion(new Sesion());
            p.parametros.put("cmd", "save"); p.parametros.put("respuesta_contacto_json", "1");
            Respuesta respuesta = new Respuesta();
            ActionForward resultado = accion.responder(p.request(), respuesta.proxy, popup);
            igual("save nuevo devuelve JSON de exito", "{\"status\":\"ok\"}", respuesta.texto());
            igual("tipo de respuesta JSON", "application/json", respuesta.contentType);
            igual("JSON usa COMMON_NULL", ActionConstants.COMMON_NULL, resultado.getPath());

            p = new Peticion(new Sesion()); p.parametros.put("cmd", "save");
            p.parametros.put("respuesta_contacto_json", "1");
            accion.forward(p.request(), "portlet.afiliados.error");
            respuesta = new Respuesta(); boolean rechazo = false;
            try {
                accion.responder(p.request(), respuesta.proxy, new ActionForward("/error.jsp"));
            } catch (Exception e) {
                validarRechazo(e); rechazo = true;
            }
            igual("forward de error no responde exito", Boolean.TRUE, Boolean.valueOf(rechazo));
            igual("error no escribe status OK", "", respuesta.texto());
            igual("error no anuncia tipo JSON de exito", null, respuesta.contentType);

            p = new Peticion(new Sesion()); p.parametros.put("cmd", "save"); respuesta = new Respuesta();
            igual("save legacy conserva el mismo forward", popup, accion.responder(p.request(), respuesta.proxy, popup));
            igual("save legacy no agrega JSON", "", respuesta.texto());
            p.parametros.put("respuesta_contacto_json", "0");
            igual("flag desactivada conserva forward", popup, accion.responder(p.request(), respuesta.proxy, popup));
            p.parametros.put("cmd", "view"); p.parametros.put("respuesta_contacto_json", "1"); respuesta = new Respuesta();
            igual("view conserva popup aunque tenga flag", popup, accion.responder(p.request(), respuesta.proxy, popup));
            igual("view no escribe JSON de guardado", "", respuesta.texto());
            p.parametros.put("cmd", "save"); p.parametros.remove("respuesta_contacto_json");
            accion.forward(p.request(), "portlet.afiliados.error");
            ActionForward error = new ActionForward("/error.jsp");
            igual("error legacy conserva su forward", error, accion.responder(p.request(), respuesta.proxy, error));
            igual("error legacy sin falso OK", "", respuesta.texto());
        } finally {
            portalUtil.setPortal(anterior);
        }
    }

    private static String publicar(Peticion p, RequerimientoCompra r, boolean suministro) {
        if (suministro) ActualizarContactoAfiliadoCompraToken.publicarSuministro(p.request(), r);
        else ActualizarContactoAfiliadoCompraToken.publicar(p.request(), r);
        String token = (String)p.atributos.get(ActualizarContactoAfiliadoCompraToken.ATTR_TOKEN);
        p.parametros.put(ActualizarContactoAfiliadoCompraToken.PARAM_TOKEN, token);
        return token;
    }

    private static void suministro(String caso, Peticion p, String cuil, int inte, int id, boolean debeRechazar)
            throws Exception {
        boolean rechazo = false;
        int actual = 0;
        try {
            actual = ActualizarContactoAfiliadoCompraToken.validarSuministro(p.request(), cuil, inte);
        } catch (Exception e) {
            validarRechazo(e); rechazo = true;
        }
        igual(caso + " rechazo", Boolean.valueOf(debeRechazar), Boolean.valueOf(rechazo));
        if (!debeRechazar) igual(caso + " id", Integer.valueOf(id), Integer.valueOf(actual));
    }

    private static void permiso(String caso, Accion accion, Peticion p, User user, String cuil, int inte,
            boolean debeRechazar) throws Exception {
        boolean rechazo = false;
        try {
            accion.autorizar(p.request(), user, cuil, inte);
        } catch (Exception e) {
            validarRechazo(e); rechazo = true;
        }
        igual(caso, Boolean.valueOf(debeRechazar), Boolean.valueOf(rechazo));
    }

    private static void validarRechazo(Exception e) throws Exception {
        if (e instanceof NullPointerException || e.getMessage() == null || e.getMessage().length() == 0) throw e;
    }

    private static RequerimientoCompra requerimiento(int id) throws Exception {
        RequerimientoCompra r = new RequerimientoCompra();
        r.setIdRequerimientoCompra(id); r.setEstado(ConfiguracionComprasHelper.getEstadoOrdenCompra());
        r.setAfiliadoCuilTitular(CUIL); r.setAfiliadoInt(Integer.valueOf(1));
        return r;
    }

    private static User usuario(String[] nombres) {
        final List<Role> roles = new ArrayList<Role>();
        for (int i = 0; i < nombres.length; i++) {
            Role role = new RoleImpl(); role.setName(nombres[i]); roles.add(role);
        }
        return new UserImpl() {
            public List<Role> getRoles() { return roles; }
        };
    }

    private static void igual(String caso, Object esperado, Object actual) {
        checks++;
        if (esperado == null ? actual != null : !esperado.equals(actual)) {
            throw new AssertionError(caso + ": esperado=" + esperado + ", actual=" + actual);
        }
    }

    private static class Accion extends ActualizarContactoAfiliadoCompraAction {
        private final Modelo modelo;
        Accion(Modelo modelo) { this.modelo = modelo; }
        protected RequerimientoCompraOrdenHelper obtenerHelperOrden() { return modelo; }
        int integranteContacto(RenderRequest request) { return obtenerIntegranteContacto(request); }
        ActionForward responder(RenderRequest request, RenderResponse response, ActionForward resultado) throws Exception {
            return responderActualizacion(request, response, resultado);
        }
        void forward(RenderRequest request, String forward) { setForward(request, forward); }
        void autorizar(RenderRequest request, User user, String cuil, int inte) throws Exception {
            validarPermiso(request, user, cuil, inte);
        }
    }

    private static class CrmPrueba extends ActualizaDomicilioAfiliadoAction {
        int integranteContacto(RenderRequest request) { return obtenerIntegranteContacto(request); }
    }

    private static class Respuesta implements InvocationHandler {
        private final java.io.StringWriter salida = new java.io.StringWriter();
        private final java.io.PrintWriter writer = new java.io.PrintWriter(salida);
        String contentType;
        final RenderResponse proxy = (RenderResponse)Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] { RenderResponse.class }, this);
        String texto() { writer.flush(); return salida.toString(); }
        public Object invoke(Object objeto, Method metodo, Object[] args) {
            if ("getWriter".equals(metodo.getName())) return writer;
            if ("setContentType".equals(metodo.getName())) { contentType = (String)args[0]; return null; }
            return vacio(metodo.getReturnType());
        }
    }

    private static class Modelo extends RequerimientoCompraOrdenHelper {
        RequerimientoCompra r;
        RequerimientoCompraOrden orden;
        int lecturas;
        int ultimoId;
        boolean falla;
        Modelo() throws Exception {
            r = requerimiento(40);
            orden = new RequerimientoCompraOrden(); orden.setIdOrdenCompra(900); orden.setIdRequerimientoCompra(40);
        }
        protected RequerimientoCompra obtenerRequerimiento(int id) throws Exception {
            lecturas++; ultimoId = id;
            if (falla) throw new java.sql.SQLException("Lectura simulada no disponible.");
            return r;
        }
        protected List<RequerimientoCompraDetalle> obtenerDetalles(int id) {
            return new ArrayList<RequerimientoCompraDetalle>();
        }
        protected RequerimientoCompraOrden obtenerOrden(int id) { ultimoId = id; return orden; }
    }

    private static class Sesion implements InvocationHandler {
        Map<String, Object> atributos = new HashMap<String, Object>();
        final PortletSession proxy = (PortletSession)Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] { PortletSession.class }, this);
        public Object invoke(Object objeto, Method metodo, Object[] args) {
            String nombre = metodo.getName();
            if ("getAttribute".equals(nombre)) return atributos.get(args[0]);
            if ("setAttribute".equals(nombre)) { atributos.put((String)args[0], args[1]); return null; }
            if ("removeAttribute".equals(nombre)) { atributos.remove(args[0]); return null; }
            if ("getAttributeNames".equals(nombre)) return Collections.enumeration(atributos.keySet());
            return vacio(metodo.getReturnType());
        }
    }

    private static class Peticion implements InvocationHandler {
        Map<String, String> parametros = new HashMap<String, String>();
        Map<String, Object> atributos = new HashMap<String, Object>();
        private final Sesion sesion;
        private final RenderRequest proxy = (RenderRequest)Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] { RenderRequest.class }, this);
        Peticion(Sesion sesion) {
            this.sesion = sesion;
            // PortletAction.getForward/setForward requieren la configuracion real del contenedor.
            com.liferay.portal.model.Portlet portlet = (com.liferay.portal.model.Portlet)Proxy.newProxyInstance(
                    getClass().getClassLoader(), new Class[] { com.liferay.portal.model.Portlet.class },
                    new InvocationHandler() {
                        public Object invoke(Object objeto, Method metodo, Object[] args) {
                            if ("getRootPortletId".equals(metodo.getName()) || "getPortletId".equals(metodo.getName())) {
                                return "compras_contacto_prueba";
                            }
                            return vacio(metodo.getReturnType());
                        }
                    });
            atributos.put("javax.portlet.config", new com.liferay.portlet.PortletConfigImpl(portlet, null));
        }
        RenderRequest request() { return proxy; }
        public Object invoke(Object objeto, Method metodo, Object[] args) {
            String nombre = metodo.getName();
            if ("getPortletSession".equals(nombre)) return sesion.proxy;
            if ("getParameter".equals(nombre)) return parametros.get(args[0]);
            if ("getParameterValues".equals(nombre)) {
                String valor = parametros.get(args[0]); return valor == null ? null : new String[] { valor };
            }
            if ("getParameterNames".equals(nombre)) return Collections.enumeration(parametros.keySet());
            if ("setAttribute".equals(nombre)) { atributos.put((String)args[0], args[1]); return null; }
            if ("getAttribute".equals(nombre)) return atributos.get(args[0]);
            if ("removeAttribute".equals(nombre)) { atributos.remove(args[0]); return null; }
            if ("getAttributeNames".equals(nombre)) return Collections.enumeration(atributos.keySet());
            return vacio(metodo.getReturnType());
        }
    }

    private static Object vacio(Class tipo) {
        if (tipo == Boolean.TYPE) return Boolean.FALSE;
        if (tipo == Integer.TYPE) return Integer.valueOf(0);
        if (tipo == Long.TYPE) return Long.valueOf(0);
        return null;
    }
}
