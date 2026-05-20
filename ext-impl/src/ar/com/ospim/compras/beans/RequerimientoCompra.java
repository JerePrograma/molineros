package ar.com.ospim.compras.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.util.DateUtils;

public class RequerimientoCompra {

    private int idRequerimientoCompra;
    private int numero;
    private Integer sectorId;
    private String sectorDescripcion;
    private String solicitanteUsr;
    private String entidad;
    private int prioridad;
    private int estado;
    private Date fechaAlta;
    private String altaUsr;
    private Date fechaModi;
    private String modiUsr;
    private Date bajaFecha;
    private String bajaUsr;
    private Date fechaNecesidad;
    private String motivo;
    private String observaciones;
    private BigDecimal importeEstimadoTotal;
    private Integer idOrdenCompra;

    private List<RequerimientoCompraItem> items;
    private List<RequerimientoCompraHistorial> historial;
    private List<RequerimientoCompraAdjunto> adjuntos;

    public RequerimientoCompra() {
        this.estado = WebKeysCompras.ESTADO_BORRADOR;
        this.prioridad = WebKeysCompras.PRIORIDAD_MEDIA;
        this.importeEstimadoTotal = BigDecimal.ZERO;
        this.items = new ArrayList<RequerimientoCompraItem>();
        this.historial = new ArrayList<RequerimientoCompraHistorial>();
        this.adjuntos = new ArrayList<RequerimientoCompraAdjunto>();
    }

    public RequerimientoCompra(int idRequerimientoCompra) {
        this();
        this.idRequerimientoCompra = idRequerimientoCompra;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimientoCompra;
    }

    public String getIdRequerimientoCompraString() {
        return String.valueOf(idRequerimientoCompra);
    }

    public void setIdRequerimientoCompra(int idRequerimientoCompra) {
        this.idRequerimientoCompra = idRequerimientoCompra;
    }

    public int getNumero() {
        return numero;
    }

    public String getNumeroString() {
        return numero > 0 ? String.valueOf(numero) : "";
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Integer getSectorId() {
        return sectorId;
    }

    public void setSectorId(Integer sectorId) {
        this.sectorId = sectorId;
    }

    public String getSectorDescripcion() {
        return sectorDescripcion;
    }

    public void setSectorDescripcion(String sectorDescripcion) {
        this.sectorDescripcion = sectorDescripcion;
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

    public int getPrioridad() {
        return prioridad;
    }

    public String getPrioridadDescripcion() {
        return WebKeysCompras.getPrioridadDescripcion(prioridad);
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getEstado() {
        return estado;
    }

    public String getEstadoDescripcion() {
        return WebKeysCompras.getEstadoDescripcion(estado);
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public String getFechaAltaAsString() {
        return fechaAlta != null ? DateUtils.format(fechaAlta, DateUtils.SHORT) : "";
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getAltaUsr() {
        return altaUsr;
    }

    public void setAltaUsr(String altaUsr) {
        this.altaUsr = altaUsr;
    }

    public Date getFechaModi() {
        return fechaModi;
    }

    public void setFechaModi(Date fechaModi) {
        this.fechaModi = fechaModi;
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

    public String getBajaFechaAsString() {
        return bajaFecha != null ? DateUtils.format(bajaFecha, DateUtils.SHORT) : "";
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

    public Date getFechaNecesidad() {
        return fechaNecesidad;
    }

    public String getFechaNecesidadAsString() {
        return fechaNecesidad != null ? DateUtils.format(fechaNecesidad, DateUtils.SHORT) : "";
    }

    public void setFechaNecesidad(Date fechaNecesidad) {
        this.fechaNecesidad = fechaNecesidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getImporteEstimadoTotal() {
        return importeEstimadoTotal;
    }

    public String getImporteEstimadoTotalString() {
        return importeEstimadoTotal != null ? importeEstimadoTotal.toString() : "0";
    }

    public void setImporteEstimadoTotal(BigDecimal importeEstimadoTotal) {
        this.importeEstimadoTotal = importeEstimadoTotal;
    }

    public Integer getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public String getIdOrdenCompraString() {
        return idOrdenCompra != null && idOrdenCompra.intValue() > 0 ? String.valueOf(idOrdenCompra) : "";
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public List<RequerimientoCompraItem> getItems() {
        return items;
    }

    public void setItems(List<RequerimientoCompraItem> items) {
        this.items = items;
    }

    public List<RequerimientoCompraHistorial> getHistorial() {
        return historial;
    }

    public void setHistorial(List<RequerimientoCompraHistorial> historial) {
        this.historial = historial;
    }

    public List<RequerimientoCompraAdjunto> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<RequerimientoCompraAdjunto> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public boolean isEditable() {
        return WebKeysCompras.esEditable(estado);
    }

    public boolean isAnulado() {
        return estado == WebKeysCompras.ESTADO_ANULADO || bajaFecha != null;
    }
}
