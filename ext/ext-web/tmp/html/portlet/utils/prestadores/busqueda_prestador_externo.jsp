
<%
	//String dinámico que se le debe pasar a esta pagina para que sepa a que direccion redireccionar
	//con el formato /<prestador_portlet>/buscar_prestador
	//ej: /tesoreria/buscar_prestador
	//esto se debe corresponder con el action definido en struts-config
	//de la forma: <action path="/<prestador_portlet>/buscar_prestador" forward="portlet.utils.prestador.view" />
	String searchURL = ParamUtil.getString(request, "search_url");
	String esEditableStr = ParamUtil.getString(request, "esEditable");
	if (esEditableStr == null || esEditableStr.equals("false")) {
		esEditableStr = "false";
	}
	boolean esEditable = Boolean.parseBoolean(esEditableStr);

	String id = ParamUtil.getString(request, "id_prestador");
	String prestador = ParamUtil.getString(request, "nombre_prestador");
	String mat_tipo = ParamUtil.getString(request, "mat_tipo");
	String mat_numero = ParamUtil.getString(request, "mat_numero");
	String cuit = ParamUtil.getString(request, "prest_cuit");

	if (mat_tipo == null) {
		mat_tipo = "";
	}
	if (mat_numero == null) {
		mat_numero = "";
	}
	if (cuit == null) {
		cuit = "";
	}
	if (prestador == null) {
		prestador = "";
	}
	if (id == null) {
		id = "";
	}
%>
<%@ include file="/html/portlet/utils/prestadores/init.jsp"%>
<table>
	<tr>
		<td><label><liferay-ui:message key="matricula" />:</label></td>
		<td><input id="<portlet:namespace />id_prestador"
			name="<portlet:namespace />id_prestador" type="hidden" value="" /> <select
			<%if (!esEditable) {%> <%="disabled='disabled'"%> <%} else {%>
			onChange="javascript:<portlet:namespace />buscarPrestadorOnDiv(event)"
			onBlur="javascript:<portlet:namespace />pierdeFocoPd();" <%}%>
			name="<portlet:namespace/>mat_tipo" id="<portlet:namespace/>mat_tipo">
			<option value=""></option>
			<option value="N">NACIONAL</option>
			<option value="P">PROVINCIAL</option>
			<option value="O">OTRO</option>
		</select></td>
		<td><label><liferay-ui:message key="numero" />:</label></td>
		<td><input id="<portlet:namespace/>mat_numero"
			name="<portlet:namespace />mat_numero" size="6" maxlength="6"
			type="text" value="" <%if (!esEditable) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigits(event);"
			onKeyUp="javascript:<portlet:namespace />buscarPrestadorOnDiv(event)"
			onBlur="javascript:<portlet:namespace />pierdeFocoPd();" <%}%> /></td>
		<td><label><liferay-ui:message key="cuit" />:</label></td>
		<td><input id="<portlet:namespace />prest_cuit"
			name="<portlet:namespace />cuit" size="10" maxlength="11" type="text" value="" <%if (!esEditable) {%>
			<%="readonly='readonly'"%> <%} else {%>
			onkeydown="allowOnlyDigits(event);"
			onKeyUp="javascript:<portlet:namespace />buscarPrestadorOnDiv(event)"
			onBlur="javascript:<portlet:namespace />pierdeFocoPd();" <%}%> /></td>
		<td><liferay-ui:message key="razon-social" /></td>
		<td><input id="<portlet:namespace />nombre_prestador"
			name="<portlet:namespace />nombre_prestador" size="50" type="text"
			value="" <%if (!esEditable) {%> <%="readonly='readonly'"%>
			<%} else {%>
			onKeyUp="javascript:<portlet:namespace />buscarPrestadorOnDiv(event)"
			onBlur="javascript:<portlet:namespace />pierdeFocoPd();" <%}%> />&nbsp;
		</td>
				
		<td>
		<%
			if (esEditable) {
		%>
		<div id="<portlet:namespace />divBtnBuscaPrestador"><a
			href="javascript: void(0);"
			onclick="javascript:<portlet:namespace />buscarPrestador();"
			tabindex="-1">Buscar</a></div>
		<%
			}
		%>
		</td>
		<td><img alt="Nuevo Profesional" align="right"
			src="<%=themeDisplay.getPathThemeImages()%>/common/add.png"
			onClick="<portlet:namespace />prestadoresExternos();" /></td>
	</tr>
</table>
<div id='divPrestador' style="float: left;"></div>
<input id="<portlet:namespace />prest_seleccionada"
	name="<portlet:namespace />prest_seleccionada" type="hidden" value="" />

<script type="text/javascript">
var popupPD;
function <portlet:namespace />buscarPrestador() {

	var id_prestador=jQuery("#<portlet:namespace />id_prestador").val();
    var prestador=jQuery("#<portlet:namespace />nombre_prestador").val();    
    var cuit=jQuery("#<portlet:namespace />prest_cuit").val();    
    var mat_tipo=jQuery("#<portlet:namespace />mat_tipo").val();
    var mat_numero=jQuery("#<portlet:namespace />mat_numero").val();
    
    if (id_prestador == null){
    	id_prestador = "";
    }    
    if (prestador==null){
    	prestador = "";
    }
    if (mat_tipo==null){
    	mat_tipo = "";
    }
    if (mat_numero==null){
    	mat_numero = "";
    }
    if (cuit==null){
    	cuit = "";
    }
    
    if(id_prestador.length == 0 && prestador.length==0 && mat_tipo.length==0 && mat_numero.length==0 && cuit.length==0){

('<liferay-ui:message key="ingrese-parametros-busqueda" />');
    }else {
	    popupPD = Liferay.Popup({title:"Búsqueda profesional externo",modal:true,width:420});
	    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=<%=searchURL%>&id_prestador='+id_prestador+
		  '&nombre_prestador='+encodeURI(prestador)+'&mat_tipo='+mat_tipo+'&mat_numero='+mat_numero+'&cuit='+cuit;
		jQuery(popupPD).load(url);
    }
}

