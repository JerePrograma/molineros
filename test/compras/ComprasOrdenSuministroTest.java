import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraGuardadoToken;
import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraOrdenAction;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSuministro;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceImpl;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

/** Helper/Action/token productivos; solo las fronteras JDBC y de sesion son fixtures. */
public class ComprasOrdenSuministroTest {
    private static int checks;
    public static void main(String[] args) throws Exception {
        ComprasFuenteDatosFixture configuracion=new ComprasFuenteDatosFixture();configuracion.cargarConfiguracionPrueba();configuracion.instalar();
        carga(); validaciones(); guardado(); action();
        System.out.println("COMPRAS_ORDEN_SUMINISTRO_OFFLINE_OK checks=" + checks);
        System.out.println("Sin base, portal ni datos reales; validaciones y coordinacion productivas.");
    }

    private static void carga() throws Exception {
        Modelo m = new Modelo();
        RequerimientoCompraOrdenSuministro s = m.cargarSuministro(m.orden);
        igual("ausencia conserva OC", Integer.valueOf(900), Integer.valueOf(s.getIdOrdenCompra()));
        igual("ausencia version cero", Integer.valueOf(0), Integer.valueOf(s.getVersionEdicion()));
        igual("carga no inserta", Integer.valueOf(0), Integer.valueOf(m.escrituras));
        m.fila = fila("id_orden_compra", Integer.valueOf(900), "version_edicion", Integer.valueOf(2),
                "persona_contactada", "Persona guardada", "respuesta_1", "NO");
        s=m.cargarSuministro(m.orden);
        igual("carga persona", "Persona guardada", s.getPersonaContactada());
        igual("carga respuesta", "NO", s.getRespuesta1());
        igual("carga version", Integer.valueOf(2), Integer.valueOf(s.getVersionEdicion()));
        m.fila.put("id_orden_compra",Integer.valueOf(901));
        boolean fallo=false;try {m.cargarSuministro(m.orden);}catch(IllegalArgumentException e){fallo=true;}catch(IllegalStateException e){fallo=true;}
        comprobar("carga no acepta otra OC",fallo);
        m=new Modelo(); m.orden.setIdOrdenCompra(0);fallo=false;
        try {m.cargarSuministro(m.orden);}catch(IllegalArgumentException e){fallo=true;}
        comprobar("carga requiere OC emitida",fallo);
        m=new Modelo();m.r.setBajaFecha(new Date());fallo=false;
        try {m.cargarSuministro(m.orden);}catch(IllegalArgumentException e){fallo=true;}
        comprobar("carga no acepta requerimiento inactivo",fallo);
    }

    private static void validaciones() throws Exception {
        String[] fechas={"31/02/2026","2026-11-03","03/11/2026resto","00/11/2026"};
        for(int i=0;i<fechas.length;i++){Map<String,String> e=entrada();e.put("fecha_contacto",fechas[i]);rechazo("fecha invalida "+i,new Modelo(),40,900,0,e);}
        for(int i=1;i<=5;i++){Map<String,String> e=entrada();e.put("suministro_pregunta_"+i,"TAL_VEZ");rechazo("respuesta fuera de lista "+i,new Modelo(),40,900,0,e);}
        String[] campos={"suministro_persona","suministro_medio","suministro_detalle"};int[] limites={100,100,4000};
        for(int i=0;i<campos.length;i++){Map<String,String> e=entrada();e.put(campos[i],repetir(limites[i]+1));rechazo("longitud "+campos[i],new Modelo(),40,900,0,e);}
        rechazo("entrada ausente",new Modelo(),40,900,0,null);
        rechazo("requerimiento ausente",new Modelo(),0,900,0,entrada());
        rechazo("requerimiento ajeno",new Modelo(),41,900,0,entrada());
        rechazo("OC ajena",new Modelo(),40,901,0,entrada());
        rechazo("OC ausente",new Modelo(),40,0,0,entrada());
        rechazo("version negativa",new Modelo(),40,900,-1,entrada());
        rechazo("version agotada",new Modelo(),40,900,Integer.MAX_VALUE,entrada());
        Modelo m=new Modelo();m.r.setEstado(47);rechazo("no emitida",m,40,900,0,entrada());
        m=new Modelo();m.r.setBajaFecha(new Date());rechazo("requerimiento inactivo",m,40,900,0,entrada());
        m=new Modelo();m.sectorActivo=false;rechazo("sector inactivo",m,40,900,0,entrada());
        m=new Modelo();m.orden.setIdOrdenCompra(0);rechazo("sin OC persistida",m,40,900,0,entrada());
        m=new Modelo();m.orden.setIdRequerimientoCompra(41);rechazo("OC de otro requerimiento",m,40,900,0,entrada());
        m=new Modelo();m.fila=fila("id_orden_compra",Integer.valueOf(900),"version_edicion",Integer.valueOf(2));
        rechazo("formulario obsoleto no sobrescribe",m,40,900,1,entrada());
    }

