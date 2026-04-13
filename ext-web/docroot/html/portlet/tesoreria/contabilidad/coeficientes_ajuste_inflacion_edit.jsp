<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.CoeficienteAjusteInflacion"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	CoeficienteAjusteInflacion coef=(CoeficienteAjusteInflacion)request.getSession().getAttribute(WebKeysTesoreria.COEFICIENTE_EN_EDICION);
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	
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
	Calendar current = CalendarFactoryUtil.getCalendar();
	if(coef!=null && coef.getPeriodo()!=null){
		current.set((int)coef.getPeriodo()/100,
				((int)coef.getPeriodo()%100)-1, 1);
	}
%>
<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
		
<form action="" method="post" name="<portlet:namespace />fmCoef">
	
<fieldset class="block-labels"><legend>Coeficientes Ajuste Inflación</legend>


<table class="lfr-table">		 
	<tr>
		<td><label><liferay-ui:message key="periodo" />:</label></td>
		 <td>
		 
		   <liferay-ui:input-date dayParam="periodoDia"
			       dayNullable="<%= true %>" dayValue=""
			       monthAndYearParam="periodoMesAnio"
			       monthValue="<%= current.get(Calendar.MONTH)%>"
			       monthAndYearNullable="<%= true %>"
			       yearValue="<%= current.get(Calendar.YEAR) %>"
			       yearRangeStart="<%= current.get(Calendar.YEAR) - 3 %>"
			       yearRangeEnd="<%= current.get(Calendar.YEAR) + 1 %>"
			       firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
			       disabled="<%= false %>" />
			
		 </td>  
		 <td>Coeficiente:</td>
		 <td>
		 <input type="text" name="<portlet:namespace />coeficiente"  id="<portlet:namespace />coeficiente"  value="<%=coef.getCoeficiente()!=null?coef.getCoeficiente():1%>" size="50"/>
		 </td>    
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>

<table class="lfr-table">	
	<tr>
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>

<input id="<portlet:namespace />guardar1"
				value="<liferay-ui:message key="save"/>"
				title="<liferay-ui:message key="save" />"
				onClick="javascript: <portlet:namespace />guardar();"
				type="button" /> 
				

</form>

<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
		<%if(portlet_name.equals("farmacia")){%>
			<portlet:param name="struts_action" value="/farmacia/coeficientes_ajuste_inflacion" />
		<%}else if(portlet_name.equals("uoma")){%>
			<portlet:param name="struts_action" value="/uoma/coeficientes_ajuste_inflacion" />			
		<%}else{%>
			<portlet:param name="struts_action" value="/tesoreria/coeficientes_ajuste_inflacion" />
		<%}%>	
		<portlet:param name="cmd" value="volver" />
</portlet:renderURL>
<p><a href="<%= volver %>">Volver

<script type="text/javascript">
<portlet:namespace />hideDayFieldOfPeriodFields ();

function <portlet:namespace />hideDayFieldOfPeriodFields () {
	jQuery("#<portlet:namespace />periodoDia").hide();
}

function <portlet:namespace />guardar(){
	var popup;
   var periodo=jQuery('#<portlet:namespace />periodoMesAnio').val();
   var coeficiente=jQuery('#<portlet:namespace />coeficiente').val();
   var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/coeficientes_ajuste_inflacion';
	   url += '&cmd=save';
	   url += '&coeficiente=' + escape(coeficiente);
	   url += '&periodo=' + escape(periodo);
	   url += '&rnd=' + Math.floor(Math.random()*100);
	   
	   
  document.<portlet:namespace />fmCoef.method = 'post';
  submitForm(document.<portlet:namespace />fmCoef, url);
   
  return false;		
}


</script>

