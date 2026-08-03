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
 * validos en Compras y deben llegar como referencia temporal al alta de RP.
 */
public final class ReclamoPrestacionalCompraPrecargaContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String SERVICE =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                    + "ReclamoPrestacionalCompraPrecargaServiceUtil.java";

    private static final String VIEW =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/view_reclamo.jspf";

    private static final String CONTEXTO =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/view_reclamo_contexto.jspf";

    private static final String CABECERA =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/view_reclamo_cabecera.jspf";

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
        String contexto = leer(CONTEXTO);
        String cabecera = leer(CABECERA);
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
        noContiene(
                recuperable,
                "no elimina la lista precargada",
                "removeAttribute(\n"
                        + "                WebKeysAutorizaciones\n"
                        + "                        .LISTADO_PRESTACIONES"
        );
        contiene(
                recuperable,
                "expone el valor inicial para el cliente",
                "recuperable_sur_compra_inicial"
        );

        contiene(
                contexto,
                "restaura el titulo legacy del alta",
                "String nroreclamo = \"Caso Nro 00000\";"
        );
        contiene(
                contexto,
                "muestra el requerimiento en el campo inferior",
                "String opAsignadaalReclamo = esBorradorCompras"
        );
        contiene(
                contexto,
                "identifica el requerimiento de Compras",
                "\"Requerimiento #\""
        );
        noContiene(
                contexto,
                "no mezcla el requerimiento con el titulo principal",
                "\"Nuevo Reclamo Prestacional - Requerimiento #\""
        );
        contiene(
                contexto,
                "conserva el titulo del reclamo persistido",
                "nroreclamo =\"Reclamo Nro : \""
        );
        contiene(
                contexto,
                "conserva la leyenda sin orden de pago",
                "opAsignadaalReclamo=\"Sin Orden de Pago\""
        );
        antes(
                cabecera,
                "<%= nroreclamo %>",
                "<%= opAsignadaalReclamo %>"
        );

        contiene(
                view,
                "normaliza ADD antes de resolver el contexto",
                "boolean handoffComprasModoValido"
        );
        contiene(
                view,
                "exige nonce del handoff",
                "Validator.isNotNull(nonceComprasModo)"
        );
        contiene(
                view,
                "valida coincidencia del nonce",
                "contextoComprasModo.coincideNonce(nonceComprasModo)"
        );
        contiene(
                view,
                "valida pertenencia al usuario",
                "contextoComprasModo.perteneceAUsuario("
        );
        contiene(
                view,
                "valida vigencia del contexto",
                "contextoComprasModo.estaVigente("
        );
        noContiene(
                view,
                "no depende del parametro generico origen",
                "\"origen\""
        );
        antes(
                view,
                "request.setAttribute(Constants.CMD, Constants.ADD);",
                "view_reclamo_contexto.jspf"
        );

        contiene(
                view,
                "neutraliza solo el editor inicial de Compras",
                "boolean neutralizarEdicionInicialCompras"
        );
        contiene(
                view,
                "exige modo de alta validado",
                "esBorradorCompras"
        );
        contiene(
                view,
                "no pisa una edicion explicitamente tipada",
                "request.getAttribute(\"tipoEdicion\") == null"
        );
        contiene(
                view,
                "elimina solo el estado de edicion de prestacion",
                "removeAttribute(\n"
                        + "            WebKeysAutorizaciones"
                        + ".PRESTACION_EN_PROCESO_DE_EDICION"
        );
        antes(
                view,
                "neutralizarEdicionInicialCompras",
                "Object tipoEdicionOriginalAntesDePrestaciones"
        );

        contiene(
                view,
                "corrige el modo visual despues del JavaScript legacy",
                "function asegurarAltaInicialCompras()"
        );
        contiene(
                view,
                "no oculta una edicion explicita con contenido",
                "editor.children().length"
        );
        contiene(
                view,
                "oculta el editor vacio",
                "editor.hide().attr(\"aria-hidden\", \"true\")"
        );
        contiene(
                view,
                "muestra el formulario de alta",
                "ingreso.show().attr(\"aria-hidden\", \"false\")"
        );
        contiene(
                view,
                "programa la correccion despues de los scripts",
                "window.setTimeout(asegurarAltaInicialCompras, 0);"
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
                "conserva el parche de Compras",
                "view_reclamo_compras_surge_patch.js"
        );

        contiene(
                surgePatch,
                "propaga Recuperable SUR en solicitudes AJAX",
                "copia.recuperableSur = parseInt(valorActual, 10);"
        );
        noContiene(
                surgePatch,
                "no elimina el contexto de Compras",
                "CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA"
        );

        System.out.println(
                "CONTRATO_RECLAMO_PRESTACIONAL_COMPRAS_PRECARGA_OK"
        );
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), ISO_8859_1);
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
