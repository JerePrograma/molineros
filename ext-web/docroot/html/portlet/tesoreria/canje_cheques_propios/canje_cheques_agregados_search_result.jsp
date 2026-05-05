<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.tesoreria.OrdenPagoInexistenteException" %>
<%@ page import="ar.com.ospim.tesoreria.OrdenPagoAnuladaException" %>
<%@ page import="ar.com.ospim.liquidaciones.DuplicateNumeroChequeException" %>
<%@ page import="ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio" %>


<liferay-ui:error exception="<%= DuplicateNumeroChequeException.class %>" message="duplicate-cheque" />
	
<portlet:defineObjects />
<%
	List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
			.getSession().getAttribute(
					WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);
	
	CanjeChequePropio canjeChequePropio = (CanjeChequePropio) session
		.getAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION);
	List<Cheque> pagos = canjeChequePropio.getChequesNuevos();

	PortletURL portletURLTercerizadora = renderResponse
			.createRenderURL();
	List<String> headerNamesTercerizadora = new ArrayList<String>();
	headerNamesTercerizadora.add("tipo");
	headerNamesTercerizadora.add("numero");
	headerNamesTercerizadora.add("importe");
	headerNamesTercerizadora.add("cuenta-bancaria");
	headerNamesTercerizadora.add("a-nombre-de");
	headerNamesTercerizadora.add("delete");
	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURLTercerizadora,
			headerNamesTercerizadora, LanguageUtil.get(pageContext,
					"no-cheques-were-found"));

	int cant = 0;
	int total = 0;
	BigDecimal importe = BigDecimal.ZERO;
	if (null != pagos) {
		 total = pagos.size();
		List resultRowsInspector = searchContainer.getResultRows();
		for (int i = 0; i < pagos.size(); i++) {
			Cheque pago = pagos.get(i);
				ResultRow row = new ResultRow(pago,	pago.getNumeroStr(), i);
				row.addText(pago.getTipo());
				row.addText(pago.getNumeroStr());
				row.addText(pago.getImporte().toString());
				importe = importe.add(pago.getImporte());
				if (pago.getCuentaBancaria() != null && pago.getCuentaBancaria().getId_cuenta_bcria() != 0) {
					int index = ctas.indexOf(pago.getCuentaBancaria());
					row.addText(ctas.get(index).getDescripcion() + " "
							+ ctas.get(index).getNro_cuenta() + "/"
							+ ctas.get(index).getSucursal());
				} else {
					row.addText("");
				}
				row.addText(pago.getANombreDe() != null ? pago.getANombreDe() : "");
				if (canjeChequePropio.getId() == 0){
				StringBuilder sb= new StringBuilder();
					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/delete.png\" onClick=\"javascript:borraPago('");
					sb.append(pago.getNumeroStr());
					sb.append("','");
					if (pago.getCuentaBancaria() != null) {
						sb.append(pago.getCuentaBancaria().getId_cuenta_bcria());
					} else {
						sb.append(0);
					}
					sb.append("','");
					sb.append(pago.getImporte().toString());
					sb.append("');\" />");
					row.addText(sb.toString());
				} else {
					row.addText("");
				}
				resultRowsInspector.add(row);
		}
	}
		searchContainer.setTotal(total);
%>

<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainer%>" />

<table width="100%">
	<tr>
		<td>Importe total de cheques nuevos:&nbsp;<input type="text" disabled="disabled" id="total_nuevos" value="<%= importe.toString()%>"/></td>
	</tr>
</table>