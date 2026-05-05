<%//String dinámico que se le debe pasar a esta pagina para que sepa a que direccion redireccionar
			//con el formato /<farmacia_portlet>/buscar_medicamentos
			//ej: /farmacia/buscar_medicamento
			//esto se debe corresponder con el action definido en struts-config
			//de la forma: <action path="/<farmacia_portlet>/buscar_farmacia" forward="portlet.utils.medicamentos.view" />
			String searchURL = ParamUtil.getString(request, "search_url_edit");
			String esEditableStr = ParamUtil.getString(request, "esEditable");
			if (esEditableStr == null || esEditableStr.equals("false")) {
				esEditableStr = "false";
			}
			boolean esEditable = Boolean.parseBoolean(esEditableStr);
			String id_medicamento = ParamUtil.getString(request,
					"id_medicamento_edit", "");
			String troquel_edit = ParamUtil.getString(request, "troquel_edit", "");
			String medicamento = ParamUtil.getString(request,
					"nombre_medicamento_edit", "");
			boolean popup=ParamUtil.getBoolean(request, "popup", false);
			boolean mostrarConPresentacion = ParamUtil.getBoolean(request,"mostrar_con_presentacion", false);%>
			
<%@ include file="/html/portlet/utils/medicamentos/init.jsp" %>
		<table>
			<tr>
				<td>
					<input id="<portlet:namespace />id_medicamento_edit" name="<portlet:namespace />id_medicamento_edit" type="hidden" value="" 
					    <%if (!esEditable) {%>
							<%="readonly='readonly'"%> 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFocoMdEdit();" onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDivEdit(event);" onkeydown="allowOnlyDigits(event);"
						<%}%>/>
					Medicamento:&nbsp;&nbsp;
				</td>
				<td>
					<input id="<portlet:namespace />nombre_medicamento_edit" name="<portlet:namespace />nombre_medicamento_edit" size="50" type="text" value=""  
						<%if (!esEditable) {%>
							<%="readonly='readonly'"%>
						<%} else {%>
					   		onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDivEdit(event)" onBlur="javascript:<portlet:namespace />pierdeFocoMdEdit();"
					   	<%}%>/>&nbsp;
				</td>
				<td>
					Troquel:&nbsp;&nbsp;
				</td>
				<td>
					<input  id="<portlet:namespace />troquel_edit" name="<portlet:namespace />troquel_edit" maxlength="7" size="7" type="text" value=""   
						<%if (!esEditable) {%> 
							<%="readonly='readonly'"%> 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFocoMdEdit();" onKeyUp="javascript:<portlet:namespace />buscarMedicamentoOnDivEdit(event);"
						<%}%>/>
				</td>				
				<td>
				<%
					if (esEditable) {
				%> 
					<div id="<portlet:namespace />divBtnBuscaMedicamento">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarMedicamento_edit();" tabindex="-1">Buscar</a>
					</div>
					<%
						}
					%>
				</td>
			</tr>			
		</table>
		<div id='divMedicamento' style="float:left;">
		</div>
<input id="<portlet:namespace />med_seleccionado" name="<portlet:namespace />med_seleccionado" type="hidden" value=""/>
	
<script type="text/javascript">

jQuery(document).ready(function() {
	
	if(jQuery("#<portlet:namespace />troquel_edit").val().length > 1){
		<portlet:namespace />buscarMedicamento_edit();
	}
});


