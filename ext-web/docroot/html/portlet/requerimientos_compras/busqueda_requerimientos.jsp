<%@ include file="/html/portlet/requerimientos_compras/init.jsp" %>

<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysRequerimientosCompras.ROL_ABM_REQUERIMIENTOS_COMPRAS);

Calendar fechaDesde = Calendar.getInstance();
fechaDesde.add(Calendar.MONTH, -1);

Calendar fechaHasta = Calendar.getInstance();

List<ClaseBase> sectores = new ArrayList<ClaseBase>();
try {
    sectores = TraeListasServiceUtil.getSectoresLiquidaciones();
} catch (Exception e) {
    sectores = new ArrayList<ClaseBase>();
}
%>

<fieldset class="block-labels">
    <legend>Filtro de búsqueda de requerimientos de compras</legend>

    <table class="lfr-table">
        <tr>
            <td><label>Número:</label></td>
            <td><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="10" maxlength="10" type="text" /></td>

            <td><label>Fecha desde:</label></td>
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

            <td><label>Fecha hasta:</label></td>
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
        </tr>

        <tr>
            <td><label>Sector:</label></td>
            <td>
                <select id="<portlet:namespace />sector_id" name="<portlet:namespace />sector_id">
                    <option value="0">Todos</option>
                    <% for (ClaseBase sector : sectores) { %>
                        <option value="<%= sector.getId() %>"><%= sector.getDescripcion() %></option>
                    <% } %>
                </select>
            </td>

            <td><label>Solicitante:</label></td>
            <td><input id="<portlet:namespace />solicitante_usr" name="<portlet:namespace />solicitante_usr" size="18" maxlength="75" type="text" /></td>

            <td><label>Entidad:</label></td>
            <td><input id="<portlet:namespace />entidad" name="<portlet:namespace />entidad" size="18" maxlength="75" type="text" value="O.S.P.I.M." /></td>
        </tr>

        <tr>
            <td><label>Prioridad:</label></td>
            <td>
                <select id="<portlet:namespace />prioridad" name="<portlet:namespace />prioridad">
                    <option value="0">Todas</option>
                    <option value="<%= WebKeysRequerimientosCompras.PRIORIDAD_BAJA %>">Baja</option>
                    <option value="<%= WebKeysRequerimientosCompras.PRIORIDAD_MEDIA %>">Media</option>
                    <option value="<%= WebKeysRequerimientosCompras.PRIORIDAD_ALTA %>">Alta</option>
                    <option value="<%= WebKeysRequerimientosCompras.PRIORIDAD_URGENTE %>">Urgente</option>
                </select>
            </td>

            <td><label>Estado:</label></td>
            <td>
                <select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
                    <option value="0">Todos</option>
                    <option value="<%= WebKeysRequerimientosCompras.ESTADO_BORRADOR %>">Borrador</option>
                    <option value="<%= WebKeysRequerimientosCompras.ESTADO_PENDIENTE_APROBACION %>">Pendiente aprobacion</option>
                    <option value="<%= WebKeysRequerimientosCompras.ESTADO_APROBADO %>">Aprobado</option>
                    <option value="<%= WebKeysRequerimientosCompras.ESTADO_OBSERVADO %>">Observado</option>
                    <option value="<%= WebKeysRequerimientosCompras.ESTADO_RECHAZADO %>">Rechazado</option>
                    <option value="<%= WebKeysRequerimientosCompras.ESTADO_EN_COMPRA %>">En compra</option>
                    <option value="<%= WebKeysRequerimientosCompras.ESTADO_CERRADO %>">Cerrado</option>
                    <option value="<%= WebKeysRequerimientosCompras.ESTADO_ANULADO %>">Anulado</option>
                </select>
            </td>

            <td><label>Texto:</label></td>
            <td><input id="<portlet:namespace />texto" name="<portlet:namespace />texto" size="30" maxlength="200" type="text" /></td>
        </tr>

        <tr>
            <td>
                <input id="<portlet:namespace />buscar" value="Buscar" type="button" />
            </td>
            <td colspan="5">
                <c:if test="<%= puedeABM %>">
                    <input type="button" value="Nuevo requerimiento" onclick="<portlet:namespace />altaRequerimiento();" />
                </c:if>
            </td>
        </tr>
    </table>
</fieldset>

<fieldset class="block-labels">
    <div align="center" id="<portlet:namespace />buscando" style="display:none;">
        <table>
            <tr>
                <td>Buscando</td>
                <td><img alt="Buscando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></td>
            </tr>
        </table>
    </div>

    <div align="center" id="<portlet:namespace />busquedaRequerimientosDiv"></div>
</fieldset>

<script type="text/javascript">
    function <portlet:namespace />buildUrl() {
        var url = "<portlet:renderURL windowState='<%= LiferayWindowState.EXCLUSIVE.toString() %>' />&struts_action=/requerimientos_compras/buscar_requerimientos";

        url += "&numero=" + jQuery("#<portlet:namespace />numero").val();
        url += "&fechaDesdeDia=" + jQuery("#<portlet:namespace />fechaDesdeDia").val();
        url += "&fechaDesdeMes=" + jQuery("#<portlet:namespace />fechaDesdeMes").val();
        url += "&fechaDesdeAnio=" + jQuery("#<portlet:namespace />fechaDesdeAnio").val();
        url += "&fechaHastaDia=" + jQuery("#<portlet:namespace />fechaHastaDia").val();
        url += "&fechaHastaMes=" + jQuery("#<portlet:namespace />fechaHastaMes").val();
        url += "&fechaHastaAnio=" + jQuery("#<portlet:namespace />fechaHastaAnio").val();
        url += "&sector_id=" + jQuery("#<portlet:namespace />sector_id").val();
        url += "&solicitante_usr=" + encodeURIComponent(jQuery("#<portlet:namespace />solicitante_usr").val());
        url += "&entidad=" + encodeURIComponent(jQuery("#<portlet:namespace />entidad").val());
        url += "&prioridad=" + jQuery("#<portlet:namespace />prioridad").val();
        url += "&estado=" + jQuery("#<portlet:namespace />estado").val();
        url += "&texto=" + encodeURIComponent(jQuery("#<portlet:namespace />texto").val());

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
        var url = "<portlet:renderURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/requerimientos_compras/editar_requerimiento' /></portlet:renderURL>";
        window.location.href = url;
    }

    <portlet:namespace />buscarRequerimientos();
</script>
