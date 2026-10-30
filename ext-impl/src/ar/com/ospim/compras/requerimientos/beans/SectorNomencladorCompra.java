package ar.com.ospim.compras.requerimientos.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Criterios concretos de una asociación de nomencladores de Compras. */
public class SectorNomencladorCompra {

    private Integer idSector;
    private Integer idTipoPrestacion;
    private Integer idTipoNomenclador;
    private Integer idTipoNomencladorCatalogo;
    private Integer marcaReinLiq;
    private String codigoNomenclador;

    public Integer getIdSector() {
        return idSector;
    }

    public Integer getIdTipoPrestacion() {
        return idTipoPrestacion;
    }

    public Integer getIdTipoNomenclador() {
        return idTipoNomenclador;
    }

    public Integer getIdTipoNomencladorCatalogo() {
        return idTipoNomencladorCatalogo;
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
        asociacion.idSector = Integer.valueOf(rs.getInt("id_sector"));
        int rubro = rs.getInt("id_tipo_prestacion");
        asociacion.idTipoPrestacion = rs.wasNull()
                ? null : Integer.valueOf(rubro);
        int tipo = rs.getInt("id_tipo_nomenclador");
        asociacion.idTipoNomenclador = rs.wasNull()
                ? null : Integer.valueOf(tipo);
        int tipoCatalogo = rs.getInt("tipo_nomenclador_catalogo");
        asociacion.idTipoNomencladorCatalogo = rs.wasNull()
                ? null : Integer.valueOf(tipoCatalogo);
        int marca = rs.getInt("marca_rein_liq");
        asociacion.marcaReinLiq = rs.wasNull()
                ? null : Integer.valueOf(marca);
        asociacion.codigoNomenclador = rs.getString("codigo_nomenclador");
        return asociacion;
    }
}
