import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraComparativaAction;
import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraGuardadoToken;
import ar.com.ospim.compras.requerimientos.action.ValidacionCompraException;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

/** Adapta ComprasOrdenSeguimientoActionTest: Action, permisos y token reales. */
public class ComprasComparativaRecuperacionActionTest {
    private static int checks;
    public static void main(String[] args) throws Exception {
        ComprasFuenteDatosFixture datos = new ComprasFuenteDatosFixture();
        datos.cargarConfiguracionPrueba();
        datos.instalar();
        autorizacionYActor(); permisos(); tokens(); parametros(); errores(); soloLectura();
        System.out.println("COMPRAS_COMPARATIVA_RECUPERACION_ACTION_OK checks=" + checks);
        System.out.println("Action y token reales; permisos, confirmacion, contexto, actor y errores; sin DB ni UI.");
    }

    private static void autorizacionYActor() throws Exception {
        String[][] roles = { {"rol_abm_prueba"}, {"rol_cotizar_prueba"}, {"rol_abm_prueba", "rol_cotizar_prueba"} };
        for (int i = 0; i < roles.length; i++) {
            Accion accion = new Accion(roles[i]); Peticion p = peticion(); token(p); accion.ejecutar(p);
            igual("una invocacion autorizada " + i, Integer.valueOf(1), Integer.valueOf(accion.modelo.invocaciones));
            igual("un resultado autorizado " + i, Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
            igual("ID enviado " + i, Integer.valueOf(1405), Integer.valueOf(accion.modelo.requerimiento));
            igual("firma capturada " + i, "firma_fixture_1405", accion.modelo.firma);
            igual("actor autenticado " + i, "recuperador_fixture", accion.modelo.actor);
            igual("cantidad retirada " + i, Integer.valueOf(2), p.atributos.get("comparativaReparada"));
            comprobar("sin error de exito " + i, p.atributos.get("comparativaError") == null);
            navegacion(p, "1405", "exito " + i);
        }
    }

    private static void permisos() throws Exception {
        String[][] denegados = { {"rol_view_prueba"}, {"rol_autorizar_prueba"}, {"ABM_COMPRAS"}, {"rol_cotizar_prueba_otro"}, {} };
        for (int i = 0; i < denegados.length; i++) {
            Accion accion = new Accion(denegados[i]); Peticion p = peticion(); token(p); accion.ejecutar(p);
            sinGuardar(accion, p, "rol no autorizado " + i);
            navegacion(p, "1405", "permiso " + i);
            Accion permitida = new Accion(new String[] {"rol_abm_prueba"});
            Peticion reintento = copiarEnvio(p); permitida.ejecutar(reintento);
            igual("permiso rechazado no consume token " + i, Integer.valueOf(1), Integer.valueOf(permitida.modelo.guardados));
        }
        Accion anonima = new Accion(null); Peticion p = peticion(); token(p); anonima.ejecutar(p);
        sinGuardar(anonima, p, "anonimo"); navegacion(p, "1405", "anonimo");
    }

    private static void tokens() throws Exception {
        Accion accion = new Accion(new String[] {"rol_cotizar_prueba"});
        Peticion p = peticion(); accion.ejecutar(p); sinGuardar(accion, p, "token omitido");
        p = peticion(); p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO, "desconocido");
        accion.ejecutar(p); sinGuardar(accion, p, "token desconocido");
        Peticion original = peticion(); token(original); p = peticion();
        p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO,
                original.parametros.get(RequerimientoCompraGuardadoToken.PARAMETRO));
        accion.ejecutar(p); sinGuardar(accion, p, "token de otra sesion");
        p = peticion(); token(p); accion.ejecutar(p);
        igual("primer envio invoca", Integer.valueOf(1), Integer.valueOf(accion.modelo.invocaciones));
        Peticion repetida = copiarEnvio(p); accion.ejecutar(repetida);
        igual("doble envio no repite helper", Integer.valueOf(1), Integer.valueOf(accion.modelo.invocaciones));
        igual("doble envio no repite operacion", Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
        comprobar("doble envio informa rechazo", repetida.atributos.get("comparativaError") != null);
        comprobar("doble envio sin falso exito", repetida.atributos.get("comparativaReparada") == null);
    }