    private static void guardado() throws Exception {
        Modelo m=new Modelo();RequerimientoCompraOrdenSuministro s=m.guardarSuministro(40,900,0,entrada(),"gestor_fixture");
        igual("insert version uno",Integer.valueOf(1),Integer.valueOf(s.getVersionEdicion()));
        igual("insert persona trim","Persona de prueba",s.getPersonaContactada());
        igual("insert medio","Telefono",s.getMedioContacto());
        igual("insert detalle","Primera linea\nSegunda linea",s.getDetalle());
        igual("insert fecha","03/11/2026",new SimpleDateFormat("dd/MM/yyyy").format(s.getFechaContacto()));
        String[] respuestas={s.getRespuesta1(),s.getRespuesta2(),s.getRespuesta3(),s.getRespuesta4(),s.getRespuesta5()};
        String[] esperado={"SI","NO","NO_CORRESPONDE","","SI"};
        for(int i=0;i<5;i++)igual("respuesta exacta "+i,esperado[i],respuestas[i]);
        igual("auditoria alta","gestor_fixture",s.getAltaUsr());
        comprobar("fecha alta",s.getAltaFecha()!=null);
        igual("commit una vez",Integer.valueOf(1),Integer.valueOf(m.commits));
        igual("sin rollback exitoso",Integer.valueOf(0),Integer.valueOf(m.rollbacks));
        igual("conexion cerrada",Integer.valueOf(1),Integer.valueOf(m.cierres));
        Map<String,String> vacia=new HashMap<String,String>();vacia.put("suministro_pregunta_5","NO");
        s=m.guardarSuministro(40,900,1,vacia,"segundo_gestor");
        igual("update version dos",Integer.valueOf(2),Integer.valueOf(s.getVersionEdicion()));
        comprobar("fecha opcional vacia",s.getFechaContacto()==null);
        comprobar("persona opcional vacia",vacio(s.getPersonaContactada()));
        comprobar("respuesta borrada",vacio(s.getRespuesta1()));
        igual("ultima respuesta modificada","NO",s.getRespuesta5());
        igual("alta conservada","gestor_fixture",s.getAltaUsr());
        igual("auditoria modificacion","segundo_gestor",s.getModiUsr());
        comprobar("fecha modificacion",s.getModiFecha()!=null);
        igual("no modifica version comercial",Integer.valueOf(0),Integer.valueOf(m.orden.getVersionEdicion()));
        m=new Modelo();m.falloEscritura=true;boolean fallo=false;
        try{m.guardarSuministro(40,900,0,entrada(),"gestor_fixture");}catch(SQLException e){fallo=true;}
        comprobar("propaga fallo SQL",fallo);igual("sin exito tras fallo",Integer.valueOf(0),Integer.valueOf(m.commits));
        igual("rollback fallo SQL",Integer.valueOf(1),Integer.valueOf(m.rollbacks));comprobar("fallo no deja fila",m.fila==null);
    }

