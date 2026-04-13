<%@ include file="/html/portlet/farmacia_ospim/init.jsp" %>  
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

			<% 
				
				String prefijo=ParamUtil.getString(request, "origen","");
				String view=ParamUtil.getString(request,"view");
				String checkbox=ParamUtil.getString(request,"checkbox");
				boolean showABMButtons = true;
				List<ColegioFarmacia> colegioList= (ArrayList<ColegioFarmacia>)renderRequest.getAttribute(WebKeysFarmaciaOspim.BUSQUEDA_COLEGIO );				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("Codigo");
		 		headerNames.add("Descripcion");		 		
				headerNames.add("choose");
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-colegio-were-found-that-matched-the-keywords-x"));
			
				if(null!=colegioList){	
	 				//Seteo el total de la lista.
				 	int total = colegioList.size();
	 				if (total == 1){
	 					ColegioFarmacia colegio = (ColegioFarmacia) colegioList.get(0);
	 					%>
	 					<script type="text/javascript">						
	 					seleccionaColegio<%=prefijo%>('<%=colegio.getCodigo()%>','<%=colegio.getDescripcion() %>');	 				
	 					</script>
	 					<%
	 				} else {	 				
					 	searchContainer.setTotal(total);					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < colegioList.size(); i++) {
					 		ColegioFarmacia colegio = (ColegioFarmacia) colegioList.get(i);
		 					ResultRow row = new ResultRow(colegio,colegio.getCodigo(), i);		 				
			 				row.addText(colegio.getCodigo());
			 				row.addText(colegio.getDescripcion());		
							StringBuilder sb= new StringBuilder();
							if(null!=checkbox && !checkbox.trim().equals("")){
								sb.append("<input type=\"checkbox\""); 
								sb.append("name=\"");
								sb.append(colegio.getCodigo());
								sb.append("\" id=\"");
								sb.append(colegio.getCodigo());
								sb.append("\" value=\"");
								sb.append(colegio.getCodigo());
								sb.append("\"/>");
								row.addText(sb.toString());
							}else{
								if(null==view || !view.trim().equals("true")){
				 					sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
				 					sb.append(themeDisplay.getPathThemeImages());
				 					sb.append("/portlet/edit_guest.png\" onClick=\"javascript:seleccionaColegio");
				 					sb.append(prefijo+"('");
				 					sb.append(colegio.getCodigo() );
				 					sb.append("','");
				 					sb.append(colegio.getDescripcion() );
				 					sb.append("');\" />");
				 					row.addText(sb.toString());			 					
			 					}
							}
				 			resultRows.add(row);
					 	}
	 				}
	 			}
	 	
 		%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	<%if(null!=checkbox && !checkbox.trim().equals("")){ %>
		<div align="right">
			<input id="<portlet:namespace />seleccionarColegioFarmacia" value="<liferay-ui:message key="choose"/>" title="<liferay-ui:message key="seleccionar" />" type="button" onClick="javascript:<portlet:namespace />seleccionarColegioSeteado();"/>
		</div>		
	<%} %>

<script type="text/javascript">	
	function <portlet:namespace />seleccionarColegioSeteado(){
		var inputs=jQuery('input:checkbox');
		var aux=serializaInputs(inputs);		
		
	}	 		

	function serializaInputs(inputText){
		var i=0;
		var text='';				
		for(i=0;i<inputText.length;i++){
			if(inputText[i].checked){				
				text=text+'-'+inputText[i].id;
			}			 
		}		
		return "&codigosColegios="+text;
	}	
</script>