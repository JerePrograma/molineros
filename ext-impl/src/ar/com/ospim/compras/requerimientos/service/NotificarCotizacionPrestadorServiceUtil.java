package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;

public class NotificarCotizacionPrestadorServiceUtil {

    private static NotificarCotizacionPrestadorServiceImpl instance;

    public static NotificarCotizacionPrestadorServiceImpl
    getInstance() {

        if (instance == null) {
            instance =
                    new NotificarCotizacionPrestadorServiceImpl();
        }

        return instance;
    }

    public static NotificacionCotizacionResultado
    notificarPrestadores(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        return getInstance().notificarPrestadores(
                idRequerimientoCompra,
                usuario,
                companyId
        );
    }

    private NotificarCotizacionPrestadorServiceUtil() {
    }
}