package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

import java.math.BigDecimal;

/**
 * Detalle adjudicado de un requerimiento de compra.
 *
 * El ID de artículo pertenece al módulo Compras y no debe reutilizarse como
 * ID de prestación ni como ID de medicamento del módulo Autorizaciones.
 */
public class RequerimientoCompraDetalle {

    private Integer id;
    private Integer idRequerimiento;
    private Integer idArticulo;
    private String articulo;
    private Integer cantidad;
    private BigDecimal precioUnitarioEstimado;
    private BigDecimal precioTotalEstimado;
    private Integer idPrestador;
    private String prestadorCuit;
    private String prestadorRazonSocial;
    private String observaciones;

    public RequerimientoCompraDetalle() {
        this.cantidad =
                Integer.valueOf(
                        1
                );
    }

    public RequerimientoCompraDetalle(
            Integer id) {

        this();
        this.id =
                id;
    }

    public Integer getId() {
        return id;
    }

    public int getIdInt() {
        return id != null
                ? id.intValue()
                : 0;
    }

    public String getIdString() {
        return id != null
                && id.intValue() > 0
                ? String.valueOf(
                id
        )
                : "";
    }

    public void setId(
            Integer id) {

        this.id =
                id;
    }

    public Integer getIdRequerimiento() {
        return idRequerimiento;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimiento != null
                ? idRequerimiento.intValue()
                : 0;
    }

    public void setIdRequerimiento(
            Integer idRequerimiento) {

        this.idRequerimiento =
                idRequerimiento;
    }

    public void setIdRequerimientoCompra(
            int idRequerimientoCompra) {

        this.idRequerimiento =
                idRequerimientoCompra > 0
                        ? Integer.valueOf(
                        idRequerimientoCompra
                )
                        : null;
    }

    public Integer getIdArticulo() {
        return idArticulo;
    }

    public int getIdArticuloInt() {
        return idArticulo != null
                ? idArticulo.intValue()
                : 0;
    }

    public String getIdArticuloString() {
        return idArticulo != null
                && idArticulo.intValue() > 0
                ? String.valueOf(
                idArticulo
        )
                : "";
    }

    public void setIdArticulo(
            Integer idArticulo) {

        this.idArticulo =
                idArticulo;
    }

    public String getArticulo() {
        return articulo;
    }

    public String getArticuloVisible() {
        return articulo != null
                ? articulo
                : "";
    }

    public void setArticulo(
            String articulo) {

        this.articulo =
                WebKeysCompras.trimToNull(
                        articulo
                );
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public int getCantidadInt() {
        return cantidad != null
                ? cantidad.intValue()
                : 0;
    }

    public String getCantidadString() {
        return cantidad != null
                ? cantidad.toString()
                : "0";
    }

    public void setCantidad(
            Integer cantidad) {

        this.cantidad =
                cantidad;

        this.precioTotalEstimado =
                calcularPrecioTotalEstimado();
    }

    public BigDecimal getPrecioUnitarioEstimado() {
        return precioUnitarioEstimado;
    }

    public String getPrecioUnitarioEstimadoString() {
        return WebKeysCompras.formatearImporte(
                precioUnitarioEstimado
        );
    }

    public void setPrecioUnitarioEstimado(
            BigDecimal precioUnitarioEstimado) {

        this.precioUnitarioEstimado =
                WebKeysCompras.normalizarImporte(
                        precioUnitarioEstimado
                );

        this.precioTotalEstimado =
                calcularPrecioTotalEstimado();
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
        BigDecimal total =
                getPrecioTotalEstimado();

        return WebKeysCompras.formatearImporte(
                total
        );
    }

    public void setPrecioTotalEstimado(
            BigDecimal precioTotalEstimado) {

        this.precioTotalEstimado =
                WebKeysCompras.normalizarImporte(
                        precioTotalEstimado
                );
    }

    public BigDecimal calcularPrecioTotalEstimado() {
        return WebKeysCompras.calcularPrecioTotal(
                cantidad,
                precioUnitarioEstimado
        );
    }

    public Integer getIdPrestador() {
        return idPrestador;
    }

    public int getIdPrestadorInt() {
        return idPrestador != null
                ? idPrestador.intValue()
                : 0;
    }

    public String getIdPrestadorString() {
        return idPrestador != null
                && idPrestador.intValue() > 0
                ? String.valueOf(
                idPrestador
        )
                : "";
    }

    public void setIdPrestador(
            Integer idPrestador) {

        this.idPrestador =
                idPrestador != null
                        && idPrestador.intValue() > 0
                        ? idPrestador
                        : null;
    }

    /*
     * Alias explícitos para el contrato de un único prestador adjudicado por
     * requerimiento. Se conserva get/setIdPrestador por compatibilidad con la
     * persistencia existente.
     */
    public Integer getIdPrestadorAdjudicado() {
        return getIdPrestador();
    }

    public int getIdPrestadorAdjudicadoInt() {
        return getIdPrestadorInt();
    }

    public String getIdPrestadorAdjudicadoString() {
        return getIdPrestadorString();
    }

    public void setIdPrestadorAdjudicado(
            Integer idPrestadorAdjudicado) {

        setIdPrestador(
                idPrestadorAdjudicado
        );
    }

    public void aplicarPrestadorAdjudicado(
            Integer idPrestadorAdjudicado) {

        setIdPrestador(
                idPrestadorAdjudicado
        );
    }

    public boolean tienePrestadorAdjudicado() {
        return idPrestador != null
                && idPrestador.intValue() > 0;
    }

    public boolean tienePrecioUnitarioEstimado() {
        return precioUnitarioEstimado != null
                && precioUnitarioEstimado.compareTo(
                BigDecimal.ZERO
        ) >= 0;
    }

    public boolean estaCompletoParaCotizacion() {
        return cantidad != null
                && cantidad.intValue() > 0
                && tienePrecioUnitarioEstimado()
                && getPrecioTotalEstimado() != null
                && tienePrestadorAdjudicado();
    }

    public String getPrestadorCuit() {
        return prestadorCuit;
    }

    public String getPrestadorCuitVisible() {
        return prestadorCuit != null
                ? prestadorCuit
                : "";
    }

    public void setPrestadorCuit(
            String prestadorCuit) {

        this.prestadorCuit =
                WebKeysCompras.trimToNull(
                        prestadorCuit
                );
    }

    public String getPrestadorRazonSocial() {
        return prestadorRazonSocial;
    }

    public String getPrestadorRazonSocialVisible() {
        return prestadorRazonSocial != null
                ? prestadorRazonSocial
                : "";
    }

    public void setPrestadorRazonSocial(
            String prestadorRazonSocial) {

        this.prestadorRazonSocial =
                WebKeysCompras.trimToNull(
                        prestadorRazonSocial
                );
    }

    public String getPrestadorSeleccionadoVisible() {
        if (WebKeysCompras.isEmpty(
                prestadorRazonSocial
        )) {
            return getPrestadorCuitVisible();
        }

        if (WebKeysCompras.isEmpty(
                prestadorCuit
        )) {
            return getPrestadorRazonSocialVisible();
        }

        return getPrestadorRazonSocialVisible()
                + " - "
                + getPrestadorCuitVisible();
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getObservacionesVisible() {
        return observaciones != null
                ? observaciones
                : "";
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones =
                WebKeysCompras.trimToNull(
                        observaciones
                );
    }
}
