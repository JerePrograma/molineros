<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

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
	
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS);
	/* String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE); */

%>

<portlet:defineObjects />

<div id="<portlet:namespace />div1" style="display: table-row;">
<div id="F1_C0" style="display: table-cell;">
<fieldset class="block-labels">
<legend><liferay-ui:message	key="plan-cuenta" /></legend>
		<div style="display: table; vertical-align: top; border-spacing: 4px;">
			<div id="<portlet:namespace />divPlanesCab" style="display: table-row;">
				<div id="FP_C1" style="display: table-cell;"><liferay-ui:message key="id" /></div>
				<div id="FP_C2" style="display: table-cell;"><liferay-ui:message key="cuenta" /></div>
				<div id="FP_C3" style="display: table-cell;"><liferay-ui:message key="imputable" /></div>
			   	<div id="FP_C4" style="display: table-cell;"><liferay-ui:message key="tipo" /></div>
			    <div id="FP_C5" style="display: table-cell;"><liferay-ui:message key="numero" /></div>
			    <div id="FP_C6" style="display: table-cell;"><liferay-ui:message key="valido-desde" /></div>
			    <div id="FP_C7" style="display: table-cell;"><liferay-ui:message key="valido-hasta" /></div>
				<div id="FP_C8" style="display: table-cell;"><liferay-ui:message key="Editar" /></div>
				<div id="FP_C9" style="display: table-cell;"><liferay-ui:message key="Eliminar" /></div>
			</div>
			
			<% ArrayList<PlanCuentas> planCuentasIdMaestro = (ArrayList<PlanCuentas>) request.getAttribute(WebKeysTesoreria.PLANES_CUENTAS_EN_SESSION); 
			   
			   Calendar vigenDesde = Calendar.getInstance();	
			   Calendar vigenHasta = Calendar.getInstance();	
			   
			   for(int i=0; i < planCuentasIdMaestro.size(); i++){	
				   
				   PlanCuentas pc = planCuentasIdMaestro.get(i);
				   
				   vigenDesde.setTime(pc.getValidoDesde());
				   vigenHasta.setTime(pc.getValidoHasta());
				   
			%>
			<div id="<portlet:namespace />divPlanNuevoDet_<%=i%>" style="display: table-row; border-spacing: 2px;">
				<div id="FP<%=i%>_C1" style="display: table-cell;">

					<input type="text" name="id_cuenta_<%=i%>" style="width: 40px;" readonly="readonly"
										   id="<portlet:namespace/>id_cuenta_<%=i%>" value="<%=pc.getId()%> ">
				</div>
				<div id="FP<%=i%>_C2" style="display: table-cell;">

					<input type="text" name="cuenta_<%=i%>" style="width: 150px;" readonly="readonly"
										   id="<portlet:namespace/>cuenta_<%=i%>" value="<%=pc.getCuenta()%> ">
				</div>
				<div id="FP<%=i%>_C3" style="display: table-cell;">

					<input type="checkbox" name="imputable_<%=i%>" id="<portlet:namespace/>imputable_<%=i%>" 
							value="" <%if(pc.isImputable()){%> checked="checked" <%} %> readonly="readonly" >
				</div>
				<div id="FP<%=i%>_C4" style="display: table-cell;">
					<select name="tipo_<%=i%>" id="<portlet:namespace/>tipo_<%=i%>" disabled="disabled" >
							<option value="DEUDORA" <%if(pc.getTipo().equalsIgnoreCase("DEUDORA")){ %> selected="selected" <%} %>>DEUDORA</option>
							<option value="ACREEDORA" <%if(pc.getTipo().equalsIgnoreCase("ACREEDORA")){ %> selected="selected" <%} %>>ACREEDORA</option>
					</select>
				</div>
				<div id="FP<%=i%>_C5" style="display: table-cell;">

					<input type="text" name="numero_<%=i%>" readonly="readonly"
										   id="<portlet:namespace/>numero_<%=i%>" value="<%=pc.getNumero()%> ">
				</div>
				<%	  String nuevaVigDesdeDayParamName = renderResponse.getNamespace() + "nuevo_fechaVigenDesdeDia_"+i;
					  String nuevaVigDesdeMonthParamName = renderResponse.getNamespace() + "nuevo_fechaVigenDesdeMes_"+i; 
					  String nuevaVigDesdeYearParamName = renderResponse.getNamespace() + "nuevo_fechaVigenDesdeAnio_"+i;
					  
					  String nuevaVigHastaDayParamName = renderResponse.getNamespace() + "nuevo_fechaVigenHastaDia_"+i;
					  String nuevaVigHastaMonthParamName = renderResponse.getNamespace() + "nuevo_fechaVigenHastaMes_"+i; 
					  String nuevaVigHastaYearParamName = renderResponse.getNamespace() + "nuevo_fechaVigenHastaAnio_"+i;
					%>		
				<div id="FP<%=i%>_C6" style="display: table-cell;">
					<liferay-ui:input-date 
							dayParam="<%=nuevaVigDesdeDayParamName%>"
							dayValue="<%= vigenDesde.get(Calendar.DATE)%>"
							monthParam="<%=nuevaVigDesdeMonthParamName%>"
							monthValue="<%= vigenDesde.get(Calendar.MONTH)%>"
							yearParam="<%=nuevaVigDesdeYearParamName%>"
							yearValue="<%= vigenDesde.get(Calendar.YEAR)%>"
							yearRangeStart="<%= vigenDesde.get(Calendar.YEAR) - 40 %>"
							yearRangeEnd="<%= vigenDesde.get(Calendar.YEAR)+20%>"
							firstDayOfWeek="<%= vigenDesde.getFirstDayOfWeek() - 1 %>" 
							disabled="<%=true%>" />
				</div>
				<script type="text/javascript">
				jQuery('#<portlet:namespace/><%=nuevaVigDesdeDayParamName%>').hide();
				</script>
				<div id="FP<%=i%>_C7" style="display: table-cell;">
					<liferay-ui:input-date
							dayParam="<%=nuevaVigHastaDayParamName%>"
							dayValue="<%= vigenHasta.get(Calendar.DATE)%>"
							monthParam="<%=nuevaVigHastaMonthParamName%>"
							monthValue="<%= vigenHasta.get(Calendar.MONTH)%>"
							yearParam="<%=nuevaVigHastaYearParamName%>"
							yearValue="<%= vigenHasta.get(Calendar.YEAR)%>"
							yearRangeStart="<%= vigenHasta.get(Calendar.YEAR) - 1%>"
							yearRangeEnd="<%= vigenHasta.get(Calendar.YEAR)+100%>"
							firstDayOfWeek="<%= vigenHasta.getFirstDayOfWeek() - 1 %>" 
							disabled="<%=true%>" />
				</div>
				<script type="text/javascript">
				jQuery('#<portlet:namespace/><%=nuevaVigHastaDayParamName%>').hide();
				</script>
				<div id="FP<%=i%>_C8" style="display: table-cell;">
					<%
					String editURL="javascript:editarCuentaAmtima('"+pc.getId()+"')"; 
					%>
					<liferay-ui:icon-menu>
						<liferay-ui:icon image="edit" url="<%= editURL %>" />
					</liferay-ui:icon-menu>
				</div>
				<div id="FP<%=i%>_C9" style="display: table-cell;">
					<%
					String deleteURL="javascript:eliminarCuentaAmtima('"+pc.getId()+"')"; 
					%>
					<liferay-ui:icon-menu>
						<liferay-ui:icon image="delete" url="<%= deleteURL %>" />
					</liferay-ui:icon-menu>
				</div>
			</div>
			<%} %> <!-- fin for plan ctas -->
	</div>
	</fieldset>		
