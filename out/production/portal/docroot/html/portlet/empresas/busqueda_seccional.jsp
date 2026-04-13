<%@ include file="/html/portlet/empresas/init.jsp" %>

<%

String esEdicionStr=ParamUtil.getString(request,"esEdicion");
boolean esEdicion = true;
if (esEdicionStr != null && !esEdicionStr.trim().equals("")) {
	if (esEdicionStr.equals("true")){
		esEdicion = true;
	} else {
		esEdicion = false;
	}
}
String id_seccional=ParamUtil.getString(request,"id_seccional");
String seccional=ParamUtil.getString(request,"seccional");
String prefijo=ParamUtil.getString(request,"prefijo","");
%>

<input  id="<portlet:namespace />id_seccional<%=prefijo%>" name="<portlet:namespace />id_seccional<%=prefijo%>" maxlenght="4" size="4" type="text" value="<%=id_seccional%>" 
		<% if (esEdicion) { %> onBlur="javascript:<portlet:namespace />pierdeFoco();" onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDiv(event)" <%} %>
		<% if (!esEdicion) { %> readonly='readonly'<%} %>/>
<input id="<portlet:namespace />seccional<%=prefijo%>" name="<portlet:namespace />seccional<%=prefijo%>" size="15" type="text" 
	   value="<%=seccional%>" 
	   <% if (esEdicion) { %> onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDiv(event)" onBlur="javascript:<portlet:namespace />pierdeFoco();" <%} %>
	   <% if (!esEdicion) { %> readonly='readonly'<%} %>/>
<div id="<portlet:namespace />btnBuscarSeccional" style="float:right;">
	<% if (esEdicion) { %>
	<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarSeccional();" tabindex="-1">
		Buscar
	</a>
	<%} %>
</div>
<input id="<portlet:namespace />secc_seleccionada<%=prefijo%>" name="<portlet:namespace />secc_seleccionada<%=prefijo%>" type="hidden" value=""/>
<div id='divSeccional' style="float:right;">
</div>
	
<script type="text/javascript">
<% if (null!=id_seccional && !id_seccional.trim().equals("") && !id_seccional.trim().equals("0")){%>	
	jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val("<%=id_seccional%>");	
	<portlet:namespace />buscarSeccional();
<%}%>
var popupSecc;

function <portlet:namespace />buscarSeccional() {
	var id_seccional=jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val();	
    var seccional=jQuery("#<portlet:namespace />seccional<%=prefijo%>").val();
	if (!<portlet:namespace />validaForm(id_seccional,seccional)){
		return false;
	}	
    popupSecc = Liferay.Popup({title:"<liferay-ui:message key="busqueda-seccionales" />",modal:true,width:420});    
    //BUSCA DESTINO EN LIQ
    <%if(portlet_name.equals("liquidaciones")){%>    	
    	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_seccional_emp&id_seccional='+id_seccional+
	  	'&seccional='+encodeURI(seccional)+'&prefijo=<%=prefijo%>';    
    <%}else{%>    	
    	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_seccional&id_seccional='+id_seccional+
    		  '&seccional='+encodeURI(seccional)+'&prefijo=<%=prefijo%>';
    <%}%>   	
	jQuery(popupSecc).load(url);    
}
function <portlet:namespace />buscarSeccionalOnDiv(e){	
	//Se modificó el campo, debemos cambiar el selecc	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />seccional<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />btnBuscarSeccional").show();
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
		  	'&seccional='+encodeURI(seccional)+'&prefijo=<%=prefijo%>';        
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
	if(popupSecc){		
		Liferay.Popup.close(popupSecc);
	}	
}


function <portlet:namespace />cerrarDivSecc<%=prefijo%>(){
	jQuery("#divSeccional").hide("slow");
}
function <portlet:namespace />cerrarSecc<%=prefijo%>(){	
	<portlet:namespace />cerrarDiv();
	if(popupSecc){		
		Liferay.Popup.close(popupSecc);
	}	
}

function <portlet:namespace />pierdeFoco(){
	var seleccionada=jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val();	
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

function <portlet:namespace />resetValid() {
	if (jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val() != "") {
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1")
	}
}

<portlet:namespace />resetValid();
</script>