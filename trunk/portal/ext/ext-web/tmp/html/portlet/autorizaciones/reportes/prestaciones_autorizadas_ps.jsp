<%@ include file="/html/portlet/autorizaciones/init.jsp" %>


<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	//verificar los calendars
	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();	
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="estad-prest-aut"/></legend>
		<table>
			<tr>
			   <td>
				<label><liferay-ui:message key="periodo-desde" />:</label>
				</td>
				<td>
				    <liferay-ui:input-date
									dayParam="fechaDia"
									dayValue="1" 
									dayNullable="<%= false %>"
									monthParam="fechaMes"
									monthValue="<%= fechaInicio.get(Calendar.MONTH)-1 %>"	
									monthNullable="<%= false %>"			
									yearParam="fechaAnio"
									yearValue="<%=fechaInicio.get(Calendar.YEAR)%>"
									yearNullable="<%= false %>"
									yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 10 %>"
									yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
									firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />		
				
				</td>

				<td>&nbsp;</td>
				<td valign="bottom" >				
				<input type="button" value="Reporte" onClick="<portlet:namespace />reportePrestacionesAutorizadas();"/>&nbsp;
				</td>
			</tr>
		</table>	
		
	</fieldset>
	
</form>		

<script type="text/javascript">

	jQuery('#<portlet:namespace />fechaDia').hide();
	
	function <portlet:namespace />reportePrestacionesAutorizadas(){
		/* var fechaDia=jQuery('#<portlet:namespace />fechaDia').val(); */
		var fechaMes=jQuery('#<portlet:namespace />fechaMes').val();
		var fechaAnio=jQuery('#<portlet:namespace />fechaAnio').val();
		
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_PRESTACIONES_AUTORIZADAS_PS&fechaDia=01'+
				              '&fechaMes='+fechaMes+'&fechaAnio='+fechaAnio;
	}
		
</script>

