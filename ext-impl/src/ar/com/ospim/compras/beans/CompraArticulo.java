package ar.com.ospim.compras.beans;

public class CompraArticulo {

    private Integer id;
    private Integer idSector;
    private String sectorDescripcion;
    private String descripcion;

    public CompraArticulo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdSector() {
        return idSector;
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public String getSectorDescripcion() {
        return sectorDescripcion;
    }

    public void setSectorDescripcion(String sectorDescripcion) {
        this.sectorDescripcion = sectorDescripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}