var popupMD;
function <portlet:namespace />buscarMedicamento_edit() {	
	var id_medicamento_edit=jQuery("#<portlet:namespace />id_medicamento_edit").val();
	var troquel_edit=jQuery("#<portlet:namespace />troquel_edit").val();	
    var nombre_medicamento_edit=jQuery("#<portlet:namespace />nombre_medicamento_edit").val();    
	
    if (troquel_edit == null || troquel_edit == ''){
    	troquel_edit = '0';    	
    }    
    if (nombre_medicamento_edit==null){
    	nombre_medicamento_edit = '';
    }
    if(troquel_edit=='0' && nombre_medicamento_edit.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
	    popupMD = Liferay.Popup({title:"Búsqueda Medicamentos",modal:true,width:700});	   	       
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=<%=searchURL%>&troquel='+troquel_edit+
		  '&nombre_medicamento='+encodeURI(nombre_medicamento_edit)+'&popup=<%=popup%>';   	
		jQuery(popupMD).load(url);
    }
}
function <portlet:namespace />buscarMedicamentoOnDivEdit(e){
	//Se modificó el campo, debemos cambiar el selecc		
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
			
	if(jQuery("#<portlet:namespace />med_seleccionado").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />id_medicamento_edit").val("");
		jQuery("#<portlet:namespace />troquel_edit").val("");
		jQuery("#<portlet:namespace />nombre_medicamento_edit").val("");
		jQuery("#<portlet:namespace />med_seleccionado").val("");
		jQuery("#<portlet:namespace />divBtnBuscaMedicamento").show();
		return false;
	}
	var id_medicamento_edit=jQuery("#<portlet:namespace />id_medicamento_edit").val();
	var troquel_edit=jQuery("#<portlet:namespace />troquel_edit").val();	
    var nombre_medicamento_edit=jQuery("#<portlet:namespace />nombre_medicamento_edit").val();
    if (troquel_edit == null || troquel_edit == ''){
        
    	troquel_edit = '0';    	
    }    
    if (nombre_medicamento_edit==null){
    	nombre_medicamento_edit = '';
    }    
    if(jQuery("#<portlet:namespace />med_seleccionado").val() != "1" && (nombre_medicamento_edit.length>=6 || troquel_edit.length>3)){
        if (troquel_edit.length>3){
    		jQuery("#<portlet:namespace />id_medicamento_edit").val("");
    		jQuery("#<portlet:namespace />nombre_medicamento_edit").val("");
        } else if (nombre_medicamento_edit.length>=6) {
    		jQuery("#<portlet:namespace />troquel_edit").val("");
    		jQuery("#<portlet:namespace />id_medicamento_edit").val("");
        }
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=<%=searchURL%>&troquel='+troquel_edit+
		  '&nombre_medicamento='+encodeURI(nombre_medicamento_edit);
		jQuery("#divMedicamento").load(url);
		jQuery("#divMedicamento").show();
    }else{
    	jQuery("#divMedicamento").hide("slow");
    }
}

function <portlet:namespace />cerrarDivMd(){
	jQuery("#divMedicamento").hide("slow");		
}

function <portlet:namespace />cerrarMd(){
	<portlet:namespace />cerrarDivMd();
	if(popupMD){
		Liferay.Popup.close(popupMD);
	}
}

function <portlet:namespace />pierdeFocoMdEdit(){
	var seleccionada=jQuery("#<portlet:namespace />med_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivPd();
		return false;
	}else{
		return false;
	}
}

var idJS = ""
var troquelJs = "<%=troquel_edit%>";
var medicamentoJs = "<%=medicamento%>";

if ("<%=String.valueOf(esEditable)%>" == "true" && troquelJs != ""){
	//<portlet:namespace />buscarMedicamento();
}

function pasarParametrosAParentMd_edit(troquel_edit,medicamento,id, pres) {	
	seleccionaCamposMd_edit(id, troquel_edit, medicamento, pres);
    <portlet:namespace />cerrarMd();
}

function seleccionaCamposMd_edit(id, cod, param, pres) {
	<%if (mostrarConPresentacion) {%>
		jQuery("#<portlet:namespace />nombre_medicamento_edit").val(param + " " + pres);
	<% } else {%>
    	jQuery("#<portlet:namespace />nombre_medicamento_edit").val(param);
	<%}%>
    jQuery("#<portlet:namespace />id_medicamento_edit").val(id);
    jQuery("#<portlet:namespace />troquel_edit").val(cod);
    jQuery("#<portlet:namespace />med_seleccionado").val("1");
    jQuery("#<portlet:namespace />divBtnBuscaMedicamento").hide();
}
</script>