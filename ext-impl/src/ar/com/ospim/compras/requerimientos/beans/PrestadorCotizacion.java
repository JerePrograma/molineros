package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

import java.util.Locale;

public class PrestadorCotizacion {

    private int idPrestador;
    private String descripcion;
    private String cuit;
    private String email;
    private int idTipoPrestador;
    private String tipoPrestador;
    private String estadoEnvio;
    private boolean presupuestoCargado;

    public int getIdPrestador() {
        return presupuestoCargado
                ? 0
                : idPrestador;
    }

    public int getIdPrestadorPersistido() {
        return idPrestador;
    }

    public void setIdPrestador(int idPrestador) {
        this.idPrestador = idPrestador;
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

    public String getRazonSocialVisible() {
        return getDescripcionVisible();
    }

    public String getEtiquetaVisible() {
        if (WebKeysCompras.isEmpty(descripcion)) {
            return getCuitVisible();
        }

        if (WebKeysCompras.isEmpty(cuit)) {
            return getDescripcionVisible();
        }

        return getDescripcionVisible() + " - " + getCuitVisible();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion =
                WebKeysCompras.trimToNull(descripcion);
    }

    public String getCuit() {
        return cuit;
    }

    public String getCuitVisible() {
        return cuit != null ? cuit : "";
    }

    public void setCuit(String cuit) {
        this.cuit =
                WebKeysCompras.trimToNull(cuit);
    }

    public String getEmail() {
        return email;
    }

    public String getEmailVisible() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email =
                WebKeysCompras.trimToNull(email);
    }

    public int getIdTipoPrestador() {
        return idTipoPrestador;
    }

    public void setIdTipoPrestador(
            int idTipoPrestador) {

        this.idTipoPrestador =
                idTipoPrestador;
    }

    public String getTipoPrestador() {
        return tipoPrestador;
    }

    public String getTipoPrestadorVisible() {
        return tipoPrestador != null
                ? tipoPrestador
                : "";
    }

    public void setTipoPrestador(
            String tipoPrestador) {

        this.tipoPrestador =
                WebKeysCompras.trimToNull(
                        tipoPrestador
                );
    }

    public String getEstadoEnvio() {
        return estadoEnvio;
    }

    public String getEstadoEnvioVisible() {
        if (presupuestoCargado) {
            return "COTIZADO";
        }

        return estadoEnvio != null
                ? estadoEnvio
                : "";
    }

    public void setEstadoEnvio(
            String estadoEnvio) {

        this.estadoEnvio =
                WebKeysCompras.trimToNull(
                        estadoEnvio
                );
    }

    public boolean isPresupuestoCargado() {
        return presupuestoCargado;
    }

    public void setPresupuestoCargado(
            boolean presupuestoCargado) {

        this.presupuestoCargado =
                presupuestoCargado;
    }
}
