<%@ include file="/html/portlet/rrhh/init.jsp" %>
<%
String nroTarjeta = "";
boolean esEdicion = false;

String cmd = (String) request.getAttribute(Constants.CMD);

TarjetaAcceso tarjeta = (TarjetaAcceso)request.getAttribute(WebKeysRrhh.TARJETA_ACCESO_EN_EDICION  );

if (cmd != null && !cmd.equalsIgnoreCase(Constants.VIEW)){
	esEdicion= true;
}

if(tarjeta != null  ){
	nroTarjeta ="Nro de Tarjeta : " + tarjeta.getId_tarjeta_acceso();
}

%>
<style>

div.divHeaderNro {
  position: absolute;
  top: 210px;
  right:30;
  left:1000px;
  background-color: #cccccc;
  width:200px;
  height:20px;
  border:1px solid black;
  font-size:145%;
  font-weight: bold;
}
</style>

<form action="EditarTarjetaEntryAction" name="<portlet:namespace />tarjeta_fm" id="<portlet:namespace />tarjeta_fm" >
    
 	<input  type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd%>" />
 	<input  type="hidden" id="<portlet:namespace />idTarjeta"	name="<portlet:namespace />idTarjeta" value="<%=Validator.isNotNull(tarjeta)  ? tarjeta.getId() : "0"  %>" />
    <input  type="hidden" id="<portlet:namespace />nroTarjetaDeBase"	name="<portlet:namespace />nroTarjetaDeBase" value="<%=Validator.isNotNull(tarjeta)  ? tarjeta.getId_tarjeta_acceso() : "0"  %>" />
		
    		
<fieldset class="block-labels">
	<legend>		
		<liferay-ui:message key="registro-ingreso" /> 
	</legend>
	
	<div class="divHeaderNro">		     
		  <label><%=nroTarjeta%></label>   
    </div>
		
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	<tr>
		<td colspan="1"><label>Nro Tarjeta</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />nrotarjeta"
			name="<portlet:namespace />nrotarjeta" size="8" maxlength="8" type="text" onkeydown="allowOnlyDigits(event)"   
			value="<%= tarjeta  != null &&  tarjeta.getId_tarjeta_acceso()>0  ? tarjeta.getId_tarjeta_acceso()  : "" %>" />			
		</td>
		<td colspan="1"><label>Apellido</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />apellido"
			name="<portlet:namespace />apellido" size="40" maxlength="40"
			type="text"
			value="<%= tarjeta != null &&  tarjeta.getApellido()!=null   ? tarjeta.getApellido()   : "" %>" />			
		</td>
		<td colspan="1"><label>Nombre</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />nombre"
			name="<portlet:namespace />nombre" size="40" maxlength="40"
			type="text"
			value="<%= tarjeta  != null &&  tarjeta.getNombre()!=null   ? tarjeta.getNombre()   : "" %>" />			
		</td>		
	</tr>
	<tr>
		<td colspan="1"><label>Legajo</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />legajo"
			name="<portlet:namespace />legajo" size="8" maxlength="8"  onkeydown="allowOnlyDigits(event)"
			type="text"
			value="<%= tarjeta  != null     ? tarjeta.getLegajo()   : "" %>" />			
		</td>
		
		<td colspan="1"><label>Piso</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />piso"
			name="<portlet:namespace />piso" size="4" maxlength="2"
				type="text"
			value="<%= tarjeta != null &&  tarjeta.getPiso()   !=null   ? String.valueOf(tarjeta.getPiso())   : "" %>" />			
		</td>
		
		<td colspan="1"><label>Entidad</label></td>
		<td>
		    <select name="<portlet:namespace />entidad" <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>
			id="<portlet:namespace />entidad" onchange="">
			    <option value="SELECCIONE">SELECCIONE</option>
				<%for(int i = 0; i < WebKeysRrhh.TIPOS_ENTIDADES_TARJETAS_RRHH.length; i++ ) {%>
				<option
					value="<%=WebKeysRrhh.TIPOS_ENTIDADES_TARJETAS_RRHH[i][0] %>"
					<%if (tarjeta != null && tarjeta.getEntidad() !=null && 
			        WebKeysRrhh.TIPOS_ENTIDADES_TARJETAS_RRHH[i][0].equals(tarjeta.getEntidad())  ) { %>
					selected="selected" <%} %>>
								<%=WebKeysRrhh.TIPOS_ENTIDADES_TARJETAS_RRHH[i][1] %>
							</option>
				<% } %>
		    </select>
  	    </td>
		<td colspan="1"><label>Sector</label></td>
		<td>
		    <select name="<portlet:namespace />sector" <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>
			id="<portlet:namespace />sector" onchange="">
			    <option value="SELECCIONE">SELECCIONE</option>
				<%for(int i = 0; i < WebKeysRrhh.TIPOS_SECTOR_TARJETAS_RRHH.length; i++ ) {%>
				<option
					value="<%=WebKeysRrhh.TIPOS_SECTOR_TARJETAS_RRHH[i][1] %>"
					<%if (tarjeta != null && tarjeta.getSector() !=null && 
							WebKeysRrhh.TIPOS_SECTOR_TARJETAS_RRHH[i][1].equals(tarjeta.getSector())  ) { %>
					selected="selected" <%} %>>
								<%=WebKeysRrhh.TIPOS_SECTOR_TARJETAS_RRHH[i][1] %>
							</option>
				<% } %>
		    </select>
  		</td>		   
	</tr>
	<tr>
		<td colspan="1"><label>Horas Jornada</label></td>
		<td colspan="5"><input <% if (!esEdicion) { %><%="disabled='disabled'" %><%}%>   id="<portlet:namespace />horas_jornada"
			name="<portlet:namespace />horas_jornada" size="5" maxlength="4" type="text" onkeydown="allowOnlyDigits(event)"   
			value="<%= tarjeta  != null &&  tarjeta.getHoras_jornada()>0  ? tarjeta.getHoras_jornada()  : "" %>" />			
		</td>
	</tr>
