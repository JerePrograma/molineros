<%@ include file="/html/portlet/empresas/init.jsp"%>
<%@ page import = "ar.com.ospim.util.StringUtils" %>
<%
Empresa empresa  = (Empresa)request.getSession().getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION);

List<Provincia> provincias = (ArrayList<Provincia>) portletSession
.getAttribute(WebKeysEmpresas.PROVINCIAS_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

List<Localidad> localidades = (ArrayList<Localidad>) portletSession
.getAttribute(WebKeysEmpresas.LOCALIDADES_EN_SESSION,
PortletSession.APPLICATION_SCOPE);

List<EntidadCamaraEmpresa> entidades = (ArrayList<EntidadCamaraEmpresa>) portletSession
.getAttribute(WebKeysEmpresas.ENTIDADESCAMARAEMPRESA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);


boolean esEdicion = true;


%>
<fieldset class="block-labels">
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="camara" />:</label></td>
		<td><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>entidad"
			id="<portlet:namespace/>entidad">
			<option value=""></option>
			<% for (EntidadCamaraEmpresa ent: entidades) { %>
			<option
				<%= empresa != null && empresa.getEntidadCamaraEmpresa()!= null && empresa.getEntidadCamaraEmpresa().equals(ent) ? "selected" : ""  %>
				value="<%= ent.getId_entidad_cam_empresa() %>"><%=ent.getDescripcion()%></option>
			<% } %>
		</select></td>
		<td><label><liferay-ui:message key="posicion-iva" />:</label></td>
		<td><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>iva"
			id="<portlet:namespace/>iva">
			<option value=""></option>
			<option value="NI" <%=empresa != null && empresa.getImpIva()!= null && empresa.getImpIva().equals("NI") ? "selected" : ""%>>No Inscripto</option>
			<option value="AC" <%=empresa != null && empresa.getImpIva()!= null && empresa.getImpIva().equals("AC") ? "selected" : ""%>>Activo</option>
			<option value="EX" <%=empresa != null && empresa.getImpIva()!= null && empresa.getImpIva().equals("EX") ? "selected" : ""%>>Exento</option>
			<option value="NA" <%=empresa != null && empresa.getImpIva()!= null && empresa.getImpIva().equals("NA") ? "selected" : ""%>>No Alcanzado</option>
			<option value="XN" <%=empresa != null && empresa.getImpIva()!= null && empresa.getImpIva().equals("XN") ? "selected" : ""%>>Exento no alcanzado</option>
			<option value="AN" <%=empresa != null && empresa.getImpIva()!= null && empresa.getImpIva().equals("AN") ? "selected" : ""%>>Activo no alcanzado</option>
			<option value="NC" <%=empresa != null && empresa.getImpIva()!= null && empresa.getImpIva().equals("NC") ? "selected" : ""%>>No Corresponde</option>			  			
			</select>
		</td>
		<td><label><liferay-ui:message key="posicion-ganancias" />:</label></td>
		<td><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>gananacias"
			id="<portlet:namespace/>ganancias">
			<option value=""></option>
			<option value="NI" <%=empresa != null && empresa.getImpGanancias()!= null && empresa.getImpGanancias().equals("NI") ? "selected" : ""%>>No Inscripto</option>
			<option value="AC" <%=empresa != null && empresa.getImpGanancias()!= null && empresa.getImpGanancias().equals("AC") ? "selected" : ""%>>Activo</option>
			<option value="EX" <%=empresa != null && empresa.getImpGanancias()!= null && empresa.getImpGanancias().equals("EX") ? "selected" : ""%>>Exento</option>
			<option value="NC" <%=empresa != null && empresa.getImpGanancias()!= null && empresa.getImpGanancias().equals("NC") ? "selected" : ""%>>No Corresponde</option>			  			
			</select>
		</td>
		<td><label><liferay-ui:message key="cai-cae" />:</label></td>
		<td><select <% if (!esEdicion) { %>
			<%="disabled='disabled'" %> <%}%> name="<portlet:namespace/>tipo_cai_cae"
			id="<portlet:namespace/>tipo_cai_cae">
			<option value="">Seleccione una opción</option>
			<option value="CAE" <%=empresa != null && empresa.getCaeCai()!= null && empresa.getCaeCai().equalsIgnoreCase("CAE") ? "selected" : ""%> ><liferay-ui:message key="cae" /></option>
			<option value="CAI" <%=empresa != null && empresa.getCaeCai()!= null && empresa.getCaeCai().equalsIgnoreCase("CAI") ? "selected" : ""%> ><liferay-ui:message key="cai" /></option>
		</select></td>
		<td><input type="text" maxlength="14" onkeydown="allowOnlyDigits(event);" name="<portlet:namespace/>numero_cai_cae" id="<portlet:namespace/>numero_cai_cae"
			value="<%=empresa != null && StringUtils.checkNotEmpty(empresa.getNumeroCaeCai())?empresa.getNumeroCaeCai():"" %>" ></td>
	</tr>	
</table>
</fieldset>


<% if (esEdicion) { %>
<br />
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa" />
<input type="hidden" value="" name="tabs1" id="tabs1" />
<input type="hidden" value="" name="view" id="view" />
<input type="hidden"
	value="<%= request.getAttribute("accionOriginal")%>"
	name="accionOriginal" id="accionOriginal" />
<%} %>
<script type="text/javascript">

function submitFormNotSave(){

	document.<portlet:namespace />emple.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
	document.getElementById("cambioSolapa").value="cambioSolapa";
	document.getElementById("tabs1").value="datos";
	document.getElementById("view").value="true";
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_empleadores_entry';
	document.<portlet:namespace />emple.method = 'post';
	submitForm(document.<portlet:namespace />emple, url);
	
}

function addElementToSelect(id_combo, texto, valor) {
	var combo = document.getElementById(id_combo);
	var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
	combo.options[idxElemento] = new Option();
	combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
	combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
}

function <portlet:namespace />validarCampos() {		
	return true;
}

</script>