<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%	
	
	//...
	String cuil_titular=request.getParameter("cuil_titular");
	String inte=request.getParameter("inte");
	String accion=request.getParameter("accion");
	
	Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
	fechaHoy.setTime(new Date());
%>
<div id="<portlet:namespace/>gestionCobMedica">
	<table style="left:50%;">
		<tr>
			<td>
				<table>
					<tr>
						<td>
							<%if(accion.equalsIgnoreCase(Constants.DEACTIVATE)){ %>
								<liferay-ui:message key="afi-cod-med-desde" />
							<%}else{ %>
								<liferay-ui:message key="afi-cod-med-hasta" />
							<%} %>
						</td>
						<td>&nbsp;</td>			
						<td> 
										<liferay-ui:input-date
											dayParam="fechaSupCobDia"
											dayValue="<%= fechaHoy.get(Calendar.DATE) %>"
											monthParam="fechaSupCobMes"
											monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"								
											yearParam="fechaSupCobAnio"
											yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
											yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 15 %>"
											yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR)+40%>"
											firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
											disabled="<%= false %>"
										/>				
						</td>
						<%
							String ejecutaCobMedicaURL="javascript:ejecutarSuspencionCobertura('"+cuil_titular+"','"+inte+"','"+accion+"');";
						%>
						<td>&nbsp;</td>		
						<td>
							<input type="button" value="<liferay-ui:message key="save" />" onClick="<%=ejecutaCobMedicaURL%>" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
<div id="<portlet:namespace/>resultCobMed">
</div>
