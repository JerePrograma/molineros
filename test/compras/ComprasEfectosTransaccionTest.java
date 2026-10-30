import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceImpl;

public class ComprasEfectosTransaccionTest {
    private static int aprobadas;
    private static void comprobar(boolean valor, String mensaje) {
        if (!valor) throw new IllegalStateException(mensaje);
        aprobadas++;
    }
    private static Object defecto(Class tipo) {
        if (tipo == Boolean.TYPE) return Boolean.FALSE;
        if (tipo == Integer.TYPE) return Integer.valueOf(0);
        if (tipo == Long.TYPE) return Long.valueOf(0);
        return null;
    }
    private static final class BD implements InvocationHandler {
        private final List<String> sql = new ArrayList<String>();
        private final List<Sentencia> sentencias = new ArrayList<Sentencia>();
        private final Timestamp fecha = Timestamp.valueOf("2026-10-30 11:00:00.123456");
        private int commits;
        private int rollbacks;
        private int cierres;
        private boolean fallarEscritura;
        private Connection conexion() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Connection.class }, this);
        }
        public Object invoke(Object proxy, Method metodo, Object[] args) throws Throwable {
            String n = metodo.getName();
            if (n.equals("prepareStatement") || n.equals("prepareCall")) {
                String texto = (String) args[0]; sql.add(texto);
                Sentencia s = new Sentencia(this, texto); sentencias.add(s);
                return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { n.equals("prepareCall") ? CallableStatement.class : PreparedStatement.class }, s);
            }
            if (n.equals("commit")) commits++;
            if (n.equals("rollback")) rollbacks++;
            if (n.equals("close")) cierres++;
            return defecto(metodo.getReturnType());
        }
    }
    private static final class Sentencia implements InvocationHandler {
        private final BD bd;
        private final String sql;
        private final Map<Integer, Object> parametros = new HashMap<Integer, Object>();
        private Sentencia(BD bd, String sql) { this.bd = bd; this.sql = sql; }
        public Object invoke(Object proxy, Method metodo, Object[] args) throws Throwable {
            String n = metodo.getName();
            if (n.startsWith("set") && args != null && args.length >= 2) parametros.put((Integer) args[0], n.equals("setNull") ? null : args[1]);
            if (n.equals("executeUpdate")) {
                if (bd.fallarEscritura) throw new SQLException("Fallo JDBC de prueba.");
                return Integer.valueOf(1);
            }
            if (n.equals("getInt")) return Integer.valueOf(3);
            if (n.equals("executeQuery")) {
                return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { ResultSet.class }, new InvocationHandler() {
                    private boolean consumido;
                    public Object invoke(Object p, Method m, Object[] a) {
                        String nombre = m.getName();
                        if (nombre.equals("next")) { boolean resultado = !consumido; consumido = true; return Boolean.valueOf(resultado); }
                        if (nombre.equals("getInt")) {
                            if (a[0].equals("id_requerimiento")) return Integer.valueOf(1388);
                            if (a[0].equals("id_reclamo_prestacional")) return Integer.valueOf(42);
                            return Integer.valueOf(901);
                        }
                        if (nombre.equals("getString")) return "VINCULADO";
                        if (nombre.equals("getTimestamp")) return bd.fecha;
                        return defecto(m.getReturnType());
                    }
                });
            }
            return defecto(metodo.getReturnType());
        }
    }
    private static EditarRequerimientoCompraServiceUtil.Transaccion transaccion(Connection con) throws Exception {
        Constructor c = EditarRequerimientoCompraServiceImpl.Transaccion.class.getDeclaredConstructor(new Class[] { EditarRequerimientoCompraServiceImpl.class, Connection.class });
        c.setAccessible(true);
        Object impl = c.newInstance(new Object[] { new EditarRequerimientoCompraServiceImpl(), con });
        Constructor u = EditarRequerimientoCompraServiceUtil.Transaccion.class.getDeclaredConstructor(new Class[] { EditarRequerimientoCompraServiceImpl.Transaccion.class });
        u.setAccessible(true);
        return (EditarRequerimientoCompraServiceUtil.Transaccion) u.newInstance(new Object[] { impl });
    }
    public static void main(String[] args) throws Exception {
        BD bd = new BD(); Connection con = bd.conexion();
        BusquedaRequerimientoCompraServiceUtil.esperada = con;
        EditarRequerimientoCompraServiceUtil.Transaccion tx = transaccion(con);
        tx.bloquearYObtenerRequerimiento(1388);
        comprobar(bd.sql.get(0).indexOf("bloquear_requerimiento_reclamo") >= 0, "Advisory primero.");
        comprobar(bd.sql.get(1).indexOf("FOR UPDATE OF rp") >= 0 && bd.sql.get(3).indexOf("compras.requerimiento ") >= 0 && bd.sql.get(3).indexOf("FOR UPDATE") >= 0, "RP bloqueado antes de cabecera.");
        comprobar(bd.sql.get(2).indexOf("requerimiento_reclamo_prestacional") >= 0 && bd.sql.get(4).indexOf("ORDER BY id_detalle FOR UPDATE") >= 0, "Vinculo y detalles respetan orden comun despues de RP.");
        tx.getDetalles(1388); tx.listarSectores(); tx.listarTiposPrestacion(); tx.listarAsociacionesNomenclador(2); tx.obtenerNomencladorCanonico(10415);
        comprobar("2=10415".equals(tx.getConfiguracionCompra("COMPRAS_CONCEPTOS_AUTOMATICOS")), "Configuracion misma TX.");
        comprobar(BusquedaRequerimientoCompraServiceUtil.lecturas == 7, "Lecturas misma conexion.");
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();
        d.setId(Integer.valueOf(445)); d.setIdRequerimientoCompra(1388); d.setTipoItem("NOMENCLADOR");
        d.setIdPrestacion(Integer.valueOf(10415)); d.setIdTipoNomenclador(Integer.valueOf(3));
        d.setCodigoNomenclador("101002"); d.setDescripcionNomenclador("Concepto de prueba"); d.setCantidad(Integer.valueOf(1));
        comprobar(tx.guardarConceptoAutomatico(d, "usuario_prueba") == 901, "Identificador tecnico de INSERT.");
        comprobar(bd.sentencias.get(bd.sentencias.size()-1).parametros.get(Integer.valueOf(8)) == null, "RubroNULL no se reclasifica en persistencia.");
        d.setPrecioUnitarioEstimado(new BigDecimal("10.00")); d.setPrecioTotalEstimado(new BigDecimal("17.23"));
        comprobar(tx.persistirTotalDetalle(d, "usuario_prueba") == 1, "Total persistido.");
        comprobar(new BigDecimal("17.23").equals(bd.sentencias.get(bd.sentencias.size()-1).parametros.get(Integer.valueOf(1))), "Se conserva total explicito, sin multiplicar en ServiceImpl.");
        comprobar(tx.persistirImportesDetalle(d, "usuario_prueba") == 1, "Importes persistidos.");
        comprobar(new BigDecimal("17.23").equals(bd.sentencias.get(bd.sentencias.size()-1).parametros.get(Integer.valueOf(2))), "Importe explicito sigue intacto.");
        // guardarCotizacion ahora coordina reglas Java y se prueba con contexto
        // completo en el harness funcional; aqui se verifican escritores JDBC.
        comprobar(tx.persistirSurge(1388, 3, false, "usuario_prueba") == 1,
                "CAS crudo conserva resultado de persistencia sin abrir otra TX.");
        comprobar(bd.commits == 0 && bd.cierres == 0, "Persistencias no hacen commit/cierre propio.");
        bd.fallarEscritura = true;
        boolean fallo = false;
        try { tx.persistirTotalDetalle(d, "usuario_prueba"); } catch (SQLException e) { fallo = true; tx.rollback(); }
        comprobar(fallo && bd.rollbacks == 1, "Fallo JDBC propagado y rollback explicito.");
        bd.fallarEscritura = false;
        RequerimientoCompraReclamoPrestacionalServiceImpl rp = new RequerimientoCompraReclamoPrestacionalServiceImpl();
        Timestamp baja = rp.bloquearReclamoPrestacional(con, 42);
        List<RequerimientoCompraReclamoPrestacional> vinculos = rp.obtenerVinculosPorRPForUpdate(con, 42);
        comprobar(vinculos.size() == 1 && vinculos.get(0).getIdRequerimientoCompra() == 1388, "Vinculo autoritativo mapeado.");
        RequerimientoCompraReclamoPrestacional relacion = vinculos.get(0); relacion.setEstado("BAJA"); relacion.setTokenReserva(null);
        comprobar(rp.guardarBajaVinculo(con, relacion, baja, "usuario_prueba", "VINCULADO") == 1, "Baja vinculo CAS.");
        Sentencia ultima = bd.sentencias.get(bd.sentencias.size() - 1);
        comprobar(ultima.parametros.get(Integer.valueOf(2)) == baja && baja.getNanos() == 123456000, "Fecha exacta microsegundos, sin perdida.");
        comprobar("VINCULADO".equals(ultima.parametros.get(Integer.valueOf(8))), "Estado esperado parametrizado.");
        comprobar(ultima.parametros.get(Integer.valueOf(4)) == null, "Token se limpia segun beanHelper.");
        comprobar(ar.com.ospim.util.ConnectionHelper.aperturas == 0, "Sin otra conexion.");
        comprobar(tx.tieneCreacionReclamoPrestacional(1388), "Existencia de cualquier vinculo autoritativa.");
        comprobar(bd.sql.get(bd.sql.size() - 1).indexOf("baja_fecha") < 0, "El vinculo dado de baja tambien bloquea correccion.");
        comprobar(!tx.existeRequerimientoDuplicado("00000000000", 0, 10415,
                new java.util.Date(0), 1388), "Duplicado consulta misma conexion.");
        tx.commit(); tx.cerrar();
        comprobar(bd.commits == 1 && bd.cierres == 1, "Commit/cierre coordinados.");
        System.out.println("APROBADAS=" + aprobadas + "; JDBC simulado, sin DB ni prueba UI.");
    }
}
