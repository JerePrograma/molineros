<%@ include file="/html/portlet/uoma/init.jsp" %>

<%
String id_seccional=ParamUtil.getString(request,"id_seccional");
String seccional=ParamUtil.getString(request,"seccional");
String prefijo= ParamUtil.getString(request,"prefijo","");
%>

<input  id="<portlet:namespace />id_seccional_afiliado<%=prefijo%>" name="<portlet:namespace />id_seccional_afiliado<%=prefijo%>" maxlenght="4" size="4" type="text" value="<%=id_seccional%>" 
		onBlur="javascript:<portlet:namespace />pierdeFocoSeccAfiliado();" onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDivAfiliado(event)"/>		
<input id="<portlet:namespace />seccional_afiliado<%=prefijo%>" name="<portlet:namespace />seccional_afiliado<%=prefijo%>" size="15" type="text" 
	   value="<%=seccional%>" onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDivAfiliado(event)" onBlur="javascript:<portlet:namespace />pierdeFocoSeccAfiliado();"/>
<div id="<portlet:namespace />btnBuscarSeccionalAfiliado" style="float:left;">
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarSeccionalAfiliado();" tabindex="-1">
		Buscar
	</a>
</div>
<input id="<portlet:namespace />secc_seleccionada_afiliado<%=prefijo%>" name="<portlet:namespace />secc_seleccionada_afiliado<%=prefijo%>" type="hidden" value=""/>
<div id='divSeccionalAfiliado<%=prefijo%>' style="float:right;">
</div>
	
<script type="text/javascript">
var popup;
function <portlet:namespace />buscarSeccionalAfiliado() {
	var id_seccional=jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").val();
    var seccional=jQuery("#<portlet:namespace />seccional_afiliado<%=prefijo%>").val();
	if (!<portlet:namespace />validaFormSeccAfiliado(id_seccional,seccional)){
		return false;
	}
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-seccionales" />",modal:true,width:420});        
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_seccional&id_seccional='+id_seccional+
    		  '&seccional='+encodeURI(seccional);   	
	jQuery(popup).load(url);    
}
function <portlet:namespace />buscarSeccionalOnDivAfiliado(e){
	//Se modificó el campo, debemos cambiar el selecc
	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />secc_seleccionada_afiliado<%=prefijo%>").val() == "1"  && (keyPressed!=9 && keyPressed!=16)){
		jQuery("#<portlet:namespace />seccional_afiliado<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />secc_seleccionada_afiliado<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />btnBuscarSeccionalAfiliado<%=prefijo%>").show();
		return false;
	}
	var id_seccional=jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").val();
    var seccional=jQuery("#<portlet:namespace />seccional_afiliado<%=prefijo%>").val();
    if((seccional.length>=3 || id_seccional.length>2) && (keyPressed!=9 && keyPressed!=16)){
        if(id_seccional.length >2){
        	jQuery("#<portlet:namespace />seccional_afiliado<%=prefijo%>").val("");
        }else{
    		jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").val("");
        }
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_seccional&id_seccional='+id_seccional+
		  '&seccional='+encodeURI(seccional);
		jQuery("#divSeccionalAfiliado<%=prefijo%>").load(url);		
		jQuery("#divSeccionalAfiliado<%=prefijo%>").show();
    }else{        
    	jQuery("#divSeccionalAfiliado<%=prefijo%>").hide("slow");
    }     
}
function <portlet:namespace />cerrarDivSeccAfiliado(){
	jQuery("#divSeccionalAfiliado<%=prefijo%>").hide("slow");		
}
function <portlet:namespace />cerrarSeccAfiliado(){	
	<portlet:namespace />cerrarDivSeccAfiliado();
	if(popup){		
		Liferay.Popup.close(popup);
	}
}
function <portlet:namespace />pierdeFocoSeccAfiliado(){
	var seleccionada=jQuery("#<portlet:namespace />secc_seleccionada_afiliado<%=prefijo%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivSeccAfiliado();
		return false;
	}else{				
		return false; 
	}
}
function <portlet:namespace />validaFormSeccAfiliado(id_seccional, seccional){
	 if(trim(id_seccional).length==0 && trim(seccional).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValidSeccAfiliado() {
	
	if (jQuery("#<portlet:namespace />id_seccional_afiliado<%=prefijo%>").val() != "") {
		jQuery("#<portlet:namespace />secc_seleccionada_afiliado<%=prefijo%>").val("1")
	}
}

</script>