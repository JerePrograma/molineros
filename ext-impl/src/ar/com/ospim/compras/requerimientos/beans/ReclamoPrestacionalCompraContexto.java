package ar.com.ospim.compras.requerimientos.beans;

import java.io.Serializable;

public class ReclamoPrestacionalCompraContexto implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final long VIGENCIA_MAXIMA_MILLIS =
            2L * 60L * 60L * 1000L;

    private final int idRequerimientoCompra;
    private final String afiliadoCuilTitular;
    private final Integer afiliadoInt;
    private final String usuarioInicio;
    private final long fechaInicio;
    private final String nonce;

    public ReclamoPrestacionalCompraContexto(
            int idRequerimientoCompra,
            String afiliadoCuilTitular,
            Integer afiliadoInt,
            String usuarioInicio,
            long fechaInicio,
            String nonce) {

        this.idRequerimientoCompra = idRequerimientoCompra;
        this.afiliadoCuilTitular = afiliadoCuilTitular;
        this.afiliadoInt = afiliadoInt;
        this.usuarioInicio = usuarioInicio;
        this.fechaInicio = fechaInicio;
        this.nonce = nonce;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimientoCompra;
    }

    public String getAfiliadoCuilTitular() {
        return afiliadoCuilTitular;
    }

    public Integer getAfiliadoInt() {
        return afiliadoInt;
    }

    public String getAfiliadoIntString() {
        return afiliadoInt != null ? String.valueOf(afiliadoInt) : "";
    }

    public String getUsuarioInicio() {
        return usuarioInicio;
    }

    public long getFechaInicio() {
        return fechaInicio;
    }

    public String getNonce() {
        return nonce;
    }

    public boolean coincideNonce(String nonceRequest) {
        return nonce != null && nonce.equals(nonceRequest);
    }

    public boolean perteneceAUsuario(String usuario) {
        return usuarioInicio != null && usuarioInicio.equals(usuario);
    }

    public boolean estaVigente(long ahoraMillis) {
        return fechaInicio > 0L
                && ahoraMillis >= fechaInicio
                && ahoraMillis - fechaInicio <= VIGENCIA_MAXIMA_MILLIS;
    }
}
