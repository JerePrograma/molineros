import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.documentos.*;
import ar.com.ospim.compras.requerimientos.helper.*;
import ar.com.ospim.compras.requerimientos.service.*;
import ar.com.ospim.global.beans.Empresa;

/** Helpers reales; unicamente datos, JDBC y DL se sustituyen en sus fronteras. */
public class ComprasDocumentosTransaccionTest {
    private interface Scenario {void run() throws Exception;}
    private static int passed;
    private static int failed;
    private static void test(String name,Scenario action) {
        try {action.run();passed++;System.out.println("PASS "+name);}
        catch(Throwable error) {failed++;System.out.println("FAIL "+name+": "+error);error.printStackTrace(System.out);}
    }
    private static void check(boolean value,String message) {if(!value)throw new AssertionError(message);}
    private static void reject(Scenario action) throws Exception {
        try {action.run();}catch(Exception expected){return;}
        throw new AssertionError("La operacion invalida fue aceptada.");
    }
    private static Object invoke(Method method,Object target,Object[] args) throws Exception {
        try{return method.invoke(target,args);}catch(InvocationTargetException error){if(error.getCause() instanceof Exception)throw (Exception)error.getCause();throw error;}
    }
    private static Object defaults(Class type) {
        if(type==Boolean.TYPE)return Boolean.FALSE;
        if(type==Integer.TYPE)return Integer.valueOf(0);
        if(type==Long.TYPE)return Long.valueOf(0);
        return null;
    }
    private static RequerimientoCompraPresupuesto copy(RequerimientoCompraPresupuesto original) throws Exception {
        RequerimientoCompraPresupuesto result=new RequerimientoCompraPresupuesto();
        for(Field field:RequerimientoCompraPresupuesto.class.getDeclaredFields()) {
            if(Modifier.isStatic(field.getModifiers()))continue;
            field.setAccessible(true);Object value=field.get(original);
            if(value instanceof java.util.Date)value=((java.util.Date)value).clone();
            field.set(result,value);
        }
        return result;
    }
    private static final class Fixture implements InvocationHandler {
        final List<String> events=new ArrayList<String>();
        final List<Integer> lockedDocumentIds=new ArrayList<Integer>();
        final ComprasFuenteDatosFixture data=new ComprasFuenteDatosFixture(){
            public RequerimientoCompraPresupuesto bloquearDocumento(Connection con,int req,int doc) throws Exception {
                events.add("lock-documento");lockedDocumentIds.add(Integer.valueOf(doc));return super.bloquearDocumento(con,req,doc);
            }
        };
        final Map<Integer,String> envios=new HashMap<Integer,String>();
        final List<Map<Integer,Object>> bindings=new ArrayList<Map<Integer,Object>>();
        final List<String> statements=new ArrayList<String>();
        Map<Integer,RequerimientoCompraPresupuesto> beforeDocuments;
        Map<Integer,String> beforeEnvios;
        final Connection con;
        final Storage service;
        final EditarRequerimientoCompraServiceUtil.Transaccion tx;
        RequerimientoCompra req;
        int inserts;
        int insertFailAt;
        int commits;
        int rollbacks;
        int closes;
        int comparisonWrites;
        int nextId=500;
        int adjudWrites;
        String headerAuditUser;
        boolean failAdjudMark;
        boolean useJdbcStatus;
        boolean failEmpresa;
        boolean failStatus;
        boolean failCommit;
        boolean failRollback;
        boolean rpPresent;
        boolean failComparison;
        Integer duplicateReq;
        String ultimaReceta;
        Fixture() throws Exception {
            data.cargarConfiguracionPrueba();data.instalar();
            data.configuracion.put("COMPRAS_ESTADOS_ADMINISTRAR_PRESUPUESTOS","42");
            req=RequerimientoCompra.paraMapeo();req.setIdRequerimientoCompra(7001);req.setEstado(42);
            req.setIdSector(Integer.valueOf(2));req.setSectorDescripcion("PRESTACIONES MEDICAS");
            data.requerimientos.put(Integer.valueOf(7001),req);
            RequerimientoCompraSector sector=new RequerimientoCompraSector();sector.setId(Integer.valueOf(2));sector.setDescripcion("LEGALES");data.sectores.add(sector);
            envios.put(Integer.valueOf(101),"ENVIADO");envios.put(Integer.valueOf(102),"ENVIADO");
            con=(Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class[]{Connection.class},this);
            service=new Storage(this);
            tx=transaction(service,con);
            Field instance=EditarRequerimientoCompraServiceUtil.class.getDeclaredField("instance");instance.setAccessible(true);instance.set(null,service);
            begin();
        }
        void begin() throws Exception {
            beforeDocuments=new HashMap<Integer,RequerimientoCompraPresupuesto>();
            for(Map.Entry<Integer,RequerimientoCompraPresupuesto> e:data.documentos.entrySet())beforeDocuments.put(e.getKey(),copy(e.getValue()));
            beforeEnvios=new HashMap<Integer,String>(envios);
        }
        void empresa() {req.setEstado(41);req.setSectorDescripcion("LEGALES");}
        void sameConnection() {
            for(Connection actual:data.conexiones)check(actual==con,"Se abrio una lectura fuera de la transaccion.");
            check(commits==0&&closes==0,"El Helper cerro o confirmo la transaccion del caller.");
        }
        public Object invoke(Object proxy,Method method,Object[] args) throws Throwable {
            String name=method.getName();
            if("prepareStatement".equals(name)||"prepareCall".equals(name)) {
                String sql=(String)args[0];statements.add(sql);
                if(sql.indexOf("FROM compras.requerimiento ")>=0&&sql.indexOf("FOR UPDATE")>=0)events.add("lock-cabecera");
                return statement(sql);
            }
            if("commit".equals(name)){commits++;if(failCommit)throw new SQLException("COMMIT_SIMULADO");return null;}
            if("rollback".equals(name)) {
                rollbacks++;if(failRollback)throw new SQLException("ROLLBACK_SIMULADO");
                data.documentos.clear();for(Map.Entry<Integer,RequerimientoCompraPresupuesto> e:beforeDocuments.entrySet())data.documentos.put(e.getKey(),copy(e.getValue()));
                envios.clear();envios.putAll(beforeEnvios);comparisonWrites=0;return null;
            }
            if("close".equals(name)){closes++;return null;}
            if("toString".equals(name))return "JDBC_FIXTURE_SIN_RED";
            return defaults(method.getReturnType());
        }
        Object statement(final String sql) {
            final Map<Integer,Object> values=new HashMap<Integer,Object>();bindings.add(values);
            return Proxy.newProxyInstance(CallableStatement.class.getClassLoader(),new Class[]{CallableStatement.class},new InvocationHandler(){
                public Object invoke(Object proxy,Method method,Object[] args) throws Throwable {
                    String name=method.getName();
                    if(name.startsWith("set")&&args!=null&&args.length>=2&&args[0] instanceof Integer){values.put((Integer)args[0],"setNull".equals(name)?null:args[1]);return null;}
                    if("getConnection".equals(name))return con;
                    if("executeUpdate".equals(name)) {
                        if(sql.indexOf("SET empresa_adjudicada = FALSE")>=0) {
                            adjudWrites++;events.add("limpiar-adjudicacion");for(RequerimientoCompraPresupuesto row:data.documentos.values())if(row.isCotizacionEmpresa())row.setEmpresaAdjudicada(false);
                        }
                        if(sql.indexOf("SET empresa_adjudicada = TRUE")>=0) {
                            if(failAdjudMark)throw new SQLException("ADJUDICACION_SIMULADA");adjudWrites++;events.add("marcar-adjudicacion");data.documentos.get((Integer)values.get(Integer.valueOf(1))).setEmpresaAdjudicada(true);
                        }
                        if(sql.startsWith("UPDATE compras.requerimiento SET modi_fecha")) {events.add("auditar-adjudicacion");headerAuditUser=(String)values.get(Integer.valueOf(1));}
                        if(sql.indexOf("compras.requerimiento_comparativa")>=0) {if(failComparison)throw new SQLException("COMPARATIVA_SIMULADA");comparisonWrites++;}
                        return Integer.valueOf(1);
                    }
                    if("executeQuery".equals(name)) {
                        boolean exists=true;int value=999;
                        if(sql.indexOf("SELECT 1 FROM compras.requerimiento_reclamo_prestacional")>=0)exists=rpPresent;
                        if(sql.indexOf("SELECT rp.id_requerimiento")>=0&&sql.indexOf("numero_receta")>=0){exists=duplicateReq!=null;value=duplicateReq==null?0:duplicateReq.intValue();}
                        if(sql.startsWith("INSERT")&&sql.indexOf("compras.requerimiento_comparativa")>=0){if(failComparison)throw new SQLException("COMPARATIVA_SIMULADA");comparisonWrites++;}
                        return rows(exists,value);
                    }
                    return defaults(method.getReturnType());
                }
            });
        }
        ResultSet rows(final boolean exists,final int value) {
            return (ResultSet)Proxy.newProxyInstance(ResultSet.class.getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){
                int cursor;
                public Object invoke(Object proxy,Method method,Object[] args) {
                    if("next".equals(method.getName()))return Boolean.valueOf(exists&&cursor++==0);
                    if("getInt".equals(method.getName()))return Integer.valueOf(value);
                    if("getString".equals(method.getName()))return String.valueOf(value);
                    return defaults(method.getReturnType());
                }
            });
        }
    }
    private static final class Storage extends EditarRequerimientoCompraServiceImpl {
        final Fixture f;
        Storage(Fixture fixture){f=fixture;}
        private void con(Connection actual){check(actual==f.con,"La escritura recibio otra conexion.");}
        public Transaccion abrirTransaccion() throws Exception {f.begin();return nativeTransaction(this,f.con);}
        public int insertarDocumento(Connection con,RequerimientoCompraPresupuesto p,String usuario) throws Exception {
            con(con);f.events.add("insertar");f.inserts++;
            if(f.insertFailAt==f.inserts)throw new SQLException("INSERT_DOC_SIMULADO");
            int id=++f.nextId;RequerimientoCompraPresupuesto row=copy(p);row.setIdRequerimientoPresupuesto(Integer.valueOf(id));f.data.documentos.put(Integer.valueOf(id),row);return id;
        }
        public String bloquearEstadoPrestadorCotizacion(Connection con,int req,int prest) {
            con(con);f.events.add("lock-prestador");return f.envios.get(Integer.valueOf(prest));
        }
        public boolean actualizarEstadoPrestadorCotizacion(Connection con,int req,int prest,String expected,String next,String user) throws Exception {
            con(con);f.events.add("estado:"+next);
            if(f.failStatus||!expected.equals(f.envios.get(Integer.valueOf(prest))))return false;
            if(f.useJdbcStatus&&!super.actualizarEstadoPrestadorCotizacion(con,req,prest,expected,next,user))return false;
            f.envios.put(Integer.valueOf(prest),next);return true;
        }
        public boolean cambiarBajaDocumento(Connection con,int req,int doc,boolean activate,boolean clear,String user) {
            con(con);f.events.add("baja:"+activate);RequerimientoCompraPresupuesto row=f.data.documentos.get(Integer.valueOf(doc));
            row.setBajaFecha(activate?null:new java.util.Date());if(clear)row.setEmpresaAdjudicada(false);return true;
        }
        public void bloquearClaveReceta(Connection con,int req,java.util.Date fecha,String receta) {con(con);f.events.add("lock-receta");f.ultimaReceta=receta;}
        public Integer buscarRequerimientoRecetaDuplicada(Connection con,int req,java.util.Date fecha,String receta,int excluded) {
            con(con);f.events.add("buscar-receta");check(excluded==48,"Estado excluido de receta no configurable");return f.duplicateReq;
        }
        public boolean actualizarEmpresaDocumento(Connection con,int req,int doc,String nombre,String cuit,String suc,String user) throws Exception {
            con(con);if(f.failEmpresa)throw new SQLException("SQL_PRIVADO_SIMULADO");f.events.add("empresa");RequerimientoCompraPresupuesto row=f.data.documentos.get(Integer.valueOf(doc));row.setDescripcionEmpresa(nombre);row.setEmpresaCuit(cuit);row.setEmpresaSucursal(suc);return true;
        }
    }
    private static EditarRequerimientoCompraServiceImpl.Transaccion nativeTransaction(EditarRequerimientoCompraServiceImpl impl,Connection con) throws Exception {
        Constructor ctor=EditarRequerimientoCompraServiceImpl.Transaccion.class.getDeclaredConstructor(new Class[]{EditarRequerimientoCompraServiceImpl.class,Connection.class});ctor.setAccessible(true);
        return (EditarRequerimientoCompraServiceImpl.Transaccion)ctor.newInstance(new Object[]{impl,con});
    }
    private static EditarRequerimientoCompraServiceUtil.Transaccion transaction(EditarRequerimientoCompraServiceImpl impl,Connection con) throws Exception {
        Constructor ctor=EditarRequerimientoCompraServiceUtil.Transaccion.class.getDeclaredConstructor(new Class[]{EditarRequerimientoCompraServiceImpl.Transaccion.class});ctor.setAccessible(true);
        return (EditarRequerimientoCompraServiceUtil.Transaccion)ctor.newInstance(new Object[]{nativeTransaction(impl,con)});
    }
    private static RequerimientoCompraPresupuesto presupuesto(int id,int prest) {
        RequerimientoCompraPresupuesto p=new RequerimientoCompraPresupuesto();p.setIdRequerimiento(Integer.valueOf(7001));p.setIdRequerimientoPresupuesto(Integer.valueOf(id));p.setIdPrestador(Integer.valueOf(prest));
        p.setDlGroupId(Long.valueOf(10136));p.setDlFolderId(Long.valueOf(200));p.setDlFileEntryId(Long.valueOf(300+id));p.setDlFileUuid("doc-fixture");p.setNombreOriginal("presupuesto.pdf");p.setNombrePersistido("presupuesto-"+id+".pdf");p.setTitulo("Presupuesto fixture");return p;
    }
    private static RequerimientoCompraPresupuesto empresa(int id,String cuit,String suc) {
        RequerimientoCompraPresupuesto p=presupuesto(id,101);p.setTipoDocumento(Integer.valueOf(3));p.setIdPrestador(null);p.setEmpresaCuit(cuit);p.setEmpresaSucursal(suc);p.setDescripcionEmpresa("Empresa fixture");return p;
    }
    private static OrdenMedicaValidada orden(String receta) {
        return new OrdenMedicaValidada(new File("fixture-validado.pdf"),"orden.pdf","pdf","application/pdf",java.sql.Date.valueOf("2020-10-10"),receta);
    }
    private static DocumentoComprasCreado documento() {return new DocumentoComprasCreado(10136,200,300,"uuid-fixture","orden-persistida.pdf","Orden fixture");}
    private static void miembro(Fixture f){f.req.setEstado(41);f.req.setAfiliadoCuilTitular("20000000001");f.req.setAfiliadoInt(Integer.valueOf(0));}
    private static List<RequerimientoCompraComparativa> comparativa(Fixture f) {
        RequerimientoCompraDetalle origen=new RequerimientoCompraDetalle();origen.setIdPrestacion(Integer.valueOf(1001));
        f.req.setDetalles(new ArrayList<RequerimientoCompraDetalle>());f.req.getDetalles().add(origen);
        RequerimientoCompraComparativa c=RequerimientoCompraComparativa.paraMapeo();c.setIdComparativa(10);c.setIdRequerimiento(7001);c.setIdPrestador(101);c.setPrestacionesEsperadas("1001");
        RequerimientoCompraComparativaDetalle d=new RequerimientoCompraComparativaDetalle();d.setIdDetalle(20);d.setIdPrestacion(1001);c.getDetalles().add(d);
        return new ArrayList<RequerimientoCompraComparativa>(Collections.singletonList(c));
    }
    private static Map<String,String> entrada() {
        Map<String,String> result=new HashMap<String,String>();result.put("prestador_101","101");result.put("fecha_101","10/10/2020");result.put("pago_101","30");result.put("plazo_101","24/48");result.put("validez_101","24");result.put("envio_101","Beneficiario");result.put("cantidad_101_1001","1");result.put("importe_101_1001","100");return result;
    }
    private static void noBusinessCall(Fixture f) {
        for(String sql:f.statements) {
            String lower=sql.toLowerCase(java.util.Locale.ROOT);
            check(lower.indexOf("call compras.registrar_requerimiento")<0&&lower.indexOf("call compras.baja_")<0&&lower.indexOf("call compras.reactivar_")<0&&lower.indexOf("bloquear_guardado_comparativa")<0&&lower.indexOf("call compras.editar_empresa")<0&&lower.indexOf("call compras.completar_empresa")<0&&lower.indexOf("call compras.guardar_empresa_adjudicada")<0,"CALL funcional en bridge publico: "+sql);
        }
    }
    private static Map<Integer,Object> binding(Fixture f,String fragment) {
        for(int i=f.statements.size()-1;i>=0;i--)if(f.statements.get(i).indexOf(fragment)>=0)return f.bindings.get(i);
        throw new AssertionError("Falta sentencia raw esperada: "+fragment);
    }
    private static File evidence;
    private static final class DlBoundary {
        final Object fixture;
        final Class fixtureType;
        final com.liferay.portal.service.ServiceContext context;
        final List<String> deleted=new ArrayList<String>();
        final List<String> created=new ArrayList<String>();
        final File pdf;
        boolean failDelete;
        DlBoundary() throws Exception {
            fixtureType=Class.forName("ComprasDocumentosPermisoAltaTest$Fixture");Constructor ctor=fixtureType.getDeclaredConstructor(new Class[0]);ctor.setAccessible(true);fixture=ctor.newInstance(new Object[0]);
            method("install").invoke(fixture,new Object[0]);Field field=fixtureType.getDeclaredField("context");field.setAccessible(true);context=(com.liferay.portal.service.ServiceContext)field.get(fixture);
            final Field service=com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil.class.getDeclaredField("_service");service.setAccessible(true);final Object original=service.get(null);
            service.set(null,Proxy.newProxyInstance(com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService.class.getClassLoader(),new Class[]{com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService.class},new InvocationHandler(){
                public Object invoke(Object proxy,Method m,Object[] args)throws Throwable {
                    if("deleteFileEntry".equals(m.getName())&&args!=null&&args.length==2) {
                        deleted.add(String.valueOf(args[1]));if(failDelete)throw new com.liferay.portal.SystemException("DELETE_DL_SIMULADO");return null;
                    }
                    try {Object result=m.invoke(original,args);if("addFileEntry".equals(m.getName()))created.add(String.valueOf(args[2]));return result;}catch(InvocationTargetException error){throw error.getCause();}
                }
            }));
            Method pdfMethod=ComprasDocumentosPermisoAltaTest.class.getDeclaredMethod("generarPdf",new Class[0]);pdfMethod.setAccessible(true);byte[] bytes=(byte[])pdfMethod.invoke(null,new Object[0]);
            pdf=File.createTempFile("documentos-tx-fixture-",".pdf",evidence);FileOutputStream output=new FileOutputStream(pdf);try{output.write(bytes);}finally{output.close();}
        }
        private Method method(String name)throws Exception {Method m=fixtureType.getDeclaredMethod(name,new Class[0]);m.setAccessible(true);return m;}
        void close()throws Exception {try{method("restore").invoke(fixture,new Object[0]);}finally{if(!pdf.delete())throw new IOException("No se pudo retirar PDF sintetico.");}}
        int adds()throws Exception {Field f=fixtureType.getDeclaredField("adds");f.setAccessible(true);return f.getInt(fixture);}
    }
    private static List<PresupuestoCompraHelper.PresupuestoEntrada> batch(Fixture f,DlBoundary dl,int count) {
        f.data.configuracion.put("COMPRAS_ROLES","VIEW=VIEW_COMPRAS;ABM=ABM_COMPRAS;ANULAR=ANULAR_COMPRAS;COTIZAR=COTIZAR_COMPRAS;AUTORIZAR=AUTORIZAR_COMPRAS");
        List<PresupuestoCompraHelper.PresupuestoEntrada> result=new ArrayList<PresupuestoCompraHelper.PresupuestoEntrada>();
        for(int i=0;i<count;i++) {PrestadorCotizacion prest=new PrestadorCotizacion();prest.setIdPrestador(101+i);prest.setDescripcion("Prestador fixture "+i);prest.setEstadoEnvio("ENVIADO");f.data.prestadores.add(prest);result.add(new PresupuestoCompraHelper.PresupuestoEntrada(i,dl.pdf,"presupuesto.pdf",101+i));}
        return result;
    }
    public static void main(String[] args) throws Exception {
        evidence=new File(args[0]);
        com.liferay.portal.kernel.util.PortalClassLoaderUtil.setClassLoader(ComprasDocumentosTransaccionTest.class.getClassLoader());
        com.liferay.portal.util.PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM=5;
        test("alta proveedor cambia ENVIADO a COTIZADO misma TX y sin commit interno",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();int id=new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,presupuesto(0,101),"fixture");
            check(id>0&&"COTIZADO".equals(f.envios.get(Integer.valueOf(101))),"alta o estado omitido");f.sameConnection();
        }});
        test("estado permitido de presupuesto viene de DB sin matriz fija",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.req.setEstado(43);f.data.configuracion.put("COMPRAS_ESTADOS_ADMINISTRAR_PRESUPUESTOS","43");
            new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,presupuesto(0,101),"fixture");f.sameConnection();
        }});
        test("estado fuera de configuracion no escribe presupuesto",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();f.req.setEstado(43);reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,presupuesto(0,101),"fixture");}});check(f.inserts==0,"escritura previa a validacion");
        }});
        test("duplicado activo de prestador rechaza y baja no bloquea nueva alta",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();RequerimientoCompraPresupuesto p=presupuesto(20,101);f.data.documentos.put(Integer.valueOf(20),p);
            reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,presupuesto(0,101),"fixture");}});
            p.setBajaFecha(new java.util.Date());new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,presupuesto(0,101),"fixture");
        }});
        test("proveedor no ENVIADO no inserta",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();f.envios.put(Integer.valueOf(101),"COTIZADO");reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,presupuesto(0,101),"fixture");}});check(f.inserts==0,"se inserto con estado invalido");
        }});
        test("ultimo presupuesto restaura ENVIADO con locks cabecera documento prestador",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.data.documentos.put(Integer.valueOf(20),presupuesto(20,101));f.envios.put(Integer.valueOf(101),"COTIZADO");
            check(new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,false,"fixture"),"baja sin resultado");
            check("ENVIADO".equals(f.envios.get(Integer.valueOf(101))),"no restauro ENVIADO");check(f.events.indexOf("lock-cabecera")<f.events.indexOf("lock-documento")&&f.events.indexOf("lock-documento")<f.events.indexOf("lock-prestador"),"orden de locks modificado");f.sameConnection();
        }});
        test("baja con otro presupuesto activo mantiene COTIZADO",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.data.documentos.put(Integer.valueOf(20),presupuesto(20,101));f.data.documentos.put(Integer.valueOf(21),presupuesto(21,101));f.envios.put(Integer.valueOf(101),"COTIZADO");
            new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,false,"fixture");check("COTIZADO".equals(f.envios.get(Integer.valueOf(101))),"restauro antes del ultimo");
        }});
        test("reactivacion restaura COTIZADO y rechaza otro activo",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();RequerimientoCompraPresupuesto p=presupuesto(20,101);p.setBajaFecha(new java.util.Date());f.data.documentos.put(Integer.valueOf(20),p);
            new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,true,"fixture");check(p.isActivo()&&"COTIZADO".equals(f.envios.get(Integer.valueOf(101))),"reactivacion incompleta");
            p.setBajaFecha(new java.util.Date());f.data.documentos.put(Integer.valueOf(21),presupuesto(21,101));reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,true,"fixture");}});
        }});
        test("fallo parcial de estado se revierte con rollback del caller",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();f.data.documentos.put(Integer.valueOf(20),presupuesto(20,101));f.envios.put(Integer.valueOf(101),"COTIZADO");f.begin();f.failStatus=true;
            reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,false,"fixture");}});f.tx.rollback();check(f.data.documentos.get(Integer.valueOf(20)).isActivo()&&"COTIZADO".equals(f.envios.get(Integer.valueOf(101))),"rollback no restauro ambos efectos");
        }});
        test("empresa duplica CUIT y sucursal nula sin escribir",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();f.empresa();f.data.documentos.put(Integer.valueOf(20),empresa(20,"20123456786",null));
            reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,empresa(0,"20123456786",null),"fixture");}});check(f.inserts==0,"duplicado empresa escrito");
        }});
        test("empresa con sucursal verifica padron en misma conexion",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.empresa();f.data.empresas.add(new Empresa("20123456786","0001","Empresa padron"));new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,empresa(0,"20123456786","0001"),"fixture");f.sameConnection();
        }});
        test("reactivacion empresa rechaza duplicado sin prestador",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();f.empresa();RequerimientoCompraPresupuesto p=empresa(20,"20123456786",null);p.setBajaFecha(new java.util.Date());f.data.documentos.put(Integer.valueOf(20),p);f.data.documentos.put(Integer.valueOf(21),empresa(21,"20123456786",null));
            reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,true,"fixture");}});check(!p.isActivo(),"reactivacion parcial");
        }});
        test("completar Empresa preserva sucursal y CUIT inmutable",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();f.empresa();f.data.empresas.add(new Empresa("20123456786","0001","Empresa padron"));f.data.documentos.put(Integer.valueOf(20),empresa(20,"20123456786","0001"));
            new PresupuestoCompraHelper().actualizarEmpresaEnTransaccion(f.tx,7001,20,null,"20123456786",null,true,"fixture");check("0001".equals(f.data.documentos.get(Integer.valueOf(20)).getEmpresaSucursal()),"borro sucursal");
            reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().actualizarEmpresaEnTransaccion(f.tx,7001,20,null,"20999999999",null,true,"fixture");}});f.sameConnection();
        }});
        test("editar Empresa admite OC configurada y preserva identidad",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.empresa();f.req.setEstado(45);f.data.documentos.put(Integer.valueOf(20),empresa(20,"20123456786",null));new PresupuestoCompraHelper().actualizarEmpresaEnTransaccion(f.tx,7001,20,"Nombre nuevo","20123456786",null,false,"fixture");check("Nombre nuevo".equals(f.data.documentos.get(Integer.valueOf(20)).getDescripcionEmpresa()),"nombre no actualizado");
        }});
        test("orden medica normaliza receta y bloquea antes de consultar duplicado",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();miembro(f);DocumentoLibraryComprasHelper.registrarOrdenMedicaEnTransaccion(f.tx,7001,orden(" a 1\t\n "),documento(),"fixture");
            check("A1".equals(f.ultimaReceta)&&"A1".equals(f.data.documentos.get(Integer.valueOf(501)).getNumeroReceta()),"normalizacion distinta");check(f.events.indexOf("lock-cabecera")<f.events.indexOf("lock-receta")&&f.events.indexOf("lock-receta")<f.events.indexOf("buscar-receta"),"orden de lock/consulta");f.sameConnection();
        }});
        test("receta duplicada no inserta documento",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();miembro(f);f.duplicateReq=Integer.valueOf(9001);reject(new Scenario(){public void run() throws Exception{DocumentoLibraryComprasHelper.registrarOrdenMedicaEnTransaccion(f.tx,7001,orden("a1"),documento(),"fixture");}});check(f.inserts==0,"duplicado insertado");
        }});
        test("receta opcional o requerimiento sin afiliado no toma lock funcional",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.req.setEstado(41);DocumentoLibraryComprasHelper.registrarOrdenMedicaEnTransaccion(f.tx,7001,orden(null),documento(),"fixture");check(!f.events.contains("lock-receta"),"lock indebido sin receta");
        }});
        test("limite receta viene de DB y techo de columna no se sobrepasa",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();miembro(f);f.data.configuracion.put("COMPRAS_LIMITES","RECETA_ORDEN_MEDICA=2");reject(new Scenario(){public void run() throws Exception{DocumentoLibraryComprasHelper.registrarOrdenMedicaEnTransaccion(f.tx,7001,orden("ABC"),documento(),"fixture");}});
            f.data.configuracion.put("COMPRAS_LIMITES","RECETA_ORDEN_MEDICA=101");reject(new Scenario(){public void run() throws Exception{DocumentoLibraryComprasHelper.registrarOrdenMedicaEnTransaccion(f.tx,7001,orden("AB"),documento(),"fixture");}});check(f.inserts==0,"limite incumplido insertado");
        }});
        test("config documental ausente falla sin fallback",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();f.data.configuracion.remove("COMPRAS_ESTADOS_ENVIO");reject(new Scenario(){public void run() throws Exception{new PresupuestoCompraHelper().registrarAsociacionEnTransaccion(f.tx,presupuesto(0,101),"fixture");}});check(f.inserts==0,"acepto config ausente");
        }});
        test("JDBC documental real usa INSERT y UPDATE parametrizados sin CALL negocio",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();EditarRequerimientoCompraServiceImpl real=new EditarRequerimientoCompraServiceImpl();real.insertarDocumento(f.con,presupuesto(0,101),"fixture");real.cambiarBajaDocumento(f.con,7001,20,false,false,"fixture");real.actualizarEstadoPrestadorCotizacion(f.con,7001,101,"ENVIADO","COTIZADO","fixture");
            for(String sql:f.statements)check(sql.indexOf("call compras.registrar_requerimiento")<0&&sql.indexOf("call compras.baja_requerimiento_presupuesto")<0,"decision SQL de negocio conservada en escritura raw");check(f.bindings.get(0).get(Integer.valueOf(1)).equals(Integer.valueOf(7001)),"parametro requerimiento cambiado");check(f.commits==0&&f.closes==0,"JDBC raw cierra conexion del caller");
        }});
        test("advisory SQL conserva namespace y origen autoritativo de la cabecera",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();new EditarRequerimientoCompraServiceImpl().bloquearClaveReceta(f.con,7001,java.sql.Date.valueOf("2020-10-10"),"A1");String sql=f.statements.get(0);check(sql.indexOf("hashtext('compras.orden_medica.receta')")>=0&&sql.indexOf("r.afiliado_cuil_titular")>=0&&sql.indexOf("chr(31)")>=0,"clave distinta de instalada");
        }});
        test("comparativa guarda JDBC real con guardas Java y conexion del caller",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();new RequerimientoCompraComparativaHelper().guardar(f.tx,comparativa(f),entrada(),"fixture");check(f.comparisonWrites==2,"cabecera y detalle no guardados");
            for(String sql:f.statements)check(sql.indexOf("bloquear_guardado_comparativa")<0,"decision en CALL negocio");f.sameConnection();
        }});
        test("comparativa rechaza dataset cambiado sin mutacion de entrada ni JDBC",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();final List<RequerimientoCompraComparativa> list=comparativa(f);f.req.getDetalles().clear();reject(new Scenario(){public void run() throws Exception{new RequerimientoCompraComparativaHelper().guardar(f.tx,list,entrada(),"fixture");}});check(f.comparisonWrites==0&&list.get(0).getFechaPresupuesto()==null,"mutacion anterior a guardas");
        }});
        test("comparativa rechaza IDs duplicados y RP incluso si no activo",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();final List<RequerimientoCompraComparativa> list=comparativa(f);list.get(0).setPrestacionesEsperadas("1001,1001");reject(new Scenario(){public void run() throws Exception{new RequerimientoCompraComparativaHelper().guardar(f.tx,list,entrada(),"fixture");}});
            list.get(0).setPrestacionesEsperadas("1001");f.rpPresent=true;reject(new Scenario(){public void run() throws Exception{new RequerimientoCompraComparativaHelper().guardar(f.tx,list,entrada(),"fixture");}});check(f.comparisonWrites==0,"guarda RP posterior a escritura");
        }});
        test("comparativa falla persistencia y rollback unico restaura JDBC",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();final List<RequerimientoCompraComparativa> list=comparativa(f);f.failComparison=true;reject(new Scenario(){public void run() throws Exception{new RequerimientoCompraComparativaHelper().guardar(list,entrada(),"fixture");}});check(f.rollbacks==1&&f.commits==0&&f.comparisonWrites==0,"rollback no atomico");
        }});
        test("opciones y defaults comparativa releen DB sin arrays funcionales",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();RequerimientoCompraComparativa c=new RequerimientoCompraComparativa();check(c.getFormaPago().intValue()==45&&"Beneficiario".equals(c.getEnvio()),"default legacy GUI distinto");
            f.data.configuracion.put("COMPRAS_COMPARATIVA_PAGO","75;90");f.data.configuracion.put("COMPRAS_COMPARATIVA_VALIDEZ","72;96");f.data.configuracion.put("COMPRAS_COMPARATIVA_PLAZO","7 dias;15 dias");f.data.configuracion.put("COMPRAS_COMPARATIVA_ENVIO","Destino & especial;Retiro");f.data.configuracion.put("COMPRAS_COMPARATIVA_DEFAULTS","PAGO=90;PLAZO=7 dias;VALIDEZ=96;ENVIO=Destino & especial");
            c=new RequerimientoCompraComparativa();check(c.getFormaPago().intValue()==90&&c.getValidezPresupuesto().intValue()==96&&"Destino & especial".equals(c.getEnvio()),"default cacheado");check(f.data.lecturas("COMPRAS_COMPARATIVA_PAGO")==2,"lecturas constructor extra");
            List<RequerimientoCompraComparativa> list=comparativa(f);Map<String,String> input=entrada();input.put("pago_101","75");input.put("validez_101","72");input.put("plazo_101","15 dias");input.put("envio_101","Destino & especial");new RequerimientoCompraComparativaHelper().aplicarEntrada(f.tx,list,input);check(list.get(0).getFormaPago().intValue()==75,"rechazo nueva opcion DB");f.sameConnection();
        }});
        test("paraMapeo comparativa evita lecturas ocultas y preserva historico",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.data.configuracion.clear();RequerimientoCompraComparativa c=RequerimientoCompraComparativa.paraMapeo();c.setFormaPago(Integer.valueOf(30));check(f.data.lecturas.isEmpty()&&c.getFormaPago().intValue()==30,"factory consulto DB o aplico default");
        }});
        test("TODAS opciones numericas se validan aunque se elija una valida",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();final List<RequerimientoCompraComparativa> list=comparativa(f);
            for(String bad:new String[]{"30;mal","30;0","30;2147483648","30;030","30;30"}) {
                f.data.configuracion.put("COMPRAS_COMPARATIVA_PAGO",bad);reject(new Scenario(){public void run() throws Exception{new RequerimientoCompraComparativaHelper().aplicarEntrada(f.tx,list,entrada());}});
            }
            f.data.configuracion.put("COMPRAS_COMPARATIVA_PAGO","30;45;60");f.data.configuracion.put("COMPRAS_COMPARATIVA_VALIDEZ","24;bad");reject(new Scenario(){public void run() throws Exception{RequerimientoCompraComparativaHelper.cargarOpciones();}});check(list.get(0).getFechaPresupuesto()==null,"mutacion previa a validar configcompleta");
        }});
        test("defaults ausentes extra duplicados o fuera lista fallan sin fallback",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();for(String bad:new String[]{"PAGO=45;PLAZO=48/72;VALIDEZ=48","PAGO=45;PLAZO=48/72;VALIDEZ=48;ENVIO=Beneficiario;EXTRA=x","PAGO=75;PLAZO=48/72;VALIDEZ=48;ENVIO=Beneficiario","PAGO=45;PAGO=45;PLAZO=48/72;VALIDEZ=48;ENVIO=Beneficiario"}) {
                f.data.configuracion.put("COMPRAS_COMPARATIVA_DEFAULTS",bad);reject(new Scenario(){public void run() throws Exception{new RequerimientoCompraComparativa();}});
            }
        }});
        test("opciones vacias duplicadas controles o ausentes fallan cerradas",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();for(String bad:new String[]{"x;","x;x","x;\nY",""}) {f.data.configuracion.put("COMPRAS_COMPARATIVA_ENVIO",bad);reject(new Scenario(){public void run() throws Exception{RequerimientoCompraComparativaHelper.cargarOpciones();}});}
            f.data.configuracion.remove("COMPRAS_COMPARATIVA_ENVIO");reject(new Scenario(){public void run() throws Exception{new RequerimientoCompraComparativa();}});
        }});
        test("batch real DL y presupuesto confirma una vez sin compensar",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();DlBoundary dl=new DlBoundary();try {int count=new PresupuestoCompraHelper().guardarPresupuestos(7001,batch(f,dl,1),dl.context,"fixture");check(count==1&&f.commits==1&&f.rollbacks==0&&f.data.documentos.size()==1&&dl.adds()==1&&dl.deleted.isEmpty(),"alta no atomica");for(Connection con:f.data.conexiones)check(con==f.con,"lectura funcional global fueraTX");}finally{dl.close();}
        }});
        test("fallo segundo presupuesto revierte todos y compensa DL en orden inverso",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();final DlBoundary dl=new DlBoundary();try {final List<PresupuestoCompraHelper.PresupuestoEntrada> files=batch(f,dl,2);f.insertFailAt=2;reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarPresupuestos(7001,files,dl.context,"fixture");}});check(f.rollbacks==1&&f.commits==0&&f.data.documentos.isEmpty()&&"ENVIADO".equals(f.envios.get(Integer.valueOf(101))),"rollback parcial");check(dl.adds()==2&&dl.deleted.size()==2&&dl.deleted.get(0).equals(dl.created.get(1))&&dl.deleted.get(1).equals(dl.created.get(0)),"compensacion DL incompleta o desordenada");}finally{dl.close();}
        }});
        test("commit incierto conserva archivo DL y no comunica exito",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();final DlBoundary dl=new DlBoundary();try {final List<PresupuestoCompraHelper.PresupuestoEntrada> files=batch(f,dl,1);f.failCommit=true;reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarPresupuestos(7001,files,dl.context,"fixture");}});check(f.commits==1&&dl.adds()==1&&dl.deleted.isEmpty(),"elimino archivo con commit incierto");}finally{dl.close();}
        }});
        test("rollback incierto conserva archivos y error de compensacion no da exito",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();final DlBoundary dl=new DlBoundary();try {final List<PresupuestoCompraHelper.PresupuestoEntrada> files=batch(f,dl,1);f.insertFailAt=1;f.failRollback=true;reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarPresupuestos(7001,files,dl.context,"fixture");}});check(dl.adds()==1&&dl.deleted.isEmpty(),"elimino archivo con rollback incierto");}finally{dl.close();}
            final Fixture f2=new Fixture();final DlBoundary dl2=new DlBoundary();try {final List<PresupuestoCompraHelper.PresupuestoEntrada> files=batch(f2,dl2,1);f2.insertFailAt=1;dl2.failDelete=true;reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarPresupuestos(7001,files,dl2.context,"fixture");}});check(f2.rollbacks==1&&dl2.deleted.size()==1,"no intento compensacion confirmada");}finally{dl2.close();}
        }});
        test("empresa valida catalogo activo aun si sector no seleccionable",new Scenario(){public void run() throws Exception{
            final Fixture f=new Fixture();f.empresa();f.data.documentos.put(Integer.valueOf(20),empresa(20,"20123456786",null));
            Map<String,Object> row=new HashMap<String,Object>();row.put("id",Integer.valueOf(2));row.put("activo",Boolean.TRUE);f.data.catalogoSectores.add(row);f.data.sectores.clear();
            new PresupuestoCompraHelper().actualizarEmpresaEnTransaccion(f.tx,7001,20,"Nombre","20123456786",null,false,"fixture");
            row.put("activo",Boolean.FALSE);reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().actualizarEmpresaEnTransaccion(f.tx,7001,20,"Otro","20123456786",null,false,"fixture");}});
            row.put("activo",Boolean.TRUE);row.put("baja",new java.util.Date());reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().actualizarEmpresaEnTransaccion(f.tx,7001,20,"Otro","20123456786",null,false,"fixture");}});f.sameConnection();
        }});
        test("fallo JDBC empresa entrega mensaje seguro y rollback confirmado",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.empresa();f.data.documentos.put(Integer.valueOf(20),empresa(20,"20123456786",null));f.failEmpresa=true;
            try{new PresupuestoCompraHelper().editarEmpresaCotizacion(7001,20,"Nombre","20123456786",null,"fixture");throw new AssertionError("fallo JDBC aceptado");}
            catch(Exception expected){check(expected.getMessage().indexOf("SQL_PRIVADO")<0&&expected.getCause()!=null,"SQLException filtrada aUI");}
            check(f.rollbacks==1&&f.commits==0,"rollback empresa omitido");
        }});
        test("API publica presupuesto alta baja reactivacion reutiliza guardasJava",new Scenario(){public void run()throws Exception{
            Fixture f=new Fixture();int id=EditarRequerimientoCompraServiceUtil.registrarPresupuesto(presupuesto(0,101),"fixture");
            check(EditarRequerimientoCompraServiceUtil.darDeBajaPresupuesto(id,7001,"fixture"),"bridge baja omitido");check(EditarRequerimientoCompraServiceUtil.reactivarPresupuesto(id,7001),"bridge reactivacion omitido");
            check(f.commits==3&&f.rollbacks==0&&f.data.documentos.get(Integer.valueOf(id)).isActivo()&&"COTIZADO".equals(f.envios.get(Integer.valueOf(101))),"bridge no atomico");noBusinessCall(f);
        }});
        test("API publica empresa respeta tipo y metadata con mismaTX",new Scenario(){public void run()throws Exception{
            Fixture f=new Fixture();f.empresa();int id=EditarRequerimientoCompraServiceUtil.registrarPresupuesto(empresa(0,"20123456786",null),"fixture");
            check(!EditarRequerimientoCompraServiceUtil.darDeBajaPresupuesto(id,7001,"fixture"),"bridge presupuesto acepto documento empresa");
            EditarRequerimientoCompraServiceUtil.editarEmpresaCotizacion(7001,id,"Nombre API","20123456786",null,"fixture");
            check(EditarRequerimientoCompraServiceUtil.darDeBajaCotizacionEmpresa(id,7001,"fixture")&&EditarRequerimientoCompraServiceUtil.reactivarCotizacionEmpresa(id,7001),"bridge empresa no delego");check("Nombre API".equals(f.data.documentos.get(Integer.valueOf(id)).getDescripcionEmpresa()),"metadata omitida");noBusinessCall(f);
        }});
        test("API Transaccion presupuesto y orden mantienen conexion sin commit",new Scenario(){public void run()throws Exception{
            Fixture f=new Fixture();f.empresa();int id=f.tx.registrarPresupuesto(empresa(0,"20123456786",null),"fixture");check(id>0,"TX presupuesto omitido");f.sameConnection();noBusinessCall(f);
            Fixture om=new Fixture();miembro(om);int doc=om.service.registrarOrdenMedica(om.con,7001,orden("API1"),documento(),"fixture");check(doc>0&&"API1".equals(om.ultimaReceta),"bridge orden no valido receta");om.sameConnection();noBusinessCall(om);
        }});
        test("API Comparativa publica valida snapshot Java sin CALLdeguarda",new Scenario(){public void run()throws Exception{
            final Fixture f=new Fixture();final List<RequerimientoCompraComparativa> list=comparativa(f);new RequerimientoCompraComparativaHelper().aplicarEntrada(f.tx,list,entrada());
            new RequerimientoCompraComparativaServiceImpl().guardar(list,new ArrayList<Integer>(),"fixture");check(f.commits==1&&f.comparisonWrites==2,"bridge comparativa no guardo");
            f.req.getDetalles().clear();reject(new Scenario(){public void run()throws Exception{new RequerimientoCompraComparativaServiceImpl().guardar(list,new ArrayList<Integer>(),"fixture");}});check(f.rollbacks==1,"guarda Java API no rechazo snapshot");noBusinessCall(f);
        }});
        test("habilitacion busqueda empresa preserva false y no exige sector activo",new Scenario(){public void run()throws Exception{
            Fixture f=new Fixture();PresupuestoCompraHelper helper=new PresupuestoCompraHelper();check(!helper.esRequerimientoHabilitadoBusquedaEmpresaCotizacion(0)&&!helper.esRequerimientoHabilitadoBusquedaEmpresaCotizacion(-1)&&!helper.esRequerimientoHabilitadoBusquedaEmpresaCotizacion(9000),"ID invalido o inexistente habilitado");
            check(!helper.esRequerimientoHabilitadoBusquedaEmpresaCotizacion(7001),"no pendiente habilitado");f.empresa();f.data.sectores.clear();check(helper.esRequerimientoHabilitadoBusquedaEmpresaCotizacion(7001),"consulta bool exige sectoractivo adicional");
            f.req.setBajaFecha(new java.util.Date());check(!helper.esRequerimientoHabilitadoBusquedaEmpresaCotizacion(7001),"baja habilitada");f.req.setBajaFecha(null);f.req.setSectorDescripcion("PRESTACIONES MEDICAS");check(!helper.esRequerimientoHabilitadoBusquedaEmpresaCotizacion(7001),"sector fuera config habilitado");
            f.data.configuracion.put("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR","PRESTACIONES MEDICAS");check(helper.esRequerimientoHabilitadoBusquedaEmpresaCotizacion(7001),"sector de DB cacheado");
        }});
        test("raw documental normaliza auditoria por mismaCon y conserva tabs",new Scenario(){public void run()throws Exception{
            Fixture f=new Fixture();EditarRequerimientoCompraServiceImpl real=new EditarRequerimientoCompraServiceImpl();real.insertarDocumento(f.con,presupuesto(0,101),null);
            check(f.statements.get(0).toLowerCase(java.util.Locale.ROOT).indexOf("current_user")>=0,"fallback no se leyo en conexioncaller");check("999".equals(f.bindings.get(f.bindings.size()-1).get(Integer.valueOf(17))),"no uso current_user en INSERT");
            real.insertarDocumento(f.con,presupuesto(0,101)," \tfixture\t ");check("\tfixture\t".equals(f.bindings.get(f.bindings.size()-1).get(Integer.valueOf(17))),"trim removio tabs noASCII de contrato");check(f.commits==0&&f.closes==0,"auditoria cierra conexion");
        }});
        test("Action real respeta maximo adjuntos DB superior a20 y retorno UI1",new Scenario(){public void run()throws Exception{
            Fixture f=new Fixture();f.data.configuracion.put("COMPRAS_LIMITES","ORDENES_MEDICAS_POR_CARGA=30");
            final Map<String,String> values=new HashMap<String,String>();final Map<String,String> output=new HashMap<String,String>();values.put("orden_medica_count","25");
            javax.portlet.ActionRequest request=(javax.portlet.ActionRequest)Proxy.newProxyInstance(javax.portlet.ActionRequest.class.getClassLoader(),new Class[]{javax.portlet.ActionRequest.class},new InvocationHandler(){public Object invoke(Object p,Method m,Object[] a){if("getParameter".equals(m.getName()))return values.get((String)a[0]);return defaults(m.getReturnType());}});
            javax.portlet.ActionResponse response=(javax.portlet.ActionResponse)Proxy.newProxyInstance(javax.portlet.ActionResponse.class.getClassLoader(),new Class[]{javax.portlet.ActionResponse.class},new InvocationHandler(){public Object invoke(Object p,Method m,Object[] a){if("setRenderParameter".equals(m.getName())&&a[1] instanceof String)output.put((String)a[0],(String)a[1]);return defaults(m.getReturnType());}});
            ar.com.ospim.compras.requerimientos.action.EditarRequerimientoCompraAction action=new ar.com.ospim.compras.requerimientos.action.EditarRequerimientoCompraAction();
            Method count=action.getClass().getDeclaredMethod("obtenerCantidadOrdenesMedicas",new Class[]{javax.portlet.ActionRequest.class});count.setAccessible(true);
            check(((Integer)invoke(count,action,new Object[]{request})).intValue()==25,"Action mantiene maximo fijo20");
            Method copy=action.getClass().getDeclaredMethod("copiarParametrosOrdenesMedicas",new Class[]{javax.portlet.ActionRequest.class,javax.portlet.ActionResponse.class});copy.setAccessible(true);invoke(copy,action,new Object[]{request,response});check("25".equals(output.get("orden_medica_count")),"retorno recorta por20");
            f.data.configuracion.put("COMPRAS_LIMITES","ORDENES_MEDICAS_POR_CARGA=2");values.put("orden_medica_count","3");try{invoke(count,action,new Object[]{request});throw new AssertionError("acepto excedente configurado");}catch(Exception expected){check(expected.getMessage().indexOf("2")>=0,"mensaje no usa limiteactual");}
            values.remove("orden_medica_count");check(((Integer)invoke(count,action,new Object[]{request})).intValue()==1,"defaultUI1 perdido");values.put("orden_medica_count","invalido");invoke(copy,action,new Object[]{request,response});check("1".equals(output.get("orden_medica_count")),"retorno error perdio filaUI1");
        }});
        test("reactivacion proveedor audita politica DB y Empresa conserva ausencia de auditor",new Scenario(){public void run()throws Exception{
            final Fixture f=new Fixture();RequerimientoCompraPresupuesto doc=presupuesto(20,101);doc.setBajaFecha(new java.util.Date());f.data.documentos.put(Integer.valueOf(20),doc);f.useJdbcStatus=true;
            EditarRequerimientoCompraServiceUtil.reactivarPresupuesto(20,7001);check("sistema".equals(binding(f,"SET estado_envio").get(Integer.valueOf(2))),"no preservo sistema configuradoinstalled");
            doc.setBajaFecha(new java.util.Date());f.envios.put(Integer.valueOf(101),"ENVIADO");f.data.configuracion.put("COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION","auditor_configurado");new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,true,"usuario_recibido");check("auditor_configurado".equals(binding(f,"SET estado_envio").get(Integer.valueOf(2))),"politica cacheada o reemplazada por caller");
            StringBuilder longUser=new StringBuilder();for(int i=0;i<101;i++)longUser.append('a');doc.setBajaFecha(new java.util.Date());f.envios.put(Integer.valueOf(101),"ENVIADO");f.data.configuracion.put("COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION",longUser.toString());new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,true,null);check(((String)binding(f,"SET estado_envio").get(Integer.valueOf(2))).length()==100,"LEFT100 estructural perdido");
            doc.setBajaFecha(new java.util.Date());f.envios.put(Integer.valueOf(101),"ENVIADO");f.data.configuracion.remove("COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION");reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,20,true,null);}});check(!doc.isActivo(),"config ausente reactivo parcialmente");
            f.empresa();RequerimientoCompraPresupuesto company=empresa(30,"20123456786",null);company.setBajaFecha(new java.util.Date());f.data.documentos.put(Integer.valueOf(30),company);int reads=f.data.lecturas("COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION");check(new PresupuestoCompraHelper().cambiarAsociacionEnTransaccion(f.tx,7001,30,true,null),"empresa no reactiva");check(reads==f.data.lecturas("COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION"),"empresa aplica politica no existente eninstalled");
        }});
        test("adjudicacion porID publica limpia antes de marcar y audita mismaTX",new Scenario(){public void run()throws Exception{
            Fixture f=new Fixture();f.empresa();RequerimientoCompraPresupuesto old=empresa(30,"20123456786",null);old.setEmpresaAdjudicada(true);f.data.documentos.put(Integer.valueOf(30),old);f.data.documentos.put(Integer.valueOf(20),empresa(20,null,null));
            EditarRequerimientoCompraServiceUtil.guardarEmpresaAdjudicada(7001,20,"fixture");check(f.data.documentos.get(Integer.valueOf(20)).isEmpresaAdjudicada()&&!old.isEmpresaAdjudicada(),"adjudicacion no unica");check(f.events.indexOf("limpiar-adjudicacion")<f.events.indexOf("marcar-adjudicacion")&&f.events.indexOf("marcar-adjudicacion")<f.events.indexOf("auditar-adjudicacion"),"orden UPDATE causa unique transitorio");check(f.lockedDocumentIds.equals(Arrays.asList(new Integer[]{Integer.valueOf(20),Integer.valueOf(30)})),"presupuestos no bloqueadosordenados");check(f.commits==1&&"fixture".equals(f.headerAuditUser),"auditoria o commit omitido");noBusinessCall(f);
        }});
        test("adjudicacionCUIT verifica padron unica activa y limpia conambosnulos",new Scenario(){public void run()throws Exception{
            final Fixture f=new Fixture();f.empresa();f.data.empresas.add(new Empresa("20123456786","0001","Padron"));f.data.documentos.put(Integer.valueOf(20),empresa(20,"20123456786","0001"));new PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(f.tx,7001," 20123456786 "," 0001 ","fixture");check(f.data.documentos.get(Integer.valueOf(20)).isEmpresaAdjudicada(),"CUIT no selecciono fuente");
            f.data.documentos.put(Integer.valueOf(30),empresa(30,"20123456786","0001"));reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(f.tx,7001,"20123456786","0001","fixture");}});
            reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(f.tx,7001,"20123456786",null,"fixture");}});new PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(f.tx,7001,(String)null,(String)null,"fixture");check(!f.data.documentos.get(Integer.valueOf(20)).isEmpresaAdjudicada(),"no limpio seleccion");f.sameConnection();noBusinessCall(f);
        }});
        test("adjudicacion rechaza estado sectorbaja tipoodescripcion antes de escribir",new Scenario(){public void run()throws Exception{
            final Fixture f=new Fixture();f.empresa();f.data.documentos.put(Integer.valueOf(20),empresa(20,null,null));f.req.setEstado(42);reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(f.tx,7001,20,"fixture");}});f.req.setEstado(41);Map<String,Object> sector=new HashMap<String,Object>();sector.put("id",Integer.valueOf(2));sector.put("activo",Boolean.FALSE);f.data.catalogoSectores.add(sector);reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(f.tx,7001,20,"fixture");}});sector.put("activo",Boolean.TRUE);f.data.documentos.get(Integer.valueOf(20)).setDescripcionEmpresa(" ");reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(f.tx,7001,20,"fixture");}});reject(new Scenario(){public void run()throws Exception{new PresupuestoCompraHelper().guardarEmpresaAdjudicadaEnTransaccion(f.tx,7001,-1,"fixture");}});check(f.adjudWrites==0,"guarda posterior a escritura");
        }});
        test("falloalmarcar adjudicacion revierte limpieza enAPI real y noCALL",new Scenario(){public void run()throws Exception{
            final Fixture f=new Fixture();f.empresa();RequerimientoCompraPresupuesto old=empresa(30,null,null);old.setEmpresaAdjudicada(true);f.data.documentos.put(Integer.valueOf(30),old);f.data.documentos.put(Integer.valueOf(20),empresa(20,null,null));f.failAdjudMark=true;reject(new Scenario(){public void run()throws Exception{EditarRequerimientoCompraServiceUtil.guardarEmpresaAdjudicada(7001,20,"fixture");}});check(f.rollbacks==1&&f.commits==0&&f.data.documentos.get(Integer.valueOf(30)).isEmpresaAdjudicada()&&!f.data.documentos.get(Integer.valueOf(20)).isEmpresaAdjudicada(),"limpieza parcial sinrollback");noBusinessCall(f);
        }});
        System.out.println("RESULT passed="+passed+" failed="+failed);
        if(failed>0)System.exit(1);
    }
}