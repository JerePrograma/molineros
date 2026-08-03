package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual de acciones de prestacion y Recuperable SUR. */
public final class ReclamoPrestacionalPrestacionRulesContractTest {

    private static final Charset ISO_8859_1 =
        Charset.forName("ISO-8859-1");
    private static final String JSP_DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";
    private static final String ACTION =
            "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                    + "EditarPrestacionReclamoAction.java";

    private ReclamoPrestacionalPrestacionRulesContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(JSP_DIR + "view_reclamo.jsp");
        String reglas = leer(
                JSP_DIR + "view_reclamo_prestacion_rules_patch.js"
        );
        String compras = leer(
                JSP_DIR + "view_reclamo_compras_surge_patch.js"
        );
        String action = leer(ACTION);
        String estructura = leer(JSP_DIR + "view_reclamo.jspf");
        String neutralizacionServidor = leer(
                JSP_DIR + "view_reclamo_recuperable_neutro.jspf"
        );

        contiene(
                view,
                "carga del contrato de prestacion",
                "view_reclamo_prestacion_rules_patch.js"
                        + "?v=20260803-compras-alta-directa-2"
        );

        contiene(
                view,
                "invalida cache de la seleccion inicial",
                "view_reclamo.js"
                        + "?v=20260803-compras-alta-directa-2"
        );

        contiene(
                reglas,
                "detecta una edicion real",
                "function hayEdicionPrestacionActiva()"
        );
        contiene(
                reglas,
                "exige contenido real en el editor",
                "editor.children().length > 0"
        );
        contiene(
                reglas,
                "exige idRegistro renderizado",
                "idRegistro.length > 0"
        );
        contiene(
                reglas,
                "normaliza solo una edicion activa",
                "if (hayEdicionPrestacionActiva()) {"
        );
        contiene(
                reglas,
                "llamada protegida por el guard",
                "if (hayEdicionPrestacionActiva()) {\n"
                        + "        normalizarPrestacionInicialEnSesion("
        );

        contiene(
                reglas,
                "separacion de botones mediante CSS",
                "botonGuardar.css("
        );
        contiene(
                reglas,
                "margen derecho del boton guardar",
                "\"marginRight\","
        );
        noContiene(
                reglas,
                "sin separador textual anonimo",
                "contenedor.append(\"\\u00a0\\u00a0\")"
        );
        noContiene(
                reglas,
                "sin escapes Unicode en el parche focalizado",
                "\\u00"
        );
        contiene(
                reglas,
                "texto directo de cancelacion",
                "\"Cancelar Edición de la Prestación\""
        );

        contiene(
                view,
                "captura del load nativo",
                "window.ReclamoPrestacionalJQueryLoadOriginal"
        );
        antes(
                view,
                "view_reclamo_p0_patch.js",
                "view_reclamo_prestacion_rules_patch.js"
        );

        contiene(
                estructura,
                "neutralizacion server-side incluida",
                "view_reclamo_recuperable_neutro.jspf"
        );
        antes(
                estructura,
                "view_reclamo_recuperable_neutro.jspf",
                "view_reclamo_prestaciones.jspf"
        );
        contiene(
                estructura,
                "parche de Surge cargado despues de la configuracion",
                "view_reclamo_compras_surge_patch.js"
        );

        contiene(
                neutralizacionServidor,
                "lista de sesion recorrida",
                "LISTADO_PRESTACIONES_RECLAMOS_EN_SESION"
        );
        contiene(
                neutralizacionServidor,
                "prestacion en edicion recorrida",
                "PRESTACION_EN_PROCESO_DE_EDICION"
        );
        contiene(
                neutralizacionServidor,
                "Surge determina Recuperable inicial",
                "contextoCompras.isSurge()"
        );
        contiene(
                neutralizacionServidor,
                "valor inicial expuesto al cliente",
                "recuperable_sur_compra_inicial"
        );
        contiene(
                neutralizacionServidor,
                "reconocido SSS neutral en servidor",
                "prestacionNeutra.setReconocidoSSS("
        );

        contiene(
                reglas,
                "cancelacion namespaced reemplazada",
                "window[namespace + \"cancelaEdicionPrestacion\"]"
        );
        contiene(
                reglas,
                "edicion namespaced protegida",
                "window[nombreEditar] = guardarEdicionSeguro"
        );
        contiene(
                reglas,
                "combo opcional validado",
                "if (combo)"
        );
        antes(
                reglas,
                "if (combo)",
                "combo.selectedIndex = 0"
        );
        contiene(
                reglas,
                "boton seguro de edicion",
                "rp_guardar_prestacion_seguro"
        );
        contiene(
                reglas,
                "boton seguro de cancelacion",
                "rp_cancelar_prestacion_seguro"
        );
        contiene(
                reglas,
                "handler directo de edicion",
                "botonGuardar[0].onclick"
        );
        contiene(
                reglas,
                "handler directo de cancelacion",
                "botonCancelar[0].onclick"
        );
        contiene(
                reglas,
                "guardado usa load nativo",
                "window.ReclamoPrestacionalJQueryLoadOriginal"
        );
        contiene(
                reglas,
                "callback libera bloqueo",
                "guardadoEnCurso = false;"
        );
        noContiene(
                reglas,
                "sin first de jQuery moderno",
                ".find(\"option\").first()"
        );

        contiene(
                compras,
                "Surge se propaga por AJAX",
                "copia.recuperableSur = parseInt(valorActual, 10);"
        );
        contiene(
                compras,
                "valor de contexto se usa al guardar",
                "recuperableSur: parseInt(valorRecuperableActual, 10) || 0"
        );
        contiene(
                compras,
                "selector de alta conserva Surge",
                "alta.removeAttr(\"disabled\").val(valorRecuperableActual)"
        );
        contiene(
                compras,
                "selector de edicion conserva Surge",
                "edicion.removeAttr(\"disabled\").val(valorRecuperableActual)"
        );

        contiene(
                action,
                "servidor consume Recuperable acotado",
                "ParamUtil.getInteger("
        );
        contiene(
                action,
                "parametro Recuperable SUR",
                "\"recuperableSur\""
        );
        contiene(
                action,
                "rango Recuperable validado",
                "recuperableSur < 0 || recuperableSur > 3"
        );
        contiene(
                action,
                "valor Recuperable preservado",
                "Integer recuperable = Integer.valueOf(recuperableSur);"
        );
        contiene(
                action,
                "prestacion editada recibe Recuperable",
                "presta.setRecuperable(recuperable);"
        );
        contiene(
                action,
                "bandera SUR deriva de Recuperable",
                "Boolean.valueOf(recuperable.intValue() == 1)"
        );
        contiene(
                action,
                "Reconocido SSS permanece neutral",
                "presta.setReconocidoSSS(0D);"
        );
        noContiene(
                action,
                "no vuelve a forzar Recuperable cero",
                "Integer recuperable = Integer.valueOf(0);"
        );

        String javascript = reglas + compras;
        noContiene(javascript, "sin funciones flecha", "=>");
        noContiene(javascript, "sin optional chaining", "?.");
        noContiene(javascript, "sin fetch", "fetch(");
        noContiene(javascript, "sin let", "let ");
        noContiene(javascript, "sin const", "const ");

        System.out.println(
                "CONTRATO_RECLAMO_PRESTACION_BOTONES_RECUPERABLE_OK"
        );
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(
        Files.readAllBytes(path),
        ISO_8859_1
);
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
