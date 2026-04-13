<%@ include file="/html/portlet/utils/mailing/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

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
		<td>
				<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button"/>
		</td>		
		<td colspan="2">															
			<div id="<portlet:namespace />nuevaSubscriber">
				<input type="button" value="<liferay-ui:message key="nuevo-destinatario" />" onClick="nuevoDestinatario();" />
			</div>									
		</td>
	</tr>	
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<tr>
		<td>
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
					<liferay-util:param name="fromBusqueda" value="true"/>
				</liferay-util:include>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>	
</table>
<input type="hidden" id="<portlet:namespace />id_destinatario" name="<portlet:namespace />id_destinatario" />

<script type="text/javascript">	
	jQuery('#<portlet:namespace />agregandoContacto').hide();
	jQuery('#<portlet:namespace />agregandoLineas').hide();
	
	jQuery('#<portlet:namespace />buscar').click(function(){
		
		var nombre=jQuery('#<portlet:namespace />nombre').val();			
		var apellido=jQuery('#<portlet:namespace />apellido').val();			
		var email=jQuery('#<portlet:namespace />email').val();
		var telefono=jQuery('#<portlet:namespace />telefono_contacto').val();
		var tratamiento=jQuery('#<portlet:namespace />tratamiento').val();
		
		
		jQuery('#<portlet:namespace />agregandoContacto').show();
		//NUEVA LISTA EMPRESAS		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/buscar_subscriber&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)
		+'&tratamiento='+encodeURI(tratamiento)+'&email='+encodeURI(email);		
		jQuery('#<portlet:namespace />contactos').load(url, function() {
																		
																			jQuery('#<portlet:namespace />agregandoContacto').hide();
	        															  } );
	    
	});
	
	

	function borraContacto(id_destinatario){		
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/borrar_subscriber';		
			url=url+'&id_destinatario=' + encodeURI(id_destinatario);						
			jQuery('#<portlet:namespace />contactos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoContacto').hide();            															
																			   }
															   );
		}	
	}
	
	function editarDestinatario(id_destinatario){
		jQuery('#<portlet:namespace />id_destinatario').val(id_destinatario);
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/buscar_subscriber" /></portlet:renderURL>';
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = 'update';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function nuevoDestinatario(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/cgt_ddhh/editar_subscriber" /></portlet:renderURL>';
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = 'addNew';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	
</script>