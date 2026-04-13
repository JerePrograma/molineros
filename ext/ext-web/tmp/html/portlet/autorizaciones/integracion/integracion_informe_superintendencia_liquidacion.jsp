<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<portlet:defineObjects/>
<%
 
    Integer idLote = ParamUtil.getInteger(request, "nrolote");
    List<TercerizadoraServicio> tercServList=TraeListasServiceUtil.getTercerizadoraServicio();
    String tercValidas = TraeListasServiceUtil.getSystemConfig("INTEGRACION_TERCERIZADORAS");
    
    Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
    fechaDesde.add(Calendar.MONTH, -1);
    Calendar current = CalendarFactoryUtil.getCalendar();
   
%>
<fieldset class="block-labels">
		<legend>Impresión Liquidación Superintendencia</legend>			
				<table class="lfr-table">
					<tr>
					
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
					</tr>									
				</table>
								
				<div align="center" id="<portlet:namespace />buscandoIMPDet">
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='buscando'/></td>
							<td align="center">					
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>
</fieldset>								
			


<input id="<portlet:namespace />imprimirDetalleLiquidacion" value="<liferay-ui:message key="imprimir"/>" title="Imprimir Detalle Liquidación" type="button" onClick="javascript:imprimirDetalleLiquidacionIntegracion();"/>
	
<script type="text/javascript">
jQuery('#<portlet:namespace />buscandoIMPDet').hide();
jQuery("#<portlet:namespace />carpetaDia").hide();

function imprimirDetalleLiquidacionIntegracion() {
	var periodo=jQuery('#<portlet:namespace />carpetaMesAnio').val();
	window.location.href ='/xlsservlet/?reporte=REPORTE_LIQUIDACION_SUPERINTENDENCIA_INTEGRACION&periodo='+periodo+
	'&rnd=' + Math.floor(Math.random()*100);
	
	jQuery('#<portlet:namespace />buscandoIMPDet').hide();	
}


</script>
