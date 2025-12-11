<%@ include file="/html/portlet/empresas/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>	
			<%				
				EntidadPadronUnificado empresa = (EntidadPadronUnificado)portletSession.getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION,PortletSession.APPLICATION_SCOPE);				
				if(empresa==null){
					
					LlamadosEstudio llest = null; 
					try{
						llest=(LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
					}catch(Exception e){
						
					}
					empresa=llest!=null?llest.getEmpresa():null;
				}
				List<Domicilio> domicilios=null;
				
				if(null!=empresa){
					domicilios=empresa.getDomicilios();
				}
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("tipo");
		 		headerNames.add("calle");
		 		headerNames.add("numero");		 		
		 		headerNames.add("piso");
		 		headerNames.add("departamento");
		 		headerNames.add("provincia");
		 		headerNames.add("localidad");
		 		headerNames.add("codigo-postal");
		 		headerNames.add("observaciones");
		 		headerNames.add("vigen-fecha");		 		
		 		headerNames.add("delete");		 			 		
		 		
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-domicilios-were-found"));			
			
				if(null!=domicilios){%>
					<input type="hidden" name="<portlet:namespace />tiene_domicilios" id="<portlet:namespace />tiene_domicilios" value="true"/>
				<%
	 				//Seteo el total de la lista.
				 	int total = domicilios.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < domicilios.size(); i++) {				 		
				 		Domicilio domicilio = (Domicilio) domicilios.get(i);
				 		StringBuilder sb= new StringBuilder();
				 		
				 		ResultRow row = new ResultRow(domicilio,domicilio.getDomi_tipo(), i);
		 				row.addText(domicilio.getDomi_tipo());
		 				row.addText(domicilio.getCalle());
		 				row.addText(null!=domicilio.getNumero()?domicilio.getNumero():"");
		 				row.addText(null!=domicilio.getPiso()?domicilio.getPiso():"");
		 				row.addText(null!=domicilio.getDepto()?domicilio.getDepto():"");
		 				row.addText(null!=domicilio.getProvinciaAsString()?domicilio.getProvinciaAsString():"");
		 				row.addText(null!=domicilio.getLocalidadAsString()?domicilio.getLocalidadAsString():"");		 				
		 				row.addText(null!=domicilio.getPostal_codi()?domicilio.getPostal_codi():"");
		 				row.addText(null!=domicilio.getObservaciones()?domicilio.getObservaciones():"");
		 				row.addText(domicilio.getModi_fechaAsString());
				 		
				 		if(null==domicilio.getBaja_fecha() 
				 				|| (domicilio.getEstado()!=null && !domicilio.getEstado().equals(Domicilio.ESTADOS.BAJA))
				 		  ){
		 					
			 				if(empresa instanceof Empresa && domicilio.getDomi_tipo().equals("PORTAL")){
					 			sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
					 			sb.append(themeDisplay.getPathThemeImages());
					 			sb.append("/common/delete.png\" onClick=\"javascript:borraDomicilio('");			 			
					 			sb.append(domicilio.getDomi_tipo());
					 			sb.append("','");
					 			sb.append(domicilio.getProvincia()!=null?domicilio.getProvincia().getId():0);
					 			sb.append("','");			 			
					 			sb.append(domicilio.getLocalidad()!=null?domicilio.getLocalidad().getId():0);
					 			sb.append("','");
					 			sb.append(domicilio.getPostal_codi());	
					 			sb.append("','");			 			
					 			sb.append(domicilio.getCalle());	
					 			sb.append("','");
					 			sb.append(domicilio.getNumero());	
					 			sb.append("','");
					 			sb.append(domicilio.getPiso());	
					 			sb.append("','");
					 			sb.append(domicilio.getDepto());	
					 			sb.append("','");
					 			sb.append(domicilio.getId_domicilio());
					 			sb.append("');\" /> /");
					 			
					 			sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
					 			sb.append(themeDisplay.getPathThemeImages());
					 			sb.append("/common/edit.png\" onClick=\"javascript:editaDomicilio('");			 			
					 			sb.append(domicilio.getDomi_tipo());
					 			sb.append("','");
					 			sb.append(domicilio.getProvincia()!=null?domicilio.getProvincia().getId():0);
					 			sb.append("','");			 			
					 			sb.append(domicilio.getLocalidad()!=null?domicilio.getLocalidad().getId():0);
					 			sb.append("','");
					 			sb.append(domicilio.getPostal_codi());	
					 			sb.append("','");			 			
					 			sb.append(domicilio.getCalle());	
					 			sb.append("','");
					 			sb.append(domicilio.getNumero());	
					 			sb.append("','");
					 			sb.append(domicilio.getPiso());	
					 			sb.append("','");
					 			sb.append(domicilio.getDepto());	
					 			sb.append("','");
					 			sb.append(domicilio.getId_domicilio());
					 			sb.append("','");
					 			sb.append(null!=domicilio.getObservaciones()?domicilio.getObservaciones():"");
					 			sb.append("');\" />");
					 			row.addText(sb.toString());
					 			
			 				}else{
			 					row.addText("");
			 				}
				 			
				 		}else{
				  			sb.append("&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
				  			row.addText(sb.toString());
				 		}
				 		resultRows.add(row);
				 	}
	 			}else{%>
	 				<input type="hidden" name="<portlet:namespace />tiene_domicilios" id="<portlet:namespace />tiene_domicilios" value="false"/>
	 			<%}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	
