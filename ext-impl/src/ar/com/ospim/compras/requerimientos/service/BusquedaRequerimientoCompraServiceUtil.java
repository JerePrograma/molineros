package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;

import java.util.List;

public class BusquedaRequerimientoCompraServiceUtil {

    private static BusquedaRequerimientoCompraServiceImpl instance = null;

    public static BusquedaRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new BusquedaRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static List<RequerimientoCompra> buscarRequerimientos(
            RequerimientoCompraFiltro filtro) throws Exception {

        return getInstance().buscarRequerimientos(filtro);
    }

    public static RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getRequerimientoCompra(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getDetalles(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getItems(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraEstado> listarEstados()
            throws Exception {

        return getInstance().listarEstados();
    }

    public static List<RequerimientoCompraSector> listarSectores()
            throws Exception {

        return getInstance().listarSectores();
    }

    public static RequerimientoCompraEstado getEstado(int idEstado)
            throws Exception {

        return getInstance().getEstado(idEstado);
    }

    public static RequerimientoCompraSector getSector(int idSector)
            throws Exception {

        return getInstance().getSector(idSector);
    }

    public static List<PrestadorCotizacion> buscarPrestadoresEnviados(
            int idRequerimientoCompra,
            String texto,
            int limite) throws Exception {

        return getInstance().buscarPrestadoresEnviados(
                idRequerimientoCompra,
                texto,
                limite
        );
    }

    public static List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra) throws Exception {

        return getInstance().listarPrestadoresEnviados(
                idRequerimientoCompra
        );
    }

    public static boolean hayPrestadoresPendientesNotificacion(
            int idRequerimientoCompra) throws Exception {

        return getInstance().hayPrestadoresPendientesNotificacion(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraPresupuesto> listarPresupuestos(
            int idRequerimientoCompra) throws Exception {

        return getInstance().listarPresupuestos(
                idRequerimientoCompra
        );
    }

    public static RequerimientoCompraPresupuesto getPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        return getInstance().getPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra
        );
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }
}
