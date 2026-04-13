<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%@ page import="ar.com.ospim.liquidaciones.beans.Prestador" %>


<liferay-ui:error key="errormedespecialperiodonocoincide" message="error-medicacion-especial-periodonocoincide"/>
<liferay-ui:error key="errormedespecialarchivonombre" message="error-medicacion-especial-archivonombre"/>
<liferay-ui:error key="errormedespecialperiodoyaprocesado" message="error-medicacion-especial-periodoyaprocesado"/>
<liferay-ui:error key="errormedespecialdatosdentrodearchivo" message="error-medicacion-especial-datosdentrodearchivo"/>

<liferay-ui:error key="errordesgloseperiodonocoincide" message="error-desglose-periodonocoincide"/>
<liferay-ui:error key="errordesglosearchivonombre" message="error-desglose-archivonombre"/>
<liferay-ui:error key="errordesgloseperiodoyaprocesado" message="error-desglose-periodoyaprocesado"/>
<liferay-ui:error key="errordesglosedatosdentrodearchivo" message="error-desglose-datosdentrodearchivo"/>

<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="Ok-medicacion-especial-grabacion" />		
	</c:when>
</c:choose>
<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed_desglose") %>'>
		<liferay-ui:success key="request_processed_desglose" message="Ok-desglose-grabacion" />		
	</c:when>
</c:choose>

<%

Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
fechaDesde.add(Calendar.MONTH, -1);
Calendar current = CalendarFactoryUtil.getCalendar();
String portlet_name=null;
portlet_name="farmaciaospim";	
List<String> errores = (List<String>)request.getAttribute("errores");
String  opcionSeleccionada;
opcionSeleccionada=(SessionMessages.contains(renderRequest, "sel_proceso_archivo")?"1":"0");
List<Prestador> list = TraeListasServiceUtil.getPrestodresInexistentesMedicacionEspecial();
%> 

<table align="center">
<tr>
<td >
<input type="radio" name="<portlet:namespace />importacion_tipo"  id="medespecial" onclick = "abrirDiv(0);" value="true" checked="checked"> Archivo Medicación Especial (Alto Costo) &nbsp;
</td>

<td >
<input type="radio" name="<portlet:namespace />importacion_tipo" id="desgloseradio" onclick = "abrirDiv(1);" value="false"> Archivo Prevención Farmacia
</td>
<td colspan="4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td>
<%if(!list.isEmpty()){ %>
   <a href="#" onclick="javascript:<portlet:namespace />prestadoresInexistentes()">Prestadores Inexistentes</a>&nbsp;
   <a href="javascript:void(0)" onclick="help(event, 'helpPrestadoresInexistentes')"><img style="height: 16px; width: 16px" src="/html/themes/classic/images/arrows/02_x.png" title="Ayuda" alt="Ayuda"/></a>			
<%}%>
</td>
</tr>
</table>



<div id="<portlet:namespace />divmedespecial">				
<form action="" method="post" name="<portlet:namespace />fm" enctype="multipart/form-data">
	<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="subir-archivo-medesp" />
	</legend>
		<table style="width: 900px;"  > <!-- class="lfr-table"-->  
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
							monthValue="<%= fechaDesde.get(Calendar.MONTH)+1 %>"
							yearParam="fechaArchivoAnio"
							yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)+1 %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
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
							<a href="javascript:void(0)" onclick="help(event, 'helpMedEsp22')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>			
						</td>
						<td>							
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
						<td><label><liferay-ui:message key="listado-archivos-importados-medespecial" />:</label></td>
					</tr>
					<tr>
				 		<td colspan="3">&nbsp;</td>
				 	</tr>	
				 	<tr>
				 		<td colspan="3">
							<div id="<portlet:namespace />archivos_novedades">								
								<jsp:include page='/html/portlet/farmacia_ospim/archivos_subidos_medespecial.jsp' />
							</div>
						</td>
					</tr>		
				</table>
			</td>			
		</tr>		
		</table>
	</fieldset>
</form>
</div>


