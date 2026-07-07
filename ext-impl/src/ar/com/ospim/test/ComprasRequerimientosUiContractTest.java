package ar.com.ospim.test;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;

/**
 * Contrato ejecutable sin dependencias de Liferay. Verifica la integración
 * textual de JSP, Java y configuración que define los requerimientos de QA.
 */
public final class ComprasRequerimientosUiContractTest {

    public static void main(String[] args) throws Exception {
        assertFiltroPrestadoresEsCheckbox();
        assertPrestadorAdjudicadoEsUnico();
        assertPrecioCotizacionNoConcatenaAtributos();
        assertMensajesYPresupuestos();
        assertNotificacionesFailClosed();
        assertCorreoYDestinatarioTemporal();
        assertComponenteAfiliadoCompartido();
        assertEditarSoloEnListado();
        assertEstadosYBotones();
        assertPersistenciaComprasUsaFunciones();
        assertReclamoPrestacionalUsaHandoffSeguro();
        System.out.println("CONTRATO_UI_COMPRAS_OK");
    }

    private static void assertFiltroPrestadoresEsCheckbox() throws Exception {
        String jsp = leer("ext-web/docroot/html/portlet/prestadores/busqueda_prestadores.jsp");
        String action = leer("ext-impl/src/ar/com/ospim/prestadores/action/BuscarPrestadoresAction.java");
        assertContains("checkbox habilitados", jsp, "type=\"checkbox\"");
        assertContains("valor marcado", jsp, "? 'true'");
        assertNotContains("selector triestado", jsp, "<select id=\"<portlet:namespace />solicitar_cotizacion_filtro\"");
        assertContains("backend booleano", action, "boolean soloHabilitadosCotizar");
        assertContains("ParamUtil booleano", action, "ParamUtil.getBoolean");
        assertNotContains("semantica deshabilitados", action, "getSolicitarCotizacionFiltro");
    }

    private static void assertPrestadorAdjudicadoEsUnico() throws Exception {
        String tabla = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_tabla.jsp");
        String comunes = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_comunes.jsp");
        String editable = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_editable.jsp");
        String service = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/service/EditarRequerimientoCompraServiceImpl.java");
        assertContains("selector global", tabla, "id_prestador_adjudicado");
        assertNotContains("aplicar a todos", comunes, "Aplicar a todos");
        assertNotContains("selector por detalle", comunes, "detalle_id_prestador_");
        assertContains("parametro unico", editable, "PARAM_ID_PRESTADOR_ADJUDICADO");
        assertContains("backend exige unico", service, "obtenerPrestadorAdjudicadoUnico");
        assertContains(
                "backend envía prestador único",
                service,
                "idPrestadorAdjudicado"
        );
        assertContains(
                "backend delega cotización atómica",
                service,
                "compras.guardar_cotizacion_requerimiento"
        );
    }

    private static void assertPrecioCotizacionNoConcatenaAtributos()
            throws Exception {

        String comunes = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_comunes.jsp"
        );

