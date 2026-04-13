<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.AfipCantidadRegistrosIncorrectaException.class %>" message="afip-cantidad-registros-incorrecta" />
<liferay-ui:error exception="<%= java.text.ParseException.class %>" message="afip-parse-exception" />
<liferay-ui:error exception="<%= org.postgresql.util.PSQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.sql.SQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.io.IOException.class %>" message="afip-io-exception" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException.class%>" message="error-procesando-archivos-afip" />
<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado.class%>" message="rendicion-duplicada" />
<%

String portlet_name=null;

portlet_name="autorizaciones";


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
	<legend>Archivo de Prevencion Salud</legend>

		<table class="lfr-table">
		<tr>
			<td  align="center">
				<input type="file" name="archivo"/>
			</td>
			<td align="center">

				<a href="javascript:void(0)" onclick="help(event, 'helpUpload')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
			</td>			
			<td>
			  <div id="<portlet:namespace />pagos_imputados">
						<jsp:include page='/html/portlet/autorizaciones/pre_autorizaciones/preautorizacion_procesa_archivo_upload.jsp' />  
			  </div>
			</td>			
		</tr>		
		</table>
	</fieldset>
<div id="helpUpload" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" 
style="top: 200px; left: 300px">
El archivo debe ser txt y tener el siguiente formato:AuthorizationProposalNumber TransactionId AuthorizationStatus TributaryCodeNumber FinishDate MedicalPractice MedicalPracticeDescription MedicalPracticeStatus DeleteReason.
deben estar separados los campos por | 
</div>
<script type="text/javascript">	
	
	function <portlet:namespace />uploadArchivo() {
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/preautorizacion_upload_archivos';
		document.<portlet:namespace />fm2.method = 'post';
		submitForm(document.<portlet:namespace />fm2, url);
	}
</script>