<div id="<portlet:namespace />divarchivodesglose">				
<form action="" method="post" name="<portlet:namespace />frmmeddesglose" enctype="multipart/form-data">
	<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="subir-archivo-desglose" />
	</legend>
		<table style="width: 900px;"  >   
		<tr>
			<td>
				<table>
					<tr>						
						<td colspan="2"><label>Período archivo:</label>&nbsp;
							<liferay-ui:input-date
							dayParam="fechaDesgloseArchivoDia"
 							dayNullable="<%= true %>" 
							dayValue="01"
							monthParam="fechaDesgloseArchivoMes"
							monthValue="<%= fechaDesde.get(Calendar.MONTH)+1 %>"
							yearParam="fechaDesgloseArchivoAnio"
							yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)+1 %>"
							firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
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
							<a href="javascript:void(0)" onclick="help(event, 'helpArchivoPrevencion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>			
						</td>
						<td>							
							<input type="submit" value="<liferay-ui:message key="upload-file" />" onClick="<portlet:namespace />uploadArchivoDesglose()"/>
						</td>
					</tr>	
				</table>
			</td>			
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>
				<table>
					<tr>						
						<td><label><liferay-ui:message key="listado-archivos-importados-desglose" />:</label></td>	
					</tr>
					<tr>
				 		<td colspan="3">&nbsp;</td>
				 	</tr>	
				 	<tr>
				 		<td colspan="3">
							<div id="<portlet:namespace />archivos_novedades">								
								<jsp:include page='/html/portlet/farmacia_ospim/archivos_prevencion_liquidacion.jsp' />
							</div>
						</td>
					</tr>		
				</table>
			</td>			
		</tr>		
		</table>
	</fieldset>
</form>
</div>



<div id="helpArchivoPrevencion" class="containerPlus draggable {buttons:'c', skin:'default', width:'700',title:'Ayuda',closed:'true'}" style="top: 20px; left: 150px">
El diseño del archivo prevencion farmacia es:<br> 
periodo,cod_col,colegio,cod_farmacia,farmacia,cuit,direccion,localidad,region,codReg,codReceta,orden,env,pvp,entidad,porcentaje,troquel<br>
,registro,nombre comercial,pot,forma farm,cont,principio,accion,fecha,dispensa,matricula,profesional,grupo<br>,nombre_benef,tp 
  
<hr>
Los datos deben ir separados por | (pipe) <br>
El archivo informado debe llamarse <b>CONSUMOAMBULATORIO_mmaaaa.CSV</b>&nbsp;donde:<br>
mm    : indica el mes del archivo a procesar de 01 a 12. <br>
aaaa  : indica el año del archivo a procesar por ejemplo 2017,2018,... 
<hr>
El sistema valida que:<br>
 <ul>
  <li>El nombre del archivo cumpla con el formato requerido.</li>
  <li>No se procese un archivo de per&iacute;odo ya procesado</li>
  <li>El archivo debe incluir la primera fila con los nombres de las columnas (encabezado)</li>  
</ul> 
</div>


<div id="helpMedEsp22" class="containerPlus draggable {buttons:'c', skin:'default', width:'700',title:'Ayuda',closed:'true'}" style="top: 100px; left: 250px">
El formato de archivo medicaci&oacuten especial es:<br> 
Periodo,Fec Doc Cont , Nro Req , Nombre Tipo Proveedor , Proveedor , Cuil Afiliado , Nombre Afiliado , 
<br> Desc Articulo , Cantidad , Moneda , Precio Unitario Sin Iva , Iva Unitario , Precio Unitario Con Iva ,  <br>
Total Con Iva, Iva Total  , Total Sin Iva , Cuenta , Plan , Afiliado, Documento , Troquel

 
<hr>
Los datos deben ir separados por ; (punto y coma)<br>
El archivo informado debe llamarse <b>MEDESPmmaaaa.CSV</b>&nbsp;donde:<br>
mm    : indica el mes del archivo a procesar de 01 a 12. <br>
aaaa  : indica el año del archivo a procesar por ejemplo 2017,2018,... 
<hr>
El sistema valida que:<br>
 <ul>
  <li>La primer columna Periodo cumpla el formato mm/aaaa, por ejemplo 08/2017.</li>
  <li>No se procese un archivo de per&iacute;odo ya procesado</li>  
</ul> 
</div>




<!-- Div para mostrar procesando -->
<div align="center" id="<portlet:namespace />procesando">
		<table style="align:center;">
		<tr>
		<td><liferay-ui:message key='procesando'/></td>
		<td align="center">
			<img alt="<liferay-ui:message key='procesando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
		</tr>
		</table>		
</div>
			

<div id="helpPrestadoresInexistentes" class="containerPlus draggable {buttons:'c', skin:'default', width:'700',title:'Ayuda',closed:'true'}" style="top: 20px; left: 150px">
Listado de Prestadores no encontrados con el proceso de Medicación Especial
</div>





<script type="text/javascript">	
	
jQuery("#<portlet:namespace />fechaArchivoDia").hide();
jQuery("#<portlet:namespace />fechaDesgloseArchivoDia").hide();
jQuery("#<portlet:namespace />divarchivodesglose").hide();
jQuery("#<portlet:namespace />procesando").hide();

var radio = document.getElementsByClassName('<portlet:namespace />importacion_tipo');

