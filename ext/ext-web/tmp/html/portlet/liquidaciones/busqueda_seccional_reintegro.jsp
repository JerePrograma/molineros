<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%
String id_seccional_r=ParamUtil.getString(request,"id_seccional_r");
String seccional_r=ParamUtil.getString(request,"seccional_r");
%>
<input  id="<portlet:namespace />id_seccional_r" name="<portlet:namespace />id_seccional_r" maxlenght="4" size="4" type="text" value="<%=id_seccional_r%>" 
		onBlur="javascript:<portlet:namespace />pierdeFocoSeccRein();" onKeyUp="javascript:<portlet:namespace />buscarSeccionalReintegroOnDiv(event)"/>
<input id="<portlet:namespace />seccional_r" name="<portlet:namespace />seccional_r" size="15" type="text" 
	   value="<%=seccional_r%>" onKeyUp="javascript:<portlet:namespace />buscarSeccionalReintegroOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFocoSeccRein();"/>
<input id="<portlet:namespace />secc_seleccionada_r" name="<portlet:namespace />secc_seleccionada_r" type="hidden" value=""/>
<div id="<portlet:namespace />btnBuscarSeccional_r" style="float:left;">
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarSeccionalReintegro();" tabindex="-1">
		Buscar			
	</a>
</div>
<div id='divSeccional_r' style="float:right;">
</div>
	
<script type="text/javascript">
var popupRein;
function <portlet:namespace />buscarSeccionalReintegro() {
	var id_seccional_r=jQuery("#<portlet:namespace />id_seccional_r").val();
    var seccional_r=jQuery("#<portlet:namespace />seccional_r").val();
	if (!<portlet:namespace />validaFormSecc(id_seccional_r,seccional_r)){
		return false;
	}
    popupRein = Liferay.Popup({title:"<liferay-ui:message key="busqueda-seccionales" />",modal:true,width:420});        

    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_seccional_reintegros&id_seccional='+id_seccional_r+
    		  '&seccional='+encodeURI(seccional_r);

    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'> 
	    url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_seccional_reintegros&id_seccional='+id_seccional_r+
		  '&seccional='+encodeURI(seccional_r);
    </c:if>
	     	
	jQuery(popupRein).load(url);    
}

function <portlet:namespace />buscarSeccionalReintegroOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />secc_seleccionada_r").val() == "1" && (keyPressed==8 || keyPressed==46)){		
		jQuery("#<portlet:namespace />seccional_r").val("");
		jQuery("#<portlet:namespace />id_seccional_r").val("");
		jQuery("#<portlet:namespace />secc_seleccionada_r").val("");
		jQuery("#<portlet:namespace />btnBuscarSeccional_r").show();
		return false;
	}
	var id_seccional_r=jQuery("#<portlet:namespace />id_seccional_r").val();	
    var seccional_r=jQuery("#<portlet:namespace />seccional_r").val();
    if((keyPressed!=9 && keyPressed!=16) && (seccional_r.length>=3 || id_seccional_r.length>2)){
        if(id_seccional_r.length >2){
        	jQuery("#<portlet:namespace />seccional_r").val("");
        }else{
    		jQuery("#<portlet:namespace />id_seccional_r").val("");
        }        
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/buscar_seccional_reintegros&id_seccional='+id_seccional_r+
		  '&seccional='+encodeURI(seccional_r);

		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COR_1_"))%>'>
			url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/correspondencia/buscar_seccional_reintegros&id_seccional='+id_seccional_r+
		  	'&seccional='+encodeURI(seccional_r);
		</c:if>
		
		jQuery("#divSeccional_r").load(url);		
		jQuery("#divSeccional_r").show();
    }else{
    	jQuery("#divSeccional_r").hide("slow");
    }    
}

function <portlet:namespace />cerrarDivSeccRein(){
	jQuery("#divSeccional_r").hide("slow");		
}

function <portlet:namespace />cerrarSeccRein(){
	<portlet:namespace />cerrarDivSeccRein();
	if(popupRein){		
		Liferay.Popup.close(popupRein);
	}	
}

function <portlet:namespace />pierdeFocoSeccRein(){
	var seleccionada=jQuery("#<portlet:namespace />secc_seleccionada_r").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivSeccRein();
		return false;
	}else{				
		return false; 
	}	
}

function <portlet:namespace />validaFormSeccRein(id_seccional_r, seccional_r){
	 if(trim(id_seccional_r).length==0 && trim(seccional_r).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValidSeccRein() {
	if (jQuery("#<portlet:namespace />id_seccional_r").val() != "") {
		jQuery("#<portlet:namespace />secc_seleccionada_r").val("1")
	}
}

<portlet:namespace />resetValidSeccRein();
</script>