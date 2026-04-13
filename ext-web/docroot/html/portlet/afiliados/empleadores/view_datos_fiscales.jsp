<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%
Empresa empresa  = (Empresa)request.getSession().getAttribute(WebKeysEmpleadores.EMPRESA_EN_EDICION);


List<Provincia> provincias = (ArrayList<Provincia>) portletSession
		.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
				PortletSession.APPLICATION_SCOPE);

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysAfiliados.LOCALIDADES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

List<PosicionIva> posicionesIva = (ArrayList<PosicionIva>) portletSession
.getAttribute(WebKeysAfiliados.POSICIONESIVA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

List<EntidadCamaraEmpresa> entidades = (ArrayList<EntidadCamaraEmpresa>) portletSession
.getAttribute(WebKeysAfiliados.ENTIDADESCAMARAEMPRESA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);


		

boolean esEdicion = false;

if (request.getAttribute(WebKeysEmpleadores.EMPLEADORES_ACTION_EDICION) != null || empresa == null) {
	esEdicion = true;
}
%>
<fieldset class="block-labels">
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="entidad" />:</label></td>
		<td colspan="5"><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>entidad"
			id="<portlet:namespace/>entidad">
			<option value=""></option>
			<% for (EntidadCamaraEmpresa ent: entidades) { %>
			<option
				<%= empresa != null && empresa.getEntidadCamaraEmpresa()!= null && empresa.getEntidadCamaraEmpresa().equals(ent) ? "selected" : ""  %>
				value="<%= ent.getId_entidad_cam_empresa() %>"><%=ent.getDescripcion()%></option>
			<% } %>
		</select></td>
	</tr>
</table>
</fieldset>
<fieldset class="block-labels"><legend><liferay-ui:message
	key="domi-fiscal" /></legend>
<table class="lfr-table">
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="provincia" />:</label></td>
		<td><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
			<%}%> name="<portlet:namespace/>provinciafisc" id="provinciaselect" onchange="javascript:filtrarLocalidad();">
			<% for (Provincia provincia : provincias) { %>
			<option
				<%= empresa != null && empresa.getDomicilioFiscal() != null && empresa.getDomicilioFiscal().getProvinciaId() == provincia.getId() ? "selected" : ""  %>
				<%= empresa == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
				value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
			<% } %>
		</select></td>

		<td><label><liferay-ui:message key="localidad" />:</label></td>
		<td colspan="2"><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%>
			name="<portlet:namespace/>localidadfisc" id="<portlet:namespace/>localidad">
			<% for (Localidad localidad : localidades) { %>
			<option
				<%= empresa != null && empresa.getDomicilioFiscal()!=null && empresa.getDomicilioFiscal().getLocalidadId() == localidad.getId() ? "selected" : ""  %>
				<%= empresa == null && localidad.getId() == WebKeysAfiliados.ID_DEFAULT_LOCALIDAD ? "selected" : ""  %>
				value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
			<% } %>
		</select></td>

		<td><label><liferay-ui:message key="cod-postal" />:</label></td>
		<td><input id="<portlet:namespace />cod_postalfisc"
			name="<portlet:namespace />cod_postalfisc" size="5" type="text"
			value="<%= empresa != null && empresa.getDomicilioFiscal() != null && empresa.getDomicilioFiscal().getPostal_codi() != null ? empresa.getDomicilioFiscal().getPostal_codi() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="calle" />:</label></td>
		<td colspan="5"><input id="<portlet:namespace />callefisc"
			name="<portlet:namespace />callefisc" size="50" maxlength="100"
			type="text"
			value="<%= empresa != null && empresa.getDomicilioFiscal() != null && empresa.getDomicilioFiscal().getCalle() != null  ? empresa.getDomicilioFiscal().getCalle() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="numero" />:</label></td>
		<td><input id="<portlet:namespace />numerofisc"
			name="<portlet:namespace />numerofisc" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getDomicilioFiscal() != null  && empresa.getDomicilioFiscal().getNumero() != null  ? empresa.getDomicilioFiscal().getNumero() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>

		<td><label><liferay-ui:message key="piso" />:</label></td>
		<td><input id="<portlet:namespace />pisofisc"
			name="<portlet:namespace />pisofisc" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getDomicilioFiscal() != null && empresa.getDomicilioFiscal().getPiso() != null ? empresa.getDomicilioFiscal().getPiso() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>

		<td><label><liferay-ui:message key="departamento" />:</label></td>
		<td><input id="<portlet:namespace />departamentofisc"
			name="<portlet:namespace />departamentofisc" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getDomicilioFiscal() != null && empresa.getDomicilioFiscal().getDepto() != null ? empresa.getDomicilioFiscal().getDepto() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="iva" />:</label></td>
		<td colspan="5"><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>iva"
			id="<portlet:namespace/>iva">
			<option value=""></option>
			<% for (PosicionIva pos: posicionesIva) { %>
			<option
				<%= empresa != null && empresa.getPosicionIva()!= null && empresa.getPosicionIva().equals(pos) ? "selected" : ""  %>
				value="<%= pos.getId() %>"><%=pos.getDescripcion()%></option>
			<% } %>
		</select></td>
	</tr>
