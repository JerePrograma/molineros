<%@ include file="/html/portlet/comprobantes/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<liferay-ui:error exception="<%=ar.com.ospim.global.services.ComprobantesYaPagadosException.class %>" message="exception-comprobantes-ya-pagados-baja" />

<%

	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "comprobantes";
	}else if(renderResponse.getNamespace().equals("_COM_1_")){
		portlet_name = "comprobantes";
	}
	List<ClaseBase>sectores = (List<ClaseBase>)ComprobanteServiceUtil.getSectoresByUser(user.getScreenName());
	String usuario=user.getScreenName();
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
//	fecha.add(Calendar.DATE, -1);


 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
 		
 		String esEditableStr = ParamUtil.getString(request, "esEditable");
 		if (esEditableStr == null || esEditableStr.equals("false")){
 			esEditableStr ="false";
 		}
 		boolean esEditable = Boolean.parseBoolean(esEditableStr);
%>
		<fieldset class="block-labels">
		<legend>Descarga de Comprobantes</legend>
					
		<table class="lfr-table">
		   <tr><td colspan="8">&nbsp;</td></tr>
		   <tr>			
					
			 <td><label>Estado:</label></td>
			 <td>
				<select id="<portlet:namespace />estado" name="<portlet:namespace />estado">
						<option value="Verificado">Verificado</option>
						<option value="Rechazado">Rechazado</option>
					</select>
			</td>
		 </tr>
	</table>
	<table class="lfr-table">			
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<label>Estado Desde:</label>
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
						<label>Estado Hasta:</label>
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
						<input id="<portlet:namespace />descarcar" value="Descargar Comprobantes" title="Descargar comprobantes y adjuntos" type="button"
						onclick="javascript:<portlet:namespace />downloadCptes();"/>							
					</td>
					<td>
					&nbsp;
					</td>	
					<td>
					&nbsp;
					</td>
					
					<td colspan="6">&nbsp;</td>	
					
					<td>
						<input id="<portlet:namespace />descarcar" value="Descargar Recibos" title="Descargar Recibos" type="button"
						onclick="javascript:<portlet:namespace />downloadCptesRecibo();"/>							
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
				<liferay-util:include page="/html/portlet/comprobantes/comprobantes_download_ws_search_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEditable)%>" />
				</liferay-util:include>
		</div>	
		
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
	
function <portlet:namespace />downloadCptes(){		
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
				
				jQuery('#<portlet:namespace />buscando').show();
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/administracion';
				url += '&cmd=download'+
				    '&fechaEstadoComprobanteDiaDde='+fechaEstadoComprobanteDiaDde+'&fechaEstadoComprobanteMesDde='+fechaEstadoComprobanteMesDde+
				    '&fechaEstadoComprobanteAnioDde='+fechaEstadoComprobanteAnioDde;
				url+='&fechaEstadoComprobanteDiaHta='+fechaEstadoComprobanteDiaHta+'&fechaEstadoComprobanteMesHta='+fechaEstadoComprobanteMesHta+
				    '&fechaEstadoComprobanteAnioHta='+fechaEstadoComprobanteAnioHta;
				url += '&estado='+estado;
				url += '&portlet_name=<%=portlet_name%>';
				url += '&user='+usr;
				
				url += '&rnd=' + Math.floor(Math.random()*100);
				
				jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
		        		jQuery('#<portlet:namespace />buscando').hide();
					}
		        );
};


function <portlet:namespace />downloadCptesRecibo(){		
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
	var estado="Con Recibo";
	
	jQuery('#<portlet:namespace />buscando').show();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/administracion';
	url += '&cmd=downloadRecibo'+
	    '&fechaEstadoComprobanteDiaDde='+fechaEstadoComprobanteDiaDde+'&fechaEstadoComprobanteMesDde='+fechaEstadoComprobanteMesDde+
	    '&fechaEstadoComprobanteAnioDde='+fechaEstadoComprobanteAnioDde;
	url+='&fechaEstadoComprobanteDiaHta='+fechaEstadoComprobanteDiaHta+'&fechaEstadoComprobanteMesHta='+fechaEstadoComprobanteMesHta+
	    '&fechaEstadoComprobanteAnioHta='+fechaEstadoComprobanteAnioHta;
	url += '&estado='+encodeURI(estado);
	url += '&portlet_name=<%=portlet_name%>';
	url += '&user='+usr;
	url += '&rnd=' + Math.floor(Math.random()*100);
	
	jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
    		jQuery('#<portlet:namespace />buscando').hide();
		}
    );
};


</script>
