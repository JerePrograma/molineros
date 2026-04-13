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
		<legend>Resumen Reservas</legend>
		
		<table class="lfr-table">
		  <tr>
		   <td>Año:</td>
		   <td><select name="<portlet:namespace />anio_filtro"  id="<portlet:namespace />anio_filtro" >
		                <option value="<%=current.get(Calendar.YEAR)%>"><%=current.get(Calendar.YEAR)%>	</option>
						<option value="<%=current.get(Calendar.YEAR)-1%>"><%=current.get(Calendar.YEAR)-1%>	</option>
				 </select>
		   </td>	 
		   
		   <td>Reserva Nro:</td>
		   <td><input id="<portlet:namespace />idReserva_filtro" 
		       name="<portlet:namespace />idReserva_filtro" size="5" maxlength="5" 
			   type="text" value='' onkeydown="allowOnlyDigits(event);"/>
		   </td>
		   
		   <td>Habitación Nro:</td>
		   <td><input id="<portlet:namespace />idHabitacion_filtro" 
		       name="<portlet:namespace />idHabitacion_filtro" size="4" maxlength="4" 
			   type="text" value=''/>
		   </td>
		   
		   
		   <td><label>Finalización Reserva Dde:</label></td>
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
						yearRangeStart="<%= current.get(Calendar.YEAR) - 1 %>"
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
							yearRangeStart="<%= current.get(Calendar.YEAR) - 1 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
			</td>
		   
		   
		     
		  </tr>
		  <tr>
		    <td>&nbsp;</td>
		  </tr>
		</table>
		<input type="button" value="Buscar" onClick="<portlet:namespace />buscarReservas();"/>&nbsp;
		
		<input type="button" value="Imprimir Todos" onClick="<portlet:namespace />imprimirTodos();"/>&nbsp;
			  
		<div id="<portlet:namespace />div_reservas">
			<jsp:include page='/html/portlet/hoteles/reservas_result.jsp' />  	
		</div>
		
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
		
		window.location.href ='/pdfservlet/?accion=resumenliquidacionreservageneral'
			+'&hotel='+idHotel	
			+'&anio='+anio
			+'&reserva='+reserva
			+'&habitacion='+habitacion
			+'&fechadesdedia='+fechaDesdeDia
			+'&fechadesdemes='+fechaDesdeMes
			+'&fechadesdeanio='+fechaDesdeAnio
			+'&fechahastadia='+fechaHastaDia
			+'&fechahastames='+fechaHastaMes
			+'&fechahastaanio='+fechaHastaAnio;	

	}
	
	function <portlet:namespace />buscarReservas() {
		var anio=jQuery('#<portlet:namespace />anio_filtro').val();
		var reserva=jQuery('#<portlet:namespace />idReserva_filtro').val();
		var habitacion=jQuery('#<portlet:namespace />idHabitacion_filtro').val();
		var idHotel=jQuery('#<portlet:namespace />id_hotel').val();
		
		
		
		var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDiaFiltro");
		var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMesFiltro");
		var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnioFiltro");

		var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDiaFiltro");
		var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMesFiltro");
		var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnioFiltro");

		
		var busquedaNom = {"anio":anio,"cmd":"filtrar","id_hotel":idHotel,"reserva":reserva,"habitacion":habitacion,
				"fechadesdedia":fechaDesdeDia.value,"fechadesdemes":fechaDesdeMes.value,"fechadesdeanio":fechaDesdeAnio.value,
				"fechahastadia":fechaHastaDia.value,"fechahastames":fechaHastaMes.value,"fechahastaanio":fechaHastaAnio.value};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/hoteles/hoteles_gestion_administrativa" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />div_reservas').load(url,busquedaNom, function(){	});	
	}
	
</script>

