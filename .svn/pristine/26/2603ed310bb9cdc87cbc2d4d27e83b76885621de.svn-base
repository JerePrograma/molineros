<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Concepto"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<liferay-ui:error exception="<%= ConceptoUtilizadoException.class %>"
	message="concepto-utilizado" />

<%

	String portlet_name="tesoreria";	
	
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	} 
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD)||portlet_name.equals("uoma");
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
%>
<portlet:defineObjects />
<form action="" method="POST" id="gen_asientos_automaticos" name="gen_asientos_automaticos">
	<table style="width: 80%">
		<tr>
			<td colspan="4"><b>Generación Asientos Automáticos</b><a href="javascript:void(0)" onclick="help(event, 'helpTitulo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</tr>
		<tr>
			<td colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td>Ejercicio:&nbsp;<select name="ejercicio"  id="ejercicio">
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
			</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</td>
			<td colspan="3">Periodo:&nbsp;
			    
			    <% if(portlet_name.equals("farmacia")){%>
			    <select name="periodo" id="periodo" onChange="javascript:resetChecks();">
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
				&nbsp;Periodo Hasta:&nbsp;
				<select name="periodoHasta" id="periodoHasta" onChange="javascript:resetChecks();">
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
				</select> (Dejar vacío para un solo período)
			    
			    <%}else{%>	
				<select name="periodo" id="periodo" onChange="javascript:resetChecks();">
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
				</select>
				&nbsp;Periodo Hasta:&nbsp;
				<select name="periodoHasta" id="periodoHasta" onChange="javascript:resetChecks();">
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
				</select> (Dejar vacío para un solo período)
				<%}%>
				<a href="javascript:void(0)" onclick="help(event, 'helpPeriodo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</td>
		</tr>
		<tr>
			<td colspan="4">&nbsp;</td>
		</tr>
		
		<%if(!portlet_name.equals("tesoreria")){%>
		    <tr>
				<td>Devengado Boletas del mes - Automático&nbsp;</td>
				<td><input type="checkbox" id="devengado_boletas" checked="checked">
				<span id="span_devengado_boletas"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
				<span id="devengado_boletas_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
				<span id="devengado_boletas_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
				<a href="javascript:void(0)" onclick="help(event, 'helpAsientos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				</td>
				<td colspan="2">&nbsp;</td>
			</tr>
		
		<%}%>
		
		<%if(!portlet_name.equals("tesoreria")){%>
			<tr>
				<td>Ingresos Boletas del mes - Automático&nbsp;</td>
				<td><input type="checkbox" id="ingresos_boletas" checked="checked">
				<span id="span_ingresos_boletas"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
				<span id="ingresos_boletas_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
				<span id="ingresos_boletas_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
<!-- 				<a href="javascript:void(0)" onclick="help(event, 'helpAsientos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>   -->
				</td>
				<td colspan="2">&nbsp;</td>
			</tr>
		<%}else{%>
			<tr>
				<td>Ingresos AFIP del mes - Automático&nbsp;<i>(Tiempo esperado de generación 1.5 - 2 min)</i></td>
				<td><input type="checkbox" id="ingresos_afip" checked="checked">
				<span id="span_ingresos_afip"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
				<span id="ingresos_afip_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
				<span id="ingresos_afip_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
				<a href="javascript:void(0)" onclick="help(event, 'helpAsientos')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				</td>
				<td colspan="2">&nbsp;</td>
			</tr>
		<%}%>
		<tr>
			<td>Ingresos por recibos del mes - Automático&nbsp;</td>
			<td><input type="checkbox" id="ingresos_recibo" checked="checked">
			<span id="span_ingresos_recibo"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
			<span id="ingresos_recibo_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
			<span id="ingresos_recibo_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>Ingresos por movimientos bancarios del mes - Automático&nbsp;</td>
			<td><input type="checkbox" id="ingresos_mov_bcrio" checked="checked">
			<span id="span_ingresos_mov_bcrio"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
			<span id="ingresos_mov_bcrio_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
			<span id="ingresos_mov_bcrio_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>Egresos por movimientos bancarios del mes - Automático&nbsp;</td>
			<td><input type="checkbox" id="egresos_mov_bcrios" checked="checked">
			<span id="span_egresos_mov_bcrios"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
			<span id="egresos_mov_bcrios_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
			<span id="egresos_mov_bcrios_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<%if(null==portlet_name || portlet_name.equals("tesoreria")){%>
		<tr>
			<td>Egresos por reintegros del mes - Automático&nbsp;</td>
			<td><input type="checkbox" id="egresos_reintegros" checked="checked">
			<span id="span_egresos_reintegros"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
			<span id="egresos_reintegros_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
			<span id="egresos_reintegros_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		
		<tr>
			<td>Egresos por prestadores del mes - Automático&nbsp;</td>
			<td><input type="checkbox" id="egresos_prestaciones" checked="checked">
			<span id="span_egresos_prestaciones"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
			<span id="egresos_prestaciones_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
			<span id="egresos_prestaciones_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<%}%>
		<tr>
			<td>Egresos por proveedores del mes - Automático&nbsp;</td>
			<td><input type="checkbox" id="egresos_proveedores" checked="checked">
			<span id="span_egresos_proveedores"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
			<span id="egresos_proveedores_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
			<span id="egresos_proveedores_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>Actas y convenios del mes - Automático&nbsp;</td>
			<td><input type="checkbox" id="ingresos_actas" checked="checked">
			<span id="span_ingresos_actas"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
			<span id="ingresos_actas_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
			<span id="ingresos_actas_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		
		<%if(!portlet_name.equals("uoma")){%>
		<tr>
			<td>Comprobantes a pagar del mes - Automático&nbsp;</td>
			<td><input type="checkbox" id="comprobantes" checked="checked">
			<span id="span_comprobantes"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
			<span id="comprobantes_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
			<span id="comprobantes_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<%}%>
		
		<%if(portlet_name.equals("uoma")){%>
		    <tr>
				<td>Devengado Comprobantes del mes - Automático&nbsp;</td>
				<td><input type="checkbox" id="devengado_comprobantes" checked="checked">
				<span id="span_devengado_comprobantes"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
				<span id="devengado_comprobantes_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
				<span id="devengado_comprobantes_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
				</td>
				<td colspan="2">&nbsp;</td>
			</tr>
		
		<%}%>
		
		<%if(portlet_name.equals("uoma")){%>
		    <tr>
				<td>Facturas de Ventas del mes - Automático&nbsp;</td>
				<td><input type="checkbox" id="devengado_facturas_ventas" checked="checked">
				<span id="span_devengado_facturas_ventas"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
				<span id="devengado_facturas_ventas_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
				<span id="devengado_facturas_ventas_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
				</td>
				<td colspan="2">&nbsp;</td>
			</tr>
			
			<tr>
				<td>Cobranzas de Ventas del mes - Automático&nbsp;</td>
				<td><input type="checkbox" id="cobranzas_facturas_ventas" checked="checked">
				<span id="span_cobranzas_facturas_ventas"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
				<span id="cobranzas_facturas_ventas_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
				<span id="cobranzas_facturas_ventas_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
				</td>
				<td colspan="2">&nbsp;</td>
			</tr>
			
			
		
		<%}%>
		
		<tr>
				<td>Judicial del mes - Automático&nbsp;</td>
				<td><input type="checkbox" id="judiciales" checked="checked">
				<span id="span_judiciales"><img alt="generando" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" /></span>
				<span id="judiciales_exito"><img alt="exito" src="<%= themeDisplay.getPathThemeImages() %>/common/checked.png" /></span>
				<span id="judiciales_error"><img alt="error" src="<%= themeDisplay.getPathThemeImages() %>/common/close.png" /></span>
				</td>
				<td colspan="2">&nbsp;</td>
		</tr>
		
		<tr>
			<td colspan="4">&nbsp;
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<% if (rolABM) {%>
			<input type="button" id="generar" onclick="generarAsientosAutomaticos()"
				value="Generar Asientos Automaticos" /><a href="javascript:void(0)" onclick="help(event, 'helpGenerar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			<% } %>
			</td>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="4">&nbsp;
			</td>
		</tr>		
	</table>
</form>
	<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" var="volver">
		<%if(portlet_name.equals("farmacia")){%>
			<portlet:param name="struts_action" value="/farmacia/asientos" />
		<%}else if(portlet_name.equals("uoma")){%>
			<portlet:param name="struts_action" value="/uoma/asientos" />
		<%}else{%>
			<portlet:param name="struts_action" value="/tesoreria/asientos" />
		<%}%>
	</portlet:renderURL>
	<p><a href="<%= volver %>">Volver</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>
	

<div id="helpTitulo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Generación de asientos automáticos: Este proceso genera los 9 asientos que se describen más abajo y corresponden a todas las transacciones que se efectúan en el sistema y tienen impacto sobre la contabilidad. Se podrá seleccionar cuáles generar y cuáles no. También regenerarlos las veces que se desee; en este caso, reemplazando el asiento anterior para cada uno de los seleccionados.
</div>
<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el ejercicio que le corresponde al período que se quieren generar los asientos.
</div>
<div id="helpPeriodo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Período: Es el período al que se le desean generar los asientos.
</div>
<div id="helpAsientos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Selección de asientos: Se seleccionan los asientos que se desean generar o regenerar.
</div>
<div id="helpGenerar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Generar Asientos Automáticos: se ejecuta la generación de los asientos automáticos.
</div>
<div id="helpVolver" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Volver: Seleccionando este link, se vuelve a la pantalla anterior.
</div>


<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>
jQuery(document).ready(function(){
	jQuery('#<portlet:namespace />buscando').hide();
	jQuery("#span_devengado_boletas").hide();
	jQuery("#span_ingresos_boletas").hide();
	jQuery("#span_ingresos_afip").hide();
	jQuery("#span_ingresos_recibo").hide();
	jQuery("#span_ingresos_mov_bcrio").hide();
	jQuery("#span_egresos_mov_bcrios").hide();
	jQuery("#span_egresos_reintegros").hide();
	jQuery("#span_egresos_prestaciones").hide();
	jQuery("#span_egresos_proveedores").hide();
	jQuery("#span_ingresos_actas").hide();
	jQuery("#span_comprobantes").hide();
	jQuery("#span_devengado_comprobantes").hide();
	jQuery("#span_devengado_facturas_ventas").hide();
	jQuery("#span_cobranzas_facturas_ventas").hide();
	jQuery("#span_judiciales").hide();
	jQuery("#ingresos_afip_exito").hide();
	jQuery("#ingresos_boletas_exito").hide();
	jQuery("#devengado_boletas_exito").hide();
	jQuery("#devengado_comprobantes_exito").hide();
	jQuery("#devengado_facturas_ventas_exito").hide();
	jQuery("#cobranzas_facturas_ventas_exito").hide();
	jQuery("#judiciales_exito").hide();
	jQuery("#ingresos_recibo_exito").hide();
	jQuery("#ingresos_mov_bcrio_exito").hide();
	jQuery("#egresos_mov_bcrios_exito").hide();
	jQuery("#egresos_reintegros_exito").hide();
	jQuery("#egresos_prestaciones_exito").hide();
	jQuery("#egresos_proveedores_exito").hide();
	jQuery("#ingresos_actas_exito").hide();
	jQuery("#comprobantes_exito").hide();
	jQuery("#ingresos_afip_error").hide();
	jQuery("#ingresos_boletas_error").hide();
	jQuery("#devengado_boletas_error").hide();
	jQuery("#devengado_comprobantes_error").hide();
	jQuery("#ingresos_recibo_error").hide();
	jQuery("#ingresos_mov_bcrio_error").hide();
	jQuery("#egresos_mov_bcrios_error").hide();
	jQuery("#egresos_reintegros_error").hide();
	jQuery("#egresos_prestaciones_error").hide();
	jQuery("#egresos_proveedores_error").hide();
	jQuery("#ingresos_actas_error").hide();
	jQuery("#comprobantes_error").hide();
	jQuery("#devengado_facturas_ventas_error").hide();
	jQuery("#cobranzas_facturas_ventas_error").hide();
	jQuery("#judiciales_error").hide();
	
});

function generarAsientosAutomaticos(){	
	if (jQuery("#periodo").val() == "-1"){
		alert("Debe elegir un periodo");
		return;
	}	
	jQuery("#generar").hide();
	var ejercicio=jQuery('#ejercicio').val();
	var periodo=jQuery('#periodo').val();
	var periodoHasta=jQuery('#periodoHasta').val();	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/verificar_equivalencias_completas';
	url += '&ejercicio=' + escape(ejercicio);
	url += '&periodo=' + escape(periodo);
	url += '&periodoHasta=' + escape(periodoHasta);
	url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';	
	url += '&rnd=' + Math.floor(Math.random()*100);	
	jQuery.ajax({   
		url: url,
		cache: false,
		dataType: JSON,
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
				var asientos=["devengado_boletas","ingresos_boletas","ingresos_afip","ingresos_recibo","ingresos_mov_bcrio","egresos_mov_bcrios","egresos_reintegros","egresos_prestaciones","egresos_proveedores","ingresos_actas","comprobantes","devengado_comprobantes",
					"devengado_facturas_ventas","cobranzas_facturas_ventas","judiciales"];
				generarAsiento(asientos);
				return;
			}
			jQuery("#generar").show();
			urlRefresh();  
		},
		error: function () {
            alert('error');
        }
	});

}

