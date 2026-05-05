<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%

String portlet_name=null;
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "liquidaciones";
}

int entidad=2;
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
	entidad=1;
} 
if(renderResponse.getNamespace().equals("_FAR_1_")){
	portlet_name = "farmacia";
	entidad=2;
}

Calendar current = CalendarFactoryUtil.getCalendar();
List<Localidad> percepciones = TraeListasServiceUtil.getPercepcionesIIBB(entidad);
%>
  <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
			        <tr>
			        
			            <td>
				           <input type="radio" name="<portlet:namespace />libro_iva" value="COMPRAS" checked="checked">Compras &nbsp;
				           <input type="radio" name="<portlet:namespace />libro_iva" value="VENTAS">Ventas
			            </td>
			            <td><label>Desde:</label></td>
						<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaDesdeDiaFiltro"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaDesdeMesFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaDesdeAnioFiltro"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
						
						
						<td><label>Hasta:</label></td>
						<td colspan="2">
							<liferay-ui:input-date
							dayParam="fechaHastaDiaFiltro"
							dayValue="-1" 
							dayNullable="<%= true %>"
							monthParam="fechaHastaMesFiltro"
							monthValue="-1"	
							monthNullable="<%= true %>"			
							yearParam="fechaHastaAnioFiltro"
							yearValue="-1"
							yearNullable="<%= true %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
							yearRangeEnd="<%= current.get(Calendar.YEAR)%>"
							firstDayOfWeek="<%= current.getFirstDayOfWeek() - 1 %>"
							disabled="false" />
						</td>
					</tr>
					<tr>	
						<td>							
							<input id="<portlet:namespace />planilla" value="Planilla" title="Obtener Planilla" type="button"/>
							&nbsp;&nbsp;
						</td>
						
						<td>							
							<input id="<portlet:namespace />planilla_rg_3685_cptes" value="Comprobantes RG 3685" title="Obtener Archivo" type="button"/>
							&nbsp;&nbsp;
						</td>
						
						<td>							
							<input id="<portlet:namespace />planilla_rg_3685_alicuotas" value="Alícuotas RG 3685" title="Obtener Archivo" type="button"/>
							&nbsp;&nbsp;
						</td>	
						
						<td>							
							<input id="<portlet:namespace />percepcionesARBA" value="Percepciones ARBA" title="Obtener Archivo" type="button"/>
							&nbsp;&nbsp;
						</td>	
						
						<td>							
							<input id="<portlet:namespace />retencionesARBA" value="Retenciones ARBA" title="Obtener Archivo" type="button"/>
							&nbsp;&nbsp;
						</td>
						
						<td>							
							<input id="<portlet:namespace />retencionesARBA122" value="Retenciones ARBA - A122R" title="Obtener Archivo" type="button"/>
							&nbsp;&nbsp;
						</td>
						
					</tr>
				    
</table>	   
<div align="center" id="<portlet:namespace />buscando">
	<table style="align:center;">
		<tr>
			<td><liferay-ui:message key='buscando'/></td>
				<td align="center">					
							<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
			</td>
		</tr>
	</table>		
</div>		   
<div id='divPercepciones' style="float:right;">
</div>
	
<script type="text/javascript">

jQuery('#<portlet:namespace />buscando').hide();	

jQuery('#<portlet:namespace />planilla').click(function(){
	
	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDiaFiltro').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMesFiltro').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnioFiltro').val();
	
	var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDiaFiltro').val();
	var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMesFiltro').val();
	var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnioFiltro').val();
	
	var libro = jQuery("input[name='<portlet:namespace />libro_iva']:checked").val();
	
	if(fechaDesdeDia=="" || fechaDesdeMes=="" || fechaDesdeAnio=="" || fechaHastaDia=="" || fechaHastaMes=="" || fechaHastaAnio==""){
		alert("Debe ingresar las fechas de búsqueda");
		return false;
	}
			
    window.location.href ='/xlsservlet/?reporte=LIBRO_IVA'
		+'&fechadesdedia='+fechaDesdeDia
		+'&fechadesdemes='+fechaDesdeMes
		+'&fechadesdeanio='+fechaDesdeAnio
		+'&fechahastadia='+fechaHastaDia
		+'&fechahastames='+fechaHastaMes
		+'&fechahastaanio='+fechaHastaAnio
		+'&libro='+libro
		+'&entidad=<%=entidad%>';
    
});


jQuery('#<portlet:namespace />planilla_rg_3685_cptes').click(function(){
	
	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDiaFiltro').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMesFiltro').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnioFiltro').val();
	
	var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDiaFiltro').val();
	var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMesFiltro').val();
	var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnioFiltro').val();
	var libro = jQuery("input[name='<portlet:namespace />libro_iva']:checked").val();
	
	if(fechaDesdeDia=="" || fechaDesdeMes=="" || fechaDesdeAnio=="" || fechaHastaDia=="" || fechaHastaMes=="" || fechaHastaAnio==""){
		alert("Debe ingresar las fechas de búsqueda");
		return false;
	}
			
    window.location.href ='/txtservlet/?reporte=RG3685_LIBRO_IVA_CPTES'
		+'&fechadesdedia='+fechaDesdeDia
		+'&fechadesdemes='+fechaDesdeMes
		+'&fechadesdeanio='+fechaDesdeAnio
		+'&fechahastadia='+fechaHastaDia
		+'&fechahastames='+fechaHastaMes
		+'&fechahastaanio='+fechaHastaAnio
		+'&libro='+libro
		+'&entidad=<%=entidad%>';
    
});


