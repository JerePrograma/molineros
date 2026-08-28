<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>

<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0L);

RequerimientoCompra requerimiento =
        (RequerimientoCompra) request.getAttribute(
                WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW
        );

List<PrestadorCotizacion> prestadores =
        (List<PrestadorCotizacion>) request.getAttribute(
                WebKeysCompras.PRESTADORES_HABILITADOS_COTIZACION
        );

if (prestadores == null) {
    prestadores = new ArrayList<PrestadorCotizacion>();
}

String error =
        (String) request.getAttribute(
                WebKeysCompras
                        .ERROR_PRESTADORES_HABILITADOS_COTIZACION
        );

Set<String> rubros = new LinkedHashSet<String>();

if (requerimiento != null && requerimiento.getDetalles() != null) {
    List<RequerimientoCompraDetalle> detalles =
            requerimiento.getDetalles();

    for (int i = 0; i < detalles.size(); i++) {
        RequerimientoCompraDetalle detalle = detalles.get(i);

        if (detalle != null
                && detalle.getIdTipoPrestacionInt() > 0
                && !WebKeysCompras.isEmpty(
                        detalle.getTipoPrestacionDescripcion()
                )) {

            rubros.add(
                    detalle.getTipoPrestacionDescripcion()
            );
        }
    }
}
%>

<div class="compras-prestadores-habilitados-cotizacion">
    <fieldset class="block-labels">
        <legend>Prestadores habilitados para cotizar</legend>

        <table class="lfr-table" width="100%">
            <tr>
                <td><strong>Sector:</strong></td>
                <td>
                    <%= HtmlUtil.escape(
                            requerimiento != null
                                    ? requerimiento
                                            .getSectorDescripcionVisible()
                                    : ""
                    ) %>
                </td>
            </tr>
            <tr>
                <td><strong>Tipos de cotizaci&#243;n / rubros:</strong></td>
                <td>
                    <% if (rubros.isEmpty()) { %>
                        Sin tipos de cotizaci&#243;n guardados.
                    <% } else {
                        int indiceRubro = 0;

                        for (String rubro : rubros) {
                            if (indiceRubro > 0) {
                    %>, <%
                            }
                    %><%= HtmlUtil.escape(rubro) %><%
                            indiceRubro++;
                        }
                    } %>
                </td>
            </tr>
            <tr>
                <td><strong>Cantidad:</strong></td>
                <td><%= prestadores.size() %></td>
            </tr>
        </table>
    </fieldset>

    <% if (!WebKeysCompras.isEmpty(error)) { %>
        <div class="portlet-msg-error">
            <%= HtmlUtil.escape(error) %>
        </div>
    <% } else if (rubros.isEmpty()) { %>
        <div class="portlet-msg-info">
            El requerimiento no tiene tipos de cotizaci&#243;n guardados para
            determinar prestadores compatibles.
        </div>
    <% } else if (prestadores.isEmpty()) { %>
        <div class="portlet-msg-info">
            No se encontraron prestadores vigentes, habilitados para cotizar
            y con rubros compatibles con este requerimiento.
        </div>
    <% } else { %>
        <table class="lfr-table taglib-search-iterator" width="100%">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Raz&#243;n social</th>
                    <th>CUIT</th>
                    <th>Tipo de prestador</th>
                </tr>
            </thead>
            <tbody>
                <%
                int numeroPrestador = 0;

                for (int i = 0; i < prestadores.size(); i++) {
                    PrestadorCotizacion prestador = prestadores.get(i);

                    if (prestador == null) {
                        continue;
                    }

                    numeroPrestador++;
                %>
                    <tr>
                        <td><%= numeroPrestador %></td>
                        <td><%= HtmlUtil.escape(
                                prestador.getRazonSocialVisible()
                        ) %></td>
                        <td><%= HtmlUtil.escape(
                                prestador.getCuitVisible()
                        ) %></td>
                        <td><%= HtmlUtil.escape(
                                prestador.getTipoPrestadorVisible()
                        ) %></td>
                    </tr>
                <%
                }
                %>
            </tbody>
        </table>
    <% } %>

    <div class="portlet-msg-info">
        Se consideran habilitados los prestadores vigentes, marcados para
        cotizar y con al menos un rubro compatible. La consulta utiliza los
        datos guardados; al enviar se vuelven a validar los prestadores.
    </div>
</div>
