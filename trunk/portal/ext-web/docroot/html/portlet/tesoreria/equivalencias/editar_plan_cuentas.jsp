<%@ include file="/html/portlet/tesoreria/init.jsp" %>
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
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS)||portlet_name.equals("farmacia")||portlet_name.equals("uoma");
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
%>


<liferay-ui:error exception="<%=ar.com.ospim.tesoreria.CuentaDuplicadaException.class %>" message="duplicate-cuenta" />
<form action="" method="post" name="<portlet:namespace />editar_plan" >
<c:if test="${cuenta.id != 0}">
	<input type="hidden" name="ejercicio_hasta" value="${ejercicio_hasta}"/>
	<input type="hidden" name="ejercicio_desde" value="${ejercicio_desde}"/>
</c:if>
<input type="hidden" name="id" value="${cuenta.id}"/>
<input type="hidden" name="<%=Constants.CMD%>" value="<%=Constants.EDIT%>"/>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;"  > <!-- style="width: 50%" -->
	<tr>
			<%PlanCuentas pc = (PlanCuentas)request.getAttribute("cuenta");  %>
		
			<%   
				if(pc.getId() != 0 && !portlet_name.equals("farmacia")){
			
			%>
					<td><b>Ejercicio:</b></td>
					<td><b>${ejercicio_desde}&nbsp;-&nbsp;${ejercicio_hasta}</b></td>
			<%} %>
			
				<%
				   Calendar vigenDesde = Calendar.getInstance();	
				   Calendar vigenHasta = Calendar.getInstance();
				   vigenDesde.add(Calendar.YEAR,-1);
				   vigenHasta.set(Calendar.YEAR,2999);
				   
				   if(pc.getValidoDesde()!=null){
				  	 vigenDesde.setTime(pc.getValidoDesde());
				   }
				   if(pc.getValidoHasta()!=null){
				     vigenHasta.setTime(pc.getValidoHasta());
				   }  
				   
				   if(/* pc.getId() != 0 && */ portlet_name.equals("farmacia")){
				%>
				<td><liferay-ui:message	key="valido-desde" /> </td>
				<td>
					<liferay-ui:input-date 
							dayParam="validoDesdeDia"
							dayValue="1"
							monthParam="validoDesdeMes"
							monthValue="<%= Calendar.JULY%>"
							yearParam="validoDesdeAnio"
							yearValue="<%= vigenDesde.get(Calendar.YEAR)%>"
							yearRangeStart="<%= vigenDesde.get(Calendar.YEAR) - 100 %>"
							yearRangeEnd="<%= vigenDesde.get(Calendar.YEAR)+250%>"
							firstDayOfWeek="<%= vigenDesde.getFirstDayOfWeek() - 1 %>" 
							/>
					<a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				</td>
			</tr>
			<tr>	
				<td><liferay-ui:message	key="valido-hasta" /> </td>
				<td>
					<liferay-ui:input-date 
							dayParam="validoHastaDia"
							dayValue="30"
							monthParam="validoHastaMes"
							monthValue="<%= Calendar.JUNE%>"
							yearParam="validoHastaAnio"
							yearValue="<%= vigenHasta.get(Calendar.YEAR)%>"
							yearRangeStart="<%= vigenHasta.get(Calendar.YEAR) - 990 %>"
							yearRangeEnd="<%= vigenHasta.get(Calendar.YEAR)+100%>"
							firstDayOfWeek="<%= vigenHasta.getFirstDayOfWeek() - 1 %>" 
							/>
					<a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				</td>
			<%} %>
			<script type="text/javascript">
				jQuery('#<portlet:namespace/>validoDesdeDia').hide();
				jQuery('#<portlet:namespace/>validoHastaDia').hide();
			</script>
				
			<c:if test="${cuenta.id == 0}">
				<%if(!portlet_name.equals("farmacia")){%>
				<td><b>Ejercicio:</b></td>
				<td><select name="ejercicio" id="ejercicio">
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
							<%if(portlet_name.equals("farmacia")){%>
								Julio&nbsp;<%=i %>&nbsp;-&nbsp;Junio&nbsp;<%= i+1 %>
							<%}else{%>
								Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %>
							<%}%>
							</option>
						<% } %>
					</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				</td>
				<%} %>
			</c:if>
	</tr>	
	<tr>
		<td>Cuenta:</td>
		<td><input type="text" name="cuenta" value="${cuenta.cuenta}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpCuenta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
	</tr>
	<tr>
		<td>Numero:</td>
		<td><input type="text" name="numero" value="${cuenta.numero}" size="50"/><a href="javascript:void(0)" onclick="help(event, 'helpNumero')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
	</tr>
	<tr>
		<td colspan="2">Imputable:&nbsp;<input type="checkbox" name="imputable" id="imputable" value="true"/><a href="javascript:void(0)" onclick="help(event, 'helpImputable')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		<td colspan="2">Ajusta por Inflación:&nbsp;<input type="checkbox" name="ajinflacion" id="ajinflacion"/></td>
	</tr>
	<tr>
		<td>Tipo:</td>
		<td><select name="tipo" id="tipo">
							<option value="DEUDORA">DEUDORA</option>
							<option value="ACREEDORA">ACREEDORA</option>
						</select><a href="javascript:void(0)" onclick="help(event, 'helpTipo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
	</tr>
	<tr>
		<td colspan="2"><span id="botonGuardar"><input type="button" value="Guardar" onclick="guardar()"/><a href="javascript:void(0)" onclick="help(event, 'helpGuardar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></span></td>
	</tr>
