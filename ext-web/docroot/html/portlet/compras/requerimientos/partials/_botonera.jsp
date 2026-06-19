<%
int botoneraIdRequerimientoActual = req != null ? req.getIdRequerimientoCompra() : 0;
int botoneraEstadoActual = req != null ? req.getEstado() : 0;

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

boolean botoneraPuedeEnviarACotizar =
        botoneraRequerimientoPersistido
        && botoneraTieneRolCotizar
        && WebKeysCompras.puedeEnviarACotizar(botoneraEstadoActual);

boolean botoneraPuedeReintentarCotizacion =
        botoneraRequerimientoPersistido
        && botoneraTieneRolCotizar
        && WebKeysCompras.puedeReintentarNotificaciones(botoneraEstadoActual);

boolean botoneraPuedeAnular =
        botoneraRequerimientoPersistido
        && botoneraTieneRolAnular
        && WebKeysCompras.puedeAnular(botoneraEstadoActual);

boolean botoneraPuedeImprimir =
        botoneraRequerimientoPersistido
        && (botoneraTieneRolView || puedeABM || botoneraTieneRolCotizar);

PortletURL botoneraCambiarEstadoURL = renderResponse.createActionURL();
botoneraCambiarEstadoURL.setWindowState(WindowState.MAXIMIZED);
botoneraCambiarEstadoURL.setParameter("struts_action", "/compras/cambiar_estado_requerimiento");

String botoneraEnviarCotizarFormId =
        namespaceCompra + "enviarCotizarRequerimientoCompraForm";
String botoneraReintentarCotizacionFormId =
        namespaceCompra + "reintentarCotizacionRequerimientoCompraForm";
String botoneraAnularFormId =
        namespaceCompra + "anularRequerimientoCompraForm";
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
                       value="Guardar avance"
                       onClick="return <%= namespaceCompra %>guardarCotizacion(false);" />
                &nbsp;&nbsp;

                <input type="button"
                       id="<portlet:namespace />btnCerrarCotizacionCompra"
                       value="Cerrar cotización"
                       onClick="return <%= namespaceCompra %>guardarCotizacion(true);" />
                &nbsp;&nbsp;
            <% } %>

            <% if (modoVista && puedeABM && editablePorEstado && botoneraRequerimientoPersistido) { %>
                <input type="button"
                       id="<portlet:namespace />btnEditarRequerimientoCompra"
                       value="Editar"
                       onClick="window.location.href='<%= editarURL.toString() %>';" />
                &nbsp;&nbsp;
            <% } %>

            <% if (modoVista && botoneraTieneRolCotizar && req.puedeEditarCotizacion()) { %>
                <input type="button"
                       id="<portlet:namespace />btnCotizarRequerimientoCompra"
                       value="Cotizar"
                       onClick="window.location.href='<%= editarURL.toString() %>';" />
                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeEnviarACotizar) { %>
                <input type="button"
                       id="<portlet:namespace />btnEnviarCotizarRequerimientoCompra"
                       value="Enviar a cotizar"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraEnviarCotizarFormId %>',
                               '<portlet:namespace />btnEnviarCotizarRequerimientoCompra',
                               '¿Confirma enviar a cotizar y notificar a los prestadores habilitados?',
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
                <input type="button"
                       id="<portlet:namespace />btnAnularRequerimientoCompra"
                       value="Anular"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraAnularFormId %>',
                               '<portlet:namespace />btnAnularRequerimientoCompra',
                               '¿Confirma anular el requerimiento?',
                               'Anulando...'
                       );" />
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
