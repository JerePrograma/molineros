package ar.com.ospim.compras.requerimientos.service;
import java.sql.Connection;
import java.util.*;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.global.beans.Empresa;
public class BusquedaRequerimientoCompraServiceUtil {
    public static final Map<String,String> configuraciones = new HashMap<String,String>();
    public static RequerimientoCompra requerimiento;
    public static List<RequerimientoCompraPresupuesto> documentos=new ArrayList<RequerimientoCompraPresupuesto>();
    public static boolean permitirLecturaGlobalPreparacion;
    public static String getConfiguracionCompra(String clave,Connection con){if(con==null)throw new IllegalStateException("Conexion requerida.");return configuraciones.get(clave);}
    public static String getConfiguracionCompra(String clave){if(permitirLecturaGlobalPreparacion)return configuraciones.get(clave);throw new IllegalStateException("Configuracion global prohibida.");}
    public static RequerimientoCompra getRequerimientoCompra(Connection con,int id){return requerimiento;}
    public static RequerimientoCompra getRequerimientoCompra(int id){throw new IllegalStateException("Lectura global prohibida.");}
    public static List<RequerimientoCompraDetalle> getDetalles(int id){throw new IllegalStateException("Lectura global prohibida.");}
    public static List<RequerimientoCompraPresupuesto> listarDocumentos(Connection con,int id,int tipo){return documentos;}
    public static List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(Connection con,int id){return new ArrayList<RequerimientoCompraPresupuesto>();}
    public static List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(int id){throw new IllegalStateException("Lectura global prohibida.");}
    public static List<RequerimientoCompraPresupuesto> listarCotizacionesEmpresa(int id){throw new IllegalStateException("Lectura global prohibida.");}
    public static List<Empresa> buscarEmpresasCotizacion(Connection con,String cuit,String descripcion,String sucursal,int limite){return new ArrayList<Empresa>();}
}
