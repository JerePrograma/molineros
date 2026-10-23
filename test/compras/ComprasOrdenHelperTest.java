import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Ejecuta el Helper productivo. Las consultas y la persistencia se sustituyen
 * mediante sus fronteras protected; no requiere base, Tomcat ni sesion UI. */
public class ComprasOrdenHelperTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        empresa();
        importes();
        textosYFechas();
        estadoYAdjudicacion();
        medicina();
        System.out.println("COMPRAS_ORDEN_HELPER_OFFLINE_OK checks=" + checks);
        System.out.println("Helper productivo; fronteras de consulta/persistencia simuladas; sin DB ni UI.");
    }

    private static void empresa() throws Exception {
        Mock h = empresaFixture();
        h.detalles.get(0).setObservaciones("Comentario tecnico del articulo");
        Map<String, String> entrada = entrada();
        entrada.put("precio_70", "125,50");
        entrada.put("iva", "20.50");
        entrada.put("iibb", "5");
        entrada.put("proveedor", "Proveedor alterado");
        entrada.put("proveedorCuit", "00000000000");
        entrada.put("cantidad_70", "999");
        RequerimientoCompraOrden resultado = h.crear(40, entrada, "auditoria");
        igual("proveedor canonico", "Empresa de prueba", resultado.getProveedor());
        igual("CUIT canonico vacio", "", resultado.getProveedorCuit());
        igual("cantidad fija", Integer.valueOf(2), resultado.getDetalles().get(0).getCantidad());
        decimal("precio ingresado", "125.50", resultado.getDetalles().get(0).getPrecioUnitarioEstimado());
        decimal("total incluye impuestos", "276.50", resultado.getTotal());
        decimal("precio original preservado", "100", h.detalles.get(0).getPrecioUnitarioEstimado());
        igual("comentario tecnico en snapshot", "Equipo de prueba\nComentario tecnico del articulo",
                resultado.getDetalles().get(0).getDescripcionItemVisible());
        igual("comentario sin duplicar", "", resultado.getDetalles().get(0).getObservacionesVisible());
        igual("descripcion original preservada", "Equipo de prueba", h.detalles.get(0).getDescripcionItemVisible());
        igual("comentario original preservado", "Comentario tecnico del articulo", h.detalles.get(0).getObservacionesVisible());
        igual("detalle independiente", Boolean.FALSE, Boolean.valueOf(resultado.getDetalles().get(0) == h.detalles.get(0)));
        igual("auditoria", "auditoria", resultado.getAltaUsr());
        igual("un guardado", Integer.valueOf(1), Integer.valueOf(h.guardados));
        RequerimientoCompraOrden repetida = h.crear(40, entrada(), "auditoria");
        igual("emision repetida", Integer.valueOf(900), Integer.valueOf(repetida.getIdOrdenCompra()));
        igual("idempotente", Integer.valueOf(1), Integer.valueOf(h.guardados));
        igual("hidratacion existente", h.r, h.preparar(40, "nombre").getRequerimiento());
    }

    private static void importes() throws Exception {
        String[] invalidos = new String[] {"", "0", "-1", "NaN", "Infinity", "1.000,00",
                "1.001", "100000000000000.00", "1e3", "9999999999999999999999999999999999999"};
        for (int i = 0; i < invalidos.length; i++) {
            Map<String, String> entrada = entrada();
            entrada.put("precio_70", invalidos[i]);
            rechaza(empresaFixture(), entrada, "precio invalido " + invalidos[i]);
        }
        String[] impuestosInvalidos = new String[] {"-1", "NaN", "1.001", "100000000000000.00"};
        for (int i = 0; i < impuestosInvalidos.length; i++) {
            Map<String, String> entrada = entrada();
            entrada.put("iva", impuestosInvalidos[i]);
            rechaza(empresaFixture(), entrada, "IVA invalido " + impuestosInvalidos[i]);
        }
        Map<String, String> entrada = entrada();
        entrada.put("precio_70", "99999999999999.99");
        rechaza(empresaFixture(), entrada, "subtotal fuera de rango");
        Mock limite = empresaFixture();
        limite.detalles.get(0).setCantidad(Integer.valueOf(1));
        decimal("importe maximo valido", "99999999999999.99", limite.crear(40, entrada, "auditoria").getTotal());
        entrada.put("iva", "0.01");
        Mock total = empresaFixture();
        total.detalles.get(0).setCantidad(Integer.valueOf(1));
        rechaza(total, entrada, "total fuera de rango por impuesto");
    }

    private static void textosYFechas() throws Exception {
        String[] monedas = new String[] {"", "EUR", "ars", "USDX"};
        for (int i = 0; i < monedas.length; i++) {
            Map<String, String> entrada = entrada();
            entrada.put("moneda", monedas[i]);
            rechaza(empresaFixture(), entrada, "moneda invalida");
        }
        Map<String, String> entrada = entrada();
        entrada.put("moneda", "USD");
        entrada.put("fechaOrdenMedica", "29/02/2024");
        entrada.put("fechaRecepcion", "23/10/2026");
        entrada.put("plan", "Plan de prueba");
        RequerimientoCompraOrden resultado = empresaFixture().crear(40, entrada, "auditoria");
        igual("fecha estricta valida", "29/02/2024", new SimpleDateFormat("dd/MM/yyyy").format(resultado.getFechaOrdenMedica()));
        igual("plan", "Plan de prueba", resultado.getPlan());
        String[] fechas = new String[] {"29/02/2025", "31/04/2026", "1/01/2026", "01/01/2026extra", "2026-01-01"};
        for (int i = 0; i < fechas.length; i++) {
            entrada = entrada();
            entrada.put("fechaRecepcion", fechas[i]);
            rechaza(empresaFixture(), entrada, "fecha invalida " + fechas[i]);
        }
        String[] campos = new String[] {"formaPago", "entrega", "observaciones", "plan", "proveedorEmail"};
        int[] largos = new int[] {251, 1001, 4001, 121, 251};
        for (int i = 0; i < campos.length; i++) {
            entrada = entrada();
            entrada.put(campos[i], repetir(largos[i]));
            rechaza(empresaFixture(), entrada, "sin truncamiento " + campos[i]);
        }
        entrada = entrada(); entrada.put("formaPago", " ");
        rechaza(empresaFixture(), entrada, "pago obligatorio");
        entrada = entrada(); entrada.put("entrega", " ");
        rechaza(empresaFixture(), entrada, "entrega obligatoria");
        entrada = entrada(); entrada.put("confeccionadoPor", " ");
        rechaza(empresaFixture(), entrada, "confeccionado por obligatorio");
    }

    private static void estadoYAdjudicacion() throws Exception {
        Mock h = empresaFixture();
        h.r.setEstado(WebKeysCompras.ESTADO_PENDIENTE);
        rechaza(h, entrada(), "estado no autorizado");
        h = empresaFixture();
        h.preparar(40, "nombre");
        h.r.setEstado(WebKeysCompras.ESTADO_PENDIENTE);
        rechaza(h, entrada(), "estado recargado al confirmar");
        h = empresaFixture(); h.r.setBajaFecha(new Date());
        rechaza(h, entrada(), "requerimiento inactivo");
        h = empresaFixture(); h.empresas.clear();
        rechaza(h, entrada(), "sin adjudicacion");
        h = empresaFixture(); h.empresas.add(h.empresas.get(0));
        rechaza(h, entrada(), "dos empresas adjudicadas");
        h = empresaFixture(); h.empresas.get(0).setIdRequerimiento(Integer.valueOf(99));
        rechaza(h, entrada(), "empresa de otro requerimiento");
        h = empresaFixture(); h.detalles.get(0).setCantidad(Integer.valueOf(0));
        rechaza(h, entrada(), "cantidad no positiva");
        h = empresaFixture(); h.detalles.get(0).setIdRequerimientoCompra(99);
        rechaza(h, entrada(), "detalle ajeno");
        h = empresaFixture(); h.detalles.add(h.detalles.get(0));
        rechaza(h, entrada(), "detalle duplicado");
    }

    private static void medicina() throws Exception {
        Mock h = medicinaFixture();
        RequerimientoCompraOrden precarga = h.preparar(40, "Compras");
        igual("moneda requiere eleccion", "", precarga.getMoneda());
        igual("RP canonico", Integer.valueOf(68030), Integer.valueOf(precarga.getIdReclamoPrestacional()));
        igual("pago comparativa", "45 dias", precarga.getFormaPago());
        igual("fecha documento", "02/10/2026", new SimpleDateFormat("dd/MM/yyyy").format(precarga.getFechaOrdenMedica()));
        Map<String, String> entrada = entrada();
        entrada.put("precio_70", "999999");
        entrada.put("cantidad_70", "999");
        entrada.put("iva", "999");
        entrada.put("iibb", "999");
        entrada.put("proveedor", "Proveedor alterado");
        entrada.put("proveedorCuit", "00000000000");
        entrada.put("idReclamoPrestacional", "999");
        RequerimientoCompraOrden resultado = h.crear(40, entrada, "auditoria");
        igual("proveedor medico canonico", "Prestador de prueba", resultado.getProveedor());
        igual("CUIT medico canonico", "30700000001", resultado.getProveedorCuit());
        igual("cantidad comparativa", Integer.valueOf(3), resultado.getDetalles().get(0).getCantidad());
        decimal("precio comparativa", "50", resultado.getDetalles().get(0).getPrecioUnitarioEstimado());
        decimal("IVA comparativa", "10", resultado.getIva());
        decimal("IIBB comparativa", "5", resultado.getIibb());
        decimal("total medico", "165", resultado.getTotal());
        igual("cantidad original preservada", Integer.valueOf(2), h.detalles.get(0).getCantidad());
        decimal("precio original medico preservado", "100", h.detalles.get(0).getPrecioUnitarioEstimado());
        h = medicinaFixture(); h.relacion = null;
        rechaza(h, entrada(), "sin RP");
        h = medicinaFixture(); h.relacion.setIdRequerimientoCompra(99);
        rechaza(h, entrada(), "RP ajeno");
        h = medicinaFixture(); h.comparativas.clear();
        rechaza(h, entrada(), "sin comparativa guardada");
        h = medicinaFixture(); h.comparativas.get(0).setIdPrestador(99);
        rechaza(h, entrada(), "comparativa de otro prestador");
        h = medicinaFixture(); h.comparativas.get(0).getDetalles().clear();
        rechaza(h, entrada(), "comparativa incompleta");
        h = medicinaFixture(); h.comparativas.get(0).getDetalles().add(h.comparativas.get(0).getDetalles().get(0));
        rechaza(h, entrada(), "comparativa duplicada");
        h = medicinaFixture(); h.comparativas.get(0).setIva(new BigDecimal("-1"));
        rechaza(h, entrada(), "impuesto adjudicado negativo");
        h = medicinaFixture(); h.detalles.get(0).setIdPrestador(null);
        rechaza(h, entrada(), "adjudicacion medica incompleta");
    }

    private static Mock empresaFixture() {
        Mock h = base();
        h.r.setSectorDescripcion("Sistemas");
        RequerimientoCompraPresupuesto empresa = new RequerimientoCompraPresupuesto();
        empresa.setIdRequerimientoPresupuesto(Integer.valueOf(101));
        empresa.setIdRequerimiento(Integer.valueOf(40));
        empresa.setTipoDocumento(Integer.valueOf(RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA));
        empresa.setDescripcionEmpresa("Empresa de prueba");
        empresa.setEmpresaAdjudicada(true);
        h.empresas.add(empresa);
        return h;
    }

    private static Mock medicinaFixture() throws Exception {
        Mock h = base();
        h.r.setSectorDescripcion("Prestaciones Medicas");
        RequerimientoCompraDetalle d = h.detalles.get(0);
        d.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR);
        d.setIdPrestacion(Integer.valueOf(200));
        d.setIdTipoNomenclador(Integer.valueOf(10));
        d.setCodigoNomenclador("INS200");
        d.setDescripcionNomenclador("Insumo de prueba");
        d.setIdPrestador(Integer.valueOf(30));
        d.setPrestadorRazonSocial("Prestador de prueba");
        d.setPrestadorCuit("30700000001");
        RequerimientoCompraComparativa c = new RequerimientoCompraComparativa();
        c.setIdComparativa(60); c.setIdRequerimiento(40); c.setIdPrestador(30);
        c.setFormaPago(Integer.valueOf(45)); c.setPlazoEntrega("24/48"); c.setEnvio("Obra Social");
        c.setIva(new BigDecimal("10")); c.setIibb(new BigDecimal("5"));
        RequerimientoCompraComparativaDetalle precio = new RequerimientoCompraComparativaDetalle();
        precio.setIdDetalle(61); precio.setIdPrestacion(200); precio.setCantidad(Integer.valueOf(3));
        precio.setImporteUnitario(new BigDecimal("50")); c.getDetalles().add(precio);
        h.comparativas.add(c);
        h.relacion = new RequerimientoCompraReclamoPrestacional();
        h.relacion.setIdRequerimientoCompra(40);
        h.relacion.setIdReclamoPrestacional(Integer.valueOf(68030));
        h.relacion.setEstado(WebKeysCompras.VINCULO_RECLAMO_VINCULADO);
        RequerimientoCompraPresupuesto documento = new RequerimientoCompraPresupuesto();
        documento.setIdRequerimiento(Integer.valueOf(40));
        documento.setFechaDocumento(new SimpleDateFormat("dd/MM/yyyy").parse("02/10/2026"));
        h.documentos.add(documento);
        return h;
    }

    private static Mock base() {
        Mock h = new Mock();
        h.r = new RequerimientoCompra(); h.r.setIdRequerimientoCompra(40);
        h.r.setEstado(WebKeysCompras.ESTADO_AUTORIZADO); h.r.setAltaUsr("solicitante");
        h.r.setAltaFecha(new Date());
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle(Integer.valueOf(70));
        d.setIdRequerimientoCompra(40); d.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_OBSERVACION);
        d.setDescripcionItem("Equipo de prueba"); d.setCantidad(Integer.valueOf(2));
        d.setPrecioUnitarioEstimado(new BigDecimal("100"));
        h.detalles.add(d); h.r.setDetalles(h.detalles);
        return h;
    }

    private static Map<String, String> entrada() {
        Map<String, String> entrada = new HashMap<String, String>();
        entrada.put("moneda", "ARS"); entrada.put("formaPago", "Transferencia");
        entrada.put("confeccionadoPor", "Compras de prueba");
        entrada.put("entrega", "A coordinar"); entrada.put("precio_70", "100");
        return entrada;
    }

    private static void rechaza(Mock h, Map<String, String> entrada, String caso) throws Exception {
        try {
            h.crear(40, entrada, "auditoria");
        } catch (IllegalArgumentException esperado) {
            igual(caso + " sin persistencia", Integer.valueOf(0), Integer.valueOf(h.guardados));
            return;
        }
        throw new AssertionError("No rechazo: " + caso);
    }

    private static String repetir(int cantidad) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < cantidad; i++) { texto.append('x'); }
        return texto.toString();
    }

    private static void decimal(String caso, String esperado, BigDecimal actual) {
        checks++;
        if (actual == null || new BigDecimal(esperado).compareTo(actual) != 0) {
            throw new AssertionError(caso + ": esperado=" + esperado + ", actual=" + actual);
        }
    }

    private static void igual(String caso, Object esperado, Object actual) {
        checks++;
        if (esperado == null ? actual != null : !esperado.equals(actual)) {
            throw new AssertionError(caso + ": esperado=" + esperado + ", actual=" + actual);
        }
    }

    private static class Mock extends RequerimientoCompraOrdenHelper {
        RequerimientoCompra r;
        RequerimientoCompraOrden guardada;
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        List<RequerimientoCompraPresupuesto> empresas = new ArrayList<RequerimientoCompraPresupuesto>();
        List<RequerimientoCompraComparativa> comparativas = new ArrayList<RequerimientoCompraComparativa>();
        List<RequerimientoCompraPresupuesto> documentos = new ArrayList<RequerimientoCompraPresupuesto>();
        RequerimientoCompraReclamoPrestacional relacion;
        int guardados;
        protected RequerimientoCompra obtenerRequerimiento(int id) { return r; }
        protected List<RequerimientoCompraDetalle> obtenerDetalles(int id) { return detalles; }
        protected RequerimientoCompraOrden obtenerOrden(int id) { return guardada; }
        protected RequerimientoCompraOrden guardarOrden(RequerimientoCompraOrden orden, String usuario) {
            guardados++; orden.setIdOrdenCompra(900); guardada = orden;
            r.setEstado(WebKeysCompras.ESTADO_ORDEN_COMPRA);
            return orden;
        }
        protected List<RequerimientoCompraPresupuesto> obtenerCotizacionesEmpresa(int id) { return empresas; }
        protected List<RequerimientoCompraComparativa> obtenerComparativas(int id, int idPrestador) { return comparativas; }
        protected RequerimientoCompraReclamoPrestacional obtenerRelacionRp(int id) { return relacion; }
        protected List<RequerimientoCompraPresupuesto> obtenerOrdenesMedicas(int id) { return documentos; }
    }
}
