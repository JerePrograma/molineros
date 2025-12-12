<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.beans.FichaBoletaPortal"%>
<%@ page import="java.text.DecimalFormat"%>
<% 
String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

DecimalFormat fm = new DecimalFormat("###0.00");

List<FichaBoletaPortal> boletas= (List<FichaBoletaPortal>)request.getSession().getAttribute(WebKeysTesoreria.BOLETA_EMPLEADORES_IMPAGAS);


 
%>



<portlet:defineObjects/>
			<% 
			
			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("Tipo");
	 		headerNamesTercerizadora.add("Nro");
	 		headerNamesTercerizadora.add("Período");
	 		headerNamesTercerizadora.add("Vencimiento");
	 		headerNamesTercerizadora.add("Importe");
	 		SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			"No se encontraron Boletas Impagas");
		
			
			if(null!=boletas){
				int total=boletas.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < boletas.size(); i++) {
 			 			FichaBoletaPortal boleta = boletas.get(i);
 			 			ResultRow row = new ResultRow(boleta,boleta.getNro_boleta_portal_emple(), i);
	 					row.addText(boleta.getDescripcion());
	 					row.addText(String.valueOf(boleta.getNro_boleta_portal_emple()));
	 					row.addText(boleta.getPeriodoAsString());
	 					row.addText(boleta.getFecha_ing());
	 					row.addText(fm.format(  (boleta.getCapital().add(boleta.getInteres())).add(boleta.getAjusteCapital()).doubleValue())); 					
	 					resultRowsInspector.add(row);
	 					
 			 	}
 				searchContainer.setTotal(total);
	 		}
 		%>
 		
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />

