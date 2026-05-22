package ar.com.ospim.compras.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class RequerimientoCompraSector {

    private int idSector;
    private String codigo;
    private String descripcion;
    private boolean requiereAfiliado;
    private boolean activo;

    public RequerimientoCompraSector() {
        this.activo = true;
    }

    public RequerimientoCompraSector(int idSector) {
        this();
        this.idSector = idSector;
    }

    public RequerimientoCompraSector(int idSector, String codigo, String descripcion, boolean requiereAfiliado) {
        this(idSector);
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.requiereAfiliado = requiereAfiliado;
    }

    public int getIdSector() {
        return idSector;
    }

    public int getId() {
        return idSector;
    }

    public String getIdSectorString() {
        return idSector > 0 ? String.valueOf(idSector) : "";
    }

    public void setIdSector(int idSector) {
        this.idSector = idSector;
    }

    public void setId(int id) {
        this.idSector = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCodigoVisible() {
        return codigo != null ? codigo : "";
    }

    public void setCodigo(String codigo) {
        this.codigo = WebKeysCompras.trimToNull(codigo);
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

    public boolean isRequiereAfiliado() {
        return requiereAfiliado;
    }

    public boolean getRequiereAfiliado() {
        return requiereAfiliado;
    }

    public String getRequiereAfiliadoDescripcion() {
        return WebKeysCompras.getBooleanDescripcion(Boolean.valueOf(requiereAfiliado));
    }

    public void setRequiereAfiliado(boolean requiereAfiliado) {
        this.requiereAfiliado = requiereAfiliado;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}