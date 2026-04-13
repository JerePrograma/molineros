<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%
	Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
	fechaHoy.setTime(new Date());
	//TODO: Validar que exista documentación si se selecciona un motivo de baja.
%>
<div>
	<table style="left:50%;">
		<tr>			
			<td>
				<liferay-ui:message key="fecha-firma" />
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
			<td>
				&nbsp;
			</td>		
			<td>
				<input type="button" value="<liferay-ui:message key="save" />" onClick="javascript:cierraLote();" />
			</td>
		</tr>
	</table>
</div>

