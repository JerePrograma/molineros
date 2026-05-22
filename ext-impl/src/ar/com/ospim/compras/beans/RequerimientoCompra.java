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

    private int idEstado;
    private String estadoCodigo;
    private String estadoDescripcion;

    private Integer idSector;
    private String sectorCodigo;
    private String sectorDescripcion;

    private boolean requiereAfiliado;

    private Date fechaSolicitud;

    private String solicitanteUsr;
    private String solicitanteNombre;

    private String afiliadoCuilTitular;
    private Integer afiliadoInte;

    private String descripcion;
    private String observaciones;

    private Date altaFecha;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;
    private Date bajaFecha;
    private String bajaUsr;

    private List<RequerimientoCompraDetalle> detalles;

    public RequerimientoCompra() {
        this.idEstado = WebKeysCompras.ESTADO_BORRADOR;
        this.requiereAfiliado = false;
        this.detalles = new ArrayList<RequerimientoCompraDetalle>();
    }

    public RequerimientoCompra(int idRequerimientoCompra) {
        this();
        this.idRequerimientoCompra = idRequerimientoCompra;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimientoCompra;
    }

    public String getIdRequerimientoCompraString() {
        return idRequerimientoCompra > 0 ? String.valueOf(idRequerimientoCompra) : "";
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

    public String getNumeroVisible() {
        return numero > 0 ? String.valueOf(numero) : "Sin numero";
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public int getEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public void setEstado(int estado) {
        this.idEstado = estado;
    }

    public String getEstadoCodigo() {
        if (!WebKeysCompras.isEmpty(estadoCodigo)) {
            return estadoCodigo;
        }

        return WebKeysCompras.getEstadoCodigo(idEstado);
    }

    public void setEstadoCodigo(String estadoCodigo) {
        this.estadoCodigo = WebKeysCompras.trimToNull(estadoCodigo);
    }

    public String getEstadoDescripcion() {
        if (!WebKeysCompras.isEmpty(estadoDescripcion)) {
            return estadoDescripcion;
        }

        return WebKeysCompras.getEstadoDescripcion(idEstado);
    }

    public String getEstadoDescripcionVisible() {
        String value = getEstadoDescripcion();
        return value != null ? value : "";
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = WebKeysCompras.trimToNull(estadoDescripcion);
    }

    public Integer getIdSector() {
        return idSector;
    }

    public Integer getSectorId() {
        return idSector;
    }

    public String getIdSectorString() {
        return idSector != null && idSector.intValue() > 0 ? String.valueOf(idSector) : "";
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public void setSectorId(Integer sectorId) {
        this.idSector = sectorId;
    }

    public String getSectorCodigo() {
        return sectorCodigo;
    }

    public String getSectorCodigoVisible() {
        return sectorCodigo != null ? sectorCodigo : "";
    }

    public void setSectorCodigo(String sectorCodigo) {
        this.sectorCodigo = WebKeysCompras.trimToNull(sectorCodigo);
    }

    public String getSectorDescripcion() {
        return sectorDescripcion;
    }

    public String getSectorDescripcionVisible() {
        return sectorDescripcion != null ? sectorDescripcion : "";
    }

    public void setSectorDescripcion(String sectorDescripcion) {
        this.sectorDescripcion = WebKeysCompras.trimToNull(sectorDescripcion);
    }

    public boolean isRequiereAfiliado() {
        return requiereAfiliado;
    }

    public boolean getRequiereAfiliado() {
        return requiereAfiliado;
    }

    public String getRequiereAfiliadoDescripcion() {
        return requiereAfiliado ? "SI" : "NO";
    }

    public void setRequiereAfiliado(boolean requiereAfiliado) {
        this.requiereAfiliado = requiereAfiliado;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public String getFechaSolicitudAsString() {
        return fechaSolicitud != null ? DateUtils.format(fechaSolicitud, DateUtils.SHORT) : "";
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getSolicitanteUsr() {
        return solicitanteUsr;
    }

    public void setSolicitanteUsr(String solicitanteUsr) {
        this.solicitanteUsr = WebKeysCompras.trimToNull(solicitanteUsr);
    }

    public String getSolicitanteNombre() {
        return solicitanteNombre;
    }

    public String getSolicitanteVisible() {
        if (!WebKeysCompras.isEmpty(solicitanteNombre)) {
            return solicitanteNombre;
        }

        return solicitanteUsr != null ? solicitanteUsr : "";
    }

    public void setSolicitanteNombre(String solicitanteNombre) {
        this.solicitanteNombre = WebKeysCompras.trimToNull(solicitanteNombre);
    }

    public String getAfiliadoCuilTitular() {
        return afiliadoCuilTitular;
    }

    public String getAfiliadoCuilTitularVisible() {
        return afiliadoCuilTitular != null ? afiliadoCuilTitular : "";
    }

    public void setAfiliadoCuilTitular(String afiliadoCuilTitular) {
        this.afiliadoCuilTitular = WebKeysCompras.trimToNull(afiliadoCuilTitular);
    }

    public Integer getAfiliadoInte() {
        return afiliadoInte;
    }

    public String getAfiliadoInteString() {
        return afiliadoInte != null ? String.valueOf(afiliadoInte) : "";
    }

    public void setAfiliadoInte(Integer afiliadoInte) {
        this.afiliadoInte = afiliadoInte;
    }

    public boolean tieneAfiliadoInformado() {
        return !WebKeysCompras.isEmpty(afiliadoCuilTitular) && afiliadoInte != null;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcionVisible() {
        return descripcion != null ? descripcion : "";
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = WebKeysCompras.trimToNull(descripcion);
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

    public Date getFechaAlta() {
        return altaFecha;
    }

    public String getAltaFechaAsString() {
        return altaFecha != null ? DateUtils.format(altaFecha, DateUtils.SHORT) : "";
    }

    public String getFechaAltaAsString() {
        return getAltaFechaAsString();
    }

    public void setAltaFecha(Date altaFecha) {
        this.altaFecha = altaFecha;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.altaFecha = fechaAlta;
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

    public Date getFechaModi() {
        return modiFecha;
    }

    public String getModiFechaAsString() {
        return modiFecha != null ? DateUtils.format(modiFecha, DateUtils.SHORT) : "";
    }

    public void setModiFecha(Date modiFecha) {
        this.modiFecha = modiFecha;
    }

    public void setFechaModi(Date fechaModi) {
        this.modiFecha = fechaModi;
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

    public List<RequerimientoCompraDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<RequerimientoCompraDetalle> detalles) {
        this.detalles = detalles != null ? detalles : new ArrayList<RequerimientoCompraDetalle>();
    }

    public List<RequerimientoCompraDetalle> getItems() {
        return detalles;
    }

    public void setItems(List<RequerimientoCompraDetalle> items) {
        setDetalles(items);
    }

    public boolean tieneDetalles() {
        return detalles != null && !detalles.isEmpty();
    }

    public BigDecimal getTotalEstimado() {
        BigDecimal total = BigDecimal.ZERO;

        if (detalles == null) {
            return total;
        }

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle = detalles.get(i);

            if (detalle != null && !detalle.isBorrado() && detalle.getPrecioTotalEstimado() != null) {
                total = total.add(detalle.getPrecioTotalEstimado());
            }
        }

        return total;
    }

    public String getTotalEstimadoString() {
        BigDecimal total = getTotalEstimado();
        return total != null ? total.toString() : "0";
    }

    public boolean isEditable() {
        return WebKeysCompras.esEditable(idEstado) && bajaFecha == null;
    }

    public boolean isAnulado() {
        return idEstado == WebKeysCompras.ESTADO_ANULADO || bajaFecha != null;
    }

    public boolean isActivo() {
        return bajaFecha == null;
    }

    public boolean puedeSolicitar() {
        return WebKeysCompras.puedeSolicitar(idEstado) && bajaFecha == null;
    }

    public boolean puedeAnular() {
        return WebKeysCompras.puedeAnular(idEstado) && bajaFecha == null;
    }
}