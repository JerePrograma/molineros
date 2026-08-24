package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del selector obligatorio Surge en requerimientos. */
public final class ComprasSurgeSelectContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String BASE =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/";

    private static final String ACTION =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                    + "EditarRequerimientoCompraAction.java";

    private ComprasSurgeSelectContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String datosBasicos = leer(BASE + "requerimiento_compra_datos_basicos_componente.jsp");
        String formHidden = leer(BASE + "requerimiento_compra_campos_ocultos_formulario_componente.jsp");
        String modelo = leer(BASE + "requerimiento_compra_modelo_vista_componente.jsp");
        String scripts = leer(BASE + "requerimiento_compra_scripts_base_componente.jsp");
        String scriptsEdicion = leer(
                BASE + "requerimiento_compra_scripts_edicion_guardado_componente.jsp"
        );
        String estilos = leer(BASE + "requerimiento_compra_estilos_componente.jsp");
        String resultados = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_compra_busqueda_resultado.jsp"
        );
        String requerimiento = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "RequerimientoCompra.java"
        );
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
                modelo,
                "alta conserva Seleccione vacío",
                "String surgeSeleccionadoCompra"
        );
        contiene(
                modelo,
                "modelo persiste uno o cero",
                "req.isSurge() ? \"1\" : \"0\""
        );
        contiene(
                formHidden,
                "hidden usa el valor normalizado",
                "value=\"<%= HtmlUtil.escape(surgeSeleccionadoCompra) %>\""
        );

        contiene(
                scripts,
                "sincronización sólo admite cero o uno",
                "&& surgeValue != '1'"
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
                scriptsEdicion,
                "guardado protegido",
                "if (!<portlet:namespace />validarSurgeCompra())"
        );
        unaVez(
                scripts,
                "validación Surge sin duplicados",
                "function <portlet:namespace />validarSurgeCompra()"
        );

        contiene(
                resultados,
                "encabezado de búsqueda SURGE",
                "headerNames.add(\"SURGE\")"
        );
        noContiene(
                resultados,
                "la columna ya no se titula Recupero",
                "headerNames.add(\"recupero\")"
        );
        contiene(
                resultados,
                "la columna usa el dato Surge",
                "req.getSurgeDescripcion()"
        );
        noContiene(
                resultados,
                "la columna no usa el dato Recupero",
                "req.getRecuperoDescripcion()"
        );

        contiene(
                requerimiento,
                "sector visible con acentuación canónica",
                "WebKeysCompras.getSectorDescripcionVisible("
        );
        contiene(
                requerimiento,
                "Surge visible SI o NO",
                "return isSurge() ? \"SI\" : \"NO\";"
        );

        contiene(
                datosBasicos,
                "resumen compacto separado",
                "lfr-table compras-resumen-requerimiento"
        );
        contiene(
                datosBasicos,
                "cargos conservados debajo",
                "compras-cargos-requerimiento"
        );
        noContiene(
                datosBasicos,
                "Recupero derivado no se expone como control manipulable",
                "id=\"<portlet:namespace />recupero\""
        );
        contiene(
                datosBasicos,
                "opción Surge SI",
                "                            SI\n"
        );
        contiene(
                datosBasicos,
                "opción Surge NO",
                "                            NO\n"
        );

        contiene(
                estilos,
                "resumen sin ancho fijo",
                ".compras-resumen-requerimiento {\n        width: auto;"
        );

        contiene(
                action,
                "Action usa parser estricto de Surge",
                "parseSurgeObligatorio(request)"
        );

        contiene(
                action,
                "Action sólo acepta cero o uno",
                "!\"0\".equals(value)"
        );

        contiene(
                action,
                "Action valida también el valor uno",
                "&& !\"1\".equals(value)"
        );

        contiene(
                action,
                "Action devuelve verdadero sólo para uno",
                "return \"1\".equals(value);"
        );

        contiene(
                action,
                "mensaje obligatorio del servidor",
                "Surge: debe seleccionar Sí o No."
        );

        noContiene(
                action,
                "Surge no puede usar parser booleano permisivo",
                "setSurge(getParametroBoolean(request, \"surge\"))"
        );

        System.out.println(
                "CONTRATO_COMPRAS_SURGE_SELECT_OBLIGATORIO_OK"
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

    private static void unaVez(
            String contenido,
            String etiqueta,
            String esperado) {

        int primero = contenido.indexOf(esperado);
        int segundo = primero >= 0
                ? contenido.indexOf(esperado, primero + esperado.length())
                : -1;

        if (primero < 0 || segundo >= 0) {
            throw new AssertionError(
                    etiqueta + ": cantidad distinta de uno para ["
                            + esperado + "]"
            );
        }
    }
}
