<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="ar.com.ospim.tesoreria.beans.AportesContribucionesHistorico" %>

<portlet:defineObjects/>

<portlet:renderURL var="volverListadoURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
	<portlet:param name="struts_action" value="/tesoreria/view"/>
	<portlet:param name="tabs1" value="tabla-aportes-contrib"/>
</portlet:renderURL>

<%
	List<AportesContribucionesHistorico> historico = (List<AportesContribucionesHistorico>)renderRequest.getAttribute("historicoAportesContribuciones");

	if (historico == null) {
		historico = new ArrayList<AportesContribucionesHistorico>();
	}

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdfFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	DecimalFormat df = new DecimalFormat("#,##0.00");

	List<String> headerNames = new ArrayList<String>();

	headerNames.add("Fecha modificación");
	headerNames.add("Usuario");
	headerNames.add("Desde");
	headerNames.add("Hasta");
	headerNames.add("Remuneración máxima anterior");
	headerNames.add("Remuneración mínima anterior");
	headerNames.add("Aporte real anterior");

	PortletURL portletURL = renderResponse.createRenderURL();

	SearchContainer searchContainer = new SearchContainer(
					renderRequest,
					null,
					null,
					SearchContainer.DEFAULT_CUR_PARAM,
					Integer.MAX_VALUE,
					portletURL,
					headerNames,
					"No se encontraron modificaciones"
			);

	searchContainer.setTotal(historico.size());

	List resultRows = searchContainer.getResultRows();

	for (int i = 0; i < historico.size(); i++) {

		AportesContribucionesHistorico h = historico.get(i);

		ResultRow row = new ResultRow(h, i, i);

		row.addText(h.getAltaFecha() != null ? sdfFechaHora.format(h.getAltaFecha()) : "");
		row.addText(h.getAltaUsr() != null ? h.getAltaUsr() : "");
		row.addText(h.getFechaDesde() != null ? sdf.format(h.getFechaDesde()) : "");
		row.addText(h.getFechaHasta() != null ? sdf.format(h.getFechaHasta()) : "");
		row.addText(h.getTopeRemuneracion() != null ? df.format(h.getTopeRemuneracion()) : "");
		row.addText(h.getTopeAporte() != null ? df.format(h.getTopeAporte()) : "");
		row.addText(h.getAporteReal() != null ? df.format(h.getAporteReal()) : "");

		resultRows.add(row);
	}

%>

<fieldset class="block-labels">

	<legend>
		Histórico de Aportes y Contribuciones
	</legend>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

	<br/>

	<input
		type="button"
		value="Volver"
		onclick="window.location.href='<%= volverListadoURL %>';"
	/>
	
</fieldset>