<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.ChequeRechazadoException" %>

<liferay-ui:error exception="<%=ChequeRechazadoException.class %>" message="cheques-ya-rechazados" />
<portlet:defineObjects/>
			<% 	
				boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
				List<MovimientoBancario> movList= (List<MovimientoBancario>)renderRequest.getAttribute(WebKeysTesoreria.MOVS_BCRIOS);				
				PortletURL portletURLSitu = renderResponse.createRenderURL();
		 		List<String> headerNamesSitu = new ArrayList<String>();
		 		headerNamesSitu.add("id_movimiento");		 		 		
		 		headerNamesSitu.add("fecha-mov");
		 		headerNamesSitu.add("movimiento");	
		 		headerNamesSitu.add("cta-bcria");
		 		headerNamesSitu.add("fecha-comprobante");
		 		headerNamesSitu.add("nro-comprobante");				
				headerNamesSitu.add("descripcion");
				headerNamesSitu.add("importe");
				headerNamesSitu.add("baja-fecha");
				headerNamesSitu.add("editar-borrar");
				
				String view="";
							
				SearchContainer searchContainerMov= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,1000, portletURLSitu, headerNamesSitu,
				LanguageUtil.get(pageContext, "no-movimiento-bancario-were-found"));
			
				if(null!=movList){
					int total=movList.size();	 				
	 				searchContainerMov.setTotal(total);
	 			 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRowsSitu = searchContainerMov.getResultRows();
	 			 	for (int i = 0; i < movList.size(); i++) {
	 			 		MovimientoBancario mov = (MovimientoBancario) movList.get(i);
	 			 		ResultRow rowSitu=null;
	 			 		
	 			 		rowSitu = new ResultRow(mov,mov.getId_movimiento(), i);
	 			 			 						 			
	 					rowSitu.addText(mov.getId_movimiento() == 0 ? "" : String.valueOf(mov.getId_movimiento()));
	 					rowSitu.addText(mov.getFecha_movimientoAsString());	 						 					
	 					rowSitu.addText(mov.getTipo_mov().getDescripcion());	
	 					rowSitu.addText(mov.getCta_bcria().getDescripcion());
	 					rowSitu.addText(mov.getFecha_comprobanteAsString());
	 					rowSitu.addText(mov.getNro_comprobante() == null ? "" : mov.getNro_comprobante());
	 					rowSitu.addText(mov.getDescripcion());
	 					rowSitu.addText(mov.getImporteAsString());
	 					rowSitu.addText(mov.getBaja_fechaAsString());
		 				StringBuilder sb= new StringBuilder();	 
	 					if (mov.getId_movimiento() != 0 && !soloVer){
		 					sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/portlet/edit.png\" onClick=\"javascript:editaMovBcrio('");
		 					sb.append(mov.getId_movimiento());						 					
		 					sb.append("');\" />");
		 					sb.append(" / ");
		 					if (!mov.getTipo_mov().getDescripcion().equals("CANJE DE CHEQUES PROPIOS")){
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borraMovBcrio('");	 					
			 					sb.append(mov.getId_movimiento());	 					
			 					sb.append("');\" />");
		 					}
	 					}else if(!soloVer){
	 						sb.append("");
	 					}
	 					rowSitu.addText(sb.toString());
	 					resultRowsSitu.add(rowSitu);
	 			 	}
	 			}
	 	
 		%>
 	<c:choose>		
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
			<liferay-ui:success key="request_processed" message="grabar-exitoso" />
		</c:when>		
	</c:choose>
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />	
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainerMov%>" />

		
