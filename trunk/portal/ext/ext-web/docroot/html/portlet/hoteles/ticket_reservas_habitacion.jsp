<%@include file="/html/portlet/hoteles/hoteles.css"%>
<%@include file="/html/portlet/hoteles/init.jsp" %>
<%
	response.setHeader("Cache-Control","no-store"); //HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0
	response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
	List<Reserva> reservas=(List<Reserva>)request.getSession().getAttribute(WebKeysHoteles.RESERVAS);

    
	Calendar fecha = CalendarFactoryUtil.getCalendar(); 		
	
	
	NumberFormat format2D = new DecimalFormat("#0.00");
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

			
%>	

</style>
<div id="id_ticket">
<html>
    <head>
       
    </head>
    <body>
      <table>
			<tr>
			<td>
			<select name="<portlet:namespace />reservas"
					id="<portlet:namespace />reservas">
					<option value="0">Seleccione una reserva</option>
					
					<%for(Reserva r:reservas) {%>
						<option value="<%=r.getDocumento() + "," + r.getIdCliente() + "," + r.getIdReserva() + "," + r.getApellido() + "," + r.getNombre() %>">
			<%=r.getApellido()+" "+r.getNombre() +  " Documento " +  r.getDocumento() + " Desde " + sdf.format(r.getFechaDesde()) + " Hasta " + sdf.format(r.getFechaHasta()) %>
						</option>
					<% } %>
			</select>
			</td>
			</tr>
			<tr><td>&nbsp;</td></tr>
		</table>
    </body>
</html>        

</div>



 
<input type="button" id="btn_facturar" 
  value="Seleccionar Reserva" onclick="javascript:facturarReserva();" />
<script type="text/javascript">

function facturarReserva(){
	var aux = '';
	var aux= jQuery("#<portlet:namespace />reservas").val();

	
	values=aux.split(',');
	var doc = values[0];
	var id_cliente=values[1];
	var id_reserva=values[2];
	var apellido = values[3];
	var nombre = values[4];


	if(doc==0){
		alert("Debe Seleccionar una Reserva");
	}else{
	    
	    jQuery("#<portlet:namespace />cliente_nro_doc").val(doc);
	    
	    jQuery("#<portlet:namespace />cliente_apellido").val(apellido + "," + nombre);

	    
	    jQuery("#id_cliente").val(id_cliente);
	    jQuery("#id_reserva").val(id_reserva);

	    
	    <portlet:namespace />buscarPersFisica();
	    
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/buscar_reservas_vigentes_hotel';
		url +='&cmd=update_consumo_habitaciones';
		url +='&reserva='+doc;
		url +='&id_reserva='+id_reserva;
        url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery.ajax({   
			url: url,
			async:false,
			success: function(data){
				if(popupHOT){
					   Liferay.Popup.close(popupHOT);
				}
			}
		});
		

		
	}
	
	  

	


}


</script>
