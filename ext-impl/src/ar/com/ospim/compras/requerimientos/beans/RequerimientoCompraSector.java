package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class RequerimientoCompraSector {

    private Integer id;
    private String descripcion;
    private Boolean requiereAfiliado;
    private String tipoItem;
    private boolean seleccionableAlta;
    private boolean permiteCotizacionEmpresa;
    private boolean permiteOrdenCompraDirecta;
    private boolean busquedaNomencladorMedica;
    private boolean permiteMedicamentoLegacy;
    private String sectorReclamoPrestacional;
    private java.util.List<Integer> nomencladores = new java.util.ArrayList<Integer>();

    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String value) { tipoItem = value; }
    public boolean isSeleccionableAlta() { return seleccionableAlta; }
    public void setSeleccionableAlta(boolean value) { seleccionableAlta = value; }
    public boolean isPermiteCotizacionEmpresa() { return permiteCotizacionEmpresa; }
    public void setPermiteCotizacionEmpresa(boolean value) { permiteCotizacionEmpresa = value; }
    public boolean isPermiteOrdenCompraDirecta() { return permiteOrdenCompraDirecta; }
    public void setPermiteOrdenCompraDirecta(boolean value) { permiteOrdenCompraDirecta = value; }
    public boolean isBusquedaNomencladorMedica() { return busquedaNomencladorMedica; }
    public void setBusquedaNomencladorMedica(boolean value) { busquedaNomencladorMedica = value; }
    public boolean isPermiteMedicamentoLegacy() { return permiteMedicamentoLegacy; }
    public void setPermiteMedicamentoLegacy(boolean value) { permiteMedicamentoLegacy = value; }
    public String getSectorReclamoPrestacional() { return sectorReclamoPrestacional; }
    public void setSectorReclamoPrestacional(String value) { sectorReclamoPrestacional = value; }
    public java.util.List<Integer> getNomencladores() { return nomencladores; }
    public void setNomencladores(java.util.List<Integer> value) { nomencladores = value; }
    public boolean isNomenclador() { return RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR.equals(tipoItem); }
    public boolean isObservacion() { return RequerimientoCompraDetalle.TIPO_ITEM_OBSERVACION.equals(tipoItem); }
    public Integer getFiltroTipoNomenclador() {
        if (!isNomenclador() || nomencladores.isEmpty()) { return null; }
        return nomencladores.size() == 1 ? nomencladores.get(0) : Integer.valueOf(0);
    }


    public RequerimientoCompraSector() {
        this.requiereAfiliado = Boolean.FALSE;
    }

    public RequerimientoCompraSector(Integer id) {
        this();
        this.id = id;
    }

    public RequerimientoCompraSector(Integer id, String descripcion, Boolean requiereAfiliado) {
        this.id = id;
        setDescripcion(descripcion);
        this.requiereAfiliado = requiereAfiliado != null ? requiereAfiliado : Boolean.FALSE;
    }

    public Integer getId() {
        return id;
    }

    public int getIdSector() {
        return id != null ? id.intValue() : 0;
    }

    public String getIdSectorString() {
        return id != null && id.intValue() > 0 ? String.valueOf(id) : "";
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIdSector(int idSector) {
        this.id = idSector > 0 ? Integer.valueOf(idSector) : null;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcionVisible() {
        return WebKeysCompras.getSectorDescripcionVisible(
                getDescripcion()
        );
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = WebKeysCompras.trimToNull(descripcion);
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
}
