package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestadorSector;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.ConfiguracionCotizacionPrestadorServiceUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Valida y coordina la configuración de prestadores por sector y tipo de
 * cotización. La persistencia permanece en el ServiceImpl.
 */
public class ConfiguracionCotizacionPrestadorHelper {

    public void guardarConfiguracion(
            int idSector,
            List<TipoPrestadorSector> seleccionados,
            String usuario) throws Exception {

        RequerimientoCompraSector sector =
                BusquedaRequerimientoCompraServiceUtil
                        .getSector(idSector);

        if (sector == null || sector.getIdSector() <= 0) {
            throw new Exception(
                    "El sector seleccionado no existe."
            );
        }

        List<TipoPrestadorSector> configurables =
                ConfiguracionCotizacionPrestadorServiceUtil
                        .listarTiposPrestadorSector(idSector);

        Set<String> clavesValidas = new HashSet<String>();

        for (int i = 0;
                configurables != null && i < configurables.size();
                i++) {

            TipoPrestadorSector tipo = configurables.get(i);

            if (tipo != null
                    && tipo.getClaveConfiguracion().length() > 0) {
                clavesValidas.add(tipo.getClaveConfiguracion());
            }
        }

        Set<String> clavesRecibidas = new HashSet<String>();

        for (int i = 0;
                seleccionados != null && i < seleccionados.size();
                i++) {

            TipoPrestadorSector tipo = seleccionados.get(i);
            String clave = tipo != null
                    ? tipo.getClaveConfiguracion()
                    : "";

            if (clave.length() == 0
                    || !clavesValidas.contains(clave)
                    || !clavesRecibidas.add(clave)) {

                throw new Exception(
                        "La configuración de prestadores recibida "
                                + "es inválida o está repetida."
                );
            }
        }

        ConfiguracionCotizacionPrestadorServiceUtil
                .guardarConfiguracion(
                        idSector,
                        seleccionados,
                        usuario
                );
    }
}
