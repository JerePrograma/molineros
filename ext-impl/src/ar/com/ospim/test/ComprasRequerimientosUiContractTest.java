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
        assertDetalleTecnicoUsaBuscadoresCanonicos();
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
        assertContains(
                "mensaje de detalle guardado",
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
        assertContains("descripcion", mail, " | Descripcion: ");
        assertNotContains("obs anterior", mail, " | Obs: ");
        assertContains("modo temporal explicito", mail, "USAR_EMAIL_DESTINO_TEMPORAL = true");
        assertContains("destinatario QA", mail, "acomas@ospim.org.ar");
        assertContains("reserva canonica", mail, "reservar_notificacion_");
        assertContains("finalizacion canonica", mail, "finalizar_notificacion_");
        assertContains("estado enviado", mail, "WebKeysCompras.ENVIO_ENVIADO");
        assertContains("email real obligatorio", mail,
                "El email real reservado del prestador ");
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

        assertNotContains("sin funciones de articulo", service, "listar_articulos");
    }

    private static void assertDetalleTecnicoUsaBuscadoresCanonicos()
            throws Exception {

        String editor = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_editor.jsp"
        );
        String scripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_editable.jsp"
        );
        String comunes = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_comunes.jsp"
        );
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/BuscarItemTecnicoComprasAction.java"
        );
        String resultado = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/buscar_item_tecnico_result.jsp"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/EditarRequerimientoCompraServiceImpl.java"
        );
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );
        String struts = leer("ext-web/docroot/WEB-INF/struts-config.xml");
        String jasper = leer(
                "ext-web/docroot/WEB-INF/classes/jasper/compras/requerimiento_compra.jrxml"
        );

        assertContains("ids internos hidden", editor, "detalle_id_prestacion\"\n                           value=\"\"");
        assertContains("id medicamento hidden", editor, "detalle_id_medicamento\"\n                           value=\"\"");
        assertNotContains("sin label ID prestacion", editor, "ID Prestaci");
        assertNotContains("sin label ID medicamento", editor, "ID Medicamento");
        assertContains("buscar medicamento", editor, "buscarMedicamentoDetalle");
        assertContains("limpiar medicamento", editor, "limpiarSeleccionMedicamento");
        assertContains("buscar nomenclador", editor, "buscarNomencladorDetalle");
        assertContains("limpiar nomenclador", editor, "limpiarSeleccionNomenclador");
        assertContains("texto invalida medicamento", scripts, "limpiarSeleccionMedicamento(false)");
        assertContains("texto invalida nomenclador", scripts, "limpiarSeleccionNomenclador(false)");
        assertContains("callback id prestacion", scripts, "idPrestacion,");
        assertContains("callback id tipo", scripts, "idTipoNomenclador,");
        assertContains("sector Farmacia exacto", comunes, "== 'FARMACIA'");
        assertContains("sector Prestaciones exacto", comunes, "descripcion == 'PRESTACIONES MEDICAS'");
        assertContains("sector Legales exacto", comunes, "descripcion == 'LEGALES'");
        assertContains("busqueda medicamento canonica", action, "getBusquedaMedicamentos(");
        assertContains("busqueda PM canonica", action, "getListaNomencladorPrestacionesMedicas(");
        assertContains("busqueda Legales canonica", action, "getListaNomenclador(");
        assertContains("resultado devuelve id prestacion", resultado, "getId_prestacion()");
        assertContains("resultado devuelve id tipo", resultado, "getId_tipo_nomenclador()");
        assertContains("validacion medicamento DB", service, "obtenerMedicamentoCanonico");
        assertContains("validacion nomenclador DB", service, "obtenerNomencladorCanonico");
        assertContains("sector recargado DB", service, "obtenerRequerimientoDetalle");
        assertContains("mapping buscador", struts, "path=\"/compras/buscar_item_tecnico\"");
        assertContains("Jasper tipo", jasper, "field name=\"tipo_item\"");
        assertContains("Jasper codigo", jasper, "field name=\"codigo_item\"");
        assertContains("Jasper descripcion", jasper, "field name=\"descripcion_item\"");
        assertNotContains("schema sin id articulo", schema, "id_articulo");
        assertNotContains("schema sin tabla articulo", schema, "compras.articulo");
        assertNotContains("schema sin tipo ARTICULO", schema, "'ARTICULO'");
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
        String precargaService = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "ReclamoPrestacionalCompraPrecargaServiceUtil.java"
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
        String viewReclamoJs = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.js"
        );
        String prestacionEnEdicion = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/datos_edicion_prestacion.jsp"
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
                "precarga Compras inicia PENDIENTE",
                precargaService,
                ".RECLAMO_PRESTACIONAL_ESTADO_CARGADO"
        );
        assertNotContains(
                "precarga Compras no inicia en PRECARGA",
                precargaService,
                "reclamo.setEstado(\n                0"
        );
        assertContains(
                "render conserva alta Compras",
                actionReclamo,
                "if (cmd.equals(Constants.ADD))"
        );
        assertContains(
                "cmd de handoff es ADD",
                actionCompra,
                "Constants.ADD"
        );
        assertContains(
                "id_reclamosel usa el id de alta cero",
                viewReclamoJspf,
                "id=\"<portlet:namespace />id_reclamosel\""
        );
        assertContains(
                "idreclamoprestacion usa el id de alta cero",
                viewReclamoJspf,
                "id=\"<portlet:namespace />idreclamoprestacion\" value=\"<%= idReclamoPantalla %>\""
        );
        assertContains(
                "JS no promueve idreclamoprestacion sin persistencia",
                viewReclamoJs,
                "if (reclamoPrestacionalViewConfig.values.hasReclamo)"
        );
        assertContains(
                "primera prestacion temporal alimenta editor",
                precargaService,
                ".PRESTACION_EN_PROCESO_DE_EDICION"
        );
        assertContains(
                "editor usa la misma primera prestacion de la lista",
                precargaService,
                "prestaciones.get(0)"
        );
        assertContains(
                "editor Compras permanece visible",
                viewReclamoJs,
                "if (reclamoPrestacionalViewConfig.values.esBorradorCompras)"
        );
        assertContains(
                "precarga cantidad",
                precargaService,
                "prestacion.setCantidad("
        );
        assertContains(
                "precarga importe",
                precargaService,
                "prestacion.setImporte("
        );
        assertContains(
                "precarga total comprobante",
                precargaService,
                "prestacion.setComprobanteTotal("
        );
        assertContains(
                "precarga cargo OSPIM",
                precargaService,
                "prestacion.setCargo_ospim("
        );
        assertContains(
                "precarga cargo prestadora",
                precargaService,
                "prestacion.setCargo_ps("
        );
        assertContains(
                "precarga recuperable",
                precargaService,
                "prestacion.setRecuperable("
        );
        assertContains(
                "precarga CUIT",
                precargaService,
                "prestacion.setComprobanteCUIT("
        );
        assertContains(
                "precarga razon social",
                precargaService,
                "prestacion.setComprobanteRazonSocial("
        );
        assertContains(
                "editor muestra importes del comprobante",
                prestacionEnEdicion,
                "prestacionEnEdicion.getComprobanteImporte()"
        );
        assertContains(
                "editor muestra total autorizado",
                prestacionEnEdicion,
                "prestacionEnEdicion.getTotalString()"
        );
        assertContains(
                "editor muestra prestador adjudicado",
                prestacionEnEdicion,
                "prestacionEnEdicion.getComprobanteRazonSocial()"
        );
        assertContains(
                "editor muestra codigo y descripcion de Compras",
                prestacionEnEdicion,
                "prestacionEnEdicion.getCodigoPrestacion()"
        );
        assertContains(
                "integracion visible sin depender del JS en Compras",
                viewReclamoJspf,
                "esBorradorCompras ? \"inline\" : \"none\""
        );
        assertNotContains(
                "accion de inicio no persiste reclamo",
                actionCompra,
                "ReclamosPrestacionesServiceUtil.insertar("
        );
        assertNotContains(
                "precarga no persiste reclamo",
                precargaService,
                "ReclamosPrestacionesServiceUtil.insertar("
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
