package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.util.DateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequerimientoCompra {

    private Integer id;

    private Date altaFecha;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;
    private Date bajaFecha;
    private String bajaUsr;

    private String afiliadoCuilTitular;
    private Integer afiliadoInt;

    private Integer idSector;
    private String sectorDescripcion;
    private Boolean requiereAfiliado;

    private Integer cargoOspim;
    private Integer cargoTercerizadora;
    private String idTercerizadora;

    private Boolean recupero;
    private String observaciones;

    private Integer idEstado;
    private String estadoDescripcion;

    private List<RequerimientoCompraDetalle> detalles;

    private String afiliadoNombre;
    private String afiliadoApellido;
    private String afiliadoNombreApellido;
    private String afiliadoDocumentoTipo;
    private String afiliadoDocumentoNro;
    private String afiliadoDocumento;

    public RequerimientoCompra() {
        this.idEstado = Integer.valueOf(WebKeysCompras.ESTADO_BORRADOR);
        this.requiereAfiliado = Boolean.FALSE;
        this.cargoOspim = Integer.valueOf(0);
        this.cargoTercerizadora = Integer.valueOf(0);
        this.recupero = Boolean.FALSE;
        this.detalles = new ArrayList<RequerimientoCompraDetalle>();
    }

    public RequerimientoCompra(Integer id) {
        this();
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public int getIdRequerimientoCompra() {
        return id != null ? id.intValue() : 0;
    }

    public String getIdString() {
        return id != null && id.intValue() > 0 ? String.valueOf(id) : "";
    }

    public String getIdRequerimientoCompraString() {
        return getIdString();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIdRequerimientoCompra(int idRequerimientoCompra) {
        this.id = idRequerimientoCompra > 0 ? Integer.valueOf(idRequerimientoCompra) : null;
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

    public String getAfiliadoCuilTitular() {
        return afiliadoCuilTitular;
    }

    public String getAfiliadoCuilTitularVisible() {
        return afiliadoCuilTitular != null ? afiliadoCuilTitular : "";
    }

    public void setAfiliadoCuilTitular(String afiliadoCuilTitular) {
        this.afiliadoCuilTitular = WebKeysCompras.trimToNull(afiliadoCuilTitular);
    }

    public Integer getAfiliadoInt() {
        return afiliadoInt;
    }

    public String getAfiliadoIntString() {
        return afiliadoInt != null ? String.valueOf(afiliadoInt) : "";
    }

    public void setAfiliadoInt(Integer afiliadoInt) {
        this.afiliadoInt = afiliadoInt;
    }

    public boolean tieneAfiliadoInformado() {
        return !WebKeysCompras.isEmpty(afiliadoCuilTitular) && afiliadoInt != null;
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

    public String getSectorDescripcion() {
        return sectorDescripcion;
    }

    public String getSectorDescripcionVisible() {
        return sectorDescripcion != null ? sectorDescripcion : "";
    }

    public void setSectorDescripcion(String sectorDescripcion) {
        this.sectorDescripcion = WebKeysCompras.trimToNull(sectorDescripcion);
    }

    public Boolean getRequiereAfiliado() {
        return requiereAfiliado;
    }

    public boolean isRequiereAfiliado() {
        return Boolean.TRUE.equals(requiereAfiliado);
    }

    public String getRequiereAfiliadoDescripcion() {
        return WebKeysCompras.getBooleanDescripcion(requiereAfiliado);
    }

    public void setRequiereAfiliado(Boolean requiereAfiliado) {
        this.requiereAfiliado = requiereAfiliado != null ? requiereAfiliado : Boolean.FALSE;
    }

    public void setRequiereAfiliado(boolean requiereAfiliado) {
        this.requiereAfiliado = Boolean.valueOf(requiereAfiliado);
    }

    public Integer getCargoOspim() {
        return cargoOspim;
    }

    public String getCargoOspimString() {
        return cargoOspim != null ? String.valueOf(cargoOspim) : "0";
    }

    public void setCargoOspim(Integer cargoOspim) {
        this.cargoOspim = cargoOspim != null ? cargoOspim : Integer.valueOf(0);
    }

    public Integer getCargoTercerizadora() {
        return cargoTercerizadora;
    }

    public String getCargoTercerizadoraString() {
        return cargoTercerizadora != null ? String.valueOf(cargoTercerizadora) : "0";
    }

    public void setCargoTercerizadora(Integer cargoTercerizadora) {
        this.cargoTercerizadora = cargoTercerizadora != null ? cargoTercerizadora : Integer.valueOf(0);
    }

    public String getIdTercerizadora() {
        return idTercerizadora;
    }

    public void setIdTercerizadora(String idTercerizadora) {
        this.idTercerizadora = WebKeysCompras.trimToNull(idTercerizadora);
    }

    public Boolean getRecupero() {
        return recupero;
    }

    public boolean isRecupero() {
        return Boolean.TRUE.equals(recupero);
    }

    public String getRecuperoDescripcion() {
        return WebKeysCompras.getBooleanDescripcion(recupero);
    }

    public void setRecupero(Boolean recupero) {
        this.recupero = recupero != null ? recupero : Boolean.FALSE;
    }

    public void setRecupero(boolean recupero) {
        this.recupero = Boolean.valueOf(recupero);
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

    public Integer getIdEstado() {
        return idEstado;
    }

    public int getEstado() {
        return idEstado != null ? idEstado.intValue() : 0;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = Integer.valueOf(idEstado);
    }

    public void setEstado(int estado) {
        this.idEstado = Integer.valueOf(estado);
    }

    public String getEstadoDescripcion() {
        if (!WebKeysCompras.isEmpty(estadoDescripcion)) {
            return estadoDescripcion;
        }

        return WebKeysCompras.getEstadoDescripcion(getEstado());
    }

    public String getEstadoDescripcionVisible() {
        String value = getEstadoDescripcion();
        return value != null ? value : "";
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = WebKeysCompras.trimToNull(estadoDescripcion);
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

            if (detalle != null && detalle.getPrecioTotalEstimado() != null) {
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
        return WebKeysCompras.puedeEditar(getEstado()) && bajaFecha == null;
    }

    public boolean isBorrador() {
        return WebKeysCompras.esBorrador(getEstado());
    }

    public boolean isCotizado() {
        return WebKeysCompras.esCotizado(getEstado());
    }

    public boolean isAnulado() {
        return WebKeysCompras.esAnulado(getEstado());
    }

    public boolean isActivo() {
        return bajaFecha == null;
    }

    public boolean puedeCotizar() {
        return WebKeysCompras.puedeCotizar(getEstado()) && bajaFecha == null;
    }

    public boolean puedeAnular() {
        return WebKeysCompras.puedeAnular(getEstado()) && bajaFecha == null;
    }

    public String getAfiliadoNombre() {
        return afiliadoNombre;
    }

    public String getAfiliadoNombreVisible() {
        return afiliadoNombre != null ? afiliadoNombre : "";
    }

    public void setAfiliadoNombre(String afiliadoNombre) {
        this.afiliadoNombre = WebKeysCompras.trimToNull(afiliadoNombre);
    }

    public String getAfiliadoApellido() {
        return afiliadoApellido;
    }

    public String getAfiliadoApellidoVisible() {
        return afiliadoApellido != null ? afiliadoApellido : "";
    }

    public void setAfiliadoApellido(String afiliadoApellido) {
        this.afiliadoApellido = WebKeysCompras.trimToNull(afiliadoApellido);
    }

    public String getAfiliadoNombreApellido() {
        return afiliadoNombreApellido;
    }

    public String getAfiliadoNombreApellidoVisible() {
        if (!WebKeysCompras.isEmpty(afiliadoNombreApellido)) {
            return afiliadoNombreApellido;
        }

        String apellido = getAfiliadoApellidoVisible();
        String nombre = getAfiliadoNombreVisible();

        if (!WebKeysCompras.isEmpty(apellido) && !WebKeysCompras.isEmpty(nombre)) {
            return apellido + ", " + nombre;
        }

        if (!WebKeysCompras.isEmpty(apellido)) {
            return apellido;
        }

        return nombre;
    }

    public void setAfiliadoNombreApellido(String afiliadoNombreApellido) {
        this.afiliadoNombreApellido = WebKeysCompras.trimToNull(afiliadoNombreApellido);
    }

    public String getAfiliadoDocumentoTipo() {
        return afiliadoDocumentoTipo;
    }

    public String getAfiliadoDocumentoTipoVisible() {
        return afiliadoDocumentoTipo != null ? afiliadoDocumentoTipo : "";
    }

    public void setAfiliadoDocumentoTipo(String afiliadoDocumentoTipo) {
        this.afiliadoDocumentoTipo = WebKeysCompras.trimToNull(afiliadoDocumentoTipo);
    }

    public String getAfiliadoDocumentoNro() {
        return afiliadoDocumentoNro;
    }

    public String getAfiliadoDocumentoNroVisible() {
        return afiliadoDocumentoNro != null ? afiliadoDocumentoNro : "";
    }

    public void setAfiliadoDocumentoNro(String afiliadoDocumentoNro) {
        this.afiliadoDocumentoNro = WebKeysCompras.trimToNull(afiliadoDocumentoNro);
    }

    public String getAfiliadoDocumento() {
        return afiliadoDocumento;
    }

    public String getAfiliadoDocumentoVisible() {
        if (!WebKeysCompras.isEmpty(afiliadoDocumento)) {
            return afiliadoDocumento;
        }

        String tipo = getAfiliadoDocumentoTipoVisible();
        String nro = getAfiliadoDocumentoNroVisible();

        if (!WebKeysCompras.isEmpty(tipo) && !WebKeysCompras.isEmpty(nro)) {
            return tipo + " " + nro;
        }

        return nro;
    }

    public void setAfiliadoDocumento(String afiliadoDocumento) {
        this.afiliadoDocumento = WebKeysCompras.trimToNull(afiliadoDocumento);
    }
}
