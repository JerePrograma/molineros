<%@ include file="/html/portlet/cgt_ddhh/init.jsp"%>
<%

Calendar current = CalendarFactoryUtil.getCalendar();
Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
boolean esArea= Boolean.parseBoolean(ParamUtil.getString(request, "esArea"));

%>
<table class="lfr-table" width="100%">
<%if(esEdicion){%>			
	<tr>		
		<td><label><liferay-ui:message key="fecha-acta" />:</label></td>
		<td>
				<liferay-ui:input-date
				dayParam="fechaInicioDia"
				dayValue="<%= fechaInicio.get(Calendar.DATE) %>" 
				monthParam="fechaInicioMes"
				monthValue="<%= fechaInicio.get(Calendar.MONTH) %>"				
				yearParam="fechaInicioAnio"
				yearValue="<%= fechaInicio.get(Calendar.YEAR) %>"
				yearRangeStart="<%= current.get(Calendar.YEAR) - 1 %>"
				yearRangeEnd="<%= fechaInicio.get(Calendar.YEAR) + 1%>"
				firstDayOfWeek="<%= fechaInicio.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion   %>" />
		</td>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>		
		<td>
			<textarea id="<portlet:namespace />comentario" name="<portlet:namespace />comentario" cols="100" rows="5"></textarea>
		</td>		
		<td>
				<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarComentario();" />
		</td>	
	</tr>	
	<tr>
		<td colspan="11">&nbsp;</td>
	</tr>
	<tr>
		<td>
			<div align="center" id="<portlet:namespace />agregandoComentarios">
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
<%}%>	
	<tr>
		<td colspan="11" align="center">
			<div align="center" id="<portlet:namespace />comentarios">
				<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/comentarios_search_result.jsp">						
					<liferay-util:param name="esArea" value="<%=String.valueOf(esArea) %>"/>						
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
	jQuery('#<portlet:namespace />agregandoComentarios').hide();	
	function <portlet:namespace />agregarComentario(){
		jQuery('#<portlet:namespace />agregandoComentarios').show();
		var fechaComentarioDia=trim(document.getElementById("<portlet:namespace />fechaInicioDia").value);
		var fechaComentarioMes=trim(document.getElementById("<portlet:namespace />fechaInicioMes").value);
		var fechaComentarioAnio=trim(document.getElementById("<portlet:namespace />fechaInicioAnio").value);
		var comentario=jQuery('#<portlet:namespace />comentario').val();
			
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/agregar_comentario';
			
		url=url+'&comentario=' +encodeURI(comentario);
		url=url+'&dia=' +fechaComentarioDia;
		url=url+'&mes=' +fechaComentarioMes;
		url=url+'&anio=' +fechaComentarioAnio;
		<%if(esArea){%>	
		 	 url=url+'&isArea=true';
		<%}%>			
			
			jQuery('#<portlet:namespace />comentarios').load(url, function() {
														jQuery('#<portlet:namespace />agregandoComentarios').hide();});	
	}

	function borraComentario(fecha, comentario){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/sacar_comentario_organismo';
		
			url=url+'&comentario=' +encodeURI(comentario);
			url=url+'&fecha=' +fecha;
			<%if(esArea){%>	
		 	url=url+'&isArea=true';
			<%}%>			
						
			jQuery('#<portlet:namespace />comentarios').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoComentarios').hide();            															
																			   }
															   );
		}	
	}
	
	
	
</script>