<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
	fechaDesde.setTime(new Date());
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date()); 		
	Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
	periodoDesde.setTime(new Date());
	Calendar periodoHasta = CalendarFactoryUtil.getCalendar(); 		
	periodoHasta.setTime(new Date());
	
	List<TipoBono> tiposBonos = TraeListasServiceUtil.getTiposDeBonos();
%>
<fieldset class="block-labels">
		<legend><liferay-ui:message key="busqueda-bonos" /></legend>			
				<table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="tipo-bono" />:</label></td>
						<td>
							<select name="<portlet:namespace/>tipo_bono" id="<portlet:namespace/>tipo_bono">
								<option value="TODOS" selected>TODOS</option>
									<%
										for (TipoBono tb : tiposBonos) {
									%>
										<option value="<%=tb.getTipo_bono()+"-"+tb.getTipo_bono_string().trim() %>">
											<%=tb.getTipo_bono()+"-"+tb.getTipo_bono_string().trim()%>											
										</option>										
									<%
									}
									%>
							</select>
						</td>
						<td><label><liferay-ui:message key="seccional" />:</label></td>		
						<td colspan="2" rowspan="2" style="vertical-align:top" ><jsp:include page='/html/portlet/afiliados/busqueda_seccional.jsp' /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="fecha-desde" /> envío:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaDesdeDia"
								dayValue="1"
								dayNullable="<%= true %>" 
								monthParam="fechaDesdeMes"
								monthValue="0"
								monthNullable="<%= true %>"				
								yearParam="fechaDesdeAnio"
								yearValue="2011"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 2 %>"
								yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
								firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
						<td><label><liferay-ui:message key="fecha-hasta" /> envío:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="fechaHastaDia"																					
								dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
								dayNullable="<%= true %>"
								monthParam="fechaHastaMes"
								monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
								monthNullable="<%= true %>"
								yearParam="fechaHastaAnio"
								yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
								yearNullable="<%= true %>"
								yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
								yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
								firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>
					</tr>
					<tr>
						<td colspan="10">&nbsp;</td>
					</tr>						
					<tr>
						<td><label><liferay-ui:message key="bono-desde" />: </label></td>
						<td><input id="<portlet:namespace />bono_desde" name="<portlet:namespace />bono_desde" size="8" maxlength="8" type="text" value="" /></td>
						<td><label><liferay-ui:message key="bono-hasta" />:</label></td>
						<td colspan="2"><input id="<portlet:namespace />bono_hasta" name="<portlet:namespace />bono_hasta" size="8" maxlength="8" type="text" value="" /></td>												
						<td ><label><liferay-ui:message key="sin-rendir" />:</label>&nbsp;<input type="checkbox" id="<portlet:namespace />sin_rendir" name="<portlet:namespace />sin_rendir" value="false" checked/>
						<td ><label><liferay-ui:message key="anulados" />:</label>&nbsp;<input type="checkbox" id="<portlet:namespace />anulados" name="<portlet:namespace />anulados" value="false" checked/>
						
						<td><label><liferay-ui:message key="rendidos" />:</label>&nbsp;<input type="checkbox" id="<portlet:namespace />rendidos" name="<portlet:namespace />rendidos" value="false" checked/></td>
						<td colspan="3"><label><liferay-ui:message key="sin-enviar" />:</label>&nbsp;<input type="checkbox" id="<portlet:namespace />sin_enviar" name="<portlet:namespace />sin_enviar" value="false"/></td>
						</td>						
					</tr>									
				</table>				
					<div align="center" id="<portlet:namespace />buscando">
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

			<div align="right">
				<input id="<portlet:namespace />search" value="<liferay-ui:message key="search"/>" title="<liferay-ui:message key="search" />" type="button" onClick="javascript:busquedaEnvioBonos();"/>
				<input id="<portlet:namespace />nueva-carga" value="<liferay-ui:message key="nueva-carga"/>" title="<liferay-ui:message key="nueva-carga" />" type="button" onClick="javascript:nuevaCargaBonos();"/>
				<input id="<portlet:namespace />nuevo-envio" value="<liferay-ui:message key="nuevo-envio"/>" title="<liferay-ui:message key="nuevo-envio" />" type="button" onClick="javascript:nuevoEnvioBonos();"/>
				<!-- <input id="<portlet:namespace />nueva-rendicion" value="<liferay-ui:message key="nueva-rendicion"/>" title="<liferay-ui:message key="nueva-rendicion" />" type="button" onClick="javascript:rendirBonos();"/>-->		
				<input id="<portlet:namespace />bonos-seccional" value="<liferay-ui:message key="bonos-seccional"/>" title="<liferay-ui:message key="bonos-seccional" />" type="button" onClick="javascript:reporteBonosSeccional();"/>
				<input id="<portlet:namespace />bonos-vent" value="<liferay-ui:message key="bonos-vent"/>" title="<liferay-ui:message key="bonos-vent" />" type="button" onClick="javascript:reporteBonosSeccionalVent();"/>
				<input id="<portlet:namespace />bonos-gsal" value="<liferay-ui:message key="bonos-gsal"/>" title="<liferay-ui:message key="bonos-gsal" />" type="button" onClick="javascript:reporteBonosSeccionalGsal();"/>
				<input type="hidden" id="<portlet:namespace />tipo_bono_hidden" name="<portlet:namespace />tipo_bono_hidden"/>					
				<input type="hidden" id="<portlet:namespace />bono_hasta_hidden" name="<portlet:namespace />bono_hasta_hidden"/>
				<input type="hidden" id="<portlet:namespace />bono_desde_hidden" name="<portlet:namespace />bono_desde_hidden"/>
				<input type="hidden" id="<portlet:namespace />fecha_envio_hidden" name="<portlet:namespace />fecha_envio_hidden"/>
			</div>
