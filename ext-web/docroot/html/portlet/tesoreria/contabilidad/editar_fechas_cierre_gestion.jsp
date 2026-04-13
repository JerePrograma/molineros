<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
	
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
	rolABM = true;
%>

<form action="" method="post" name="<portlet:namespace />editar_fecha_asiento" >

<table style="width: 100%">
	<tr><td colspan="2"><b>Fecha cierre gestión</b></td></tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td>Fecha:&nbsp;<input type="text" name="fecha" id="fecha" value="" size="9"/></td>
		<td>Descripción:&nbsp;<input type="text" name="descripcion" id="descripcion" value="" size="80"/></td>
	</tr>
</table>
<% if (rolABM) {%> 
		<input type="button" onclick="altaFechaAsiento()" value="Guardar" />
<% } %>
<span id="guardando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</span>
</form>
	
<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
	<%if(portlet_name.equals("farmacia")){%>
		<portlet:param name="struts_action" value="/farmacia/fechas_cierre_contable" />
	<%}else if(portlet_name.equals("uoma")){%>
		<portlet:param name="struts_action" value="/uoma/fechas_cierre_contable" />	
	<%}else{%>
		<portlet:param name="struts_action" value="/tesoreria/fechas_cierre_contable" />
	<%}%>
</portlet:renderURL>
<p><a href="<%= volver %>">Volver</a></p>	 
<script type="text/javascript">	
	jQuery(document).ready(function(){
		jQuery("#guardando").hide();
		
		jQuery("#fecha").datepicker(jQuery.datepicker.regional['es']);
	});
	
	function altaFechaAsiento(){
		
		if (!validarFecha()){
			alert("Debe elegir una fecha valida");
		}
		
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/guardar_fecha_cierre_gestion';
		
		document.<portlet:namespace />editar_fecha_asiento.method = 'post';
		submitForm(document.<portlet:namespace />editar_fecha_asiento, url);
	}
	
	function validarFecha(){
		if (jQuery("#fecha").val().toString().length == 0){
			jQuery("#fecha").css({'color': 'red'});
			jQuery("#fecha").val("Debe completar la fecha");
 			return false;
		}
		if (jQuery("#fecha").val().toString().length != 0){
			if (jQuery("#fecha").val().split("/").length != 3){
				jQuery("#fecha").css({'color': 'red'});
				return false;
			}
	 		if (jQuery("#fecha").val().split("/")[0] > 31 ){
	 			jQuery("#fecha").css({'color': 'red'});
	 			return false;
	 		}
	 		if (jQuery("#fecha").val().split("/")[1] > 12 ){
	 			jQuery("#fecha").css({'color': 'red'});
	 			return false;
	 		}
	 		if (jQuery("#fecha").val().split("/")[2] < 1800 || jQuery("#fecha").val().split("/")[2] >2099 ){
	 			jQuery("#fecha").css({'color': 'red'});
	 			return false;
	 		}
		}
	 	jQuery("#fecha").css({'color': 'black'});
		return true;
	}
</script>

