<%@ include file="/html/portlet/comprobantes/init.jsp"%>

<%
	String id_seccional = ParamUtil.getString(request, "id_seccional");
	String seccional = ParamUtil.getString(request, "seccional");
	String prefijo = ParamUtil.getString(request, "prefijo","");
%>
<table>
	<tr>
		<td>
			<input id="<portlet:namespace />id_seccional<%=prefijo%>"
				name="<portlet:namespace />id_seccional<%=prefijo%>" maxlength="4" size="4"
				type="text" value="<%=id_seccional%>"
				onBlur="javascript:<portlet:namespace />pierdeFocoSecc<%=prefijo%>();"
				onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDiv<%=prefijo%>(event)" />
		</td>
		<td>&nbsp;&nbsp;</td>				
		<td>
			<input id="<portlet:namespace />seccional<%=prefijo%>"
				name="<portlet:namespace />seccional<%=prefijo%>" size="15" type="text"
				value="<%=seccional%>"
				onKeyUp="javascript:<portlet:namespace />buscarSeccionalOnDiv<%=prefijo%>(event)"
				onBlur="javascript:<portlet:namespace />pierdeFocoSecc<%=prefijo%>();" />
		</td>
		<td>&nbsp;		
			<div id="<portlet:namespace />btnBuscarSeccional<%=prefijo%>" style="float: left;">
				<a href="javascript: void(0);" 
					onclick="javascript:<portlet:namespace />buscarSeccional<%=prefijo%>();" tabindex="-1"> Buscar</a></div>
		</td>
	</tr>	
</table>
<input id="<portlet:namespace />secc_seleccionada<%=prefijo%>"
	name="<portlet:namespace />secc_seleccionada<%=prefijo%>" type="hidden" value="" />
<div id='divSeccional<%=prefijo%>' style="float: right;"></div>

<script type="text/javascript">

var popup;

function <portlet:namespace />buscarSeccional<%=prefijo%>() {

	var id_seccional=jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val();	
    var seccional=jQuery("#<portlet:namespace />seccional<%=prefijo%>").val();
	if (!<portlet:namespace />validaFormSecc<%=prefijo%>(id_seccional,seccional)){
		return false;
	}
	
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-seccionales" />",modal:true,width:420});
       
    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/comprobantes/buscar_seccional&id_seccional='+id_seccional+
    		  '&seccional='+encodeURI(seccional)+'&prefijo=<%=prefijo%>';

	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COM_1_"))%>'> 
       url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/comprobantes/buscar_seccional&id_seccional='+id_seccional+
	  '&seccional='+encodeURI(seccional)+'&prefijo=<%=prefijo%>';
	</c:if>
	
	jQuery(popup).load(url);    
}
function <portlet:namespace />buscarSeccionalOnDiv<%=prefijo%>(e){	
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
        
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/comprobantes/buscar_seccional&id_seccional='+id_seccional+
		  '&seccional='+encodeURI(seccional)+'&prefijo=<%=prefijo%>';
		<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_COM_1_"))%>'> 
	       url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/comprobantes/buscar_seccional&id_seccional='+id_seccional+
			  '&seccional='+encodeURI(seccional)+'&prefijo=<%=prefijo%>';
	 	</c:if>
		jQuery("#divSeccional<%=prefijo%>").load(url);		
		jQuery("#divSeccional<%=prefijo%>").show();
    }else{        
    	jQuery("#divSeccional<%=prefijo%>").hide("slow");
    }     
}
function <portlet:namespace />cerrarDivSecc<%=prefijo%>(){
	jQuery("#divSeccional<%=prefijo%>").hide("slow");		
}
function <portlet:namespace />cerrarSecc<%=prefijo%>(){	
	<portlet:namespace />cerrarDivSecc<%=prefijo%>();
	if(popup){		
		Liferay.Popup.close(popup);
	}
}
function <portlet:namespace />pierdeFocoSecc<%=prefijo%>(){
	var seleccionada=jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivSecc<%=prefijo%>();
		return false;
	}else{				
		return false; 
	}
}
function <portlet:namespace />validaFormSecc<%=prefijo%>(id_seccional, seccional){
	 if(trim(id_seccional).length==0 && trim(seccional).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValidSecc<%=prefijo%>(){
	if (jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val() != "") {
		jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1")
	}
}

<portlet:namespace />resetValidSecc<%=prefijo%>();
</script>