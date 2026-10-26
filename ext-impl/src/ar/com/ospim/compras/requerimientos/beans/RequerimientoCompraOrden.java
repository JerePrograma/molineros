package ar.com.ospim.compras.requerimientos.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequerimientoCompraOrden {

    public static final String MONEDA_PESOS = "ARS";

    private int idOrdenCompra;
    private int idRequerimientoCompra;
    private int idReclamoPrestacional;
    private Date fechaEmision;
    private Date fechaOrdenMedica;
    private Date fechaRecepcion;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;
    private int versionEdicion;
    private String proveedor;
    private String proveedorCuit;
    private String proveedorDomicilio;
    private String proveedorContacto;
    private String proveedorEmail;
    private String proveedorTelefono;
    private String condicion;
    private String formaPago;
    private String plazoPago;
    private String entrega;
    private String observaciones;
    private String confeccionadoPor;
    private String telefonoCompras;
    private String solicitante;
    private String plan;
    private BigDecimal iva = BigDecimal.ZERO;
    private BigDecimal iibb = BigDecimal.ZERO;
    private RequerimientoCompra requerimiento;
    private List<RequerimientoCompraDetalle> detalles =
            new ArrayList<RequerimientoCompraDetalle>();

    public int getIdOrdenCompra() { return idOrdenCompra; }
    public void setIdOrdenCompra(int idOrdenCompra) { this.idOrdenCompra = idOrdenCompra; }

    public int getIdRequerimientoCompra() { return idRequerimientoCompra; }
    public void setIdRequerimientoCompra(int idRequerimientoCompra) { this.idRequerimientoCompra = idRequerimientoCompra; }

    public int getIdReclamoPrestacional() { return idReclamoPrestacional; }
    public void setIdReclamoPrestacional(int idReclamoPrestacional) { this.idReclamoPrestacional = idReclamoPrestacional; }

    public Date getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Date fechaEmision) { this.fechaEmision = fechaEmision; }

    public Date getFechaOrdenMedica() { return fechaOrdenMedica; }
    public void setFechaOrdenMedica(Date fechaOrdenMedica) { this.fechaOrdenMedica = fechaOrdenMedica; }

    public Date getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(Date fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public String getAltaUsr() { return altaUsr; }
    public void setAltaUsr(String altaUsr) { this.altaUsr = altaUsr; }

    public Date getModiFecha() { return modiFecha; }
    public void setModiFecha(Date modiFecha) { this.modiFecha = modiFecha; }

    public String getModiUsr() { return modiUsr; }
    public void setModiUsr(String modiUsr) { this.modiUsr = modiUsr; }

    public int getVersionEdicion() { return versionEdicion; }
    public void setVersionEdicion(int versionEdicion) { this.versionEdicion = versionEdicion; }

    public String getMoneda() { return MONEDA_PESOS; }
    public void setMoneda(String moneda) {
        String valor = moneda != null ? moneda.trim() : "";
        if (valor.length() > 0 && !MONEDA_PESOS.equals(valor)) {
            throw new IllegalArgumentException("La Orden de Compra solo admite pesos argentinos (ARS).");
        }
    }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public String getProveedorCuit() { return proveedorCuit; }
    public void setProveedorCuit(String proveedorCuit) { this.proveedorCuit = proveedorCuit; }

    public String getProveedorDomicilio() { return proveedorDomicilio; }
    public void setProveedorDomicilio(String proveedorDomicilio) { this.proveedorDomicilio = proveedorDomicilio; }

    public String getProveedorContacto() { return proveedorContacto; }
    public void setProveedorContacto(String proveedorContacto) { this.proveedorContacto = proveedorContacto; }

    public String getProveedorEmail() { return proveedorEmail; }
    public void setProveedorEmail(String proveedorEmail) { this.proveedorEmail = proveedorEmail; }

    public String getProveedorTelefono() { return proveedorTelefono; }
    public void setProveedorTelefono(String proveedorTelefono) { this.proveedorTelefono = proveedorTelefono; }

    public String getCondicion() { return condicion; }
    public void setCondicion(String condicion) { this.condicion = condicion; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public String getPlazoPago() { return plazoPago; }
    public void setPlazoPago(String plazoPago) { this.plazoPago = plazoPago; }

    public String getEntrega() { return entrega; }
    public void setEntrega(String entrega) { this.entrega = entrega; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getConfeccionadoPor() { return confeccionadoPor; }
    public void setConfeccionadoPor(String confeccionadoPor) { this.confeccionadoPor = confeccionadoPor; }

    public String getTelefonoCompras() { return telefonoCompras; }
    public void setTelefonoCompras(String telefonoCompras) { this.telefonoCompras = telefonoCompras; }

    public String getSolicitante() { return solicitante; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public BigDecimal getIva() { return iva != null ? iva : BigDecimal.ZERO; }
    public void setIva(BigDecimal iva) { this.iva = iva != null ? iva : BigDecimal.ZERO; }

    public BigDecimal getIibb() { return iibb != null ? iibb : BigDecimal.ZERO; }
    public void setIibb(BigDecimal iibb) { this.iibb = iibb != null ? iibb : BigDecimal.ZERO; }

    public RequerimientoCompra getRequerimiento() { return requerimiento; }
    public void setRequerimiento(RequerimientoCompra requerimiento) { this.requerimiento = requerimiento; }

    public List<RequerimientoCompraDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<RequerimientoCompraDetalle> detalles) {
        this.detalles = detalles != null ? detalles
                : new ArrayList<RequerimientoCompraDetalle>();
    }

    public BigDecimal getTotal() {
        if (detalles == null || detalles.isEmpty()) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (RequerimientoCompraDetalle detalle : detalles) {
            if (detalle == null || detalle.getPrecioUnitarioEstimado() == null
                    || detalle.getPrecioTotalEstimado() == null) {
                return null;
            }
            total = total.add(detalle.getPrecioTotalEstimado());
        }
        return total.add(getIva()).add(getIibb());
    }
}
