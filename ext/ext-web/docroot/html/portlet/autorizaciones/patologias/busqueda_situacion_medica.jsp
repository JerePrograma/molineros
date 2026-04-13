<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/patologias/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
 
<liferay-util:include page='/html/portlet/autorizaciones/patologias/filtros_situacion_medica.jsp'>
</liferay-util:include>
 
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<input type="hidden"   name="pagina" id="pagina" value="5"/>
<script type="text/javascript">
   	
    jQuery('#<portlet:namespace />buscando').show();
   	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_situacion_medica_registros_sesion';
	jQuery('#<portlet:namespace />busquedaSituacionMedicaDiv').load(url);
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />buscar').click(function(){<portlet:namespace />busquedaSituacionMedica();});
	jQuery('#<portlet:namespace />buscando').hide();
	
	
// rutinas de botones de la grilla de busqueda 

	function <portlet:namespace />busquedaSituacionMedica(){

		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
		var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
		var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
		var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
		var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
		var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();		
		var situacionMedica =jQuery('#<portlet:namespace/>situacionMedica').val();
		 
		if(trim(cuil).length != 0 && !validarCuil(cuil,"<liferay-ui:message key='valida-cuil'/>")){
			alert("Cuil inválido");
			jQuery('#<portlet:namespace />cuil').focus();
			return false;
		}
		
	    var pagina_sel=jQuery("#<portlet:namespace/>pagina_sel").val();    
		jQuery("#pagina").val(pagina_sel);
		var entidad=jQuery('#<portlet:namespace />entidad').val();		
		jQuery('#<portlet:namespace />buscando').show();                                                          
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/buscar_situacion_medica&situacionMedica='+situacionMedica+		
		'&cuil_titular='+cuil+'&inte='+inte+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
        '&pagina_sel='+pagina_sel;    
        jQuery('#<portlet:namespace />busquedaSituacionMedicaDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );
	}
	
	
		 
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
		
		window.location.href ='/xlsservlet/?reporte=REPORTE_RESULT_BUSQUEDA_SITUACION_MEDICA'
		+ '&cuil_titular='+cuil+'&inte='+inte+
		'&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
		'&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio+
		'&situacionMedica='+situacionMedica;   
	});

   function <portlet:namespace />altaRegistroSituacionMedica() {		
	   var cuil=jQuery('#<portlet:namespace />cuil').val();
	   var inte=jQuery('#<portlet:namespace />inte').val();
	   var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_registro_situacionmedica_entry" /></portlet:renderURL>';		
	   url = url + '&cuil=' +  cuil  +'&inte='+  inte;
	   
	    document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
		
	}
	
	

	

   
</script>