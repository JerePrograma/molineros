<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<%!
private String jsArticuloPopup(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}
%>

<%
String articulo = ParamUtil.getString(request, "articulo", "");
String callback = ParamUtil.getString(request, "callback", "");
String articuloError = ParamUtil.getString(request, "articulo_error", "");

int idSector = ParamUtil.getInteger(request, "id_sector", 0);

boolean articuloGuardado = ParamUtil.getBoolean(request, "articulo_guardado", false);
int idArticuloGuardado = ParamUtil.getInteger(request, "id_articulo_guardado", 0);

String articuloDescripcionGuardada =
        ParamUtil.getString(request, "articulo_descripcion_guardada", "");

if (callback == null || !callback.matches("[A-Za-z0-9_]+")) {
    callback = "";
}

PortletURL guardarArticuloURL = renderResponse.createActionURL();
guardarArticuloURL.setWindowState(LiferayWindowState.EXCLUSIVE);
guardarArticuloURL.setParameter("struts_action", "/compras/alta_articulo_popup");
%>

<% if (articuloGuardado && idArticuloGuardado > 0 && callback.length() > 0) { %>
    <script type="text/javascript">
        (function() {
            var callback = '<%= callback %>';

            if (typeof window[callback] == 'function') {
                window[callback](
                    '<%= idArticuloGuardado %>',
                    '<%= jsArticuloPopup(articuloDescripcionGuardada) %>',
                    '<%= idSector %>'
                );
            } else {
                alert('No se encontró la función de retorno del artículo.');
            }
        })();
    </script>
<% } else { %>

<div id="<portlet:namespace />articulo_popup_content">

    <fieldset class="block-labels">
        <legend>Alta de art&iacute;culo</legend>

        <% if (!WebKeysCompras.isEmpty(articuloError)) { %>
            <div class="portlet-msg-error">
                <%= HtmlUtil.escape(articuloError) %>
            </div>

            <br />
        <% } %>

        <form action="<%= guardarArticuloURL.toString() %>"
              method="post"
              name="<portlet:namespace />articuloFm"
              id="<portlet:namespace />articuloFm">

            <input type="hidden"
                   name="<portlet:namespace /><%= Constants.CMD %>"
                   value="saveArticuloPopup" />

            <input type="hidden"
                   name="<portlet:namespace />callback"
                   value="<%= HtmlUtil.escape(callback) %>" />

            <input type="hidden"
                   name="<portlet:namespace />id_sector"
                   value="<%= idSector %>" />

            <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
                <tr>
                    <td><label>Sector:</label></td>
                    <td>
                        <strong>ID <%= idSector %></strong>
                    </td>
                </tr>

                <tr>
                    <td>
                        <label for="<portlet:namespace />articulo_descripcion">
                            Art&iacute;culo:
                        </label>
                    </td>
                    <td>
                        <input type="text"
                               id="<portlet:namespace />articulo_descripcion"
                               name="<portlet:namespace />articulo_descripcion"
                               size="70"
                               maxlength="180"
                               value="<%= HtmlUtil.escape(articulo) %>" />
                    </td>
                </tr>

                <tr>
                    <td colspan="2">&nbsp;</td>
                </tr>

                <tr>
                    <td colspan="2" align="center">
                        <input type="button"
                               value="Aceptar"
                               onClick="<portlet:namespace />guardarArticuloCompra();" />

                        &nbsp;&nbsp;

                        <input type="button"
                               value="Cancelar"
                               onClick="<portlet:namespace />cerrarPopupArticuloCompra();" />
                    </td>
                </tr>
            </table>
        </form>
    </fieldset>

    <script type="text/javascript">
        function <portlet:namespace />guardarArticuloCompra() {
            var descripcion = jQuery.trim(
                    jQuery('#<portlet:namespace />articulo_descripcion').val()
            );

            if (descripcion == '') {
                alert('Debe informar el artículo.');
                jQuery('#<portlet:namespace />articulo_descripcion').focus();
                return false;
            }

            if (<%= idSector %> <= 0) {
                alert('No se recibió el sector del artículo.');
                return false;
            }

            var form = document.getElementById('<portlet:namespace />articuloFm');

            if (!form) {
                alert('No se encontró el formulario de alta de artículo.');
                return false;
            }

            jQuery('#<portlet:namespace />articulo_popup_content').load(
                form.action,
                jQuery(form).serialize()
            );

            return false;
        }

        function <portlet:namespace />cerrarPopupArticuloCompra() {
            var callbackCerrar = '<%= callback %>Cerrar';

            if (typeof window[callbackCerrar] == 'function') {
                window[callbackCerrar]();
            }
        }

        window['<portlet:namespace />guardarArticuloCompra'] =
                <portlet:namespace />guardarArticuloCompra;

        window['<portlet:namespace />cerrarPopupArticuloCompra'] =
                <portlet:namespace />cerrarPopupArticuloCompra;

        jQuery(function() {
            jQuery('#<portlet:namespace />articulo_descripcion').focus();
        });
    </script>

</div>

<% } %>