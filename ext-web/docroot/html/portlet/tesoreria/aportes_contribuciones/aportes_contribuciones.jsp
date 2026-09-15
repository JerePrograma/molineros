<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchEntry" %>
<%@ page import="ar.com.ospim.tesoreria.beans.AportesContribuciones" %>
<%@ page import="ar.com.ospim.tesoreria.service.AportesContribucionesServiceUtil" %>

<portlet:defineObjects/>
<%
	Calendar calFiltro = CalendarFactoryUtil.getCalendar();

	int filtroDesdeDia = ParamUtil.getInteger(request, "filtroDesdeDia", calFiltro.get(Calendar.DAY_OF_MONTH));
	int filtroDesdeMes = ParamUtil.getInteger(request, "filtroDesdeMes", calFiltro.get(Calendar.MONTH));
	int filtroDesdeAnio = ParamUtil.getInteger(request, "filtroDesdeAnio", calFiltro.get(Calendar.YEAR));
	int filtroHastaDia = ParamUtil.getInteger(request, "filtroHastaDia", calFiltro.get(Calendar.DAY_OF_MONTH));
	int filtroHastaMes = ParamUtil.getInteger(request, "filtroHastaMes", calFiltro.get(Calendar.MONTH));
	int filtroHastaAnio = ParamUtil.getInteger(request, "filtroHastaAnio", calFiltro.get(Calendar.YEAR));

	List<AportesContribuciones> topes = AportesContribucionesServiceUtil.getAportesContribuciones();
	
	boolean buscar = ParamUtil.getBoolean(request, "buscar", false);
	boolean fechaFiltroInvalida = false;
	
	if (buscar && topes != null) {
		Calendar calDesdeBusqueda = CalendarFactoryUtil.getCalendar();

		calDesdeBusqueda.clear();
		calDesdeBusqueda.set(filtroDesdeAnio, filtroDesdeMes, filtroDesdeDia, 0, 0, 0);

		Date fechaFiltroDesde = calDesdeBusqueda.getTime();

		Calendar calHastaBusqueda = CalendarFactoryUtil.getCalendar();

		calHastaBusqueda.clear();
		calHastaBusqueda.set(filtroHastaAnio, filtroHastaMes, filtroHastaDia, 23, 59, 59);

		Date fechaFiltroHasta = calHastaBusqueda.getTime();

		if (fechaFiltroHasta.before(fechaFiltroDesde)) {
			fechaFiltroInvalida = true;
			topes = new ArrayList<AportesContribuciones>();
		} else {
			List<AportesContribuciones> topesFiltrados = new ArrayList<AportesContribuciones>();
			
			for (AportesContribuciones tope : topes) {
				if (tope.getFechaDesde() != null && tope.getFechaHasta() != null) {
					if (!tope.getFechaDesde().after(fechaFiltroHasta) && !tope.getFechaHasta().before(fechaFiltroDesde)) {
						topesFiltrados.add(tope);
					}
				}
			}
			topes = topesFiltrados;
		}
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat df = new DecimalFormat("#,##0.00");

	//Permisos
	boolean showABMButtons = PermissionUtil.userContainsRole(user, "ABM_Tesoreria");

	PortletURL portletURL = renderResponse.createRenderURL();

	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/tesoreria/view");
	portletURL.setParameter("tabs1", "tabla-aportes-contrib");
	
	if (buscar) {
		portletURL.setParameter("buscar", "true");
		portletURL.setParameter("filtroDesdeDia", String.valueOf(filtroDesdeDia));
		portletURL.setParameter("filtroDesdeMes", String.valueOf(filtroDesdeMes));
		portletURL.setParameter("filtroDesdeAnio", String.valueOf(filtroDesdeAnio));
		portletURL.setParameter("filtroHastaDia", String.valueOf(filtroHastaDia));
		portletURL.setParameter("filtroHastaMes", String.valueOf(filtroHastaMes));
		portletURL.setParameter("filtroHastaAnio", String.valueOf(filtroHastaAnio));
	}

	List<String> headerNames = new ArrayList<String>();

	headerNames.add("Desde");
	headerNames.add("Hasta");
	headerNames.add("Remuneración máxima");
	headerNames.add("Remuneración mínima");
	headerNames.add("Aporte real (2,55%)");

	if (showABMButtons) {
		headerNames.add("Acciones");
	}

	SearchContainer searchContainer = new SearchContainer(
			renderRequest,
			null,
			null,
			SearchContainer.DEFAULT_CUR_PARAM,
			Integer.MAX_VALUE,
			portletURL,
			headerNames,
			"No se encontraron registros"
	);

	int registrosPorPagina = 50; //ajustar cantidad
	int paginaSel = ParamUtil.getInteger(request, "pagina_sel", 0);
	int totalReg = topes != null ? topes.size() : 0;
	int totalPag = totalReg / registrosPorPagina;

	if ((totalReg % registrosPorPagina) > 0) {
		totalPag++;
	}

	//Si por algun motivo quedamos en una pagina
	//que ya no existe, volvemos a la primera.
	if (totalPag > 0 && paginaSel >= totalPag) {
		paginaSel = 0;
	}

	int inicio = paginaSel * registrosPorPagina;
	int fin = inicio + registrosPorPagina;

	if (fin > totalReg) {
		fin = totalReg;
	}

	if (topes != null) {
		searchContainer.setTotal(topes.size());

		List resultRows = searchContainer.getResultRows();

		for (int i = inicio; i < fin; i++) {
			
			AportesContribuciones tope = topes.get(i);

			String primaryKey = tope.getFechaDesde() != null ? sdfParam.format(tope.getFechaDesde()) : String.valueOf(i);
			
			ResultRow row = new ResultRow(tope, primaryKey, i);

			row.addText(tope.getFechaDesde() != null ? sdf.format(tope.getFechaDesde()) : "");
			row.addText(tope.getFechaHasta() != null ? sdf.format(tope.getFechaHasta()) : "");
			row.addText(tope.getTopeRemuneracion() != null ? df.format(tope.getTopeRemuneracion()) : "");
			row.addText(tope.getTopeAporte() != null ? df.format(tope.getTopeAporte()) : "");
			row.addText(tope.getAporteReal() != null ? df.format(tope.getAporteReal()) : "");
			
			if (showABMButtons) {

				PortletURL editarURL = renderResponse.createRenderURL();

				editarURL.setWindowState(LiferayWindowState.MAXIMIZED);
				editarURL.setParameter("struts_action", "/tesoreria/aportes_contribuciones");
				editarURL.setParameter("modo", "editar");
				editarURL.setParameter("fechaDesde", sdfParam.format(tope.getFechaDesde()));

				PortletURL historicoURL = renderResponse.createRenderURL();

				historicoURL.setWindowState(LiferayWindowState.MAXIMIZED);
				historicoURL.setParameter("struts_action", "/tesoreria/aportes_contribuciones");
				historicoURL.setParameter("modo", "historico");
				historicoURL.setParameter("fechaDesde", sdfParam.format(tope.getFechaDesde()));

				PortletURL eliminarURL = renderResponse.createActionURL();

				eliminarURL.setParameter("struts_action", "/tesoreria/aportes_contribuciones");
				eliminarURL.setParameter("modo", "eliminar");
				eliminarURL.setParameter("fechaDesde", sdfParam.format(tope.getFechaDesde()));
				eliminarURL.setParameter("redirect", portletURL.toString());

				StringBuilder sbAcciones = new StringBuilder();

				sbAcciones.append("<img alt=\"Editar\" ");
				sbAcciones.append("title=\"Editar\" ");
				sbAcciones.append("style=\"cursor:pointer; margin-right:8px;\" ");
				sbAcciones.append("src=\"");
				sbAcciones.append(themeDisplay.getPathThemeImages());
				sbAcciones.append("/common/edit.png\" ");
				sbAcciones.append("onClick=\"window.location.href='");
				sbAcciones.append(editarURL.toString());
				sbAcciones.append("';\" ");
				sbAcciones.append("/>");

				sbAcciones.append("<img alt=\"Histórico\" ");
				sbAcciones.append("title=\"Histórico\" ");
				sbAcciones.append("style=\"cursor:pointer; margin-right:8px;\" ");
				sbAcciones.append("src=\"");
				sbAcciones.append(themeDisplay.getPathThemeImages());
				sbAcciones.append("/common/history.png\" ");
				sbAcciones.append("onClick=\"window.location.href='");
				sbAcciones.append(historicoURL.toString());
				sbAcciones.append("';\" ");
				sbAcciones.append("/>");

				sbAcciones.append("<img alt=\"Eliminar\" ");
				sbAcciones.append("title=\"Eliminar\" ");
				sbAcciones.append("style=\"cursor:pointer;\" ");
				sbAcciones.append("src=\"");
				sbAcciones.append(themeDisplay.getPathThemeImages());
				sbAcciones.append("/common/delete.png\" ");
				sbAcciones.append("onClick=\"eliminarRegistroAportes('");
				sbAcciones.append(eliminarURL.toString());
				sbAcciones.append("');\" ");
				sbAcciones.append("/>");

				row.addText(sbAcciones.toString());
			}

			resultRows.add(row);
		}
	}
%>

<liferay-ui:success key="tope-aportes-guardado" message="El registro se guardó correctamente."/>
<liferay-ui:success key="tope-aportes-eliminado" message="El registro se eliminó correctamente."/>

<%
if (fechaFiltroInvalida) {
%>
	<div class="portlet-msg-error">
		La Fecha Hasta no puede ser menor que la Fecha Desde.
	</div>
<%
}
%>

<form action="" method="get" name="<portlet:namespace />fm">
	<fieldset class="block-labels">
		<legend>
			Topes de Aportes y Contrib
		</legend>
		
		<table class="lfr-table">
			<tr>
				<td>
					<label>
						Fecha Desde:
					</label>
				</td>
		
				<td>
					<liferay-ui:input-date
						dayParam="filtroDesdeDia"
						monthParam="filtroDesdeMes"
						yearParam="filtroDesdeAnio"
						dayValue="<%= filtroDesdeDia %>"
						monthValue="<%= filtroDesdeMes %>"
						yearValue="<%= filtroDesdeAnio %>"
						yearRangeStart="<%= calFiltro.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= calFiltro.get(Calendar.YEAR) + 20 %>"
						dayNullable="false"
						monthNullable="false"
						yearNullable="false"
						disabled="<%= false %>"
					/>
				</td>
		
				<td style="padding-left:30px;">
					<label>
						Fecha Hasta:
					</label>
				</td>
		
				<td>
					<liferay-ui:input-date
						dayParam="filtroHastaDia"
						monthParam="filtroHastaMes"
						yearParam="filtroHastaAnio"
						dayValue="<%= filtroHastaDia %>"
						monthValue="<%= filtroHastaMes %>"
						yearValue="<%= filtroHastaAnio %>"
						yearRangeStart="<%= calFiltro.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= calFiltro.get(Calendar.YEAR) + 20 %>"
						dayNullable="false"
						monthNullable="false"
						yearNullable="false"
						disabled="<%= false %>"
					/>
				</td>
		
				<td style="padding-left:20px; vertical-align:middle;">
					<input
						type="button"
						value="Buscar"
						onclick="<portlet:namespace />buscarPeriodo();"
					/>
				</td>
		
				<%
				if (showABMButtons) {
				%>
					<td style="padding-left:10px; vertical-align:middle;">
						<input
							type="button"
							value="Nuevo"
							onclick="<portlet:namespace />nuevoTopeAporte();"
						/>
					</td>
				<%
				}
				%>
			</tr>
</table>
<br/>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

		<table class="lfr-table" width="100%">
			<tr>
				<td align="center">
					Total Filas encontradas: <%= totalReg %> &nbsp;&nbsp;&nbsp;&nbsp;
					<%
					if (totalPag > 0) {
					%>
						<%= paginaSel + 1 %>/<%= totalPag %> páginas
					<%
					} else {
					%>
						0/0 páginas
					<%
					}
					%>
					&nbsp;&nbsp;
					<%
					if (totalPag > 0) {
					%>
						<select
							name="<portlet:namespace />pagina_sel"
							id="<portlet:namespace />pagina_sel"
							onchange="<portlet:namespace />cambiarPagina();">
							<%
							for (int i = 1; i <= totalPag; i++) {
							%>
								<option
									value="<%= i - 1 %>"
									<%= paginaSel == i - 1
											? "selected=\"selected\""
											: "" %>>
									<%= i %>
								</option>
							<%
							}
							%>
						</select>
					<%
					}
					%>
				</td>
			</tr>
		</table>
	</fieldset>
</form>


<script type="text/javascript">

	function <portlet:namespace />nuevoTopeAporte() {

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>' +
			'&struts_action=/tesoreria/aportes_contribuciones' +
			'&modo=nuevo';

		document.<portlet:namespace />fm.method ='post';
		submitForm(document.<portlet:namespace />fm,url);
	}

	function <portlet:namespace />buscarPeriodo() {

		var pagina = document.getElementById('<portlet:namespace />pagina_sel');

		if (pagina) {
			pagina.value = '0';
		}

		var url =
			'<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>' +
			'&struts_action=/tesoreria/view' +
			'&tabs1=tabla-aportes-contrib' +
			'&buscar=true';

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm,url);
	}
	
	setTimeout(function() {
		var mensajes =document.getElementsByClassName('portlet-msg-success');

		for (var i = 0; i < mensajes.length; i++) {
			mensajes[i].style.display = 'none';
		}

	}, 4000);
	
	function <portlet:namespace />cambiarPagina() {

		var url =
			'<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>' +
			'&struts_action=/tesoreria/view' +
			'&tabs1=tabla-aportes-contrib';

		<%
		if (buscar) {
		%>
			url += '&buscar=true';
		<%
		}
		%>

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function eliminarRegistroAportes(url) {

		if (confirm('¿Está seguro que desea eliminar el registro?')) {

			var form = document.createElement('form');

			form.method = 'post';
			form.action = url;
			form.style.display = 'none';

			document.body.appendChild(form);

			form.submit();
		}
	}
</script>
