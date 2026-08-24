package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Contrato de capas para validación técnica de detalles de Compras.
 */
public final class ComprasDetalleTecnicoServiceContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "EditarRequerimientoCompraHelper.java"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "EditarRequerimientoCompraServiceImpl.java"
        );

        contiene(
                helper,
                "valida antes de persistir",
                "validarDetalleParaGuardar("
        );
        antes(
                helper,
                "validarDetalleParaGuardar(",
                "persistence.guardarDetalle("
        );
        contiene(
                helper,
                "alta sólo con nomenclador",
                "Los detalles nuevos de Compras deben utilizar NOMENCLADOR."
        );
        contiene(
                helper,
                "identidad canónica",
                "obtenerNomencladorCanonico("
        );
        contiene(
                helper,
                "tipo técnico canónico",
                "idTipoNomencladorCanonico"
        );

        contiene(service, "persistencia CallableStatement", "CallableStatement");
        contiene(service, "invoca función PostgreSQL", "prepareCall(");
        noContiene(
                service,
                "ServiceImpl sin regla funcional de alta",
                "Los detalles nuevos de Compras deben utilizar NOMENCLADOR."
        );
        noContiene(
                service,
                "ServiceImpl sin resolución canónica",
                "obtenerNomencladorCanonico("
        );

        System.out.println("CONTRATO_DETALLE_TECNICO_COMPRAS_OK");
    }

    private static String leer(String ruta) throws Exception {
        return new String(
                Files.readAllBytes(Paths.get(ruta)),
                LATIN1
        );
    }

    private static void contiene(
            String texto,
            String descripcion,
            String esperado) {

        if (texto.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion + ": falta [" + esperado + "]"
            );
        }
    }

    private static void noContiene(
            String texto,
            String descripcion,
            String prohibido) {

        if (texto.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    descripcion + ": contiene [" + prohibido + "]"
            );
        }
    }

    private static void antes(
            String texto,
            String primero,
            String segundo) {

        int a = texto.indexOf(primero);
        int b = texto.indexOf(segundo, a + 1);

        if (a < 0 || b <= a) {
            throw new AssertionError(
                    "Orden inválido: " + primero + " / " + segundo
            );
        }
    }

    private ComprasDetalleTecnicoServiceContractTest() {
    }
}
