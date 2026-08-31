package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Consulta y filtrado de nomencladores para el editor de detalle de Compras.
 *
 * El JSP no debe ejecutar NomencladorServiceUtil ni aplicar la matriz de
 * sectores. Esta clase deja el resultado ya validado para presentación.
 */
public final class NomencladorCompraBusquedaHelper {

    private static final int[] TIPOS_PRESTACIONES_MEDICAS = {
            WebKeysCompras.TIPO_NOMENCLADOR_ANALISIS_CLINICOS,
            WebKeysCompras.TIPO_NOMENCLADOR_PRACTICAS_ESPECIALIZADAS,
            WebKeysCompras.TIPO_NOMENCLADOR_PROTESIS_INSUMOS,
            WebKeysCompras.TIPO_NOMENCLADOR_QUIRURGICO,
            WebKeysCompras.TIPO_NOMENCLADOR_PROPIO
    };

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
            resultados = buscarPrestacionesMedicas(
                    idTipoNomenclador,
                    descripcionNormalizada,
                    codigoNormalizado
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
                    && idTipoNomenclador > 0
                    && idTipoReal != idTipoNomenclador) {

                continue;
            }

            filtrados.add(
                    nomenclador
            );
        }

        return filtrados;
    }

    private List<Nomenclador> buscarPrestacionesMedicas(
            int idTipoNomenclador,
            String descripcion,
            String codigo) throws Exception {

        List<Nomenclador> resultados = new ArrayList<Nomenclador>();
        Set<String> identidades = new HashSet<String>();

        if (idTipoNomenclador > 0
                && !WebKeysCompras
                .esTipoNomencladorPrestacionesMedicas(
                        idTipoNomenclador
                )) {

            throw new Exception(
                    "La clasificación técnica informada no es válida "
                            + "para PRESTACIONES MÉDICAS."
            );
        }

        for (int i = 0; i < TIPOS_PRESTACIONES_MEDICAS.length; i++) {
            int tipo = TIPOS_PRESTACIONES_MEDICAS[i];

            if (idTipoNomenclador <= 0
                    && tipo == WebKeysCompras
                    .TIPO_NOMENCLADOR_PROTESIS_INSUMOS) {

                continue;
            }

            if (idTipoNomenclador > 0
                    && tipo != idTipoNomenclador) {
                continue;
            }

            List<Nomenclador> parciales =
                    NomencladorServiceUtil
                            .getListaNomencladorPrestacionesMedicasCompras(
                                    tipo,
                                    descripcion,
                                    0,
                                    codigo,
                                    false,
                                    ""
                            );

            for (int j = 0;
                    parciales != null && j < parciales.size();
                    j++) {

                Nomenclador nomenclador = parciales.get(j);

                if (nomenclador == null) {
                    continue;
                }

                String identidad =
                        String.valueOf(nomenclador.getId_tipo_nomenclador())
                                + ":"
                                + String.valueOf(
                                nomenclador.getId_prestacion()
                        );

                if (identidades.add(identidad)) {
                    resultados.add(nomenclador);
                }
            }
        }

        return resultados;
    }
}
