<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<%
String articulo = ParamUtil.getString(request, "articulo", "");
String callback = ParamUtil.getString(request, "callback", "");

if (callback == null || !callback.matches("[A-Za-z0-9_]+")) {
    callback = "";
}
%>

<fieldset class="block-labels">
    <legend>Alta de artículo</legend>

    <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
        <tr>
            <td><label for="<portlet:namespace />articulo_descripcion">Artículo:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />articulo_descripcion"
                       name="<portlet:namespace />articulo_descripcion"
                       size="70"
                       maxlength="255"
                       value="<%= HtmlUtil.escape(articulo) %>" />
            </td>
        </tr>

        <tr>
            <td><label for="<portlet:namespace />articulo_observaciones">Observaciones:</label></td>
            <td>
                <input type="text"
                       id="<portlet:namespace />articulo_observaciones"
                       name="<portlet:namespace />articulo_observaciones"
                       size="70"
                       maxlength="500"
                       value="" />
            </td>
        </tr>

        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="2" align="center">
                <input type="button"
                       value="Aceptar"
                       onClick="<portlet:namespace />aceptarArticuloCompra();" />

                &nbsp;&nbsp;

                <input type="button"
                       value="Cancelar"
                       onClick="<portlet:namespace />cerrarPopupArticuloCompra();" />
            </td>
        </tr>
    </table>
</fieldset>

<script type="text/javascript">
    function <portlet:namespace />aceptarArticuloCompra() {
        var descripcion = jQuery.trim(jQuery('#<portlet:namespace />articulo_descripcion').val());

        if (descripcion == '') {
            alert('Debe informar el artículo.');
            jQuery('#<portlet:namespace />articulo_descripcion').focus();
            return false;
        }

        <% if (callback.length() > 0) { %>
            if (typeof window['<%= callback %>'] == 'function') {
                window['<%= callback %>'](descripcion);
            } else {
                alert('No se encontró la función de retorno del artículo.');
            }
        <% } else { %>
            alert('No se recibió callback para devolver el artículo.');
        <% } %>

        return false;
    }

    function <portlet:namespace />cerrarPopupArticuloCompra() {
        if (typeof window['<%= callback %>Cerrar'] == 'function') {
            window['<%= callback %>Cerrar']();
        }
    }

    jQuery(function() {
        jQuery('#<portlet:namespace />articulo_descripcion').focus();
    });
</script>