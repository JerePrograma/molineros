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
