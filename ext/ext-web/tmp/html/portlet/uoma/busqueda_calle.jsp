<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
String calle=ParamUtil.getString(request,"calle_edit",null);
String prefi= request.getParameter("prefi");
%>

<input id="<portlet:namespace />calle<%=prefi!=null?prefi:""%>" name="<portlet:namespace />calle<%=prefi!=null?prefi:""%>"
	size="15" type="text" 	onKeyUp="javascript:<portlet:namespace />buscarCalleOnDiv(event);" value="<%=null!=calle?calle:""%>"/>
<input id="<portlet:namespace />calle_seleccionada<%=prefi!=null?prefi:""%>"	name="<portlet:namespace />calle_seleccionada<%=prefi!=null?prefi:""%>" type="hidden" value="" />
<div id='divCalle' style="float: right;"></div>

<script type="text/javascript">
	function <portlet:namespace />buscarCalleOnDiv(e) {
		//Se modificó el campo, debemos cambiar el selecc	
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad<%=prefi!=null?prefi:""%>").val() == "265") {
			var calle = jQuery("#<portlet:namespace />calle<%=prefi!=null?prefi:""%>").val();			
		    if (calle.length > 0) {
		        if (calle.length >= 4 || (calle.length > 3 && keyPressed != 9 && keyPressed != 16)) {			        	
					var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/uoma/buscar_calle&calle='+calle;
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