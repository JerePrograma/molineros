<%@include file="/html/portlet/hoteles/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/hoteles/view");
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "hoteles";
	}
	
	String ptoVtaAfip="00030";

	try{
		ptoVtaAfip=user.getExpandoBridge().getAttribute("punto_venta_afip").toString(); 		
	}catch(Exception e){
		//ptoVtaAfip="0000";
		ptoVtaAfip="00030";
	}
	
	Calendar current = CalendarFactoryUtil.getCalendar();
	
	//List<ProductoCategoria> categorias =  HotelesServiceUtil.getProductosCategorias(ptoVtaAfip);
	//session.setAttribute(WebKeysHoteles.CATEGORIAS_HOTEL,categorias);
	
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
    <input name="<portlet:namespace />id_hotel"  id="<portlet:namespace />id_hotel" type="hidden"	value="<%=ptoVtaAfip%>" /> 
	<fieldset class="block-labels">
		<legend>Estadística Desayunos</legend>
		
		<table class="lfr-table">
		  <tr>
		   <td><label>Desde:</label></td>
		   <td colspan="2">
				<liferay-ui:input-date
						dayParam="fechaDesdeDiaFiltro"
						dayValue="<%=current.get(Calendar.DATE) %>" 
						dayNullable="<%= false %>"
						monthParam="fechaDesdeMesFiltro"
						monthValue="<%= current.get(Calendar.MONTH) %>"	
						monthNullable="<%= false %>"			
						yearParam="fechaDesdeAnioFiltro"
						yearValue="<%= current.get(Calendar.YEAR) %>"
						yearNullable="<%= false %>"
						yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
						yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
						firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
			</td>
						
			<td><label>Hasta:</label></td>
			<td colspan="2">
				<liferay-ui:input-date
							dayParam="fechaHastaDiaFiltro"
							dayValue="<%=current.get(Calendar.DATE) %>" 
							dayNullable="<%= false %>"
							monthParam="fechaHastaMesFiltro"
							monthValue="<%= current.get(Calendar.MONTH) %>"	
							monthNullable="<%= false %>"			
							yearParam="fechaHastaAnioFiltro"
							yearValue="<%= current.get(Calendar.YEAR) %>"
							yearNullable="<%= false %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			</td>
		   
		   
		     
		  </tr>
		  <tr>
		    <td>&nbsp;</td>
		  </tr>
		</table>
		
		<input type="button" value="Imprimir" onClick="<portlet:namespace />imprimirTodos();"/>&nbsp;
		
	</fieldset>
</form>		

<script type="text/javascript">
		
	var popupMD;
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
	}
	
	function <portlet:namespace />imprimirTodos() {
		var anio=jQuery('#<portlet:namespace />anio_filtro').val();
		var reserva=jQuery('#<portlet:namespace />idReserva_filtro').val();
		var habitacion=jQuery('#<portlet:namespace />idHabitacion_filtro').val();
		var idHotel=jQuery('#<portlet:namespace />id_hotel').val();
		
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDiaFiltro').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMesFiltro').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnioFiltro').val();
		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDiaFiltro').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMesFiltro').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnioFiltro').val();
		
		window.location.href ='/pdfservlet/?accion=estadisticadesayunohoteles'
			+'&hotel='+idHotel	
			+'&fechadesdedia='+fechaDesdeDia
			+'&fechadesdemes='+fechaDesdeMes
			+'&fechadesdeanio='+fechaDesdeAnio
			+'&fechahastadia='+fechaHastaDia
			+'&fechahastames='+fechaHastaMes
			+'&fechahastaanio='+fechaHastaAnio;	

	}
</script>

