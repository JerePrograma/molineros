import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.*;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceUtil;
import java.math.BigDecimal;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.*;

/** Ejecuta reglas productivas con fuente de comparativa simulada; sin DB ni UI. */
public class ComprasDetalleAdjudicadoVistaTest {
    private static int checks;
    public static void main(String[] args) throws Exception {
        ConfiguracionComprasDatosFixture.instalar();
        casoBase();
        decimalesYExtras();
        elegibilidad();
        datosInvalidos();
        consumidoresOriginales();
        System.out.println("DETALLE_ADJUDICADO_VISTA_OK checks=" + checks + "; sin DB, UI ni persistencia real");
    }
    private static void casoBase() throws Exception {
        Fuente f = new Fuente(); RequerimientoCompra r = requerimiento();
        f.lista.add(comparativa());
        Map<Integer, RequerimientoCompraDetalle> vista = f.obtenerDetalleAdjudicadoParaVista(r);
        check(vista.size() == 2, "Conserva dos filas sin agregar ni borrar identidades");
        valores(vista.get(Integer.valueOf(11)), 7, "120.00", "840.00");
        valores(vista.get(Integer.valueOf(12)), 0, "0.00", "0.00");
        valores(r.getDetalles().get(0), 10, "100.00", "1000.00");
        valores(r.getDetalles().get(1), 5, "200.00", "1000.00");
        check(vista.get(Integer.valueOf(11)) != r.getDetalles().get(0), "Bean visual independiente del original");
        check(vista.get(Integer.valueOf(12)).getIdInt() == 12, "Fila omitida mantiene ID de vista");
        check(f.req == 40 && f.prestador == 20, "Fuente consultada por requerimiento y ganador persistido");
        vista.get(Integer.valueOf(11)).setCantidad(Integer.valueOf(999));
        Map<Integer, RequerimientoCompraDetalle> reabierta = f.obtenerDetalleAdjudicadoParaVista(r);
        valores(reabierta.get(Integer.valueOf(11)), 7, "120.00", "840.00");
        check(f.lista.get(0).getDetalles().get(0).getCantidad().intValue() == 7,
                "Reapertura y cambios en vista no mutan presupuesto");
    }
    private static void decimalesYExtras() throws Exception {
        Fuente f = new Fuente(); RequerimientoCompraComparativa c = comparativa(); f.lista.add(c);
        c.getDetalles().get(0).setImporteUnitario(new BigDecimal("120.25"));
        c.getDetalles().add(cotizado(102, 2, "15.50"));
        c.getDetalles().add(cotizado(999, 3, "99.99"));
        Map<Integer, RequerimientoCompraDetalle> vista = f.obtenerDetalleAdjudicadoParaVista(requerimiento());
        valores(vista.get(Integer.valueOf(11)), 7, "120.25", "841.75");
        valores(vista.get(Integer.valueOf(12)), 2, "15.50", "31.00");
        check(vista.size() == 2 && c.getDetalles().size() == 3, "Extra no incorporada ni eliminada de fuente");
        c.getDetalles().get(0).setImporteUnitario(new BigDecimal("1.005"));
        vista = f.obtenerDetalleAdjudicadoParaVista(requerimiento());
        valores(vista.get(Integer.valueOf(11)), 7, "1.01", "7.07");
    }
    private static void elegibilidad() throws Exception {
        int[] cerrados = new int[] {3, 4, 6, 7, 5};
        for (int i = 0; i < cerrados.length; i++) {
            Fuente f = new Fuente(); f.lista.add(comparativa()); RequerimientoCompra r = requerimiento();
            r.setEstado(cerrados[i]);
            check(f.obtenerDetalleAdjudicadoParaVista(r) != null, "Proyeccion cerrada estado " + cerrados[i]);
        }
        int[] sinCierre = new int[] {1, 2, 99};
        for (int i = 0; i < sinCierre.length; i++) {
            Fuente f = new Fuente(); RequerimientoCompra r = requerimiento(); r.setEstado(sinCierre[i]);
            check(f.obtenerDetalleAdjudicadoParaVista(r) == null && f.lecturas == 0,
                    "Antes de cierre o anulado conserva vista sin consultar fuente: " + sinCierre[i]);
        }
        Fuente f = new Fuente(); RequerimientoCompra r = requerimiento();
        for (RequerimientoCompraDetalle d : r.getDetalles()) d.setIdPrestador(null);
        check(f.obtenerDetalleAdjudicadoParaVista(r) == null && f.lecturas == 0, "Sin ganador conserva vista original");
        r = requerimiento(); r.getDetalles().get(1).setIdPrestador(Integer.valueOf(21));
        check(f.obtenerDetalleAdjudicadoParaVista(r) == null, "Ganadores mixtos no proyectan");
        r = requerimiento(); r.setSectorDescripcion("SISTEMAS");
        check(f.obtenerDetalleAdjudicadoParaVista(r) == null && f.lecturas == 0, "Circuito documental excluido");
        r = requerimiento(); r.setBajaFecha(new Date());
        check(f.obtenerDetalleAdjudicadoParaVista(r) == null, "Requerimiento inactivo conserva vista");
        f = new Fuente();
        check(f.obtenerDetalleAdjudicadoParaVista(requerimiento()) == null, "Sin comparativa no inventa ceros");
        RequerimientoCompraComparativa c = comparativa(); c.setIdComparativa(0); f.lista.add(c);
        check(f.obtenerDetalleAdjudicadoParaVista(requerimiento()) == null, "Placeholder sin guardar no adjudica");
        c.setIdComparativa(30); c.getDetalles().clear();
        check(f.obtenerDetalleAdjudicadoParaVista(requerimiento()) == null, "Cabecera sin desglose conserva original");
    }
    private static void datosInvalidos() throws Exception {
        Fuente f = new Fuente(); f.error = new SQLException("lectura simulada");
        falla(f, requerimiento(), "Error de lectura propagado, nunca todo cero");
        f = fuente(); f.lista.add(comparativa()); falla(f, requerimiento(), "Comparativa ambigua rechazada");
        f = fuente(); f.lista.get(0).setIdPrestador(21); falla(f, requerimiento(), "Ganador ajeno rechazado");
        f = fuente(); f.lista.get(0).setIdRequerimiento(41); falla(f, requerimiento(), "Requerimiento ajeno rechazado");
        f = fuente(); f.lista.get(0).getDetalles().add(cotizado(101, 3, "20"));
        falla(f, requerimiento(), "Dos coincidencias de prestacion rechazadas");
        f = fuente(); RequerimientoCompra r = requerimiento(); r.getDetalles().get(1).setIdPrestacion(Integer.valueOf(101));
        falla(f, r, "Duplicado de prestacion original conserva validacion");
        f = fuente(); f.lista.get(0).getDetalles().get(0).setImporteUnitario(null);
        falla(f, requerimiento(), "Renglon incompleto no se interpreta como omitido");
        f = fuente(); f.lista.get(0).getDetalles().get(0).setCantidad(Integer.valueOf(-1));
        falla(f, requerimiento(), "Cantidad invalida no produce proyeccion");
    }
    private static void consumidoresOriginales() throws Exception {
        Fuente f = fuente(); RequerimientoCompra r = requerimiento();
        Method pdf = RequerimientoCompraComparativaHelper.class.getDeclaredMethod("filaPdf",
                RequerimientoCompra.class, RequerimientoCompraComparativa.class, RequerimientoCompraComparativaDetalle.class);
        pdf.setAccessible(true);
        Object[] parametros = new Object[] {r, f.lista.get(0), f.lista.get(0).getDetalles().get(0)};
        Map antes = (Map) pdf.invoke(new RequerimientoCompraComparativaHelper(), parametros);
        f.obtenerDetalleAdjudicadoParaVista(r);
        Map despues = (Map) pdf.invoke(new RequerimientoCompraComparativaHelper(), parametros);
        check(antes.equals(despues) && "10".equals(despues.get("unidad")) && "7".equals(despues.get("cantidad")),
                "Fuente de PDF intacta: solicitada10, cotizada7 y mismos importes");
        Method rp = ReclamoPrestacionalCompraPrecargaHelper.class.getDeclaredMethod("validarDetalleCotizado", RequerimientoCompraDetalle.class);
        rp.setAccessible(true);
        Method oc = RequerimientoCompraOrdenHelper.class.getDeclaredMethod("validarDetalles", RequerimientoCompra.class,
                Boolean.TYPE, RequerimientoCompraOrdenServiceUtil.Transaccion.class);
        oc.setAccessible(true);
        for (int i = 0; i < 2; i++) {
            for (RequerimientoCompraDetalle d : r.getDetalles()) rp.invoke(null, new Object[] {d});
            oc.invoke(new RequerimientoCompraOrdenHelper(), new Object[] {r, Boolean.FALSE, null});
            f.obtenerDetalleAdjudicadoParaVista(r);
        }
        check(true, "RP/OC mantienen validacion exitosa sobre entradas originales tras proyectar");
        r.getDetalles().get(1).setCantidad(Integer.valueOf(0));
        String rpAntes = rechazo(rp, null, new Object[] {r.getDetalles().get(1)});
        String ocAntes = rechazo(oc, new RequerimientoCompraOrdenHelper(), new Object[] {r, Boolean.FALSE, null});
        f.obtenerDetalleAdjudicadoParaVista(r);
        check(rpAntes.equals(rechazo(rp, null, new Object[] {r.getDetalles().get(1)})), "RP conserva rechazo y mensaje");
        check(ocAntes.equals(rechazo(oc, new RequerimientoCompraOrdenHelper(), new Object[] {r, Boolean.FALSE, null})),
                "OC conserva rechazo y mensaje");
    }
    private static String rechazo(Method m, Object target, Object[] args) throws Exception {
        try { m.invoke(target, args); throw new AssertionError("Validacion omitida"); }
        catch (InvocationTargetException e) { return e.getCause().getMessage(); }
    }
    private static void falla(Fuente f, RequerimientoCompra r, String mensaje) throws Exception {
        boolean rechazada = false;
        try { f.obtenerDetalleAdjudicadoParaVista(r); } catch (Exception esperado) { rechazada = true; }
        check(rechazada, mensaje);
    }
    private static void valores(RequerimientoCompraDetalle d, int cantidad, String unitario, String subtotal) {
        check(d != null && d.getCantidadInt() == cantidad && unitario.equals(d.getPrecioUnitarioEstimadoString())
                && subtotal.equals(d.getPrecioTotalEstimadoString()), "Valores " + cantidad + " x " + unitario + " = " + subtotal);
    }
    private static void check(boolean ok, String mensaje) {
        if (!ok) throw new AssertionError(mensaje); checks++; System.out.println("OK " + mensaje);
    }
    private static RequerimientoCompra requerimiento() {
        RequerimientoCompra r = new RequerimientoCompra(); r.setIdRequerimientoCompra(40); r.setEstado(3);
        r.setSectorDescripcion("PRESTACIONES MEDICAS"); r.setIdSector(Integer.valueOf(1));
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        detalles.add(original(11, 101, 10, "100")); detalles.add(original(12, 102, 5, "200")); r.setDetalles(detalles); return r;
    }
    private static RequerimientoCompraDetalle original(int id, int prestacion, int cantidad, String unitario) {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle(Integer.valueOf(id)); d.setIdRequerimientoCompra(40);
        d.setIdPrestacion(Integer.valueOf(prestacion)); d.setIdPrestador(Integer.valueOf(20));
        d.setTipoItem("NOMENCLADOR"); d.setIdTipoNomenclador(Integer.valueOf(1));
        d.setCodigoNomenclador(String.valueOf(prestacion)); d.setDescripcionNomenclador("Prestacion " + prestacion);
        d.setCantidad(Integer.valueOf(cantidad)); d.setPrecioUnitarioEstimado(new BigDecimal(unitario)); return d;
    }
    private static RequerimientoCompraComparativa comparativa() {
        RequerimientoCompraComparativa c = RequerimientoCompraComparativa.paraMapeo();
        c.setIdComparativa(30); c.setIdRequerimiento(40); c.setIdPrestador(20);
        List<RequerimientoCompraComparativaDetalle> detalles = new ArrayList<RequerimientoCompraComparativaDetalle>();
        detalles.add(cotizado(101, 7, "120")); c.setDetalles(detalles); return c;
    }
    private static RequerimientoCompraComparativaDetalle cotizado(int prestacion, int cantidad, String unitario) {
        RequerimientoCompraComparativaDetalle d = new RequerimientoCompraComparativaDetalle();
        d.setIdDetalle(prestacion + 1000); d.setIdPrestacion(prestacion); d.setCantidad(Integer.valueOf(cantidad));
        d.setImporteUnitario(new BigDecimal(unitario));
        d.setSubtotal(ImportesCompraHelper.calcularPrecioTotal(d.getCantidad(), d.getImporteUnitario())); return d;
    }
    private static Fuente fuente() { Fuente f = new Fuente(); f.lista.add(comparativa()); return f; }
    private static class Fuente extends EditarRequerimientoCompraHelper {
        List<RequerimientoCompraComparativa> lista = new ArrayList<RequerimientoCompraComparativa>();
        Exception error; int lecturas, req, prestador;
        protected List<RequerimientoCompraComparativa> obtenerComparativaParaDetalleAdjudicado(int id, int ganador) throws Exception {
            lecturas++; req = id; prestador = ganador; if (error != null) throw error; return lista;
        }
    }
}
