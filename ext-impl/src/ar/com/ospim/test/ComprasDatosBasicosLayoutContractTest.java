package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual de la distribución de datos básicos de Compras. */
public final class ComprasDatosBasicosLayoutContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String DATOS_BASICOS =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                    + "_datos_basicos.jsp";

    private ComprasDatosBasicosLayoutContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String jsp = leer(DATOS_BASICOS);

        contiene(
                jsp,
                "Sector comparte la fila de ID y Estado",
                "</strong>\n"
                        + "            </td>\n\n"
                        + "            <td><label for=\"<portlet:namespace />sector_id\">Sector:</label></td>"
        );
        noContiene(
                jsp,
                "Sector no abre una segunda fila de resumen",
                "</tr>\n\n"
                        + "        <tr>\n"
                        + "            <td><label for=\"<portlet:namespace />sector_id\">Sector:</label></td>"
        );

        contiene(
                jsp,
                "la fila visual de cargos se conserva",
                "<table class=\"lfr-table compras-cargos-requerimiento\">\n"
                        + "        <tr>"
        );
        contiene(
                jsp,
                "Surge queda en la misma fila visual que los cargos",
                "</table>\n"
                        + "                </div>\n"
                        + "            </td>\n\n"
                        + "            <td style=\"vertical-align: middle;\">\n"
                        + "                <label for=\"<portlet:namespace />surge\">"
        );
        contiene(
                jsp,
                "el grupo ocultable conserva los tres datos de cargos",
                "id=\"<portlet:namespace />fila_cargos_compra\""
        );
        noContiene(
                jsp,
                "Surge no se oculta con la fila automática de cargos",
                "<tr id=\"<portlet:namespace />fila_cargos_compra\""
        );

        contiene(jsp, "creación y edición conservan Sector", "id=\"<portlet:namespace />sector_id\"");
        contiene(jsp, "vista conserva Sector", "sectorDescripcionSoloLectura");
        contiene(jsp, "creación y edición conservan Surge", "id=\"<portlet:namespace />surge\"");
        contiene(jsp, "vista conserva Surge", "req.getSurgeDescripcion()");
        contiene(jsp, "Cargo OSPIM se conserva", "id=\"<portlet:namespace />cargo_ospim\"");
        contiene(jsp, "Cargo tercerizadora se conserva", "id=\"<portlet:namespace />cargo_tercerizadora\"");
        contiene(jsp, "Recupero se conserva", "id=\"<portlet:namespace />recupero\"");

        System.out.println("CONTRATO_COMPRAS_DATOS_BASICOS_LAYOUT_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), ISO_8859_1);
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
