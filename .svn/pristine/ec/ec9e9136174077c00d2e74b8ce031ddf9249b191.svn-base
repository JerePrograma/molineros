<%@ include file="/html/portlet/utils/obrassociales/init.jsp" %>

<%


//String dinámico que se le debe pasar a esta pagina para que sepa a que direccion redireccionar
//con el formato /<nombre_portlet>/buscar_obrasocial
//ej: /afiliaciones/buscar_obrasocial
//esto se debe corresponder con el action definido en struts-config
//de la forma: <action path="/<nombre_portlet>/buscar_obrasocial" forward="portlet.utils.obrasocial.view" />
String searchURL = ParamUtil.getString(request, "search_url");
String esEdicionStr = ParamUtil.getString(request, "esEdicion");

boolean esEdicion = true;
if (esEdicionStr != null && !esEdicionStr.trim().equals("")) {
	if (esEdicionStr.equals("true")){
		esEdicion = true;
	} else {
		esEdicion = false;
	}
}

String idObraSocial = ParamUtil.getString(request, "id_obrasocial");
if (idObraSocial == null){
	idObraSocial = "";
}
%>

<input  id="<portlet:namespace />obra_social_ant" name="<portlet:namespace />obra_social_ant" type="text" value="<%=idObraSocial%>"  
<% if (esEdicion) { %> onkeydown="allowOnlyDigits(event)"
		onKeyUp="document.getElementById('<portlet:namespace />obrasocial').value=''; javascript:<portlet:namespace />buscarObraSocialOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFocoOS();" size="7"
		<%} %>
		<% if (!esEdicion) { %> readonly='readonly'<%} %>
		/>
<input id="<portlet:namespace />obrasocial" name="<portlet:namespace />obrasocial" type="text" size="80" 
	   value="" 
	   <% if (esEdicion) { %> 
	   onKeyUp="document.getElementById('<portlet:namespace />obra_social_ant').value=''; javascript:<portlet:namespace />buscarObraSocialOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFocoOS();"
	   <%} %>
		<% if (!esEdicion) { %> readonly='readonly'<%} %>
	   />	   
<div id="<portlet:namespace />btnBuscarOS" style="float:right;">
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarObraSocial();" tabindex="-1">
		Buscar			
	</a>
</div>
<div id='divObraSocial' style="float:right;">
</div>
	
<script type="text/javascript">
var popup;
function <portlet:namespace />buscarObraSocial() {
    var obrasocial=jQuery("#<portlet:namespace />obrasocial").val();
    var idObrasocial=jQuery("#<portlet:namespace />obra_social_ant").val();
	if (trim(obrasocial) == "" && trim(idObrasocial == "")){
		alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	}
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-os" />",modal:true,width:420});        
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>&obrasocial='+escape(obrasocial)+'&idObrasocial='+idObrasocial;
	jQuery(popup).load(url);    
}

function <portlet:namespace />buscarObraSocialOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(keyPressed==8 || keyPressed==46){		
		jQuery("#<portlet:namespace />obra_social_ant").val("");
		jQuery("#<portlet:namespace />obrasocial").val("");
		jQuery("#<portlet:namespace />btnBuscarOS").show();
		return false;
	}
	var obrasocial=jQuery("#<portlet:namespace />obrasocial").val();
    var idObrasocial=jQuery("#<portlet:namespace />obra_social_ant").val();
    if(trim(obrasocial).length > 2 || trim(idObrasocial).length > 1 ){
    	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>&obrasocial='+escape(obrasocial)+'&idObrasocial='+idObrasocial;
		jQuery("#divObraSocial").load(url);		
		jQuery("#divObraSocial").show();
    }else{        
    	jQuery("#divObraSocial").hide("slow");
    }
     
}
function <portlet:namespace />cerrarDivOS(){
	jQuery("#divObraSocial").hide("slow");		
}
function <portlet:namespace />cerrarOS(){	
	<portlet:namespace />cerrarDivOS();
	if(popup){		
		Liferay.Popup.close(popup);
	}	
}
function <portlet:namespace />pierdeFocoOS(){
	var seleccionada=jQuery("#<portlet:namespace />obra_social_ant").val();	
	if(trim(seleccionada).length!=0){
		<portlet:namespace />cerrarDivOS();
		return false;
	}else{				
		return false; 
	}	
}

<%if (idObraSocial != null && !idObraSocial.trim().equals("")){ %>
var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>&idObrasocial=<%=idObraSocial%>';
jQuery("#divObraSocial").load(url);		
jQuery("#divObraSocial").show();
<%}%>

</script>