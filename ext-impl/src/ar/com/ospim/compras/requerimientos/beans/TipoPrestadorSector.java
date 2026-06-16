package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class TipoPrestadorSector {

    private int idTipoPrestador;
    private String descripcion;
    private boolean activo;

    public TipoPrestadorSector() {
    }

    public TipoPrestadorSector(int idTipoPrestador,
                               String descripcion,
                               boolean activo) {

        this.idTipoPrestador = idTipoPrestador;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public int getIdTipoPrestador() {
        return idTipoPrestador;
    }

    public String getIdTipoPrestadorString() {
        return idTipoPrestador > 0
                ? String.valueOf(idTipoPrestador)
                : "";
    }

    public void setIdTipoPrestador(int idTipoPrestador) {
        this.idTipoPrestador = idTipoPrestador;
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

    public boolean isActivo() {
        return activo;
    }

    public Boolean getActivo() {
        return Boolean.valueOf(activo);
    }

    public String getActivoDescripcion() {
        return activo ? "SI" : "NO";
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = Boolean.TRUE.equals(activo);
    }
}