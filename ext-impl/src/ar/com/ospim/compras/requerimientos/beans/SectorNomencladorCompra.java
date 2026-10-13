package ar.com.ospim.compras.requerimientos.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Criterios concretos de una asociación de nomencladores de Compras. */
public class SectorNomencladorCompra {

    private Integer idTipoNomenclador;
    private Integer marcaReinLiq;
    private String codigoNomenclador;

    public Integer getIdTipoNomenclador() {
        return idTipoNomenclador;
    }

    public Integer getMarcaReinLiq() {
        return marcaReinLiq;
    }

    public String getCodigoNomenclador() {
        return codigoNomenclador;
    }

    public static SectorNomencladorCompra getMapping(ResultSet rs)
            throws SQLException {
        SectorNomencladorCompra asociacion = new SectorNomencladorCompra();
        int tipo = rs.getInt("id_tipo_nomenclador");
        asociacion.idTipoNomenclador = rs.wasNull()
                ? null : Integer.valueOf(tipo);
        int marca = rs.getInt("marca_rein_liq");
        asociacion.marcaReinLiq = rs.wasNull()
                ? null : Integer.valueOf(marca);
        asociacion.codigoNomenclador = rs.getString("codigo_nomenclador");
        return asociacion;
    }
}
