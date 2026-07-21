package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato del presupuesto unico por prestador y su estado real. */
public final class ComprasPresupuestoPrestadorContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String BASE = "ext-impl/src/ar/com/ospim/compras/";

    private ComprasPresupuestoPrestadorContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String schema = leer(BASE + "sql/compras_schema.sql");
        String action = leer(
                BASE + "requerimientos/action/"
                        + "UploadPresupuestosComprasAction.java"
        );
        String jsp = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_adjuntos.jsp"
        );
        String webKeys = leer(BASE + "WebKeysCompras.java");
        String searchImpl = leer(
                BASE + "requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceImpl.java"
        );
        String searchUtil = leer(
                BASE + "requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceUtil.java"
        );
        String editUtil = leer(
                BASE + "requerimientos/service/"
                        + "EditarRequerimientoCompraServiceUtil.java"
        );

        contiene(schema, "estado persistible", "'COTIZADO',");
        contiene(
                schema,
                "indice unico parcial",
                "CREATE UNIQUE INDEX "
                        + "ux_compras_presupuesto_requerimiento_prestador_activo"
        );
        contiene(schema, "bloqueo pesimista", "FOR UPDATE");
        contiene(schema, "alta cotizada", "SET estado_envio = 'COTIZADO'");
        contiene(schema, "baja enviada", "SET estado_envio = 'ENVIADO'");
        contiene(
                schema,
                "unicidad en base",
                "El prestador ya tiene un presupuesto activo"
        );

        contiene(
                webKeys,
                "constante Java",
                "ENVIO_COTIZADO = \"COTIZADO\""
        );
        contiene(
                searchImpl,
                "consulta de estados",
                "rcp.estado_envio IN (?, ?)"
        );
        contiene(
                searchImpl,
                "parametro cotizado",
                "stmt.setString(2, WebKeysCompras.ENVIO_COTIZADO)"
        );
        contiene(action, "control servidor", "prestadoresSeleccionados");
        contiene(action, "mensaje servidor", "un archivo por prestador");
        contiene(jsp, "selector disponible", "prestadoresDisponiblesPresupuestos");
        contiene(
                jsp,
                "selector enviado",
                "WebKeysCompras.ENVIO_ENVIADO.equals"
        );
        contiene(jsp, "maximo dinamico", "maxPresupuestosCargaActual");
        contiene(
                jsp,
                "control cliente",
                "prestadoresSeleccionados[prestador]"
        );
        noContiene(
                searchUtil,
                "sin proyeccion visual",
                "PrestadorCotizacionConPresupuesto"
        );
        noContiene(
                editUtil,
                "sin synchronized local",
                "synchronized int registrarPresupuesto"
        );

        Path proyeccion = Paths.get(
                BASE + "requerimientos/beans/"
                        + "PrestadorCotizacionConPresupuesto.java"
        );
        if (Files.exists(proyeccion)) {
            throw new AssertionError("La proyeccion visual no debe existir.");
        }

        System.out.println("CONTRATO_COMPRAS_PRESUPUESTO_PRESTADOR_OK");
    }

    private static String leer(String ruta) throws Exception {
        return new String(
                Files.readAllBytes(Paths.get(ruta)),
                UTF_8
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
}
