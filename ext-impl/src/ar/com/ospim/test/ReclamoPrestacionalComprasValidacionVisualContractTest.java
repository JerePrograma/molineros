package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/** Contrato del borrador Compras, auxiliares ocultos y validacion visual. */
public final class ReclamoPrestacionalComprasValidacionVisualContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";

    private ReclamoPrestacionalComprasValidacionVisualContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Path inicioPath = Paths.get(
                DIR + "view_reclamo_inicio_formulario.jspf"
        );
        Path comprasPath = Paths.get(
                DIR + "view_reclamo_compras_surge_patch.js"
        );
        Path ensambladorPath = Paths.get(
                DIR + "view_reclamo.jspf"
        );
        Path contratoPath = Paths.get(
                "ext-impl/src/ar/com/ospim/test/"
                        + "ReclamoPrestacionalComprasValidacionVisualContractTest.java"
        );

        String inicio = leer(inicioPath);
        String compras = leer(comprasPath);
        String ensamblador = leer(ensambladorPath);

        contiene(
                inicio,
                "resumen de validacion visible",
                "id=\"<portlet:namespace />reclamo_validacion_resumen\""
        );
        contiene(
                inicio,
                "lista de errores visible",
                "id=\"<portlet:namespace />reclamo_validacion_lista\""
        );
        contiene(inicio, "estilo de campo invalido", ".rp-campo-error");
        contiene(
                inicio,
                "draft renderizado oculto",
                "type=\"hidden\"\n\t\tid=\"<portlet:namespace />reclamoDraftId\""
        );
        contiene(
                inicio,
                "plan bloqueado renderizado oculto",
                "id=\"<portlet:namespace />plan_reclamo_bloqueado\""
        );
        contiene(
                inicio,
                "nombre de plan renderizado oculto",
                "id=\"<portlet:namespace />nombre_plan_reclamo_bloqueado\""
        );
        contiene(
                inicio,
                "accion de prestacion permanece oculta",
                "type=\"hidden\" id=\"<portlet:namespace />tipoaccionprestacion\""
        );

        contiene(
                ensamblador,
                "parche Compras cargado en el ensamblador",
                "view_reclamo_compras_surge_patch.js"
                        + "?v=20260723-restaura-precarga-1"
        );
        noContiene(
                ensamblador,
                "ensamblador sin logica duplicada de guardado",
                "ReclamoPrestacionalComprasGuardadoFinal"
        );

        contiene(
                compras,
                "fallback para jQuery sin ajaxPrefilter",
                "jQuery.ajaxPrefilter = function(prefiltro)"
        );
        contiene(
                compras,
                "fallback instalado antes de tab guard",
                "instalarAjaxPrefilterLegacy();"
        );
        contiene(
                compras,
                "input oculto creado antes de insertarlo",
                "reemplazo = document.createElement(\"input\")"
        );
        contiene(
                compras,
                "tipo oculto asignado antes de reemplazar",
                "reemplazo.type = \"hidden\""
        );
        noContiene(
                compras,
                "no cambia type mediante attr legacy",
                ".attr(\"type\", \"hidden\")"
        );
        contiene(
                compras,
                "normaliza draft auxiliar",
                "reemplazarPorHiddenSeguro(\"reclamoDraftId\")"
        );
        contiene(
                compras,
                "normaliza accion auxiliar",
                "reemplazarPorHiddenSeguro(\"tipoaccionprestacion\")"
        );
        contiene(
                compras,
                "normaliza plan auxiliar",
                "reemplazarPorHiddenSeguro(\"plan_reclamo_bloqueado\")"
        );
        contiene(
                compras,
                "normaliza nombre plan auxiliar",
                "reemplazarPorHiddenSeguro(\"nombre_plan_reclamo_bloqueado\")"
        );

        contiene(
                compras,
                "datos documentales ocultos solo para Compras",
                "var comprobante = campo(\"datos_comprobante\")"
        );
        contiene(
                compras,
                "comprobante temporal OTR",
                "campo(\"comprobante_tipo_edicion\").val(\"OTR\")"
        );
        contiene(
                compras,
                "explica que cotizacion no es factura",
                "La cotizacion de Compras no es una factura."
        );
        contiene(
                compras,
                "guardado principal con resumen visual",
                "envolverGuardado(namespace + \"saveReclamo\")"
        );
        contiene(
                compras,
                "scroll al primer error",
                "window.scrollTo(0, Math.max(0, posicion.top - 100))"
        );
        contiene(
                compras,
                "foco al primer error",
                "primero.focus()"
        );

        contiene(
                compras,
                "API final exclusiva de Compras",
                "window.ReclamoPrestacionalComprasGuardadoFinal"
        );
        contiene(
                compras,
                "fecha de prestacion usa sufijo Edicion",
                "fechaAusente(\"fechaPrestacion\", \"Edicion\")"
        );
        contiene(
                compras,
                "fecha de prestacion usa id real de dia",
                "valor(\"fechaPrestacionDiaEdicion\")"
        );
        contiene(
                compras,
                "fecha de prestacion usa id real de mes",
                "valor(\"fechaPrestacionMesEdicion\")"
        );
        contiene(
                compras,
                "fecha de prestacion usa id real de anio",
                "valor(\"fechaPrestacionAnioEdicion\")"
        );
        contiene(
                compras,
                "rechaza referencia temporal ART",
                "codigo.indexOf(\"ART-\") === 0"
        );
        contiene(
                compras,
                "guarda como OTR",
                "cpbte_tipo: \"OTR\""
        );
        contiene(
                compras,
                "no exige sucursal documental",
                "cpbte_cuit_sucursal: \"\""
        );
        contiene(
                compras,
                "no exige fecha documental",
                "cpbte_anio: \"\""
        );
        contiene(
                compras,
                "usa cargador nativo para guardar",
                "window.ReclamoPrestacionalJQueryLoadOriginal"
        );
        contiene(
                compras,
                "reemplaza la accion legacy de editar",
                "window[namespace + \"editarPrestacionSeleccionada\"]"
        );
        contiene(
                compras,
                "recuperable sincronizado antes del envio",
                "sincronizarAliasRecuperable();"
        );
        contiene(
                compras,
                "contrato historico de Surge preservado",
                "copia.recuperableSur = parseInt(valorActual, 10);"
        );

        String javascript = compras + ensamblador;
        noContiene(javascript, "sin funciones flecha", "=>");
        noContiene(javascript, "sin optional chaining", "?.");
        noContiene(javascript, "sin fetch", "fetch(");
        noContiene(javascript, "sin let", "let ");
        noContiene(javascript, "sin const", "const ");
        noContiene(javascript, "sin jQuery.on", ".on(");

        verificaCodificacion(inicioPath);
        verificaCodificacion(comprasPath);
        verificaCodificacion(ensambladorPath);
        verificaCodificacion(contratoPath);

        System.out.println(
                "CONTRATO_RECLAMO_COMPRAS_VALIDACION_VISUAL_OK"
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
