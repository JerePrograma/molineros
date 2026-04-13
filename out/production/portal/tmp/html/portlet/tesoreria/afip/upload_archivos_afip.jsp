<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.AfipCantidadRegistrosIncorrectaException.class %>" message="afip-cantidad-registros-incorrecta" />
<liferay-ui:error exception="<%= java.text.ParseException.class %>" message="afip-parse-exception" />
<liferay-ui:error exception="<%= org.postgresql.util.PSQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.sql.SQLException.class %>" message="afip-sql-exception" />
<liferay-ui:error exception="<%= java.io.IOException.class %>" message="afip-io-exception" />
<liferay-ui:error exception="<%= ar.com.ospim.tesoreria.afip.ErrorProcesandoArchivosAfipException.class%>" message="error-procesando-archivos-afip" />
<liferay-ui:error exception="<%= ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado.class%>" message="rendicion-duplicada" />
<%

String portlet_name=null;
if(renderResponse.getNamespace().equals("_AFI_1_")) {
	portlet_name="afiliados";
}else if(renderResponse.getNamespace().equals("_UOM_1_")) {
	portlet_name="uoma";
}else if(renderResponse.getNamespace().equals("_FAR_1_")) {
	portlet_name="farmacia";
}else if(renderResponse.getNamespace().equals("_EST_1_")) {
	portlet_name="estudio_isidro";	
}else{
	portlet_name="tesoreria";
}	


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
		<%if(renderResponse!=null && renderResponse.getNamespace()!=null && 
		(renderResponse.getNamespace().equals("_AFI_1_") || renderResponse.getNamespace().equals("_UOM_1_") || 
				renderResponse.getNamespace().equals("_FAR_1_") || renderResponse.getNamespace().equals("_EST_1_") )) {%>
			<liferay-ui:message key="subir-archivo" />
		<%}else{%>
			<liferay-ui:message key="subir-archivo-afip" />
		<%}%>
	</legend>

		<table class="lfr-table">
		<tr>
			<td  align="center">
				<input type="file" name="archivo"/>
			</td>
			<td align="center">
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
			</td>
			<% if(renderResponse.getNamespace().equals("_TES_1_")) { %>
			<td>		
				<a href="javascript:void(0)" onclick="help(event, 'helpArchivoAFIP')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>			
			</td>			
			<%} %>
			
			<% if(renderResponse.getNamespace().equals("_UOM_1_")) { %>
			<td>		
				<a href="javascript:void(0)" onclick="help(event, 'helpArchivoUOMA')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>			
			</td>			
			<%} %>
			
			<td>
				<%if(renderResponse!=null && renderResponse.getNamespace()!=null && (renderResponse.getNamespace().equals("_TES_1_")
						|| renderResponse.getNamespace().equals("_AFI_1_"))) {%>
					<div id="<portlet:namespace />archivos_subidos_afip">
						<jsp:include page='/html/portlet/tesoreria/afip/archivos_subidos_afip.jsp' />  
					</div>
				<%}else{%>
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
					</table>
				<div id="<portlet:namespace />archivos_subidos_bco">
					<jsp:include page='/html/portlet/tesoreria/afip/archivos_subidos_bco.jsp' />  
				</div>
				<%}%>
			</td>			
		</tr>		
		</table>
	</fieldset>
	
<div id="helpArchivoAFIP" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 20px; left: 150px">
Archivos Txt</br>
Los archivos que comienzan con ORGANS_TRANSFER, DJ, DDJJ, 0DK2, 0DJU y terminan con .txt					
<hr>
Archivos Zip</br>
Los siguientes archivos de texto deben ser comprimidos (zipeados) con mismo nombre y extensión .zip
Los nombres que comienzan con: DESEMPLEO, SO, EB, EX, B2, A112608, AM112608, 00005782, RENDICION RECAUDACIONES-MR,
00005783, 00005784, 00005785, 00005652, AMTIMA, CONSTA, SUMARTE_, SUMA70_, SUMA_, SUBASI_, RG830N1

</div>
<div id="helpArchivoUOMA" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 20px; left: 150px">
Archivos XLS</br>
Recibos y Retenciones de Hoteles - archivos que comienzan con RETENCIONES,RECIBOS y terminan con .xls					
<hr>
Archivos Zip</br>
Facturación de Hoteles - Archivo comienza con EXPORT y termina con .zip
</div>

<script type="text/javascript">	
	function <portlet:namespace />buscaArchivos(){
		var fechaUltimoArchivoDia=jQuery('#<portlet:namespace />fechaUltimoArchivoDia').val();		
		var fechaUltimoArchivoMes=jQuery('#<portlet:namespace />fechaUltimoArchivoMes').val();		
		var fechaUltimoArchivoAnio=jQuery('#<portlet:namespace />fechaUltimoArchivoAnio').val();  		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscaArchivoBco&fechaUltimoArchivoDia='+fechaUltimoArchivoDia+'&fechaUltimoArchivoMes='+fechaUltimoArchivoMes+'&fechaUltimoArchivoAnio='+fechaUltimoArchivoAnio;		
 		jQuery("#<portlet:namespace />archivos_subidos_bco").load(url);   
	}

		function <portlet:namespace />uploadArchivo() {			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_archivo_afip';
			document.<portlet:namespace />fm2.method = 'post';
			submitForm(document.<portlet:namespace />fm2, url);
		}
</script>