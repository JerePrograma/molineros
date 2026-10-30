import ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/** Helper y lector JDBC reales; ninguna conexion de produccion. */
public class ComprasDuplicadoJavaTest {
    private static int pruebas;
    private static final String CUIL="20111111112";
    public static void main(String[] args)throws Exception {
        Estado e=new Estado();BusquedaRequerimientoCompraServiceImpl s=new BusquedaRequerimientoCompraServiceImpl();
        java.util.Date fecha=fecha(30,18);
        comprobar(!s.existeRequerimientoDuplicado(e.con,null,0,100,fecha,0),"CUIL NULL retorna false sin consultar DB");
        comprobar(!s.existeRequerimientoDuplicado(e.con,"   ",0,100,fecha,0),"CUIL solo espacios retorna false como NULLIF btrim SQL");
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,-1,100,fecha,0),"Integrante negativo retorna false");
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,0,fecha,0),"Canonico no positivo retorna false");
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,null,0)&&e.consultas==0,"Fecha NULL retorna false sin lecturas");
        comprobar(!NomencladorCompraBusquedaHelper.evaluarRequerimientoDuplicado(CUIL,null,Integer.valueOf(100),fecha,null,99,e.filas),"Integrante NULL conserva CASE FALSE instalado");
        comprobar(!NomencladorCompraBusquedaHelper.evaluarRequerimientoDuplicado(CUIL,Integer.valueOf(0),null,fecha,null,99,e.filas),"Canonico NULL conserva CASE FALSE instalado");

        e=new Estado();e.filas.add(fila());
        comprobar(s.existeRequerimientoDuplicado(e.con,"  "+CUIL+"  ",0,100,fecha,0),"Coincidencia activa se decide por Helper real con JDBC crudo");
        comprobar(CUIL.equals(e.parametros.get(Integer.valueOf(1)))&&Integer.valueOf(0).equals(e.parametros.get(Integer.valueOf(2)))
                &&Integer.valueOf(100).equals(e.parametros.get(Integer.valueOf(3))),"Parametros JDBC conservan btrim SQL e identidades exactas");
        comprobar(e.consultas==2&&e.call==0&&!e.cerrada&&e.cierresStmt==2&&e.cierresRs==2,"Misma conexion caller preservada y recursos JDBC cerrados sin CALL de reglas");
        e.filas.clear();comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Sin candidatos retorna false");
        e.filas.add(fila());
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,42),"Requerimiento propio positivo se excluye");
        comprobar(s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,-1),"Exclusion no positiva no excluye coincidencias");
        comprobar(NomencladorCompraBusquedaHelper.evaluarRequerimientoDuplicado(CUIL,Integer.valueOf(0),Integer.valueOf(100),fecha,null,99,datosMapeados(e.filas)),"Exclusion NULL no excluye coincidencias");
        e.filas.get(0).put("id_prestacion",Integer.valueOf(101));comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Canonico distinto no duplica aunque pudiera compartir codigo");
        e.filas.set(0,fila());e.filas.get(0).put("afiliado_cuil_titular",CUIL+" ");
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"CUIL persistido no se recorta: comparacion exacta VARCHAR del SQL");
        e.filas.set(0,fila());e.filas.get(0).put("estado",null);
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Estado NULL no cumple estado distinto de ANULADO");
        e.filas.set(0,fila());e.filas.get(0).put("estado",Integer.valueOf(99));
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"ANULADO vigente se excluye");
        e.configuracion="ANULADO=199";
        comprobar(s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Estado ANULADO se relee de configuracion sin cache ni 99 fijo");
        for(String baja:new String[]{"requerimiento_baja","detalle_baja","documento_baja"}){
            e=new Estado();e.filas.add(fila());e.filas.get(0).put(baja,new Timestamp(1));
            comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Baja de "+baja+" excluye candidato");
        }
        e=new Estado();e.filas.add(fila());e.filas.get(0).put("tipo_item","nomenclador");
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Tipo item se compara exacto y distingue mayusculas");
        e.filas.set(0,fila());e.filas.get(0).put("tipo_documento",Integer.valueOf(1));
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Presupuesto prestador no se trata como orden medica");
        e.filas.set(0,fila());e.filas.get(0).put("fecha_documento",null);
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Fecha persistida NULL no coincide");
        e.filas.set(0,fila());
        comprobar(s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha(30,23),0),"Fecha DATE ignora hora del parametro como setDate existente");
        comprobar(!s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha(31,0),0),"Dia diferente no coincide");
        e.filas.get(0).put("afiliado_cuil_titular","\t"+CUIL+"\t");
        comprobar(s.existeRequerimientoDuplicado(e.con," \t"+CUIL+"\t ",0,100,fecha,0)
                &&("\t"+CUIL+"\t").equals(e.parametros.get(Integer.valueOf(1))),"btrim solo elimina espacios ASCII y conserva tabs");
        e.filas.get(0).put("afiliado_cuil_titular",CUIL);
        comprobar(!s.existeRequerimientoDuplicado(e.con,"\t"+CUIL+"\t",0,100,fecha,0),"Tab del parametro no se normaliza silenciosamente a CUIL limpio");
        e=new Estado();e.filas.add(fila());e.filas.get(0).put("estado",Integer.valueOf(99));e.filas.add(fila());
        comprobar(s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0),"Candidato posterior valido se acepta aunque el primero no cumpla");
        e=new Estado();e.configuracion="";boolean error=false;
        try{s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0);}catch(ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.ConfiguracionInvalidaException esperado){error=true;}
        comprobar(error&&!e.cerrada&&e.consultas==1,"Configuracion ausente falla tipada antes de leer candidatos");
        e=new Estado();e.duplicarConfig=true;error=false;
        try{s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0);}catch(SQLException esperado){error=true;}
        comprobar(error&&!e.cerrada&&e.cierresStmt==1&&e.cierresRs==1,"Config duplicada propaga error y preserva conexion caller");
        e=new Estado();e.falloDatos=true;error=false;
        try{s.existeRequerimientoDuplicado(e.con,CUIL,0,100,fecha,0);}catch(SQLException esperado){error=true;}
        comprobar(error&&!e.cerrada&&e.cierresStmt==2,"Fallo de lectura propaga sin false ni cierre de conexion caller");
        System.out.println("APROBADAS_DUPLICADO="+pruebas+"; Helper/JDBC reales, sin DB ni UI");
    }
    private static void comprobar(boolean ok,String nombre)throws Exception{if(!ok)throw new Exception("FALLO: "+nombre);pruebas++;System.out.println("OK "+nombre);}
    private static java.util.Date fecha(int dia,int hora){Calendar c=Calendar.getInstance();c.clear();c.set(2026,Calendar.OCTOBER,dia,hora,0,0);return c.getTime();}
    private static Map<String,Object> fila(){
        Map<String,Object> f=new HashMap<String,Object>();f.put("id_requerimiento",Integer.valueOf(42));f.put("estado",Integer.valueOf(3));
        f.put("afiliado_cuil_titular",CUIL);f.put("afiliado_int",Integer.valueOf(0));f.put("tipo_item","NOMENCLADOR");
        f.put("id_prestacion",Integer.valueOf(100));f.put("tipo_documento",Integer.valueOf(2));f.put("fecha_documento",new java.sql.Date(fecha(30,0).getTime()));return f;
    }
    private static List<Map<String,Object>> datosMapeados(List<Map<String,Object>> filas){
        List<Map<String,Object>> r=new ArrayList<Map<String,Object>>();for(Map<String,Object> f:filas){Map<String,Object> c=new HashMap<String,Object>(f);
            c.put("cuil",f.get("afiliado_cuil_titular"));c.put("integrante",f.get("afiliado_int"));r.add(c);}return r;
    }
    private static class Estado implements InvocationHandler{
        Connection con;List<Map<String,Object>> filas=new ArrayList<Map<String,Object>>();Map<Integer,Object> parametros;
        String configuracion="ANULADO=99";boolean cerrada,duplicarConfig,falloDatos;int consultas,call,cierresStmt,cierresRs;
        Estado(){con=(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},this);}
        public Object invoke(Object p,Method m,Object[] a)throws Throwable{
            String n=m.getName();if("close".equals(n)){cerrada=true;return null;}if("isClosed".equals(n))return Boolean.valueOf(cerrada);
            if("prepareCall".equals(n)){call++;throw new SQLException("CALL prohibido");}
            if(!"prepareStatement".equals(n))throw new UnsupportedOperationException(n);
            final String sql=(String)a[0];final boolean cfg=sql.equals("SELECT valor FROM public.system_config WHERE id = ?");
            if(!cfg&&!sql.startsWith("SELECT r.id_requerimiento,r.estado"))throw new SQLException("SQL inesperado: "+sql);
            return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PreparedStatement.class},new InvocationHandler(){
                Map<Integer,Object> valores=new HashMap<Integer,Object>();
                public Object invoke(Object p,Method m,Object[] a)throws Throwable{
                    String n=m.getName();if(n.startsWith("set")){valores.put((Integer)a[0],a[1]);return null;}
                    if("close".equals(n)){cierresStmt++;return null;}
                    if(!"executeQuery".equals(n))throw new UnsupportedOperationException(n);
                    consultas++;if(!cfg){parametros=valores;if(falloDatos)throw new SQLException("Fallo simulado");}
                    List<Map<String,Object>> r=new ArrayList<Map<String,Object>>();
                    if(cfg){Map<String,Object> f=new HashMap<String,Object>();f.put("1",configuracion);f.put("valor",configuracion);r.add(f);if(duplicarConfig)r.add(f);}else r.addAll(filas);
                    return resultado(r);
                }});
        }
        private ResultSet resultado(final List<Map<String,Object>> filas){
            return (ResultSet)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){
                int posicion=-1;boolean nulo;
                public Object invoke(Object p,Method m,Object[] a)throws Throwable{
                    String n=m.getName();if("next".equals(n))return Boolean.valueOf(++posicion<filas.size());
                    if("close".equals(n)){cierresRs++;return null;}if("wasNull".equals(n))return Boolean.valueOf(nulo);
                    Object v=filas.get(posicion).get(String.valueOf(a[0]));nulo=v==null;
                    if("getInt".equals(n))return v!=null?v:Integer.valueOf(0);
                    if("getString".equals(n)||"getTimestamp".equals(n)||"getDate".equals(n)||"getObject".equals(n))return v;
                    throw new UnsupportedOperationException(n);
                }});
        }
    }
}
