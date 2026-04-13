<%@ include file="/html/portlet/utils/prestaciones/init.jsp" %>

<%

//String dinámico que se le debe pasar a esta pagina para que sepa a que direccion redireccionar
//con el formato /<nombre_portlet>/busqueda_prestacion_codigo
//ej: /tesoreria/busqueda_prestacion_codigo
//esto se debe corresponder con el action definido en struts-config
//de la forma: <action path="/<nombre_portlet>/busqueda_prestacion_codigo" forward="portlet.utils.prestacion_codigo.view" />
String searchURL = ParamUtil.getString(request, "search_url");
String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEditable = Boolean.parseBoolean(esEditableStr);

String idPrestacion = ParamUtil.getString(request, "id_prestacion");
String idTipoNomenclador = ParamUtil.getString(request, "id_tipo_nomenclador");
String codigo = ParamUtil.getString(request, "codigo");
String prestacion = ParamUtil.getString(request, "prestacion");
String suf = ParamUtil.getString(request, "suf", "");

if (suf == null) {
	suf = "";
}
if (idPrestacion == null){
	idPrestacion = "";
}
if (prestacion == null){
	prestacion = "";
}
%>
 <table>
 <tr>
	<td>
		<input id="<portlet:namespace />id_prestacion<%=suf%>" name="<portlet:namespace />id_prestacion<%=suf%>" type="hidden" value=""/>
		<input id="<portlet:namespace />codigo<%=suf%>" name="<portlet:namespace />codigo<%=suf%>" maxlength="6" size="6" type="text" value="" 
		onBlur="javascript:<portlet:namespace />pierdeFocoPresc<%=suf%>();" onKeyUp="javascript:<portlet:namespace />buscarPrestacionOnDiv<%=suf%>(event)" <%= !esEditable ? " readonly='readonly'" : ""  %>/>				
		<%-- <input id="<portlet:namespace />prestacion<%=suf%>" name="<portlet:namespace />prestacion<%=suf%>" type="hidden" 
	   	value=""/> --%>
	   	<input id="<portlet:namespace />id_tipo_nomenclador<%=suf%>" name="<portlet:namespace />id_tipo_nomenclador<%=suf%>" type="hidden" 
	   	value=""/>
	</td>
	<td>
		<liferay-ui:message key="descripcion" />:&nbsp;&nbsp;
		<input id="<portlet:namespace />prestacion<%=suf%>" name="<portlet:namespace />prestacion<%=suf%>" size="60" type="text" 
	   value="" onKeyUp="javascript:<portlet:namespace />buscarPrestacionOnDiv<%=suf%>(event)" onBlur="javascript:<portlet:namespace />pierdeFocoPresc<%=suf%>();" <%= !esEditable ? " readonly='readonly'" : ""  %>/>
	</td>
	<td>
		<div id="<portlet:namespace />btnBuscarPrestacion<%=suf%>"> <!-- style="float:right;" -->
		 <% if (esEditable) {%>
		<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarPrestacion<%=suf%>();" tabindex="-1">Buscar</a>
		<%} %>
		</div>
	</td>
</tr>	
</table>
<input id="<portlet:namespace />pres_seleccionada<%=suf%>" name="<portlet:namespace />pres_seleccionada<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />pres_transporte<%=suf%>" name="<portlet:namespace />pres_transporte<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />tipo_nomenclador<%=suf%>" name="<portlet:namespace />tipo_nomenclador<%=suf%>" type="hidden" value=""/>
<%-- <input id="<portlet:namespace />importe<%=suf%>" name="<portlet:namespace />importe<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />honorarios<%=suf%>" name="<portlet:namespace />honorarios<%=suf%>" type="hidden" value=""/>
<input id="<portlet:namespace />gastos<%=suf%>" name="<portlet:namespace />gastos<%=suf%>" type="hidden" value=""/> --%>

<div id='divPrestacion<%=suf%>' style="float:right;">
</div>

<script type="text/javascript">
var popup<%=suf%>;
function <portlet:namespace />buscarPrestacion<%=suf%>() {
	var idTipoNom=jQuery("#<portlet:namespace />id_tipo_nomenclador<%=suf%>").val();
	var codigo=jQuery("#<portlet:namespace />codigo<%=suf%>").val();
	var descripcion=jQuery("#<portlet:namespace />prestacion<%=suf%>").val();
	
	if (!<portlet:namespace />validaFormPresc<%=suf%>(idTipoNom,codigo,descripcion)){
		return false;
	}
    popup<%=suf%> = Liferay.Popup({title:"<liferay-ui:message key="busqueda-prestaciones" />",modal:true,width:420});
   <%--  var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>'+'&codigo='+codigo+'&suf=<%=suf%>';
	jQuery(popup<%=suf%>).load(url); --%>
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>';
	var params = {"codigo" : codigo, "suf" : '<%=suf%>', "id_tipo_nomenclador" : idTipoNom , "prestacion" : descripcion };
	jQuery(popup<%=suf%>).load(url, params);
	
}

