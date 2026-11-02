import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceImpl;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/** INSERT productivo real sobre esquema JDBC sin descripcion_prestador fisica. */
public class ComprasPresupuestoPrestadorInsercionTest {
    private interface Caso {void ejecutar() throws Exception;}
    private static int casos,checks,fallos,columnasAusentes;
    private static void caso(String nombre,Caso caso){
        casos++;try{caso.ejecutar();System.out.println("PASS "+nombre);}catch(Throwable e){fallos++;System.out.println("FAIL "+nombre+" "+e.getClass().getName()+": "+e.getMessage());}
    }
    private static void check(boolean ok,String texto){checks++;if(!ok)throw new AssertionError(texto);}
    private static void eq(Object esperado,Object actual,String texto){check(esperado==null?actual==null:esperado.equals(actual),texto+" esperado="+esperado+" actual="+actual);}
    public static void main(String[] args)throws Exception {
        caso("tipo1 INSERT reducido guarda identidad y16valores sin copia DTO",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();RequerimientoCompraPresupuesto p=entrada(1);int id=new EditarRequerimientoCompraServiceImpl().insertarDocumento(e.con,p,"auditor");
            eq(Integer.valueOf(501),Integer.valueOf(id),"ID retornado");eq(Integer.valueOf(101),e.fila.get("id_prestador"),"referencia proveedor");
            comunes(e);eq("auditor",e.parametros.get(Integer.valueOf(16)),"auditor en16");check(!e.fila.containsKey("descripcion_prestador"),"no persiste texto DTO");e.caller();
        }});
        caso("tipo2 OM conserva fecha receta ynullprestador",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();new EditarRequerimientoCompraServiceImpl().insertarDocumento(e.con,entrada(2),"qa");
            eq(null,e.fila.get("id_prestador"),"OM sinprestador");eq(java.sql.Date.valueOf("2026-01-02"),e.fila.get("fecha_documento"),"fecha");eq("RECETA-QA",e.fila.get("numero_receta"),"receta");comunes(e);e.caller();
        }});
        caso("tipo3 conserva empresa libre y no convierte su descripcion en proveedor",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();new EditarRequerimientoCompraServiceImpl().insertarDocumento(e.con,entrada(3),"qa");
            eq(null,e.fila.get("id_prestador"),"empresa sinprestador");eq(null,e.fila.get("empresa_cuit"),"CUIT libre");eq(null,e.fila.get("empresa_sucursal"),"sucursal libre");
            eq("Empresa libre QA",e.fila.get("descripcion_empresa"),"descripcion empresa conservada");eq("Empresa libre QA",e.parametros.get(Integer.valueOf(8)),"bind8 inmutable");comunes(e);e.caller();
        }});
        caso("auditoria nula consulta current_user por mismaconexion ybind16",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();new EditarRequerimientoCompraServiceImpl().insertarDocumento(e.con,entrada(1),null);
            eq(Integer.valueOf(1),Integer.valueOf(e.lecturasAuditor),"lector tecnico mismaCon");eq("auditor_db",e.parametros.get(Integer.valueOf(16)),"auditor tecnico");comunes(e);e.caller();
        }});
        caso("auditoria conserva tabs y no desplaza columnas",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();new EditarRequerimientoCompraServiceImpl().insertarDocumento(e.con,entrada(1)," \tqa\t ");
            eq("\tqa\t",e.parametros.get(Integer.valueOf(16)),"btrimASCII sintrimtabs");eq(Integer.valueOf(0),Integer.valueOf(e.lecturasAuditor),"sin lectura global");comunes(e);e.caller();
        }});
        caso("tabla legacy testigo evidencia numero real de bindings",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.admitirColumnaLegacy=true;new EditarRequerimientoCompraServiceImpl().insertarDocumento(e.con,entrada(1),"auditor");
            System.out.println("BINDINGS_REALES_OBSERVADOS="+e.parametros.size()+" AUDITOR_POSICION="+posicion(e.parametros,"auditor"));
            eq(Integer.valueOf(16),Integer.valueOf(e.parametros.size()),"contrato16 incluso si tabla admite columna antigua");
            eq("auditor",e.parametros.get(Integer.valueOf(16)),"usuario16");e.caller();
        }});
        caso("error SQL realista se propaga y cierra recursos sin controlar TX",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.fallarInsert=true;boolean recibido=false;
            try{new EditarRequerimientoCompraServiceImpl().insertarDocumento(e.con,entrada(1),"qa");}catch(SQLException esperado){recibido="23514".equals(esperado.getSQLState());}
            check(recibido,"SQLSTATE de persistencia no ocultado");eq(Integer.valueOf(1),Integer.valueOf(e.sentenciasCerradas),"statement cerrado");e.caller();
        }});
        check(ar.com.ospim.util.ConnectionHelper.aperturas==0,"sin conexiones globales");
        System.out.println("PRESUPUESTO_PRESTADOR_INSERCION casos="+casos+" checks="+checks+" fallos="+fallos+" columnas_ausentes="+columnasAusentes+"; JDBC simulado sin DB/UI");
        if(fallos>0)throw new Exception("Contrato INSERT sin copia incumplido: "+fallos);
    }
    private static int posicion(Map<Integer,Object> p,Object v){for(Map.Entry<Integer,Object> e:p.entrySet())if(v.equals(e.getValue()))return e.getKey().intValue();return 0;}
    private static RequerimientoCompraPresupuesto entrada(int tipo){
        RequerimientoCompraPresupuesto p=new RequerimientoCompraPresupuesto();p.setIdRequerimiento(Integer.valueOf(41));p.setTipoDocumento(Integer.valueOf(tipo));p.setIdPrestador(tipo==1?Integer.valueOf(101):null);
        p.setFechaDocumento(java.sql.Date.valueOf("2026-01-02"));p.setNumeroReceta(tipo==2?"RECETA-QA":null);p.setDescripcionEmpresa(tipo==3?"Empresa libre QA":null);
        p.setDlGroupId(Long.valueOf(9012345678L));p.setDlFolderId(Long.valueOf(9012345679L));p.setDlFileEntryId(Long.valueOf(9012345680L));p.setDlFileUuid("uuid-qa");
        p.setNombreOriginal("original.pdf");p.setNombrePersistido("persistido.pdf");p.setTitulo("Titulo QA");p.setDescripcionPrestador("COPIA DTO NO PERSISTIR");return p;
    }
    private static void comunes(Estado e){
        eq(Integer.valueOf(16),Integer.valueOf(e.parametros.size()),"cantidad bindings");eq(Integer.valueOf(41),e.parametros.get(Integer.valueOf(1)),"req bind1");
        eq(Long.valueOf(9012345678L),e.parametros.get(Integer.valueOf(9)),"grupo long bind9");eq(Long.valueOf(9012345679L),e.parametros.get(Integer.valueOf(10)),"folder bind10");
        eq(Long.valueOf(9012345680L),e.parametros.get(Integer.valueOf(11)),"file bind11");eq("uuid-qa",e.parametros.get(Integer.valueOf(12)),"uuid12");
        eq("original.pdf",e.parametros.get(Integer.valueOf(13)),"nombre13");eq("persistido.pdf",e.parametros.get(Integer.valueOf(14)),"persistido14");eq("Titulo QA",e.parametros.get(Integer.valueOf(15)),"titulo15");
        check(!e.fila.containsKey("descripcion_prestador"),"tabla fisica sin copia");
    }
    private static class Estado implements InvocationHandler {
        final Connection con;Map<String,Object> fila=new LinkedHashMap<String,Object>();Map<Integer,Object> parametros=new LinkedHashMap<Integer,Object>();
        boolean admitirColumnaLegacy,fallarInsert,cerrada;int commits,rollbacks,sentenciasCerradas,lecturasAuditor;
        Estado(){con=(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},this);}
        void caller(){check(!cerrada&&commits==0&&rollbacks==0,"conexion TX del caller intacta");}
        public Object invoke(Object p,Method m,Object[] a)throws Throwable{
            String n=m.getName();if("prepareCall".equals(n))throw new SQLException("Sin funciones SQL en esta frontera");
            if("prepareStatement".equals(n)){
                final String sql=(String)a[0];final String q=sql.toLowerCase(Locale.ROOT);
                final boolean auditor=q.indexOf("current_user")>=0;
                final String[] campos=auditor?new String[0]:q.substring(q.indexOf('(')+1,q.indexOf(')')).split(",");
                if(!auditor){
                    for(int i=0;i<campos.length;i++)if("descripcion_prestador".equals(campos[i].trim())&&!admitirColumnaLegacy){columnasAusentes++;throw new SQLException("column descripcion_prestador does not exist; INSERT columns="+campos.length,"42703");}
                    parametros=new LinkedHashMap<Integer,Object>();
                }
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PreparedStatement.class},new InvocationHandler(){
                    public Object invoke(Object p,Method method,Object[] args)throws Throwable{
                        String name=method.getName();if(name.startsWith("set")&&args!=null&&args.length>1){parametros.put((Integer)args[0],"setNull".equals(name)?null:args[1]);return null;}
                        if("close".equals(name)){sentenciasCerradas++;return null;}
                        if("executeQuery".equals(name)){
                            if(auditor){lecturasAuditor++;return row("auditor_db");}
                            if(fallarInsert)throw new SQLException("persistencia sintetica","23514");
                            int signos=0;for(int i=0;i<sql.length();i++)if(sql.charAt(i)=='?')signos++;
                            if(parametros.size()!=campos.length||signos!=campos.length)throw new SQLException("Cantidad de parametros no coincide","07001");
                            fila=new LinkedHashMap<String,Object>();for(int i=0;i<campos.length;i++)fila.put(campos[i].trim(),parametros.get(Integer.valueOf(i+1)));
                            return row(Integer.valueOf(501));
                        }
                        return defecto(method.getReturnType());
                    }
                });
            }
            if("close".equals(n)){cerrada=true;return null;}if("isClosed".equals(n))return Boolean.valueOf(cerrada);
            if("commit".equals(n)){commits++;return null;}if("rollback".equals(n)){rollbacks++;return null;}return defecto(m.getReturnType());
        }
    }
    private static ResultSet row(final Object value){return (ResultSet)Proxy.newProxyInstance(ComprasPresupuestoPrestadorInsercionTest.class.getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){
        boolean primero=true;
        public Object invoke(Object p,Method m,Object[] a){String n=m.getName();if("next".equals(n)){boolean v=primero;primero=false;return Boolean.valueOf(v);}if("getInt".equals(n))return value;if("getString".equals(n))return String.valueOf(value);return defecto(m.getReturnType());}
    });}
    private static Object defecto(Class c){if(c==Boolean.TYPE)return Boolean.FALSE;if(c==Integer.TYPE)return Integer.valueOf(0);if(c==Long.TYPE)return Long.valueOf(0);return null;}
}
