<%@ include file="/html/portlet/administracion/init.jsp"%>
<%@ page
	import="ar.com.ospim.administracion.exception.EmailInvalidoException"%>

<%
	ReportesAutomaticosConfiguracion rac = (ReportesAutomaticosConfiguracion) renderRequest
			.getAttribute(WebKeysAdministracion.REPORTES_AUTOMATICOS_CONFIGURACION);
%>

<liferay-ui:error
	exception="<%= ar.com.ospim.administracion.exception.EmailInvalidoException.class %>"
	message="email-invalido" />

<fieldset class="block-labels"><legend>Editar configuracion reportes</legend>
<table class="lfr-table">
	<tr>
		<td>Mail desde el cual se envian los reportes:</td>
		<td><input type="text" id="<portlet:namespace />mails_from" name="<portlet:namespace />mails_from" value="<%=rac.getMailFrom()%>" size="40"/></td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td>Password del Mail desde el cual se envian los reportes:</td>
		<td><input type="text" id="<portlet:namespace />pass" name="<portlet:namespace />pass" value="<%=rac.getPass()%>" size="20"/></td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><input type="text" id="<portlet:namespace />mails_error" name="<portlet:namespace />mails_error" value="<%=rac.getMailsDeError()%>" size="100"/></td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2"><input id="<portlet:namespace />editar"
			value="<liferay-ui:message key="editar"/>"
			title="<liferay-ui:message key="editar" />" type="button"
			onclick="editarConfiguracion()" /></td>
	</tr>
</table>
</fieldset>

<script type="text/javascript">
	function editarConfiguracion(){
		var mailsFrom = jQuery('#<portlet:namespace />mails_from').val();
		var pass = jQuery('#<portlet:namespace />pass').val();
		var mailsError = jQuery('#<portlet:namespace />mails_error').val();

		if (trim(mailsFrom) == ""){
			alert("Debe ingresar un mail desde el cual enviar los reportes");
			return;
		}

		if (trim(pass) == ""){
			alert("Debe ingresar el password correspondiente al mail para enviar los reportes");
			return;
		}

		if (trim(mailsError) == ""){
			alert("Debe ingresar mails para reportar errores en la ejecucion de los reportes");
			return;
		} 

			
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/administracion/editar_configuracion_reportes_automaticos'
			+ '&mail_from=' + escape(trim(mailsFrom))
			+ '&pass=' + escape(trim(pass))
			+ '&mails_error=' + escape(trim(mailsError))
			+ '&rnd=' + Math.floor(Math.random()*100);
		jQuery(popupConfig).load(url);
	}

	<%String success = (String) request
						.getAttribute(WebKeysAdministracion.SUCCESS);
				if (success != null
						&& success.equals(WebKeysAdministracion.SUCCESS)) {%>
	if(popupConfig){
	Liferay.Popup.close(popupConfig);
	}
	<%}%>
</script>