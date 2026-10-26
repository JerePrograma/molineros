package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;

public class RequerimientoCompraOrdenServiceUtil {

    private static final RequerimientoCompraOrdenServiceImpl service =
            new RequerimientoCompraOrdenServiceImpl();

    public static void completarDatosConsulta(RequerimientoCompraOrden orden,
            RequerimientoCompra requerimiento) throws Exception {
        service.completarDatosConsulta(orden, requerimiento);
    }

    public static RequerimientoCompraOrden obtenerPorRequerimiento(int idRequerimiento)
            throws Exception {
        return service.obtenerPorRequerimiento(idRequerimiento);
    }

    public static RequerimientoCompraOrden crear(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return service.crear(orden, usuario);
    }

    public static RequerimientoCompraOrden actualizar(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return service.actualizar(orden, usuario);
    }
}
