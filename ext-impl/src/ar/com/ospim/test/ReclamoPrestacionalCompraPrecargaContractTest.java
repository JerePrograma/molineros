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

    private static final String VIEW =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/view_reclamo.jspf";

    private static final String RECUPERABLE =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/"
                    + "view_reclamo_recuperable_neutro.jspf";

    private static final String SURGE_PATCH =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/"
                    + "view_reclamo_compras_surge_patch.js";

    private ReclamoPrestacionalCompraPrecargaContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String service = leer(SERVICE);
        String view = leer(VIEW);
        String recuperable = leer(RECUPERABLE);
        String surgePatch = leer(SURGE_PATCH);

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

        contiene(
                recuperable,
                "reconoce el borrador de Compras",
                "esBorradorCompras\n        && contextoCompras != null"
        );
        contiene(
                recuperable,
                "obtiene SUR exclusivamente desde surge",
                "contextoCompras.isSurge()"
        );
        contiene(
                recuperable,
                "conserva la lista de prestaciones precargadas",
                "LISTADO_PRESTACIONES_RECLAMOS_EN_SESION"
        );
        contiene(
                recuperable,
                "conserva la prestacion activa en edicion",
                "PRESTACION_EN_PROCESO_DE_EDICION"
        );
        noContiene(
                recuperable,
                "no elimina la cabecera ni la precarga de sesion",
                "session.removeAttribute("
        );
        noContiene(
                recuperable,
                "no convierte el borrador en alta vacia",
                "esBorradorCompras = false"
        );
        contiene(
                recuperable,
                "expone el valor inicial para el cliente",
                "recuperable_sur_compra_inicial"
        );

        antes(
                view,
                "view_reclamo_cabecera.jspf",
                "view_reclamo_recuperable_neutro.jspf"
        );
        antes(
                view,
                "view_reclamo_recuperable_neutro.jspf",
                "view_reclamo_prestaciones.jspf"
        );
        contiene(
                view,
                "invalida cache del parche restaurado",
                "view_reclamo_compras_surge_patch.js"
                        + "?v=20260723-restaura-precarga-1"
        );

        contiene(
                surgePatch,
                "propaga Recuperable SUR en solicitudes AJAX",
                "copia.recuperableSur = parseInt(valorActual, 10);"
        );
        noContiene(
                surgePatch,
                "no oculta el editor de la prestacion precargada",
                ".hide()"
        );
        noContiene(
                surgePatch,
                "no fuerza el formulario de carga vacio",
                ".show()"
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

    private static void antes(
            String contenido,
            String primero,
            String segundo) {

        int posicionPrimero = contenido.indexOf(primero);
        int posicionSegundo = contenido.indexOf(segundo);

        if (posicionPrimero < 0
                || posicionSegundo < 0
                || posicionPrimero >= posicionSegundo) {

            throw new AssertionError(
                    "Orden invalido entre ["
                            + primero
                            + "] y ["
                            + segundo
                            + "]"
            );
        }
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
