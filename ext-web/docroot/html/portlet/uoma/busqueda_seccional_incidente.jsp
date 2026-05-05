<%@ include file="/html/portlet/uoma/init.jsp" %>

<%
String prefijo= request.getParameter("prefijo");
StringBuffer aux_secc=new StringBuffer("seccional_r");
StringBuffer aux_idsecc=new StringBuffer("id_seccional_r");
aux_secc.append(prefijo!=null?prefijo:"");
aux_idsecc.append(prefijo!=null?prefijo:"");

String id_seccional_r=ParamUtil.getString(request,aux_idsecc.toString());
String seccional_r=ParamUtil.getString(request,aux_secc.toString());
%>
<input  id="<portlet:namespace />id_seccional_r<%=prefijo!=null?prefijo:""%>" name="<portlet:namespace />id_seccional_r<%=prefijo!=null?prefijo:""%>" maxlenght="4" size="4" type="text" value="<%=id_seccional_r%>" 
		onBlur="javascript:<portlet:namespace />pierdeFocoSeccInci<%=prefijo!=null?prefijo:""%>();" onKeyUp="javascript:<portlet:namespace />buscarSeccionalIncidenteOnDiv<%=prefijo!=null?prefijo:""%>(event)"/>
<input id="<portlet:namespace />seccional_r<%=prefijo!=null?prefijo:""%>" name="<portlet:namespace />seccional_r<%=prefijo!=null?prefijo:""%>" size="15" type="text" 
	   value="<%=seccional_r%>" onKeyUp="javascript:<portlet:namespace />buscarSeccionalIncidenteOnDiv<%=prefijo!=null?prefijo:""%>(event)" onBlur="javascript:<portlet:namespace />pierdeFocoSeccRein<%=prefijo!=null?prefijo:""%>();"/>
<input id="<portlet:namespace />secc_seleccionada_r<%=prefijo!=null?prefijo:""%>" name="<portlet:namespace />secc_seleccionada_r<%=prefijo!=null?prefijo:""%>" type="hidden" value=""/>
<div id="<portlet:namespace />btnBuscarSeccional_r<%=prefijo!=null?prefijo:""%>" style="float:left;">
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarSeccionalIncidente<%=prefijo!=null?prefijo:""%>();" tabindex="-1">
		Buscar			
	</a>
</div>
<div id='divSeccional_r<%=prefijo!=null?prefijo:""%>' style="float:right;">
</div>
	
<script type="text/javascript">
var popupRein;
function <portlet:namespace />buscarSeccionalIncidente<%=prefijo!=null?prefijo:""%>() {
	var id_seccional_r=jQuery("#<portlet:namespace />id_seccional_r<%=prefijo!=null?prefijo:""%>").val();
    var seccional_r=jQuery("#<portlet:namespace />seccional_r<%=prefijo!=null?prefijo:""%>").val();
	if (!<portlet:namespace />validaFormSecc(id_seccional_r,seccional_r)){
		return false;
	}
    popupRein = Liferay.Popup({title:"<liferay-ui:message key="busqueda-seccionales" />",modal:true,width:420});        
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_seccional_incidentes&id_seccional='+id_seccional_r+
    		  '&seccional='+encodeURI(seccional_r);   	
	jQuery(popupRein).load(url);    
}

function <portlet:namespace />buscarSeccionalIncidenteOnDiv<%=prefijo!=null?prefijo:""%>(e){
	//Se modificó el campo, debemos cambiar el selecc
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />secc_seleccionada_r<%=prefijo!=null?prefijo:""%>").val() == "1" && (keyPressed==8 || keyPressed==46)){		
		jQuery("#<portlet:namespace />seccional_r<%=prefijo!=null?prefijo:""%>").val("");
		jQuery("#<portlet:namespace />id_seccional_r<%=prefijo!=null?prefijo:""%>").val("");
		jQuery("#<portlet:namespace />secc_seleccionada_r<%=prefijo!=null?prefijo:""%>").val("");
		jQuery("#<portlet:namespace />btnBuscarSeccional_r<%=prefijo!=null?prefijo:""%>").show();
		return false;
	}
	var id_seccional_r=jQuery("#<portlet:namespace />id_seccional_r<%=prefijo!=null?prefijo:""%>").val();	
    var seccional_r=jQuery("#<portlet:namespace />seccional_r<%=prefijo!=null?prefijo:""%>").val();
    if((keyPressed!=9 && keyPressed!=16) && (seccional_r.length>=3 || id_seccional_r.length>2)){
        if(id_seccional_r.length >2){
        	jQuery("#<portlet:namespace />seccional_r<%=prefijo!=null?prefijo:""%>").val("");
        }else{
    		jQuery("#<portlet:namespace />id_seccional_r<%=prefijo!=null?prefijo:""%>").val("");
        }        
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_seccional_incidentes&id_seccional='+id_seccional_r+
		  '&prefijo=<%=prefijo!=null?prefijo:""%>&seccional='+encodeURI(seccional_r);
		jQuery("#divSeccional_r<%=prefijo!=null?prefijo:""%>").load(url);		
		jQuery("#divSeccional_r<%=prefijo!=null?prefijo:""%>").show();
    }else{
    	jQuery("#divSeccional_r<%=prefijo!=null?prefijo:""%>").hide("slow");
    }    
}

function <portlet:namespace />cerrarDivSeccRein<%=prefijo!=null?prefijo:""%>(){
	jQuery("#divSeccional_r<%=prefijo!=null?prefijo:""%>").hide("slow");		
}

function <portlet:namespace />cerrarSeccRein<%=prefijo!=null?prefijo:""%>(){
	<portlet:namespace />cerrarDivSeccRein<%=prefijo!=null?prefijo:""%>();
	if(popupRein){		
		Liferay.Popup.close(popupRein);
	}	
}

function <portlet:namespace />pierdeFocoSeccInci<%=prefijo!=null?prefijo:""%>(){
	var seleccionada=jQuery("#<portlet:namespace />secc_seleccionada_r<%=prefijo!=null?prefijo:""%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivSeccRein<%=prefijo!=null?prefijo:""%>();
		return false;
	}else{				
		return false; 
	}	
}

function <portlet:namespace />validaFormSeccRein<%=prefijo!=null?prefijo:""%>(id_seccional_r, seccional_r){
	 if(trim(id_seccional_r).length==0 && trim(seccional_r).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValidSeccRein<%=prefijo!=null?prefijo:""%>() {
	if (jQuery("#<portlet:namespace />id_seccional_r").val() != "") {
		jQuery("#<portlet:namespace />secc_seleccionada_r").val("1")
	}
}

<portlet:namespace />resetValidSeccRein<%=prefijo!=null?prefijo:""%>();
</script>