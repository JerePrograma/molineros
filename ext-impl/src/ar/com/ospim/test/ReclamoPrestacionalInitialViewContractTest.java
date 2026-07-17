package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato textual del selector legacy Tipo de Pedido x Sector. */
public final class ReclamoPrestacionalInitialViewContractTest {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String DIR =
            "ext-web/docroot/html/portlet/autorizaciones/"
                    + "reclamos_prestacionales/";

    private ReclamoPrestacionalInitialViewContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String view = leer(DIR + "view_reclamo.jsp");
        String legacy = leer(DIR + "view_reclamo.js");
        String patch = leer(DIR + "view_reclamo_p0_patch.js");
        String cabecera = leer(DIR + "view_reclamo_cabecera.jspf");
        String prestaciones = leer(DIR + "view_reclamo_prestaciones.jspf");

        contiene(view, "carga del legacy versionada",
                "view_reclamo.js?v=20260717-legacy-flows-1");
        contiene(view, "P0 posterior al legacy",
                "view_reclamo_p0_patch.js?v=20260717-legacy-flows-1");
        antes(view, "view_reclamo.js?v=", "view_reclamo_p0_patch.js?v=");
        noContiene(view, "segunda máquina de estado retirada",
                "view_reclamo_initial_state.js");
        noContiene(view, "snapshot compensatorio retirado",
                "ReclamoPrestacionalBootstrapSnapshot");
        noContiene(view, "API jQuery moderna incompatible", ".on(");
        contiene(view, "submit compatible con jQuery legacy",
                ").submit(normalizarFechasOpcionales)");
        contiene(view, "rebind AJAX compatible con jQuery legacy",
                "jQuery(document).ajaxComplete(function");

        contiene(legacy, "único dueño del selector",
                "function manejarTipoSector(){");
        contiene(legacy, "regla productiva farmacia salvo excepción",
                "return sector == 'FARMACIA' && tipoPedido != 'EXCEPCION';");
        contiene(legacy, "farmacia usa troquel",
                "reclamoPrestacionalNamespace + \"busqueda_farmacia\").show()");
        contiene(legacy, "farmacia oculta código presentado",
                "reclamoPrestacionalNamespace + \"busqueda_prestaciones\").hide()");
        contiene(legacy, "excepción farmacia usa nomenclador 9",
                "sector == 'FARMACIA' && tipoPedido == 'EXCEPCION'");
        contiene(legacy, "discapacidad usa nomenclador 8",
                "sector == 'DISCAPACIDAD'");
        contiene(legacy, "odontología usa nomenclador 1",
                "sector == 'ODONTOLOGIA'");
        contiene(legacy, "tipo pedido delega al selector",
                "function cambioTipoPedido(){");
        contiene(legacy, "selector ejecutado en carga",
                "manejarTipoSector();");

        noContiene(patch, "P0 no sobrescribe sector",
                "window.manejarTipoSector");
        noContiene(patch, "P0 no sobrescribe pedido",
                "window.cambioTipoPedido");
        noContiene(patch, "P0 no duplica matriz", "renderModoSector");
        noContiene(patch, "P0 no restaura snapshot",
                "restaurarSeleccionInicial");

        contiene(cabecera, "tipo pedido mantiene handler legacy",
                "cambioTipoPedido();manejarTipoPedidoCierre();");
        contiene(cabecera, "sector mantiene handler legacy",
                "manejarTipoSector();");
        contiene(prestaciones, "farmacia inicia protegida",
                "busqueda_farmacia\" align=\"left\" width=\"80%\" style=\"display:none;\"");
        contiene(prestaciones, "código presentado inicia protegido",
                "busqueda_prestaciones\" align=\"left\" width=\"80%\" style=\"display:none;\"");

        System.out.println("CONTRATO_RECLAMO_PRESTACIONAL_SELECTOR_LEGACY_OK");
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
