<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/patologias/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<liferay-util:include page='/html/portlet/autorizaciones/patologias/filtros_situacion_medica.jsp'>
</liferay-util:include>

<script type="text/javascript">

    jQuery('#<portlet:namespace />buscando').hide();
    jQuery('#<portlet:namespace />buscar').hide();
    jQuery('#<portlet:namespace />nueva').hide();
    jQuery('#<portlet:namespace />busquedaSituacionMedicaDiv').hide();
    
    jQuery('#<portlet:namespace/>situacionMedica').val('SELECCIONE');
    
	jQuery('#<portlet:namespace />exportar-busqueda').click(function exportarBusqueda(){
		
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();		
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		var situacionMedica =jQuery('#<portlet:namespace/>situacionMedica').val();
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_RESULT_BUSQUEDA_SITUACION_MEDICA_COMPLETO'
		+ '&cuil_titular='+cuil+'&inte='+inte+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&situacionMedica='+situacionMedica+'&completo=true';   
	});


   
</script>