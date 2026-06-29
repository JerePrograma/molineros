package ar.com.ospim.compras.requerimientos.beans;

import java.io.Serializable;

public class NotificacionCotizacionDetalle implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String RESULTADO_ENVIADO = "ENVIADO";
    public static final String RESULTADO_OMITIDO = "OMITIDO";
    public static final String RESULTADO_EMAIL_INVALIDO = "EMAIL_INVALIDO";
    public static final String RESULTADO_ERROR = "ERROR";
    private int idPrestador;
    private String prestador;
    private String emailReal;
    private String emailDestino;
    private String resultado;
    private String etapa;
    private String motivo;
    private boolean emailRealInvalidoAdvertido;

    public int getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(int idPrestador) {
        this.idPrestador = idPrestador;
    }

    public String getPrestador() {
        return prestador;
    }

    public void setPrestador(String prestador) {
        this.prestador = prestador;
    }

    public String getEmailReal() {
        return emailReal;
    }

    public void setEmailReal(String emailReal) {
        this.emailReal = emailReal;
    }

    public String getEmailDestino() {
        return emailDestino;
    }

    public void setEmailDestino(String emailDestino) {
        this.emailDestino = emailDestino;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public boolean isEmailRealInvalidoAdvertido() {
        return emailRealInvalidoAdvertido;
    }

    public void setEmailRealInvalidoAdvertido(boolean emailRealInvalidoAdvertido) {
        this.emailRealInvalidoAdvertido = emailRealInvalidoAdvertido;
    }
}