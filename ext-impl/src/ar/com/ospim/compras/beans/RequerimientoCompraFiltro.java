package ar.com.ospim.compras.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class RequerimientoCompraFiltro {

    private Integer idEstado;
    private Integer idSector;
    private String afiliadoCuilTitular;
    private Integer afiliadoInt;
    private String idTercerizadora;
    private Boolean recupero;
    private String texto;

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

    public String getAfiliadoCuilTitular() {
        return afiliadoCuilTitular;
    }

    public void setAfiliadoCuilTitular(String afiliadoCuilTitular) {
        this.afiliadoCuilTitular = WebKeysCompras.trimToNull(afiliadoCuilTitular);
    }

    public Integer getAfiliadoInt() {
        return afiliadoInt;
    }

    public String getAfiliadoIntString() {
        return afiliadoInt != null && afiliadoInt.intValue() >= 0 ? String.valueOf(afiliadoInt) : "";
    }

    public void setAfiliadoInt(Integer afiliadoInt) {
        this.afiliadoInt = afiliadoInt;
    }

    public String getIdTercerizadora() {
        return idTercerizadora;
    }

    public void setIdTercerizadora(String idTercerizadora) {
        this.idTercerizadora = WebKeysCompras.trimToNull(idTercerizadora);
    }

    public Boolean getRecupero() {
        return recupero;
    }

    public String getRecuperoString() {
        return recupero != null ? recupero.toString() : "";
    }

    public void setRecupero(Boolean recupero) {
        this.recupero = recupero;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = WebKeysCompras.trimToNull(texto);
    }

    public boolean tieneFiltros() {
        return idEstado != null
                || idSector != null
                || !WebKeysCompras.isEmpty(afiliadoCuilTitular)
                || afiliadoInt != null
                || !WebKeysCompras.isEmpty(idTercerizadora)
                || recupero != null
                || !WebKeysCompras.isEmpty(texto);
    }
}
