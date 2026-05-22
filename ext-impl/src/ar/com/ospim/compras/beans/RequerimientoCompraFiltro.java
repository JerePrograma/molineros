package ar.com.ospim.compras.beans;

import java.util.Date;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.util.DateUtils;

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

    public String getNumeroString() {
        return numero != null && numero.intValue() > 0 ? String.valueOf(numero) : "";
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public String getFechaDesdeAsString() {
        return fechaDesde != null ? DateUtils.format(fechaDesde, DateUtils.SHORT) : "";
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public String getFechaHastaAsString() {
        return fechaHasta != null ? DateUtils.format(fechaHasta, DateUtils.SHORT) : "";
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

    public String getIdSectorString() {
        return idSector != null && idSector.intValue() > 0 ? String.valueOf(idSector) : "";
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

    public String getIdEstadoString() {
        return idEstado != null && idEstado.intValue() > 0 ? String.valueOf(idEstado) : "";
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
        this.solicitanteUsr = WebKeysCompras.trimToNull(solicitanteUsr);
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = WebKeysCompras.trimToNull(texto);
    }

    public String getAfiliadoCuilTitular() {
        return afiliadoCuilTitular;
    }

    public void setAfiliadoCuilTitular(String afiliadoCuilTitular) {
        this.afiliadoCuilTitular = WebKeysCompras.trimToNull(afiliadoCuilTitular);
    }

    public Integer getAfiliadoInte() {
        return afiliadoInte;
    }

    public String getAfiliadoInteString() {
        return afiliadoInte != null && afiliadoInte.intValue() >= 0 ? String.valueOf(afiliadoInte) : "";
    }

    public void setAfiliadoInte(Integer afiliadoInte) {
        this.afiliadoInte = afiliadoInte;
    }

    public String getTipoArticulo() {
        return tipoArticulo;
    }

    public void setTipoArticulo(String tipoArticulo) {
        this.tipoArticulo = WebKeysCompras.trimToNull(tipoArticulo);
    }

    public boolean tieneFiltros() {
        return numero != null
                || fechaDesde != null
                || fechaHasta != null
                || idSector != null
                || idEstado != null
                || !WebKeysCompras.isEmpty(solicitanteUsr)
                || !WebKeysCompras.isEmpty(texto)
                || !WebKeysCompras.isEmpty(afiliadoCuilTitular)
                || afiliadoInte != null
                || !WebKeysCompras.isEmpty(tipoArticulo);
    }
}