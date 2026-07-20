package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual para evitar bloqueos del pool al cargar requerimientos. */
public final class ComprasRequerimientoConnectionContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String SERVICE_DIR =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/service/";

    private ComprasRequerimientoConnectionContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String util = leer(
                SERVICE_DIR + "BusquedaRequerimientoCompraServiceUtil.java"
        );
        String segura = leer(
                SERVICE_DIR
                        + "BusquedaRequerimientoCompraLecturaSeguraServiceImpl.java"
        );

        contiene(
                util,
                "la lectura publica usa la implementacion segura",
                "lecturaSeguraInstance.getRequerimientoCompra("
        );
        noContiene(
                extraerMetodo(
                        util,
                        "public static RequerimientoCompra getRequerimientoCompra("
                ),
                "la API no vuelve a la implementacion con conexion anidada",
                "getInstance().getRequerimientoCompra("
        );

        String metodoPublico = extraerMetodo(
                segura,
                "public RequerimientoCompra getRequerimientoCompra("
        );
        antes(
                metodoPublico,
                "getCabeceraRequerimiento(idRequerimientoCompra)",
                "detalleService.getDetalles(idRequerimientoCompra)"
        );

        String metodoCabecera = extraerMetodo(
                segura,
                "private RequerimientoCompra getCabeceraRequerimiento("
        );
        contiene(
                metodoCabecera,
                "la cabecera cierra recursos en finally",
                "ConnectionHelper.cerrar(stmt, con);"
        );
        contiene(
                metodoCabecera,
                "la consulta tiene timeout",
                "stmt.setQueryTimeout(QUERY_TIMEOUT_SEGUNDOS);"
        );
        noContiene(
                metodoCabecera,
                "la cabecera no solicita detalles con la conexion abierta",
                "getDetalles("
        );

        System.out.println(
                "CONTRATO_COMPRAS_REQUERIMIENTO_CONEXION_SECUENCIAL_OK"
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
