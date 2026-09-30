package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.farmaciaOspim.beans.ItemMedicacionTotal;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;

import com.liferay.portal.kernel.util.GetterUtil;

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
            WebKeysCompras.TIPO_NOMENCLADOR_PROPIO,
            WebKeysCompras.TIPO_NOMENCLADOR_PROTESIS
    };

    public List<Nomenclador> buscar(
            String sectorDescripcion,
            int idTipoPrestacion,
            int idTipoNomenclador,
            int marcaReinLiq,
            String codigo,
            String descripcion,
            String droga) throws Exception {

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

        String drogaNormalizada =
                "FARMACIA".equals(sector) && droga != null
                        ? droga.trim()
                        : "";

        Set<Integer> troqueles = null;

        if (drogaNormalizada.length() > 0) {
            List<Medicamento> medicamentos =
                    BusquedaMedicamentoServiceUtil
                            .getBusquedaMedicamentosDroga(
                                    drogaNormalizada
                            );

            if (medicamentos == null) {
                throw new Exception(
                        "No se pudieron buscar medicamentos por droga."
                );
            }

            troqueles = new HashSet<Integer>();

            for (int i = 0; i < medicamentos.size(); i++) {
                Medicamento medicamento = medicamentos.get(i);

                if (medicamento != null
                        && medicamento.getTroquel() > 0) {

                    troqueles.add(
                            Integer.valueOf(medicamento.getTroquel())
                    );
                }
            }

            if (troqueles.isEmpty()) {
                return new ArrayList<Nomenclador>();
            }
        }

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
                    idTipoPrestacion,
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

            boolean valido =
                    "PRESTACIONES MEDICAS".equals(sector)
                            ? WebKeysCompras
                            .esNomencladorValidoParaTipoPrestacionCompras(
                                    sector,
                                    idTipoPrestacion,
                                    idTipoReal,
                                    nomenclador.getMarcaReintegroLiquidacion(),
                                    nomenclador.getCodigo()
                            )
                            : WebKeysCompras
                            .esNomencladorValidoParaSectorCompras(
                                    sector,
                                    idTipoReal,
                                    nomenclador.getMarcaReintegroLiquidacion(),
                                    nomenclador.getCodigo()
                            );

            if (!valido) {
                continue;
            }

            if ("PRESTACIONES MEDICAS".equals(sector)
                    && idTipoNomenclador > 0
                    && idTipoReal != idTipoNomenclador) {

                continue;
            }

            if (troqueles != null) {
                String codigoTroquel =
                        nomenclador.getCodigo() != null
                                ? nomenclador.getCodigo().trim()
                                : "";

                if (!codigoTroquel.matches("^[0-9]+$")
                        || !troqueles.contains(
                                Integer.valueOf(
                                        GetterUtil.getInteger(codigoTroquel, 0)
                                )
                        )) {

                    continue;
                }
            }

            filtrados.add(
                    nomenclador
            );
        }

        return filtrados;
    }

    public String obtenerDrogaMedicamento(
            String sectorDescripcion,
            String codigo) throws Exception {

        String sector =
                WebKeysCompras.normalizarSectorCompra(
                        sectorDescripcion
                );

        if (!"FARMACIA".equals(sector)) {
            return "";
        }

        String codigoTroquel =
                codigo != null
                        ? codigo.trim()
                        : "";

        if (!codigoTroquel.matches("^[0-9]+$")) {
            return "";
        }

        int troquel = GetterUtil.getInteger(codigoTroquel, 0);

        if (troquel <= 0) {
            return "";
        }

        List<ItemMedicacionTotal> medicamentos =
                BusquedaMedicamentoServiceUtil
                        .getBusquedaMedicamentosOspimTotal(
                                troquel,
                                0,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                false,
                                0,
                                false
                        );

        if (medicamentos == null) {
            throw new Exception(
                    "No se pudo consultar la droga del medicamento."
            );
        }

        Medicamento medicamento =
                medicamentos.size() > 0
                        ? medicamentos.get(0)
                        : null;

        return medicamento != null && medicamento.getDroga() != null
                ? medicamento.getDroga().trim()
                : "";
    }

    private List<Nomenclador> buscarPrestacionesMedicas(
            int idTipoPrestacion,
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

            if (idTipoNomenclador <= 0
                    && tipo == WebKeysCompras
                    .TIPO_NOMENCLADOR_PROTESIS
                    && !WebKeysCompras.esTipoPrestacionProtesis(
                    idTipoPrestacion
            )) {

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
