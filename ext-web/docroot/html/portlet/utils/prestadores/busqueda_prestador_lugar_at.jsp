<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%

//String din�mico que a esta pagina para que sepa a que direccion redireccionar
//con el formato /<prestador_portlet>/buscar_prestador
//ej: /tesoreria/buscar_prestador
//esto se debe corresponder con el action definido en struts-config
//de la forma: <action path="/<prestador_portlet>/buscar_prestador" forward="portlet.utils.prestador.view" />
String searchURL = ParamUtil.getString(request, "search_url");
String esEditableStr = ParamUtil.getString(request, "esEditable");
if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEditable = Boolean.parseBoolean(esEditableStr);

String solo_vigentes = ParamUtil.getString(request, "solo_vigentes");
String cuit = ParamUtil.getString(request, "cuit_prestador");
String prestador = ParamUtil.getString(request, "nombre_prestador");
String id = ParamUtil.getString(request, "id_prestador");

if (cuit == null){
	cuit = "";
}
if (prestador == null){
	prestador = "";
}
if (id == null){
	id = "";
}
String ext = ParamUtil.getString(request, "ext");
if (ext == null) {
	ext = "";
}

%>
<%@ include file="/html/portlet/utils/prestadores/init.jsp" %>
<table>
  <tr>
 	 <td valign="top">	  
 
		<fieldset class="block-labels"><legend>Prestador indirecto</legend>	
		<table>
			<tr>
				<td>
					<liferay-ui:message key="cod-prestador" />
				</td>
				<td>
					<input id="<portlet:namespace />id_prestador<%=ext%>" name="<portlet:namespace />id_prestador<%=ext%>"  type="text" maxlength="6" size="6" value="" 
					    <% if (!esEditable) { %>
							<%="readonly='readonly'" %> 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFocoPd<%=ext%>();" onKeyUp="javascript:<portlet:namespace />buscarPrestadorOnDiv<%=ext%>(event);" onkeydown="allowOnlyDigits(event);"
						<%} %>/>
				</td>
				<td>
					<liferay-ui:message key="cuit" />
				</td>
				<td>
					<input  id="<portlet:namespace />cuit_prestador<%=ext%>" name="<portlet:namespace />cuit_prestador<%=ext%>" maxlength="11" size="13" type="text" value=""   
						<% if (!esEditable) { %> 
							<%="readonly='readonly'" %> 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFocoPd<%=ext%>();" onKeyUp="javascript:<portlet:namespace />buscarPrestadorOnDiv<%=ext%>(event);"
						<%} %>/>
				</td>
				<td>
					<liferay-ui:message key="razon-social" />
				</td>
				<td>
					<input id="<portlet:namespace />nombre_prestador<%=ext%>" name="<portlet:namespace />nombre_prestador<%=ext%>" size="50" type="text" value=""  
						<% if (!esEditable) { %> 
							<%="readonly='readonly'" %> 
						<%} else {%> 
					   		onKeyUp="javascript:<portlet:namespace />buscarPrestadorOnDiv<%=ext%>(event)" onBlur="javascript:<portlet:namespace />pierdeFocoPd<%=ext%>();"
					   	<%} %>/>&nbsp;
				</td>
				<td>
				<% if (esEditable) { %> 
					<div id="<portlet:namespace />divBtnBuscaPrestador<%=ext%>">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarPrestador<%=ext%>();" tabindex="-1">Buscar</a>
					</div>
					<%} %>
				</td>
				<td>
					<div id="<portlet:namespace />divBtnMostrarLugarAtPrestador<%=ext%>">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />mostrarLugaresAtIndirecto();" tabindex="-1">Mostrar Lugares de Atenci�n</a>
					</div>
				</td>
			</tr>			
		</table>
		</fieldset>
		<div id='divPrestador<%=ext%>' style="float:left;">
		</div>		
		<input id="<portlet:namespace />prest_seleccionada<%=ext%>" name="<portlet:namespace />prest_seleccionada<%=ext%>" 
				type="hidden" value=""/>
	</td>
	<%-- <td valign="top">	
		<fieldset class="block-labels"><legend>Prestador indirecto lugares de atenci�n</legend>
			<div id="divPrestador_Indirecto_LugaresAt<%=ext%>">
				<liferay-util:include page="/html/portlet/prestadores/lista_lugares_atencion_prestador_indirecto.jsp"></liferay-util:include>
			</div>
		</fieldset>	
	</td>	 --%>
 </tr>
