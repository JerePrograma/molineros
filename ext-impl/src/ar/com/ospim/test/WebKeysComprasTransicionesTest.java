package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;

public class WebKeysComprasTransicionesTest {

    public static void main(String[] args) {
        assertTransicion(1, 1, false);
        assertTransicion(2, 2, false);
        assertTransicion(3, 3, false);
        assertTransicion(4, 4, false);
        assertTransicion(5, 5, false);
        assertTransicion(99, 99, false);

        assertTransicion(1, 2, true);
        assertTransicion(2, 3, true);

        assertTransicion(3, 4, false);
        assertTransicion(4, 5, false);
        assertTransicion(2, 1, false);
        assertTransicion(3, 2, false);
        assertTransicion(4, 3, false);
        assertTransicion(5, 4, false);
        assertTransicion(99, 1, false);

        assertTransicion(1, 99, true);
        assertTransicion(2, 99, true);
        assertTransicion(3, 99, false);
        assertTransicion(4, 99, false);
        assertTransicion(5, 99, false);

        assertAccionesEstado(1, true, false, false, false, true, true);
        assertAccionesEstado(2, false, true, true, true, true, false);
        assertAccionesEstado(3, false, false, false, false, false, false);
        assertAccionesEstado(4, false, false, false, false, false, false);
        assertAccionesEstado(5, false, false, false, false, false, false);
        assertAccionesEstado(99, false, false, false, false, false, false);
    }

    private static void assertAccionesEstado(
            int estado,
            boolean enviarACotizar,
            boolean editarCotizacion,
            boolean reintentarNotificaciones,
            boolean cerrarCotizacion,
            boolean anular,
            boolean editarEstructura) {

        assertBoolean(
                "puedeEnviarACotizar(" + estado + ")",
                enviarACotizar,
                WebKeysCompras.puedeEnviarACotizar(estado)
        );
        assertBoolean(
                "puedeEditarCotizacion(" + estado + ")",
                editarCotizacion,
                WebKeysCompras.puedeEditarCotizacion(estado)
        );
        assertBoolean(
                "puedeReintentarNotificaciones(" + estado + ")",
                reintentarNotificaciones,
                WebKeysCompras.puedeReintentarNotificaciones(estado)
        );
        assertBoolean(
                "puedeCerrarCotizacion(" + estado + ")",
                cerrarCotizacion,
                WebKeysCompras.puedeCerrarCotizacion(estado)
        );
        assertBoolean(
                "puedeAnular(" + estado + ")",
                anular,
                WebKeysCompras.puedeAnular(estado)
        );
        assertBoolean(
                "puedeEditarEstructura(" + estado + ")",
                editarEstructura,
                WebKeysCompras.puedeEditarEstructura(estado)
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