        assertNotContains(
                "precio no concatenado en value",
                comunes,
                "value=\"' + <portlet:namespace />detalleEscapeHtml(detalle.precioUnitario)"
        );
        assertContains("input creado por DOM", comunes, "jQuery('<input/>', {");
        assertContains("precio asignado con val", comunes, "precioInput.val(");
        assertContains("eventos enlazados", comunes, "input.bind('keyup change'");
    }

    private static void assertMensajesYPresupuestos() throws Exception {
        String mensajes = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_mensajes.jsp");
        String adjuntos = leer("ext-web/docroot/html/portlet/compras/requerimientos/requerimiento_adjuntos.jsp");
        assertNotContains("mensaje azul eliminado", mensajes,
                "La estructura del requerimiento solo puede editarse en estado PENDIENTE.");
        assertNotContains(
                "mensaje verde detalle eliminado",
                mensajes,
                "Detalle del requerimiento guardado correctamente."
        );
        assertContains("accion borrar", adjuntos, "value=\"Borrar\"");
        assertBefore("orden Subir/Agregar", adjuntos,
                "value=\"Subir\"", "value=\"Agregar otro presupuesto\"");
    }

    private static void assertNotificacionesFailClosed() throws Exception {
        String botonera = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_botonera.jsp");
        String action = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/action/EditarRequerimientoCompraAction.java");
        String viewAction = leer(
                "ext-impl/src/ar/com/ospim/compras/"
                        + "requerimientos/action/"
                        + "VerRequerimientoCompraAction.java"
        );
        String upload = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/action/UploadPresupuestosComprasAction.java");
        String service = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/service/EditarRequerimientoCompraServiceImpl.java");
        assertContains("boton fail closed", botonera, "Boolean.TRUE.equals");
        assertNotContains("fallback fail open", botonera,
                ": req != null && req.puedeReintentarNotificaciones();");
        assertContains("action oculta ante error", action, "El botón permanecerá oculto.");
        assertContains(
                "vista consulta pendientes",
                viewAction,
                "hayPrestadoresPendientesNotificacion"
        );
        assertContains(
                "vista carga atributo pendientes",
                viewAction,
                "HAY_PRESTADORES_PENDIENTES_NOTIFICACION"
        );
        assertContains("upload oculta ante error", upload, "El botón permanecerá oculto.");
        assertContains("reintento idempotente", service,
                "hayPrestadoresPendientesNotificacion");
        assertContains("reintento no-op", service,
                "return new NotificacionCotizacionResultado();");
    }

    private static void assertCorreoYDestinatarioTemporal() throws Exception {
        String mail = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/service/NotificarCotizacionPrestadorServiceImpl.java");
        String helper = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/service/CotizacionPrestadorMailHelper.java");
        String pdfServlet = leer(
                "ext-impl/src/ar/com/ospim/servlets/"
                        + "PdfServlet.java"
        );
        assertContains("descripcion", mail, " | Descripción: ");
        assertNotContains("obs anterior", mail, " | Obs: ");
        assertContains("modo temporal explicito", mail, "USAR_EMAIL_DESTINO_TEMPORAL = true");
        assertContains("destinatario QA", mail, "acomas@ospim.org.ar");
        assertContains("reserva canonica", mail, "registrar_cotizacion_prestador");
        assertContains("reserva procesando", mail, "AND estado_envio = 'PROCESANDO'");
        assertContains("estado enviado", mail, "WebKeysCompras.ENVIO_ENVIADO");
        assertContains("email real obligatorio", mail,
                "Email real reservado del prestador inválido.");
        assertBefore("validacion real antes de redireccion", mail,
                "String emailReservadoNormalizado", "String emailDestino");
        assertNotContains("destino fuera de logs de servicio", mail,
                "\", emailDestino=\"");
        assertNotContains("destino fuera de logs SMTP", helper,
                "emailDestino=");
        assertNotContains("usuario fuera de logs SMTP", helper,
                "usuarioSmtp=");
        assertNotContains("remitente fuera de logs SMTP", helper,
                "remitente=");
        assertContains(
                "correo recibe PDF",
                mail,
                "pedidoPresupuestoPdf"
        );
        assertContains(
                "correo multipart",
                helper,
                "MimeMultipart"
        );
        assertContains(
                "adjunto application pdf",
                helper,
                "application/pdf"
        );
        assertContains(
                "generador PDF adjunto",
                pdfServlet,
                "crearRequerimientoCompraComoAdjunto"
        );
    }

    private static void assertComponenteAfiliadoCompartido() throws Exception {
        String edicion = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_layout_edicion.jsp");
        String vista = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_layout_vista.jsp");
        String action = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/action/EditarRequerimientoCompraAction.java");
        assertContains("afiliado edicion", edicion, "_afiliado_editable.jsp");
        assertContains("afiliado vista", vista, "_afiliado_editable.jsp");
        assertContains("carga afiliado", action, "cargarAfiliadoRequerimiento");
        assertContains("atributo afiliado", action, "AFILIADO_REQUERIMIENTO_COMPRA");
    }

    private static void assertEditarSoloEnListado() throws Exception {
        String botonera = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_botonera.jsp");
        String acciones = leer("ext-web/docroot/html/portlet/compras/requerimientos/editar_borrar_requerimiento.jsp");
        assertNotContains("editar no aparece en vista", botonera, "value=\"Editar\"");
        assertContains("lapiz en listado", acciones, "image=\"edit\"");
        assertContains("ruta editar", acciones, "/compras/editar_requerimiento");
        assertContains("papelera", acciones, "icon-delete");
        assertContains(
                "A COTIZAR consulta rol cotizar",
                acciones,
                "showCotizarButtons"
        );
        assertContains(
                "A COTIZAR consulta edición de cotización",
                acciones,
                "req.puedeEditarCotizacion()"
        );
        assertNotContains(
                "ABM no habilita cotización",
                acciones,
                "(showABMButtons || showCotizarButtons)"
        );
    }

    private static void assertEstadosYBotones() throws Exception {
        String keys = leer("ext-impl/src/ar/com/ospim/compras/WebKeysCompras.java");
        String botonera = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_botonera.jsp");
        assertContains("PENDIENTE", keys, "return \"PENDIENTE\"");
        assertContains("A COTIZAR", keys, "return \"A COTIZAR\"");
        assertContains("COTIZADO", keys, "return \"COTIZADO\"");
        assertContains("RECLAMO RP", keys, "return \"RECLAMO (RP)\"");
        assertContains("ORDEN declarada", keys, "return \"ORDEN DE COMPRA\"");
        assertContains("A Cotizar solo pendiente", botonera, "puedeEnviarACotizar");
        assertContains("notificar condicionado", botonera, "puedeReintentarNotificaciones");
    }

    private static void assertPersistenciaComprasUsaFunciones()
            throws Exception {

        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/"
                        + "requerimientos/service/"
                        + "EditarRequerimientoCompraServiceImpl.java"
        );

        assertNotContains(
                "sin PreparedStatement",
                service,
                "prepareStatement("
        );

        assertNotContains(
                "sin SELECT nativo",
                service,
                "\"SELECT "
        );

        assertNotContains(
                "sin UPDATE nativo",
                service,
                "\"UPDATE "
        );

        assertNotContains(
                "sin INSERT nativo",
                service,
                "\"INSERT "
        );

        assertNotContains(
                "sin DELETE nativo",
                service,
                "\"DELETE "
        );

        assertContains(
                "confirmación mediante función",
                service,
                "compras.confirmar_envio_a_cotizar"
        );

        assertContains(
                "cotización mediante función",
                service,
                "compras.guardar_cotizacion_requerimiento"
        );

        assertContains(
                "artículos mediante refcursor",
                service,
                "compras.listar_articulos_cursor"
        );

        assertContains(
                "artículo individual mediante refcursor",
                service,
                "compras.get_articulo_cursor"
        );
    }
    private static void assertReclamoPrestacionalUsaHandoffSeguro()
            throws Exception {

        String botonera = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/_botonera.jsp"
        );
        String struts = leer(
                "ext-web/docroot/WEB-INF/struts-config.xml"
        );
        String actionCompra = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "IniciarReclamoPrestacionalCompraAction.java"
        );
        String actionReclamo = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "EditarReclamosEntryAction.java"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "RequerimientoCompraReclamoPrestacionalServiceImpl.java"
        );
        String migration = leer(
                "sql/compras/"
                        + "20260625_requerimiento_reclamo_prestacional.sql"
        );
        String viewReclamo = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );
        String viewReclamoJspf = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jspf"
        );

        assertContains(
                "boton RP",
                botonera,
                "Crear Reclamo Prestacional"
        );
        assertContains(
                "boton ver RP",
                botonera,
                "Ver Reclamo Prestacional"
        );
        assertContains(
                "solo cotizado",
                botonera,
                "WebKeysCompras.esCotizado"
        );
        assertContains(
                "handoff backend",
                botonera,
                "/compras/iniciar_reclamo_prestacional"
        );
        assertNotContains(
                "sin salto directo desde JSP",
                botonera,
                "/autorizaciones/editar_reclamosprestaciones_entry"
        );
        assertContains(
                "mapping handoff",
                struts,
                "path=\"/compras/iniciar_reclamo_prestacional\""
        );
        assertContains(
                "action handoff",
                struts,
                "IniciarReclamoPrestacionalCompraAction"
        );
        assertContains(
                "revalida cotizado",
                actionCompra,
                "El Reclamo Prestacional sólo puede iniciarse"
        );
        assertContains(
                "rol reclamo backend",
                actionCompra,
                "ROL_ABM_RECLAM_PREST"
        );
        assertContains(
                "reserva antes del insert",
                actionReclamo,
                ".reservarCreacion("
        );
        assertBefore(
                "reserva antes de insertar",
                actionReclamo,
                ".reservarCreacion(",
                "ReclamosPrestacionesServiceUtil.insertar("
        );
        assertContains(
                "finaliza vinculo",
                actionReclamo,
                ".finalizarCreacion("
        );
        assertContains(
                "nonce invalido no degrada a alta generica",
                actionReclamo,
                "nunca puede degradarse"
        );
        assertContains(
                "contexto con vencimiento",
                actionReclamo,
                ".estaVigente(System.currentTimeMillis())"
        );
        assertContains(
                "nonce en formulario",
                viewReclamoJspf,
                "PARAM_RECLAMO_PRESTACIONAL_NONCE"
        );
        assertContains(
                "vista importa contexto Compras",
                viewReclamo,
                "ReclamoPrestacionalCompraContexto"
        );
        assertContains(
                "bean temporal no implica reclamo persistido",
                viewReclamoJspf,
                "boolean existeReclamoPersistido"
        );
        assertContains(
                "hasReclamo exige persistencia",
                viewReclamoJspf,
                "hasReclamo: <%= existeReclamoPersistido %>"
        );
        assertContains(
                "ids de alta quedan en cero",
                viewReclamoJspf,
                "int idReclamoPantalla = esAlta || !existeReclamoPersistido"
        );
        assertContains(
                "precarga Compras muestra estado cero",
                viewReclamoJspf,
                "esAlta && !esBorradorCompras && estados.getId()==0"
        );
        assertContains(
                "render conserva alta Compras",
                actionReclamo,
                "if (cmd.equals(Constants.ADD))"
        );
        assertContains(
                "servicio consulta relacion",
                service,
                "get_requerimiento_reclamo_prestacional"
        );
        assertContains(
                "servicio reserva",
                service,
                "reservar_reclamo_prestacional"
        );
        assertContains(
                "tabla uno a uno",
                migration,
                "PRIMARY KEY (id_requerimiento)"
        );
        assertContains(
                "reclamo unico",
                migration,
                "ux_compras_requerimiento_reclamo_id_reclamo"
        );
        assertContains(
                "reserva exige cotizado",
                migration,
                "v_estado_requerimiento <> 3"
        );
        assertContains(
                "reserva exige afiliado",
                migration,
                "v_afiliado_int IS NULL"
        );
        assertNotContains(
                "sin transicion estado RP",
                actionCompra,
                "ESTADO_RECLAMO_RP"
        );
    }

    private static String leer(String path) throws Exception {
        File file = new File(path);
        if (!file.isFile()) {
            throw new AssertionError("archivo inexistente: " + path);
        }
        byte[] bytes = Files.readAllBytes(file.toPath());
        String value = decodificar(bytes);
        if (value.length() > 0 && value.charAt(0) == '\uFEFF') {
            value = value.substring(1);
        }
        return value.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static String decodificar(byte[] bytes) throws Exception {
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE) {
            return new String(bytes, 2, bytes.length - 2, Charset.forName("UTF-16LE"));
        }
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF) {
            return new String(bytes, 2, bytes.length - 2, Charset.forName("UTF-16BE"));
        }
        int offset = 0;
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) {
            offset = 3;
        }
        CharsetDecoder utf8 = Charset.forName("UTF-8").newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return utf8.decode(ByteBuffer.wrap(bytes, offset, bytes.length - offset)).toString();
        } catch (CharacterCodingException invalidUtf8) {
            return new String(bytes, Charset.forName("windows-1252"));
        }
    }

    private static void assertContains(String d, String v, String e) {
        if (v == null || v.indexOf(e) < 0) {
            throw new AssertionError(d + ": falta [" + e + "]");
        }
    }

    private static void assertNotContains(String d, String v, String e) {
        if (v != null && v.indexOf(e) >= 0) {
            throw new AssertionError(d + ": contenido inesperado [" + e + "]");
        }
    }

    private static void assertBefore(String d, String v, String a, String b) {
        int ia = v == null ? -1 : v.indexOf(a);
        int ib = v == null ? -1 : v.indexOf(b);
        if (ia < 0 || ib < 0 || ia >= ib) {
            throw new AssertionError(d + ": orden inválido: " + ia + ", " + ib);
        }
    }

    private ComprasRequerimientosUiContractTest() {
    }
}