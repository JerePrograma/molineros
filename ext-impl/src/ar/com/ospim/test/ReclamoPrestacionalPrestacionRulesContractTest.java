package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual de acciones de prestación y Recuperable SUR neutral. */
public final class ReclamoPrestacionalPrestacionRulesContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
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
        String action = leer(ACTION);

        contiene(
                view,
                "carga del contrato de prestación",
                "view_reclamo_prestacion_rules_patch.js"
        );
        antes(
                view,
                "view_reclamo_p0_patch.js",
                "view_reclamo_prestacion_rules_patch.js"
        );

        contiene(
                reglas,
                "cancelación namespaced reemplazada",
                "window[namespace + \"cancelaEdicionPrestacion\"]"
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
                "edición envuelta",
                "envolverAccion(\"editarPrestacionSeleccionada\""
        );
        contiene(
                reglas,
                "alta envuelta",
                "envolverAccion(\"agregarPrestacion\")"
        );

        contiene(
                reglas,
                "recuperable enviado neutral",
                "datos.recuperableSur = 0"
        );
        contiene(
                reglas,
                "reconocido SSS enviado neutral",
                "datos.reconocidoSSS = 0"
        );
        contiene(
                reglas,
                "select visible fijado en cero",
                ".val(\"0\")"
        );
        contiene(
                reglas,
                "select bloqueado",
                ".attr(\"disabled\", \"disabled\")"
        );
        contiene(
                reglas,
                "reconocido bloqueado",
                ".attr(\"readonly\", \"readonly\")"
        );

        contiene(
                action,
                "servidor ignora reconocido informado",
                "double reconocidoSSS = 0D;"
        );
        contiene(
                action,
                "servidor fija recuperable cero",
                "Integer recuperable = Integer.valueOf(0);"
        );
        contiene(
                action,
                "prestación editada queda neutral",
                "presta.setReconocidoSSS(0D);"
        );
        contiene(
                action,
                "prestación abierta queda neutral",
                "presta.setRecuperable(Integer.valueOf(0));"
        );
        noContiene(
                action,
                "no se consume Recuperable desde request",
                "ParamUtil.getString(renderRequest, \"recuperableSur\""
        );

        System.out.println(
                "CONTRATO_RECLAMO_PRESTACION_BOTONES_RECUPERABLE_OK"
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