<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle" %>

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
	portlet_name = "estudio_isidro";
}
NumberFormat format2D = new DecimalFormat("#0.00");

%>



<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
					DemandaJudicial demanda=(DemandaJudicial)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
					Asiento asiento=(Asiento)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_EN_EDICION);
					List<Detalle> detalles =asiento!=null && asiento.getDetalle()!=null?asiento.getDetalle():null;
					
					Detalle totalAsiento =(Detalle)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_ASIENTO_TOTALES);
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Cuenta");
			 		headerNames.add("Pase");
			 		headerNames.add("Debe");
			 		headerNames.add("Haber");
			 		
			 		headerNames.add("Comandos");
					
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-entries-were-found"));
				
					if(null!=detalles){					
					 	int total = detalles.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < detalles.size(); i++) {
					 		Detalle comp = detalles.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 					row.addText( comp.getCuenta().getNumero() );
		 					row.addText( String.valueOf(comp.getPase()));
		 					row.addText( format2D.format(comp.getDebe()));
		 					row.addText( format2D.format(comp.getHaber()));
		 					
		 					
		 					StringBuilder sb= new StringBuilder();
		 					
		 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='delete'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/delete.png\" onClick=\"javascript:deleteDetalle('");
			 				sb.append(comp.getPase());
			 				sb.append("');\" />");
			 				
		 					row.addText(sb.toString());			
	 						
	 						resultRows.add(row);
						}
					 }
			%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		


<table>
		          <tr><td>&nbsp;</td></tr>
		          <tr>
    		       <td><p style="font-size:110%;;vertical-align: text-bottom">Total DEBE:</p></td>
		           <td>
				     <input type="text" id="<portlet:namespace />total_debe" name="<portlet:namespace />total_debe"  size="20" 
				         value="<%= format2D.format(totalAsiento.getDebe())%>"  
				         maxlength="60"        readonly="readonly" 
				         style="font-size:150%;color:<%if(totalAsiento.getDebe().doubleValue()*100D
				        		       ==
				        		           totalAsiento.getHaber().doubleValue()*100D
				        		       ){%>
				                green
				                <%}else{%>
				                red
				                <%}%>
				         " />
			       </td>
			       <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
			       <td><p style="font-size:110%;;vertical-align: text-bottom">Total HABER:</p></td> 
		           <td>
				    <input type="text" id="<portlet:namespace />total_haber" name="<portlet:namespace />total_haber"  size="20" 
				      value="<%=format2D.format(totalAsiento.getHaber())%>"   maxlength="60"
				     readonly="readonly"
				     style="font-size:150%;color:<%if(totalAsiento.getDebe().doubleValue()*100D
				        		       ==
				        		           totalAsiento.getHaber().doubleValue()*100D
				        		       ){%>
				                green
				                <%}else{%>
				                red
				                <%}%>
				         "
				     
				      />
			       </td>
		         </tr>	
       </table>
	
<script type="text/javascript">
           

function deleteDetalle(id){	
		//jQuery('#<portlet:namespace />buscando').show();
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	   url += '&cmd=deleteAsientoDetalle&id='+id;
	   url += '&rnd=' + Math.floor(Math.random()*100);
	   jQuery('#<portlet:namespace />detallesDiv').load(url, function() {
			//jQuery('#<portlet:namespace />buscando').hide();
		  }
	   );
 }	
	
	
</script>	
