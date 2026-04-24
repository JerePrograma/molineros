<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>

<portlet:defineObjects/>
			<%
			String portlet_name=null;
			NumberFormat formatter = new DecimalFormat("#0.00");
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION); 
			ComprobanteTratamientoDiscapacidad comprobante = new ComprobanteTratamientoDiscapacidad();
			int idLiquidacion = ParamUtil.getInteger(request,"idLiquidacion");
			int idPrestacion = ParamUtil.getInteger(request,"idPrestacion");
			int orden = ParamUtil.getInteger(request,"orden");
			for(ComprobanteTratamientoDiscapacidad c:seguimiento.getLiquidaciones()){
				if(c.getLiquidacionPrestacion().getId_liquidacion()==idLiquidacion &&
					c.getLiquidacionPrestacion().getId_prestacion()==idPrestacion &&
					c.getLiquidacionPrestacion().getOrden()==orden){
					comprobante = c;
					break;
				}
			}
			
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	
		
   	<table class="lfr-table">
		<tr>
			<td>
			   <b>Comprobante: </b> 
			</td>
			<td>
			   <%=comprobante.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_tipo() %>
			   <%=comprobante.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_letra() %>
			   <%=comprobante.getLiquidacionPrestacion().getLiquidacion().getCompro_a_debitar_numero() %>
			 </td>  
		</tr>
		<tr>
				<td>&nbsp;</td>
		</tr>
		<tr>
		    <td>
			   <b>Prestador: </b>  
			</td>
			<td><%=comprobante.getPrestador().getDescripcion()%> (<%=comprobante.getPrestador().getCuit()%>)</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		
		<tr>
		    <td>
			   <b>Período: </b>  
			</td>
			<td><%=comprobante.getLiquidacionPrestacion().getLiquidacion().getPeriodoString() %> </td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		
		<tr>
		    <td>
			   <b>Cantidad: </b> 
			</td>
			<td><%=formatter.format(comprobante.getLiquidacionPrestacion().getCantidad()) %></td>
		</tr>
		<tr>
				<td>&nbsp;</td>
		</tr>
		<tr>
		    <td>
			   <b>Importe: </b> 
			</td>
			<td>
			    <%=formatter.format(comprobante.getLiquidacionPrestacion().getImporte()) %>
			</td>
		</tr>
		<tr>
				<td>&nbsp;</td>
		</tr>
		<tr>
		    <td>
			   <b>Total: </b> 
			</td>
			<td>
			   <%=formatter.format(
			   comprobante.getLiquidacionPrestacion().getCantidad().multiply(comprobante.getLiquidacionPrestacion().getImporte())) %>
			 </td>  
		</tr>
		
		
		<tr>
				<td>&nbsp;</td>
		</tr>
		<tr>
		    <td>
			   <b>Debitado: </b> 
			</td>
			<td>
			    <%=formatter.format(comprobante.getLiquidacionPrestacion().getLiquidacion().getDebitado()) %>
			</td>
		</tr>
		
		
		
		
		<tr>
				<td>&nbsp;</td>
		</tr>
		<tr>
		    <td>
			   <b>Liquidación: </b> 
			</td>
			<td>
			    <%=comprobante.getLiquidacionPrestacion().getId_liquidacion() %>
			</td>
		</tr>
	</table>


	