<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.NomencladorPlan" %>
<%@ page import="java.text.DecimalFormat" %>

<portlet:defineObjects/>
			<%
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			DecimalFormat df= new DecimalFormat("#0.00"); 
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			List<NomencladorPlan>  conceptos= (List<NomencladorPlan>) request.getSession().getAttribute(WebKeysAutorizaciones.TOPES_REINTEGROS);
		
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
	 		headerNamesTercerizadora.add("Tope");
	 		headerNamesTercerizadora.add("Desde");
	 		headerNamesTercerizadora.add("Hasta");
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Editar");
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-topes-reintegros-were-found"));
		
			
			if(null!=conceptos){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < conceptos.size(); i++) {
 			 		NomencladorPlan modalidad = conceptos.get(i);
 			 		if (modalidad.getBaja_fecha()!=null ){
 			 			continue;
 			 		}
	 					ResultRow row = new ResultRow(modalidad, modalidad.getId(), i);
	 					row.addText(modalidad.getPlan().getDescripcion());
	 					row.addText(df.format(modalidad.getTopeReintegro()));
	 					row.addText(sdf.format(modalidad.getVigencia_desde() ));
	 					row.addText(modalidad.getVigencia_hasta()!=null?sdf.format(modalidad.getVigencia_hasta()):"");
	 					
	 					if (showABMButtons && esEdicion){
	 						Integer ddeDia=0;
	 						Integer ddeMes=0;
	 						Integer ddeAnio=0;
	 						
	 						Integer htaDia=0;
	 						Integer htaMes=0;
	 						Integer htaAnio=0;
	 							 						
	 						Calendar dde=Calendar.getInstance();
	 						dde.setTime(modalidad.getVigencia_desde());
	 						ddeDia=dde.get(Calendar.DAY_OF_MONTH);
	 						ddeMes=dde.get(Calendar.MONTH);
	 						ddeAnio=dde.get(Calendar.YEAR);
	 						
	 						if(modalidad.getVigencia_hasta()!=null){
	 						    Calendar hta=Calendar.getInstance();
		 						hta.setTime(modalidad.getVigencia_hasta());
		 						htaDia=hta.get(Calendar.DAY_OF_MONTH);
		 						htaMes=hta.get(Calendar.MONTH);
		 						htaAnio=hta.get(Calendar.YEAR);	
	 						}
	 						
	 						StringBuilder sb1= new StringBuilder();
		 					sb1.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb1.append(themeDisplay.getPathThemeImages());
		 					sb1.append("/common/edit.png\" onClick=\"javascript:editaTope('");
		 					sb1.append(modalidad.getId());
		 					sb1.append("','");
		 					sb1.append(modalidad.getPlan().getId());
		 					sb1.append("','");
		 					sb1.append(modalidad.getTopeReintegro());
		 					sb1.append("','");
		 					sb1.append(ddeDia);
		 					sb1.append("','");
		 					sb1.append(ddeMes);
		 					sb1.append("','");
		 					sb1.append(ddeAnio);
		 					sb1.append("','");
		 					sb1.append(htaDia);
		 					sb1.append("','");
		 					sb1.append(htaMes);
		 					sb1.append("','");
		 					sb1.append(htaAnio);
		 					sb1.append("');\" />");
		 					row.addText(sb1.toString());
		 					
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraTope('");
		 					sb.append(modalidad.getId());
//		 					sb.append(modalidad.getPlan().getId());
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
	 			 		} else {
	 			 			row.addText("");
	 			 			row.addText("");
	 			 		}
	 					resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
		
		