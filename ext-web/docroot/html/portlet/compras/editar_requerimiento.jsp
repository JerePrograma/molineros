<%@ include file="/html/portlet/compras/init.jsp" %>

<%
RequerimientoCompra req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);
if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo = req.getIdRequerimientoCompra() == 0;
boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

List<RequerimientoCompraSector> sectores = (List<RequerimientoCompraSector>) renderRequest.getAttribute(WebKeysCompras.SECTORES_REQUERIMIENTO_COMPRA);
if (sectores == null) {
    try {
        sectores = BusquedaRequerimientoCompraServiceUtil.listarSectores();
    } catch (Exception e) {
        sectores = new ArrayList<RequerimientoCompraSector>();
    }
}

PortletURL volverURL = renderResponse.createRenderURL();
volverURL.setWindowState(WindowState.MAXIMIZED);
volverURL.setParameter("struts_action", "/compras/view");

PortletURL verURL = renderResponse.createRenderURL();
verURL.setWindowState(WindowState.MAXIMIZED);
verURL.setParameter("struts_action", "/compras/ver_requerimiento");
verURL.setParameter("id_requerimiento_compra", req.getIdRequerimientoCompraString());

PortletURL actionURL = renderResponse.createActionURL();
actionURL.setWindowState(WindowState.MAXIMIZED);
actionURL.setParameter("struts_action", "/compras/editar_requerimiento");

String solicitanteDefault = req.getSolicitanteUsr() != null ? req.getSolicitanteUsr() : (user != null ? user.getScreenName() : "");
String solicitanteNombreDefault = req.getSolicitanteNombre() != null ? req.getSolicitanteNombre() : (user != null ? user.getFullName() : "");
String reqSectorId = req.getSectorId() != null ? String.valueOf(req.getSectorId().intValue()) : "";
%>

<c:if test="<%= !puedeABM %>">
    <div class="portlet-msg-error">No posee permisos para editar requerimientos de compras.</div>
</c:if>

<c:if test="<%= puedeABM %>">
    <form action="<%= actionURL.toString() %>" method="post" name="<portlet:namespace />fm">
        <input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>" value="<%= esNuevo ? Constants.ADD : Constants.UPDATE %>" />
        <input type="hidden" name="<portlet:namespace />id_requerimiento_compra" id="<portlet:namespace />id_requerimiento_compra" value="<%= req.getIdRequerimientoCompra() %>" />
        <input type="hidden" name="<portlet:namespace />id_estado" id="<portlet:namespace />id_estado" value="<%= req.getIdEstado() %>" />

        <fieldset class="block-labels">
            <legend><%= esNuevo ? "Nuevo requerimiento de compra" : "Editar requerimiento de compra" %></legend>

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>Estado:</label></td>
                    <td><strong><%= HtmlUtil.escape(req.getEstadoDescripcionVisible()) %></strong></td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Fecha solicitud:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />fecha_solicitud" id="<portlet:namespace />fecha_solicitud" value="<%= HtmlUtil.escape(req.getFechaSolicitudAsString()) %>" size="10" maxlength="10" /> dd/MM/yyyy
                    </td>

                    <td><label>Sector:</label></td>
                    <td>
                        <select name="<portlet:namespace />sector_id" id="<portlet:namespace />sector_id">
                            <option value="0">Seleccione</option>
                            <% for (int i = 0; i < sectores.size(); i++) {
                                RequerimientoCompraSector sector = sectores.get(i);
                                String sectorId = String.valueOf(sector.getIdSector());
                                String selected = reqSectorId.equals(sectorId) ? "selected=\"selected\"" : "";
                            %>
                                <option value="<%= sectorId %>" <%= selected %>><%= HtmlUtil.escape(sector.getDescripcionVisible()) %></option>
                            <% } %>
                        </select>
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Solicitante usuario:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />solicitante_usr" id="<portlet:namespace />solicitante_usr" value="<%= HtmlUtil.escape(solicitanteDefault) %>" size="30" maxlength="75" />
                    </td>

                    <td><label>Solicitante nombre:</label></td>
                    <td>
                        <input type="text" name="<portlet:namespace />solicitante_nombre" id="<portlet:namespace />solicitante_nombre" value="<%= HtmlUtil.escape(solicitanteNombreDefault) %>" size="35" maxlength="120" />
                    </td>
                </tr>
            </table>
        </fieldset>

        <liferay-util:include page="/html/portlet/compras/busqueda_afiliado_requerimiento.jsp" />

        <fieldset class="block-labels">
            <legend>Solicitud</legend>

            <table class="lfr-table" width="100%">
                <tr>
                    <td><label>Descripción:</label></td>
                    <td colspan="3">
                        <input type="text" name="<portlet:namespace />descripcion" id="<portlet:namespace />descripcion" value="<%= HtmlUtil.escape(req.getDescripcionVisible()) %>" size="100" maxlength="500" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Observaciones:</label></td>
                    <td colspan="3">
                        <textarea name="<portlet:namespace />observaciones" id="<portlet:namespace />observaciones" cols="100" rows="4"><%= HtmlUtil.escape(req.getObservacionesVisible()) %></textarea>
                    </td>
                </tr>
            </table>
        </fieldset>

        <table class="lfr-table">
            <tr>
                <td>
                    <input type="button" value="Guardar" onclick="<portlet:namespace />guardar();" />

                    <c:if test="<%= !esNuevo %>">
                        &nbsp;&nbsp;<input type="button" value="Ver" onclick="window.location.href='<%= verURL.toString() %>';" />
                    </c:if>

                    &nbsp;&nbsp;<input type="button" value="Volver" onclick="window.location.href='<%= volverURL.toString() %>';" />
                </td>
            </tr>
        </table>
    </form>

    <c:if test="<%= req.getIdRequerimientoCompra() > 0 %>">
        <liferay-util:include page="/html/portlet/compras/requerimiento_detalle.jsp" />
    </c:if>
</c:if>

<script type="text/javascript">
    function <portlet:namespace />guardar() {
        if (jQuery("#<portlet:namespace />sector_id").val() == "0") {
            alert("Debe informar sector.");
            return;
        }

        if (jQuery("#<portlet:namespace />solicitante_usr").val() == "") {
            alert("Debe informar solicitante.");
            return;
        }

        if (jQuery("#<portlet:namespace />descripcion").val() == "") {
            alert("Debe informar descripción del requerimiento.");
            return;
        }

        submitForm(document.<portlet:namespace />fm);
    }
</script>