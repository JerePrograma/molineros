package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/** Contrato textual de la tabla de datos basicos de Compras. */
public final class ComprasDatosBasicosLayoutContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String DATOS_BASICOS =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                    + "requerimiento_compra_datos_basicos_componente.jsp";

    private static final String CAMPOS_OCULTOS =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                    + "requerimiento_compra_campos_ocultos_formulario_componente.jsp";

    private static final String SCRIPTS_BASE =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                    + "requerimiento_compra_scripts_base_componente.jsp";

    private ComprasDatosBasicosLayoutContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Path path = Paths.get(DATOS_BASICOS);
        byte[] bytes = Files.readAllBytes(path);
        String jsp = new String(bytes, ISO_8859_1);
        String camposOcultos = leer(CAMPOS_OCULTOS);
        String scriptsBase = leer(SCRIPTS_BASE);

        contiene(
                jsp,
                "tabla exterior unica de datos basicos",
                "<table class=\"lfr-table compras-resumen-requerimiento "
                        + "compras-cargos-requerimiento "
                        + "compras-datos-basicos-requerimiento\""
        );
        ocurrencias(
                jsp,
                "una sola tabla exterior compartida",
                "<table class=\"lfr-table compras-resumen-requerimiento ",
                1
        );
        noContiene(
                jsp,
                "sin segunda tabla exterior de cargos",
                "<table class=\"lfr-table compras-cargos-requerimiento\">"
        );

        contiene(
                jsp,
                "ID se muestra en textbox readonly",
                "id=\"<portlet:namespace />requerimiento_id_visual\""
        );
        contiene(
                jsp,
                "Estado se muestra en textbox readonly",
                "id=\"<portlet:namespace />estado_visual\""
        );
        contiene(
                jsp,
                "campos visuales no se pueden editar",
                "readonly=\"readonly\""
        );

        antes(
                jsp,
                "requerimiento_id_visual",
                "estado_visual"
        );
        antes(
                jsp,
                "estado_visual",
                "sector_id"
        );
        antes(
                jsp,
                "fila_cargos_compra",
                "id=\"<portlet:namespace />surge\""
        );

        contiene(
                jsp,
                "Sector conserva select en creacion y edicion",
                "<select id=\"<portlet:namespace />sector_id\""
        );
        contiene(
                jsp,
                "Sector usa textbox en vista",
                "value=\"<%= HtmlUtil.escape(sectorDescripcionSoloLectura) %>\""
        );
        contiene(
                jsp,
                "Surge conserva select en creacion y edicion",
                "<select id=\"<portlet:namespace />surge\""
        );
        contiene(
                jsp,
                "Surge usa textbox en vista",
                "value=\"<%= HtmlUtil.escape(req.getSurgeDescripcion()) %>\""
        );

        contiene(
                jsp,
                "Cargo OSPIM conserva sincronizacion",
                "onkeyup=\"<portlet:namespace />sincronizarFormularioCompra();\""
        );
        contiene(
                jsp,
                "Cargo tercerizadora conserva sincronizacion",
                "id=\"<portlet:namespace />cargo_tercerizadora\""
        );
        contiene(
                camposOcultos,
                "Recupero conserva valor calculado no manipulable",
                "id=\"<portlet:namespace />recupero_hidden\""
        );
        contiene(
                scriptsBase,
                "Recupero se deriva del cargo de tercerizadora",
                "cargoTercerizadora != null && cargoTercerizadora > 0"
        );

        contiene(
                jsp,
                "grupo ocultable de cargos preservado",
                "id=\"<portlet:namespace />fila_cargos_compra\""
        );
        noContiene(
                jsp,
                "Surge no se oculta con los cargos",
                "<tr id=\"<portlet:namespace />fila_cargos_compra\""
        );
        noContiene(
                jsp,
                "vista sin divs de texto plano",
                "<div class=\"compras-campo-solo-lectura\">"
        );

        verificaCodificacion(path, bytes, jsp);

        System.out.println("CONTRATO_COMPRAS_DATOS_BASICOS_LAYOUT_OK");
    }

    private static void verificaCodificacion(
            Path path,
            byte[] bytes,
            String contenido) {

        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF) {

            throw new AssertionError("BOM UTF-8 no permitido: " + path);
        }

        if (!Arrays.equals(bytes, contenido.getBytes(ISO_8859_1))) {
            throw new AssertionError(
                    "El archivo no hace round-trip ISO-8859-1: " + path
            );
        }

        noContiene(contenido, "sin mojibake A tilde", "\u00C3");
        noContiene(contenido, "sin mojibake A circunflejo", "\u00C2");
        noContiene(contenido, "sin reemplazo Unicode", "\uFFFD");

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

    private static String leer(String ruta) throws Exception {
        return new String(
                Files.readAllBytes(Paths.get(ruta)),
                ISO_8859_1
        );
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

    private static void ocurrencias(
            String contenido,
            String etiqueta,
            String buscado,
            int esperado) {

        int cantidad = 0;
        int posicion = 0;

        while ((posicion = contenido.indexOf(buscado, posicion)) >= 0) {
            cantidad++;
            posicion += buscado.length();
        }

        if (cantidad != esperado) {
            throw new AssertionError(
                    etiqueta
                            + ": esperado="
                            + esperado
                            + " actual="
                            + cantidad
            );
        }
    }
}
