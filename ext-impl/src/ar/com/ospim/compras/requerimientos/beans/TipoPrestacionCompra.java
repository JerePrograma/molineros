package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class TipoPrestacionCompra {

    private Integer id;
    private String descripcion;
    private Integer idSector;
    private String sectorDescripcion;

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
