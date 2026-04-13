<%@ include file="/html/portlet/utils/mailing/init.jsp"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<liferay-ui:error exception="<%= EmailYaRegistradoException.class %>" message="email-registrado" />
<% 

boolean showABMButtons = true;

ListaDestinatarios lista  = (ListaDestinatarios)portletSession.getAttribute(WebKeysGlobal.LISTA_DESTINATARIOS);

boolean esEdicion = false;

String cmd=lista!=null&&lista.getIdListaDestinatarios()!=0?"update":"";

%>

<form name="<portlet:namespace />org" id="<portlet:namespace />org">
<fieldset class="block-labels"><legend><liferay-ui:message key="lista-correo" /></legend>

<table class="lfr-table" width="100%">	
	<tr>		
		<td>
			<label><liferay-ui:message key="nombre-lista" />:</label>
		</td>
		<td colspan="5">
			<input id="<portlet:namespace />nombreLista" name="<portlet:namespace />nombreLista" size="50"  type="text" 
				value="<%=(null!=lista&&lista.getNombre()!=null)?lista.getNombre():""%>"/>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="5" align="center" valign="center">
			<textarea id="<portlet:namespace />observaciones" name="<portlet:namespace />observaciones" cols="160" rows="5"><%=(null!=lista&&lista.getObservaciones()!=null)?lista.getObservaciones():""%></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
</table>
<div id="div_formas_de_pago"  >
<table class="lfr-table" width="100%">
		<tr>
			<td width="100%">
				<fieldset class="block-labels">
					<legend><liferay-ui:message	key="contacto" /></legend>
					<liferay-util:include page="/html/portlet/utils/mailing/agregar_mail.jsp">
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
		
</table>
</div>
<div align="center">
	<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveMailing();return false;"/>
</div>
<input type="hidden" name="<portlet:namespace />cmd" id="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd!=null?cmd:""%>"/>

</fieldset>
</form>

				
<script type="text/javascript">
	
	var popup;
	function <portlet:namespace />saveMailing() {
		 var cmd=document.<portlet:namespace />org.<portlet:namespace />cmd.value;		 
		 if (trim(jQuery('#<portlet:namespace />nombre').val()) != "" ||
				 trim(jQuery('#<portlet:namespace />apellido').val())!= ""){
			 document.getElementById("<portlet:namespace />nombre").focus();
			 alert("Para agregar informacion sobre contactos debe presionar el boton 'Agregar'");
			 return false;
		 }
		//if (<portlet:namespace />validarCampos()) {
			if(cmd==""){
				document.<portlet:namespace />org.<portlet:namespace /><%= Constants.CMD %>.value='<%= Constants.ADD %>';
			}			
			var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_mailing_entry" /></portlet:actionURL>';			
			document.<portlet:namespace />org.method = 'post';			
			submitForm(document.<portlet:namespace />org, url);			
		//}
	}     
	
	function <portlet:namespace />validarCampos() {

		return true;
	}
	
	function <portlet:namespace />agregarArea(id_organismo){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_area_entry" /></portlet:renderURL>';
		document.<portlet:namespace />org.<portlet:namespace /><%= Constants.CMD %>.value = 'addnew';
		document.<portlet:namespace />org.<portlet:namespace />id_organismo.value = id_organismo;
		document.<portlet:namespace />org.method = 'post';
		submitForm(document.<portlet:namespace />org, url);
	
	}	
		
</script>


