package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Contrato ejecutable sin Liferay para la integracion minima Compras/RP.
 */
public final class ComprasRequerimientosUiContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String iniciar = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "IniciarReclamoPrestacionalCompraAction.java"
        );
        String editar = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "EditarReclamosEntryAction.java"
        );
        String servicioReclamo = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoPrestacionServiceImpl.java"
        );
        String vista = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );
        String wrapper = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/"
                        + "editar_reclamosprestacionales_entry.jsp"
        );
        String botonera = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_botonera.jsp"
        );
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );
        String struts = leer(
                "ext-web/docroot/WEB-INF/struts-config.xml"
        );

        assertOrden(
                "consulta antes de validar alta",
                iniciar,
                ".obtenerPorRequerimiento(",
                "validarPermisoCreacion("
        );
        assertContains("rama vinculada", iniciar, ".isVinculado()");
        assertContains("modo view", iniciar, "Constants.VIEW");
        assertContains("id RP", iniciar, "PARAM_ID_RECLAMO");
        assertContains(
                "permiso consulta",
                iniciar,
                "validarPermisoConsulta("
        );
        assertContains(
                "bloquea relacion en error",
                iniciar,
                ".isError()"
        );

        int ramaCompra = editar.indexOf("} else {", editar.indexOf(
                "if (contextoCompra == null)"
        ));
        assertTrue("rama ordinaria preservada", ramaCompra > 0);
        assertOrdenDesde(
                "reserva antes de insertar",
                editar,
                ramaCompra,
                ".reservarCreacion(",
                ".insertar(reclamoPrestacional, user)"
        );
        assertOrdenDesde(
                "preserva indicadores antes de insertar",
                editar,
                ramaCompra,
                "reclamoPrestacional.setRecuperable(",
                ".insertar(reclamoPrestacional, user)"
        );
        assertContains(
                "preserva recupero desde contexto",
                editar,
                "contextoCompra.isRecupero()"
        );
        assertContains(
                "preserva surge desde contexto",
                editar,
                "reclamoPrestacional.setSuperintendencia("
        );
        assertContains(
                "preserva tercerizadora desde requerimiento",
                editar,
                "idTercerizadora = requerimiento.getIdTercerizadora();"
        );
        assertOrdenDesde(
                "tercerizadora antes de insertar",
                editar,
                ramaCompra,
                "asignarTercerizadoraAPrestaciones(",
                ".insertar(reclamoPrestacional, user)"
        );
        assertOrdenDesde(
                "insertar antes de finalizar",
                editar,
                ramaCompra,
                ".insertar(reclamoPrestacional, user)",
                ".finalizarCreacion("
        );
        assertOrdenDesde(
                "finalizar antes de limpiar contexto",
                editar,
                ramaCompra,
                ".finalizarCreacion(",
                ".CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA"
        );
        assertContains(
                "compensa luego de insert",
                editar,
                ".marcarErrorPosteriorAlInsert("
        );
        assertContains(
                "libera reserva sin insert",
                editar,
                ".liberarReserva("
        );
        assertContains(
                "propaga origen en rerender",
                editar,
                "setRenderParameter(\"origen\", \"compras\")"
        );
        assertContains(
                "limpia precarga invalida",
                editar,
                "limpiarSesionHandoffCompra(session)"
        );
        assertOrden(
                "rollback se informa al caller",
                servicioReclamo,
                "ConnectionHelper.rollback(con);",
                "throw new SystemException(e);"
        );

        assertContains(
                "guard de handoff",
                vista,
                "boolean handoffReclamoComprasValido"
        );
        assertContains(
                "alta forzada solo con guard",
                vista,
                "request.setAttribute(Constants.CMD, Constants.ADD);"
        );
        assertContains(
                "nonce oculto",
                vista,
                "WebKeysCompras.PARAM_RECLAMO_PRESTACIONAL_NONCE"
        );
        assertContains(
                "handoff invalido falla cerrado",
                vista,
                "request.setAttribute(Constants.CMD, Constants.VIEW);"
        );
        assertOrden(
                "neutraliza editor antes del include",
                vista,
                "WebKeysAutorizaciones.PRESTACION_EN_PROCESO_DE_EDICION",
                "datos_edicion_prestacion.jsp"
        );
        assertContains("boton legacy grabar", vista, "key=\"Grabar\"");
        assertContains(
                "boton legacy actualizar",
                vista,
                "key=\"Actualizar\""
        );
        assertNotContains(
                "sin vista segmentada",
                vista,
                "view_reclamo.jspf"
        );
        assertNotContains(
                "sin artefacto comprimido",
                vista,
                "__JSP_STATIC_NAMESPACE_"
        );

        assertContains(
                "wrapper conserva nonce",
                wrapper,
                "PARAM_RECLAMO_PRESTACIONAL_NONCE"
        );
        assertContains(
                "wrapper conserva origen",
                wrapper,
                "portletURL.setParameter(\"origen\", \"compras\")"
        );
        assertContains(
                "wrapper muestra error",
                wrapper,
                "error-reclamo-compras"
        );
        assertContains(
                "boton crear",
                botonera,
                "Crear Reclamo Prestacional"
        );
        assertContains(
                "boton ver",
                botonera,
                "Ver Reclamo Prestacional"
        );
        assertContains(
                "ruta struts",
                struts,
                "/compras/iniciar_reclamo_prestacional"
        );
        assertContains(
                "reserva SQL",
                schema,
                "compras.reservar_reclamo_prestacional"
        );
        assertContains(
                "finaliza SQL",
                schema,
                "compras.finalizar_reclamo_prestacional"
        );
        assertContains(
                "compensacion SQL",
                schema,
                "compras.marcar_error_reclamo_prestacional"
        );

        System.out.println(
                "CONTRATO_INTEGRACION_COMPRAS_RECLAMO_OK"
        );
    }

    private static String leer(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());

        assertTrue(
                path + " sin BOM UTF-8",
                bytes.length < 3
                        || (bytes[0] & 0xFF) != 0xEF
                        || (bytes[1] & 0xFF) != 0xBB
                        || (bytes[2] & 0xFF) != 0xBF
        );
        assertTrue(
                path + " sin BOM UTF-16",
                bytes.length < 2
                        || !(((bytes[0] & 0xFF) == 0xFF
                                && (bytes[1] & 0xFF) == 0xFE)
                        || ((bytes[0] & 0xFF) == 0xFE
                                && (bytes[1] & 0xFF) == 0xFF))
        );

        String contenido = new String(bytes, LATIN1);
        assertNotContains(
                path + " sin mojibake C3",
                contenido,
                String.valueOf((char) 0x00C3)
        );
        assertNotContains(
                path + " sin mojibake C2",
                contenido,
                String.valueOf((char) 0x00C2)
        );
        assertNotContains(
                path + " sin reemplazo",
                contenido,
                String.valueOf((char) 0xFFFD)
        );
        return contenido;
    }

    private static void assertOrden(
            String nombre,
            String texto,
            String primero,
            String segundo) {

        assertOrdenDesde(nombre, texto, 0, primero, segundo);
    }

    private static void assertOrdenDesde(
            String nombre,
            String texto,
            int desde,
            String primero,
            String segundo) {

        int posPrimero = texto.indexOf(primero, desde);
        int posSegundo = texto.indexOf(segundo, posPrimero + 1);
        assertTrue(
                nombre,
                posPrimero >= desde && posSegundo > posPrimero
        );
    }

    private static void assertContains(
            String nombre,
            String texto,
            String esperado) {

        assertTrue(nombre, texto.indexOf(esperado) >= 0);
    }

    private static void assertNotContains(
            String nombre,
            String texto,
            String prohibido) {

        assertTrue(nombre, texto.indexOf(prohibido) < 0);
    }

    private static void assertTrue(String nombre, boolean condicion) {
        if (!condicion) {
            throw new AssertionError(nombre);
        }
    }

    private ComprasRequerimientosUiContractTest() {
    }
}
