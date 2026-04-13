<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Cheque" %>

<portlet:defineObjects/>
			<% 
			BigDecimal capital = new BigDecimal("0");
			
			String ids  ="";
			Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

			
			boolean esEdicion = false;

			if (request.getAttribute(WebKeysTesoreria.RECIBOS_ACTION_EDICION) != null || recibo == null) {
				esEdicion = true; 
			}
			String esEd = ParamUtil.getString(request, "esEdicion");
			if (esEd == null || esEd.equals("")){
				esEd = (String) request.getAttribute("esEdicion");
			}
			if (esEd != null && !esEd.equals("")){
				esEdicion= Boolean.parseBoolean(esEd);
			}
			if (recibo != null && recibo.getId() != 0){
				esEdicion = false;
			}
			
			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
			List<ReciboIngreso> ingresos= null;
			if (recibo != null ){
				ingresos = recibo.getIngresos();
			}

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("numero");
	 		headerNamesTercerizadora.add("importe");
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-anticipos-were-found"));
		
			
			if(null!=ingresos){
				int total=ingresos.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < ingresos.size(); i++) {
 			 			Ingreso ingreso = ingresos.get(i).getIngreso();
 			 			if (!ingreso.getTipo().equals("Anticipo")){
 			 				total--;
 			 				continue;
 			 			}
	 					ResultRow row = new ResultRow(ingreso,ingreso.getNumeroStr(), i);
	 					row.addText(ingreso.getNumeroStr());
	 					row.addText(ingreso.getImporte().negate().toString());
	 					resultRowsInspector.add(row);
	 					capital = capital.add(ingreso.getImporte());
	 					if (showABMButtons && esEdicion && ingreso.isNew()){
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:alert('");
		 					sb.append("asd");
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
	 			 		} else if (showABMButtons && esEdicion && !ingreso.isNew()){
	 			 			row.addText("");
	 			 		}
 			 		}
 				searchContainer.setTotal(total);
	 		}
 		%>
 		
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />

	<script type="text/javascript" >
		document.getElementById("capitalAnticiposTmp").value = "<%=capital.negate().toString()%>";
		
	</script>
