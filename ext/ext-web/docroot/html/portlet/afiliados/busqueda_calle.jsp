<%@ include file="/html/portlet/afiliados/init.jsp"%>

<%
Afiliado afiliado = (Afiliado)session.getAttribute(WebKeysAfiliados.AFILIADO_EN_EDICION);
boolean esTitular = afiliado == null ? true : afiliado.esTitular();
%>

<input id="<portlet:namespace />calle" name="<portlet:namespace />calle"
	size="15" type="text" value="<%= afiliado != null ? afiliado.getDomicilioDefault().getCalle() : "" %>"
	onKeyUp="javascript:<portlet:namespace />buscarCalleOnDiv(event);"
	<% if (!esTitular) { %> readonly="readonly" <%} %> />
<input id="<portlet:namespace />calle_seleccionada"
	name="<portlet:namespace />calle_seleccionada" type="hidden" value="" />
<div id='divCalle' style="float: right;"></div>

<script type="text/javascript">
	function <portlet:namespace />buscarCalleOnDiv(e) {
		//Se modificó el campo, debemos cambiar el selecc	
		var evtobj=window.event? event : e;
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode;
		if (jQuery("#<portlet:namespace/>localidad").val() == "265") {
			var calle = jQuery("#<portlet:namespace />calle").val();			
		    if (calle.length > 0) {
		        if (calle.length >= 4 || (calle.length > 3 && keyPressed != 9 && keyPressed != 16)) {			        	
		        
		        <% if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
					var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/estudio_isidro/buscar_calle&calle='+calle;
				<%}else{%>
					var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/buscar_calle&calle='+calle;
				<%}%>
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