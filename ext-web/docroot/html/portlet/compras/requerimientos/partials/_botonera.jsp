<%
/*
 * ============================================================
 * BOTONERA REQUERIMIENTO COMPRA
 * ============================================================
 */

String namespaceCompra = renderResponse.getNamespace();

/*
 * Activar en true temporalmente si no aparece el boton Cotizar.
 */
boolean debugCotizarRequerimiento = false;

int idRequerimientoActual = 0;
int estadoActual = 0;

if (req != null) {
    idRequerimientoActual = req.getIdRequerimientoCompra();
    estadoActual = req.getEstado();
}

boolean requerimientoPersistido =
        req != null
        && idRequerimientoActual > 0;

boolean puedeCotizarPorEstado =
        requerimientoPersistido
        && WebKeysCompras.validarTransicionEstado(
                estadoActual,
                WebKeysCompras.ESTADO_COTIZADO
        );

boolean puedeCotizarRequerimiento =
        requerimientoPersistido
        && modoVista
        && !modoEditable
        && puedeABM
        && puedeCotizarPorEstado;

String cotizarFormId =
        namespaceCompra + "cotizarRequerimientoCompraForm";

PortletURL cotizarRequerimientoURL =
        renderResponse.createActionURL();

cotizarRequerimientoURL.setWindowState(WindowState.MAXIMIZED);

cotizarRequerimientoURL.setParameter(
        "struts_action",
        "/compras/cambiar_estado_requerimiento"
);
%>

<% if (debugCotizarRequerimiento) { %>
    <div style="margin:10px 0; padding:10px; border:1px solid #999; background:#ffffe0;">
        <strong>DEBUG COTIZAR REQUERIMIENTO</strong><br />

        req != null:
        <%= String.valueOf(req != null) %><br />

        idRequerimientoActual:
        <%= String.valueOf(idRequerimientoActual) %><br />

        estadoActual:
        <%= String.valueOf(estadoActual) %><br />

        modoVista:
        <%= String.valueOf(modoVista) %><br />

        modoEditable:
        <%= String.valueOf(modoEditable) %><br />

        puedeABM:
        <%= String.valueOf(puedeABM) %><br />

        requerimientoPersistido:
        <%= String.valueOf(requerimientoPersistido) %><br />

        puedeCotizarPorEstado:
        <%= String.valueOf(puedeCotizarPorEstado) %><br />

        puedeCotizarRequerimiento:
        <%= String.valueOf(puedeCotizarRequerimiento) %><br />
    </div>
<% } %>

<% if (puedeCotizarRequerimiento) { %>
    <form action="<%= cotizarRequerimientoURL.toString() %>"
          method="post"
          id="<%= cotizarFormId %>"
          style="display:none;">

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(idRequerimientoActual) %>" />

        <input type="hidden"
               name="<portlet:namespace />estado_nuevo"
               value="<%= String.valueOf(WebKeysCompras.ESTADO_COTIZADO) %>" />
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

            <% if (modoVista && puedeABM && editablePorEstado && requerimientoPersistido) { %>
                <input type="button"
                       id="<portlet:namespace />btnEditarRequerimientoCompra"
                       value="Editar"
                       onClick="window.location.href='<%= editarURL.toString() %>';" />

                &nbsp;&nbsp;
            <% } %>

            <% if (puedeCotizarRequerimiento) { %>
                <input type="button"
                       id="<portlet:namespace />btnCotizarRequerimientoCompra"
                       value="Cotizar"
                       onClick="return <%= namespaceCompra %>cotizarRequerimientoCompra();" />

                &nbsp;&nbsp;
            <% } %>

            <% if (requerimientoPersistido) { %>
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
    function <%= namespaceCompra %>cotizarRequerimientoCompra() {
        var form = document.getElementById('<%= cotizarFormId %>');
        var btn = document.getElementById('<portlet:namespace />btnCotizarRequerimientoCompra');

        if (!form) {
            alert('No se pudo preparar la cotización del requerimiento.');
            return false;
        }

        if (!confirm('Confirma cotizar el requerimiento?')) {
            return false;
        }

        if (btn) {
            btn.disabled = true;
            btn.value = 'Cotizando...';
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
                alert(
                    'No se pudo imprimir automáticamente el PDF. ' +
                    'Revise que el navegador permita imprimir contenido embebido.'
                );
            }
        };

        iframe.src = url;

        return false;
    }
</script>