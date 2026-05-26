<%@ include file="/html/portlet/compras/init.jsp" %>

<%
int idRequerimientoCompra = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);

RequerimientoCompra req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW);
if (req == null && idRequerimientoCompra > 0) {
    req = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);
}
if (req == null) {
    req = new RequerimientoCompra();
}

renderRequest.setAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW, req);

boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);
boolean puedeAnular = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS) || puedeABM;
boolean puedeCambiarEstado = (req.puedeSolicitar() && puedeABM) || (req.puedeAnular() && puedeAnular);

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

    <table class="lfr-table" width="100%">
        <tr>
            <td><label>Número:</label></td>
            <td><%= HtmlUtil.escape(req.getNumeroVisible()) %></td>

            <td><label>Estado:</label></td>
            <td><strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong></td>
        </tr>

        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Fecha solicitud:</label></td>
            <td><%= HtmlUtil.escape(req.getFechaSolicitudAsString()) %></td>

            <td><label>Fecha alta:</label></td>
            <td><%= HtmlUtil.escape(req.getFechaAltaAsString()) %></td>
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
            <td><label>Solicitante:</label></td>
            <td><%= HtmlUtil.escape(req.getSolicitanteVisible()) %></td>

            <td><label>Usuario:</label></td>
            <td><%= HtmlUtil.escape(req.getSolicitanteUsr() != null ? req.getSolicitanteUsr() : "") %></td>
        </tr>
    </table>
</fieldset>

<c:if test="<%= req.isRequiereAfiliado() || req.tieneAfiliadoInformado() %>">
<fieldset class="block-labels">
    <legend>Afiliado</legend>

    <table class="lfr-table" width="100%">
        <tr>
            <td><label>CUIL titular:</label></td>
            <td><%= HtmlUtil.escape(req.getAfiliadoCuilTitularVisible()) %></td>

            <td><label>Integrante:</label></td>
            <td><%= HtmlUtil.escape(req.getAfiliadoInteString()) %></td>
        </tr>
    </table>
</fieldset>
</c:if>

<fieldset class="block-labels">
    <legend>Solicitud</legend>

    <table class="lfr-table" width="100%">
        <tr>
            <td><label>Descripción:</label></td>
            <td colspan="3"><%= HtmlUtil.escape(req.getDescripcionVisible()) %></td>
        </tr>

        <tr>
            <td><label>Observaciones:</label></td>
            <td colspan="3"><%= HtmlUtil.escape(req.getObservacionesVisible()) %></td>
        </tr>

        <tr>
            <td><label>Total estimado:</label></td>
            <td colspan="3"><%= HtmlUtil.escape(req.getTotalEstimadoString()) %></td>
        </tr>
    </table>
</fieldset>

<liferay-util:include page="/html/portlet/compras/requerimiento_detalle.jsp" />

<c:if test="<%= puedeCambiarEstado %>">
<form action="<%= cambiarEstadoURL.toString() %>" method="post" name="<portlet:namespace />cambioEstadoFm">
    <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" value="<%= req.getIdRequerimientoCompra() %>" />

    <fieldset class="block-labels">
        <legend>Cambio de estado</legend>

        <table class="lfr-table" width="100%">
            <tr>
                <td><label>Estado nuevo:</label></td>
                <td>
                    <select name="<portlet:namespace />estado_nuevo" id="<portlet:namespace />estado_nuevo">
                        <option value="">Seleccione</option>

                        <c:if test="<%= req.puedeSolicitar() && puedeABM %>">
                            <option value="<%= WebKeysCompras.ESTADO_SOLICITADO %>">Solicitar</option>
                        </c:if>

                        <c:if test="<%= req.puedeAnular() && puedeAnular %>">
                            <option value="<%= WebKeysCompras.ESTADO_ANULADO %>">Anular</option>
                        </c:if>
                    </select>
                </td>

                <td>
                    <input type="button" value="Aplicar" onclick="<portlet:namespace />cambiarEstado();" />
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
                <input type="button" value="Editar" onclick="window.location.href='<%= editarURL.toString() %>';" />
                &nbsp;&nbsp;
            </c:if>

            <input type="button" value="Volver" onclick="window.location.href='<%= volverURL.toString() %>';" />
        </td>
    </tr>
</table>

<script type="text/javascript">
    function <portlet:namespace />cambiarEstado() {
        if (jQuery("#<portlet:namespace />estado_nuevo").val() == "") {
            alert("Debe seleccionar un estado.");
            jQuery("#<portlet:namespace />estado_nuevo").focus();
            return;
        }

        submitForm(document.<portlet:namespace />cambioEstadoFm);
    }
</script>