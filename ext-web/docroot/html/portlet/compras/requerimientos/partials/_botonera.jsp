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
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS);

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
 * Fail closed: el botón solo se muestra cuando el action confirmó mediante
 * la consulta canónica que realmente existen prestadores pendientes.
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

boolean botoneraPuedeAnular =
        botoneraRequerimientoPersistido
        && botoneraTieneRolAnular
        && WebKeysCompras.puedeAnular(botoneraEstadoActual);

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
        && WebKeysCompras.esCotizado(botoneraEstadoActual)
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
String botoneraAnularFormId =
        namespaceCompra + "anularRequerimientoCompraForm";
String botoneraReclamoPrestacionalFormId =
        namespaceCompra + "reclamoPrestacionalCompraForm";
String botoneraAnularURL =
        "javascript:"
        + namespaceCompra
        + "cambiarEstadoRequerimientoCompra('"
        + botoneraAnularFormId
        + "','"
        + renderResponse.getNamespace()
        + "btnAnularRequerimientoCompra'"
        + ",'¿Confirma anular el requerimiento?'"
        + ",'Anulando...');";
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

<% if (botoneraPuedeAnular) { %>
    <form action="<%= botoneraCambiarEstadoURL.toString() %>"
          method="post"
          id="<%= botoneraAnularFormId %>"
          style="display:none;">
        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(botoneraIdRequerimientoActual) %>" />
        <input type="hidden"
               name="<portlet:namespace />estado_nuevo"
               value="<%= String.valueOf(WebKeysCompras.ESTADO_ANULADO) %>" />
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

<table class="lfr-table">
    <tr>
        <td>
            <% if (modoEditable && puedeEditarEstructuraPantalla) { %>
                <input type="button"
                       id="<portlet:namespace />btnGuardarCompras"
                       value="Guardar"
                       onClick="return <%= namespaceCompra %>guardar();" />
                &nbsp;&nbsp;
            <% } %>

            <% if (modoEditable && puedeEditarCotizacionPantalla) { %>
                <input type="button"
                       id="<portlet:namespace />btnGuardarCotizacionCompra"
                       value="Guardar cotización"
                       onClick="return <%= namespaceCompra %>guardarCotizacion();" />
                &nbsp;&nbsp;
            <% } %>

            <%--
                No se muestra Editar dentro de la vista.
                La edición continúa disponible en el menú de acciones del
                listado, respetando la observación funcional más reciente.
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
                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeReintentarCotizacion) { %>
                <input type="button"
                       id="<portlet:namespace />btnReintentarCotizacionRequerimientoCompra"
                       value="Notificar prestadores pendientes"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraReintentarCotizacionFormId %>',
                               '<portlet:namespace />btnReintentarCotizacionRequerimientoCompra',
                               '¿Confirma notificar nuevamente a los prestadores pendientes?',
                               'Notificando...'
                       );" />
                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeAnular) { %>
                <span id="<portlet:namespace />btnAnularRequerimientoCompra">
                    <liferay-ui:icon-delete url="<%= botoneraAnularURL %>" />
                </span>
                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeCrearReclamoPrestacional) { %>
                <input type="button"
                       id="<portlet:namespace />btnCrearReclamoPrestacional"
                       value="Crear Reclamo Prestacional"
                       onClick="submitForm(document.getElementById('<%= botoneraReclamoPrestacionalFormId %>')); return false;" />
                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeVerReclamoPrestacional) { %>
                <input type="button"
                       id="<portlet:namespace />btnVerReclamoPrestacional"
                       value="Ver Reclamo Prestacional"
                       onClick="submitForm(document.getElementById('<%= botoneraReclamoPrestacionalFormId %>')); return false;" />
                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraReclamoEnProceso
                    && (botoneraTieneRolVerReclamo
                        || botoneraTieneRolCrearReclamo)) { %>
                <input type="button"
                       value="<%= botoneraRelacionReclamo.isError()
                               ? "Reclamo creado: vinculación pendiente"
                               : "Creación de reclamo en proceso" %>"
                       disabled="disabled" />
                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeImprimir) { %>
                <input type="button"
                       id="<portlet:namespace />btnImprimirRequerimientoCompra"
                       value="Imprimir PDF"
                       onClick="return <%= namespaceCompra %>imprimirRequerimientoCompra();" />
                &nbsp;&nbsp;
            <% } %>

            <input type="button"
                   id="<portlet:namespace />btnVolverCompras"
                   class="compras-btn-volver"
                   value="Volver"
                   onClick="window.location.href='<%= volverURL.toString() %>';" />
        </td>
    </tr>
</table>

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
