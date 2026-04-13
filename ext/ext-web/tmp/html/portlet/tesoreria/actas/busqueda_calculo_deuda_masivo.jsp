<%@page import="ar.com.ospim.tesoreria.action.CalculoDeudaMasivoAction"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%		
	//verificar los calendars
	Calendar fechaDeuNominaDesde = CalendarFactoryUtil.getCalendar();
	fechaDeuNominaDesde.set(Calendar.DATE, 1);
	fechaDeuNominaDesde.set(Calendar.MONTH, 0);
	fechaDeuNominaDesde.set(Calendar.YEAR, 2007);
	
	Calendar fechaDeuNominaHasta = CalendarFactoryUtil.getCalendar();
	fechaDeuNominaHasta.set(Calendar.DATE, 31);
	fechaDeuNominaHasta.set(Calendar.MONTH, 11);
	fechaDeuNominaHasta.set(Calendar.YEAR, 2011);
	
	Calendar fechaPeriodoDesde = CalendarFactoryUtil.getCalendar();
	fechaPeriodoDesde.add(Calendar.YEAR,-10);
	fechaPeriodoDesde.set(Calendar.DATE,1);
	
	Calendar fechaPeriodoHasta = Calendar.getInstance();
	/* fechaPeriodoHasta.add(Calendar.DATE,-30);
	fechaPeriodoHasta.set(Calendar.DATE, Calendar.getInstance().getActualMaximum(Calendar.DATE)); */
	
	Calendar fechaPeriodoImpago = CalendarFactoryUtil.getCalendar();
	fechaPeriodoImpago.set(Calendar.DATE, 1);
	fechaPeriodoImpago.set(Calendar.MONTH, fechaPeriodoImpago.get(Calendar.MONTH)-5);
	/* fechaPeriodoImpago.set(Calendar.YEAR, fechaPeriodoImpago.get(Calendar.YEAR)); */
	
	Calendar fechaObligacion = CalendarFactoryUtil.getCalendar();
	/*fechaObligacion.set(Calendar.DATE, 1);
	fechaObligacion.set(Calendar.MONTH, fechaObligacion.get(Calendar.MONTH)-5);
	fechaObligacion.set(Calendar.YEAR, fechaObligacion.get(Calendar.YEAR)); */
	
	Calendar current = CalendarFactoryUtil.getCalendar();
%>
 
<liferay-ui:error exception="<%= Exception.class %>" message="error-al-correr-proceso-calc-deuda-masivo" />
<fieldset class="block-labels">
		<legend>
			<p style="font-size: medium ; font-style: italic;">
			<liferay-ui:message key="calculos-deuda-masivo-parametros" />
			</p>
		</legend>
