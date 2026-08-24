package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

import java.util.Locale;

public class TipoPrestadorSector {

    private int idTipoPrestacion;
    private String tipoPrestacionDescripcion;
    private int idTipoPrestador;
    private String descripcion;
    private boolean activo;

    public TipoPrestadorSector() {
    }

    public int getIdTipoPrestacion() {
        return idTipoPrestacion;
    }

    public void setIdTipoPrestacion(int idTipoPrestacion) {
        this.idTipoPrestacion = idTipoPrestacion;
    }

    public String getTipoPrestacionDescripcion() {
        return tipoPrestacionDescripcion;
    }

    public String getTipoPrestacionDescripcionVisible() {
        return tipoPrestacionDescripcion != null
                ? tipoPrestacionDescripcion
                : "";
    }

    public void setTipoPrestacionDescripcion(
            String tipoPrestacionDescripcion) {

        this.tipoPrestacionDescripcion =
                WebKeysCompras.trimToNull(tipoPrestacionDescripcion);
    }

    public String getClaveConfiguracion() {
        if (idTipoPrestacion <= 0 || idTipoPrestador <= 0) {
            return "";
        }

        return String.valueOf(idTipoPrestacion)
                + ":"
                + String.valueOf(idTipoPrestador);
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
        String value = getDescripcion();

        return value != null
                ? value.toUpperCase(Locale.ROOT)
                : "";
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
        return activo ? "Sí" : "No";
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = Boolean.TRUE.equals(activo);
    }
}
