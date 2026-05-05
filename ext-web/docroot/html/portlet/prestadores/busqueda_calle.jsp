<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>

<%
PrestadorLugarAtencion lugarAtencion = (PrestadorLugarAtencion) request.getSession().getAttribute(WebKeysLiquidaciones.LUGAR_ATENCION_PRESTADOR_EN_EDICION);
Domicilio domicilio = lugarAtencion!=null?lugarAtencion.getDomicilio():null;

String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEditable = Boolean.parseBoolean(esEditableStr);
%>

<input id="<portlet:namespace />calle" name="<portlet:namespace />calle"
	size="25" type="text" value="<%= domicilio != null ? domicilio.getCalle() : "" %>"
	onKeyUp="javascript:<portlet:namespace />buscarCalleOnDiv(event);"
	<% if(!esEditable) { %>readonly="readonly" <%} %> />
<input id="<portlet:namespace />calle_seleccionada"
	name="<portlet:namespace />calle_seleccionada" type="hidden" value="" />
<div id='divCalle' style="float: right;"></div>

<script type="text/javascript">
	function <portlet:namespace />buscarCalleOnDiv(e) {
				  var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/buscar_calle&calle='+calle;
		var evtobj=window.event? event : e;
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode;
		if (jQuery("#<portlet:namespace/>localidad").val() == "265") {
			var calle = jQuery("#<portlet:namespace />calle").val();			
		    if (calle.length > 0) {
		        if (calle.length >= 4 || (calle.length > 3 && keyPressed != 9 && keyPressed != 16)) {			        	
		        
				  var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/buscar_calle&calle='+calle;
				
					jQuery("#divCalle").load(url);		
					jQuery("#divCalle").show();
		    	} else {        
		    		jQuery("#divCalle").hide("slow");
		   		}
	   		}
		}
	}

	function <portlet:namespace />cerrarCalle() {	
		jQuery("#divCalle").hide("slow");
	}
</script>