package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;

import java.util.List;

/**
 * Fachada estática legacy para las consultas de cotización a prestadores.
 */
public class NotificarCotizacionPrestadorServiceUtil {

    private static NotificarCotizacionPrestadorServiceImpl instance = null;

    public static NotificarCotizacionPrestadorServiceImpl getInstance() {
        if (instance == null) {
            instance = new NotificarCotizacionPrestadorServiceImpl();
        }

        return instance;
    }

    public static List<PrestadorCotizacion> listarPrestadoresCandidatos(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        return getInstance().listarPrestadoresCandidatos(
                idRequerimientoCompra
        );
    }

    public static List<PrestadorCotizacion>
    listarPrestadoresConfiguracionCorreosPorRubro(
            int idTipoPrestacion) throws Exception {

        if (idTipoPrestacion <= 0) {
            throw new Exception(
                    "Debe informar el rubro de cotizaci\u00f3n."
            );
        }

        return getInstance()
                .listarPrestadoresConfiguracionCorreosPorRubro(
                        idTipoPrestacion
                );
    }
}
