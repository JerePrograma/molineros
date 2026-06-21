package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraEstado;

import java.util.List;

public class WebKeysComprasTransicionesTest {

    public static void main(String[] args) {
        assertEstados();

        assertTransicion(1, 1, false);
        assertTransicion(2, 2, false);
        assertTransicion(3, 3, false);
        assertTransicion(4, 4, false);
        assertTransicion(5, 5, false);
        assertTransicion(99, 99, false);

        assertTransicion(1, 2, true);
        assertTransicion(2, 3, true);

        assertTransicion(1, 4, false);
        assertTransicion(1, 5, false);
        assertTransicion(2, 4, false);
        assertTransicion(2, 5, false);
        assertTransicion(3, 4, false);
        assertTransicion(3, 5, false);
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

        assertAccionesEstado(1, true, false, false, true, true);
        assertAccionesEstado(2, false, true, true, true, false);
        assertAccionesEstado(3, false, false, false, false, false);
        assertAccionesEstado(4, false, false, false, false, false);
        assertAccionesEstado(5, false, false, false, false, false);
        assertAccionesEstado(99, false, false, false, false, false);

        assertBoolean(
                "solo lectura 3",
                true,
                WebKeysCompras.esSoloLectura(3)
        );
        assertBoolean(
                "solo lectura 4",
                true,
                WebKeysCompras.esSoloLectura(4)
        );
        assertBoolean(
                "solo lectura 5",
                true,
                WebKeysCompras.esSoloLectura(5)
        );
        assertBoolean(
                "solo lectura 99",
                true,
                WebKeysCompras.esSoloLectura(99)
        );
    }

    private static void assertEstados() {
        int[] ids = new int[]{1, 2, 3, 4, 5, 99};
        String[] descripciones = new String[]{
                "PENDIENTE",
                "A COTIZAR",
                "COTIZADO",
                "RECLAMO (RP)",
                "ORDEN DE COMPRA",
                "ANULADO"
        };

        List<RequerimientoCompraEstado> estados =
                WebKeysCompras.listarEstados();

        if (estados.size() != ids.length) {
            throw new AssertionError(
                    "Cantidad de estados: esperado="
                            + ids.length
                            + ", actual="
                            + estados.size()
            );
        }

        for (int i = 0; i < ids.length; i++) {
            RequerimientoCompraEstado estado =
                    estados.get(i);

            assertBoolean(
                    "id estado " + i,
                    true,
                    estado.getIdEstado() == ids[i]
            );

            if (!descripciones[i].equals(
                    estado.getDescripcionVisible()
            )) {
                throw new AssertionError(
                        "Descripcion estado "
                                + ids[i]
                                + ": esperado="
                                + descripciones[i]
                                + ", actual="
                                + estado.getDescripcionVisible()
                );
            }
        }

        RequerimientoCompraEstado estadoConDescripcionLegacy =
                new RequerimientoCompraEstado(
                        Integer.valueOf(4),
                        "descripcion legacy"
                );

        if (!"RECLAMO (RP)".equals(
                estadoConDescripcionLegacy.getDescripcionVisible()
        )) {
            throw new AssertionError(
                    "La descripcion Java debe prevalecer sobre valores legacy."
            );
        }
    }

    private static void assertAccionesEstado(
            int estado,
            boolean enviarACotizar,
            boolean editarCotizacion,
            boolean reintentarNotificaciones,
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
