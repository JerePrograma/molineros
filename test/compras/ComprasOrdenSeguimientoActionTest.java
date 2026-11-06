import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraGuardadoToken;
import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraOrdenAction;
import ar.com.ospim.compras.requerimientos.action.ValidacionCompraException;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSeguimiento;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenSeguimientoHelper;
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

/** Action/token productivos; adapta el fixture de ComprasOrdenSuministroTest.action. */
public class ComprasOrdenSeguimientoActionTest {
    private static int checks;
    public static void main(String[] args) throws Exception {
        ComprasFuenteDatosFixture configuracion = new ComprasFuenteDatosFixture();
        configuracion.cargarConfiguracionPrueba();
        configuracion.instalar();
        guardadoYCaptura(); permisos(); tokens(); erroresYReenvio(); parametrosIncompletos();
        System.out.println("COMPRAS_ORDEN_SEGUIMIENTO_ACTION_OK checks=" + checks);
        System.out.println("Action y token reales: permiso Gestion, captura, doble envio y recuperacion de errores; sin DB ni UI.");
    }

    private static void guardadoYCaptura() throws Exception {
        String[][] autorizados = { {"GESTION_COMPRAS"}, {"ABM_COMPRAS", "GESTION_COMPRAS"} };
        for (int i = 0; i < autorizados.length; i++) {
            Accion accion = new Accion(autorizados[i]);
            Peticion p = peticion(); token(p); accion.ejecutar(p);
            igual("un guardado autorizado " + i, Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
            igual("ID requerimiento " + i, Integer.valueOf(40), Integer.valueOf(accion.modelo.requerimiento));
            igual("ID OC " + i, Integer.valueOf(900), Integer.valueOf(accion.modelo.orden));
            igual("version capturada " + i, Integer.valueOf(3), Integer.valueOf(accion.modelo.version));
            igual("actor autenticado " + i, "gestor_fixture", accion.modelo.actor);
            String[] campos = {"seg_calificacion", "seg_observaciones", "seg_factura_ingresada",
                    "seg_numero_factura", "seg_fecha_factura", "seg_estado_liquidacion",
                    "seg_liquidacion_auditada", "seg_estado_pago", "seg_id_liquidacion", "seg_id_orden_pago",
                    "version_seguimiento"};
            for (String campo : campos) igual("captura literal " + campo + " " + i,
                    p.parametros.get(campo), accion.modelo.entrada.get(campo));
            igual("exito " + i, Boolean.TRUE, p.atributos.get("ordenSeguimientoGuardado"));
            igual("resultado publicado " + i, accion.modelo.resultado, p.atributos.get("ordenSeguimiento"));
            comprobar("entrada retirada tras exito " + i, !p.atributos.containsKey("ordenSeguimientoEntrada"));
            comprobar("sin error de exito " + i, !p.atributos.containsKey("ordenSeguimientoError"));
            comprobar("seguimiento no marca suministro " + i, !p.atributos.containsKey("ordenSuministroGuardado"));
            navegacion(p, "navegacion exito " + i);
        }
        Accion accion = new Accion(new String[] {"GESTION_COMPRAS"});
        Peticion p = peticion(); p.parametros.remove("seg_liquidacion_auditada"); token(p); accion.ejecutar(p);
        igual("checkbox ausente false", "false", accion.modelo.entrada.get("seg_liquidacion_auditada"));
        p = peticion(); p.parametros.put("seg_liquidacion_auditada", "MANIPULADA"); token(p);
        accion = new Accion(new String[] {"GESTION_COMPRAS"}); accion.ejecutar(p);
        igual("Action conserva boolean para validacion del helper", "MANIPULADA", accion.modelo.entrada.get("seg_liquidacion_auditada"));
    }

    private static void permisos() throws Exception {
        String[][] denegados = { {"ABM_COMPRAS"}, {"VIEW_COMPRAS"}, {"GESTION_COMPRAS_OTRO"}, {} };
        for (int i = 0; i < denegados.length; i++) {
            Accion accion = new Accion(denegados[i]); Peticion p = peticion(); token(p); accion.ejecutar(p);
            igual("rol no llama helper " + i, Integer.valueOf(0), Integer.valueOf(accion.modelo.invocaciones));
            comprobar("rol muestra rechazo " + i, p.atributos.get("ordenSeguimientoError") != null);
            comprobar("rol sin falso exito " + i, !Boolean.TRUE.equals(p.atributos.get("ordenSeguimientoGuardado")));
            navegacion(p, "navegacion permiso " + i);
            Accion permitida = new Accion(new String[] {"GESTION_COMPRAS"});
            Peticion reintento = copiarEnvio(p); permitida.ejecutar(reintento);
            igual("permiso rechazado no consume token " + i, Integer.valueOf(1), Integer.valueOf(permitida.modelo.guardados));
        }
        Accion anonima = new Accion(null); Peticion p = peticion(); token(p); anonima.ejecutar(p);
        igual("anonimo no llama helper", Integer.valueOf(0), Integer.valueOf(anonima.modelo.invocaciones));
        comprobar("anonimo muestra rechazo", p.atributos.get("ordenSeguimientoError") != null);
    }

    private static void tokens() throws Exception {
        Accion accion = new Accion(new String[] {"GESTION_COMPRAS"});
        Peticion p = peticion(); accion.ejecutar(p);
        sinGuardar(accion, p, "token omitido");
        p = peticion(); p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO, "desconocido"); accion.ejecutar(p);
        sinGuardar(accion, p, "token desconocido");
        Peticion otraSesion = peticion(); token(otraSesion); p = peticion();
        p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO,
                otraSesion.parametros.get(RequerimientoCompraGuardadoToken.PARAMETRO)); accion.ejecutar(p);
        sinGuardar(accion, p, "token de otra sesion");
        p = peticion(); token(p); accion.ejecutar(p);
        igual("primer envio guarda", Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
        Peticion repetida = copiarEnvio(p); accion.ejecutar(repetida);
        igual("doble envio no repite helper", Integer.valueOf(1), Integer.valueOf(accion.modelo.invocaciones));
        igual("doble envio conserva un guardado", Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
        comprobar("doble envio sin falso exito", !Boolean.TRUE.equals(repetida.atributos.get("ordenSeguimientoGuardado")));
        comprobar("doble envio informa rechazo", repetida.atributos.get("ordenSeguimientoError") != null);
    }

    private static void erroresYReenvio() throws Exception {
        for (int modo = 1; modo <= 3; modo++) {
            Accion accion = new Accion(new String[] {"GESTION_COMPRAS"}); accion.modelo.error = modo;
            Peticion p = peticion(); token(p); accion.ejecutar(p);
            igual("error sin guardado " + modo, Integer.valueOf(0), Integer.valueOf(accion.modelo.guardados));
            comprobar("error conserva entrada " + modo, p.atributos.get("ordenSeguimientoEntrada") instanceof Map);
            Map entrada = (Map) p.atributos.get("ordenSeguimientoEntrada");
            igual("error conserva observaciones " + modo, "Observacion enviada <texto>", entrada.get("seg_observaciones"));
            igual("error conserva factura " + modo, "0001-00000042", entrada.get("seg_numero_factura"));
            igual("error conserva fecha " + modo, "06/11/2026", entrada.get("seg_fecha_factura"));
            igual("error conserva version " + modo, "3", entrada.get("version_seguimiento"));
            comprobar("error no informa exito " + modo, !Boolean.TRUE.equals(p.atributos.get("ordenSeguimientoGuardado")));
            Object mensaje = p.atributos.get("ordenSeguimientoError");
            comprobar("error tiene mensaje " + modo, mensaje instanceof String && ((String) mensaje).length() > 0);
            if (modo == 3) comprobar("error infraestructura no expone causa", ((String) mensaje).indexOf("DETALLE_INTERNO_SINTETICO") < 0);
            navegacion(p, "navegacion error " + modo);
            accion.modelo.error = 0;
            Peticion repetida = copiarEnvio(p); accion.ejecutar(repetida);
            igual("token de intento fallido no se reutiliza " + modo, Integer.valueOf(1), Integer.valueOf(accion.modelo.invocaciones));
            igual("reenvio fallido sin guardado " + modo, Integer.valueOf(0), Integer.valueOf(accion.modelo.guardados));
            Peticion nuevoIntento = peticion(); token(nuevoIntento); accion.ejecutar(nuevoIntento);
            igual("token nuevo permite corregir " + modo, Integer.valueOf(1), Integer.valueOf(accion.modelo.guardados));
        }
    }

    private static void parametrosIncompletos() throws Exception {
        String[] invalidos = {null, "no_entero"};
        for (int i = 0; i < invalidos.length; i++) {
            Accion accion = new Accion(new String[] {"GESTION_COMPRAS"}); Peticion p = peticion();
            if (invalidos[i] == null) p.parametros.remove("version_seguimiento");
            else p.parametros.put("version_seguimiento", invalidos[i]);
            token(p); accion.ejecutar(p);
            igual("version invalida usa centinela " + i, Integer.valueOf(-1), Integer.valueOf(accion.modelo.version));
            igual("version invalida sin guardado " + i, Integer.valueOf(0), Integer.valueOf(accion.modelo.guardados));
            comprobar("version invalida informa error " + i, p.atributos.get("ordenSeguimientoError") != null);
        }
        Accion accion = new Accion(new String[] {"GESTION_COMPRAS"}); Peticion p = peticion();
        p.parametros.remove("id_orden_compra"); token(p); accion.ejecutar(p);
        igual("OC ausente no inventa ID", Integer.valueOf(0), Integer.valueOf(accion.modelo.orden));
        igual("OC ausente sin guardado", Integer.valueOf(0), Integer.valueOf(accion.modelo.guardados));
    }

    private static void sinGuardar(Accion accion, Peticion p, String caso) {
        igual(caso + " no llama helper", Integer.valueOf(0), Integer.valueOf(accion.modelo.invocaciones));
        comprobar(caso + " informa rechazo", p.atributos.get("ordenSeguimientoError") != null);
        comprobar(caso + " no publica exito", !Boolean.TRUE.equals(p.atributos.get("ordenSeguimientoGuardado")));
    }
    private static void navegacion(Peticion p, String caso) {
        igual(caso + " ruta", "/compras/orden_compra", p.render.get("struts_action"));
        igual(caso + " requerimiento", "40", p.render.get("id_requerimiento_compra"));
        igual(caso + " origen", "1", p.render.get("origen_ordenes"));
        comprobar(caso + " vista OC", !p.render.containsKey("gestion_suministro"));
    }
    private static Peticion peticion() {
        Peticion p = new Peticion();
        p.parametros.put("cmd", "guardar_seguimiento"); p.parametros.put("id_requerimiento_compra", "40");
        p.parametros.put("id_orden_compra", "900"); p.parametros.put("version_seguimiento", "3");
        p.parametros.put("seg_calificacion", "APTO"); p.parametros.put("seg_observaciones", "Observacion enviada <texto>");
        p.parametros.put("seg_factura_ingresada", "SI"); p.parametros.put("seg_numero_factura", "0001-00000042");
        p.parametros.put("seg_fecha_factura", "06/11/2026"); p.parametros.put("seg_estado_liquidacion", "LIQUIDADA");
        p.parametros.put("seg_liquidacion_auditada", "true"); p.parametros.put("seg_estado_pago", "PAGADO");
        p.parametros.put("seg_id_liquidacion", "123"); p.parametros.put("seg_id_orden_pago", "456");
        p.parametros.put("usuario", "actor_manipulado");
        return p;
    }
    private static Peticion copiarEnvio(Peticion anterior) {
        Peticion nueva = new Peticion(); nueva.parametros.putAll(anterior.parametros); nueva.sesion.putAll(anterior.sesion); return nueva;
    }
    private static void token(Peticion p) {
        RequerimientoCompraGuardadoToken.publicar((RenderRequest) p.proxy());
        p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO, (String) p.atributos.get("COMPRAS_SAVE_TOKEN"));
    }
    private static class Modelo extends RequerimientoCompraOrdenSeguimientoHelper {
        int invocaciones; int guardados; int error; int requerimiento; int orden; int version;
        Map<String, String> entrada; String actor; RequerimientoCompraOrdenSeguimiento resultado;
        public RequerimientoCompraOrdenSeguimiento guardar(int req, int oc, int ver,
                Map<String, String> enviada, String usuario) throws Exception {
            invocaciones++; requerimiento = req; orden = oc; version = ver; entrada = enviada; actor = usuario;
            if (error == 1 || req <= 0 || oc <= 0 || ver < 0) throw new IllegalArgumentException("Validacion sintetica de seguimiento.");
            if (error == 2) throw new ValidacionCompraException("guardar", "Rechazo sintetico de seguimiento.");
            if (error == 3) throw new Exception("DETALLE_INTERNO_SINTETICO");
            guardados++; resultado = new RequerimientoCompraOrdenSeguimiento();
            resultado.setIdOrdenCompra(oc); resultado.setVersionEdicion(ver + 1); return resultado;
        }
    }
    private static class Accion extends RequerimientoCompraOrdenAction {
        final Modelo modelo = new Modelo(); final User usuario;
        Accion(String[] nombres) {
            if (nombres == null) { usuario = null; return; }
            final List<Role> roles = new ArrayList<Role>();
            for (String nombre : nombres) { Role rol = new RoleImpl(); rol.setName(nombre); roles.add(rol); }
            usuario = new UserImpl() {
                public List<Role> getRoles() { return roles; }
                public String getScreenName() { return "gestor_fixture"; }
                public String getFullName() { return "Gestor de prueba"; }
            };
        }
        protected RequerimientoCompraOrdenSeguimientoHelper obtenerSeguimientoHelper() { return modelo; }
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
