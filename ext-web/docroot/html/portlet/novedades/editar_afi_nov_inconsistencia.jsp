<%@ include file="/html/portlet/novedades/init.jsp" %>

<% 
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
NovedadTotal nov=(NovedadTotal)row.getObject();

String marcarInconsistenciaURL="javascript:marcarInconsistencia('"+nov.getId()+"','"+nov.getIdProceso()+"');";
String desmarcarInconsistenciaURL="javascript:desmarcarInconsistencia('"+nov.getId()+"','"+nov.getIdProceso()+"');";

String cuil=null, inte=null;

if("NOVEDADES".equalsIgnoreCase(nov.getTipoNovedad()) && nov.getInconsistencia() == 0){ 
	%>
	<liferay-ui:icon-menu>
			<liferay-ui:icon image="../common/add"
				message="Marcar Inconsistencia"
				url="<%=marcarInconsistenciaURL%>"/>

	</liferay-ui:icon-menu>
	<%
}else{
	%>
		<liferay-ui:icon-menu>
			<liferay-ui:icon image="../common/close"
				message="Inconsistencia"
				url="<%=desmarcarInconsistenciaURL%>"/>

	</liferay-ui:icon-menu>
<%
}

 %>


<script type="text/javascript">




function marcarInconsistencia(idInconsistencia, idProceso){
	var cuil_titular=jQuery('#<portlet:namespace />b_cuil_titular').val();
	var cuil=jQuery('#<portlet:namespace />b_cuil').val();
	var tipoDoc=jQuery('#<portlet:namespace />b_tipoDoc').val();		
	var nroDoc=jQuery('#<portlet:namespace />b_nroDoc').val();			
	var apellido=jQuery('#<portlet:namespace />b_apellido').val();		
	var nombre=jQuery('#<portlet:namespace />b_nombre').val();
	var tipoOri=jQuery('#<portlet:namespace />b_tipoOri').val();
	var fechaProc=jQuery('#<portlet:namespace />b_fecha_nov').val();
	var tipoNov=jQuery('#<portlet:namespace />b_tipoNov').val();

    var mesHasta=parseInt(jQuery('#<portlet:namespace />b_periodo_hastaMes').val())+1;	    
    var anioHasta=jQuery('#<portlet:namespace />b_periodo_hastaAnio').val();
    var tipoNovEmp=jQuery('#<portlet:namespace />b_tipo_novedad_Emp').val();
	
	
	
	var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val(); 

	jQuery('#<portlet:namespace />buscando').show();

	var url = "";
	var tipoOrigen=jQuery('#<portlet:namespace/>b_tipoOri').val();idProceso
	idInconsistencia
	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_novedades&cuil_titular='+cuil_titular+
		'&cuil='+cuil+'&tipoDoc='+tipoDoc+'&nroDoc='+escape(nroDoc)+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+
		'&tipoOri='+tipoOri+'&tipoNov='+tipoNov+'&fechaProc='+encodeURI(fechaProc)+'&pagina='+offset_reg+'&idInconsistencia='+idInconsistencia
		+'&idProceso='+idProceso+'&accInconsistencia='+'ALTA';
	
	


    jQuery('#<portlet:namespace />busquedaNovedadDiv').load(url, function() {
    							jQuery('#<portlet:namespace />buscando').hide();            															
        				});
}




function desmarcarInconsistencia(idInconsistencia, idProceso){
	var cuil_titular=jQuery('#<portlet:namespace />b_cuil_titular').val();
	var cuil=jQuery('#<portlet:namespace />b_cuil').val();
	var tipoDoc=jQuery('#<portlet:namespace />b_tipoDoc').val();		
	var nroDoc=jQuery('#<portlet:namespace />b_nroDoc').val();			
	var apellido=jQuery('#<portlet:namespace />b_apellido').val();		
	var nombre=jQuery('#<portlet:namespace />b_nombre').val();
	var tipoOri=jQuery('#<portlet:namespace />b_tipoOri').val();
	var fechaProc=jQuery('#<portlet:namespace />b_fecha_nov').val();
	var tipoNov=jQuery('#<portlet:namespace />b_tipoNov').val();

    var mesHasta=parseInt(jQuery('#<portlet:namespace />b_periodo_hastaMes').val())+1;	    
    var anioHasta=jQuery('#<portlet:namespace />b_periodo_hastaAnio').val();
    var tipoNovEmp=jQuery('#<portlet:namespace />b_tipo_novedad_Emp').val();
	
	
	
	var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val(); 

	jQuery('#<portlet:namespace />buscando').show();

	var url = "";
	var tipoOrigen=jQuery('#<portlet:namespace/>b_tipoOri').val();idProceso
	idInconsistencia
	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/buscar_novedades&cuil_titular='+cuil_titular+
		'&cuil='+cuil+'&tipoDoc='+tipoDoc+'&nroDoc='+escape(nroDoc)+'&nombre='+encodeURI(nombre)+'&apellido='+encodeURI(apellido)+
		'&tipoOri='+tipoOri+'&tipoNov='+tipoNov+'&fechaProc='+encodeURI(fechaProc)+'&pagina='+offset_reg+'&idInconsistencia='+idInconsistencia
		+'&idProceso='+idProceso+'&accInconsistencia='+'BAJA';
	
	


    jQuery('#<portlet:namespace />busquedaNovedadDiv').load(url, function() {
    							jQuery('#<portlet:namespace />buscando').hide();            															
        				});
}






</script>

