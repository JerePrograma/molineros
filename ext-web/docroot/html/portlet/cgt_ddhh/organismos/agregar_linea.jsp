<%@ include file="/html/portlet/cgt_ddhh/init.jsp"%>
<%
boolean auditorActas = true;
Calendar current = CalendarFactoryUtil.getCalendar();

boolean esArea= Boolean.parseBoolean(ParamUtil.getString(request, "esArea"));

//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

%>
<table class="lfr-table" width="100%">
<%if(esEdicion){%>		
	<tr>
		<td>	
			<liferay-ui:message key='tipo' />:
		</td>
		<td>
			<select id="<portlet:namespace />tipo_linea" name="<portlet:namespace />tipo_linea"/>			
				<option value='LINEA'>LINEA</option>
			</select>										
		</td>
		<td>	
			<liferay-ui:message key='descripcion' />:
		</td>	
		<td>
			<select  name="<portlet:namespace/>id_linea" id="<portlet:namespace/>id_linea">
				<%	for (String amb : WebKeysCGT.LINEAS_TRABAJO) {	%>
						<option value="<%= amb %>">
							<%=amb%>
						</option>
				<%	}	%>					
			</select>													
		</td>		
		<td>
				<input type="button" value="<liferay-ui:message key="agregar"/>" onClick="<portlet:namespace />agregarLinea();" />
		</td>		
	</tr>	
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="5">
			<div align="center" id="<portlet:namespace />agregandoLineas">
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
		<td colspan="5" align="center" width="100%">
			<div align="center" id="<portlet:namespace />lineas">
				<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/lineas_search_result.jsp">						
					<liferay-util:param name="esArea" value="<%=String.valueOf(esArea) %>"/>						
				</liferay-util:include>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>	
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<script type="text/javascript">	
	jQuery('#<portlet:namespace />agregandoLineas').hide();	
	function <portlet:namespace />agregarLinea(){
			jQuery('#<portlet:namespace />agregandoLineas').show();
			var idLinea=jQuery('#<portlet:namespace />id_linea').val();			
			var tipoLinea=jQuery('#<portlet:namespace />tipo_linea').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/agregar_linea';
			
			url=url+'&id_linea=' +encodeURI(idLinea)+'&tipo_linea='+tipoLinea;
			<%if(esArea){%>	
			 	 url=url+'&isArea=true';
			<%}%>			
			
			jQuery('#<portlet:namespace />lineas').load(url, function() {
														jQuery('#<portlet:namespace />id_linea').val("");	
														jQuery('#<portlet:namespace />agregandoLineas').hide();});	
	}

	function borraLinea(id_linea){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{	
		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/cgt_ddhh/sacar_linea_organismo';
		
			url=url+'&id_linea=' +encodeURI(id_linea);
			<%if(esArea){%>	
		 	url=url+'&isArea=true';
			<%}%>			
			jQuery('#<portlet:namespace />lineas').load(url, function() {
																						jQuery('#<portlet:namespace />agregandoLineas').hide();            															
																			   }
															   );
		}	
	}
	
	
	
</script>