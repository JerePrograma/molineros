import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

/* Navegacion segun Liquidaciones/ReintegroActionUtil.getReintegroEntry:
 * una apertura reemplaza el estado temporal, sin persistir el abandonado.
 * Adaptacion del harness aislado ComprasDocumentosPermisoAltaTest.
 * Solo metodos productivos del helper y una HttpSession simulada; sin DB ni red. */
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
        for (int caso = 0; caso < 10; caso++) {
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
