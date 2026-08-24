<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.TipoMovBcrio"%>
<%@ page import="ar.com.ospim.liquidaciones.ConceptoUtilizadoException"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
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
<form action="" method="POST" id="busqueda_tipos"
	name="busqueda_tipos">
	<table style="width: 100%">
		<tr>
			<td colspan="2"><b>Tipos de Movimientos Bancarios</b></td>
		</tr>
		<tr>
			<td colspan="2"> &nbsp;Ejercicio:&nbsp; <select name="ejercicio" id="ejercicio">
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
					<%}} %>
			</select><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
			<input type="button" onclick="buscarTipos()" value="Buscar" /><a href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp; 
			<% if (rolABMEquivalencias) {%> 
				<input type="button" onclick="altaTipoMov()" value="Alta Tipo Movimiento" /><a href="javascript:void(0)" onclick="help(event, 'helpAlta')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
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
					
		List<TipoMovBcrio> tipos = (List<TipoMovBcrio>) request.getAttribute("tiposMovBcrios");
	//Si debe mostrarse el btn de agregar afiliado								
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Descripcion <a href='javascript:void(0)' onclick='help(event, \"helpDescripcion\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Concepto <a href='javascript:void(0)' onclick='help(event, \"helpConcepto\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Desde <a href='javascript:void(0)' onclick='help(event, \"helpDesde\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Hasta <a href='javascript:void(0)' onclick='help(event, \"helpHasta\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		 if (rolABMEquivalencias) {
	 		headerNames.add("Editar <a href='javascript:void(0)' onclick='help(event, \"helpEditar\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
	 		headerNames.add("Eliminar <a href='javascript:void(0)' onclick='help(event, \"helpEliminar\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
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
		for (TipoMovBcrio tipo: tipos){
			i++;
			ResultRow row = new ResultRow(i, i, i);
			row.addText(tipo.getDescripcion());
			row.addText(tipo.getConcepto() != null ? tipo.getConcepto().getDescripcion() : "");
			
			String dd = ejDesde;
			String hta = ejHasta;
			if (DateUtils.compararFechasTruncarEnDia(tipo.getValidoDesde(), fechaDesde) >= 0){
				dd = tipo.getValidoDesdeString();
			}
			row.addText(dd);
			
			if (DateUtils.compararFechasTruncarEnDia(tipo.getValidoHasta(), fechaHasta) <= 0){
				hta = tipo.getValidoHastaString();
			}
			row.addText(hta);
			
			 if (rolABMEquivalencias) {
				row.addText("<a href='javascript:void(0)' onclick=\"editarTipoMov(" + tipo.getId_tipo_mov() +", '"+tipo.getValidoDesdeString()+"', '"+dd+"','"+ hta+"')\">Editar</a>");
				row.addText("<a href='javascript:void(0)' onclick='eliminarTipoMov(" + tipo.getId_tipo_mov() +")'>Eliminar</a>");
			 }
			resultRows.add(row);
		}
		
%>
<% if (tipos != null && tipos.size()>=1){ %>
<p>
	<b>Ejercicio &nbsp;<%=ejDesde %>&nbsp;-&nbsp;<%=ejHasta %></b><a href="javascript:void(0)" onclick="help(event, 'helpEjercicio2')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
</p>
<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />
<%} %>



<div id="helpEjercicio" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Se indica el ejercicio en el cual se desean buscar los movimientos bancarios. Luego, se deberá seleccionar el botón "Buscar" para visualizar el resultado.
</div>
<div id="helpBuscar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Buscar: Ejecuta la búsqueda para el ejercicio que se indique.
</div>
<div id="helpAlta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Alta Tipo Movimiento: Seleccionando este botón, se abrirá la pantalla de alta de un nuevo movimiento bancario.
</div>
<div id="helpEjercicio2" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Ejercicio: Es el ejercicio al cual pertenecen los registros del cuadro inferior de la pantalla. Es decir, de la última búsqueda efectuada.
</div>
<div id="helpDescripcion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripción: descripción del tipo de movimiento bancario.
</div>
<div id="helpConcepto" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Concepto: concepto asignado al movimiento bancario. En el caso de no asignar un concepto, los movimientos bancarios de este tipo sólo tendrán efecto en el libro banco/caja y no se verán reflejados en los subdiarios ni en los asientos automáticos. Si se asigna un concepto, los movimientos de este tipo se incuilrán, o no, en alguno de los subdiarios, dependiendo de lo que se indique en la tabla de conceptos para el concepto asignado en los campos: "va en subdiario de ingresos" y "va en subdiario de egresos".
</div>
<div id="helpDesde" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Desde: Es la fecha de inicio de la vigencia del tipo de movimiento. Dentro de un mismo ejercicio se podrán asignar distintas equivalencias para diferentes períodos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio.
</div>
<div id="helpHasta" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Hasta: Es la fecha de finalización de la vigencia del tipo de movimiento. Dentro de un mismo ejercicio se podrán asignar distintas equivalencias para diferentes períodos. De esta forma, se evita que los cambios efectuados sean retroactivos a meses ya cerrados del ejercicio que se trate.
</div>
<div id="helpEditar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Editar: Se selecciona en el caso que se desee efectuar cambios sobre algún dato de un registro. Se abrirá una nueva pantalla de actualización.
</div>
<div id="helpEliminar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Eliminar: Se selecciona en el caso que se desee un registro. No podrá borrarse un registro que fuera utilizado en alguna tabla o transacción del sistema.
</div>


<script type="text/javascript">
<%if(null!=ejercicio_seleccionado&&ejercicio_seleccionado.trim().length()>0){%>
jQuery('#ejercicio').val('<%=ejercicio_seleccionado%>');
<%}%>
function editarTipoMov(id, ddOriginal, dd, hasta){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_equivalencias_tipos_mov_bcrios'
	+  '&id=' +id + '&ejercicio_desde=' + escape(dd)+ '&ejercicio_hasta='+ escape(hasta)  + '&ejercicio_desde_original=' + escape(ddOriginal);
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function eliminarTipoMov(id){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/eliminar_equivalencias_tipos_mov_bcrios'
	+  '&id=' +id;
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function altaTipoMov(){
	var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_equivalencias_tipos_mov_bcrios';
	url += '&rnd=' + Math.floor(Math.random()*100);
	window.location = url;
}
function buscarTipos(){
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/equivalencias_tipos_mov_bcrios';	
	submitForm(document.busqueda_tipos, url);
}
</script>
