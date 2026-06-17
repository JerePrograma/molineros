package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;

public class WebKeysComprasTransicionesTest {

    public static void main(String[] args) {
        assertTransicion(1, 1, false);
        assertTransicion(2, 2, false);
        assertTransicion(3, 3, false);
        assertTransicion(4, 4, false);

        assertTransicion(1, 2, true);
        assertTransicion(2, 3, true);
        assertTransicion(3, 4, true);
        assertTransicion(4, 5, true);

        assertTransicion(2, 1, false);
        assertTransicion(3, 2, false);
        assertTransicion(4, 3, false);
        assertTransicion(5, 4, false);

        assertTransicion(5, 99, false);
        assertTransicion(99, 1, false);
        assertTransicion(99, 99, false);

        assertTransicion(1, 99, true);
        assertTransicion(2, 99, true);
        assertTransicion(3, 99, true);
        assertTransicion(4, 99, true);

        assertAccionesEstado(1, true, false, false, false, false, true, true);
        assertAccionesEstado(2, false, true, false, false, false, true, false);
        assertAccionesEstado(3, false, false, true, false, false, true, false);
        assertAccionesEstado(4, false, false, false, true, true, true, false);
        assertAccionesEstado(5, false, false, false, false, false, false, false);
        assertAccionesEstado(99, false, false, false, false, false, false, false);
    }

    private static void assertAccionesEstado(
            int estado,
            boolean enviarAAutorizar,
            boolean autorizar,
            boolean iniciarCotizaciones,
            boolean reintentarNotificaciones,
            boolean generarOrdenCompra,
            boolean anular,
            boolean editar) {

        assertBoolean(
                "puedeEnviarAAutorizar(" + estado + ")",
                enviarAAutorizar,
                WebKeysCompras.puedeEnviarAAutorizar(estado)
        );
        assertBoolean(
                "puedeAutorizar(" + estado + ")",
                autorizar,
                WebKeysCompras.puedeAutorizar(estado)
        );
        assertBoolean(
                "puedeIniciarCotizaciones(" + estado + ")",
                iniciarCotizaciones,
                WebKeysCompras.puedeIniciarCotizaciones(estado)
        );
        assertBoolean(
                "puedeReintentarNotificacionesCotizaciones(" + estado + ")",
                reintentarNotificaciones,
                WebKeysCompras.puedeReintentarNotificacionesCotizaciones(estado)
        );
        assertBoolean(
                "puedeGenerarOrdenCompra(" + estado + ")",
                generarOrdenCompra,
                WebKeysCompras.puedeGenerarOrdenCompra(estado)
        );
        assertBoolean(
                "puedeAnular(" + estado + ")",
                anular,
                WebKeysCompras.puedeAnular(estado)
        );
        assertBoolean(
                "puedeEditar(" + estado + ")",
                editar,
                WebKeysCompras.puedeEditar(estado)
        );
    }

    private static void assertBoolean(
            String descripcion,
            boolean esperado,
            boolean actual) {

        if (actual != esperado) {
            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static void assertTransicion(int actual,
                                         int nuevo,
                                         boolean esperado) {

        boolean actualResultado =
                WebKeysCompras.validarTransicionEstado(
                        actual,
                        nuevo
                );

        if (actualResultado != esperado) {
            throw new AssertionError(
                    "Transicion "
                            + actual
                            + " -> "
                            + nuevo
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actualResultado
            );
        }
    }

    private WebKeysComprasTransicionesTest() {
    }
}
