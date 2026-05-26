<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
RequerimientoCompra req = (RequerimientoCompra) renderRequest.getAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION);

if (req == null) {
    req = new RequerimientoCompra();
}

boolean esNuevo = req.getIdRequerimientoCompra() == 0;
boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);
boolean editable = esNuevo || req.isEditable();

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

<c:if test="<%= puedeABM && !editable %>">
    <div class="portlet-msg-info">El requerimiento solo puede editarse en estado Borrador.</div>

    <table class="lfr-table">
        <tr>
            <td>
                <input type="button"
                       value="Ver"
                       onClick="window.location.href='<%= verURL.toString() %>';" />

                &nbsp;&nbsp;

                <input type="button"
                       value="Volver"
                       onClick="window.location.href='<%= volverURL.toString() %>';" />
            </td>
        </tr>
    </table>
</c:if>

<c:if test="<%= puedeABM && editable %>">
    <form action="<%= actionURL.toString() %>" method="post" name="<portlet:namespace />fm">
        <input type="hidden"
               name="<portlet:namespace /><%= Constants.CMD %>"
               id="<portlet:namespace /><%= Constants.CMD %>"
               value="<%= esNuevo ? Constants.ADD : Constants.UPDATE %>" />

        <input type="hidden"
               name="<portlet:namespace />id_requerimiento_compra"
               id="<portlet:namespace />id_requerimiento_compra"
               value="<%= req.getIdRequerimientoCompra() %>" />

        <input type="hidden"
               name="<portlet:namespace />id_estado"
               id="<portlet:namespace />id_estado"
               value="<%= req.getIdEstado() %>" />

        <fieldset class="block-labels">
            <legend><%= esNuevo ? "Nuevo requerimiento de compra" : "Editar requerimiento de compra" %></legend>

            <table class="lfr-table">
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
                        <input type="text"
                               name="<portlet:namespace />fecha_solicitud"
                               id="<portlet:namespace />fecha_solicitud"
                               value="<%= HtmlUtil.escape(req.getFechaSolicitudAsString()) %>"
                               size="10"
                               maxlength="10" />
                        dd/MM/yyyy
                    </td>

                    <td><label>Sector:</label></td>
                    <td>
                        <select name="<portlet:namespace />sector_id"
                                id="<portlet:namespace />sector_id">
                            <option value="0">Seleccione</option>

                            <%
                            for (int i = 0; i < sectores.size(); i++) {
                                RequerimientoCompraSector sector = sectores.get(i);
                                String sectorId = String.valueOf(sector.getIdSector());
                                String selected = reqSectorId.equals(sectorId) ? "selected=\"selected\"" : "";
                            %>
                                <option value="<%= sectorId %>"
                                        data-requiere-afiliado="<%= sector.isRequiereAfiliado() ? "true" : "false" %>"
                                        <%= selected %>><%= HtmlUtil.escape(sector.getDescripcionVisible()) %></option>
                            <%
                            }
                            %>
                        </select>
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Solicitante usuario:</label></td>
                    <td>
                        <input type="text"
                               name="<portlet:namespace />solicitante_usr"
                               id="<portlet:namespace />solicitante_usr"
                               value="<%= HtmlUtil.escape(solicitanteDefault) %>"
                               size="30"
                               maxlength="75" />
                    </td>

                    <td><label>Solicitante nombre:</label></td>
                    <td>
                        <input type="text"
                               name="<portlet:namespace />solicitante_nombre"
                               id="<portlet:namespace />solicitante_nombre"
                               value="<%= HtmlUtil.escape(solicitanteNombreDefault) %>"
                               size="35"
                               maxlength="120" />
                    </td>
                </tr>
            </table>
        </fieldset>

        <liferay-util:include page="/html/portlet/compras/busqueda_afiliado_requerimiento.jsp" />

        <fieldset class="block-labels">
            <legend>Solicitud</legend>

            <table class="lfr-table">
                <tr>
                    <td><label>Descripci&oacute;n:</label></td>
                    <td colspan="3">
                        <input type="text"
                               name="<portlet:namespace />descripcion"
                               id="<portlet:namespace />descripcion"
                               value="<%= HtmlUtil.escape(req.getDescripcionVisible()) %>"
                               size="100"
                               maxlength="500" />
                    </td>
                </tr>

                <tr>
                    <td colspan="4">&nbsp;</td>
                </tr>

                <tr>
                    <td><label>Observaciones:</label></td>
                    <td colspan="3">
                        <textarea name="<portlet:namespace />observaciones"
                                  id="<portlet:namespace />observaciones"
                                  cols="100"
                                  rows="4"><%= HtmlUtil.escape(req.getObservacionesVisible()) %></textarea>
                    </td>
                </tr>
            </table>
        </fieldset>

        <table class="lfr-table">
            <tr>
                <td>
                    <input type="button"
                           value="Guardar"
                           onClick="<portlet:namespace />guardar();" />

                    <c:if test="<%= !esNuevo %>">
                        &nbsp;&nbsp;

                        <input type="button"
                               value="Ver"
                               onClick="window.location.href='<%= verURL.toString() %>';" />
                    </c:if>

                    &nbsp;&nbsp;

                    <input type="button"
                           value="Volver"
                           onClick="window.location.href='<%= volverURL.toString() %>';" />
                </td>
            </tr>
        </table>
    </form>

    <c:if test="<%= req.getIdRequerimientoCompra() > 0 %>">
        <liferay-util:include page="/html/portlet/compras/requerimiento_detalle.jsp" />
    </c:if>
