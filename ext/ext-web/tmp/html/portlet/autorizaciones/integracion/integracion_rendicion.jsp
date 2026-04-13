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

<form action="" method="post" name="<portlet:namespace />fmSSS" id="<portlet:namespace />fmSSS" enctype="multipart/form-data">
<fieldset class="block-labels">
 
	<legend>Rendición</legend>
		<table class="lfr-table">
		<tr>
		  <td  align="center">
				<input type="file" name="archivoRendicion"/>
			</td>
			<td align="center">
			<a href="javascript:void(0)" onclick="help(event, 'helpUploadSSS')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoRendicion()"/>
			</td>			
		</tr>	
		
			
		</table>
		
		<table class="lfr-table">
		  <tr>
		   <td>
			  <div id="<portlet:namespace />lotes_sss">
						<jsp:include page='/html/portlet/autorizaciones/integracion/integracion_rendicion_result.jsp' />  
			  </div>
			</td>	
		  </tr>
		</table>
		
   </fieldset>
   <div id="helpUploadSSS" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" 
style="top: 200px; left: 300px">
El nombre del archivo debe comenzar con el nro 112608 y finalizar con la extensión .envio</div>
<input id="<portlet:namespace />fmSSSH" name="<portlet:namespace />fmSSSH" type="hidden" value="" />
</form>


<script type="text/javascript">	

<portlet:namespace />hideDayFieldOfPeriodFields ();

function <portlet:namespace />hideDayFieldOfPeriodFields () {
//	jQuery("#<portlet:namespace />periodoDia").hide();
//	jQuery("#<portlet:namespace />carpetaDia").hide();
}



function <portlet:namespace />uploadArchivoRendicion() {
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/integracion_upload_archivos_rendicion';
	document.<portlet:namespace />fmSSS.method = 'post';
	submitForm(document.<portlet:namespace />fmSSS, url);
}


</script>