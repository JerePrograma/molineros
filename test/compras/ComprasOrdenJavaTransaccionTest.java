import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.math.BigDecimal;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import ar.com.ospim.compras.requerimientos.service.*;

public class ComprasOrdenJavaTransaccionTest {
    static int checks;
    static void ok(boolean condicion,String mensaje){if(!condicion)throw new IllegalStateException(mensaje);checks++;}
    static Object defecto(Class c){if(c==Boolean.TYPE)return Boolean.FALSE;if(c==Integer.TYPE)return Integer.valueOf(0);if(c==Long.TYPE)return Long.valueOf(0);return null;}
    static Map<String,Object> fila(Object... datos){Map<String,Object> f=new HashMap<String,Object>();for(int i=0;i<datos.length;i+=2)f.put((String)datos[i],datos[i+1]);return f;}
    static final class BD {
        final ReentrantLock lock=new ReentrantLock();final List<String> sql=Collections.synchronizedList(new ArrayList<String>());
        RequerimientoCompra r; String estadoRp="VINCULADO";Timestamp bajaRp;boolean sinRp; boolean sectorActivo=true; boolean fallarDetalle; boolean casPerdido; int orden; int origen=101;int version;int inserts;int commits;int rollbacks;BigDecimal precio;String condicion;String usuarioModi;int detalle=10;
        BD() {
            configuracion();BusquedaRequerimientoCompraServiceUtil.permitirLecturaGlobalPreparacion=true;
            r=new RequerimientoCompra();BusquedaRequerimientoCompraServiceUtil.permitirLecturaGlobalPreparacion=false;
            r.setIdRequerimientoCompra(42);r.setEstado(7);r.setIdSector(Integer.valueOf(4));r.setSectorDescripcion("Sistemas");r.setAltaUsr("solicitante");
            RequerimientoCompraDetalle d=new RequerimientoCompraDetalle();d.setId(Integer.valueOf(10));d.setIdRequerimientoCompra(42);d.setTipoItem("OBSERVACION");d.setObservaciones("Equipo de prueba");d.setDescripcionItem(null);d.setCantidad(Integer.valueOf(2));r.getDetalles().add(d);
            BusquedaRequerimientoCompraServiceUtil.requerimiento=r;
            BusquedaRequerimientoCompraServiceUtil.documentos.clear();RequerimientoCompraPresupuesto p=new RequerimientoCompraPresupuesto();p.setIdRequerimientoPresupuesto(Integer.valueOf(101));p.setIdRequerimiento(Integer.valueOf(42));p.setTipoDocumento(Integer.valueOf(3));p.setEmpresaAdjudicada(true);p.setDescripcionEmpresa("Empresa de prueba");BusquedaRequerimientoCompraServiceUtil.documentos.add(p);
        }
        Connection conexion(){return(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},new Conexion(this));}
    }
    static final class Conexion implements InvocationHandler {
        final BD b;int locks;boolean snap;int orden;int estado;int version;BigDecimal precio;String condicion;
        Conexion(BD b){this.b=b;}
        void bloquear(){b.lock.lock();locks++;if(!snap){snap=true;orden=b.orden;estado=b.r.getEstado();version=b.version;precio=b.precio;condicion=b.condicion;}}
        void soltar(){while(locks>0){locks--;b.lock.unlock();}snap=false;}
        public Object invoke(Object proxy,Method m,Object[] a)throws Throwable {
            String n=m.getName();if(n.equals("prepareStatement")||n.equals("prepareCall")){String sql=(String)a[0];b.sql.add(sql);return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{n.equals("prepareCall")?CallableStatement.class:PreparedStatement.class},new Sentencia(this,sql));}
            if(n.equals("commit")){b.commits++;soltar();}
            if(n.equals("rollback")){b.rollbacks++;if(snap){b.orden=orden;b.r.setEstado(estado);b.version=version;b.precio=precio;b.condicion=condicion;}soltar();}
            if(n.equals("close"))soltar();return defecto(m.getReturnType());
        }
    }
    static final class Sentencia implements InvocationHandler {
        final Conexion c;final String sql;final Map<Integer,Object> p=new HashMap<Integer,Object>();
        Sentencia(Conexion c,String sql){this.c=c;this.sql=sql;}
        public Object invoke(Object proxy,Method m,Object[] a)throws Throwable {
            String n=m.getName();if(n.startsWith("set")&&a!=null&&a.length>=2)p.put((Integer)a[0],a[1]);
            if(n.equals("execute")){c.bloquear();return Boolean.TRUE;}
            if(n.equals("executeQuery"))return resultado();if(n.equals("executeUpdate"))return Integer.valueOf(escribir());return defecto(m.getReturnType());
        }
        int escribir()throws SQLException {
            BD b=c.b;
            if(sql.startsWith("INSERT INTO compras.requerimiento_orden_compra_detalle")){if(b.fallarDetalle)throw new SQLException("Fallo detalle aislado.");b.precio=(BigDecimal)p.get(5);b.detalle=((Integer)p.get(3)).intValue();return 1;}
            if(sql.startsWith("UPDATE compras.requerimiento SET estado")){if(b.casPerdido)return 0;b.r.setEstado(((Integer)p.get(1)).intValue());b.usuarioModi=(String)p.get(2);return 1;}
            if(sql.startsWith("UPDATE compras.requerimiento_orden_compra SET")){if(b.casPerdido)return 0;b.version++;b.condicion=(String)p.get(1);return 1;}
            if(sql.startsWith("UPDATE compras.requerimiento_orden_compra_detalle")){if(b.precio!=null)return 0;b.precio=(BigDecimal)p.get(1);return 1;}
            throw new SQLException("Escritura inesperada: "+sql);
        }
        ResultSet resultado()throws Exception {
            BD b=c.b;List<Map<String,Object>> f=new ArrayList<Map<String,Object>>();
            if(sql.indexOf("FOR UPDATE")>=0||sql.indexOf("FOR SHARE")>=0)c.bloquear();
            if(sql.startsWith("SELECT activo,baja_fecha"))f.add(fila("activo",Boolean.valueOf(b.sectorActivo)));
            else if(sql.startsWith("SELECT CURRENT_DATE"))f.add(fila("1",java.sql.Date.valueOf("2026-10-30")));
            else if(sql.startsWith("SELECT p.id_requerimiento FROM")){if(b.orden>0)f.add(fila("1",Integer.valueOf(42)));}
            else if(sql.startsWith("SELECT id_presupuesto FROM")){if(b.orden>0)f.add(fila("1",Integer.valueOf(b.origen)));}
            else if(sql.startsWith("INSERT INTO compras.requerimiento_orden_compra ")){b.orden=900;b.inserts++;b.origen=((Integer)p.get(2)).intValue();b.condicion=(String)p.get(5);f.add(fila("1",Integer.valueOf(900)));}
            else if(sql.startsWith("SELECT * FROM compras.requerimiento_orden_compra_consulta")){if(b.orden>0)f.add(fila("id_orden_compra",Integer.valueOf(b.orden),"id_requerimiento",Integer.valueOf(42),"alta_usr","auditoria","version_edicion",Integer.valueOf(b.version),"fecha_emision",java.sql.Date.valueOf("2026-10-30"),"iva",BigDecimal.ZERO,"iibb",BigDecimal.ZERO,"condicion",b.condicion));}
            else if(sql.startsWith("SELECT * FROM compras.requerimiento_orden_compra_detalle_consulta")){if(b.orden>0)f.add(fila("id_requerimiento_detalle",Integer.valueOf(b.detalle),"descripcion_item","Equipo de prueba","cantidad",Integer.valueOf(2),"precio_unitario",b.precio,"precio_total",b.precio==null?null:b.precio.multiply(new BigDecimal(2))));}
            else if(sql.indexOf("FOR UPDATE")>=0)f.add(fila("1",Integer.valueOf(42)));
            else if(sql.startsWith("SELECT rr.estado")){if(!b.sinRp)f.add(fila("estado",b.estadoRp,"id_reclamo_prestacional",Integer.valueOf(800),"rp_id",Integer.valueOf(800),"baja_fecha",b.bajaRp));}
            else throw new SQLException("Lectura inesperada: "+sql);
            return rs(f);
        }
    }
    static ResultSet rs(final List<Map<String,Object>> f){return(ResultSet)Proxy.newProxyInstance(ComprasOrdenJavaTransaccionTest.class.getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){int i=-1;boolean nulo;public Object invoke(Object p,Method m,Object[] a){String n=m.getName();if(n.equals("next"))return Boolean.valueOf(++i<f.size());if(n.equals("wasNull"))return Boolean.valueOf(nulo);if(n.startsWith("get")){Object v=f.get(i).get(String.valueOf(a[0]));nulo=v==null;if(n.equals("getInt"))return Integer.valueOf(v==null?0:((Number)v).intValue());if(n.equals("getBoolean"))return Boolean.valueOf(Boolean.TRUE.equals(v));return v;}return defecto(m.getReturnType());}});}
    static final class Helper extends RequerimientoCompraOrdenHelper {
        final BD b;Helper(BD b){this.b=b;}
        protected RequerimientoCompraOrdenServiceUtil.Transaccion abrirTransaccion()throws Exception {
            Constructor c=RequerimientoCompraOrdenServiceImpl.Transaccion.class.getDeclaredConstructor(new Class[]{RequerimientoCompraOrdenServiceImpl.class,Connection.class});c.setAccessible(true);
            Object tx=c.newInstance(new Object[]{new RequerimientoCompraOrdenServiceImpl(),b.conexion()});
            Constructor u=RequerimientoCompraOrdenServiceUtil.Transaccion.class.getDeclaredConstructor(new Class[]{RequerimientoCompraOrdenServiceImpl.Transaccion.class});u.setAccessible(true);
            return(RequerimientoCompraOrdenServiceUtil.Transaccion)u.newInstance(new Object[]{tx});
        }
    }
    static void configuracion(){Map<String,String> c=BusquedaRequerimientoCompraServiceUtil.configuraciones;c.clear();c.put("COMPRAS_OC_MAX_IMPORTE","99999999999999.99");c.put("COMPRAS_ESTADOS_IDS","PENDIENTE=1;A_COTIZAR=2;COTIZADO=3;RECLAMO_RP=4;ORDEN_COMPRA=5;PARA_AUTORIZAR=6;AUTORIZADO=7;ANULADO=99");c.put("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR","RRHH;SISTEMAS");c.put("COMPRAS_SECTORES_DETALLE_OBSERVACION","RRHH;SISTEMAS");}
    static BD prestador() {
        BD b=new BD();b.r.setSectorDescripcion("Insumos diabetes");
        RequerimientoCompraDetalle d=b.r.getDetalles().get(0);d.setTipoItem("NOMENCLADOR");d.setIdPrestacion(Integer.valueOf(501));d.setIdTipoPrestacion(Integer.valueOf(15));d.setIdTipoNomenclador(Integer.valueOf(15));d.setCodigoNomenclador("PRUEBA");d.setDescripcionNomenclador("Equipo de prueba");d.setIdPrestador(Integer.valueOf(33));d.setPrestadorRazonSocial("Proveedor de prueba");d.setPrestadorCuit("00000000000");d.setPrecioUnitarioEstimado(new BigDecimal("5.00"));d.setPrecioTotalEstimado(new BigDecimal("10.00"));
        RequerimientoCompraPresupuesto p=BusquedaRequerimientoCompraServiceUtil.documentos.get(0);p.setTipoDocumento(Integer.valueOf(1));p.setIdPrestador(Integer.valueOf(33));p.setEmpresaAdjudicada(false);
        RequerimientoCompraComparativa c=RequerimientoCompraComparativa.paraMapeo();c.setIdComparativa(4);c.setIdRequerimiento(42);c.setIdPrestador(33);c.setIva(BigDecimal.ZERO);c.setIibb(BigDecimal.ZERO);
        RequerimientoCompraComparativaDetalle cd=new RequerimientoCompraComparativaDetalle();cd.setIdDetalle(6);cd.setIdPrestacion(501);cd.setCantidad(Integer.valueOf(2));cd.setImporteUnitario(new BigDecimal("5.00"));c.getDetalles().add(cd);
        RequerimientoCompraComparativaServiceUtil.comparativas.clear();RequerimientoCompraComparativaServiceUtil.comparativas.add(c);return b;
    }
    static Map<String,String> entrada(){Map<String,String> f=new HashMap<String,String>();f.put("precio_10","12.30");return f;}
    public static void main(String[] args)throws Exception {
        BD b=new BD();Helper h=new Helper(b);RequerimientoCompraOrden o=h.crear(42,entrada(),"auditoria");ok(o.getIdOrdenCompra()==900&&b.r.getEstado()==5&&b.inserts==1,"Crea y confirma bajo una TX.");
        ok(b.precio.compareTo(new BigDecimal("12.30"))==0&&b.commits==1&&b.rollbacks==0,"Precio persistido y unico commit.");
        ok(b.sql.get(0).indexOf("bloquear_requerimiento_reclamo")>=0&&b.sql.get(1).indexOf("FOR UPDATE OF rp")>=0,"Bloqueo RP previo a cabecera.");
        for(String q:b.sql)ok(q.indexOf("bloquear_creacion_orden_compra")<0&&q.indexOf("confirmar_orden_compra_requerimiento")<0,"Sin decisiones SQL de OC.");
        h.crear(42,null,"auditoria");ok(b.inserts==1&&b.commits==2,"Reintento OC emitida idempotente.");
        Map<String,String> cambios=new HashMap<String,String>();cambios.put("condicion","Entrega coordinada");h.actualizar(42,900,0,cambios,"editor");ok(b.version==1&&"Entrega coordinada".equals(b.condicion),"Edicion CAS incrementa version.");
        boolean rechazo=false;try{h.actualizar(42,900,0,cambios,"editor");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.version==1,"Version obsoleta rechazada.");
        b=new BD();b.orden=900;b.precio=new BigDecimal("12.30");new Helper(b).crear(42,null," auditoria ");ok(b.inserts==0&&b.r.getEstado()==5&&"auditoria".equals(b.usuarioModi),"OC existente autorizada confirma origen/cobertura y normaliza auditoria.");
        b=new BD();b.orden=900;b.precio=new BigDecimal("12.30");b.origen=102;rechazo=false;try{new Helper(b).crear(42,null,"auditoria");}catch(IllegalStateException e){rechazo=true;}ok(rechazo&&b.r.getEstado()==7,"Origen distinto rechaza confirmacion OC existente.");
        b=new BD();b.orden=900;b.precio=new BigDecimal("12.30");b.detalle=999;rechazo=false;try{new Helper(b).crear(42,null,"auditoria");}catch(IllegalStateException e){rechazo=true;}ok(rechazo&&b.r.getEstado()==7,"Detalle ajeno rechaza confirmacion OC existente.");
        b=new BD();b.fallarDetalle=true;rechazo=false;try{new Helper(b).crear(42,entrada(),"auditoria");}catch(SQLException e){rechazo=true;}ok(rechazo&&b.orden==0&&b.r.getEstado()==7&&b.rollbacks==1,"Error detalle revierte cabecera y estado.");
        b=new BD();b.casPerdido=true;rechazo=false;try{new Helper(b).crear(42,entrada(),"auditoria");}catch(IllegalStateException e){rechazo=true;}ok(rechazo&&b.orden==0&&b.r.getEstado()==7,"CAS perdido revierte toda OC.");
        b=new BD();b.sectorActivo=false;rechazo=false;try{new Helper(b).crear(42,entrada(),"auditoria");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Sector inactivo rechazado antes persistir.");
        b=new BD();BusquedaRequerimientoCompraServiceUtil.configuraciones.remove("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR");rechazo=false;try{new Helper(b).crear(42,entrada(),"auditoria");}catch(IllegalStateException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Configuracion ausente falla cerrada.");
        b=new BD();BusquedaRequerimientoCompraServiceUtil.documentos.get(0).setEmpresaAdjudicada(false);rechazo=false;try{new Helper(b).crear(42,entrada(),"auditoria");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Empresa no adjudicada rechazada.");
        b=new BD();Map<String,String> ajeno=entrada();ajeno.put("precio_999","1");rechazo=false;try{new Helper(b).crear(42,ajeno,"auditoria");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Precio de detalle ajeno rechazado.");
        b=new BD();BusquedaRequerimientoCompraServiceUtil.configuraciones.put("COMPRAS_OC_MAX_IMPORTE","20.00");rechazo=false;try{new Helper(b).crear(42,entrada(),"auditoria");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Limite de importe configurado se aplica al subtotal.");
        b=new BD();BusquedaRequerimientoCompraServiceUtil.configuraciones.remove("COMPRAS_OC_MAX_IMPORTE");rechazo=false;try{new Helper(b).crear(42,entrada(),"auditoria");}catch(IllegalStateException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Limite de importe ausente falla cerrado.");
        final BD concurrente=new BD();final List<Throwable> fallos=Collections.synchronizedList(new ArrayList<Throwable>());Runnable tarea=new Runnable(){public void run(){try{new Helper(concurrente).crear(42,entrada(),"auditoria");}catch(Throwable e){fallos.add(e);}}};Thread a=new Thread(tarea),c=new Thread(tarea);a.start();c.start();a.join();c.join();ok(fallos.isEmpty()&&concurrente.inserts==1&&concurrente.r.getEstado()==5,"Dos emisiones concurrentes conservan una sola OC.");
        b=new BD();ar.com.ospim.util.ConnectionHelper.transaccionPreparada=b.conexion();RequerimientoCompraOrden dto=new RequerimientoCompraOrden();dto.setIdRequerimientoCompra(42);dto.setDetalles(new ArrayList<RequerimientoCompraDetalle>());RequerimientoCompraDetalle d=new RequerimientoCompraDetalle();d.setId(Integer.valueOf(10));d.setPrecioUnitarioEstimado(new BigDecimal("12.30"));d.setDescripcionItem("Descripcion manipulada");dto.getDetalles().add(d);o=RequerimientoCompraOrdenServiceUtil.crear(dto,"auditoria");ok(o.getDetalles().get(0).getDescripcionItemVisible().equals("Equipo de prueba")&&b.inserts==1,"Facade reconstruye DB e ignora descripcion DTO.");
        b=prestador();o=new Helper(b).crearDirecta(42,"auditoria","auditoria");ok(o.getIdOrdenCompra()==900&&b.r.getEstado()==5&&b.precio.compareTo(new BigDecimal("5.00"))==0,"OC prestador reconstruye comparativa y RP vigentes.");
        b=prestador();b.bajaRp=Timestamp.valueOf("2026-10-30 12:00:00.123456");rechazo=false;try{new Helper(b).crearDirecta(42,"auditoria","auditoria");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.inserts==0,"RP de baja no permite OC prestador.");
        b=prestador();b.sinRp=true;rechazo=false;try{new Helper(b).crearDirecta(42,"auditoria","auditoria");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Vinculo RP inexistente rechaza.");
        b=prestador();b.r.getDetalles().get(0).setPrecioTotalEstimado(new BigDecimal("11.00"));rechazo=false;try{new Helper(b).crearDirecta(42,"auditoria","auditoria");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Subtotal instalado incoherente rechaza antes insertar.");
        b=prestador();BusquedaRequerimientoCompraServiceUtil.documentos.clear();rechazo=false;try{new Helper(b).crearDirecta(42,"auditoria","auditoria");}catch(IllegalArgumentException e){rechazo=true;}ok(rechazo&&b.inserts==0,"Presupuesto prestador activo obligatorio.");
        ok(ar.com.ospim.util.ConnectionHelper.aperturas==0,"Ninguna conexion global oculta.");
        System.out.println("APROBADAS="+checks+"; JDBC simulado, sin DB, UI ni DL reales.");
    }
}
