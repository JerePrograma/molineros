import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.helper.TextoCompraHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

/** Lector productivo real. Solo la frontera JDBC representa la tabla sin la copia. */
public class ComprasPresupuestoPrestadorLecturaTest {
    private interface Caso {void ejecutar() throws Exception;}
    private static int casos, checks, fallos, columnasAusentes;
    private static void probar(String nombre,Caso caso) {
        casos++;
        try {caso.ejecutar();System.out.println("PASS "+nombre);}
        catch(Throwable e) {fallos++;System.out.println("FAIL "+nombre+" "+e.getClass().getName()+": "+e.getMessage());}
    }
    private static void check(boolean ok,String texto) {checks++;if(!ok)throw new AssertionError(texto);}
    private static void eq(Object esperado,Object actual,String texto) {check(esperado==null?actual==null:esperado.equals(actual),texto+" esperado="+esperado+" actual="+actual);}
    private static RequerimientoCompraPresupuesto leer(Estado e,int tipo)throws Exception {
        e.fila.put("tipo_documento",Integer.valueOf(tipo));
        return new BusquedaRequerimientoCompraServiceImpl().listarDocumentos(e.con,41,tipo).get(0);
    }
    public static void main(String[] args)throws Exception {
        probar("tipo1 resuelve maestro y refresca sin cache",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();RequerimientoCompraPresupuesto p=leer(e,1);
            eq("Maestro actual",p.getDescripcionPrestador(),"descripcion canonica");
            eq(Integer.valueOf(101),p.getIdPrestador(),"identidad proveedor");
            e.descripcion="Maestro cambiado";eq(e.descripcion,leer(e,1).getDescripcionPrestador(),"maestro releido");e.caller();
        }});
        probar("lista especifica presupuesto conserva metadatos y binding",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();RequerimientoCompraPresupuesto p=new BusquedaRequerimientoCompraServiceImpl().listarPresupuestos(e.con,41).get(0);
            eq("Maestro actual",p.getDescripcionPrestador(),"alias canonico");eq(Integer.valueOf(41),e.binds.get(Integer.valueOf(1)),"requerimiento");
            eq(Integer.valueOf(1),e.binds.get(Integer.valueOf(2)),"tipo1");metadatos(p);e.caller();
        }});
        probar("bloqueo no depende de rp estrella y mapea alias derivado",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();RequerimientoCompraPresupuesto p=new BusquedaRequerimientoCompraServiceImpl().bloquearDocumento(e.con,41,22);
            eq("Maestro actual",p.getDescripcionPrestador(),"rp estrella fisico ya no contiene descripcion");
            eq(Integer.valueOf(41),e.binds.get(Integer.valueOf(1)),"lock req");eq(Integer.valueOf(22),e.binds.get(Integer.valueOf(2)),"lock documento");
            check(e.sql.toLowerCase(Locale.ROOT).matches("(?s).*for\\s+update\\s+of\\s+rp\\b.*"),"LEFT JOIN debe bloquear solo rp");metadatos(p);e.caller();
        }});
        probar("maestro de baja permanece visible para documento historico",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.maestroDeBaja=true;eq(e.descripcion,leer(e,1).getDescripcionPrestador(),"baja logica no elimina identidad");e.caller();
        }});
        probar("maestro inexistente conserva documento e identidad con marcador",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.maestroExiste=false;RequerimientoCompraPresupuesto p=leer(e,1);
            eq(Integer.valueOf(22),p.getIdRequerimientoPresupuesto(),"fila conservada");eq(Integer.valueOf(101),p.getIdPrestador(),"id no resuelto conservado");
            eq("[PRESTADOR NO RESUELTO]",p.getDescripcionPrestador(),"marcador vigente");e.caller();
        }});
        probar("descripcion larga conserva texto raw y par surrogate",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.descripcion="abcdefghijklmnopqrstuvwxyzABCDE\uD83D\uDE00 texto completo mayor a treinta y dos";
            String actual=leer(e,1).getDescripcionPrestador();eq(e.descripcion,actual,"sin recorte ni uppercase nuevo");
            check(actual.indexOf("\uD83D\uDE00")>=0&&actual.endsWith("dos"),"par UTF16 completo");e.caller();
        }});
        probar("descripcion vacia no inventa copia ni fallback al ID",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.descripcion="";RequerimientoCompraPresupuesto p=leer(e,1);
            eq("",p.getDescripcionPrestador(),"raw vacio");eq(Integer.valueOf(101),p.getIdPrestador(),"ID disponible independientemente");e.caller();
        }});
        probar("descripcion null no reutiliza texto retirado",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.descripcion=null;eq(null,leer(e,1).getDescripcionPrestador(),"raw null");e.caller();
        }});
        probar("OM tipo2 sin prestador conserva receta fecha y DL",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.fila.put("id_prestador",null);e.fila.put("tipo_documento",Integer.valueOf(2));
            RequerimientoCompraPresupuesto p=new BusquedaRequerimientoCompraServiceImpl().listarOrdenesMedicas(e.con,41).get(0);
            eq(null,p.getIdPrestador(),"OM sin proveedor");eq(null,p.getDescripcionPrestador(),"OM sin copia");eq("RECETA-QA",p.getNumeroReceta(),"receta");
            eq(java.sql.Date.valueOf("2026-01-02"),p.getFechaDocumento(),"fecha");metadatos(p);e.caller();
        }});
        probar("Empresa libre tipo3 conserva descripcion propia sin prestador",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.fila.put("id_prestador",null);e.fila.put("descripcion_empresa","Empresa libre QA");e.fila.put("empresa_adjudicada",Boolean.TRUE);
            RequerimientoCompraPresupuesto p=leer(e,3);eq("Empresa libre QA",p.getDescripcionEmpresa(),"texto libre empresa");
            eq(null,p.getEmpresaCuit(),"sin CUIT");eq(null,p.getEmpresaSucursal(),"sin sucursal");eq(null,p.getDescripcionPrestador(),"sin copia proveedor");
            check(p.isEmpresaAdjudicada(),"marca adjudicacion");metadatos(p);e.caller();
        }});
        probar("bloqueo Empresa conserva mapper especifico",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.fila.put("tipo_documento",Integer.valueOf(3));e.fila.put("id_prestador",null);e.fila.put("descripcion_empresa","Empresa libre bloqueada");
            RequerimientoCompraPresupuesto p=new BusquedaRequerimientoCompraServiceImpl().bloquearDocumento(e.con,41,22);
            eq("Empresa libre bloqueada",p.getDescripcionEmpresa(),"map Empresa");eq(null,p.getDescripcionPrestador(),"alias sin ID");e.caller();
        }});
        probar("bloqueo Empresa identificada conserva snapshot raw aunque maestro cambie",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.fila.put("tipo_documento",Integer.valueOf(3));e.fila.put("id_prestador",null);
            e.fila.put("empresa_cuit","20123456786");e.fila.put("empresa_sucursal","0001");e.fila.put("descripcion_empresa","SNAPSHOT EMPRESA");
            RequerimientoCompraPresupuesto p=new BusquedaRequerimientoCompraServiceImpl().bloquearDocumento(e.con,41,22);
            eq("SNAPSHOT EMPRESA",p.getDescripcionEmpresa(),"bloqueador no sustituye snapshot por AFIP actual");e.caller();
        }});
        probar("SQLException se propaga y conserva conexion caller",new Caso(){public void ejecutar()throws Exception{
            Estado e=new Estado();e.fallarExecute=true;boolean error=false;
            try {leer(e,1);}catch(SQLException esperado){error="58000".equals(esperado.getSQLState());}
            check(error,"error de persistencia no ocultado");check(e.sentenciasCerradas==1,"statement cerrado tras fallo");e.caller();
        }});
        probar("formatter existente NFC ROOT CUIT0 y null sin nueva regla SQL",new Caso(){public void ejecutar()throws Exception{
            PrestadorCotizacion p=new PrestadorCotizacion();p.setDescripcion(" e\u0301 ");p.setCuit("0");eq("\u00c9 - 0",p.getEtiquetaVisible(),"NFC ROOT y cero literal");
            p.setDescripcion(null);eq("0",p.getEtiquetaVisible(),"fallback CUIT");p.setCuit(null);eq("",p.getEtiquetaVisible(),"sin null literal");
            eq("\u00e9",TextoCompraHelper.normalizarTexto(" e\u0301 "),"NFC y trim existentes");
            Locale anterior=Locale.getDefault();try {Locale.setDefault(new Locale("tr","TR"));p.setDescripcion("stra\u00dfe i");eq("STRASSE I",p.getEtiquetaVisible(),"Locale.ROOT independiente de default");}finally{Locale.setDefault(anterior);}
        }});
        check(ar.com.ospim.util.ConnectionHelper.aperturas==0,"no abre pool ni otra conexion");
        System.out.println("PRESUPUESTO_PRESTADOR_LECTURA casos="+casos+" checks="+checks+" fallos="+fallos+" columnas_ausentes="+columnasAusentes+"; JDBC simulado sin DB/UI");
        if(fallos>0)throw new Exception("Contrato de lectura sin copia incumplido: "+fallos);
    }
    private static void metadatos(RequerimientoCompraPresupuesto p) {
        eq(Long.valueOf(9012345678L),p.getDlGroupId(),"grupo long");eq(Long.valueOf(9012345679L),p.getDlFolderId(),"carpeta long");
        eq(Long.valueOf(9012345680L),p.getDlFileEntryId(),"file long");eq("uuid-qa",p.getDlFileUuid(),"uuid");
        eq("original.pdf",p.getNombreOriginal(),"nombre original");eq("persistido.pdf",p.getNombrePersistido(),"nombre persistido");eq("Titulo QA",p.getTitulo(),"titulo");
    }
    private static class Estado implements InvocationHandler {
        final LinkedHashMap<String,Object> fila=new LinkedHashMap<String,Object>();
        final Map<Integer,Object> binds=new HashMap<Integer,Object>();final Connection con;
        String sql,descripcion="Maestro actual";boolean maestroExiste=true,maestroDeBaja,fallarExecute,cerrada;int commits,rollbacks,sentenciasCerradas,filasCerradas;
        Estado(){
            String[] campos={"id_requerimiento_presupuesto","id_requerimiento","id_prestador","dl_group_id","dl_folder_id","dl_file_entry_id","dl_file_uuid","nombre_original","nombre_persistido","titulo","alta_fecha","alta_usr","baja_fecha","baja_usr","tipo_documento","fecha_documento","numero_receta","empresa_cuit","empresa_sucursal","descripcion_empresa","empresa_adjudicada"};
            for(int i=0;i<campos.length;i++)fila.put(campos[i],null);
            fila.put("id_requerimiento_presupuesto",Integer.valueOf(22));fila.put("id_requerimiento",Integer.valueOf(41));fila.put("id_prestador",Integer.valueOf(101));fila.put("tipo_documento",Integer.valueOf(1));
            fila.put("dl_group_id",Long.valueOf(9012345678L));fila.put("dl_folder_id",Long.valueOf(9012345679L));fila.put("dl_file_entry_id",Long.valueOf(9012345680L));fila.put("dl_file_uuid","uuid-qa");
            fila.put("nombre_original","original.pdf");fila.put("nombre_persistido","persistido.pdf");fila.put("titulo","Titulo QA");fila.put("alta_usr","qa");
            fila.put("fecha_documento",java.sql.Date.valueOf("2026-01-02"));fila.put("numero_receta","RECETA-QA");
            con=(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},this);
        }
        void caller(){check(!cerrada&&commits==0&&rollbacks==0,"conexion caller intacta");}
        public Object invoke(Object p,Method m,Object[] a)throws Throwable {
            String n=m.getName();if("prepareCall".equals(n))throw new SQLException("No SQL functions in offline boundary");
            if("prepareStatement".equals(n)){
                sql=(String)a[0];binds.clear();String q=sql.toLowerCase(Locale.ROOT);
                if(Pattern.compile("\\brp\\.descripcion_prestador\\b").matcher(q).find()){columnasAusentes++;throw new SQLException("column rp.descripcion_prestador does not exist","42703");}
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PreparedStatement.class},new InvocationHandler(){
                    public Object invoke(Object p,Method method,Object[] args)throws Throwable {
                        String name=method.getName();if(name.startsWith("set")&&args!=null&&args.length>1){binds.put((Integer)args[0],"setNull".equals(name)?null:args[1]);return null;}
                        if("close".equals(name)){sentenciasCerradas++;return null;}
                        if("executeQuery".equals(name)){
                            if(fallarExecute)throw new SQLException("persistencia sintetica","58000");
                            Map<String,Object> result=new LinkedHashMap<String,Object>(fila);
                            String q=sql.toLowerCase(Locale.ROOT);
                            // La estrella obtiene SOLO las21columnas fisicas. Un JOIN proyectado entrega alias22.
                            if(q.indexOf("join public.prestador")>=0&&q.indexOf("as descripcion_prestador")>=0){
                                boolean filtroMaestro=Pattern.compile("\\bp\\.baja_fecha\\s+is\\s+null\\b").matcher(q).find();
                                boolean presente=maestroExiste&&(!maestroDeBaja||!filtroMaestro);
                                result.put("descripcion_prestador",fila.get("id_prestador")==null?null:(presente?descripcion:"[PRESTADOR NO RESUELTO]"));
                            }
                            if(q.indexOf("join informacion_afip.empresa")>=0&&q.indexOf("e.razon_soc")>=0
                                    &&fila.get("empresa_cuit")!=null&&fila.get("empresa_sucursal")!=null){
                                result.put("descripcion_empresa","MAESTRO AFIP DISTINTO");
                            }
                            return filas(result,Estado.this);
                        }
                        return defecto(method.getReturnType());
                    }
                });
            }
            if("close".equals(n)){cerrada=true;return null;}if("isClosed".equals(n))return Boolean.valueOf(cerrada);
            if("commit".equals(n)){commits++;return null;}if("rollback".equals(n)){rollbacks++;return null;}return defecto(m.getReturnType());
        }
    }
    private static ResultSet filas(final Map<String,Object> fila,final Estado e) {
        return (ResultSet)Proxy.newProxyInstance(ComprasPresupuestoPrestadorLecturaTest.class.getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){
            boolean primero=true,nulo;
            public Object invoke(Object p,Method m,Object[] a)throws Throwable {
                String n=m.getName();if("next".equals(n)){boolean v=primero;primero=false;return Boolean.valueOf(v);}if("wasNull".equals(n))return Boolean.valueOf(nulo);
                if("close".equals(n)){e.filasCerradas++;return null;}
                if("findColumn".equals(n)){if(!fila.containsKey(a[0]))throw new SQLException("Alias no proyectado");return Integer.valueOf(1);}
                if(n.startsWith("get")&&a!=null&&a.length>0){Object value=fila.get(String.valueOf(a[0]));nulo=value==null;
                    if("getString".equals(n))return value==null?null:String.valueOf(value);if("getInt".equals(n))return Integer.valueOf(value==null?0:((Number)value).intValue());
                    if("getLong".equals(n))return Long.valueOf(value==null?0:((Number)value).longValue());if("getBoolean".equals(n))return Boolean.valueOf(Boolean.TRUE.equals(value));return value;}
                return defecto(m.getReturnType());
            }
        });
    }
    private static Object defecto(Class c){if(c==Boolean.TYPE)return Boolean.FALSE;if(c==Integer.TYPE)return Integer.valueOf(0);if(c==Long.TYPE)return Long.valueOf(0);return null;}
}
