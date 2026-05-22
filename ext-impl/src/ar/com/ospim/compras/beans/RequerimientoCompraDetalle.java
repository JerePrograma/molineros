package ar.com.ospim.compras.beans;

import java.math.BigDecimal;
import java.util.Date;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.util.DateUtils;

public class RequerimientoCompraDetalle {

    private int idRequerimientoDetalle;
    private int idRequerimientoCompra;

    private int renglon;
    private String tipoArticulo;
    private String articulo;

    private BigDecimal cantidad;
    private String unidadMedida;

    private BigDecimal precioUnitarioEstimado;
    private BigDecimal precioTotalEstimado;

    private String observaciones;

    private Date altaFecha;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;
    private Date bajaFecha;
    private String bajaUsr;

    public RequerimientoCompraDetalle() {
        this.cantidad = BigDecimal.ONE;
        this.precioUnitarioEstimado = BigDecimal.ZERO;
        this.precioTotalEstimado = null;
    }

    public RequerimientoCompraDetalle(int idRequerimientoDetalle) {
        this();
        this.idRequerimientoDetalle = idRequerimientoDetalle;
    }

    public int getIdRequerimientoDetalle() {
        return idRequerimientoDetalle;
    }

    public String getIdRequerimientoDetalleString() {
        return idRequerimientoDetalle > 0 ? String.valueOf(idRequerimientoDetalle) : "";
    }

    public void setIdRequerimientoDetalle(int idRequerimientoDetalle) {
        this.idRequerimientoDetalle = idRequerimientoDetalle;
    }

    public int getIdItem() {
        return idRequerimientoDetalle;
    }

    public String getIdItemString() {
        return getIdRequerimientoDetalleString();
    }

    public void setIdItem(int idItem) {
        this.idRequerimientoDetalle = idItem;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimientoCompra;
    }

    public void setIdRequerimientoCompra(int idRequerimientoCompra) {
        this.idRequerimientoCompra = idRequerimientoCompra;
    }

    public int getRenglon() {
        return renglon;
    }

    public String getRenglonString() {
        return renglon > 0 ? String.valueOf(renglon) : "";
    }

    public void setRenglon(int renglon) {
        this.renglon = renglon;
    }

    public String getTipoArticulo() {
        return tipoArticulo;
    }

    public String getTipoArticuloVisible() {
        return tipoArticulo != null ? tipoArticulo : "";
    }

    public void setTipoArticulo(String tipoArticulo) {
        this.tipoArticulo = WebKeysCompras.trimToNull(tipoArticulo);
    }

    public String getArticulo() {
        return articulo;
    }

    public String getArticuloVisible() {
        return articulo != null ? articulo : "";
    }

    public void setArticulo(String articulo) {
        this.articulo = WebKeysCompras.trimToNull(articulo);
    }

    public String getDescripcion() {
        return articulo;
    }

    public void setDescripcion(String descripcion) {
        this.articulo = WebKeysCompras.trimToNull(descripcion);
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public String getCantidadString() {
        return cantidad != null ? cantidad.toString() : "0";
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public String getUnidadMedidaVisible() {
        return unidadMedida != null ? unidadMedida : "";
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = WebKeysCompras.trimToNull(unidadMedida);
    }

    public BigDecimal getPrecioUnitarioEstimado() {
        return precioUnitarioEstimado;
    }

    public String getPrecioUnitarioEstimadoString() {
        return precioUnitarioEstimado != null ? precioUnitarioEstimado.toString() : "0";
    }

    public void setPrecioUnitarioEstimado(BigDecimal precioUnitarioEstimado) {
        this.precioUnitarioEstimado = precioUnitarioEstimado;
    }

    public BigDecimal getPrecioTotalEstimado() {
        if (precioTotalEstimado != null) {
            return precioTotalEstimado;
        }

        return calcularPrecioTotalEstimado();
    }

    public String getPrecioTotalEstimadoString() {
        BigDecimal total = getPrecioTotalEstimado();
        return total != null ? total.toString() : "0";
    }

    public void setPrecioTotalEstimado(BigDecimal precioTotalEstimado) {
        this.precioTotalEstimado = precioTotalEstimado;
    }

    public BigDecimal getImporteEstimado() {
        return precioUnitarioEstimado;
    }

    public String getImporteEstimadoString() {
        return getPrecioUnitarioEstimadoString();
    }

    public void setImporteEstimado(BigDecimal importeEstimado) {
        this.precioUnitarioEstimado = importeEstimado;
    }

    public BigDecimal getSubtotalEstimado() {
        return getPrecioTotalEstimado();
    }

    public String getSubtotalEstimadoString() {
        return getPrecioTotalEstimadoString();
    }

    public BigDecimal calcularPrecioTotalEstimado() {
        if (cantidad == null || precioUnitarioEstimado == null) {
            return BigDecimal.ZERO;
        }

        return cantidad.multiply(precioUnitarioEstimado);
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getObservacionesVisible() {
        return observaciones != null ? observaciones : "";
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = WebKeysCompras.trimToNull(observaciones);
    }

    public Date getAltaFecha() {
        return altaFecha;
    }

    public String getAltaFechaAsString() {
        return altaFecha != null ? DateUtils.format(altaFecha, DateUtils.SHORT) : "";
    }

    public void setAltaFecha(Date altaFecha) {
        this.altaFecha = altaFecha;
    }

    public String getAltaUsr() {
        return altaUsr;
    }

    public void setAltaUsr(String altaUsr) {
        this.altaUsr = WebKeysCompras.trimToNull(altaUsr);
    }

    public Date getModiFecha() {
        return modiFecha;
    }

    public String getModiFechaAsString() {
        return modiFecha != null ? DateUtils.format(modiFecha, DateUtils.SHORT) : "";
    }

    public void setModiFecha(Date modiFecha) {
        this.modiFecha = modiFecha;
    }

    public String getModiUsr() {
        return modiUsr;
    }

    public void setModiUsr(String modiUsr) {
        this.modiUsr = WebKeysCompras.trimToNull(modiUsr);
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
        this.bajaUsr = WebKeysCompras.trimToNull(bajaUsr);
    }

    public boolean isBorrado() {
        return bajaFecha != null;
    }

    public boolean isActivo() {
        return bajaFecha == null;
    }
}