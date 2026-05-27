<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
int idRequerimientoCompra = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);

RequerimientoCompra req =
        (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);

if (req == null && idRequerimientoCompra > 0) {
    req = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);
}

if (req == null) {
    req = new RequerimientoCompra();
}

renderRequest.setAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW, req);
renderRequest.setAttribute(WebKeysCompras.SOLO_LECTURA_ATTR, Boolean.TRUE);

boolean puedeABM =
        user != null
        && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

boolean puedeAnular =
        user != null
        && (PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS) || puedeABM);

boolean puedeCotizarEstado = req.puedeCotizar() && puedeABM;
boolean puedeAnularEstado = req.puedeAnular() && puedeAnular;

PortletURL volverURL = renderResponse.createRenderURL();
volverURL.setWindowState(WindowState.MAXIMIZED);
volverURL.setParameter("struts_action", "/compras/view");

PortletURL editarURL = renderResponse.createRenderURL();
editarURL.setWindowState(WindowState.MAXIMIZED);
editarURL.setParameter("struts_action", "/compras/editar_requerimiento");
editarURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

PortletURL cambiarEstadoURL = renderResponse.createActionURL();
cambiarEstadoURL.setWindowState(WindowState.MAXIMIZED);
cambiarEstadoURL.setParameter("struts_action", "/compras/cambiar_estado_requerimiento");
%>

<fieldset class="block-labels">
    <legend>Requerimiento de compra</legend>

    <table class="lfr-table">
        <tr>
            <td><label>ID:</label></td>
            <td><%= HtmlUtil.escape(req.getIdString()) %></td>

            <td><label>Estado:</label></td>
            <td><strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong></td>
        </tr>

        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Sector:</label></td>
            <td><%= HtmlUtil.escape(req.getSectorDescripcionVisible()) %></td>

            <td><label>Requiere afiliado:</label></td>
            <td><%= HtmlUtil.escape(req.getRequiereAfiliadoDescripcion()) %></td>
        </tr>

        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Alta:</label></td>
            <td><%= HtmlUtil.escape(req.getAltaFechaAsString()) %></td>

            <td><label>Usuario alta:</label></td>
            <td><%= HtmlUtil.escape(req.getAltaUsr() != null ? req.getAltaUsr() : "") %></td>
        </tr>
    </table>
</fieldset>

<c:if test="<%= req.isRequiereAfiliado() || req.tieneAfiliadoInformado() %>">
    <fieldset class="block-labels">
        <legend>Afiliado</legend>

        <table class="lfr-table">
            <tr>
                <td><label>CUIL titular:</label></td>
                <td><%= HtmlUtil.escape(req.getAfiliadoCuilTitularVisible()) %></td>

                <td><label>Integrante:</label></td>
                <td><%= HtmlUtil.escape(req.getAfiliadoIntString()) %></td>
            </tr>
        </table>
    </fieldset>
</c:if>

<fieldset class="block-labels">
    <legend>Cargos y recupero</legend>

    <table class="lfr-table">
        <tr>
            <td><label>Cargo OSPIM %:</label></td>
            <td><%= HtmlUtil.escape(req.getCargoOspimString()) %></td>

            <td><label>Cargo tercerizadora %:</label></td>
            <td><%= HtmlUtil.escape(req.getCargoTercerizadoraString()) %></td>
        </tr>

        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Tercerizadora:</label></td>
            <td><%= HtmlUtil.escape(req.getIdTercerizadoraString()) %></td>

            <td><label>Recupero:</label></td>
            <td><%= HtmlUtil.escape(req.getRecuperoDescripcion()) %></td>
        </tr>
    </table>
</fieldset>

<fieldset class="block-labels">
    <legend>Observaciones</legend>

    <table class="lfr-table">
        <tr>
            <td><%= HtmlUtil.escape(req.getObservacionesVisible()) %></td>
        </tr>

        <tr>
            <td>&nbsp;</td>
        </tr>

        <tr>
            <td><label>Total estimado:</label> <%= HtmlUtil.escape(req.getTotalEstimadoString()) %></td>
        </tr>
    </table>
</fieldset>

<liferay-util:include page="/html/portlet/compras/requerimiento_detalle.jsp">
    <liferay-util:param name="solo_lectura" value="true" />
</liferay-util:include>

<c:if test="<%= puedeCotizarEstado || puedeAnularEstado %>">
    <form action="<%= cambiarEstadoURL.toString() %>"
          method="post"
          name="<portlet:namespace />cambioEstadoFm"
          id="<portlet:namespace />cambioEstadoFm">

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               value="<%= req.getIdRequerimientoCompra() %>" />

        <fieldset class="block-labels">
            <legend>Cambio de estado</legend>

            <table class="lfr-table">
                <tr>
                    <td><label>Estado nuevo:</label></td>
                    <td>
                        <select name="<portlet:namespace />estado_nuevo"
                                id="<portlet:namespace />estado_nuevo">
                            <option value="">Seleccione</option>

                            <c:if test="<%= puedeCotizarEstado %>">
                                <option value="<%= WebKeysCompras.ESTADO_COTIZADO %>">Cotizado</option>
                            </c:if>

                            <c:if test="<%= puedeAnularEstado %>">
                                <option value="<%= WebKeysCompras.ESTADO_ANULADO %>">Anulado</option>
                            </c:if>
                        </select>
                    </td>

                    <td>
                        <input type="button"
                               value="Aplicar"
                               onClick="<portlet:namespace />cambiarEstado();" />
                    </td>
                </tr>
            </table>
        </fieldset>
    </form>
</c:if>

<table class="lfr-table">
    <tr>
        <td>
            <c:if test="<%= puedeABM && req.isEditable() %>">
                <input type="button"
                       value="Editar"
                       onClick="window.location.href='<%= editarURL.toString() %>';" />

                &nbsp;&nbsp;
            </c:if>

            <input type="button"
                   value="Volver"
                   onClick="window.location.href='<%= volverURL.toString() %>';" />
        </td>
    </tr>
</table>

<c:if test="<%= puedeCotizarEstado || puedeAnularEstado %>">
<script type="text/javascript">
    function <portlet:namespace />cambiarEstado() {
        if (jQuery('#<portlet:namespace />estado_nuevo').val() == '') {
            alert('Debe seleccionar un estado.');
            jQuery('#<portlet:namespace />estado_nuevo').focus();
            return;
        }

        submitForm(document.getElementById('<portlet:namespace />cambioEstadoFm'));
    }
</script>
</c:if>
