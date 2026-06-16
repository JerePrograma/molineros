package ar.com.ospim.compras.requerimientos.beans;

public class NotificacionCotizacionResultado {

    private int totalCandidatos;
    private int enviados;
    private int errores;
    private int omitidos;

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

    public int getOmitidos() {
        return omitidos;
    }

    public void setOmitidos(int omitidos) {
        this.omitidos = omitidos;
    }

    public void incrementarOmitidos() {
        omitidos++;
    }

    public int getTotalProcesados() {
        return enviados + errores + omitidos;
    }

    public boolean tieneErrores() {
        return errores > 0;
    }
}