function resetChecks(){
	var asientos=["devengado_boletas","ingresos_boletas","ingresos_afip","ingresos_recibo","ingresos_mov_bcrio","egresos_mov_bcrios","egresos_reintegros","egresos_prestaciones","egresos_proveedores","ingresos_actas","comprobantes","devengado_comprobantes","devengado_facturas_ventas","cobranzas_facturas_ventas","judiciales"];
	resetearAsiento(asientos);
	
}

function resetearAsiento(asientos){	
	
	var tipoAsiento = asientos[0];	
	
	if (!jQuery("#" + tipoAsiento).attr('checked') && tipoAsiento !== undefined){	
		jQuery("#span_" + tipoAsiento).hide();
		jQuery("#" + tipoAsiento).show();
		jQuery("#" + tipoAsiento).attr('checked', true);
		jQuery("#" + tipoAsiento + "_exito").hide();
		jQuery("#" + tipoAsiento + "_error").hide();
		resetearAsiento(asientos.slice(1));
	} else if(tipoAsiento !== undefined) {		
		resetearAsiento(asientos.slice(1)); //llamada recursiva
	}
}

function generarAsiento(asientos){	
	if (asientos.length == 0){
		jQuery("#generar").show();
		return;
	}
	var tipoAsiento = asientos[0];
	if (jQuery("#" + tipoAsiento).attr('checked')){	
		jQuery("#span_" + tipoAsiento).show();
		jQuery("#" + tipoAsiento).hide();
		jQuery("#" + tipoAsiento).attr('checked', false);
		var ejercicio=jQuery('#ejercicio').val();
		var periodo=jQuery('#periodo').val();
		var periodoHasta=jQuery('#periodoHasta').val();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/generar_asientos_automaticos';
		url += '&ejercicio=' + escape(ejercicio);
		url += '&periodo=' + escape(periodo);
		url += '&periodoHasta=' + escape(periodoHasta);
		url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';			
		
		url += '&rnd=' + Math.floor(Math.random()*100);
				
		jQuery.ajax({ url: url,  data: { tipo: tipoAsiento },
			success: function(data){
				var obj = jQuery.parseJSON(data);
				if (obj.status == "exito"){
					jQuery("#" + tipoAsiento + "_exito").show();
				} else {
					jQuery("#" + tipoAsiento + "_error").show();
				}
				jQuery("#span_" + tipoAsiento).hide();
				generarAsiento(asientos.slice(1)); //llamada recursiva
			}
		});
	} else {		
		generarAsiento(asientos.slice(1)); //llamada recursiva
	}
}
</script>