<div id="<portlet:namespace />div_busqueda" style="display: table; vertical-align: top; border-spacing: 2px;">
	<div id="<portlet:namespace />div1" style="display: table-row;">
	
		<div id="F1_C0" style="display: table-cell; border-spacing: 2px; width: 425px;">
		
			<fieldset class="block-labels">
			<legend><liferay-ui:message key="cal-deu-mas-fecha-deuda-nomina" /></legend>
					
			<div id="<portlet:namespace />div1_parametros" style="display: table;">
				<div id="<portlet:namespace />div1_param_f1" style="display: table-row;">
					
					<div id="div1_param_F1_C1" style="display: table-cell;">
						<label><liferay-ui:message key="desde" />:</label>
					</div>
					<div id="div1_param_F1_C2" style="display: table-cell;">
						<liferay-ui:input-date
								dayParam="fechaDeuNomiDesdeDia"
								dayValue="<%= fechaDeuNominaDesde.get(Calendar.DATE) %>" 
								monthParam="fechaDeuNomiDesdeMes"
								monthValue="<%= fechaDeuNominaDesde.get(Calendar.MONTH) %>"		
								yearParam="fechaDeuNomiDesdeAnio"
								yearValue="<%= fechaDeuNominaDesde.get(Calendar.YEAR) %>"
								yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
								yearRangeEnd="<%= fechaDeuNominaDesde.get(Calendar.YEAR) + 10 %>"
								firstDayOfWeek="<%= fechaDeuNominaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
					</div>		
				</div>
				<div id="<portlet:namespace />div1_param_f2" style="display: table-row;">
					<div id="div1_param_F2_C1" style="display: table-cell;">
						<label><liferay-ui:message key="hasta" />:</label>
					</div>
					<div id="div1_param_F2_C2" style="display: table-cell;">
						<liferay-ui:input-date
								dayParam="fechaDeuNomiHastaDia"
								dayValue="<%= fechaDeuNominaHasta.get(Calendar.DATE) %>" 
								monthParam="fechaDeuNomiHastaMes"
								monthValue="<%= fechaDeuNominaHasta.get(Calendar.MONTH) %>"			
								yearParam="fechaDeuNomiHastaAnio"
								yearValue="<%= fechaDeuNominaHasta.get(Calendar.YEAR) %>"
								yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaDeuNominaHasta.get(Calendar.YEAR) + 10 %>"
								firstDayOfWeek="<%= fechaDeuNominaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
					</div>		
				</div>
				<div id="<portlet:namespace />div1_param_f3" style="display: table-row;">
					
					<div id="div1_param_F3_C2" style="display: table-cell;">
						<p><input type="checkbox" name="<portlet:namespace />sin_calc_deuda_nomina" 
							id="<portlet:namespace />sin_calc_deuda_nomina" checked="checked" ></p>
					</div>
					
					<div id="div1_param_F3_C1" style="display: table-cell;">
						<label><liferay-ui:message key="Sin Deuda Nómina Empresa en período seleccionado" /><!-- : --></label>
					</div> <!-- cal-deu-mas-sin-deuda-nomina -->
					
				</div>	
			</div>
			</fieldset>
			
			<fieldset class="block-labels">
			<legend><liferay-ui:message key="cal-deu-mas-periodo-deuda-calc" /></legend>
					
			<div id="<portlet:namespace />div2_parametros" style="display: table;">
				<div id="<portlet:namespace />div2_param_f1" style="display: table-row;">
					
					<div id="div2_param_F1_C1" style="display: table-cell;">
						<label><liferay-ui:message key="desde" />:</label>
					</div>
					<div id="div2_param_F1_C2" style="display: table-cell;">
						<liferay-ui:input-date
								dayParam="fechaPeriodoDesdeDia"
								dayValue="<%= fechaPeriodoDesde.get(Calendar.DATE) %>" 
								monthParam="fechaPeriodoDesdeMes"
								monthValue="<%= fechaPeriodoDesde.get(Calendar.MONTH) %>"		
								yearParam="fechaPeriodoDesdeAnio"
								yearValue="<%= fechaPeriodoDesde.get(Calendar.YEAR) %>"
								yearRangeStart="<%= current.get(Calendar.YEAR) - 20 %>"
								yearRangeEnd="<%= fechaPeriodoDesde.get(Calendar.YEAR) + 10 %>"
								firstDayOfWeek="<%= fechaPeriodoDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
					</div>		
				</div>
				<div id="<portlet:namespace />div2_param_f2" style="display: table-row;">
					<div id="div2_param_F2_C1" style="display: table-cell;">
						<label><liferay-ui:message key="hasta" />:</label>
					</div>
					<div id="div2_param_F2_C2" style="display: table-cell;">
						<liferay-ui:input-date
								dayParam="fechaPeriodoHastaDia"
								dayValue="<%= fechaPeriodoHasta.get(Calendar.DATE) %>" 
								monthParam="fechaPeriodoHastaMes"
								monthValue="<%= fechaPeriodoHasta.get(Calendar.MONTH) %>"			
								yearParam="fechaPeriodoHastaAnio"
								yearValue="<%= fechaPeriodoHasta.get(Calendar.YEAR) %>"
								yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaPeriodoHasta.get(Calendar.YEAR) + 10 %>"
								firstDayOfWeek="<%= fechaPeriodoHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
					</div>		
				</div>
			</div>
			</fieldset>
			
			<fieldset class="block-labels">
			<legend><liferay-ui:message key="cal-deu-mas-periodo-impago-calc" /></legend>
					
			<div id="<portlet:namespace />div3_parametros" style="display: table;">
				<div id="<portlet:namespace />div3_param_f1" style="display: table-row;">
					
					<div id="div3_param_F1_C1" style="display: table-cell;">
						<label><liferay-ui:message key="periodo" />:</label>
					</div>
					<div id="div3_param_F1_C2" style="display: table-cell;">
						<liferay-ui:input-date
								dayParam="fechaImpagoDesdeDia"
								dayValue="<%= fechaPeriodoImpago.get(Calendar.DATE) %>" 
								monthParam="fechaImpagoDesdeMes"
								monthValue="<%= fechaPeriodoImpago.get(Calendar.MONTH) %>"		
								yearParam="fechaImpagoDesdeAnio"
								yearValue="<%= fechaPeriodoImpago.get(Calendar.YEAR) %>"
								yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaPeriodoImpago.get(Calendar.YEAR) + 10 %>"
								firstDayOfWeek="<%= fechaPeriodoImpago.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
					</div>		
				</div>
			</div>
			</fieldset>
			
			<fieldset class="block-labels">
			<legend><liferay-ui:message key="cal-deu-mas-periodo-obligacion-calc" /></legend>
					
			<div id="<portlet:namespace />div4_parametros" style="display: table;">
				<div id="<portlet:namespace />div4_param_f1" style="display: table-row;">
					
					<div id="div4_param_F1_C1" style="display: table-cell;">
						<label><liferay-ui:message key="fecha" />:</label>
					</div>
					<div id="div4_param_F1_C2" style="display: table-cell;">
						<liferay-ui:input-date
								dayParam="fechaObligacionDesdeDia"
								dayValue="<%= fechaObligacion.get(Calendar.DATE) %>" 
								monthParam="fechaObligacionDesdeMes"
								monthValue="<%= fechaObligacion.get(Calendar.MONTH) %>"		
								yearParam="fechaObligacionDesdeAnio"
								yearValue="<%= fechaObligacion.get(Calendar.YEAR) %>"
								yearRangeStart="<%= current.get(Calendar.YEAR) - 10 %>"
								yearRangeEnd="<%= fechaObligacion.get(Calendar.YEAR) + 10 %>"
								firstDayOfWeek="<%= fechaObligacion.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
					</div>		
				</div>
				<div id="<portlet:namespace />div4_param_f1" style="display: table-row;">
					
					<div id="div4_param_F1_C2" style="display: table-cell; vertical-align: bottom;">
						<p><input type="checkbox" name="<portlet:namespace />sin_calc_deuda_nomina" 
							id="<portlet:namespace />extender_30_dias_para_molineras" ></p>
					</div>
					
					<div id="div4_param_F1_C1" style="display: table-cell; width: 200px;">
						<label><liferay-ui:message key="cal-deu-mas-extender-oblig-molineras-calc" /><!-- : --></label>
					</div>
					
				</div>
				
			</div>
			</fieldset>
			<div id="<portlet:namespace />div5_param_f1" style="display: table-row;">
					
				<%-- <div id="div4_param_F1_C1" style="display: table-cell; vertical-align: bottom;">
					<p><input type="button" name="<portlet:namespace />procesar" 
						id="<portlet:namespace />procesar" value="Procesar" 
						onclick="javscript:<portlet:namespace />correrCalculoDeudaMasivo();" ></p>
					<p>
						<label id="<portlet:namespace />mensaje" style="color: red"></label>
					</p>	
				</div> --%>
				<liferay-util:include page='/html/portlet/tesoreria/actas/busqueda_calculo_deuda_masivo_procesar.jsp'>
				</liferay-util:include>
			</div>	
		</div>
		<div id="F1_C1" style="display: table-cell;" >
			<fieldset class="block-labels">
				<legend><liferay-ui:message	key="Procesos de Cálculos de Deuda Masivos" /></legend>
				
				<liferay-util:include page='/html/portlet/tesoreria/actas/resultados_calculo_deuda_masivo.jsp'>
				</liferay-util:include>
				
			</fieldset>
		</div>
	</div>
