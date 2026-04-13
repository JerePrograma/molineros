<%@ include file="/html/portlet/cgt_ddhh/init.jsp"%>
<%

//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

%>
<table class="lfr-table" width="100%">	
	<tr>
		<td>		
			<liferay-ui:message key="titulo-contenido"/>:			
		</td>
		<td>			
			<input type="text" value="" name="<portlet:namespace />titulo_contenido" id="<portlet:namespace />titulo_contenido" />
		</td>
		<td rowspan="2">		
			<liferay-ui:message key="contenido"/>:			
		</td>
		<td rowspan="2">
			<textarea id="<portlet:namespace />contenido" name="<portlet:namespace />contenido" cols="120" rows="6"></textarea>
		</td>
	</tr>	
	<tr>
		<td>		
			<liferay-ui:message key="seccion"/>:			
		</td>
		<td>
			<select name="<portlet:namespace/>seccion" id="<portlet:namespace/>seccion">
				<%for (String seccion : WebKeysGlobal.SECCIONES_MAIL) {	%>												
						<option value="<%= seccion %>"><%=seccion%></option>					
				<%}%>
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td align="center" colspan="4" >
				<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarContenido();" />
		</td>		
	</tr>	
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td>
			<div align="center" id="<portlet:namespace />agregandoContenido">
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
			<div align="center" id="<portlet:namespace />contenidos">
				<liferay-util:include page="/html/portlet/utils/mailing/contenido_search_result.jsp">
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
	jQuery('#<portlet:namespace />agregandoContenido').hide();	
	function <portlet:namespace />agregarContenido(){
			jQuery('#<portlet:namespace />agregandoContenido').show();	
			var titulo=jQuery('#<portlet:namespace />titulo_contenido').val();			
			var contenido=jQuery('#<portlet:namespace />contenido').val();
			var seccion=jQuery('#<portlet:namespace />seccion').val();
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/agregar_contenido_boletin';			
			url=url+'&titulo_contenido=' +encodeURI(titulo) 
						+'&contenido=' + encodeURI(contenido)
						+'&seccion=' + encodeURI(seccion);
									
			jQuery('#<portlet:namespace />contenidos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoContenido').hide();	
														 jQuery('#<portlet:namespace />titulo_contenido').val("");
														 jQuery('#<portlet:namespace />contenido').val("");
										   }
			 );	
	}
	

	function borraContenido(titulo){		
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/borrar_contenido_boletin';		
			url=url+'&titulo=' + encodeURI(titulo);						
			jQuery('#<portlet:namespace />contenidos').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoContenido').hide();            															
																			   }
															   );
		}	
	}
	
	
	
</script>