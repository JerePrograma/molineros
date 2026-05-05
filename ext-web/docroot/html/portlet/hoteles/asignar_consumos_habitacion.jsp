<%@include file="/html/portlet/hoteles/hoteles.css"%>
<%@ include file="/html/portlet/hoteles/init.jsp" %>
<%
	response.setHeader("Cache-Control","no-store"); //HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0
	response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
	List<Consumo> consumos=(List<Consumo>)request.getSession().getAttribute(WebKeysHoteles.CONSUMOS_HOTEL);
	String unidad= (String)request.getSession().getAttribute(WebKeysHoteles.MESA_ASIGNAR_HOTEL);
    String empleadoId=(String)request.getSession().getAttribute(WebKeysHoteles.EMPLEADO_A_ASIGNAR_HOTEL);
    String ptoVta=(String)request.getSession().getAttribute(WebKeysHoteles.HOTEL_ID);
    String tipo=(String)request.getSession().getAttribute(WebKeysHoteles.TIPO_UNIDAD);
	Calendar fecha = CalendarFactoryUtil.getCalendar(); 		
	fecha.setTime(new Date());
	
	List<Reserva> reservas=HotelesServiceUtil.getReservasActivas(fecha.get(Calendar.YEAR), fecha.getTime());
	
	NumberFormat format2D = new DecimalFormat("#0.00");
	
	Double total=0D;
	
	
	for(Consumo c:consumos){
		total += c.getPrecio()*c.getCantidad();
		//unidad=c.getMesa().getDescripcion();
	}
	
			
%>	


<div id="div_titulo" class="titulo">
    <h1>
      <label><%=tipo%>: <%=unidad %></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <label>Total $ <%= format2D.format(total)%></label>
    </h1>
</div>

<table>
<tr>
<td>
<select name="<portlet:namespace />reservas" class="select-css"
		id="<portlet:namespace />reservas">
		                <option value="0">Seleccione una RESERVA</option>
		
						<%for(Reserva r:reservas) {%>
						 <option value="<%=r.getIdReserva()%>"
						 <%if("HABITACION".equalsIgnoreCase(tipo) && r.getIdHabitacion().equalsIgnoreCase(unidad) ){%>selected="selected"<%}%>>
							<%=r.getIdHabitacion()+"-- "+r.getApellido()+" "+r.getNombre() + " Desde " + r.getFechaDesde() + " Hasta " + r.getFechaHasta() %>
						</option>
						<% } %>
</select>
</td>
</tr>
<tr><td>&nbsp;</td></tr>
</table>
 <input type="button" id="btn_asigna_a_habitacion" class="comandos" value="Asignar" onclick="javascript:guardarConsumoHabitacion();" />
 
 <input type="button" id="btn_ticket_a_habitacion" class="comandos" value="Ticket" onclick="javascript:ticketConsumoHabitacion();" />
<script type="text/javascript">

jQuery("#btn_ticket_a_habitacion").hide();

function guardarConsumoHabitacion(){
	var idReserva= jQuery("#<portlet:namespace />reservas").val();
	if(idReserva==0){
		alert("Debe Seleccionar una Habitación");
	}else{
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/hoteles/comandos_confiteria';
        url +='&ptovta=<%=ptoVta%>';
		url +='&tipo=<%=tipo%>';
    	url +='&unidad=<%=unidad%>';
		url +='&idpersonal=<%=empleadoId%>';
		url +='&cmd=update_consumo_habitaciones';
		url +='&reserva='+idReserva;
        url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				jQuery("#btn_asigna_a_habitacion").hide();
				if(popupHOT){
					   Liferay.Popup.close(popupHOT);
				}
			}
		});
	}
}



function ticketConsumoHabitacion(){
	var idReserva= jQuery("#<portlet:namespace />reservas").val();
	
	if(idReserva==0){
		alert("Debe Seleccionar una Habitación");
	}else{
		popupTIC = Liferay.Popup({title:"TICKET CONSUMOS HABITACION ",modal:true,width:350,position:[350,30],xy: ['center', 100],
			onClose: function() {}});
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/hoteles/comandos_confiteria';
        url +='&ptovta=<%=ptoVta%>';
		url +='&tipo=<%=tipo%>';
    	url +='&unidad=<%=unidad%>';
		url +='&idpersonal=<%=empleadoId%>';
		url +='&cmd=ticket_consumo_habitaciones';
		url +='&reserva='+idReserva;
        url += '&rnd=' + Math.floor(Math.random()*100);
        
        jQuery(popupTIC).load(url);
		
	}
}


</script>
