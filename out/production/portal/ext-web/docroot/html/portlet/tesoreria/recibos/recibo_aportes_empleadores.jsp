<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.FichaBoletaPortal"%>
<%@ page import="java.text.DecimalFormat"%>
<% 

DecimalFormat fm = new DecimalFormat("###0.00");

FichaBoletaPortal boleta= (FichaBoletaPortal)request.getSession().getAttribute(WebKeysTesoreria.BOLETA_EMPLEADORES_NRO);


String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

Double totalBoleta =0D;
if(boleta!=null){ 
  totalBoleta=(boleta.getCapital().add(boleta.getInteres())).add(boleta.getAjusteCapital()).doubleValue();
}
%>
<fieldset class="block-labels"><legend>Detalle Portal Empleadores</legend>
  
  <fieldset class="block-labels"><legend>Boleta</legend>
 
  <%if(boleta==null){ %>
      <label>No se ha encontrado la boleta buscada</label>
  <%}else{%>
      <table class="lfr-table">
         <tr>
            <td><label>Boleta:</label></td>
            <td><label><%=boleta.getDescripcion()%></label></td>
            
            <td><label>Nro:</label></td>
            <td>
            <label><%=boleta.getNro_boleta_portal_emple()%> </label>
            </td>
            <td><label>Cuit:</label></td>
            <td><label><%=boleta.getEmpresa_cuit() %> </label> </td>
            <td><label>Período:</label></td>
            <td><label><%=boleta.getPeriodoAsString() %> </label> </td>
            <td><label>Vencimiento:</label></td>
            <td><label><%=boleta.getFecha_ing() %> </label> </td>
            <td><label>Total:</label></td>
            <td><label><%=fm.format( totalBoleta!=null?totalBoleta:0D )%> </label> </td>
         </tr>
         <tr><td>&nbsp;</td></tr>
         <tr>
           
           <table class="lfr-table" style="border: 1px solid black">
           <th>&nbsp;&nbsp;Pago Registrado</th>
           <tr>
           <td>&nbsp;</td>
           <td><label>Fecha Recaudación:</label></td>
           <td><label style="color:red"><%=boleta.getFecha_recaudaAsString()%></label></td>
           <td> <td><label>Importe Recaudación:</label></td>
           <td><label style="color:red"><%=boleta.getImporte()!=null?fm.format(boleta.getImporte().doubleValue()):"" %> </label> </td>
           <td><label>Datos Bancarios:</label></td>
           <td><label style="color:red"><%=(boleta.getCuenta_sucursal() + "/" + (boleta.getCod_sucursal_nacion()==0?"": boleta.getCod_sucursal_nacion())) %></label></td>
           <td>&nbsp;</td>
           </tr>
           </table>
           
         </tr>
            
      </table>   
  <%}%>   
</fieldset>

<div align="center" id="<portlet:namespace />boletasImpagas">
    <fieldset class="block-labels"><legend>Boletas Impagas</legend>
			<jsp:include page='recibo_aportes_empleadores_search_result.jsp' />
	</fieldset>		
</div>
				
<script type="text/javascript">	

</script>

