package ar.com.ospim.compras.service;

public class AprobacionRequerimientoCompraServiceUtil {

    private static AprobacionRequerimientoCompraServiceImpl instance = null;

    public static AprobacionRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new AprobacionRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static void cambiarEstado(int idRequerimientoCompra, int estadoNuevo, String comentario, String usuario)
            throws Exception {

        getInstance().cambiarEstado(idRequerimientoCompra, estadoNuevo, comentario, usuario);
    }

    public static boolean validarCambioEstado(int estadoActual, int estadoNuevo) {
        return getInstance().validarCambioEstado(estadoActual, estadoNuevo);
    }

    private AprobacionRequerimientoCompraServiceUtil() {
    }
}
