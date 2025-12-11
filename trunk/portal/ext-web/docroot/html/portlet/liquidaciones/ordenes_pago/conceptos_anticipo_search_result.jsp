<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%@page import="ar.com.ospim.global.beans.OrdenPago.FormaPago"%><portlet:defineObjects />
<%
	BigDecimal totalConceptos = BigDecimal.ZERO;
	boolean esEdicion = true;

	BigDecimal totalPagos = BigDecimal.ZERO;
	String ids  ="";
	OrdenPago  ordenPago = (OrdenPago) request.getSession().getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

	List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
	if (ordenPago != null && ordenPago.getFormaPago() != null && ordenPago.getFormaPago().size()>0) {
		for (OrdenPagoOspim.FormaPago fp : ordenPago.getFormaPago()) {
			if (fp.getTipo().equals("Anticipo")){
				Comprobante comp = ((Anticipo)fp.getPago()).getAnticipo();
				if (comp.getConceptos() != null){
					for (ComprobanteConcepto cc : comp.getConceptos()){
						if (conceptos.contains(cc)){
							ComprobanteConcepto ccEnLista = conceptos.get(conceptos.indexOf(cc));
								ccEnLista.setImporte(ccEnLista.getImporte().add(cc.getImporte()));
						} else {
						 		conceptos.add(new ComprobanteConcepto(cc.getConceptoComprobante(),cc.getImporte()));
						}
					}
				}
			}
		}
	}
	PortletURL portletURL = renderResponse.createRenderURL();
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("concepto");
	headerNames.add("importe");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-conceptos-were-found"));

	List resultRows = searchContainer.getResultRows();
	int total = conceptos.size();
	if (null != conceptos && conceptos.size() > 0) {
		for (int i = 0; i < conceptos.size(); i++) {
			ComprobanteConcepto comp = conceptos.get(i);
			if (comp.getImporte().compareTo(BigDecimal.ZERO) == 0){
				total--;
				continue;
			}
			ResultRow row = new ResultRow(comp, comp.hashCode(), i);
			row.addText(comp.getConceptoComprobante().getDescripcion());
			row.addText(comp.getImporte().negate().toString());
			totalConceptos = totalConceptos.add(comp.getImporte());
			resultRows.add(row);
		}
	}
	searchContainer.setTotal(total);
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<table width="100%" align="left">
<tr>
<td><label><liferay-ui:message key="importe-anticipos" />:</label>&nbsp;&nbsp;&nbsp;<%=totalConceptos.negate().toString()%></td>
<input type="hidden" id="total_anticipos" value="<%=totalConceptos.toString()%>"/>
</tr>
</table>