<%@page import="ar.com.ospim.afiliados.WebKeysAfiliados"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
		BusquedaConsultasIGSFiltro filtro = (BusquedaConsultasIGSFiltro) session.getAttribute(WebKeysAfiliados.BUSQUEDA_CONSULTAS_IGS_FILTRO);
 		
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
 		
 		if(filtro!=null && filtro.getFechaDesde()!=null){
			fechaDesde.setTime(filtro.getFechaDesde());
		}else{
			fechaDesde.setTime(new Date());
		}
		
		if(filtro!=null && filtro.getFechaHasta()!=null){
			fechaHasta.setTime(filtro.getFechaHasta());
		}else{
			fechaHasta.setTime(new Date());
		}
%>

<fieldset class="block-labels"><legend><liferay-ui:message	key="consulta-igs" /></legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	<tr>
		<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesdeDiaBusq"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			monthParam="fechaDesdeMesBusq"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			yearParam="fechaDesdeAnioBusq"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>			
			
		<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
		<td><liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
			yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
			disabled="<%= false %>" /></td>			
	</tr>
	<tr>
		<td align="right" colspan="3">
		<input
			id="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar" />"
			onClick="javascript:<portlet:namespace />buscarConsultasIGS();" type="button" />&nbsp;
		</td>
		<td>
			<input type="button" id="<portlet:namespace />reporte" value="<liferay-ui:message key="obtener-excel"/>" onClick="<portlet:namespace />reporteXLS();" />
		</td>
	</tr>
</table>
</fieldset>

<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />busquedaConsultaIGSDiv">
	<liferay-util:include page="/html/portlet/autorizaciones/reportes/consultas_igs_search_result.jsp"></liferay-util:include>
</div>
</fieldset>

<script type="text/javascript">

	jQuery('#<portlet:namespace />buscando').hide();
	
	function <portlet:namespace />buscarConsultasIGS(){

		jQuery('#<portlet:namespace />buscando').show();
		
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;	    		

		var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
		
		var busquedaCorr = { "fechaDesdeFinal": fechaDesdeFinal, 
							 "fechaHastaFinal": fechaHastaFinal, 
							 "pagina" : offset_reg};

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/reporte_igs" /></portlet:renderURL>';
		
        jQuery('#<portlet:namespace />busquedaConsultaIGSDiv').load(url,busquedaCorr, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  });
	}
	
	function <portlet:namespace />reporteXLS(){
		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDiaBusq').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMesBusq').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnioBusq').val();
	    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
		var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();	    
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;	    
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
	    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;	    
		
		 window.location.href ='/xlsservlet/?reporte=REPORTE_CONSULTA_IGS'
			 +'&fechaDesdeFinal='+fechaDesdeFinal
			 +'&fechaHastaFinal='+fechaHastaFinal; 
	}
</script>