<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Concepto"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="ar.com.uoma.beans.CentroCosto" %>
<%@ page import="ar.com.uoma.centro_costo.CentroCostoServiceUtil" %>
<%@ page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %>
<%@ page import="com.liferay.portal.theme.ThemeDisplay" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>	


<portlet:defineObjects/>

<%
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	int entidad = 0;
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
		entidad = WebKeysGlobal.OSPIM;
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
		entidad = WebKeysGlobal.AMTIMA;
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
		entidad = WebKeysGlobal.UOMA;
	}
	
	
	Calendar fecha_desde = CalendarFactoryUtil.getCalendar();
	fecha_desde.setTime(new Date());
	
	Calendar fecha_hasta = CalendarFactoryUtil.getCalendar();
	fecha_hasta.setTime(new Date());
	
	
	List<CentroCosto> centros = new ArrayList<CentroCosto>();
	try{
	   //centros=TraeListasServiceUtil.getCentrosDeCostosVigentes(entidad);
	   centros=CentroCostoServiceUtil.getContables(fecha_desde.getTime(), entidad);
	}catch(Exception e){}		

%>		
		
		<style>
			.hidden {
				display: none;
			}
				
		</style>
		<fieldset class="block-labels">
			<legend>Centro de costo - Contabilidad</legend>
			<table class="lfr-table">
				<tr>
					<td></td>
				</tr>
				<tr>	
					<td>
						<div>
							<span>Periodo - desde:</span> 
							<liferay-ui:input-date 
							dayNullable="true"
							dayValue="<%=fecha_desde.get(Calendar.DAY_OF_MONTH )%>" 
							dayParam="fechaDesdeDia"
							monthNullable="true"
							monthValue="<%=fecha_desde.get(Calendar.MONTH )%>"
							monthParam="fechaDesdeMes"
							yearNullable="true"
							yearValue="<%=fecha_desde.get(Calendar.YEAR ) %>"
							yearParam="fechaDesdeAnio"
							yearRangeStart="<%= fecha_desde.get(Calendar.YEAR) - 20 %>"
							yearRangeEnd="<%= fecha_desde.get(Calendar.YEAR) + 20 %>"
							firstDayOfWeek="<%= fecha_desde.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</div>
						<div>						
							<span>Periodo - hasta:&nbsp;</span>
							<liferay-ui:input-date 
							dayNullable="true"
							dayValue="<%=fecha_hasta.get(Calendar.DAY_OF_MONTH )%>" 
							dayParam="fechaHastaDia"
							monthNullable="true"
							monthValue="<%=fecha_hasta.get(Calendar.MONTH )%>"
							monthParam="fechaHastaMes"
							yearNullable="true"
							yearValue="<%=fecha_hasta.get(Calendar.YEAR ) %>"
							yearParam="fechaHastaAnio"
							yearRangeStart="<%= fecha_hasta.get(Calendar.YEAR) - 20 %>"
							yearRangeEnd="<%= fecha_hasta.get(Calendar.YEAR) + 20 %>"
							firstDayOfWeek="<%= fecha_hasta.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</div>
					</td>
					<td>
						<input 
						type="button"  
						onclick="traerCuentas()" 
						value="Traer Cuentas" />
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td id="container_guardando_cuentas" class="hidden">
						<span id="guardando">
						<img 
							alt="<liferay-ui:message key='buscando'/>" 
							src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</span>
					</td>
					<td id="container_cuentas" class="hidden">
						<select size="20" id="cuentas" name="cuentas" multiple="multiple"></select>
					</td>

					<td id="container_guardando" class="hidden">
						<span id="guardando">
						<img 
							alt="<liferay-ui:message key='buscando'/>" 
							src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
						</span>
					</td>
					<td id="container_centro_costo" class="hidden">
						<select 
							id="<portlet:namespace />id_centroCosto" 
							name="<portlet:namespace />id_centroCosto" 
							multiple="multiple"
							size="20">
							<% 	for (CentroCosto centro : centros) {  %>
									<option value="<%=centro.getId()%>"><%=centro.getDescripcion()%></option>
							<%	} %>
						</select>
					</td>
				</tr>
			</table>	
			<div 
				style="
					display:flex; 
					justify-content:space-around;
					margin-top: 50px;">
				
				<div>				
				    <label style="margin-right:5px;">Vista desde la cuenta</label>
				    <input 
				        type="checkbox"   
				        id="vista_cuenta" 
				        name="vista" 
				        value="true"
				        onclick="toggleCheckbox('vista_cuenta', 'vista_centro_costo', 'vista_cartesiano')"
				        checked="checked"
				    />&nbsp;
				</div>
								
				<div>				
				    <label style="margin-left:20px;">Vista desde el centro de costo</label>
				    <input 
				        type="checkbox"  
				        id="vista_centro_costo" 
				        name="vista" 
				        value="false"
				        onclick="toggleCheckbox('vista_centro_costo', 'vista_cuenta', 'vista_cartesiano')"
				    />&nbsp;
				</div>
				
				<div>				
				    <label style="margin-left:20px;">Matriz</label>
				    <input 
				        type="checkbox"  
				        id="vista_cartesiano" 
				        name="vista" 
				        value="false"
				        onclick="toggleCheckbox('vista_cartesiano', 'vista_cuenta', 'vista_centro_costo')"
				    />&nbsp;
				</div>
				
								
				<input 
						type="button" 
						id="generar_excel" 
						onclick="generarExcel()" 
						value="Generar Excel" />
				
			</div>      	  
		</fieldset>	

