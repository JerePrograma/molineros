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
List<String> erroresCheques = (List<String>)request.getAttribute("erroresCheques");
if (erroresCheques != null && !erroresCheques.isEmpty()){
	%>
	<table  style="color:red" >
	<%
	for (String error : erroresCheques){
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
					List<Cheque> actas =demanda!=null && demanda.getCheques()!=null?demanda.getCheques():null;
					
					
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Nro Cheque");
			 		headerNames.add("Cuit");
			 		headerNames.add("Banco");
			 		headerNames.add("Cta.Bancaria");
			 		
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
					 		Cheque comp = actas.get(i);
		 					ResultRow row = new ResultRow(comp, comp.hashCode(), i);
		 					
		 					row.addText( comp.getNumero().toString() );
		 					row.addText( comp.getCuit());
		 					row.addText( comp.getCuentaBancaria().getBanco().getDescripcion_banco());
		 					row.addText( comp.getCuentaBancaria().getDescripcion());
		 					
		 					
		 					StringBuilder sb= new StringBuilder();
		 					
		 					sb.append("&nbsp;&nbsp;<img alt=\"<liferay-ui:message key='delete'/>\" src=\"");
			 				sb.append(themeDisplay.getPathThemeImages());
			 				sb.append("/common/delete.png\" onClick=\"javascript:deleteCheque('");
			 				sb.append(comp.getNumero());
			 				sb.append("','");
			 				sb.append(comp.getCuit());
			 				sb.append("','");
			 				sb.append(comp.getBanco().getId_banco());
			 				sb.append("','");
			 				sb.append(comp.getCuentaBancaria().getId_cuenta_bcria());
			 				sb.append("');\" />");
			 				
		 					row.addText(sb.toString());			
	 						
	 						resultRows.add(row);
						}
					 }
			%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
<script type="text/javascript">
   
function deleteCheque(id,cuit,banco,idCta){	
		//jQuery('#<portlet:namespace />buscando').show();
	   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/demandas_editar';
	   url += '&cmd=deleteCheque&nro='+id+"&cuit="+cuit+"&banco="+banco+"&ctaBcria="+idCta;
	   url += '&rnd=' + Math.floor(Math.random()*100);
	   jQuery('#<portlet:namespace />chequesDiv').load(url, function() {
			//jQuery('#<portlet:namespace />buscando').hide();
		  }
	   );
 }	
	
	
</script>	
