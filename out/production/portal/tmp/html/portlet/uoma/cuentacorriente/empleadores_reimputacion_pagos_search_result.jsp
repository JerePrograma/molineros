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

List<FichaBoletaPortal> boletas= (List<FichaBoletaPortal>)request.getSession().getAttribute("BOLETAS_EMPLEADOR");


 
%>



<portlet:defineObjects/>
			<% 
			
			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("CUIT");
	 		headerNamesTercerizadora.add("Tipo");
	 		headerNamesTercerizadora.add("Nro");
	 		headerNamesTercerizadora.add("Período");
	 		headerNamesTercerizadora.add("Recaudación");
	 		headerNamesTercerizadora.add("Rendición");
	 		headerNamesTercerizadora.add("Nro.Movimiento");
	 		headerNamesTercerizadora.add("Cheque");
	 		headerNamesTercerizadora.add("Importe");
	 		headerNamesTercerizadora.add("Comando");
	 		SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			"No se encontraron Boletas");
		
			
			if(null!=boletas){
				int total=boletas.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < boletas.size(); i++) {
 			 			FichaBoletaPortal boleta = boletas.get(i);
 			 			ResultRow row = new ResultRow(boleta,boleta.getNro_boleta_portal_emple(), i);
 			 			row.addText(boleta.getCuit());
	 					row.addText(boleta.getDescripcion());
	 					row.addText(String.valueOf(boleta.getNro_boleta_portal_emple()));
	 					row.addText(boleta.getPeriodoAsString());
	 					row.addText(boleta.getFecha_recaudaAsString());
	 					row.addText(boleta.getFecha_rendicionAsString());
	 					row.addText(boleta.getNroMovimiento());
	 					row.addText(String.valueOf(boleta.getNro_cheque()));
	 					row.addText(fm.format(boleta.getImporte()));
	 					
	 					
	 					StringBuilder sb= new StringBuilder();		
	 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
		 				sb.append(themeDisplay.getPathThemeImages());
		 				sb.append("/common/edit.png\" onClick=\"javascript:editaBoletaPago('");
		 				sb.append(boleta.getCuit());
		 				sb.append("','");
		 				sb.append(boleta.getTipoBoleta());
		 				sb.append("','");
		 				sb.append(boleta.getNro_boleta_portal_emple());
		 				sb.append("','");
		 				sb.append(boleta.getNroMovimiento());
		 				sb.append("');\" />");
	 					row.addText(sb.toString());			
	 					
	 					
	 					resultRowsInspector.add(row);
	 					
 			 	}
 				searchContainer.setTotal(total);
	 		}
 		%>
 		
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
	
	
<script type="text/javascript">
   
	function editaBoletaPago(cuit,tipo,numero,movimiento){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/empleadores_reimputacion_pagos" /></portlet:renderURL>';
        //url += "&accion=edit";
        url += '&cuit='+cuit;
        url += "&tipo_boleta="+tipo;
        url += "&nro_boleta="+numero;
        url += "&nro_movimiento="+movimiento;
        url += '&cmd=edit';
        document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
</script>	

