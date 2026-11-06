import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSeguimiento;
import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenSeguimientoHelper;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenSeguimientoServiceUtil.Transaccion;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/** Valida contratos reales y rechazo antes de abrir TX; no usa DB ni portal. */
public class ComprasOrdenSeguimientoTest {
    private static int checks;
    public static void main(String[] args) throws Exception {
        Expuesta h=new Expuesta(); RequerimientoCompraOrdenSeguimiento vacio=h.entrada(new HashMap<String,String>());
        igual("calificacion sin defecto","",vacio.getCalificacionProveedor()); igual("factura sin defecto","",vacio.getFacturaIngresada());
        igual("liquidacion sin defecto","",vacio.getEstadoLiquidacion()); igual("pago sin defecto","",vacio.getEstadoPago());
        comprobar("auditada ausente false",!vacio.isLiquidacionAuditada()); comprobar("IDs/fecha opcionales",vacio.getIdLiquidacion()==null&&vacio.getIdOrdenPago()==null&&vacio.getFechaFactura()==null);
        RequerimientoCompraOrdenSeguimiento datos=h.entrada(completa()); igual("observaciones trim","Observacion manual",datos.getObservacionesProveedor());
        igual("factura trim","A-0001-123",datos.getNumeroFactura()); igual("fecha bisiesta","29/02/2024",new SimpleDateFormat("dd/MM/yyyy").format(datos.getFechaFactura()));
        comprobar("auditada LIQUIDADA",datos.isLiquidacionAuditada()); igual("IDliquidacion",Integer.valueOf(123),datos.getIdLiquidacion()); igual("ID OP maximo",Integer.valueOf(Integer.MAX_VALUE),datos.getIdOrdenPago());
        String[][] dominios={{"seg_calificacion","APTO","PENDIENTE_ENTREGA","NO_RESPONDE"},{"seg_factura_ingresada","SI","NO"},{"seg_estado_liquidacion","PENDIENTE","LIQUIDADA","NO_CORRESPONDE"},{"seg_estado_pago","PENDIENTE_PAGO","PAGADO","NO_CORRESPONDE"}};
        for(String[] dominio:dominios){for(int i=1;i<dominio.length;i++){Map<String,String>e=new HashMap<String,String>();e.put(dominio[0],dominio[i]);h.entrada(e);checks++;}rechazo(dominio[0],"DESCONOCIDO");}
        for(String fecha:new String[]{"29/02/2023","31/04/2026","00/11/2026","01/13/2026","1/11/2026","2026-11-06","01/01/0000","01/01/2026x"})rechazo("seg_fecha_factura",fecha);
        for(String id:new String[]{"0","-1","+1","1.2","2147483648","99999999999","abc"}){rechazo("seg_id_liquidacion",id);rechazo("seg_id_orden_pago",id);}
        rechazo("seg_liquidacion_auditada","SI");rechazo("seg_liquidacion_auditada","TRUE");
        for(String estado:new String[]{"","PENDIENTE","NO_CORRESPONDE"}){Map<String,String>e=new HashMap<String,String>();e.put("seg_estado_liquidacion",estado);e.put("seg_liquidacion_auditada","true");rechazo(e);}
        Map<String,String> limite=new HashMap<String,String>();limite.put("seg_observaciones",repetir(4000));limite.put("seg_numero_factura",repetir(100));h.entrada(limite);checks++;
        rechazo("seg_observaciones",repetir(4001));rechazo("seg_numero_factura",repetir(101));
        try{h.entrada(null);throw new AssertionError("Entrada nula aceptada");}catch(IllegalArgumentException esperado){checks++;}
        for(int[] caso:new int[][]{{0,1,0},{1,0,0},{1,1,-1},{1,1,Integer.MAX_VALUE}}){Expuesta invalida=new Expuesta();try{invalida.guardar(caso[0],caso[1],caso[2],completa(),"usuario");throw new AssertionError("Identidad invalida aceptada");}catch(IllegalArgumentException esperado){comprobar("identidad antes TX",invalida.aperturas==0);}}
        Expuesta sinUsuario=new Expuesta();try{sinUsuario.guardar(40,900,0,completa()," ");throw new AssertionError("Auditoria vacia aceptada");}catch(IllegalArgumentException esperado){comprobar("auditoria antes TX",sinUsuario.aperturas==0);}
        Expuesta valida=new Expuesta();try{valida.guardar(40,900,0,completa(),"usuario");throw new AssertionError("Falta apertura controlada");}catch(Exception apertura){igual("abre tras validar","APERTURA_CONTROLADA",apertura.getMessage());comprobar("abre una vez",valida.aperturas==1);}
        carga();System.out.println("COMPRAS_ORDEN_SEGUIMIENTO_OFFLINE_OK checks="+checks);
    }
    private static void carga()throws Exception{Expuesta h=new Expuesta();RequerimientoCompraOrden o=orden();RequerimientoCompraOrdenSeguimiento nuevo=h.cargar(o);comprobar("carga sin persistir",nuevo.getIdOrdenCompra()==900&&nuevo.getVersionEdicion()==0);
        h.actual=h.entrada(completa());h.actual.setIdOrdenCompra(900);h.actual.setVersionEdicion(4);comprobar("gestion cargada",h.cargar(o)==h.actual);
        h.actual.setEstadoLiquidacion("PENDIENTE");try{h.cargar(o);throw new AssertionError("Auditada inconsistente cargada");}catch(IllegalArgumentException esperado){checks++;}
        h.actual.setEstadoLiquidacion("LIQUIDADA");h.actual.setIdOrdenCompra(901);try{h.cargar(o);throw new AssertionError("Otra OC cargada");}catch(IllegalStateException esperado){checks++;}
        try{h.cargar(null);throw new AssertionError("Orden nula cargada");}catch(IllegalArgumentException esperado){checks++;}
        o.getRequerimiento().setBajaFecha(new java.util.Date());try{h.cargar(o);throw new AssertionError("Orden inactiva cargada");}catch(IllegalArgumentException esperado){checks++;}}
    static RequerimientoCompraOrden orden(){RequerimientoCompra r=RequerimientoCompra.paraMapeo();r.setIdRequerimientoCompra(40);r.setEstado(ConfiguracionComprasHelper.resolverEstado("ORDEN_COMPRA",ConfiguracionComprasHelper.getValorFijo("COMPRAS_ESTADOS_IDS")));RequerimientoCompraOrden o=new RequerimientoCompraOrden();o.setIdOrdenCompra(900);o.setIdRequerimientoCompra(40);o.setRequerimiento(r);return o;}
    static Map<String,String> completa(){Map<String,String>e=new HashMap<String,String>();e.put("seg_calificacion","APTO");e.put("seg_observaciones"," Observacion manual ");e.put("seg_factura_ingresada","SI");e.put("seg_numero_factura"," A-0001-123 ");e.put("seg_fecha_factura","29/02/2024");e.put("seg_estado_liquidacion","LIQUIDADA");e.put("seg_liquidacion_auditada","true");e.put("seg_estado_pago","PAGADO");e.put("seg_id_liquidacion","123");e.put("seg_id_orden_pago","2147483647");return e;}
    static class Expuesta extends RequerimientoCompraOrdenSeguimientoHelper{int aperturas;RequerimientoCompraOrdenSeguimiento actual;RequerimientoCompraOrdenSeguimiento entrada(Map<String,String>e){return prepararEntrada(e);}protected Transaccion abrirTransaccion()throws Exception{aperturas++;throw new Exception("APERTURA_CONTROLADA");}protected RequerimientoCompraOrdenSeguimiento obtener(int id){return actual;}}
    private static void rechazo(String clave,String valor)throws Exception{Map<String,String>e=new HashMap<String,String>();e.put(clave,valor);rechazo(e);}
    private static void rechazo(Map<String,String>e)throws Exception{Expuesta h=new Expuesta();try{h.guardar(40,900,0,e,"usuario");throw new AssertionError("Valor invalido aceptado");}catch(IllegalArgumentException esperado){comprobar("rechazo antes TX",h.aperturas==0);}}
    private static String repetir(int n){StringBuilder s=new StringBuilder();for(int i=0;i<n;i++)s.append('x');return s.toString();}
    private static void igual(String caso,Object esperado,Object actual){comprobar(caso,esperado==null?actual==null:esperado.equals(actual));}
    private static void comprobar(String caso,boolean ok){checks++;if(!ok)throw new AssertionError(caso);}
}
