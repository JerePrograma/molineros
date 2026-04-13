<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Concepto"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>	
<portlet:defineObjects/>

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
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
%>
		<fieldset class="block-labels">
				<legend>Diario</legend>
				<table class="lfr-table">
					<tr>
						<td>Ejercicio:&nbsp; <select name="ejercicio"  id="ejercicio">
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						if(portlet_name.equals("farmacia")){
							if (cal.get(Calendar.MONTH) < Calendar.JULY){
								hastaAnio--;
							}
						}else{
							if (cal.get(Calendar.MONTH) < Calendar.AUGUST){
								hastaAnio--;
							}
						}
						for (int i = 2000; i<=hastaAnio; i++){  %>
					<option value="<%=i%>-<%=i+1%>" <%if (i == hastaAnio) { %>
						selected="selected" <%} %>>
						<% if(portlet_name.equals("farmacia")){%>
							Julio&nbsp;<%=i %>&nbsp;-&nbsp;Junio&nbsp;<%= i+1 %></option>
						<%}else{%>						
							Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
						<%}%>
					<%} %>
			</select>
						&nbsp;Periodo: desde&nbsp; <% if(portlet_name.equals("farmacia")){%>
					<select name="periodo_desde" id="periodo_desde">
					<option value="-1"></option>
					<option value="07">Julio</option>
					<option value="08">Agosto</option>
					<option value="09">Septiembre</option>
					<option value="10">Octubre</option>
					<option value="11">Noviembre</option>
					<option value="12">Diciembre</option>
					<option value="01">Enero</option>
					<option value="02">Febrero</option>
					<option value="03">Marzo</option>
					<option value="04">Abril</option>
					<option value="05">Mayo</option>
					<option value="06">Junio</option>					
					</select>
				<%}else{%>
				<select name="periodo_desde" id="periodo_desde">
					<option value="-1"></option>
					<option value="08">Agosto</option>
					<option value="09">Septiembre</option>
					<option value="10">Octubre</option>
					<option value="11">Noviembre</option>
					<option value="12">Diciembre</option>
					<option value="01">Enero</option>
					<option value="02">Febrero</option>
					<option value="03">Marzo</option>
					<option value="04">Abril</option>
					<option value="05">Mayo</option>
					<option value="06">Junio</option>
					<option value="07">Julio</option>
				</select><%}%>&nbsp; - hasta:&nbsp;
				<% if(portlet_name.equals("farmacia")){%>
					<select name="periodo_hasta" id="periodo_hasta">
					<option value="-1"></option>
					<option value="07">Julio</option>
					<option value="08">Agosto</option>
					<option value="09">Septiembre</option>
					<option value="10">Octubre</option>
					<option value="11">Noviembre</option>
					<option value="12">Diciembre</option>
					<option value="01">Enero</option>
					<option value="02">Febrero</option>
					<option value="03">Marzo</option>
					<option value="04">Abril</option>
					<option value="05">Mayo</option>
					<option value="06">Junio</option>					
					</select>
				<%}else{%>
				<select name="periodo_hasta" id="periodo_hasta">
					<option value="-1"></option>
					<option value="08">Agosto</option>
					<option value="09">Septiembre</option>
					<option value="10">Octubre</option>
					<option value="11">Noviembre</option>
					<option value="12">Diciembre</option>
					<option value="01">Enero</option>
					<option value="02">Febrero</option>
					<option value="03">Marzo</option>
					<option value="04">Abril</option>
					<option value="05">Mayo</option>
					<option value="06">Junio</option>
					<option value="07">Julio</option>
				</select><%}%>
						</td>
					</tr>
					<tr>
						<td >
							Asientos desde:&nbsp;<input type="input" id="nro_asiento_desde" name="nro_asiento_desde" size="5"/>&nbsp;-&nbsp;
							hasta:&nbsp;<input type="input" id="nro_asiento_hasta" name="nro_asiento_hasta" size="5"/>&nbsp;
							Incluir Automáticos&nbsp;<input type="checkbox"  checked="checked" id="incluir_automaticos" name="incluir_automaticos" value="true"/>&nbsp;
							Incluir Manuales&nbsp;<input type="checkbox" checked="checked" id="incluir_manuales" name="incluir_manuales" value="true"/>&nbsp;
							Incluir Detalle Asientos&nbsp;<input type="checkbox" checked="checked" id="incluir_detalle" name="incluir_detalle" value="true"/>&nbsp;
							<input type="button" id="generar" onclick="generarDiario()" value="Generar Diario" /><a href="javascript:void(0)" onclick="help(event, 'helpDiario')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
						</td>
					</tr>
				</table>	      	  
		</fieldset>	
		<fieldset class="block-labels">
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
<div id="helpDiario" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Libro Diario: Se ejecuta el reporte del libro diario de acuerdo a los parámetros indicados para el ejercicio, períodos, asientos y tipo de asiento. También puede optarse por listar o no el detalle de los pases de cada asiento o sólo los encabezados.
</div>

			
<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>
jQuery(document).ready(function(){
	jQuery('#<portlet:namespace />buscando').hide();	
	
});
	
