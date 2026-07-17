package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del estado inicial de Nuevo Reclamo Prestacional. */
public final class ReclamoPrestacionalInitialViewContractTest {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";

    private ReclamoPrestacionalInitialViewContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(DIR + "view_reclamo.jsp");
        String initial = leer(DIR + "view_reclamo_initial_state.js");
        String cabecera = leer(DIR + "view_reclamo_cabecera.jspf");
        String afiliado = leer(DIR + "view_reclamo_afiliado_diagnostico.jspf");
        String prestaciones = leer(DIR + "view_reclamo_prestaciones.jspf");
        String seguimiento = leer(DIR + "view_reclamo_seguimiento_cierre.jspf");
        String config = leer(DIR + "view_reclamo_configuracion.jspf");

        contiene(view, "namespace independiente", "window.ReclamoPrestacionalNamespace");
        contiene(view, "capa antes de legacy", "view_reclamo_initial_state.js?v=20260717-initial-state-4");
        antes(view, "view_reclamo_initial_state.js?v=", "view_reclamo.js?v=");
        contiene(view, "diagnóstico asset", "RECLAMO_PRESTACIONAL_ASSET_ERROR");
        contiene(view, "fallback excluye Compras", "if (<%= esBorradorCompras %>)");
        contiene(view, "fallback síncrono nomenclador", "jQuery(\"#\" + namespace + \"busqueda_prestaciones\").show()");

        contiene(initial, "defaults seguros", "config.values = jQuery.extend");
        contiene(initial, "preserva Compras", "if (config.values.esBorradorCompras)");
        contiene(initial, "espera ready legacy", "window.setTimeout(aplicarEstadoInicial, 0)");
        contiene(initial, "selector positivo", "function mostrarBuscadorSegunSeleccion()");
        contiene(initial, "farmacia reintegro", "sector === \"FARMACIA\" && tipoPedido !== \"EXCEPCION\"");
        contiene(initial, "muestra farmacia", "campo(\"busqueda_farmacia\").show()");
        contiene(initial, "muestra nomenclador", "campo(\"busqueda_prestaciones\").show()");
        contiene(initial, "Nuevo usa Código Presentado", "Comportamiento legacy: Nuevo comienza con Código Presentado visible");
        contiene(initial, "binding jQuery legacy sector", "campo(\"sector\").change(actualizarBuscadorPrestacion)");
        contiene(initial, "binding jQuery legacy pedido", "campo(\"tipopedido\").change(actualizarBuscadorPrestacion)");
        contiene(initial, "función namespaced", "window[namespace + \"actualizarBuscadorPrestacion\"]");
        noContiene(initial, "API jQuery moderna incompatible", "jQuery(document).on(");
        contiene(initial, "marca ejecutada", "ReclamoPrestacionalInitialStateOk = true");

        contiene(cabecera, "tipo pedido invoca selector seguro", "actualizarBuscadorPrestacion(); } cambioTipoPedido()");
        contiene(cabecera, "sector invoca selector seguro", "actualizarBuscadorPrestacion(); } manejarTipoSector()");

        contiene(afiliado, "mensaje oculto", "divResultadoActualizarOK\" style=\"display:none;\"");
        contiene(prestaciones, "farmacia protegida", "busqueda_farmacia\" align=\"left\" width=\"80%\" style=\"display:none;\"");
        contiene(prestaciones, "nomenclador protegido", "busqueda_prestaciones\" align=\"left\" width=\"80%\" style=\"display:none;\"");
        contiene(prestaciones, "editor Compras", "esBorradorCompras ? \"\" : \"display:none;\"");
        contiene(prestaciones, "ingreso normal", "esBorradorCompras ? \"display:none;\" : \"\"");
        contiene(prestaciones, "asociadas ocultas", "style=\"display:none; height: 120px;");
        contiene(seguimiento, "CRM oculto", "style=\"display:none; height: 160px;");
        contiene(seguimiento, "cierre persistido", "existeReclamoPersistido && reclamoprestacional.getEstado() == 3");
        contiene(config, "flag Compras", "esBorradorCompras: <%= esBorradorCompras %>");

        System.out.println("CONTRATO_RECLAMO_PRESTACIONAL_VISTA_INICIAL_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), UTF_8);
    }

    private static void contiene(String contenido, String etiqueta, String esperado) {
        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(etiqueta + ": no se encontró [" + esperado + "]");
        }
    }

    private static void noContiene(String contenido, String etiqueta, String prohibido) {
        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(etiqueta + ": se encontró [" + prohibido + "]");
        }
    }

    private static void antes(String contenido, String primero, String segundo) {
        int a = contenido.indexOf(primero);
        int b = contenido.indexOf(segundo);
        if (a < 0 || b < 0 || a >= b) {
            throw new AssertionError("Orden inválido: " + primero + " / " + segundo);
        }
    }
}
