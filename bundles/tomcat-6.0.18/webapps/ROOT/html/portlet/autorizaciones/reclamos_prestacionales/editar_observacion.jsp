<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />
<%

/*
boolean showReadOnlyReclamPrestac=PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_CONSULTA_RECLAMOS_PRESTACIONALES);
*/
/*
String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}
*/

String cmd = "";
int idReclamoAux = (int) request.getSession().getAttribute("EditarObservacion");

/*
PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/autorizaciones/editar_reclamosobservacion");
portletURL.setParameter("cmd", cmd);
portletURL.setParameter("reclamo_id", String.valueOf(idReclamoAux));
*/

String nroreclamo = "Reclamo Nro : ";
nroreclamo += String.valueOf(idReclamoAux); 

%>
<style>
div.divheaderNroReclamo {
	background-color: #cccccc;
	width: 250px;
	height: 20px;
	border: 1px solid black;
	font-size: 145%
}
</style>

<liferay-ui:error key="error-estado-reclamo" message="falta-estado-reclamo-prestacion" />
<liferay-ui:error key="error-fechaseccional-reclamo" message="falta-fechaseccional-reclamo-prestacion" />
<liferay-ui:error key="error-fechaingresoospim-reclamo" message="falta-fechaingresoospim-reclamo-prestacion" />
<liferay-ui:error key="error-cargos-reclamo" message="completar-area-medica" />
<liferay-ui:error key="error-comprobante-invalido" message="tipo-comprobante-invalido" />
<liferay-ui:error key="error-comprobante-duplicado" message="comprobante-duplicado" />

<liferay-ui:error key="errorCuentaReclamo" message="<%=(String)request.getAttribute(\"msgErrorCuentaReclamo\") %>" />


<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="Cabecera Caso" />
	</legend>

	<table>
		<tr>
			<td>
				<div class="divheaderNroReclamo">
					<label><b><liferay-ui:message key="<%=nroreclamo%>" /></b></label>
				</div> <input type="hidden" id="<portlet:namespace />id_reclamo"
				name="<portlet:namespace />id_reclamo" value="<%=idReclamoAux%>" />
			</td>
		</tr>
		<tr>
			<%-- <td colspan="8"><liferay-ui:message key="observacion" />:</td>--%>

			<td><liferay-ui:message key="observacion" />: <textarea
					rows="2" cols="100"
					id="<portlet:namespace />agregar_obs_prestacion" maxlength="250"
					name="<portlet:namespace />agregar_obs_prestacion"></textarea> <br>
				<b><liferay-ui:message
						key="La observación de 200 caracteres como máximo." /></b></td>
			<td colspan="12">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td><input type="button"
				value="<liferay-ui:message key="agregar-guardar" />"
				onClick="<portlet:namespace />agregarObservacion();"
				id="<portlet:namespace />buttonaddobservacion"
				name="<portlet:namespace />buttonaddobservacion"
				title="<liferay-ui:message key="agregar-guardar" />" /></td>
		</tr>

	</table>


</fieldset>

<script type="text/javascript">


function <portlet:namespace />agregarObservacion() {

	var _idreclamo = jQuery("#<portlet:namespace />id_reclamo").val();
	var _obs = jQuery("#<portlet:namespace />agregar_obs_prestacion").val();
	if (_obs == null)
		_obs = '';
	
	if (_obs.trim().length <= 5) {
		alert('Por favor, ingrese Observación');		
	} else {
			
		try {
			
			var accionEnCurso = document.<portlet:namespace />reclamo_fm.<portlet:namespace /><%= Constants.CMD %>.value;
			document.<portlet:namespace />reclamo_fm.<portlet:namespace /><%= Constants.CMD %>.value='<%=Constants.RESET %>'; 
		
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_reclamosprestaciones_entry" /></portlet:actionURL>';
			url = url + '&accionEnCurso=' + accionEnCurso + "&idreclamo=" + _idreclamo + "&obs=" + _obs;
			document.<portlet:namespace />reclamo_fm.method = 'post';
					
			submitForm(document.<portlet:namespace />reclamo_fm, url);
			
		}
		catch (err) {
			alert(err);
		}
	}
  	
}
</script>

