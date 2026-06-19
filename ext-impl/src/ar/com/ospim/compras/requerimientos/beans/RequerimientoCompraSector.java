package ar.com.ospim.compras.requerimientos.beans;

import ar.com.ospim.compras.WebKeysCompras;

import java.util.Locale;

public class RequerimientoCompraSector {

    private Integer id;
    private String descripcion;
    private Boolean requiereAfiliado;

    public RequerimientoCompraSector() {
        this.requiereAfiliado = Boolean.FALSE;
    }

    public RequerimientoCompraSector(Integer id) {
        this();
        this.id = id;
    }

    public RequerimientoCompraSector(Integer id, String descripcion, Boolean requiereAfiliado) {
        this.id = id;
        this.descripcion = descripcion;
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
        String value = getDescripcion();

        return value != null
                ? value.toUpperCase(Locale.ROOT)
                : "";
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