jQuery('#<portlet:namespace />planilla_rg_3685_alicuotas').click(function(){

	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDiaFiltro').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMesFiltro').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnioFiltro').val();
	
	var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDiaFiltro').val();
	var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMesFiltro').val();
	var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnioFiltro').val();
	var libro = jQuery("input[name='<portlet:namespace />libro_iva']:checked").val();
	
	if(fechaDesdeDia=="" || fechaDesdeMes=="" || fechaDesdeAnio=="" || fechaHastaDia=="" || fechaHastaMes=="" || fechaHastaAnio==""){
		alert("Debe ingresar las fechas de búsqueda");
		return false;
	}
			
    window.location.href ='/txtservlet/?reporte=RG3685_LIBRO_IVA_ALICUOTAS'
		+'&fechadesdedia='+fechaDesdeDia
		+'&fechadesdemes='+fechaDesdeMes
		+'&fechadesdeanio='+fechaDesdeAnio
		+'&fechahastadia='+fechaHastaDia
		+'&fechahastames='+fechaHastaMes
		+'&fechahastaanio='+fechaHastaAnio
		+'&libro='+libro
		+'&entidad=<%=entidad%>';
    
});

jQuery('#<portlet:namespace />percepcionesARBA').click(function(){

	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDiaFiltro').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMesFiltro').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnioFiltro').val();
	
	var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDiaFiltro').val();
	var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMesFiltro').val();
	var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnioFiltro').val();
	var libro = jQuery("input[name='<portlet:namespace />libro_iva']:checked").val();
	
	if(fechaDesdeDia=="" || fechaDesdeMes=="" || fechaDesdeAnio=="" || fechaHastaDia=="" || fechaHastaMes=="" || fechaHastaAnio==""){
		alert("Debe ingresar las fechas de búsqueda");
		return false;
	}
	
    window.location.href ='/txtservlet/?reporte=PERCEPCIONES_ARBA_IIBB'
		+'&fechadesdedia='+fechaDesdeDia
		+'&fechadesdemes='+fechaDesdeMes
		+'&fechadesdeanio='+fechaDesdeAnio
		+'&fechahastadia='+fechaHastaDia
		+'&fechahastames='+fechaHastaMes
		+'&fechahastaanio='+fechaHastaAnio
		+'&entidad=<%=entidad%>';
    
});

jQuery('#<portlet:namespace />retencionesARBA').click(function(){

	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDiaFiltro').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMesFiltro').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnioFiltro').val();
	
	var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDiaFiltro').val();
	var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMesFiltro').val();
	var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnioFiltro').val();
	var libro = jQuery("input[name='<portlet:namespace />libro_iva']:checked").val();
	
	if(fechaDesdeDia=="" || fechaDesdeMes=="" || fechaDesdeAnio=="" || fechaHastaDia=="" || fechaHastaMes=="" || fechaHastaAnio==""){
		alert("Debe ingresar las fechas de búsqueda");
		return false;
	}
	
    window.location.href ='/txtservlet/?reporte=RETENCIONES_ARBA_IIBB'
		+'&fechadesdedia='+fechaDesdeDia
		+'&fechadesdemes='+fechaDesdeMes
		+'&fechadesdeanio='+fechaDesdeAnio
		+'&fechahastadia='+fechaHastaDia
		+'&fechahastames='+fechaHastaMes
		+'&fechahastaanio='+fechaHastaAnio
		+'&entidad=<%=entidad%>';
    
});


jQuery('#<portlet:namespace />retencionesARBA122').click(function(){

	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDiaFiltro').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMesFiltro').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnioFiltro').val();
	
	var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDiaFiltro').val();
	var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMesFiltro').val();
	var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnioFiltro').val();
	var libro = jQuery("input[name='<portlet:namespace />libro_iva']:checked").val();
	
	if(fechaDesdeDia=="" || fechaDesdeMes=="" || fechaDesdeAnio=="" || fechaHastaDia=="" || fechaHastaMes=="" || fechaHastaAnio==""){
		alert("Debe ingresar las fechas de búsqueda");
		return false;
	}
	
    window.location.href ='/txtservlet/?reporte=RETENCIONES_ARBA_IIBB_A122'
		+'&fechadesdedia='+fechaDesdeDia
		+'&fechadesdemes='+fechaDesdeMes
		+'&fechadesdeanio='+fechaDesdeAnio
		+'&fechahastadia='+fechaHastaDia
		+'&fechahastames='+fechaHastaMes
		+'&fechahastaanio='+fechaHastaAnio
		+'&entidad=<%=entidad%>';
    
});

</script>