<%@ include file="/html/portlet/farmacia/init.jsp" %>

<%
String id_seccional=ParamUtil.getString(request,"id_seccional");
String seccional=ParamUtil.getString(request,"seccional");
String prefijo=ParamUtil.getString(request,"prefijo");

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_LIQ_1_")){
	portlet_name = "liquidaciones";
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

if(renderResponse.getNamespace().equals("_AUT_1_")){
	portlet_name = "autorizaciones";
}
if(renderResponse.getNamespace().equals("_FOS_1_")){
	portlet_name = "farmaciaospim";
}
 
%>

<input  id="<portlet:namespace />id_seccional<%=prefijo%>" name="<portlet:namespace />id_seccional<%=prefijo%>" maxlenght="4" size="4" type="text" value="<%=id_seccional%>" 
		onBlur="javascript:<portlet:namespace />pierdeFocoSecc();" onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDiv(event)"/>
<input id="<portlet:namespace />seccional<%=prefijo%>" name="<portlet:namespace />seccional<%=prefijo%>" size="15" type="text" 
	   value="<%=seccional%>" onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFocoSecc();"/>
<div id="<portlet:namespace />btnBuscarSeccional<%=prefijo%>" style="float:left;">
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarSeccional();" tabindex="-1">
		Buscar
	</a>
</div>
<input id="<portlet:namespace />secc_seleccionada<%=prefijo%>" name="<portlet:namespace />secc_seleccionada<%=prefijo%>" type="hidden" value=""/>
<div id='divSeccional<%=prefijo%>' style="float:right;">
</div>
	
<script type="text/javascript">
var popup;
function <portlet:namespace />buscarSeccional() {
	var id_seccional=jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val();
    var seccional=jQuery("#<portlet:namespace />seccional<%=prefijo%>").val();
	if (!<portlet:namespace />validaFormSecc(id_seccional,seccional)){
		return false;
	}
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-seccionales" />",modal:true,width:420});
        
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_seccional&id_seccional='+id_seccional+
	  '&seccional='+encodeURI(seccional);
    
//    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmacia/buscar_seccional&id_seccional='+id_seccional+
//    		  '&seccional='+encodeURI(seccional);  


	jQuery(popup).load(url);    
}
function <portlet:namespace />buscarSeccionalOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc
	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val() == "1"  && (keyPressed!=9 && keyPressed!=16)){
		jQuery("#<portlet:namespace />seccional<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />btnBuscarSeccional<%=prefijo%>").show();
		return false;
	}
	var id_seccional=jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val();
    var seccional=jQuery("#<portlet:namespace />seccional<%=prefijo%>").val();
    if((seccional.length>=3 || id_seccional.length>2) && (keyPressed!=9 && keyPressed!=16)){
        if(id_seccional.length >2){
        	jQuery("#<portlet:namespace />seccional<%=prefijo%>").val("");
        }else{
    		jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val("");
        }
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_seccional&id_seccional='+id_seccional+
		  '&seccional='+encodeURI(seccional);
			
//		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/farmacia/buscar_seccional&id_seccional='+id_seccional+
//		  '&seccional='+encodeURI(seccional);

		jQuery("#divSeccional<%=prefijo%>").load(url);		
		jQuery("#divSeccional<%=prefijo%>").show();
    }else{        
    	jQuery("#divSeccional<%=prefijo%>").hide("slow");
    }     
}
function <portlet:namespace />cerrarDivSecc(){
	jQuery("#divSeccional<%=prefijo%>").hide("slow");		
}
function <portlet:namespace />cerrarSecc(){	
	<portlet:namespace />cerrarDivSecc();
	if(popup){		
		Liferay.Popup.close(popup);
	}
}
function <portlet:namespace />pierdeFocoSecc(){
	var seleccionada=jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivSecc();
		return false;
	}else{				
		return false; 
	}
}
function <portlet:namespace />validaFormSecc(id_seccional, seccional){
	 if(trim(id_seccional).length==0 && trim(seccional).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValidSecc() {
	if (jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val() != "") {
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1")
	}
}

<portlet:namespace />resetValidSecc();
</script>