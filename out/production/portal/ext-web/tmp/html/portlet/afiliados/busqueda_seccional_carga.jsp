<%@ include file="/html/portlet/afiliados/init.jsp" %>
<input  id="<portlet:namespace />id_seccional" name="<portlet:namespace />id_seccional" maxlenght="4" size="4" type="text" value="" 
		onBlur="javascript:<portlet:namespace />pierdeFoco();" onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDiv(event)"/>
<input id="<portlet:namespace />seccional" name="<portlet:namespace />seccional" size="15" type="text" 
	   value="" onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFoco();"/>
<div id="<portlet:namespace />btnBuscarSeccional" style="float:right;">
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarSeccional();" tabindex="-1">
		Buscar			
	</a>
</div>
<input id="<portlet:namespace />secc_seleccionada" name="<portlet:namespace />secc_seleccionada" type="hidden" value=""/>
<div id='divSeccional' style="float:right;">
</div>
	
<script type="text/javascript">
var popup;
function <portlet:namespace />buscarSeccional() {
	var id_seccional=jQuery("#<portlet:namespace />id_seccional").val();
    var seccional=jQuery("#<portlet:namespace />seccional").val();
	if (!<portlet:namespace />validaForm(id_seccional,seccional)){
		return false;
	}
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-seccionales" />",modal:true,width:420});        
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_seccional_carga&id_seccional='+id_seccional+
    		  '&seccional='+encodeURI(seccional);   	
	jQuery(popup).load(url);    
}
function <portlet:namespace />buscarSeccionalOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />secc_seleccionada").val() == "1" && (keyPressed==8 || keyPressed==46)){		
		jQuery("#<portlet:namespace />seccional").val("");
		jQuery("#<portlet:namespace />id_seccional").val("");
		jQuery("#<portlet:namespace />secc_seleccionada").val("");
		jQuery("#<portlet:namespace />btnBuscarSeccional").show();
		return false;
	}
	var id_seccional=jQuery("#<portlet:namespace />id_seccional").val();	
    var seccional=jQuery("#<portlet:namespace />seccional").val();
    if((keyPressed!=9 && keyPressed!=16) && (seccional.length>=3 || id_seccional.length>2)){        
        if(id_seccional.length >2){
        	jQuery("#<portlet:namespace />seccional").val("");
        }else{
    		jQuery("#<portlet:namespace />id_seccional").val("");
        }        
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_seccional_carga&id_seccional='+id_seccional+
		  '&seccional='+encodeURI(seccional);
		jQuery("#divSeccional").load(url);		
		jQuery("#divSeccional").show();
    }else{        
    	jQuery("#divSeccional").hide("slow");
    }
    
}
function <portlet:namespace />cerrarDiv(){
	jQuery("#divSeccional").hide("slow");		
}
function <portlet:namespace />cerrar(){	
	<portlet:namespace />cerrarDiv();
	if(popup){		
		Liferay.Popup.close(popup);
	}	
}
function <portlet:namespace />pierdeFoco(){
	var seleccionada=jQuery("#<portlet:namespace />secc_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDiv();
		return false;
	}else{				
		return false; 
	}	
}
function <portlet:namespace />validaForm(id_seccional, seccional){
	 if(trim(id_seccional).length==0 && trim(seccional).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}
</script>