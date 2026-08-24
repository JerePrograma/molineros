package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual para evitar bloqueos del pool al cargar requerimientos. */
public final class ComprasRequerimientoConnectionContractTest {

    private static final Charset ISO_8859_1 =
        Charset.forName("ISO-8859-1");

    private static final String SERVICE_DIR =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/service/";

    private ComprasRequerimientoConnectionContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String util = leer(
                SERVICE_DIR + "BusquedaRequerimientoCompraServiceUtil.java"
        );
        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "BusquedaRequerimientoCompraHelper.java"
        );
        String persistencia = leer(
                SERVICE_DIR + "BusquedaRequerimientoCompraServiceImpl.java"
        );

        contiene(
                util,
                "la lectura pública delega reglas al Helper",
                "return helper.getRequerimientoCompra(idRequerimientoCompra);"
        );
        contiene(
                helper,
                "el Helper delega sólo persistencia",
                "return service.getRequerimientoCompra("
        );

        String metodoPublico = extraerMetodo(
                persistencia,
                "public RequerimientoCompra getRequerimientoCompra("
        );
        antes(
                metodoPublico,
                "getCabeceraRequerimiento(idRequerimientoCompra)",
                "getDetalles(idRequerimientoCompra)"
        );

        String metodoCabecera = extraerMetodo(
                persistencia,
                "public RequerimientoCompra getCabeceraRequerimiento("
        );
        validarConsultaAislada(
                metodoCabecera,
                "cabecera",
                "getDetalles("
        );

        String metodoDetalles = extraerMetodo(
                persistencia,
                "public List<RequerimientoCompraDetalle> getDetalles("
        );
        validarConsultaAislada(
                metodoDetalles,
                "detalles",
                "getCabeceraRequerimiento("
        );

        System.out.println(
                "CONTRATO_COMPRAS_REQUERIMIENTO_CONEXION_SECUENCIAL_OK"
        );
    }

    private static void validarConsultaAislada(
            String metodo,
            String etapa,
            String llamadaProhibida) {

        contiene(
                metodo,
                etapa + " cierra recursos en finally",
                "ConnectionHelper.cerrar(stmt, con);"
        );
        noContiene(
                metodo,
                etapa + " no inicia la otra consulta con recursos abiertos",
                llamadaProhibida
        );
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(
        Files.readAllBytes(path),
        ISO_8859_1
);
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

    private static void antes(
            String contenido,
            String primero,
            String segundo) {

        int a = contenido.indexOf(primero);
        int b = contenido.indexOf(segundo);

        if (a < 0 || b < 0 || a >= b) {
            throw new AssertionError(
                    "Orden invalido: " + primero + " / " + segundo
            );
        }
    }
}
