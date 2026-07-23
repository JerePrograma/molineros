package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/** Contrato focalizado de compatibilidad legacy y ajustes Compras/Reclamo. */
public final class ReclamoPrestacionalRuntimeFixContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String RECLAMOS_DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";

    private ReclamoPrestacionalRuntimeFixContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Path compatPath = Paths.get(
                RECLAMOS_DIR + "view_reclamo_legacy_compat_patch.js"
        );
        Path viewPath = Paths.get(RECLAMOS_DIR + "view_reclamo.jspf");
        Path validatorPath = Paths.get(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "ValidarReclamoAfiliadoPrestaciones.java"
        );
        Path detallePath = Paths.get(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_tabla.jsp"
        );
        Path adjuntosPath = Paths.get(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_adjuntos.jsp"
        );
        Path contratoPath = Paths.get(
                "ext-impl/src/ar/com/ospim/test/"
                        + "ReclamoPrestacionalRuntimeFixContractTest.java"
        );

        String compat = leer(compatPath);
        String view = leer(viewPath);
        String validator = leer(validatorPath);
        String detalle = leer(detallePath);
        String adjuntos = leer(adjuntosPath);

        contiene(
                compat,
                "fallback de prop para jQuery legacy",
                "jQuery.fn.prop = propCompatible"
        );
        contiene(
                compat,
                "setter booleano compatible",
                "jQuery(this).attr(nombre, nombre)"
        );
        contiene(
                compat,
                "offset solo para elementos DOM",
                "this[0].nodeType !== 1"
        );
        contiene(
                compat,
                "integracion no recuperable de Compras",
                "texto === \"NO RECUPERABLE\""
        );
        contiene(
                compat,
                "compatibilidad con descripcion historica",
                "texto === \"NO ES RECUPERABLE\""
        );
        contiene(
                compat,
                "oculta buscador general duplicado",
                "campo(\"busqueda_prestaciones\")"
        );
        contiene(
                compat,
                "oculta buscador de farmacia duplicado",
                "campo(\"busqueda_farmacia\")"
        );

        antes(
                view,
                "view_reclamo_legacy_compat_patch.js?v=20260723-runtime-1",
                "view_reclamo_compras_surge_patch.js"
        );

        contiene(
                validator,
                "parseo estricto de fecha de baja",
                "sdf.setLenient(false);"
        );
        contiene(
                validator,
                "lista de prestaciones opcional",
                "bajaFecha !=null && prestaciones != null"
        );
        contiene(
                validator,
                "prestacion y fecha protegidas",
                "p == null || p.getFechaPrestacion() == null"
        );
        contiene(
                validator,
                "contrato JSON conservado",
                "\\\"codError\\\""
        );

        contiene(
                detalle,
                "mensaje funcional sin cotizaciones",
                "No hay cotizaciones cargadas para poder seleccionar "
                        + "un prestador adjudicado"
        );
        noContiene(
                detalle,
                "mensaje tecnico anterior eliminado",
                "No hay prestadores notificados correctamente con un archivo"
        );

        contiene(
                adjuntos,
                "titulo solicitado del group box",
                "leyenda.text(\"Pedidos de presupuestos\")"
        );
        contiene(
                adjuntos,
                "selector acotado al formulario de presupuestos",
                "compra_presupuesto_fm"
        );

        String javascript = compat + adjuntos;
        noContiene(javascript, "sin funciones flecha", "=>");
        noContiene(javascript, "sin optional chaining", "?.");
        noContiene(javascript, "sin fetch", "fetch(");
        noContiene(javascript, "sin let", "let ");
        noContiene(javascript, "sin const", "const ");
        noContiene(javascript, "sin jQuery.on", ".on(");

        verificaCodificacion(compatPath);
        verificaCodificacion(viewPath);
        verificaCodificacion(validatorPath);
        verificaCodificacion(detallePath);
        verificaCodificacion(adjuntosPath);
        verificaCodificacion(contratoPath);

        System.out.println(
                "CONTRATO_RECLAMO_PRESTACIONAL_RUNTIME_FIX_OK"
        );
    }

    private static String leer(Path path) throws Exception {
        return new String(Files.readAllBytes(path), ISO_8859_1);
    }

    private static void verificaCodificacion(Path path) throws Exception {
        byte[] bytes = Files.readAllBytes(path);
        String texto = new String(bytes, ISO_8859_1);

        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF) {

            throw new AssertionError("BOM no permitido: " + path);
        }

        if (!Arrays.equals(bytes, texto.getBytes(ISO_8859_1))) {
            throw new AssertionError(
                    "No hace round-trip ISO-8859-1: " + path
            );
        }

        noContiene(texto, "sin mojibake A tilde", "\u00C3");
        noContiene(texto, "sin mojibake A circunflejo", "\u00C2");
        noContiene(texto, "sin reemplazo Unicode", "\uFFFD");

        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0x80) != 0) {
                throw new AssertionError(
                        "Archivo no ASCII/ISO-8859-1: "
                                + path
                                + " byte="
                                + i
                );
            }
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
