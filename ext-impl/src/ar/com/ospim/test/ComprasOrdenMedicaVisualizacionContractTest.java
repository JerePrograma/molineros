package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public final class ComprasOrdenMedicaVisualizacionContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "DescargarOrdenMedicaCompraAction.java"
        );
        String vista = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/requerimiento_compra_orden_medica_consulta_componente.jsp"
        );

        contiene(
                vista,
                "la lupa solicita visualizacion",
                "\"visualizar\""
        );
        antes(
                vista,
                "\"visualizar\"",
                "\"true\""
        );
        contiene(
                vista,
                "la imagen se abre en una ventana legacy",
                "window.open("
        );
        contiene(
                action,
                "el servidor interpreta la opcion",
                "ParamUtil.getBoolean("
        );
        contiene(
                action,
                "respuesta visible en navegador",
                "\"Content-Disposition\",\n                        \"inline\""
        );
        contiene(
                action,
                "MIME validado",
                "response.setContentType(\n                        documento.getContentType()"
        );
        contiene(
                action,
                "evita reinterpretar el contenido",
                "\"X-Content-Type-Options\",\n                        \"nosniff\""
        );
        antes(
                action,
                "response.setContentType(",
                "ServletResponseUtil.write("
        );
        contiene(
                action,
                "descarga legacy conservada",
                "ServletResponseUtil.sendFile("
        );

        System.out.println(
                "COMPRAS_ORDEN_MEDICA_VISUALIZACION_OK"
        );
    }

    private static String leer(String path) throws Exception {
        return new String(
                Files.readAllBytes(new File(path).toPath()),
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

    private static void antes(
            String texto,
            String primero,
            String segundo) {

        int a = texto.indexOf(primero);
        int b = texto.indexOf(segundo, a + 1);

        if (a < 0 || b <= a) {
            throw new AssertionError(
                    "Orden invalido: " + primero + " / " + segundo
            );
        }
    }

    private ComprasOrdenMedicaVisualizacionContractTest() {
    }
}
