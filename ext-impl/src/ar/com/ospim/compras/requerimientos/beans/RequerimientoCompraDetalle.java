package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

import java.math.BigDecimal;

public class RequerimientoCompraDetalle {

    private Integer id;
    private Integer idRequerimiento;
    private Integer idArticulo;
    private String articulo;
    private Integer cantidad;
    private BigDecimal precioUnitarioEstimado;
    private BigDecimal precioTotalEstimado;
    private String observaciones;

    public RequerimientoCompraDetalle() {
        this.cantidad = Integer.valueOf(1);
    }

    public RequerimientoCompraDetalle(Integer id) {
        this();
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public int getIdInt() {
        return id != null ? id.intValue() : 0;
    }

    public String getIdString() {
        return id != null && id.intValue() > 0 ? String.valueOf(id) : "";
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdRequerimiento() {
        return idRequerimiento;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimiento != null ? idRequerimiento.intValue() : 0;
    }

    public void setIdRequerimiento(Integer idRequerimiento) {
        this.idRequerimiento = idRequerimiento;
    }

    public void setIdRequerimientoCompra(int idRequerimientoCompra) {
        this.idRequerimiento = idRequerimientoCompra > 0
                ? Integer.valueOf(idRequerimientoCompra)
                : null;
    }

    public Integer getIdArticulo() {
        return idArticulo;
    }

    public int getIdArticuloInt() {
        return idArticulo != null ? idArticulo.intValue() : 0;
    }

    public String getIdArticuloString() {
        return idArticulo != null && idArticulo.intValue() > 0
                ? String.valueOf(idArticulo)
                : "";
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
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

    public Integer getCantidad() {
        return cantidad;
    }

    public int getCantidadInt() {
        return cantidad != null ? cantidad.intValue() : 0;
    }

    public String getCantidadString() {
        return cantidad != null ? cantidad.toString() : "0";
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioEstimado() {
        return precioUnitarioEstimado;
    }

    public String getPrecioUnitarioEstimadoString() {
        return precioUnitarioEstimado != null ? precioUnitarioEstimado.toString() : "";
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

    public BigDecimal getPrecioTotalEstimadoInformado() {
        return precioTotalEstimado;
    }

    public String getPrecioTotalEstimadoString() {
        BigDecimal total = getPrecioTotalEstimado();
        return total != null ? total.toString() : "";
    }

    public void setPrecioTotalEstimado(BigDecimal precioTotalEstimado) {
        this.precioTotalEstimado = precioTotalEstimado;
    }

    public BigDecimal calcularPrecioTotalEstimado() {
        if (cantidad == null || precioUnitarioEstimado == null) {
            return null;
        }

        return precioUnitarioEstimado.multiply(new BigDecimal(cantidad.intValue()));
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
}