import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.compras.action.ViewComprasAction;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraEstado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper;

/* Referencias: ComprasRolesPermisosTest (reglas reales y fixture ServiceImpl),
 * ComprasContactoAfiliadoTokenTest (proxy Portal y RenderRequest).
 * Ejecuta la preparacion inicial aislada, no render completo, BD, red ni UI. */
public class ComprasPrimeraCargaVistaTest {
    private static int checks;
    private static Datos datos;
    private static Peticion actual;
    private static Method cargar;
    private static final class Datos extends ComprasFuenteDatosFixture {
        int consultas;
        boolean fallar;
        RequerimientoCompraFiltro ultimo;
        public List<RequerimientoCompra> buscarRequerimientosPaginados(
                RequerimientoCompraFiltro filtro, boolean incluirRp, int pagina) throws Exception {
            consultas++; ultimo = filtro;
            comprobar(!incluirRp && pagina == 1, "primera pagina sin mezcla RP");
            if (fallar) throw new Exception("Fallo simulado de lectura del listado");
            return new ArrayList<RequerimientoCompra>();
        }
    }
    private static class Atributos implements InvocationHandler {
        final Map<String,Object> valores = new HashMap<String,Object>();
        public Object invoke(Object objeto, Method metodo, Object[] args) {
            String nombre = metodo.getName();
            if ("getAttribute".equals(nombre)) return valores.get(args[0]);
            if ("setAttribute".equals(nombre)) { valores.put((String)args[0],args[1]); return null; }
            if ("removeAttribute".equals(nombre)) { valores.remove(args[0]); return null; }
            return vacio(metodo.getReturnType());
        }
    }
    private static class Peticion extends Atributos {
        final Map<String,String> parametros = new HashMap<String,String>();
        final Atributos sesionDatos = new Atributos();
        final HttpSession sesion = (HttpSession)proxy(HttpSession.class,sesionDatos);
        final User usuario;
        final RenderRequest request = (RenderRequest)proxy(RenderRequest.class,this);
        final HttpServletRequest http = (HttpServletRequest)proxy(HttpServletRequest.class,
                new InvocationHandler() {
                    public Object invoke(Object objeto, Method metodo, Object[] args) {
                        if ("getSession".equals(metodo.getName())) return sesion;
                        return vacio(metodo.getReturnType());
                    }
                });
        Peticion(String tab, String rol) {
            final List<Role> roles = new ArrayList<Role>();
            if (rol != null) { Role role = new RoleImpl(); role.setName(rol); roles.add(role); }
            usuario = new UserImpl() { public List<Role> getRoles() { return roles; } };
            usuario.setUserId(17L);
            valores.put("tabs1",tab);
            valores.put(WebKeysCompras.ESTADOS_REQUERIMIENTO,Arrays.asList(new RequerimientoCompraEstado[] {
                new RequerimientoCompraEstado(Integer.valueOf(41)),
                new RequerimientoCompraEstado(Integer.valueOf(43)),
                new RequerimientoCompraEstado(Integer.valueOf(46)) }));
            valores.put(WebKeysCompras.SECTORES_REQUERIMIENTO,Arrays.asList(new RequerimientoCompraSector[] {
                new RequerimientoCompraSector(Integer.valueOf(8)) }));
        }
        public Object invoke(Object objeto, Method metodo, Object[] args) {
            if ("getParameter".equals(metodo.getName())) return parametros.get(args[0]);
            if ("getLocale".equals(metodo.getName())) return Locale.ENGLISH;
            return super.invoke(objeto,metodo,args);
        }
    }
    private static Object proxy(Class tipo, InvocationHandler h) {
        return Proxy.newProxyInstance(ComprasPrimeraCargaVistaTest.class.getClassLoader(),new Class[] {tipo},h);
    }
    private static Object vacio(Class tipo) {
        if (tipo == Boolean.TYPE) return Boolean.FALSE;
        if (tipo == Integer.TYPE) return Integer.valueOf(0);
        if (tipo == Long.TYPE) return Long.valueOf(0);
        return null;
    }
    private static void comprobar(boolean ok, String mensaje) {
        checks++; if (!ok) throw new AssertionError(mensaje);
    }
    private static void cargar(Peticion p) throws Exception {
        actual=p; cargar.invoke(new ViewComprasAction(),new Object[] {p.request});
    }
    private static void resultado(Peticion p, int estado) {
        Object token=p.valores.get(ExportarRequerimientosCompraHelper.TOKEN);
        comprobar(token instanceof String && ((String)token).length()>0,"token inicial publicado");
        RequerimientoCompraFiltro f=ExportarRequerimientosCompraHelper.obtener(p.sesion,17L,(String)token).getFiltro();
        comprobar(Integer.valueOf(estado).equals(f.getIdEstado()),"token conserva estado inicial");
        comprobar(p.valores.get(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA) instanceof List,"resultado inicial publicado");
        comprobar(Integer.valueOf(1).equals(p.valores.get(WebKeysCompras.PAGINA_REQUERIMIENTOS_COMPRA)),"pagina inicial publicada");
    }
    public static void main(String[] args) throws Exception {
        datos=new Datos(); datos.cargarConfiguracionPrueba(); datos.instalar();
        cargar=ViewComprasAction.class.getDeclaredMethod("cargarRequerimientos",new Class[] {RenderRequest.class});
        cargar.setAccessible(true);
        Portal anterior=PortalUtil.getPortal();
        new PortalUtil().setPortal((Portal)proxy(Portal.class,new InvocationHandler() {
            public Object invoke(Object objeto,Method metodo,Object[] args) {
                if ("getUser".equals(metodo.getName())) return actual.usuario;
                if ("getHttpServletRequest".equals(metodo.getName())) return actual.http;
                if ("getPortletId".equals(metodo.getName())) return "compras";
                if ("getPortletNamespace".equals(metodo.getName())) return "_compras_";
                throw new UnsupportedOperationException("Portal inesperado: "+metodo.getName());
            }
        }));
        try {
            Peticion p=new Peticion("requerimientos","rol_view_prueba"); cargar(p);
            comprobar(datos.consultas==1,"una consulta inicial"); resultado(p,41);
            comprobar(datos.ultimo.getFechaAltaDesde()==null && datos.ultimo.getFechaAltaHasta()==null,"fechas iniciales vacias");
            p=new Peticion("para-autorizar","rol_autorizar_prueba"); p.parametros.put("estado","43"); cargar(p); resultado(p,46);
            p=new Peticion("requerimientos","rol_view_prueba");
            p.parametros.put("estado","43"); p.parametros.put("sector_id","8");
            p.parametros.put("id_requerimiento_compra","123"); p.parametros.put("surge","true");
            p.parametros.put("id_tercerizadora","tercero");
            p.valores.put("compras.requerimientos.tercerizadoras",Arrays.asList(new TercerizadoraServicio[] {
                new TercerizadoraServicio("TERCERO") }));
            cargar(p); resultado(p,43);
            comprobar(Integer.valueOf(8).equals(datos.ultimo.getIdSector()),"sector visible conservado");
            comprobar(Integer.valueOf(123).equals(datos.ultimo.getIdRequerimientoCompra()),"ID visible conservado");
            comprobar(Boolean.TRUE.equals(datos.ultimo.getSurge()),"SURGE visible conservado");
            comprobar("TERCERO".equals(datos.ultimo.getIdTercerizadora()),"tercerizadora visible conservada");
            int antes=datos.consultas;
            cargar(new Peticion("orden-de-compra","rol_view_prueba"));
            cargar(new Peticion("configuracion-de-correos","rol_cotizar_prueba"));
            comprobar(datos.consultas==antes,"otras solapas no consultan requerimientos");
            p=new Peticion("requerimientos",null); cargar(p);
            comprobar(datos.consultas==antes,"usuario sin rol no consulta");
            comprobar(p.valores.get(WebKeysCompras.ERROR_PARA_ALERT)!=null,"falta permiso publica error");
            p=new Peticion("para-autorizar","rol_view_prueba"); cargar(p);
            comprobar(datos.consultas==antes,"consulta sin autorizacion no ingresa a Para Autorizar");
            datos.fallar=true; p=new Peticion("requerimientos","rol_view_prueba");
            p.valores.put(ExportarRequerimientosCompraHelper.TOKEN,"obsoleto"); cargar(p);
            comprobar(p.valores.get(ExportarRequerimientosCompraHelper.TOKEN)==null,"fallo limpia token anterior");
            comprobar(p.valores.get(WebKeysCompras.ERROR_PARA_ALERT)!=null,"fallo publica error");
            comprobar(((List)p.valores.get(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA)).isEmpty(),"fallo publica resultado vacio");
            System.out.println("COMPRAS_PRIMERA_CARGA_VISTA_OFFLINE_OK checks="+checks);
        } finally { new PortalUtil().setPortal(anterior); }
    }
}
