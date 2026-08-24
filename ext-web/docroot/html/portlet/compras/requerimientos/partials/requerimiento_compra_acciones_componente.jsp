<%--
Responsabilidad:
    Renderiza la botonera autorizada del requerimiento según estado y permisos.
Incluido desde:
    requerimiento_compra_acciones_runtime_componente.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
Entradas requeridas:
    Variables léxicas preparadas por requerimiento_compra_modelo_vista_componente.jsp o por el caller indicado.
Atributos de request consumidos:
    Ninguno directamente, salvo los accesos request declarados en el cuerpo.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    id_requerimiento_compra, estado_nuevo, reintentar_notificaciones, btnGuardarCompras, btnGuardarCotizacionCompra, btnEnviarCotizarRequerimientoCompra, btnReintentarCotizacionRequerimientoCompra, btnCrearReclamoPrestacional
Efectos secundarios:
    Sólo modifica el DOM o el modelo JavaScript; no ejecuta persistencia.
--%>
<%@ page import="ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional" %>
<%
int botoneraIdRequerimientoActual = req != null ? req.getIdRequerimientoCompra() : 0;
int botoneraEstadoActual = req != null ? req.getEstado() : 0;
String botoneraSectorDescripcionActual =
        req != null
                ? req.getSectorDescripcion()
                : "";

boolean botoneraSectorPermiteReclamoPrestacional =
        WebKeysCompras
                .puedeGenerarReclamoPrestacional(
                        botoneraSectorDescripcionActual
                );

boolean botoneraRequerimientoPersistido =
        req != null && botoneraIdRequerimientoActual > 0;

boolean botoneraTieneRolAnular =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

boolean botoneraTieneRolView =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS);

boolean botoneraTieneRolCotizar =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS);

boolean botoneraTieneRolCrearReclamo =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysAutorizaciones.ROL_ABM_RECLAM_PREST
        );

boolean botoneraTieneRolVerReclamo =
        user != null
        && (
                botoneraTieneRolCrearReclamo
                || PermissionUtil.userContainsRole(
                        user,
                        WebKeysAutorizaciones
                                .ROL_CONSULTA_RECLAMOS_PRESTACIONALES
                )
        );

Object botoneraPendientesNotificacionAttr =
        renderRequest.getAttribute(
                WebKeysCompras.HAY_PRESTADORES_PENDIENTES_NOTIFICACION
        );

/*
 * Fail closed: el boton solo se muestra cuando el action confirmo mediante
 * la consulta canonica que realmente existen prestadores pendientes.
 */
boolean botoneraHayPrestadoresPendientesNotificacion =
        Boolean.TRUE.equals(
                botoneraPendientesNotificacionAttr
        );

Object botoneraRelacionAttr =
        renderRequest.getAttribute(
                WebKeysCompras
                        .RELACION_RECLAMO_PRESTACIONAL_COMPRA
        );

RequerimientoCompraReclamoPrestacional botoneraRelacionReclamo =
        botoneraRelacionAttr
                instanceof RequerimientoCompraReclamoPrestacional
                ? (RequerimientoCompraReclamoPrestacional)
                        botoneraRelacionAttr
                : null;

boolean botoneraConsultaRelacionReclamoOk =
        Boolean.TRUE.equals(
                renderRequest.getAttribute(
                        WebKeysCompras
                                .RELACION_RECLAMO_PRESTACIONAL_CONSULTA_OK
                )
        );

boolean botoneraPuedeEnviarACotizar =
        botoneraRequerimientoPersistido
        && botoneraTieneRolCotizar
        && WebKeysCompras.puedeEnviarACotizar(botoneraEstadoActual);

boolean botoneraPuedeReintentarCotizacion =
        botoneraRequerimientoPersistido
        && botoneraTieneRolCotizar
        && WebKeysCompras.puedeReintentarNotificaciones(
                botoneraEstadoActual,
                botoneraHayPrestadoresPendientesNotificacion
        );

boolean botoneraPuedeImprimir =
        botoneraRequerimientoPersistido
        && (botoneraTieneRolView || puedeABM || botoneraTieneRolCotizar);

