package ar.com.ospim.compras.requerimientos.beans;

public class CotizacionPrestadorDiagnostico {

    private int prestadoresHabilitados;
    private int prestadoresCompatiblesSector;
    private int prestadoresBloqueadosEstadoPrevio;

    public int getPrestadoresHabilitados() {
        return prestadoresHabilitados;
    }

    public void setPrestadoresHabilitados(int value) {
        this.prestadoresHabilitados = value;
    }

    public int getPrestadoresCompatiblesSector() {
        return prestadoresCompatiblesSector;
    }

    public void setPrestadoresCompatiblesSector(int value) {
        this.prestadoresCompatiblesSector = value;
    }

    public int getPrestadoresBloqueadosEstadoPrevio() {
        return prestadoresBloqueadosEstadoPrevio;
    }

    public void setPrestadoresBloqueadosEstadoPrevio(int value) {
        this.prestadoresBloqueadosEstadoPrevio = value;
    }
}
