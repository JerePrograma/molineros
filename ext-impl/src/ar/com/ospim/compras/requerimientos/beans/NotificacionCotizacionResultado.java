package ar.com.ospim.compras.requerimientos.beans;

public class NotificacionCotizacionResultado {

    private int totalCandidatos;
    private int enviados;
    private int errores;
    private int omitidos;

    public int getTotalCandidatos() {
        return totalCandidatos;
    }

    public void setTotalCandidatos(int totalCandidatos) {
        this.totalCandidatos = totalCandidatos;
    }

    public int getEnviados() {
        return enviados;
    }

    public void incrementarEnviados() {
        enviados++;
    }

    public int getErrores() {
        return errores;
    }

    public void incrementarErrores() {
        errores++;
    }

    public int getOmitidos() {
        return omitidos;
    }

    public void incrementarOmitidos() {
        omitidos++;
    }
}