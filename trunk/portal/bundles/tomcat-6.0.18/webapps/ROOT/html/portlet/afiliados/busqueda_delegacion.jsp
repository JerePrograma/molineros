<%@page import="ar.com.ospim.util.StringUtils"%>
<%@ include file="/html/portlet/afiliados/init.jsp" %>

<%
String esEdicionStr=ParamUtil.getString(request,"esEdicion"); 

boolean esEdicion = true;
if (esEdicionStr != null && esEdicionStr.equalsIgnoreCase("vista")){
	esEdicion = false;
} 
String id_delegacion=ParamUtil.getString(request,"id_delegacion");
String delegacion=ParamUtil.getString(request,"delegacion");
%>

<input  id="<portlet:namespace />id_delegacion" name="<portlet:namespace />id_delegacion" maxlength="4" size="4" type="text" value="<%=id_delegacion%>" 
		<% if (esEdicion) { %> onBlur="javascript:<portlet:namespace />pierdeFoco();" onKeyUp="javascript:<portlet:namespace />buscarDelegacionOnDiv(event)" <%} %>
		<% if (!esEdicion) { %> readonly='readonly'<%} %>/>
<input id="<portlet:namespace />delegacion" name="<portlet:namespace />delegacion" size="15" type="text" 
	   value="<%=delegacion%>" 
	   <% if (esEdicion) { %> onKeyUp="javascript:<portlet:namespace />buscarDelegacionOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFoco();" <%} %>
	   <% if (!esEdicion) { %> readonly='readonly'<%} %>/>
<div id="<portlet:namespace />btnBuscarDelegacion" style="float:right;">
	<% if (esEdicion) { %>
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarDelegacion();" tabindex="-1">
		Buscar
	</a>
	<%} %>
</div>
<input id="<portlet:namespace />deleg_seleccionada" name="<portlet:namespace />deleg_seleccionada" type="hidden" value=""/>
<input id="<portlet:namespace />deleg_libro" name="<portlet:namespace />deleg_libro" type="hidden" value=""/>
<input id="<portlet:namespace />deleg_tomo" name="<portlet:namespace />deleg_tomo" type="hidden" value=""/>
<div id='divDelegacion' style="float:right;">
</div>
	
<script type="text/javascript">
var popupDeleg;
function <portlet:namespace />buscarDelegacion() {
	var id_delegacion=jQuery("#<portlet:namespace />id_delegacion").val();
    var delegacion=jQuery("#<portlet:namespace />delegacion").val();
	if (!<portlet:namespace />validaForm(id_delegacion,delegacion)){
		return false;
	}
    popupDeleg = Liferay.Popup({title:"<liferay-ui:message key="busqueda-delegaciones" />",modal:true,width:420});
<%--     <%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
    	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/buscar_delegacion&id_delegacion='+id_delegacion+
    		  '&delegacion='+encodeURI(delegacion);
    <%}else{%>  --%>       
    	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_delegacion&id_delegacion='+id_delegacion+
    		  '&delegacion='+encodeURI(delegacion);
  <%--   <%}%>   --%> 	
	jQuery(popupDeleg).load(url);    
}
function <portlet:namespace />buscarDelegacionOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />deleg_seleccionada").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />delegacion").val("");
		jQuery("#<portlet:namespace />id_delegacion").val("");
		jQuery("#<portlet:namespace />deleg_libro").val("");
		jQuery("#<portlet:namespace />deleg_tomo").val("");
		jQuery("#<portlet:namespace />libro").val("");
		jQuery("#<portlet:namespace />tomo").val("");
		jQuery("#<portlet:namespace />deleg_seleccionada").val("");
		jQuery("#<portlet:namespace />btnBuscarDelegacion").show();
		return false;
	}
	var id_delegacion=jQuery("#<portlet:namespace />id_delegacion").val();	
    var delegacion=jQuery("#<portlet:namespace />delegacion").val();
    if((delegacion.length>=3 || id_delegacion.length>2) && (keyPressed!=9 && keyPressed!=16)){        
        if(id_delegacion.length >2){
        	jQuery("#<portlet:namespace />delegacion").val("");
        }else{
    		jQuery("#<portlet:namespace />id_delegacion").val("");
        }
<%--         <%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
        	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/buscar_delegacion&id_delegacion='+id_delegacion+
		  	'&delegacion='+encodeURI(delegacion);
        <%}else{%> --%>     
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_delegacion&id_delegacion='+id_delegacion+
		  	'&delegacion='+encodeURI(delegacion);
		<%-- <%}%> --%>
		jQuery("#divDelegacion").load(url);		
		jQuery("#divDelegacion").show();
    }else{
        jQuery("#divDelegacion").hide("slow");
    }
}
function <portlet:namespace />cerrarDiv(){
	jQuery("#divDelegacion").hide("slow");		
}

function <portlet:namespace />cerrar(){
	<portlet:namespace />cerrarDiv();
	if(popupDeleg){		
		Liferay.Popup.close(popupDeleg);
	}	
}

function <portlet:namespace />pierdeFoco(){
	var seleccionada=jQuery("#<portlet:namespace />deleg_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDiv();
		return false;
	}else{				
		return false; 
	}	
}

function <portlet:namespace />validaForm(id_delegacion, delegacion){
	 if(trim(id_delegacion).length==0 && trim(delegacion).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValid() {
	if (jQuery("#<portlet:namespace />id_delegacion").val() != "") {
		jQuery("#<portlet:namespace />deleg_seleccionada").val("1")
	}
}

<portlet:namespace />resetValid();
</script>