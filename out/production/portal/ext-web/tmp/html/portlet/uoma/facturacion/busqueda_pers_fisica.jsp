<%@ include file="/html/portlet/afiliados/init.jsp" %>

<%
String portlet_name = ParamUtil.getString(request, "portlet_name");
String prefijo = ParamUtil.getString(request, "prefijo","");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}
if(renderResponse.getNamespace().equals("_HOT_1_")){
	portlet_name = "hoteles";
}

String esEdicionStr=ParamUtil.getString(request,"esEdicion");
boolean esEdicion = false;
if (esEdicionStr != null && !esEdicionStr.trim().equals("")) {
	if (esEdicionStr.equals("true")){
		esEdicion = true;
	} else {
		esEdicion = false;
	}
}
String cliente_nro_doc=ParamUtil.getString(request,"cliente_nro_doc");
String cliente_apellido=ParamUtil.getString(request,"cliente_apellido");
String cliente_nombre=ParamUtil.getString(request,"cliente_nombre");
%>
<div style="float:left;">
<label><liferay-ui:message key="nro-documento" />:</label>
<input  id="<portlet:namespace />cliente_nro_doc" name="<portlet:namespace />cliente_nro_doc" maxlength="8" size="15" type="text" value="<%=cliente_nro_doc%>" 
		<% if (esEdicion) { %> onBlur="javascript:<portlet:namespace />pierdeFoco();" onKeyUp="javascript:<portlet:namespace />buscarPersFisicaOnDiv(event)" 
		onkeydown="allowOnlyDigits(event);" <%} %>
		<% if (!esEdicion) { %> readonly='readonly'<%} %>/>
<label><liferay-ui:message key="apellido" />:</label>
<input id="<portlet:namespace />cliente_apellido" name="<portlet:namespace />cliente_apellido" maxlength="50" size="15" type="text" 
	   value="<%=cliente_apellido%>" 
	   <% if (esEdicion) { %> onKeyUp="javascript:<portlet:namespace />buscarPersFisicaOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFoco();" <%} %>
	   <% if (!esEdicion) { %> readonly='readonly'<%} %>/>
<label><liferay-ui:message key="nombre" />:</label>	   
<input id="<portlet:namespace />cliente_nombre" name="<portlet:namespace />cliente_nombre" maxlength="50" size="15" type="text" 
	   value="<%=cliente_nombre%>" 
	   <% if (esEdicion) { %> onKeyUp="javascript:<portlet:namespace />buscarPersFisicaOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFoco();" <%} %>
	   <% if (!esEdicion) { %> readonly='readonly'<%} %>/>	   
</div>
<div id="<portlet:namespace />btnBuscarSeccional" style="float:left;">
	<% if (esEdicion) { %>
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarPersFisica();" tabindex="-1">
		Buscar
	</a>&nbsp;&nbsp;&nbsp;
	<%} %>
</div>
<% if (esEdicion) { %>
<div id="<portlet:namespace />btnNuevoCli" style="float:left;">
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />cargarNuevoCliente();" tabindex="-1">
		Nuevo
	</a>&nbsp;&nbsp;&nbsp;
</div>
<%} %>
<input id="<portlet:namespace />persfisica_seleccionada" name="<portlet:namespace />persfisica_seleccionada" type="hidden" value=""/>
<input id="<portlet:namespace />persfisica_tipo" name="<portlet:namespace />persfisica_tipo" type="hidden" value=""/>
<input id="<portlet:namespace />persfisica_estado" name="<portlet:namespace />persfisica_estado" type="hidden" value=""/>

<div id='divPersonaFisica' style="float:right;">
</div>
	