</table>	
<script type="text/javascript">
jQuery("#<portlet:namespace />divBtnMostrarLugarAtPrestador<%=ext%>").hide();

var popupPD<%=ext%>;
function <portlet:namespace />buscarPrestador<%=ext%>() {	
	var id_prestador=jQuery("#<portlet:namespace />id_prestador<%=ext%>").val();
	var cuit_prestador=jQuery("#<portlet:namespace />cuit_prestador<%=ext%>").val();	
    var prestador=jQuery("#<portlet:namespace />nombre_prestador<%=ext%>").val();    

    if (id_prestador == null){
    	id_prestador = "";
    }
    
    if (cuit_prestador == null){
    	cuit_prestador = "";
    }

    if (prestador==null){
    	prestador = "";
    }
    if(id_prestador.length == 0 && cuit_prestador.length==0 && prestador.length==0){
        alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
	    popupPD<%=ext%> = Liferay.Popup({title:"<liferay-ui:message key="busqueda-prestadores" />",modal:true,width:420});
	    var id_prestador=jQuery("#<portlet:namespace />id_prestador<%=ext%>").val();
	    var cuit_prestador=jQuery("#<portlet:namespace />cuit_prestador<%=ext%>").val();
	    var prestador=jQuery("#<portlet:namespace />nombre_prestador<%=ext%>").val();    
	    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>&id_prestador='+id_prestador+
		  '&cuit_prestador='+cuit_prestador+
	    		  '&nombre_prestador='+encodeURI(prestador)+'&ext=<%=ext%>'+'&solo_vigentes=<%=solo_vigentes%>';	    
		jQuery(popupPD<%=ext%>).load(url);
		
		/* <portlet:namespace />buscarLugaresAtPrestadorIndirecto(); */
    }
}

function <portlet:namespace />buscarPrestadorOnDiv<%=ext%>(e){	
	//Se modific� el campo, debemos cambiar el selecc		
	var evtobj=window.event? event : e
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
			
	if(jQuery("#<portlet:namespace />prest_seleccionada<%=ext%>").val() == "1" && (keyPressed==8 || keyPressed==46)){
		jQuery("#<portlet:namespace />id_prestador<%=ext%>").val("");
		jQuery("#<portlet:namespace />cuit_prestador<%=ext%>").val("");
		jQuery("#<portlet:namespace />nombre_prestador<%=ext%>").val("");
		jQuery("#<portlet:namespace />prest_seleccionada<%=ext%>").val("");
		jQuery("#<portlet:namespace />divBtnBuscaPrestador<%=ext%>").show();
		jQuery("#<portlet:namespace />divBtnMostrarLugarAtPrestador<%=ext%>").hide();
		return false;
	}
	var id_prestador=jQuery("#<portlet:namespace />id_prestador<%=ext%>").val();
	var cuit_prestador=jQuery("#<portlet:namespace />cuit_prestador<%=ext%>").val();	
    var prestador=jQuery("#<portlet:namespace />nombre_prestador<%=ext%>").val();
    if(jQuery("#<portlet:namespace />prest_seleccionada<%=ext%>").val() != "1" && (prestador.length>=6 || cuit_prestador.length>10 || id_prestador.length > 3)){        
        if(id_prestador.length >3){
        	jQuery("#<portlet:namespace />nombre_prestador<%=ext%>").val("");
        	jQuery("#<portlet:namespace />cuit_prestador<%=ext%>").val("");
        } else if (cuit_prestador.length>10){
    		jQuery("#<portlet:namespace />id_prestador<%=ext%>").val("");
    		jQuery("#<portlet:namespace />nombre_prestador<%=ext%>").val("");
        } else if (prestador.length>=6) {
    		jQuery("#<portlet:namespace />cuit_prestador<%=ext%>").val("");
    		jQuery("#<portlet:namespace />id_prestador<%=ext%>").val("");
        }
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>&id_prestador='+id_prestador+
		'&cuit_prestador='+cuit_prestador+
		  '&nombre_prestador='+encodeURI(prestador)+'&ext=<%=ext%>'+'&solo_vigentes=<%=solo_vigentes%>';				  		
		jQuery("#divPrestador<%=ext%>").load(url);
		jQuery("#divPrestador<%=ext%>").show();
    }else{
    	jQuery("#divPrestador<%=ext%>").hide("slow");
    }
    
   /*  <portlet:namespace />buscarLugaresAtPrestadorIndirecto(); */
    
}

