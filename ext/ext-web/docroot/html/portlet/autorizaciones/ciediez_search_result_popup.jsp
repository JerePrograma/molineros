<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

			<% 
				
				String prefijo=ParamUtil.getString(request, "origen","");
				String view=ParamUtil.getString(request,"view");
				String checkbox=ParamUtil.getString(request,"checkbox");
				boolean showABMButtons = true;//PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
				List<CieDiez> cieDiezList= (ArrayList<CieDiez>)renderRequest.getAttribute(WebKeysAfiliados.BUSQUEDA_CIEDIEZ );				
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("Codigo");
		 		headerNames.add("Descripcion");		 		
				headerNames.add("choose");
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-cie-diez-were-found-that-matched-the-keywords-x"));
			
				if(null!=cieDiezList){
	 								 	
	 				//Seteo el total de la lista.
				 	int total = cieDiezList.size();
	 				if (total == 1){
	 					CieDiez cieDiez = (CieDiez) cieDiezList.get(0);
	 					%>
	 					<script type="text/javascript">						
	 					seleccionaCieDiez<%=prefijo%>('<%=cieDiez.getCodigo()%>','<%=cieDiez.getDescripcion() %>');	 				
	 					</script>
	 					<%
	 				} else {
	 				
					 	searchContainer.setTotal(total);					 	
		 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < cieDiezList.size(); i++) {
					 		CieDiez cieDiez = (CieDiez) cieDiezList.get(i);
		 					ResultRow row = new ResultRow(cieDiez,cieDiez.getCodigo(), i);		 				
			 				row.addText(cieDiez.getCodigo());
			 				row.addText(cieDiez.getDescripcion());		
							StringBuilder sb= new StringBuilder();
							if(null!=checkbox && !checkbox.trim().equals("")){
								sb.append("<input type=\"checkbox\""); 
								sb.append("name=\"");
								sb.append(cieDiez.getCodigo());
								sb.append("\" id=\"");
								sb.append(cieDiez.getCodigo());
								sb.append("\" value=\"");
								sb.append(cieDiez.getCodigo());
								sb.append("\"/>");
								row.addText(sb.toString());
							}else{
								if(null==view || !view.trim().equals("true")){
				 					sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
				 					sb.append(themeDisplay.getPathThemeImages());
				 					sb.append("/portlet/edit_guest.png\" onClick=\"javascript:seleccionaCieDiez");
				 					sb.append(prefijo+"('");
				 					sb.append(cieDiez.getCodigo() );
				 					sb.append("','");
				 					sb.append(cieDiez.getDescripcion() );
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
			<input id="<portlet:namespace />seleccionarCieDiez" value="<liferay-ui:message key="choose"/>" title="<liferay-ui:message key="seleccionar" />" type="button" onClick="javascript:<portlet:namespace />seleccionarCieDiezs();"/>
		</div>		
	<%} %>

<script type="text/javascript">	
	function <portlet:namespace />seleccionarCieDiezs(){
		var inputs=jQuery('input:checkbox');
		var aux=serializaInputs(inputs);		
		<portlet:namespace />pedirCredencial(aux);						
	}	 		

	function serializaInputs(inputText){
		var i=0;
		var text='';				
		for(i=0;i<inputText.length;i++){
			if(inputText[i].checked){				
				text=text+'-'+inputText[i].id;
			}			 
		}		
		return "&codigosCieDiez="+text;
	}	
</script>