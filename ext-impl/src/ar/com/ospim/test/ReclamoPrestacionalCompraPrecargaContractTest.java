package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual de la precarga Compras -> Reclamo Prestacional.
 *
 * Evita volver a mezclar la completitud economica de una cotizacion con la
 * existencia de una referencia medica canonica. Los detalles OBSERVACION son
 * validos en Compras y deben llegar como referencia temporal al editor de RP.
 */
public final class ReclamoPrestacionalCompraPrecargaContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String SERVICE =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                    + "ReclamoPrestacionalCompraPrecargaServiceUtil.java";

    private ReclamoPrestacionalCompraPrecargaContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String service = leer(SERVICE);

        String validacion = extraerMetodo(
                service,
                "private static void validarDetalleCotizado("
        );

        noContiene(
                validacion,
                "la cotizacion no depende de una referencia tecnica",
                ".estaCompletoParaCotizacion()"
        );
        contiene(
                validacion,
                "valida cantidad positiva",
                "detalle.getCantidad().intValue() <= 0"
        );
        contiene(
                validacion,
                "valida precio unitario",
                "detalle.getPrecioUnitarioEstimado()"
        );
        contiene(
                validacion,
                "valida precio total",
                "detalle.getPrecioTotalEstimado()"
        );
        contiene(
                validacion,
                "valida prestador adjudicado",
                "!detalle.tienePrestadorAdjudicado()"
        );

        String referencia = extraerMetodo(
                service,
                "private static void aplicarReferenciaTecnica("
        );

        contiene(
                referencia,
                "mantiene validacion canonica del nomenclador",
                "NomencladorServiceUtil"
        );
        contiene(
                referencia,
                "mantiene compatibilidad de medicamento historico",
                "detalle.tieneMedicamento()"
        );
        contiene(
                referencia,
                "acepta detalles de observacion",
                "detalle.esObservacion()"
        );
        contiene(
                referencia,
                "no fabrica id medico para observacion",
                "prestacion.setId_prestacion(\n                    0"
        );
        contiene(
                referencia,
                "crea codigo temporal trazable",
                "\"ART-\" + detalle.getIdInt()"
        );
        contiene(
                referencia,
                "usa el texto de observacion como descripcion temporal",
                "detalle.getObservacionesVisible()"
        );
        contiene(
                service,
                "obliga a confirmar la referencia medica",
                "Confirmar nomenclador/medicamento."
        );

        System.out.println(
                "CONTRATO_RECLAMO_PRESTACIONAL_COMPRAS_PRECARGA_OK"
        );
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
    }

    private static String extraerMetodo(String contenido, String firma) {
        int inicio = contenido.indexOf(firma);
        if (inicio < 0) {
            throw new AssertionError("No se encontro la firma: " + firma);
        }

        int apertura = contenido.indexOf('{', inicio);
        if (apertura < 0) {
            throw new AssertionError("No se encontro apertura para: " + firma);
        }

        int nivel = 0;
        for (int i = apertura; i < contenido.length(); i++) {
            char c = contenido.charAt(i);
            if (c == '{') {
                nivel++;
            } else if (c == '}') {
                nivel--;
                if (nivel == 0) {
                    return contenido.substring(inicio, i + 1);
                }
            }
        }

        throw new AssertionError("Metodo sin cierre: " + firma);
    }

    private static void contiene(
            String contenido,
            String etiqueta,
            String esperado) {

        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontro [" + esperado + "]"
            );
        }
    }

    private static void noContiene(
            String contenido,
            String etiqueta,
            String prohibido) {

        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": se encontro [" + prohibido + "]"
            );
        }
    }
}
