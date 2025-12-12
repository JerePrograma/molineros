<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ page import="ar.com.ospim.novedades.beans.ArchivoNovedad"%>
<%@ page import="java.util.Locale" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%	
	ArrayList<ArchivoNovedad> archivosProc = (ArrayList<ArchivoNovedad>) request.getAttribute("archivosNovedades");
	SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdf2 = new SimpleDateFormat("MMM/yyyy",  new Locale("es", "ES"));

	Calendar fechaPadron = CalendarFactoryUtil.getCalendar();
	fechaPadron.setTime(new Date());	
	
	
%>
<table class="lfr-table">
<tr>
	<td>
		<fieldset class="block-labels"><legend><liferay-ui:message key="reporte-novedades-procesadas" /></legend>
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">	
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>
					<!-- <liferay-ui:message key="fecha-desde" /> -->Proceso:
				</td>
				<td>	
					<liferay-ui:input-date
						dayParam="fechaProcesoDia"
						dayValue="<%= 1 %>" 
						dayNullable="<%=false%>" 
						monthParam="fechaProcesoMes"
						monthValue="<%= fechaPadron.get(Calendar.MONTH) %>"				
						yearParam="fechaProcesoAnio"
						monthAndYearNullable="<%=false %>"
						yearValue="<%= fechaPadron.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaPadron.get(Calendar.YEAR) - 25 %>"
						yearRangeEnd="<%= fechaPadron.get(Calendar.YEAR) + 10%>"
						firstDayOfWeek="<%= fechaPadron.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
				</td>	
				<td><label><liferay-ui:message key="periodo-novedades" />:</label></td>
				<td>
					<select name="<portlet:namespace/>b_fecha_nov" id="<portlet:namespace/>b_fecha_nov">
							<option value=""></option>
							<%
								for (ArchivoNovedad archProc : archivosProc) {
							%>
								<option value="<%=sdf1.format(archProc.getFechaArchivo()) %>" ><%= sdf2.format(archProc.getFechaArchivo())%></option>
							<%
							}
							%>
					</select>
				</td>
			</tr>
			<tr>	
				<td>
					<!-- <liferay-ui:message key="fecha-desde" /> -->Padrón inicial:&nbsp;	
				</td>
				<td>		
					<liferay-ui:input-date
						dayParam="fechaPadronDesdeDia"
						dayValue="<%= fechaPadron.get(Calendar.DATE) %>" 
						dayNullable="<%=false%>" 
						monthParam="fechaPadronDesdeMes"
						monthValue="<%= fechaPadron.get(Calendar.MONTH) %>"				
						yearParam="fechaPadronDesdeAnio"
						monthAndYearNullable="<%=false %>"
						yearValue="<%= fechaPadron.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaPadron.get(Calendar.YEAR) - 25 %>"
						yearRangeEnd="<%= fechaPadron.get(Calendar.YEAR) + 10%>"
						firstDayOfWeek="<%= fechaPadron.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
				</td>
				<td>
					<!-- <liferay-ui:message key="fecha-desde" /> -->Padrón final:&nbsp;
				</td>
				<td>			
					<liferay-ui:input-date
						dayParam="fechaPadronHastaDia"
						dayValue="<%= fechaPadron.get(Calendar.DATE) %>" 
						dayNullable="<%=false%>" 
						monthParam="fechaPadronHastaMes"
						monthValue="<%= fechaPadron.get(Calendar.MONTH) %>"				
						yearParam="fechaPadronHastaAnio"
						monthAndYearNullable="<%=false %>"
						yearValue="<%= fechaPadron.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaPadron.get(Calendar.YEAR) - 25 %>"
						yearRangeEnd="<%= fechaPadron.get(Calendar.YEAR) + 10%>"
						firstDayOfWeek="<%= fechaPadron.getFirstDayOfWeek() - 1 %>"
						disabled="false" />
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<liferay-ui:message key="registrar-informado" />&nbsp;
					<input type="checkbox" name="<portlet:namespace />informar" id="<portlet:namespace />informar"/>
				</td>
				<td>
					<input id="<portlet:namespace />exportar" name="<portlet:namespace />exportar"  
								   type="button" value="Exportar Estadistica" onclick="generarExcel()" />
				</td>
			</tr>
			<%-- <tr>	
				<td align="left" colspan="3">
				<input type="button" id="<portlet:namespace />buscar"
					value="<liferay-ui:message key="buscar"/>"
					title="<liferay-ui:message key="buscar" />" />
				</td>
			</tr> --%>
			<tr>
				<td colspan="4">
					<liferay-util:include page='/html/portlet/afiliados/reportes/reporte_proceso_novedades_sss_agendado.jsp'></liferay-util:include>	
				</td>
			</tr>
		</table>
		</fieldset>
		
		<%--<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscando">
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
		</fieldset> --%>
	</td>
</tr>
</table>


<script type="text/javascript">
	jQuery('#<portlet:namespace />fechaProcesoDia').hide();
	
	function generarExcel(){
		
		window.location.href ='/xlsservlet/?reporte=ESTADISTICA_NOVEDADES_SSS_PROCESADAS';
		
	}
</script>