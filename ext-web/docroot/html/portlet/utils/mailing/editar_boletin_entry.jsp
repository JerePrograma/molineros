<%@ include file="/html/portlet/utils/mailing/init.jsp"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<liferay-ui:error exception="<%= EmailYaRegistradoException.class %>" message="email-registrado" />
<% 

boolean showABMButtons = true;


Boletin boletin  = (Boletin) portletSession.getAttribute(WebKeysGlobal.BOLETIN_EN_EDICION);

String[] listasDest=boletin!=null?boletin.getListas():null;

List<ListaDestinatarios> listas= (ArrayList<ListaDestinatarios>) portletSession.getAttribute(WebKeysGlobal.ALL_LISTAS_MAILING, PortletSession.APPLICATION_SCOPE);

boolean esEdicion = false;

String cmd=boletin!=null&&boletin.getIdBoletin()!=0?"update":"";

%>
<form name="<portlet:namespace />org" id="<portlet:namespace />org">
<fieldset class="block-labels"><legend><liferay-ui:message key="boletin" /></legend>

<table class="lfr-table" width="100%">	
	<tr>		
		<td>
			<label><liferay-ui:message key="nombre-boletin" />:</label>
		</td>
		<td>
			<input id="<portlet:namespace />nombreBoletin" name="<portlet:namespace />nombreBoletin" size="50"  type="text" 
				value="<%=(null!=boletin&&boletin.getNombre()!=null)?boletin.getNombre():""%>"/>
		</td>
		<td>
			<label><liferay-ui:message key="subject" />:</label>
		</td>
		<td colspan="3">
			<input id="<portlet:namespace />asuntoBoletin" name="<portlet:namespace />asuntoBoletin" size="50"  type="text" 
				value="<%=(null!=boletin&&boletin.getAsunto()!=null)?boletin.getAsunto():""%>"/>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="5">
			<textarea id="<portlet:namespace />observaciones" name="<portlet:namespace />observaciones" cols="160" rows="1"><%=(null!=boletin&&boletin.getObservaciones()!=null)?boletin.getObservaciones():""%>
			</textarea>
		</td>		
		<td><label><liferay-ui:message key="solo-texto" />:</label></td>
		<td><input type="checkbox" id="<portlet:namespace />solo_texto"
					name="<portlet:namespace />solo_texto" value="true"
					<%if (boletin != null && boletin.isSoloTexto()) {%>
					checked="checked" <%}%>/></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="lista-correo" />:</label></td>
		<td colspan="5">						
			<select name="<portlet:namespace/>listas_mailing" id="<portlet:namespace/>listas_mailing" multiple="multiple" size="10">		
					<option value=""></option>		
					<%try {
						for (ListaDestinatarios lista : listas) {	%>
							<option 
								<%if(null!=listasDest && !listasDest[0].trim().equals("")){
										for(String id_lista: listasDest){
											if(Integer.parseInt(id_lista.trim())==lista.getIdListaDestinatarios()){%>											
												SELECTED
											<%}
										}
								  }%>
								  value="<%= lista.getIdListaDestinatarios()%>"> <%=lista.getNombre()%>								  
							</option>
						<%}
					  } catch(Exception e) {
							e.printStackTrace();
					  }%>
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
</table>
<div id="div_formas_de_pago" align="center" >
<table class="lfr-table" width="100%">
		<tr>
			<td width="100%">
				<fieldset class="block-labels">
					<legend><liferay-ui:message	key="contenido" /></legend>
					<liferay-util:include page="/html/portlet/utils/mailing/agregar_contenido.jsp">
						<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
					</liferay-util:include>
				</fieldset>
			</td>
		</tr>
		
</table>
</div>
<br/>
<div align="center">
	<table>
		<tr>
			<td>
				<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveBoletin();return false;"/>
			</td>
			<td>
				&nbsp;
			</td>
			<%if(boletin!=null && boletin.getIdBoletin()>0){%>
				<td>
					<input type="button" value="<liferay-ui:message key="send" />" onClick="<portlet:namespace />enviarBoletin('TODOS');return false;"/>
				</td>
				<td>
					&nbsp;
				</td>
				<td>
					<input type="button" value="<liferay-ui:message key="send-test" />" onClick="<portlet:namespace />enviarBoletin('PRUEBA');return false;"/>
				</td>
			<%}%>
		</tr>
	</table>
</div>
<input type="hidden" name="<portlet:namespace />cmd" id="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd!=null?cmd:""%>"/>
<input type="hidden" name="<portlet:namespace />listas_mailing_h" id="<portlet:namespace />listas_mailing_h"/>
<input type="hidden" name="<portlet:namespace />tipo_destinatarios" id="<portlet:namespace />tipo_destinatarios"/>

</fieldset>
</form>

				
<script type="text/javascript">	
	var popup;
	function <portlet:namespace />saveBoletin() {
		 var cmd=document.<portlet:namespace />org.<portlet:namespace />cmd.value;		
		 var listas=jQuery('#<portlet:namespace/>listas_mailing').val();
		 jQuery('#<portlet:namespace />listas_mailing_h').val(encodeURI(listas));
		 if (trim(jQuery('#<portlet:namespace />titulo_contenido').val()) != "" ||
				 trim(jQuery('#<portlet:namespace />contenido').val())!= ""){
			 document.getElementById("<portlet:namespace />nombre").focus();
			 alert("Para agregar contenido sobre contactos debe presionar el boton 'Agregar'");
			 return false;
		 }
		if(cmd==""){
			document.<portlet:namespace />org.<portlet:namespace /><%= Constants.CMD %>.value='<%= Constants.ADD %>';
		}			
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_boletin_entry" /></portlet:actionURL>';			
		document.<portlet:namespace />org.method = 'post';			
		submitForm(document.<portlet:namespace />org, url);
	}     
	
	function <portlet:namespace />validarCampos() {

		return true;
	}
	
		
	function <portlet:namespace />enviarBoletin(tipo){
		jQuery('#<portlet:namespace />tipo_destinatarios').val(tipo);
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/enviar_boletin" /></portlet:renderURL>';
		document.<portlet:namespace />org.<portlet:namespace /><%= Constants.CMD %>.value = 'addnew';		
		document.<portlet:namespace />org.method = 'post';
		submitForm(document.<portlet:namespace />org, url);
	
	}	
		
</script>


