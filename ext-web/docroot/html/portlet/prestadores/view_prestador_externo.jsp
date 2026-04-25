<%@ include file="/html/portlet/prestadores/init.jsp" %>
<%

int id_prestador_ext = 0; 
id_prestador_ext = ParamUtil.getInteger(request, "id_prestador_ext");

PrestadorExterno prestador = null;
prestador = PrestadorExternoServiceUtil.getPrestadorExterno(id_prestador_ext);

List<Provincia> provincias = (ArrayList<Provincia>) portletSession
		.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
				PortletSession.APPLICATION_SCOPE);

List<PosicionIva> posicionesIva = (ArrayList<PosicionIva>) portletSession
.getAttribute(WebKeysAfiliados.POSICIONESIVA_EN_SESSION,
		PortletSession.APPLICATION_SCOPE);

boolean esEdicion = false;

if (prestador == null) {
	esEdicion = true;
}

%>

<input name="<portlet:namespace />prestadExt<%= Constants.CMD %>" type="hidden" value="" />
<fieldset class="block-labels"><legend><liferay-ui:message
	key="datos-prestador" /></legend>
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="descripcion" />:</label></td>
		<td colspan="2"><input id="<portlet:namespace />desc"
		name="<portlet:namespace />desc" size="50"
		type="text"
		value="<%= prestador != null ? prestador.getDescripcion() : "" %>"
		/></td>
		<td><label><liferay-ui:message key="cod-prestador" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />id_prestador_ext"
			name="<portlet:namespace />id_prestador_ext" size="7"
			type="text"
			value="<%= prestador != null ? prestador.getId_prestadorString() : "" %>"
			readonly='readonly' />
		</td>
		<td><label><liferay-ui:message key="cuit" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />cuit"
		name="<portlet:namespace />cuit" size="13" maxlength="11"
		type="text"
		value="<%= prestador != null ? prestador.getCuit() : "" %>"
		/></td>
		<td colspan="1"><label><liferay-ui:message key="iva" />:</label></td>
		<td colspan="1">
		<select name="<portlet:namespace/>iva" id="<portlet:namespace/>iva">
			<option value=""></option>
			<% for (PosicionIva pos: posicionesIva) { %>
			<option
				<%= prestador != null && prestador.getPosicionIva()!= null && prestador.getPosicionIva().equals(pos) ? "selected" : ""  %>
				value="<%= pos.getId() %>"><%=pos.getDescripcion()%></option>
			<% } %>
		</select>
		</td>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>	
	<tr>
		<td colspan="9">
		<table>
		<tr>
			<td><label><liferay-ui:message key="matricula" />:</label></td>
			<td>
				<select name="mat_tipo" id="mat_tipo">
					<option value=""></option>
					<option value="N" <%= prestador != null && prestador.getTipo_matricula() != null  && prestador.getTipo_matricula().equals("N") ? "selected" : "" %>>NACIONAL</option>
					<option value="P" <%= prestador != null && prestador.getTipo_matricula() != null  && prestador.getTipo_matricula().equals("P") ? "selected" : "" %>>PROVINCIAL</option>
					<option value="O" <%= prestador != null && prestador.getTipo_matricula() != null  && prestador.getTipo_matricula().equals("O") ? "selected" : "" %>>OTRO</option>
				</select>
			</td>
			<td><label><liferay-ui:message key="numero" />:</label></td>
			<td><input id="mat_numero"
			name="mat_numero" size="6" maxlength="6"
			type="text"
			value="<%= prestador != null && prestador.getNro_matricula() != 0 ? prestador.getNro_matricula() : "" %>"
			/></td>			
			<td><label><liferay-ui:message key="provincia" />:</label></td>
			<td><select name="mat_provincia" id="mat_provincia">
				<% for (Provincia provincia : provincias) { %>
				<option
					<%= prestador != null && prestador.getProvinciaMatricula() != null && prestador.getProvinciaMatricula().getId() == provincia.getId() ? "selected" : ""  %>
					value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
				<% } %>
				</select>
			</td>
			<td><label><liferay-ui:message key="categoria" />:</label></td>
			<td>
				<select name="mat_categoria" id="mat_categoria">
					<option value=""></option>
					<option value="A" <%= prestador != null && prestador.getId_mat_categoria() != null  && prestador.getId_mat_categoria().equals("A") ? "selected" : "" %>>A</option>
					<option value="B" <%= prestador != null && prestador.getId_mat_categoria() != null  && prestador.getId_mat_categoria().equals("B") ? "selected" : "" %>>B</option>
					<option value="C" <%= prestador != null && prestador.getId_mat_categoria() != null  && prestador.getId_mat_categoria().equals("C") ? "selected" : "" %>>C</option>
					<option value="X" <%= prestador != null && prestador.getId_mat_categoria() != null  && prestador.getId_mat_categoria().equals("X") ? "selected" : "" %>>X</option>
				</select>
			</td>			
		</tr>
		</table>
	</td>
	</tr>
	<tr><td colspan="9">&nbsp;</td></tr>
	<tr>
		<td>
		<input type="button" value="<liferay-ui:message key="save" />" id="savePrestadorExt_" onClick="<portlet:namespace />savePrestador(); return false;"/>
		<input type="hidden" value="<%= prestador != null ? prestador.getId_prestador() : "" %>" name="prestador_id" id="prestador_id"/>
		<input type="hidden" value="<%= (prestador == null ? Constants.ADD : Constants.UPDATE) %>" name="accionOriginal" id="accionOriginal"/>		
		</td>	
	</tr>
 <tr> 
