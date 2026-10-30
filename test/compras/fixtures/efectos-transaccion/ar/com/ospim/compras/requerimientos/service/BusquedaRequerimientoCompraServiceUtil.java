package ar.com.ospim.compras.requerimientos.service;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
public class BusquedaRequerimientoCompraServiceUtil {
    public static Connection esperada;
    public static int lecturas;
    private static void verificar(Connection con) {
        if (con != esperada) throw new IllegalStateException("Otra conexion en lectura.");
        lecturas++;
    }
    public static boolean existeRequerimientoDuplicado(Connection con, String cuil,
            int inte, int prestacion, java.util.Date fecha, int excluir) {
        verificar(con); return false;
    }
    public static List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(Connection con, int id) { verificar(con); return new ArrayList<RequerimientoCompraPresupuesto>(); }
    public static List<RequerimientoCompraPresupuesto> listarPresupuestos(Connection con, int id) { verificar(con); return new ArrayList<RequerimientoCompraPresupuesto>(); }
    public static RequerimientoCompra getRequerimientoCompra(Connection con, int id) { verificar(con); return null; }
    public static List<RequerimientoCompraDetalle> getDetalles(Connection con, int id) { verificar(con); return new ArrayList<RequerimientoCompraDetalle>(); }
    public static List<java.util.Map<String,Object>> listarCatalogoSectores(Connection con) { verificar(con); return new ArrayList<java.util.Map<String,Object>>(); }
    public static List<ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion> listarPrestadoresEnviados(Connection con, int id, int limite) { verificar(con); return new ArrayList<ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion>(); }
    public static RequerimientoCompraPresupuesto bloquearDocumento(Connection con, int id, int documento) { verificar(con); return null; }
    public static List<RequerimientoCompraPresupuesto> listarDocumentos(Connection con, int id, int tipo) { verificar(con); return new ArrayList<RequerimientoCompraPresupuesto>(); }
    public static List<ar.com.ospim.global.beans.Empresa> buscarEmpresasCotizacion(Connection con, String cuit, String descripcion, String sucursal, int limite) { verificar(con); return new ArrayList<ar.com.ospim.global.beans.Empresa>(); }
    public static List<RequerimientoCompraSector> listarSectores(Connection con) { verificar(con); return new ArrayList<RequerimientoCompraSector>(); }
    public static List<TipoPrestacionCompra> listarTiposPrestacion(Connection con) { verificar(con); return new ArrayList<TipoPrestacionCompra>(); }
    public static List<SectorNomencladorCompra> listarAsociacionesNomenclador(Connection con, int id) { verificar(con); return new ArrayList<SectorNomencladorCompra>(); }
    public static Nomenclador obtenerNomencladorCanonico(Connection con, int id) { verificar(con); return null; }
    public static String getConfiguracionCompra(String clave, Connection con) { verificar(con); return "2=10415"; }
}
