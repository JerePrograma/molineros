<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%
	String id_diagnostico = ParamUtil.getString(request, "id_diagnostico");
	String descripDiag = ParamUtil.getString(request, "descripDiag");
	String prefijo = ParamUtil.getString(request, "prefijo","");
%>
<table>
	<tr>
		<td>
			<input id="<portlet:namespace />id_diagnostico<%=prefijo%>"
				name="<portlet:namespace />id_diagnostico<%=prefijo%>" maxlength="4" size="4"
				type="text" value="<%=id_diagnostico%>"
				onBlur="javascript:<portlet:namespace />pierdeFocoDiagPreaut<%=prefijo%>();"
				onKeyUp="javascript:<portlet:namespace />buscarDiagPreautOnDiv<%=prefijo%>(event)" />
		</td>
		<td>&nbsp;&nbsp;</td>				
		<td>
			<input id="<portlet:namespace />descripDiag<%=prefijo%>"
				name="<portlet:namespace />descripDiag<%=prefijo%>" size="50" type="text"
				value="<%=descripDiag%>"
				onKeyUp="javascript:<portlet:namespace />buscarDiagPreautOnDiv<%=prefijo%>(event)"
				onBlur="javascript:<portlet:namespace />pierdeFocoDiagPreaut<%=prefijo%>();" />
		</td>
		<td>&nbsp;		
			<div id="<portlet:namespace />btnBuscarDiagPreaut<%=prefijo%>" style="float: left;">
				<a href="javascript: void(0);" 
					onclick="javascript:<portlet:namespace />buscarDiagnosticoP<%=prefijo%>();" tabindex="-1"> Buscar</a></div>
		</td>
	</tr>	
</table>
<input id="<portlet:namespace />diag_seleccionada<%=prefijo%>"
	name="<portlet:namespace />diag_seleccionada<%=prefijo%>" type="hidden" value="" />
<div id='divDiagnosticoP<%=prefijo%>' style="float: right;"></div>

<script type="text/javascript">
var popup;

function <portlet:namespace />buscarDiagnosticoP<%=prefijo%>() {
	var id_diagnostico=jQuery("#<portlet:namespace />id_diagnostico<%=prefijo%>").val();	
    var descripDiag=jQuery("#<portlet:namespace />descripDiag<%=prefijo%>").val();
	if (!<portlet:namespace />validaFormDiagnosP<%=prefijo%>(id_diagnostico,descripDiag)){
		return false;
	}
	
    popup = Liferay.Popup({title:"<liferay-ui:message key="observaciones-diagnostico" />",modal:true,width:420});
       
    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_diagnostico_p&id_diagnostico='+id_diagnostico+
    		  '&descripDiag='+encodeURI(descripDiag)+'&prefijo=<%=prefijo%>';
	
	jQuery(popup).load(url);    
}
function <portlet:namespace />buscarDiagPreautOnDiv<%=prefijo%>(e){	
	//Se modificó el campo, debemos cambiar el selecc
	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />diag_seleccionada<%=prefijo%>").val() == "1"  && (keyPressed!=9 && keyPressed!=16)){
		jQuery("#<portlet:namespace />descripDiag<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />id_diagnostico<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />diag_seleccionada<%=prefijo%>").val("");
		jQuery("#<portlet:namespace />btnBuscarDiagPreaut<%=prefijo%>").show();
		return false;
	}
	var id_diagnostico=jQuery("#<portlet:namespace />id_diagnostico<%=prefijo%>").val();
    var descripDiag=jQuery("#<portlet:namespace />descripDiag<%=prefijo%>").val();
    
    if((descripDiag.length>=3 || id_diagnostico.length>2) && (keyPressed!=9 && keyPressed!=16)){
        if(id_diagnostico.length >2){
        	jQuery("#<portlet:namespace />descripDiag<%=prefijo%>").val("");
        }else{
    		jQuery("#<portlet:namespace />id_diagnostico<%=prefijo%>").val("");
        }
        
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_diagnostico_p&id_diagnostico='+id_diagnostico+
		  '&descripDiag='+encodeURI(descripDiag)+'&prefijo=<%=prefijo%>';

		jQuery("#divDiagnosticoP<%=prefijo%>").load(url);		
		jQuery("#divDiagnosticoP<%=prefijo%>").show();
    }else{        
    	jQuery("#divDiagnosticoP<%=prefijo%>").hide("slow");
    }     
}
function <portlet:namespace />cerrarDivDiagP<%=prefijo%>(){
	jQuery("#divDiagnosticoP<%=prefijo%>").hide("slow");		
}
function <portlet:namespace />cerrarDiagP<%=prefijo%>(){	
	<portlet:namespace />cerrarDivDiagP<%=prefijo%>();
	if(popup){		
		Liferay.Popup.close(popup);
	}
}
function <portlet:namespace />pierdeFocoDiagPreaut<%=prefijo%>(){
	var seleccionada=jQuery("#<portlet:namespace />diag_seleccionada<%=prefijo%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivDiagP<%=prefijo%>();
		return false;
	}else{				
		return false; 
	}
}
function <portlet:namespace />validaFormDiagnosP<%=prefijo%>(id_diagnostico, descripDiag){
	 if(trim(id_diagnostico).length==0 && trim(descripDiag).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function <portlet:namespace />resetValidDiagnosP<%=prefijo%>(){
	if (jQuery("#<portlet:namespace />id_diagnostico<%=prefijo%>").val() != "") {
		jQuery("#<portlet:namespace />diag_seleccionada<%=prefijo%>").val("1")
	}
}

<portlet:namespace />resetValidDiagnosP<%=prefijo%>();
</script>