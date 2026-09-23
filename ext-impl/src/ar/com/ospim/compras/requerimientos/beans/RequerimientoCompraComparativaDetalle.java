package ar.com.ospim.compras.requerimientos.beans;

import java.math.BigDecimal;

public class RequerimientoCompraComparativaDetalle {

    private int idDetalle;
    private int idPrestacion;
    private String codigo;
    private String prestacion;
    private Integer cantidad;
    private BigDecimal importeUnitario;
    private BigDecimal subtotal;

    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdPrestacion() { return idPrestacion; }
    public void setIdPrestacion(int idPrestacion) { this.idPrestacion = idPrestacion; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getPrestacion() { return prestacion; }
    public void setPrestacion(String prestacion) { this.prestacion = prestacion; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getImporteUnitario() { return importeUnitario; }
    public void setImporteUnitario(BigDecimal importeUnitario) { this.importeUnitario = importeUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
