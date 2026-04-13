<%@ include file="/html/portlet/utils/mailing/init.jsp"%>
<liferay-ui:error exception="<%= EmailYaRegistradoException.class %>" message="email-registrado" />
<%

Destinatario dest=(Destinatario)portletSession.getAttribute(WebKeysGlobal.DESTINATARIO_EN_SESSION);
String[] listasDest=dest!=null?dest.getListas():null;
List<ListaDestinatarios> listas= (ArrayList<ListaDestinatarios>) portletSession.getAttribute(WebKeysGlobal.ALL_LISTAS_MAILING, PortletSession.APPLICATION_SCOPE);

//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
String cmd=dest!=null&&dest.getIdDestinatario()!=0?"update":"";
%>
<form name="<portlet:namespace />mailForm" id="<portlet:namespace />mailForm">
<table class="lfr-table" width="100%">	
	<tr>
		<td>		
			<liferay-ui:message key="tratamiento"/>:			
		</td>
		<td>			
			<input type="text" value="<%=(null!=dest&&dest.getTitle()!=null)?dest.getTitle():""%>" name="<portlet:namespace />tratamiento" id="<portlet:namespace />tratamiento" />
		</td>
		<td>		
			<liferay-ui:message key="nombre"/>:			
		</td>
		<td>			
			<input type="text" value="<%=(null!=dest&&dest.getFirstname()!=null)?dest.getFirstname():""%>" name="<portlet:namespace />nombre" id="<portlet:namespace />nombre" />
		</td>
		<td>			
			<liferay-ui:message key="apellido"/>:			
		</td>
		<td>			
			<input type="text" value="<%=(null!=dest&&dest.getLastname()!=null)?dest.getLastname():""%>" name="<portlet:namespace />apellido" id="<portlet:namespace />apellido" />
		</td>		
		
	</tr>	
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
		
	<tr>	
		<td>			
			<liferay-ui:message key="email"/>:			
		</td>
		<td>			
			<input type="text" value="<%=(null!=dest&&dest.getEmail()!=null)?dest.getEmail():""%>" name="<portlet:namespace />email" id="<portlet:namespace />email" />
		</td>
		<td>
			<liferay-ui:message key="incluido-lista"/>:
		</td>
		<td>						
			<select name="<portlet:namespace/>listas_mailing" id="<portlet:namespace/>listas_mailing" multiple="multiple" size="5">		
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
		<td>
			<liferay-ui:message key="marcar-prueba"/>:
		</td>
		<td>
			<input type="checkbox" name="<portlet:namespace />para_prueba" id="<portlet:namespace />para_prueba" value="true" <% if (dest != null && dest.isCasillaPrueba()) {%> checked="checked" <%} %>/>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>		
	<tr>	
		<td colspan="5" align="right">
				<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveMail();" />
		</td>		
		<td align="right" >															
			<div id="<portlet:namespace />nuevaSubscriber">
				<input type="button" value="<liferay-ui:message key="nuevo-destinatario" />" onClick="nuevoDestinatario();" />
			</div>									
		</td>
	</tr>	
	
</table>
<input type="hidden" name="<portlet:namespace />cmd" id="<portlet:namespace /><%= Constants.CMD %>" value="<%=cmd!=null?cmd:""%>"/>
<input type="hidden" name="<portlet:namespace />listas_mailing_p" id="<portlet:namespace />listas_mailing_p" value=""/>
<input type="hidden" value="<%=(null!=dest&&dest.getIdDestinatario()>0)?dest.getIdDestinatario():""%>" name="<portlet:namespace />id_destinatario" id="<portlet:namespace />id_destinatario" />
</form>


<script type="text/javascript">	
	function <portlet:namespace />saveMail() {		
		var listas=jQuery('#<portlet:namespace />listas_mailing').val();
		jQuery('#<portlet:namespace />listas_mailing_p').val(encodeURI(listas));		
		var cmd=document.<portlet:namespace />mailForm.<portlet:namespace />cmd.value;		 
		 
		if(cmd==""){
			document.<portlet:namespace />mailForm.<portlet:namespace /><%= Constants.CMD %>.value='<%= Constants.ADD %>';
		}		
		
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_subscriber" /></portlet:actionURL>';			
		document.<portlet:namespace />mailForm.method = 'post';			
		submitForm(document.<portlet:namespace />mailForm, url);			
		
	}     
	function nuevoDestinatario(){
		jQuery('#<portlet:namespace />tratamiento').val("");
		jQuery('#<portlet:namespace />nombre').val("");
		jQuery('#<portlet:namespace />apellido').val("");
		jQuery('#<portlet:namespace />email').val("");		
	}
</script>