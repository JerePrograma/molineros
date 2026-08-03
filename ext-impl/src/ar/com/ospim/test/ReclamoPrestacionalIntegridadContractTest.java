package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato focalizado de revision, comprobante y ayuda del Reclamo. */
public final class ReclamoPrestacionalIntegridadContractTest {

    private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    private static final String JSP_DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";
    private static final String ACTION =
            "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                    + "ListaRevisionesAction.java";

    private ReclamoPrestacionalIntegridadContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String estructura = leer(JSP_DIR + "view_reclamo.jspf");
        String inicio = leer(JSP_DIR + "view_reclamo_inicio_formulario.jspf");
        String integridad = leer(JSP_DIR + "view_reclamo_integridad_patch.js");
        String action = leer(ACTION);

        contiene(estructura, "carga parche focalizado",
                "view_reclamo_integridad_patch.js?v=20260803-revision-comprobante-ayuda-1");
        contiene(estructura, "conserva alta Compras",
                "boolean neutralizarEdicionInicialCompras");
        noContiene(estructura, "no elimina lista precargada",
                "removeAttribute(\n            WebKeysAutorizaciones.LISTADO_PRESTACIONES");

        contiene(action, "fecha estricta", "setLenient(false)");
        contiene(action, "serializa alta de revision", "synchronized (session)");
        contiene(action, "bloquea revision activa", "tieneRevisionActiva(");
        contiene(action, "informa error funcional", "RevisionesReclamosException.class.getName()");
        noContiene(action, "no vacia lista antes de agregar",
                "removeAttribute(WebKeysAutorizaciones.LISTADO_REVISIONES");

        contiene(integridad, "evita doble envio", "revisionEnCurso");
        contiene(integridad, "espera resultado AJAX", "estado === \"error\"");
        contiene(integridad, "sincroniza lista de revisiones", "hayRevisionActivaEnListado()");
        contiene(integridad, "captura respuesta del editor", "extraerMetadatosEditor(respuesta)");
        contiene(integridad, "restaura letra despues de opciones",
                "letra.children(\"option\").length");
        contiene(integridad, "muestra comprobante en edicion",
                "comprobante.show().attr(\"aria-hidden\", \"false\")");
        contiene(integridad, "retira aviso solo en editor",
                "campo(\"rp_compras_comprobante_info\").remove()");

        noContiene(inicio, "ayuda sin containerPlus", "class=\"containerPlus");
        contiene(inicio, "ayuda namespaced",
                "id=\"<portlet:namespace />helpComprobantes\"");
        contiene(integridad, "intercepta ayuda legacy", "id === \"helpComprobantes\"");

        noContiene(integridad, "sin flechas", "=>");
        noContiene(integridad, "sin optional chaining", "?.");
        noContiene(integridad, "sin fetch", "fetch(");
        noContiene(integridad, "sin let", "let ");
        noContiene(integridad, "sin const", "const ");
        noContiene(integridad, "sin escapes Unicode", "\\u00");

        System.out.println("CONTRATO_RECLAMO_PRESTACIONAL_INTEGRIDAD_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), ISO_8859_1);
    }

    private static void contiene(String contenido, String etiqueta, String esperado) {
        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(etiqueta + ": no se encontro [" + esperado + "]");
        }
    }

    private static void noContiene(String contenido, String etiqueta, String prohibido) {
        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(etiqueta + ": se encontro [" + prohibido + "]");
        }
    }
}
