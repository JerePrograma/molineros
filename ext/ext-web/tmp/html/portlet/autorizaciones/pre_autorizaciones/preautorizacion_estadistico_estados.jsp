<%@ include file="/html/portlet/autorizaciones/init.jsp" %>

<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page import="com.liferay.portal.kernel.util.OrderByComparator"%>
<%@ page import="ar.com.enterpriseadmin.search.UserSearch" %>
<%@ page import="ar.com.enterpriseadmin.search.UserSearchTerms" %>
<%@ page import="ar.com.enterpriseadmin.search.UserDisplayTerms" %>
<%@ page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@ page import="ar.com.ospim.global.beans.Seccional" %>
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
	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();
	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
	fechaInicio.add(Calendar.MONTH, -1);
	
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="estad-preautoriz" />:</legend>
		
		<table>
			<tr>
			   <td>
				<label><liferay-ui:message key="fecha-desde" />:</label>
				</td>
				<td>
				    <liferay-ui:input-date
									dayParam="fechaDia"
									dayValue="<%=current.get(Calendar.DAY_OF_MONTH) %>" 
									dayNullable="<%= false %>"
									monthParam="fechaMes"
									monthValue="<%= current.get(Calendar.MONTH)-1 %>"	
									monthNullable="<%= false %>"			
									yearParam="fechaAnio"
									yearValue="<%=current.get(Calendar.YEAR)%>"
									yearNullable="<%= false %>"
									yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
									yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
									firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />		
				
				</td>
			
				<td>
				<label><liferay-ui:message key="fecha-hasta" />:</label>
				</td>
				<td>
				    <liferay-ui:input-date
									dayParam="fechaDiaHta"
									dayValue="<%=current.get(Calendar.DAY_OF_MONTH) %>" 
									dayNullable="<%= false %>"
									monthParam="fechaMesHta"
									monthValue="<%= current.get(Calendar.MONTH) %>"	
									monthNullable="<%= false %>"			
									yearParam="fechaAnioHta"
									yearValue="<%=current.get(Calendar.YEAR)%>"
									yearNullable="<%= false %>"
									yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
									yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR)%>"
									firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
									disabled="<%= false %>" />		
				
				</td>
				<td>&nbsp;</td>
				<td valign="bottom" >				
				<input type="button" value="Reporte" onClick="<portlet:namespace />reportePreautorizacion();"/>&nbsp;
				</td>
			</tr>
		</table>	
		
	</fieldset>
	
</form>		

<script type="text/javascript">
	function <portlet:namespace />reportePreautorizacion(){
		var fechaDia=jQuery('#<portlet:namespace />fechaDia').val();
		var fechaMes=jQuery('#<portlet:namespace />fechaMes').val();
		var fechaAnio=jQuery('#<portlet:namespace />fechaAnio').val();
		
		var fechaDiaHta=jQuery('#<portlet:namespace />fechaDiaHta').val();
		var fechaMesHta=jQuery('#<portlet:namespace />fechaMesHta').val();
		var fechaAnioHta=jQuery('#<portlet:namespace />fechaAnioHta').val();
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_PREAUTORIZACION_ESTADISTICO_ESTADOS&fechaDia='+fechaDia+
				              '&fechaMes='+fechaMes+'&fechaAnio='+fechaAnio+
				              '&fechaDiaHta='+fechaDiaHta+'&fechaMesHta='+fechaMesHta+'&fechaAnioHta='+fechaAnioHta;
	}
		
</script>