<script type="text/javascript">
var popupPFisi;
function <portlet:namespace />buscarPersFisica() {
	//alert ('test');
	var cliente_nro_doc=jQuery("#<portlet:namespace />cliente_nro_doc").val();
    var cliente_apellido=jQuery("#<portlet:namespace />cliente_apellido").val();
	if (!<portlet:namespace />validaForm(cliente_nro_doc,cliente_apellido)){
		return false;
	}
    popupPFisi = Liferay.Popup({title:"<liferay-ui:message key="busqueda-pers-fis" />",modal:true,width:420});
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_pers_fisica&cliente_nro_doc='+cliente_nro_doc+
    		  '&cliente_apellido='+encodeURI(cliente_apellido);
       	
	jQuery(popupPFisi).load(url);    
}
function <portlet:namespace />buscarPersFisicaOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />persfisica_seleccionada").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />cliente_apellido").val("");
		jQuery("#<portlet:namespace />cliente_nombre").val("");
		jQuery("#<portlet:namespace />cliente_nro_doc").val("");
		jQuery("#<portlet:namespace />persfisica_seleccionada").val("");
		jQuery("#<portlet:namespace />btnBuscarSeccional").show();
		return false;
	}
	var cliente_nro_doc=jQuery("#<portlet:namespace />cliente_nro_doc").val();	
    var cliente_apellido=jQuery("#<portlet:namespace />cliente_apellido").val();
    /* if((cliente_apellido.length>=3 || cliente_nro_doc.length>2) && (keyPressed!=9 && keyPressed!=16)){ */
    if((cliente_apellido.length>=4 || cliente_nro_doc.length==8) && (keyPressed!=9 && keyPressed!=16)){	
        if(cliente_nro_doc.length == 8){
        	jQuery("#<portlet:namespace />cliente_apellido").val("");
        }else{
    		jQuery("#<portlet:namespace />cliente_nro_doc").val("");
        }
        
        var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_pers_fisica&cliente_nro_doc='+cliente_nro_doc+
		  	'&cliente_apellido='+encodeURI(cliente_apellido);
        
		jQuery("#divPersonaFisica").load(url);		
		jQuery("#divPersonaFisica").show();
    }else{
        jQuery("#divPersonaFisica").hide("slow");
    }
}
function <portlet:namespace />cerrarDiv(){
	jQuery("#divPersonaFisica").hide("slow");		
}

function <portlet:namespace />cerrar(){
	<portlet:namespace />cerrarDiv();
	if(popupPFisi){		
		Liferay.Popup.close(popupPFisi);
	}	
}

function <portlet:namespace />pierdeFoco(){
	var seleccionada=jQuery("#<portlet:namespace />persfisica_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDiv();
		return false;
	}else{				
		return false; 
	}	
}

function <portlet:namespace />validaForm(cliente_nro_doc, cliente_apellido){
	 if(trim(cliente_nro_doc).length==0 && trim(cliente_apellido).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValid() {
	if (jQuery("#<portlet:namespace />cliente_nro_doc").val() != "") {
		jQuery("#<portlet:namespace />persfisica_seleccionada").val("1")
	}
}

<portlet:namespace />resetValid();
function <portlet:namespace />cerrarDivPersFis<%=prefijo%>(){
	jQuery("#divPersonaFisica<%=prefijo%>").hide("slow");		
}
function <portlet:namespace />cerrarPFisi<%=prefijo%>(){	
	<portlet:namespace />cerrarDivPersFis<%=prefijo%>();
	if(popupPFisi){		
		Liferay.Popup.close(popupPFisi);
	}
}


var popupNuevoCliente;

function <portlet:namespace />cargarNuevoCliente() {

	var nroDoc= jQuery("#<portlet:namespace />cliente_nro_doc").val();
	
	popupNuevoCliente= new Liferay.Popup({title:"<liferay-ui:message key="nuevo-cli" />",modal:true, width: 650, height: 150, position:['center',30]});
//	var urlCierre = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/cargar_nuevo_cliente';
	
	var urlCierre = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/cargar_nuevo_cliente';
	
	urlCierre = urlCierre +'&nroDocumento='+nroDoc;
	
	jQuery(popupNuevoCliente).load(urlCierre); 
				
}

function <portlet:namespace />grabarCliente(){
	
	
	
	var nroDoc = jQuery("#<portlet:namespace />cliente_nro_doc_popup").val();
	var apellido = jQuery("#<portlet:namespace />cliente_apellido_popup").val();
	var nombre = jQuery("#<portlet:namespace />cliente_nombre_popup").val();
	var tipo = jQuery("#<portlet:namespace />persfisica_tipo_popup").val();
	var estado = jQuery("#<portlet:namespace />persfisica_estado_popup").val();
	
    jQuery("#<portlet:namespace />cliente_nro_doc<%=prefijo!=null?prefijo:""%>").val(nroDoc);
    jQuery("#<portlet:namespace />cliente_apellido<%=prefijo!=null?prefijo:""%>").val(apellido);
    jQuery("#<portlet:namespace />cliente_nombre<%=prefijo!=null?prefijo:""%>").val(nombre);
    jQuery("#<portlet:namespace />persfisica_tipo<%=prefijo!=null?prefijo:""%>").val(tipo);
    jQuery("#<portlet:namespace />persfisica_estado<%=prefijo!=null?prefijo:""%>").val(estado);
    jQuery("#<portlet:namespace />persfisica_seleccionada<%=prefijo!=null?prefijo:""%>").val("1");


  
    Liferay.Popup.close(popupNuevoCliente);

	/* jQuery('#<portlet:namespace />busquedaCorrespondenciaInboxDiv').load(url,params, function() {
    		Liferay.Popup.close(popupNuevoCliente);          															
    		 });	 */
    
}
</script>