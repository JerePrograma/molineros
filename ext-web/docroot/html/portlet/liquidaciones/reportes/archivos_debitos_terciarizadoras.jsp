<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<%
	PortletURL portletURL = renderResponse.createRenderURL();

	java.util.Date fecha = new Date();
	String porletName = renderResponse.getNamespace();

	NumberFormat format2D = new DecimalFormat("#0.00");
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdf1 = new SimpleDateFormat("MM/yyyy");
// para exportacion(): necesitamos yyyy-MM
	SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM");

	List<DebitosaTotal> archivos = new ArrayList<DebitosaTotal>();

// =====================
// Filtro (periodo + tercerizadora) desde params
// =====================
	String ns = renderResponse.getNamespace();

	String mesStr = request.getParameter("fechaDesdeMes");
	if (mesStr == null) mesStr = request.getParameter(ns + "fechaDesdeMes");

	String anioStr = request.getParameter("fechaDesdeAnio");
	if (anioStr == null) anioStr = request.getParameter(ns + "fechaDesdeAnio");

	String terc = request.getParameter("tipo_debitos_tercerizadoras");
	if (terc == null) terc = request.getParameter(ns + "tipo_debitos_tercerizadoras");
	if (terc == null) terc = request.getParameter("tipo_debito");
	if (terc == null) terc = request.getParameter(ns + "tipo_debito");

	boolean filtroOk =
			(mesStr != null && anioStr != null && terc != null &&
					terc.trim().length() > 0 && !"0".equals(terc));

	if (filtroOk) {
		try {
			int mes0 = Integer.parseInt(mesStr);   // 0-based (liferay input-date)
			int anio = Integer.parseInt(anioStr);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, anio);
			cal.set(Calendar.MONTH, mes0);
			// IMPORTANTE: el "cierre" en tu sistema está por fechaHasta (último día del mes)
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			Date periodoHasta = cal.getTime();

			// ahora pedís por fechaHasta (coherencia con existe_reporte_grabado...)
			archivos = BusquedaDebitosTercerizadorasServiceUtil.getArchivosDebitos(periodoHasta, terc);

			// FIX: tu método nuevo NO está estático en ServiceUtil => llamar al service instance

		} catch (Exception e) {
			archivos = new ArrayList<DebitosaTotal>();
		}
	}

// =====================
// UI
// =====================
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("Periodo");
	headerNames.add("Alta Fecha");
	headerNames.add("Usuario");
	headerNames.add("Tercerizadora");
	headerNames.add("Hospitales");
	headerNames.add("Reintegros");
	headerNames.add("Prestadores");
	headerNames.add("Liquidaciones Pendientes");
	headerNames.add("Total Debito");
	headerNames.add("Exportación");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-periodo-were-found")
	);

	if (archivos != null && !archivos.isEmpty()) {
		int total = archivos.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();

		for (int i = 0; i < archivos.size(); i++) {
			DebitosaTotal deb = (DebitosaTotal) archivos.get(i);
			ResultRow row = new ResultRow(deb, new Integer(1 + i), i);

			row.addText(sdf1.format(deb.getPeriodo()));
			row.addText(Validator.isNotNull(deb.getAltaFecha()) ? sdf.format(deb.getAltaFecha()) : "");
			row.addText(deb.getAltaUsr());
			row.addText(deb.getDescTercerizadora());
			row.addText(format2D.format(deb.getMontoHospitales()));
			row.addText(format2D.format(deb.getMontoReintegros()));
			row.addText(format2D.format(deb.getMontoPrestadores()));
			row.addText(format2D.format(deb.getMontoLiquidacionPendiente()));
			row.addText(format2D.format(deb.getTotal()));

			StringBuilder sbo = new StringBuilder();
			sbo.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"edita prestacion\" src=\"");
			sbo.append(themeDisplay.getPathThemeImages());
			sbo.append("/common/download.png\"  title='Exportación a Excel'");
			sbo.append(" onClick=\"javascript:exportacion('");

			// FIX: antes pasabas deb.getPeriodo() (Date crudo). Ahora mandamos yyyy-MM para split("-")
			sbo.append(sdfYm.format(deb.getPeriodo()));

			sbo.append("','");
			sbo.append(deb.getIdTercerizadora());
			sbo.append("');\" />");

			row.addText(sbo.toString());
			resultRows.add(row);
		}
	} else {
		searchContainer.setTotal(0);
	}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

<script type="text/javascript">
	function exportacion(periodo, idTercerizadora){

		// periodo = "yyyy-MM"
		var test = periodo.split("-");
		var anio = test[0];
		var mes = parseInt(test[1], 10);

		var periodo_aux = anio + '-' + (mes < 10 ? ('0' + mes) : mes);

		var url_sub = '/xlsservlet/?reporte=REPORTE_DEBITO_TERCERIZADORAS'
				+ '&fechaDesdeDia=01'
				+ '&fechaDesdeMes=' + (mes - 1)
				+ '&fechaDesdeAnio=' + anio
				+ '&grabarDebitos=false'
				+ '&periodo=' + periodo_aux
				+ '&tipo_debitos_tercerizadoras=' + idTercerizadora
				+ '&rnd=' + Math.floor(Math.random()*100);

		window.location.href = url_sub;
	}
</script>
