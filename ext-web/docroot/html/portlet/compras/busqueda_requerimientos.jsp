<%@ include file="/html/portlet/compras/init.jsp" %>

<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

Calendar fechaDesde = Calendar.getInstance();
fechaDesde.add(Calendar.MONTH, -1);

Calendar fechaHasta = Calendar.getInstance();

List<RequerimientoCompraSector> sectores = (List<RequerimientoCompraSector>) renderRequest.getAttribute(WebKeysCompras.SECTORES_REQUERIMIENTO_COMPRA);
if (sectores == null) {
    try {
        sectores = BusquedaRequerimientoCompraServiceUtil.listarSectores();
    } catch (Exception e) {
        sectores = new ArrayList<RequerimientoCompraSector>();
    }
}

List<RequerimientoCompraEstado> estados = (List<RequerimientoCompraEstado>) renderRequest.getAttribute(WebKeysCompras.ESTADOS_REQUERIMIENTO_COMPRA);
if (estados == null) {
    try {
        estados = BusquedaRequerimientoCompraServiceUtil.listarEstados();
    } catch (Exception e) {
        estados = new ArrayList<RequerimientoCompraEstado>();
    }
}
%>

<fieldset class="block-labels">
    <legend>Filtro de búsqueda de requerimientos de compras</legend>

    <table class="lfr-table" width="100%">
        <tr>
            <td><label>Número:</label></td>
            <td>
                <input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="10" maxlength="10" type="text" />
            </td>

            <td><label>Sector:</label></td>
            <td>
                <select id="<portlet:namespace />sector_id" name="<portlet:namespace />sector_id">
                    <option value="0">Todos</option>
                    <% for (int i = 0; i < sectores.size(); i++) {
                        RequerimientoCompraSector sector = sectores.get(i);
                    %>
                        <option value="<%= sector.getIdSector() %>"><%= HtmlUtil.escape(sector.getDescripcionVisible()) %></option>
                    <% } %>
                </select>
            </td>

            <td><label>Estado:</label></td>
            <td>
                <select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
                    <option value="0">Todos</option>
                    <% for (int i = 0; i < estados.size(); i++) {
                        RequerimientoCompraEstado estado = estados.get(i);
                    %>
                        <option value="<%= estado.getIdEstado() %>"><%= HtmlUtil.escape(estado.getDescripcionVisible()) %></option>
                    <% } %>
                </select>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Fecha solicitud desde:</label></td>
            <td>
                <liferay-ui:input-date
                    dayParam="fechaDesdeDia"
                    dayValue="<%= fechaDesde.get(Calendar.DATE) %>"
                    dayNullable="<%= true %>"
                    monthParam="fechaDesdeMes"
                    monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
                    monthNullable="<%= true %>"
                    yearParam="fechaDesdeAnio"
                    yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
                    yearNullable="<%= true %>"
                    yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 5 %>"
                    yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 2 %>"
                    firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
                    disabled="<%= false %>" />
            </td>

            <td><label>Fecha solicitud hasta:</label></td>
            <td>
                <liferay-ui:input-date
                    dayParam="fechaHastaDia"
                    dayValue="<%= fechaHasta.get(Calendar.DATE) %>"
                    dayNullable="<%= true %>"
                    monthParam="fechaHastaMes"
                    monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
                    monthNullable="<%= true %>"
                    yearParam="fechaHastaAnio"
                    yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
                    yearNullable="<%= true %>"
                    yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
                    yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 2 %>"
                    firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
                    disabled="<%= false %>" />
            </td>

            <td><label>Solicitante:</label></td>
            <td>
                <input id="<portlet:namespace />solicitante_usr" name="<portlet:namespace />solicitante_usr" size="22" maxlength="75" type="text" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>CUIL titular:</label></td>
            <td>
                <input id="<portlet:namespace />afiliado_cuil_titular" name="<portlet:namespace />afiliado_cuil_titular" size="18" maxlength="20" type="text" />
            </td>

            <td><label>Integrante:</label></td>
            <td>
                <input id="<portlet:namespace />afiliado_inte" name="<portlet:namespace />afiliado_inte" size="8" maxlength="8" type="text" />
            </td>

            <td><label>Tipo artículo:</label></td>
            <td>
                <input id="<portlet:namespace />tipo_articulo" name="<portlet:namespace />tipo_articulo" size="22" maxlength="80" type="text" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Texto:</label></td>
            <td colspan="5">
                <input id="<portlet:namespace />texto" name="<portlet:namespace />texto" size="90" maxlength="200" type="text" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td colspan="6" align="center">
                <input id="<portlet:namespace />buscar" value="Buscar" title="Buscar" type="button" />

                <c:if test="<%= puedeABM %>">
                    &nbsp;&nbsp;
                    <input type="button" value="Nuevo requerimiento" onclick="<portlet:namespace />altaRequerimiento();" />
                </c:if>
            </td>
        </tr>
    </table>
</fieldset>

<fieldset class="block-labels">
    <div align="center" id="<portlet:namespace />buscando" style="display:none;">
        <table style="align:center;">
            <tr>
                <td>Buscando</td>
                <td align="center">
                    <img alt="Buscando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
                </td>
            </tr>
        </table>
    </div>

    <div align="center" id="<portlet:namespace />busquedaRequerimientosDiv"></div>
</fieldset>

<script type="text/javascript">
    function <portlet:namespace />appendParam(url, name, value) {
        return url + "&" + name + "=" + encodeURIComponent(value == null ? "" : value);
    }

    function <portlet:namespace />fieldValue(name) {
        return jQuery("#<portlet:namespace />" + name).val();
    }

    function <portlet:namespace />buildUrl() {
        var url = "<portlet:renderURL windowState='<%= LiferayWindowState.EXCLUSIVE.toString() %>' />&struts_action=/compras/buscar_requerimientos";

        var params = [
            "numero",
            "fechaDesdeDia", "fechaDesdeMes", "fechaDesdeAnio",
            "fechaHastaDia", "fechaHastaMes", "fechaHastaAnio",
            "sector_id",
            "estado",
            "solicitante_usr",
            "afiliado_cuil_titular",
            "afiliado_inte",
            "tipo_articulo",
            "texto"
        ];

        for (var i = 0; i < params.length; i++) {
            url = <portlet:namespace />appendParam(url, params[i], <portlet:namespace />fieldValue(params[i]));
        }

        return url;
    }

    function <portlet:namespace />buscarRequerimientos() {
        jQuery("#<portlet:namespace />buscando").show();

        jQuery("#<portlet:namespace />busquedaRequerimientosDiv").load(<portlet:namespace />buildUrl(), function() {
            jQuery("#<portlet:namespace />buscando").hide();
        });
    }

    jQuery("#<portlet:namespace />buscar").click(function() {
        <portlet:namespace />buscarRequerimientos();
    });

    function <portlet:namespace />altaRequerimiento() {
        var url = "<portlet:renderURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/compras/editar_requerimiento' /></portlet:renderURL>";
        window.location.href = url;
    }

    <portlet:namespace />buscarRequerimientos();
</script>