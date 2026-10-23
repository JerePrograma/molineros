package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;

public class RequerimientoCompraOrdenServiceUtil {

    private static final RequerimientoCompraOrdenServiceImpl service =
            new RequerimientoCompraOrdenServiceImpl();

    public static RequerimientoCompraOrden obtenerPorRequerimiento(int idRequerimiento)
            throws Exception {
        return service.obtenerPorRequerimiento(idRequerimiento);
    }

    public static RequerimientoCompraOrden crear(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return service.crear(orden, usuario);
    }
}