<td>
<div align="center" id="<portlet:namespace />buscandoPrestador" name="<portlet:namespace />buscandoPrestador">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
</td>
</tr>
<tr>
<td>
<div align="center" id="<portlet:namespace />prestadores_resultado">
</div>
</td>
</tr>
</table>
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscandoPrestador').hide();
	
	function <portlet:namespace />savePrestador() {

		var id_prestador_ext = document.getElementById("<portlet:namespace />id_prestador_ext").value;
		var desc = document.getElementById("<portlet:namespace />desc").value;
		var cuit = document.getElementById("<portlet:namespace />cuit").value;
		var mat_tipo = document.getElementById("mat_tipo").value;
		var mat_numero = document.getElementById("mat_numero").value;
		var mat_provincia = document.getElementById("mat_provincia").value;
		var mat_categoria = document.getElementById("mat_categoria").value;		
		var iva = document.getElementById("<portlet:namespace/>iva").value;						
		var accionOriginal = document.getElementById("accionOriginal").value;
		
		if (<portlet:namespace />validarCamposBP()) {
				jQuery('#<portlet:namespace />buscandoPrestador').show();

			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/editar_prestador_externo_entry&id_prestador_ext='+id_prestador_ext+'&desc='+encodeURI(desc)+'&cuit='+cuit+'&mat_tipo='+mat_tipo+'&mat_provincia='+mat_provincia+'&mat_categoria='+mat_categoria+'&mat_numero='+mat_numero+'&iva='+iva+'&accionOriginal='+accionOriginal;
							
			jQuery('#<portlet:namespace />prestadores_resultado').load(url, function() {
				jQuery('#<portlet:namespace />buscandoPrestador').hide();});		
		}
	}
	
	function <portlet:namespace />validarCamposBP() {
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		try {
			if (trim(jQuery("#<portlet:namespace />desc").val()).length == 0) {
				alert("<liferay-ui:message key='nombre-obligatorio' />");
				jQuery('#<portlet:namespace />desc').focus();
				return false;
			}
			if(trim(cuit).length != 0 && !validarCuil(cuit,"<liferay-ui:message key='valida-cuil'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}			
			if (document.getElementById("mat_tipo").selectedIndex == 0 ||
				trim(document.getElementById("mat_provincia").options[document.getElementById("mat_provincia").selectedIndex].text) == "DESCONOCIDO" ||				
				trim(document.getElementById("mat_numero").value) == ""){
				alert("<liferay-ui:message key='datos-matricula-obligatorios' />");
				document.getElementById("mat_numero").focus();
				return false;
			}
		} catch (err) {
			return false;
		}
		return true;
	}
	
</script>

