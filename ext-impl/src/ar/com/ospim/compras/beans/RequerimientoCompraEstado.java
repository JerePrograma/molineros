package ar.com.ospim.compras.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class RequerimientoCompraEstado {

    private Integer id;
    private String descripcion;

    public RequerimientoCompraEstado() {
    }

    public RequerimientoCompraEstado(Integer id) {
        this.id = id;
    }

    public RequerimientoCompraEstado(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public int getIdEstado() {
        return id != null ? id.intValue() : 0;
    }

    public int getEstado() {
        return getIdEstado();
    }

    public String getIdEstadoString() {
        return id != null && id.intValue() > 0 ? String.valueOf(id) : "";
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIdEstado(int idEstado) {
        this.id = idEstado > 0 ? Integer.valueOf(idEstado) : null;
    }

    public void setEstado(int estado) {
        setIdEstado(estado);
    }

    public String getDescripcion() {
        if (!WebKeysCompras.isEmpty(descripcion)) {
            return descripcion;
        }

        return WebKeysCompras.getEstadoDescripcion(getIdEstado());
    }

    public String getDescripcionVisible() {
        String value = getDescripcion();
        return value != null ? value : "";
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = WebKeysCompras.trimToNull(descripcion);
    }

    public boolean isBorrador() {
        return WebKeysCompras.esBorrador(getIdEstado());
    }

    public boolean isCotizado() {
        return WebKeysCompras.esCotizado(getIdEstado());
    }

    public boolean isAnulado() {
        return WebKeysCompras.esAnulado(getIdEstado());
    }
}
