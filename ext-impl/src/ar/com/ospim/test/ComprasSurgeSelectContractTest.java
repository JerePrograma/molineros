package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del selector obligatorio Surge en requerimientos. */
public final class ComprasSurgeSelectContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String BASE =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/";

    private static final String ACTION =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                    + "EditarRequerimientoCompraAction.java";

    private ComprasSurgeSelectContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String datosBasicos = leer(BASE + "_datos_basicos.jsp");
        String formHidden = leer(BASE + "_form_hidden.jsp");
        String scripts = leer(BASE + "_scripts_comunes.jsp");
        String action = leer(ACTION);

        contiene(
                datosBasicos,
                "selector visible Surge",
                "id=\"<portlet:namespace />surge\""
        );
        contiene(
                datosBasicos,
                "selector obligatorio",
                "required=\"required\""
        );
        contiene(
                datosBasicos,
                "accesibilidad obligatoria",
                "aria-required=\"true\""
        );
        contiene(
                datosBasicos,
                "opcion inicial sin valor persistible",
                "<option value=\"\""
        );
        contiene(
                datosBasicos,
                "Si usa valor uno",
                "<option value=\"1\""
        );
        contiene(
                datosBasicos,
                "No usa valor cero",
                "<option value=\"0\""
        );
        noContiene(
                datosBasicos,
                "Surge ya no es checkbox",
                "id=\"<portlet:namespace />surge\"\n                           value=\"true\""
        );
        antes(
                datosBasicos,
                "id=\"<portlet:namespace />sector_id\"",
                "id=\"<portlet:namespace />surge\""
        );

        contiene(
                formHidden,
                "alta conserva Seleccione vacío",
                "esNuevo ? \"\""
        );
        contiene(
                formHidden,
                "hidden persiste uno o cero",
                "req.isSurge() ? \"1\" : \"0\""
        );

        contiene(
                scripts,
                "sincronización sólo admite cero o uno",
                "surgeValue != '0' && surgeValue != '1'"
        );
        contiene(
                scripts,
                "validación permite No",
                "surgeValue == '0'"
        );
        contiene(
                scripts,
                "validación permite Si",
                "surgeValue == '1'"
        );
        contiene(
                scripts,
                "mensaje obligatorio",
                "Surge: debe seleccionar Sí o No."
        );
        contiene(
                scripts,
                "guardado protegido",
                "__comprasSurgeObligatorio"
        );

        contiene(
                action,
                "Action consume el parámetro Surge",
                "requerimiento.setSurge(getParametroBoolean(request, \"surge\"))"
        );
        contiene(
                action,
                "Action interpreta uno como verdadero",
                "\"1\".equals(value)"
        );

        System.out.println(
                "CONTRATO_COMPRAS_SURGE_SELECT_OBLIGATORIO_OK"
        );
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
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
