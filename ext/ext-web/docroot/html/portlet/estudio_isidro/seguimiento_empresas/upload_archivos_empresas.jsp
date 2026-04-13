<%@ include file="/html/portlet/estudio_isidro/init.jsp"%>

<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.AfipCantidadRegistrosIncorrectaException.class %>" message="afip-cantidad-registros-incorrecta" />
<liferay-ui:error exception="<%= java.text.ParseException.class %>" message="afip-parse-exception" />
<liferay-ui:error exception="<%= org.postgresql.util.PSQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.sql.SQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.io.IOException.class %>" message="afip-io-exception" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException.class%>" message="error-procesando-archivos-seguimiento-empresas" />
<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado.class%>" message="rendicion-duplicada" />
<%

String portlet_name=null;
portlet_name="estudio_isidro";	

Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
fechaDesde.add(Calendar.MONTH, -1);
Calendar current = CalendarFactoryUtil.getCalendar();

List<TipoLoteEmpresa> tiposLoteEmp = (ArrayList<TipoLoteEmpresa>) request.getSession().getAttribute(WebKeysEstudioIsidro.TIPOS_LOTE_EMPRESA_EN_SESSION);

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
			<liferay-ui:message key="subir-archivo" />
		
	</legend>

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
				<jsp:include page='/html/portlet/estudio_isidro/seguimiento_empresas/archivos_subidos_estudio.jsp' />  
			</td>			
		</tr>		
		</table>
	</fieldset>
</form>

<div id="helpUpload" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
El formato de archivo Lote Empresa es: cuit; nro de lote; tipo lote;cálculo de deuda(sin separador de miles y punto como separador decimal) (datos separados por ;). 
Los tipos de lote son:
<ul> 
<%for (TipoLoteEmpresa tle : tiposLoteEmp){ %>
	<li>(<%=tle.getTipoLote()%>)<%=tle.getDescripcionLote() %></li>
									
<%} %>
</ul>
El archivo se debe llamar LOTE_EMPRESA***.csv
</div>

<script type="text/javascript">	
	<%-- function <portlet:namespace />buscaArchivos(){
		var fechaUltimoArchivoDia=jQuery('#<portlet:namespace />fechaUltimoArchivoDia').val();		
		var fechaUltimoArchivoMes=jQuery('#<portlet:namespace />fechaUltimoArchivoMes').val();		
		var fechaUltimoArchivoAnio=jQuery('#<portlet:namespace />fechaUltimoArchivoAnio').val();	
  		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscaArchivoBco&fechaUltimoArchivoDia='+fechaUltimoArchivoDia+'&fechaUltimoArchivoMes='+fechaUltimoArchivoMes+'&fechaUltimoArchivoAnio='+fechaUltimoArchivoAnio;		
 		jQuery("#<portlet:namespace />archivos_subidos_bco").load(url);   
	} --%>

		function <portlet:namespace />uploadArchivo() {			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_archivos_empresas';
			document.<portlet:namespace />fm2.method = 'post';
			submitForm(document.<portlet:namespace />fm2, url);
		}
</script>