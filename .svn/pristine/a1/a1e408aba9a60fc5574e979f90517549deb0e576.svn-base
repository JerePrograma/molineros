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
//	List<ClaseBase>sectores = (List<ClaseBase>)ComprobanteServiceUtil.getSectoresByUser(user.getScreenName());
	String usuario=user.getScreenName();
	
	Calendar fecha = CalendarFactoryUtil.getCalendar();
	//fecha.setTime(new Date());
	fecha.add(Calendar.DATE, -1);


// 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ENTIDAD_OSPIM);
 		
 		String esEditableStr = ParamUtil.getString(request, "esEditable");
 		if (esEditableStr == null || esEditableStr.equals("false")){
 			esEditableStr ="false";
 		}
 		boolean esEditable = Boolean.parseBoolean(esEditableStr);
%>
		<fieldset class="block-labels">
		<legend><liferay-ui:message key="busqueda-comprobantes" /></legend>
					
	<table class="lfr-table">			
				<tr><td colspan="8">&nbsp;</td></tr>
				<tr>
					<td>
						<label>Fecha a procesar:</label>
					</td>
					<td colspan="3">
						<liferay-ui:input-date 
						monthNullable="false" 
						dayNullable="false"
						dayValue="<%=fecha.get(Calendar.DAY_OF_MONTH )%>"
						yearNullable="false"
						dayParam="fechaDiaDde"
						monthValue="<%=fecha.get(Calendar.MONTH )%>"
						monthParam="fechaMesDde"
						yearParam="fechaAnioDde"
						yearRangeStart="<%= fecha.get(Calendar.YEAR) - 20 %>"
						yearRangeEnd="<%= fecha.get(Calendar.YEAR) + 20 %>"
						yearValue="<%=fecha.get(Calendar.YEAR )%>"
						firstDayOfWeek="<%= fecha.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
					</td>
				</tr>
				<tr><td colspan="8">&nbsp;</td></tr>
    </table>
    		
	<table>		
				<tr>
					<td>
						<input id="<portlet:namespace />procesar" value="Procesar" title="Informar pagos" type="button"
						onclick="javascript:<portlet:namespace />uploadPagos();"/>							
					</td>
					<td>
					&nbsp;
					</td>	
					<td>
					&nbsp;
					</td>
					
					<td colspan="6">&nbsp;</td>	
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
				<liferay-util:include page="/html/portlet/comprobantes/comprobantes_upload_op_ws_search_result.jsp">
					<liferay-util:param name="esEditable" value="<%=String.valueOf(esEditable)%>" />
				</liferay-util:include>
		</div>	
		
			
<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();
	
function <portlet:namespace />uploadPagos(){		
				var fechaDiaDde=jQuery('#<portlet:namespace />fechaDiaDde').val();
				var fechaMesDde=jQuery('#<portlet:namespace />fechaMesDde').val();
				var fechaAnioDde=jQuery('#<portlet:namespace />fechaAnioDde').val();
				
				if (fechaDiaDde != "" || fechaMesDde != "" || fechaAnioDde != ""){
					if (fechaDiaDde == "" || fechaMesDde == "" || fechaAnioDde == ""){
						alert("Por favor seleccione todos los campos de la fecha");
						return false;
					}
				}
				
				var usr ="<%=usuario%>";
				jQuery('#<portlet:namespace />buscando').show();
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/administracion';
				url += '&cmd=upload_op'+
				    '&fechaDiaDde='+fechaDiaDde+'&fechaMesDde='+fechaMesDde+
				    '&fechaAnioDde='+fechaAnioDde;
				url += '&portlet_name=<%=portlet_name%>';
				url += '&user='+usr;
				url += '&rnd=' + Math.floor(Math.random()*100);
				
				jQuery('#<portlet:namespace />busquedaComprobDiv').load(url, function() {
		        		jQuery('#<portlet:namespace />buscando').hide();
					}
		        );
};
</script>
