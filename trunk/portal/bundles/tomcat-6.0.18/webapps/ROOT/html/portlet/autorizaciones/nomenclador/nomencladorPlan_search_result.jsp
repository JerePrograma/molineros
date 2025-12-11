<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.NomencladorPlan" %>

<portlet:defineObjects/>
			<%
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			 
			List<NomencladorPlan>  conceptos= (List<NomencladorPlan>) request.getSession().getAttribute(WebKeysAutorizaciones.MODALIDAD_ATENCION);
		
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);

			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = true;			

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("Plan");
	 		headerNamesTercerizadora.add("Autorizacion");
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-modalidadesAtencion-were-found"));
		
			
			if(null!=conceptos){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < conceptos.size(); i++) {
 			 		NomencladorPlan modalidad = conceptos.get(i);
 			 		if (modalidad.getBaja_fecha()!=null ){
 			 			continue;
 			 		}
	 					ResultRow row = new ResultRow(modalidad, modalidad.getId(), i);
	 					row.addText(modalidad.getPlan().getDescripcion());
	 					row.addText(modalidad.getAutorizacion()!=null?modalidad.getAutorizacion().getDescripcion():"");
	 					
	 					if (showABMButtons && esEdicion){
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraModalidad('");
		 					sb.append(modalidad.getPlan().getId());
		 					sb.append("|");
		 					sb.append(modalidad.getAutorizacion()!=null?modalidad.getAutorizacion().getId():-1);
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
	 			 		} else {
	 			 			row.addText("");
	 			 		}
	 					resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
		
		