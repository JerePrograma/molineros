<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%
	String id_patologia = ParamUtil.getString(request, "id_patologia");
	String patologia = ParamUtil.getString(request, "patologia");
	String prefijo = ParamUtil.getString(request, "prefijo","");
%>
<table>
	<tr>
		<td>
			<input id="<portlet:namespace />patologiaSeguimiento<%=prefijo%>"
				name="<portlet:namespace />patologiaSeguimiento<%=prefijo%>" maxlenght="15" size="5"
				type="text" value="<%=id_patologia%>"
				onBlur="javascript:<portlet:namespace />pierdeFocoPatologia<%=prefijo%>();"
				onKeyUp="javascript:<portlet:namespace />buscarPatologiaOnDiv<%=prefijo%>(event)" />
		</td>
		<td>&nbsp;&nbsp;</td>				
		<td>
			<input id="<portlet:namespace />patologia<%=prefijo%>"
				name="<portlet:namespace />patologia<%=prefijo%>" size="50" type="text"
				value="<%=patologia%>"
				onKeyUp="javascript:<portlet:namespace />buscarPatologiaOnDiv<%=prefijo%>(event)"
				onBlur="javascript:<portlet:namespace />pierdeFocoPatologia<%=prefijo%>();" />
		</td>
		<td>&nbsp;		
			<div id="<portlet:namespace />btnBuscarPatologia<%=prefijo%>" style="float: left;">
				<a href="javascript: void(0);" 
					onclick="javascript:<portlet:namespace />buscarPatologia<%=prefijo%>();" tabindex="-1"> Buscar</a></div>
		</td>
	</tr>	
</table>
<input id="<portlet:namespace />patologia_seleccionada<%=prefijo%>"
	name="<portlet:namespace />patologia_seleccionada<%=prefijo%>" type="hidden" value="" />
<div id='divPatologia<%=prefijo%>' style="float: right;"></div>

<script type="text/javascript">
var popup;

function <portlet:namespace />buscarPatologia<%=prefijo%>() {
	var id_patologia=jQuery("#<portlet:namespace />patologiaSeguimiento<%=prefijo%>").val();	
    var patologia=jQuery("#<portlet:namespace />patologia<%=prefijo%>").val();
	if (!<portlet:namespace />validaFormPatologia<%=prefijo%>(id_patologia,patologia)){
		return false;
	}
	
    popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-patologia" />",modal:true,width:420});
       
    var url = '';

	<c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_AUT_1_"))%>'> 
       url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_patologia&id_patologia='+id_patologia+
	  '&patologia='+encodeURI(patologia)+'&prefijo=<%=prefijo%>';
	</c:if>
	jQuery(popup).load(url);    
}

function <portlet:namespace />buscarPatologiaOnDiv<%=prefijo%>(e){	
	//Se modificó el campo, debemos cambiar el selecc
	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />patologia_seleccionada<%=prefijo%>").val() == "1"  && (keyPressed!=9 && keyPressed!=16)){
		jQuery("#<portlet:namespace />patologia<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />patologiaSeguimiento<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />patologia_seleccionada<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />btnBuscarPatologia<%=prefijo%>").show();
		return false;
	}
	var id_patologia=jQuery("#<portlet:namespace />patologiaSeguimiento<%=prefijo%>").val();
    var patologia=jQuery("#<portlet:namespace />patologia<%=prefijo%>").val();
    
    if((patologia.length>=5 || id_patologia.length>2) && (keyPressed!=9 && keyPressed!=16)){
        if(id_patologia.length >2){
        	jQuery("#<portlet:namespace />patologia<%=prefijo%>").val("");
        }else{
    		jQuery("#<portlet:namespace />patologiaSeguimiento<%=prefijo%>").val("");
        }
        
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_patologia&id_patologia='+id_patologia +
		  '&patologia='+encodeURI(patologia)+'&prefijo=<%=prefijo%>';
		jQuery("#divPatologia<%=prefijo%>").load(url);		
		jQuery("#divPatologia<%=prefijo%>").show();
    }else{        
    	jQuery("#divPatologia<%=prefijo%>").hide("slow");
    }     
}
function <portlet:namespace />cerrarDivPatologia<%=prefijo%>(){
	jQuery("#divPatologia<%=prefijo%>").hide("slow");		
}
function <portlet:namespace />cerrarPatologia<%=prefijo%>(){	
	<portlet:namespace />cerrarDivPatologia<%=prefijo%>();
	if(popup){		
		Liferay.Popup.close(popup);
	}
}
function <portlet:namespace />pierdeFocoPatologia<%=prefijo%>(){
	var seleccionada=jQuery("#<portlet:namespace />patologia_seleccionada<%=prefijo%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivPatologia<%=prefijo%>();
		return false;
	}else{				
		return false; 
	}
}
function <portlet:namespace />validaFormPatologia<%=prefijo%>(id_patologia, patologia){
	 if(trim(id_patologia).length==0 && trim(patologia).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValidPatologia<%=prefijo%>(){
	if (jQuery("#<portlet:namespace />patologiaSeguimiento<%=prefijo%>").val() != "") {
		jQuery("#<portlet:namespace />patologia_seleccionada<%=prefijo%>").val("1")
	}
}

<portlet:namespace />resetValidPatologia<%=prefijo%>();
</script>