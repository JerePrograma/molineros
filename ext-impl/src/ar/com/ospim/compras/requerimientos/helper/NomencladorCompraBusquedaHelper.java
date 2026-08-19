package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;

import java.util.ArrayList;
import java.util.List;

/**
 * Consulta y filtrado de nomencladores para el editor de detalle de Compras.
 *
 * El JSP no debe ejecutar NomencladorServiceUtil ni aplicar la matriz de
 * sectores. Esta clase deja el resultado ya validado para presentación.
 */
public final class NomencladorCompraBusquedaHelper {

    public List<Nomenclador> buscar(
            String sectorDescripcion,
            int idTipoNomenclador,
            int marcaReinLiq,
            String codigo,
            String descripcion) throws Exception {

        String sector =
                WebKeysCompras.normalizarSectorCompra(
                        sectorDescripcion
                );

        String codigoNormalizado =
                codigo != null
                        ? codigo.trim()
                        : "";

        String descripcionNormalizada =
                descripcion != null
                        ? descripcion.trim()
                        : "";

        List<Nomenclador> resultados;

        if ("DISCAPACIDAD".equals(sector)
                && marcaReinLiq
                == WebKeysCompras.MARCA_REIN_LIQ_DISCAPACIDAD) {

            resultados =
                    NomencladorServiceUtil
                            .getListaNomencladorMarcaReinLiq(
                                    WebKeysCompras
                                            .FILTRO_NOMENCLADOR_GENERAL,
                                    descripcionNormalizada,
                                    0,
                                    codigoNormalizado,
                                    false,
                                    "",
                                    WebKeysCompras
                                            .MARCA_REIN_LIQ_DISCAPACIDAD
                            );

        } else if ("PRESTACIONES MEDICAS".equals(sector)) {

            if (!WebKeysCompras
                    .esTipoNomencladorPrestacionesMedicas(
                            idTipoNomenclador
                    )) {

                throw new Exception(
                        "El Tipo Nomenclador informado no es válido "
                                + "para PRESTACIONES MEDICAS."
                );
            }

            resultados =
                    NomencladorServiceUtil
                            .getListaNomencladorPrestacionesMedicasCompras(
                                    idTipoNomenclador,
                                    descripcionNormalizada,
                                    0,
                                    codigoNormalizado,
                                    false,
                                    ""
                            );

        } else {
            resultados =
                    NomencladorServiceUtil
                            .getListaNomenclador(
                                    idTipoNomenclador,
                                    descripcionNormalizada,
                                    0,
                                    codigoNormalizado,
                                    false,
                                    ""
                            );
        }

        List<Nomenclador> filtrados =
                new ArrayList<Nomenclador>();

        for (int i = 0;
                resultados != null
                        && i < resultados.size();
                i++) {

            Nomenclador nomenclador =
                    resultados.get(i);

            if (nomenclador == null
                    || nomenclador.getBaja_fecha() != null
                    || nomenclador.getId_prestacion() <= 0
                    || nomenclador.getId_tipo_nomenclador() <= 0) {

                continue;
            }

            int idTipoReal =
                    nomenclador.getId_tipo_nomenclador();

            if (!WebKeysCompras
                    .esNomencladorValidoParaSectorCompras(
                            sector,
                            idTipoReal,
                            nomenclador.getMarcaReintegroLiquidacion(),
                            nomenclador.getCodigo()
                    )) {

                continue;
            }

            if ("PRESTACIONES MEDICAS".equals(sector)
                    && idTipoReal != idTipoNomenclador) {

                continue;
            }

            filtrados.add(
                    nomenclador
            );
        }

        return filtrados;
    }
}