function <portlet:namespace />buscarPrestadorOnDiv(e){
	//Se modificó el campo, debemos cambiar el selecc		
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode

	if(jQuery("#<portlet:namespace />prest_seleccionada").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />id_prestador").val("");
	    jQuery("#<portlet:namespace />nombre_prestador").val("");    
	    jQuery("#<portlet:namespace />mat_tipo").val("");
	    jQuery("#<portlet:namespace />mat_numero").val("");
			
		jQuery("#<portlet:namespace />prest_cuit").val("");
		jQuery("#<portlet:namespace />prest_seleccionada").val("");
		jQuery("#<portlet:namespace />divBtnBuscaPrestador").show();
		
		return false;
	}	
	var id_prestador=jQuery("#<portlet:namespace />id_prestador").val();
    var prestador=jQuery("#<portlet:namespace />nombre_prestador").val();
    var cuit=jQuery("#<portlet:namespace />prest_cuit").val();
    var mat_tipo=jQuery("#<portlet:namespace />mat_tipo").val();
    var mat_numero=jQuery("#<portlet:namespace />mat_numero").val();

    if (id_prestador == null){
    	id_prestador = "";
    }
    if (prestador==null){
    	prestador = "";
    }
    if (cuit==null){
    	cuit = "";
    }
    if (mat_tipo==null){
    	mat_tipo = "";
    }
    if (mat_numero==null){
    	mat_numero = "";
    }

    if(jQuery("#<portlet:namespace />prest_seleccionada").val() != "1" && (prestador.length>=6 || mat_numero.length>3 || cuit.length>=11)){    	
         if (mat_numero.length>3){
			jQuery("#<portlet:namespace />id_prestador").val("");
		    jQuery("#<portlet:namespace />nombre_prestador").val("");
		    jQuery("#<portlet:namespace />mat_tipo").val("");
		    jQuery("#<portlet:namespace />prest_cuit").val("");
        } else if (prestador.length>=6) {
			jQuery("#<portlet:namespace />id_prestador").val("");
			jQuery("#<portlet:namespace />mat_tipo").val("");
		    jQuery("#<portlet:namespace />mat_numero").val("");
		    jQuery("#<portlet:namespace />prest_cuit").val("");
        } else if (cuit.length>=11) {
			jQuery("#<portlet:namespace />id_prestador").val("");
			jQuery("#<portlet:namespace />mat_tipo").val("");
		    jQuery("#<portlet:namespace />mat_numero").val("");
		    jQuery("#<portlet:namespace />nombre_prestador").val("");
        }
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=<%=searchURL%>&id_prestador='+id_prestador+
			'&nombre_prestador='+encodeURI(prestador)+'&mat_tipo='+mat_tipo+'&mat_numero='+mat_numero+'&cuit='+cuit;

		jQuery("#divPrestador").load(url);
		jQuery("#divPrestador").show();
    }else{        
    	jQuery("#divPrestador").hide("slow");
    }
}

function <portlet:namespace />cerrarDivPd(){	
	jQuery("#divPrestador").hide("slow");		
}

function <portlet:namespace />cerrarPd(){
	<portlet:namespace />cerrarDivPd();
	if(popupPD){
		Liferay.Popup.close(popupPD);
	}
}

function <portlet:namespace />pierdeFocoPd(){
	var seleccionada=jQuery("#<portlet:namespace />prest_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivPd();
		return false;
	}else{
		return false;
	}
}

var matNumeroJs = '<%=mat_numero%>';
var matTipoJs = '<%=mat_tipo%>';
var prestadorJs = '<%=prestador%>';
var idJs = '<%=id%>';

if ('<%=String.valueOf(esEditable)%>' == 'true' && idJs != ''){
	seleccionaCamposPrestad(idJs, matTipoJs, matNumeroJs, cuitJs, prestadorJs);
	<portlet:namespace />buscarPrestador();
}

function seleccionaCamposPrestad(id, tipo, num, cuit, param) {
    jQuery("#<portlet:namespace />id_prestador").val(id);
    jQuery("#<portlet:namespace />nombre_prestador").val(param);
    jQuery("#<portlet:namespace />mat_tipo").val(tipo);
    jQuery("#<portlet:namespace />mat_numero").val(num);
    jQuery("#<portlet:namespace />prest_cuit").val(cuit);
    jQuery("#<portlet:namespace />prest_seleccionada").val("1");
    jQuery("#<portlet:namespace />divBtnBuscaPrestador").hide();
}

function pasarParametrosAParentPd(tipo, num, cuit, nombre, id) {
	seleccionaCamposPrestad(id, tipo, num, cuit, nombre);
    <portlet:namespace />cerrarPd();
}

function <portlet:namespace />altaEmpleador() {
	jQuery('#<portlet:namespace />prest_cuit').val("");
	var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/afiliados/editar_empleadores_entry" /></portlet:renderURL>';
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}

var popupPrestadoresExternos;
function <portlet:namespace />prestadoresExternos() {
	var id_prestador=jQuery("#<portlet:namespace />id_prestador").val();
	popupPrestadoresExternos = Liferay.Popup({title:"Alta de Profesional",modal:true,width:1000});
    var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/prestadores_externos&id_prestador_ext='+id_prestador;
	jQuery(popupPrestadoresExternos).load(url);
}

</script>