function cambiarPeriodo(){
	var periodo=jQuery('#periodo_desde').val();
	jQuery('#periodo_hasta').val(periodo);
}

function generarDiario(){
	if (jQuery("#periodo").val() == "-1"){
		alert("Debe elegir un periodo");
		return;
	}
	jQuery("#generar").hide();
	var ejercicio=jQuery('#ejercicio').val();
	var periodo_desde=jQuery('#periodo_desde').val();
	var periodo_hasta=jQuery('#periodo_hasta').val();
	
	var nro_asiento_desde=jQuery('#nro_asiento_desde').val();
	var nro_asiento_hasta=jQuery('#nro_asiento_hasta').val();
	var incluir_automaticos=document.getElementById('incluir_automaticos');
	var incluir_manuales=document.getElementById('incluir_manuales');
	var incluir_detalle=document.getElementById('incluir_detalle');
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verificar_equivalencias_completas';
		url += '&ejercicio=' + escape(ejercicio);
		url += '&periodo_desde=' + escape(periodo_desde);
		url += '&periodo_hasta=' + escape(periodo_hasta);
		url += '&rnd=' + Math.floor(Math.random()*100);
		url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		
		
	jQuery.ajax({   
		url: url,
		success: function(data){	
			var obj = jQuery.parseJSON(data);
			if (obj.status == "equivalencias_conceptos_incompleto"){
				alert("Las equivalencias conceptos-cuentas se encuentran incompletas, por favor complete las mismas y vuelva a intentarlo.");
				return;
			}
			if (obj.status == "equivalencias_prestaciones_incompleto"){
				alert("Las equivalencias prestaciones-conceptos se encuentran incompletas, por favor complete las mismas y vuelva a intentarlo.");
				return;
			}
			if (obj.status == "falla_inesperada"){
				alert("Falla inesperada. Contacte a sistemas");
				return;
			}
			
			if (obj.status == "ok"){
				window.location.href ='/xlsservlet/?reporte=REPORTE_CONTABILIDAD_DIARIO'
				 + '&ejercicio=' + escape(ejercicio)
				 + '&periodo_desde=' + escape(periodo_desde)
				 + '&periodo_hasta=' + escape(periodo_hasta)
				 + '&nro_asiento_desde=' + escape(nro_asiento_desde)
				 + '&nro_asiento_hasta=' + escape(nro_asiento_hasta)
				 + '&incluir_automaticos='  + (incluir_automaticos.checked ? incluir_automaticos.value : 'false')
				 + '&incluir_manuales=' + (incluir_manuales.checked ? incluir_manuales.value : 'false')
				 + '&incluir_detalle='  + (incluir_detalle.checked ? incluir_detalle.value : 'false')
				 + '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
				 + '&rnd=' + Math.floor(Math.random()*100);
				
			}
			jQuery("#generar").show();
		}
	});

}	
</script>
