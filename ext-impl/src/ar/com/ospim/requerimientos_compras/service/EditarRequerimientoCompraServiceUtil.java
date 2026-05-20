package ar.com.ospim.requerimientos_compras.service;

import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompra;
import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraAdjunto;
import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraItem;

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

    public static int guardarItem(RequerimientoCompraItem item, String usuario) throws Exception {
        return getInstance().guardarItem(item, usuario);
    }

    public static void borrarItem(int idItem, String usuario) throws Exception {
        getInstance().borrarItem(idItem, usuario);
    }

    public static void borrarRequerimientoCompra(int idRequerimientoCompra, String usuario) throws Exception {
        getInstance().borrarRequerimientoCompra(idRequerimientoCompra, usuario);
    }

    public static int guardarAdjunto(RequerimientoCompraAdjunto adjunto, String usuario) throws Exception {
        return getInstance().guardarAdjunto(adjunto, usuario);
    }

    public static void borrarAdjunto(int idAdjunto, String usuario) throws Exception {
        getInstance().borrarAdjunto(idAdjunto, usuario);
    }

    private EditarRequerimientoCompraServiceUtil() {
    }
}