    private static void parametros() throws Exception {
        String[][] invalidos = {
            {"id_requerimiento_compra", null}, {"id_requerimiento_compra", "0"},
            {"id_requerimiento_compra", "-1"}, {"id_requerimiento_compra", "texto"},
            {"comparativa_firma_reparacion", null}, {"comparativa_firma_reparacion", ""},
            {"comparativa_firma_reparacion", "   "}, {"comparativa_confirmar_reparacion", null},
            {"comparativa_confirmar_reparacion", "NO"}, {"comparativa_confirmar_reparacion", "true"}
        };
        for (int i = 0; i < invalidos.length; i++) {
            Accion accion = new Accion(new String[] {"rol_abm_prueba"}); Peticion p = peticion();
            if (invalidos[i][1] == null) p.parametros.remove(invalidos[i][0]);
            else p.parametros.put(invalidos[i][0], invalidos[i][1]);
            token(p); accion.ejecutar(p); sinGuardar(accion, p, "parametro invalido " + i);
            comprobar("parametro conserva ruta " + i, "/compras/comparativa".equals(p.render.get("struts_action")));
            Peticion repetida = copiarEnvio(p);
            repetida.parametros.put("id_requerimiento_compra", "1405");
            repetida.parametros.put("comparativa_firma_reparacion", "firma_fixture_1405");
            repetida.parametros.put("comparativa_confirmar_reparacion", "SI");
            accion.ejecutar(repetida); sinGuardar(accion, repetida, "envio invalido consume token " + i);
        }
    }

    private static void errores() throws Exception {
        for (int modo = 1; modo <= 5; modo++) {
            Accion accion = new Accion(new String[] {"rol_cotizar_prueba"}); accion.modelo.error = modo;
            Peticion p = peticion(); token(p); accion.ejecutar(p);
            igual("error invoca una vez " + modo, Integer.valueOf(1), Integer.valueOf(accion.modelo.invocaciones));
            comprobar("error sin exito " + modo, p.atributos.get("comparativaReparada") == null);
            Object mensaje = p.atributos.get("comparativaError");
            comprobar("error informado " + modo, mensaje instanceof String && ((String) mensaje).length() > 0);
            comprobar("error no expone detalle interno " + modo,
                    ((String) mensaje).indexOf("DETALLE_INTERNO_SINTETICO") < 0);
            if (modo <= 2) comprobar("validacion conserva mensaje " + modo,
                    ((String) mensaje).indexOf("Rechazo sintetico") >= 0);
            navegacion(p, "1405", "error " + modo);
            accion.modelo.error = 0;
            Peticion repetida = copiarEnvio(p); accion.ejecutar(repetida);
            igual("reenvio de error no repite helper " + modo, Integer.valueOf(1), Integer.valueOf(accion.modelo.invocaciones));
            comprobar("reenvio de error sin exito " + modo, repetida.atributos.get("comparativaReparada") == null);
            Peticion nuevo = peticion(); token(nuevo); accion.ejecutar(nuevo);
            igual("token nuevo permite reintentar " + modo, Integer.valueOf(2), Integer.valueOf(accion.modelo.invocaciones));
            igual("reintento exitoso " + modo, Integer.valueOf(2), nuevo.atributos.get("comparativaReparada"));
        }
    }

    private static void soloLectura() throws Exception {
        String[] comandos = {null, "guardar", "otro"};
        for (int i = 0; i < comandos.length; i++) {
            Accion accion = new Accion(new String[] {"rol_view_prueba"}); Peticion p = peticion();
            if (comandos[i] == null) p.parametros.remove("cmd"); else p.parametros.put("cmd", comandos[i]);
            token(p); accion.ejecutar(p);
            igual("comando ordinario no llama reparacion " + i, Integer.valueOf(0), Integer.valueOf(accion.modelo.invocaciones));
            comprobar("comando ordinario conserva solo lectura " + i,
                    ((String) p.atributos.get("comparativaError")).indexOf("solo lectura") >= 0);
            comprobar("comando ordinario sin falso exito " + i, p.atributos.get("comparativaReparada") == null);
            navegacion(p, "1405", "solo lectura " + i);
            Accion autorizada = new Accion(new String[] {"rol_cotizar_prueba"});
            Peticion reparacion = copiarEnvio(p); reparacion.parametros.put("cmd", "REPARAR_COMPARATIVA");
            autorizada.ejecutar(reparacion);
            igual("solo lectura no consume token " + i, Integer.valueOf(1), Integer.valueOf(autorizada.modelo.invocaciones));
        }
    }

