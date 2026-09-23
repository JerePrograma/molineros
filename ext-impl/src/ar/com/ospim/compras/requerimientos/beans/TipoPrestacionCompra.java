package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class TipoPrestacionCompra {

    private Integer id;
    private String descripcion;
    private Integer idSector;
    private String sectorDescripcion;
    private String rubroPrestador;
    private java.util.List<Integer> nomencladores = new java.util.ArrayList<Integer>();

    public String getRubroPrestador() { return rubroPrestador; }
    public void setRubroPrestador(String value) { rubroPrestador = value; }
    public java.util.List<Integer> getNomencladores() { return nomencladores; }
    public void setNomencladores(java.util.List<Integer> value) { nomencladores = value; }
    public boolean admiteNomenclador(int idTipoNomenclador) {
        return nomencladores.contains(Integer.valueOf(idTipoNomenclador));
    }


    public Integer getId() {
        return id;
    }

    public int getIdInt() {
        return id != null ? id.intValue() : 0;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcionVisible() {
        return descripcion != null ? descripcion : "";
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = WebKeysCompras.trimToNull(descripcion);
    }

    public Integer getIdSector() {
        return idSector;
    }

    public int getIdSectorInt() {
        return idSector != null ? idSector.intValue() : 0;
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public String getSectorDescripcion() {
        return sectorDescripcion;
    }

    public void setSectorDescripcion(String sectorDescripcion) {
        this.sectorDescripcion =
                WebKeysCompras.trimToNull(sectorDescripcion);
    }
}
