package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import java.util.ArrayList;
import java.util.List;

/** Reutiliza el buscador legacy y la relacion N:M configurada en Compras. */
public final class NomencladorCompraBusquedaHelper {

    public static TipoPrestacionCompra getTipo(int idTipoPrestacion) throws Exception {
        List<TipoPrestacionCompra> tipos = BusquedaRequerimientoCompraServiceUtil.listarTiposPrestacion();
        for (int i = 0; i < tipos.size(); i++) {
            if (tipos.get(i).getIdInt() == idTipoPrestacion) { return tipos.get(i); }
        }
        return null;
    }

    public static boolean esValido(RequerimientoCompraSector sector,
            int idTipoPrestacion, int idTipoNomenclador) throws Exception {
        if (idTipoPrestacion <= 0) {
            return WebKeysCompras.esNomencladorValidoParaSectorCompras(sector,idTipoNomenclador);
        }
        return WebKeysCompras.esNomencladorValidoParaTipoPrestacionCompras(
                sector,getTipo(idTipoPrestacion),idTipoNomenclador);
    }

    public List<Nomenclador> buscar(RequerimientoCompraSector sector,
            int idTipoPrestacion, int idTipoNomenclador, int marcaReinLiq,
            String codigo, String descripcion) throws Exception {
        TipoPrestacionCompra tipo = getTipo(idTipoPrestacion);
        if (sector == null || !sector.isNomenclador() || tipo == null
                || tipo.getIdSectorInt() != sector.getIdSector()) {
            throw new Exception("El tipo de prestación no corresponde al sector del requerimiento.");
        }
        List<Nomenclador> resultado = new ArrayList<Nomenclador>();
        List<Integer> admitidos = tipo.getNomencladores();
        for (int i = 0; i < admitidos.size(); i++) {
            int idTipo = admitidos.get(i).intValue();
            if (idTipoNomenclador > 0 && idTipo != idTipoNomenclador) { continue; }
            List<Nomenclador> parciales = sector.isBusquedaNomencladorMedica()
                    ? NomencladorServiceUtil.getListaNomencladorPrestacionesMedicasCompras(
                        idTipo, (descripcion != null ? descripcion.trim() : ""), 0, (codigo != null ? codigo.trim() : ""), false, "")
                    : NomencladorServiceUtil.getListaNomenclador(
                        idTipo, (descripcion != null ? descripcion.trim() : ""), 0, (codigo != null ? codigo.trim() : ""), false, "");
            for (int j = 0; parciales != null && j < parciales.size(); j++) {
                Nomenclador item = parciales.get(j);
                if (item != null && item.getBaja_fecha() == null && item.getId_prestacion() > 0
                        && tipo.admiteNomenclador(item.getId_tipo_nomenclador())) {
                    resultado.add(item);
                }
            }
        }
        return resultado;
    }
}
