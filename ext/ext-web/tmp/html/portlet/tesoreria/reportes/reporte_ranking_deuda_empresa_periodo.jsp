<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%		
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
%>
<div id="<portlet:namespace/>div_ranking_deuda_empresa" >
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-ranking-deuda-empresa-periodo" /></legend>
				<table class="lfr-table">			
					<tr>
						<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
								monthParam="fechaDesdeMes"
								monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
								yearParam="fechaDesdeAnio"
								yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaHastaDia"
								dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
								monthParam="fechaHastaMes"
								monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
								yearParam="fechaHastaAnio"
								yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 25 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td>
							<input id="<portlet:namespace />generar" value="<liferay-ui:message key="generar"/>" title="<liferay-ui:message key="generar" />" type="button" onClick="javascript:<portlet:namespace />rankingDeudaEmpresa();"/>
						</td>
						
						<td>
							<input id="<portlet:namespace />excel" value="<liferay-ui:message key="excel"/>" title="<liferay-ui:message key="excel" />" type="button" onClick="javascript:<portlet:namespace />rankingDeudaEmpresaExcel();"/>
						</td>
						<td colspan="5"><label id="<portlet:namespace />mensaje" style="color: red"></label></td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
				</table>	      	  
		</fieldset>
</div>
		
<script type="text/javascript">
<portlet:namespace />verificaEstadoReporte();

function <portlet:namespace />rankingDeudaEmpresa(){	
	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
	
	var fechaHastaDia=jQuery('#<portlet:namespace />fechaHastaDia').val();
	var fechaHastaMes=jQuery('#<portlet:namespace />fechaHastaMes').val();
	var fechaHastaAnio=jQuery('#<portlet:namespace />fechaHastaAnio').val();
	var confirmar = false;
	confirmar=confirm ('Se eliminará el ranking anterior '+'\nDesea generarlo?');
	if(confirmar){
	  var d = new Date(); 
	  var dd=d.getDate() + "/" + (d.getMonth() +1) + "/" + d.getFullYear()+ ', '+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds(); 
	  document.getElementById("<portlet:namespace />generar").style.visibility = "hidden";
	  document.getElementById("<portlet:namespace />excel").style.visibility = "hidden";
	  document.getElementById("<portlet:namespace />mensaje").innerHTML = "Lanzado el " + dd;	  
		
	  var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/ranking_deuda_empresa_periodo_generacion';
	      url += '&fechaDesdeDia='+fechaDesdeDia+'&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio+
	             '&fechaHastaDia='+fechaHastaDia+'&fechaHastaMes='+fechaHastaMes+'&fechaHastaAnio='+fechaHastaAnio;
	
	     jQuery('#<portlet:namespace/>div_ranking_deuda_empresa').load(url, function(){});
    }	     
  
}
	
function <portlet:namespace />rankingDeudaEmpresaExcel(){
	var url = '/xlsservlet/?reporte=REPORTE_RANKING_DEUDA_EMPRESAS' 
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
}


function <portlet:namespace />verificaEstadoReporte() {
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/ranking_deuda_empresa_periodo_verifica';
	jQuery.ajax({   
		url: url,
		async: false,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			
			if("r"==obj.status){
				document.getElementById("<portlet:namespace />generar").style.visibility = "hidden";
				document.getElementById("<portlet:namespace />excel").style.visibility = "hidden";
				document.getElementById("<portlet:namespace />mensaje").innerHTML = "Lanzado el " + obj.descripcion;
				
			}else{
				document.getElementById("<portlet:namespace />generar").style.visibility = "visible";
				document.getElementById("<portlet:namespace />excel").style.visibility = "visible";
				document.getElementById("<portlet:namespace />mensaje").innerHTML="";
			}
		}
	});	
}

</script>