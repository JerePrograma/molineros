import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;

/* Productive JDBC readers with mutable authoritative rows and no SQL functions.
 * The boundary records SELECT and supplies master values only for explicit joins.
 * SQL execution and QA query plans are a separate read-only verification. */
public class ComprasLecturasVigentesTest {
    private static int checks;
    private static String nombre = "Nombre vigente";
    private static String descripcion = "Prestacion vigente";
    private static String codigo = "NUEVO";
    private static boolean ausente;
    private static boolean vacio;
    private static String lastSql;
    private static final Map<Integer,Object> binds = new HashMap<Integer,Object>();

    public static void main(String[] args) throws Exception {
        ConfiguracionComprasDatosFixture.instalar();
        BusquedaRequerimientoCompraServiceImpl service = new ConfiguracionComprasDatosFixture();
        Connection con = connection();
        RequerimientoCompra r = service.getCabeceraRequerimiento(con, 41);
        eq("master affiliate name", nombre, r.getAfiliadoNombre());
        eq("requested requirement", Integer.valueOf(41), binds.get(Integer.valueOf(1)));
        nombre = "Nombre corregido";
        r = service.getCabeceraRequerimiento(con, 41);
        eq("master edit visible on next read", nombre, r.getAfiliadoNombre());
        vacio = true;
        r = service.getCabeceraRequerimiento(con, 41);
        eq("empty master overrides old snapshot", null, r.getAfiliadoNombre());
        vacio = false;
        ausente = true;
        r = service.getCabeceraRequerimiento(con, 41);
        eq("missing master retains requirement", Integer.valueOf(41), r.getId());
        eq("missing master does not reuse old name", null, r.getAfiliadoNombre());
        ausente = false;
        RequerimientoCompraDetalle d = service.getDetalles(con, 41).get(0);
        eq("canonical detail description", descripcion, d.getDescripcionNomenclador());
        eq("canonical detail code", codigo, d.getCodigoNomenclador());
        descripcion = "Prestacion corregida"; codigo = "OTRO";
        d = service.getDetalles(con, 41).get(0);
        eq("detail master edit visible", descripcion, d.getDescripcionNomenclador());
        eq("detail master code edit visible", codigo, d.getCodigoNomenclador());
        vacio = true;
        d = service.getDetalles(con, 41).get(0);
        eq("empty detail master clears copied text", null, d.getDescripcionNomenclador());
        check(lastSql.indexOf("public.medicamentos") >= 0, "medication master joined");
        check(lastSql.indexOf("public.prestador") >= 0, "provider master joined");
        check(lastSql.indexOf("id_medicamento") >= 0, "medication identity retained");
        System.out.println("COMPRAS_LECTURAS_VIGENTES_OFFLINE_OK checks=" + checks);
    }
    private static Connection connection() {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] {Connection.class}, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) throws Throwable {
                if ("prepareCall".equals(m.getName())) throw new SQLException("No SQL functions installed in fixture");
                if ("prepareStatement".equals(m.getName())) {
                    final String sql = ((String)a[0]).toLowerCase(); lastSql = sql;
                    if (sql.indexOf("compras.get_") >= 0 || sql.indexOf("compras.requerimiento_base(") >= 0)
                        throw new SQLException("No SQL functions installed in fixture");
                    if (sql.indexOf("compras.requerimiento_detalle") >= 0)
                        check(sql.indexOf("autorizaciones.nomenclador") >= 0, "canonical nomenclator joined");
                    else check(sql.indexOf("public.afiliado") >= 0, "affiliate master joined");
                    binds.clear();
                    return Proxy.newProxyInstance(PreparedStatement.class.getClassLoader(), new Class[] {PreparedStatement.class}, new InvocationHandler() {
                        public Object invoke(Object p, Method m, Object[] a) throws Throwable {
                            if (m.getName().startsWith("set") && a != null && a.length > 1) {binds.put((Integer)a[0],a[1]);return null;}
                            if ("executeQuery".equals(m.getName())) return rows(sql.indexOf("compras.requerimiento_detalle") >= 0);
                            return defaultValue(m.getReturnType());
                        }
                    });
                }
                return defaultValue(m.getReturnType());
            }
        });
    }
    private static ResultSet rows(final boolean detail) {
        return (ResultSet) Proxy.newProxyInstance(ResultSet.class.getClassLoader(), new Class[] {ResultSet.class}, new InvocationHandler() {
            private boolean first = true;
            private Object last;
            public Object invoke(Object p, Method m, Object[] a) {
                if ("next".equals(m.getName())) {boolean r = first;first=false;return Boolean.valueOf(r);}
                if ("wasNull".equals(m.getName())) return Boolean.valueOf(last == null);
                if (m.getName().startsWith("get") && a != null) {
                    String key = String.valueOf(a[0]); last=null;
                    if ("id".equals(key)) last=Integer.valueOf(detail?17:41);
                    if ("id_requerimiento".equals(key)) last=Integer.valueOf(41);
                    if ("id_estado".equals(key)) last=Integer.valueOf(1);
                    if ("afiliado_cuil_titular".equals(key)) last="20000000001";
                    if ("afiliado_int".equals(key)) last=Integer.valueOf(0);
                    if ("afiliado_nombre".equals(key) && !ausente && !vacio) last=nombre;
                    if ("codigo_nomenclador".equals(key) && !vacio) last=codigo;
                    if ("descripcion_nomenclador".equals(key) && !vacio) last=descripcion;
                    if ("getInt".equals(m.getName())) return Integer.valueOf(last==null?0:((Number)last).intValue());
                    if ("getLong".equals(m.getName())) return Long.valueOf(last==null?0:((Number)last).longValue());
                    if ("getBoolean".equals(m.getName())) return Boolean.FALSE;
                    if ("getString".equals(m.getName())) return last==null?null:String.valueOf(last);
                    return last;
                }
                return defaultValue(m.getReturnType());
            }
        });
    }
    private static Object defaultValue(Class type) {
        if (type == Boolean.TYPE) return Boolean.FALSE;
        if (type == Integer.TYPE) return Integer.valueOf(0);
        if (type == Long.TYPE) return Long.valueOf(0);
        return null;
    }
    private static void eq(String label, Object expected, Object actual) {
        check(expected==null?actual==null:expected.equals(actual),label+": expected="+expected+" actual="+actual);
    }
    private static void check(boolean ok,String label) {checks++;if(!ok)throw new AssertionError(label);}
}
