<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
		
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
	
	
	boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
	boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
%>
<table style="width: 100%">
	<tr><td colspan="3"><b>Asiento</b></td></tr>
	<tr><td colspan="3">&nbsp;</td></tr>
	<tr>
		<td colspan="3"><b>Ejercicio:</b>&nbsp;
			<b>${asiento.ejercicioDesdeString}&nbsp;-&nbsp;${asiento.ejercicioHastaString}</b>
		</td>
	</tr>	
	<tr>
		<td>Fecha:&nbsp;${asiento.fechaString}</td>
		<td>Nro:&nbsp;${asiento.nro}</td>
		<td>Descripción:&nbsp;${asiento.descripcion}</td>
	</tr>
	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>
</table>
<fieldset class="block-labels" style="width: 80%">
<legend>Detalle</legend>
<table style="width: 100%" id="detalle_asientos">
	<thead>
		<tr>
			<th style="text-align: center;">Pase</th>
			<th style="text-align: center;">Cuenta</th>
			<th style="text-align: center;">Centro de Costo</th>
			<th style="text-align: center;">Comprobante</th>
			<th style="text-align: right;">Debe</th>
			<th style="text-align: right;">Haber</th>
			<th style="text-align: center;">Observaciones</th>
		</tr>
	</thead>
	<tbody>
		 <c:forEach var="det" items="${asiento.detalle}">
		 	<tr>
				<td>${det.pase}</td>
				<td>${det.cuenta.numero}&nbsp;-&nbsp;${det.cuenta.cuenta}</td>
				<td style="width:200px;">${det.centroCosto != null && det.centroCosto.id != 0 ? det.centroCosto.descripcion : ''}</td>
				<td>${det.comprobante}</td>
				<td style="text-align: right;">${det.debeAsString}</td>
				<td style="text-align: right;">${det.haberAsString}</td>
				<td>&nbsp;&nbsp;${det.observaciones}</td>
			</tr>
		 </c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">&nbsp;</td>
			<td style="text-align: right;"><b>Totales</b></td>
			<td style="text-align: right;"><b>${asiento.totalDebeAsString}</td>
			<td style="text-align: right;"><b>${asiento.totalHaberAsString}</td>
			<td>&nbsp;</td>
		</tr>
   </tfoot>
</table>
</fieldset>
<br/>

<% if (rolABM && !soloVer) {%> 
		<c:if test="${not asiento.automatico}">
			<input type="button" onclick="editarAsiento()" value="Editar" />
		</c:if>
		<input type="button" onclick="altaAsiento()" value="Alta Nuevo Asiento" />
<% } %>

	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
		<%if(portlet_name.equals("farmacia")){%>
			<portlet:param name="struts_action" value="/farmacia/asientos" />
		<%}else if(portlet_name.equals("uoma")){%>		
			<portlet:param name="struts_action" value="/uoma/asientos" />		
		<%}else{%>		
			<portlet:param name="struts_action" value="/tesoreria/asientos" />
		<%}%>
	</portlet:renderURL>
	<p><a href="<%= volver %>">Volver</a></p>

<script type="text/javascript">
	function editarAsiento(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_asiento';
		url += '&asiento_id=${asiento.id}';
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location = url;
	}
	
	function altaAsiento(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_asiento';
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location = url;
	}
</script>

