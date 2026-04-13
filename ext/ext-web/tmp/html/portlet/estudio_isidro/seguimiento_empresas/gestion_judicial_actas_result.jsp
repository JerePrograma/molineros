<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

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



<%
List<String> erroresActas = (List<String>)request.getAttribute("erroresActas");
if (erroresActas != null && !erroresActas.isEmpty()){
	%>
	<table  style="color:red" >
	<%
	for (String error : erroresActas){
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

<portlet:defineObjects/>
			<%
					
					StringBuilder obs = new StringBuilder();
					DemandaJudicial demanda=(DemandaJudicial)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);
					List<Acta> actas =demanda!=null && demanda.getActas()!=null?demanda.getActas():null;
					
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Nro Acta");
			 		headerNames.add("Comandos");
					
					/*
					if(esEdicion) { 
						headerNames.add("Borrar");
					}
					*/
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-entries-were-found"));
				
					if(null!=actas){					
					 	int total = actas.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < actas.size(); i++) {
					 		Acta comp = actas.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 					row.addText( comp.getNumero());
		 					
		 					
		 					StringBuilder sb= new StringBuilder();
		 					
		 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='delete'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/delete.png\" onClick=\"javascript:deleteActa('");
			 				sb.append(comp.getId());
			 				sb.append("');\" />");
			 				
		 					row.addText(sb.toString());			
	 						
	 						resultRows.add(row);
						}
					 }
			%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
<script type="text/javascript">


    function deleteActa(id){	
	   //jQuery('#<portlet:namespace />buscando').show();
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	   url += '&cmd=deleteActa&acta='+id;
	   url += '&rnd=' + Math.floor(Math.random()*100);
	   jQuery('#<portlet:namespace />actasDiv').load(url, function() {
			//jQuery('#<portlet:namespace />buscando').hide();
		  }
	   );
    }
   
</script>	
