<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.beans.SaldoDiarioCuentaBancaria" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%

	List<SaldoDiarioCuentaBancaria> saldos =
		(List<SaldoDiarioCuentaBancaria>) request.getAttribute(
			WebKeysTesoreria.SALDOS_DIARIOS_CUENTAS_BANCARIAS
		);

	PortletURL portletURL = renderResponse.createRenderURL();

	List<String> headerNames = new ArrayList<String>();

	headerNames.add("Cuenta bancaria");
	headerNames.add("Fecha");
	headerNames.add("Saldo");
	headerNames.add("Acciones");

	SearchContainer searchContainer = new SearchContainer(
		renderRequest,
		null,
		null,
		SearchContainer.DEFAULT_CUR_PARAM,
		SearchContainer.DEFAULT_DELTA,
		portletURL,
		headerNames,
		"No se encontraron registros."
	);

	if (saldos != null) {
		searchContainer.setTotal(saldos.size());

		List resultRows = searchContainer.getResultRows();

		SimpleDateFormat sdfMostrar = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat formatoImporte = new DecimalFormat("#,##0.00");
		
		for (int i = 0; i < saldos.size(); i++) {
			SaldoDiarioCuentaBancaria saldo = saldos.get(i);

			ResultRow row = new ResultRow(saldo, String.valueOf(i), i);

			row.addText(saldo.getCuentaBancaria() != null ? saldo.getCuentaBancaria() : "");

			String fechaInicioEjercicioMostrar = "";
			String fechaInicioEjercicioParam = "";

			if (saldo.getFechaInicioEjercicio() != null) {
				fechaInicioEjercicioMostrar = sdfMostrar.format(saldo.getFechaInicioEjercicio());
				fechaInicioEjercicioParam = sdfParam.format(saldo.getFechaInicioEjercicio());
			}

			row.addText(fechaInicioEjercicioMostrar);

			row.addText(saldo.getSaldo() != null ? "$ " + formatoImporte.format(saldo.getSaldo()) : ""
				);

			String borrar =
					"<img src=\"" +
					themeDisplay.getPathThemeImages() +
					"/common/delete.png\" " +
					"style=\"cursor:pointer;\" " +
					"title=\"Borrar\" " +
					"onClick=\"javascript:borrarSaldoCuentaBancaria('" +
					saldo.getIdCuentaBcria() +
					"', '" +
					fechaInicioEjercicioParam +
					"');\" />";

			row.addText(borrar);
			resultRows.add(row);
		}
	}
	else {
		searchContainer.setTotal(0);
	}
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />