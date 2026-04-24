<%@ include file="/html/portlet/autorizaciones/init.jsp"%>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.AfipCantidadRegistrosIncorrectaException.class %>" message="afip-cantidad-registros-incorrecta" />
<liferay-ui:error exception="<%= java.text.ParseException.class %>" message="afip-parse-exception" />
<liferay-ui:error exception="<%= org.postgresql.util.PSQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.sql.SQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.io.IOException.class %>" message="afip-io-exception" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException.class%>" message="error-procesando-archivos-afip" />
<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado.class%>" message="rendicion-duplicada" />
<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.ErrorGeneralProcesandoArchivos.class%>" message="error-procesando-archivos" />
<%

String portlet_name=null;

portlet_name="autorizaciones";


Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
fechaDesde.add(Calendar.MONTH, -1);
Calendar current = CalendarFactoryUtil.getCalendar();

List<String> errores = (List<String>)request.getAttribute("errores");
if (errores != null && !errores.isEmpty()){
	%>
	<table  style="color:#0000ff" >
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
 
	<legend>Procesos de Archivos de Transferencias Bancarias</legend>
		<table class="lfr-table">
		<tr>
		    <td  align="center">
				<input type="file" name="archivo"/>
				<a href="javascript:void(0)" onclick="help(event, 'helpArchivoRecibos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
			</td>
			<td align="center">
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
			</td>
			 <td><input id="<portlet:namespace />reporteInconsistencias" name="<portlet:namespace />/>reporteInconsistencias" type="button" value="OPs sin fecha Extracto"  onclick="verInconsistenciasSSS()"/></td>			
		</tr>	
		</table>
   </fieldset>
</form>
<div id="helpArchivoRecibos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  El nombre del archivo para procesar los extractos bancarios debe comenzar con 'Movimientos Conformados' y tener extensión csv.
  
  Para tratar las inconsistencias el archivo a subir debe ser un Excel(XLS) sin fila de Titulos y sus columnas deberan contener Nro de Orden de Pago y Fecha de
  Transferencia.
</div>
</div>
<table class="lfr-table">
		  <tr>
		   <td>
			  <div id="<portlet:namespace />lotes_sss">
						<jsp:include page='/html/portlet/autorizaciones/integracion/integracion_archivos_transferencia_result.jsp' />  
			  </div>
			</td>	
		  </tr>
</table>

<script type="text/javascript">	

	
function <portlet:namespace />uploadArchivo() {
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/integracion_upload_transferencias';
	document.<portlet:namespace />fm2.method = 'post';
	submitForm(document.<portlet:namespace />fm2, url);
}

function verInconsistenciasSSS(){
	window.location.href ='/xlsservlet/?reporte=REPORTE_INCONSISTENCIAS_FECHA_TRANSFERENCIA_INTEGRACION';
}

</script>