function <portlet:namespace />buscarPrestacionOnDiv<%=suf%>(e){
	//Se modificó el campo, debemos cambiar el selecc
	
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
	
	if(jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />prestacion<%=suf%>").val("");
		jQuery("#<portlet:namespace />id_prestacion<%=suf%>").val("");
		jQuery("#<portlet:namespace />codigo<%=suf%>").val("");
		jQuery("#<portlet:namespace />tipo_nomenclador<%=suf%>").val("");
<%-- 		jQuery("#<portlet:namespace />importe<%=suf%>").val("");
		jQuery("#<portlet:namespace />honorarios<%=suf%>").val("");
		jQuery("#<portlet:namespace />gastos<%=suf%>").val(""); --%>
		jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val("");
<%--		jQuery("#<portlet:namespace />pres_transporte<%=suf%>").val(""); --%>
		jQuery("#<portlet:namespace />btnBuscarPrestacion<%=suf%>").show();		
		cambioCodigoHasta();
	}
		
	var idTipoNom=jQuery("#<portlet:namespace />id_tipo_nomenclador<%=suf%>").val();
	var codigo=jQuery("#<portlet:namespace />codigo<%=suf%>").val();
	var descripcion=jQuery("#<portlet:namespace />prestacion<%=suf%>").val();
	
	
    if(jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val() != "1" && codigo.length>=4){
		<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>'+'&codigo='+codigo+'&suf=<%=suf%>';
		jQuery("#divPrestacion<%=suf%>").load(url); --%>
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>';
		var params = {"codigo" : codigo, "suf" : '<%=suf%>', "id_tipo_nomenclador" : idTipoNom , "prestacion" : descripcion };
		jQuery("#divPrestacion<%=suf%>").load(url, params);
		jQuery("#divPrestacion<%=suf%>").show();
    }else{
    	jQuery("#divPrestacion<%=suf%>").hide("slow");
    }    
}
function <portlet:namespace />cerrarDivPresc<%=suf%>(){
	jQuery("#divPrestacion<%=suf%>").hide("slow");
}
function <portlet:namespace />cerrarPresc<%=suf%>(){
	<portlet:namespace />cerrarDivPresc<%=suf%>();
	if(popup<%=suf%>){
		Liferay.Popup.close(popup<%=suf%>);
	}
}
function <portlet:namespace />pierdeFocoPresc<%=suf%>(){
	var seleccionada=jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivPresc<%=suf%>();
		return false;
	}else{
		return false; 
	}
}
<%-- function <portlet:namespace />validaFormPresc<%=suf%>(codigo){
	 if(trim(codigo).length==0){
	 	alert('Ingrese Código');
		return false;
	 }else{
		return true;
	 }
} --%>
function <portlet:namespace />validaFormPresc<%=suf%>(id_tipo_nom, codigo, prestacion){
	 if(trim(id_tipo_nom).length==0 && trim(prestacion).length==0 && trim(codigo).length==0){
	 	alert('<liferay-ui:message key="ingrese-parametros-busqueda"/>');
		return false;
	 }else{
		return true;
	 }
}

function pasarParametrosAParentPresc<%=suf%>(id, cod, param, tipoNomencla, importe, honorarios, gastos) {	
	seleccionaCamposPrescBis<%=suf%>(id, cod, param, tipoNomencla, importe, honorarios, gastos);
    <portlet:namespace />cerrarPresc<%=suf%>();
 }

function seleccionaCamposPrescBis<%=suf%>(id, cod, param, tipo_nomenclador, importe, honorarios, gastos) {
    jQuery("#<portlet:namespace />id_prestacion<%=suf%>").val(id);
    jQuery("#<portlet:namespace />codigo<%=suf%>").val(cod);    
    jQuery("#<portlet:namespace />prestacion<%=suf%>").val(param);
    jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val("1");
	jQuery("#<portlet:namespace />tipo_nomenclador<%=suf%>").val(tipo_nomenclador);
	<%-- jQuery("#<portlet:namespace />importe<%=suf%>").val(importe);
	jQuery("#<portlet:namespace />honorarios<%=suf%>").val(honorarios);
	jQuery("#<portlet:namespace />gastos<%=suf%>").val(gastos); --%>
    jQuery("#<portlet:namespace />btnBuscarPrestacion<%=suf%>").hide();
    try {
    	pasarParametrosConveniosPrest<%=suf%>();
    }catch (err) {}
}

function seleccionaCamposPresc<%=suf%>(cod,desc) {    
    jQuery("#<portlet:namespace />codigo<%=suf%>").val(cod);    
    jQuery("#<portlet:namespace />prestacion<%=suf%>").val(desc);    
    jQuery("#<portlet:namespace />pres_seleccionada<%=suf%>").val("1");
    jQuery("#<portlet:namespace />btnBuscarPrestacion<%=suf%>").hide();
}

var codigoJs = "<%=codigo%>";
var descripcionJs = "<%=prestacion%>";
if (codigoJs != "" && descripcionJs != "" ) {
	seleccionaCamposPresc<%=suf%>(codigoJs,descripcionJs);
	<portlet:namespace />buscarPrestacion<%=suf%>();
}

</script>