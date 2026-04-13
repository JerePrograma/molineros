<%@ include file="/html/portlet/uoma/init.jsp"%>
<%
Factura factura = (Factura) portletSession.getAttribute(WebKeysUOMA.FACTURA_EN_EDICION,PortletSession.APPLICATION_SCOPE);
Calendar current = CalendarFactoryUtil.getCalendar();
boolean esEdicion = ParamUtil.getBoolean(request, "esEdicion", false);
String ptoVtaAfip = ParamUtil.getString(request, "ptoVtaAfip"); 
String portlet_name = ParamUtil.getString(request, "portlet_name");

if (portlet_name == null || portlet_name.trim().equals("") || renderResponse.getNamespace().equals("_TES_1_")){
	portlet_name = "tesoreria";
}
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
}

if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
}

if(renderResponse.getNamespace().equals("_HOT_1_")){
	portlet_name = "hoteles";
}

%>

<table width="100%">
  <tr>
	<td width="50%" valign="top">
	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
		<tr>
			<td>
			</td>
		</tr>
		<%} %>
		<tr>
			<td>
			<div align="center" id="<portlet:namespace />agregandoAnticipo">
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
			<td>
			<div align="center" id="<portlet:namespace />anticipos">
			  <jsp:include page='adelantos_search_result.jsp' />
			
			 
			</div>
			</td>
		</tr>
	 </table>
	</td>
  </tr>
</table>

<script type="text/javascript">
	
	function borraAnticipo(sucursal, nro, importe){			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/sacar_factura_recibo_adelanto'
			+  '&cmd=borrar_adelanto'
			+  '&sucursal=' +sucursal
			+  '&nro=' +encodeURI(nro)
			+  '&importe_pago=' + importe
			+  '&esEdicion=' + "<%=esEdicion%>" ;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />agregandoAnticipo').show();
			jQuery('#<portlet:namespace />mostrar_adelantos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoAnticipo').hide();
		       }
			);
	}
	
	jQuery('#<portlet:namespace />agregandoAnticipo').hide();
</script>