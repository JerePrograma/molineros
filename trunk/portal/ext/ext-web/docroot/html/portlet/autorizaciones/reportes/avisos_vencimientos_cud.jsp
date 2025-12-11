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

<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="Proceso finalizado" />		
	</c:when>
</c:choose>	

<form action="" method="post" name="<portlet:namespace />fmSSS" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
		<legend>Avisos Vencimientos CUD</legend>
		<table>
			<tr>
			   <td>
				<label>Fecha Origen:</label>
				</td>
				<td>
				    <liferay-ui:input-date
									dayParam="fechaDia"
									dayValue="<%= fechaInicio.get(Calendar.DAY_OF_MONTH) %>" 
									dayNullable="<%= false %>"
									monthParam="fechaMes"
									monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"	
									monthNullable="<%= false %>"			
									yearParam="fechaAnio"
									yearValue="<%=fechaInicio.get(Calendar.YEAR)%>"
									yearNullable="<%= false %>"
									yearRangeStart="<%= fechaInicio.get(Calendar.YEAR) - 10 %>"
									yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
									firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />		
				
				</td>

				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
				
				
				<td><label>Días al Vencimiento:</label></td>
				<td> 
		           <input id="<portlet:namespace />diasVto"
			        name="<portlet:namespace />diasVto" size="5" maxlength="10"
			        type="text"
			        value="30" />
				</td>
				
				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td valign="bottom" >				
				<input type="button" value="Generar Avisos" onClick="<portlet:namespace />generarAvisos();"/>&nbsp;
				</td>
				
				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			</tr>
			
			<tr>
			   <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
			</tr>   
		</table>
		
		<table>
		  <tr>	
			<td><p style='color:blue;font-size: 16px;'>Genera mails a los afiliados cuya fecha de vencimiento del CUD coincida con la fecha que resulta de sumar a la fecha de origen la cantidad de Días al vencimiento</p> </td>
		  </tr>		
		</table>	
		
	</fieldset>
	
</form>		

<script type="text/javascript">

	
	
	function <portlet:namespace />generarAvisos(){
		var fechaDia=jQuery('#<portlet:namespace />fechaDia').val(); 
		var fechaMes=jQuery('#<portlet:namespace />fechaMes').val();
		var fechaAnio=jQuery('#<portlet:namespace />fechaAnio').val();
		var qDias=jQuery('#<portlet:namespace />diasVto').val();
		
		
		var url = '<portlet:actionURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/autorizaciones/editar_autorizacionprestacional_entry"  /></portlet:actionURL>';
		
		url = url +  '&fechaDia='+fechaDia+'&fechaMes='+fechaMes+'&fechaAnio='+fechaAnio+'&qDias='+qDias+
		'&accionOriginal='+'avisovencimientocud';

		submitForm(document.<portlet:namespace />fmSSS, url);
		
	}
		
</script>

