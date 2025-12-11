<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>
<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentas" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.util.DateUtils" %>
<%@ page import="ar.com.ospim.tesoreria.beans.FechaCierre" %>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
	
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

	boolean rolABM = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_CONTABILIDAD);
	boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
%>
<fieldset class="block-labels" style="width: 80%">
<legend>Fecha cierre contable para movimientos de gestión<a href="javascript:void(0)" onclick="help(event, 'helpCierreGestion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></legend>
<%if (rolABM && !soloVer){  %>
<input type="button" value="Agregar" onclick="agregarCierreGestion()"/><a href="javascript:void(0)" onclick="help(event, 'helpAgregarGestion')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
<%} %>
<%
		List<FechaCierre> fechasGestion= (List<FechaCierre>)renderRequest.getAttribute("fechasCierreContableGestion");
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Fecha <a href='javascript:void(0)' onclick='help(event, \"helpFechaGestion\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Descripcion <a href='javascript:void(0)' onclick='help(event, \"helpDescripcionGestion\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		if (rolABM){ 
			headerNames.add("Baja <a href='javascript:void(0)' onclick='help(event, \"helpBajaGestion\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		}
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-fechas-were-found"));
	
		if(null!=fechasGestion){
	 								 	
	 		//Seteo el total de la lista.
		 	int total = fechasGestion.size();
		 	searchContainer.setTotal(total);

		 	List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < fechasGestion.size(); i++) {
		 		FechaCierre fecha = (FechaCierre) fechasGestion.get(i);
				ResultRow row = new ResultRow(fecha, i, i);
 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
 				
 				row.addText(fecha.getFechaString());
 				row.addText(fecha.getObservacion());
 				if (rolABM && i == 0){ 
 					row.addText("<a href=\"javascript:void(0)\" onclick=\"eliminarFechaGestion('"+fecha.getFechaString()+"')\">Borrar</a>");
 				} else {
 					row.addText("");
 				}
				resultRows.add(row);
		 	}
	 	}
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
</fieldset>
<br/>
<fieldset class="block-labels" style="width: 80%">
<legend>Fecha cierre contable para generación de asientos<a href="javascript:void(0)" onclick="help(event, 'helpCierreContable')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a></legend>
<table style="width: 100%" id="detalle_asientos">

<%if (rolABM && !soloVer){  %>
<input type="button" value="Agregar" onclick="agregarCierreAsientos()"/><a href="javascript:void(0)" onclick="help(event, 'helpAgregarContable')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>
<%} %>
<%

		List<String> headerNames2 = new ArrayList<String>();
		headerNames2.add("Fecha <a href='javascript:void(0)' onclick='help(event, \"helpFechaContable\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
		headerNames2.add("Descripcion <a href='javascript:void(0)' onclick='help(event, \"helpDescripcionContable\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
		if (rolABM){ 
			headerNames2.add("Baja <a href='javascript:void(0)' onclick='help(event, \"helpBajaContable\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
		}
		
		List<FechaCierre> fechasAsientos= (List<FechaCierre>)renderRequest.getAttribute("fechasCierreContableAsientos");
		SearchContainer searchContainer2 = new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames2,
		LanguageUtil.get(pageContext, "no-fechas-were-found"));
	
		if(null!=fechasAsientos){
	 								 	
	 		//Seteo el total de la lista.
		 	int total = fechasAsientos.size();
		 	searchContainer2.setTotal(total);

		 	List resultRows = searchContainer2.getResultRows();
		 	for (int i = 0; i < fechasAsientos.size(); i++) {
		 		FechaCierre fecha = (FechaCierre) fechasAsientos.get(i);
				ResultRow row = new ResultRow(fecha, i, i);
 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
 				
 				row.addText(fecha.getFechaString());
 				row.addText(fecha.getObservacion());
 				if (rolABM && i == 0){ 
 					row.addText("<a href=\"javascript:void(0)\" onclick=\"eliminarFechaAsiento('"+fecha.getFechaString()+"')\">Borrar</a>");
 				} else {
 					row.addText("");
 				}
				resultRows.add(row);
		 	}
	 	}
%>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer2 %>" />
</fieldset>			 

<div id="helpCierreGestion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cierre Gestión: La fecha de cierre de gestión es establecida para que no pueda efectuarse transacción alguna en los módulos de ingresos, liquidaciones, egresos y bancos con una fecha anterior a la indicada. Se genera en forma automática el día 15 de cada mes, con el último día del mes anterior. También, se genera automáticamente cuando se ejecuta la generación de asientos automáticos de un período determinado. Puede ser modificada o eliminada de acuerdo a las necesidades y/o agregada manualmente en cualquier momento. Se listan en el cuadro inferior de mayor a menor ya que, la que se toma para esta validación es la mayor ingresada. Es decir, la primer fecha que aparece en dicho cuadro.
</div>
<div id="helpAgregarGestion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Agregar: seleccionando este botón se puede agregar manualmente una fecha de cierre de gestión.
</div>
<div id="helpFechaGestion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Fecha: Es la fecha hasta la cual no puede efectuarse transacción alguna en los módulos de ingresos, liquidaciones, egresos y bancos.
</div>
<div id="helpDescripcionGestion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripción: Para los registros generados manualmente, es un texto descriptivo ingresado por el usuario que definió la fecha de cierre. En los automáticos es establecido por el sistema, dependiendo el proceso que la generó.
</div>
<div id="helpBajaGestion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Baja: Se elimina una fecha de cierre. La restricción de transacciones se efectuará, luego, hasta la siguiente fecha indicada.
</div>
<div id="helpCierreContable" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Cierre Contable: La fecha de cierre contable es establecida para que no pueda efectuarse transacción alguna en los asientos de la contabilidad con una fecha anterior a la indicada. Debe ser indicada por el usuario del módulo contable y tiene por objeto evitar que por error, se regeneren asientos automáticos o se modifiquen o agreguen asientos manuales sobre un período que ya se encuentra controlado, cerrado e informado. Puede ser modificada o eliminada de acuerdo a las necesidades. Se listan en el cuadro inferior de mayor a menor ya que, la que se toma para esta validación es la mayor ingresada. Es decir, la primer fecha que aparece en dicho cuadro.
</div>
<div id="helpAgregarContable" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Agregar: seleccionando este botón se puede agregar una nueva fecha de cierre contable.
</div>
<div id="helpFechaContable" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Fecha: Es la fecha hasta la cual no puede efectuarse transacción alguna en los asientos del módulos contable.
</div>
<div id="helpDescripcionContable" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripción: Es un texto descriptivo ingresado por el usuario que definió la fecha de cierre.
</div>
<div id="helpBajaContable" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Baja: Se elimina una fecha de cierre. La restricción de transacciones se efectuará, luego, hasta la siguiente fecha indicada.
</div>


<script type="text/javascript">
	function agregarCierreGestion(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_fecha_cierre_gestion';
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location = url;
	}
	
	function agregarCierreAsientos(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_fecha_cierre_asientos';
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location = url;
	}
	
	function eliminarFechaAsiento(fecha){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/eliminar_fecha_cierre_asientos';
		url += '&fecha=' + escape(fecha);
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location = url;
	}
	
	function eliminarFechaGestion(fecha){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/eliminar_fecha_cierre_gestion';
		url += '&fecha=' + escape(fecha);
		url += '&rnd=' + Math.floor(Math.random()*100);
		window.location = url;
	}
</script>

