<%@page import="ar.com.ospim.afip.beans.ReporteDeudaEmpresaListado"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab"%>
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);
		
		List<ReporteDeudaEmpresaCab> reportes = (ArrayList<ReporteDeudaEmpresaCab>) renderRequest.getAttribute("reportesDeuEmpPeriodo"); 
	
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
						<td><label>Recomendamos agendar el reporte si se incluyen mas de 1 período, 
								este se correrá automaticamente a las 3 de la mañana del siguiente día </label></td>
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
			
			headerNames.add("Usuario");
			headerNames.add("Fecha Proceso");
			headerNames.add("Listar");
			
			SearchContainer searchContainer = new SearchContainer(
					renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 
					Integer.MAX_VALUE, portletURL, headerNames, 
					LanguageUtil.get(pageContext, "no-reportes-were-found"));
				
			if (null != reportes && reportes.size() > 0) {
				total = reportes.size();
				searchContainer.setTotal(total);
				List resultRows = searchContainer.getResultRows();
				for (int i = 0; i < reportes.size(); i++) {
					ReporteDeudaEmpresaCab cab = (ReporteDeudaEmpresaCab) reportes.get(i);
					ResultRow row = new ResultRow(cab, String.valueOf(cab.getId()), i);			
					PortletURL rowURL = renderResponse.createRenderURL();
					rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		 			rowURL.setParameter("struts_action","/correspondencia/view_correspondencia_entry");
		 			rowURL.setParameter("id_repo_deu_emp_periodo", String.valueOf(cab.getId()));
		 			
					row.addText(cab.getUsuario().trim());
					row.addText(sdf.format(cab.getFechaProceso()));
					
					StringBuffer sb = new StringBuffer("");
					sb.append("<img src=\" ");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/print.png\" " );
					sb.append("alt=\"Listar\" height='16px;' weight='18px;' onClick=\"javascript:listarReporte('");
					sb.append(String.valueOf(cab.getId()));								
					sb.append("');\" />"); 
					
					row.addText(sb.toString());
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
	
	var tipoReporte='<%=ReporteDeudaEmpresaListado.REPORTE_DEUDA_EMPRESAS_PERIODO%>';	    
	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
	var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

	var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
	var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
	var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
	var agrupar_remuneracion= 	jQuery("#<portlet:namespace />agrupar_remuneracion").is(':checked');
	var sin_deuda=jQuery("#<portlet:namespace />incluir_sin_deuda").is(':checked');
	
	var ramo_desde=document.getElementById("<portlet:namespace />id_ramo");
	var ramo_hasta=document.getElementById("<portlet:namespace />id_ramo_hasta");
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/agendarCorridaDiferidaReporte'
		+'&tipoReporte='+tipoReporte
		+'&fechaDesdeDia='+fechaDesdeDia.value
		+'&fechaDesdeMes='+fechaDesdeMes.value
		+'&fechaDesdeAnio='+fechaDesdeAnio.value
		+'&fechaHastaDia='+fechaHastaDia.value
		+'&fechaHastaMes='+fechaHastaMes.value
		+'&fechaHastaAnio='+fechaHastaAnio.value
		+'&agrupar_remuneracion='+agrupar_remuneracion
		+'&sin_deuda='+sin_deuda
		+'&ramo_desde='+ramo_desde.value
		+'&ramo_hasta='+ramo_hasta.value;
		
	
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

function listarReporte(id){
	
	window.location.href ='/xlsservlet/?reporte=REPORTE_DEUDA_EMPRESA_PERIODO_CONSOLIDADO'
						  +'&idReporte='+id;
			
	<%-- var tipoReporte='<%=ReporteDeudaEmpresaCab.REPORTE_DEUDA_EMPRESAS_PERIODO%>';	    
	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia1");
	var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes1");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaInicioAnio1");

	var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia2");
	var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes2");
	var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio2");
	var agrupar_remuneracion= 	jQuery("#<portlet:namespace />agrupar_remuneracion").is(':checked');
	var sin_deuda=jQuery("#<portlet:namespace />incluir_sin_deuda").is(':checked');
	
	var ramo_desde=document.getElementById("<portlet:namespace />id_ramo");
	var ramo_hasta=document.getElementById("<portlet:namespace />id_ramo_hasta");
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/listarReporteDeudaEmpPeriodo'
		+'&idReporte='+id;

	jQuery.ajax({   
			url: url,
			success: function(data){
				var obj = jQuery.parseJSON(data);
				
				if(obj.validado=="0"){
					/*jQuery('#<portlet:namespace />valida_fechas').val("OK"); */
				}			
			}
		});  --%>
	
		
}
</script>
