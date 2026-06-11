<%
/*
 * ============================================================
 * BOTONERA REQUERIMIENTO COMPRA
 * ============================================================
 *
 * Este partial asume que el JSP padre ya declaró:
 *
 * - String namespaceCompra
 * - RequerimientoCompra req
 * - boolean modoEditable
 * - boolean modoVista
 * - boolean puedeABM
 * - boolean editablePorEstado
 * - PortletURL editarURL
 * - PortletURL volverURL
 * - PortletURL imprimirURL
 */
int botoneraIdRequerimientoActual = 0;
int botoneraEstadoActual = 0;

if (req != null) {
    botoneraIdRequerimientoActual = req.getIdRequerimientoCompra();
    botoneraEstadoActual = req.getEstado();
}

boolean botoneraRequerimientoPersistido =
        req != null
        && botoneraIdRequerimientoActual > 0;

boolean botoneraTieneRolAnular =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS);

boolean botoneraTieneRolAutorizar =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_AUTORIZAR_COMPRAS);

boolean botoneraTieneRolCotizar =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS);

boolean botoneraTieneRolOrdenCompra =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ORDEN_COMPRA_COMPRAS);

boolean botoneraPuedeEnviarAAutorizar =
        botoneraRequerimientoPersistido
        && puedeABM
        && WebKeysCompras.validarTransicionEstado(
                botoneraEstadoActual,
                WebKeysCompras.ESTADO_REQUERIMIENTO
        );

boolean botoneraPuedeAutorizar =
        botoneraRequerimientoPersistido
        && botoneraTieneRolAutorizar
        && WebKeysCompras.validarTransicionEstado(
                botoneraEstadoActual,
                WebKeysCompras.ESTADO_AUTORIZADO
        );

boolean botoneraPuedeIniciarCotizaciones =
        botoneraRequerimientoPersistido
        && botoneraTieneRolCotizar
        && WebKeysCompras.validarTransicionEstado(
                botoneraEstadoActual,
                WebKeysCompras.ESTADO_COTIZACIONES
        );

boolean botoneraPuedeGenerarOrdenCompra =
        botoneraRequerimientoPersistido
        && botoneraTieneRolOrdenCompra
        && WebKeysCompras.validarTransicionEstado(
                botoneraEstadoActual,
                WebKeysCompras.ESTADO_ORDEN_COMPRA
        );

boolean botoneraPuedeAnular =
        botoneraRequerimientoPersistido
        && (puedeABM || botoneraTieneRolAnular)
        && WebKeysCompras.validarTransicionEstado(
                botoneraEstadoActual,
                WebKeysCompras.ESTADO_ANULADO
        );

PortletURL botoneraCambiarEstadoURL =
        renderResponse.createActionURL();

botoneraCambiarEstadoURL.setWindowState(WindowState.MAXIMIZED);

botoneraCambiarEstadoURL.setParameter(
        "struts_action",
        "/compras/cambiar_estado_requerimiento"
);

String botoneraEnviarAutorizarFormId =
        namespaceCompra + "enviarAutorizarRequerimientoCompraForm";

String botoneraAutorizarFormId =
        namespaceCompra + "autorizarRequerimientoCompraForm";

String botoneraIniciarCotizacionesFormId =
        namespaceCompra + "iniciarCotizacionesRequerimientoCompraForm";

String botoneraGenerarOrdenFormId =
        namespaceCompra + "generarOrdenCompraForm";

String botoneraAnularFormId =
        namespaceCompra + "anularRequerimientoCompraForm";
%>

<% if (botoneraPuedeEnviarAAutorizar) { %>
    <form action="<%= botoneraCambiarEstadoURL.toString() %>"
          method="post"
          id="<%= botoneraEnviarAutorizarFormId %>"
          style="display:none;">

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(botoneraIdRequerimientoActual) %>" />

        <input type="hidden"
               name="<portlet:namespace />estado_nuevo"
               value="<%= String.valueOf(WebKeysCompras.ESTADO_REQUERIMIENTO) %>" />
    </form>
<% } %>

<% if (botoneraPuedeAutorizar) { %>
    <form action="<%= botoneraCambiarEstadoURL.toString() %>"
          method="post"
          id="<%= botoneraAutorizarFormId %>"
          style="display:none;">

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(botoneraIdRequerimientoActual) %>" />

        <input type="hidden"
               name="<portlet:namespace />estado_nuevo"
               value="<%= String.valueOf(WebKeysCompras.ESTADO_AUTORIZADO) %>" />
    </form>
<% } %>

