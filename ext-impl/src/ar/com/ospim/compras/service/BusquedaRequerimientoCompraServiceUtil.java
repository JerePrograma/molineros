package ar.com.ospim.compras.service;

import java.util.List;

import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.beans.RequerimientoCompraEstado;
import ar.com.ospim.compras.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.beans.RequerimientoCompraSector;

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

    public static List<RequerimientoCompraEstado> listarEstados() throws Exception {
        return getInstance().listarEstados();
    }

    public static List<RequerimientoCompraSector> listarSectores() throws Exception {
        return getInstance().listarSectores();
    }

    public static RequerimientoCompraEstado getEstado(int idEstado) throws Exception {
        return getInstance().getEstado(idEstado);
    }

    public static RequerimientoCompraSector getSector(int idSector) throws Exception {
        return getInstance().getSector(idSector);
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }
}