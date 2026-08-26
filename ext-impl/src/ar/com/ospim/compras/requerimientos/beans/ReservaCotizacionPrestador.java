package ar.com.ospim.compras.requerimientos.beans;

public class ReservaCotizacionPrestador {

    private boolean reservado;
    private String estadoEnvio;
    private String emailDestino;
    private String motivoCodigo;
    private String motivoDescripcion;

    public boolean isReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }

    public String getEstadoEnvio() {
        return estadoEnvio;
    }

    public void setEstadoEnvio(String estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }

    /**
     * Contiene 0..N destinatarios separados por ';'.
     *
     * Se conserva String para mantener compatibilidad
     * con el contrato de persistencia existente.
     */
    public String getEmailDestino() {
        return emailDestino;
    }

    public void setEmailDestino(String emailDestino) {
        this.emailDestino = emailDestino;
    }

    public String getMotivoCodigo() {
        return motivoCodigo;
    }

    public void setMotivoCodigo(String motivoCodigo) {
        this.motivoCodigo = motivoCodigo;
    }

    public String getMotivoDescripcion() {
        return motivoDescripcion;
    }

    public void setMotivoDescripcion(String motivoDescripcion) {
        this.motivoDescripcion = motivoDescripcion;
    }
}