</div>

</fieldset>
				

<script type="text/javascript">
	
	<portlet:namespace />verificaEstadoReporte(); 		

	function <portlet:namespace />correrCalculoDeudaMasivo(){	
		
		var diaDeuNomDesde=jQuery('#<portlet:namespace />fechaDeuNomiDesdeDia').val();	    
	    var mesDeuNomDesde=parseInt(jQuery('#<portlet:namespace />fechaDeuNomiDesdeMes').val())+1;	    
	    var anioDeuNomDesde=jQuery('#<portlet:namespace />fechaDeuNomiDesdeAnio').val();
/* 	    var fechaDeuNomDesdeFinal = diaDeuNomDesde+'/'+mesDeuNomDesde+'/'+anioDeuNomDesde; */

		var diaDeuNomHasta=jQuery('#<portlet:namespace />fechaDeuNomiHastaDia').val();	    
	    var mesDeuNomHasta=parseInt(jQuery('#<portlet:namespace />fechaDeuNomiHastaMes').val())+1;	    
	    var anioDeuNomHasta=jQuery('#<portlet:namespace />fechaDeuNomiHastaAnio').val();
/* 	    var fechaDeuNomHastaFinal = diaDeuNomHasta+'/'+mesDeuNomHasta+'/'+anioDeuNomHasta; */
	    
	    var sinCalDeudaNominaCheck = jQuery("#<portlet:namespace />sin_calc_deuda_nomina").is(':checked');
	    
		var diaDeuPeriDesde=jQuery('#<portlet:namespace />fechaPeriodoDesdeDia').val();	    
	    var mesDeuPeriDesde=parseInt(jQuery('#<portlet:namespace />fechaPeriodoDesdeMes').val())+1;	    
	    var anioDeuPeriDesde=jQuery('#<portlet:namespace />fechaPeriodoDesdeAnio').val();
/* 	    var fechaDeuPeriDesdeFinal = diaDeuPeriDesde+'/'+mesDeuPeriDesde+'/'+anioDeuPeriDesde; */

		var diaDeuPeriHasta=jQuery('#<portlet:namespace />fechaPeriodoHastaDia').val();	    
	    var mesDeuPeriHasta=parseInt(jQuery('#<portlet:namespace />fechaPeriodoHastaMes').val())+1;	    
	    var anioDeuPeriHasta=jQuery('#<portlet:namespace />fechaPeriodoHastaAnio').val();
/* 	    var fechaDeuPeriHastaFinal = diaDeuPeriHasta+'/'+mesDeuPeriHasta+'/'+anioDeuPeriHasta; */
	    
	    var diaImpago=jQuery('#<portlet:namespace />fechaImpagoDesdeDia').val();	    
	    var mesImpago=parseInt(jQuery('#<portlet:namespace />fechaImpagoDesdeMes').val())+1;	    
	    var anioImpago=jQuery('#<portlet:namespace />fechaImpagoDesdeAnio').val();
/* 	    var fechaImpagoFinal = diaImpago+'/'+mesImpago+'/'+anioImpago; */
	   
	    var diaObligacion=jQuery('#<portlet:namespace />fechaObligacionDesdeDia').val();	    
	    var mesObligacion=parseInt(jQuery('#<portlet:namespace />fechaObligacionDesdeMes').val())+1;	    
	    var anioObligacion=jQuery('#<portlet:namespace />fechaObligacionDesdeAnio').val();
/* 	    var fechaObligacionFinal = diaObligacion+'/'+mesObligacion+'/'+anioObligacion; */
	    	    
	    var extender30diasMoliCheck = jQuery("#<portlet:namespace />extender_30_dias_para_molineras").is(':checked');
	    
	    
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/calculo_deuda_masivo';

		var params = {
					  fechaDeuNomDiaDesde : diaDeuNomDesde,
					  fechaDeuNomMesDesde : mesDeuNomDesde,
					  fechaDeuNomAnioDesde : anioDeuNomDesde,
					  fechaDeuNomDiaHasta : diaDeuNomHasta,
					  fechaDeuNomMesHasta : mesDeuNomHasta,
					  fechaDeuNomAnioHasta : anioDeuNomHasta,

					  fechaDeuPeriDiaDesde : diaDeuPeriDesde,
					  fechaDeuPeriMesDesde : mesDeuPeriDesde,
					  fechaDeuPeriAnioDesde : anioDeuPeriDesde,
					  fechaDeuPeriDiaHasta : diaDeuPeriHasta,
					  fechaDeuPeriMesHasta : mesDeuPeriHasta,
					  fechaDeuPeriAnioHasta : anioDeuPeriHasta,
					  
					  fechaDiaImpago : diaImpago,
					  fechaMesImpago : mesImpago,
					  fechaAnioImpago : anioImpago,
					  
					  fechaDiaObligacion : diaObligacion,
					  fechaMesObligacion : mesObligacion,
					  fechaAnioObligacion : anioObligacion,
					  
/* 					  fechaDeuNomDesde : fechaDeuNomDesdeFinal, 
					  fechaDeuNomHasta : fechaDeuNomHastaFinal, */
					  sinCalDeudaNomina : sinCalDeudaNominaCheck,
/* 					  fechaDeuPeriDesde : fechaDeuPeriDesdeFinal,
					  fechaDeuPeriHasta : fechaDeuPeriHastaFinal, */
/* 					  fechaImpago : fechaImpagoFinal,
					  fechaObligacion : fechaObligacionFinal, */
					  extender30diasMoli : extender30diasMoliCheck
					  };
		
		var d = new Date();
		
		document.getElementById("<portlet:namespace />procesar").style.visibility = "hidden";
		document.getElementById("<portlet:namespace />mensaje").innerHTML = "Lanzado el " + d.getDate()+ "/"+(d.getMonth()+1)+"/"+d.getFullYear() + " "+d.getHours()+":"+d.getMinutes();
		
		jQuery('#<portlet:namespace/>div4_param_F1_C1').load(url, params, function(){ });

      
	}
	
	function cancelarProceso(procid){			
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/calculo_deuda_masivo&cancela='+procid;
		jQuery('#<portlet:namespace/>div4_param_F1_C1').load(url, function(){});
	  
	}

	function <portlet:namespace />verificaEstadoReporte() {
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/calc_deuda_masivo_verifica';
		url+= '&reporte=<%=CalculoDeudaMasivoAction.reporte_system_config%>';
		jQuery.ajax({   
					url: url,
					async: false,
					success: function(data){
						var obj = jQuery.parseJSON(data);
						
						if("r"==obj.status){
							document.getElementById("<portlet:namespace />procesar").style.visibility = "hidden";
							document.getElementById("<portlet:namespace />mensaje").innerHTML = "Lanzado el " + obj.descripcion;
							
						}else{
							document.getElementById("<portlet:namespace />procesar").style.visibility = "visible";
							document.getElementById("<portlet:namespace />mensaje").innerHTML="";
						}
					}
		});	
	}
	
	function exportarExcel(id_proceso){		
		window.location.href ='/xlsservlet/?reporte=REPORTE_RESUMEN_CALC_DEUDA_MASIVO&idProceso='+id_proceso;										
	}
</script>
