package ar.com.ospim.compras.requerimientos.beans;

import java.io.Serializable;

public class NotificacionCotizacionResultado
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalCandidatos;
    private int enviados;
    private int errores;
    private int emailsInvalidos;
    private int omitidos;
    private int prestadoresHabilitados;
    private int prestadoresCompatiblesSector;
    private int prestadoresBloqueadosEstadoPrevio;

    public int getTotalCandidatos() {
        return totalCandidatos;
    }

    public void setTotalCandidatos(
            int totalCandidatos) {

        this.totalCandidatos =
                totalCandidatos;
    }

    public int getEnviados() {
        return enviados;
    }

    public void setEnviados(int enviados) {
        this.enviados = enviados;
    }

    public void incrementarEnviados() {
        enviados++;
    }

    public int getErrores() {
        return errores;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public void incrementarErrores() {
        errores++;
    }

    public int getEmailsInvalidos() {
        return emailsInvalidos;
    }

    public void setEmailsInvalidos(int emailsInvalidos) {
        this.emailsInvalidos = emailsInvalidos;
    }

    public void incrementarEmailsInvalidos() {
        emailsInvalidos++;
    }

    public int getOmitidos() {
        return omitidos;
    }

    public void setOmitidos(int omitidos) {
        this.omitidos = omitidos;
    }

    public void incrementarOmitidos() {
        omitidos++;
    }

    public int getPrestadoresHabilitados() {
        return prestadoresHabilitados;
    }

    public void setPrestadoresHabilitados(int prestadoresHabilitados) {
        this.prestadoresHabilitados = prestadoresHabilitados;
    }

    public int getPrestadoresCompatiblesSector() {
        return prestadoresCompatiblesSector;
    }

    public void setPrestadoresCompatiblesSector(
            int prestadoresCompatiblesSector) {

        this.prestadoresCompatiblesSector =
                prestadoresCompatiblesSector;
    }

    public int getPrestadoresBloqueadosEstadoPrevio() {
        return prestadoresBloqueadosEstadoPrevio;
    }

    public void setPrestadoresBloqueadosEstadoPrevio(
            int prestadoresBloqueadosEstadoPrevio) {

        this.prestadoresBloqueadosEstadoPrevio =
                prestadoresBloqueadosEstadoPrevio;
    }

    public int getTotalProcesados() {
        return enviados + errores + emailsInvalidos + omitidos;
    }

    public boolean tieneErrores() {
        return errores > 0 || emailsInvalidos > 0;
    }
}
