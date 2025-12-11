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
    String datosReserva= (String)request.getSession().getAttribute(WebKeysHoteles.DATOS_RESERVAS);
    
	Calendar fecha = CalendarFactoryUtil.getCalendar(); 		
	fecha.setTime(new Date());
	
	
	
	NumberFormat format2D = new DecimalFormat("#0.00");
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	Double total=0D;
	
	
	for(Consumo c:consumos){
		total += c.getPrecio()*c.getCantidad();
		//unidad=c.getMesa().getDescripcion();
	}
	
			
%>	

<style type="text/css">
 * {
    font-size: 12px;
    font-family: 'Times New Roman';
}

td,
th,
tr,
table {
    border-top: 1px solid black;
    border-collapse: collapse;
}

td.producto,
th.producto {
    width: 90px;
    max-width: 90px;
}

td.cantidad,
th.cantidad {
    width: 70px;
    max-width: 70px;
    word-break: break-all;
    text-align: right;
}

td.precio,
th.precio {
    width: 70px;
    max-width: 70px;
    word-break: break-all;
    text-align: right;
}

.centrado {
    text-align: center;
    align-content: center;
   
}

.ticket {
    width: 355px;
    max-width: 355px;
}


.total{
    font: bold 12px/30px Times New Roman;

}

.reserva{
    font-size: bold 14px;
    font-family: 'Times New Roman';
    text-align: left;

}

img {
    max-width: inherit;
    width: inherit;
}

@media print {
    .oculto-impresion,
    .oculto-impresion * {
        display: none !important;
    }
}
</style>
<div id="id_ticket">
<html>
    <head>
       <!--   <link rel="stylesheet" href="style.css"> -->
       <!--    <script src="script.js"></script> -->
       
    </head>
    <body>
        <div class="ticket" >
        
        <img
                src="/html/images/logo_hotel.jpg"
                alt="Logotipo">
        
        <p class="centrado">COMPROBANTE DE CONSUMO<br>Fecha: <%=sdf.format(fecha.getTime()) %></p>
        <p class="reserva"><%=datosReserva%></p>
        
            <table>
                <thead>
                    <tr>
                        <th class="producto">PRODUCTO</th>
                        <th class="cantidad">CANTIDAD</th>
                        <th class="precio">PRECIO</th>
                        <th class="precio">TOTAL</th>
                    </tr>
                </thead>
                <tbody>
                
                    <%for(Consumo c:consumos){ %>
                      <tr>
                        <td class="producto"><%=c.getProducto().getDescripcion() %></td>
                        <td class="cantidad"><%=c.getCantidad() %></td>
                        <td class="precio"><%=format2D.format(c.getPrecio()) %></td>
                        <td class="precio"><%=format2D.format(c.getPrecio()*c.getCantidad()) %></td>
                      </tr>
                    <%}%>
                     <tr>
                        <td></td>
                        <td class="total">TOTAL</td>
                        <td class="total">$<%=format2D.format(total) %></td>
                    </tr>
                </tbody>
            </table>  
            <br><br><br><br>      
            <p class="centrado">................................................................<br>Firma</p>
        </div>
    </body>
</html>        

</div>



<!--   
<input type="button" id="btn_ticket_imprimir" class="oculto-impresion"
  value="Imprimir" onclick="javascript:imprimirTicketConsumo();" />
-->  

<script type="text/javascript">

function imprimirTicketConsumo(){
	
//	popupTIC.print();
	  window.print();

/*
	var prtContent = document.getElementById("id_ticket");
	var WinPrint = window.open('', '', 'left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0');
	WinPrint.document.write(prtContent.innerHTML);
	WinPrint.document.close();
	WinPrint.focus();
	WinPrint.print();
	WinPrint.close();
*/

}


</script>
