<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%
Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));
if (recibo != null && recibo.getId() != 0){
	esEdicion = false;
}


%>

<table width="100%">
  <tr>
	<td width="50%" valign="top">
  <fieldset class="block-labels">
	<legend><liferay-ui:message	key="anticipos" /></legend>
	<table class="lfr-table" width="100%">
		<%if (esEdicion){ %>
		<tr>
			<td>
			<table width="100%">
				<tr>
					<td>
						<input type="button" value="<liferay-ui:message key="buscar" />" onClick="<portlet:namespace />agregarIngresoAnticipo();" />
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<%} %>
		<tr>
			<td>
			<div align="center" id="<portlet:namespace />agregandoIngresoAnticipo">
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
			<div align="center" id="<portlet:namespace />ingresosAnticipos">
			<jsp:include page='recibo_no_os_anticipos_search_result.jsp' /></div>
			</td>
		</tr>
	</table>
	</fieldset>
	</td>
  </tr>
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<script type="text/javascript">
	function <portlet:namespace />agregarIngresoAnticipo(){
	
			jQuery('#<portlet:namespace />agregandoIngresoAnticipo').show();	

			var cuit = document.getElementById("<portlet:namespace />cuit_entidad");
			var sucu = document.getElementById("<portlet:namespace />sucursal_entidad");
			var entidad=jQuery('#<portlet:namespace/>entidad_bla').val();			
			
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/estudio_isidro/agregar_recibo_no_os_ingreso'
			+'&tipo=Anticipo'
			+'&cuit_empleador=' + cuit.value
			+'&sucursal_empleador=' + sucu.value
			+'&entidad=' + entidad;
			url += '&rnd=' + Math.floor(Math.random()*100);
			jQuery('#<portlet:namespace />ingresosAnticipos').load(url, function() {
														jQuery('#<portlet:namespace />agregandoIngresoAnticipo').hide();	
														sumarConceptos();
										   }
			 );	
	}


	jQuery('#<portlet:namespace />agregandoIngresoAnticipo').hide();
</script>