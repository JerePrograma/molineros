import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSuministro;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceImpl;
import ar.com.ospim.util.ConnectionHelper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Persistencia productiva y transacciones reales, solo en fixture aislado validado. */
public class ComprasOrdenSuministroJdbcTest {
    private static int checks;
    public static void main(String[] args)throws Exception {
        if(args.length!=1||!args[0].matches("^compras_orden_test_edicion_[a-z0-9_]+$"))throw new IllegalArgumentException("Solo se admite la base aislada de pruebas de OC.");
        ComboPooledDataSource pool=new ComboPooledDataSource();Field field=ConnectionHelper.class.getDeclaredField("datasource");field.setAccessible(true);Object anterior=field.get(null);boolean instalado=false;
        try {
            int port=Integer.parseInt(System.getProperty("compras.test.port","5432"));if(port<=0||port>65535)throw new IllegalArgumentException("Puerto invalido.");
            pool.setDriverClass("org.postgresql.Driver");pool.setJdbcUrl("jdbc:postgresql://127.0.0.1:"+port+"/"+args[0]);pool.setUser("postgres");pool.setPassword("");pool.setMinPoolSize(1);pool.setInitialPoolSize(1);pool.setMaxPoolSize(4);pool.setCheckoutTimeout(15000);pool.setAcquireRetryAttempts(1);field.set(null,pool);instalado=true;
            Fuente fuente=new Fuente();fuente.cargarConfiguracionPrueba();fuente.configuracion.put("COMPRAS_ESTADOS_IDS","PENDIENTE=1;A_COTIZAR=2;COTIZADO=3;RECLAMO_RP=4;ORDEN_COMPRA=5;PARA_AUTORIZAR=6;AUTORIZADO=7;ANULADO=99");fuente.instalar();
            ejecutar(pool);
            System.out.println("COMPRAS_ORDEN_SUMINISTRO_JDBC_OK checks="+checks);
            System.out.println("Roundtrip, auditoria, aislamiento por OC, rollback, versiones y dos guardados concurrentes; solo fixture sintetico.");
        }finally{try{if(instalado)field.set(null,anterior);}finally{pool.close();}}
    }
    private static void ejecutar(ComboPooledDataSource pool)throws Exception {
        RequerimientoCompraOrdenServiceImpl service=new RequerimientoCompraOrdenServiceImpl();RequerimientoCompraOrdenHelper helper=new RequerimientoCompraOrdenHelper();
        RequerimientoCompraOrden oc1=service.obtenerPorRequerimiento(1001),oc2=service.obtenerPorRequerimiento(1002);
        comprobar("dos OCs de fixture",oc1!=null&&oc2!=null&&oc1.getIdOrdenCompra()!=oc2.getIdOrdenCompra());
        int id1=oc1.getIdOrdenCompra(),id2=oc2.getIdOrdenCompra();
        List<Map<String,String>> cabeceras=filas(pool,"SELECT * FROM compras.requerimiento_orden_compra ORDER BY id_orden_compra");
        List<Map<String,String>> detalles=filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_detalle ORDER BY id_orden_compra,orden");
        List<Map<String,String>> requerimientos=filas(pool,"SELECT * FROM compras.requerimiento ORDER BY id_requerimiento");
        comprobar("primera OC aun sin gestion",service.obtenerSuministro(id1)==null);comprobar("segunda OC aun sin gestion",service.obtenerSuministro(id2)==null);
        Map<String,String> e=entrada("Primera persona");RequerimientoCompraOrdenSuministro guardada=helper.guardarSuministro(1001,id1,0,e,"gestor_jdbc");
        RequerimientoCompraOrdenSuministro nuevaLectura=new RequerimientoCompraOrdenServiceImpl().obtenerSuministro(id1);
        comprobar("lectura desde otra instancia",nuevaLectura!=null);igual("numero OC",Integer.valueOf(id1),Integer.valueOf(nuevaLectura.getIdOrdenCompra()));
        igual("version inicial uno",Integer.valueOf(1),Integer.valueOf(nuevaLectura.getVersionEdicion()));
        verificarCampos(e,nuevaLectura);igual("usuario alta","gestor_jdbc",nuevaLectura.getAltaUsr());comprobar("fecha alta",nuevaLectura.getAltaFecha()!=null);igual("sin modificacion inicial",null,nuevaLectura.getModiFecha());
        Date alta=nuevaLectura.getAltaFecha();igual("una fila de gestion",Integer.valueOf(1),Integer.valueOf(filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_suministro").size()));
        Map<String,String> segunda=new HashMap<String,String>();segunda.put("suministro_persona","Persona editada");segunda.put("suministro_pregunta_5","NO");
        guardada=helper.guardarSuministro(1001,id1,1,segunda,"editor_jdbc");nuevaLectura=new RequerimientoCompraOrdenServiceImpl().obtenerSuministro(id1);
        igual("edicion version dos",Integer.valueOf(2),Integer.valueOf(nuevaLectura.getVersionEdicion()));igual("persona editada","Persona editada",nuevaLectura.getPersonaContactada());
        comprobar("fecha borrada persiste null",nuevaLectura.getFechaContacto()==null);comprobar("medio borrado",vacio(nuevaLectura.getMedioContacto()));comprobar("detalle borrado",vacio(nuevaLectura.getDetalle()));comprobar("primera respuesta borrada",vacio(nuevaLectura.getRespuesta1()));igual("respuesta editada","NO",nuevaLectura.getRespuesta5());
        igual("alta fecha preservada",alta,nuevaLectura.getAltaFecha());igual("alta usuario preservado","gestor_jdbc",nuevaLectura.getAltaUsr());igual("usuario modificacion","editor_jdbc",nuevaLectura.getModiUsr());comprobar("fecha modificacion",nuevaLectura.getModiFecha()!=null);
        List<Map<String,String>> antes=filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_suministro ORDER BY id_orden_compra");
        boolean fallo=false;try{helper.guardarSuministro(1001,id1,1,entrada("No persistir obsoleta"),"obsoleto");}catch(IllegalArgumentException ex){fallo=true;}comprobar("rechazo version obsoleta",fallo);igual("obsoleta no sobrescribe",antes,filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_suministro ORDER BY id_orden_compra"));
        fallo=false;try{helper.guardarSuministro(1001,id2,0,entrada("Otra OC no debe guardar"),"ajeno");}catch(IllegalArgumentException ex){fallo=true;}comprobar("rechazo OC de otro requerimiento",fallo);igual("identidad incorrecta sin cambios",antes,filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_suministro ORDER BY id_orden_compra"));
        rollbackSql(service,pool,nuevaLectura,antes);
        concurrentes(helper,pool,1002,id2,0);
        igual("otra OC no altera primera",antes.get(0),filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_suministro WHERE id_orden_compra="+id1).get(0));
        igual("dos OCs, dos gestiones",Integer.valueOf(2),Integer.valueOf(filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_suministro").size()));
        concurrentes(helper,pool,1001,id1,2);
        igual("gestiones no duplicadas tras actualizar",Integer.valueOf(2),Integer.valueOf(filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_suministro").size()));
        igual("cabeceras comerciales intactas",cabeceras,filas(pool,"SELECT * FROM compras.requerimiento_orden_compra ORDER BY id_orden_compra"));
        igual("detalle comprado intacto",detalles,filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_detalle ORDER BY id_orden_compra,orden"));
        igual("requerimientos intactos",requerimientos,filas(pool,"SELECT * FROM compras.requerimiento ORDER BY id_requerimiento"));
        encuesta(helper, service, pool, id1);
        igual("encuesta no altera cabeceras comerciales",cabeceras,filas(pool,"SELECT * FROM compras.requerimiento_orden_compra ORDER BY id_orden_compra"));
        igual("encuesta no altera detalle comprado",detalles,filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_detalle ORDER BY id_orden_compra,orden"));
    }
    private static void encuesta(RequerimientoCompraOrdenHelper helper,
            RequerimientoCompraOrdenServiceImpl service, ComboPooledDataSource pool, int id) throws Exception {
        String historicoSql = "SELECT respuesta_1,respuesta_2,respuesta_3,respuesta_4,respuesta_5 "
                + "FROM compras.requerimiento_orden_compra_suministro WHERE id_orden_compra=" + id;
        List<Map<String,String>> historico = filas(pool, historicoSql);
        Map<String,String> e = new HashMap<String,String>();
        e.put("suministro_recepcion", "PARCIALMENTE");
        e.put("suministro_satisfaccion", "MUY_INSATISFECHO");
        for (int i = 1; i <= 5; i++) e.put("suministro_pregunta_" + i, "MANIPULADA");
        helper.guardarSuministro(1001, id, 3, e, "encuesta_jdbc");
        RequerimientoCompraOrdenSuministro s = service.obtenerSuministro(id);
        igual("encuesta recepcion JDBC", "PARCIALMENTE", s.getRecepcionSuministro());
        igual("encuesta satisfaccion JDBC", "MUY_INSATISFECHO", s.getSatisfaccionEntrega());
        igual("encuesta conserva historicos JDBC", historico, filas(pool, historicoSql));
        helper.guardarSuministro(1001, id, 4, entrada("Formulario anterior"), "legacy_jdbc");
        s = service.obtenerSuministro(id);
        igual("formulario anterior conserva recepcion JDBC", "PARCIALMENTE", s.getRecepcionSuministro());
        igual("formulario anterior conserva satisfaccion JDBC", "MUY_INSATISFECHO", s.getSatisfaccionEntrega());
        concurrentes(helper, pool, 1001, id, 5);
        s = service.obtenerSuministro(id);
        igual("concurrentes conservan recepcion nueva", "PARCIALMENTE", s.getRecepcionSuministro());
        igual("concurrentes conservan satisfaccion nueva", "MUY_INSATISFECHO", s.getSatisfaccionEntrega());
        List<Map<String,String>> antes = filas(pool, "SELECT * FROM compras.requerimiento_orden_compra_suministro ORDER BY id_orden_compra");
        e.put("suministro_recepcion", "NEUTRAL");
        boolean fallo = false;
        try { helper.guardarSuministro(1001, id, 6, e, "invalido_jdbc"); }
        catch (IllegalArgumentException ex) { fallo = true; }
        comprobar("JDBC rechaza dominio cruzado recepcion", fallo);
        e.put("suministro_recepcion", "SI"); e.put("suministro_satisfaccion", "NO"); fallo = false;
        try { helper.guardarSuministro(1001, id, 6, e, "invalido_jdbc"); }
        catch (IllegalArgumentException ex) { fallo = true; }
        comprobar("JDBC rechaza dominio cruzado satisfaccion", fallo);
        igual("rechazos encuesta sin escritura JDBC", antes,
                filas(pool, "SELECT * FROM compras.requerimiento_orden_compra_suministro ORDER BY id_orden_compra"));
        historico = filas(pool, historicoSql);
        e.put("suministro_recepcion", ""); e.put("suministro_satisfaccion", "");
        helper.guardarSuministro(1001, id, 6, e, "borrado_encuesta_jdbc");
        s = service.obtenerSuministro(id);
        comprobar("recepcion vacia persiste JDBC", vacio(s.getRecepcionSuministro()));
        comprobar("satisfaccion vacia persiste JDBC", vacio(s.getSatisfaccionEntrega()));
        igual("borrado encuesta conserva historicos JDBC", historico, filas(pool, historicoSql));
    }

    private static void rollbackSql(RequerimientoCompraOrdenServiceImpl service,ComboPooledDataSource pool,RequerimientoCompraOrdenSuministro actual,List<Map<String,String>> antes)throws Exception {
        RequerimientoCompraOrdenServiceImpl.Transaccion tx=service.abrirTransaccion();boolean fallo=false;
        try{actual.setRespuesta1("VALOR_INVALIDO");tx.actualizarSuministro(actual,"sql_invalido");tx.commit();}catch(SQLException ex){fallo=true;tx.rollback();}finally{tx.cerrar();}
        comprobar("constraint SQL rechaza respuesta",fallo);igual("fallo SQL no cambia gestion ni version",antes,filas(pool,"SELECT * FROM compras.requerimiento_orden_compra_suministro ORDER BY id_orden_compra"));
    }
    private static void concurrentes(RequerimientoCompraOrdenHelper helper,ComboPooledDataSource pool,int req,int oc,int version)throws Exception {
        CountDownLatch listos=new CountDownLatch(2),inicio=new CountDownLatch(1),fin=new CountDownLatch(2);Editor uno=new Editor(helper,req,oc,version,"concurrente_uno",listos,inicio,fin),dos=new Editor(helper,req,oc,version,"concurrente_dos",listos,inicio,fin);uno.start();dos.start();boolean preparados=listos.await(10,TimeUnit.SECONDS);inicio.countDown();if(!preparados||!fin.await(30,TimeUnit.SECONDS))throw new AssertionError("Timeout de suministro concurrente.");
        int exitos=(uno.resultado==null?0:1)+(dos.resultado==null?0:1);igual("un solo ganador version "+version,Integer.valueOf(1),Integer.valueOf(exitos));Editor ganador=uno.resultado!=null?uno:dos,perdedor=uno.resultado==null?uno:dos;comprobar("segundo rechazado por version "+version,perdedor.error instanceof IllegalArgumentException);comprobar("ganador sin error "+version,ganador.error==null);
        RequerimientoCompraOrdenSuministro finalS=new RequerimientoCompraOrdenServiceImpl().obtenerSuministro(oc);igual("version final una vez "+version,Integer.valueOf(version+1),Integer.valueOf(finalS.getVersionEdicion()));igual("contenido del ganador "+version,ganador.usuario,finalS.getPersonaContactada());igual("auditoria ganador "+version,ganador.usuario,version==0?finalS.getAltaUsr():finalS.getModiUsr());
    }
    private static class Editor extends Thread {
        final RequerimientoCompraOrdenHelper helper;final int req;final int oc;final int version;final String usuario;final CountDownLatch listos;final CountDownLatch inicio;final CountDownLatch fin;volatile RequerimientoCompraOrdenSuministro resultado;volatile Throwable error;
        Editor(RequerimientoCompraOrdenHelper helper,int req,int oc,int version,String usuario,CountDownLatch listos,CountDownLatch inicio,CountDownLatch fin){super(usuario);this.helper=helper;this.req=req;this.oc=oc;this.version=version;this.usuario=usuario;this.listos=listos;this.inicio=inicio;this.fin=fin;setDaemon(true);}
        public void run(){listos.countDown();try{if(!inicio.await(10,TimeUnit.SECONDS))throw new IllegalStateException("No se libero inicio.");resultado=helper.guardarSuministro(req,oc,version,entrada(usuario),usuario);}catch(Throwable ex){error=ex;}finally{fin.countDown();}}
    }
    private static class Fuente extends ComprasFuenteDatosFixture {
        public RequerimientoCompra getRequerimientoCompra(Connection con,int id)throws Exception {
            PreparedStatement s=con.prepareStatement("SELECT id_requerimiento,id_sector,estado,baja_fecha FROM compras.requerimiento WHERE id_requerimiento=?");try{s.setInt(1,id);ResultSet rs=s.executeQuery();try{if(!rs.next())return null;RequerimientoCompra r=new RequerimientoCompra();r.setIdRequerimientoCompra(rs.getInt("id_requerimiento"));r.setSectorId(Integer.valueOf(rs.getInt("id_sector")));r.setEstado(rs.getInt("estado"));r.setBajaFecha(rs.getTimestamp("baja_fecha"));return r;}finally{rs.close();}}finally{s.close();}
        }
    }
    private static Map<String,String> entrada(String persona){Map<String,String> e=new HashMap<String,String>();e.put("fecha_contacto","03/11/2026");e.put("suministro_persona",persona);e.put("suministro_medio","Telefono");e.put("suministro_detalle","Gestion 'detalle'\nSegunda linea \u00f1");String[] r={"SI","NO","NO_CORRESPONDE","","SI"};for(int i=0;i<r.length;i++)e.put("suministro_pregunta_"+(i+1),r[i]);return e;}
    private static void verificarCampos(Map<String,String> e,RequerimientoCompraOrdenSuministro s){igual("fecha roundtrip",e.get("fecha_contacto"),new SimpleDateFormat("dd/MM/yyyy").format(s.getFechaContacto()));igual("persona roundtrip",e.get("suministro_persona"),s.getPersonaContactada());igual("medio roundtrip",e.get("suministro_medio"),s.getMedioContacto());igual("detalle y caracteres roundtrip",e.get("suministro_detalle"),s.getDetalle());String[] r={s.getRespuesta1(),s.getRespuesta2(),s.getRespuesta3(),s.getRespuesta4(),s.getRespuesta5()};for(int i=0;i<r.length;i++)igual("respuesta roundtrip "+i,e.get("suministro_pregunta_"+(i+1)),r[i]);}
    private static List<Map<String,String>> filas(ComboPooledDataSource pool,String sql)throws Exception{Connection con=pool.getConnection();try{PreparedStatement s=con.prepareStatement(sql);try{ResultSet rs=s.executeQuery();try{List<Map<String,String>> rows=new ArrayList<Map<String,String>>();ResultSetMetaData meta=rs.getMetaData();while(rs.next()){Map<String,String> row=new LinkedHashMap<String,String>();for(int i=1;i<=meta.getColumnCount();i++){String valor;if(meta.getColumnType(i)==java.sql.Types.TIMESTAMP){Timestamp t=rs.getTimestamp(i);valor=t==null?null:t.toString();}else valor=rs.getString(i);row.put(meta.getColumnLabel(i),valor);}rows.add(row);}return rows;}finally{rs.close();}}finally{s.close();}}finally{con.close();}}
    private static boolean vacio(String s){return s==null||s.length()==0;}
    private static void igual(String caso,Object esperado,Object actual){comprobar(caso+": esperado="+esperado+", actual="+actual,esperado==null?actual==null:esperado.equals(actual));}
    private static void comprobar(String caso,boolean condicion){checks++;if(!condicion)throw new AssertionError(caso);}
}