<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.global.beans.Concepto"%>
<%@ page import="ar.com.ospim.global.beans.PrestacionConcepto"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%
	boolean rolABMEquivalencias = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS);
	boolean rolABMNomenclador = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_EQUIVALENCIAS_PRESTACIONES);
%>
<liferay-ui:error exception="<%= ConceptoUtilizadoException.class %>" message="prestacion-concepto-utilizado" />
<portlet:defineObjects />
<form action="" method="POST" id="busqueda_conceptos"
	name="busqueda_conceptos">
	<table style="width: 100%">
		<tr>
			<td colspan="2"><b>Buscar Prestaciones</b></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;Ejercicio: &nbsp;
				<select name="ejercicio" id="ejercicio">
					<%	Calendar cal = Calendar.getInstance();
						int hastaAnio = cal.get(Calendar.YEAR);
						if (cal.get(Calendar.MONTH) < Calendar.AUGUST){
							hastaAnio--;
						}
						for (int i = 2000; i<=hastaAnio; i++){  %>
						<option value="<%=i%>-<%=i+1%>" <%if (i == hastaAnio) { %> selected="selected"<%} %>>Agosto&nbsp;<%=i %>&nbsp;-&nbsp;Julio&nbsp;<%= i+1 %></option>
					<%} %>
				</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				Código:&nbsp;<input type="text" id="codigo" name="codigo" value="" size="10" /> <a href="javascript:void(0)" onclick="help(event, 'helpCodigo')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp; 
				Descripción:&nbsp;<input type="text" id="descripcion" name="descripcion" value="" size="50" /> <a href="javascript:void(0)" onclick="help(event, 'helpDescripcion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			</td>
			
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2"><input type="button" onclick="buscarConceptos()" value="Buscar" /><a href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
				<% if (rolABMEquivalencias || rolABMNomenclador) {%>
					<input type="button" onclick="altaPrestacion()" value="Alta Prestacion" /><a href="javascript:void(0)" onclick="help(event, 'helpAlta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
				<% }%>
				<br /> <a href="javascript:void(0)" onclick="bajarListadoCompleto()">Bajar listado completo</a><a href="javascript:void(0)" onclick="help(event, 'helpBajar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></td>
		</tr>
	</table>