<% if (botoneraPuedeIniciarCotizaciones) { %>
    <form action="<%= botoneraCambiarEstadoURL.toString() %>"
          method="post"
          id="<%= botoneraIniciarCotizacionesFormId %>"
          style="display:none;">

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(botoneraIdRequerimientoActual) %>" />

        <input type="hidden"
               name="<portlet:namespace />estado_nuevo"
               value="<%= String.valueOf(WebKeysCompras.ESTADO_COTIZACIONES) %>" />
    </form>
<% } %>

<% if (botoneraPuedeGenerarOrdenCompra) { %>
    <form action="<%= botoneraCambiarEstadoURL.toString() %>"
          method="post"
          id="<%= botoneraGenerarOrdenFormId %>"
          style="display:none;">

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(botoneraIdRequerimientoActual) %>" />

        <input type="hidden"
               name="<portlet:namespace />estado_nuevo"
               value="<%= String.valueOf(WebKeysCompras.ESTADO_ORDEN_COMPRA) %>" />
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
            <% if (modoEditable) { %>
                <input type="button"
                       id="<portlet:namespace />btnGuardarCompras"
                       value="Guardar"
                       onClick="return <%= namespaceCompra %>guardar();" />

                &nbsp;&nbsp;
            <% } %>

            <% if (modoVista && puedeABM && editablePorEstado && botoneraRequerimientoPersistido) { %>
                <input type="button"
                       id="<portlet:namespace />btnEditarRequerimientoCompra"
                       value="Editar"
                       onClick="window.location.href='<%= editarURL.toString() %>';" />

                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeEnviarAAutorizar) { %>
                <input type="button"
                       id="<portlet:namespace />btnEnviarAutorizarRequerimientoCompra"
                       value="Enviar a autorizar"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraEnviarAutorizarFormId %>',
                               '<portlet:namespace />btnEnviarAutorizarRequerimientoCompra',
                               'Confirma enviar el requerimiento a autorizacion?',
                               'Enviando...'
                       );" />

                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeAutorizar) { %>
                <input type="button"
                       id="<portlet:namespace />btnAutorizarRequerimientoCompra"
                       value="Autorizar"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraAutorizarFormId %>',
                               '<portlet:namespace />btnAutorizarRequerimientoCompra',
                               'Confirma autorizar el requerimiento?',
                               'Autorizando...'
                       );" />

                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeIniciarCotizaciones) { %>
                <input type="button"
                       id="<portlet:namespace />btnIniciarCotizacionesRequerimientoCompra"
                       value="Iniciar cotizaciones"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraIniciarCotizacionesFormId %>',
                               '<portlet:namespace />btnIniciarCotizacionesRequerimientoCompra',
                               'Confirma iniciar cotizaciones para el requerimiento?',
                               'Iniciando...'
                       );" />

                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraPuedeGenerarOrdenCompra) { %>
                <input type="button"
                       id="<portlet:namespace />btnGenerarOrdenCompra"
                       value="Generar orden de compra"
                       onClick="return <%= namespaceCompra %>cambiarEstadoRequerimientoCompra(
                               '<%= botoneraGenerarOrdenFormId %>',
                               '<portlet:namespace />btnGenerarOrdenCompra',
                               'Confirma generar la orden de compra?',
                               'Generando...'
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
                               'Confirma anular el requerimiento?',
                               'Anulando...'
                       );" />

                &nbsp;&nbsp;
            <% } %>

            <% if (botoneraRequerimientoPersistido) { %>
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
            alert('No se pudo preparar la impresion del requerimiento.');
            return false;
        }

        var url = '<%= imprimirURL.toString() %>';

        url += (url.indexOf('?') >= 0 ? '&' : '?') + '_ts=' + new Date().getTime();

        iframe.onload = function() {
            try {
                iframe.contentWindow.focus();
                iframe.contentWindow.print();
            } catch (e) {
                alert(
                    'No se pudo imprimir automaticamente el PDF. ' +
                    'Revise que el navegador permita imprimir contenido embebido.'
                );
            }
        };

        iframe.src = url;

        return false;
    }
</script>