boolean botoneraPuedeCrearReclamoPrestacional =
        botoneraRequerimientoPersistido
        && botoneraSectorPermiteReclamoPrestacional
        && WebKeysCompras.esCotizado(botoneraEstadoActual)
        && (puedeABM || botoneraTieneRolCotizar)
        && botoneraTieneRolCrearReclamo
        && botoneraConsultaRelacionReclamoOk
        && botoneraRelacionReclamo == null;

boolean botoneraPuedeVerReclamoPrestacional =
        botoneraRequerimientoPersistido
        && (
                WebKeysCompras.esCotizado(
                        botoneraEstadoActual
                )
                || WebKeysCompras.esReclamoRP(
                        botoneraEstadoActual
                )
        )
        && (
                botoneraTieneRolView
                || puedeABM
                || botoneraTieneRolCotizar
                || botoneraTieneRolAnular
        )
        && botoneraTieneRolVerReclamo
        && botoneraConsultaRelacionReclamoOk
        && botoneraRelacionReclamo != null
        && botoneraRelacionReclamo.isVinculado();

boolean botoneraReclamoEnProceso =
        botoneraRequerimientoPersistido
        && WebKeysCompras.esCotizado(botoneraEstadoActual)
        && botoneraConsultaRelacionReclamoOk
        && botoneraRelacionReclamo != null
        && !botoneraRelacionReclamo.isVinculado();

PortletURL botoneraCambiarEstadoURL = renderResponse.createActionURL();
botoneraCambiarEstadoURL.setWindowState(WindowState.MAXIMIZED);
botoneraCambiarEstadoURL.setParameter("struts_action", "/compras/cambiar_estado_requerimiento");

PortletURL botoneraReclamoPrestacionalURL =
        renderResponse.createActionURL();
botoneraReclamoPrestacionalURL.setWindowState(
        WindowState.MAXIMIZED
);
botoneraReclamoPrestacionalURL.setParameter(
        "struts_action",
        "/compras/iniciar_reclamo_prestacional"
);

String botoneraEnviarCotizarFormId =
        namespaceCompra + "enviarCotizarRequerimientoCompraForm";
String botoneraReintentarCotizacionFormId =
        namespaceCompra + "reintentarCotizacionRequerimientoCompraForm";
String botoneraReclamoPrestacionalFormId =
        namespaceCompra + "reclamoPrestacionalCompraForm";
%>

<% if (botoneraPuedeEnviarACotizar) { %>
    <form action="<%= botoneraCambiarEstadoURL.toString() %>"
          method="post"
          id="<%= botoneraEnviarCotizarFormId %>"
          style="display:none;">
        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(botoneraIdRequerimientoActual) %>" />
        <input type="hidden"
               name="<portlet:namespace />estado_nuevo"
               value="<%= String.valueOf(WebKeysCompras.ESTADO_A_COTIZAR) %>" />
    </form>
<% } %>

<% if (botoneraPuedeReintentarCotizacion) { %>
    <form action="<%= botoneraCambiarEstadoURL.toString() %>"
          method="post"
          id="<%= botoneraReintentarCotizacionFormId %>"
          style="display:none;">
        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(botoneraIdRequerimientoActual) %>" />
        <input type="hidden"
               name="<portlet:namespace />reintentar_notificaciones"
               value="true" />
    </form>
<% } %>

<% if (botoneraPuedeCrearReclamoPrestacional
        || botoneraPuedeVerReclamoPrestacional) { %>
    <form action="<%= botoneraReclamoPrestacionalURL.toString() %>"
          method="post"
          id="<%= botoneraReclamoPrestacionalFormId %>"
          style="display:none;">
        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(botoneraIdRequerimientoActual) %>" />
    </form>
<% } %>