    private static void action() throws Exception {
        String[][] roles={{"GESTION_COMPRAS"},{"ABM_COMPRAS","GESTION_COMPRAS"}};
        for(int i=0;i<roles.length;i++){
            Accion a=new Accion(roles[i]);Peticion p=peticion();token(p);a.ejecutar(p);
            igual("Action gestion guarda "+i,Integer.valueOf(1),Integer.valueOf(a.modelo.guardados));
            igual("Action exito "+i,Boolean.TRUE,p.atributos.get("ordenSuministroGuardado"));
            igual("Action vista suministro "+i,"1",p.render.get("gestion_suministro"));
            igual("Action fecha de tags "+i,"03/11/2026",a.modelo.entrada.get("fecha_contacto"));
            igual("Action actor autenticado "+i,"gestor_fixture",a.modelo.actor);
            Peticion repetida=peticion();repetida.parametros.putAll(p.parametros);repetida.sesion.putAll(p.sesion);a.ejecutar(repetida);igual("Action doble envio sin guardar "+i,Integer.valueOf(1),Integer.valueOf(a.modelo.guardados));comprobar("Action repetido sin falso exito "+i,!Boolean.TRUE.equals(repetida.atributos.get("ordenSuministroGuardado")));
        }
        String[][] rechazados={{"ABM_COMPRAS"},{"VIEW_COMPRAS"},{"GESTION_COMPRAS_OTRO"},{}};
        for(int i=0;i<rechazados.length;i++){Accion a=new Accion(rechazados[i]);Peticion p=peticion();token(p);a.ejecutar(p);igual("Action rol rechazado "+i,Integer.valueOf(0),Integer.valueOf(a.modelo.guardados));comprobar("Action informa error permiso "+i,p.atributos.get("ordenSuministroError")!=null||p.atributos.get("ordenCompraError")!=null);}
        Accion a=new Accion(new String[]{"GESTION_COMPRAS"});Peticion p=peticion();a.ejecutar(p);igual("Action token omitido",Integer.valueOf(0),Integer.valueOf(a.modelo.guardados));
        p=peticion();p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO,"desconocido");a.ejecutar(p);igual("Action token desconocido",Integer.valueOf(0),Integer.valueOf(a.modelo.guardados));
        Peticion otra=peticion();token(otra);p=peticion();p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO,otra.parametros.get(RequerimientoCompraGuardadoToken.PARAMETRO));a.ejecutar(p);igual("Action otra sesion",Integer.valueOf(0),Integer.valueOf(a.modelo.guardados));
        a=new Accion(new String[]{"GESTION_COMPRAS"});a.modelo.rechazar=true;p=peticion();token(p);a.ejecutar(p);
        comprobar("Action conserva entrada tras validacion",p.atributos.get("ordenSuministroEntrada") instanceof Map);
        igual("Action conserva texto","Persona enviada",((Map)p.atributos.get("ordenSuministroEntrada")).get("suministro_persona"));
        comprobar("Action no informa falso exito",!Boolean.TRUE.equals(p.atributos.get("ordenSuministroGuardado")));
        comprobar("Action error validacion",p.atributos.get("ordenSuministroError")!=null);
    }

    static Map<String,String> entrada(){Map<String,String> e=new HashMap<String,String>();e.put("fecha_contacto","03/11/2026");e.put("suministro_persona"," Persona de prueba ");e.put("suministro_medio","Telefono");e.put("suministro_detalle","Primera linea\nSegunda linea");String[] r={"SI","NO","NO_CORRESPONDE","","SI"};for(int i=0;i<r.length;i++)e.put("suministro_pregunta_"+(i+1),r[i]);return e;}
    private static String repetir(int n){StringBuffer s=new StringBuffer();for(int i=0;i<n;i++)s.append('x');return s.toString();}
    private static boolean vacio(String s){return s==null||s.length()==0;}
    private static void rechazo(String caso,Modelo m,int req,int oc,int version,Map<String,String> e)throws Exception{Map<String,Object> antes=m.fila==null?null:new HashMap<String,Object>(m.fila);boolean rechazo=false;try{m.guardarSuministro(req,oc,version,e,"gestor_fixture");}catch(IllegalArgumentException ex){rechazo=true;}comprobar(caso,rechazo);igual(caso+" sin escritura",Integer.valueOf(0),Integer.valueOf(m.escrituras));igual(caso+" conserva fila",antes,m.fila);igual(caso+" sin commit",Integer.valueOf(0),Integer.valueOf(m.commits));}
    private static void igual(String caso,Object esperado,Object actual){comprobar(caso+": esperado="+esperado+", actual="+actual,esperado==null?actual==null:esperado.equals(actual));}
    private static void comprobar(String caso,boolean condicion){checks++;if(!condicion)throw new AssertionError(caso);}
    private static Map<String,Object> fila(Object... datos){Map<String,Object> r=new HashMap<String,Object>();for(int i=0;i<datos.length;i+=2)r.put((String)datos[i],datos[i+1]);return r;}
    private static Object vacioTipo(Class tipo){if(tipo==Boolean.TYPE)return Boolean.FALSE;if(tipo==Integer.TYPE)return Integer.valueOf(0);if(tipo==Long.TYPE)return Long.valueOf(0);return null;}

    private static class Modelo extends RequerimientoCompraOrdenHelper {
        RequerimientoCompra r=new RequerimientoCompra();RequerimientoCompraOrden orden=new RequerimientoCompraOrden();
        Map<String,Object> fila;boolean sectorActivo=true;boolean falloEscritura;int escrituras;int commits;int rollbacks;int cierres;
        Modelo()throws Exception{r.setIdRequerimientoCompra(40);r.setSectorId(Integer.valueOf(1));r.setEstado(45);orden.setIdOrdenCompra(900);orden.setIdRequerimientoCompra(40);orden.setRequerimiento(r);ComprasFuenteDatosFixture f=new ComprasFuenteDatosFixture();f.cargarConfiguracionPrueba();f.instalar();}
        protected RequerimientoCompraOrdenSuministro obtenerSuministro(int id)throws Exception{return convertir(fila);}
        protected RequerimientoCompraOrdenServiceUtil.Transaccion abrirTransaccion()throws Exception{
            final Map<String,Object> antes=fila==null?null:new HashMap<String,Object>(fila);
            ComprasFuenteDatosFixture f=new ComprasFuenteDatosFixture();f.cargarConfiguracionPrueba();f.requerimientos.put(Integer.valueOf(40),r);f.instalar();
            Connection con=(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},new InvocationHandler(){public Object invoke(Object p,Method m,Object[] a)throws Throwable{String n=m.getName();if(n.equals("prepareStatement")||n.equals("prepareCall"))return sentencia((String)a[0],n.equals("prepareCall"));if(n.equals("commit"))commits++;if(n.equals("rollback")){rollbacks++;fila=antes;}if(n.equals("close"))cierres++;return vacioTipo(m.getReturnType());}});
            Constructor c=RequerimientoCompraOrdenServiceImpl.Transaccion.class.getDeclaredConstructor(new Class[]{RequerimientoCompraOrdenServiceImpl.class,Connection.class});c.setAccessible(true);Object tx=c.newInstance(new Object[]{new RequerimientoCompraOrdenServiceImpl(),con});Constructor w=RequerimientoCompraOrdenServiceUtil.Transaccion.class.getDeclaredConstructor(new Class[]{RequerimientoCompraOrdenServiceImpl.Transaccion.class});w.setAccessible(true);return(RequerimientoCompraOrdenServiceUtil.Transaccion)w.newInstance(new Object[]{tx});
        }
        private Object sentencia(final String sql,boolean call){return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{call?CallableStatement.class:PreparedStatement.class},new InvocationHandler(){final Map<Integer,Object> p=new HashMap<Integer,Object>();public Object invoke(Object x,Method m,Object[] a)throws Throwable{String n=m.getName();if(n.startsWith("set")&&a!=null&&a.length>1)p.put((Integer)a[0],n.equals("setNull")?null:a[1]);if(n.equals("execute"))return Boolean.TRUE;if(n.equals("executeQuery"))return leer(sql,p);if(n.equals("executeUpdate"))return Integer.valueOf(escribir(sql,p));return vacioTipo(m.getReturnType());}});}
        private ResultSet leer(String sql,Map<Integer,Object> p)throws Exception{
            List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();
            if(sql.indexOf("FROM compras.requerimiento_orden_compra_suministro")>=0){if(fila!=null)rows.add(new HashMap<String,Object>(fila));}
            else if(sql.startsWith("SELECT activo,baja_fecha"))rows.add(fila("activo",Boolean.valueOf(sectorActivo)));
            else if(sql.startsWith("SELECT o.id_orden_compra,o.id_requerimiento")){if(orden.getIdOrdenCompra()>0)rows.add(fila("id_orden_compra",Integer.valueOf(orden.getIdOrdenCompra()),"id_requerimiento",Integer.valueOf(orden.getIdRequerimientoCompra()),"version_edicion",Integer.valueOf(0),"tipo_documento",Integer.valueOf(3)));}
            else if(sql.startsWith("SELECT od.id_requerimiento_detalle")){}
            else if(sql.startsWith("SELECT fecha_documento FROM compras.requerimiento_presupuesto")){}
            else if(sql.indexOf("FOR UPDATE")>=0)rows.add(fila("1",Integer.valueOf(40)));
            else throw new SQLException("Lectura inesperada de fixture suministro: "+sql);
            final List<Map<String,Object>> datos=rows;
            return(ResultSet)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){int i=-1;boolean nulo;public Object invoke(Object p,Method m,Object[] a)throws Throwable{String n=m.getName();if(n.equals("next"))return Boolean.valueOf(++i<datos.size());if(n.equals("wasNull"))return Boolean.valueOf(nulo);if(n.startsWith("get")){Object v=datos.get(i).get(String.valueOf(a[0]));nulo=v==null;if(n.equals("getInt"))return Integer.valueOf(v==null?0:((Number)v).intValue());if(n.equals("getBoolean"))return Boolean.valueOf(Boolean.TRUE.equals(v));if(n.equals("getDate")&&v!=null)return new java.sql.Date(((Date)v).getTime());if(n.equals("getTimestamp")&&v!=null)return new Timestamp(((Date)v).getTime());return v;}return vacioTipo(m.getReturnType());}});
        }
        private int escribir(String sql,Map<Integer,Object> p)throws Exception{
            if(falloEscritura)throw new SQLException("Fallo SQL sintetico suministro","22001");
            if(sql.startsWith("INSERT INTO compras.requerimiento_orden_compra_suministro")){
                escrituras++;fila=fila("id_orden_compra",p.get(Integer.valueOf(1)),"alta_fecha",new Date(),"alta_usr",p.get(Integer.valueOf(11)),"version_edicion",Integer.valueOf(1));asignar(p,2);return 1;
            }
            if(sql.startsWith("UPDATE compras.requerimiento_orden_compra_suministro")){
                if(fila==null||!fila.get("version_edicion").equals(p.get(Integer.valueOf(12))))return 0;
                escrituras++;asignar(p,1);fila.put("modi_usr",p.get(Integer.valueOf(10)));fila.put("modi_fecha",new Date());fila.put("version_edicion",Integer.valueOf(((Integer)fila.get("version_edicion")).intValue()+1));return 1;
            }
            throw new SQLException("Escritura inesperada de fixture suministro.");
        }
        private void asignar(Map<Integer,Object> p,int inicio){String[] c={"fecha_contacto","persona_contactada","medio_contacto","detalle","respuesta_1","respuesta_2","respuesta_3","respuesta_4","respuesta_5"};for(int i=0;i<c.length;i++)fila.put(c[i],p.get(Integer.valueOf(inicio+i)));}
        private RequerimientoCompraOrdenSuministro convertir(Map<String,Object> f){if(f==null)return null;RequerimientoCompraOrdenSuministro s=new RequerimientoCompraOrdenSuministro();s.setIdOrdenCompra(((Integer)f.get("id_orden_compra")).intValue());s.setVersionEdicion(((Integer)f.get("version_edicion")).intValue());s.setPersonaContactada((String)f.get("persona_contactada"));s.setRespuesta1((String)f.get("respuesta_1"));return s;}
    }

    private static class ModeloAction extends RequerimientoCompraOrdenHelper {
        int guardados;boolean rechazar;Map<String,String> entrada;String actor;
        public RequerimientoCompraOrdenSuministro guardarSuministro(int req,int oc,int version,Map<String,String> e,String usuario){entrada=e;actor=usuario;if(rechazar)throw new IllegalArgumentException("Validacion sintetica de suministro.");guardados++;RequerimientoCompraOrdenSuministro s=new RequerimientoCompraOrdenSuministro();s.setIdOrdenCompra(oc);s.setVersionEdicion(version+1);return s;}
    }
    private static class Accion extends RequerimientoCompraOrdenAction {
        final ModeloAction modelo=new ModeloAction();final User usuario;
        Accion(String[] nombres){final List<Role> roles=new ArrayList<Role>();for(int i=0;i<nombres.length;i++){Role r=new RoleImpl();r.setName(nombres[i]);roles.add(r);}usuario=new UserImpl(){public List<Role> getRoles(){return roles;}public String getScreenName(){return "gestor_fixture";}public String getFullName(){return "Gestor de prueba";}};}
        protected RequerimientoCompraOrdenHelper obtenerHelper(){return modelo;}
        protected User obtenerUsuario(PortletRequest r){return usuario;}
        void ejecutar(Peticion p)throws Exception{processAction(null,null,null,(ActionRequest)p.proxy(),(ActionResponse)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ActionResponse.class},p));}
    }
    private static Peticion peticion(){Peticion p=new Peticion();p.parametros.put("cmd","guardar_suministro");p.parametros.put("id_requerimiento_compra","40");p.parametros.put("id_orden_compra","900");p.parametros.put("version_suministro","0");p.parametros.put("gestion_suministro","1");p.parametros.put("contactoDia","3");p.parametros.put("contactoMes","10");p.parametros.put("contactoAnio","2026");p.parametros.put("suministro_persona","Persona enviada");return p;}
    private static void token(Peticion p){RequerimientoCompraGuardadoToken.publicar((RenderRequest)p.proxy());p.parametros.put(RequerimientoCompraGuardadoToken.PARAMETRO,(String)p.atributos.get("COMPRAS_SAVE_TOKEN"));}
    private static class Peticion implements InvocationHandler {
        final Map<String,String> parametros=new HashMap<String,String>();final Map<String,String> render=new HashMap<String,String>();final Map<String,Object> atributos=new HashMap<String,Object>();final Map<String,Object> sesion=new HashMap<String,Object>();
        final PortletSession session=(PortletSession)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PortletSession.class},this);
        Object proxy(){return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ActionRequest.class,RenderRequest.class},this);}
        public Object invoke(Object proxy,Method m,Object[] a){String n=m.getName();Map<String,Object> store=proxy instanceof PortletSession?sesion:atributos;if(n.equals("getPortletSession"))return session;if(n.equals("getAttribute"))return store.get(a[0]);if(n.equals("setAttribute")){store.put((String)a[0],a[1]);return null;}if(n.equals("removeAttribute")){store.remove(a[0]);return null;}if(n.equals("getAttributeNames"))return java.util.Collections.enumeration(store.keySet());if(n.equals("getParameter"))return parametros.get(a[0]);if(n.equals("getParameterValues")){String s=parametros.get(a[0]);return s==null?null:new String[]{s};}if(n.equals("getParameterNames"))return java.util.Collections.enumeration(parametros.keySet());if(n.equals("getParameterMap")){Map<String,String[]> p=new HashMap<String,String[]>();for(String k:parametros.keySet())p.put(k,new String[]{parametros.get(k)});return p;}if(n.equals("setRenderParameter")){render.put((String)a[0],(String)a[1]);return null;}return vacioTipo(m.getReturnType());}
    }
}