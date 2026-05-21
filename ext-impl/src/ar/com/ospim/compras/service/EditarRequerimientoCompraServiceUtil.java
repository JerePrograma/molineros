package ar.com.ospim.compras.service;

import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraItem;

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

    public static void guardarItem(RequerimientoCompraItem item, String usuario) throws Exception {
        getInstance().guardarItem(item, usuario);
    }

    public static void borrarItem(int idItem, String usuario) throws Exception {
        getInstance().borrarItem(idItem, usuario);
    }

    public static void borrarRequerimientoCompra(int idRequerimientoCompra, String usuario) throws Exception {
        getInstance().borrarRequerimientoCompra(idRequerimientoCompra, usuario);
    }

    private EditarRequerimientoCompraServiceUtil() {
    }
}