<div class="compras-seccion compras-seccion-botonera">
<table class="lfr-table">
    <tr>
        <td class="compras-botonera-acciones">
            <% if (modoEditable && puedeEditarEstructuraPantalla) { %>
                <input type="button"
                       id="<portlet:namespace />btnGuardarCompras"
                       value="Guardar"
                       onClick="return <%= namespaceCompra %>guardar();" />
            <% } %>

            <% if (modoEditable && puedeEditarCotizacionPantalla) { %>
                <input type="button"
                       id="<portlet:namespace />btnGuardarCotizacionCompra"
                       value="Guardar cotizaci&#243;n"
                       onClick="return <%= namespaceCompra %>guardarCotizacion();" />
            <% } %>

            <%--
                No se muestra Editar dentro de la vista.
                La edicion continua disponible en el menu de acciones del
                listado, respetando la observacion funcional mas reciente.
            --%>

            <% if (botoneraPuedeEnviarACotizar) { %>
                <input type="button"
                       id="<portlet:namespace />btnEnviarCotizarRequerimientoCompra"
                       value="Enviar a Cotizar"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraEnviarCotizarFormId %>',
                               '<portlet:namespace />btnEnviarCotizarRequerimientoCompra',
                               '\u00bfConfirma enviar a Cotizar a los prestadores habilitados?',
                               'Notificando...'
                       );" />
            <% } %>

            <% if (botoneraPuedeReintentarCotizacion) { %>
                <input type="button"
                       id="<portlet:namespace />btnReintentarCotizacionRequerimientoCompra"
                       value="Notificar prestadores pendientes"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraReintentarCotizacionFormId %>',
                               '<portlet:namespace />btnReintentarCotizacionRequerimientoCompra',
                               '\u00bfConfirma notificar nuevamente a los prestadores pendientes?',
                               'Notificando...'
                       );" />
            <% } %>

            <% if (botoneraPuedeCrearReclamoPrestacional) { %>
                <input type="button"
                       id="<portlet:namespace />btnCrearReclamoPrestacional"
                       value="Crear Reclamo Prestacional"
                       onClick="submitForm(document.getElementById('<%= botoneraReclamoPrestacionalFormId %>')); return false;" />
            <% } %>

            <% if (botoneraPuedeVerReclamoPrestacional) { %>
                <input type="button"
                       id="<portlet:namespace />btnVerReclamoPrestacional"
                       value="Ver Reclamo Prestacional"
                       onClick="submitForm(document.getElementById('<%= botoneraReclamoPrestacionalFormId %>')); return false;" />
            <% } %>

            <% if (botoneraReclamoEnProceso
                    && (botoneraTieneRolVerReclamo
                        || botoneraTieneRolCrearReclamo)) { %>
                <input type="button"
                       value="<%= botoneraRelacionReclamo.isError()
                               ? "Reclamo creado: vinculación pendiente"
                               : "Creación de reclamo en proceso" %>"
                       disabled="disabled" />
            <% } %>

            <% if (botoneraPuedeImprimir) { %>
                <input type="button"
                       id="<portlet:namespace />btnImprimirRequerimientoCompra"
                       value="Imprimir PDF"
                       onClick="return <%= namespaceCompra %>imprimirRequerimientoCompra();" />
            <% } %>

            <input type="button"
                   id="<portlet:namespace />btnVolverCompras"
                   class="compras-btn-volver"
                   value="Volver"
                   onClick="window.location.href='<%= volverURL.toString() %>';" />
        </td>
    </tr>
</table>
</div>

<iframe id="<portlet:namespace />iframeImpresionRequerimientoCompra"
        name="<portlet:namespace />iframeImpresionRequerimientoCompra"
        style="position:absolute; width:0; height:0; border:0; visibility:hidden;">
</iframe>

<script type="text/javascript">
    function <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(formId, botonId, mensajeConfirmacion, textoProcesando) {
        var form = document.getElementById(formId);
        var btn = document.getElementById(botonId);

        if (!form) {
            alert('No se pudo preparar el cambio de estado del requerimiento.');
            return false;
        }

        if (!confirm(mensajeConfirmacion)) {
            return false;
        }

        if (btn) {
            btn.disabled = true;
            btn.value = textoProcesando;
        }

        submitForm(form);
        return false;
    }

    function <%= namespaceCompra %>imprimirRequerimientoCompra() {
        var iframe = document.getElementById('<portlet:namespace />iframeImpresionRequerimientoCompra');

        if (!iframe) {
            alert('No se pudo preparar la impresión del requerimiento.');
            return false;
        }

        var url = '<%= imprimirURL.toString() %>';
        url += (url.indexOf('?') >= 0 ? '&' : '?') + '_ts=' + new Date().getTime();

        iframe.onload = function() {
            try {
                iframe.contentWindow.focus();
                iframe.contentWindow.print();
            } catch (e) {
                alert('No se pudo imprimir automáticamente el PDF.');
            }
        };

        iframe.src = url;
        return false;
    }
</script>
