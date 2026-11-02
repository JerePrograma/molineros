import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper;
import ar.com.ospim.compras.requerimientos.service.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/** Helper y UPDATE JDBC productivos con conexion simulada, sin DB ni UI. */
public class ComprasReasignacionAfiliadoTest {
    private static int pruebas;
    private static Field lectura, escritura;
    private static Estado enCurso;
    public static void main(String[] args) throws Exception {
        lectura=BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        escritura=EditarRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        lectura.setAccessible(true);escritura.setAccessible(true);
        Object previaLectura=lectura.get(null), previaEscritura=escritura.get(null);
        try {
            Estado e=nuevo(); instalar(e);
            if(args.length>0 && "baseline".equals(args[0])) {
                boolean rechazo=false;
                try { guardar(entrada("20222222223",Integer.valueOf(1))); }
                catch(Exception esperado) { rechazo=esperado.getMessage().indexOf("no puede modificarse")>=0; }
                comprobar(rechazo && e.updates==0 && e.rollbacks==1,
                    "DEFECTO_REPRODUCIDO: borrador rechaza cambio de referencia valido antes del UPDATE");
                return;
            }
            RequerimientoCompra r=entrada("20222222223",Integer.valueOf(1));
            r.setAfiliadoNombre("HTTP FALSO");r.setAfiliadoDireccion("HTTP FALSO");
            comprobar(guardar(r)==1388 && e.updates==1 && e.commits==1 && e.rollbacks==0,
                "Borrador sin asociaciones reasigna en una sola TX confirmada");
            comprobar("20222222223".equals(e.guardado.getAfiliadoCuilTitular())
                && e.guardado.getAfiliadoInt().intValue()==1
                && "MAESTRO".equals(e.guardado.getAfiliadoNombre())
                && "DOMICILIO ACTUAL".equals(e.guardado.getAfiliadoDireccion()),
                "Referencia y snapshot nuevo provienen del maestro, nunca de HTTP");
            comprobar(e.eventos.indexOf("LOCK_CABECERA")<e.eventos.indexOf("MAESTRO")
                && e.eventos.indexOf("MAESTRO")<e.eventos.indexOf("UPDATE")
                && e.maestroLeido==1 && ar.com.ospim.util.ConnectionHelper.aperturas==0,
                "Estado bloqueado y maestro se validan antes de escribir sin segunda conexion");
            comprobar(e.sqlUpdate.indexOf("afiliado_cuil_titular=?")>=0
                && e.sqlUpdate.indexOf("estado=? AND baja_fecha IS NULL")>=0
                && e.sqlUpdate.indexOf("RETURNING id_requerimiento")>=0,
                "UPDATE parametrizado conserva CAS estado/baja y devuelve identidad original");
            comprobar("20222222223".equals(e.parametros.get(Integer.valueOf(9)))
                && Integer.valueOf(1).equals(e.parametros.get(Integer.valueOf(10)))
                && Integer.valueOf(99).equals(e.parametros.get(Integer.valueOf(11)))
                && "MAESTRO".equals(e.parametros.get(Integer.valueOf(12)))
                && "DOMICILIO ACTUAL".equals(e.parametros.get(Integer.valueOf(16)))
                && Integer.valueOf(1388).equals(e.parametros.get(Integer.valueOf(22)))
                && Integer.valueOf(1).equals(e.parametros.get(Integer.valueOf(23))),
                "Parametros JDBC enlazan referencia, snapshot maestro e identidad/estado sin desplazar indices");
            comprobar(e.auditInserts==1 && e.auditsPersistidos==1
                && e.eventos.indexOf("UPDATE")<e.eventos.indexOf("AUDIT")
                && Integer.valueOf(1388).equals(e.auditParametros.get(Integer.valueOf(1)))
                && "20111111112".equals(e.auditParametros.get(Integer.valueOf(2)))
                && Integer.valueOf(0).equals(e.auditParametros.get(Integer.valueOf(3)))
                && "20222222223".equals(e.auditParametros.get(Integer.valueOf(4)))
                && Integer.valueOf(1).equals(e.auditParametros.get(Integer.valueOf(5)))
                && "prueba".equals(e.auditParametros.get(Integer.valueOf(6))),
                "Auditoria persistente registra un evento antes/despues y usuario en la misma TX");

            e=nuevo();e.actual.setEstado(2);instalar(e);rechazar(entrada("20222222223",Integer.valueOf(1)),e,
                "Cambio concurrente a otro estado impide reasignar antes de leer maestro");
            comprobar(e.maestroLeido==0,"Estado vigente prevalece sobre formulario atrasado");
            e=nuevo();e.actual.setBajaFecha(new java.util.Date(1));instalar(e);
            rechazar(entrada("20222222223",Integer.valueOf(1)),e,"Requerimiento dado de baja no se modifica");
            e=nuevo();instalar(e);rechazar(entrada("20222222223",null),e,"CUIL sin integrante se rechaza");
            e=nuevo();instalar(e);rechazar(entrada(null,Integer.valueOf(1)),e,"Integrante sin CUIL se rechaza");
            e=nuevo();instalar(e);rechazar(entrada("20222222223",Integer.valueOf(-1)),e,"Integrante negativo se rechaza");
            e=nuevo();instalar(e);rechazar(entrada("no-es-cuil",Integer.valueOf(1)),e,"CUIL malformado se rechaza antes de consultar maestro");
            e=nuevo();e.actual.setRequiereAfiliado(false);instalar(e);
            rechazar(entrada("20222222223",Integer.valueOf(1)),e,"Sector sin afiliado rechaza referencia nueva");
            e=nuevo();e.maestro.setAfiliadoInt(Integer.valueOf(2));instalar(e);
            rechazar(entrada("20222222223",Integer.valueOf(1)),e,"Resultado de maestro de otra clave se rechaza");
            e=nuevo();e.maestro=null;instalar(e);rechazar(entrada("20222222223",Integer.valueOf(1)),e,
                "Afiliado inexistente o dado de baja se rechaza sin persistencia");
            e=nuevo();e.duplicado=true;instalar(e);rechazar(entrada("20222222223",Integer.valueOf(1)),e,
                "Maestro ambiguo provoca rollback y nunca exito falso");
            String[] conservadas={"requerimiento_presupuesto","requerimiento_cotizacion_prestador","requerimiento_comparativa"};
            for(int i=0;i<conservadas.length;i++){
                e=nuevo();e.asociacion=conservadas[i];instalar(e);String documentosAntes=e.documentos.toString();
                comprobar(guardar(entrada("20222222223",Integer.valueOf(1)))==1388 && e.commits==1
                    && e.documentos.toString().equals(documentosAntes) && e.escriturasDocumentos==0,
                    "Reasigna conservando "+conservadas[i]+" e ids/hash/metadata de adjuntos sin escrituras documentales");
            }
            String[] relaciones={"requerimiento_reclamo_prestacional","requerimiento_orden_compra"};
            for(int i=0;i<relaciones.length;i++){
                e=nuevo();e.asociacion=relaciones[i];instalar(e);
                rechazar(entrada("20222222223",Integer.valueOf(1)),e,
                    "Borrador inconsistente con "+relaciones[i]+" impide cambiar identidad de efecto emitido");
            }
            e=nuevo();e.falloAsociaciones=true;instalar(e);
            rechazar(entrada("20222222223",Integer.valueOf(1)),e,"Fallo al verificar asociaciones bloquea cambio de afiliado");
            e=nuevo();e.maestro=null;e.asociacion="requerimiento_presupuesto";e.actual.setAfiliadoNombre("MAESTRO ACTUAL");instalar(e);
            r=entrada("20111111112",Integer.valueOf(0));r.setAfiliadoNombre("HTTP FALSO");
            comprobar(guardar(r)==1388 && e.maestroLeido==0 && "HISTORICO".equals(e.snapshotPersistido) && e.sqlUpdate.indexOf("afiliado_")<0 && e.auditInserts==0,
                "Misma referencia conserva historial y no aplica reglas nuevas ni recarga snapshot");
            e=nuevo();instalar(e);
            comprobar(guardar(entrada(null,null))==1388 && e.maestroLeido==0
                && "20111111112".equals(e.guardado.getAfiliadoCuilTitular()),
                "Caller legacy que omite ambos campos conserva relacion persistida");
            e=nuevo();e.casCero=true;instalar(e);rechazar(entrada("20222222223",Integer.valueOf(1)),e,
                "CAS sin fila no devuelve exito y revierte TX",true);
            e=nuevo();e.falloUpdate=true;instalar(e);rechazar(entrada("20222222223",Integer.valueOf(1)),e,
                "Error de persistencia revierte referencia y snapshot",true);
            e=nuevo();e.falloAuditoria=true;instalar(e);rechazar(entrada("20222222223",Integer.valueOf(1)),e,
                "Error auditoria revierte UPDATE y conserva adjuntos en la misma transaccion",true);
            comprobar(e.updates==1 && e.auditsPersistidos==0 && "HISTORICO".equals(e.snapshotPersistido)
                && e.escriturasDocumentos==0,"Fallo de auditoria no deja reasignacion sin evento ni cambia documentos");
            e=nuevo();e.auditCero=true;instalar(e);rechazar(entrada("20222222223",Integer.valueOf(1)),e,
                "Auditoria sin fila insertada revierte toda la reasignacion",true);
            System.out.println("APROBADAS_REASIGNACION="+pruebas+"; alcance=Helper/JDBC simulados, sin DB ni UI");
        } finally { lectura.set(null,previaLectura);escritura.set(null,previaEscritura); }
    }
    private static int guardar(RequerimientoCompra r)throws Exception{
        enCurso.guardado=r;return new EditarRequerimientoCompraHelper().guardarRequerimientoCompra(r,"prueba");
    }
    private static void rechazar(RequerimientoCompra r,Estado e,String nombre)throws Exception{rechazar(r,e,nombre,false);}
    private static void rechazar(RequerimientoCompra r,Estado e,String nombre,boolean intento)throws Exception{
        boolean fallo=false;try{guardar(r);}catch(Exception esperado){fallo=true;}
        comprobar(fallo&&e.commits==0&&e.rollbacks==1&&(intento||e.updates==0)&&e.cerrada,nombre);
    }
    private static void comprobar(boolean valor,String nombre)throws Exception{
        if(!valor)throw new Exception("FALLO: "+nombre);pruebas++;System.out.println("OK "+nombre);
    }
    private static void instalar(Estado e)throws Exception{enCurso=e;lectura.set(null,new Lecturas(e));escritura.set(null,new Escrituras(e));}
    private static RequerimientoCompra entrada(String cuil,Integer inte){
        RequerimientoCompra r=RequerimientoCompra.paraMapeo();r.setIdRequerimientoCompra(1388);
        r.setIdSector(Integer.valueOf(2));r.setEstado(1);r.setCargoOspim(Integer.valueOf(100));
        r.setCargoTercerizadora(Integer.valueOf(0));r.setAfiliadoCuilTitular(cuil);r.setAfiliadoInt(inte);return r;
    }
    private static Estado nuevo(){
        Estado e=new Estado();e.actual=entrada("20111111112",Integer.valueOf(0));
        e.actual.setAfiliadoNombre("HISTORICO");e.actual.setAfiliadoDireccion("DOMICILIO HISTORICO");
        e.actual.setRequiereAfiliado(true);e.actual.setDetalles(new ArrayList<ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle>());
        e.maestro=entrada("20222222223",Integer.valueOf(1));e.maestro.setAfiliadoNombre("MAESTRO");
        e.maestro.setAfiliadoDireccion("DOMICILIO ACTUAL");e.maestro.setAfiliadoIdOspim(Integer.valueOf(99));
        e.documentos.put("id","44");e.documentos.put("hash","f2d93f5c");e.documentos.put("fecha","2026-10-01");e.documentos.put("metadata","orden-medica.pdf;tipo=2;dl=700");
        e.con=(Connection)Proxy.newProxyInstance(ComprasReasignacionAfiliadoTest.class.getClassLoader(),new Class[]{Connection.class},e);return e;
    }
    private static class Escrituras extends EditarRequerimientoCompraServiceImpl{
        Estado e;Escrituras(Estado estado){e=estado;}
        public Transaccion abrirTransaccion()throws Exception{
            Constructor<Transaccion> c=Transaccion.class.getDeclaredConstructor(EditarRequerimientoCompraServiceImpl.class,Connection.class);
            c.setAccessible(true);return c.newInstance(this,e.con);
        }
        public int guardarRequerimientoCompra(Connection con,RequerimientoCompra r,String usuario)throws Exception{
            e.guardado=r;return super.guardarRequerimientoCompra(con,r,usuario);
        }
    }
    private static class Lecturas extends BusquedaRequerimientoCompraServiceImpl{
        Estado e;Lecturas(Estado estado){e=estado;}
        private void verificar(Connection con)throws Exception{if(con!=e.con)throw new Exception("Otra conexion");}
        public RequerimientoCompra getRequerimientoCompra(Connection con,int id)throws Exception{verificar(con);return e.actual;}
        public RequerimientoCompra getAfiliadoVigente(Connection con,String cuil,int inte)throws Exception{
            verificar(con);e.maestroLeido++;e.eventos.add("MAESTRO");
            if(e.duplicado)throw new SQLException("Maestro ambiguo");return e.maestro;
        }
        public String getConfiguracionCompra(String clave,Connection con)throws Exception{
            verificar(con);if("COMPRAS_ESTADOS_IDS".equals(clave))return "PENDIENTE=1;A_COTIZAR=2;COTIZADO=3;ANULADO=99";
            throw new Exception("Configuracion inesperada: "+clave);
        }
    }
    private static class Estado implements InvocationHandler{
        Connection con;RequerimientoCompra actual,maestro,guardado;String asociacion,sqlUpdate;
        boolean duplicado,casCero,falloUpdate,cerrada,falloAsociaciones,falloAuditoria,auditCero;int updates,commits,rollbacks,maestroLeido;
        List<String> eventos=new ArrayList<String>();
        Map<Integer,Object> parametros,auditParametros;int auditInserts,auditPendientes,auditsPersistidos;String snapshotPersistido="HISTORICO",snapshotPendiente;
        Map<String,String> documentos=new LinkedHashMap<String,String>();int escriturasDocumentos;
        public Object invoke(Object proxy,Method m,Object[] args)throws Throwable{
            String n=m.getName();
            if("prepareCall".equals(n)||"prepareStatement".equals(n)){
                String sql=(String)args[0];
                if((sql.startsWith("UPDATE ")||sql.startsWith("INSERT ")||sql.startsWith("DELETE "))
                    && (sql.indexOf("requerimiento_presupuesto")>=0||sql.indexOf("DLFileEntry")>=0)){
                    escriturasDocumentos++;throw new SQLException("Escritura documental no autorizada");
                }
                if(sql.indexOf("FROM compras.requerimiento ")>=0&&sql.indexOf("FOR UPDATE")>=0)eventos.add("LOCK_CABECERA");
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{"prepareCall".equals(n)?CallableStatement.class:PreparedStatement.class},new Sentencia(this,sql));
            }
            if("commit".equals(n)){commits++;auditsPersistidos+=auditPendientes;auditPendientes=0;if(snapshotPendiente!=null)snapshotPersistido=snapshotPendiente;return null;}
            if("rollback".equals(n)){rollbacks++;auditPendientes=0;snapshotPendiente=null;return null;}
            if("close".equals(n)){cerrada=true;return null;}
            if("isClosed".equals(n))return Boolean.valueOf(cerrada);
            return defecto(m.getReturnType());
        }
    }
    private static class Sentencia implements InvocationHandler{
        Estado e;String sql;Map<Integer,Object> p=new HashMap<Integer,Object>();
        Sentencia(Estado estado,String consulta){e=estado;sql=consulta;}
        public Object invoke(Object proxy,Method m,Object[] args)throws Throwable{
            String n=m.getName();if(n.startsWith("set")&&args!=null&&args.length>=2){p.put((Integer)args[0],"setNull".equals(n)?null:args[1]);return null;}
            if("executeUpdate".equals(n) && sql.startsWith("INSERT INTO compras.requerimiento_afiliado_historial")){
                e.auditInserts++;e.eventos.add("AUDIT");e.auditParametros=p;
                if(e.falloAuditoria)throw new SQLException("Fallo auditoria controlado");
                if(e.auditCero)return Integer.valueOf(0);e.auditPendientes++;return Integer.valueOf(1);
            }
            if("execute".equals(n))return Boolean.TRUE;
            if("executeQuery".equals(n)){
                List<Map<String,Object>> filas=new ArrayList<Map<String,Object>>();
                if(sql.startsWith("UPDATE compras.requerimiento SET")){
                    e.sqlUpdate=sql;e.parametros=p;e.updates++;e.eventos.add("UPDATE");
                    if(e.falloUpdate)throw new SQLException("Fallo controlado");
                    if(!e.casCero&&sql.indexOf("afiliado_nombre=?")>=0)e.snapshotPendiente=(String)p.get(Integer.valueOf(12));
                    if(!e.casCero){Map<String,Object> f=new HashMap<String,Object>();f.put("1",Integer.valueOf(1388));filas.add(f);}
                }else if(sql.indexOf("FROM compras.sector_requerimiento")>=0){
                    Map<String,Object> f=new HashMap<String,Object>();f.put("descripcion","DIABETES");f.put("requiere_afiliado",Boolean.TRUE);f.put("activo",Boolean.TRUE);filas.add(f);
                }else if(sql.indexOf("SELECT EXISTS")>=0||sql.indexOf("SELECT 1 FROM compras.")>=0){
                    if(sql.indexOf("SELECT EXISTS")>=0){
                        if(e.falloAsociaciones)throw new SQLException("Fallo asociaciones controlado");
                        Map<String,Object> f=new HashMap<String,Object>();
                        f.put("1",Boolean.valueOf(e.asociacion!=null&&sql.indexOf("compras."+e.asociacion)>=0));filas.add(f);
                    }else if(e.asociacion!=null&&sql.indexOf("compras."+e.asociacion)>=0){
                        Map<String,Object> f=new HashMap<String,Object>();f.put("1",Boolean.TRUE);filas.add(f);
                    }
                }
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ResultSet.class},new Filas(filas));
            }
            return defecto(m.getReturnType());
        }
    }
    private static class Filas implements InvocationHandler{
        List<Map<String,Object>> filas;int i=-1;boolean nulo;
        Filas(List<Map<String,Object>> f){filas=f;}
        public Object invoke(Object proxy,Method m,Object[] args)throws Throwable{
            String n=m.getName();if("next".equals(n))return Boolean.valueOf(++i<filas.size());
            if("wasNull".equals(n))return Boolean.valueOf(nulo);
            if(n.startsWith("get")&&args!=null&&args.length>0){Object v=filas.get(i).get(String.valueOf(args[0]));nulo=v==null;
                if("getInt".equals(n))return Integer.valueOf(v==null?0:((Number)v).intValue());
                if("getBoolean".equals(n))return Boolean.valueOf(Boolean.TRUE.equals(v));return v;}
            return defecto(m.getReturnType());
        }
    }
    private static Object defecto(Class c){
        if(c==Boolean.TYPE)return Boolean.FALSE;if(c==Integer.TYPE)return Integer.valueOf(0);if(c==Long.TYPE)return Long.valueOf(0);return null;
    }
}