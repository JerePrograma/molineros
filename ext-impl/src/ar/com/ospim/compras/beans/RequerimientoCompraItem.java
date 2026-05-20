package ar.com.ospim.compras.beans;

import java.math.BigDecimal;
import java.util.Date;

public class RequerimientoCompraItem {

    private int idItem;
    private int idRequerimientoCompra;
    private String descripcion;
    private BigDecimal cantidad;
    private String unidadMedida;
    private BigDecimal importeEstimado;
    private String observaciones;
    private int estado;
    private Date bajaFecha;
    private String bajaUsr;

    public RequerimientoCompraItem() {
        this.cantidad = BigDecimal.ONE;
        this.importeEstimado = BigDecimal.ZERO;
        this.estado = 1;
    }

    public RequerimientoCompraItem(int idItem) {
        this();
        this.idItem = idItem;
    }

    public int getIdItem() {
        return idItem;
    }

    public String getIdItemString() {
        return String.valueOf(idItem);
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimientoCompra;
    }

    public void setIdRequerimientoCompra(int idRequerimientoCompra) {
        this.idRequerimientoCompra = idRequerimientoCompra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getImporteEstimado() {
        return importeEstimado;
    }

    public String getImporteEstimadoString() {
        return importeEstimado != null ? importeEstimado.toString() : "0";
    }

    public void setImporteEstimado(BigDecimal importeEstimado) {
        this.importeEstimado = importeEstimado;
    }

    public BigDecimal getSubtotalEstimado() {
        if (cantidad == null || importeEstimado == null) {
            return BigDecimal.ZERO;
        }
        return cantidad.multiply(importeEstimado);
    }

    public String getSubtotalEstimadoString() {
        return getSubtotalEstimado().toString();
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
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
}
