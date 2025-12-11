<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento"%>

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
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat sdfYear = new SimpleDateFormat("yyyyMMdd");


%>

<%
List<String> erroresContabilidad = (List<String>)request.getAttribute("erroresContibilidad");
if (erroresContabilidad != null && !erroresContabilidad.isEmpty()){
	%>
	<table  style="color:red" >
	<%
	for (String error : erroresContabilidad){
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
					List<Asiento> asientos =demanda!=null && demanda.getAsientos()!=null?demanda.getAsientos():null;
					
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Grupo");
			 		headerNames.add("Fecha");
			 		headerNames.add("Descripción");
			 		headerNames.add("Debe");
			 		headerNames.add("Haber");
			 		headerNames.add("Editar");
			 		headerNames.add("Eliminar");
					
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-entries-were-found"));
				
					if(null!=asientos){					
					 	int total = asientos.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < asientos.size(); i++) {
					 		Asiento comp = asientos.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 					row.addText( String.valueOf( comp.getId()) );
		 					row.addText(comp.getFecha()!=null?sdf.format(comp.getFecha()):"" );
		 					row.addText( String.format("%1$-100s",comp.getDescripcion()) );
		 					row.addText(comp.getTotalDebeAsString());
		 					row.addText(comp.getTotalHaberAsString());
		 					
		 					StringBuilder sbE= new StringBuilder();
		 					sbE.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='edit'/>\" src=\"");
			 				sbE.append(themeDisplay.getPathThemeImages());
			 				sbE.append("/common/edit.png\" onClick=\"javascript:editAsiento('");
			 				sbE.append(comp.getId());
			 				sbE.append("');\" />");
			 				row.addText(sbE.toString());
		 					
		 					
		 					StringBuilder sb= new StringBuilder();
		 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='delete'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/delete.png\" onClick=\"javascript:deleteAsiento('");
/*			 				
			 				sb.append(demanda.getId());
			 				sb.append("','");
*/			 				
			 				sb.append(comp.getId());
			 				sb.append("');\" />");
			 				row.addText(sb.toString());			
	 						
	 						resultRows.add(row);
						}
					 }
			%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
<script type="text/javascript">

function editAsiento(asientoId){
//	var popupE;
	if(popupE==null)
	    popupE = Liferay.Popup({title:"Registro Contable",modal:true,width:1200,
	    	onClose: function() { popupE = null;
	    	           var url1 ='<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_contabilidad_result';
	    	           jQuery("#<portlet:namespace />asientosDiv").load(url1); 
	    	}});

    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
    url += '&cmd=editarAsiento&asientoid='+asientoId;
    url += '&rnd=' + Math.floor(Math.random()*100);
    jQuery(popupE).load(url); 
}
   
function deleteAsiento(asientoId){	
	   var convenio=jQuery('#<portlet:namespace />convenio').val();
	   //jQuery('#<portlet:namespace />buscando').show();
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	   url += '&cmd=deleteAsiento&asientoid='+asientoId;
	   url += '&rnd=' + Math.floor(Math.random()*100);
	   jQuery('#<portlet:namespace />asientosDiv').load(url, function() {
			//jQuery('#<portlet:namespace />buscando').hide();
		  }
	   );
 }
	
	
</script>	