</table>
		
</fieldset>
      

<fieldset id="listatarjetas" class="block-labels"><legend><liferay-ui:message key="listado-historico-tarjetas" /></legend>
<div  id="<portlet:namespace />lista_tarjetas_historico" align="center"  style="height:170px; overflow: scroll; overflow-x: hidden;">
<liferay-util:include page="/html/portlet/rrhh/listado_tarjetas_persona.jsp">
					</liferay-util:include> 	
</div>
</fieldset>
      
      

<br/>

<div id="<portlet:namespace />saveTarjetaDiv" align="left">
<p>
<%if(esEdicion && tarjeta == null){ %>
<input type="button" value="<liferay-ui:message key="Grabar" />"
	onClick="<portlet:namespace />saveTarjeta();"/>	
	
<%}else if(esEdicion && tarjeta != null){ %>	
<input type="button" value="<liferay-ui:message key="Actualizar" />"		
	onClick="<portlet:namespace />editaTarjeta();"/>
<%} %>		

&nbsp;

<input id="<portlet:namespace />nuevo"
			value="<liferay-ui:message key="nueva-tarjeta"/>"
			title="<liferay-ui:message key="nueva-tarjeta" />" type="button"
			onClick="<portlet:namespace />altaTarjeta();"
			/>
</p>
</div>

<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>

</form>

<script type="text/javascript">

var popupMD;

jQuery('#<portlet:namespace />buscando').hide();

function <portlet:namespace />saveTarjeta() {
			
	if ( validaDatos())  {
		document.<portlet:namespace />tarjeta_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.SAVE  %>';
		
		var xportletUrl = '/rrhh/editar_borrar_tarjetas_entry';
		var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'</liferay-portlet:actionURL>';
	    url = url.replace("__xportletUrl",xportletUrl); 
		document.<portlet:namespace />tarjeta_fm.method = 'post';
		submitForm(document.<portlet:namespace />tarjeta_fm, url);
	}	
}	

function <portlet:namespace />editaTarjetaCheck(tarjetaCambio) {
	var cambioTarjeta=tarjetaCambio;
	document.<portlet:namespace />tarjeta_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.UPDATE %>';			 
	var xportletUrl = '/rrhh/editar_borrar_tarjetas_entry';
	var url= '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
	'<liferay-portlet:param name="cambioDeTarjeta" value="__cambioDeTarjeta"/>'+
	'</liferay-portlet:actionURL>';
    url = url.replace("__xportletUrl",xportletUrl); 
	url = url.replace("__cambioDeTarjeta", cambioTarjeta );
	document.<portlet:namespace />tarjeta_fm.method = 'post';			
	submitForm(document.<portlet:namespace />tarjeta_fm, url);
}

function <portlet:namespace />editaTarjeta() {
	<% if ( tarjeta!=null ) { %>
     if (validaDatos())	{
    	    nroTarjetaOriginal = jQuery('#<portlet:namespace />nroTarjetaDeBase').val();
    	    nroTarjetaEditada  = jQuery('#<portlet:namespace />nrotarjeta').val();
    	        	    
    	    if (nroTarjetaOriginal==nroTarjetaEditada) {
    	    	<portlet:namespace />editaTarjetaCheck(false);
    	    }else{   
    	    	if (confirm('¿Cambio el nro de tarjeta está seguro de que quiere continuar ?') == true) {
    	    		<portlet:namespace />editaTarjetaCheck(true);
    			} else {
    				return false;
    			}			    	    	
    	    }			
   	 }				
     <%}%>
}
	
function validaDatos(){
	var respuesta=true;
	var nroTarjeta =jQuery('#<portlet:namespace />nrotarjeta').val();
	var legajo = jQuery('#<portlet:namespace />legajo').val();
	
	if (legajo==""){
		alert ('Debe ingresar el nro de legajo.');		
		jQuery('#<portlet:namespace />legajo').focus();
		return false;
	}
	
    if (nroTarjeta==""){		
		alert ('Debe ingresar el nro de registro.');		
		jQuery('#<portlet:namespace />registro').focus();
		return false;
	}	
    
    respuesta=validaNroTarjetayLegajo();
    
	return respuesta;
}

function validaNroTarjetayLegajo(){
    var nroTarjeta = jQuery('#<portlet:namespace />nrotarjeta').val();
    var legajoPersona = jQuery('#<portlet:namespace />legajo').val();
    var idTarjeta = <%=tarjeta!=null?tarjeta.getId():0%> ;
    
    var resp =true;
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/rrhh/valida_nrotarjeta_legajo';
	url +='&nroTarjeta='+nroTarjeta;
	url +='&legajoPersona='+legajoPersona;
	url +='&idTarjeta='+idTarjeta;
	
	jQuery.ajax({
		url: url,
		async: false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			var respTarjeta=obj.tarjetaValidacion;
			if  (respTarjeta!=0 ){
				alert("La tarjeta esta activa y asignada a otra persona.");
				resp=false;
			}			
		}
	});
	return resp;
}

function <portlet:namespace />altaTarjeta() {		

	var xportletUrl = '/rrhh/editar_borrar_tarjetas_entry';
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
	'<liferay-portlet:param name="cmd" value="add" />'+
    '</liferay-portlet:renderURL>';

    url = url.replace("__xportletUrl",xportletUrl); 
	document.<portlet:namespace />tarjeta_fm.method = 'post';
	submitForm(document.<portlet:namespace />tarjeta_fm, url);
}

</script>

