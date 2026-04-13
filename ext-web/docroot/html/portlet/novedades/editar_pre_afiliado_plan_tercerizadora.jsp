<%@ include file="/html/portlet/novedades/init.jsp"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
List<Plan> planes = TraeListasServiceUtil.getPlanesMolineros();
Plan removerPlan1 = new Plan(9,"COBERTURA TOTAL O");
Plan removerPlan2 = new Plan(20,"COBERTURA TOTAL M");
Plan removerPlan3 = new Plan(21,"COBERTURA TOTAL MA");
Plan removerPlan4 = new Plan(22,"COBERTURA TOTAL MO");
Plan agregarPlan1 = new Plan(18,"USUFRUCTO");
Plan agregarPlan2 = new Plan(6,"SINDICATO");

planes.remove(removerPlan1);
planes.remove(removerPlan2);
planes.remove(removerPlan3);
planes.remove(removerPlan4);
planes.add(agregarPlan1);
planes.add(agregarPlan2);

PreAfiliado afiliado = (PreAfiliado)session.getAttribute(WebKeysAfiliados.PREAFILIADO_EN_EDICION);	

/* String cuit=null!=request.getParameter("cuit")?(String)request.getParameter("cuit"):"";
String razon_soc=null!=request.getParameter("razon_soc")?(String)request.getParameter("razon_soc"):"";
String sucursal=null!=request.getParameter("sucur")?(String)request.getParameter("sucur"):""; */
String esEdicionStr = request.getParameter("esEdicion");
boolean esEdicion=false;
if(esEdicionStr == null || esEdicionStr.equalsIgnoreCase("true")){
	esEdicion = true;
}

Calendar fechaVigDesde = Calendar.getInstance();
Calendar fechaVigHasta = null;
	
if(afiliado != null && afiliado.getId_plan() != null){ // update
	if(afiliado.getVigenDesde() != null){
		fechaVigDesde.setTime(afiliado.getVigenDesde());	
	}
	
	if(afiliado.getVigenHasta() != null){
		fechaVigHasta = Calendar.getInstance();
		fechaVigHasta.setTime(afiliado.getVigenHasta());
	}
}

%>
<script>

<%-- function filtrarTerceriz() {
	
	var idplan = jQuery('#<portlet:namespace/>nuevoPlan').val();
	var id = '';
	<%if(afiliado != null && afiliado.getId_tercerizadora() != null){%>
		id ='<%=afiliado.getId_tercerizadora()%>';
	<%}%>
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/valida_plan_tercerizadora&id_plan='+idplan;
	jQuery.ajax({   
		url: url,
		success: function(data){
			document.getElementById("<portlet:namespace/>tercerizadora").length = 0;						
			var obj = jQuery.parseJSON(data);
			//addElementToSelect("<portlet:namespace/>usuario_destino", "Seleccione un usuario", ""); 
			for(var i =0;i< obj.listaFiltrada.length; i++){					
				var value = obj.listaFiltrada[i].split('|')[0];
				var text = obj.listaFiltrada[i].split('|')[1];
				addElementToSelectSel("<portlet:namespace/>tercerizadora", text, value, id);					
			}	                                                                                                                                                                                                                                                            
		}
	});	
}
 --%>
function addElementToSelectSel(id_combo, texto, valor, id) {
	var combo = document.getElementById(id_combo);
	var idxElemento = combo.options.length; //Numero de elementos de la combo si esta vacio es 0. Este indice será el del nuevo elemento
	combo.options[idxElemento] = new Option();
	combo.options[idxElemento].text = texto; //Este es el texto que verás en la combo
	combo.options[idxElemento].value = valor; //Este es el valor que se enviará cuando hagas un submit del formulario que lo contiene
	if(valor == id){
		combo.options[idxElemento].selected = true;
	}
} 
</script>

