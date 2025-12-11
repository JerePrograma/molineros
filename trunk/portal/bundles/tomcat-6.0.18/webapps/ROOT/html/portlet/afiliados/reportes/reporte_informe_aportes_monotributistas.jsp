<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%		
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 		
%>
<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">  
   <div id="<portlet:namespace/>div_aportes_monotributistas" >
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="reporte-informe-aportes-monotributistas" /></legend>
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
						<td>
							<input id="<portlet:namespace />generar" value="<liferay-ui:message key="generar"/>" title="<liferay-ui:message key="generar" />" type="button" onClick="javascript:<portlet:namespace />controlAportesMonotributistas();"/>
						</td>
						
						<td>
							<input id="<portlet:namespace />excel" value="<liferay-ui:message key="excel"/>" title="<liferay-ui:message key="excel" />" type="button" onClick="javascript:<portlet:namespace />informeAportesMonotributistasExcel();"/>
						</td>
						<td colspan="5"><label id="<portlet:namespace />mensaje" style="color: red"></label></td>
						
						<td colspan="5">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						
						<td align="right">
							<input id="<portlet:namespace />tablas" value="Cargar/Modificar Valores Categorías" title="Tablas de Categorías" type="button" onClick="javascript:<portlet:namespace />cargarTablasMonotributo();"/>
						</td>
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
				</table>	      	  
		</fieldset>	
	</div>
</form>			
<script type="text/javascript">
<portlet:namespace />verificaEstadoReporteInformeAportesMonotributistas();
jQuery('#<portlet:namespace />fechaDesdeDia').hide();

function <portlet:namespace />controlAportesMonotributistas(){	
	var fechaDesdeDia=jQuery('#<portlet:namespace />fechaDesdeDia').val();
	var fechaDesdeMes=jQuery('#<portlet:namespace />fechaDesdeMes').val();
	var fechaDesdeAnio=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
	var confirmar = false;
	confirmar=confirm ('Se eliminará el informe anterior '+'\nDesea generarlo?');
	if(confirmar){
	  var d = new Date(); 
	  var dd=d.getDate() + "/" + (d.getMonth() +1) + "/" + d.getFullYear()+ ', '+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds(); 
	  document.getElementById("<portlet:namespace />generar").style.visibility = "hidden";
	  document.getElementById("<portlet:namespace />excel").style.visibility = "hidden";
	  document.getElementById("<portlet:namespace />mensaje").innerHTML = "Lanzado el " + dd;	  
		
	  var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/informe_aportes_monotributistas_generacion';
	      url += '&fechaDesdeDia=01&fechaDesdeMes='+fechaDesdeMes+'&fechaDesdeAnio='+fechaDesdeAnio;
	     jQuery('#<portlet:namespace/>div_aportes_monotributistas').load(url, function(){});
    }	     
  
}
	
function <portlet:namespace />informeAportesMonotributistasExcel(){
	var url = '/xlsservlet/?reporte=REPORTE_INFORME_APORTES_MONOTRIBUTISTAS' 
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
}


function <portlet:namespace />verificaEstadoReporteInformeAportesMonotributistas() {
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/informe_aportes_monotributistas_verifica';
	url+= '&reporte=reporte.informe_aportes_monotributistas';
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

function <portlet:namespace />cargarTablasMonotributo() {
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/abm_categorias_monotributo" /></portlet:renderURL>';
	url = url;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);		
}

</script>