</table>
</form>
<span id="guardando">
	<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
</span>
<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>" var="volver">
	<%if(portlet_name.equals("farmacia")){%>
		<portlet:param name="struts_action" value="/farmacia/plan_cuentas" />
	<%}else if(portlet_name.equals("uoma")){%>
		<portlet:param name="struts_action" value="/uoma/plan_cuentas" />
	<%}else{%>
		<portlet:param name="struts_action" value="/tesoreria/plan_cuentas" />
	<%}%>	
</portlet:renderURL>
<p><a href="<%= volver %>">Volver</a><a href="javascript:void(0)" onclick="help(event, 'helpVolver')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></p>

<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el ejercicio al que se aplica la cuenta. En el caso de un alta puede modificarse al ejercicio que se desee.
</div>
<div id="helpNumero" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Nï¿½mero: Indica el nï¿½mero o cï¿½digo que se asigna a la cuenta contable.
</div>
<div id="helpCuenta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta: Indica la descripciï¿½n de la cuenta contable.
</div>
<div id="helpImputable" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Imputable: Indica si la cuenta puede ser utilizada en asientos y equivalencias o se trata de un tï¿½tulo/subtï¿½tulo de un capï¿½tulo del plan de cuentas.
</div>
<div id="helpTipo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Tipo: Se refiere al tipo de cuenta contable. Sï¿½lo a nivel informativo. No es utilizado para ninguna validaciï¿½n.
</div>
<div id="helpGuardar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Guardar: Al seleccionar este botï¿½n, se efectï¿½an todos los controles sobre los datos ingresados y se graba en la tabla correspondiente; confirmando asï¿½ lo ingresado. No serï¿½ guardado ningï¿½n cambio si se abandona la pantalla sin seleccionar este botï¿½n.
</div>
<div id="helpVolver" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Volver: Seleccionando este link, se vuelve a la pantalla anterior. Se perderï¿½ toda actualizaciï¿½n efectuada en el caso que los cambios no se guarden previamente.
</div>



<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}else{%>

<%}%>
function guardar(){
		jQuery("#botonGuardar").toggle();
		jQuery("#guardando").toggle();
		var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta'
		document.<portlet:namespace />editar_plan.method = 'post';
		submitForm(document.<portlet:namespace />editar_plan, url);
}

	jQuery(document).ready(function() {
		jQuery("#guardando").hide();
		jQuery('#imputable').attr('checked', ${cuenta.imputable});
		jQuery('#ajinflacion').attr('checked', ${cuenta.ajustaInflacion});
		jQuery('#tipo').val("${cuenta.tipo}");
	});
	
</script>

