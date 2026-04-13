<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "liquidaciones";
}
if(renderResponse.getNamespace().equals("_EST_1_")){
	portlet_name = "estudio_isidro";
}
if(renderResponse.getNamespace().equals("_COR_1_")){
	portlet_name = "correspondencia";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

String cuit = ParamUtil.getString(request, "cuit");
String sucu = ParamUtil.getString(request, "sucu");
String razon = ParamUtil.getString(request, "razon");

String suf_entidad = ParamUtil.getString(request, "suf_entidad", "");

if (suf_entidad == null) {
	suf_entidad = "";
}

String suf = ParamUtil.getString(request, "suf", "");
request.setAttribute("suf",suf);

if (suf == null) {
	suf = "";
}


int id_seccional_ent = ParamUtil.getInteger(request, "id_seccional_ent");

String esEditableStr = ParamUtil.getString(request, "esEditable");
String soloOP = ParamUtil.getString(request, "soloOP");
String soloIngresos = ParamUtil.getString(request, "soloIngresos");
String esEmpresaPrestador = ParamUtil.getString(request, "esEmpresaPrestador");

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

if (esEmpresaPrestador == null || esEmpresaPrestador.equals("")) {
	esEmpresaPrestador = "false";
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
						<input id="<portlet:namespace />id_prestador<%=suf%>" name="<portlet:namespace />id_prestador<%=suf%>"  type="text" maxlength="6" size="6" value="" 
						    <% if (!esEditable) { %>
								<%="readonly='readonly'" %> 
							<%} else {%> 
								onBlur="javascript:<portlet:namespace />pierdeFoco<%=suf%>();" onKeyUp="javascript:<portlet:namespace />buscarEntidadOnDiv<%=suf%>(event)" onkeydown="cuitCambio<%=suf%>();" onchange="cambiaCuit<%=suf%>();"
							<%} %>/>
					</td>
				<%}%>
				<td valign="top"><liferay-ui:message key="cuit" />&nbsp;
					<input  id="<portlet:namespace />cuit_entidad<%=suf%>" name="<portlet:namespace />cuit_entidad<%=suf%>" maxlength="11" size="13" type="text" value=""   
						<% if (!esEditable) { %> 
							<%="readonly='readonly'" %>
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFoco<%=suf%>();" onKeyUp="javascript:<portlet:namespace />buscarEntidadOnDiv<%=suf%>(event)" onkeydown="cuitCambio<%=suf%>();" onchange="cambiaCuit<%=suf%>();"
						<%} %>/> <br/>
						<% if (esEditable && "false".equals(esEmpresaPrestador) && "false".equals(soloIngresos)) { %> 
						<span style="font-size: 7pt"><a href="#" onclick="javascript:sugerirCuitPadron<%=suf%>('OSPIM','<portlet:namespace />cuit_entidad<%=suf%>')">OSPIM</a>&nbsp;
											  <a href="#" onclick="javascript:sugerirCuitPadron<%=suf%>('UOMA','<portlet:namespace />cuit_entidad<%=suf%>')">UOMA</a>&nbsp;
											  <a href="#" onclick="javascript:sugerirCuitPadron<%=suf%>('AMTIMA','<portlet:namespace />cuit_entidad<%=suf%>')">AMTIMA</a>&nbsp;</span>
						<%} %>
				</td>
				<td valign="top">
					<%if(portlet_name.equals("uoma")){%>
						<b>/<b/> &nbsp;&nbsp;&nbsp;&nbsp;
					<%}else{%>
						<liferay-ui:message key="sucursal" />
					<%}%>
				</td>
				<td valign="top">
					<input  id="<portlet:namespace />sucursal_entidad<%=suf%>" name="<portlet:namespace />sucursal_entidad<%=suf%>" maxlenght="6" size="5" type="text" value=""
						<% if (!esEditable) { %> 
							readonly='readonly' 
						<%} else {%> 
							onBlur="javascript:<portlet:namespace />pierdeFoco<%=suf%>();" onkeydown="sucuCambio<%=suf%>();"
						<%} %>/>
				</td>
				<td valign="top">
					<liferay-ui:message key="razon-social" />
				</td>
				<td valign="top">
					<input id="<portlet:namespace />entidad<%=suf_entidad%>" name="<portlet:namespace />entidad<%=suf_entidad%>" size="50" type="text" value=""  
						<% if (!esEditable) { %> 
							<%="readonly='readonly'" %> 
						<%} else {%> 
					   		onBlur="javascript:<portlet:namespace />pierdeFoco<%=suf%>();" onkeydown="nombreCambio<%=suf%>();"
					   	<%} %>/>&nbsp;
				</td>
				<td valign="top">
				<% if (esEditable) { %> 
					<div id="<portlet:namespace />divBtnBuscaEntidad<%=suf%>">
						<a href="javascript: void(0);" onclick="javascript:<portlet:namespace />buscarEntidad<%=suf%>();" tabindex="-1">Buscar</a>
					</div>
					<%} %>
				</td>
			</tr>
			<tr>			
				<td width="100%" colspan="8" align="left">
					<div id='divEntidad<%=suf%>' style="float:left;">
					</div>
				</td>	
			</tr>	
		</table>
		
		<input type="hidden" name="<portlet:namespace />id_seccional_ent<%=suf_entidad%>" id="<portlet:namespace />id_seccional_ent<%=suf_entidad%>" value=""/>	
		<input id="<portlet:namespace />ent_seleccionada<%=suf%>" name="<portlet:namespace />ent_seleccionada<%=suf%>" type="hidden" value=""/>
	
<script type="text/javascript">
var popup;
function <portlet:namespace />buscarEntidad<%=suf%>() {
	var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad<%=suf%>").val();
    var entidad=jQuery("#<portlet:namespace />entidad<%=suf_entidad%>").val();
    var sucursal=jQuery("#<portlet:namespace />sucursal_entidad<%=suf%>").val();
    var id_prestador=jQuery("#<portlet:namespace />id_prestador<%=suf%>").val();    
    if('true'=='<%=soloOP%>' && cuit_entidad.length==0 && entidad.length==0 && id_prestador==0 ){
    	alert('<liferay-ui:message key="ingrese-parametros-busqueda" />');
    }else if('false'=='<%=soloOP%>' && cuit_entidad.length==0 && entidad.length==0){
    	alert('<liferay-ui:message key="ingrese-parametros-busqueda" />'); 
    }else {
    	buscarEnPopUp<%=suf%>("1");
    }
}

function buscarEnPopUp<%=suf%>(cur){		
		<%if(null!=suf && suf.equals("acta_")){%>
			popup=null;
		<%}%>		
		if (popup == null){			
			 popup = Liferay.Popup({title:"<liferay-ui:message key="busqueda-empresas" />",modal:true,width:420,position:[150,5],
				 onClose: function() { popup = null;}});			
		}
	    var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad<%=suf%>").val();
	    var entidad=jQuery("#<portlet:namespace />entidad<%=suf_entidad%>").val();    
	    var sucursal=jQuery("#<portlet:namespace />sucursal_entidad<%=suf%>").val();
	    var id_prestador=jQuery("#<portlet:namespace />id_prestador<%=suf%>").val();   
	    
	   	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_padron_entidad&cuit_entidad='+cuit_entidad+
		  '&entidad='+encodeURI(entidad)+'&sucursal='+sucursal+'&suf=<%=suf%>';
		  
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
function <portlet:namespace />buscarEntidadOnDiv<%=suf%>(e){
	//Se modificó el campo, debemos cambiar el selecc		
	var evtobj=window.event? event : e;
	var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode;

	if(jQuery("#<portlet:namespace />ent_seleccionada<%=suf%>").val() == "1" &&  (keyPressed==8 || keyPressed==46)){		
		jQuery("#<portlet:namespace />entidad<%=suf_entidad%>").val("");
		jQuery("#<portlet:namespace />cuit_entidad<%=suf%>").val("");
		jQuery("#<portlet:namespace />sucursal_entidad<%=suf%>").val("");
		jQuery("#<portlet:namespace />ent_seleccionada<%=suf%>").val("");
		jQuery("#<portlet:namespace />divBtnBuscaEntidad<%=suf%>").show();
		jQuery("#<portlet:namespace />id_seccional_ent<%=suf_entidad%>").val("");
		jQuery("#<portlet:namespace />id_prestador<%=suf%>").val("");
		return false;
	}
	
	if(keyPressed==8){
		jQuery("#<portlet:namespace />entidad<%=suf_entidad%>").val("");
	} else  {
		buscarEnDiv<%=suf%>();
	}
}

function buscarEnDiv<%=suf%>(){	
	var sucursal=jQuery("#<portlet:namespace />sucursal_entidad<%=suf%>").val();
	var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad<%=suf%>").val();	
    var entidad=jQuery("#<portlet:namespace />entidad<%=suf_entidad%>").val();
    var id_prestador = jQuery("#<portlet:namespace />id_prestador<%=suf%>").val();    
    
    if(jQuery("#<portlet:namespace />ent_seleccionada<%=suf%>").val() != "1" && (entidad.length>=3 || cuit_entidad.length>10 || id_prestador.length>=3)){        
        if(cuit_entidad.length >2){
        	jQuery("#<portlet:namespace />entidad<%=suf_entidad%>").val("");
        }else{
    		jQuery("#<portlet:namespace />cuit_entidad<%=suf%>").val("");
        }        

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_padron_entidad&cuit_entidad='+cuit_entidad+
		  	'&entidad='+encodeURI(entidad)+'&sucursal='+sucursal+'&suf=<%=suf%>';
		  	
        if('true'=='<%=soloOP%>'){            
        	url += '&id_prestador='+id_prestador+'&soloOP=true';  		    
        }
        
        if('true'=='<%=soloIngresos%>'){
	    	url += '&soloIngresos=true';
	    }
	    
	    url += '&cur=1';
        url+= '&rnd=' + Math.floor(Math.random()*100);
		jQuery("#divEntidad<%=suf%>").load(url);		
		jQuery("#divEntidad<%=suf%>").show();
    }else{        
    	jQuery("#divEntidad<%=suf%>").hide("slow");
    }
}

function <portlet:namespace />cerrarDivBusquedaPadrones<%=suf%>(){
	if (document.getElementById("<portlet:namespace />nro_comprobante") != null){
		sugerirNumero<%=suf%>();
	}
	jQuery("#divEntidad<%=suf%>").hide("slow");		
}
function <portlet:namespace />cerrarBusquedaPadrones<%=suf%>(){
	<portlet:namespace />cerrarDivBusquedaPadrones<%=suf%>();
	if(popup){
		Liferay.Popup.close(popup);
	}	
}


function <portlet:namespace />pierdeFoco<%=suf%>(){
	var seleccionada=jQuery("#<portlet:namespace />ent_seleccionada<%=suf%>").val();	
	if(seleccionada=="1"){
		jQuery("#divEntidad<%=suf%>").hide("slow");		
		return false;
	}else{				
		return false; 
	}	
}

function sucuCambio<%=suf%>(){
	jQuery("#<portlet:namespace />divBtnBuscaEntidad<%=suf%>").show();
}

function cuitCambio<%=suf%>(){
	jQuery("#<portlet:namespace />divBtnBuscaEntidad<%=suf%>").show();
}

function nombreCambio<%=suf%>(){
	jQuery("#<portlet:namespace />divBtnBuscaEntidad<%=suf%>").show();
}

function sugerirCuitPadron<%=suf%>(entidad,  inp){
	if (entidad == 'OSPIM'){
		jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_OSPIM%>');	
	}
	if (entidad == 'UOMA'){
		jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_UOMA%>');
	}
	if (entidad == 'AMTIMA'){
		jQuery('#' + inp).val('<%=WebKeysGlobal.CUIT_AMTIMA%>');
	}


	 buscarEnDiv<%=suf%>();
	 cambiaCuit<%=suf%>();
}


var cuitJs = "<%=cuit%>";
var sucuJs = "<%=sucu%>";
var razonJs = "<%=razon%>";
var id_seccionalJs = "<%=id_seccional_ent%>";

	jQuery("#<portlet:namespace />cuit_entidad<%=suf%>").val(cuitJs);
	<%if(renderResponse==null || (renderResponse.getNamespace()!=null && !renderResponse.getNamespace().equals("_EST_1_"))) {%>
		if (id_seccionalJs != "" && id_seccionalJs != "0" && cuitJs=="30531143856"){
			jQuery("#<portlet:namespace />sucursal_entidad<%=suf%>").val(id_seccionalJs);
		}else {
			jQuery("#<portlet:namespace />sucursal_entidad<%=suf%>").val(sucuJs);
		}
	<%}%>
	jQuery("#<portlet:namespace />entidad<%=suf_entidad%>").val(razonJs);
	jQuery("#<portlet:namespace />id_seccional_ent<%=suf_entidad%>").val(id_seccionalJs);


	function pasarParametrosAParentBusquedaPadrones<%=suf%>(cuit, razon, sucursal, id_seccional_ent) {		
		jQuery("#<portlet:namespace />cuit_entidad<%=suf%>").val(cuit);
	    jQuery("#<portlet:namespace />entidad<%=suf_entidad%>").val(razon);
	    jQuery("#<portlet:namespace />sucursal_entidad<%=suf%>").val(sucursal);	    
	    //jQuery("#<portlet:namespace />empl_seleccionada").val("1");	    	   
	    jQuery("#<portlet:namespace />ent_seleccionada<%=suf%>").val("1");	    
	    jQuery("#<portlet:namespace />divBtnBuscaEntidad<%=suf%>").hide();
		jQuery("#<portlet:namespace />id_seccional_ent<%=suf_entidad%>").val(id_seccional_ent);
	    <portlet:namespace />cerrarBusquedaPadrones<%=suf%>();	    
	    cambiaCuit<%=suf%>();	    
	 }

</script>