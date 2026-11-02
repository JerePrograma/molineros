import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper;
import ar.com.ospim.compras.requerimientos.service.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/** Lectores, Helper y UPDATE reales; la frontera JDBC separa maestro y evento. */
public class ComprasDetalleSnapshotEdicionTest {
    private static int checks,fallos;
    private static Field buscar,editar;
    public static void main(String[] args)throws Exception{
        buscar=BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        editar=EditarRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        buscar.setAccessible(true);editar.setAccessible(true);
        Object anteriorBuscar=buscar.get(null),anteriorEditar=editar.get(null);
        try{
            probar(false,false,false);
            probar(true,false,false);
            probar(true,true,false);
            probar(false,false,true);
            check(ar.com.ospim.util.ConnectionHelper.aperturas==0,"Sin apertura de otra conexion en el guardado");
            System.out.println("COMPRAS_DETALLE_SNAPSHOT_CHECKS="+checks+" FALLOS="+fallos+"; JDBC simulado sin DB ni UI");
            if(fallos>0)throw new Exception("Se reescribio el evento persistido desde el maestro: "+fallos+" comprobaciones fallidas.");
        }finally{buscar.set(null,anteriorBuscar);editar.set(null,anteriorEditar);}
    }
    private static void probar(boolean medicamento,boolean maestroAusente,boolean tipoDistinto)throws Exception{
        Estado e=new Estado(medicamento,maestroAusente,tipoDistinto);Lecturas lecturas=new Lecturas(e);
        buscar.set(null,lecturas);editar.set(null,new Escrituras(e));
        RequerimientoCompraDetalle visible=lecturas.getDetalles(e.con,1388).get(0);
        if(medicamento){
            eq(maestroAusente?null:"MEDICAMENTO VIGENTE",visible.getNombreMedicamento(),"Pantalla usa nombre actual de medicamento sin recuperar snapshot");
            eq(maestroAusente?null:Integer.valueOf(999),visible.getTroquel(),"Pantalla usa troquel actual");
        }else{
            eq("CODIGO-VIGENTE",visible.getCodigoNomenclador(),"Pantalla usa codigo maestro actual");
            eq("DESCRIPCION VIGENTE",visible.getDescripcionNomenclador(),"Pantalla usa descripcion maestra actual");
        }
        RequerimientoCompraDetalle entrada=new RequerimientoCompraDetalle();entrada.setId(Integer.valueOf(445));
        entrada.setIdRequerimientoCompra(1388);entrada.setTipoItem(medicamento?"MEDICAMENTO":"NOMENCLADOR");
        entrada.setIdTipoPrestacion(Integer.valueOf(8));entrada.setCantidad(Integer.valueOf(2));
        entrada.setIdPrestacion(medicamento?null:Integer.valueOf(10415));
        entrada.setIdTipoNomenclador(medicamento?null:Integer.valueOf(tipoDistinto?16:15));
        entrada.setCodigoNomenclador(medicamento?null:"HTTP-FALSO");
        entrada.setDescripcionNomenclador(medicamento?null:"HTTP FALSO");
        entrada.setIdMedicamento(medicamento?Integer.valueOf(123):null);
        entrada.setTroquel(medicamento?Integer.valueOf(777):null);entrada.setNombreMedicamento(medicamento?"HTTP FALSO":null);
        boolean rechazo=false;try{new EditarRequerimientoCompraHelper().guardarDetalle(entrada,"prueba");}
        catch(Exception fallo){rechazo=true;System.out.println("OPERACION_RECHAZADA "+fallo.getMessage());}
        if(tipoDistinto){
            check(rechazo&&e.updates==0&&e.commits==0&&e.rollbacks==1,
                "Tipo maestro distinto del evento conserva rechazo legacy sin UPDATE");return;
        }
        check(!rechazo&&e.updates==1&&e.commits==1&&e.rollbacks==0,
            "Editar cantidad misma identidad confirma TX "+(medicamento?"MEDICAMENTO":"NOMENCLADOR")+(maestroAusente?" maestro ausente":""));
        eq(Integer.valueOf(2),e.evento.get("cantidad"),"Cantidad nueva se persiste");
        if(medicamento){
            eq(Integer.valueOf(123),e.evento.get("id_medicamento"),"Identidad de medicamento historico conservada");
            eq(Integer.valueOf(222),e.evento.get("troquel"),"UPDATE conserva troquel original del evento");
            eq("MEDICAMENTO HISTORICO",e.evento.get("nombre_medicamento"),"UPDATE conserva nombre original del evento");
        }else{
            eq(Integer.valueOf(10415),e.evento.get("id_prestacion"),"Identidad nomenclador conservada");
            eq(Integer.valueOf(15),e.evento.get("id_tipo_nomenclador"),"Tipo original conservado");
            eq("CODIGO-HISTORICO",e.evento.get("codigo_nomenclador"),"UPDATE conserva codigo original del evento");
            eq("DESCRIPCION HISTORICA",e.evento.get("descripcion_nomenclador"),"UPDATE conserva descripcion original del evento");
        }
        check(e.eventos.indexOf("LOCK_CABECERA")>=0&&e.eventos.indexOf("LOCK_CABECERA")<e.eventos.indexOf("UPDATE")&&e.cerrada,
            "Se preserva bloqueo antes de UPDATE y cierre de TX");
        e.nuevaConexion();visible=lecturas.getDetalles(e.con,1388).get(0);
        eq(medicamento?(maestroAusente?null:"MEDICAMENTO VIGENTE"):"DESCRIPCION VIGENTE",
            medicamento?visible.getNombreMedicamento():visible.getDescripcionNomenclador(),
            "Lectura de pantalla posterior conserva el maestro actual");
    }
    private static void eq(Object a,Object b,String mensaje){check(a==null?b==null:a.equals(b),mensaje+"; esperado="+a+" recibido="+b);}
    private static void check(boolean ok,String mensaje){checks++;if(!ok)fallos++;System.out.println((ok?"OK ":"FALLO ")+mensaje);}
    private static class Lecturas extends ConfiguracionComprasDatosFixture{
        Estado e;Lecturas(Estado estado){e=estado;}
        public Nomenclador obtenerNomencladorCanonico(Connection con,int id)throws Exception{
            if(con!=e.con)throw new SQLException("Otra conexion para maestro");
            Nomenclador n=new Nomenclador();n.setId_prestacion(10415);n.setId_tipo_nomenclador(e.tipoDistinto?16:15);
            n.setCodigo("CODIGO-VIGENTE");n.setDescripcion("DESCRIPCION VIGENTE");return n;
        }
        public List<TipoPrestacionCompra> listarTiposPrestacion(Connection con)throws Exception{
            if(con!=e.con)throw new SQLException("Otra conexion para catalogo");
            TipoPrestacionCompra t=new TipoPrestacionCompra();t.setId(Integer.valueOf(8));t.setIdSector(Integer.valueOf(2));t.setDescripcion("DIABETES");
            List<TipoPrestacionCompra> l=new ArrayList<TipoPrestacionCompra>();l.add(t);return l;
        }
    }
    private static class Escrituras extends EditarRequerimientoCompraServiceImpl{
        Estado e;Escrituras(Estado estado){e=estado;}
        public Transaccion abrirTransaccion()throws Exception{
            Constructor<Transaccion> c=Transaccion.class.getDeclaredConstructor(EditarRequerimientoCompraServiceImpl.class,Connection.class);
            c.setAccessible(true);return c.newInstance(this,e.con);
        }
    }
    private static class Estado implements InvocationHandler{
        Connection con;Map<String,Object> evento=new HashMap<String,Object>(),pendiente;
        boolean medicamento,maestroAusente,tipoDistinto,cerrada;int updates,commits,rollbacks;
        List<String> eventos=new ArrayList<String>();
        Estado(boolean med,boolean ausente,boolean distinto){
            medicamento=med;maestroAusente=ausente;tipoDistinto=distinto;
            evento.put("id",Integer.valueOf(445));evento.put("id_detalle",Integer.valueOf(445));evento.put("id_requerimiento",Integer.valueOf(1388));
            evento.put("tipo_item",med?"MEDICAMENTO":"NOMENCLADOR");evento.put("id_tipo_prestacion",Integer.valueOf(8));
            evento.put("cantidad",Integer.valueOf(1));evento.put("precio_unitario_estimado",new BigDecimal("10.00"));
            evento.put("precio_total_estimado",new BigDecimal("77.00"));evento.put("observaciones","EVENTO");
            evento.put("id_prestacion",med?null:Integer.valueOf(10415));evento.put("id_tipo_nomenclador",med?null:Integer.valueOf(15));
            evento.put("codigo_nomenclador",med?null:"CODIGO-HISTORICO");evento.put("descripcion_nomenclador",med?null:"DESCRIPCION HISTORICA");
            evento.put("id_medicamento",med?Integer.valueOf(123):null);evento.put("troquel",med?Integer.valueOf(222):null);
            evento.put("nombre_medicamento",med?"MEDICAMENTO HISTORICO":null);nuevaConexion();
        }
        void nuevaConexion(){cerrada=false;con=(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},this);}
        public Object invoke(Object p,Method m,Object[] args)throws Throwable{
            String n=m.getName();if("prepareStatement".equals(n)||"prepareCall".equals(n)){
                String sql=(String)args[0];
                if("prepareCall".equals(n)&&sql.indexOf("compras.bloquear_requerimiento_reclamo_prestacional")<0)
                    throw new SQLException("No se admiten funciones de lectura en el fixture");
                if(sql.indexOf("FROM compras.requerimiento ")>=0&&sql.indexOf("FOR UPDATE")>=0)eventos.add("LOCK_CABECERA");
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{"prepareCall".equals(n)?CallableStatement.class:PreparedStatement.class},new Sentencia(this,sql));
            }
            if("commit".equals(n)){commits++;if(pendiente!=null)evento=pendiente;pendiente=null;return null;}
            if("rollback".equals(n)){rollbacks++;pendiente=null;return null;}
            if("close".equals(n)){cerrada=true;return null;}if("isClosed".equals(n))return Boolean.valueOf(cerrada);
            return defecto(m.getReturnType());
        }
    }
    private static class Sentencia implements InvocationHandler{
        Estado e;String sql;Map<Integer,Object> parametros=new HashMap<Integer,Object>();
        Sentencia(Estado estado,String consulta){e=estado;sql=consulta;}
        public Object invoke(Object p,Method m,Object[] args)throws Throwable{
            String n=m.getName();if(n.startsWith("set")&&args!=null&&args.length>1){parametros.put((Integer)args[0],"setNull".equals(n)?null:args[1]);return null;}
            if("execute".equals(n))return Boolean.TRUE;
            if("executeQuery".equals(n)){
                List<Map<String,Object>> filas=new ArrayList<Map<String,Object>>();String consulta=sql.toLowerCase();
                if(consulta.startsWith("update compras.requerimiento_detalle set")){
                    e.updates++;e.eventos.add("UPDATE");e.pendiente=new HashMap<String,Object>(e.evento);
                    String[] campos={"id_requerimiento","tipo_item","id_tipo_prestacion","id_prestacion","id_tipo_nomenclador","codigo_nomenclador","descripcion_nomenclador","id_medicamento","troquel","nombre_medicamento","cantidad","observaciones"};
                    for(int i=0;i<campos.length;i++)e.pendiente.put(campos[i],parametros.get(Integer.valueOf(i+1)));
                    Map<String,Object> f=new HashMap<String,Object>();f.put("1",Integer.valueOf(445));filas.add(f);
                }else if(consulta.indexOf("for update")<0&&consulta.indexOf("from compras.requerimiento_detalle")>=0){
                    Map<String,Object> f=new HashMap<String,Object>(e.evento);
                    String columnas=consulta.substring(0,consulta.indexOf("from compras.requerimiento_detalle"));
                    if(columnas.indexOf("n.codigo")>=0){f.put("codigo_nomenclador","CODIGO-VIGENTE");f.put("descripcion_nomenclador","DESCRIPCION VIGENTE");f.put("id_tipo_nomenclador",Integer.valueOf(e.tipoDistinto?16:15));}
                    if(e.medicamento&&columnas.indexOf("m.troquel")>=0){f.put("troquel",e.maestroAusente?null:Integer.valueOf(999));f.put("nombre_medicamento",e.maestroAusente?null:"MEDICAMENTO VIGENTE");}
                    if(e.medicamento){f.put("codigo_nomenclador",null);f.put("descripcion_nomenclador",null);f.put("id_tipo_nomenclador",null);}
                    f.put("codigo_item",e.medicamento?f.get("troquel"):f.get("codigo_nomenclador"));
                    f.put("descripcion_item",e.medicamento?f.get("nombre_medicamento"):f.get("descripcion_nomenclador"));filas.add(f);
                }else if(consulta.indexOf("for update")<0&&consulta.indexOf("from compras.requerimiento r")>=0){
                    Map<String,Object> f=new HashMap<String,Object>();f.put("id",Integer.valueOf(1388));f.put("id_requerimiento",Integer.valueOf(1388));
                    f.put("id_estado",Integer.valueOf(1));f.put("estado",Integer.valueOf(1));f.put("id_sector",Integer.valueOf(2));f.put("sector_descripcion","DIABETES");filas.add(f);
                }
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ResultSet.class},new Filas(filas));
            }
            return defecto(m.getReturnType());
        }
    }
    private static class Filas implements InvocationHandler{
        List<Map<String,Object>> filas;int i=-1;boolean nulo;
        Filas(List<Map<String,Object>> f){filas=f;}
        public Object invoke(Object p,Method m,Object[] a)throws Throwable{
            String n=m.getName();if("next".equals(n))return Boolean.valueOf(++i<filas.size());
            if("findColumn".equals(n)){if(!filas.get(i).containsKey((String)a[0]))throw new SQLException("Columna ausente");return Integer.valueOf(1);}
            if("wasNull".equals(n))return Boolean.valueOf(nulo);
            if(n.startsWith("get")&&a!=null&&a.length>0){Object v=filas.get(i).get(String.valueOf(a[0]));nulo=v==null;
                if("getInt".equals(n))return Integer.valueOf(v==null?0:((Number)v).intValue());if("getLong".equals(n))return Long.valueOf(v==null?0:((Number)v).longValue());
                if("getBoolean".equals(n))return Boolean.valueOf(Boolean.TRUE.equals(v));if("getString".equals(n))return v==null?null:String.valueOf(v);return v;}
            return defecto(m.getReturnType());
        }
    }
    private static Object defecto(Class c){if(c==Boolean.TYPE)return Boolean.FALSE;if(c==Integer.TYPE)return Integer.valueOf(0);if(c==Long.TYPE)return Long.valueOf(0);return null;}
}