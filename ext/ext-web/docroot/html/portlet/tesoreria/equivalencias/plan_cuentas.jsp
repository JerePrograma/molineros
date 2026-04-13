<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
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
	String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);

%>

<portlet:defineObjects />

<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.ConceptoUtilizadoException.class %>" message="cuenta-utilizada" />

<form action="" method="POST" id="busqueda_cuentas"
	name="busqueda_cuentas">
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
 		 if (rolABMEquivalencias && !portlet_name.equalsIgnoreCase("farmacia")) {
 	 		headerNames.add("Editar <a href='javascript:void(0)' onclick='help(event, \"helpEditarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 	 		headerNames.add("Eliminar <a href='javascript:void(0)' onclick='help(event, \"helpEliminarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 	 		 }
 		 if (rolABMEquivalencias && portlet_name.equalsIgnoreCase("farmacia")) {
  	 		headerNames.add("Editar <a href='javascript:void(0)' onclick='help(event, \"helpEditarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
  	 		/* headerNames.add("Eliminar <a href='javascript:void(0)' onclick='help(event, \"helpEliminarHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>"); */
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
				if (rolABMEquivalencias && !portlet_name.equalsIgnoreCase("farmacia")) {
					row.addText("<a href='javascript:void(0)' onclick=\"editarCuenta(" + cuenta.getId() +", '"+ejDesde+"','"+ ejHasta+"')\">Editar</a>");
					row.addText("<a href='javascript:void(0)' onclick=\"eliminarCuenta(" + cuenta.getId() +", '"+ejDesde+"','"+ ejHasta+"')\">Eliminar</a>");
				 }
				if (rolABMEquivalencias && portlet_name.equalsIgnoreCase("farmacia")) {
					row.addText("<a href='javascript:void(0)' onclick=\"editarCuenta(" + cuenta.getId() +", '"+ejDesde+"','"+ ejHasta+"')\">Editar</a>");
					/* row.addText("<a href='javascript:void(0)' onclick=\"eliminarCuenta(" + cuenta.getId() +", '"+ejDesde+"','"+ ejHasta+"')\">Eliminar</a>"); */
				 }
				resultRows.add(row);
			}
		}
		
%>

<b>Ejercicio &nbsp;<%=ejDesde %>&nbsp;-&nbsp;<%=ejHasta %></b><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio2')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>

<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />

<div id="helpNumero" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
N�mero: Se completa s�lo cuando se quiere efectuar una b�squeda que filtre por el n�mero de cuenta o parte del mismo. Tomar� s�lo las cuentas del ejercicio que se indique. Luego de ingresar el texto, se deber� seleccionar el bot�n "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpDescripcion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripci�n: Se completa s�lo cuando se quiere efectuar una b�squeda que filtre por la descripci�n, el texto ingresado. Tomar� s�lo las cuentas del ejercicio que se indique. Se deber� seleccionar el bot�n "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Se indica el ejercicio por el cual se quiere efectuar la b�squeda. Se deber� seleccionar el bot�n "Buscar" para visualizar el resultado en el cuadro inferior de esta pantalla.
</div>
<div id="helpBuscar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Buscar: De acuerdo a los par�metros previos, seleccionando este bot�n, se ejecuta la b�squeda de registros coincidentes. El resultado se visualiza en el cuadro inferior de esta pantalla.
</div>
<div id="helpAlta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Alta Cuenta: Seleccionando este bot�n, se abrir� la pantalla de alta de una nueva cuenta contable.
</div>
<div id="helpEjercicio2" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el ejercicio al cual pertenecen los registros del cuadro inferior de la pantalla. Es decir, de la �ltima b�squeda efectuada.
</div>
<div id="helpNumeroHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
N�mero: Indica el n�mero o c�digo que se asign� a la cuenta contable.
</div>
<div id="helpCuentaHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cuenta: Indica la descripci�n de la cuenta contable.
</div>
<div id="helpImputableHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Imputable: Indica si la cuenta puede ser utilizada en asientos y equivalencias o se trata de un t�tulo/subt�tulo de un cap�tulo del plan de cuentas.
</div>
<div id="helpEditarHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Editar: Se selecciona en el caso que se desee efectuar cambios sobre alg�n dato de un registro. Se abrir� una nueva pantalla de actualizaci�n.
</div>
<div id="helpEliminarHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Eliminar: Se selecciona en el caso que se desee un registro. No podr� borrarse un registro que fuera utilizado en alguna tabla o transacci�n del sistema.
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
	+  '&id=' +id + '&<%=Constants.CMD%>=<%=Constants.SEARCH%>'
	+ '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta); //Agregado
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
