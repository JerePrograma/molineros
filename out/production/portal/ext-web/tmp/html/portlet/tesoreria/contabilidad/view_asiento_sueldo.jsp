<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento" %>
		
<%
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
	
	Asiento asiento = (Asiento)request.getSession().getAttribute(WebKeysTesoreria.ASIENTO_SUELDO_EN_SESSION);
	
	boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
	boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
%>
<table style="width: 100%">
	
</table>
<fieldset class="block-labels" style="width: 80%">
<legend>Detalle</legend>
<table style="width: 100%" id="detalle_asientos">
	<thead>
		<tr>
			<th style="text-align: center;">Pase</th>
			<th style="text-align: center;">Cuenta</th>
			<th style="text-align: right;">Debe</th>
			<th style="text-align: right;">Haber</th>
			
		</tr>
	</thead>
	
	<tbody>
	  <%for(Asiento.Detalle detalle:asiento.getDetalle()){ %>
	      <tr> 
	        <td><%=detalle.getPase()%> </td>
	        <td><%=detalle.getCuenta().getNumero()!=null? detalle.getCuenta().getNumero() + " " + detalle.getCuenta().getCuenta():""%> </td>
	        <td><%=detalle.getDebeAsString()!=null?detalle.getDebeAsString():"" %></td>
	        <td><%=detalle.getHaberAsString()!=null? detalle.getHaberAsString():"" %></td>
	      
	      </tr>
	  <%} %>
	</tbody>
	
	<tfoot style="border-top: solid thick">
	<tr> 
	        <td></td>
	        <td>Suma de Columnas </td>
	        <td><%=asiento!=null && asiento.getTotalDebeAsString()!=null?asiento.getTotalDebeAsString():"" %></td>
	        
	        <td><%=asiento!=null && asiento.getTotalHaberAsString()!=null?asiento.getTotalHaberAsString():"" %></td>
	      
	      </tr>
	</tfoot>
	
</table>
</fieldset>
<br/>
