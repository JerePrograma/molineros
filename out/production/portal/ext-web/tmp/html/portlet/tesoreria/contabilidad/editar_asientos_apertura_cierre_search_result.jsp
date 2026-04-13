<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento" %>
<%@page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle"%>

<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>

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
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
} 


NumberFormat format2D = new DecimalFormat("###,###,###,###,##0.00");
Asiento asiento = (Asiento)request.getSession().getAttribute(WebKeysTesoreria.ASIENTO_ESPECIAL_EN_SESSION);
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
	
	if(asiento.getDetalle()!=null){
	   Collections.sort(asiento.getDetalle(), new Comparator<Detalle>() {

		public int compare(Detalle pc1, Detalle pc2) {
			    String tipo1=pc1.getDebe().compareTo(BigDecimal.ZERO) >0?"D":"H" + pc1.getCuenta().getNumero() ;
			    String tipo2=pc2.getDebe().compareTo(BigDecimal.ZERO) >0?"D":"H" +pc2.getCuenta().getNumero();
				return (tipo1).compareTo( (tipo2));
		}

	   });
	}

%>
		

<liferay-ui:success key="insertCabOk" message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

  <fieldset class="block-labels" >
                    <legend>Totales</legend>
                      <table class="lfr-table">
		<tr>
			<th style="font-weight: bold; font-size:14px"><label>Debe:</label></th>
			<th style="font-weight: bold; font-size:14px">
				<label id='<portlet:namespace />totalD'  name='<portlet:namespace />totalD'>	<%=asiento.getTotalDebeAsString() %></label>	
			</th>
			<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
			<th style="font-weight: bold; font-size:14px"><label>Haber:</label></th>
			<th style="font-weight: bold; font-size:14px">
				<label id='<portlet:namespace />totalH' name='<portlet:namespace />totalH'>	<%=asiento.getTotalHaberAsString() %></label>
			</th>
			<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
			<th style="font-weight: bold; font-size:14px"><label>Diferencia:</label></th>
			<th style="font-weight: bold; font-size:14px;color:<%if(asiento.getTotalDebe().subtract(asiento.getTotalHaber()).doubleValue() !=0D){ %>
			                                                     red
			                                                   <%}else{%>
			                                                     green
			                                                   <%}%>">
				<%=format2D.format(asiento.getTotalDebe().subtract(asiento.getTotalHaber())) %>	
			</th>
			
			
		</tr>
  </table>
</fieldset>	


<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Cta.Contable");
			 		headerNames.add("Cta.Contable Desc");
			 		headerNames.add("Debe");
			 		headerNames.add("Haber");
			 		headerNames.add("Comandos");
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-comprobantes-were-found"));
				
					if(null!=asiento && asiento.getDetalle()!=null){					
					 	int total = asiento.getDetalle().size();
					 	searchContainer.setTotal(total);
					 	pageContext.setAttribute("total", total);	
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < asiento.getDetalle().size(); i++) {
					 		Detalle comp =  asiento.getDetalle().get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 							 					
		 					Boolean marcar=false;
		 					
		 					
		 				    row.addText( comp.getCuenta().getNumero());
		 				    
		 					 String sb000 = "";
	                         if("ERR".equals(comp.getCuenta().getTipo())){
	                           	sb000 = "<span id=lb_'"+ comp.getId()+"' style='background-color:#F1948A; font-weight:bold; font-size:10px'>" + (comp.getCuenta().getCuenta())   +"</span>";
	                         }else{
	                           	sb000 = "<span id=lb_'"+ comp.getId()+"' style='background-color:#ABEBC6; font-weight:bold; font-size:10px'>" + (comp.getCuenta().getCuenta())   +"</span>";
	                         }
	                        row.addText(sb000);
		 				    
//		 				    row.addText(comp.getCuenta().getCuenta());
		 				    
		 				    row.addText(format2D.format(comp.getDebe()));
		 				    row.addText(format2D.format(comp.getHaber()));
		 				    
		 					StringBuilder sb1= new StringBuilder();
		 					sb1.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb1.append(themeDisplay.getPathThemeImages());
		 					sb1.append("/common/delete.png\" onClick=\"javascript:eliminarRenglon(");
		 					sb1.append(comp.getId());
		 					sb1.append(");\"");
		 					sb1.append(" title=\"Eliminar Renglón\"");
		 				    sb1.append("/>");
		 					row.addText(sb1.toString());
		 					
	 						resultRows.add(row);
						}
					 }
			%>

    <%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />		
    		
	
<script type="text/javascript">
jQuery("#totalDebe").val('<%=asiento.getTotalDebeAsString() %>') ;
jQuery("#totalHaber").val('<%=asiento.getTotalHaberAsString() %>') ;	
</script>	
