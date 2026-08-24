package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Protege la atomicidad del alta de RP cuando Compras aporta la conexion.
 */
public final class ReclamoPrestacionTransaccionComprasContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String servicio = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoPrestacionServiceImpl.java"
        );
        String vinculo = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "RequerimientoCompraReclamoPrestacionalHelper.java"
        );

        String interno = extraerMetodo(
                servicio,
                "private int insertarInterno("
        );

        noContiene(
                interno,
                "no reemplaza la conexion recibida",
                "ConnectionHelper.getConnectionForTransaction()"
        );
        noContiene(
                interno,
                "no confirma la transaccion del caller",
                "con.commit()"
        );
        noContiene(
                interno,
                "no revierte la transaccion del caller",
                "ConnectionHelper.rollback(con)"
        );
        noContiene(
                interno,
                "no cierra la conexion del caller",
                "ConnectionHelper.cerrar(stmt5, con)"
        );
        contiene(
                interno,
                "cierra solo el ultimo statement",
                "ConnectionHelper.cerrar(stmt5);"
        );

        String standalone = extraerMetodo(
                servicio,
                "public int insertar(\n"
                        + "            ReclamoPrestacional reclamoPrestacional,"
        );
        contiene(
                standalone,
                "standalone abre transaccion",
                "getConnectionForTransaction()"
        );
        contiene(
                standalone,
                "standalone confirma",
                "con.commit()"
        );
        contiene(
                standalone,
                "standalone revierte",
                "ConnectionHelper.rollback("
        );
        contiene(
                standalone,
                "standalone cierra",
                "ConnectionHelper.cerrar("
        );

        String crearYVincular = extraerMetodo(
                vinculo,
                "private int crearYVincularInterno("
        );
        antes(
                crearYVincular,
                ".insertarReclamoPrestacional(",
                "finalizarCreacion("
        );
        antesDesde(
                crearYVincular,
                "finalizarCreacion(",
                "transaccion.commit()"
        );
        contiene(
                crearYVincular,
                "el flujo Compras revierte RP y vinculo juntos",
                "transaccion.rollback()"
        );

        System.out.println(
                "CONTRATO_TRANSACCION_RP_COMPRAS_OK"
        );
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), LATIN1)
                .replace("\r\n", "\n");
    }

    private static String extraerMetodo(String contenido, String firma) {
        int inicio = contenido.indexOf(firma);

        if (inicio < 0) {
            throw new AssertionError("No se encontro la firma: " + firma);
        }

        int apertura = contenido.indexOf('{', inicio);
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
                    etiqueta + ": falta [" + esperado + "]"
            );
        }
    }

    private static void noContiene(
            String contenido,
            String etiqueta,
            String prohibido) {

        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": contiene [" + prohibido + "]"
            );
        }
    }

    private static void antes(
            String contenido,
            String primero,
            String segundo) {

        int a = contenido.indexOf(primero);
        int b = contenido.indexOf(segundo);

        if (a < 0 || b <= a) {
            throw new AssertionError(
                    "Orden invalido: " + primero + " / " + segundo
            );
        }
    }

    private static void antesDesde(
            String contenido,
            String primero,
            String segundo) {

        int a = contenido.indexOf(primero);
        int b = a >= 0
                ? contenido.indexOf(segundo, a + primero.length())
                : -1;

        if (a < 0 || b <= a) {
            throw new AssertionError(
                    "Orden invalido desde: " + primero + " / " + segundo
            );
        }
    }

    private ReclamoPrestacionTransaccionComprasContractTest() {
    }
}
