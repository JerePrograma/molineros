<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@page import="ar.com.ospim.util.DateUtils"%>

<portlet:defineObjects/>

<liferay-ui:error exception="<%= AddTercerizadoraException.class %>" message="tercerizadora-no-valida" />
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

	<% 
		//Si debe mostrarse el btn de agregar afiliado			
		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);
		String cuil = null;
		
		String cuil_attribute = (String) request.getAttribute("cuil_titular");
		if(null == cuil_attribute){
			cuil = request.getParameter("cuil_titular");
		}else{
			cuil = cuil_attribute;
		}
		String view=request.getParameter("view");
		int inte=0;
		if(null!=request.getParameter("inte")&&!request.getParameter("inte").trim().equals("")){
			inte=Integer.parseInt(request.getParameter("inte"));
		}
		///////
		List<AfiTercerizadoraServicio> tercerizadoraList= (ArrayList<AfiTercerizadoraServicio>)
										request.getSession().getAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION);
		/* if(null==tercerizadoraList){					
			tercerizadoraList=TercerizadoraServiceUtil.buscaTercerizadoras(cuil,inte);					
			request.getSession().setAttribute(WebKeysAfiliados.TERCERIZADORA_AFILIADO_EN_SESSION, tercerizadoraList);
		} */
		//////
		
		PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
 		List<String> headerNamesTercerizadora = new ArrayList<String>();
 		headerNamesTercerizadora.add("tercerizadora-servicio");		 		
		headerNamesTercerizadora.add("ingre-fecha");
		headerNamesTercerizadora.add("egreso-fecha");
		/* if(showABMButtons && (null==view || view.trim().equals(""))) { 
			headerNamesTercerizadora.add("editar-borrar");
		}	 */			
		SearchContainer searchContainerTercerizadora= new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
		LanguageUtil.get(pageContext, "no-tercerizadora-were-found"));
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		AfiTercerizadoraServicio terce = null;
		if(null!=tercerizadoraList){
			int total=tercerizadoraList.size();	 				
				searchContainerTercerizadora.setTotal(total);
			 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
				List resultRowsTercerizadora = searchContainerTercerizadora.getResultRows();
			 	for (int i = 0; i < tercerizadoraList.size(); i++) {
			 		terce = (AfiTercerizadoraServicio) tercerizadoraList.get(i);
			 		
			 		/* if (terce.isBorradoLogico()){ */
			 		if (terce.getEstado()!=null && terce.getEstado().equals(AfiTercerizadoraServicio.ESTADOS.BAJA)){	
			 			total--;
			 			continue;
			 		}
				ResultRow rowTercerizadora = new ResultRow(terce,terce.getTercerizadora().getId_tercerizadora(), i);			
 				rowTercerizadora.addText(terce.getTercerizadora().getDescripcion());	 					
 				rowTercerizadora.addText(sdf.format(terce.getFechaInicioPres()));
 				rowTercerizadora.addText(terce.getFechaFinPres()!=null?sdf.format(terce.getFechaFinPres()):"");
 				/* StringBuilder sb= new StringBuilder(); */
 				/* if(null==view || !view.trim().equals("true")){	 	
	 				sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
	 				sb.append(themeDisplay.getPathThemeImages());
	 				sb.append("/portlet/edit_guest.png\" onClick=\"javascript:editaTercerizadora('");
	 				sb.append(terce.getTercerizadora().getId_tercerizadora());
	 				sb.append("','");	 					
	 				sb.append(terce.getFecha_ingreAsString());	 					
	 				sb.append("','");
	 				sb.append(terce.getFecha_bajaAsString());
	 				sb.append("','");
	 				sb.append(terce.getFecha_ingreOriginalAsString());
	 				sb.append("');\" />");
	 				sb.append(" / ");
	 				sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
	 				sb.append(themeDisplay.getPathThemeImages());
	 				sb.append("/common/delete.png\" onClick=\"javascript:borraTercerizadora('");
	 				sb.append(terce.getTercerizadora().getId_tercerizadora());
	 				sb.append("','");	 					
	 				sb.append(terce.getFecha_ingreAsString());	 					
	 				sb.append("','");
	 				sb.append(terce.getFecha_bajaAsString());	 
	 				sb.append("','");
	 				sb.append(terce.getFecha_ingreOriginalAsString());
	 				sb.append("');\" />");
	 				rowTercerizadora.addText(sb.toString());
 				} */
 				resultRowsTercerizadora.add(rowTercerizadora);
			 	}
			}	 	
	%>

<liferay-ui:search-iterator searchContainer="<%=searchContainerTercerizadora%>" />
