<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.ImporteMayorException" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

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

String[] estadosStr = TraeListasServiceUtil.getSystemConfig("GESTION_JUDICIAL_TIPOS_ESTADOS").split(";");
Map<String,String> estadosJud=new HashMap<String,String>();
	for(int i=0;i<=estadosStr.length-1;i++){
		String codigo = estadosStr[i].split("=")[0];
		String descripcion = estadosStr[i].split("=")[1];
		estadosJud.put(codigo,descripcion);
}

%>

<%
List<String> erroresEstados = (List<String>)request.getAttribute("erroresEstados");
if (erroresEstados != null && !erroresEstados.isEmpty()){
	%>
	<table  style="color:red" >
	<%
	for (String error : erroresEstados){
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
					List<Estado> estados =demanda!=null && demanda.getEstados()!=null?demanda.getEstados():null;
					
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Estado");
			 		headerNames.add("Fecha");
			 		headerNames.add("Observaciones");
			 		headerNames.add("Eliminar");
					
					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,10000, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-entries-were-found"));
				
					if(null!=estados){					
					 	int total = estados.size();
					 	searchContainer.setTotal(total);
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < estados.size(); i++) {
					 		Estado comp = estados.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 					row.addText( estadosJud.get(comp.getId()));
//		 					row.addText( comp.getDescripcion());
		 					row.addText(sdf.format(comp.getFecha()));
		 					row.addText(comp.getObservacionesExternas()!=null?comp.getObservacionesExternas():"");
		 					
		 					StringBuilder sb= new StringBuilder();
		 					
		 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='delete'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/delete.png\" onClick=\"javascript:deleteEstado('");
			 				sb.append(comp.getIdSerial());
			 				sb.append("','");
			 				sb.append(sdfYear.format(comp.getFecha()));
			 				sb.append("');\" />");
			 				
		 					row.addText(sb.toString());			
	 						
	 						resultRows.add(row);
						}
					 }
			%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
<script type="text/javascript">
   
function deleteEstado(id,fechaStr){	
	   var convenio=jQuery('#<portlet:namespace />convenio').val();
	   //jQuery('#<portlet:namespace />buscando').show();
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	   url += '&cmd=deleteEstado&id='+id+"&fecha="+fechaStr;
	   url += '&rnd=' + Math.floor(Math.random()*100);
	   jQuery('#<portlet:namespace />estadosDiv').load(url, function() {
			//jQuery('#<portlet:namespace />buscando').hide();
		  }
	   );
 }
	
	
</script>	
