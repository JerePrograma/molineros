<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "liquidaciones";
}
String cuit = ParamUtil.getString(request, "cuit");
String sucu = ParamUtil.getString(request, "sucu");
String razon = ParamUtil.getString(request, "razon");
int id_seccional = ParamUtil.getInteger(request, "id_seccional");

String esEditableStr = ParamUtil.getString(request, "esEditable");
String soloOP = ParamUtil.getString(request, "soloOP");
String soloIngresos = ParamUtil.getString(request, "soloIngresos");

if(soloOP ==null || !soloOP.equals("true")){
	soloOP="false";
}

if(soloIngresos == null || !soloIngresos.equals("true")){
	soloIngresos="false";
}

if (esEditableStr == null){
	esEditableStr  = (String)request.getAttribute("esEdicion");
}

if (esEditableStr == null || esEditableStr.equals("false")){
	esEditableStr ="false";
}
boolean esEditable = Boolean.parseBoolean(esEditableStr);
%>
		<table width="100%">
			<tr>
				<%if (soloOP.equals("true")){ %>
					<td valign="top">
						<liferay-ui:message key="prestador" />
					</td>
				
					<td valign="top">
						<input id="<portlet:namespace />id_prestador" name="<portlet:namespace />id_prestador"  type="text" maxlength="6" size="6" value="" 
						    <% if (!esEditable) { %>
								<%="readonly='readonly'" %> 
							<%} else {%> 
								onBlur="javascript:<portlet:namespace />pierdeFoco();" onKeyUp="javascript:<portlet:namespace />buscarEntidadOnDiv(event)" onkeydown="cuitCambio();" onchange="cambiaCuit();"
							<%} %>/>
					</td>
				<%}%>
				<td valign="top"><liferay-ui:message key="cuit" />&nbsp;
					<input  id="<portlet:namespace />cuit_entidad" name="<portlet:namespace />cuit_entidad" maxlength="11" size="13" type="text" value=""   
						<% if (!esEditable) { %> 
							<%="readonly='readonly'" %> 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFoco();" onKeyUp="javascript:<portlet:namespace />buscarEntidadOnDiv(event)" onkeydown="cuitCambio();" onchange="cambiaCuit();"
						<%} %>/> <br/>
						<% if (esEditable && "false".equals(soloIngresos)) { %> 
						<span style="font-size: 7pt"><a href="#" onclick="javascript:sugerirCuitPadron('OSPIM','<portlet:namespace />cuit_entidad')">OSPIM</a>&nbsp;
											  <a href="#" onclick="javascript:sugerirCuitPadron('UOMA','<portlet:namespace />cuit_entidad')">UOMA</a>&nbsp;
											  <a href="#" onclick="javascript:sugerirCuitPadron('AMTIMA','<portlet:namespace />cuit_entidad')">AMTIMA</a>&nbsp;</span>
						<%} %>
				</td>
				<td valign="top">
					<liferay-ui:message key="sucursal" />
				</td>
				<td valign="top">
					<input  id="<portlet:namespace />sucursal_entidad" name="<portlet:namespace />sucursal_entidad" maxlenght="6" size="5" type="text" value=""
						<% if (!esEditable) { %> 
							readonly='readonly' 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFoco();" onKeyUp="javascript:<portlet:namespace />buscarEntidadOnDiv(event)" onkeydown="sucuCambio();"
						<%} %>/>
				</td>
				<td valign="top">
					<liferay-ui:message key="razon-social" />
				</td>
				<td valign="top">
					<input id="<portlet:namespace />entidad" name="<portlet:namespace />entidad" size="50" type="text" value=""  
						<% if (!esEditable) { %> 
							<%="readonly='readonly'" %> 
						<%} else {%> 
					   		onBlur="javascript:<portlet:namespace />pierdeFoco();" onkeydown="nombreCambio();"
					   	<%} %>/>&nbsp;
				</td>
				<td valign="top">
				<% if (esEditable) { %> 
					<div id="<portlet:namespace />divBtnBuscaEntidad">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarEntidad();" tabindex="-1">Buscar</a>
					</div>
					<%} %>
				</td>
			</tr>
			<tr>			
				<td width="100%" colspan="8" align="left">
					<div id='divEntidad' style="float:left;">
					</div>
				</td>	
			</tr>	
		</table>
		<input type="hidden" name="<portlet:namespace />id_seccional2" id="<portlet:namespace />id_seccional2" value=""/>
	
