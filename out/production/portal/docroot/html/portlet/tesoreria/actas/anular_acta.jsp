<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.tesoreria.ActaConReciboException" %>
<%@ page import="ar.com.ospim.tesoreria.ActaRelacionadaException" %>
<%@ page import="ar.com.ospim.tesoreria.ImposibleBorrarActaException" %>
<%@ page import="ar.com.ospim.tesoreria.actas.action.BorrarActaAction" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>


<liferay-ui:error exception="<%= ImposibleBorrarActaException.class %>" message="imposible-borrar-acta" />
<liferay-ui:error exception="<%= ActaConReciboException.class %>" message="acta-con-recibo" />
<liferay-ui:error exception="<%= ActaRelacionadaException.class %>" message="acta-relacionada" />
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="acta-baja-menor-fecha-contable" />

<%

	Acta acta = (Acta)request.getAttribute(BorrarActaAction.ACTA_A_ANULAR);
	Calendar fechaPago = CalendarFactoryUtil.getCalendar();
	fechaPago.setTime(new Date());
	
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	if (acta != null && acta.getBaja_fecha() != null) {
		fechaPago.setTime(acta.getBaja_fecha());
	}
%>
<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	</c:when>
</c:choose>
	
<table>
	<% if (acta != null && acta.getBaja_fecha() != null) {%>
	<tr>
		<td>
			Fecha de baja actual:&nbsp;<%= formatter.format(acta.getBaja_fecha()) %>
		</td>
	</tr>
	<%} %>
	<tr>
		<td>
			<input type="hidden" id="<portlet:namespace />id" value="<%= (String)request.getAttribute("id")%>"/>
			Nueva Fecha de Baja:
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
			<%if (acta == null || acta.getBaja_fecha() == null)  { %>
				<input type="button" value="<liferay-ui:message key="anular" />" onClick="<portlet:namespace />grabarAnulacionActa();" />
			<%} %>
			<%if (acta != null && acta.getBaja_fecha() != null) { %>
						<input type="button" value="<liferay-ui:message key="actualizar-anulacion" />" onClick="<portlet:namespace />grabarAnulacionActa();" />
			<%} %>
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
 function <portlet:namespace />grabarAnulacionActa(){
 	
	jQuery('#<portlet:namespace />guardando_anulacion').show();
	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaBajaDia");
	var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaBajaMes");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaBajaAnio");
	
	<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")){%>
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/borrar_actas_entry'
				+'&fechaBajaDia='+fechaDesdeDia.value
				+'&fechaBajaMes='+fechaDesdeMes.value
				+'&fechaBajaAnio='+fechaDesdeAnio.value
				+'&popupActa=true';
	<%}else{%>
		 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/borrar_actas_entry'
				+'&fechaBajaDia='+fechaDesdeDia.value
				+'&fechaBajaMes='+fechaDesdeMes.value
				+'&fechaBajaAnio='+fechaDesdeAnio.value;
	<%}%>
	 
	 
	url += '&id=' + document.getElementById("<portlet:namespace />id").value;
	url += '&accion=borrar';
	url += '&rnd=' + Math.floor(Math.random()*100);
	<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")){%>		
		Liferay.Popup.close(popup);
		jQuery("#allPage").load(url);
		//submitForm(document.<portlet:namespace />act, url);
	<%}else{%>
		jQuery(popup).load(url);
	<%}%>
		
 }
 
 jQuery('#<portlet:namespace />guardando_anulacion').hide();
</script>