<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio y períodos: Se establece el ejercicio y los períodos que se desean considerar en el reporte. En el caso que no se indiquen períodos, se considerará el ejercicio completo.
</div>
<div id="helpCuentas" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Selección de Cuentas: Se seleccionan las cuentas a considerar en el reporte. Si no se indica ninguna, se emitirá para todas las cuentas. Se podrán seleccionar cuentas en forma alternada utilizando la tecla "Control" como así también seleccionar varias cuentas consecutivas utilizando la tecla de "Mayúsculas".
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

<script src="https://unpkg.com/xlsx/dist/xlsx.full.min.js"></script>
<script type="text/javascript">


jQuery(document).ready(function(){
	
	//jQuery('#<portlet:namespace />buscando').hide();	
	//actualizarCuentas();
});

function toggleCheckbox(checkboxId, otherCheckboxId1, otherCheckboxId2) {
    var checkbox = document.getElementById(checkboxId);
    var otherCheckbox1 = document.getElementById(otherCheckboxId1);
    var otherCheckbox2 = document.getElementById(otherCheckboxId2);

    if (checkboxId === 'vista_cartesiano') {
        otherCheckbox1.checked = false;
        otherCheckbox2.checked = false;
    } else {
        var isChecked = checkbox.checked;
        if (otherCheckbox1.checked && isChecked) {
            otherCheckbox1.checked = false;
        }
        if (otherCheckbox2.checked && isChecked) {
            otherCheckbox2.checked = false;
        }
    }
}



function generarExcel(){
	
	var vista_cuenta = document.getElementById("vista_cuenta").checked;
	var vista_centro_costo = document.getElementById("vista_centro_costo").checked; 
	
	var dia_desde = document.getElementById('<portlet:namespace />fechaDesdeDia').value;
	var mes_desde = document.getElementById('<portlet:namespace />fechaDesdeMes').value;
	var anio_desde = document.getElementById('<portlet:namespace />fechaDesdeAnio').value;
	mes_desde = parseInt(mes_desde) + 1;
	var fecha_desde = dia_desde + "/" + mes_desde + "/" + anio_desde;

	var dia_hasta = document.getElementById('<portlet:namespace />fechaHastaDia').value;
	var mes_hasta = document.getElementById('<portlet:namespace />fechaHastaMes').value;
	var anio_hasta = document.getElementById('<portlet:namespace />fechaHastaAnio').value;
	mes_hasta = parseInt(mes_hasta) + 1;
	var fecha_hasta = dia_hasta + "/" + mes_hasta + "/" + anio_hasta;
	
	var vista_desde_cuenta = document.getElementById('vista_cuenta').checked;
	console.log("vista desde la cuenta contable " + vista_desde_cuenta);

	var vista_desde_centro_costo = document.getElementById('vista_centro_costo').checked;
	console.log("vista desde centro de costo " + vista_desde_centro_costo);

	var vista_cartesiano = document.getElementById('vista_cartesiano').checked;
	console.log("vista producto cartesiano " + vista_cartesiano);

	
	var cuentas = jQuery("#cuentas").val();
	var centro_costo = jQuery("#<portlet:namespace />id_centroCosto").val();
	
	var centro_costo_ids = [];
	var cuentas_ids = [];
	
	if(cuentas===null && centro_costo===null){
		
		alert("Por favor elegir una cuenta o un centro de costo para continuar");
	}
	
	if(!(cuentas===null)){
		
		if(centro_costo === null){
			
			jQuery("#<portlet:namespace />id_centroCosto option").each(function() {
				centro_costo_ids.push(jQuery(this).val());
				
            });
			
			window.location.href ='/xlsservlet/?reporte=REPORTE_CENTROS_COSTOS_CONTABLE'
				+ '&fecha_desde=' + escape(fecha_desde)
				+ '&fecha_hasta=' + escape(fecha_hasta)																																																																																																																						
				+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'		
				+ '&cuentas=' + escape(jQuery("#cuentas").val())
				+ '&centro_costo=' + escape(centro_costo_ids)
				+ '&vista_cuenta=' + escape(vista_cuenta)
				+ '&vista_centro_costo=' + escape(vista_centro_costo)
				+ '&vista_cartesiano=' + escape(vista_cartesiano)
				+ '&rnd=' + Math.floor(Math.random()*100);
			
		}else {
			
			window.location.href ='/xlsservlet/?reporte=REPORTE_CENTROS_COSTOS_CONTABLE'
				+ '&fecha_desde=' + escape(fecha_desde)
				+ '&fecha_hasta=' + escape(fecha_hasta)																																																																																																																						
				+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'		
				+ '&cuentas=' + escape(jQuery("#cuentas").val())
				+ '&centro_costo=' + escape(jQuery("#<portlet:namespace />id_centroCosto").val())
				+ '&vista_cuenta=' + escape(vista_cuenta)
				+ '&vista_centro_costo=' + escape(vista_centro_costo)
				+ '&vista_cartesiano=' + escape(vista_cartesiano)
				+ '&rnd=' + Math.floor(Math.random()*100);
		}
		
	}
	
	if(!(centro_costo === null)){
		
		console.log(centro_costo)
		
		if(cuentas === null){
			
			jQuery("#cuentas option").each(function(){
				cuentas_ids.push(jQuery(this).val());
			})
			
			console.log(cuentas_ids);
			console.log(centro_costo);
			
			window.location.href ='/xlsservlet/?reporte=REPORTE_CENTROS_COSTOS_CONTABLE'
				+ '&fecha_desde=' + escape(fecha_desde)
				+ '&fecha_hasta=' + escape(fecha_hasta)																																																																																																																						
				+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'		
				+ '&cuentas=' + escape(cuentas_ids)
				+ '&centro_costo=' + escape(centro_costo)
				+ '&vista_cuenta=' + escape(vista_cuenta)
				+ '&vista_centro_costo=' + escape(vista_centro_costo)
				+ '&vista_cartesiano=' + escape(vista_cartesiano)
				+ '&rnd=' + Math.floor(Math.random()*100);
		}else {
			
			window.location.href ='/xlsservlet/?reporte=REPORTE_CENTROS_COSTOS_CONTABLE'
				+ '&fecha_desde=' + escape(fecha_desde)
				+ '&fecha_hasta=' + escape(fecha_hasta)																																																																																																																						
				+ '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'		
				+ '&cuentas=' + escape(jQuery("#cuentas").val())
				+ '&centro_costo=' + escape(jQuery("#<portlet:namespace />id_centroCosto").val())
				+ '&vista_cuenta=' + escape(vista_cuenta)
				+ '&vista_centro_costo=' + escape(vista_centro_costo)
				+ '&vista_cartesiano=' + escape(vista_cartesiano)
				+ '&rnd=' + Math.floor(Math.random()*100);
		}	
	}
}

