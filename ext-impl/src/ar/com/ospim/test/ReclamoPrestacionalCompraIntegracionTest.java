package ar.com.ospim.test;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.action.ReclamosBaseAction;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.portlet.PortletRequest;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

/** Ejecuta las guardias y recuperacion reales sin BD, portal ni servicios externos. */
public class ReclamoPrestacionalCompraIntegracionTest {
    private static int casos;
    private static final String USUARIO = "usuario-prueba";

    public static void main(String[] args) throws Exception {
        HttpSession session = sesion();
        check(validar(session, "", "", 0) == null, "RP ordinario limpio");
        ReclamoPrestacionalCompraContexto contexto = contexto(31, "token-a", System.currentTimeMillis());
        session.setAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, contexto);
        check(validar(session, "", "", 0) == null, "metadata sin borrador no bloquea RP ordinario");
        check(session.getAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA) == null, "retira solo metadata residual");
        rechaza(session, "", "compras", USUARIO, 0, "handoff sin nonce");
        rechaza(session, "token-a", "compras", USUARIO, 0, "handoff sin contexto");

        session.setAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, contexto);
        rechaza(session, "token-a", "compras", USUARIO, 0, "contexto sin precarga no abre alta generica");
        session.removeAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA);
        ReclamoPrestacional borrador = new ReclamoPrestacional();
        check(borrador.getId_reclamo() == 0, "alta ordinaria comienza en ID cero");
        session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, borrador);
        session.setAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, contexto);
        check(validar(session, "", "", 0) == null, "contexto ajeno no bloquea alta ordinaria ID cero");
        check(session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION) == borrador, "conserva alta ordinaria");
        contexto.setReclamoEnEdicion(borrador);
        session.setAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION, borrador);
        session.setAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, contexto);
        check(validar(session, "token-a", "compras", 0) == contexto, "contexto valido");
        rechaza(session, "", "", USUARIO, 0, "otra pestana ordinaria no modifica borrador Compras");
        rechaza(session, "token-b", "compras", USUARIO, 0, "otra pestana Compras no modifica borrador");
        rechaza(session, "token-a", "compras", "otro-usuario", 0, "otro propietario");
        rechaza(session, "token-a", "compras", USUARIO, 71, "ID ajeno no se vincula");
        check(session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION) == borrador, "errores preservan instancia");

        ReclamoPrestacionalCompraContexto vencido = contexto(31, "vencido", 1L);
        vencido.setReclamoEnEdicion(borrador);
        session.setAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, vencido);
        rechaza(session, "", "", USUARIO, 0, "borrador propio vencido no se degrada en manual");
        rechaza(session, "vencido", "compras", USUARIO, 0, "contexto vencido");
        session.setAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, contexto(31, "futuro", System.currentTimeMillis() + 60000L));
        rechaza(session, "futuro", "compras", USUARIO, 0, "contexto futuro");
        session.setAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA, contexto);

        ReclamoPrestacionalCompraPrecargaHelper.RegistroContextoBorrador mismo =
                ReclamoPrestacionalCompraPrecargaHelper.registrarContextoBorrador(session, contexto(31, "nuevo", System.currentTimeMillis()), "COMPRA_1", 1L);
        check(mismo.isReutilizado() && "token-a".equals(mismo.getNonceContextoActual()), "doble inicio reutiliza borrador");
        ReclamoPrestacionalCompraPrecargaHelper.RegistroContextoBorrador otro =
                ReclamoPrestacionalCompraPrecargaHelper.registrarContextoBorrador(session, contexto(32, "token-b", System.currentTimeMillis()), "COMPRA_1", 1L);
        check(otro.isColision(), "otro requerimiento solicita recuperacion");
        check(session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION) == borrador, "colision conserva borrador exacto");
        check(ReclamoPrestacionalCompraPrecargaHelper.obtenerRecuperacionEdicion(session, "token-b", USUARIO) == otro.getRecuperacion(), "recuperacion valida");

        borrador.setId(71);
        session.removeAttribute(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA);
        check(validar(session, "", "", 71) == null, "RP ordinario actual puede editarse");
        rechaza(session, "", "", USUARIO, 72, "AJAX de otra pestana persistida se rechaza");
        rechaza(session, "", "", USUARIO, 0, "alta antigua no modifica RP vinculado");
        rechaza(session, "", "", USUARIO, -1, "ID negativo explicito no omite guarda");
        check(session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION) == borrador,
                "callback tardio mantiene RP actual");
        check(ReclamoPrestacionalCompraPrecargaHelper.validarContextoEditor(session, "", "", USUARIO, 72, true) == null,
                "apertura explicita permite seleccionar otro RP");
        ReclamoPrestacionalCompraPrecargaHelper.RegistroContextoBorrador persistido =
                ReclamoPrestacionalCompraPrecargaHelper.registrarContextoBorrador(session, contexto(33, "token-c", System.currentTimeMillis()), "COMPRA_1", 1L);
        check(persistido.isColision(), "RP persistido no se limpia implicitamente");
        check(persistido.getRecuperacion().getIdReclamoActual() == 71, "recupera RP persistido correcto");
        check(session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION) == borrador, "preserva cambios del RP persistido");
        rechaza(session, "token-a", "compras", USUARIO, 0, "formulario antiguo no crea segundo RP");
        int req = ReclamoPrestacionalCompraPrecargaHelper.descartarEdicionActual(session, "token-c", USUARIO, 33);
        check(req == 33 && session.getAttribute(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION) == null, "descarte explicito del editor validado");
        try {
            ReclamoPrestacionalCompraPrecargaHelper.descartarEdicionActual(session, "token-c", USUARIO, 33);
            throw new AssertionError("reenvio del descarte fue aceptado");
        } catch (AssertionError e) { throw e; } catch (Exception expected) { casos++; }
        // La identidad se conserva al serializar juntos los atributos de sesion.
        borrador.setId(0);
        contexto.setReclamoEnEdicion(borrador);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream salida = new ObjectOutputStream(bytes);
        salida.writeObject(new Object[] { borrador, contexto }); salida.close();
        ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        Object[] recuperados = (Object[]) entrada.readObject(); entrada.close();
        check(((ReclamoPrestacionalCompraContexto) recuperados[1]).esBorradorEnEdicion(recuperados[0]), "identidad Serializable");

        RequerimientoCompra requerimiento = new RequerimientoCompra();
        List<PrestacionesReclamo> prestaciones = new ArrayList<PrestacionesReclamo>();
        PrestacionesReclamo prestacion = new PrestacionesReclamo("", "", 0D, 0D, 0D, 0, 0, 1, "", "", Boolean.FALSE, 1);
        prestaciones.add(prestacion);
        prestacion.setRecuperable(Integer.valueOf(2));
        ReclamoPrestacionalCompraPrecargaHelper.validarRecuperablesParaGuardar(requerimiento, prestaciones); casos++;
        requerimiento.setSurge(true); prestacion.setRecuperable(Integer.valueOf(1));
        ReclamoPrestacionalCompraPrecargaHelper.validarRecuperablesParaGuardar(requerimiento, prestaciones); casos++;
        prestacion.setRecuperable(Integer.valueOf(3));
        try {
            ReclamoPrestacionalCompraPrecargaHelper.validarRecuperablesParaGuardar(requerimiento, prestaciones);
            throw new AssertionError("Compras acepto Recuperable Integracion");
        } catch (AssertionError e) { throw e; } catch (Exception expected) { casos++; }
        prestacion.setEstado(PrestacionesReclamo.ESTADOS.BAJA);
        ReclamoPrestacionalCompraPrecargaHelper.validarRecuperablesParaGuardar(requerimiento, prestaciones); casos++;

        numero("12.50", 12.5D); numero("12,50", 12.5D); numero("0", 0D); numero("", 0D);
        for (String invalido : new String[] { "12x", "NaN", "Infinity", "1.234,56" }) {
            try {
                ReclamosBaseAction.getImportePrestacionFromRequest(peticion(invalido), "importe");
                throw new AssertionError("Se acepto numero invalido " + invalido);
            } catch (AssertionError e) { throw e; } catch (Exception expected) { casos++; }
        }
        System.out.println("RP_INTEGRACION_REAL_OK casos=" + casos);
    }

    private static ReclamoPrestacionalCompraContexto contexto(int id, String nonce, long fecha) {
        return new ReclamoPrestacionalCompraContexto(id, "", Integer.valueOf(0), USUARIO, fecha, nonce);
    }
    private static ReclamoPrestacionalCompraContexto validar(HttpSession s, String nonce, String origen, int id) throws Exception {
        return ReclamoPrestacionalCompraPrecargaHelper.validarContextoEditor(s, nonce, origen, USUARIO, id);
    }
    private static void rechaza(HttpSession s, String nonce, String origen, String usuario, int id, String caso) throws Exception {
        try {
            ReclamoPrestacionalCompraPrecargaHelper.validarContextoEditor(s, nonce, origen, usuario, id);
        } catch (Exception expected) { casos++; return; }
        throw new AssertionError(caso);
    }
    private static void check(boolean ok, String caso) {
        if (!ok) { throw new AssertionError(caso); }
        casos++;
    }
    private static void numero(String texto, double esperado) throws Exception {
        check(ReclamosBaseAction.getImportePrestacionFromRequest(peticion(texto), "importe") == esperado,
                "parser servidor " + texto);
    }
    private static PortletRequest peticion(final String valor) {
        return (PortletRequest) Proxy.newProxyInstance(PortletRequest.class.getClassLoader(),
                new Class[] { PortletRequest.class }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) {
                if ("getParameter".equals(method.getName())) { return valor; }
                return null;
            }
        });
    }
    private static HttpSession sesion() {
        final Map<String, Object> atributos = new HashMap<String, Object>();
        return (HttpSession) Proxy.newProxyInstance(HttpSession.class.getClassLoader(), new Class[] { HttpSession.class }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) {
                String nombre = method.getName();
                if ("getAttribute".equals(nombre)) { return atributos.get(args[0]); }
                if ("setAttribute".equals(nombre)) { atributos.put((String) args[0], args[1]); return null; }
                if ("removeAttribute".equals(nombre)) { atributos.remove(args[0]); return null; }
                if ("toString".equals(nombre)) { return "SesionPruebaRP"; }
                return null;
            }
        });
    }
}