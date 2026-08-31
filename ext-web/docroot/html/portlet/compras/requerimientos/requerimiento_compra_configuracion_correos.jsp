<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra" %>

<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0L);

List<TipoPrestacionCompra> rubrosConfiguracionCorreos =
        (List<TipoPrestacionCompra>) request.getAttribute(
                WebKeysCompras.TIPOS_PRESTACION_REQUERIMIENTO_COMPRA
        );

if (rubrosConfiguracionCorreos == null) {
    rubrosConfiguracionCorreos =
            new ArrayList<TipoPrestacionCompra>();
}

List<PrestadorCotizacion> prestadoresConfiguracionCorreos =
        (List<PrestadorCotizacion>) request.getAttribute(
                WebKeysCompras.PRESTADORES_HABILITADOS_COTIZACION
        );

if (prestadoresConfiguracionCorreos == null) {
    prestadoresConfiguracionCorreos =
            new ArrayList<PrestadorCotizacion>();
}

String errorConfiguracionCorreos =
        (String) request.getAttribute(
                WebKeysCompras
                        .ERROR_PRESTADORES_HABILITADOS_COTIZACION
        );

int idRubroConfiguracionCorreos =
        ParamUtil.getInteger(
                renderRequest,
                "id_tipo_prestacion",
                0
        );

TipoPrestacionCompra rubroConfiguracionCorreos = null;

for (int i = 0; i < rubrosConfiguracionCorreos.size(); i++) {
    TipoPrestacionCompra rubro = rubrosConfiguracionCorreos.get(i);

    if (rubro != null
            && rubro.getIdInt() == idRubroConfiguracionCorreos) {

        rubroConfiguracionCorreos = rubro;
        break;
    }
}

PortletURL configuracionCorreosURL = renderResponse.createRenderURL();
configuracionCorreosURL.setWindowState(LiferayWindowState.MAXIMIZED);
configuracionCorreosURL.setParameter("struts_action", "/compras/view");
configuracionCorreosURL.setParameter(
        "tabs1",
        "configuracion-de-correos"
);
pageContext.setAttribute(
        "configuracionCorreosURL",
        configuracionCorreosURL
);
%>

<form action="<%= configuracionCorreosURL %>"
      method="get"
      name="<portlet:namespace />fmConfiguracionCorreos"
      onSubmit="submitForm(this); return false;">

    <liferay-portlet:renderURLParams
            varImpl="configuracionCorreosURL" />

    <fieldset class="block-labels">
        <legend>Consulta de correos por rubro</legend>

        <table class="lfr-table">
            <tr>
                <td>
                    <label for="<portlet:namespace />id_tipo_prestacion">Rubro:</label>
                </td>
                <td>
                    <select id="<portlet:namespace />id_tipo_prestacion"
                            name="<portlet:namespace />id_tipo_prestacion">

                        <option value="0"
                                <%= idRubroConfiguracionCorreos == 0
                                        ? "selected=\"selected\""
                                        : "" %>>
                            Seleccione
                        </option>

                        <%
                        for (int i = 0;
                             i < rubrosConfiguracionCorreos.size();
                             i++) {

                            TipoPrestacionCompra rubro =
                                    rubrosConfiguracionCorreos.get(i);

                            if (rubro == null || rubro.getIdInt() <= 0) {
                                continue;
                            }
                        %>
                            <option value="<%= rubro.getIdInt() %>"
                                    <%= rubro.getIdInt()
                                            == idRubroConfiguracionCorreos
                                                    ? "selected=\"selected\""
                                                    : "" %>>

                                <%= HtmlUtil.escape(
                                        rubro.getDescripcionVisible()
                                ) %>
                            </option>
                        <%
                        }
                        %>
                    </select>
                </td>
                <td>
                    <input type="submit"
                           value="Consultar" />
                </td>
            </tr>
        </table>
    </fieldset>
</form>

<% if (!WebKeysCompras.isEmpty(errorConfiguracionCorreos)) { %>
    <div class="portlet-msg-error">
        <%= HtmlUtil.escape(errorConfiguracionCorreos) %>
    </div>
<% } else if (idRubroConfiguracionCorreos == 0) { %>
    <div class="portlet-msg-info">
        Seleccione un rubro para consultar los correos configurados.
    </div>
<% } else if (rubroConfiguracionCorreos == null) { %>
    <div class="portlet-msg-error">
        El rubro seleccionado no es v&#225;lido.
    </div>
<% } else { %>
    <fieldset class="block-labels">
        <legend>Correos configurados</legend>

        <table class="lfr-table" width="100%">
            <tr>
                <td><strong>Rubro:</strong></td>
                <td>
                    <%= HtmlUtil.escape(
                            rubroConfiguracionCorreos
                                    .getDescripcionVisible()
                    ) %>
                </td>
                <td><strong>Cantidad de prestadores:</strong></td>
                <td><%= prestadoresConfiguracionCorreos.size() %></td>
            </tr>
        </table>
    </fieldset>

    <% if (prestadoresConfiguracionCorreos.isEmpty()) { %>
        <div class="portlet-msg-info">
            No se encontraron prestadores vigentes y habilitados para
            cotizar asociados al rubro seleccionado.
        </div>
    <% } else { %>
        <table class="lfr-table taglib-search-iterator" width="100%">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Raz&#243;n social</th>
                    <th>CUIT</th>
                    <th>Tipo de prestador</th>
                    <th>Correos configurados</th>
                </tr>
            </thead>
            <tbody>
                <%
                int numeroPrestadorConfiguracionCorreos = 0;

                for (int i = 0;
                     i < prestadoresConfiguracionCorreos.size();
                     i++) {

                    PrestadorCotizacion prestador =
                            prestadoresConfiguracionCorreos.get(i);

                    if (prestador == null) {
                        continue;
                    }

                    numeroPrestadorConfiguracionCorreos++;
                %>
                    <tr>
                        <td>
                            <%= numeroPrestadorConfiguracionCorreos %>
                        </td>
                        <td>
                            <%= HtmlUtil.escape(
                                    prestador.getRazonSocialVisible()
                            ) %>
                        </td>
                        <td>
                            <%= HtmlUtil.escape(
                                    prestador.getCuitVisible()
                            ) %>
                        </td>
                        <td>
                            <%= HtmlUtil.escape(
                                    prestador.getTipoPrestadorVisible()
                            ) %>
                        </td>
                        <td>
                            <% if (WebKeysCompras.isEmpty(
                                    prestador.getEmailVisible()
                            )) { %>
                                Sin correos v&#225;lidos registrados.
                            <% } else { %>
                                <%= HtmlUtil.escape(
                                        prestador.getEmailVisible()
                                ) %>
                            <% } %>
                        </td>
                    </tr>
                <%
                }
                %>
            </tbody>
        </table>
    <% } %>

    <div class="portlet-msg-info">
        Se muestran los correos reales registrados de los prestadores
        vigentes, habilitados para cotizar y asociados al rubro seleccionado.
        Esta consulta no modifica los contactos ni el env&#237;o.
    </div>
<% } %>
