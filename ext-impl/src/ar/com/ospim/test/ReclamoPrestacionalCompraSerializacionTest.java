package ar.com.ospim.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.session.StandardSession;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper;

/** Serializa el DTO historico y restaura usando la clase actual y StandardSession real. */
public class ReclamoPrestacionalCompraSerializacionTest {
    private static int casos;
    private static final String USUARIO = "usuario-prueba";
    private static final String CONTEXTO = WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA;
    private static final String EDITOR = WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION;
    private static final String LISTA = WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION;
    private static final String CLASE = "ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto";

    public static void main(String[] args) throws Exception {
        if (args.length != 1) { throw new IllegalArgumentException("Indique directorio compilado del DTO historico 15a2ae3."); }
        URLClassLoader anterior = new URLClassLoader(new URL[] { new File(args[0]).toURI().toURL() }, null);
        Class<?> claseAnterior = anterior.loadClass(CLASE);
        Object contextoAnterior = claseAnterior.getConstructor(Integer.TYPE, String.class, Integer.class,
                String.class, Long.TYPE, String.class).newInstance(Integer.valueOf(31), "",
                Integer.valueOf(0), USUARIO, Long.valueOf(System.currentTimeMillis()), "anterior");
        check(claseAnterior != ReclamoPrestacionalCompraContexto.class, "DTO historico aislado");

        StandardSession session = sesion();
        session.setAttribute(CONTEXTO, contextoAnterior);
        session.setAttribute(EDITOR, new ReclamoPrestacional());
        session.setAttribute(LISTA, new ArrayList<Object>());
        session.setAttribute("edicion-ajena-prueba", "conservar");
        session = restaurar(session);
        Object contextoRestaurado = session.getAttribute(CONTEXTO);
        Object borradorRestaurado = session.getAttribute(EDITOR);
        Object listaRestaurada = session.getAttribute(LISTA);
        check(contextoRestaurado instanceof ReclamoPrestacionalCompraContexto, "restore usa DTO actual");
        check(!((ReclamoPrestacionalCompraContexto) contextoRestaurado).esBorradorEnEdicion(borradorRestaurado),
                "stream antiguo no contiene identidad");
        rechaza(session, "", "", "sin nonce/origen no degrada sesion anterior");
        check(session.getAttribute(CONTEXTO) == contextoRestaurado, "conserva metadata antigua");
        check(session.getAttribute(EDITOR) == borradorRestaurado && session.getAttribute(LISTA) == listaRestaurada,
                "conserva borrador y lista exactos");
        rechaza(session, "anterior", "compras", "nonce no reconstruye identidad desconocida");

        session = restaurar(session);
        rechaza(session, "", "", "segundo restore conserva restriccion antigua");

        ReclamoPrestacionalCompraContexto nuevo = nuevoContexto();
        ReclamoPrestacionalCompraPrecargaHelper.RegistroContextoBorrador registro =
                ReclamoPrestacionalCompraPrecargaHelper.registrarContextoBorrador(session, nuevo, "COMPRA_1", 1L);
        check(registro.isColision(), "registro conserva editor antiguo en recuperacion");
        check(registro.getRecuperacion().getContextoCompraVigente(USUARIO) == null,
                "recuperacion no inventa asociacion");
        check(registro.getRecuperacion().tieneContextoCompraNoVigente(USUARIO),
                "recuperacion antigua solo consulta sin alta ordinaria");
        check(ReclamoPrestacionalCompraPrecargaHelper.obtenerRecuperacionEdicion(session, "nuevo", USUARIO)
                == registro.getRecuperacion(), "recuperacion mantiene identidad esperada");
        check(ReclamoPrestacionalCompraPrecargaHelper.descartarEdicionActual(session, "nuevo", USUARIO, 31) == 31,
                "descarte explicito disponible");
        check(session.getAttribute(EDITOR) == null && session.getAttribute(CONTEXTO) == null,
                "descarte especifico consumido");
        check("conservar".equals(session.getAttribute("edicion-ajena-prueba")), "no vacia HttpSession");
        try {
            ReclamoPrestacionalCompraPrecargaHelper.descartarEdicionActual(session, "nuevo", USUARIO, 31);
            throw new AssertionError("reenvio del descarte aceptado");
        } catch (AssertionError e) { throw e; } catch (Exception expected) { casos++; }

        // Metadata anterior sin un borrador o junto a RP persistido no bloquea ordinario.
        session = sesion(); session.setAttribute(CONTEXTO, contextoAnterior);
        session = restaurar(session);
        check(validar(session, "", "") == null, "antiguo sin editor permite ordinario");
        session = sesion(); session.setAttribute(CONTEXTO, contextoAnterior);
        ReclamoPrestacional persistido = new ReclamoPrestacional(); persistido.setId(71);
        session.setAttribute(EDITOR, persistido); session = restaurar(session);
        Object actualPersistido = session.getAttribute(EDITOR);
        check(ReclamoPrestacionalCompraPrecargaHelper.validarContextoEditor(session, "", "", USUARIO, 71) == null,
                "antiguo con persistido permite ordinario");
        check(session.getAttribute(EDITOR) == actualPersistido, "conserva edicion persistida");

        session = sesion(); session.setAttribute(CONTEXTO, nuevoContexto());
        session.setAttribute(EDITOR, new ReclamoPrestacional()); session = restaurar(session);
        Object ordinario = session.getAttribute(EDITOR);
        check(validar(session, "", "") == null && session.getAttribute(EDITOR) == ordinario,
                "metadata nueva no asociada mantiene alta ordinaria");
        session = sesion(); nuevo = nuevoContexto();
        ReclamoPrestacional actual = new ReclamoPrestacional(); nuevo.setReclamoEnEdicion(actual);
        session.setAttribute(CONTEXTO, nuevo); session.setAttribute(EDITOR, actual); session = restaurar(session);
        check(validar(session, "nuevo", "compras") == session.getAttribute(CONTEXTO),
                "sesion nueva conserva identidad despues restore Tomcat");
        rechaza(session, "", "", "sesion nueva Compras sigue exigiendo nonce");
        System.out.println("RP_SERIALIZACION_TOMCAT_REAL_OK casos=" + casos);
        anterior.close();
    }

