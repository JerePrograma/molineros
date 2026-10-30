import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import javax.portlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import ar.com.ospim.autorizaciones.action.*;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;

public class ComprasFechasComprobanteTest {
    private static int passed;
    private static int failed;
    private interface Scenario { void run() throws Exception; }
    private static void test(String name, Scenario scenario) {
        try { scenario.run(); passed++; System.out.println("PASS " + name); }
        catch (Throwable error) { failed++; System.out.println("FAIL " + name + ": " + error); }
    }
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static Date date(String value) throws Exception {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value);
    }
    private static PrestacionesReclamo row(int id, String type, String prestation) throws Exception {
        PrestacionesReclamo row = new PrestacionesReclamo();
        row.setIdRegistro(id); row.setId_prestacion(1000+id);
        row.setCodigoPrestacion("COD"+id); row.setDescripcion("Fila "+id);
        row.setEstado(PrestacionesReclamo.ESTADOS.MODIF);
        row.setComprobanteTipo(type); row.setComprobanteNro("ANTERIOR"+id);
        row.setComprobanteLetra("B"); row.setComprobanteSucursal("0002");
        row.setComprobanteFecha(date("2020-10-25 12:00"));
        row.setFechaPrestacion(prestation==null?null:date(prestation));
        row.setCantidad(2); row.setImporte(300); row.setObservaciones("antes "+id);
        return row;
    }
    private static byte[] snapshot(Object value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes); out.writeObject(value); out.close();
        return bytes.toByteArray();
    }
    private static class Fixture implements InvocationHandler {
        final Map<String,String> params = new HashMap<String,String>();
        final Map<String,Object> attributes = new HashMap<String,Object>();
        final Map<String,Object> sessionValues = new HashMap<String,Object>();
        final ArrayList<PrestacionesReclamo> rows = new ArrayList<PrestacionesReclamo>();
        HttpSession session;
        RenderRequest request;
        PortletSession portletSession;
        final ComprasFuenteDatosFixture datos = new ComprasFuenteDatosFixture();
        ReclamoPrestacionalCompraContexto contexto;
        Fixture() throws Exception {
            datos.cargarConfiguracionPrueba(); datos.instalar();
            rows.add(row(1,"OTR","2020-10-01 12:00"));
            rows.add(row(2,"OTR","2020-10-05 12:00"));
            rows.add(row(3,"RCB","2020-10-07 12:00"));
            rows.add(row(4,"OTR","2020-10-29 12:00"));
            rows.get(3).setEstado(PrestacionesReclamo.ESTADOS.BAJA);
            sessionValues.put(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION,rows);
            session=(HttpSession)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpSession.class},new InvocationHandler(){
                public Object invoke(Object proxy,Method method,Object[] args) {
                    String name=method.getName();
                    if ("getAttribute".equals(name)) return sessionValues.get(args[0]);
                    if ("setAttribute".equals(name)) {sessionValues.put((String)args[0],args[1]);return null;}
                    if ("removeAttribute".equals(name)) {sessionValues.remove(args[0]);return null;}
                    return primitiveDefault(method.getReturnType());
                }
            });
            portletSession=(PortletSession)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PortletSession.class},Proxy.getInvocationHandler(session));
            request=(RenderRequest)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{RenderRequest.class},this);
            PortalUtil.request=(HttpServletRequest)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpServletRequest.class},this);
            params.put("idRegistro","1"); params.put("grabaedicion","true");
            params.put("cpbte_tipo","FCP"); params.put("cpbte_nro","NUEVO");
            params.put("cpbte_letra","A"); params.put("cpbte_sucursal","0001");
            params.put("cpbte_dia","10"); params.put("cpbte_mes","9"); params.put("cpbte_anio","2020");
            params.put("fecha_prestacion_dia","1"); params.put("fecha_prestacion_mes","9"); params.put("fecha_prestacion_anio","2020");
            params.put("cantidad","7"); params.put("importe","100"); params.put("observaciones","despues");
            params.put("codigoSeguimiento_filtro_edit","");
            params.put("aplicarFechaCompras","true");
            RequerimientoCompra requerimiento = new RequerimientoCompra(Integer.valueOf(7001));
            requerimiento.setEstado(43); requerimiento.setIdSector(Integer.valueOf(2));
            requerimiento.setAfiliadoCuilTitular("20000000001"); requerimiento.setAfiliadoInt(Integer.valueOf(0));
            Map<Integer,Integer> origenes = new HashMap<Integer,Integer>();
            for (PrestacionesReclamo fila : rows) {
                RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();
                int idDetalle = 500 + fila.getIdRegistro();
                detalle.setId(Integer.valueOf(idDetalle)); detalle.setIdRequerimientoCompra(7001);
                detalle.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR);
                detalle.setIdTipoPrestacion(Integer.valueOf(8)); detalle.setIdPrestacion(Integer.valueOf(fila.getId_prestacion()));
                detalle.setIdTipoNomenclador(Integer.valueOf(3)); detalle.setCodigoNomenclador(fila.getCodigoPrestacion());
                detalle.setDescripcionNomenclador(fila.getDescripcion()); requerimiento.getDetalles().add(detalle);
                origenes.put(Integer.valueOf(fila.getIdRegistro()),Integer.valueOf(idDetalle));
                Nomenclador canonico = new Nomenclador(); canonico.setId_prestacion(fila.getId_prestacion());
                canonico.setId_tipo_nomenclador(3); canonico.setCodigo(fila.getCodigoPrestacion()); canonico.setDescripcion(fila.getDescripcion());
                datos.canonicos.put(Integer.valueOf(canonico.getId_prestacion()),canonico);
            }
            datos.requerimientos.put(Integer.valueOf(7001),requerimiento);
            contexto = new ReclamoPrestacionalCompraContexto(7001,"20000000001",Integer.valueOf(0),"fixture",System.currentTimeMillis(),"fixture-nonce");
            ReclamoPrestacional borrador = new ReclamoPrestacional(); contexto.setReclamoEnEdicion(borrador);
            contexto.definirDetallesOrigen(origenes);
            sessionValues.put(WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA,contexto);
            sessionValues.put(WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION,borrador);
            ReclamosBaseAction.contexto=contexto;
            ReclamosBaseAction.compras=true;
        }
        public Object invoke(Object proxy,Method method,Object[] args) {
            String name=method.getName();
            if ("getSession".equals(name)) return session;
            if ("getPortletSession".equals(name)) return portletSession;
            if ("getParameter".equals(name)) return params.get(args[0]);
            if ("getAttribute".equals(name)) return attributes.get(args[0]);
            if ("setAttribute".equals(name)) {attributes.put((String)args[0],args[1]);return null;}
            if ("removeAttribute".equals(name)) {attributes.remove(args[0]);return null;}
            if ("getParameterMap".equals(name)) return Collections.emptyMap();
            if ("hashCode".equals(name)) return Integer.valueOf(System.identityHashCode(proxy));
            if ("equals".equals(name)) return Boolean.valueOf(proxy==args[0]);
            return primitiveDefault(method.getReturnType());
        }
        void execute() throws Exception {
            ActionMapping mapping = new ActionMapping(){
                public ActionForward findForward(String name) {return new ActionForward(name);}
            };
            ActionForward forward=new EditarPrestacionReclamoAction().render(mapping,null,null,request,null);
            check(forward.getPath().startsWith("portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo"),"forward no es lista");

        }
        void rejectUnchanged(String blockingId) throws Exception {
            byte[] before=snapshot(rows); execute();
            Object message=attributes.get("msgErrorFechaComprobanteCompras");
            check(message!=null,"no rechazo visible; mutacion="+!Arrays.equals(before,snapshot(rows)));
            check(SessionErrors.contains(request,"error-fecha-comprobante-compras"),"sin clave de error");
            if (blockingId!=null) check(message.toString().indexOf("fila "+blockingId+".")>=0,"no identifica fila bloqueante: "+message);
            check(Arrays.equals(before,snapshot(rows)),"rechazo con mutacion de filas/orden/estados");
        }
        void rejectSelectionUnchanged() throws Exception {
            byte[] before=snapshot(rows); execute();
            check(attributes.get("msgErrorSeleccionCompras")!=null,"seleccion alterada fue aceptada");
            check(SessionErrors.contains(request,"error-seleccion-compras"),"sin error visible de seleccion");
            check(Arrays.equals(before,snapshot(rows)),"rechazo de seleccion con mutacion de sesion");
        }
        void accept() throws Exception {execute();check(attributes.get("msgErrorFechaComprobanteCompras")==null&&attributes.get("msgErrorSeleccionCompras")==null,"rechazo inesperado");}
    }
    private static Object primitiveDefault(Class type) {
        if (type==Boolean.TYPE) return Boolean.FALSE;
        if (type==Integer.TYPE) return Integer.valueOf(0);
        if (type==Long.TYPE) return Long.valueOf(0);
        return null;
    }
    public static void main(String[] args) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        test("destino posterior, con numero y OTR, sin ninguna mutacion",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.rows.get(1).setFechaPrestacion(date("2020-10-20 12:00"));
            f.params.put("aplicarNumeroCompras","true");f.params.put("aplicarComprobanteOTR","true");f.rejectUnchanged("2");
        }});
        test("ultimo destino invalido no deja primera copia parcial",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.rows.get(2).setFechaPrestacion(date("2020-10-20 12:00"));f.rejectUnchanged("3");
        }});
        test("mismo dia con hora posterior acepta",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.rows.get(1).setFechaPrestacion(date("2020-10-10 23:59"));f.accept();
            check(new SimpleDateFormat("yyyy-MM-dd").format(f.rows.get(0).getComprobanteFecha()).equals("2020-10-10"),"destino no actualizado");
        }});
        test("baja con fecha nula no bloquea y no cambia",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();PrestacionesReclamo baja=f.rows.get(3);baja.setFechaPrestacion(null);byte[] before=snapshot(baja);f.accept();
            check(Arrays.equals(before,snapshot(baja)),"baja modificada");
        }});
        test("emision nula rechaza sin mutar",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("cpbte_anio","0");f.rejectUnchanged(null);
        }});
        test("prestacion destino nula rechaza e identifica fila",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.rows.get(1).setFechaPrestacion(null);f.rejectUnchanged("2");
        }});
        test("prestacion origen nueva nula rechaza sin mutar",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("fecha_prestacion_anio","0");f.rejectUnchanged("1");
        }});
        test("emision futura rechaza sin mutar",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("cpbte_anio","2100");f.rejectUnchanged(null);
        }});
        test("origen usa fecha nueva, destinos usan sesion",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.rows.get(0).setFechaPrestacion(date("2020-10-25 12:00"));f.accept();
        }});
        test("solo numero cambia activas sin cambiar tipo/fecha y excluye bajas",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("aplicarFechaCompras","false");f.params.put("aplicarNumeroCompras","true");
            PrestacionesReclamo otr=f.rows.get(1),rcb=f.rows.get(2),baja=f.rows.get(3);byte[] before=snapshot(baja);Date old=otr.getComprobanteFecha();f.accept();
            check("NUEVO".equals(otr.getComprobanteNro())&&"NUEVO".equals(rcb.getComprobanteNro()),"numero no propagado");
            check("OTR".equals(otr.getComprobanteTipo())&&"RCB".equals(rcb.getComprobanteTipo()),"tipo cambiado sin consentimiento OTR");
            check(old.equals(otr.getComprobanteFecha()),"fecha cambio sin consentimiento");check(Arrays.equals(before,snapshot(baja)),"baja cambio");
        }});
        test("solo OTR conserva alcance distinto de todas las activas",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("aplicarFechaCompras","false");f.params.put("aplicarComprobanteOTR","true");
            PrestacionesReclamo otr=f.rows.get(1),rcb=f.rows.get(2);byte[] before=snapshot(rcb);Date old=otr.getComprobanteFecha();f.accept();
            check("NUEVO".equals(otr.getComprobanteNro())&&"FCP".equals(otr.getComprobanteTipo())&&"A".equals(otr.getComprobanteLetra()),"OTR incompleto");
            check(old.equals(otr.getComprobanteFecha()),"OTR propago fecha");check(Arrays.equals(before,snapshot(rcb)),"OTR cambio fila no OTR");
        }});
        test("sin consentimiento no cambia otras filas",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("aplicarFechaCompras","false");byte[] before=snapshot(f.rows.subList(1,4).toArray());f.accept();
            check(Arrays.equals(before,snapshot(new Object[]{f.rows.get(0),f.rows.get(1),f.rows.get(2)})),"otra fila cambio");
        }});
        test("RP manual ignora flags Compras",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();ReclamosBaseAction.compras=false;f.rows.get(1).setFechaPrestacion(date("2020-10-20 12:00"));
            PrestacionesReclamo other=f.rows.get(1);byte[] before=snapshot(other);f.params.put("aplicarNumeroCompras","true");f.accept();
            check(Arrays.equals(before,snapshot(other)),"RP manual cambio destino por flags Compras");
        }});
        test("autorizacion no propaga datos Compras",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("estadoAprobacion","2");PrestacionesReclamo other=f.rows.get(1);byte[] before=snapshot(other);f.accept();
            check(Arrays.equals(before,snapshot(other)),"aprobacion cambio destino");
        }});
        test("flag cero no convierte origen canonico en medicamento HTTP",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("nom_seleccionado_edit","0");f.params.put("id_medicamento_edit","987654");f.rejectSelectionUnchanged();
        }});
        test("flag manipulado tampoco permite medicamento en origen canonico",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("nom_seleccionado_edit","2");f.params.put("id_medicamento_edit","987654");f.rejectSelectionUnchanged();
        }});
        test("sin reseleccion conserva ID canonico por origen servidor",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();PrestacionesReclamo origen=f.rows.get(0);f.params.put("nom_seleccionado_edit","0");f.accept();
            check(origen.getId_prestacion()==1001&&origen.getId_medicamento()==0,"identidad canonica perdida");
            check(f.datos.lecturasRequerimiento==1&&f.datos.lecturasCanonico==1&&f.datos.lecturasAsociaciones==0,"historico fue revalidado con matriz nueva");
        }});
        test("flag cero con codigo cambiado rechaza antes de mutar",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.params.put("nom_seleccionado_edit","0");f.params.put("codigoSeguimiento_filtro_edit","CODIGO_AJENO");f.rejectSelectionUnchanged();
        }});
        test("mapa de origen ausente falla cerrado sin mutar",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();f.contexto.definirDetallesOrigen(Collections.singletonMap(Integer.valueOf(999),Integer.valueOf(501)));f.rejectSelectionUnchanged();
        }});
        test("medicamento historico omitido en HTTP conserva identidad validada en servidor",new Scenario(){public void run() throws Exception{
            Fixture f=new Fixture();PrestacionesReclamo origen=f.rows.get(0);
            RequerimientoCompraDetalle detalle=f.datos.requerimientos.get(Integer.valueOf(7001)).getDetalles().get(0);
            detalle.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_MEDICAMENTO);detalle.setIdMedicamento(Integer.valueOf(556));detalle.setNombreMedicamento("Medicamento prueba");
            origen.setId_prestacion(0);origen.setId_medicamento(556);
            f.params.put("nom_seleccionado_edit","0");f.params.put("id_medicamento_edit","0");f.params.put("nombre_medicamento_edit","Medicamento prueba");f.accept();
            check(origen.getId_prestacion()==0&&origen.getId_medicamento()==556,"medicamento historico quedo sin identidad");
        }});
        System.out.println("RESULT passed="+passed+" failed="+failed);
        if (failed>0) System.exit(1);
    }
}