</div>
</div>

<script type="text/javascript">
function editarCuentaAmtima(idCuenta){

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta';
		url +=  '&id=' +idCuenta + '&<%=Constants.CMD%>='+'<%=Constants.EDIT%>';
		url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function eliminarCuentaAmtima(idCuenta){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/eliminar_plan_cuenta'
	+  '&id=' +idCuenta ;
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
</script>

<%-- 

<form action="" method="POST" id="busqueda_cuentas" name="busqueda_cuentas">
	<table style="width: 100%">
		<tr>
			<td colspan="2"><b>Plan Cuentas</b></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>Ejercicio:&nbsp;<select name="ejercicio" id="ejercicio">
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
			</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
				N&uacute;mero:&nbsp;<input type="text" id="numero" name="numero" value="" size="10" /> <a href="javascript:void(0)" onclick="help(event, 'helpNumero')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp; Descripci&oacute;n:&nbsp;
				<input type="text" id="descripcion" name="descripcion" value="" size="50" /><a href="javascript:void(0)" onclick="help(event, 'helpDescripcion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a> 
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2"><input type="button" onclick="buscarCuentas()" value="Buscar" /><a href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
				<%if (rolABMEquivalencias){ %>
				<input type="button" onclick="altaCuenta()" value="Alta Cuenta" /><a href="javascript:void(0)" onclick="help(event, 'helpAlta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				<%} %>
		</tr>
	</table>
</form>
<hr />
<br />
<%
		String ejDesde = (String)request.getAttribute("ejercicio_desde");
		String ejHasta = (String)request.getAttribute("ejercicio_hasta");


		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = format.parse(ejDesde);
		Date fechaHasta = format.parse(ejHasta);
		
		List<PlanCuentas> cuentas = (List<PlanCuentas>) request.getAttribute("planCuentas");
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("N&uacute;mero <a href='javascript:void(0)' onclick='help(event, \"helpNumeroHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Cuenta <a href='javascript:void(0)' onclick='help(event, \"helpCuentaHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Imputable <a href='javascript:void(0)' onclick='help(event, \"helpImputableHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		 if (rolABMEquivalencias) {
 	 		headerNames.add("Editar <a href='javascript:void(0)' onclick='help(event, \"helpEditarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 	 		headerNames.add("Eliminar <a href='javascript:void(0)' onclick='help(event, \"helpEliminarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 	 		 }
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-cuentas-were-found"));
	
		if (cuentas != null){ 
		 	int total = 1;
			searchContainer.setTotal(total);
				List resultRows = searchContainer.getResultRows();
	
			int i = 0; 
			for (PlanCuentas cuenta: cuentas){
				i++;
				ResultRow row = new ResultRow(i, i, i);
				row.addText(cuenta.getNumero());
				row.addText(cuenta.getCuenta());
				row.addText(cuenta.isImputable() ? "SI" : "NO");
				if (rolABMEquivalencias) {
					row.addText("<a href='javascript:void(0)' onclick=\"editarCuenta(" + cuenta.getId() +", '"+ejDesde+"','"+ ejHasta+"')\">Editar</a>");
					row.addText("<a href='javascript:void(0)' onclick=\"eliminarCuenta(" + cuenta.getId() +", '"+ejDesde+"','"+ ejHasta+"')\">Eliminar</a>");
				 }
				resultRows.add(row);
			}
		}
		
%>

<b>Ejercicio &nbsp;<%=ejDesde %>&nbsp;-&nbsp;<%=ejHasta %></b><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio2')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>

<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />

<div id="helpNumero" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Número: Se completa sólo cuando se quiere efectuar una búsqueda que filtre por el número de cuenta o parte del mismo. Tomará sólo las cuentas del ejercicio que se indique. Luego de ingresar el texto, se deberá seleccionar el botón "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpDescripcion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripción: Se completa sólo cuando se quiere efectuar una búsqueda que filtre por la descripción, el texto ingresado. Tomará sólo las cuentas del ejercicio que se indique. Se deberá seleccionar el botón "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Se indica el ejercicio por el cual se quiere efectuar la búsqueda. Se deberá seleccionar el botón "Buscar" para visualizar el resultado en el cuadro inferior de esta pantalla.
</div>
<div id="helpBuscar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Buscar: De acuerdo a los parámetros previos, seleccionando este botón, se ejecuta la búsqueda de registros coincidentes. El resultado se visualiza en el cuadro inferior de esta pantalla.
</div>
<div id="helpAlta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Alta Cuenta: Seleccionando este botón, se abrirá la pantalla de alta de una nueva cuenta contable.
</div>
<div id="helpEjercicio2" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el ejercicio al cual pertenecen los registros del cuadro inferior de la pantalla. Es decir, de la última búsqueda efectuada.
</div>
<div id="helpNumeroHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Número: Indica el número o código que se asignó a la cuenta contable.
</div>
<div id="helpCuentaHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta: Indica la descripción de la cuenta contable.
</div>
<div id="helpImputableHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Imputable: Indica si la cuenta puede ser utilizada en asientos y equivalencias o se trata de un título/subtítulo de un capítulo del plan de cuentas.
</div>
<div id="helpEditarHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Editar: Se selecciona en el caso que se desee efectuar cambios sobre algún dato de un registro. Se abrirá una nueva pantalla de actualización.
</div>
<div id="helpEliminarHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Eliminar: Se selecciona en el caso que se desee un registro. No podrá borrarse un registro que fuera utilizado en alguna tabla o transacción del sistema.
</div>

<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
	jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>
function buscarCuentas(){
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/plan_cuentas';
	submitForm(document.busqueda_cuentas, url);
}
function altaCuenta(){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function editarCuenta(id, dd, hasta){
    <%if(portlet_name.equals("farmacia")){%>
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta'
	+  '&id=' +id;
	<% }else{ %>
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_plan_cuenta'
		+  '&id=' +id + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta);
	<%}%>
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function eliminarCuenta(id, dd, hasta){
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/eliminar_plan_cuenta'
	+  '&id=' +id + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta);
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
</script>
 --%>