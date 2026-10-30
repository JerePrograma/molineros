import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.NotificarCotizacionPrestadorHelper;
import ar.com.ospim.compras.requerimientos.service.*;

public class ComprasNotificacionJavaTest {
    private static int checks;
    private static void ok(boolean valor, String motivo) { if (!valor) throw new IllegalStateException(motivo); checks++; }
    private static Object defecto(Class tipo) {
        if (tipo == Boolean.TYPE) return Boolean.FALSE;
        if (tipo == Integer.TYPE) return Integer.valueOf(0);
        if (tipo == Long.TYPE) return Long.valueOf(0);
        return null;
    }
    private static Map<String,Object> fila(Object... datos) {
        Map<String,Object> f = new HashMap<String,Object>();
        for (int i=0;i<datos.length;i+=2) f.put((String)datos[i],datos[i+1]); return f;
    }
    private static final class BD {
        final ReentrantLock bloqueo = new ReentrantLock();
        final Timestamp ahora = Timestamp.valueOf("2026-10-30 11:00:00.123456");
        final List<Map<String,Object>> contactos = new ArrayList<Map<String,Object>>();
        final List<Map<String,Object>> notificados = new ArrayList<Map<String,Object>>();
        final List<String> sql = Collections.synchronizedList(new ArrayList<String>());
        String estado; int intentos; String email; String ultimoError; boolean existe;
        boolean habilitado = true; boolean compatible = true; boolean fallarEscritura; boolean carreraCAS; boolean falloCommit; boolean falloRollback; boolean falloPedidoTrasInsertar;
        Map<String,Object> pedido; int insercionesPedido; int commits; int rollbacks; int cierres;int cambiosEstado;String usuarioEstado;Timestamp bajaPrestador;
        BD() {
            contactos.add(contacto(1,"E","same@example.invalid",Timestamp.valueOf("2026-10-01 00:00:00"),null));
            contactos.add(contacto(2,"E","SAME@example.invalid",Timestamp.valueOf("2026-10-20 00:00:00"),null));
            contactos.add(contacto(3,"F","other@example.invalid",ahora,null));
            contactos.add(contacto(4,"E","future@example.invalid",ahora,Timestamp.valueOf("2026-10-30 11:00:00.123457")));
            contactos.add(contacto(5,"E","bad space@example.invalid",ahora,null));
            contactos.add(contacto(6,"E","tabbed@example.invalid" + (char)9,ahora,null));
            contactos.add(contacto(7,String.valueOf((char)9)+"E","wrongtype@example.invalid",ahora,null));
        }
        Map<String,Object> contacto(int id,String tipo,String email,Timestamp fecha,Timestamp vigencia) {
            return fila("id_contacto_e",Integer.valueOf(id),"tipo_contacto_e",tipo,"contacto",email,"contacto_clave",email.toLowerCase(Locale.ROOT),"modi_fecha",fecha,"contacto_desde",vigencia);
        }
        Connection conexion() {
            return (Connection)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Connection.class},new Conexion(this));
        }
    }
    private static final class Conexion implements InvocationHandler {
        final BD bd; int locks; boolean snap; int estadoReq; String estado; int intentos; String email; String ultimoError; boolean existe; Map<String,Object> pedido;
        Conexion(BD bd) { this.bd=bd; }
        void lock() {
            bd.bloqueo.lock(); locks++;
            if (!snap) { snap=true; estadoReq=BusquedaRequerimientoCompraServiceUtil.requerimiento.getEstado(); estado=bd.estado; intentos=bd.intentos; email=bd.email; existe=bd.existe; pedido=bd.pedido; }
        }
        void soltar() { while (locks>0) {locks--;bd.bloqueo.unlock();} snap=false; }
        public Object invoke(Object p, Method m, Object[] a) throws Throwable {
            String n=m.getName();
            if (n.equals("prepareStatement") || n.equals("prepareCall")) {
                String sql=(String)a[0]; bd.sql.add(sql);
                return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{n.equals("prepareCall")?CallableStatement.class:PreparedStatement.class},new Sentencia(this,sql));
            }
            if (n.equals("commit")) { bd.commits++;soltar(); if(bd.falloCommit)throw new SQLException("Commit ambiguo de prueba."); }
            if (n.equals("rollback")) { bd.rollbacks++;if(bd.falloRollback){soltar();throw new SQLException("Rollback fallido de prueba.");}if(snap){BusquedaRequerimientoCompraServiceUtil.requerimiento.setEstado(estadoReq);bd.estado=estado;bd.intentos=intentos;bd.email=email;bd.existe=existe;bd.pedido=pedido;}soltar(); }
            if (n.equals("close")) {bd.cierres++;soltar();}
            return defecto(m.getReturnType());
        }
    }
    private static final class Sentencia implements InvocationHandler {
        final Conexion con; final String sql; final Map<Integer,Object> p=new HashMap<Integer,Object>();
        Sentencia(Conexion con,String sql) {this.con=con;this.sql=sql;}
        public Object invoke(Object proxy,Method m,Object[] a) throws Throwable {
            String n=m.getName();
            if(n.startsWith("set")&&a!=null&&a.length>=2)p.put((Integer)a[0],n.equals("setNull")?null:a[1]);
            if(n.equals("executeUpdate"))return Integer.valueOf(escribir());
            if(n.equals("executeQuery"))return resultado();
            return defecto(m.getReturnType());
        }
        int escribir() throws SQLException {
            BD b=con.bd;if(b.fallarEscritura)throw new SQLException("Fallo JDBC de prueba.");
            if(sql.startsWith("UPDATE compras.requerimiento SET estado")) {
                if(b.carreraCAS)return 0;BusquedaRequerimientoCompraServiceUtil.requerimiento.setEstado(((Integer)p.get(1)).intValue());b.usuarioEstado=(String)p.get(2);b.cambiosEstado++;return 1;
            }
            if(sql.startsWith("INSERT INTO compras.requerimiento_cotizacion_prestador")) {
                if(b.existe)return 0;b.existe=true;b.estado=(String)p.get(3);b.intentos=0;b.email=(String)p.get(4);return 1;
            }
            if(sql.indexOf("SET estado_envio = ?, intentos = ?")>=0) {
                if(b.carreraCAS)return 0;
                if(!b.estado.equals(p.get(7))||b.intentos!=((Integer)p.get(8)).intValue())return 0;
                b.estado=(String)p.get(1);b.intentos=((Integer)p.get(2)).intValue();b.email=(String)p.get(3);return 1;
            }
            if(sql.indexOf("SET estado_envio = ?, fecha_envio = ?")>=0) {
                if(!b.estado.equals(p.get(7)))return 0;b.estado=(String)p.get(1);b.ultimoError=(String)p.get(3);return 1;
            }
            if(sql.startsWith("INSERT INTO compras.requerimiento_pedido_cotizacion")) {
                b.insercionesPedido++;b.pedido=fila("dl_group_id",p.get(4),"dl_folder_id",p.get(5),"dl_file_entry_id",p.get(6),"dl_file_uuid",p.get(7),"nombre_original",p.get(8),"nombre_persistido",p.get(9),"titulo",p.get(10));if(b.falloPedidoTrasInsertar)throw new SQLException("Resultado de INSERT incierto de prueba.");return 1;
            }
            throw new SQLException("SQL de prueba inesperado.");
        }
        ResultSet resultado() throws Exception {
            final BD b=con.bd;final List<Map<String,Object>> filas=new ArrayList<Map<String,Object>>();
            if(sql.indexOf("FOR UPDATE")>=0)con.lock();
            if(sql.indexOf("SELECT id_requerimiento FROM")>=0)filas.add(fila("1",Integer.valueOf(1388)));
            else if(sql.startsWith("SELECT estado_envio FROM")){if(b.existe)filas.add(fila("1",b.estado));}
            else if(sql.indexOf("FROM compras.requerimiento_cotizacion_prestador rcp")>=0){
                if(!b.notificados.isEmpty())filas.addAll(b.notificados);
                else if(b.existe)filas.add(fila("id_prestador",Integer.valueOf(8338),"descripcion","Proveedor de prueba","id_tipo_prestador",Integer.valueOf(1),"tipo_prestador","PRESTADOR","solicitar_cotizacion",Boolean.valueOf(b.habilitado),"baja_fecha",b.bajaPrestador,"estado_envio",b.estado,"email_destino",b.email));
            }
            else if(sql.indexOf("FROM public.prestador p")>=0)filas.add(fila("id_prestador",Integer.valueOf(8338),"descripcion","Proveedor de prueba","id_tipo_prestador",Integer.valueOf(1),"tipo_prestador","PRESTADOR","solicitar_cotizacion",Boolean.valueOf(b.habilitado),"estado_envio",b.estado,"email_destino",b.email));
            else if(sql.indexOf("FROM compras.requerimiento_detalle d")>=0)filas.add(fila("descripcion","INSUMOS DIABETES"));
            else if(sql.indexOf("FROM public.prestador_rubro")>=0)filas.add(fila("id_prestador",Integer.valueOf(8338),"rubro",b.compatible?"INSUMOS_DIABETES":"OTRO"));
            else if(sql.indexOf("FROM public.prestad_contacto_e")>=0)filas.addAll(b.contactos);
            else if(sql.indexOf("SELECT CURRENT_USER")>=0)filas.add(fila("1","usuario_db_prueba"));
            else if(sql.indexOf("SELECT LOCALTIMESTAMP")>=0||sql.indexOf("SELECT clock_timestamp()")>=0)filas.add(fila("1",b.ahora));
            else if(sql.indexOf("FROM compras.requerimiento_cotizacion_prestador")>=0) {if(b.existe)filas.add(fila("estado_envio",b.estado,"intentos",Integer.valueOf(b.intentos),"email_destino",b.email));}
            else if(sql.indexOf("FROM compras.requerimiento_pedido_cotizacion")>=0) {if(b.pedido!=null)filas.add(b.pedido);}
            else throw new Exception("Consulta de prueba inesperada.");
            return (ResultSet)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){
                int i=-1;boolean nulo;
                public Object invoke(Object p,Method m,Object[] a) {
                    String n=m.getName();if(n.equals("next")){i++;return Boolean.valueOf(i<filas.size());}
                    if(n.equals("wasNull"))return Boolean.valueOf(nulo);
                    if(n.startsWith("get")){
                        Object v=filas.get(i).get(String.valueOf(a[0]));nulo=v==null;
                        if(n.equals("getInt"))return Integer.valueOf(v==null?0:((Number)v).intValue());
                        if(n.equals("getLong"))return Long.valueOf(v==null?0:((Number)v).longValue());
                        if(n.equals("getBoolean"))return Boolean.valueOf(Boolean.TRUE.equals(v));return v;
                    }
                    return defecto(m.getReturnType());
                }
            });
        }
    }
    private static final class Helper extends NotificarCotizacionPrestadorHelper {
        final BD bd;Helper(BD b){bd=b;}
        protected NotificarCotizacionPrestadorServiceUtil.Transaccion abrirTransaccionNotificacion() throws Exception {
            Constructor c=NotificarCotizacionPrestadorServiceImpl.Transaccion.class.getDeclaredConstructor(new Class[]{NotificarCotizacionPrestadorServiceImpl.class,Connection.class});c.setAccessible(true);
            Object impl=c.newInstance(new Object[]{new NotificarCotizacionPrestadorServiceImpl(),bd.conexion()});
            Constructor u=NotificarCotizacionPrestadorServiceUtil.Transaccion.class.getDeclaredConstructor(new Class[]{NotificarCotizacionPrestadorServiceImpl.Transaccion.class});u.setAccessible(true);
            return (NotificarCotizacionPrestadorServiceUtil.Transaccion)u.newInstance(new Object[]{impl});
        }
    }
    private static RequerimientoCompraPedidoCotizacion documento(long id) {
        RequerimientoCompraPedidoCotizacion d=new RequerimientoCompraPedidoCotizacion();d.setIdRequerimiento(Integer.valueOf(1388));d.setIdPrestador(Integer.valueOf(8338));d.setDlGroupId(Long.valueOf(1));d.setDlFolderId(Long.valueOf(2));d.setDlFileEntryId(Long.valueOf(id));d.setDlFileUuid("uuid-prueba");d.setNombreOriginal("pedido.pdf");d.setNombrePersistido("pedido-prueba.pdf");d.setTitulo("Pedido de prueba");return d;
    }
    private static void configuracion() {
        Map<String,String> c=BusquedaRequerimientoCompraServiceUtil.configuraciones;c.clear();
        BusquedaRequerimientoCompraServiceUtil.permitirLecturaGlobalPreparacion=true;
        c.put("COMPRAS_USUARIO_DEFECTO_NOTIFICACION","sistema");
        c.put("COMPRAS_ESTADOS_PRESTADORES_ENVIADOS","2;3;4;5;6;7;99");
        c.put("COMPRAS_EXCEPCIONES_PRESTADOR_REQUERIMIENTO","NINGUNA");
        c.put("COMPRAS_TRANSICIONES_ESTADO","1=2;2=3;3=4;4=6;6=7;7=5");
        c.put("COMPRAS_ESTADOS_IDS","PENDIENTE=1;A_COTIZAR=2;COTIZADO=3;RECLAMO_RP=4;ORDEN_COMPRA=5;PARA_AUTORIZAR=6;AUTORIZADO=7;ANULADO=99");
        c.put("COMPRAS_ESTADOS_ENVIO","PENDIENTE=PENDIENTE;PROCESANDO=PROCESANDO;ENVIADO=ENVIADO;ERROR=ERROR;EMAIL_INVALIDO=EMAIL_INVALIDO;COTIZADO=COTIZADO");
        c.put("COMPRAS_ESTADOS_NOTIFICAR_PRESTADORES","1;2");c.put("COMPRAS_ESTADOS_NOTIFICAR_TODOS","1");
        c.put("COMPRAS_ENVIO_ESTADOS_REINTENTO","PENDIENTE;ERROR;EMAIL_INVALIDO");c.put("COMPRAS_ENVIO_ESTADOS_FINALES","ENVIADO;ERROR;EMAIL_INVALIDO");
        c.put("COMPRAS_CONTACTO_TIPOS_EMAIL","E");c.put("COMPRAS_CONTACTO_PRIORIDADES_EMAIL","E=1");c.put("COMPRAS_MAX_ERROR_NOTIFICACION","4000");
        RequerimientoCompra r=new RequerimientoCompra();r.setId(Integer.valueOf(1388));r.setEstado(1);BusquedaRequerimientoCompraServiceUtil.requerimiento=r;BusquedaRequerimientoCompraServiceUtil.permitirLecturaGlobalPreparacion=false;
    }
    public static void main(String[] args) throws Exception {
        configuracion();final BD b=new BD();final Helper h=new Helper(b);
        final List<ReservaCotizacionPrestador> resultados=Collections.synchronizedList(new ArrayList<ReservaCotizacionPrestador>());
        final List<Throwable> errores=Collections.synchronizedList(new ArrayList<Throwable>());
        Runnable tarea=new Runnable(){public void run(){try{resultados.add(h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba"));}catch(Throwable e){errores.add(e);}}};
        Thread t1=new Thread(tarea);Thread t2=new Thread(tarea);t1.start();t2.start();t1.join();t2.join();
        if(!errores.isEmpty())throw new Exception(errores.get(0));
        int ganadores=0;for(ReservaCotizacionPrestador r:resultados)if(r.isReservado())ganadores++;
        ok(ganadores==1&&b.intentos==1,"Dos reservas concurrentes: un ganador y un intento.");
        ok("SAME@example.invalid".equals(b.email),"SoloE vigente, dedupcase, masreciente y microsegundo futuro excluido.");
        ok(b.sql.get(0).indexOf("FOR UPDATE")>=0,"Cabecera bloqueada antes de decidir.");
        int intentos=b.intentos;h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");ok(b.intentos==intentos,"Doble envio PROCESANDO no reintenta.");
        FinalizacionCotizacionPrestador f=h.finalizarCotizacionPrestadorJava(1388,8338,"ENVIADO",null,"usuario_prueba");ok(f.isActualizado()&&"ENVIADO".equals(b.estado),"Finaliza enviado bajo CAS.");
        ok(!h.finalizarCotizacionPrestadorJava(1388,8338,"ERROR",null,"usuario_prueba").isActualizado(),"Finalizacion repetida no cambiaENVIADO.");
        b.estado="ERROR";BusquedaRequerimientoCompraServiceUtil.configuraciones.put("COMPRAS_CONTACTO_TIPOS_EMAIL","E;F");BusquedaRequerimientoCompraServiceUtil.configuraciones.put("COMPRAS_CONTACTO_PRIORIDADES_EMAIL","E=1;F=2");
        ok(h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba").isReservado()&&b.email.indexOf("other@example.invalid")>0,"CambioDB configuracion visible sin cache.");
        int i=h.registrarPedidoCotizacionDocumentoJava(documento(100),"usuario_prueba");ok(i==2&&b.insercionesPedido==1,"Documento del intentoactual.");
        ok(h.registrarPedidoCotizacionDocumentoJava(documento(100),"usuario_prueba")==i&&b.insercionesPedido==1,"Documento repetido idempotente.");
        boolean rechazo=false;try{h.registrarPedidoCotizacionDocumentoJava(documento(101),"usuario_prueba");}catch(Exception e){rechazo=true;}ok(rechazo&&b.insercionesPedido==1,"Otro documento mismo intento rechaza sin insertar.");
        b.estado="ERROR";b.fallarEscritura=true;int antes=b.intentos;rechazo=false;try{h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(SQLException e){rechazo=true;}ok(rechazo&&b.intentos==antes&&"ERROR".equals(b.estado),"Fallo escritura rollback total.");b.fallarEscritura=false;
        b.carreraCAS=true;rechazo=false;try{h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(Exception e){rechazo=true;}ok(rechazo&&b.intentos==antes,"CAS perdido no devuelve reserva falsa.");b.carreraCAS=false;
        BusquedaRequerimientoCompraServiceUtil.configuraciones.put("COMPRAS_ENVIO_ESTADOS_REINTENTO","");rechazo=false;try{h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(IllegalStateException e){rechazo=true;}ok(rechazo&&b.intentos==antes,"Configvacia falla cerrada.");configuracion();
        b.habilitado=false;rechazo=false;try{h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(Exception e){rechazo=true;}ok(rechazo,"Proveedor nohabilitado rechaza.");b.habilitado=true;
        b.compatible=false;rechazo=false;try{h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(Exception e){rechazo=true;}ok(rechazo,"Rubro incompatible rechaza.");b.compatible=true;
        b.estado="ERROR";b.falloCommit=true;rechazo=false;try{h.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(Exception e){rechazo=e.getClass().getSimpleName().equals("OperacionNotificacionAmbiguaException");}ok(rechazo&&"PROCESANDO".equals(b.estado),"Commitambiguo reportaestado incierto sin exito/compensacion destructiva.");
        configuracion();BD fachada=new BD();ar.com.ospim.util.ConnectionHelper.transaccionPreparada=fachada.conexion();
        ok(NotificarCotizacionPrestadorServiceUtil.reservarCotizacionPrestador(1388,8338,"usuario_prueba").isReservado(),"Fachada publica real ingresa al HelperJava.");
        boolean callNegocio=false;for(String sql:fachada.sql)if(sql.indexOf("call compras.reservar_notificacion")>=0)callNegocio=true;
        ok(!callNegocio&&ar.com.ospim.util.ConnectionHelper.transacciones==1,"Fachada no invoca funcionSQL de negocio.");
        BD negativo=new BD();Helper hn=new Helper(negativo);int previas=negativo.sql.size();rechazo=false;
        RequerimientoCompraPedidoCotizacion invalido=documento(200);invalido.setDlFolderId(Long.valueOf(0));
        try{hn.registrarPedidoCotizacionDocumentoJava(invalido,"usuario_prueba");}catch(Exception e){rechazo=true;}
        ok(rechazo&&negativo.sql.size()==previas,"DocumentoDL invalido rechaza antes de abrir TX.");
        ok(!hn.finalizarCotizacionPrestadorJava(1388,8338,"ERROR",null,"usuario_prueba").isActualizado(),"Fila inexistente no devuelve exito finalizacion.");
        BusquedaRequerimientoCompraServiceUtil.configuraciones.put("COMPRAS_CONTACTO_TIPOS_EMAIL","E;F");rechazo=false;
        try{hn.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(IllegalStateException e){rechazo=true;}
        ok(rechazo&&!negativo.existe,"Tipo permitido sin prioridad no crea reserva.");configuracion();
        BusquedaRequerimientoCompraServiceUtil.requerimiento.setEstado(3);rechazo=false;
        try{hn.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(Exception e){rechazo=true;}
        ok(rechazo&&!negativo.existe,"Estado vigente autoritativo rechaza antes de crear reserva.");configuracion();
        negativo.existe=true;negativo.estado="NO_CONFIGURADO";rechazo=false;
        try{hn.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(Exception e){rechazo=true;}
        ok(rechazo&&"NO_CONFIGURADO".equals(negativo.estado),"Estado desconocido falla cerrado.");
        BusquedaRequerimientoCompraServiceUtil.configuraciones.remove("COMPRAS_CONTACTO_TIPOS_EMAIL");rechazo=false;
        try{hn.reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");}catch(IllegalStateException e){rechazo=true;}
        ok(rechazo,"Configuracion no disponible no acepta todo.");
        configuracion();BD unicode=new BD();unicode.existe=true;unicode.estado="PROCESANDO";unicode.intentos=1;
        StringBuilder largo=new StringBuilder();for(int j=0;j<3999;j++)largo.append('A');largo.append(Character.toChars(0x1f642));largo.append('B');
        new Helper(unicode).finalizarCotizacionPrestadorJava(1388,8338,"ERROR",largo.toString(),"usuario_prueba");
        ok(unicode.ultimoError.codePointCount(0,unicode.ultimoError.length())==4000
                &&Character.isSurrogatePair(unicode.ultimoError.charAt(3999),unicode.ultimoError.charAt(4000)),"Limite4000caracteres preserva parsuplementario completo.");
        configuracion();BD bordes=new BD();Helper hb=new Helper(bordes);
        hb.reservarCotizacionPrestadorJava(1388,8338,null);
        rechazo=false;try{hb.finalizarCotizacionPrestadorJava(1388,8338,String.valueOf((char)9)+"ENVIADO",null,"usuario_prueba");}catch(Exception e){rechazo=true;}
        ok(rechazo&&"PROCESANDO".equals(bordes.estado),"TAB no se elimina del estado final como BTRIM SQL.");
        RequerimientoCompraPedidoCotizacion tab=documento(300);tab.setNombreOriginal(String.valueOf((char)9)+"pedido.pdf"+(char)9);
        hb.registrarPedidoCotizacionDocumentoJava(tab,null);
        ok(tab.getNombreOriginal().charAt(0)==9&&tab.getNombreOriginal().charAt(tab.getNombreOriginal().length()-1)==9,"BTRIM documental conserva TAB y obtiene auditor DB mismaTX.");
        BD minusculas=new BD();minusculas.contactos.clear();
        Map<String,Object> primero=minusculas.contacto(20,"E","i@example.invalid",Timestamp.valueOf("2026-10-01 00:00:00"),null);
        Map<String,Object> segundo=minusculas.contacto(21,"E","\u0130@example.invalid",Timestamp.valueOf("2026-10-20 00:00:00"),null);segundo.put("contacto_clave","i@example.invalid");
        minusculas.contactos.add(primero);minusculas.contactos.add(segundo);new Helper(minusculas).reservarCotizacionPrestadorJava(1388,8338,"usuario_prueba");
        ok("\u0130@example.invalid".equals(minusculas.email),"Dedup usa clave lower de LC_CTYPE instalada sin expansion Java ROOT.");
        BD incierto=new BD();incierto.existe=true;incierto.estado="PROCESANDO";incierto.intentos=1;incierto.falloPedidoTrasInsertar=true;incierto.falloRollback=true;rechazo=false;
        try{new Helper(incierto).registrarPedidoCotizacionDocumentoJava(documento(400),"usuario_prueba");}catch(Exception e){rechazo=e.getClass().getSimpleName().equals("OperacionNotificacionAmbiguaException");}
        ok(rechazo&&incierto.pedido!=null,"Rollback no confirmado conserva estado ambiguo para impedir compensacion DL destructiva.");
        configuracion();BD lectura=new BD();lectura.existe=true;lectura.estado="ENVIADO";lectura.email=" destino@example.invalid ";BusquedaRequerimientoCompraServiceUtil.requerimiento.setEstado(2);
        Connection prestada=lectura.conexion();List<PrestadorCotizacion> enviados=new Helper(lectura).listarPrestadoresEnviados(prestada,1388,3);
        ok(enviados.size()==1&&"destino@example.invalid".equals(enviados.get(0).getEmailDestino()),"Enviados conserva email destino BTRIM y resuelve contacto actual.");
        ok(lectura.commits==0&&lectura.rollbacks==0&&lectura.cierres==0,"Lectura prestada no concluye ni cierra TX caller.");
        lectura.compatible=false;ok(new Helper(lectura).listarPrestadoresEnviados(prestada,1388,3).isEmpty(),"Enviado incompatible se excluye solo A_COTIZAR.");
        BusquedaRequerimientoCompraServiceUtil.requerimiento.setEstado(3);lectura.habilitado=false;lectura.bajaPrestador=lectura.ahora;
        ok(new Helper(lectura).listarPrestadoresEnviados(prestada,1388,3).size()==1,"Historico COTIZADO conserva prestador sin solicitar/inactivo y sin rubro compatible.");
        BusquedaRequerimientoCompraServiceUtil.configuraciones.put("COMPRAS_ESTADOS_PRESTADORES_ENVIADOS","999");rechazo=false;try{new Helper(lectura).listarPrestadoresEnviados(prestada,1388,3);}catch(IllegalStateException e){rechazo=true;}ok(rechazo&&lectura.cierres==0,"Lista estados desconocidos falla cerrada sin cerrar caller.");
        configuracion();BD limite=new BD();BusquedaRequerimientoCompraServiceUtil.requerimiento.setEstado(3);
        limite.notificados.add(fila("id_prestador",Integer.valueOf(8000),"descripcion","A","estado_envio","ERROR"));limite.notificados.add(fila("id_prestador",Integer.valueOf(8338),"descripcion","B","estado_envio","ENVIADO"));limite.notificados.add(fila("id_prestador",Integer.valueOf(9000),"descripcion","C","estado_envio","COTIZADO"));
        enviados=new Helper(limite).listarPrestadoresEnviados(limite.conexion(),1388,1);ok(enviados.size()==1&&enviados.get(0).getIdPrestador()==8338,"Limite se aplica despues de filtrar, conservando orden SQL descripcion/CUIT/ID.");
        boolean ordenExacto=false;for(String q:limite.sql)if(q.indexOf("ORDER BY p.descripcion,p.cuit,p.id_prestador")>=0)ordenExacto=true;ok(ordenExacto,"Orden instalado se conserva como orden tecnico del lector.");
        configuracion();BD pendientes=new BD();ok(new Helper(pendientes).hayPrestadoresPendientesNotificacionJava(1388),"Hay pendientes usa mismos candidatos aunque contacto no determine elegibilidad.");
        pendientes.compatible=false;ok(!new Helper(pendientes).hayPrestadoresPendientesNotificacionJava(1388),"Sin rubro compatible no hay pendientes.");pendientes.compatible=true;BusquedaRequerimientoCompraServiceUtil.requerimiento.setEstado(3);ok(!new Helper(pendientes).hayPrestadoresPendientesNotificacionJava(1388),"Hay pendientes devuelve false en estado fuera del selector.");
        configuracion();BD confirmacion=new BD();Helper hc=new Helper(confirmacion);ok(hc.confirmarEnvioACotizarJava(1388,"usuario_prueba")==1&&confirmacion.cambiosEstado==0,"Sin ENVIADO persistido conserva PENDIENTE.");
        confirmacion.existe=true;confirmacion.estado="COTIZADO";ok(hc.confirmarEnvioACotizarJava(1388,"usuario_prueba")==1,"COTIZADO solo no reemplaza ENVIADO requerido por SQL instalado.");confirmacion.estado="ENVIADO";
        ok(hc.confirmarEnvioACotizarJava(1388,null)==2&&confirmacion.cambiosEstado==1&&"usuario_db_prueba".equals(confirmacion.usuarioEstado),"Confirmacion ENVIADO pasa a ACOTIZAR y conserva auditor defaultDB.");
        ok(hc.confirmarEnvioACotizarJava(1388,"usuario_prueba")==2&&confirmacion.cambiosEstado==1,"Confirmar envio repetido idempotente.");
        configuracion();BD casConfirmacion=new BD();casConfirmacion.existe=true;casConfirmacion.estado="ENVIADO";casConfirmacion.carreraCAS=true;rechazo=false;try{new Helper(casConfirmacion).confirmarEnvioACotizarJava(1388,"usuario_prueba");}catch(IllegalStateException e){rechazo=true;}ok(rechazo&&BusquedaRequerimientoCompraServiceUtil.requerimiento.getEstado()==1&&casConfirmacion.rollbacks==1,"CAS confirmacion perdido revierte sin exito falso.");
        ok(ar.com.ospim.util.ConnectionHelper.aperturas==0,"No abre otra conexion fuera TX.");
        System.out.println("APROBADAS="+checks+"; concurrencia JDBC simulada; sin DB, SMTP, DL ni UI reales.");
    }
}
