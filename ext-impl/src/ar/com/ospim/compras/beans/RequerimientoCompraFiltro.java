package ar.com.ospim.compras.beans;

import java.util.Date;

public class RequerimientoCompraFiltro {

    private Integer numero;
    private Date fechaDesde;
    private Date fechaHasta;
    private Integer sectorId;
    private String solicitanteUsr;
    private String entidad;
    private Integer prioridad;
    private Integer estado;
    private Integer idOrdenCompra;
    private String texto;

    private String afiliado;
    private String dni;
    private String detalleRequerimiento;
    private Integer rpNumero;
    private Integer ordenCompraNumero;
    private Boolean recupero;
    private Boolean cotizado;
    private Date fechaPedidoCotizacionDesde;
    private Date fechaPedidoCotizacionHasta;
    private String localidad;
    private String provincia;

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

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
    }

    public String getSolicitanteUsr() {
        return solicitanteUsr;
    }

    public void setSolicitanteUsr(String solicitanteUsr) {
        this.solicitanteUsr = solicitanteUsr;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getAfiliado() {
        return afiliado;
    }

    public void setAfiliado(String afiliado) {
        this.afiliado = afiliado;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDetalleRequerimiento() {
        return detalleRequerimiento;
    }

    public void setDetalleRequerimiento(String detalleRequerimiento) {
        this.detalleRequerimiento = detalleRequerimiento;
    }

    public Integer getRpNumero() {
        return rpNumero;
    }

    public void setRpNumero(Integer rpNumero) {
        this.rpNumero = rpNumero;
    }

    public Integer getOrdenCompraNumero() {
        return ordenCompraNumero;
    }

    public void setOrdenCompraNumero(Integer ordenCompraNumero) {
        this.ordenCompraNumero = ordenCompraNumero;
    }

    public Boolean getRecupero() {
        return recupero;
    }

    public void setRecupero(Boolean recupero) {
        this.recupero = recupero;
    }

    public Boolean getCotizado() {
        return cotizado;
    }

    public void setCotizado(Boolean cotizado) {
        this.cotizado = cotizado;
    }

    public Date getFechaPedidoCotizacionDesde() {
        return fechaPedidoCotizacionDesde;
    }

    public void setFechaPedidoCotizacionDesde(Date fechaPedidoCotizacionDesde) {
        this.fechaPedidoCotizacionDesde = fechaPedidoCotizacionDesde;
    }

    public Date getFechaPedidoCotizacionHasta() {
        return fechaPedidoCotizacionHasta;
    }

    public void setFechaPedidoCotizacionHasta(Date fechaPedidoCotizacionHasta) {
        this.fechaPedidoCotizacionHasta = fechaPedidoCotizacionHasta;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
}