    private static StandardSession sesion() throws Exception {
        StandardContext contexto = new StandardContext(); contexto.setName("/rp-prueba");
        StandardManager manager = new StandardManager();
        // El test usa Tomcat instalado; el build legacy tambien contiene catalina anterior.
        manager.getClass().getMethod("setContext", new Class[] { org.apache.catalina.Context.class })
                .invoke(manager, new Object[] { contexto });
        StandardSession session = new StandardSession(manager);
        session.setValid(true); session.setCreationTime(System.currentTimeMillis());
        return session;
    }
    private static StandardSession restaurar(StandardSession session) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream salida = new ObjectOutputStream(bytes);
        session.writeObjectData(salida); salida.close();
        StandardSession restaurada = sesion();
        ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        restaurada.readObjectData(entrada); entrada.close();
        return restaurada;
    }
    private static ReclamoPrestacionalCompraContexto nuevoContexto() {
        return new ReclamoPrestacionalCompraContexto(31, "", Integer.valueOf(0), USUARIO,
                System.currentTimeMillis(), "nuevo");
    }
    private static ReclamoPrestacionalCompraContexto validar(StandardSession session, String nonce, String origen) throws Exception {
        return ReclamoPrestacionalCompraPrecargaHelper.validarContextoEditor(session, nonce, origen, USUARIO, 0);
    }
    private static void rechaza(StandardSession session, String nonce, String origen, String caso) throws Exception {
        try { validar(session, nonce, origen); } catch (Exception expected) { casos++; return; }
        throw new AssertionError(caso);
    }
    private static void check(boolean ok, String caso) {
        if (!ok) { throw new AssertionError(caso); } casos++;
    }
}