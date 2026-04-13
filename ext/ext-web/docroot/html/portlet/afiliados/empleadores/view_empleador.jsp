<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>
<%
Empresa empresa = (Empresa)request.getSession().getAttribute(WebKeysEmpleadores.EMPRESA_EN_EDICION);

String estudio_flag = ParamUtil.getString(request, "bandera", null);
if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {
	estudio_flag="true";
}

List<RamoEmpresa> ramos = (ArrayList<RamoEmpresa>) portletSession
.getAttribute(WebKeysAfiliados.RAMOS_EMPRESA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

boolean esEdicion = false;
if (request.getAttribute(WebKeysEmpleadores.EMPLEADORES_ACTION_EDICION) != null || empresa == null) {
	esEdicion = true;
}

List<Seccional> seccionales = (ArrayList<Seccional>) portletSession
.getAttribute(WebKeysGlobal.SECCIONALES_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

if (seccionales == null) {
	seccionales = TraeListasServiceUtil.getSeccionales();
	portletSession.setAttribute(WebKeysGlobal.SECCIONALES_EN_SESSION,
	seccionales,PortletSession.APPLICATION_SCOPE);
}
%>



<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos-empresa" /></legend>
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="descripcion" />:</label></td>
		<td colspan="5"><input id="<portlet:namespace />desc"
			name="<portlet:namespace />desc" size="50" type="text"
			value="<%= empresa != null ? empresa.getRazon_soc() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="cuit" />:</label></td>
		<td><input id="<portlet:namespace />cuit"
			name="<portlet:namespace />cuit" size="13" maxlength="11" type="text"
			value="<%= empresa != null ? empresa.getCuit() : "" %>"
			<% if (empresa != null) { %> <%="readonly='readonly'" %> <%}%> /></td>

		<td><label><liferay-ui:message key="sucursal" />:</label></td>
		<td><input id="<portlet:namespace />sucursal"
			name="<portlet:namespace />sucursal" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null ? empresa.getSucursal() : "000" %>"
			<% if (empresa != null) { %> <%="readonly='readonly'" %> <%}%> /></td>

		<td><label><liferay-ui:message key="ramo" />:</label></td>
		<td colspan="2"><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>ramo"
			id="<portlet:namespace/>ramo">
			<option value=""></option>
			<% for (RamoEmpresa ramo: ramos) { %>
			<option
				<%= empresa != null && empresa.getRamoEmpresa()!= null && empresa.getRamoEmpresa().equals(ramo) ? "selected" : ""  %>
				value="<%= ramo.getId_ramo_empresa() %>"><%=ramo.getDescripcion()%></option>
			<% } %>
		</select></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<% if (esEdicion) { %>
		<td colspan="6">
		<table class="lfr-table">
			<tr>
				<td><label><liferay-ui:message key="seccional" />:</label></td>
				<td colspan="5"><liferay-util:include
					page="/html/portlet/afiliados/busqueda_seccional.jsp">
					<liferay-util:param name="id_seccional"
						value="<%= empresa!=null ? String.valueOf(empresa.getId_seccional()) : new String() %>" />
					<liferay-util:param name="seccional" value="" />
				</liferay-util:include></td>
			</tr>
		</table>
		</td>
		<%} else { %>
		<td><label><liferay-ui:message key="seccional" />:</label></td>
		<td colspan="5"><select disabled='disabled'
			name="<portlet:namespace/>seccional">
			<% for (Seccional seccional : seccionales) { %>
			<option
				<%= empresa != null && empresa.getId_seccional() == seccional.getId() ? "selected" : ""  %>
				value="<%= seccional.getId() %>"><%=seccional.getDescripcion()%></option>
			<% } %>
		</select></td>
		<%} %>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><liferay-ui:message key="domicilio-afip" />:</td>
		<td colspan="5">
			<input id="<portlet:namespace />domicilio_afip" name="<portlet:namespace />domicilio_afip" size="100" maxlength="100"
			type="text" value="<%=empresa != null && empresa.getDomicilioAfip() != null ? empresa.getDomicilioAfip() : "" %>" readonly='readonly' />
			
		</td>
	</tr>
	<tr>
		<td><liferay-ui:message key="domicilio-tesoreria" />:</td>
		<td colspan="5">
			<input id="<portlet:namespace />domicilio_remo" name="<portlet:namespace />domicilio_remo" size="100" maxlength="100"
			type="text" value="<%=empresa != null && empresa.getDomicilioRemo() != null ? empresa.getDomicilioRemo() : "" %>" readonly='readonly' />
			
		</td>
	</tr>
	<tr>
		<td><liferay-ui:message key="domicilio-estudio" />:</td>
		<td colspan="5">
			<input id="<portlet:namespace />domicilio_estudio" name="<portlet:namespace />domicilio_estudio" size="100" maxlength="100"
			type="text" value="<%=empresa != null && empresa.getDomicilioEstudio() != null ? empresa.getDomicilioEstudio() : "" %>" readonly='readonly' />
			
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	</table>
	<fieldset class="block-labels"><legend><liferay-ui:message	key="domicilio-empresa-ospim" /></legend>
	<table>
	<tr>
		<td><label><liferay-ui:message key="provincia" />:</label></td>
		<td><select <% if (!esEdicion) { %> <%="disabled='disabled'" %>
			<%}%> name="<portlet:namespace/>provincia" id="provinciaselect" onchange="javascript:filtrarLocalidad();">
			<% for (Provincia provincia : provincias) { %>
			<option
				<%= empresa != null && empresa.getDomicilio() != null && empresa.getDomicilio().getProvinciaId() == provincia.getId() ? "selected" : ""  %>
				<%= empresa == null && provincia.getId() == WebKeysAfiliados.ID_DEFAULT_PROVINCIA ? "selected" : ""  %>
				value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
			<% } %>
		</select></td>
		<td><label><liferay-ui:message key="localidad" />:</label></td>
		<td colspan="2"><select id="<portlet:namespace/>localidad" 
			name="<portlet:namespace/>localidad" <% if (!esEdicion) { %>
			disabled="disabled" <%} %> onchange="javascript:filtrarCodPostal();"> 
			<option selected value="0">Seleccione una localidad</option>	
			<% for (Localidad localidad : localidades) { %>
			<option
				<%= empresa != null && empresa.getDomicilio() != null && empresa.getDomicilio().getLocalidadId() == localidad.getId() ? "selected" : ""  %>
				<%= empresa == null && localidad.getId() == WebKeysAfiliados.ID_DEFAULT_LOCALIDAD ? "selected" : ""  %>
				value="<%= localidad.getId() %>"><%=localidad.getDescripcion()%></option>
			<% } %>
		</select></td>

		<td><label><liferay-ui:message key="cod-postal" />:</label></td>
		<td><input id="<portlet:namespace />cod_postal"
			name="<portlet:namespace />cod_postal" size="5" type="text"
			value="<%= empresa != null && empresa.getDomicilio() != null ? empresa.getDomicilio().getPostal_codi() : "" %>"
			<% if (!esEdicion) { %> readonly="readonly" %> <%}%> /></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="calle" />:</label></td>
		<td colspan="1" style="vertical-align:top" >
		<liferay-util:include page='/html/portlet/afiliados/empleadores/busqueda_calle_empleador.jsp'>
			<liferay-util:param name="calle"
				value="<%=empresa != null &&  empresa.getDomicilio() != null ? empresa.getDomicilio().getCalle() : new String()  %>" />
		</liferay-util:include>	
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="numero" />:</label></td>
		<td><input id="<portlet:namespace />numero"
			name="<portlet:namespace />numero" size="5" maxlength="5" type="text"
			value="<%= empresa != null && empresa.getDomicilio() != null  && empresa.getDomicilio().getNumero() != null  ? empresa.getDomicilio().getNumero() : "" %>"
			<% if (!esEdicion) { %> readonly="readonly" <%}%> 
			onblur="javascript:<portlet:namespace />buscarCodPostalOnDiv(event);" /></td>
		<div id='divCodPostal' style="float: right;"></div>	
		<td><label><liferay-ui:message key="piso" />:</label></td>
		<td><input id="<portlet:namespace />piso"
			name="<portlet:namespace />piso" size="5" maxlength="5" type="text"
			value="<%= empresa != null && empresa.getDomicilio() != null && empresa.getDomicilio().getPiso() != null ? empresa.getDomicilio().getPiso() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>

		<td><label><liferay-ui:message key="departamento" />:</label></td>
		<td><input id="<portlet:namespace />departamento"
			name="<portlet:namespace />departamento" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getDomicilio() != null && empresa.getDomicilio().getDepto() != null ? empresa.getDomicilio().getDepto() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
	</tr>
	</table>
	</fieldset>
	<table>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="telefonos" />:</label></td>
		<td colspan="2">+<input id="<portlet:namespace />telefono0_pais"
			name="<portlet:namespace />telefono0_pais" size="3" maxlength="3"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 0 ? empresa.getTelefonos().get(0).getCodigoPais() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> -(<input
			id="<portlet:namespace />telefono0_area"
			name="<portlet:namespace />telefono0_area" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && 
			           empresa.getTelefonos().size() > 0 && 
			           null != empresa.getTelefonos().get(0).getCodigoArea() ? empresa.getTelefonos().get(0).getCodigoArea() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />)- <input
			id="<portlet:namespace />telefono0_numero"
			name="<portlet:namespace />telefono0_numero" size="50" maxlength="50"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 0 ? empresa.getTelefonos().get(0).getNumero() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />
		&nbsp;Ext.&nbsp; <input id="<portlet:namespace />telefono0_ext"
			name="<portlet:namespace />telefono0_ext" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && 
			           empresa.getTelefonos().size() > 0 && empresa.getTelefonos().get(0).getExtension()!=null 
			           ? empresa.getTelefonos().get(0).getExtension() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> <input
			type="hidden" name="<portlet:namespace />telefono0_id"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 0 ? empresa.getTelefonos().get(0).getId() : "" %>" />

		<td><label><liferay-ui:message key="fax" />:</label></td>
		<td colspan="2"><input id="<portlet:namespace />fax"
			name="<portlet:namespace />fax" size="50" maxlength="50" type="text"
			value="<%= empresa != null && empresa.getFax() != null && empresa.getFax().getContacto() != null ? empresa.getFax().getContacto() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /></td>
		<input type="hidden" name="<portlet:namespace />fax_id"
			value="<%= empresa != null && empresa.getFax() != null ? empresa.getFax().getId() : "" %>" />
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td colspan="2">+<input id="<portlet:namespace />telefono1_pais"
			name="<portlet:namespace />telefono1_pais" size="3" maxlength="3"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 1 ? empresa.getTelefonos().get(1).getCodigoPais() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> -(<input
			id="<portlet:namespace />telefono1_area"
			name="<portlet:namespace />telefono1_area" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 1 && 
			empresa.getTelefonos().get(1).getCodigoArea()!=null ? empresa.getTelefonos().get(1).getCodigoArea() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />)- <input
			id="<portlet:namespace />telefono1_numero"
			name="<portlet:namespace />telefono1_numero" size="50" maxlength="50"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 1 && 
			empresa.getTelefonos().get(1).getNumero()!=null ? empresa.getTelefonos().get(1).getNumero() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />
		&nbsp;Ext.&nbsp; <input id="<portlet:namespace />telefono1_ext"
			name="<portlet:namespace />telefono1_ext" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 1 && 
			empresa.getTelefonos().get(1).getExtension()!=null ? empresa.getTelefonos().get(1).getExtension() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> <input
			type="hidden" name="<portlet:namespace />telefono1_id"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 1 ? empresa.getTelefonos().get(1).getId() : "" %>" />
		</td>

		<td><label><liferay-ui:message key="email-short" />:</label></td>
		<td colspan="2"><input id="<portlet:namespace />email"
			name="<portlet:namespace />email" size="50" maxlength="50"
			type="text"
			value="<%= empresa != null && empresa.getEmail() != null && empresa.getEmail().getContacto() !=null? empresa.getEmail().getContacto() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> <input
			type="hidden" name="<portlet:namespace />email_id"
			value="<%= empresa != null && empresa.getEmail() != null ? empresa.getEmail().getId() : "" %>" />
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td colspan="2">+<input id="<portlet:namespace />telefono2_pais"
			name="<portlet:namespace />telefono2_pais" size="3" maxlength="3"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 2 &&
			           empresa.getTelefonos().get(2).getCodigoPais()!=null ? empresa.getTelefonos().get(2).getCodigoPais() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> -(<input
			id="<portlet:namespace />telefono2_area"
			name="<portlet:namespace />telefono2_area" size="5" maxlength="5"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 2  && 
			           empresa.getTelefonos().get(2).getCodigoArea()!=null ? empresa.getTelefonos().get(2).getCodigoArea() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />)- <input
			id="<portlet:namespace />telefono2_numero"
			name="<portlet:namespace />telefono2_numero" size="50" maxlength="50"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 2 && 
			           empresa.getTelefonos().get(2).getNumero()!=null ? empresa.getTelefonos().get(2).getNumero() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> />
		&nbsp;Ext.&nbsp; <input id="<portlet:namespace />telefono2_ext"
			name="<portlet:namespace />telefono2_ext" size="4" maxlength="4"
			type="text"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 2 && 
			           empresa.getTelefonos().get(2).getExtension()!=null ? empresa.getTelefonos().get(2).getExtension() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> <input
			type="hidden" name="<portlet:namespace />telefono2_id"
			value="<%= empresa != null && empresa.getTelefonos() != null && empresa.getTelefonos().size() > 2 ? empresa.getTelefonos().get(2).getId() : "" %>" />
		</td>

		<td><label><liferay-ui:message key="sitio-web" />:</label></td>
		<td colspan="2"><input id="<portlet:namespace />sitioweb"
			name="<portlet:namespace />sitioweb" size="50" maxlength="50"
			type="text"
			value="<%= empresa != null && empresa.getSitioWeb() != null && empresa.getSitioWeb().getContacto() !=null? empresa.getSitioWeb().getContacto() : "" %>"
			<% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> /> <input
			type="hidden" name="<portlet:namespace />sitioweb_id"
			value="<%= empresa != null && empresa.getSitioWeb() != null ? empresa.getSitioWeb().getId() : "" %>" />
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="contacto" />:</label></td>
		<td colspan="5"><textarea rows="5" cols="50"
			id="<portlet:namespace />contacto"
			name="<portlet:namespace />contacto" <% if (!esEdicion) { %>
			<%="readonly='readonly'" %> <%}%>><%= empresa != null && empresa.getContacto() != null? empresa.getContacto() : "" %></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="5"><textarea rows="5" cols="50"
			id="<portlet:namespace />observaciones"
			name="<portlet:namespace />observaciones" <% if (!esEdicion) { %>
			<%="readonly='readonly'" %> <%}%>><%= empresa != null && empresa.getObservaciones() != null? empresa.getObservaciones() : "" %></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
</table>
</fieldset>
<input type="hidden" name="<portlet:namespace />id_domicilio"
	value="<%= empresa != null && empresa.getDomicilio() != null ? empresa.getDomicilio().getId_domicilio() : "" %>" />
<% if (esEdicion) { %>
<br />
<input type="submit" value="<liferay-ui:message key="save" />"
	<% if (estudio_flag == null || (estudio_flag != null && !estudio_flag.equals("true"))) { %>
		onClick="<portlet:namespace />saveEmpleador();return false;" 
	<%} else { %>
		onClick="<portlet:namespace />saveEmpleadorPopUp();return false;"
	<%} %> />
	
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
<input type="hidden" value="" name="tabs1" id="tabs1" />
<input type="hidden" value="" name="view" id="view" />
<input type="hidden" value="" name="flag" id="flag" />
<input type="hidden"
	value="<%= request.getAttribute("accionOriginal") != null && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (empresa == null ? Constants.ADD : Constants.UPDATE)%>"
	name="accionOriginal" id="accionOriginal" />
<%} %>
<div align="center" id="<portlet:namespace />saveEmpleadorDiv">						
			</div>
<script type="text/javascript">
	function <portlet:namespace />saveEmpleador() {				
		if (<portlet:namespace />validarCampos()) {			
			document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "<%= request.getAttribute("accionOriginal") != null  && !request.getAttribute("accionOriginal").equals("") ? request.getAttribute("accionOriginal") :  (empresa == null ? Constants.ADD : Constants.UPDATE) %>";			
			<% if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
				var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/estudio_isidro/editar_empleadores_entry" /></portlet:actionURL>';
				url=url+'&flagEstudio=true';
			<%}else{%>			
				var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_empleadores_entry" /></portlet:actionURL>';			    
			<%}%>			
			document.<portlet:namespace />emple.method = 'post';			
			document.getElementById("cambioSolapa").name = "xx";			
			document.getElementById("tabs1").name = "xx2";			
			submitForm(document.<portlet:namespace />emple, url);
		} 
	}

	function <portlet:namespace />saveEmpleadorPopUp() {		
		var flag=true;
		document.getElementById("flag").value = flag;		
		if (<portlet:namespace />validarCampos()) {			 	
			var cmd = "<%= Constants.ADD %>";
			var desc=jQuery("#<portlet:namespace />desc").val();			
			var cuit=jQuery('#<portlet:namespace />cuit').val();
			var sucursal=jQuery('#<portlet:namespace />sucursal').val();
			var ramo=jQuery('#<portlet:namespace />ramo').val();
			var id_seccional=jQuery("#<portlet:namespace />id_seccional").val();
			var provincia=jQuery('#provinciaselect').val();
			var localidad=jQuery('#<portlet:namespace/>localidad').val();
			var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
			var calle=jQuery('#<portlet:namespace />calle').val();
			var numero=jQuery('#<portlet:namespace />numero').val();
			var piso=jQuery('#<portlet:namespace />piso').val();
			var departamento=jQuery('#<portlet:namespace />departamento').val();
			var telefono0_pais=jQuery('#<portlet:namespace />telefono0_pais').val();
			var telefono0_area=jQuery('#<portlet:namespace />telefono0_area').val();
			var telefono0_numero=jQuery('#<portlet:namespace />telefono0_numero').val();
			var telefono0_ext=jQuery('#<portlet:namespace />telefono0_ext').val();
			var telefono1_pais=jQuery('#<portlet:namespace />telefono1_pais').val();
			var telefono1_area=jQuery('#<portlet:namespace />telefono1_area').val();
			var telefono1_numero=jQuery('#<portlet:namespace />telefono1_numero').val();
			var telefono1_ext=jQuery('#<portlet:namespace />telefono1_ext').val();
			var telefono2_pais=jQuery('#<portlet:namespace />telefono2_pais').val();
			var telefono2_area=jQuery('#<portlet:namespace />telefono2_area').val();
			var telefono2_numero=jQuery('#<portlet:namespace />telefono2_numero').val();
			var telefono2_ext=jQuery('#<portlet:namespace />telefono2_ext').val();
			var fax=jQuery('#<portlet:namespace />fax').val();
			var email=jQuery('#<portlet:namespace />email').val();
			var sitioweb=jQuery('#<portlet:namespace />sitioweb').val();
			var contacto=jQuery('#<portlet:namespace />contacto').val();
			var observaciones=jQuery('#<portlet:namespace />observaciones').val();	
			var telefono0_id=jQuery('#<portlet:namespace />telefono0_id').val();
			var telefono1_id=jQuery('#<portlet:namespace />telefono1_id').val();
			var telefono2_id=jQuery('#<portlet:namespace />telefono2_id').val();
			var fax_id=jQuery('#<portlet:namespace />fax_id').val();
			var email_id=jQuery('#<portlet:namespace />email_id').val();
			var sitioweb_id=jQuery('#<portlet:namespace />sitioweb_id').val();			
			var id_domicilio=jQuery('#<portlet:namespace />id_domicilio').val();
			
			var empleadorNuevo = { "cmd": cmd, "desc": desc, "cuit": cuit, "sucursal": sucursal, "ramo": ramo, "id_seccional": id_seccional, 
			"provincia": provincia, "localidad": localidad, "cod_postal": cod_postal, "calle": calle, "numero": numero, "piso": piso, 
			"departamento": departamento, "telefono0_pais": telefono0_pais, "telefono0_area": telefono0_area, "telefono0_numero": telefono0_numero, 
			"telefono0_ext": telefono0_ext, "telefono1_pais": telefono1_pais, "telefono1_area": telefono1_area, "telefono1_numero": telefono1_numero, 
			"telefono1_ext": telefono1_ext, "telefono2_pais": telefono2_pais, "telefono2_area": telefono2_area, "telefono2_numero": telefono2_numero, 
			"telefono2_ext": telefono2_ext, "fax": fax, "email": email, "sitioweb": sitioweb, "contacto": contacto, "observaciones": observaciones, 
			"telefono0_id": telefono0_id, "telefono1_id": telefono1_id, "telefono2_id": telefono2_id, "fax_id": fax_id, "email_id": email_id, 
			"sitioweb_id": sitioweb_id, "id_domicilio": id_domicilio};
			<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")){%>
				cmd = "<%= Constants.UPDATE %>";
				var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>"/>&struts_action=/estudio_isidro/editar_empleadores_entry&flag='+flag+'&popupSeguimiento=true&cmd=update';				
				submitForm(document.<portlet:namespace />emple, url);					
			<%}else{%>
				var url = '<portlet:actionURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/editar_empleadores_entry&flag='+flag;
				jQuery('#<portlet:namespace />saveEmpleadorDiv').load(url, empleadorNuevo, function() {});
			<%}%>
			
			
		} 
	}
	
	function <portlet:namespace />validarCampos() {
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var cod_postal=jQuery('#<portlet:namespace />cod_postal').val();
		var calle=jQuery('#<portlet:namespace />calle').val();
		try {
			if (trim(jQuery("#<portlet:namespace />desc").val()).length == 0) {
				alert("<liferay-ui:message key='nombre-obligatorio' />");
				jQuery('#<portlet:namespace />desc').focus();
				return false;
			}

			if (trim(cuit).length == 0){
				alert("<liferay-ui:message key='valida-cuit' />");
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
							
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}

			if (!isInteger(jQuery('#<portlet:namespace />sucursal').val())){
				alert("<liferay-ui:message key='valida-sucu' />");
				jQuery('#<portlet:namespace />sucursal').focus();
				return false;
			}
			
			if (trim(jQuery("#<portlet:namespace />sucursal").val()).length == 0){
				alert("<liferay-ui:message key='valida-sucu' />");
				jQuery('#<portlet:namespace />sucursal').focus();
				return false;
			}
			
			if(jQuery("#<portlet:namespace />id_seccional").val() == ""){
				alert("<liferay-ui:message key='seccional_invalida' />");
				jQuery("#<portlet:namespace />id_seccional").focus();
				return false;
			}
			if(jQuery("#<portlet:namespace />id_seccional").val() != "" && jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
				alert("<liferay-ui:message key='seccional_invalida' />");
				jQuery("#<portlet:namespace />id_seccional").focus();
				return false;
			}						
			var provSelect = document.getElementById("provinciaselect");
			if (trim(provSelect.options[provSelect.selectedIndex].innerHTML) == 'DESCONOCIDA') {
				alert("<liferay-ui:message key='provincia-obligatoria' />");
				jQuery('#<portlet:namespace />provinciaselect').focus();
				return false;
			}

			var <portlet:namespace/>localidad = document.getElementById("<portlet:namespace/>localidad");
			if (trim(<portlet:namespace/>localidad.options[<portlet:namespace/>localidad.selectedIndex].innerHTML) == 'DESCONOCIDA') {
				alert("<liferay-ui:message key='localidad-obligatoria' />");
				jQuery('#<portlet:namespace /><portlet:namespace/>localidad').focus();
				return false;
			}
			
			if (trim(calle).length == 0) {
				alert("<liferay-ui:message key='calle-obligatorio' />");
				jQuery('#<portlet:namespace />calle').focus();
				return false;
			}
			
			if (!isPositiveInteger(trim(cod_postal))){
				alert("<liferay-ui:message key='codigo-postal-invalido' />");
				jQuery('#<portlet:namespace />cod_postal').focus();
				return false;
			}	

			if(document.getElementById('<portlet:namespace/>ramo').selectedIndex == 0){
				alert("<liferay-ui:message key='ramo-obligatorio' />");
				jQuery('#<portlet:namespace />ramo').focus();
				return false;
			}

			if(trim(jQuery('#<portlet:namespace />telefono0_pais').val()) != '' ||
					trim(jQuery('#<portlet:namespace />telefono0_area').val()) != '' ||
					trim(jQuery('#<portlet:namespace />telefono0_numero').val()) != '' ||
					trim(jQuery('#<portlet:namespace />telefono0_ext').val()) != '' ){
						if ( trim(jQuery('#<portlet:namespace />telefono0_pais').val()) == '' ||
							trim(jQuery('#<portlet:namespace />telefono0_area').val()) == '' ||
							trim(jQuery('#<portlet:namespace />telefono0_numero').val()) == ''){
								alert("Si completa algun campo del telefono, debe completar necesariamente el codigo de pais, de area y el número");
								jQuery('#<portlet:namespace />telefono0_numero').focus();
								return false;
						}
				}
			
			if(trim(jQuery('#<portlet:namespace />telefono1_pais').val()) != '' ||
				trim(jQuery('#<portlet:namespace />telefono1_area').val()) != '' ||
				trim(jQuery('#<portlet:namespace />telefono1_numero').val()) != '' ||
				trim(jQuery('#<portlet:namespace />telefono1_ext').val()) != '' ){
					if ( trim(jQuery('#<portlet:namespace />telefono1_pais').val()) == '' ||
						trim(jQuery('#<portlet:namespace />telefono1_area').val()) == '' ||
						trim(jQuery('#<portlet:namespace />telefono1_numero').val()) == ''){
							alert("Si completa algun campo del telefono, debe completar necesariamente el codigo de pais, de area y el número");
							jQuery('#<portlet:namespace />telefono1_numero').focus();
							return false;
					}
			}

			if(trim(jQuery('#<portlet:namespace />telefono2_pais').val()) != '' ||
					trim(jQuery('#<portlet:namespace />telefono2_area').val()) != '' ||
					trim(jQuery('#<portlet:namespace />telefono2_numero').val()) != '' ||
					trim(jQuery('#<portlet:namespace />telefono2_ext').val()) != '' ){
						if ( trim(jQuery('#<portlet:namespace />telefono2_pais').val()) == '' ||
							trim(jQuery('#<portlet:namespace />telefono2_area').val()) == '' ||
							trim(jQuery('#<portlet:namespace />telefono2_numero').val()) == ''){
								alert("Si completa algun campo del telefono, debe completar necesariamente el codigo de pais, de area y el número");
								jQuery('#<portlet:namespace />telefono2_numero').focus();
								return false;
						}
				}

			if (trim(jQuery('#<portlet:namespace />email').val()) != '' && !validarMail(jQuery('#<portlet:namespace />email').val())){
				jQuery('#<portlet:namespace />email').focus();
				return false;
			}
		} catch (err) {
			return false;
		}
		return true;
	}

	function submitFormNotSave(){
		if (<portlet:namespace />validarCampos()) {
			document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
			document.getElementById("cambioSolapa").value="cambioSolapa";
			document.getElementById("tabs1").value="datos-fiscales";
			document.getElementById("view").value="true";
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_empleadores_entry" /></portlet:actionURL>';
			document.<portlet:namespace />emple.method = 'post';
			submitForm(document.<portlet:namespace />emple, url);
		}
	}

	function filtrarLocalidad() {		
		var idProvincia = jQuery('#provinciaselect').val();
		<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")){%>
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/id_provincia_localidad&idProvincia='+idProvincia;
		<%}else{%>
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_provincia_localidad&idProvincia='+idProvincia;
		<%}%>
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
		<%if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")){%>
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/id_localidad_codpostal&idLocalidad='+idLocalidad;
		<%}else{%>
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/id_localidad_codpostal&idLocalidad='+idLocalidad;
		<%}%>
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
				<% if(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_EST_1_")) {%>
					var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/estudio_isidro/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				<%}else{%>
					var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/afiliados/buscar_codPostal&calle='+escape(calle)+'&numero='+numero;
				<%}%>
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