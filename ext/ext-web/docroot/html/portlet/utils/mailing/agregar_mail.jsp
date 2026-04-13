<%@ include file="/html/portlet/cgt_ddhh/init.jsp"%>
<%

//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

%>
<table class="lfr-table" width="100%">	
	<tr>
		<td>		
			<liferay-ui:message key="tratamiento"/>:			
		</td>
		<td>			
			<input type="text" value="" name="<portlet:namespace />tratamiento" id="<portlet:namespace />tratamiento" />
		</td>
		<td>		
			<liferay-ui:message key="nombre"/>:			
		</td>
		<td>			
			<input type="text" value="" name="<portlet:namespace />nombre" id="<portlet:namespace />nombre" />
		</td>
		<td>			
			<liferay-ui:message key="apellido"/>:			
		</td>
		<td>			
			<input type="text" value="" name="<portlet:namespace />apellido" id="<portlet:namespace />apellido" />
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
			<input type="text" value="" name="<portlet:namespace />email" id="<portlet:namespace />email" />
		</td>
		<td colspan="4" >
				<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarContacto();" />
		</td>		
	</tr>	
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6" align="center">
			<div align="center" id="<portlet:namespace />agregandoContacto">
			<table style="align: center;">
				<tr>
					<td><liferay-ui:message key='buscando' /></td>
					<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="11" align="center">
			<div align="center" id="<portlet:namespace />contactos">
				<liferay-util:include page="/html/portlet/utils/mailing/mails_search_result.jsp">
				</liferay-util:include>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>	
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<script type="text/javascript">
	jQuery('#<portlet:namespace />agregandoContacto').hide();
	jQuery('#<portlet:namespace />agregandoLineas').hide();
	function <portlet:namespace />agregarContacto(){
			jQuery('#<portlet:namespace />agregandoContacto').show();	
			var nombre=jQuery('#<portlet:namespace />nombre').val();			
			var apellido=jQuery('#<portlet:namespace />apellido').val();
			var idCargo=jQuery('#<portlet:namespace />cargo').val();			
			var email=jQuery('#<portlet:namespace />email').val();
			var telefono=jQuery('#<portlet:namespace />telefono_contacto').val();
			var tratamiento=jQuery('#<portlet:namespace />tratamiento').val();
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/agregar_mail_mailing';			
			url=url+'&nombre=' +encodeURI(nombre) 
						+'&apellido=' + encodeURI(apellido)						 
						+'&email=' + encodeURI(email)						
						+'&tratamiento='+encodeURI(tratamiento);
									
			jQuery('#<portlet:namespace />contactos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoContacto').hide();	
														 jQuery('#<portlet:namespace />nombre').val("");
														 jQuery('#<portlet:namespace />apellido').val("");						
														 jQuery('#<portlet:namespace />email').val("");
														 jQuery('#<portlet:namespace />tratamiento').val("");
										   }
			 );	
	}
	

	function borraContacto(email){		
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/borrar_mail_mailing';		
			url=url+'&email=' + encodeURI(email);						
			jQuery('#<portlet:namespace />contactos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoContacto').hide();            															
																			   }
															   );
		}	
	}
	
	
	
</script>