<input id="<portlet:namespace />ent_seleccionada" name="<portlet:namespace />ent_seleccionada" type="hidden" value=""/>
	
<script type="text/javascript">
var popup;
function <portlet:namespace />buscarEntidad() {	
	var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad").val();	
    var entidad=jQuery("#<portlet:namespace />entidad").val();
    var sucursal=jQuery("#<portlet:namespace />sucursal_entidad").val();
    var id_prestador=jQuery("#<portlet:namespace />id_prestador").val();    
    if("true"=="<%=soloOP%>" && cuit_entidad.length==0 && entidad.length==0 && id_prestador==0 ){
    	alert('<liferay-ui:message key="ingrese-parametros-busqueda" />');
    }else if("false"=="<%=soloOP%>" && cuit_entidad.length==0 && entidad.length==0){
    	alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
    	buscarEnPopUp("1");
    }    
}

function buscarEnPopUp(cur){
		if (popup == null){
			 popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-empresas" />",modal:true,width:420,position:[150,5],
				 onClose: function() { popup = null;}});
		}
	    var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad").val();
	    var entidad=jQuery("#<portlet:namespace />entidad").val();    
	    var sucursal=jQuery("#<portlet:namespace />sucursal_entidad").val();
	    var id_prestador=jQuery("#<portlet:namespace />id_prestador").val();   
	    
	   	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_padron_entidad&cuit_entidad='+cuit_entidad+
		  '&entidad='+encodeURI(entidad)+'&sucursal='+sucursal;
		  
	    if("true"=="<%=soloOP%>"){
	    	url += '&id_prestador='+id_prestador+'&soloOP=true';
	    }

	    if("true"=="<%=soloIngresos%>"){
	    	url += '&soloIngresos=true';
	    }

	    url += '&cur=' + cur;
	    url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popup).load(url);
}
function <portlet:namespace />buscarEntidadOnDiv(e){	
	//Se modificó el campo, debemos cambiar el selecc		
	var evtobj=window.event? event : e;	
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode;	

	if(jQuery("#<portlet:namespace />ent_seleccionada").val() == "1" &&  (keyPressed==8 || keyPressed==46)){		
		jQuery("#<portlet:namespace />entidad").val("");
		jQuery("#<portlet:namespace />cuit_entidad").val("");
		jQuery("#<portlet:namespace />sucursal_entidad").val("");
		jQuery("#<portlet:namespace />ent_seleccionada").val("");
		jQuery("#<portlet:namespace />divBtnBuscaEntidad").show();
		jQuery("#<portlet:namespace />id_seccional2").val("");
		jQuery("#<portlet:namespace />id_prestador").val("");
		return false;
	}
	
	if(keyPressed==8){
		jQuery("#<portlet:namespace />entidad").val("");
	} else  {
		buscarEnDiv();
	}
}

