<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%	
	
	/* Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION); */

	List<AfiObservacion> obs = null;
	
	/* if(afiliado != null){
		obs = afiliado.getObservacionesInternas();
	}else{
		obs = 	(ArrayList<AfiObservacion>) request.getAttribute(WebKeysAfiliados.OBSERVACIONES_GRUPO_FLIAR);

	} */
	obs = 	(ArrayList<AfiObservacion>) request.getAttribute(WebKeysAfiliados.OBSERVACIONES_GRUPO_FLIAR);
	
	PortletURL portletURL = renderResponse.createRenderURL();				

	List<String> headerNames = new ArrayList<String>();
	headerNames.add("observaciones");
	headerNames.add("cuil-titular");
	headerNames.add("inte");
	headerNames.add("Alta Fecha");
	headerNames.add("Alta Usr");
	headerNames.add("Ver");
	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
	SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
	LanguageUtil.get(pageContext, "no-obs-interna-were-found"));

	if(obs!=null){
							 	
		//Seteo el total de la lista.
	 	int total = obs.size();
	 	searchContainer.setTotal(total);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	 	List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < obs.size(); i++) {

	 		AfiObservacion ao = (AfiObservacion) obs.get(i);
	 		
	 		ResultRow row = new ResultRow(ao,ao.getCuilTitular(), i);
				
	 		row.addText(ao.getObservacion());
			row.addText(ao.getCuilTitular());
			row.addText(String.valueOf(ao.getInte()));
			row.addText(sdf.format(ao.getAltaFecha()));
			row.addText(ao.getAltaUsr());
			
			StringBuilder sb= new StringBuilder();		
			sb.append("<img alt=\"<liferay-ui:message key='obs-interna'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/common/view.png\" onClick=\"javascript:verObservacionInterna('");
			sb.append(ao.getId());
			sb.append("');\" />");
			row.addText(sb.toString());	
			
 			resultRows.add(row);
	 	}
	}
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	



<script type="text/javascript">

function verObservacionInterna(idObs) {
	var params = "&<%= Constants.CMD %>=" + "<%= Constants.VIEW%>";
	params = params + '&idObservacionInt='+idObs;
	
	var popupObs = new Liferay.Popup({title:"<liferay-ui:message key="obs-interna" />",modal:true, width: 880, height:500, position:['center',30]});
	
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_observaciones_internas';   		       	
	
 	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
	    url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cai/buscar_observaciones_internas';
    </c:if> 
 	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_AUT_1_"))%>'>
    	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/buscar_observaciones_internas';
	</c:if> 
    url = url + params;
	jQuery(popupObs).load(url);	
	
	
	
}

</script>

