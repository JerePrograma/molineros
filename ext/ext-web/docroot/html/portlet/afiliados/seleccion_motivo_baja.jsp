<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%
	//obtengo lista de session
	List<MotivoBaja> motivosBaja=(ArrayList<MotivoBaja>) portletSession.getAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,PortletSession.APPLICATION_SCOPE);
/* 	if (motivosBaja == null) {
		motivosBaja=TraeListasServiceUtil.getMotivosBaja();
		portletSession.setAttribute(WebKeysAfiliados.MOTIVOS_BAJA_EN_SESSION,motivosBaja,PortletSession.APPLICATION_SCOPE);	
	} */	
	
	//...
	String cuil_titular=request.getParameter("cuil_titular");
	String inte=request.getParameter("inte");
	
	Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
	fechaHoy.setTime(new Date());
	//TODO: Validar que exista documentación si se selecciona un motivo de baja.
%>
<div id="<portlet:namespace/>selectMotivosBaja">
	<table style="left:50%;">
		<tr>
			<td>
				<liferay-ui:message key="motivo-baja" />
			</td>
			<td>
				<select name="<portlet:namespace/>tipo_aporte" id="<portlet:namespace/>tipo_aporte">									
					<!-- Sacamos que por baja tachito no se elijan DESPIDOS, DESEMPLEO, FALLECIMIENTO NI RENUNCIA -->
					<%
						if(inte.equalsIgnoreCase("0") ){
							for (MotivoBaja motivoBaja : motivosBaja) {
								if(motivoBaja.getId_motivo_baja() > 3 && motivoBaja.getId_motivo_baja() != 21 ){	
						%>
						<option value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>
						<%
									} 
							}
						}else{
							for (MotivoBaja motivoBaja : motivosBaja) {
								if( motivoBaja.getId_motivo_baja() != 1 &&
									motivoBaja.getId_motivo_baja() != 3 &&
									motivoBaja.getId_motivo_baja() != 21	){
								%>
								<option value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>
								<%
								}
							}
						}
						%>
				</select>		
			</td>
			<td>
				&nbsp;
			</td>		
			<td>
				<liferay-ui:message key="baja-fecha" />
			</td>			
			<td> 
							<liferay-ui:input-date
								dayParam="fechaBajaDia"
								dayValue="<%= fechaHoy.get(Calendar.DATE) %>"
								monthParam="fechaBajaMes"
								monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"								
								yearParam="fechaBajaAnio"
								yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 50 %>"
								yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR)+40%>"
								firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>"
							/>				
			</td>
			<%
				String ejecutaBorraURL="javascript:ejecutarBaja('"+cuil_titular+"','"+inte+"');";
			%>
			<td>
				&nbsp;
			</td>		
			<td>
				<input type="button" value="<liferay-ui:message key="save" />" onClick="<%=ejecutaBorraURL%>" />
			</td>
		</tr>
	</table>
</div>
<div id="<portlet:namespace/>resultBaja">
</div>
