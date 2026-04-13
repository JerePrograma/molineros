<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<portlet:defineObjects/>
	<br/>	

 	<c:choose>		
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
			<liferay-ui:success key="request_processed" message="grabar-exitoso" />
				<div align="center" id="<portlet:namespace />buscandoCuota">
	<table style="align: center;">
		<tr>
			<td><liferay-ui:message key='buscando' /></td>
			<td align="center"><img
				alt="<liferay-ui:message key='buscando'/>"
				src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
			</td>
		</tr>
	</table>
	</div>
			
		</c:when>		
	</c:choose>

	<script type="text/javascript">
	 	<c:choose>
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
			document.getElementById("<portlet:namespace />id_liquidacion").value = '<%= request.getAttribute(WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION) == null ? 0 : (Integer)request.getAttribute(WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION) %>';
			document.getElementById("<portlet:namespace />periodoMesAnio").disabled = true;
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/generar_nota_debito_periodo&id_liquidacion='+<%= request.getAttribute(WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION) == null ? 0 : (Integer)request.getAttribute(WebKeysLiquidaciones.ID_LIQUIDACION_EN_EDICION) %>;		
			jQuery(popupAltaDT).load(url);
		</c:when>		 
		</c:choose>			
 	</script>	