package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class ComprasPrestadoresHabilitadosUiContractTest {

    public static void main(String[] args) throws Exception {
        String vistaPrincipal = leer(
                "ext-web/docroot/html/portlet/compras/view.jsp"
        );
        String acciones = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/requerimiento_compra_acciones_componente.jsp"
        );
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/action/"
                        + "ViewComprasAction.java"
        );
        String serviceUtil = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "NotificarCotizacionPrestadorServiceUtil.java"
        );
        String serviceImpl = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "NotificarCotizacionPrestadorServiceImpl.java"
        );
        String jsp = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_compra_configuracion_correos.jsp"
        );

        assertContains(
                "pestana principal",
                vistaPrincipal,
                "configuracion-de-correos"
        );
        assertContains(
                "titulo visible de pestana",
                vistaPrincipal,
                "Configuraci\\u00f3n de Correos"
        );
        assertContains(
                "pestana exige rol de cotizacion",
                vistaPrincipal,
                "WebKeysCompras.ROL_COTIZAR_COMPRAS"
        );
        assertContains(
                "pestana incluye vista focalizada",
                vistaPrincipal,
                "requerimiento_compra_configuracion_correos.jsp"
        );
        assertNotContains(
                "boton anterior retirado",
                acciones,
                "Ver prestadores habilitados"
        );
        assertNotContains(
                "popup anterior retirado",
                acciones,
                "abrirPrestadoresHabilitadosCotizacion"
        );

        assertContains(
                "action exige rol de cotizacion",
                action,
                "WebKeysCompras.ROL_COTIZAR_COMPRAS"
        );
        assertContains(
                "action carga catalogo de rubros",
                action,
                ".listarTiposPrestacion()"
        );
        assertContains(
                "action conserva pestana en alcance de aplicacion",
                action,
                "PortletSession.APPLICATION_SCOPE"
        );
        assertContains(
                "action consulta por rubro",
                action,
                ".listarPrestadoresConfiguracionCorreosPorRubro("
        );
        assertContains(
                "service util delega al service existente",
                serviceUtil,
                ".listarPrestadoresConfiguracionCorreosPorRubro("
        );

        assertContains(
                "consulta usa rubro real del prestador",
                serviceImpl,
                "public.prestador_rubro"
        );
        assertContains(
                "consulta normaliza rubro como el envio",
                serviceImpl,
                "compras.normalizar_rubro(pr.rubro) = t.descripcion"
        );
        assertContains(
                "consulta usa emails canonicos",
                serviceImpl,
                "compras.resolver_emails_cotizacion_prestador("
        );
        assertContains(
                "consulta conserva habilitacion",
                serviceImpl,
                "COALESCE(p.solicitar_cotizacion, FALSE) = TRUE"
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
                "vista filtra por rubro",
                jsp,
                ">Rubro:</label>"
        );
        assertContains(
                "vista usa id de tipo de prestacion",
                jsp,
                "id_tipo_prestacion"
        );
        assertContains(
                "vista escapa valores",
                jsp,
                "HtmlUtil.escape("
        );
        assertContains(
                "vista muestra correos configurados",
                jsp,
                "getEmailVisible()"
        );
        assertNotContains(
                "vista no permite seleccion manual",
                jsp,
                "type=\"checkbox\""
        );

        assertNotContains(
                "vista no depende de un requerimiento",
                jsp,
                "id_requerimiento_compra"
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
