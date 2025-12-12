<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.tesoreria.OrdenPagoInexistenteException" %>
<%@ page import="ar.com.ospim.tesoreria.OrdenPagoAnuladaException" %>
<%@ page import="ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio" %>


	<liferay-ui:error exception="<%= OrdenPagoAnuladaException.class %>" message="orden-pago-anulada-exception" />
	<liferay-ui:error exception="<%= OrdenPagoInexistenteException.class %>" message="orden-pago-inexistente-exception" />
	
<portlet:defineObjects />
<%
	List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
			.getSession().getAttribute(
					WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);

	CanjeChequePropio canjeChequePropio = (CanjeChequePropio) session
		.getAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION);
	List<CanjeChequePropio.ChequeACanjear> pagos = canjeChequePropio.getChequesViejos();

	PortletURL portletURLTercerizadora = renderResponse
			.createRenderURL();
	List<String> headerNamesTercerizadora = new ArrayList<String>();
	headerNamesTercerizadora.add("tipo");
	headerNamesTercerizadora.add("numero");
	headerNamesTercerizadora.add("importe");
	headerNamesTercerizadora.add("cuenta-bancaria");
	headerNamesTercerizadora.add("a-nombre-de");
	headerNamesTercerizadora.add("canjear");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURLTercerizadora,
			headerNamesTercerizadora, LanguageUtil.get(pageContext,
					"no-cheques-were-found"));

	BigDecimal importeTotal = BigDecimal.ZERO;
	int cant = 0;
	if (null != pagos) {
		int total = pagos.size();
		List resultRowsInspector = searchContainer.getResultRows();
		for (int i = 0; i < pagos.size(); i++) {
			CanjeChequePropio.ChequeACanjear chequeACanjear = pagos.get(i);
			Cheque pago = chequeACanjear.getCheque();
			if (pago.getTipo().equals("Cheque")) {
				ResultRow row = new ResultRow(pago,	pago.getNumeroStr(), i);
				row.addText(pago.getTipo());
				row.addText(pago.getNumeroStr());
				row.addText(pago.getImporte().toString());
				if (pago.getCuentaBancaria() != null && pago.getCuentaBancaria().getId_cuenta_bcria() != 0) {
					int index = ctas.indexOf(pago.getCuentaBancaria());
					row.addText(ctas.get(index).getDescripcion() + " "
							+ ctas.get(index).getNro_cuenta() + "/"
							+ ctas.get(index).getSucursal());
				} else {
					row.addText("");
				}
				row.addText(pago.getANombreDe() != null ? pago.getANombreDe() : "");
				
				StringBuilder sb = new StringBuilder();
				cant++;
				if (canjeChequePropio.getId() != 0 || chequeACanjear.getCheque().getEstado().getId() != Cheque.Estado.CHEQUE_PROPIO_CANJEADO){
					sb.append("<input type='checkbox' value='canjear_cheque_"+pago.getNumeroStr()+"_"+pago.getCuentaBancaria().getId_cuenta_bcria()+"' name='canjear_cheque_"+i+"' id='canjear_cheque_"+i+"' ");
					if (chequeACanjear.isCanjeado()){
						importeTotal = importeTotal.add(pago.getImporte());
						sb.append(" checked='checked' ");
					}
					if (canjeChequePropio.getId() != 0){
						sb.append(" disabled='disabled' ");
					}
					sb.append(" onchange=\"javascript:sumarImporteViejo('canjear_cheque_"+i+"', '"+ pago.getImporte().toString() + "')\"/>");
				} else {
					sb.append("El cheque ya se encuentra canjeado");
				}
				row.addText(sb.toString());
				resultRowsInspector.add(row);
			}
		}
		searchContainer.setTotal(total);
	}
%>

<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainer%>" />
<input type="hidden" id="cantidad_cheques" name="cantidad_cheques" value="<%=String.valueOf(cant)%>"/>
<table width="100%">
	<tr>
		<td>Importe total de cheques a canjear:&nbsp;<input type="text" disabled="disabled" id="total_viejos" value="<%=importeTotal.toString()%>"/></td>
	</tr>
</table>
<script type="text/javascript">
	function sumarImporteViejo(chkbox, valor){
		if (document.getElementById(chkbox).checked) {
			jQuery("#total_viejos").val(parseFloat(jQuery("#total_viejos").val()) + parseFloat(valor));
		} else {
			jQuery("#total_viejos").val(parseFloat(jQuery("#total_viejos").val()) - parseFloat(valor));
		}
	}
</script>