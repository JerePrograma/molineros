<%@ include file="/html/portlet/compras/init.jsp" %>

<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);

Calendar fechaDesde = Calendar.getInstance();
fechaDesde.add(Calendar.MONTH, -1);

Calendar fechaHasta = Calendar.getInstance();

Calendar fechaPedidoCotizacionDesde = Calendar.getInstance();
fechaPedidoCotizacionDesde.add(Calendar.MONTH, -1);

Calendar fechaPedidoCotizacionHasta = Calendar.getInstance();

List<ClaseBase> sectores = new ArrayList<ClaseBase>();
try {
    sectores = TraeListasServiceUtil.getSectoresLiquidaciones();
} catch (Exception e) {
    sectores = new ArrayList<ClaseBase>();
}
%>

<fieldset class="block-labels">
    <legend>Filtro de busqueda de requerimientos de compras</legend>

    <table class="lfr-table" width="100%">
        <tr>
            <td><label>Numero:</label></td>
            <td>
                <input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="10" maxlength="10" type="text" />
            </td>

            <td><label>Afiliado:</label></td>
            <td>
                <input id="<portlet:namespace />afiliado" name="<portlet:namespace />afiliado" size="28" maxlength="255" type="text" />
            </td>

            <td><label>DNI:</label></td>
            <td>
                <input id="<portlet:namespace />dni" name="<portlet:namespace />dni" size="12" maxlength="20" type="text" />
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

            <td><label>Estado:</label></td>
            <td>
                <select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
                    <option value="0">Todos</option>
                    <option value="<%= WebKeysCompras.ESTADO_BORRADOR %>">Borrador</option>
                    <option value="<%= WebKeysCompras.ESTADO_PENDIENTE_APROBACION %>">Pendiente aprobacion</option>
                    <option value="<%= WebKeysCompras.ESTADO_APROBADO %>">Aprobado</option>
                    <option value="<%= WebKeysCompras.ESTADO_OBSERVADO %>">Observado</option>
                    <option value="<%= WebKeysCompras.ESTADO_RECHAZADO %>">Rechazado</option>
                    <option value="<%= WebKeysCompras.ESTADO_EN_COMPRA %>">En compra</option>
                    <option value="<%= WebKeysCompras.ESTADO_CERRADO %>">Cerrado</option>
                    <option value="<%= WebKeysCompras.ESTADO_ANULADO %>">Anulado</option>
                    <option value="<%= WebKeysCompras.ESTADO_PENDIENTE_COTIZACION %>">Pendiente cotizacion</option>
                    <option value="<%= WebKeysCompras.ESTADO_COTIZADO %>">Cotizado</option>
                </select>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
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
            <td>
                <input id="<portlet:namespace />solicitante_usr" name="<portlet:namespace />solicitante_usr" size="18" maxlength="75" type="text" />
            </td>

            <td><label>Entidad:</label></td>
            <td>
                <input id="<portlet:namespace />entidad" name="<portlet:namespace />entidad" size="18" maxlength="75" type="text" value="O.S.P.I.M." />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Detalle:</label></td>
            <td>
                <input id="<portlet:namespace />detalle_requerimiento" name="<portlet:namespace />detalle_requerimiento" size="28" maxlength="255" type="text" />
            </td>

            <td><label>RP:</label></td>
            <td>
                <input id="<portlet:namespace />rp_numero" name="<portlet:namespace />rp_numero" size="10" maxlength="10" type="text" />
            </td>

            <td><label>Orden compra:</label></td>
            <td>
                <input id="<portlet:namespace />orden_compra_numero" name="<portlet:namespace />orden_compra_numero" size="10" maxlength="10" type="text" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Cotizado:</label></td>
            <td>
                <select id="<portlet:namespace />cotizado" name="<portlet:namespace />cotizado">
                    <option value="0">Todos</option>
                    <option value="1">SI</option>
                    <option value="2">NO</option>
                </select>
            </td>

            <td><label>Recupero:</label></td>
            <td>
                <select id="<portlet:namespace />recupero" name="<portlet:namespace />recupero">
                    <option value="0">Todos</option>
                    <option value="1">SI</option>
                    <option value="2">NO</option>
                </select>
            </td>

            <td><label>Prioridad:</label></td>
            <td>
                <select id="<portlet:namespace />prioridad" name="<portlet:namespace />prioridad">
                    <option value="0">Todas</option>
                    <option value="<%= WebKeysCompras.PRIORIDAD_BAJA %>">Baja</option>
                    <option value="<%= WebKeysCompras.PRIORIDAD_MEDIA %>">Media</option>
                    <option value="<%= WebKeysCompras.PRIORIDAD_ALTA %>">Alta</option>
                    <option value="<%= WebKeysCompras.PRIORIDAD_URGENTE %>">Urgente</option>
                </select>
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Pedido cotizacion desde:</label></td>
            <td>
                <liferay-ui:input-date
                    dayParam="fechaPedidoCotizacionDesdeDia"
                    dayValue="<%= fechaPedidoCotizacionDesde.get(Calendar.DATE) %>"
                    dayNullable="<%= true %>"
                    monthParam="fechaPedidoCotizacionDesdeMes"
                    monthValue="<%= fechaPedidoCotizacionDesde.get(Calendar.MONTH) %>"
                    monthNullable="<%= true %>"
                    yearParam="fechaPedidoCotizacionDesdeAnio"
                    yearValue="<%= fechaPedidoCotizacionDesde.get(Calendar.YEAR) %>"
                    yearNullable="<%= true %>"
                    yearRangeStart="<%= fechaPedidoCotizacionDesde.get(Calendar.YEAR) - 5 %>"
                    yearRangeEnd="<%= fechaPedidoCotizacionDesde.get(Calendar.YEAR) + 2 %>"
                    firstDayOfWeek="<%= fechaPedidoCotizacionDesde.getFirstDayOfWeek() - 1 %>"
                    disabled="<%= false %>" />
            </td>

            <td><label>Pedido cotizacion hasta:</label></td>
            <td>
                <liferay-ui:input-date
                    dayParam="fechaPedidoCotizacionHastaDia"
                    dayValue="<%= fechaPedidoCotizacionHasta.get(Calendar.DATE) %>"
                    dayNullable="<%= true %>"
                    monthParam="fechaPedidoCotizacionHastaMes"
                    monthValue="<%= fechaPedidoCotizacionHasta.get(Calendar.MONTH) %>"
                    monthNullable="<%= true %>"
                    yearParam="fechaPedidoCotizacionHastaAnio"
                    yearValue="<%= fechaPedidoCotizacionHasta.get(Calendar.YEAR) %>"
                    yearNullable="<%= true %>"
                    yearRangeStart="<%= fechaPedidoCotizacionHasta.get(Calendar.YEAR) - 5 %>"
                    yearRangeEnd="<%= fechaPedidoCotizacionHasta.get(Calendar.YEAR) + 2 %>"
                    firstDayOfWeek="<%= fechaPedidoCotizacionHasta.getFirstDayOfWeek() - 1 %>"
                    disabled="<%= false %>" />
            </td>

            <td><label>Texto:</label></td>
            <td>
                <input id="<portlet:namespace />texto" name="<portlet:namespace />texto" size="30" maxlength="200" type="text" />
            </td>
        </tr>

        <tr>
            <td colspan="6">&nbsp;</td>
        </tr>

        <tr>
            <td><label>Localidad:</label></td>
            <td>
                <input id="<portlet:namespace />localidad" name="<portlet:namespace />localidad" size="22" maxlength="120" type="text" />
            </td>

            <td><label>Provincia:</label></td>
            <td>
                <input id="<portlet:namespace />provincia" name="<portlet:namespace />provincia" size="22" maxlength="120" type="text" />
            </td>

            <td></td>
            <td></td>
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
            "numero", "afiliado", "dni",
            "fechaDesdeDia", "fechaDesdeMes", "fechaDesdeAnio",
            "fechaHastaDia", "fechaHastaMes", "fechaHastaAnio",
            "sector_id", "solicitante_usr", "entidad", "prioridad", "estado",
            "detalle_requerimiento", "rp_numero", "orden_compra_numero", "cotizado", "recupero",
            "fechaPedidoCotizacionDesdeDia", "fechaPedidoCotizacionDesdeMes", "fechaPedidoCotizacionDesdeAnio",
            "fechaPedidoCotizacionHastaDia", "fechaPedidoCotizacionHastaMes", "fechaPedidoCotizacionHastaAnio",
            "localidad", "provincia", "texto"
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
