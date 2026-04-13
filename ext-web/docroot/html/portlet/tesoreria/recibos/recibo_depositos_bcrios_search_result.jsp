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
			boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)|| PermissionUtil.userContainsRole(user,"ABM_Farmacia") || PermissionUtil.userContainsRole(user,"Entidad_Uoma");				
			List<Recibo.DepositoBancario> depos= null;
			if (recibo != null ){
				depos =recibo.getDepositos();
			}

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("importe");
	 		headerNamesTercerizadora.add("fecha-pago");
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-depos-bcrios-were-found"));
		
			
			if(null!=depos){
				int total=depos.size();	 				
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < depos.size(); i++) {
 					ResultRow row = new ResultRow(depos.get(i),depos.get(i).getImporte().toString(), i);		
 					row.addText(depos.get(i).getImporte().toString());
 					capital = capital.add(depos.get(i).getImporte());
 					row.addText(depos.get(i).getFechaPagoAsString());
 					resultRowsInspector.add(row);
 					if (showABMButtons && esEdicion){
 						StringBuilder sb= new StringBuilder();
	 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
	 					sb.append(themeDisplay.getPathThemeImages());
	 					sb.append("/common/delete.png\" onClick=\"javascript:borraDepositoBancario('");
	 					sb.append(depos.get(i).getImporte());
	 					sb.append("','");
	 					sb.append(depos.get(i).getFechaPagoAsString());
	 					sb.append("','");
	 					sb.append(depos.get(i).getId());
	 					sb.append("');\" />");
	 					row.addText(sb.toString());
 			 		}
 			 	}
 				searchContainer.setTotal(total);
			}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />

	<script type="text/javascript" >
		document.getElementById("capitalDeposito").value = "<%=capital.toString()%>";
	</script>
