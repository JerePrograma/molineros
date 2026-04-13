<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<%
		
		List<ReporteNovedadesSSSProcesadas> reportes = (ArrayList<ReporteNovedadesSSSProcesadas>) renderRequest.getAttribute("reportesNovedSSSProc"); 

		Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
		Calendar fechaPago = CalendarFactoryUtil.getCalendar();
		
		fechaInicio.add(Calendar.MONTH, -1);
		fechaPago.add(Calendar.MONTH, -1);
		Calendar current = CalendarFactoryUtil.getCalendar();
%>
		<fieldset class="block-labels">
				<legend>Reportes disponibles</legend>
				<table class="lfr-table">
					<tr>
						<td><label>Se agendará el reporte y el mismo correrá automaticamente a las 3 de la mañana del siguiente día</label></td>
						<td>
							<input id="<portlet:namespace />agendar" name="<portlet:namespace />agendar"  
								   type="button" value="Agendar" onclick="agendarReporte()" />
						</td>
						<td>
							<div id="<portlet:namespace />agendarMsg">
								<label>Se agendó correctamente el reporte</label>
							</div>
						</td>
					</tr>
				</table>	      	  
		</fieldset>
		<%
			PortletURL portletURL = renderResponse.createRenderURL();
			List<String> headerNames = new ArrayList<String>();
			int total = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");
			
			headerNames.add("Fecha Novedades");
			headerNames.add("Fecha Proceso");
			headerNames.add("Usuario");
			headerNames.add("Fecha Solicitado");
			headerNames.add("Registrado");
			headerNames.add("Poblacion");
			headerNames.add("Total novedades SSS");
			//headerNames.add("Total acumulado");
			headerNames.add("Total a procesar");
			headerNames.add("Total procesado");
			headerNames.add("Sin Procesar");
			headerNames.add("Inconsistentes");
			headerNames.add("Usuario Baja");
			headerNames.add("Fecha Baja");
/* 			headerNames.add("Listar"); */
 			
			SearchContainer searchContainer = new SearchContainer(
					renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 
					Integer.MAX_VALUE, portletURL, headerNames, 
					LanguageUtil.get(pageContext, "no-reportes-were-found"));
				
			if (null != reportes && reportes.size() > 0) {
				total = reportes.size();
				searchContainer.setTotal(total);
				List resultRows = searchContainer.getResultRows();
				for (int i = 0; i < reportes.size(); i++) {
					ReporteNovedadesSSSProcesadas rnp = (ReporteNovedadesSSSProcesadas) reportes.get(i);
					ReporteNovedadesSSSProcesadasCab cab = rnp.getCabecera();
					ReporteNovedadesSSSProcesadasDet det = rnp.getDetalle();
					String informado = "";
					if(cab.isInformar()){
						informado = "Si"; 
					}
					
					ResultRow row = new ResultRow(cab, String.valueOf(cab.getId()), i);			
					PortletURL rowURL = renderResponse.createRenderURL();
					rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
				 			rowURL.setParameter("struts_action","/correspondencia/view_correspondencia_entry");
				 			rowURL.setParameter("id_repo_nov_proc", String.valueOf(cab.getId()));
				 	
				 	row.addText(sdf2.format(cab.getFechaNovedades()));		
				 	row.addText(sdf2.format(cab.getFechaProceso()));		
					row.addText(cab.getUsuario().trim());
					row.addText(sdf.format(cab.getFechaSolicitado()));
					row.addText(informado);
					row.addText(String.valueOf(det.getPoblacion()));
					row.addText(String.valueOf(det.getTotalNovedadesSSS()));
				//	row.addText(String.valueOf(det.getTotalNovedadesAcumuladas()));
					row.addText(String.valueOf(det.getTotalNovedadesAProcesar()));
					int Novedadesresueltas = det.getTotalNovedadesResueltas() + det.getTotalNovedadesInconsistentes();
					row.addText(String.valueOf(Novedadesresueltas)); // TotalNovedadesResueltas 
					int sinProcesar = det.getTotalNovedadesAProcesar() - det.getTotalNovedadesResueltas();
					row.addText(String.valueOf(sinProcesar));
					row.addText(String.valueOf(det.getTotalNovedadesInconsistentes()));
					row.addText(det.getBajaUsr()!=null?det.getBajaUsr().trim():"");
					row.addText(det.getBajaFecha()!=null?sdf.format(det.getBajaFecha()):"");
					
					/* StringBuffer sb = new StringBuffer("");
					sb.append("<img src=\" ");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/print.png\" " );
					sb.append("alt=\"Listar\" height='16px;' weight='18px;' onClick=\"javascript:listarReporte('");
					sb.append(String.valueOf(cab.getId()));								
					sb.append("');\" />"); 
					
					row.addText(sb.toString()); */
					resultRows.add(row);
				}
			}
		%>
		<div class="search-results">
			<c:choose>
				<c:when test="<%= total != 1 %>">
					<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
				</c:when>
				<c:otherwise>
					<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
				</c:otherwise>
			</c:choose>
		</div>
		<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
		<br/>
		
		<div class="search-results">
			<c:choose>
				<c:when test="<%= total != 1 %>">
					<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
				</c:when>
				<c:otherwise>
					<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
				</c:otherwise>
			</c:choose>
		</div>
			