function <portlet:namespace />cerrarDivPd<%=ext%>(){
	jQuery("#divPrestador<%=ext%>").hide("slow");		
	
	/* <portlet:namespace />buscarLugaresAtPrestadorIndirecto(); */
	
}

function <portlet:namespace />cerrarPd<%=ext%>(){
	<portlet:namespace />cerrarDivPd<%=ext%>();
	if(popupPD<%=ext%>){
		Liferay.Popup.close(popupPD<%=ext%>);
	}
}


function <portlet:namespace />pierdeFocoPd<%=ext%>(){	
	var seleccionada=jQuery("#<portlet:namespace />prest_seleccionada<%=ext%>").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivPd<%=ext%>();
		return false;
	}else{
		return false;
	}
}

var cuitJs<%=ext%> = "<%=cuit%>";
var prestadorJs<%=ext%> = "<%=prestador%>";
var idJs<%=ext%> = "<%=id%>";


if (idJs<%=ext%> != "") {
	seleccionaCamposPrestad<%=ext%>(idJs<%=ext%>, cuitJs<%=ext%>, prestadorJs<%=ext%>);
}

if ("<%=String.valueOf(esEditable)%>" == "true" && idJs<%=ext%> != ""){	
	<portlet:namespace />buscarPrestador<%=ext%>();
}


function pasarParametrosAParentPd<%=ext%>(cuit,nombre,id) {
	seleccionaCamposPrestad<%=ext%>(id, cuit, nombre);
    <portlet:namespace />cerrarPd<%=ext%>();    
}

function seleccionaCamposPrestad<%=ext%>(id, cod, param) {
    jQuery("#<portlet:namespace />id_prestador<%=ext%>").val(id);
    jQuery("#<portlet:namespace />cuit_prestador<%=ext%>").val(cod);
    jQuery("#<portlet:namespace />nombre_prestador<%=ext%>").val(param);
    jQuery("#<portlet:namespace />prest_seleccionada<%=ext%>").val("1");
    jQuery("#<portlet:namespace />divBtnBuscaPrestador<%=ext%>").hide();
    jQuery("#<portlet:namespace />divBtnMostrarLugarAtPrestador<%=ext%>").show();
}



function <portlet:namespace />buscarLugaresAtPrestadorIndirecto(){

	var id_prest_indirecto = jQuery("#<portlet:namespace/>id_prestador<%=ext%>").val();

	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/lista_lugar_at_prestador_indirecto';
	url = url+'&idPrestadorIn='+id_prest_indirecto+'&cmd=view';

	jQuery("#divPrestador_Indirecto_LugaresAt<%=ext%>").load(url); 
}

var popupInd;
function <portlet:namespace />mostrarLugaresAtIndirecto(){
	
	var id_prest_indirecto = jQuery("#<portlet:namespace/>id_prestador<%=ext%>").val();
	
	popupInd= Liferay.Popup({title:"<liferay-ui:message key="Seleccionar lugar de atenci�n indirecto" />",modal:true,width:700});
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/liquidaciones/lista_lugar_at_prestador_indirecto';
	url = url+'&idPrestadorIn='+id_prest_indirecto+'&cmd=view';
	
	jQuery(popupInd).load(url); 
}

</script>