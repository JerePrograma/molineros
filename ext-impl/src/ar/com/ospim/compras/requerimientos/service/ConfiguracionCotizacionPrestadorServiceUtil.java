package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.TipoPrestadorSector;

import java.util.List;

public class ConfiguracionCotizacionPrestadorServiceUtil {

    private static ConfiguracionCotizacionPrestadorServiceImpl instance;

    public static ConfiguracionCotizacionPrestadorServiceImpl getInstance() {
        if (instance == null) {
            instance =
                    new ConfiguracionCotizacionPrestadorServiceImpl();
        }

        return instance;
    }

    public static List<TipoPrestadorSector>
    listarTiposPrestadorSector(int idSector) throws Exception {

        return getInstance().listarTiposPrestadorSector(idSector);
    }

    public static void guardarConfiguracion(
            int idSector,
            List<TipoPrestadorSector> tiposSeleccionados,
            String usuario) throws Exception {

        getInstance().guardarConfiguracion(
                idSector,
                tiposSeleccionados,
                usuario
        );
    }

    public static void guardarConfiguracion(
            int idSector,
            int[] idsTiposSeleccionados,
            String usuario) throws Exception {

        getInstance().guardarConfiguracion(
                idSector,
                idsTiposSeleccionados,
                usuario
        );
    }

    private ConfiguracionCotizacionPrestadorServiceUtil() {
    }
}
