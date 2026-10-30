import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper;
import ar.com.ospim.compras.requerimientos.service.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/** Guardas Helper y persistencias JDBC reales, datos de lectura simulados. */
public class ComprasReservaRpJavaTest {
    private static int pruebas;
    public static void main(String[] args)throws Exception {
        RequerimientoCompraReclamoPrestacionalHelper h=new RequerimientoCompraReclamoPrestacionalHelper();
        Estado e=new Estado();RequerimientoCompraReclamoPrestacionalTransaccion tx=e.tx();
        comprobar(h.reservarCreacionEnTransaccion(tx,1388,"  nonce  ",null)&&e.insertados==1,"Reserva valida cabecera y persiste INSERT crudo bajo misma TX");
        comprobar("nonce".equals(e.relacion.getTokenReserva())&&"db_actual".equals(e.relacion.getAltaUsr()),"Reserva conserva btrim SQL y auditoria current_user de la conexion");
        comprobar(e.eventos.indexOf("ADVISORY")<e.eventos.indexOf("RP")&&e.eventos.indexOf("RP")<e.eventos.indexOf("VINCULO")
                &&e.eventos.indexOf("VINCULO")<e.eventos.indexOf("CABECERA"),"Reserva conserva orden advisory RP vinculo cabecera");
        comprobar(e.commits==0&&!e.cerrada&&e.callNegocio==0,"Overload con TX no confirma ni cierra conexion caller ni llama SQL de negocio");
        String[] estados={"RESERVADO","VINCULADO","ERROR"};
        for(int i=0;i<estados.length;i++){
            e=new Estado();e.relacion=relacion(estados[i],i>0?Integer.valueOf(88):null);boolean error=false;
            try{h.reservarCreacionEnTransaccion(e.tx(),1388,"nonce","u");}catch(Exception esperado){error=true;}
            comprobar(error&&e.insertados==0,"Reserva existente "+estados[i]+" no se pisa ni informa exito falso");
        }
        e=new Estado();e.estado=2;comprobar(rechazaReserva(h,e)&&e.insertados==0,"Reserva exige estado COTIZADO autoritativo");
        e=new Estado();e.estado=33;e.config.put("COMPRAS_ESTADOS_IDS","COTIZADO=33");
        comprobar(h.reservarCreacionEnTransaccion(e.tx(),1388,"nonce","u")&&e.insertados==1,"Reserva usa estado COTIZADO configurado sin ID3 fijo");
        e=new Estado();e.baja=new Timestamp(1);comprobar(rechazaReserva(h,e),"Cabecera de baja no se reserva");
        e=new Estado();e.cuil="   ";comprobar(rechazaReserva(h,e),"Reserva rechaza afiliado vacio con btrim SQL");
        e=new Estado();e.integrante=null;comprobar(rechazaReserva(h,e),"Reserva rechaza integrante NULL");
        e=new Estado();e.sinCabecera=true;comprobar(rechazaReserva(h,e),"Reserva rechaza cabecera inexistente");
        e=new Estado();boolean error=false;try{h.reservarCreacionEnTransaccion(e.tx(),1388,"   ","u");}catch(Exception esperado){error=true;}
        comprobar(error&&e.insertados==0,"Token vacio no genera reserva");
        e=new Estado();e.relacion=relacion("RESERVADO",null);
        comprobar(h.finalizarCreacionEnTransaccion(e.tx(),1388,"nonce",88,null)&&e.actualizados==1
                &&e.relacion.isVinculado()&&e.relacion.getTokenReserva()==null&&e.relacion.getUltimoError()==null,
                "Finalizacion valida token y CAS crudo limpia reserva y error");
        comprobar("db_actual".equals(e.relacion.getModiUsr()),"Finalizacion audita con identidad de misma conexion");
        e=new Estado();e.relacion=relacion("VINCULADO",Integer.valueOf(88));
        comprobar(h.finalizarCreacionEnTransaccion(e.tx(),1388,"otro",88,null)&&e.actualizados==0,"Reintento ya vinculado al mismo RP es idempotente sin mutacion");
        e=new Estado();e.relacion=relacion("RESERVADO",null);error=false;
        try{h.finalizarCreacionEnTransaccion(e.tx(),1388,"otro",88,"u");}catch(Exception esperado){error=true;}
        comprobar(error&&e.actualizados==0,"Token diferente impide finalizacion");
        e=new Estado();e.relacion=relacion("VINCULADO",Integer.valueOf(88));error=false;
        try{h.finalizarCreacionEnTransaccion(e.tx(),1388,"nonce",99,"u");}catch(Exception esperado){error=true;}
        comprobar(error&&e.actualizados==0,"RP diferente no reemplaza vinculo existente");
        e=new Estado();e.relacion=relacion("RESERVADO",null);e.falloCas=true;error=false;
        try{h.finalizarCreacionEnTransaccion(e.tx(),1388,"nonce",88,"u");}catch(Exception esperado){error=true;}
        comprobar(error&&e.actualizados==0&&e.commits==0,"CAS fallido no informa finalizacion exitosa");
        e=new Estado();e.relacion=relacion("RESERVADO",null);
        comprobar(h.liberarReservaEnTransaccion(e.tx(),1388,"nonce")&&e.eliminados==1&&e.relacion==null,"Liberacion borra solo reserva propia sin RP creado");
        e=new Estado();e.relacion=relacion("RESERVADO",null);
        comprobar(!h.liberarReservaEnTransaccion(e.tx(),1388,"otro")&&e.eliminados==0,"Liberacion con otro token no borra");
        e=new Estado();e.relacion=relacion("RESERVADO",Integer.valueOf(88));
        comprobar(!h.liberarReservaEnTransaccion(e.tx(),1388,"nonce")&&e.eliminados==0,"Liberacion no borra fila que conserva RP creado");
        e=new Estado();e.relacion=relacion("RESERVADO",null);
        comprobar(h.marcarErrorEnTransaccion(e.tx(),1388,"nonce",88,"   ",null)&&e.relacion.isError()
                &&"Error de vinculacion no especificado.".equals(e.relacion.getUltimoError())&&"nonce".equals(e.relacion.getTokenReserva()),
                "Registro de error aplica default DB y conserva token como SQL instalado");
        e=new Estado();e.relacion=relacion("ERROR",Integer.valueOf(88));
        comprobar(h.marcarErrorEnTransaccion(e.tx(),1388,"otro",88,"nuevo","u")&&e.actualizados==0,"Reintento ERROR del mismo RP es idempotente sin modificar diagnostico");
        e=new Estado();e.relacion=relacion("RESERVADO",null);e.falloCas=true;
        comprobar(!h.marcarErrorEnTransaccion(e.tx(),1388,"nonce",88,"error","u")&&e.actualizados==0,"CAS de error fallido no produce exito falso");
        e=new Estado();e.relacion=relacion("RESERVADO",null);e.config.put("COMPRAS_RP_MAX_ERROR","2");
        String unicode="A\uD83D\uDE00B";
        comprobar(h.marcarErrorEnTransaccion(e.tx(),1388,"nonce",88,unicode,"u")&&unicode.substring(0,3).equals(e.relacion.getUltimoError()),
                "Limite configurable de error cuenta caracteres Unicode como left PostgreSQL");
        e=new Estado();e.relacion=relacion("RESERVADO",null);e.config.put("COMPRAS_RP_MAX_ERROR","mal");error=false;
        try{h.marcarErrorEnTransaccion(e.tx(),1388,"nonce",88,"error","u");}catch(ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.ConfiguracionInvalidaException esperado){error=true;}
        comprobar(error&&e.actualizados==0,"Limite de error malformado bloquea escritura con tipo seguro");
        e=new Estado();Datos datos=new Datos(e);
        comprobar(datos.reservarCreacion(e.con,1388,"nonce"," u ")&&"u".equals(e.relacion.getAltaUsr())&&!e.cerrada&&e.commits==0,
                "Bridge publico Connection ejecuta guardas Java y conserva control TX del caller");
        System.out.println("APROBADAS_RESERVA_RP="+pruebas+"; Helper/JDBC reales con datos simulados, sin DB ni UI");
    }
    private static boolean rechazaReserva(RequerimientoCompraReclamoPrestacionalHelper h,Estado e)throws Exception{try{h.reservarCreacionEnTransaccion(e.tx(),1388,"nonce","u");return false;}catch(Exception esperado){return true;}}
    private static void comprobar(boolean ok,String nombre)throws Exception{if(!ok)throw new Exception("FALLO: "+nombre);pruebas++;System.out.println("OK "+nombre);}
    private static RequerimientoCompraReclamoPrestacional relacion(String estado,Integer id){RequerimientoCompraReclamoPrestacional r=new RequerimientoCompraReclamoPrestacional();r.setIdRequerimientoCompra(1388);r.setEstado(estado);r.setTokenReserva("nonce");r.setIdReclamoPrestacional(id);r.setAltaUsr("u");return r;}
    private static class Datos extends RequerimientoCompraReclamoPrestacionalServiceImpl {
        private Estado e;Datos(Estado estado){e=estado;}
        public RequerimientoCompraReclamoPrestacional obtenerPorRequerimiento(Connection con,int id)throws Exception{if(con!=e.con)throw new Exception("Otra conexion");return e.relacion;}
    }
    private static class Estado implements InvocationHandler {
        Connection con;RequerimientoCompraReclamoPrestacional relacion;Map<String,String> config=new HashMap<String,String>();
        List<String> eventos=new ArrayList<String>();Integer integrante=Integer.valueOf(0);String cuil="20111111112";int estado=3;
        Timestamp baja;boolean sinCabecera,cerrada,falloCas;int insertados,actualizados,eliminados,commits,callNegocio;
        Estado(){config.put("COMPRAS_ESTADOS_IDS","COTIZADO=3");config.put("COMPRAS_RP_MAX_ERROR","2000");config.put("COMPRAS_RP_ERROR_DEFECTO","Error de vinculacion no especificado.");con=(Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},this);}
        RequerimientoCompraReclamoPrestacionalTransaccion tx(){return RequerimientoCompraReclamoPrestacionalTransaccion.sobreConexion(new Datos(this),con);}
        public Object invoke(Object p,Method m,Object[] a)throws Throwable{
            String n=m.getName();if("close".equals(n)){cerrada=true;return null;}if("isClosed".equals(n))return Boolean.valueOf(cerrada);
            if("commit".equals(n)){commits++;return null;}if("rollback".equals(n))return null;
            if("prepareCall".equals(n)||"prepareStatement".equals(n))return sentencia((String)a[0]);throw new UnsupportedOperationException(n);
        }
        private Object sentencia(final String sql){
            if(sql.contains("reservar_reclamo_prestacional")||sql.contains("finalizar_reclamo_prestacional")||sql.contains("liberar_reserva_reclamo_prestacional")||sql.contains("marcar_error_reclamo_prestacional")){callNegocio++;throw new IllegalStateException("CALL de negocio prohibido");}
            Class<?> api=sql.startsWith("{call")?CallableStatement.class:PreparedStatement.class;
            return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{api},new InvocationHandler(){
                Map<Integer,Object> v=new HashMap<Integer,Object>();
                public Object invoke(Object p,Method m,Object[] a)throws Throwable{
                    String n=m.getName();if(n.startsWith("set")){v.put((Integer)a[0],"setNull".equals(n)?null:a[1]);return null;}if("close".equals(n))return null;
                    if("execute".equals(n)){eventos.add("ADVISORY");return Boolean.FALSE;}
                    if("executeUpdate".equals(n)){
                        if(sql.startsWith("INSERT INTO compras.requerimiento_reclamo")){if(falloCas||relacion!=null)return Integer.valueOf(0);relacion=relacion((String)v.get(2),null);relacion.setTokenReserva((String)v.get(3));relacion.setAltaUsr((String)v.get(4));insertados++;return Integer.valueOf(1);}
                        if(sql.startsWith("UPDATE compras.requerimiento_reclamo")){if(falloCas)return Integer.valueOf(0);relacion.setIdReclamoPrestacional((Integer)v.get(1));relacion.setEstado((String)v.get(2));relacion.setUltimoError((String)v.get(3));relacion.setModiUsr((String)v.get(4));if(sql.contains("token_reserva=NULL"))relacion.setTokenReserva(null);actualizados++;return Integer.valueOf(1);}
                        if(sql.startsWith("DELETE FROM compras.requerimiento_reclamo")){if(falloCas)return Integer.valueOf(0);relacion=null;eliminados++;return Integer.valueOf(1);}throw new SQLException(sql);
                    }
                    if("executeQuery".equals(n)){
                        List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();Map<String,Object> f=new HashMap<String,Object>();
                        if(sql.startsWith("SELECT rp.id"))eventos.add("RP");
                        else if(sql.startsWith("SELECT id_requerimiento FROM compras.requerimiento_reclamo"))eventos.add("VINCULO");
                        else if(sql.startsWith("SELECT id_requerimiento FROM compras.requerimiento"))eventos.add("CABECERA");
                        else if(sql.startsWith("SELECT estado,baja_fecha")){if(!sinCabecera){f.put("estado",Integer.valueOf(estado));f.put("baja_fecha",baja);f.put("afiliado_cuil_titular",cuil);f.put("afiliado_int",integrante);rows.add(f);}}
                        else if(sql.startsWith("SELECT valor FROM public.system_config")){f.put("valor",config.get((String)v.get(1)));rows.add(f);}
                        else if(sql.startsWith("SELECT current_user")){f.put("1","db_actual");rows.add(f);}else throw new SQLException(sql);
                        return resultado(rows);
                    }throw new UnsupportedOperationException(n);
                }});
        }
        private ResultSet resultado(final List<Map<String,Object>> rows){return (ResultSet)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){int pos=-1;boolean nulo;
            public Object invoke(Object p,Method m,Object[] a)throws Throwable{String n=m.getName();if("next".equals(n))return Boolean.valueOf(++pos<rows.size());if("close".equals(n))return null;if("wasNull".equals(n))return Boolean.valueOf(nulo);Object v=rows.get(pos).get(String.valueOf(a[0]));nulo=v==null;if("getInt".equals(n))return v!=null?v:Integer.valueOf(0);if("getString".equals(n)||"getTimestamp".equals(n))return v;throw new UnsupportedOperationException(n);}});}
    }
}
