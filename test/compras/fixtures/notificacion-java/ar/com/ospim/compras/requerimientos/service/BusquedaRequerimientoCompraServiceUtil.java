package ar.com.ospim.compras.requerimientos.service;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
public class BusquedaRequerimientoCompraServiceUtil {
    public static final Map<String,String> configuraciones = new HashMap<String,String>();
    public static RequerimientoCompra requerimiento;
    public static int lecturas;
    public static boolean permitirLecturaGlobalPreparacion;
    public static synchronized String getConfiguracionCompra(String clave, Connection con) { lecturas++; return configuraciones.get(clave); }
    public static String getConfiguracionCompra(String clave) { if (permitirLecturaGlobalPreparacion) return configuraciones.get(clave); throw new IllegalStateException("No se permite leer configuracion fuera de TX."); }
    public static RequerimientoCompra getRequerimientoCompra(Connection con, int id) { return requerimiento; }
    public static RequerimientoCompra getRequerimientoCompra(int id) { throw new IllegalStateException("No se permite lectura fuera de TX en la prueba."); }
    public static List<Map<String,Object>> getFilasPedidoPresupuestoPdf(int id) { throw new IllegalStateException("La prueba no genera PDF."); }
    public static Map<Integer,String> obtenerDrogasDetalleFarmacia(List<RequerimientoCompraDetalle> d) { throw new IllegalStateException("La prueba no consulta medicamentos."); }
    public static List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(int id) { throw new IllegalStateException("La prueba no consulta DL."); }
}
