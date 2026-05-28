<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

boolean showABMButtons = user != null && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

List<RequerimientoCompraSector> sectores =
        (List<RequerimientoCompraSector>) renderRequest.getAttribute(WebKeysCompras.SECTORES_REQUERIMIENTO);

if (sectores == null) {
    try {
        sectores = BusquedaRequerimientoCompraServiceUtil.listarSectores();
    } catch (Exception e) {
        sectores = new ArrayList<RequerimientoCompraSector>();
    }
}

List<RequerimientoCompraEstado> estados =
        (List<RequerimientoCompraEstado>) renderRequest.getAttribute(WebKeysCompras.ESTADOS_REQUERIMIENTO);

if (estados == null) {
    try {
        estados = BusquedaRequerimientoCompraServiceUtil.listarEstados();
    } catch (Exception e) {
        estados = new ArrayList<RequerimientoCompraEstado>();
    }
}
%>

<fieldset class="block-labels">
    <legend>Filtro de b&uacute;squeda de requerimientos de compras</legend>

    <table class="lfr-table">
        <tr>
            <td><label>Estado:</label></td>
            <td>
                <select id="<portlet:namespace />estado"
                        name="<portlet:namespace />estado">
                    <option value="0">Todos</option>

                    <%
                    for (int i = 0; i < estados.size(); i++) {
                        RequerimientoCompraEstado estado = estados.get(i);
                    %>
                        <option value="<%= estado.getIdEstado() %>"><%= HtmlUtil.escape(estado.getDescripcionVisible()) %></option>
                    <%
                    }
                    %>
                </select>
            </td>

            <td><label>Sector:</label></td>
            <td>
                <select id="<portlet:namespace />sector_id"
                        name="<portlet:namespace />sector_id">
                    <option value="0">Todos</option>

                    <%
                    for (int i = 0; i < sectores.size(); i++) {
                        RequerimientoCompraSector sector = sectores.get(i);
                    %>
                        <option value="<%= sector.getIdSector() %>"><%= HtmlUtil.escape(sector.getDescripcionVisible()) %></option>
                    <%
                    }
                    %>
                </select>
            </td>

            <td><label>Recupero:</label></td>
            <td>
                <select id="<portlet:namespace />recupero"
                        name="<portlet:namespace />recupero">
                    <option value="">Todos</option>
                    <option value="true">SI</option>
                    <option value="false">NO</option>
                </select>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>CUIL titular:</label></td>
            <td>
                <input id="<portlet:namespace />afiliado_cuil_titular"
                       name="<portlet:namespace />afiliado_cuil_titular"
                       size="18"
                       maxlength="20"
                       type="text"
                       value="" />
            </td>

            <td><label>Integrante:</label></td>
            <td>
                <input id="<portlet:namespace />afiliado_int"
                       name="<portlet:namespace />afiliado_int"
                       size="8"
                       maxlength="8"
                       type="text"
                       value="" />
            </td>

            <td><label>Tercerizadora:</label></td>
            <td>
                <input id="<portlet:namespace />id_tercerizadora"
                       name="<portlet:namespace />id_tercerizadora"
                       size="10"
                       maxlength="10"
                       type="text"
                       value="" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Texto:</label></td>
            <td colspan="5">
                <input id="<portlet:namespace />texto"
                       name="<portlet:namespace />texto"
                       size="90"
                       maxlength="200"
                       type="text"
                       value="" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="1">
                <input id="<portlet:namespace />buscar"
                       value="<liferay-ui:message key='buscar' />"
                       title="<liferay-ui:message key='buscar' />"
                       type="button" />
            </td>
            <td colspan="5">
                <c:if test="<%= showABMButtons %>">
                    <input type="button"
                           value="Nuevo requerimiento"
                           onClick="<portlet:namespace />altaRequerimiento();" />
                </c:if>
            </td>
        </tr>
    </table>
</fieldset>

<fieldset class="block-labels">
    <div align="center"
         id="<portlet:namespace />buscando">
        <table style="align:center;">
            <tr>
                <td><liferay-ui:message key="buscando" /></td>
                <td align="center">
                    <img alt="<liferay-ui:message key='buscando' />"
                         src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
                </td>
            </tr>
        </table>
    </div>

    <div align="center" id="<portlet:namespace />busquedaRequerimientosDiv"></div>
</fieldset>

<script type="text/javascript">
    function <portlet:namespace />validarFiltroBusqueda() {
        var cuil = jQuery.trim(jQuery('#<portlet:namespace />afiliado_cuil_titular').val());

        if (cuil.length > 0 && cuil.length == 11 && typeof validarCuil == "function") {
            if (!validarCuil(cuil, "CUIL titular invalido.")) {
                jQuery('#<portlet:namespace />afiliado_cuil_titular').focus();
                return false;
            }
        }

        return true;
    }

    function <portlet:namespace />buscarRequerimientos() {
        if (!<portlet:namespace />validarFiltroBusqueda()) {
            return false;
        }

        var estado = jQuery('#<portlet:namespace />estado').val();
        var sector_id = jQuery('#<portlet:namespace />sector_id').val();
        var afiliado_cuil_titular = jQuery('#<portlet:namespace />afiliado_cuil_titular').val();
        var afiliado_int = jQuery('#<portlet:namespace />afiliado_int').val();
        var id_tercerizadora = jQuery('#<portlet:namespace />id_tercerizadora').val();
        var recupero = jQuery('#<portlet:namespace />recupero').val();
        var texto = jQuery('#<portlet:namespace />texto').val();

        jQuery('#<portlet:namespace />buscando').show();

        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>" />&struts_action=/compras/buscar_requerimientos' +
            '&estado=' + estado +
            '&sector_id=' + sector_id +
            '&afiliado_cuil_titular=' + encodeURIComponent(afiliado_cuil_titular) +
            '&afiliado_int=' + afiliado_int +
            '&id_tercerizadora=' + id_tercerizadora +
            '&recupero=' + recupero +
            '&texto=' + encodeURIComponent(texto);

        jQuery('#<portlet:namespace />busquedaRequerimientosDiv').load(url, function() {
            jQuery('#<portlet:namespace />buscando').hide();
        });

        return false;
    }

    function <portlet:namespace />altaRequerimiento() {
        var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/compras/editar_requerimiento" /></portlet:renderURL>';
        window.location.href = url;
    }

    jQuery('#<portlet:namespace />buscar').click(function() {
        <portlet:namespace />buscarRequerimientos();
    });

    jQuery('#<portlet:namespace />afiliado_cuil_titular, #<portlet:namespace />afiliado_int, #<portlet:namespace />id_tercerizadora, #<portlet:namespace />texto').keypress(function(event) {
        if (event.which == 13) {
            <portlet:namespace />buscarRequerimientos();
            return false;
        }

        return true;
    });

    jQuery('#<portlet:namespace />buscando').show();
    <portlet:namespace />buscarRequerimientos();
</script>
