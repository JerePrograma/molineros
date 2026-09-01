<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Empresa" %>

<%!
private String jsEmpresaCotizacion(Object value) {
    if (value == null) {
        return "";
    }

    return String.valueOf(value)
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("&", "\\x26")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
}
%>

<%
List<Empresa> empresas =
        (List<Empresa>) renderRequest.getAttribute(
                WebKeysCompras.BUSQUEDA_EMPRESAS_COTIZACION
        );

if (empresas == null) {
    empresas = new ArrayList<Empresa>();
}

boolean busquedaRealizada =
        Boolean.TRUE.equals(
                renderRequest.getAttribute(
                        "compras.empresas.busqueda.realizada"
                )
        );

boolean busquedaLimitada =
        Boolean.TRUE.equals(
                renderRequest.getAttribute(
                        "compras.empresas.busqueda.limitada"
                )
        );

String errorBusquedaEmpresa =
        (String) renderRequest.getAttribute(
                "compras.empresas.busqueda.error"
        );

String cuitBusquedaEmpresa =
        ParamUtil.getString(renderRequest, "cuit", "");

String sucursalBusquedaEmpresa =
        ParamUtil.getString(renderRequest, "sucu", "");

String descripcionBusquedaEmpresa =
        ParamUtil.getString(renderRequest, "descripcion", "");

int idRequerimientoCompraEmpresa =
        ParamUtil.getInteger(
                renderRequest,
                WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA,
                0
        );

PortletURL buscarEmpresasURL =
        renderResponse.createRenderURL();

buscarEmpresasURL.setWindowState(
        LiferayWindowState.EXCLUSIVE
);

buscarEmpresasURL.setParameter(
        "struts_action",
        "/compras/buscar_empresas_cotizacion"
);

buscarEmpresasURL.setParameter(
        WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA,
        String.valueOf(idRequerimientoCompraEmpresa)
);
%>

<div class="compras-busqueda-empresa-cotizacion">
    <fieldset class="block-labels">
        <legend>Buscar Empresa</legend>

        <table class="lfr-table" width="100%">
            <tr>
                <td>
                    <label for="<portlet:namespace />empresa_busqueda_cuit">
                        CUIT:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />empresa_busqueda_cuit"
                           maxlength="11"
                           size="15"
                           value="<%= HtmlUtil.escape(cuitBusquedaEmpresa) %>" />
                </td>
                <td>
                    <label for="<portlet:namespace />empresa_busqueda_sucursal">
                        Sucursal:
                    </label>
                </td>
                <td>
                    <input type="text"
                           id="<portlet:namespace />empresa_busqueda_sucursal"
                           maxlength="6"
                           size="8"
                           value="<%= HtmlUtil.escape(sucursalBusquedaEmpresa) %>" />
                </td>
            </tr>
            <tr>
                <td>
                    <label for="<portlet:namespace />empresa_busqueda_descripcion">
                        Razón social:
                    </label>
                </td>
                <td colspan="2">
                    <input type="text"
                           id="<portlet:namespace />empresa_busqueda_descripcion"
                           maxlength="200"
                           size="55"
                           value="<%= HtmlUtil.escape(descripcionBusquedaEmpresa) %>" />
                </td>
                <td>
                    <input type="button"
                           id="<portlet:namespace />empresa_busqueda_boton"
                           value="Buscar"
                           onclick="return <portlet:namespace />buscarEmpresasCotizacionPopup();" />
                </td>
            </tr>
        </table>
    </fieldset>

    <% if (!WebKeysCompras.isEmpty(errorBusquedaEmpresa)) { %>
        <div class="portlet-msg-error">
            <%= HtmlUtil.escape(errorBusquedaEmpresa) %>
        </div>
    <% } else if (!busquedaRealizada) { %>
        <div class="portlet-msg-info">
            Ingrese un CUIT completo o al menos 3 caracteres de razón social.
            La sucursal permite refinar la búsqueda.
        </div>
    <% } else if (empresas.isEmpty()) { %>
        <div class="portlet-msg-info">
            No se encontraron Empresas activas para los filtros informados.
        </div>
    <% } else { %>
        <% if (busquedaLimitada) { %>
            <div class="portlet-msg-info">
                Se muestran los primeros 100 resultados activos. Refine la búsqueda.
            </div>
        <% } %>

        <table class="lfr-table taglib-search-iterator" width="100%">
            <thead>
                <tr>
                    <th>CUIT</th>
                    <th>Sucursal</th>
                    <th>Razón social</th>
                    <th>Seleccionar</th>
                </tr>
            </thead>
            <tbody>
                <%
                for (int i = 0; i < empresas.size(); i++) {
                    Empresa empresa = empresas.get(i);

                    if (empresa == null) {
                        continue;
                    }
                %>
                    <tr>
                        <td><%= HtmlUtil.escape(empresa.getCuit()) %></td>
                        <td><%= HtmlUtil.escape(empresa.getSucursal()) %></td>
                        <td><%= HtmlUtil.escape(empresa.getRazon_soc()) %></td>
                        <td>
                            <a href="javascript:void(0)"
                               onclick="return <portlet:namespace />seleccionarEmpresaCotizacionCompra(
                                       '<%= HtmlUtil.escape(jsEmpresaCotizacion(empresa.getCuit())) %>',
                                       '<%= HtmlUtil.escape(jsEmpresaCotizacion(empresa.getSucursal())) %>',
                                       '<%= HtmlUtil.escape(jsEmpresaCotizacion(empresa.getRazon_soc())) %>'
                               );">
                                Seleccionar
                            </a>
                        </td>
                    </tr>
                <%
                }
                %>
            </tbody>
        </table>
    <% } %>
</div>

<script type="text/javascript">
    function <portlet:namespace />buscarEmpresasCotizacionPopup() {
        var cuit = jQuery.trim(
                jQuery('#<portlet:namespace />empresa_busqueda_cuit').val()
                        || ''
        );

        var sucursal = jQuery.trim(
                jQuery('#<portlet:namespace />empresa_busqueda_sucursal').val()
                        || ''
        );

        var descripcion = jQuery.trim(
                jQuery('#<portlet:namespace />empresa_busqueda_descripcion').val()
                        || ''
        );

        if (cuit == '' && descripcion == '') {
            alert(
                    'Debe informar un CUIT completo o al menos '
                            + '3 caracteres de razón social.'
            );
            return false;
        }

        if (cuit != '' && !/^[0-9]{11}$/.test(cuit)) {
            alert('El CUIT debe contener exactamente 11 dígitos.');
            return false;
        }

        if (cuit == '' && descripcion != '' && descripcion.length < 3) {
            alert('La razón social debe contener al menos 3 caracteres.');
            return false;
        }

        var popup = window['<portlet:namespace />popupEmpresaCotizacion'];

        if (!popup) {
            alert('No se pudo identificar la ventana de búsqueda.');
            return false;
        }

        var boton = jQuery(
                '#<portlet:namespace />empresa_busqueda_boton'
        );

        if (boton.attr('disabled')) {
            return false;
        }

        boton.attr('disabled', 'disabled');

        var url =
                '<%= jsEmpresaCotizacion(buscarEmpresasURL.toString()) %>'
                        + '&buscar=true'
                        + '&cuit=' + encodeURIComponent(cuit)
                        + '&sucu=' + encodeURIComponent(sucursal)
                        + '&descripcion=' + encodeURIComponent(descripcion);

        jQuery(popup).load(
                url,
                function() {
                    boton.removeAttr('disabled');
                }
        );
        return false;
    }
</script>
