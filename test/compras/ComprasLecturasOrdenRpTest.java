import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.service.*;

/* Las fuentes reales consultan una frontera JDBC controlada; ningun dato QA se modifica. */
public class ComprasLecturasOrdenRpTest {
    private static int checks;
    private static void ok(boolean valor,String caso) { checks++;if(!valor)throw new AssertionError(caso); }
    private static Map<String,Object> fila(Object... valores) { Map<String,Object> m=new HashMap<String,Object>();for(int i=0;i<valores.length;i+=2)m.put((String)valores[i],valores[i+1]);return m; }
    private static Object vacio(Class tipo) { if(tipo==Boolean.TYPE)return Boolean.FALSE;if(tipo==Integer.TYPE)return Integer.valueOf(0);if(tipo==Long.TYPE)return Long.valueOf(0);return null; }
    private static class Datos implements InvocationHandler {
        String nombre="Prestador vigente",codigo="VIGENTE",descripcion="Prestacion vigente";
        boolean sinMaestro,sinProveedor,empresa,empresaLibre,sinOrden,dosFechas,falla;
        String tipo="NOMENCLADOR";
        final List<String> consultas=new ArrayList<String>();
        final List<Map<Integer,Object>> parametros=new ArrayList<Map<Integer,Object>>();
        Connection conexion() { return (Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},this); }
        public Object invoke(Object proxy,Method metodo,Object[] args)throws Throwable {
            String nombreMetodo=metodo.getName();
            if(nombreMetodo.equals("prepareCall"))throw new SQLException("Funcion propia de lectura todavia requerida");
            if(nombreMetodo.equals("prepareStatement")) {
                final String sql=(String)args[0];consultas.add(sql);
                if(sql.indexOf("_consulta")>=0)throw new SQLException("Vista propia de lectura todavia requerida");
                final Map<Integer,Object> p=new HashMap<Integer,Object>();parametros.add(p);
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PreparedStatement.class},new InvocationHandler(){public Object invoke(Object x,Method m,Object[] a)throws Throwable{
                    if(m.getName().startsWith("set")&&a!=null&&a.length>=2)p.put((Integer)a[0],a[1]);
                    if(m.getName().equals("executeUpdate")||m.getName().equals("execute"))throw new SQLException("Una lectura intento escribir");
                    if(m.getName().equals("executeQuery"))return resultado(sql);
                    return vacio(m.getReturnType());
                }});
            }
            return vacio(metodo.getReturnType());
        }
        ResultSet resultado(String sql)throws Exception {
            if(falla)throw new SQLException("Fallo JDBC sintetico");
            List<Map<String,Object>> filas=new ArrayList<Map<String,Object>>();
            if(sql.indexOf("FROM compras.requerimiento_orden_compra o ")>=0) {
                if(!sinOrden)filas.add(fila("id_orden_compra",Integer.valueOf(90),"id_requerimiento",Integer.valueOf(42),"fecha_emision",java.sql.Date.valueOf("2026-11-02"),"alta_usr","auditor","solicitante","solicitante","version_edicion",Integer.valueOf(3),"tipo_documento",Integer.valueOf(empresa?3:1),"id_prestador",empresa?null:Integer.valueOf(33),"prestador_id_actual",sinProveedor?null:Integer.valueOf(33),"prestador_descripcion_actual",nombre,"prestador_cuit_actual","30000000001","empresa_cuit",empresaLibre?null:"30000000002","empresa_sucursal",empresaLibre?null:"1","empresa_cuit_actual",sinProveedor?null:"30000000002","empresa_razon_actual",nombre,"descripcion_empresa","Texto libre","iva",BigDecimal.ZERO,"iibb",BigDecimal.ZERO));
            } else if(sql.indexOf("FROM compras.requerimiento_orden_compra_detalle od ")>=0) {
                filas.add(fila("id_requerimiento_detalle",Integer.valueOf(7),"cantidad",Integer.valueOf(2),"precio_unitario",new BigDecimal("12.345"),"tipo_item",tipo,"id_prestacion",Integer.valueOf(501),"nomenclador_id_actual",sinMaestro?null:Integer.valueOf(501),"nomenclador_codigo_actual",codigo,"nomenclador_descripcion_actual",descripcion,"medicamento_id_actual",sinMaestro?null:Integer.valueOf(801),"medicamento_troquel_actual",new BigDecimal("789"),"medicamento_nombre_actual","Medicamento vigente","medicamento_presentacion_actual","20 comprimidos","observaciones","Aclaracion"));
            } else if(sql.indexOf("SELECT fecha_documento FROM compras.requerimiento_presupuesto")>=0) {
                filas.add(fila("fecha_documento",java.sql.Date.valueOf("2026-11-01")));
                filas.add(fila("fecha_documento",java.sql.Date.valueOf(dosFechas?"2026-11-02":"2026-11-01")));
            } else if(sql.indexOf("FROM compras.requerimiento_reclamo_prestacional relacion")>=0) {
                filas.add(fila("id_requerimiento",Integer.valueOf(42),"id_reclamo_prestacional",null,"estado","RESERVADO","token_reserva","token","alta_usr","auditor"));
            } else throw new SQLException("Consulta inesperada: "+sql);
            return rs(filas);
        }
    }
    private static ResultSet rs(final List<Map<String,Object>> filas) { return (ResultSet)Proxy.newProxyInstance(ComprasLecturasOrdenRpTest.class.getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){int pos=-1;boolean nulo;public Object invoke(Object p,Method m,Object[] a){String n=m.getName();if(n.equals("next"))return Boolean.valueOf(++pos<filas.size());if(n.equals("wasNull"))return Boolean.valueOf(nulo);if(n.startsWith("get")){Object v=filas.get(pos).get(String.valueOf(a[0]));nulo=v==null;if(n.equals("getInt"))return Integer.valueOf(v==null?0:((Number)v).intValue());if(n.equals("getString"))return v==null?null:v.toString();return v;}return vacio(m.getReturnType());}}); }
    private static RequerimientoCompraOrden orden(Datos d)throws Exception {
        Method m=RequerimientoCompraOrdenServiceImpl.class.getDeclaredMethod("obtenerPorRequerimiento",new Class[]{Connection.class,Integer.TYPE});m.setAccessible(true);
        try{return (RequerimientoCompraOrden)m.invoke(new RequerimientoCompraOrdenServiceImpl(),new Object[]{d.conexion(),Integer.valueOf(42)});}catch(InvocationTargetException e){throw (Exception)e.getCause();}
    }
    public static void main(String[] args)throws Exception {
        Datos d=new Datos();RequerimientoCompraOrden o=orden(d);
        ok("Prestador vigente".equals(o.getProveedor()),"Proveedor actual sin snapshot");
        ok("VIGENTE".equals(o.getDetalles().get(0).getCodigoItemVisible()),"Codigo actual");
        ok(o.getDetalles().get(0).getDescripcionItemVisible().indexOf("Prestacion vigente")>=0,"Descripcion actual");
        ok(new BigDecimal("24.69").equals(o.getDetalles().get(0).getPrecioTotalEstimado()),"Total conserva cantidad/precio OC y redondeo");
        ok(java.sql.Date.valueOf("2026-11-01").equals(o.getFechaOrdenMedica()),"Fecha unica OM");
        d.nombre="Cambio maestro";d.codigo="NUEVO";d.descripcion="Cambio prestacion";o=orden(d);
        ok("Cambio maestro".equals(o.getProveedor())&&"NUEVO".equals(o.getDetalles().get(0).getCodigoItemVisible()),"Segunda lectura sin cache");
        d.nombre="";d.codigo="";d.descripcion="";o=orden(d);ok(o.getProveedor()==null||o.getProveedor().length()==0,"Maestro vacio prevalece");
        d.sinMaestro=true;d.sinProveedor=true;o=orden(d);ok(o.getProveedor().indexOf("NO RESUELTO")>=0,"Proveedor ausente explicito");ok(o.getDetalles().get(0).getDescripcionItemVisible().indexOf("NO RESUELTO")>=0,"Canonico ausente explicito");
        d=new Datos();d.tipo="MEDICAMENTO";o=orden(d);ok("789".equals(o.getDetalles().get(0).getCodigoItemVisible()),"Troquel actual");ok(o.getDetalles().get(0).getDescripcionItemVisible().indexOf("Medicamento vigente 20 comprimidos")>=0,"Medicamento actual completo");
        d=new Datos();d.empresa=true;o=orden(d);ok("Prestador vigente".equals(o.getProveedor()),"Empresa desde maestro");d.empresaLibre=true;o=orden(d);ok("Texto libre".equals(o.getProveedor()),"Empresa sin referencia conserva texto libre");
        d=new Datos();d.dosFechas=true;o=orden(d);ok(o.getFechaOrdenMedica()==null,"OM ambiguas no inventan fecha");
        d=new Datos();d.sinOrden=true;ok(orden(d)==null,"OC ausente sigue ausente");
        d=new Datos();d.falla=true;boolean fallo=false;try{orden(d);}catch(SQLException e){fallo=true;}ok(fallo,"Error JDBC se propaga");
        d=new Datos();ar.com.ospim.util.ConnectionHelper.lectura=d.conexion();RequerimientoCompraReclamoPrestacionalServiceImpl rp=new RequerimientoCompraReclamoPrestacionalServiceImpl();
        RequerimientoCompraReclamoPrestacional r=rp.obtenerPorRequerimiento(d.conexion(),42);ok(r.getIdReclamoPrestacional()==null,"RP null conserva null");
        ok(rp.listarPorReclamoPrestacional(8,"VINCULADO").size()==1,"Lista RP directa");
        ok(rp.listarVinculadasPorRequerimientos(null,Arrays.asList(new Integer[]{Integer.valueOf(42)})).size()==1,"Batch RP directo");
        ok("{42}".equals(d.parametros.get(2).get(Integer.valueOf(2))),"Array parametrizado conserva contrato");
        for(String sql:d.consultas)ok(sql.indexOf("{call")<0&&sql.indexOf("_consulta")<0,"Sin funciones/vistas propias de lectura");
        System.out.println("COMPRAS_LECTURAS_OC_RP_OK checks="+checks);
    }
}