    private static void sinGuardar(Accion accion, Peticion p, String caso) {
        igual(caso + " no llama helper", Integer.valueOf(0), Integer.valueOf(accion.modelo.invocaciones));
        comprobar(caso + " informa rechazo", p.atributos.get("comparativaError") != null);
        comprobar(caso + " no informa exito", p.atributos.get("comparativaReparada") == null);
    }
    private static void navegacion(Peticion p, String id, String caso) {
        igual(caso + " ruta", "/compras/comparativa", p.render.get("struts_action"));
        igual(caso + " requerimiento", id, p.render.get("id_requerimiento_compra"));
        comprobar(caso + " no redirige a presupuesto", !p.render.containsKey("id_prestador"));
    }
    private static Peticion peticion() {
        Peticion p = new Peticion(); p.parametros.put("cmd", "REPARAR_COMPARATIVA");
        p.parametros.put("id_requerimiento_compra", "1405");
        p.parametros.put("comparativa_firma_reparacion", "firma_fixture_1405");
        p.parametros.put("comparativa_confirmar_reparacion", "SI");
        p.parametros.put("usuario", "actor_manipulado"); return p;
    }
    private static Peticion copiarEnvio(Peticion anterior) {
        Peticion p = new Peticion(); p.parametros.putAll(anterior.parametros); p.sesion.putAll(anterior.sesion); return p;
    }
    private static void token(Peticion p) {
        RequerimientoCompraGuardadoToken.publicar((RenderRequest) p.proxy());
        p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO, (String) p.atributos.get("COMPRAS_SAVE_TOKEN"));
    }
    private static class Modelo extends RequerimientoCompraComparativaHelper {
        int invocaciones; int guardados; int requerimiento; int error; String firma; String actor;
        public int reparar(int id, String enviada, String usuario) throws Exception {
            invocaciones++; requerimiento = id; firma = enviada; actor = usuario;
            if (error == 1) throw new IllegalArgumentException("Rechazo sintetico de contexto desactualizado.");
            if (error == 2) throw new ValidacionCompraException("comparativa", "Rechazo sintetico de comparativa.");
            if (error == 3) throw new java.sql.SQLException("DETALLE_INTERNO_SINTETICO_SQL");
            if (error == 4) throw new Exception("DETALLE_INTERNO_SINTETICO_RESULTADO_INCIERTO");
            if (error == 5) return 0;
            guardados++; return 2;
        }
    }
    private static class Accion extends RequerimientoCompraComparativaAction {
        final Modelo modelo = new Modelo(); final User usuario;
        Accion(String[] nombres) {
            if (nombres == null) { usuario = null; return; }
            final List<Role> roles = new ArrayList<Role>();
            for (String nombre : nombres) { Role rol = new RoleImpl(); rol.setName(nombre); roles.add(rol); }
            usuario = new UserImpl() {
                public List<Role> getRoles() { return roles; }
                public String getScreenName() { return "recuperador_fixture"; }
            };
        }
        protected RequerimientoCompraComparativaHelper obtenerHelper() { return modelo; }
        protected User obtenerUsuario(PortletRequest request) { return usuario; }
        void ejecutar(Peticion p) throws Exception {
            processAction(null, null, null, (ActionRequest) p.proxy(),
                    (ActionResponse) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {ActionResponse.class}, p));
        }
    }
    private static class Peticion implements InvocationHandler {
        final Map<String, String> parametros = new HashMap<String, String>();
        final Map<String, String> render = new HashMap<String, String>();
        final Map<String, Object> atributos = new HashMap<String, Object>();
        final Map<String, Object> sesion = new HashMap<String, Object>();
        final PortletSession session = (PortletSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {PortletSession.class}, this);
        Object proxy() { return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {ActionRequest.class, RenderRequest.class}, this); }
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
    private static void igual(String caso, Object esperado, Object actual) {
        comprobar(caso + ": esperado=" + esperado + ", actual=" + actual, esperado == null ? actual == null : esperado.equals(actual));
    }
    private static void comprobar(String caso, boolean condicion) { checks++; if (!condicion) throw new AssertionError(caso); }
}
