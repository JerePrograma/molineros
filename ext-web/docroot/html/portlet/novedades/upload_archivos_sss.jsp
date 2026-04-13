<%@page import="ar.com.ospim.novedades.exception.PeriodoArchivoDuplicadoException"%>
<%@ include file="/html/portlet/afiliados/init.jsp"%>

<liferay-ui:error exception="<%= java.text.ParseException.class %>" message="afip-parse-exception" />
<liferay-ui:error exception="<%= org.postgresql.util.PSQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.sql.SQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.io.IOException.class %>" message="afip-io-exception" />
<liferay-ui:error exception="<%= ar.com.ospim.novedades.beans.ErrorProcesandoArchivosNovedadesException.class%>" message="error-procesando-archivos-sss" />
<liferay-ui:error exception="<%= PeriodoArchivoDuplicadoException.class%>" message="error-periodo-archivos-sss" />

<%

Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
fechaDesde.add(Calendar.MONTH, -1);
Calendar current = CalendarFactoryUtil.getCalendar();

List<String> errores = (List<String>)request.getAttribute("errores");
if (errores != null && !errores.isEmpty()){
	%>
	<table>
	<%
	for (String error : errores){
		%>
		<tr><td>
		<%=error%>
		</td></tr>
		<%
	}
	%>
	</table>
	<%
}
%> 
</form>
<form action="" method="get" name="<portlet:namespace />fm2" enctype="multipart/form-data">
	<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="subir-archivo-sss" />
	</legend>

	
		<table class="lfr-table">
		<tr>
			<td>
				<table>
					<tr>						
						<td colspan="2"><label>Período archivo:</label>&nbsp;
							<liferay-ui:input-date
							dayParam="fechaArchivoDia"
 							dayNullable="<%= true %>" 
							dayValue="01"
							monthParam="fechaArchivoMes"
							monthValue="<%= current.get(Calendar.MONTH) %>"
							yearParam="fechaArchivoAnio"
							yearValue="<%= current.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)+1 %>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							monthAndYearNullable="<%= false %>"
							disabled="<%= false %>" />
						</td>
					</tr>
					<tr>						
						<td colspan="2">&nbsp;&nbsp;</td>
					</tr>	
					<tr>	
						<td>
							<input type="file" name="archivo"/>
						</td>
						<td>
						    <a href="javascript:void(0)" onclick="help(event, 'helpUpload')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
							<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
						</td>
					</tr>	
				</table>
			</td>			
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>
				<table>
					<tr>						
						<td><label><liferay-ui:message key="fecha-ultimo-archivo" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaUltimoArchivoDia"
							dayValue="1" 
							monthParam="fechaUltimoArchivoMes"
							monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
							monthNullable="<%= true %>"				
							yearParam="fechaUltimoArchivoAnio"
							yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)+1 %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
						<td><input type="button" id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" onClick="<portlet:namespace />buscaArchivos()" /></td>
					</tr>
					<tr>
				 		<td colspan="3">&nbsp;</td>
				 	</tr>	
				 		
				 	<tr>
				 		<td colspan="3">
							<div id="<portlet:namespace />archivos_novedades">
								<jsp:include page='/html/portlet/novedades/archivos_subidos_sss.jsp' />  
							</div>
						</td>
					</tr>		
				</table>
			</td>			
		</tr>		
		</table>
	</fieldset>
<div id="helpUpload" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" 
style="top: 200px; left: 300px">
    * El formato de archivo para Bajas por Opción es: En la Columna "A" de la planilla de cálculo en la línea "Uno": El título (texto libre) y a partir de la fila "Dos" los Cuil titular 
<br> a dar de baja. (Este proceso masivo de bajas de afiliados dará de baja con sus respectivos miembros del grupo familiar)
 <br>El nombre del archivo a importar debe comenzar con : <b>BajasPorOpcion  </b>  y terminar con la extensión  <b> .xls</b>
 
  <br> * Para Subir el archivo de novedades SSS es necesario que el nombre del archivo tenga el siguiente formato
       Este debe empezar con  <b> novedades_sss_112608 </b>  y terminar con la extension <b>  .txt.</b>
 
 <br> * Para Subir el archivo de padrón consolidado SSS es necesario que el nombre del archivo tenga el siguiente formato
       Este debe empezar con  <b> pc112608 </b>  y terminar con la extension <b>  .txt.</b>
<br>

</div>


<script type="text/javascript">	

	
	
jQuery("#<portlet:namespace />fechaArchivoDia").hide();


	function <portlet:namespace />buscaArchivos(){
		var fechaUltimoArchivoDia=jQuery('#<portlet:namespace />fechaUltimoArchivoDia').val();		
		var fechaUltimoArchivoMes=jQuery('#<portlet:namespace />fechaUltimoArchivoMes').val();		
		var fechaUltimoArchivoAnio=jQuery('#<portlet:namespace />fechaUltimoArchivoAnio').val();	
  		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscaArchivosNovedades&fechaUltimoArchivoDia='+fechaUltimoArchivoDia+'&fechaUltimoArchivoMes='+fechaUltimoArchivoMes+'&fechaUltimoArchivoAnio='+fechaUltimoArchivoAnio;		
 		jQuery("#<portlet:namespace />archivos_novedades").load(url);   
	}

		function <portlet:namespace />uploadArchivo() {			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/afiliados/upload_archivo_novedades';
			document.<portlet:namespace />fm2.method = 'post';
			submitForm(document.<portlet:namespace />fm2, url);
		}
</script>