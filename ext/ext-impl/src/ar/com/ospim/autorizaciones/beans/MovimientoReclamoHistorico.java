package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.Timestamp;

import ar.com.ospim.util.DateUtils;

public class MovimientoReclamoHistorico implements Serializable {

    private static final long serialVersionUID = 1L;
    private int idReclamo;
    private Integer estadoId;
    private String estado;
    private Timestamp altaFecha;
    private String altaUsuario;
    private Timestamp bajaFecha;
    private String observacion;
    
    public int getIdReclamo() {
        return idReclamo;
    }

    public void setIdReclamo(int idReclamo) {
        this.idReclamo = idReclamo;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }
    
    public String getEstado() {
        return estado != null ? estado : "";
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getAltaFecha() {
        return altaFecha;
    }

    public void setAltaFecha(Timestamp altaFecha) {
        this.altaFecha = altaFecha;
    }
    
    public String getAltaFechaAsString() {
        return (altaFecha != null)
            ? DateUtils.format(altaFecha, DateUtils.SHORT)
            : "";
    }

    public String getAltaUsuario() {
        return altaUsuario;
    }

    public void setAltaUsuario(String altaUsuario) {
        this.altaUsuario = altaUsuario;
    }

    public Timestamp getBajaFecha() {
        return bajaFecha;
    }

    public void setBajaFecha(Timestamp bajaFecha) {
        this.bajaFecha = bajaFecha;
    }

    public String getBajaFechaAsString() {
        return (bajaFecha != null)
            ? DateUtils.format(bajaFecha, DateUtils.SHORT)
            : "";
    } 
    
    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