<fieldset class="block-labels">
<legend><liferay-ui:message	key="planes-prestadores" /></legend>
<table style="border-collapse: separate; border-spacing: 5px; width: 100%;">	
		
		<tr>
			<th><liferay-ui:message	key="descripcion" /></th>
			<th><liferay-ui:message	key="desde" /></th>	
			<th><liferay-ui:message	key="hasta" /></th>	
			<th><liferay-ui:message	key="motivo-baja" /></th>	 
		</tr>
		<tr>
			<td>
				<select name="<portlet:namespace/>nuevoPlan" id="<portlet:namespace/>nuevoPlan" style="width: 240px; "  
					<%if(!esEdicion){ %> disabled="disabled" <%} %> > <!-- onchange="javascript:filtrarTerceriz();" -->
					<option value='0'><liferay-ui:message key="seleccione-plan" /></option>
					<%for (Plan plan : planes) { %>
							<option value="<%= plan.getId()%>"  
								<%if(afiliado!=null 
								&& afiliado.getId_plan()==plan.getId()){ %> selected="selected" <%} %> ><%=plan.getDescripcion()%></option>
					<% } %>
				</select>
			</td>		
			<td><liferay-ui:input-date 
				dayParam="fechaVigenDesdeDia"
				dayValue="<%= fechaVigDesde.get(Calendar.DATE)%>"
				monthParam="fechaVigenDesdeMes"
				monthValue="<%= fechaVigDesde.get(Calendar.MONTH) %>"
				yearParam="fechaVigenDesdeAnio"
				yearValue="<%= fechaVigDesde.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaVigDesde.get(Calendar.YEAR) - 40 %>"
				yearRangeEnd="<%= fechaVigDesde.get(Calendar.YEAR)+20%>"
				firstDayOfWeek="<%= fechaVigDesde.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion %>" /></td>
			<%if(fechaVigHasta == null){%>
				<td><liferay-ui:input-date
					dayNullable="true"
					dayParam="fechaVigenHastaDia"
					monthNullable="true" 
					monthParam="fechaVigenHastaMes"
					yearNullable="true"
					yearParam="fechaVigenHastaAnio"
					yearRangeStart="<%= fechaVigDesde.get(Calendar.YEAR) - 40 %>"
					yearRangeEnd="<%= fechaVigDesde.get(Calendar.YEAR)+60%>"
					firstDayOfWeek="<%= fechaVigDesde.getFirstDayOfWeek() - 1 %>"
					disabled="<%= !esEdicion %>" /></td>
			<%}else{%>
				<td><liferay-ui:input-date
					dayNullable="true"
					dayParam="fechaVigenHastaDia"
					dayValue="<%= fechaVigHasta.get(Calendar.DATE)%>"
					monthNullable="true" 
					monthParam="fechaVigenHastaMes"
					monthValue="<%= fechaVigHasta.get(Calendar.MONTH) %>"
					yearNullable="true"
					yearParam="fechaVigenHastaAnio"
					yearValue="<%= fechaVigHasta.get(Calendar.YEAR) %>"
					yearRangeStart="<%= fechaVigHasta.get(Calendar.YEAR) - 40 %>"
					yearRangeEnd="<%= fechaVigHasta.get(Calendar.YEAR)+60%>"
					firstDayOfWeek="<%= fechaVigHasta.getFirstDayOfWeek() - 1 %>"
					disabled="<%= !esEdicion %>" /></td>
			<%} %>
			<td>
				<select name="<portlet:namespace/>motivoBajaPlan" id="<portlet:namespace/>motivoBajaPlan" 
					style="width: 200px; vertical-align: top;" <%if(!esEdicion){ %> disabled="disabled" <%} %>>
					<option value="" selected="selected"><liferay-ui:message key="seleccione-motivo-baja" /></option>
					<% for (MotivoBaja motivoBaja : motivos) {
						if(motivoBaja.getId_motivo_baja()!=0){%>
						<option <%=afiliado!= null && afiliado.getId_motivo_baja() == motivoBaja.getId_motivo_baja() ? "selected" : "" %> 
								value="<%= motivoBaja.getId_motivo_baja()%>"><%=motivoBaja.getDescripcion()%></option>		
					<%}} %>
				</select>
			</td>
		</tr>
		<tr><td><input type="hidden" name="<portlet:namespace/>tercerizadora"	id="<portlet:namespace/>tercerizadora" value="MEN"> </td></tr>
	</table>
	
</fieldset>   
<br />

<%-- <fieldset class="block-labels">
<legend><liferay-ui:message	key="tercerizadora-servicio" /></legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px; width: 100%;">
		<tr>
			<td><label><liferay-ui:message key="tercerizadora-servicio" /></label>&nbsp;&nbsp;
			
				<select  name="<portlet:namespace/>tercerizadora"	id="<portlet:namespace/>tercerizadora"
					<%if(!esEdicion){ %> disabled="disabled" <%} %>>
					<option value="0">Seleccione una tercerizadora</option>
				</select>
			</td>
		</tr>
	</table>
</fieldset>
<br/>	 --%>

<!-- <script>
filtrarTerceriz();
</script> -->
		