package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Contrato ejecutable de la precarga Compras -> Reclamo Prestacional.
 */
public final class ReclamoPrestacionalCompraPrecargaContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "ReclamoPrestacionalCompraPrecargaServiceUtil.java"
        );
        String contexto = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "ReclamoPrestacionalCompraContexto.java"
        );
        String guardar = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "EditarReclamosEntryAction.java"
        );
        String view = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );

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
                "consulta el nomenclador canonico",
                "NomencladorServiceUtil"
        );
        contiene(
                referencia,
                "mantiene medicamento historico",
                "detalle.tieneMedicamento()"
        );
        contiene(
                referencia,
                "acepta detalle observacion",
                "detalle.esObservacion()"
        );
        contiene(
                referencia,
                "no fabrica id medico",
                "prestacion.setId_prestacion("
        );
        contiene(
                referencia,
                "codigo temporal trazable",
                "\"ART-\" + detalle.getIdInt()"
        );

        contiene(
                service,
                "precarga cabecera en sesion",
                ".RECLAMO_PRESTACION_EN_EDICION"
        );
        contiene(
                service,
                "precarga prestaciones en sesion",
                ".LISTADO_PRESTACIONES_RECLAMOS_EN_SESION"
        );
        contiene(
                service,
                "precarga nonce temporal",
                ".CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA"
        );
        contiene(
                service,
                "cabecera conserva recupero",
                "reclamo.setRecuperable("
        );
        contiene(
                service,
                "cabecera conserva surge",
                "reclamo.setSuperintendencia("
        );

        contiene(
                contexto,
                "contexto conserva recupero",
                "public boolean isRecupero()"
        );
        contiene(
                contexto,
                "contexto conserva surge",
                "public boolean isSurge()"
        );
        String recuperable = extraerMetodo(
                contexto,
                "public int getRecuperableInicial("
        );
        antes(
                recuperable,
                "if (surge)",
                "if (recupero)"
        );

        contiene(
                guardar,
                "save restaura recupero del contexto",
                "contextoCompra.isRecupero()"
        );
        contiene(
                guardar,
                "save restaura surge del contexto",
                "reclamoPrestacional.setSuperintendencia("
        );
        contiene(
                guardar,
                "save restaura tercerizadora del requerimiento",
                "idTercerizadora = requerimiento.getIdTercerizadora();"
        );
        String resolverContexto = extraerMetodo(
                guardar,
                "private ReclamoPrestacionalCompraContexto "
                        + "resolverContextoCompra("
        );
        antes(
                resolverContexto,
                "Object contextoObj = session.getAttribute(",
                "if (StringUtils.checkEmpty(nonceRequest))"
        );
        contiene(
                resolverContexto,
                "handoff sin nonce falla cerrado",
                "if (contextoObj != null)"
        );
        contiene(
                resolverContexto,
                "handoff sin nonce no degrada a alta generica",
                "El contexto de Compras requiere un nonce valido."
        );
        antes(
                guardar,
                "reclamoPrestacional.setRecuperable(",
                ".insertar(reclamoPrestacional, user)"
        );

        contiene(
                view,
                "vista monolitica valida el contexto",
                "boolean handoffReclamoComprasValido"
        );
        contiene(
                view,
                "vista exige origen Compras",
                "\"compras\".equalsIgnoreCase(origenReclamoCompras)"
        );
        noContiene(
                view,
                "sin includes experimentales",
                "view_reclamo.jspf"
        );
        noContiene(
                view,
                "sin fuente comprimida",
                "__JSP_STATIC_NAMESPACE_"
        );

        System.out.println(
                "CONTRATO_RECLAMO_PRESTACIONAL_COMPRAS_PRECARGA_OK"
        );
    }

    private static String leer(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        sinBom(path, bytes);
        String contenido = new String(bytes, LATIN1);
        noContiene(
                contenido,
                path + " sin mojibake C3",
                String.valueOf((char) 0x00C3)
        );
        noContiene(
                contenido,
                path + " sin mojibake C2",
                String.valueOf((char) 0x00C2)
        );
        noContiene(
                contenido,
                path + " sin reemplazo",
                String.valueOf((char) 0xFFFD)
        );
        return contenido;
    }

    private static void sinBom(String path, byte[] bytes) {
        boolean utf8 = bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF;
        boolean utf16 = bytes.length >= 2
                && (((bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xFE)
                || ((bytes[0] & 0xFF) == 0xFE
                && (bytes[1] & 0xFF) == 0xFF));
        if (utf8 || utf16) {
            throw new AssertionError(path + " contiene BOM");
        }
    }

    private static String extraerMetodo(String contenido, String firma) {
        int inicio = contenido.indexOf(firma);
        int apertura = contenido.indexOf('{', inicio);
        if (inicio < 0 || apertura < 0) {
            throw new AssertionError("No se encontro la firma: " + firma);
        }
        int nivel = 0;
        for (int i = apertura; i < contenido.length(); i++) {
            char caracter = contenido.charAt(i);
            if (caracter == '{') {
                nivel++;
            } else if (caracter == '}') {
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
        int posicionSegundo = contenido.indexOf(segundo, posicionPrimero + 1);
        if (posicionPrimero < 0 || posicionSegundo <= posicionPrimero) {
            throw new AssertionError(
                    "Orden invalido entre [" + primero + "] y [" + segundo + "]"
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

    private ReclamoPrestacionalCompraPrecargaContractTest() {
    }
}
