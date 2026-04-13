<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="ar.com.ospim.global.beans.ConceptoSueldos" %>

<%String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}

if (request.getAttribute("esEditable") != null){
	esEditableStr = (String)request.getAttribute("esEditable");
}
boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
boolean esEdicion = Boolean.parseBoolean(esEditableStr); 

String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "comprobantes";
}
NumberFormat format2D = new DecimalFormat("###,###,###,###,##0.00");

//"#0.00"

List<String> errores = (List<String>)request.getAttribute("errores");
	if (errores != null && !errores.isEmpty()){
		%>
		<table  style="color:red" >
		<%
		for (String error : errores){
			%>
			<tr><td>
			<%=error%>
			</td></tr>
			<%
		}
		%>
		</table>
		<%
}

%>
		
<%if (esEdicion && !soloVer){ %>
<div align="left">
	<input type="button" value="<liferay-ui:message key="borrar-todos" />" onClick="borrarTodos();" />
</div>
<%} %>

<%-- <%if(portlet_name!=null && !portlet_name.equalsIgnoreCase("tesoreria")) {%> --%>
<liferay-ui:success key="insertCabOk" message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

<liferay-ui:error key="regimenError" message="<%=(String)request.getAttribute(\"msgError2\") %>"  />
<liferay-ui:error key="exencionError" message="<%=(String)request.getAttribute(\"msgError3\") %>"  />
<liferay-ui:error key="exencionUrlError" message="<%=(String)request.getAttribute(\"msgError4\") %>"  />
<%-- <%} %> --%>

<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
					List<ConceptoSueldos> comprobantes = (ArrayList<ConceptoSueldos>)request.getSession().getAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Status");
			 		headerNames.add("Código");
			 		headerNames.add("Descripción");
			 		headerNames.add("Cta.Contable");
			 		headerNames.add("Cta.Contable Desc");
			 		headerNames.add("D/H");
			 		headerNames.add("Remunerativo");
			 		headerNames.add("No Remunerativo");
			 		headerNames.add("Retenciones");
			 		headerNames.add("Contribuciones");
					headerNames.add("Comandos");
					headerNames.add("");
					
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					if(null!=comprobantes){					
					 	int total = comprobantes.size();
					 	searchContainer.setTotal(total);
					 	pageContext.setAttribute("total", total);	
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < comprobantes.size(); i++) {
					 		ConceptoSueldos comp = comprobantes.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 							 					
		 					Boolean marcar=false;
		 					
		 				
		 					
                            String sb000 = "";
                            if(comp.isConProblema()){
                            	sb000 = "<span id=lb_'"+ comp.getId()+"' style='background-color:#F1948A; font-weight:bold; font-size:10px'>" + (comp.getError()!=null?" " + comp.getError() +" ":"_")   +"</span>";
                            }else{
                            	if("OK".equals(comp.getError())){
                            	   sb000 = "<span id=lb_'"+ comp.getId()+"' style='background-color:#ABEBC6; font-weight:bold; font-size:10px'>" +  "OK"   +"</span>";
                            	}   
                            }
                            
                            row.addText(sb000);
		 					
                            row.addText( comp.getCodigo().toString());
		 					row.addText( comp.getDescripcion());
		 					row.addText( comp.getCuentaContable()!=null? comp.getCuentaContable().getNumero():"");
		 					row.addText( comp.getCuentaContable()!=null? comp.getCuentaContable().getCuenta():"");
		 					row.addText( comp.getDebeHaber()!=null?comp.getDebeHaber():"");
	 						row.addText( format2D.format(comp.getRemunerativo()));
	 						row.addText( format2D.format(comp.getNoRemunerativo()));
	 						row.addText( format2D.format(comp.getRetencion()));
	 						row.addText( format2D.format(comp.getContribucion()));
		 					
		 					
		 					StringBuilder sb= new StringBuilder();		
		 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/edit.png\" onClick=\"javascript:editarEquivalencias(");
			 				sb.append(comp.getId());
			 				sb.append(",");
			 				sb.append(comp.getCuentaContable()!=null?comp.getCuentaContable().getId():"0");
			 				sb.append(",");
			 				sb.append(comp.getCodigo());
			 				sb.append(",'");
			 				sb.append(comp.getDescripcion());
			 				sb.append("',");
			 				sb.append(comp.getSectorLiquidado());
			 				sb.append(",'");
			 				sb.append(comp.getEntidad());
			 				sb.append("','");
			 				sb.append(comp.getDebeHaber());
			 				sb.append("');\"");
			 				sb.append(" title=\"Crear/Editar Equivalencia\"");
		 				    sb.append("/>");
			 				
			 				row.addText(sb.toString());
			 				
			 				
			 				StringBuilder sb1= new StringBuilder();
		 					sb1.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb1.append(themeDisplay.getPathThemeImages());
		 					sb1.append("/common/delete.png\" onClick=\"javascript:eliminarEquivalencias(");
		 					sb1.append(comp.getId());
		 					sb1.append(");\"");
		 					sb1.append(" title=\"Eliminar Equivalencia\"");
		 				    sb1.append("/>");
		 					row.addText(sb1.toString());
		 					
	 						resultRows.add(row);
						}
					 }
			%>

    <%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		
    		
	
<script type="text/javascript">
   
	
</script>	