function buscarEnDiv(){	
	var sucursal=jQuery("#<portlet:namespace />sucursal_entidad").val();
	var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad").val();	
    var entidad=jQuery("#<portlet:namespace />entidad").val();
    var id_prestador = jQuery("#<portlet:namespace />id_prestador").val();
    //jQuery("#<portlet:namespace />ent_seleccionada").val() != "1" &&
    if((null!=entidad && entidad.length>=3 ||
    								null!=cuit_entidad && cuit_entidad.length>10 || null!=id_prestador && id_prestador.length>=3)){            
        if(cuit_entidad.length >2){
        	jQuery("#<portlet:namespace />entidad").val("");
        }else{
    		jQuery("#<portlet:namespace />cuit_entidad").val("");
        }

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_padron_entidad&cuit_entidad='+cuit_entidad+
		  	'&entidad='+encodeURI(entidad)+'&sucursal='+sucursal;
		  	
        if("true"=="<%=soloOP%>"){            
        	url += '&id_prestador='+id_prestador+'&soloOP=true';  		    
        }
        
        
        if("true"=="<%=soloIngresos%>"){
	    	url += '&soloIngresos=true';
	    }
	    	    
	    url += '&cur=1';	    
        url+= '&rnd=' + Math.floor(Math.random()*100);        
		jQuery("#divEntidad").load(url);		
		jQuery("#divEntidad").show();		
    }else{
    	jQuery("#divEntidad").hide("slow");
    }
    
}

function <portlet:namespace />cerrarDivBusquedaPadrones(){
	if (document.getElementById("<portlet:namespace />nro_comprobante") != null){
		sugerirNumero();
	}
	jQuery("#divEntidad").hide("slow");		
}
function <portlet:namespace />cerrarBusquedaPadrones(){
	<portlet:namespace />cerrarDivBusquedaPadrones();
	if(popup){
		Liferay.Popup.close(popup);
	}	
}


function <portlet:namespace />pierdeFoco(){
	var seleccionada=jQuery("#<portlet:namespace />ent_seleccionada").val();	
	if(seleccionada=="1"){
		<portlet:namespace />cerrarDivBusquedaPadrones();
		return false;
	}else{				
		return false; 
	}	
}

function sucuCambio(){
	jQuery("#<portlet:namespace />divBtnBuscaEntidad").show();
}

function cuitCambio(){	
	jQuery("#<portlet:namespace />divBtnBuscaEntidad").show();
}

function nombreCambio(){
	jQuery("#<portlet:namespace />divBtnBuscaEntidad").show();
}

function sugerirCuitPadron(entidad,  inp){
	if (entidad == 'OSPIM'){
		jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_OSPIM%>');	
	}
	if (entidad == 'UOMA'){
		jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_UOMA%>');
	}
	if (entidad == 'AMTIMA'){
		jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_AMTIMA%>');
	}


	 buscarEnDiv();
	 cambiaCuit();
}

function pasarParametrosAParentBusquedaPadrones(cuit, razon, sucursal, id_seccional) {
		jQuery("#<portlet:namespace />cuit_entidad").val(cuit);
	    jQuery("#<portlet:namespace />entidad").val(razon);
	    jQuery("#<portlet:namespace />sucursal_entidad").val(sucursal);	    
	    //jQuery("#<portlet:namespace />empl_seleccionada").val("1");	    	   
	    jQuery("#<portlet:namespace />ent_seleccionada").val("1");	    
	    jQuery("#<portlet:namespace />divBtnBuscaEntidad").hide();
		//jQuery("#<portlet:namespace />id_seccional").val(id_seccional);
	    <portlet:namespace />cerrarBusquedaPadrones();
	 }


var cuitJs = "<%=cuit%>";
var sucuJs = "<%=sucu%>";
var razonJs = "<%=razon%>";
var id_seccionalJs = "<%=id_seccional%>";

	jQuery("#<portlet:namespace />cuit_entidad").val(cuitJs);
	if (id_seccionalJs != "" && id_seccionalJs != "0"){
		jQuery("#<portlet:namespace />sucursal_entidad").val(id_seccionalJs);
	}else {
		jQuery("#<portlet:namespace />sucursal_entidad").val(sucuJs);
	}
	jQuery("#<portlet:namespace />entidad").val(razonJs);
	jQuery("#<portlet:namespace />id_seccional2").val(id_seccionalJs);

</script>