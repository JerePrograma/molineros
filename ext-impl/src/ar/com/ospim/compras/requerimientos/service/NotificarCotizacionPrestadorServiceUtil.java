package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.helper.NotificarCotizacionPrestadorHelper;

/**
 * Fachada estática legacy.
 */
public class NotificarCotizacionPrestadorServiceUtil {

    private static NotificarCotizacionPrestadorServiceImpl instance;

    private static final NotificarCotizacionPrestadorHelper helper =
            new NotificarCotizacionPrestadorHelper();

    public static NotificarCotizacionPrestadorServiceImpl getInstance() {
        if (instance == null) {
            instance = new NotificarCotizacionPrestadorServiceImpl();
        }

        return instance;
    }

    public static NotificacionCotizacionResultado notificarPrestadores(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        return helper.notificarPrestadores(
                idRequerimientoCompra,
                usuario,
                companyId
        );
    }

    private NotificarCotizacionPrestadorServiceUtil() {
    }
}
