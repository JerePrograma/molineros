package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato focalizado de ritmo visual del formulario de Compras. */
public final class ComprasFormularioVisualContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String BASE =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/";

    private ComprasFormularioVisualContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String estilos = leer(BASE + "_estilos.jsp");
        String edicion = leer(BASE + "_layout_edicion.jsp");
        String vista = leer(BASE + "_layout_vista.jsp");

        contiene(
                edicion,
                "contenedor de alta y edicion",
                "compras-formulario-requerimiento"
        );
        contiene(
                vista,
                "contenedor de vista",
                "compras-formulario-requerimiento compras-modo-vista"
        );
        contiene(
                estilos,
                "selector exclusivo de controles",
                ".compras-formulario-requerimiento input[type=\"text\"]"
        );
        contiene(
                estilos,
                "altura uniforme",
                "height: 26px;"
        );
        contiene(
                estilos,
                "padding uniforme",
                "padding: 2px 5px;"
        );
        contiene(
                estilos,
                "separacion vertical",
                "padding-top: 5px;"
        );
        contiene(
                estilos,
                "distancia label control",
                "margin-right: 6px;"
        );
        contiene(
                estilos,
                "textarea acotado",
                ".compras-formulario-requerimiento textarea"
        );
        noContiene(
                estilos,
                "sin flexbox",
                "display: flex"
        );
        noContiene(
                estilos,
                "sin grid",
                "display: grid"
        );
        noContiene(
                estilos,
                "sin ocultamiento horizontal",
                "overflow-x: hidden"
        );

        System.out.println(
                "CONTRATO_COMPRAS_FORMULARIO_VISUAL_OK"
        );
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
