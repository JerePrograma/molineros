package ar.com.ospim.compras.service;

import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraDetalle;

public class EditarRequerimientoCompraServiceUtil {

    private static EditarRequerimientoCompraServiceImpl instance = null;

    public static EditarRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new EditarRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static int guardarRequerimientoCompra(RequerimientoCompra requerimiento, String usuario) throws Exception {
        return getInstance().guardarRequerimientoCompra(requerimiento, usuario);
    }

    public static int guardarDetalle(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        return getInstance().guardarDetalle(detalle, usuario);
    }

    public static int guardarItem(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        return guardarDetalle(detalle, usuario);
    }

    public static void borrarDetalle(int idRequerimientoDetalle, String usuario) throws Exception {
        getInstance().borrarDetalle(idRequerimientoDetalle, usuario);
    }

    public static void borrarItem(int idItem, String usuario) throws Exception {
        borrarDetalle(idItem, usuario);
    }

    public static void borrarRequerimientoCompra(int idRequerimientoCompra, String usuario) throws Exception {
        getInstance().borrarRequerimientoCompra(idRequerimientoCompra, usuario);
    }

    public static void cambiarEstado(int idRequerimientoCompra, int idEstadoNuevo, String usuario) throws Exception {
        getInstance().cambiarEstado(idRequerimientoCompra, idEstadoNuevo, usuario);
    }

    private EditarRequerimientoCompraServiceUtil() {
    }
}