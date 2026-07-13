package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

import java.math.BigDecimal;

/**
 * Detalle técnico de un requerimiento de compra.
 *
 * Nuevo contrato de Compras:
 *
 * - FARMACIA usa MEDICAMENTO.
 * - El resto de los sectores usa NOMENCLADOR.
 *
 */
public class RequerimientoCompraDetalle {

    public static final String TIPO_ITEM_NOMENCLADOR = "NOMENCLADOR";
    public static final String TIPO_ITEM_MEDICAMENTO = "MEDICAMENTO";

    private Integer id;
    private Integer idRequerimiento;

    private String tipoItem;
    private String codigoItem;
    private String descripcionItem;

    private Integer idPrestacion;
    private Integer idTipoNomenclador;
    private String codigoNomenclador;
    private String descripcionNomenclador;

    private Integer idMedicamento;
    private Integer troquel;
    private String nombreMedicamento;

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

    public String getTipoItem() {
        return tipoItem;
    }

    public String getTipoItemNormalizado() {
        String value =
                WebKeysCompras.trimToNull(
                        tipoItem
                );

        return value != null
                ? value.toUpperCase()
                : "";
    }

    public void setTipoItem(
            String tipoItem) {

        String value =
                WebKeysCompras.trimToNull(
                        tipoItem
                );

        this.tipoItem =
                value != null
                        ? value.toUpperCase()
                        : null;
    }

    public String getCodigoItem() {
        return codigoItem;
    }

    public String getCodigoItemVisible() {
        if (!WebKeysCompras.isEmpty(
                codigoItem
        )) {
            return codigoItem;
        }

        if (esMedicamento()) {
            if (troquel != null
                    && troquel.intValue() > 0) {

                return String.valueOf(
                        troquel
                );
            }

            return getIdMedicamentoString();
        }

        if (esNomenclador()) {
            return getCodigoNomencladorVisible();
        }

        return "";
    }

    public void setCodigoItem(
            String codigoItem) {

        this.codigoItem =
                WebKeysCompras.trimToNull(
                        codigoItem
                );
    }

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public String getDescripcionItemVisible() {
        if (!WebKeysCompras.isEmpty(
                descripcionItem
        )) {
            return descripcionItem;
        }

        if (esMedicamento()) {
            return getNombreMedicamentoVisible();
        }

        if (esNomenclador()) {
            return getDescripcionNomencladorVisible();
        }

        return "";
    }

    public void setDescripcionItem(
            String descripcionItem) {

        this.descripcionItem =
                WebKeysCompras.trimToNull(
                        descripcionItem
                );
    }

    public Integer getIdPrestacion() {
        return idPrestacion;
    }

    public int getIdPrestacionInt() {
        return idPrestacion != null
                ? idPrestacion.intValue()
                : 0;
    }

    public String getIdPrestacionString() {
        return idPrestacion != null
                && idPrestacion.intValue() > 0
                ? String.valueOf(
                idPrestacion
        )
                : "";
    }

    public void setIdPrestacion(
            Integer idPrestacion) {

        this.idPrestacion =
                idPrestacion != null
                        && idPrestacion.intValue() > 0
                        ? idPrestacion
                        : null;
    }

    public Integer getIdTipoNomenclador() {
        return idTipoNomenclador;
    }

    public int getIdTipoNomencladorInt() {
        return idTipoNomenclador != null
                ? idTipoNomenclador.intValue()
                : 0;
    }

    public String getIdTipoNomencladorString() {
        return idTipoNomenclador != null
                && idTipoNomenclador.intValue() > 0
                ? String.valueOf(
                idTipoNomenclador
        )
                : "";
    }

    public void setIdTipoNomenclador(
            Integer idTipoNomenclador) {

        this.idTipoNomenclador =
                idTipoNomenclador != null
                        && idTipoNomenclador.intValue() > 0
                        ? idTipoNomenclador
                        : null;
    }

    public String getCodigoNomenclador() {
        return codigoNomenclador;
    }

    public String getCodigoNomencladorVisible() {
        return codigoNomenclador != null
                ? codigoNomenclador
                : "";
    }

    public void setCodigoNomenclador(
            String codigoNomenclador) {

        this.codigoNomenclador =
                WebKeysCompras.trimToNull(
                        codigoNomenclador
                );
    }

    public String getDescripcionNomenclador() {
        return descripcionNomenclador;
    }

    public String getDescripcionNomencladorVisible() {
        return descripcionNomenclador != null
                ? descripcionNomenclador
                : "";
    }

    public void setDescripcionNomenclador(
            String descripcionNomenclador) {

        this.descripcionNomenclador =
                WebKeysCompras.trimToNull(
                        descripcionNomenclador
                );
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public int getIdMedicamentoInt() {
        return idMedicamento != null
                ? idMedicamento.intValue()
                : 0;
    }

    public String getIdMedicamentoString() {
        return idMedicamento != null
                && idMedicamento.intValue() > 0
                ? String.valueOf(
                idMedicamento
        )
                : "";
    }

    public void setIdMedicamento(
            Integer idMedicamento) {

        this.idMedicamento =
                idMedicamento != null
                        && idMedicamento.intValue() > 0
                        ? idMedicamento
                        : null;
    }

    public Integer getTroquel() {
        return troquel;
    }

    public int getTroquelInt() {
        return troquel != null
                ? troquel.intValue()
                : 0;
    }

    public String getTroquelString() {
        return troquel != null
                && troquel.intValue() > 0
                ? String.valueOf(
                troquel
        )
                : "";
    }

    public void setTroquel(
            Integer troquel) {

        this.troquel =
                troquel != null
                        && troquel.intValue() > 0
                        ? troquel
                        : null;
    }

    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public String getNombreMedicamentoVisible() {
        return nombreMedicamento != null
                ? nombreMedicamento
                : "";
    }

    public void setNombreMedicamento(
            String nombreMedicamento) {

        this.nombreMedicamento =
                WebKeysCompras.trimToNull(
                        nombreMedicamento
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
        return WebKeysCompras.formatearImporte(
                getPrecioTotalEstimado()
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

    public boolean esMedicamento() {
        return TIPO_ITEM_MEDICAMENTO.equals(
                getTipoItemNormalizado()
        );
    }

    public boolean esNomenclador() {
        return TIPO_ITEM_NOMENCLADOR.equals(
                getTipoItemNormalizado()
        );
    }

    public boolean tieneMedicamento() {
        return esMedicamento()
                && getIdMedicamentoInt() > 0
                && !WebKeysCompras.isEmpty(
                nombreMedicamento
        );
    }

    public boolean tieneNomenclador() {
        return esNomenclador()
                && getIdPrestacionInt() > 0
                && getIdTipoNomencladorInt() > 0
                && !WebKeysCompras.isEmpty(
                codigoNomenclador
        )
                && !WebKeysCompras.isEmpty(
                descripcionNomenclador
        );
    }

    public boolean tienePrecioUnitarioEstimado() {
        return precioUnitarioEstimado != null
                && precioUnitarioEstimado.compareTo(
                BigDecimal.ZERO
        ) >= 0;
    }

    public boolean estaCompletoParaCarga() {
        return cantidad != null
                && cantidad.intValue() > 0
                && (
                tieneMedicamento()
                        || tieneNomenclador()
        );
    }

    public boolean estaCompletoParaCotizacion() {
        return estaCompletoParaCarga()
                && tienePrecioUnitarioEstimado()
                && getPrecioTotalEstimado() != null
                && tienePrestadorAdjudicado();
    }
}
