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
				<legend>Balance General</legend>
				<table class="lfr-table">
					<tr>
						<td>Ejercicio:&nbsp; <select name="ejercicio"  id="ejercicio" onchange="actualizarCuentas()">
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
				</select><%}%><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
						</td>
					</tr>
					<tr>
						<td >
							Incluir Automáticos&nbsp;<input type="checkbox"  checked="checked" id="incluir_automaticos" name="incluir_automaticos" value="true"/>&nbsp;
							Incluir Manuales&nbsp;<input type="checkbox" checked="checked" id="incluir_manuales" name="incluir_manuales" value="true"/>&nbsp;
							Incluir Saldo Inicial&nbsp;<input type="checkbox"  checked="checked" id="incluir_saldo_inicial" name="incluir_saldo_inicial" value="true" onclick="cambioCheckSaldoInicial()"/><a href="javascript:void(0)" onclick="help(event, 'helpSaldoInicial')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
							Incluir Asiento Inicial&nbsp;<input type="checkbox" checked="checked" id="incluir_asiento_inicial" name="incluir_asiento_inicial" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpAsientoInicial')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
							Incluir Asiento Final&nbsp;<input type="checkbox" checked="checked" id="incluir_asiento_final" name="incluir_asiento_final" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpAsientoFinal')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
							<input type="button" id="generar" onclick="generarDiario()" value="Generar Balance General" /><a href="javascript:void(0)" onclick="help(event, 'helpGeneracion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
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


<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio y períodos: Se establece el ejercicio y los períodos que se desean considerar en el reporte. En el caso que no se indiquen períodos, se considerará el ejercicio completo.
</div>
<div id="helpSaldoInicial" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Saldo Inicial: Si se selecciona, se efectuará el cálculo del saldo inicial al período "desde" indicado. Caso contrario, se considerará saldo inicial en cero.
</div>
<div id="helpAsientoInicial" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Asiento de Inicial: Indica si debe considerarse el asiento de inicio del ejercicio (Asiento número 1).
</div>
<div id="helpAsientoFinal" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Asiento final: Indica si debe, en el caso que exista, incluirse el asiento de cierre del ejercicio en el reporte.
</div>
<div id="helpGeneracion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Generación: Se emite el reporte con los parámetros previamente fijados.
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
	jQuery("#generar").hide();
	jQuery('#<portlet:namespace />buscando').show();
	var ejercicio=jQuery('#ejercicio').val();
	var periodo_desde=jQuery('#periodo_desde').val();
	var periodo_hasta=jQuery('#periodo_hasta').val();
	
	var incluir_automaticos=document.getElementById('incluir_automaticos');
	var incluir_manuales=document.getElementById('incluir_manuales');
	var incluir_asiento_inicial=document.getElementById('incluir_asiento_inicial');
	var incluir_asiento_final=document.getElementById('incluir_asiento_final');
	var incluir_saldo_inicial=document.getElementById('incluir_saldo_inicial');
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verificar_equivalencias_completas';
		url += '&ejercicio=' + escape(ejercicio);
		url += '&periodo_desde=' + escape(periodo_desde);
		url += '&periodo_hasta=' + escape(periodo_hasta);
		url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';
		url += '&rnd=' + Math.floor(Math.random()*100);
			
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
				window.location.href ='/xlsservlet/?reporte=REPORTE_CONTABILIDAD_BALANCE_GENERAL'
					+ '&ejercicio=' + escape(ejercicio)
					 + '&periodo_desde=' + escape(periodo_desde)
					 + '&periodo_hasta=' + escape(periodo_hasta)
					 + '&incluir_automaticos='  + (incluir_automaticos.checked ? incluir_automaticos.value : 'false')
					 + '&incluir_manuales=' + (incluir_manuales.checked ? incluir_manuales.value : 'false')
					 + '&incluir_asiento_inicial=' + (incluir_asiento_inicial.checked ? incluir_asiento_inicial.value : 'false')
					 + '&incluir_asiento_final=' + (incluir_asiento_final.checked ? incluir_asiento_final.value : 'false')
					 + '&incluir_saldo_inicial=' + (incluir_saldo_inicial.checked ? incluir_saldo_inicial.value : 'false')
					 + '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
				 	 + '&rnd=' + Math.floor(Math.random()*100);
				
			}
			jQuery("#generar").show();
		}
	});

}
function cambioCheckSaldoInicial(){
	var incluir_saldo_inicial=document.getElementById('incluir_saldo_inicial');
	if (!incluir_saldo_inicial.checked){
		jQuery("#incluir_asiento_inicial").removeAttr("checked");
		jQuery("#incluir_asiento_inicial").attr("disabled", true);
		jQuery("#incluir_asiento_final").removeAttr("checked");
		jQuery("#incluir_asiento_final").attr("disabled", true);
	} else {
		jQuery("#incluir_asiento_inicial").attr("checked", "checked");
		jQuery("#incluir_asiento_inicial").removeAttr("disabled");
		jQuery("#incluir_asiento_final").attr("checked", "checked");
		jQuery("#incluir_asiento_final").removeAttr("disabled");
	}
}
</script>
