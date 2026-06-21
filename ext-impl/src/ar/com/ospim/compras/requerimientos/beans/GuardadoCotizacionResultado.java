package ar.com.ospim.compras.requerimientos.beans;

public class GuardadoCotizacionResultado {

    private final boolean cotizacionCompleta;
    private final int estadoFinal;

    public GuardadoCotizacionResultado(
            boolean cotizacionCompleta,
            int estadoFinal) {

        this.cotizacionCompleta = cotizacionCompleta;
        this.estadoFinal = estadoFinal;
    }

    public boolean isCotizacionCompleta() {
        return cotizacionCompleta;
    }

    public int getEstadoFinal() {
        return estadoFinal;
    }
}
