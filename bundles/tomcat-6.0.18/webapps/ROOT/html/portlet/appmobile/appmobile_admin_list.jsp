<%@ include file="/html/portlet/appmobile/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "appmobile";
	}else if(renderResponse.getNamespace().equals("_APM_1_")){
		portlet_name = "appmobile";
	}
	List<ClaseBase>sectores = (List<ClaseBase>)ComprobanteServiceUtil.getSectoresByUser(user.getScreenName());
	String usuario=user.getScreenName();
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
//	fecha.add(Calendar.DATE, -1);


 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);		 		
%>
		<fieldset class="block-labels">
		<legend>Descarga de Comprobantes</legend>
					
		<table class="lfr-table">
		   <tr><td colspan="8">&nbsp;</td></tr>
		   <tr>			
					
			 <td><label>Tipo Pedido:</label></td>
			 <td>
				<select id="<portlet:namespace />tipo" name="<portlet:namespace />tipo" onchange="actualizarEstados()">
						<option value="pre">Preautorización</option>
						<option value="rei">RP Reintegro</option>
					</select>
			</td>
		 </tr>
	</table>
	
	<table class="lfr-table">
		   <tr><td colspan="8">&nbsp;</td></tr>
		   <tr>			
					
			 <td><label>Estado:</label></td>
			 <td>
				<select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
						<option value="IN">Pendiente</option>
						<option value="CA">Enviado</option>
					</select>
			</td>
		 </tr>
	</table>
	<table class="lfr-table">			
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<label>Fecha Desde:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date 
						monthNullable="false" 
						dayNullable="false"
						dayValue="<%=fecha.get(Calendar.DAY_OF_MONTH )%>"
						yearNullable="false"
						dayParam="fechaEstadoComprobanteDiaDde"
						monthValue="<%=fecha.get(Calendar.MONTH )%>"
						monthParam="fechaEstadoComprobanteMesDde"
						yearParam="fechaEstadoComprobanteAnioDde"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
						yearValue="<%=fecha.get(Calendar.YEAR )%>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
					
					
					<td>
						<label>Fecha Hasta:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date 
						monthNullable="false" 
						dayNullable="false"
						yearNullable="false"
						dayValue="<%=fecha.get(Calendar.DAY_OF_MONTH )%>"
						dayParam="fechaEstadoComprobanteDiaHta"
						monthParam="fechaEstadoComprobanteMesHta"
						monthValue="<%=fecha.get(Calendar.MONTH )%>"
						yearParam="fechaEstadoComprobanteAnioHta"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						yearValue="<%=fecha.get(Calendar.YEAR )%>"
						disabled="<%= false %>" />
					</td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
    </table>
    		
	<table>		
				<tr>
					<td>
						<input id="<portlet:namespace />descargar" value="Descargar Datos" title="Descargar comprobantes y adjuntos" type="button"
						onclick="javascript:<portlet:namespace />downloadAppMobile();"/>							
					</td>
					<td>
					&nbsp;
					</td>	
					<td>
					&nbsp;
					</td>
				</tr>
	 </table>				
</fieldset>
		<div align="center" id="<portlet:namespace />buscando">
				<table style="align:center;">
					<tr>
						<td><liferay-ui:message key='buscando'/></td>
						<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</td>
					</tr>
				</table>		
		</div>	
		
		
		<div align="center" id="<portlet:namespace />busquedaComprobDiv">
				<liferay-util:include page="/html/portlet/appmobile/comprobantes_download_app.jsp"/>
		</div>	
		
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
	
function <portlet:namespace />downloadAppMobile(){		
				var fechaEstadoComprobanteDiaDde=jQuery('#<portlet:namespace />fechaEstadoComprobanteDiaDde').val();
				var fechaEstadoComprobanteMesDde=jQuery('#<portlet:namespace />fechaEstadoComprobanteMesDde').val();
				var fechaEstadoComprobanteAnioDde=jQuery('#<portlet:namespace />fechaEstadoComprobanteAnioDde').val();
				
				var fechaEstadoComprobanteDiaHta=jQuery('#<portlet:namespace />fechaEstadoComprobanteDiaHta').val();
				var fechaEstadoComprobanteMesHta=jQuery('#<portlet:namespace />fechaEstadoComprobanteMesHta').val();
				var fechaEstadoComprobanteAnioHta=jQuery('#<portlet:namespace />fechaEstadoComprobanteAnioHta').val();

				if (fechaEstadoComprobanteDiaDde != "" || fechaEstadoComprobanteMesDde != "" || fechaEstadoComprobanteAnioDde != ""){
					if (fechaEstadoComprobanteDiaDde == "" || fechaEstadoComprobanteMesDde == "" || fechaEstadoComprobanteAnioDde == ""){
						alert("Por favor seleccione todos los campos de la fecha de Estado Desde.");
						return false;
					}
				}
				
				if (fechaEstadoComprobanteDiaHta != "" || fechaEstadoComprobanteMesHta != "" || fechaEstadoComprobanteAnioHta != ""){
					if (fechaEstadoComprobanteDiaHta == "" || fechaEstadoComprobanteMesHta == "" || fechaEstadoComprobanteAnioHta == ""){
						alert("Por favor seleccione todos los campos de la fecha de Estado Hasta.");
						return false;
					}
				}
				
				           
				var usr ="<%=usuario%>";
				var estado=jQuery('#<portlet:namespace />estado').val();
				var tipo = jQuery('#<portlet:namespace />tipo').val();
				
				jQuery('#<portlet:namespace />buscando').show();
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/administracion_appmobile';
				url += '&cmd=importarDatosApp'+
				    '&fechaEstadoComprobanteDiaDde='+fechaEstadoComprobanteDiaDde+'&fechaEstadoComprobanteMesDde='+fechaEstadoComprobanteMesDde+
				    '&fechaEstadoComprobanteAnioDde='+fechaEstadoComprobanteAnioDde;
				url+='&fechaEstadoComprobanteDiaHta='+fechaEstadoComprobanteDiaHta+'&fechaEstadoComprobanteMesHta='+fechaEstadoComprobanteMesHta+
				    '&fechaEstadoComprobanteAnioHta='+fechaEstadoComprobanteAnioHta;
				url += '&estado='+estado;
				url += '&tipo=' + tipo;
				url += '&portlet_name=<%=portlet_name%>';
				url += '&user='+usr;
				
				url += '&rnd=' + Math.floor(Math.random()*100);
				
				jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
		        		jQuery('#<portlet:namespace />buscando').hide();
					}
		        );
};

function actualizarEstados() {
	var tipo = document.getElementById('<portlet:namespace />tipo').value;
	var estadoSelect = document.getElementById('<portlet:namespace />estado');

	estadoSelect.innerHTML = '';

	var opciones = [];

	if (tipo === 'pre') {
		opciones = [
			{ value: 'IN', text: 'Pendiente' },
			{ value: 'CA', text: 'Enviado' }
		];
	} else if (tipo === 'rei') {
		opciones = [
			{ value: 'IN', text: 'Pendiente' },
			{ value: 'CA', text: 'Enviado' }
		];
	}

	for (var i = 0; i < opciones.length; i++) {
		var option = document.createElement('option');
		option.value = opciones[i].value;
		option.text = opciones[i].text;
		estadoSelect.appendChild(option);
	}
}

document.addEventListener('DOMContentLoaded', actualizarEstados);


</script>
