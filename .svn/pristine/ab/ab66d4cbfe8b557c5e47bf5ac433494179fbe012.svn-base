<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<portlet:defineObjects/>
			<%
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA);
				String view=ParamUtil.getString(request,"view");
				
				String cuil_titular = request.getParameter("cuil");
				String inte = request.getParameter("inte");
								
				List<Catastro> catastroList = (List<Catastro>)request.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CATASTRAL);
				if (catastroList == null) {
					catastroList = CatastroServiceUtil.buscaCatastro(cuil_titular, Integer.valueOf(inte));
				}
				
				PortletURL portletURLCatastro = renderResponse.createRenderURL();
		 		List<String> headerNamesCatastro = new ArrayList<String>();
		 		headerNamesCatastro.add("Fecha");
		 		headerNamesCatastro.add("Código NN");
		 		headerNamesCatastro.add("Descripción");
		 		headerNamesCatastro.add("Pieza");
		 		headerNamesCatastro.add("Cara");

		 		if(showABMButtons && (null==view || !view.equals("true"))) {
		 			headerNamesCatastro.add("borrar");
				}
				SearchContainer searchContainerCatastro= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLCatastro, headerNamesCatastro,
				LanguageUtil.get(pageContext, "there-are-no-catastro-registrations"));
							
				if(null!=catastroList){
					int total=catastroList.size();
	 				searchContainerCatastro.setTotal(total);	 			 	
	 				List resultRowsCatastro = searchContainerCatastro.getResultRows();
	 			 	for (int i = 0; i < catastroList.size(); i++) {
	 			 		Catastro catastro = (Catastro) catastroList.get(i);
	 			 		ResultRow rowCatastro = null;
	 			 		rowCatastro = new ResultRow(catastro,catastro.getId(), i);	 						 
	 			 		rowCatastro.addText(catastro.getFechaAsString());
	 			 		rowCatastro.addText(catastro.getCodigo());
	 			 		//TODO CUANDO SE INCORPOREN TODAS LAS PRESTACIONES
	 			 		//rowCatastro.addText(catastro.getPlan_prestacion().getNomenclador().getDescripcion());
	 			 		rowCatastro.addText("Extracción");
	 			 		rowCatastro.addText(new Integer(catastro.getPieza()).toString());
	 			 		rowCatastro.addText(catastro.getCara());
	 					if(showABMButtons && (null==view || !view.equals("true"))) {
		 					StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraCatastro('");
		 					sb.append(String.valueOf(catastro.getId()));
		 					sb.append("','");
		 					sb.append(catastro.getAfiliado().getCuil_titular());
		 					sb.append("','");
		 					sb.append(String.valueOf(catastro.getAfiliado().getInte()));
		 					sb.append("');\" />");		 							 				
		 					rowCatastro.addText(sb.toString());
	 					}
	 					resultRowsCatastro.add(rowCatastro);	 					
	 			 	}
	 			}
 		%>
 	<c:choose>
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
			<liferay-ui:success key="request_processed" message="grabar-exitoso" />
		</c:when>		
	</c:choose>
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainerCatastro%>" />

	<script type="text/javascript">	
	</script>