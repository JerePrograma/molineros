package ar.com.ospim.compras.service;

import java.util.List;

import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.beans.RequerimientoCompraFiltro;

public class BusquedaRequerimientoCompraServiceUtil {

    private static BusquedaRequerimientoCompraServiceImpl instance = null;

    public static BusquedaRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new BusquedaRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static List<RequerimientoCompra> buscarRequerimientos(RequerimientoCompraFiltro filtro) throws Exception {
        return getInstance().buscarRequerimientos(filtro);
    }

    public static RequerimientoCompra getRequerimientoCompra(int idRequerimientoCompra) throws Exception {
        return getInstance().getRequerimientoCompra(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getDetalles(int idRequerimientoCompra) throws Exception {
        return getInstance().getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getItems(int idRequerimientoCompra) throws Exception {
        return getInstance().getDetalles(idRequerimientoCompra);
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }
}
