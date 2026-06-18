package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class PrestadorCotizacion {

    private int idPrestador;
    private String descripcion;
    private String cuit;
    private String email;
    private int idTipoPrestador;
    private String tipoPrestador;

    public int getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(int idPrestador) {
        this.idPrestador = idPrestador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcionVisible() {
        return descripcion != null ? descripcion : "";
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
}