<% if (opcionSeleccionada=="1"){%>
    jQuery("#desgloseradio").attr('checked', 'checked');
    abrirDiv("1");
<%}%>



	function <portlet:namespace />buscaArchivos(){
		var fechaUltimoArchivoDia=jQuery('#<portlet:namespace />fechaUltimoArchivoDia').val();			
		var fechaUltimoArchivoMes=jQuery('#<portlet:namespace />fechaUltimoArchivoMes').val();		
		var fechaUltimoArchivoAnio=jQuery('#<portlet:namespace />fechaUltimoArchivoAnio').val();	

		<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscaArchivosNovedades&
		fechaUltimoArchivoDia='+fechaUltimoArchivoDia+'&fechaUltimoArchivoMes='+fechaUltimoArchivoMes+'&fechaUltimoArchivoAnio='+fechaUltimoArchivoAnio;		
 		 --%>
  		
		var xportletUrl = '/afiliados/buscaArchivosNovedades';
		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="fechaUltimoArchivoDia" value="__fechaUltimoArchivoDia"/>'+
		'<liferay-portlet:param name="fechaUltimoArchivoMes" value="__fechaUltimoArchivoMes"/>'+
		'<liferay-portlet:param name="fechaUltimoArchivoAnio" value="__fechaUltimoArchivoAnio"/>'+
		'</liferay-portlet:renderURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__fechaUltimoArchivoDia", fechaUltimoArchivoDia);
  	    url = url.replace("__fechaUltimoArchivoMes", fechaUltimoArchivoMes);
  	    url = url.replace("__fechaUltimoArchivoAnio",fechaUltimoArchivoAnio );
  		
  		jQuery("#<portlet:namespace />archivos_novedades").load(url);
 		
	}

	
	function <portlet:namespace />buscaArchivosDesglose(){
		var fechaUltimoArchivoDia=jQuery('#<portlet:namespace />fechaUltimoArchivoDia').val();			
		var fechaUltimoArchivoMes=jQuery('#<portlet:namespace />fechaUltimoArchivoMes').val();		
		var fechaUltimoArchivoAnio=jQuery('#<portlet:namespace />fechaUltimoArchivoAnio').val();	
  		<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscaArchivosNovedades&
  		fechaUltimoArchivoDia='+fechaUltimoArchivoDia+'&fechaUltimoArchivoMes='+fechaUltimoArchivoMes+'&fechaUltimoArchivoAnio='+fechaUltimoArchivoAnio;
  		 --%>
        var xportletUrl = '/afiliados/buscaArchivosNovedades';		
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__xportletUrl" />'+
		'<liferay-portlet:param name="fechaUltimoArchivoDia" value="__fechaUltimoArchivoDia"/>'+
		'<liferay-portlet:param name="fechaUltimoArchivoMes" value="__fechaUltimoArchivoMes"/>'+
		'<liferay-portlet:param name="fechaUltimoArchivoAnio" value="__fechaUltimoArchivoAnio"/>'+
		'</liferay-portlet:renderURL>';
	
	    url = url.replace("__xportletUrl",xportletUrl); 
  	    url = url.replace("__fechaUltimoArchivoDia", fechaUltimoArchivoDia);
  	    url = url.replace("__fechaUltimoArchivoMes", fechaUltimoArchivoMes);
  	    url = url.replace("__fechaUltimoArchivoAnio",fechaUltimoArchivoAnio );
  	    
 		jQuery("#<portlet:namespace />archivos_novedades").load(url);   
	}
		
		function <portlet:namespace />uploadArchivo() {
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_alto_costo';
			document.<portlet:namespace />fm.method = 'post';
			submitForm(document.<portlet:namespace />fm, url);
		}
		
		function <portlet:namespace />uploadArchivoDesglose() {
			jQuery("#<portlet:namespace />procesando").show("slow");			
			var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_desglose_farmacia';
			document.<portlet:namespace />frmmeddesglose.method = 'post';			
			submitForm(document.<portlet:namespace />frmmeddesglose, url);
			jQuery("#<portlet:namespace />procesando").hide();
		}
		

	function abrirDiv(tipo){		
			if(tipo==0 ){
				jQuery("#<portlet:namespace />divarchivodesglose").hide();
				jQuery("#<portlet:namespace />divmedespecial").show();
			}else{
				jQuery("#<portlet:namespace />divmedespecial").hide();
				jQuery("#<portlet:namespace />divarchivodesglose").show();
			}	
		    
	}	
	
	function <portlet:namespace />prestadoresInexistentes(){
		window.location.href ='/xlsservlet/?reporte=REPORTE_PRESTADORES_INEXISTENTES';
	}
		
</script>