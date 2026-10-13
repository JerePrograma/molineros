package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
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
 * La búsqueda técnica usa la configuración efectiva de Compras en SQL.
 * Se conserva la resolución de droga y troquel de Farmacia.
 */
public final class NomencladorCompraBusquedaHelper {

    public List<Nomenclador> buscar(
            int idSector,
            String sectorDescripcion,
            Integer idTipoPrestacion,
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

        }

        List<Nomenclador> resultados =
                BusquedaRequerimientoCompraServiceUtil.buscarNomencladores(
                        idSector, idTipoPrestacion,
                        descripcionNormalizada, codigoNormalizado,
                        troqueles != null ? new ArrayList<Integer>(troqueles) : null);

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

}
