import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import java.math.BigDecimal;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSeguimiento;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenSeguimientoHelper;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenSeguimientoServiceImpl;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceImpl;
import ar.com.ospim.util.ConnectionHelper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Persistencia, versiones, aislamiento y carreras reales solo sobre fixture validado. */
public class ComprasOrdenSeguimientoJdbcTest {
    private static int checks;
    public static void main(String[] args)throws Exception{
        if(args.length!=1||!args[0].matches("^compras_orden_test_edicion_[a-z0-9_]+$"))throw new IllegalArgumentException("Solo base aislada de pruebas de OC.");
        int port=Integer.parseInt(System.getProperty("compras.test.port","55451"));if(port<=0||port>65535)throw new IllegalArgumentException("Puerto invalido.");
        ComboPooledDataSource pool=new ComboPooledDataSource();Field field=ConnectionHelper.class.getDeclaredField("datasource");field.setAccessible(true);Object anterior=field.get(null);boolean instalado=false;
        try{pool.setDriverClass("org.postgresql.Driver");pool.setJdbcUrl("jdbc:postgresql://127.0.0.1:"+port+"/"+args[0]);pool.setUser("postgres");pool.setPassword("");pool.setMinPoolSize(1);pool.setInitialPoolSize(1);pool.setMaxPoolSize(4);pool.setCheckoutTimeout(15000);pool.setAcquireRetryAttempts(1);field.set(null,pool);instalado=true;
            comprobar("sin seguimiento inicial",filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_seguimiento s").isEmpty());
            fechaEntregaJdbc();ejecutar(pool);
            System.out.println("COMPRAS_ORDEN_SEGUIMIENTO_JDBC_OK checks="+checks);
            System.out.println("Fecha entrega ServiceImpl real; seguimiento manual, auditoria, aislamiento, rollback, versiones y dos carreras reales. Solo fixture sintetico.");
        }finally{try{if(instalado)field.set(null,anterior);}finally{pool.close();}}
    }
    private static void fechaEntregaJdbc()throws Exception{
        RequerimientoCompraOrdenServiceImpl service=new RequerimientoCompraOrdenServiceImpl();
        RequerimientoCompraOrden historica=service.obtenerPorRequerimiento(1001);
        comprobar("OC historica sin fecha inventada",historica!=null&&historica.getFechaEstimadaEntrega()==null);
        RequerimientoCompraOrden nueva=new RequerimientoCompraOrden();nueva.setIdRequerimientoCompra(54);
        nueva.setFechaEmision(new java.util.Date());nueva.setFechaEstimadaEntrega(new SimpleDateFormat("dd/MM/yyyy").parse("21/11/2026"));
        RequerimientoCompraDetalle detalle=new RequerimientoCompraDetalle(Integer.valueOf(2054));detalle.setIdRequerimiento(Integer.valueOf(54));detalle.setCantidad(Integer.valueOf(1));detalle.setPrecioUnitarioEstimado(new BigDecimal("10.00"));nueva.getDetalles().add(detalle);
        RequerimientoCompraOrdenServiceImpl.Transaccion tx=service.abrirTransaccion();
        try{
            tx.insertar(nueva,54,"fecha_jdbc_fixture");
            RequerimientoCompraOrden leida=tx.obtenerOrden(54);
            comprobar("INSERT OC con fecha",leida!=null&&leida.getIdOrdenCompra()>0);
            igual("fecha estimada roundtrip INSERT SQL/JDBC","21/11/2026",new SimpleDateFormat("dd/MM/yyyy").format(leida.getFechaEstimadaEntrega()));
        }finally{try{tx.rollback();}finally{tx.cerrar();}}
        comprobar("rollback prueba fecha sin OC extra",service.obtenerPorRequerimiento(54)==null);
    }
    private static void ejecutar(ComboPooledDataSource pool)throws Exception{
        RequerimientoCompraOrdenServiceImpl ocService=new RequerimientoCompraOrdenServiceImpl();int oc1=ocService.obtenerPorRequerimiento(1001).getIdOrdenCompra(),oc2=ocService.obtenerPorRequerimiento(1002).getIdOrdenCompra();
        List<String>cabeceras=filas(pool,"SELECT row_to_json(o)::text FROM compras.requerimiento_orden_compra o ORDER BY id_orden_compra");
        List<String>detalles=filas(pool,"SELECT row_to_json(d)::text FROM compras.requerimiento_orden_compra_detalle d ORDER BY id_orden_compra,orden");
        List<String>requerimientos=filas(pool,"SELECT row_to_json(r)::text FROM compras.requerimiento r ORDER BY id_requerimiento");
        List<String>suministro=filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_suministro s ORDER BY id_orden_compra");
        RequerimientoCompraOrdenSeguimientoHelper helper=new RequerimientoCompraOrdenSeguimientoHelper();RequerimientoCompraOrdenSeguimientoServiceImpl service=new RequerimientoCompraOrdenSeguimientoServiceImpl();
        Map<String,String> e=entrada("Primera gestion");RequerimientoCompraOrdenSeguimiento guardado=helper.guardar(1001,oc1,0,e,"alta_manual");
        RequerimientoCompraOrdenSeguimiento leido=new RequerimientoCompraOrdenSeguimientoServiceImpl().obtener(oc1);
        comprobar("primera fila creada",guardado!=null&&leido!=null);igual("version inicial",Integer.valueOf(1),Integer.valueOf(leido.getVersionEdicion()));
        igual("calificacion roundtrip","APTO",leido.getCalificacionProveedor());igual("observaciones roundtrip","Primera gestion",leido.getObservacionesProveedor());igual("factura roundtrip","SI",leido.getFacturaIngresada());igual("numero roundtrip","A-0001-123",leido.getNumeroFactura());
        igual("fecha factura roundtrip","06/11/2026",new SimpleDateFormat("dd/MM/yyyy").format(leido.getFechaFactura()));igual("liquidacion roundtrip","LIQUIDADA",leido.getEstadoLiquidacion());comprobar("auditada roundtrip",leido.isLiquidacionAuditada());igual("pago roundtrip","PAGADO",leido.getEstadoPago());igual("ID manual liquidacion",Integer.valueOf(123),leido.getIdLiquidacion());igual("ID manual OP",Integer.valueOf(456),leido.getIdOrdenPago());
        igual("usuario alta","alta_manual",leido.getAltaUsr());comprobar("fecha alta",leido.getAltaFecha()!=null);comprobar("sin modificacion inicial",leido.getModiUsr()==null&&leido.getModiFecha()==null);java.util.Date alta=leido.getAltaFecha();
        leido=helper.guardar(1001,oc1,1,new HashMap<String,String>(),"editor_manual");
        igual("version edicion",Integer.valueOf(2),Integer.valueOf(leido.getVersionEdicion()));comprobar("opcionales se pueden vaciar",vacio(leido.getCalificacionProveedor())&&vacio(leido.getObservacionesProveedor())&&vacio(leido.getFacturaIngresada())&&vacio(leido.getNumeroFactura())&&leido.getFechaFactura()==null&&vacio(leido.getEstadoLiquidacion())&&!leido.isLiquidacionAuditada()&&vacio(leido.getEstadoPago())&&leido.getIdLiquidacion()==null&&leido.getIdOrdenPago()==null);
        igual("alta conservada",alta,leido.getAltaFecha());igual("actor alta conservado","alta_manual",leido.getAltaUsr());igual("actor modificacion","editor_manual",leido.getModiUsr());comprobar("fecha modificacion",leido.getModiFecha()!=null);
        List<String>antes=filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_seguimiento s ORDER BY id_orden_compra");
        rechazo(helper,1001,oc1,1);igual("version obsoleta no sobrescribe",antes,filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_seguimiento s ORDER BY id_orden_compra"));
        rechazo(helper,1001,oc2,0);igual("OC ajena no sobrescribe",antes,filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_seguimiento s ORDER BY id_orden_compra"));
        rechazo(helper,53,oc1,0);
        RequerimientoCompraOrdenSeguimientoServiceImpl.Transaccion tx=service.abrirTransaccion();boolean fallo=false;
        try{leido.setEstadoLiquidacion("PENDIENTE");leido.setLiquidacionAuditada(true);tx.actualizar(leido,"invalido_sql");tx.commit();}catch(SQLException invalido){fallo=true;tx.rollback();}finally{tx.cerrar();}
        comprobar("SQL auditar solo LIQUIDADA",fallo);igual("rollback no altera version ni fila",antes,filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_seguimiento s ORDER BY id_orden_compra"));
        concurrentes(helper,1002,oc2,0);igual("otra OC no altera primera",antes.get(0),filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_seguimiento s WHERE id_orden_compra="+oc1).get(0));
        concurrentes(helper,1001,oc1,2);igual("dos OCs dos seguimientos",Integer.valueOf(2),Integer.valueOf(filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_seguimiento s ORDER BY id_orden_compra").size()));
        igual("cabeceras sin cambios del seguimiento",cabeceras,filas(pool,"SELECT row_to_json(o)::text FROM compras.requerimiento_orden_compra o ORDER BY id_orden_compra"));igual("detalles sin cambios",detalles,filas(pool,"SELECT row_to_json(d)::text FROM compras.requerimiento_orden_compra_detalle d ORDER BY id_orden_compra,orden"));igual("requerimientos sin cambios",requerimientos,filas(pool,"SELECT row_to_json(r)::text FROM compras.requerimiento r ORDER BY id_requerimiento"));igual("encuesta sin cambios",suministro,filas(pool,"SELECT row_to_json(s)::text FROM compras.requerimiento_orden_compra_suministro s ORDER BY id_orden_compra"));
    }
    private static void rechazo(RequerimientoCompraOrdenSeguimientoHelper helper,int req,int oc,int version)throws Exception{boolean fallo=false;try{helper.guardar(req,oc,version,entrada("No guardar"),"obsoleto");}catch(IllegalArgumentException esperado){fallo=true;}comprobar("identidad/estado/version rechazada",fallo);}
    private static void concurrentes(RequerimientoCompraOrdenSeguimientoHelper helper,int req,int oc,int version)throws Exception{
        CountDownLatch listos=new CountDownLatch(2),inicio=new CountDownLatch(1),fin=new CountDownLatch(2);Editor uno=new Editor(helper,req,oc,version,"uno",listos,inicio,fin),dos=new Editor(helper,req,oc,version,"dos",listos,inicio,fin);uno.start();dos.start();boolean preparados=listos.await(10,TimeUnit.SECONDS);inicio.countDown();if(!preparados||!fin.await(30,TimeUnit.SECONDS))throw new AssertionError("Timeout carrera seguimiento.");
        igual("un ganador",Integer.valueOf(1),Integer.valueOf((uno.resultado==null?0:1)+(dos.resultado==null?0:1)));Editor ganador=uno.resultado!=null?uno:dos,perdedor=uno.resultado==null?uno:dos;comprobar("perdedor version",perdedor.error instanceof IllegalArgumentException);comprobar("ganador sin error",ganador.error==null);RequerimientoCompraOrdenSeguimiento actual=new RequerimientoCompraOrdenSeguimientoServiceImpl().obtener(oc);igual("version incrementa una vez",Integer.valueOf(version+1),Integer.valueOf(actual.getVersionEdicion()));igual("contenido ganador",ganador.usuario,actual.getObservacionesProveedor());igual("auditoria ganador",ganador.usuario,version==0?actual.getAltaUsr():actual.getModiUsr());
    }
    private static class Editor extends Thread{final RequerimientoCompraOrdenSeguimientoHelper helper;final int req,oc,version;final String usuario;final CountDownLatch listos,inicio,fin;volatile Throwable error;volatile RequerimientoCompraOrdenSeguimiento resultado;Editor(RequerimientoCompraOrdenSeguimientoHelper helper,int req,int oc,int version,String usuario,CountDownLatch listos,CountDownLatch inicio,CountDownLatch fin){this.helper=helper;this.req=req;this.oc=oc;this.version=version;this.usuario=usuario;this.listos=listos;this.inicio=inicio;this.fin=fin;setDaemon(true);}public void run(){listos.countDown();try{if(!inicio.await(10,TimeUnit.SECONDS))throw new IllegalStateException("Inicio no liberado");resultado=helper.guardar(req,oc,version,entrada(usuario),usuario);}catch(Throwable e){error=e;}finally{fin.countDown();}}}
    private static Map<String,String> entrada(String obs){Map<String,String>e=ComprasOrdenSeguimientoTest.completa();e.put("seg_observaciones",obs);e.put("seg_fecha_factura","06/11/2026");e.put("seg_id_orden_pago","456");return e;}
    private static List<String> filas(ComboPooledDataSource pool,String sql)throws Exception{List<String>rows=new ArrayList<String>();Connection con=pool.getConnection();try{PreparedStatement st=con.prepareStatement(sql);try{ResultSet rs=st.executeQuery();try{while(rs.next())rows.add(rs.getString(1));}finally{rs.close();}}finally{st.close();}}finally{con.close();}return rows;}
    private static boolean vacio(String s){return s==null||s.length()==0;}
    private static void igual(String caso,Object esperado,Object actual){comprobar(caso+": esperado="+esperado+", actual="+actual,esperado==null?actual==null:esperado.equals(actual));}
    private static void comprobar(String caso,boolean ok){checks++;if(!ok)throw new AssertionError(caso);}
}
