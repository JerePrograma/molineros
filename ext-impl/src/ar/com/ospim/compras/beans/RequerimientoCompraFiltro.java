package ar.com.ospim.compras.beans;

import java.util.Date;

public class RequerimientoCompraFiltro {

    private Integer numero;
    private Date fechaDesde;
    private Date fechaHasta;

    private Integer idSector;
    private Integer idEstado;

    private String solicitanteUsr;
    private String texto;

    private String afiliadoCuilTitular;
    private Integer afiliadoInte;

    private String tipoArticulo;

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Integer getIdSector() {
        return idSector;
    }

    public Integer getSectorId() {
        return idSector;
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public void setSectorId(Integer sectorId) {
        this.idSector = sectorId;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public Integer getEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public void setEstado(Integer estado) {
        this.idEstado = estado;
    }

    public String getSolicitanteUsr() {
        return solicitanteUsr;
    }

    public void setSolicitanteUsr(String solicitanteUsr) {
        this.solicitanteUsr = solicitanteUsr;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getAfiliadoCuilTitular() {
        return afiliadoCuilTitular;
    }

    public void setAfiliadoCuilTitular(String afiliadoCuilTitular) {
        this.afiliadoCuilTitular = afiliadoCuilTitular;
    }

    public Integer getAfiliadoInte() {
        return afiliadoInte;
    }

    public void setAfiliadoInte(Integer afiliadoInte) {
        this.afiliadoInte = afiliadoInte;
    }

    public String getTipoArticulo() {
        return tipoArticulo;
    }

    public void setTipoArticulo(String tipoArticulo) {
        this.tipoArticulo = tipoArticulo;
    }
}
