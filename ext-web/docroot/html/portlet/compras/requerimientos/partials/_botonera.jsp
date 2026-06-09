<%
boolean puedeCotizarRequerimiento =
        req != null
        && req.getIdRequerimientoCompra() > 0
        && puedeABM
        && req.puedeCotizar()
        && !modoEditable;

String cotizarFormId =
        renderResponse.getNamespace() + "cotizarRequerimientoCompraForm";

javax.portlet.PortletURL cotizarRequerimientoURL =
        renderResponse.createActionURL();

cotizarRequerimientoURL.setWindowState(javax.portlet.WindowState.MAXIMIZED);
cotizarRequerimientoURL.setParameter(
        "struts_action",
        "/compras/cambiar_estado_requerimiento"
);
%>

<c:if test="<%= puedeCotizarRequerimiento %>">
    <form action="<%= cotizarRequerimientoURL.toString() %>"
          method="post"
          id="<%= cotizarFormId %>"
          style="display:none;">
        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= String.valueOf(req.getIdRequerimientoCompra()) %>" />

        <input type="hidden"
               name="<portlet:namespace />estado_nuevo"
               value="<%= String.valueOf(WebKeysCompras.ESTADO_COTIZADO) %>" />
    </form>
</c:if>

<table class="lfr-table">
    <tr>
        <td>
            <c:if test="<%= modoEditable %>">
                <input type="button"
                       id="<portlet:namespace />btnGuardarCompras"
                       value="Guardar"
                       onClick="return <%= namespaceCompra %>guardar();" />

                &nbsp;&nbsp;
            </c:if>

            <c:if test="<%= modoVista && puedeABM && editablePorEstado && req.getIdRequerimientoCompra() > 0 %>">
                <input type="button"
                       id="<portlet:namespace />btnEditarRequerimientoCompra"
                       value="Editar"
                       onClick="window.location.href='<%= editarURL.toString() %>';" />

                &nbsp;&nbsp;
            </c:if>

            <c:if test="<%= puedeCotizarRequerimiento %>">
                <input type="button"
                       id="<portlet:namespace />btnCotizarRequerimientoCompra"
                       value="Cotizar"
                       onClick="return <portlet:namespace />cotizarRequerimientoCompra();" />

                &nbsp;&nbsp;
            </c:if>

            <c:if test="<%= req != null && req.getIdRequerimientoCompra() > 0 %>">
                <input type="button"
                       id="<portlet:namespace />btnImprimirRequerimientoCompra"
                       value="Imprimir PDF"
                       onClick="return <portlet:namespace />imprimirRequerimientoCompra();" />

                &nbsp;&nbsp;
            </c:if>

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
    function <portlet:namespace />cotizarRequerimientoCompra() {
        var form = document.getElementById('<%= cotizarFormId %>');

        if (!form) {
            alert('No se pudo preparar la cotización del requerimiento.');
            return false;
        }

        if (confirm('Confirma cotizar el requerimiento?')) {
            submitForm(form);
        }

        return false;
    }

    function <portlet:namespace />imprimirRequerimientoCompra() {
        var iframe = document.getElementById('<portlet:namespace />iframeImpresionRequerimientoCompra');

        if (!iframe) {
            alert('No se pudo preparar la impresión del requerimiento.');
            return false;
        }

        var url = '<%= imprimirURL.toString() %>';

        /*
         * Cache buster para evitar que el navegador reutilice un PDF viejo.
         */
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