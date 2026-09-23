package ar.com.ospim.compras.requerimientos.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequerimientoCompraComparativa {

    private int idComparativa;
    private int idRequerimiento;
    private int idPrestador;
    private String prestador;
    private Date fechaPresupuesto;
    private Integer formaPago = Integer.valueOf(30);
    private String plazoEntrega;
    private Integer validezPresupuesto;
    private String envio;
    private BigDecimal iva;
    private BigDecimal iibb;
    private boolean incompleto;
    public boolean getIncompleto() { return incompleto; }
    public void setIncompleto(boolean incompleto) { this.incompleto = incompleto; }

    private BigDecimal neto;
    private BigDecimal total;
    private List<RequerimientoCompraComparativaDetalle> detalles = new ArrayList<RequerimientoCompraComparativaDetalle>();

    public int getIdComparativa() { return idComparativa; }
    public void setIdComparativa(int idComparativa) { this.idComparativa = idComparativa; }

    public int getIdRequerimiento() { return idRequerimiento; }
    public void setIdRequerimiento(int idRequerimiento) { this.idRequerimiento = idRequerimiento; }

    public int getIdPrestador() { return idPrestador; }
    public void setIdPrestador(int idPrestador) { this.idPrestador = idPrestador; }

    public String getPrestador() { return prestador; }
    public void setPrestador(String prestador) { this.prestador = prestador; }

    public Date getFechaPresupuesto() { return fechaPresupuesto; }
    public void setFechaPresupuesto(Date fechaPresupuesto) { this.fechaPresupuesto = fechaPresupuesto; }

    public Integer getFormaPago() { return formaPago; }
    public void setFormaPago(Integer formaPago) { this.formaPago = formaPago; }

    public String getPlazoEntrega() { return plazoEntrega; }
    public void setPlazoEntrega(String plazoEntrega) { this.plazoEntrega = plazoEntrega; }

    public Integer getValidezPresupuesto() { return validezPresupuesto; }
    public void setValidezPresupuesto(Integer validezPresupuesto) { this.validezPresupuesto = validezPresupuesto; }

    public String getEnvio() { return envio; }
    public void setEnvio(String envio) { this.envio = envio; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public BigDecimal getIibb() { return iibb; }
    public void setIibb(BigDecimal iibb) { this.iibb = iibb; }

    public BigDecimal getNeto() { return neto; }
    public void setNeto(BigDecimal neto) { this.neto = neto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<RequerimientoCompraComparativaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<RequerimientoCompraComparativaDetalle> detalles) { this.detalles = detalles; }
}
