<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ page import="ar.com.ospim.liquidaciones.FechaBajaMenorQueAltaException" %>
<%@ page import="ar.com.ospim.tesoreria.ImposibleBorrarConvenioException"%>

<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
<portlet:defineObjects />

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%
	
	String portlet_name=null;
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}	 	

	Calendar fechaPago = CalendarFactoryUtil.getCalendar();
	fechaPago.setTime(new Date());
	
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	
		
	
%>
<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	</c:when>
</c:choose>	
	<liferay-ui:error exception="<%= FechaBajaMenorQueAltaException.class %>" message="exception-fecha-baja-menor-que-alta" />	
	<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="baja-menor-fecha-contable" />
	<liferay-ui:error exception="<%= ImposibleBorrarConvenioException.class %>" message="recibo-derivado-exception" />


<table>

	<tr>
		<td>
			<input type="hidden" id="<portlet:namespace />convenio_id" value="<%= ((Integer)request.getAttribute("convenio_id")).toString()%>"/>
			<liferay-ui:message key="baja-convenio" />&nbsp;
		</td>
		<td>
			<liferay-ui:input-date
			dayParam="fechaBajaDia"
			dayValue="<%=fechaPago.get(Calendar.DATE)%>" 
			monthParam="fechaBajaMes"
			monthValue="<%=  fechaPago.get(Calendar.MONTH)%>"				
			yearParam="fechaBajaAnio"
			yearValue="<%= fechaPago.get(Calendar.YEAR)%>"
			yearRangeStart="<%= fechaPago.get(Calendar.YEAR) -20 %>"	
			yearRangeEnd="<%= fechaPago.get(Calendar.YEAR) + 20%>"
			firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
			disabled="false" />
		</td>
		<td>
			
			<input type="button" value="<liferay-ui:message key="anular" />" onClick="<portlet:namespace />grabarAnulacionConvenio();" />/
			<input type="button" value="<liferay-ui:message key="reactivar-op" />" onClick="<portlet:namespace />grabarReactivarConvenio();" />
									
			<span align="center" id="<portlet:namespace />guardando_anulacion">
				<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
			</span>	
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>	
</table>


<script type="text/javascript">
 function <portlet:namespace />grabarAnulacionConvenio(){
	 jQuery('#<portlet:namespace />guardando_anulacion').show();
	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaBajaDia");
	var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaBajaMes");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaBajaAnio");	
	
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/borrar_convenios_no_os_entry'
			+'&fechaBajaDia='+fechaDesdeDia.value
			+'&fechaBajaMes='+fechaDesdeMes.value
			+'&fechaBajaAnio='+fechaDesdeAnio.value;	 
	url += '&id=' + document.getElementById("<portlet:namespace />convenio_id").value;	
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery(popup).load(url);
		
 }
 function <portlet:namespace />grabarReactivarConvenio(){
	 jQuery('#<portlet:namespace />guardando_anulacion').show();	
	
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/borrar_convenios_no_os_entry'
			+'&reactivar=true';	 
	url += '&id=' + document.getElementById("<portlet:namespace />convenio_id").value;	
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery(popup).load(url);
		
 }
 
 jQuery('#<portlet:namespace />guardando_anulacion').hide();
</script>