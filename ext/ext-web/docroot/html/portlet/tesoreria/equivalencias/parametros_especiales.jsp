<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Concepto"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.liquidaciones.services.ConceptoServiceUtil.ParametroConcepto"%>
<%@ page import="ar.com.ospim.liquidaciones.services.ConceptoServiceUtil.ParametroCuenta"%>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas"%>
<%@ page import="java.util.List"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<liferay-ui:error exception="<%= ConceptoUtilizadoException.class %>"
	message="concepto-utilizado" />

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
<form action="" method="POST" id="busqueda_param_esp"
	name="busqueda_param_esp">
	<input type="hidden" name="ejercicio_desde" value="${ejercicio_desde}" />
	<input type="hidden" name="ejercicio_hasta" value="${ejercicio_hasta}" />
	<table style="width: 100%">
		<tr>
			<td colspan="2"><b>Parametros especiales</b></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>Ejercicio</td>
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
							Julio&nbsp;<%=i %>&nbsp;-&nbsp;Junio&nbsp;<%= i+1 %></option>
						<%}else{%>
							Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
						<%}%>
					<%} %>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2"><input type="button"
				onclick="buscarParametros()" value="Buscar" /><a href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;</td>
		</tr>
	</table>
</form>
<hr />
<br />
<%	
		String ejDesde = (String)request.getAttribute("ejercicio_desde");
		String ejHasta = (String)request.getAttribute("ejercicio_hasta");
					
		List<ParametroConcepto> pconceptos = (List<ParametroConcepto>) request.getAttribute("parametrosConceptos");
		List<ParametroCuenta> pCuentas = (List<ParametroCuenta>) request.getAttribute("parametrosCuentas");
		List<Concepto> conceptos = (List<Concepto>) request.getAttribute("conceptos");
		
		//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Parametro");
 		headerNames.add("Concepto");
 		headerNames.add("Detalle");
 		headerNames.add("Desde <a href='javascript:void(0)' onclick='help(event, \"helpDesde\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Hasta <a href='javascript:void(0)' onclick='help(event, \"helpHasta\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		 if (rolABMEquivalencias) {
	 		headerNames.add("Editar <a href='javascript:void(0)' onclick='help(event, \"helpEditar\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		 }
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-conceptos-were-found"));
	
	 	int total = 1;
		searchContainer.setTotal(total);
			List resultRows = searchContainer.getResultRows();

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = format.parse(ejDesde);
		Date fechaHasta = format.parse(ejHasta);
		int i = 0; 
		for (ParametroConcepto pc: pconceptos){
			if (pc.getParametro().equals("convenios_globales") || pc.getParametro().equals("prestaciones_medicas")){
				continue;
			}
			i++;
			ResultRow row = new ResultRow(i, i, i);
			row.addText(pc.getParametro());
			row.addText(conceptos.get(conceptos.indexOf(new Concepto(pc.getConceptoId()))).getDescripcion());
			row.addText(pc.getObservaciones());
			String dd = ejDesde;
			String hta = ejHasta;
			if (DateUtils.compararFechasTruncarEnDia(pc.getValidoDesde(), fechaDesde) >= 0){
				dd = pc.getValidoDesdeString();
			}
			row.addText(dd);
			
			if (DateUtils.compararFechasTruncarEnDia(pc.getValidoHasta(), fechaHasta) <= 0){
				hta = pc.getValidoHastaString();
			}
			row.addText(hta);
			
			 if (rolABMEquivalencias) {
				row.addText("<a href='javascript:void(0)' onclick=\"editarParam('" + pc.getParametro() +"', '"+ pc.getValidoDesdeString() +"', '"+dd+"','"+ hta+"')\">Editar</a>");
			 }
			resultRows.add(row);
		}
		
		
		
		PortletURL portletURL2 = renderResponse.createRenderURL();				
		String orderByCol2 = ParamUtil.getString(request, "orderByCol");
		String orderByType2 = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames2 = new ArrayList<String>();
 		headerNames2.add("Parametro");
 		headerNames2.add("Cuenta");
 		headerNames2.add("Detalle");
 		headerNames2.add("Desde");
 		headerNames2.add("Hasta");
 		 if (rolABMEquivalencias) {
	 		headerNames2.add("Editar");
 		 }
		SearchContainer searchContainer2 = new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL2, headerNames2,
			LanguageUtil.get(pageContext, "no-conceptos-were-found"));
	
	 	int total2 = 1;
		searchContainer2.setTotal(total);
		List resultRows2 = searchContainer2.getResultRows();

		int i2 = 0; 
		for (ParametroCuenta pc: pCuentas){
			i++;
			ResultRow row = new ResultRow(i, i, i);
			row.addText(pc.getParametro());
			row.addText(pc.getPlanCuentas().getNumero() + " - " + pc.getPlanCuentas().getCuenta());
			row.addText(pc.getObservaciones());
			String dd = ejDesde;
			String hta = ejHasta;
			if (DateUtils.compararFechasTruncarEnDia(pc.getValidoDesde(), fechaDesde) >= 0){
				dd = pc.getValidoDesdeString();
			}
			row.addText(dd);
			
			if (DateUtils.compararFechasTruncarEnDia(pc.getValidoHasta(), fechaHasta) <= 0){
				hta = pc.getValidoHastaString();
			}
			row.addText(hta);
			
			 if (rolABMEquivalencias) {
				row.addText("<a href='javascript:void(0)' onclick=\"editarParamCuenta('" + pc.getParametro() +"', '"+ pc.getValidoDesdeString() +"', '"+dd+"','"+ hta+"')\">Editar</a>");
			 }
			resultRows2.add(row);
		}
		
%>
<% if (pconceptos != null && pconceptos.size()>=1){ %>
<p>
	<b>Asociaciones con conceptos para egresos - Ejercicio &nbsp;<%=ejDesde %>&nbsp;-&nbsp;<%=ejHasta %></b>
</p>
<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />
<%} %>
<% if (pCuentas != null && pCuentas.size()>=1){ %>
<br/>
<p>
	<b>Asociaciones con cuentas para egresos/ingresos - Ejercicio &nbsp;<%=ejDesde %>&nbsp;-&nbsp;<%=ejHasta %></b>
</p>
<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer2 %>" />
<%} %>


<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Se indica el ejercicio en el cual se desean buscar los par�metros especiales. Luego, se deber� seleccionar el bot�n "Buscar" para visualizar el resultado.
</div>
<div id="helpBuscar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Buscar: Ejecuta la b�squeda de par�metros especiales para el ejercicio que se indique.
</div>
<div id="helpDesde" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Desde: Es la fecha de inicio de la vigencia de la equivalencia establecida. Dentro de un mismo ejercicio se podr�n asignar distintas equivalencias para diferentes per�odos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio.
</div>
<div id="helpHasta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Hasta: Es la fecha de finalizaci�n de la vigencia de la equivalencia establecida. Dentro de un mismo ejercicio se podr�n asignar distintas equivalencias para diferentes per�odos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio que se trate.
</div>
<div id="helpEditar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Editar: Se selecciona en el caso que se desee efectuar cambios sobre alg�n dato de un registro. Se abrir� una nueva pantalla de actualizaci�n.
</div>



<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>
function editarParam(param, ddOriginal, dd, hasta){	
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_parametro_especial'
	+  '&param=' +param + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta)+ '&ejercicio_desde_original=' + escape(ddOriginal);
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function editarParamCuenta(param, ddOriginal, dd, hasta){	
	
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_parametro_especial_cuenta'
	+  '&param=' +param + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta)+ '&ejercicio_desde_original=' + escape(ddOriginal);
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function buscarParametros(){
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/parametros_especiales'	
	submitForm(document.busqueda_param_esp, url);
}
</script>
