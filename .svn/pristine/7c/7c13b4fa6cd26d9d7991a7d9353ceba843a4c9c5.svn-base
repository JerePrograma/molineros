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
<form action="" method="post" name="<portlet:namespace />fm2" id="<portlet:namespace />fm2" enctype="multipart/form-data">
<fieldset class="block-labels">
 
	<legend>Tercerizadoras</legend>
		<table class="lfr-table">
		<tr>
		  <td>
		    <liferay-ui:message key="entidad" />
		  </td>
		  <td>
		   <select name="<portlet:namespace />tercerizadora"
						id="<portlet:namespace />tercerizadora" onchange="">
							<%for(int i = 0; i < WebKeysAutorizaciones.INTEGRACION_ENTIDADES.length; i++ ) {%>
							<option
								value="<%=WebKeysAutorizaciones.INTEGRACION_ENTIDADES[i][0] %>">
								<%=WebKeysAutorizaciones.INTEGRACION_ENTIDADES[i][1] %>
							</option>
							<% } %>
			</select>
		   </td>
		   <td>
		      <liferay-ui:message key="carpeta" />
		   </td>
		   <td>
		      <liferay-ui:input-date dayParam="carpetaDia"
			       dayNullable="<%= true %>" dayValue=""
			       monthAndYearParam="carpetaMesAnio"
			       monthValue="<%= current.get(Calendar.MONTH)%>"
			       monthAndYearNullable="<%= true %>"
			       yearValue="<%= current.get(Calendar.YEAR) %>"
			       yearRangeStart="<%= current.get(Calendar.YEAR) - 3 %>"
			       yearRangeEnd="<%= current.get(Calendar.YEAR) + 1 %>"
			       firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
			       disabled="<%= false %>" />
		   </td>		
  		   <td  align="center">
				<input type="file" name="archivo"/>
			</td>
			<td align="center">

<!--  				<a href="javascript:void(0)" onclick="help(event, 'helpUpload')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
-->
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivo()"/>
			</td>			
		</tr>	
		
			
		</table>
		
		<table class="lfr-table">
		  <tr>
		   <td>
			  <div id="<portlet:namespace />pagos_imputados">
						<jsp:include page='/html/portlet/autorizaciones/integracion/integracion_archivos_result.jsp' />  
			  </div>
			</td>	
		  </tr>
		</table>
		
   </fieldset>
<div id="helpUpload" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" 
style="top: 200px; left: 300px">
El archivo debe ser xls y tener el siguiente formato:Tipo de archivo,Código Obra Social,CUIL,Cód.Certificado,Vencimiento certificado,
Período Prestación,CUIT Prestador,Tipo Comprobante,Tipo Emisión,Fecha emisión comprobante,CAE o CAI,Punto de Venta,Nro Comprobante,
Dependencia</div>
<input id="<portlet:namespace />fm2H" name="<portlet:namespace />fm2H" type="hidden" value="" />
</form>

<form action="" method="post" name="<portlet:namespace />fmExp" id="<portlet:namespace />fmExp">
<table>
<tr>
		  <td colspan="2">
		    <fieldset class="block-labels">
	          <legend>Exportar para FTP</legend>
	            <table>
	              <tr>
	                
	                <td>
		              <liferay-ui:message key="periodo" />
		            </td>
		            <td>
		              <liferay-ui:input-date dayParam="periodoDia"
			              dayNullable="<%= true %>" dayValue=""
			              monthAndYearParam="periodoMesAnio"
			              monthValue="<%= current.get(Calendar.MONTH)%>"
			              monthAndYearNullable="<%= true %>"
			              yearValue="<%= current.get(Calendar.YEAR) %>"
			              yearRangeStart="<%= current.get(Calendar.YEAR) - 3 %>"
			              yearRangeEnd="<%= current.get(Calendar.YEAR) + 1 %>"
			              firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
			              disabled="<%= false %>" />
		            </td>
		            <td>
		             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		            </td>
		            
