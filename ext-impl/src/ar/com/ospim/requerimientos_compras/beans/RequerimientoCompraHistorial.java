package ar.com.ospim.requerimientos_compras.beans;

import java.util.Date;

import ar.com.ospim.requerimientos_compras.WebKeysRequerimientosCompras;
import ar.com.ospim.util.DateUtils;

public class RequerimientoCompraHistorial {

    private int idHistorial;
    private int idRequerimientoCompra;
    private Integer estadoAnterior;
    private int estadoNuevo;
    private String usuario;
    private Date fecha;
    private String comentario;

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimientoCompra;
    }

    public void setIdRequerimientoCompra(int idRequerimientoCompra) {
        this.idRequerimientoCompra = idRequerimientoCompra;
    }

    public Integer getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoAnteriorDescripcion() {
        return estadoAnterior != null ? WebKeysRequerimientosCompras.getEstadoDescripcion(estadoAnterior.intValue()) : "";
    }

    public void setEstadoAnterior(Integer estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public int getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getEstadoNuevoDescripcion() {
        return WebKeysRequerimientosCompras.getEstadoDescripcion(estadoNuevo);
    }

    public void setEstadoNuevo(int estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }


    public Date getFecha() {
        return fecha;
    }

    public String getFechaAsString() {
        return fecha != null ? DateUtils.format(fecha, DateUtils.SHORT) : "";
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
