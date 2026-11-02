import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.service.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

/** Esquema JDBC sintetico sin las once copias AFI. No acepta columnas fisicas ausentes. */
public class ComprasAfiliadoReferenciasSqlTest {
    private static int checks,fallos,columnasAusentes;
    private static final String[] COPIAS={"afiliado_id_ospim","afiliado_nombre","afiliado_apellido",
        "afiliado_documento_tipo","afiliado_documento_nro","afiliado_direccion","afiliado_localidad",
        "afiliado_provincia","afiliado_celular","afiliado_telefono","afiliado_email"};
    public static void main(String[] args)throws Exception{
        Field campo=BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");campo.setAccessible(true);
        Object anterior=campo.get(null);ConfiguracionComprasDatosFixture lectura=new ConfiguracionComprasDatosFixture();
        campo.set(null,lectura);
        try{
            Estado e=new Estado();RequerimientoCompra r=entrada(0,"20222222223",1);
            try{
                int id=new EditarRequerimientoCompraServiceImpl().guardarRequerimientoCompra(e.con,r,"prueba");
                check(id==41,"INSERT real funciona con tabla sin once copias");
                check(e.ultimosParametros.size()==13,"INSERT enlaza exactamente trece valores");
                eq("20222222223",e.fila.get("afiliado_cuil_titular"),"INSERT guarda CUIL de referencia");
                eq(Integer.valueOf(1),e.fila.get("afiliado_int"),"INSERT guarda integrante de referencia");
                eq("prueba",e.ultimosParametros.get(Integer.valueOf(12)),"INSERT mantiene identidad de auditoria");
                eq("NOTA",e.ultimosParametros.get(Integer.valueOf(13)),"INSERT mantiene observacion interna");
            }catch(Exception error){fallo("INSERT sin columnas retiradas",error);}
            sinCopias(e.fila,"Fila fisica INSERT no contiene datos demograficos copiados");

            e=new Estado();r=entrada(41,"20222222223",1);
            try{
                Constructor<EditarRequerimientoCompraServiceImpl.Transaccion> ct=
                    EditarRequerimientoCompraServiceImpl.Transaccion.class.getDeclaredConstructor(EditarRequerimientoCompraServiceImpl.class,Connection.class);
                ct.setAccessible(true);EditarRequerimientoCompraServiceImpl.Transaccion tx=ct.newInstance(new EditarRequerimientoCompraServiceImpl(),e.con);
                int id=tx.guardarRequerimientoCompra(r,"prueba",true);
                check(id==41,"UPDATE de reasignacion funciona con esquema reducido");
                check(e.ultimosParametros.size()==12,"UPDATE de reasignacion enlaza exactamente doce valores");
                eq("20222222223",e.ultimosParametros.get(Integer.valueOf(9)),"UPDATE enlaza CUIL nuevo");
                eq(Integer.valueOf(1),e.ultimosParametros.get(Integer.valueOf(10)),"UPDATE enlaza integrante nuevo");
                eq(Integer.valueOf(41),e.ultimosParametros.get(Integer.valueOf(11)),"UPDATE conserva ID del requerimiento");
                eq(Integer.valueOf(1),e.ultimosParametros.get(Integer.valueOf(12)),"UPDATE conserva CAS de estado");
                eq("20222222223",e.fila.get("afiliado_cuil_titular"),"Reasignacion modifica solamente la referencia fisica");
            }catch(Exception error){fallo("UPDATE sin columnas retiradas",error);}
            sinCopias(e.fila,"Fila fisica UPDATE no recibe snapshots del DTO");

            e=new Estado();r=entrada(41,"20222222223",1);
            try{
                int id=new EditarRequerimientoCompraServiceImpl().guardarRequerimientoCompra(e.con,r,"prueba");
                check(id==41&&e.ultimosParametros.size()==10,"Edicion sin reasignacion conserva contrato de diez parametros");
                eq("20111111112",e.fila.get("afiliado_cuil_titular"),"Edicion ordinaria no sustituye referencia persistida");
                r.setEstado(2);Map<String,Object> antes=new HashMap<String,Object>(e.fila);
                check(new EditarRequerimientoCompraServiceImpl().guardarRequerimientoCompra(e.con,r,"prueba")==0,
                    "Estado concurrente no produce UPDATE exitoso");
                check(antes.equals(e.fila),"CAS fallido conserva fila fisica completa");
            }catch(Exception error){fallo("UPDATE ordinario y CAS",error);}

            e=new Estado();e.maestroDeBaja=true;
            try{
                RequerimientoCompra actual=lectura.getCabeceraRequerimiento(e.con,41);
                eq("MAESTRO ACTUAL",actual.getAfiliadoNombre(),"Lector de presentacion conserva alias DTO desde maestro incluso de baja");
                e.nombreActual="MAESTRO MODIFICADO";
                actual=lectura.getCabeceraRequerimiento(e.con,41);
                eq("MAESTRO MODIFICADO",actual.getAfiliadoNombre(),"Cambio maestro visible sin copias fisicas ni cache");
                actual=lectura.getRequerimientoPersistido(e.con,41);
                eq("MAESTRO MODIFICADO",actual.getAfiliadoNombre(),"Lector TX mantiene contrato DTO canonico sin columnas retiradas");
                eq("20111111112",actual.getAfiliadoCuilTitular(),"Lector TX conserva referencia CUIL");
                eq(Integer.valueOf(0),actual.getAfiliadoInt(),"Lector TX conserva referencia integrante");
                check(actual.getDetalles()!=null&&actual.getDetalles().isEmpty(),"Lector TX compone los detalles persistidos definidos por el esquema");
                check(!e.cerrada&&e.commits==0&&e.rollbacks==0,"Lectores conservan control de la conexion del caller");
            }catch(Exception error){fallo("Lectura transaccional sin columnas retiradas",error);}
            check(ar.com.ospim.util.ConnectionHelper.aperturas==0,"Ninguna operacion abre otra conexion");
            System.out.println("AFILIADO_REFERENCIAS_SQL_CHECKS="+checks+" FALLOS="+fallos+" COLUMNAS_AUSENTES_RECHAZADAS="+columnasAusentes+"; JDBC simulado sin DB ni UI");
            if(fallos>0)throw new Exception("El contrato fisico reducido aun usa columnas retiradas: "+fallos);
        }finally{campo.set(null,anterior);}
    }
    private static RequerimientoCompra entrada(int id,String cuil,int inte){
        RequerimientoCompra r=RequerimientoCompra.paraMapeo();r.setIdRequerimientoCompra(id);r.setEstado(1);r.setIdSector(Integer.valueOf(2));
        r.setAfiliadoCuilTitular(cuil);r.setAfiliadoInt(Integer.valueOf(inte));r.setCargoOspim(Integer.valueOf(100));r.setCargoTercerizadora(Integer.valueOf(0));
        r.setObservacionInterna("NOTA");r.setAfiliadoNombre("DTO NO PERSISTIR");r.setAfiliadoApellido("DTO NO PERSISTIR");
        r.setAfiliadoIdOspim(Integer.valueOf(999));r.setAfiliadoDocumentoTipo("X");r.setAfiliadoDocumentoNro("FICTICIO");
        r.setAfiliadoDireccion("DTO NO PERSISTIR");r.setAfiliadoLocalidad("DTO NO PERSISTIR");r.setAfiliadoProvincia("DTO NO PERSISTIR");
        r.setAfiliadoCelular("FICTICIO");r.setAfiliadoTelefono("FICTICIO");r.setAfiliadoEmail("FICTICIO");return r;
    }
    private static void sinCopias(Map<String,Object> fila,String mensaje){boolean ok=true;for(int i=0;i<COPIAS.length;i++)if(fila.containsKey(COPIAS[i]))ok=false;check(ok,mensaje);}
    private static void eq(Object a,Object b,String m){check(a==null?b==null:a.equals(b),m);}
    private static void check(boolean ok,String m){checks++;if(!ok)fallos++;System.out.println((ok?"OK ":"FALLO ")+m);}
    private static void fallo(String m,Exception e){checks++;fallos++;System.out.println("FALLO "+m+": "+e.getMessage());}
    private static class Estado implements InvocationHandler{
        Connection con;Map<String,Object> fila=new HashMap<String,Object>();Map<Integer,Object> ultimosParametros;
        boolean cerrada,maestroDeBaja;int commits,rollbacks;String nombreActual="MAESTRO ACTUAL";
        Set<String> columnas=new HashSet<String>(Arrays.asList(new String[]{"id_requerimiento","estado","id_sector","afiliado_cuil_titular","afiliado_int",
            "cargo_ospim","cargo_tercerizadora","id_tercerizadora","recupero","surge","legales","observaciones","alta_fecha","alta_usr",
            "modi_fecha","modi_usr","baja_fecha","baja_usr","motivo_baja","copia_cotizacion_enviada","observacion_interna"}));
        Estado(){fila.put("id_requerimiento",Integer.valueOf(41));fila.put("estado",Integer.valueOf(1));fila.put("id_sector",Integer.valueOf(2));
            fila.put("afiliado_cuil_titular","20111111112");fila.put("afiliado_int",Integer.valueOf(0));
            con=(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},this);}
        public Object invoke(Object p,Method m,Object[] a)throws Throwable{
            String n=m.getName();if("prepareCall".equals(n))throw new SQLException("No se admiten funciones SQL de lectura/escritura en esta prueba");
            if("prepareStatement".equals(n)){String sql=(String)a[0];validarEsquema(sql);
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PreparedStatement.class},new Sentencia(this,sql));}
            if("close".equals(n)){cerrada=true;return null;}if("isClosed".equals(n))return Boolean.valueOf(cerrada);
            if("commit".equals(n)){commits++;return null;}if("rollback".equals(n)){rollbacks++;return null;}return defecto(m.getReturnType());
        }
        void validarEsquema(String sql)throws SQLException{
            String consulta=sql.toLowerCase();
            for(int i=0;i<COPIAS.length;i++)if(Pattern.compile("\\br\\."+COPIAS[i]+"\\b").matcher(consulta).find())ausente(COPIAS[i]);
            if(consulta.startsWith("insert into compras.requerimiento ")){
                String[] nombres=consulta.substring(consulta.indexOf('(')+1,consulta.indexOf(')')).split(",");
                for(int i=0;i<nombres.length;i++)if(!columnas.contains(nombres[i].trim()))ausente(nombres[i]);
            }
            if(consulta.startsWith("update compras.requerimiento set ")){
                Matcher partes=Pattern.compile("([a-z_]+)\\s*=").matcher(consulta.substring(consulta.indexOf(" set ")+5,consulta.indexOf(" where ")));
                while(partes.find())if(!columnas.contains(partes.group(1)))ausente(partes.group(1));
            }
        }
        void ausente(String nombre)throws SQLException{columnasAusentes++;throw new SQLException("column "+nombre+" does not exist", "42703");}
    }
    private static class Sentencia implements InvocationHandler{
        Estado e;String sql;Map<Integer,Object> parametros=new HashMap<Integer,Object>();
        Sentencia(Estado estado,String consulta){e=estado;sql=consulta;}
        public Object invoke(Object p,Method m,Object[] a)throws Throwable{
            String n=m.getName();if(n.startsWith("set")&&a!=null&&a.length>1){parametros.put((Integer)a[0],"setNull".equals(n)?null:a[1]);return null;}
            if("executeQuery".equals(n)){
                List<Map<String,Object>> filas=new ArrayList<Map<String,Object>>();String consulta=sql.toLowerCase();e.ultimosParametros=parametros;
                if(consulta.startsWith("insert into compras.requerimiento ")){
                    String[] columnas=consulta.substring(consulta.indexOf('(')+1,consulta.indexOf(')')).split(",");
                    Map<String,Object> nueva=new HashMap<String,Object>();nueva.put("id_requerimiento",Integer.valueOf(41));
                    for(int i=0;i<columnas.length;i++)nueva.put(columnas[i].trim(),parametros.get(Integer.valueOf(i+1)));e.fila=nueva;
                    Map<String,Object> r=new HashMap<String,Object>();r.put("1",Integer.valueOf(41));filas.add(r);
                }else if(consulta.startsWith("update compras.requerimiento set ")){
                    int cantidad=parametros.size();Object id=parametros.get(Integer.valueOf(cantidad-1)),estado=parametros.get(Integer.valueOf(cantidad));
                    if(e.fila.get("id_requerimiento").equals(id)&&e.fila.get("estado").equals(estado)&&e.fila.get("baja_fecha")==null){
                        Matcher partes=Pattern.compile("([a-z_]+)\\s*=\\s*\\?").matcher(consulta.substring(consulta.indexOf(" set ")+5,consulta.indexOf(" where ")));int indice=1;
                        while(partes.find())e.fila.put(partes.group(1),parametros.get(Integer.valueOf(indice++)));
                        Map<String,Object> r=new HashMap<String,Object>();r.put("1",Integer.valueOf(41));filas.add(r);
                    }
                }else if(consulta.indexOf("from compras.requerimiento_detalle")>=0){
                    // El esquema sintetico define cero detalles para estos requerimientos de prueba.
                }else if(consulta.indexOf("from compras.requerimiento r")>=0){
                    Map<String,Object> r=new HashMap<String,Object>(e.fila);r.put("id",e.fila.get("id_requerimiento"));r.put("id_estado",e.fila.get("estado"));
                    r.put("sector_descripcion","DIABETES");r.put("requiere_afiliado",Boolean.TRUE);
                    if(consulta.indexOf("join public.afiliado a")>=0&&(!e.maestroDeBaja||consulta.indexOf("a.baja_fecha is null")<0)){
                        r.put("afiliado_id_ospim",Integer.valueOf(1001));r.put("afiliado_nombre",e.nombreActual);r.put("afiliado_apellido","MAESTRO SINTETICO");
                        r.put("afiliado_documento_tipo","X");r.put("afiliado_documento_nro","FICTICIO");r.put("afiliado_direccion","DOMICILIO ACTUAL");
                        r.put("afiliado_localidad","LOCALIDAD ACTUAL");r.put("afiliado_provincia","PROVINCIA ACTUAL");r.put("afiliado_email","FICTICIO");
                    }filas.add(r);
                }else throw new SQLException("Consulta no modelada por el esquema reducido: "+sql);
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ResultSet.class},new Filas(filas));
            }return defecto(m.getReturnType());
        }
    }
    private static class Filas implements InvocationHandler{
        List<Map<String,Object>> filas;int i=-1;boolean nulo;Filas(List<Map<String,Object>> r){filas=r;}
        public Object invoke(Object p,Method m,Object[] a)throws Throwable{
            String n=m.getName();if("next".equals(n))return Boolean.valueOf(++i<filas.size());if("wasNull".equals(n))return Boolean.valueOf(nulo);
            if("findColumn".equals(n)){if(!filas.get(i).containsKey((String)a[0]))throw new SQLException("Columna opcional no proyectada");return Integer.valueOf(1);}
            if(n.startsWith("get")&&a!=null&&a.length>0){Object v=filas.get(i).get(String.valueOf(a[0]));nulo=v==null;
                if("getInt".equals(n))return Integer.valueOf(v==null?0:((Number)v).intValue());if("getBoolean".equals(n))return Boolean.valueOf(Boolean.TRUE.equals(v));
                if("getString".equals(n))return v==null?null:String.valueOf(v);return v;}return defecto(m.getReturnType());
        }
    }
    private static Object defecto(Class c){if(c==Boolean.TYPE)return Boolean.FALSE;if(c==Integer.TYPE)return Integer.valueOf(0);if(c==Long.TYPE)return Long.valueOf(0);return null;}
}