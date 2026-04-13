package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;

public class BusquedaCartillaConvenioFiltro implements Serializable {

    private static final long serialVersionUID = 1245789632145789632L;

    private Integer idPlan;
    private Integer idPrestador;
    private Integer idProvincia;
    private Integer idLocalidad;
    private Integer idEspecialidad;

    private String prestadorDescripcion;
    private String institucion;

    private boolean incluyeBajas;

    private int pagina;
    private int registrosTotal;
    private final int registrosPorPagina = 50;

    public BusquedaCartillaConvenioFiltro() {
        super();
        this.pagina = 1;
        this.incluyeBajas = false;
    }

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

    public String getPrestadorDescripcion() {
        return prestadorDescripcion;
    }

    public void setPrestadorDescripcion(String prestadorDescripcion) {
        this.prestadorDescripcion = prestadorDescripcion != null ? prestadorDescripcion.trim() : null;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion != null ? institucion.trim() : null;
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
        this.pagina = pagina > 0 ? pagina : 1;
    }

    public int getRegistrosTotal() {
        return registrosTotal;
    }

    public void setRegistrosTotal(int registrosTotal) {
        this.registrosTotal = registrosTotal;
    }

    public int getRegistrosPorPagina() {
        return registrosPorPagina;
    }

    @Override
    public String toString() {
        return "BusquedaCartillaConvenioFiltro [idPlan=" + idPlan
                + ", idPrestador=" + idPrestador
                + ", idProvincia=" + idProvincia
                + ", idLocalidad=" + idLocalidad
                + ", idEspecialidad=" + idEspecialidad
                + ", prestadorDescripcion=" + prestadorDescripcion
                + ", institucion=" + institucion
                + ", incluyeBajas=" + incluyeBajas
                + ", pagina=" + pagina + "]";
    }
}