</table>
</fieldset>

<% if (esEdicion) { %>
<br />
<input type="hidden" name="<portlet:namespace />id_domiciliofisc"
	value="<%= empresa != null && empresa.getDomicilioFiscal() != null ? empresa.getDomicilioFiscal().getId_domicilio() : "" %>" />
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
<input type="hidden" value="" name="tabs1" id="tabs1" />
<input type="hidden" value="" name="view" id="view" />
<input type="hidden"
	value="<%= request.getAttribute("accionOriginal")%>"
	name="accionOriginal" id="accionOriginal" />
<%} %>
<script type="text/javascript">
function <portlet:namespace />validarCampos() {

	var provElegida = true;
	var locElegida = true;
	
	var provSelect = document.getElementById("provinciaselect");
	if (trim(provSelect.options[provSelect.selectedIndex].innerHTML) == 'DESCONOCIDA') {
		provElegida = false;
	}

	var <portlet:namespace/>localidad = document.getElementById("<portlet:namespace/>localidad");
	if (trim(<portlet:namespace/>localidad.options[<portlet:namespace/>localidad.selectedIndex].innerHTML) == 'DESCONOCIDA') {
		locElegida = false;
	}

	if(trim(jQuery('#<portlet:namespace />callefisc').val()) != '' ||
			trim(jQuery('#<portlet:namespace />cod_postalfisc').val()) != '' ||
			trim(jQuery('#<portlet:namespace />numerofisc').val()) != '' ||
			trim(jQuery('#<portlet:namespace />departamentofisc').val()) != '' ||
			trim(jQuery('#<portlet:namespace />pisofisc').val()) != '' ){
				if ( trim(jQuery('#<portlet:namespace />callefisc').val()) == '' || !locElegida || !provElegida){ 
						alert("Si completa algun dato del domicilio debe completar tambien la calle, provincia y localidad");
						jQuery('#<portlet:namespace />callefisc').focus();
						return false;
				}
		}
	
	return true;
}

function submitFormNotSave(){	
	if (<portlet:namespace />validarCampos()) {
		document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
		document.getElementById("cambioSolapa").value="cambioSolapa";
		document.getElementById("tabs1").value="datos";
		document.getElementById("view").value="true";
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_empleadores_entry" /></portlet:actionURL>';
		document.<portlet:namespace />emple.method = 'post';
		submitForm(document.<portlet:namespace />emple, url);
	}
}

function filtrarLocalidad() {
	var idProvincia = jQuery('#provinciaselect').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_provincia_localidad&idProvincia='+idProvincia;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace/>localidad").length = 0;						
			var obj = jQuery.parseJSON(data);
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

</script>