<script type="text/javascript">
jQuery('#<portlet:namespace />agendarMsg').hide();

function agendarReporte(){
	var tipoReporte='<%=ReporteNovedadesSSSProcesadas.REPORTE_NOVEDADES_SSS_PROCESADAS %>';	    
	var fechaProcesoDia = jQuery('#<portlet:namespace />fechaProcesoDia').val();
	var fechaProcesoMes = jQuery('#<portlet:namespace />fechaProcesoMes').val();
	var fechaProcesoAnio = jQuery('#<portlet:namespace />fechaProcesoAnio').val();
	
	var fechaPadronDesdeDia = jQuery('#<portlet:namespace />fechaPadronDesdeDia').val();
	var fechaPadronDesdeMes = jQuery('#<portlet:namespace />fechaPadronDesdeMes').val();
	var fechaPadronDesdeAnio = jQuery('#<portlet:namespace />fechaPadronDesdeAnio').val();
	
	var fechaPadronHastaDia = jQuery('#<portlet:namespace />fechaPadronHastaDia').val();
	var fechaPadronHastaMes = jQuery('#<portlet:namespace />fechaPadronHastaMes').val();
	var fechaPadronHastaAnio = jQuery('#<portlet:namespace />fechaPadronHastaAnio').val();
	/* var informar=jQuery("#<portlet:namespace />informar").is('checked'); */
	var info = document.getElementById("<portlet:namespace />informar");
	var informar = info.checked ? 'true' : 'false';	
	
	var fechaProc=jQuery('#<portlet:namespace />b_fecha_nov').val();

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/agendarCorridaDiferidaReporte'
		+'&tipoReporte='+tipoReporte
		+'&fechaProcesoDia='+fechaProcesoDia
		+'&fechaProcesoMes='+fechaProcesoMes		
		+'&fechaProcesoAnio='+fechaProcesoAnio	
		+'&fechaPadronDesdeDia='+fechaPadronDesdeDia
		+'&fechaPadronDesdeMes='+fechaPadronDesdeMes			
		+'&fechaPadronDesdeAnio='+fechaPadronDesdeAnio
		+'&fechaPadronHastaDia='+fechaPadronHastaDia
		+'&fechaPadronHastaMes='+fechaPadronHastaMes			
		+'&fechaPadronHastaAnio='+fechaPadronHastaAnio
		+'&fechaNovedad='+fechaProc	
		+'&informar='+informar;	
		
	
	jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				
				if(obj.validado!="0"){
					jQuery('#<portlet:namespace />agendarMsg').show();
				}			
			}
		}); 
}
</script>
