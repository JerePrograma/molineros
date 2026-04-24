<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.ConceptoAfip"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<liferay-ui:error exception="<%= ConceptoUtilizadoException.class %>"
	message="concepto-utilizado" />

<%
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS);
%>
<portlet:defineObjects />
<form action="" method="POST" id="busqueda_conceptos"
	name="busqueda_conceptos">
	<table style="width: 100%">
		<tr>
			<td colspan="2"><b>Buscar conceptos Afip</b></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;Ejercicio:&nbsp; <select name="ejercicio">
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						if (cal.get(Calendar.MONTH) < Calendar.AUGUST){
							hastaAnio--;
						}
						for (int i = 2000; i<=hastaAnio; i++){  %>
					<option value="<%=i%>-<%=i+1%>" <%if (i == hastaAnio) { %>
						selected="selected" <%} %>>
						Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
					<%} %>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			&nbsp;Codigo&nbsp;<input type="text" id="codigo" name="codigo" value=""	size="10" /><a href="javascript:void(0)" onclick="help(event, 'helpConcepto')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
			Concepto&nbsp;<input type="text" id="concepto" name="concepto" value=""	size="50" /> <a href="javascript:void(0)" onclick="help(event, 'helpCodigo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2"><input type="button" onclick="buscarConceptos()" value="Buscar" /><a href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp; 
			<% if (rolABMEquivalencias) {%> 
				<input type="button" onclick="altaConcepto()" value="Alta Concepto" /><a href="javascript:void(0)" onclick="help(event, 'helpAlta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			<% } %>
			</td>
		</tr>
	</table>
</form>
<hr />
<br />

<%	

		String ejDesde = (String)request.getAttribute("ejercicio_desde");
		String ejHasta = (String)request.getAttribute("ejercicio_hasta");
					
		List<ConceptoAfip> conceptos = (List<ConceptoAfip>) request.getAttribute("conceptos");
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Concepto Afip <a href='javascript:void(0)' onclick='help(event, \"helpConceptoAfip\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Descripcion <a href='javascript:void(0)' onclick='help(event, \"helpDescripcion\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Concepto <a href='javascript:void(0)' onclick='help(event, \"helpConceptoHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
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

		int i = 0; 
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = format.parse(ejDesde);
		Date fechaHasta = format.parse(ejHasta);
		for (ConceptoAfip concepto: conceptos){
			i++;
			ResultRow row = new ResultRow(i, i, i);
			row.addText(concepto.getCodigoConcepto());
			row.addText(concepto.getDescripcion());
			row.addText(concepto.getConcepto().getDescripcion());
			String dd = ejDesde;
			String hta = ejHasta;
			if (DateUtils.compararFechasTruncarEnDia(concepto.getValidoDesde(), fechaDesde) >= 0){
				dd = concepto.getValidoDesdeString();
			}
			row.addText(dd);
			
			if (DateUtils.compararFechasTruncarEnDia(concepto.getValidoHasta(), fechaHasta) <= 0){
				hta = concepto.getValidoHastaString();
			}
			row.addText(hta);
			 if (rolABMEquivalencias) {
				row.addText("<a href='javascript:void(0)' onclick=\"editarConcepto('"+ concepto.getCodigoConcepto() +"', " + concepto.getId() +", '"+dd+"','"+ hta+"')\">Editar</a>");
			 }
			resultRows.add(row);
		}
		
%>
<% if (conceptos != null && conceptos.size()>=1){ %>
<p>
	<b>Ejercicio &nbsp;<%=ejDesde %>&nbsp;-&nbsp;<%=ejHasta %></b>
</p>
<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />
<%} %>

<div id="helpConcepto" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
	Concepto: Se completa sólo cuando se quiere efectuar una búsqueda que filtre por el descripción del concepto de AFIP o parte del mismo. Tomará sólo las cuentas del ejercicio que se indique. Luego de ingresar el texto, se deberá seleccionar el botón "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpCodigo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
	Código: Se completa sólo cuando se quiere efectuar una búsqueda que filtre por el código de concepto de AFIP o parte del mismo. Tomará sólo las cuentas del ejercicio que se indique. Luego de ingresar el texto, se deberá seleccionar el botón "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>

<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Se indica el ejercicio en el cual se desean buscar los conceptos de AFIP. Luego, se deberá seleccionar el botón "Buscar" para visualizar el resultado.
</div>
<div id="helpBuscar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Buscar: De acuerdo a los parámetros previos, seleccionando este botón, se ejecuta la búsqueda de registros coincidentes. El resultado se visualiza en el cuadro inferior de esta pantalla.
</div>
<div id="helpAlta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Alta Concepto: Seleccionando este botón, se abrirá la pantalla de alta de un nuevo concepto.
</div>
<div id="helpConceptoAfip" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto AFIP: Es el concepto definido por AFIP para identificar un tipo de aporte o contribución que se detalla en el archivo quincenal de nominas.
</div>
<div id="helpDescripcion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripción: Es la descripción que informa AFIP para el concepto que se trate.
</div>
<div id="helpConceptoHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto: es el concepto de ingreso de la tabla propia que se establece como equivalencia para el concepto de AFIP.
</div>
<div id="helpDesde" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Desde: Es la fecha de inicio de la vigencia del concepto AFIP y su equivalencia. Dentro de un mismo ejercicio se podrán asignar distintas equivalencias para diferentes períodos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio.
</div>
<div id="helpHasta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Hasta: Es la fecha de finalización de la vigencia del concepto AFIP y su equivalencia. Dentro de un mismo ejercicio se podrán asignar distintas equivalencias para diferentes períodos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio que se trate.
</div>
<div id="helpEditar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Editar: Se selecciona en el caso que se desee efectuar cambios sobre algún dato de un registro. Se abrirá una nueva pantalla de actualización.
</div>


<script type="text/javascript">
function editarConcepto(conc, id, dd, hasta){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_concepto_afip'
	+  '&id=' +id + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta);
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function altaConcepto(){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_concepto_afip';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function buscarConceptos(){
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/equivalencias_conceptos_afip" /></portlet:actionURL>';
	submitForm(document.busqueda_conceptos, url);
}

</script>
