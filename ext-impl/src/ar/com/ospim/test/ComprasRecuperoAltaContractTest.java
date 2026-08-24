package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato focalizado de Recupero derivado, oculto y no manipulable. */
public final class ComprasRecuperoAltaContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String BASE =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/";

    private ComprasRecuperoAltaContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String datos = leer(BASE + "requerimiento_compra_datos_basicos_componente.jsp");
        String hidden = leer(BASE + "requerimiento_compra_campos_ocultos_formulario_componente.jsp");
        String scripts = leer(BASE + "requerimiento_compra_scripts_base_componente.jsp");
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "EditarRequerimientoCompraAction.java"
        );

        if (datos.indexOf("id=\"<portlet:namespace />recupero\"") >= 0) {
            throw new AssertionError(
                    "Recupero no debe exponerse como control manipulable."
            );
        }
        contiene(
                hidden,
                "hidden Recupero preservado",
                "id=\"<portlet:namespace />recupero_hidden\""
        );
        contiene(
                scripts,
                "sincronizacion del hidden preservada",
                "recuperoHiddenEl.value = recuperoActivo ? 'true' : 'false';"
        );
        contiene(
                scripts,
                "derivacion desde cargo tercerizadora",
                "cargoTercerizadora != null && cargoTercerizadora > 0"
        );
        contiene(
                action,
                "backend vuelve a derivar Recupero",
                "requerimiento.setRecupero("
        );
        contiene(
                action,
                "backend usa cargo tercerizadora",
                "cargoTercerizadora.intValue() > 0"
        );

        System.out.println(
                "CONTRATO_COMPRAS_RECUPERO_DERIVADO_OK"
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
                    "Orden invalido: [" + primero + "] / [" + segundo + "]"
            );
        }
    }
}
