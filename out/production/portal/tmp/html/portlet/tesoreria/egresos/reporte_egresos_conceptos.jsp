
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.text.SimpleDateFormat" %>

<portlet:defineObjects />

<%		
		String portlet_name = ParamUtil.getString(request, "portlet_name");
	
		if (portlet_name == null || portlet_name.trim().equals("")){
			portlet_name = "tesoreria";
		}
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			portlet_name = "farmacia";
		} 
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			portlet_name = "uoma";
		}
		SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
		List<Concepto> conceptos = (List<Concepto>) request.getAttribute("ConceptosEgresoTotales");
		Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
		fechaDesde.setTime(new Date());
 		Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaHasta.setTime(new Date()); 
 		Calendar fechaPagoHasta = CalendarFactoryUtil.getCalendar(); 		
 		fechaPagoHasta.setTime(new Date()); 
%>
<fieldset class="block-labels">
	<legend>
		<liferay-ui:message key="egreso-por-concepto" />
	</legend>
	<table class="lfr-table">
		<tr>
			<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
					dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
					monthParam="fechaDesdeMes"
					monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
					yearParam="fechaDesdeAnio"
					yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
					yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)  %>"
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
			<td colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td><label><liferay-ui:message key="conceptos" />:</label></td>
			<td colspan="3"><select id="<portlet:namespace/>concepto"
				name="<portlet:namespace/>concepto" multiple="multiple" size="20">
					<optgroup label="Conceptos">
						<% for (Concepto cpt : conceptos) {
							 if(portlet_name.equals("uoma")){%>
								<option value="<%= cpt.getId()+"|"+cpt.getIdSeccional()%>"><%=cpt.getDescripcion() + " " + format.format(cpt.getValidoDesde()) + "-"+ format.format(cpt.getValidoHasta())%></option>
						<%	 }else{%>
								<option value="<%= cpt.getId()%>"><%=cpt.getDescripcion() + " " + format.format(cpt.getValidoDesde()) + "-"+ format.format(cpt.getValidoHasta())%></option>							 
						<%	 }
						   } %>
					</optgroup>
			</select> <input id="<portlet:namespace />borrar"
				value="<liferay-ui:message key="limpiar-filtro"/>"
				title="<liferay-ui:message key="limpiar-filtro" />" type="button" />
				<label><liferay-ui:message key="ninguna-seleccion-todos" /></label>
			</td>
		</tr>
		<tr>
			<td colspan="4">&nbsp;</td>
		</tr>
		<%if(!portlet_name.equals("tesoreria")){ %>
			<tr>
				<td colspan="4">
					Incluir Mov. Bcrios.&nbsp;<input type="checkbox" name="<portlet:namespace />incluir_movimientosbancarios" id="<portlet:namespace />incluir_movimientosbancarios"/>&nbsp;
				</td>
			</tr>
			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>			
			<tr>						
				<td colspan="4">
					<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
				  		<liferay-util:param name="esEditable" value='true'/>
				  		<%if(portlet_name.equals("uoma")){%>
				  			<liferay-util:param name="soloOP" value='false'/>
				  		<%}else{%>
				  			<liferay-util:param name="soloOP" value='true'/>
				  		<%}%>
						</liferay-util:include>
				</td>
			</tr>
		<%}%>
		<tr>
			<td colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3"><input id="<portlet:namespace />buscar"
				value="<liferay-ui:message key="buscar"/>"
				title="<liferay-ui:message key="buscar" />" type="button"
				onClick="javascript:<portlet:namespace />buscarMovimientos();" /></td>
			<td>&nbsp;</td>
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
	<div align="center" id="<portlet:namespace />busquedaMovimientoDiv">
	</div>
</fieldset>

<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();	
function <portlet:namespace />buscarMovimientos(){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	
	<%if(!portlet_name.equals("tesoreria")){ %>		
		var cuit_entidad=jQuery("#<portlet:namespace />cuit_entidad").val();
		var sucur_entidad=jQuery("#<portlet:namespace />sucursal_entidad").val();
		var id_seccional=jQuery("#<portlet:namespace />id_seccional").val();
		var id_incluir_mov_bcrios = document.getElementById("<portlet:namespace />incluir_movimientosbancarios");		
    <%}%>		
	
	var concepto=jQuery("#<portlet:namespace/>concepto").val();
	var url = '/xlsservlet/?reporte=EGRESO_POR_CONCEPTOS'
		+ '&conceptos='+encodeURI(concepto)
		+ '&fechaDesdeDia=' +desde_dia
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio
		+ '&fechaHastaDia=' +hasta_dia
		+ '&fechaHastaMes=' +hasta_mes
		+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'		
		+ '&fechaHastaAnio=' +hasta_anio;
	<%if(!portlet_name.equals("tesoreria")){ %>
		url=url+ '&cuit='+cuit_entidad
			+ '&sucursal='+sucur_entidad
			+ '&id_seccional='+id_seccional
			+ '&incluir_mov_bcrios='+id_incluir_mov_bcrios.checked;
	<%}%>
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location.href =url;
}

function actualizarConceptos(){
	var desde_dia=jQuery("#<portlet:namespace/>fechaDesdeDia").val();	
	var desde_mes=jQuery("#<portlet:namespace/>fechaDesdeMes").val();
	var desde_anio=jQuery("#<portlet:namespace/>fechaDesdeAnio").val();
	var hasta_dia=jQuery("#<portlet:namespace/>fechaHastaDia").val();	
	var hasta_mes=jQuery("#<portlet:namespace/>fechaHastaMes").val();
	var hasta_anio=jQuery("#<portlet:namespace/>fechaHastaAnio").val();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_conceptos_egreso_completo_para_periodo'
	    + '&fechaDesdeDia=' +desde_dia
		+ '&fechaDesdeMes=' +desde_mes
		+ '&fechaDesdeAnio=' +desde_anio
		+ '&fechaHastaDia=' +hasta_dia
		+ '&fechaHastaMes=' +hasta_mes
		+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'	
		+ '&fechaHastaAnio=' +hasta_anio;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			jQuery('#<portlet:namespace/>concepto').find('option').remove();
			for(var i =0;i< obj.conceptos.length; i++){
				var secc =obj.conceptos[i].id_seccional.split(" ");
				jQuery('#<portlet:namespace/>concepto').append('<option value="'+obj.conceptos[i].id+ "|"+secc[0] +'">'+obj.conceptos[i].descripcion+'</option>');
			}                                                                                                                                                                                                                                                            
		}
	});		
}



jQuery(document).ready(function() {
	jQuery('#<portlet:namespace/>fechaDesdeMes').change(function(){
		actualizarConceptos();
	});
	jQuery('#<portlet:namespace/>fechaDesdeAnio').change(function(){
		actualizarConceptos();
	});
	jQuery('#<portlet:namespace/>fechaPagoHastaMes').change(function(){
		actualizarConceptos();
	});
	jQuery('#<portlet:namespace/>fechaPagoHastaAnio').change(function(){
		actualizarConceptos();
	});
});

</script>