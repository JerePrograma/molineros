package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;

public class BusquedaCartillaConvenioFiltro implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idPlan;
    private Integer idPrestador;
    private String cuitPrestador;
    private String prestadorDescripcion;
    private Integer idProvincia;
    private Integer idLocalidad;
    private Integer idEspecialidad;
    private boolean incluyeBajas;
    private int pagina = 1;

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public Integer getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(Integer idPrestador) {
        this.idPrestador = idPrestador;
    }

    public String getCuitPrestador() {
        return cuitPrestador;
    }

    public void setCuitPrestador(String cuitPrestador) {
        this.cuitPrestador = cuitPrestador;
    }

    public String getPrestadorDescripcion() {
        return prestadorDescripcion;
    }

    public void setPrestadorDescripcion(String prestadorDescripcion) {
        this.prestadorDescripcion = prestadorDescripcion;
    }

    public Integer getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(Integer idProvincia) {
        this.idProvincia = idProvincia;
    }

    public Integer getIdLocalidad() {
        return idLocalidad;
    }

    public void setIdLocalidad(Integer idLocalidad) {
        this.idLocalidad = idLocalidad;
    }

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Integer idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public boolean isIncluyeBajas() {
        return incluyeBajas;
    }

    public void setIncluyeBajas(boolean incluyeBajas) {
        this.incluyeBajas = incluyeBajas;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public String toString() {
        return "BusquedaCartillaConvenioFiltro{" +
                "idPlan=" + idPlan +
                ", idPrestador=" + idPrestador +
                ", cuitPrestador='" + cuitPrestador + '\'' +
                ", prestadorDescripcion='" + prestadorDescripcion + '\'' +
                ", idProvincia=" + idProvincia +
                ", idLocalidad=" + idLocalidad +
                ", idEspecialidad=" + idEspecialidad +
                ", incluyeBajas=" + incluyeBajas +
                ", pagina=" + pagina +
                '}';
    }
}