<!--  		            
		             <td><input type="checkbox"  name="<portlet:namespace />informadoFTP" 
							               id="<portlet:namespace />informadoFTP" >Registrar como Informado</td>
							               
-->							               
		            <td>
		             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		            </td>
		            
		            <td align="center">
		                <input type="submit" value="<liferay-ui:message key="exportar" />" onClick="<portlet:namespace />exportarFTP()"/>
			        </td>
		          </tr>
	            </table>
	          
		    </fieldset>
		  </td>
		</tr>

</table>
</form>


<form action="" method="post" name="<portlet:namespace />fmSSS" id="<portlet:namespace />fmSSS" enctype="multipart/form-data">
<fieldset class="block-labels">
 
	<legend>Superintendencia</legend>
		<table class="lfr-table">
		<tr>
		  <td  align="center">
				<input type="file" name="archivoSSS"/>
			</td>
			<td align="center">
			<a href="javascript:void(0)" onclick="help(event, 'helpUploadSSS')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoSSS()"/>
			</td>			
		</tr>	
		
			
		</table>
		
		<table class="lfr-table">
		  <tr>
		   <td>
			  <div id="<portlet:namespace />lotes_sss">
						<jsp:include page='/html/portlet/autorizaciones/integracion/integracion_archivos_sss_result.jsp' />  
			  </div>
			</td>	
		  </tr>
		</table>
		
   </fieldset>
   <div id="helpUploadSSS" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" 
style="top: 200px; left: 300px">
El nombre del archivo debe comenzar con el nro 112608 y finalizar con la extensión .devok</div>
<input id="<portlet:namespace />fmSSSH" name="<portlet:namespace />fmSSSH" type="hidden" value="" />
</form>


<script type="text/javascript">	

<portlet:namespace />hideDayFieldOfPeriodFields ();

function <portlet:namespace />hideDayFieldOfPeriodFields () {
	jQuery("#<portlet:namespace />periodoDia").hide();
	jQuery("#<portlet:namespace />carpetaDia").hide();
}
	
function <portlet:namespace />uploadArchivo() {
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/integracion_upload_archivos';
		document.<portlet:namespace />fm2.method = 'post';
		submitForm(document.<portlet:namespace />fm2, url);
}

function <portlet:namespace />exportarFTP(){
	/*	
	    var periodo=jQuery('#<portlet:namespace />periodoMesAnio').val();
		window.location.href ='/txtservlet/?reporte=INTEGRACION_EXPORTAR_FTP'			
			+'&periodo='+periodo +'&informado='+'true';
	    setTimeout("location.reload(true);",3000);
	*/

	    var periodo=jQuery('#<portlet:namespace />periodoMesAnio').val();
	    var url = '/txtservlet/?reporte=INTEGRACION_EXPORTAR_FTP'+'&periodo='+periodo +'&informado='+'true';;
		document.<portlet:namespace />fmExp.method = 'post';
		submitForm(document.<portlet:namespace />fmExp, url);
		var url1 = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/integracion_editar';
	    url1 = url1+'&<%= Constants.CMD %>'+'='+'envio_ftp';
		setTimeout(function() {jQuery('#<portlet:namespace />lotes_sss').load(url1)}, 3000);
		
}


function <portlet:namespace />uploadArchivoSSS() {
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/integracion_upload_archivos_sss';
	document.<portlet:namespace />fmSSS.method = 'post';
	submitForm(document.<portlet:namespace />fmSSS, url);
}

function historicoLoteIntegracion(idLote){
	var confirmar = false;
	confirmar=confirm ('Esta seguro de pasar a Histórico el Período?');
	if(confirmar){
	    
   	  var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/integracion_editar';
 	  url = url+'&<%= Constants.CMD %>'+'='+'historico_lote'+'&id_lote='+idLote;
 	  document.<portlet:namespace />fmSSS.method = 'post';
	  submitForm(document.<portlet:namespace />fmSSS, url);
   	  
	}
}


</script>