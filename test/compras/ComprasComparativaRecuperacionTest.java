import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Referencia: ComprasCorreccionJavaTransaccionTest; Helper real sin DB ni UI. */
public class ComprasComparativaRecuperacionTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        probarLecturaEstricta();
        if (args.length > 0 && "baseline".equals(args[0])) {
            System.out.println("COMPARATIVA_RECUPERACION_BASELINE_REPRODUCIDO checks=" + checks);
            return;
        }
        probarDiagnostico();
        probarFirma();
        probarReparacionTransaccional();
        System.out.println("COMPARATIVA_RECUPERACION_OK checks=" + checks);
    }

    private static void probarLecturaEstricta() throws Exception {
        RequerimientoCompra r = requerimiento();
        RequerimientoCompraComparativa c = comparativa();
        Method completar = RequerimientoCompraComparativaHelper.class.getDeclaredMethod(
                "completarDetalles", new Class[] {RequerimientoCompra.class, RequerimientoCompraComparativa.class});
        completar.setAccessible(true);
        boolean rechazado = false;
        try {
            completar.invoke(new RequerimientoCompraComparativaHelper(), new Object[] {r, c});
        } catch (InvocationTargetException expected) {
            Throwable causa = expected.getCause();
            rechazado = causa instanceof IllegalArgumentException
                    && causa.getMessage().indexOf("Hay prestaciones guardadas") >= 0;
        }
        verificar(rechazado, "La lectura estricta bloquea una prestacion fuera del requerimiento");
        verificar(c.getDetalles().size() == 2, "La lectura fallida no retira datos guardados");
    }

    private static void probarDiagnostico() throws Exception {
        RequerimientoCompra r = requerimiento();
        List<RequerimientoCompraComparativa> raw = lista();
        marcarBaja(raw.get(0).getDetalles().get(1), true);
        List<RequerimientoCompraComparativa> fuera = inconsistencias(r, raw);
        verificar(fuera.size() == 2, "Diagnostico conserva ambos proveedores afectados");
        verificar(fuera.get(0).getIdComparativa() == 109 && fuera.get(1).getIdComparativa() == 110,
                "Diagnostico conserva identidad de cada comparativa");
        verificar(fuera.get(0).getDetalles().size() == 1 && fuera.get(1).getDetalles().size() == 1,
                "Diagnostico muestra solamente prestaciones fuera del requerimiento");
        RequerimientoCompraComparativaDetalle d = fuera.get(0).getDetalles().get(0);
        verificar(d.getIdDetalle() == 366 && d.getIdPrestacion() == 2,
                "Diagnostico conserva identidad exacta de la fila retirada");
        verificar(d.getCantidad().intValue() == 2 && d.getImporteUnitario().compareTo(new BigDecimal("17.50")) == 0,
                "Diagnostico conserva cantidad e importe para revision del usuario");
        verificar(baja(d), "Diagnostico conserva evidencia de baja real");
        verificar(!baja(fuera.get(1).getDetalles().get(0)),
                "Diagnostico no inventa baja para una referencia sin historial");
        verificar(raw.get(0).getDetalles().size() == 2 && raw.get(1).getDetalles().size() == 2,
                "Diagnosticar no altera las listas de cotizacion originales");
        raw.get(0).getDetalles().remove(1);
        raw.get(1).getDetalles().remove(1);
        verificar(inconsistencias(r, raw).isEmpty(), "Comparativa coherente no presenta reparacion");
    }

    private static void probarFirma() throws Exception {
        RequerimientoCompra r = requerimiento();
        List<RequerimientoCompraComparativa> raw = lista();
        String inicial = firma(r, raw);
        verificar(inicial != null && inicial.length() > 0, "Firma de revision no vacia");
        verificar(inicial.equals(firma(r, raw)), "Firma estable para la misma revision");
        RequerimientoCompraComparativaDetalle d = raw.get(0).getDetalles().get(1);
        d.setImporteUnitario(new BigDecimal("18.00"));
        verificar(!inicial.equals(firma(r, raw)), "Precio cambiado invalida confirmacion anterior");
        d.setImporteUnitario(new BigDecimal("17.50"));
        d.setCantidad(Integer.valueOf(3));
        verificar(!inicial.equals(firma(r, raw)), "Cantidad cambiada invalida confirmacion anterior");
        d.setCantidad(Integer.valueOf(2));
        d.setIdDetalle(999);
        verificar(!inicial.equals(firma(r, raw)), "Otra fila guardada invalida confirmacion anterior");
        d.setIdDetalle(366);
        raw.get(0).setIdPrestador(999);
        verificar(!inicial.equals(firma(r, raw)), "Otro proveedor invalida confirmacion anterior");
        raw.get(0).setIdPrestador(92967);
        r.getDetalles().get(0).setIdPrestacion(Integer.valueOf(3));
        verificar(!inicial.equals(firma(r, raw)), "Prestaciones vigentes cambiadas invalidan confirmacion");
    }

    private static List<RequerimientoCompraComparativa> lista() {
        List<RequerimientoCompraComparativa> raw = new ArrayList<RequerimientoCompraComparativa>();
        raw.add(comparativa());
        RequerimientoCompraComparativa otra = comparativa();
        otra.setIdComparativa(110);
        otra.setIdPrestador(92266);
        otra.setPrestador("Segundo proveedor de prueba");
        otra.getDetalles().get(0).setIdDetalle(451);
        otra.getDetalles().get(1).setIdDetalle(466);
        otra.getDetalles().get(1).setImporteUnitario(BigDecimal.ZERO);
        raw.add(otra);
        return raw;
    }

    private static List<RequerimientoCompraComparativa> inconsistencias(RequerimientoCompra r,
            List<RequerimientoCompraComparativa> raw) throws Exception {
        Method metodo = RequerimientoCompraComparativaHelper.class.getMethod("obtenerInconsistencias",
                new Class[] {RequerimientoCompra.class, List.class});
        return (List<RequerimientoCompraComparativa>) metodo.invoke(
                new RequerimientoCompraComparativaHelper(), new Object[] {r, raw});
    }

    private static String firma(RequerimientoCompra r, List<RequerimientoCompraComparativa> raw) throws Exception {
        Method metodo = RequerimientoCompraComparativaHelper.class.getMethod("firmaRecuperacion",
                new Class[] {RequerimientoCompra.class, List.class});
        return (String) metodo.invoke(new RequerimientoCompraComparativaHelper(), new Object[] {r, raw});
    }

    private static void marcarBaja(RequerimientoCompraComparativaDetalle d, boolean valor) throws Exception {
        Method metodo = RequerimientoCompraComparativaDetalle.class.getMethod("setBajaReal", new Class[] {Boolean.TYPE});
        metodo.invoke(d, new Object[] {Boolean.valueOf(valor)});
    }

    private static boolean baja(RequerimientoCompraComparativaDetalle d) throws Exception {
        Method metodo = RequerimientoCompraComparativaDetalle.class.getMethod("isBajaReal", new Class[0]);
        return ((Boolean) metodo.invoke(d, new Object[0])).booleanValue();
    }

    private static RequerimientoCompra requerimiento() {
        RequerimientoCompra r = RequerimientoCompra.paraMapeo();
        r.setIdRequerimientoCompra(1405);
        r.setEstado(3);
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();
        d.setId(Integer.valueOf(511));
        d.setIdPrestacion(Integer.valueOf(1));
        d.setCodigoItem("COD1");
        d.setDescripcionItem("Prestacion vigente");
        d.setCantidad(Integer.valueOf(1));
        detalles.add(d);
        r.setDetalles(detalles);
        return r;
    }

    private static RequerimientoCompraComparativa comparativa() {
        RequerimientoCompraComparativa c = RequerimientoCompraComparativa.paraMapeo();
        c.setIdComparativa(109);
        c.setIdRequerimiento(1405);
        c.setIdPrestador(92967);
        c.setPrestador("Proveedor de prueba");
        c.getDetalles().add(detalle(351, 1, 1, "100.00"));
        c.getDetalles().add(detalle(366, 2, 2, "17.50"));
        return c;
    }

    private static RequerimientoCompraComparativaDetalle detalle(int id, int prestacion,
            int cantidad, String importe) {
        RequerimientoCompraComparativaDetalle d = new RequerimientoCompraComparativaDetalle();
        d.setIdDetalle(id);
        d.setIdPrestacion(prestacion);
        d.setCodigo("COD" + prestacion);
        d.setPrestacion("Prestacion " + prestacion);
        d.setCantidad(Integer.valueOf(cantidad));
        d.setImporteUnitario(new BigDecimal(importe));
        return d;
    }


    /** Mismo patron de ComprasCorreccionJavaTransaccionTest: Helper y JDBC reales sobre una conexion simulada. */
    private static void probarReparacionTransaccional() throws Exception {
        Field buscar = BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        Field editar = EditarRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        buscar.setAccessible(true); editar.setAccessible(true);
        Object anteriorBuscar = buscar.get(null); Object anteriorEditar = editar.get(null);
        try {
            for (int estado = 2; estado <= 3; estado++) {
                Estado e = estado(estado); instalar(e, buscar, editar);
                String antes = firma(e.requerimiento, e.guardadas);
                int retiradas = new RequerimientoCompraComparativaHelper().reparar(1405, antes, "recuperador_fixture");
                verificar(retiradas == 2 && e.commits == 1 && e.rollbacks == 0 && e.cerrada,
                        "Reparacion real confirma dos bajas en estado " + estado + " y cierra conexion");
                verificar(e.guardadas.size() == 2 && e.guardadas.get(0).getDetalles().size() == 1
                        && e.guardadas.get(1).getDetalles().size() == 1 && e.borrados == 2,
                        "Reparacion retira solamente las dos filas confirmadas y conserva proveedores");
                verificar(e.actualizaciones == 2 && "recuperador_fixture".equals(e.actor),
                        "JDBC real audita ambas cabeceras con el actor recibido");
                verificar(e.eventos.indexOf("LOCK_RP") < e.eventos.indexOf("LOCK_REQ")
                        && e.eventos.indexOf("LOCK_REQ") < e.eventos.indexOf("LOCK_DET")
                        && e.eventos.indexOf("LOCK_DET") < e.eventos.indexOf("LOCK_COMP")
                        && e.eventos.indexOf("LOCK_COMP") < e.eventos.indexOf("LOCK_COMP_DET")
                        && e.eventos.indexOf("LOCK_COMP_DET") < e.eventos.indexOf("WRITE"),
                        "Reparacion mantiene orden legacy de locks antes de escribir");
                List<RequerimientoCompraComparativa> esperadas = copia(e.iniciales);
                esperadas.get(0).getDetalles().remove(1); esperadas.get(1).getDetalles().remove(1);
                verificar(firma(e.requerimiento, esperadas).equals(firma(e.requerimiento, e.guardadas)),
                        "Reparacion conserva cabeceras, referencias, cantidades y precios restantes");
                verificar(inconsistencias(e.requerimiento, e.guardadas).isEmpty(),
                        "Reparacion real deja cero huerfanos");
                String despues = firma(e.requerimiento, e.guardadas);
                rechazo(e, despues, "acreditar");
                verificar(e.borrados == 2 && e.commits == 1 && e.rollbacks == 1,
                        "Repetir reparacion no vuelve a escribir ni devuelve falso exito");
            }
            Estado e = estado(2); instalar(e, buscar, editar);
            String vieja = firma(e.requerimiento, e.guardadas);
            e.guardadas.get(0).getDetalles().get(1).setImporteUnitario(new BigDecimal("999.00"));
            rechazo(e, vieja, "cambiaron");
            verificar(e.actualizaciones == 0 && e.borrados == 0 && e.rollbacks == 1 && e.commits == 0,
                    "Firma desactualizada rechazada sin escritura");

            e = estado(2); e.guardadas.get(1).getDetalles().get(1).setBajaReal(false);
            instalar(e, buscar, editar); rechazo(e, firma(e.requerimiento, e.guardadas), "acreditar");
            verificar(e.actualizaciones == 0 && e.borrados == 0 && e.rollbacks == 1,
                    "Una baja no acreditada impide toda la reparacion, incluso la baja demostrada");

            int[] bloqueados = {1, 4, 5, 6, 7, 99};
            for (int i = 0; i < bloqueados.length; i++) {
                e = estado(bloqueados[i]); instalar(e, buscar, editar);
                rechazo(e, firma(e.requerimiento, e.guardadas), "estado");
                verificar(e.borrados == 0 && e.actualizaciones == 0 && e.rollbacks == 1,
                        "Estado bloqueado " + bloqueados[i] + " no permite reparar");
            }
            e = estado(2); e.requerimiento.setBajaFecha(new java.util.Date(1)); instalar(e, buscar, editar);
            rechazo(e, firma(e.requerimiento, e.guardadas), "estado");
            verificar(e.actualizaciones == 0 && e.rollbacks == 1, "Requerimiento inactivo no permite reparar");
            for (int i = 0; i < 2; i++) {
                e = estado(2); e.rp = i == 0; e.orden = i == 1; instalar(e, buscar, editar);
                rechazo(e, firma(e.requerimiento, e.guardadas), "RP u orden");
                verificar(e.actualizaciones == 0 && e.borrados == 0 && e.rollbacks == 1,
                        "Asociacion " + (e.rp ? "RP" : "OC") + " bloquea reparacion bajo locks");
            }
            for (int modo = 1; modo <= 5; modo++) {
                e = estado(2); e.fallo = modo; instalar(e, buscar, editar);
                String antes = firma(e.requerimiento, e.guardadas);
                rechazo(e, antes, modo == 1 ? "persistencia" : modo == 4 || modo == 5 ? "confirmar" : "verificar");
                verificar(e.rollbacks == 1 && e.cerrada && e.commits == (modo == 4 ? 1 : 0),
                        "Fallo " + modo + " nunca informa exito y cierra conexion tras rollback");
                if (modo != 5) verificar(antes.equals(firma(e.requerimiento, e.guardadas)),
                        "Fallo " + modo + " revierte incluso escrituras parciales y conserva todas las filas");
            }
        } finally { buscar.set(null, anteriorBuscar); editar.set(null, anteriorEditar); }
    }

    private static void rechazo(Estado e, String firma, String fragmento) throws Exception {
        boolean rechazado = false;
        try { new RequerimientoCompraComparativaHelper().reparar(1405, firma, "recuperador_fixture"); }
        catch (Exception esperada) { rechazado = esperada.getMessage() != null
                && esperada.getMessage().indexOf(fragmento) >= 0; }
        verificar(rechazado, "Helper real rechaza " + fragmento);
    }

    private static Estado estado(int estado) throws Exception {
        Estado e = new Estado(); e.requerimiento = requerimiento(); e.requerimiento.setEstado(estado);
        e.guardadas = lista();
        for (RequerimientoCompraComparativa c : e.guardadas) {
            c.setFechaPresupuesto(new java.sql.Date(1794441600000L)); c.setFormaPago(Integer.valueOf(1));
            c.setPlazoEntrega("72 horas"); c.setValidezPresupuesto(Integer.valueOf(10));
            c.setEnvio("INCLUIDO"); c.setIva(new BigDecimal("21.00")); c.setIibb(new BigDecimal("3.00"));
            c.getDetalles().get(1).setBajaReal(true);
        }
        e.iniciales = copia(e.guardadas); e.confirmadas = copia(e.guardadas);
        e.con = (Connection) Proxy.newProxyInstance(ComprasComparativaRecuperacionTest.class.getClassLoader(),
                new Class[] {Connection.class}, e);
        return e;
    }

    private static List<RequerimientoCompraComparativa> copia(List<RequerimientoCompraComparativa> origen) {
        List<RequerimientoCompraComparativa> resultado = new ArrayList<RequerimientoCompraComparativa>();
        for (RequerimientoCompraComparativa c : origen) {
            RequerimientoCompraComparativa n = RequerimientoCompraComparativa.paraMapeo();
            n.setIdComparativa(c.getIdComparativa()); n.setIdRequerimiento(c.getIdRequerimiento());
            n.setIdPrestador(c.getIdPrestador()); n.setPrestador(c.getPrestador());
            n.setFechaPresupuesto(c.getFechaPresupuesto()); n.setFormaPago(c.getFormaPago());
            n.setPlazoEntrega(c.getPlazoEntrega()); n.setValidezPresupuesto(c.getValidezPresupuesto());
            n.setEnvio(c.getEnvio()); n.setIva(c.getIva()); n.setIibb(c.getIibb());
            for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
                RequerimientoCompraComparativaDetalle x = new RequerimientoCompraComparativaDetalle();
                x.setIdDetalle(d.getIdDetalle()); x.setIdPrestacion(d.getIdPrestacion());
                x.setCantidad(d.getCantidad()); x.setImporteUnitario(d.getImporteUnitario());
                x.setBajaReal(d.isBajaReal()); x.setCodigo(d.getCodigo()); x.setPrestacion(d.getPrestacion());
                n.getDetalles().add(x);
            }
            resultado.add(n);
        }
        return resultado;
    }

    private static void instalar(Estado e, Field buscar, Field editar) throws Exception {
        buscar.set(null, new Lecturas(e)); editar.set(null, new Escrituras(e));
    }

    private static class Escrituras extends EditarRequerimientoCompraServiceImpl {
        private final Estado e;
        Escrituras(Estado estado) { e = estado; }
        public Transaccion abrirTransaccion() throws Exception {
            Constructor<Transaccion> c = Transaccion.class.getDeclaredConstructor(
                    EditarRequerimientoCompraServiceImpl.class, Connection.class);
            c.setAccessible(true); return c.newInstance(this, e.con);
        }
    }

    private static class Lecturas extends BusquedaRequerimientoCompraServiceImpl {
        private final Estado e;
        Lecturas(Estado estado) { e = estado; }
        public RequerimientoCompra getRequerimientoPersistido(Connection con, int id) throws Exception {
            if (con != e.con || id != 1405) throw new Exception("Contexto JDBC inesperado");
            return e.requerimiento;
        }
        public String getConfiguracionCompra(String clave, Connection con) throws Exception {
            if (con != e.con || !"COMPRAS_ESTADOS_IDS".equals(clave)) throw new Exception("Configuracion inesperada");
            return "PENDIENTE=1;A_COTIZAR=2;COTIZADO=3;RECLAMO_RP=4;ORDEN_COMPRA=5;PARA_AUTORIZAR=6;AUTORIZADO=7;ANULADO=99";
        }
    }

    private static class Estado implements InvocationHandler {
        Connection con; RequerimientoCompra requerimiento;
        List<RequerimientoCompraComparativa> guardadas, iniciales, confirmadas;
        List<String> eventos = new ArrayList<String>();
        boolean rp, orden, cerrada; int commits, rollbacks, actualizaciones, borrados, fallo; String actor;
        public Object invoke(Object proxy, Method metodo, Object[] args) throws Throwable {
            String n = metodo.getName();
            if ("commit".equals(n)) { commits++; if (fallo == 4) throw new SQLException("Commit incierto simulado"); confirmadas = copia(guardadas); return null; }
            if ("rollback".equals(n)) { rollbacks++; if (fallo == 5) throw new SQLException("Rollback fallido simulado"); guardadas = copia(confirmadas); return null; }
            if ("close".equals(n)) { cerrada = true; return null; }
            if ("isClosed".equals(n)) return Boolean.valueOf(cerrada);
            if ("setAutoCommit".equals(n)) return null;
            if ("getAutoCommit".equals(n)) return Boolean.FALSE;
            if ("prepareStatement".equals(n) || "prepareCall".equals(n)) return sentencia((String) args[0]);
            throw new UnsupportedOperationException(n);
        }
        private Object sentencia(final String sql) {
            return Proxy.newProxyInstance(ComprasComparativaRecuperacionTest.class.getClassLoader(),
                    new Class[] {sql.startsWith("{call") ? CallableStatement.class : PreparedStatement.class},
                    new InvocationHandler() {
                final Map<Integer, Object> p = new HashMap<Integer, Object>();
                public Object invoke(Object proxy, Method m, Object[] a) throws Throwable {
                    String n = m.getName();
                    if (n.startsWith("set")) { p.put((Integer) a[0], "setNull".equals(n) ? null : a[1]); return null; }
                    if ("close".equals(n)) return null;
                    if ("execute".equals(n)) { eventos.add("LOCK_RP"); return Boolean.FALSE; }
                    if ("executeUpdate".equals(n)) {
                        eventos.add("WRITE");
                        if (sql.startsWith("UPDATE compras.requerimiento_comparativa SET")) {
                            actualizaciones++; actor = (String) p.get(Integer.valueOf(8));
                            RequerimientoCompraComparativa c = comparativaPorId(((Integer) p.get(Integer.valueOf(11))).intValue());
                            verificar(c != null && Integer.valueOf(1405).equals(p.get(Integer.valueOf(9)))
                                    && Integer.valueOf(c.getIdPrestador()).equals(p.get(Integer.valueOf(10))), "UPDATE cabecera mantiene contexto");
                            c.setFechaPresupuesto((java.sql.Date) p.get(Integer.valueOf(1))); c.setFormaPago((Integer) p.get(Integer.valueOf(2)));
                            c.setPlazoEntrega((String) p.get(Integer.valueOf(3))); c.setValidezPresupuesto((Integer) p.get(Integer.valueOf(4)));
                            c.setEnvio((String) p.get(Integer.valueOf(5))); c.setIva((BigDecimal) p.get(Integer.valueOf(6))); c.setIibb((BigDecimal) p.get(Integer.valueOf(7)));
                            return Integer.valueOf(1);
                        }
                        if (sql.startsWith("DELETE FROM compras.requerimiento_comparativa_detalle")) {
                            if ((fallo == 1 || fallo == 5) && borrados == 1) throw new SQLException("Delete parcial simulado");
                            if (fallo == 2) return Integer.valueOf(0);
                            Integer id = (Integer) p.get(Integer.valueOf(1));
                            for (RequerimientoCompraComparativa c : guardadas) {
                                for (Iterator<RequerimientoCompraComparativaDetalle> it = c.getDetalles().iterator(); it.hasNext();) {
                                    if (it.next().getIdDetalle() == id.intValue()) { it.remove(); borrados++;
                                        if (fallo == 3) c.getDetalles().get(0).setImporteUnitario(new BigDecimal("999.00"));
                                        return Integer.valueOf(1); }
                                }
                            }
                            return Integer.valueOf(0);
                        }
                        throw new SQLException("Escritura fuera de alcance: " + sql);
                    }
                    if ("executeQuery".equals(n)) return resultado(filas(sql, p));
                    throw new UnsupportedOperationException(n);
                }
            });
        }
        private RequerimientoCompraComparativa comparativaPorId(int id) {
            for (RequerimientoCompraComparativa c : guardadas) if (c.getIdComparativa() == id) return c;
            return null;
        }
        private List<Map<String, Object>> filas(String sql, Map<Integer, Object> p) throws Exception {
            List<Map<String, Object>> filas = new ArrayList<Map<String, Object>>();
            if (sql.startsWith("SELECT rp.id")) return filas;
            if (sql.startsWith("SELECT id_requerimiento FROM compras.requerimiento_reclamo")) return filas;
            if (sql.startsWith("SELECT id_requerimiento FROM compras.requerimiento ")) { eventos.add("LOCK_REQ"); filas.add(fila("1", Integer.valueOf(1405))); }
            else if (sql.startsWith("SELECT id_detalle FROM compras.requerimiento_detalle")) { eventos.add("LOCK_DET"); filas.add(fila("1", Integer.valueOf(511))); }
            else if (sql.startsWith("SELECT 1 FROM compras.requerimiento_reclamo")) { if (rp) filas.add(fila("1", Integer.valueOf(1))); }
            else if (sql.startsWith("SELECT 1 FROM compras.requerimiento_orden")) { if (orden) filas.add(fila("1", Integer.valueOf(1))); }
            else if (sql.startsWith("SELECT id_comparativa FROM")) { eventos.add("LOCK_COMP"); for (RequerimientoCompraComparativa c : guardadas) filas.add(fila("1", Integer.valueOf(c.getIdComparativa()))); }
            else if (sql.startsWith("SELECT cd.id_prestacion")) { eventos.add("LOCK_COMP_DET"); for (RequerimientoCompraComparativa c : guardadas) for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) filas.add(fila("1", Integer.valueOf(d.getIdPrestacion()))); }
            else if (sql.startsWith("SELECT current_user")) filas.add(fila("1", "fixture_db"));
            else if (sql.startsWith("SELECT c.*")) {
                for (RequerimientoCompraComparativa c : guardadas) {
                    Map<String, Object> f = fila("id_comparativa", Integer.valueOf(c.getIdComparativa()));
                    f.put("prestador_id", Integer.valueOf(c.getIdPrestador())); f.put("descripcion", c.getPrestador());
                    f.put("fecha_presupuesto", c.getFechaPresupuesto()); f.put("forma_pago", c.getFormaPago());
                    f.put("plazo_entrega", c.getPlazoEntrega()); f.put("validez_presupuesto", c.getValidezPresupuesto());
                    f.put("envio", c.getEnvio()); f.put("iva", c.getIva()); f.put("iibb", c.getIibb()); filas.add(f);
                }
            } else if (sql.startsWith("SELECT cd.id_detalle")) {
                RequerimientoCompraComparativa c = comparativaPorId(((Integer) p.get(Integer.valueOf(2))).intValue());
                for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
                    Map<String, Object> f = fila("id_detalle", Integer.valueOf(d.getIdDetalle()));
                    f.put("id_prestacion", Integer.valueOf(d.getIdPrestacion())); f.put("cantidad", d.getCantidad());
                    f.put("importe_unitario", d.getImporteUnitario()); f.put("codigo", d.getCodigo());
                    f.put("descripcion", d.getPrestacion()); f.put("baja_real", Boolean.valueOf(d.isBajaReal())); filas.add(f);
                }
            } else throw new SQLException("Lectura fuera de alcance: " + sql);
            return filas;
        }
    }

    private static Map<String, Object> fila(String clave, Object valor) {
        Map<String, Object> fila = new HashMap<String, Object>(); fila.put(clave, valor); return fila;
    }

    private static ResultSet resultado(final List<Map<String, Object>> filas) {
        return (ResultSet) Proxy.newProxyInstance(ComprasComparativaRecuperacionTest.class.getClassLoader(),
                new Class[] {ResultSet.class}, new InvocationHandler() {
            int posicion = -1; boolean nulo;
            public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
                String n = m.getName();
                if ("next".equals(n)) return Boolean.valueOf(++posicion < filas.size());
                if ("close".equals(n)) return null;
                if ("wasNull".equals(n)) return Boolean.valueOf(nulo);
                Object valor = filas.get(posicion).get(String.valueOf(args[0])); nulo = valor == null;
                if ("getInt".equals(n)) return valor == null ? Integer.valueOf(0) : valor;
                if ("getBoolean".equals(n)) return valor == null ? Boolean.FALSE : valor;
                if ("getString".equals(n) || "getDate".equals(n) || "getBigDecimal".equals(n)) return valor;
                throw new UnsupportedOperationException(n);
            }
        });
    }

    private static void verificar(boolean condicion, String mensaje) {
        checks++;
        if (!condicion) throw new AssertionError(mensaje);
        System.out.println("OK " + mensaje);
    }
}
