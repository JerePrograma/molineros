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
        if (args.length == 1
                && "detalle-sector".equals(args[0])) {

            assertSectoresDetalleYAfiliado();
            System.out.println("CONTRATO_DETALLE_SECTOR_COMPRAS_OK");
            return;
        }

        if (args.length == 1
                && "adjudicacion".equals(args[0])) {

            assertPrestadorAdjudicadoEsUnico();
            System.out.println(
                    "CONTRATO_ADJUDICACION_COMPRAS_OK"
            );
            return;
        }
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
        assertContratoOriginalBuscadoresPreservado();
        assertDetalleTecnicoUsaBuscadoresCanonicos();
        assertSectoresDetalleYAfiliado();
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
        String adjudicacion = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_adjudicacion.jsp"
        );

        String tabla = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_tabla.jsp"
        );
        String comunes = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_comunes.jsp");
        String editable = leer("ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_editable.jsp");
        String service = leer("ext-impl/src/ar/com/ospim/compras/requerimientos/service/EditarRequerimientoCompraServiceImpl.java");
        assertContains(
                "selector global",
                adjudicacion,
                "id_prestador_adjudicado"
        );

        assertNotContains(
                "selector fuera del detalle",
                tabla,
                "id_prestador_adjudicado"
        );
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

    private static void assertContratoOriginalBuscadoresPreservado()
            throws Exception {

        String reclamoJspf = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jspf"
        );
        String reclamoJs = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.js"
        );
        String medicamento = leer(
                "ext-web/docroot/html/portlet/utils/medicamentos/"
                        + "busqueda_medicamentos.jsp"
        );
        String medicamentoResultado = leer(
                "ext-web/docroot/html/portlet/utils/medicamentos/"
                        + "medicamentos_search_result.jsp"
        );
        String nomencladorResultado = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "nomenclador/nomenclador_search_result.jsp"
        );
        String struts = leer("ext-web/docroot/WEB-INF/struts-config.xml");
        String tiles = leer("ext-web/docroot/WEB-INF/tiles-defs.xml");
        String liferayPortlet = leer(
                "ext-web/docroot/WEB-INF/liferay-portlet-ext.xml"
        );

        assertGitBlobHash(
                "view_reclamo.jsp",
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp",
                "2fd4dac25488671022500b6a51982ff5a4e079ec"
        );
        assertGitBlobHash(
                "view_reclamo.jspf",
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jspf",
                "d9198394466870797151c7912555062630f007d7"
        );
        assertGitBlobHash(
                "view_reclamo.js",
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.js",
                "096cb48eb221d9e785a646c576f838a8e98362ab"
        );
        assertGitBlobHash(
                "busqueda_medicamentos.jsp",
                "ext-web/docroot/html/portlet/utils/medicamentos/"
                        + "busqueda_medicamentos.jsp",
                "5dc4c8e2cbb4518977cb6ee4b2cd5c6000b3d13c"
        );
        assertGitBlobHash(
                "medicamentos_search_result.jsp",
                "ext-web/docroot/html/portlet/utils/medicamentos/"
                        + "medicamentos_search_result.jsp",
                "5960605255d05ec11df69766add58c593aac6bf3"
        );
        assertGitBlobHash(
                "nomenclador_search_result.jsp",
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "nomenclador/nomenclador_search_result.jsp",
                "0b5a73ffd1992a36a7e1efa2b4c7f29ae8d4ee86"
        );

        assertContains(
                "ruta medicamento original",
                reclamoJspf,
                "value=\"/autorizaciones/buscar_medicamentos\""
        );
        assertContains(
                "mapping medicamento original",
                struts,
                "<action path=\"/autorizaciones/buscar_medicamentos\" "
                        + "forward=\"portlet.utils.medicamento.view\" />"
        );
        assertContains(
                "tile medicamento original",
                tiles,
                "name=\"portlet.utils.medicamento.view\""
        );
        assertContains(
                "JSP medicamento original",
                tiles,
                "/portlet/utils/medicamentos/medicamentos_search_result.jsp"
        );
        assertContains(
                "campo nombre medicamento",
                medicamento,
                "<portlet:namespace />nombre_medicamento"
        );
        assertContains(
                "campo troquel medicamento",
                medicamento,
                "<portlet:namespace />troquel"
        );
        assertContains(
                "link Buscar medicamento",
                medicamento,
                ">Buscar</a>"
        );
        assertContains(
                "popup medicamento original",
                medicamento,
                "Liferay.Popup({title:"
        );
        assertContains(
                "busqueda incremental medicamento",
                medicamento,
                "function <portlet:namespace />buscarMedicamentoOnDiv(e)"
        );
        assertContains(
                "resultado incremental medicamento",
                medicamento,
                "jQuery(\"#divMedicamento\").load(url)"
        );
        assertContains(
                "firma callback medicamento",
                medicamento,
                "function pasarParametrosAParentMd(troquel,medicamento,id, pres)"
        );
        assertContains(
                "firma selector medicamento",
                medicamento,
                "function seleccionaCamposMd(id, cod, param, pres)"
        );
        assertBefore(
                "orden seleccion y cierre medicamento",
                medicamento,
                "seleccionaCamposMd(id, troquel, medicamento, pres);",
                "<portlet:namespace />cerrarMd();"
        );
        assertContains(
                "cierre popup medicamento",
                medicamento,
                "Liferay.Popup.close(popupMD)"
        );
        assertContains(
                "resultado unico medicamento",
                medicamentoResultado,
                "if(total==1)"
        );
        assertContains(
                "orden argumentos medicamento unico",
                medicamentoResultado,
                "\"<%=medicamento.getTroquel()%>\", "
                        + "\"<%=medicamento.getNombre().trim()%>\", "
                        + "\"<%=medicamento.getId_medicamentoAsString()%>\", "
                        + "\"<%=medicamento.getPresentacion()%>\""
        );
        assertOccurrences(
                "links medicamento multiples",
                medicamentoResultado,
                "javascript:pasarParametrosAParentMd",
                6
        );
        assertNotContains(
                "medicamento sin extension Compras",
                medicamento,
                "callback_seleccion"
        );
        assertNotContains(
                "resultado medicamento sin hardening ajeno",
                medicamentoResultado,
                "private String medicamentoJs"
        );

        assertContains(
                "ruta nomenclador original",
                reclamoJspf,
                "struts_action=/autorizaciones/buscar_nomenclador"
        );
        assertContains(
                "mapping nomenclador original",
                struts,
                "<action path=\"/autorizaciones/buscar_nomenclador\" "
                        + "forward=\"portlet.autorizaciones.nomenclador.search.view\" />"
        );
        assertContains(
                "tile nomenclador original",
                tiles,
                "name=\"portlet.autorizaciones.nomenclador.search.view\""
        );
        assertContains(
                "JSP nomenclador original",
                tiles,
                "/portlet/autorizaciones/nomenclador/nomenclador_search_result.jsp"
        );
        assertContains(
                "firma callback nomenclador",
                reclamoJs,
                "function pasarParametrosAParentNm("
                        + "tipoNomenclador,codigo,descripcion)"
        );
        assertBefore(
                "orden seleccion y cierre nomenclador",
                reclamoJs,
                "seleccionaCamposNm(tipoNomenclador, codigo, descripcion);",
                "reclamoPrestacional_cerrarNm();"
        );
        assertContains(
                "resultado unico nomenclador",
                nomencladorResultado,
                "if(total==1)"
        );
        assertContains(
                "seis argumentos nomenclador unico",
                nomencladorResultado,
                "pasarParametrosAParentNm("
                        + "\"<%=nom.getId_tipo_nomenclador_string() %>\", "
                        + "\"<%=nom.getCodigo().trim() %>\", "
                        + "\"<%=nom.getDescripcion().trim() %>\", "
                        + "\"\", \"\", "
                        + "\"<%=nom.getDescripcionTipoNomenclador().trim()%>\");"
        );
        assertOccurrences(
                "links nomenclador multiples",
                nomencladorResultado,
                "javascript:pasarParametrosAParentNm",
                6
        );
        assertNotContains(
                "nomenclador sin callback Compras",
                nomencladorResultado,
                "callback_seleccion"
        );
        assertNotContains(
                "nomenclador sin IDs Compras",
                nomencladorResultado,
                "devolver_ids"
        );

        assertContains(
                "AUT_1 declarado",
                liferayPortlet,
                "<portlet-name>AUT_1</portlet-name>"
        );
        assertContains(
                "struts path autorizaciones",
                liferayPortlet,
                "<struts-path>autorizaciones</struts-path>"
        );
        assertContains(
                "COMPRA_1 declarado",
                liferayPortlet,
                "<portlet-name>COMPRA_1</portlet-name>"
        );
        assertContains(
                "struts path compras",
                liferayPortlet,
                "<struts-path>compras</struts-path>"
        );
    }

    private static void assertDetalleTecnicoUsaBuscadoresCanonicos()
            throws Exception {

        String editor = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_editor.jsp"
        );

        String tabla = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_tabla.jsp"
        );

        String scripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_scripts_editable.jsp"
        );

        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "BuscarItemTecnicoComprasAction.java"
        );

        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "RequerimientoCompraDetalleHelper.java"
        );

        String resultado = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "buscar_item_tecnico_result.jsp"
        );

        String webKeys = leer(
                "ext-impl/src/ar/com/ospim/compras/"
                        + "WebKeysCompras.java"
        );

        String bean = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "RequerimientoCompraDetalle.java"
        );

        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "EditarRequerimientoCompraServiceImpl.java"
        );

        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/"
                        + "compras_schema.sql"
        );

        String struts = leer(
                "ext-web/docroot/WEB-INF/struts-config.xml"
        );

        String tiles = leer(
                "ext-web/docroot/WEB-INF/tiles-defs.xml"
        );

        String jasper = leer(
                "ext-web/docroot/WEB-INF/classes/jasper/compras/"
                        + "requerimiento_compra.jrxml"
        );

        /*
         * Formulario único de Compras.
         */
        assertNotContains(
                "Compras no incluye buscador de medicamentos",
                editor,
                "busqueda_medicamentos.jsp"
        );

        assertNotContains(
                "Compras no contiene bloque medicamento",
                editor,
                "detalle_bloque_medicamento"
        );

        assertNotContains(
                "Compras no muestra troquel",
                editor,
                "name=\"troquel\""
        );

        assertNotContains(
                "Compras no muestra nombre medicamento",
                editor,
                "nombre_medicamento"
        );

        assertContains(
                "Código Presentado visible",
                editor,
                "key=\"codigo-presentado\""
        );

        assertContains(
                "Descripción visible",
                editor,
                "Descripción:"
        );

        assertContains(
                "acción Buscar visible",
                editor,
                ">Buscar</a>"
        );

        assertContains(
                "acción Limpiar visible",
                editor,
                ">Limpiar</a>"
        );

        assertContains(
                "aviso para histórico",
                editor,
                "detalle_medicamento_historico_info"
        );

        assertContains(
                "ID prestación interno",
                editor,
                "detalle_id_prestacion"
        );

        assertContains(
                "ID tipo interno",
                editor,
                "detalle_id_tipo_nomenclador"
        );

        /*
         * JavaScript: el sector define NOMENCLADOR u OBSERVACION.
         */
        assertNotContains(
                "JS no selecciona medicamentos",
                scripts,
                "seleccionarMedicamentoDetalle"
        );

        assertNotContains(
                "JS no limpia medicamentos",
                scripts,
                "limpiarSeleccionMedicamento"
        );

        assertNotContains(
                "JS no resuelve Farmacia como medicamento",
                scripts,
                "return 'MEDICAMENTO'"
        );

        assertContains(
                "JS identifica histórico de medicamento",
                scripts,
                "esDetalleMedicamentoHistorico"
        );

        assertContains(
                "histórico se configura readonly",
                scripts,
                "configurarEditorMedicamentoHistorico"
        );

        assertContains(
                "serializa ID prestación",
                scripts,
                "prefix + 'id_prestacion'"
        );

        assertContains(
                "serializa ID tipo nomenclador",
                scripts,
                "prefix + 'id_tipo_nomenclador'"
        );

        assertNotContains(
                "cliente no serializa ID medicamento",
                scripts,
                "prefix + 'id_medicamento'"
        );

        assertNotContains(
                "cliente no serializa troquel",
                scripts,
                "prefix + 'troquel'"
        );

        assertNotContains(
                "cliente no serializa nombre medicamento",
                scripts,
                "prefix + 'nombre_medicamento'"
        );

        /*
         * Mapping central.
         */
        assertContains(
                "mapping central de filtro",
                webKeys,
                "getFiltroTipoNomencladorCompras"
        );

        assertContains(
                "filtro Farmacia 9",
                webKeys,
                "FILTRO_NOMENCLADOR_FARMACIA = 9"
        );

        assertContains(
                "filtro Discapacidad 8",
                webKeys,
                "FILTRO_NOMENCLADOR_DISCAPACIDAD = 8"
        );

        assertContains(
                "filtro Odontología 1",
                webKeys,
                "FILTRO_NOMENCLADOR_ODONTOLOGIA = 1"
        );

        assertContains(
                "filtro general 0",
                webKeys,
                "FILTRO_NOMENCLADOR_GENERAL = 0"
        );

        /*
         * Buscador.
         */
        assertContains(
                "Action usa mapping central",
                action,
                "getFiltroTipoNomencladorCompras"
        );

        assertContains(
                "Action recarga requerimiento persistido",
                action,
                "getRequerimientoCompra("
        );

        assertContains(
                "Action valida sector de alta",
                action,
                "getSector("
        );

        assertContains(
                "Action exige PENDIENTE",
                action,
                "puedeEditarEstructura()"
        );

        assertContains(
                "resultado recibe filtro",
                resultado,
                "COMPRAS_ID_TIPO_NOMENCLADOR"
        );

        assertContains(
                "resultado aplica filtro",
                resultado,
                "idTipoNomencladorBusqueda,"
        );

        assertContains(
                "resultado devuelve ID prestación",
                resultado,
                "nomenclador.getId_prestacion()"
        );

        assertContains(
                "resultado devuelve tipo real",
                resultado,
                "nomenclador.getId_tipo_nomenclador()"
        );

        /*
         * Backend.
         */
        assertContains(
                "helper reconstruye persistido",
                helper,
                "getDetallePersistido("
        );

        assertContains(
                "helper conserva históricos",
                helper,
                "detallePersistido.esMedicamento()"
        );

        assertContains(
                "servicio usa detalle persistido",
                service,
                "obtenerDetallePersistido("
        );

        assertContains(
                "servicio prepara transición",
                service,
                "prepararDetalleParaGuardar("
        );

        assertContains(
                "servicio conserva validación canónica",
                service,
                "obtenerNomencladorCanonico("
        );

        assertContains(
                "servicio valida tipo real según sector",
                service,
                "esTipoNomencladorValidoParaSectorCompras"
        );

        assertNotContains(
                "servicio no omite validación para filtro general",
                service,
                "filtroTipoNomenclador.intValue() > 0"
        );

        assertContains(
                "servicio rechaza nuevos medicamentos",
                service,
                "No se pueden crear nuevos detalles "
                        + "de tipo MEDICAMENTO"
        );

        /*
         * Persistencia e históricos.
         */
        assertNotContains(
                "SQL no obliga Farmacia medicamento",
                schema,
                "El sector Farmacia requiere seleccionar medicamento"
        );

        assertContains(
                "SQL conserva histórico MEDICAMENTO",
                schema,
                "v_tipo_item_actual = 'MEDICAMENTO'"
        );

        assertContains(
                "lectura histórica conserva código",
                schema,
                "WHEN d.tipo_item = 'MEDICAMENTO'"
        );

        assertContains(
                "bean conserva lectura histórica",
                bean,
                "public boolean esMedicamento()"
        );

        assertContains(
                "bean proyecta Código visible",
                bean,
                "getCodigoItemVisible()"
        );

        assertContains(
                "bean proyecta Descripción visible",
                bean,
                "getDescripcionItemVisible()"
        );

        /*
         * Tabla: no recuperar Tipo ni Troquel.
         */
        assertNotContains(
                "tabla no muestra Tipo",
                tabla,
                "<th>Tipo</th>"
        );

        assertNotContains(
                "tabla no rotula Troquel",
                tabla,
                "Código / Troquel"
        );

        assertContains(
                "tabla muestra Código presentado",
                tabla,
                "Código presentado"
        );

        /*
         * Integración existente.
         */
        assertContains(
                "mapping buscador técnico",
                struts,
                "path=\"/compras/buscar_item_tecnico\""
        );

        assertContains(
                "tile buscador técnico",
                tiles,
                "name=\"portlet.compras.buscar_item_tecnico\""
        );

        assertContains(
                "resultado buscador técnico",
                tiles,
                "/portlet/compras/requerimientos/"
                        + "buscar_item_tecnico_result.jsp"
        );

        assertContains(
                "Jasper conserva tipo histórico",
                jasper,
                "field name=\"tipo_item\""
        );

        assertContains(
                "Jasper conserva código común",
                jasper,
                "field name=\"codigo_item\""
        );

        assertContains(
                "Jasper conserva descripción común",
                jasper,
                "field name=\"descripcion_item\""
        );

        assertNotContains(
                "schema sin tipo ARTICULO",
                schema,
                "'ARTICULO'"
        );
    }

    private static void assertSectoresDetalleYAfiliado()
            throws Exception {

        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/"
                        + "compras_schema.sql"
        );

        String webKeys = leer(
                "ext-impl/src/ar/com/ospim/compras/"
                        + "WebKeysCompras.java"
        );

        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/"
                        + "action/RequerimientoCompraDetalleHelper.java"
        );

        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/"
                        + "service/EditarRequerimientoCompraServiceImpl.java"
        );

        String afiliado = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_afiliado_editable.jsp"
        );

        String editor = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_editor.jsp"
        );

        String scripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_scripts_editable.jsp"
        );

        String scriptsComunes = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_scripts_comunes.jsp"
        );

        String tabla = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_detalle_tabla.jsp"
        );

        assertContains(
                "Legales requiere afiliado",
                schema,
                "(6, 'Legales', TRUE"
        );

        assertContains(
                "Legales reutiliza buscador de afiliados",
                afiliado,
                "/html/portlet/autorizaciones/busqueda_afiliado.jsp"
        );

        assertContains(
                "tipo de detalle Observacion",
                schema,
                "'OBSERVACION'"
        );

        assertContains(
                "SQL deriva detalle Observacion por sector",
                schema,
                "v_tipo_item_esperado := 'OBSERVACION'"
        );

        assertContains(
                "Java deriva sectores de Observacion",
                webKeys,
                "esSectorDetalleObservacionCompras"
        );

        assertContains(
                "Monotributo usa nomenclador general",
                webKeys,
                "\"MONOTRIBUTO\".equals(sector)"
        );

        assertContains(
                "helper valida Observacion",
                helper,
                "validarDetalleObservacion("
        );

        assertContains(
                "servicio valida Observacion",
                service,
                "validarDetalleObservacionParaGuardar("
        );

        assertContains(
                "editor identifica fila Observaciones",
                editor,
                "detalle_fila_observaciones"
        );

        assertContains(
                "editor admite tipo Observacion",
                scripts,
                "return 'OBSERVACION'"
        );

        assertContains(
                "editor exige Observaciones",
                scripts,
                "Debe informar las Observaciones."
        );

        assertContains(
                "matriz incluye Monotributo con codigo",
                scriptsComunes,
                "descripcion == 'MONOTRIBUTO'"
        );

        assertContains(
                "matriz incluye Legales con Observacion",
                scriptsComunes,
                "descripcion == 'LEGALES'"
        );

        assertContains(
                "tabla alterna columnas de codigo",
                tabla,
                "compras-detalle-columna-codigo"
        );

        assertContains(
                "tabla alterna columna de Observacion",
                tabla,
                "compras-detalle-columna-observacion"
        );

        assertContains(
                "scripts alternan columnas del detalle",
                scriptsComunes,
                "actualizarVisibilidadColumnasDetalleCompra"
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
        assertContains(
                "precarga admite nomenclador técnico",
                precargaService,
                "detalle.tieneNomenclador()"
        );

        assertContains(
                "precarga valida tipo por sector",
                precargaService,
                "esTipoNomencladorValidoParaSectorCompras"
        );

        assertNotContains(
                "precarga no exige medicamento por ser Farmacia",
                precargaService,
                "El detalle de Farmacia debe tener medicamento"
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

    private static void assertGitBlobHash(
            String descripcion,
            String path,
            String esperado) throws Exception {

        byte[] worktree = Files.readAllBytes(new File(path).toPath());
        java.io.ByteArrayOutputStream canonical =
                new java.io.ByteArrayOutputStream(worktree.length);

        for (int i = 0; i < worktree.length; i++) {
            if (worktree[i] == '\r'
                    && i + 1 < worktree.length
                    && worktree[i + 1] == '\n') {

                continue;
            }

            canonical.write(worktree[i]);
        }

        byte[] bytes = canonical.toByteArray();
        java.security.MessageDigest sha1 =
                java.security.MessageDigest.getInstance("SHA-1");

        sha1.update(
                ("blob " + bytes.length + '\0')
                        .getBytes(Charset.forName("UTF-8"))
        );
        sha1.update(bytes);

        byte[] hash = sha1.digest();
        StringBuilder actual = new StringBuilder();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(hash[i] & 0xFF);

            if (hex.length() == 1) {
                actual.append('0');
            }

            actual.append(hex);
        }

        if (!esperado.equals(actual.toString())) {
            throw new AssertionError(
                    descripcion
                            + ": hash esperado " + esperado
                            + ", actual " + actual
            );
        }
    }

    private static void assertOccurrences(
            String descripcion,
            String valor,
            String esperado,
            int cantidadEsperada) {

        int cantidad = 0;
        int desde = 0;

        while (valor != null
                && (desde = valor.indexOf(esperado, desde)) >= 0) {

            cantidad++;
            desde += esperado.length();
        }

        if (cantidad != cantidadEsperada) {
            throw new AssertionError(
                    descripcion
                            + ": se esperaban " + cantidadEsperada
                            + " ocurrencias de [" + esperado + "]"
                            + " y hubo " + cantidad
            );
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
