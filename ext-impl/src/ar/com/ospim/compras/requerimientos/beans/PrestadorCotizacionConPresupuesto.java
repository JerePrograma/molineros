package ar.com.ospim.compras.requerimientos.beans;

/**
 * Proyección utilizada exclusivamente por la administración de presupuestos.
 *
 * Conserva los datos visibles del prestador notificado, muestra COTIZADO y
 * devuelve identificador cero para que los JSP legacy no lo incorporen al
 * selector de una nueva carga.
 */
public class PrestadorCotizacionConPresupuesto
        extends PrestadorCotizacion {

    private final int idPrestadorPersistido;

    public PrestadorCotizacionConPresupuesto(
            PrestadorCotizacion prestador) {

        if (prestador == null) {
            throw new IllegalArgumentException(
                    "El prestador cotizado no puede ser nulo."
            );
        }

        idPrestadorPersistido =
                prestador.getIdPrestador();

        setIdPrestador(
                idPrestadorPersistido
        );
        setDescripcion(
                prestador.getDescripcion()
        );
        setCuit(
                prestador.getCuit()
        );
        setEmail(
                prestador.getEmail()
        );
        setIdTipoPrestador(
                prestador.getIdTipoPrestador()
        );
        setTipoPrestador(
                prestador.getTipoPrestador()
        );
        setEstadoEnvio(
                prestador.getEstadoEnvio()
        );
    }

    @Override
    public int getIdPrestador() {
        return 0;
    }

    public int getIdPrestadorPersistido() {
        return idPrestadorPersistido;
    }

    @Override
    public String getEstadoEnvioVisible() {
        return "COTIZADO";
    }
}
