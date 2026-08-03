package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato focalizado de edición, forwards EXCLUSIVE y handoff desde Compras. */
public final class ReclamoPrestacionalLegacyFlowContractTest {
    private static final Charset ISO_8859_1 =
        Charset.forName("ISO-8859-1");

    private ReclamoPrestacionalLegacyFlowContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String lista = leer("ext-web/docroot/html/portlet/autorizaciones/"
                + "reclamos_prestacionales/lista_prestaciones_reclamos.jsp");
        String action = leer("ext-impl/src/ar/com/ospim/autorizaciones/action/"
                + "EditarPrestacionReclamoAction.java");
        String struts = leer("ext-web/docroot/WEB-INF/struts-config.xml");
        String tiles = leer("ext-web/docroot/WEB-INF/tiles-defs.xml");
        String compras = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                + "IniciarReclamoPrestacionalCompraAction.java");
        String contexto = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                + "ReclamoPrestacionalCompraContexto.java");
        String view = leer("ext-web/docroot/html/portlet/autorizaciones/"
                + "reclamos_prestacionales/view_reclamo.jsp");
        String jspContexto = leer("ext-web/docroot/html/portlet/autorizaciones/"
                + "reclamos_prestacionales/view_reclamo_contexto.jspf");
        String inicio = leer("ext-web/docroot/html/portlet/autorizaciones/"
                + "reclamos_prestacionales/view_reclamo_inicio_formulario.jspf");
        String acciones = leer("ext-web/docroot/html/portlet/autorizaciones/"
                + "reclamos_prestacionales/view_reclamo_acciones.jspf");
        String editor = leer("ext-web/docroot/html/portlet/autorizaciones/"
                + "reclamos_prestacionales/datos_edicion_prestacion.jsp");

        contiene(lista, "edición usa estado EXCLUSIVE",
                "windowState=\"<%=LiferayWindowState.EXCLUSIVE.toString()%>\"");
        contiene(lista, "edición invoca Action original",
                "struts_action=/autorizaciones/editar_reclamosprestaciones");
        contiene(lista, "edición conserva id y tipo",
                "&idRegistro='+idRegistro +'&tipoEdicion='+tipoEdicion");
        contiene(lista, "editor se carga en contenedor legacy",
                ").load(url, function(){");
        contiene(lista, "autorización usa mismo circuito",
                "editarPrestacion(idRegistro,0,tipoAccion)");

        contiene(action, "sesión de prestación en edición",
                "PRESTACION_EN_PROCESO_DE_EDICION");
        contiene(action, "forward editor",
                "portlet.autorizaciones.reclamosprestacionales.edicion_prestacion_reclamo");
        contiene(action, "forward lista normal",
                "portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo");
        contiene(action, "forward lista seccional",
                "portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo_seccional");

        contiene(struts, "mapping de editor",
                "path=\"/autorizaciones/editar_reclamosprestaciones\"");
        contiene(struts, "forward Struts del editor",
                "name=\"portlet.autorizaciones.reclamosprestacionales.edicion_prestacion_reclamo\"");
        contiene(tiles, "Tile del editor",
                "value=\"/portlet/autorizaciones/reclamos_prestacionales/datos_edicion_prestacion.jsp\"");
        contiene(tiles, "Tile de lista",
                "value=\"/portlet/autorizaciones/reclamos_prestacionales/lista_prestaciones_reclamos.jsp\"");

        contiene(compras, "origen explícito", "ORIGEN_COMPRAS =");
        contiene(compras, "nonce aleatorio", "UUID.randomUUID().toString()");
        contiene(compras, "alta nueva", "Constants.ADD");
        contiene(compras, "nonce enviado a Autorizaciones",
                "PARAM_RECLAMO_PRESTACIONAL_NONCE");
        contiene(compras, "no sustituye borrador normal",
                "Existe un Reclamo Prestacional en edición iniciado");
        contiene(compras, "limpieza de handoff fallido", "limpiarHandoffFallido");
        contiene(contexto, "nonce comparado", "coincideNonce");
        contiene(contexto, "usuario comparado", "perteneceAUsuario");
        contiene(contexto, "vigencia comparada", "estaVigente");

        contiene(view, "guard de alta usa parámetro ADD",
                "Constants.ADD.equalsIgnoreCase(cmdParametroCompras)");
        contiene(view, "guard de alta exige origen Compras",
                "\"compras\".equalsIgnoreCase(origenReclamoCompras)");
        contiene(view, "guard de alta valida nonce",
                "contextoReclamoCompras.coincideNonce(nonceReclamoCompras)");
        contiene(view, "guard de alta valida usuario",
                "contextoReclamoCompras.perteneceAUsuario");
        contiene(view, "guard de alta valida vigencia",
                "contextoReclamoCompras.estaVigente");
        contiene(view, "guard de alta normaliza CMD",
                "request.setAttribute(\n            Constants.CMD,\n            Constants.ADD");
        antes(view, "request.setAttribute(", "view_reclamo.jspf");

        contiene(jspContexto, "JSP valida nonce", "contextoCompras.coincideNonce(nonceCompras)");
        contiene(jspContexto, "JSP valida usuario", "contextoCompras.perteneceAUsuario");
        contiene(jspContexto, "JSP valida vigencia", "contextoCompras.estaVigente");
        contiene(inicio, "nonce preservado al guardar",
                "PARAM_RECLAMO_PRESTACIONAL_NONCE");
        contiene(acciones, "alta renderiza Grabar", "<%if(esAlta){ %>");
        contiene(acciones, "Grabar conserva handler legacy", "saveReclamo();");
        contiene(acciones, "edición persistida renderiza Actualizar",
                "<%if(esEdicionPersistida) {%>");
        contiene(acciones, "Actualizar conserva handler legacy",
                "editaReclamo(false);");

        noContiene(editor, "API ausente en Liferay 5.2", "HtmlUtil.escapeJS");
        contiene(editor, "wrapper de prestación balanceable",
                "if (prestacionEnEdicion != null) {");

        System.out.println("CONTRATO_RECLAMO_PRESTACIONAL_FLUJOS_LEGACY_OK");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(
        Files.readAllBytes(path),
        ISO_8859_1
);
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
        int posicionPrimero = contenido.indexOf(primero);
        int posicionSegundo = contenido.indexOf(segundo);
        if (posicionPrimero < 0
                || posicionSegundo < 0
                || posicionPrimero >= posicionSegundo) {
            throw new AssertionError(
                    "Orden inválido entre [" + primero + "] y [" + segundo + "]"
            );
        }
    }
}
