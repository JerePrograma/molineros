<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<%

PortletURL portletURL = renderResponse.createRenderURL();



Integer idLote = ParamUtil.getInteger(request, "nrolote");
if(idLote==null || idLote==0){
	idLote = (Integer)request.getAttribute("nrolote");
}

List<IntegracionDetalleDS> archivos= IntegracionServiceUtil.detalleDSByIdLoteSSS(idLote);

List<IntegracionDetalleDS> debitos = new ArrayList<IntegracionDetalleDS>();

for(IntegracionDetalleDS d:archivos){
	if(d.getImporteDebito()>0D){
		debitos.add(d);
	}
}


List<String> headerNames = new ArrayList<String>();
headerNames.add("CUIT");
headerNames.add("CUIL");
headerNames.add("Período Prestación");
headerNames.add("Cód.Prestación");
headerNames.add("Importe");
headerNames.add("Solicitado");
headerNames.add("Débito");
headerNames.add("Motivo");
headerNames.add("Borrar");
SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-archivos-were-found"));

boolean rolLiquidacion = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_LIQUIDACION);
boolean rolGeneracion = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_INTEGRACION_GENERACION);
					
if (debitos != null && !debitos.isEmpty()){
	int total = debitos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	NumberFormat nf = new DecimalFormat("#0.00");
	for (int i = 0; i < debitos.size(); i++) {	    
		IntegracionDetalleDS liq = (IntegracionDetalleDS) debitos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
	 	
	 	row.addText(liq.getCuitPrestador());
	 	row.addText(liq.getCuil());
	 	row.addText(liq.getPeriodoPrestacion().toString());
	 	row.addText(liq.getPrestacionCodigo());
	 	row.addText(nf.format(liq.getComprobanteImporte()/100));
	 	row.addText(nf.format(liq.getImporteSolicitado()/100));
	 	row.addText(nf.format(liq.getImporteDebito()));
	 	row.addText(liq.getMotivoDebito() );
	 	
	 	StringBuilder sb=new StringBuilder();
	 	
	 	sb=new StringBuilder();
	 	sb.append("&nbsp;&nbsp;<img alt=\"Borrar\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
	 	sb.append("/common/delete.png\" onClick=\"javascript:borrarDebito('");
	 	sb.append(liq.getId() );
	 	sb.append("','");
	 	sb.append(idLote);
	 	sb.append("');\"");
        sb.append(" title=\"Detalle\"");
 		sb.append("/>");
 		row.addText(sb.toString());
 		
 		
        	 	
	 	resultRows.add(row);
	 		
	}
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script type="text/javascript">

</script>