<div align="center" id="<portlet:namespace />busquedaAfiliadoDiv">						
</div>
<div align="right" id="<portlet:namespace />exportarBusquedaBonos">
		<input id="<portlet:namespace />exportar-busqueda" value="<liferay-ui:message key="exportar-busqueda"/>" title="<liferay-ui:message key="exportar-busqueda" />" type="button" onClick="javascript:exportarBusquedaBonos();"/>
</div>	
	
<script type="text/javascript">
	var popupAfill;
	jQuery('#<portlet:namespace />exportarBusquedaBonos').hide();
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery('#<portlet:namespace />seleccionarAfiliadoDiv').hide();
	function busquedaEnvioBonos(){		
		jQuery('#<portlet:namespace />buscando').show();
		jQuery('#<portlet:namespace />exportarBusquedaBonos').hide();
		var tipo_bono=jQuery('#<portlet:namespace />tipo_bono').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional').val();
		var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
		var desde_mes=parseInt(jQuery("#<portlet:namespace/>fechaDesdeMes").val())+1;
		var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
		var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
		var hasta_mes=parseInt(jQuery("#<portlet:namespace/>fechaHastaMes").val())+1;
		var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
		var bono_desde=	jQuery("#<portlet:namespace/>bono_desde").val();
		var bono_hasta=	jQuery("#<portlet:namespace/>bono_hasta").val();
		var sin_rendir=	jQuery("#<portlet:namespace/>sin_rendir").is(':checked');				
		var sin_enviar=	jQuery("#<portlet:namespace/>sin_enviar").is(':checked');
		var anulados=	jQuery("#<portlet:namespace/>anulados").is(':checked');
		var rendidos=	jQuery("#<portlet:namespace/>rendidos").is(':checked');						
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/busqueda_envio_bonos&tipo_bono='+escape(tipo_bono)+'&id_seccional='+seccional+
		'&fecha_hasta='+hasta_dia+'/'+hasta_mes+'/'+hasta_anio+'&fecha_desde='+desde_dia+'/'+desde_mes+'/'+desde_anio+'&bono_desde='+bono_desde+'&bono_hasta='+bono_hasta+'&sin_rendir='+sin_rendir+'&rendidos='+rendidos+'&sin_enviar='+sin_enviar+'&anulados='+anulados;		
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url, function (){jQuery('#<portlet:namespace />buscando').hide(); jQuery('#<portlet:namespace />exportarBusquedaBonos').show();});
	}	
	
	function nuevoEnvioBonos(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_envio_bonos" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

	function nuevaCargaBonos(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_envio_bonos" /><portlet:param name="accion" value="cargar" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

<%-- 	function rendirBonos(){		
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_envio_bonos" /> <portlet:param name="accion" value="rendir" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	} --%>
	
	function anularBonos(){		
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_envio_bonos" /> <portlet:param name="accion" value="anular" /></portlet:renderURL>';
		document.<portlet:namespace />fm.method10 = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	function anularBonos(tipoBono, idSeccional, seccional,fecha_envio,fecha_anulacion, bonoDesde, bonoHasta){
		jQuery("#<portlet:namespace/>tipo_bono_hidden").val(tipoBono);
		jQuery("#<portlet:namespace/>id_seccional").val(idSeccional);
		jQuery("#<portlet:namespace/>seccional").val(seccional);
		jQuery("#<portlet:namespace/>fecha_envio_hidden").val(fecha_envio);
		jQuery("#<portlet:namespace/>fecha_anulacion_hidden").val(fecha_anulacion);
		jQuery("#<portlet:namespace/>bono_desde_hidden").val(bonoDesde);
		jQuery("#<portlet:namespace/>bono_hasta_hidden").val(bonoHasta);		
		
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">'
				  '<portlet:param name="struts_action" value="/afiliados/editar_envio_bonos" />'+ 
				  '<portlet:param name="accion" value="anular" />'+			  				   
				  '</portlet:renderURL>';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}		

	
	function rendirBonos(tipoBono, idSeccional, seccional,fecha_envio, bonoDesde, bonoHasta){
		jQuery("#<portlet:namespace/>tipo_bono_hidden").val(tipoBono);
		jQuery("#<portlet:namespace/>id_seccional").val(idSeccional);
		jQuery("#<portlet:namespace/>seccional").val(seccional);
		jQuery("#<portlet:namespace/>fecha_envio_hidden").val(fecha_envio);		
		jQuery("#<portlet:namespace/>bono_desde_hidden").val(bonoDesde);
		jQuery("#<portlet:namespace/>bono_hasta_hidden").val(bonoHasta);
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">'
				  '<portlet:param name="struts_action" value="/afiliados/editar_envio_bonos" />'+ 
				  '<portlet:param name="accion" value="rendir" />'+			  				   
				  '</portlet:renderURL>';		
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
	
	
	function liberaBonos(id_envio){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/enviar_bonos&id_envio='+id_envio+'&accion=liberar';		
		jQuery('#<portlet:namespace />busquedaAfiliadoDiv').load(url);
	}
	
	function exportarExcel(tipoBono, idSeccional, seccional,fechaEnvio, bonoDesde, bonoHasta){		
		window.location.href ='/xlsservlet/?reporte=REPORTE_BONOS_SECCIONAL'
			+'&tipoBono='+tipoBono
			+'&id_seccional='+idSeccional
			+'&seccional='+seccional
			+'&bono_desde='+bonoDesde
			+'&bono_hasta='+bonoHasta;			
	}

	function reporteBonosSeccional() {		
		jQuery('#<portlet:namespace />buscando').show();		
		window.location.href ='/xlsservlet/?reporte=REPORTE_CANT_BONOS_SECCIONAL';
		jQuery('#<portlet:namespace />buscando').hide();
	}
	
	function reporteBonosSeccionalVent() {	
		
		var tipobono1=jQuery('#<portlet:namespace />tipo_bono').val();
		var tBono =tipobono1.split('-')[0];				
		var seccional1=jQuery('#<portlet:namespace />id_seccional').val();		
		jQuery('#<portlet:namespace />buscando').show();		
		tBono=100;
		seccional1=97;
		window.location.href ='/xlsservlet/?reporte=REPORTE_CANT_BONOS_SECCIONAL_VENT&tip_bono='+tBono+'&id_seccional='+seccional1				
				
		jQuery('#<portlet:namespace />buscando').hide();
	}	
	
	function reporteBonosSeccionalGsal() {	
		
		var tipobono1=jQuery('#<portlet:namespace />tipo_bono').val();
		var tBono =tipobono1.split('-')[0];				
		var seccional1=jQuery('#<portlet:namespace />id_seccional').val();		
		jQuery('#<portlet:namespace />buscando').show();		
		tBono=100;
		seccional1=95;
		window.location.href ='/xlsservlet/?reporte=REPORTE_CANT_BONOS_SECCIONAL_VENT&tip_bono='+tBono+'&id_seccional='+seccional1				
				
		jQuery('#<portlet:namespace />buscando').hide();
	}	

	function exportarBusquedaBonos() {
		jQuery('#<portlet:namespace />buscando').show();		
		window.location.href ='/xlsservlet/?reporte=REPORTE_RESULT_BUSQUEDA_BONOS_SECCIONAL';
		jQuery('#<portlet:namespace />buscando').hide();	
	}
	
</script>