function traerCuentas() {
	
	var container_guardando_cuentas = document.getElementById('container_guardando_cuentas');
	container_guardando_cuentas.classList.remove('hidden');
	
	var container_guardando = document.getElementById('container_guardando');
	container_guardando.classList.remove('hidden');
	
    var dia_desde = document.getElementById('<portlet:namespace />fechaDesdeDia').value;
    var mes_desde = document.getElementById('<portlet:namespace />fechaDesdeMes').value;
    var anio_desde = document.getElementById('<portlet:namespace />fechaDesdeAnio').value;
    var fecha_desde = dia_desde + "/" + mes_desde + "/" + anio_desde;

    var dia_hasta = document.getElementById('<portlet:namespace />fechaHastaDia').value;
    var mes_hasta = document.getElementById('<portlet:namespace />fechaHastaMes').value;
    var anio_hasta = document.getElementById('<portlet:namespace />fechaHastaAnio').value;
    var fecha_hasta = dia_hasta + "/" + mes_hasta + "/" + anio_hasta;

    //https://localhots:8080?fecha_desde=fecha_desde, fecha_hasta=fecha_hasta, entidad=emtidad;
    
    var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/traer_plan_cuentas_vigentes_reporte_final'
    url += '&fecha_desde' + fecha_desde
    url += '&fecha_hasta' + fecha_hasta
    url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
    url += '&rnd=' + Math.floor(Math.random() * 100);
    
    jQuery.ajax({
        url: url,
        success: function (data) {
            var cuentasSelect = document.getElementById('cuentas');
            cuentasSelect.innerHTML = ''; // Limpiar el contenido actual del select

            var cuentas = JSON.parse(data).cuentas;
            for (var i = 0; i < cuentas.length; i++) {
                var cuenta = cuentas[i];
                var option = document.createElement('option');
                option.value = cuenta.id;
                option.textContent = cuenta.numero + ' - ' + cuenta.cuenta;
                cuentasSelect.appendChild(option);
            }
            
            if(cuentas){
            	var container_guardando_cuentas = document.getElementById('container_guardando_cuentas');
				container_guardando_cuentas.classList.add('hidden');
				var container_cuentas = document.getElementById('container_cuentas');
				container_cuentas.classList.remove('hidden');
				
				var container_guardando = document.getElementById('container_guardando');
				container_guardando.classList.add('hidden');
				var container_centro_costo = document.getElementById('container_centro_costo');
				container_centro_costo.classList.remove('hidden');
            }
        }
    });
}

</script>
