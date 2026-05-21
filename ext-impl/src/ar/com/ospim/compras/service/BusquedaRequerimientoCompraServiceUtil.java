package ar.com.ospim.compras.service;

import java.util.List;

import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraAdjunto;
import ar.com.ospim.compras.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.beans.RequerimientoCompraItem;

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

    public static List<RequerimientoCompraItem> getItems(int idRequerimientoCompra) throws Exception {
        return getInstance().getItems(idRequerimientoCompra);
    }
    public static List<RequerimientoCompraAdjunto> getAdjuntos(int idRequerimientoCompra) throws Exception {
        return getInstance().getAdjuntos(idRequerimientoCompra);
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }
}
