<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ page import="javax.portlet.PortletURL" %>

<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>

<%@ page import="ar.com.ospim.liquidaciones.reportes.bean.DebitosaTotal" %>

<%
	PortletURL portletURL = renderResponse.createRenderURL();

	NumberFormat format2D = new DecimalFormat("#0.00");
	SimpleDateFormat sdf1 = new SimpleDateFormat("MM/yyyy");
	SimpleDateFormat sdfPeriodoParam = new SimpleDateFormat("yyyy-MM");

	// Key de sesion: el Action puede setearlo con request.setAttribute(...)
	String SESSION_TOTALES_KEY = (String) request.getAttribute("DEBITOS_TOTALES_SESSION_KEY");
	if (Validator.isNull(SESSION_TOTALES_KEY)) {
		SESSION_TOTALES_KEY = "BUSQUEDA_DEBITOS_TERCERIZADORAS_TOTALES";
	}

	List<DebitosaTotal> archivos = (List<DebitosaTotal>) session.getAttribute(SESSION_TOTALES_KEY);

	// Tipo seleccionado
	String tipoDebito = (String) request.getAttribute("DEBITOS_TIPO_SELECTED");
	if (Validator.isNull(tipoDebito)) {
		tipoDebito = ParamUtil.getString(renderRequest, "tipoDebito", "LI");
	}

	// Detalle (opcional)
	List detalle = (List) request.getAttribute("DEBITOS_DETALLE_RESULTADOS");

	List headerNames = new ArrayList();
	headerNames.add("Periodo Prueba");
	headerNames.add("Alta Fecha");
	headerNames.add("Usuario");
	headerNames.add("Tercerizadora");
	headerNames.add("Hospitales");
	headerNames.add("Reintegros");
	headerNames.add("Prestadores");
	headerNames.add("Liquidaciones Pendientes");
	headerNames.add("Total Debito");
	headerNames.add("Exportacion");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			Integer.MAX_VALUE,
			portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-periodo-were-found")
	);

	if (archivos != null && !archivos.isEmpty()) {

		searchContainer.setTotal(archivos.size());
		List resultRows = searchContainer.getResultRows();

		for (int i = 0; i < archivos.size(); i++) {

			DebitosaTotal deb = (DebitosaTotal) archivos.get(i);
			ResultRow row = new ResultRow(deb, new Integer(1 + i), i);

			// Display (igual estilo que tu JSP "compatible")
			row.addText(deb.getPeriodo() != null ? sdf1.format((Date) deb.getPeriodo()) : "");
			row.addText(deb.getAltaFecha() != null ? sdf1.format((Date) deb.getAltaFecha()) : "");
			row.addText(deb.getAltaUsr() != null ? deb.getAltaUsr() : "");
			row.addText(deb.getDescTercerizadora() != null ? deb.getDescTercerizadora() : "");

			row.addText(deb.getMontoHospitales() != null ? format2D.format(deb.getMontoHospitales()) : "");
			row.addText(deb.getMontoReintegros() != null ? format2D.format(deb.getMontoReintegros()) : "");
			row.addText(deb.getMontoPrestadores() != null ? format2D.format(deb.getMontoPrestadores()) : "");
			row.addText(deb.getMontoLiquidacionPendiente() != null ? format2D.format(deb.getMontoLiquidacionPendiente()) : "");
			row.addText(deb.getTotal() != null ? format2D.format(deb.getTotal()) : "");

			// Exportacion: asegurar formato yyyy-MM + null-safety
			String periodoParam = "";
			if (deb.getPeriodo() != null) {
				try {
					periodoParam = sdfPeriodoParam.format((Date) deb.getPeriodo());
				} catch (Exception ex) {
					periodoParam = String.valueOf(deb.getPeriodo());
				}
			}

			String idTercerizadora = (deb.getIdTercerizadora() != null) ? String.valueOf(deb.getIdTercerizadora()) : "";

			StringBuilder sbo = new StringBuilder();
			sbo.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"exportar\" src=\"");
			sbo.append(themeDisplay.getPathThemeImages());
			sbo.append("/common/download.png\"  title='Exportación a Excel'");
			sbo.append(" onClick=\"javascript:exportacion('");
			sbo.append(periodoParam);
			sbo.append("','");
			sbo.append(idTercerizadora);
			sbo.append("');\" />");

			row.addText(sbo.toString());
			resultRows.add(row);
		}
	}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

<script type="text/javascript">
	function exportacion(periodo, idTercerizadora){

		var test = (periodo + "").split("-");
		var anio = test[0];
		var mes = test[1];

		if (!anio || !mes) {
			alert("Periodo invalido para exportacion: " + periodo);
			return;
		}

		var periodo_aux = anio + '-' + mes;

		var url_sub = '/xlsservlet/?reporte=REPORTE_DEBITO_TERCERIZADORAS'
				+ '&fechaDesdeDia=01'
				+ '&fechaDesdeMes=' + (mes - 1)
				+ '&fechaDesdeAnio=' + anio
				+ '&grabarDebitos=false'
				+ '&periodo=' + periodo_aux
				+ '&tipo_debitos_tercerizadoras=' + (idTercerizadora || '')
				+ '&rnd=' + Math.floor(Math.random()*100);

		window.location.href = url_sub;
	}
</script>