</form>
<hr />
<br />
<%
		String ejDesde = (String)request.getAttribute("ejercicio_desde");
		String ejHasta = (String)request.getAttribute("ejercicio_hasta");
					
		List<PrestacionConcepto> conceptos = (List<PrestacionConcepto>) request.getAttribute("prestacionConceptos");
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Codigo Prest. <a href='javascript:void(0)' onclick='help(event, \"helpCodigoHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Prest. <a href='javascript:void(0)' onclick='help(event, \"helpPrestHeader\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Concepto Hon Amb <a href='javascript:void(0)' onclick='help(event, \"helpConceptoHA\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Concepto Hon Int <a href='javascript:void(0)' onclick='help(event, \"helpConceptoHI\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Concepto Gastos Amb <a href='javascript:void(0)' onclick='help(event, \"helpConceptoGA\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Concepto Gastos Int <a href='javascript:void(0)' onclick='help(event, \"helpConceptoGI\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Coef Gastos <a href='javascript:void(0)' onclick='help(event, \"helpCoefGastos\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Coef Honorarios <a href='javascript:void(0)' onclick='help(event, \"helpCoefHonorarios\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Desde <a href='javascript:void(0)' onclick='help(event, \"helpDesde\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Hasta <a href='javascript:void(0)' onclick='help(event, \"helpHasta\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		 if (rolABMEquivalencias || rolABMNomenclador) {
	 		headerNames.add("Editar <a href='javascript:void(0)' onclick='help(event, \"helpEditar\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
	 		headerNames.add("Eliminar <a href='javascript:void(0)' onclick='help(event, \"helpEliminar\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		 }
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-conceptos-were-found"));
	
		if (conceptos != null){ 
		 	int total = 1;
			searchContainer.setTotal(total);
				List resultRows = searchContainer.getResultRows();
	
			int i = 0;
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaDesde = format.parse(ejDesde);
			Date fechaHasta = format.parse(ejHasta);
			for (PrestacionConcepto concepto: conceptos){
				i++;
				ResultRow row = new ResultRow(i, i, i);
				row.addText(concepto.getPrestacion().getCodigo());
				row.addText(concepto.getPrestacion().getDescripcion());
				if (concepto.getHonorariosAmbulatorio() != null){
					row.addText(concepto.getHonorariosAmbulatorio().getDescripcion());
				} else {
					row.addText("");
				}
				
				if (concepto.getHonorariosInternacion() != null){
					row.addText(concepto.getHonorariosInternacion().getDescripcion());
				} else {
					row.addText("");
				}
				
				if (concepto.getGastosAmbulatorio() != null){
					row.addText(concepto.getGastosAmbulatorio().getDescripcion());
				} else {
					row.addText("");
				}
				
				if (concepto.getGastosInternacion() != null){
					row.addText(concepto.getGastosInternacion().getDescripcion());
				} else {
					row.addText("");
				}
				
				if (concepto.getCoeficienteGastos() != null){
					row.addText(concepto.getCoeficienteGastos().toString());
				} else {
					row.addText("");
				}
				
				if (concepto.getCoeficienteHonorarios() != null){
					row.addText(concepto.getCoeficienteHonorarios().toString());
				} else {
					row.addText("");
				}
				
				String dd = ejDesde;
				String hta = ejHasta;
				
				if (concepto.getValidoDesdeGastosAmbulatorio() != null && DateUtils.compararFechasTruncarEnDia(concepto.getValidoDesdeGastosAmbulatorio(), fechaDesde) >= 0){
					dd = concepto.getValidoDesdeGastosAmbulatorioString();
				}
				row.addText(dd);
				
				if (concepto.getValidoHastaGastosAmbulatorio() != null && DateUtils.compararFechasTruncarEnDia(concepto.getValidoHastaGastosAmbulatorio(), fechaHasta) <= 0){
					hta = concepto.getValidoHastaGastosAmbulatorioString();
				}
				row.addText(hta);
				String ddOrig = dd;
				if (concepto.getValidoDesdeGastosAmbulatorio() != null){
					ddOrig = concepto.getValidoDesdeGastosAmbulatorioString();
				}
				 if ((rolABMEquivalencias) || (rolABMNomenclador && concepto.getIdTipoNomenclador() == 9)) {
					row.addText("<a href='javascript:void(0)' onclick=\"editarConcepto("+ concepto.getPrestacion().getId()+", '"+ddOrig+"','"+dd+"','"+ hta+"')\">Editar</a>");
					
					if ((concepto.getIdTipoNomenclador() == 3 && rolABMEquivalencias) || (rolABMNomenclador && concepto.getIdTipoNomenclador() == 9)) {
						row.addText("<a href='javascript:void(0)' onclick='eliminarConcepto("+ concepto.getPrestacion().getId() +")'>Eliminar</a>");
					} else {
						row.addText("No es nomenc. propio");
					}
				 }
				resultRows.add(row);
			}
		}
		
%>
<p><b>Ejercicio &nbsp;<%=ejDesde %>&nbsp;-&nbsp;<%=ejHasta %></b></p>	
<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />

<div id="helpCodigo" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Código: Se completa sólo cuando se quiere efectuar una búsqueda que filtre por un código del nomenclador o parte del mismo. Tomará sólo las prestaciones del ejercicio que se indique. Luego de ingresar el texto, se deberá seleccionar el botón "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpDescripcion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripción: Se completa sólo cuando se quiere efectuar una búsqueda que filtre por la descripción de una prestación del nomenclador o parte de la misma. Tomará sólo las prestaciones del ejercicio que se indique. Luego de ingresar el texto, se deberá seleccionar el botón "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Se completa sólo cuando se quiere efectuar una búsqueda. Se indica el ejercicio en el cual se desea buscar. Luego, se podrá, opcionalmente, filtrar por alguno de los datos que le siguen. Se deberá seleccionar el botón "Buscar" para visualizar el resultado en el  cuadro inferior de esta pantalla.
</div>
<div id="helpBuscar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Buscar: De acuerdo a los parámetros previos, seleccionando este botón, se ejecuta la búsqueda de registros coincidentes. El resultado se visualiza en el cuadro inferior de esta pantalla.
</div>
<div id="helpAlta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Alta Prestación: Seleccionando este botón, se abrirá la pantalla de alta de una nueva prestación y sus equivalencias.
</div>
<div id="helpBajar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Bajar listado completo: Seleccionando este botón, se generará una planilla de cálculo con el nomenclador completo y sus equivalencias.
</div>
<div id="helpCodigoHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Código Prest.: Código asignado a la prestación, según nomenclador nacional, nomenclador propio, etc.
</div>
<div id="helpPrestHeader" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Prest.: Descripción de la prestación.
</div>
<div id="helpConceptoHA" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto Hon Amb: es la equivalencia para la prestación con el concepto de egreso que le corresponda, pero sólo para el importe de los honorarios y en el caso de efectuarse la práctica en forma ambulatoria.
</div>
<div id="helpConceptoHI" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto Hon Int: es la equivalencia para la prestación con el concepto de egreso que le corresponda, pero sólo para el importe de los honorarios y en el caso de efectuarse la práctica por una internación.
</div>
<div id="helpConceptoGA" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto Gastos Amb: es la equivalencia para la prestación con el concepto de egreso que le corresponda, pero sólo para el importe de los gastos y en el caso de efectuarse la práctica en forma ambulatoria.
</div>
<div id="helpConceptoGI" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto Gastos Int: es la equivalencia para la prestación con el concepto de egreso que le corresponda, pero sólo para el importe de los gastos y en el caso de efectuarse la práctica por una internación.
</div>
<div id="helpCoefGastos" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Coef Gastos: es el coeficiente que da la proporción a asignar como gasto, sobre el importe total de la prestación.
</div>
<div id="helpCoefHonorarios" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Coef Honorarios: es el coeficiente que da la proporción a asignar como honorarios, sobre el importe total de la prestación.
</div>
<div id="helpDesde" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Desde: Es la fecha de inicio de la vigencia de la equivalencia establecida para la prestación que se trate. Dentro de un mismo ejercicio se podrán asignar distintas equivalencias para diferentes períodos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio.
</div>
<div id="helpHasta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Hasta: Es la fecha de finalización de la vigencia de la equivalencia establecida para la prestación que se trate. Dentro de un mismo ejercicio se podrán asignar distintas equivalencias para diferentes períodos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio que se trate.
</div>
<div id="helpEditar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Editar: Se selecciona en el caso que se desee efectuar cambios sobre algún dato de un registro. Se abrirá una nueva pantalla de actualización.
</div>
<div id="helpEliminar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Eliminar: Se selecciona en el caso que se desee un registro. No podrá borrarse un registro que fuera utilizado en alguna tabla o transacción del sistema.
</div>



<script type="text/javascript">
function editarConcepto(id, ddOriginal, dd, hasta){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_equivalencias_prestaciones_conceptos'
		+  '&id=' +id + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta) + '&ejercicio_desde_original=' + escape(ddOriginal);
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}

function altaPrestacion(id){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/editar_equivalencias_prestaciones_conceptos';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function buscarConceptos(){
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/equivalencias_prestaciones_conceptos" /></portlet:actionURL>';
	submitForm(document.busqueda_conceptos, url);
}

function bajarListadoCompleto(){
	window.location.href ="/xlsservlet/?reporte=equivalencia_prestacion_concepto&ejercicio=" + document.getElementById("ejercicio").value;
}

function eliminarConcepto(id){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/tesoreria/eliminar_equivalencias_prestaciones_conceptos'
	+  '&id=' +id;
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
</script>
