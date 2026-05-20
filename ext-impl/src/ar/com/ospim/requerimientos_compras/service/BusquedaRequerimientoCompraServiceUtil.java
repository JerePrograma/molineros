package ar.com.ospim.requerimientos_compras.service;

import java.util.List;

import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompra;
import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraAdjunto;
import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraFiltro;
import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraHistorial;
import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraItem;

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

    public static List<RequerimientoCompraHistorial> getHistorial(int idRequerimientoCompra) throws Exception {
        return getInstance().getHistorial(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraAdjunto> getAdjuntos(int idRequerimientoCompra) throws Exception {
        return getInstance().getAdjuntos(idRequerimientoCompra);
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }
}
