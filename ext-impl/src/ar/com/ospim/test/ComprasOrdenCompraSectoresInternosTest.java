package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;

public final class ComprasOrdenCompraSectoresInternosTest {

    public static void main(String[] args) {
        assertCapacidad(
                "RRHH elegible",
                true,
                WebKeysCompras.puedePasarAOrdenCompra(
                        WebKeysCompras.ESTADO_PENDIENTE,
                        "RRHH",
                        true,
                        true
                )
        );

        assertCapacidad(
                "Sistemas elegible",
                true,
                WebKeysCompras.puedePasarAOrdenCompra(
                        WebKeysCompras.ESTADO_PENDIENTE,
                        "Sistemas",
                        true,
                        true
                )
        );

        assertCapacidad(
                "sin detalle",
                false,
                WebKeysCompras.puedePasarAOrdenCompra(
                        WebKeysCompras.ESTADO_PENDIENTE,
                        "RRHH",
                        false,
                        true
                )
        );

        assertCapacidad(
                "sin cotizacion",
                false,
                WebKeysCompras.puedePasarAOrdenCompra(
                        WebKeysCompras.ESTADO_PENDIENTE,
                        "SISTEMAS",
                        true,
                        false
                )
        );

        assertCapacidad(
                "sector externo",
                false,
                WebKeysCompras.puedePasarAOrdenCompra(
                        WebKeysCompras.ESTADO_PENDIENTE,
                        "FARMACIA",
                        true,
                        true
                )
        );

        assertCapacidad(
                "estado distinto de PENDIENTE",
                false,
                WebKeysCompras.puedePasarAOrdenCompra(
                        WebKeysCompras.ESTADO_A_COTIZAR,
                        "RRHH",
                        true,
                        true
                )
        );

        assertCapacidad(
                "matriz generica permanece cerrada",
                false,
                WebKeysCompras.validarTransicionEstado(
                        WebKeysCompras.ESTADO_PENDIENTE,
                        WebKeysCompras.ESTADO_ORDEN_COMPRA
                )
        );

        System.out.println(
                "ComprasOrdenCompraSectoresInternosTest: OK"
        );
    }

    private static void assertCapacidad(
            String descripcion,
            boolean esperado,
            boolean actual) {

        if (esperado != actual) {
            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private ComprasOrdenCompraSectoresInternosTest() {
    }
}
