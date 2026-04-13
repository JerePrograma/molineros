<%@ include file="/html/portlet/farmacia_ospim/init.jsp"%>
<%

String procesoVademecum=(String) request.getAttribute(WebKeysFarmaciaOspim.VADEMECUM_PROCESADO );

String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "farmaciaospim";
	}
	if(renderResponse.getNamespace().equals("_LIQ_1_")){
		portlet_name = "liquidaciones";
	}

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

Calendar fechaPeriodo = CalendarFactoryUtil.getCalendar();

%>
</form>

<input type="Hidden"  id="<portlet:namespace />conArchivoAbierto" name="<portlet:namespace />conArchivoAbierto" value="" />	
		
<form action="" method="get" name="<portlet:namespace />fm2" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="subir-archivo-farmacia" />
	</legend>		
		<table class="lfr-table">
			<tr>
			   <td>
				    <label>Período archivos:</label>		 
 					<liferay-ui:input-date dayParam="periodoDia"
						dayValue=""
						dayNullable="<%= true %>" monthParam="periodoMes"
						monthValue="-1"
						monthNullable="<%= true %>" yearParam="periodoAnio"
						yearValue=""
						yearNullable="<%= true %>"
						yearRangeStart="<%= fechaPeriodo.get(Calendar.YEAR) -1%>"
						yearRangeEnd="<%= fechaPeriodo.get(Calendar.YEAR) + 1 %>"
						firstDayOfWeek="<%= fechaPeriodo.getFirstDayOfWeek()  %>"
						disabled="<%= false %>" />									
				</td>
				<td><liferay-ui:message key="manual-dat" />:</td>				
				<td  align="center">
					<input type="file" name="archivoManualDat"/>
				</td>
				<td>		
					<a href="javascript:void(0)" onclick="help(event, 'helpArchivoManualDat')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>			
				</td>				
				<td><liferay-ui:message key="listado-sssalud" />:</td>
				<td  align="center">
					<input type="file" name="archivoListadoSSSalud"/>
				</td>
				<td>		
					<a href="javascript:void(0)" onclick="help(event, 'helpArchivoSSS')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>			
				</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
			<tr>				
				<td colspan="2" align="right">
					<input id="<portlet:namespace />updloadfiles" type="submit" value="<liferay-ui:message key="upload-files" />" onClick="<portlet:namespace />uploadArchivos();"/>
				</td>
				<td>
					<input type="button" value="<liferay-ui:message key="exportar-vademecum-altas-bajas" />" onClick="<portlet:namespace />excelAltaBajas()"/>					
				</td>
			</tr>
		</table>
	</fieldset>
	
	
	
	
	<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="Periodos Procesados" />
	</legend>
	<table>
		<tr>
	 		<td colspan="3">&nbsp;</td>
	 	</tr>	
	 	<tr>
	 		<td colspan="3">
				<div id="<portlet:namespace />archivos_novedades">								
					<jsp:include page='/html/portlet/farmacia_ospim/archivos_procesados_vademedecum.jsp' /> 
				</div>
			</td>
		</tr>		
	</table>				
	</fieldset>


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

<div id="helpArchivoManualDat" class="containerPlus draggable {buttons:'c', skin:'default', width:'400',title:'Ayuda',closed:'true'}" style="top: 20px; left: 120px">
El archivo Manual Dat que se procesa debe tener formato .ZIP<br> 
Dentro del archivo se encuentra el archivo manual.dat<br> 
<hr>

</div>

<div id="helpArchivoSSS" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 20px; left: 150px">
El archivo de la SSS es de formato XLS<br> 
El archivo debe incluir las 4 filas iniciales con informacion de la SSS.<br>
El archivo en la fila 5 debe contener las cabeceras de los datos.<br>
Las columnas son: id atc generico nombre presentacion pvp acargoos acargoafil laboratorio registro pr grupoter obser<br/>

<hr>

</div>




<script type="text/javascript">

<% if(procesoVademecum!=null  && procesoVademecum.equals("Si") ) {%>
      <portlet:namespace />excelAltaBajas();
<%}%>

jQuery("#<portlet:namespace />procesando").hide();
jQuery("#<portlet:namespace />periodoDia").hide();

	function <portlet:namespace />uploadArchivos() {
		
		if ( jQuery("#<portlet:namespace />conArchivoAbierto").val()==1) {
			alert('Debe cerrar primero el proceso abierto.');
			return false;
		}
		if ( jQuery("#<portlet:namespace />periodoMes").val()=="" ||
				jQuery("#<portlet:namespace />periodoAnio").val()=="") {
			alert('Debe seleccionar un período.');
			return false;
		}
		
		jQuery("#<portlet:namespace />procesando").show("slow");
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/upload_archivo_farmacia';
		document.<portlet:namespace />fm2.method = 'post';		
		submitForm(document.<portlet:namespace />fm2, url); //, url+'&archivo=todos'
		jQuery("#<portlet:namespace />procesando").hide();
	}
	
	function <portlet:namespace />excelAltaBajas(){
		window.location.href ='/xlsservlet/?reporte=ALTAS_BAJAS_VADEMECUM';
	}

		
		
</script>
