package ar.com.ospim.compras.requerimientos.beans;

import java.util.Date;

/** Seguimiento manual de una OC; no modifica los sistemas de contabilidad. */
public class RequerimientoCompraOrdenSeguimiento {

    private int idOrdenCompra;
    private String calificacionProveedor;
    private String observacionesProveedor;
    private String facturaIngresada;
    private String numeroFactura;
    private Date fechaFactura;
    private String estadoLiquidacion;
    private boolean liquidacionAuditada;
    private String estadoPago;
    private Integer idLiquidacion;
    private Integer idOrdenPago;
    private int versionEdicion;
    private Date altaFecha;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;

    public int getIdOrdenCompra() { return idOrdenCompra; }
    public void setIdOrdenCompra(int idOrdenCompra) { this.idOrdenCompra = idOrdenCompra; }
    public String getCalificacionProveedor() { return calificacionProveedor; }
    public void setCalificacionProveedor(String calificacionProveedor) { this.calificacionProveedor = calificacionProveedor; }
    public String getObservacionesProveedor() { return observacionesProveedor; }
    public void setObservacionesProveedor(String observacionesProveedor) { this.observacionesProveedor = observacionesProveedor; }
    public String getFacturaIngresada() { return facturaIngresada; }
    public void setFacturaIngresada(String facturaIngresada) { this.facturaIngresada = facturaIngresada; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public Date getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(Date fechaFactura) { this.fechaFactura = fechaFactura; }
    public String getEstadoLiquidacion() { return estadoLiquidacion; }
    public void setEstadoLiquidacion(String estadoLiquidacion) { this.estadoLiquidacion = estadoLiquidacion; }
    public boolean isLiquidacionAuditada() { return liquidacionAuditada; }
    public void setLiquidacionAuditada(boolean liquidacionAuditada) { this.liquidacionAuditada = liquidacionAuditada; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public Integer getIdLiquidacion() { return idLiquidacion; }
    public void setIdLiquidacion(Integer idLiquidacion) { this.idLiquidacion = idLiquidacion; }
    public Integer getIdOrdenPago() { return idOrdenPago; }
    public void setIdOrdenPago(Integer idOrdenPago) { this.idOrdenPago = idOrdenPago; }
    public int getVersionEdicion() { return versionEdicion; }
    public void setVersionEdicion(int versionEdicion) { this.versionEdicion = versionEdicion; }
    public Date getAltaFecha() { return altaFecha; }
    public void setAltaFecha(Date altaFecha) { this.altaFecha = altaFecha; }
    public String getAltaUsr() { return altaUsr; }
    public void setAltaUsr(String altaUsr) { this.altaUsr = altaUsr; }
    public Date getModiFecha() { return modiFecha; }
    public void setModiFecha(Date modiFecha) { this.modiFecha = modiFecha; }
    public String getModiUsr() { return modiUsr; }
    public void setModiUsr(String modiUsr) { this.modiUsr = modiUsr; }
}
