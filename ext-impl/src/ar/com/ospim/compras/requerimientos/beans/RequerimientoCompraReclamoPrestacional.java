package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

import java.util.Date;

public class RequerimientoCompraReclamoPrestacional {

    private int idRequerimientoCompra;
    private Integer idReclamoPrestacional;
    private String estado;
    private String tokenReserva;
    private Date reservaFecha;
    private String ultimoError;
    private Date altaFecha;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;
    private Date bajaFecha;
    private String bajaUsr;

    public int getIdRequerimientoCompra() {
        return idRequerimientoCompra;
    }

    public void setIdRequerimientoCompra(int idRequerimientoCompra) {
        this.idRequerimientoCompra = idRequerimientoCompra;
    }

    public Integer getIdReclamoPrestacional() {
        return idReclamoPrestacional;
    }

    public int getIdReclamoPrestacionalInt() {
        return idReclamoPrestacional != null
                ? idReclamoPrestacional.intValue()
                : 0;
    }

    public void setIdReclamoPrestacional(Integer idReclamoPrestacional) {
        this.idReclamoPrestacional = idReclamoPrestacional;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTokenReserva() {
        return tokenReserva;
    }

    public void setTokenReserva(String tokenReserva) {
        this.tokenReserva = tokenReserva;
    }

    public Date getReservaFecha() {
        return reservaFecha;
    }

    public void setReservaFecha(Date reservaFecha) {
        this.reservaFecha = reservaFecha;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public void setUltimoError(String ultimoError) {
        this.ultimoError = ultimoError;
    }

    public Date getAltaFecha() {
        return altaFecha;
    }

    public void setAltaFecha(Date altaFecha) {
        this.altaFecha = altaFecha;
    }

    public String getAltaUsr() {
        return altaUsr;
    }

    public void setAltaUsr(String altaUsr) {
        this.altaUsr = altaUsr;
    }

    public Date getModiFecha() {
        return modiFecha;
    }

    public void setModiFecha(Date modiFecha) {
        this.modiFecha = modiFecha;
    }

    public String getModiUsr() {
        return modiUsr;
    }

    public void setModiUsr(String modiUsr) {
        this.modiUsr = modiUsr;
    }

    public Date getBajaFecha() {
        return bajaFecha;
    }

    public void setBajaFecha(Date bajaFecha) {
        this.bajaFecha = bajaFecha;
    }

    public String getBajaUsr() {
        return bajaUsr;
    }

    public void setBajaUsr(String bajaUsr) {
        this.bajaUsr = bajaUsr;
    }

    public boolean isBaja() {
        return WebKeysCompras.VINCULO_RECLAMO_BAJA.equals(estado)
                && bajaFecha != null && getIdReclamoPrestacionalInt() > 0;
    }

    public boolean isReservado() {
        return WebKeysCompras.VINCULO_RECLAMO_RESERVADO.equals(estado);
    }

    public boolean isVinculado() {
        return WebKeysCompras.VINCULO_RECLAMO_VINCULADO.equals(estado)
                && getIdReclamoPrestacionalInt() > 0;
    }

    public boolean isError() {
        return WebKeysCompras.VINCULO_RECLAMO_ERROR.equals(estado);
    }
}
