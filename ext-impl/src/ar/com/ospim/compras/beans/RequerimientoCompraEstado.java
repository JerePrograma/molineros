package ar.com.ospim.compras.beans;

import ar.com.ospim.compras.WebKeysCompras;

public class RequerimientoCompraEstado {

    private int idEstado;
    private String codigo;
    private String descripcion;
    private Integer orden;
    private boolean activo;

    public RequerimientoCompraEstado() {
        this.activo = true;
    }

    public RequerimientoCompraEstado(int idEstado) {
        this();
        this.idEstado = idEstado;
    }

    public RequerimientoCompraEstado(int idEstado, String codigo, String descripcion) {
        this(idEstado);
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public int getId() {
        return idEstado;
    }

    public int getEstado() {
        return idEstado;
    }

    public String getIdEstadoString() {
        return idEstado > 0 ? String.valueOf(idEstado) : "";
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public void setId(int id) {
        this.idEstado = id;
    }

    public void setEstado(int estado) {
        this.idEstado = estado;
    }

    public String getCodigo() {
        if (!WebKeysCompras.isEmpty(codigo)) {
            return codigo;
        }

        return WebKeysCompras.getEstadoCodigo(idEstado);
    }

    public void setCodigo(String codigo) {
        this.codigo = WebKeysCompras.trimToNull(codigo);
    }

    public String getDescripcion() {
        if (!WebKeysCompras.isEmpty(descripcion)) {
            return descripcion;
        }

        return WebKeysCompras.getEstadoDescripcion(idEstado);
    }

    public String getDescripcionVisible() {
        String value = getDescripcion();
        return value != null ? value : "";
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = WebKeysCompras.trimToNull(descripcion);
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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

    public boolean isBorrador() {
        return idEstado == WebKeysCompras.ESTADO_BORRADOR;
    }

    public boolean isSolicitado() {
        return idEstado == WebKeysCompras.ESTADO_SOLICITADO;
    }

    public boolean isAnulado() {
        return idEstado == WebKeysCompras.ESTADO_ANULADO;
    }

    public boolean isEditable() {
        return WebKeysCompras.esEditable(idEstado);
    }

    public boolean puedeSolicitar() {
        return WebKeysCompras.puedeSolicitar(idEstado);
    }

    public boolean puedeAnular() {
        return WebKeysCompras.puedeAnular(idEstado);
    }
}