</c:if>

<script type="text/javascript">
    function <portlet:namespace />trimValue(id) {
        return jQuery.trim(jQuery('#<portlet:namespace />' + id).val());
    }

    function <portlet:namespace />sectorRequiereAfiliado() {
        return jQuery('#<portlet:namespace />sector_id option:selected').attr('data-requiere-afiliado') == 'true';
    }

    function <portlet:namespace />actualizarVisibilidadAfiliado(limpiarSiNoRequiere) {
        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        if (requiereAfiliado) {
            jQuery('#<portlet:namespace />afiliado_requerimiento_fieldset').show();
        } else {
            if (limpiarSiNoRequiere && typeof <portlet:namespace />limpiarCamposAfiliado == 'function') {
                <portlet:namespace />limpiarCamposAfiliado();
            }

            jQuery('#<portlet:namespace />afiliado_requerimiento_fieldset').hide();
        }
    }

    function <portlet:namespace />guardar() {
        if (<portlet:namespace />trimValue('sector_id') == '0') {
            alert('Debe informar sector.');
            jQuery('#<portlet:namespace />sector_id').focus();
            return;
        }

        if (<portlet:namespace />trimValue('solicitante_usr') == '') {
            alert('Debe informar solicitante.');
            jQuery('#<portlet:namespace />solicitante_usr').focus();
            return;
        }

        if (<portlet:namespace />trimValue('descripcion') == '') {
            alert('Debe informar descripci&oacute;n del requerimiento.');
            jQuery('#<portlet:namespace />descripcion').focus();
            return;
        }

        var requiereAfiliado = <portlet:namespace />sectorRequiereAfiliado();

        if (requiereAfiliado) {
            var afiliadoCuilTitular = <portlet:namespace />trimValue('afiliado_cuil_titular');
            var afiliadoInte = <portlet:namespace />trimValue('afiliado_inte');

            if (afiliadoCuilTitular == '' || afiliadoInte == '') {
                alert('Debe seleccionar un afiliado.');
                jQuery('#<portlet:namespace />cuil').focus();
                return;
            }
        } else {
            if (typeof <portlet:namespace />limpiarCamposAfiliado == 'function') {
                <portlet:namespace />limpiarCamposAfiliado();
            }
        }

        submitForm(document.<portlet:namespace />fm);
    }

    jQuery(function() {
        <portlet:namespace />actualizarVisibilidadAfiliado(false);

        jQuery('#<portlet:namespace />sector_id').change(function() {
            <portlet:namespace />actualizarVisibilidadAfiliado(true);
        });
    });
</script>
