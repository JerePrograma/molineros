<%@ include file="/html/portlet/empresas/init.jsp"%>

<%
String calle=ParamUtil.getString(request,"calle");
%>

<input id="<portlet:namespace />calle" name="<portlet:namespace />calle"
	size="20" type="text" value="<%=calle%>"
	onKeyUp="javascript:<portlet:namespace />buscarCalleOnDiv(event);"/>
<input id="<portlet:namespace />calle_seleccionada"
	name="<portlet:namespace />calle_seleccionada" type="hidden" value="" />
<div id='divCalle' style="float: right;"></div>

<script type="text/javascript">
	function <portlet:namespace />buscarCalleOnDiv(e) {
		//Se modificó el campo, debemos cambiar el selecc	
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad").val() == "265") {
			var calle = jQuery("#<portlet:namespace />calle").val();			
		    if (calle.length > 0) {
		        if (calle.length >= 4 || (calle.length > 3 && keyPressed != 9 && keyPressed != 16)) {		        
					var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/<%=portlet_name%>/buscar_calle&calle='+calle;
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