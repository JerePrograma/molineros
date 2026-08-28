package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class ComprasPrestadoresHabilitadosUiContractTest {

    public static void main(String[] args) throws Exception {
        String acciones = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/requerimiento_compra_acciones_componente.jsp"
        );
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "VerPrestadoresHabilitadosCotizacionCompraAction.java"
        );
        String serviceUtil = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "NotificarCotizacionPrestadorServiceUtil.java"
        );
        String jsp = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_compra_prestadores_habilitados.jsp"
        );
        String struts = leer("ext-web/docroot/WEB-INF/struts-config.xml");
        String tiles = leer("ext-web/docroot/WEB-INF/tiles-defs.xml");

        assertContains(
                "boton de consulta",
                acciones,
                "value=\"Ver prestadores habilitados\""
        );
        assertContains(
                "consulta conserva guarda de envio",
                acciones,
                "if (botoneraPuedeEnviarACotizar)"
        );
        assertContains(
                "popup exclusive",
                acciones,
                "LiferayWindowState.EXCLUSIVE"
        );
        assertContains(
                "popup usa ruta focalizada",
                acciones,
                "/compras/ver_prestadores_habilitados_cotizacion"
        );

        assertContains(
                "action exige rol de cotizacion",
                action,
                "WebKeysCompras.ROL_COTIZAR_COMPRAS"
        );
        assertContains(
                "action valida estado vigente",
                action,
                "requerimiento.puedeEnviarACotizar()"
        );
        assertContains(
                "action reutiliza candidatos canonicos",
                action,
                "NotificarCotizacionPrestadorServiceUtil\n"
                        + "                            .listarPrestadoresCandidatos("
        );
        assertContains(
                "service util delega al service existente",
                serviceUtil,
                "getInstance().listarPrestadoresCandidatos("
        );

        assertContains(
                "vista sigue fieldset legacy",
                jsp,
                "<fieldset class=\"block-labels\">"
        );
        assertContains(
                "vista sigue tabla legacy",
                jsp,
                "<table class=\"lfr-table taglib-search-iterator\""
        );
        assertContains(
                "vista informa sector",
                jsp,
                "<strong>Sector:</strong>"
        );
        assertContains(
                "vista informa rubros",
                jsp,
                "Tipos de cotizaci&#243;n / rubros"
        );
        assertContains(
                "vista escapa valores",
                jsp,
                "HtmlUtil.escape("
        );
        assertNotContains(
                "vista no expone email",
                jsp,
                "getEmail"
        );
        assertNotContains(
                "vista no permite seleccion manual",
                jsp,
                "type=\"checkbox\""
        );

        assertContains(
                "struts registra action",
                struts,
                "path=\"/compras/ver_prestadores_habilitados_cotizacion\""
        );
        assertContains(
                "tiles registra vista",
                tiles,
                "requerimiento_compra_prestadores_habilitados.jsp"
        );
    }

    private static String leer(String ruta) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(ruta).toPath());

        return new String(
                bytes,
                Charset.forName("ISO-8859-1")
        );
    }

    private static void assertContains(
            String descripcion,
            String texto,
            String esperado) {

        if (texto.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion
                            + ": no se encontro ["
                            + esperado
                            + "]"
            );
        }
    }

    private static void assertNotContains(
            String descripcion,
            String texto,
            String prohibido) {

        if (texto.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    descripcion
                            + ": se encontro ["
                            + prohibido
                            + "]"
            );
        }
    }
}
