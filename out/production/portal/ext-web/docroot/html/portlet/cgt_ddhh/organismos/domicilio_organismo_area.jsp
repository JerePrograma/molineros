<%@ include file="/html/portlet/cgt_ddhh/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCGT.ROL_ABM_ORGANISMO);
List<Provincia> provincias = (ArrayList<Provincia>) portletSession
		.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
				PortletSession.APPLICATION_SCOPE);

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

List<Pais> paises = (ArrayList<Pais>) portletSession
.getAttribute(WebKeysAfiliados.PAISES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);		
		
Organismo organismo = (Organismo)portletSession.getAttribute(WebKeysCGT.ORGANISMO_EN_EDICION);

Area area = (Area)portletSession.getAttribute(WebKeysCGT.AREA_EN_EDICION);

boolean esArea= Boolean.parseBoolean(ParamUtil.getString(request, "esArea"));
if(!esArea){
	esArea= (Boolean)(renderRequest.getAttribute("esArea")!=null?renderRequest.getAttribute("esArea"):false);
}
Domicilio domicilio=null;
if(esArea){
	if(area!=null&&area.getDomicilio()!=null){
		domicilio = area.getDomicilio();
	}				
}else{
	if(organismo!=null&&organismo.getDomicilio()!=null){
		domicilio = organismo.getDomicilio();
	}
}	

boolean esEdicion = true;

%>

<fieldset class="block-labels"><legend><liferay-ui:message	key="address" /></legend>
	<table width="100%">
	<tr>
		<td><label><liferay-ui:message key="country" />:</label></td>
		<td><select <%if(!showABMButtons){%>disabled<%}%>
			name="<portlet:namespace/>pais" id="paisselect" onchange="javascript:filtrarPais();">
			<option value="0">Seleccione un país</option>
			<% for (Pais pais : paises) { %>
			<option
				<%= domicilio != null  && domicilio.getPais().getId() == pais.getId() ? "selected" : ""  %>				
				value="<%= pais.getId() %>"><%=pais.getDescripcion()%></option>
			<% } %>
		</select></td>
		
		<td><label><liferay-ui:message key="provincia" />:</label></td>
		<td><select <%if(!showABMButtons){%>disabled<%}%>
			name="<portlet:namespace/>provincia" id="provinciaselect" onchange="javascript:filtrarLocalidad();">
			<option value="0">Seleccione una provincia</option>
			<% for (Provincia provincia : provincias) { %>
			<option
				<%= domicilio != null  && domicilio.getProvinciaId() == provincia.getId() ? "selected" : ""  %>				
				value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
			<% } %>
		</select></td>
		<td><label><liferay-ui:message key="localidad" />:</label></td>
		<td colspan="2"><select id="<portlet:namespace/>localidad" 
			name="<portlet:namespace/>localidad" <% if (!esEdicion) { %>
			disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();" <%if(!showABMButtons){%>disabled<%}%>> 
			<option selected value="0">Seleccione una localidad</option>	
			<% for (Localidad localidad : localidades) { %>
			<option
				<%= domicilio != null && domicilio.getLocalidadId() == localidad.getId() ? "selected" : ""  %>				
				value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
			<% } %>
		</select></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="calle" />:</label></td>
		<td style="vertical-align:top" >
			<liferay-util:include page='/html/portlet/afiliados/empleadores/busqueda_calle_empleador.jsp'>
				<liferay-util:param name="showABMButtons"
					value="showABMButtons" />
				<liferay-util:param name="calle"
					value="<%=domicilio != null ? domicilio.getCalle() : new String()  %>" />
			</liferay-util:include>	
		</td>
		<td><label><liferay-ui:message key="cod-postal" />:</label></td>
		<td colspan="3"><input id="<portlet:namespace />cod_postal"
			name="<portlet:namespace />cod_postal" size="5" type="text"
			value="<%= domicilio != null ? domicilio.getPostal_codi() : "" %>"
			<%if(!showABMButtons){%>readonly<%}%> /></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="numero" />:</label></td>
		<td><input id="<portlet:namespace />numero"
			name="<portlet:namespace />numero" size="5" maxlength="5" type="text"
			value="<%= domicilio != null  && domicilio.getNumero() != null  ? domicilio.getNumero() : "" %>"
			onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" <%if(!showABMButtons){%>readonly<%}%> /></td>
		<div id='divCodPostal' style="float: right;"></div>	
		<td><label><liferay-ui:message key="piso" />:</label></td>
		<td><input id="<portlet:namespace />piso"
			name="<portlet:namespace />piso" size="5" maxlength="5" type="text"
			value="<%= domicilio != null && domicilio.getPiso() != null ? domicilio.getPiso() : "" %>"
			<%if(!showABMButtons){%>readonly<%}%> /></td>

		<td><label><liferay-ui:message key="departamento" />:</label></td>
		<td><input id="<portlet:namespace />departamento"
			name="<portlet:namespace />departamento" size="5" maxlength="5"
			type="text"
			value="<%= domicilio != null && domicilio.getDepto() != null ? domicilio.getDepto() : "" %>"
			<%if(!showABMButtons){%>readonly<%}%> /></td>
	</tr>
	</table>
	</fieldset>
	
<script type="text/javascript">	
	function filtrarLocalidad() {		
		var idProvincia = jQuery('#provinciaselect').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/id_provincia_localidad&idProvincia='+idProvincia;
		
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace/>localidad").length = 0;						
				var obj = jQuery.parseJSON(data);
				addElementToSelect("<portlet:namespace/>localidad", "Seleccione una localidad", 0);
				for(var i =0;i< obj.listaFiltrada.length; i++){					
					var value = obj.listaFiltrada[i].split('|')[0];
					var text = obj.listaFiltrada[i].split('|')[1];
					addElementToSelect("<portlet:namespace/>localidad", text, value);
				}                                                                                                                                                                                                                                                            
			}
		});		
	}

	function addElementToSelect(id_combo, texto, valor) {
		var combo = document.getElementById(id_combo);
		var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
		combo.options[idxElemento] = new Option();
		combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
		combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	}

	function filtrarCodPostal() {
		var idLocalidad = jQuery('#<portlet:namespace/>localidad').val();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/id_localidad_codpostal&idLocalidad='+idLocalidad;
		
		jQuery.ajax({   
			url: url,
			success: function(data){
				document.getElementById("<portlet:namespace />cod_postal").length = 0;						
				var obj = jQuery.parseJSON(data);						
				jQuery('#<portlet:namespace />cod_postal').val(obj.codPostal);				                                                                                                                                                                                                                                                            
			}
		});	
	}

	function <portlet:namespace />buscarCodPostalOnDiv(e) {
		var evtobj=window.event? event : e
		var keyPressed= evtobj.keyCode? evtobj.keyCode : evtobj.charCode
		if (jQuery("#<portlet:namespace/>localidad").val() == "265" && jQuery("#<portlet:namespace />calle").val() != "" && jQuery("#<portlet:namespace />numero").val() > 0) {
			var calle = jQuery("#<portlet:namespace />calle").val();
			var numero = jQuery("#<portlet:namespace />numero").val();
			if (calle.length > 0 && numero > 0) {
				var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/cgt_ddhh/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				
				jQuery("#divCodPostal").load(url);		
				jQuery("#divCodPostal").show();
			} else {        
	    		jQuery("#divCodPostal").hide("slow");
	   		}
		}
	}

	function <portlet:namespace />cerrarCodPostal() {	
		jQuery("#divCodPostal").hide("slow");
	}	
</script>	