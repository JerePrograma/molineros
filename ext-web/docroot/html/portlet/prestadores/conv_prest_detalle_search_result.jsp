<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.DecimalFormatSymbols" %>
<%@ page import="java.util.Locale" %>

<portlet:defineObjects />

<%!
	private String formatearDecimalVista(BigDecimal value) {
		if (value == null) {
			return "";
		}

		DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "AR"));
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.');

		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		df.setGroupingUsed(true);
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);

		return df.format(value.setScale(2, RoundingMode.HALF_UP));
	}
%>

<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	ArrayList<ConvenioPrestacionalDetalle> convPrestDetalleList =
			(ArrayList<ConvenioPrestacionalDetalle>) request.getSession().getAttribute(
					WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION_DESGLOSE);

	if (convPrestDetalleList == null) {
		convPrestDetalleList =
				(ArrayList<ConvenioPrestacionalDetalle>) request.getSession().getAttribute(
						WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
	}

	String viewStr = ParamUtil.getString(request, WebKeysLiquidaciones.VIEW_CONVENIO_PREST);

	boolean esView = false;
	if (viewStr != null && viewStr.equalsIgnoreCase("true")) {
		esView = true;
	}

	PortletURL portletURLConvPrestDetalle = renderResponse.createRenderURL();

	List<String> headerNamesConvPrestDetalle = new ArrayList<String>();
	headerNamesConvPrestDetalle.add("Prestación");
	headerNamesConvPrestDetalle.add("Código");
	headerNamesConvPrestDetalle.add("Fecha Desde/Hasta");
	headerNamesConvPrestDetalle.add("Plan");
	headerNamesConvPrestDetalle.add("Servicio");
	headerNamesConvPrestDetalle.add("Coseguro");
	headerNamesConvPrestDetalle.add("Tipo Valorización");
	headerNamesConvPrestDetalle.add("Importe");
	headerNamesConvPrestDetalle.add("Porcentaje");

	if (!esView) {
		headerNamesConvPrestDetalle.add("action.DELETE");
	}

	SearchContainer searchContainerConvPrestDetalle = new SearchContainer(
			renderRequest,
			null,
			null,
			SearchContainer.DEFAULT_CUR_PARAM,
			Integer.MAX_VALUE,
			portletURLConvPrestDetalle,
			headerNamesConvPrestDetalle,
			LanguageUtil.get(pageContext, "no-convenios-prest-detalles-were-found"));

	if (convPrestDetalleList != null) {

		Collections.sort(convPrestDetalleList, new Comparator<ConvenioPrestacionalDetalle>() {
			public int compare(ConvenioPrestacionalDetalle o1, ConvenioPrestacionalDetalle o2) {
				String c1 = o1 != null && o1.getCodigo() != null ? o1.getCodigo() : "";
				String c2 = o2 != null && o2.getCodigo() != null ? o2.getCodigo() : "";
				return c1.compareTo(c2);
			}
		});

		int total = convPrestDetalleList.size();
		searchContainerConvPrestDetalle.setTotal(total);

		List resultRowsConvPrestDetalle = searchContainerConvPrestDetalle.getResultRows();

		for (int i = 0; i < convPrestDetalleList.size(); i++) {

			ConvenioPrestacionalDetalle convPrestDetalle =
					(ConvenioPrestacionalDetalle) convPrestDetalleList.get(i);

			String fechaDesdeStr = convPrestDetalle.getFechaDesde() != null
					? sdf.format(convPrestDetalle.getFechaDesde())
					: "";
			String fechaHastaStr = convPrestDetalle.getFechaHasta() != null
					? sdf.format(convPrestDetalle.getFechaHasta())
					: "";
			String fechasDesdeHasta = fechaDesdeStr + "-" + fechaHastaStr;

			if (esView || (!esView && !(
					(convPrestDetalle.getEstado() != null
							&& convPrestDetalle.getEstado().equals(ConvenioPrestacionalDetalle.ESTADOS.BAJA))
							|| convPrestDetalle.getBajaFecha() != null))) {

				ResultRow rowConvPrestDetalle =
						new ResultRow(convPrestDetalle, String.valueOf(convPrestDetalle.getId()), i);

				String prestacionDesc = "";
				if (convPrestDetalle.getPrestacion() != null
						&& convPrestDetalle.getPrestacion().getDescripcion() != null) {
					prestacionDesc = convPrestDetalle.getPrestacion().getDescripcion();
				}

				rowConvPrestDetalle.addText(prestacionDesc);
				rowConvPrestDetalle.addText(convPrestDetalle.getCodigo() != null ? convPrestDetalle.getCodigo() : "");
				rowConvPrestDetalle.addText(fechasDesdeHasta);
				rowConvPrestDetalle.addText(convPrestDetalle.getPlanDescripcion() != null ? convPrestDetalle.getPlanDescripcion() : "");
				rowConvPrestDetalle.addText(
						convPrestDetalle.getServicio() != null
								? (!convPrestDetalle.getServicio().equals("0") ? convPrestDetalle.getServicio() : "TODOS")
								: "");
				rowConvPrestDetalle.addText(formatearDecimalVista(convPrestDetalle.getCoseguro()));
				rowConvPrestDetalle.addText(
						convPrestDetalle.getTipoValorizacion() != null
								? convPrestDetalle.getTipoValorizacion().toUpperCase()
								: "");
				rowConvPrestDetalle.addText(formatearDecimalVista(convPrestDetalle.getImporte()));
				rowConvPrestDetalle.addText(formatearDecimalVista(convPrestDetalle.getPorcentaje()));

				StringBuilder sb = new StringBuilder();
				if ((convPrestDetalle.getEstado() != null
						&& convPrestDetalle.getEstado().equals(ConvenioPrestacionalDetalle.ESTADOS.BAJA))
						|| convPrestDetalle.getBajaFecha() != null) {
					sb.append("<img alt=\"Baja\" src=\"");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/close.png\"/>");
					rowConvPrestDetalle.addText(sb.toString());
				} else {
					if (!esView) {
						sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
						sb.append(themeDisplay.getPathThemeImages());
						sb.append("/common/delete.png\" onClick=\"javascript:borraConvPrestDetalle('");
						sb.append(convPrestDetalle.getId());
						sb.append("');\" />");
						rowConvPrestDetalle.addText(sb.toString());
					}
				}

				resultRowsConvPrestDetalle.add(rowConvPrestDetalle);
			}
		}
	}
%>
<%
	String msgConvenioFail = (String) request.getAttribute("msgConvenioFail");
%>

<liferay-ui:error
		key="conv-prest-validaciones"
		message="<%= Validator.isNotNull(msgConvenioFail) ? msgConvenioFail : \"No se pudo agregar el detalle.\" %>" />

<liferay-ui:search-iterator searchContainer="<%=searchContainerConvPrestDetalle%>" />