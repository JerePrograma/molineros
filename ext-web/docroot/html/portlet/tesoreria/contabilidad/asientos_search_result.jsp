<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.Asiento"%>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<liferay-ui:error
	exception="<%= FechaMenorACierreContableException.class %>"
	message="asiento-menor-fecha-contable" />

<portlet:defineObjects />
<%
boolean soloVer = PermissionUtil.userContainsRole(user,"CONTABILIDAD_SOLO_VER");
//boolean soloVer = PermissionUtil.userContainsRole(user,WebKeysGlobal.SOLO_VER);
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
	
if (request.getAttribute("ejercicio_desordenado") != null && ((String)request.getAttribute("ejercicio_desordenado")).equals("ejercicio_desordenado")){ %>
<span><p style="color: black; background-color: #FFC6B3;">
		<b>La numeración de los asientos del ejercicio se encuentra
			desordenada: <a href="javascript:void(0)" onclick="ordenarAsientos()">Ordenar</a>
		</b>
	</p></span>
<%} %>
<%
		Date fechaCierreAsientos = (Date) portletSession
				.getAttribute("fecha_cierre_asientos",
						PortletSession.APPLICATION_SCOPE);
		
		List<Asiento> asientos=(List<Asiento>) portletSession
				.getAttribute(WebKeysTesoreria.BUSQUEDA_ASIENTOS_EN_SESSION,
					PortletSession.APPLICATION_SCOPE);
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Fecha <a href='javascript:void(0)' onclick='help(event, \"helpFecha\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Número <a href='javascript:void(0)' onclick='help(event, \"helpNumero\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
 		headerNames.add("Descripcion <a href='javascript:void(0)' onclick='help(event, \"helpDescripcion\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
		headerNames.add("Automático <a href='javascript:void(0)' onclick='help(event, \"helpAutomatico\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
		headerNames.add("Editar/Borrar <a href='javascript:void(0)' onclick='help(event, \"helpEditarBorrar\")'><img style=\"height: 16px; width: 16px\" src=\"/html/images/help.png\" title=\"Ayuda\" alt=\"Ayuda\"/></a>");
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-asientos-were-found"));
	
		if(null!=asientos){
	 								 	
	 		//Seteo el total de la lista.
		 	int total = asientos.size();
		 	searchContainer.setTotal(total);

		 	List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < asientos.size(); i++) {
		 		Asiento asiento = (Asiento) asientos.get(i);
				ResultRow row = new ResultRow(asiento, asiento.getId(), i);
 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
 				String struts_action="/"+portlet_name+"/view_asientos_entry";
 				if(portlet_name.equals("farmacia")){
 					rowURL.setParameter("amtima","true");
 				}
				rowURL.setParameter("struts_action",struts_action);
				rowURL.setParameter("asiento_id", String.valueOf(asiento.getId()));
 				row.addText(asiento.getFechaString(), rowURL);
 				row.addText(String.valueOf(asiento.getNro()), rowURL);
 				row.addText(asiento.getDescripcion(), rowURL);
 				row.addText(asiento.isAutomatico() ? "X" : "", rowURL);
 				if (!soloVer && null!=asiento && null!=fechaCierreAsientos && null!=asiento.getFecha()  && DateUtils.compararFechasTruncarEnDia(fechaCierreAsientos, asiento.getFecha()) < 0){
					row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/tesoreria/contabilidad/editar_borrar_asiento.jsp");
 				} else {
 					row.addText("");
 				}
				resultRows.add(row);
		 	}
	 	}
%>
<div id="helpFecha" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Fecha: Es la fecha asignada al asiento.
</div>
<div id="helpNumero" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Número: Indica el número de asiento. Como este número debe ser correlativo por fecha, en el caso de agregarse un asiento que no respete esta correlatividad, se informará en pantalla que los asientos del ejercicio no están ordenados, dejando optar al usuario para renumerarlos.
</div>
<div id="helpDescripcion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Descripción: En el caso de los asientos automáticos es la descripción asignada por el proceso de generación de los distintos asientos. Se agrega la palabra "automático" al final del mismo. En el caso de asientos manuales, es el texto que el usuario ingrese.
</div>
<div id="helpAutomatico" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Automático: Identifica si el asiento es automático o manual.
</div>
<div id="helpEditarBorrar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
Editar/Borrar: Se utiliza eliminar el asiento o modificarlo, abriendo una nueva pantalla. Esta opción no se activa para los asientos automáticos que podrán regenerarse las veces que se desee.
</div>


<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />

<script type="text/javascript">
function ordenarAsientos(){
	var ejercicio=jQuery('#ejercicio').val();
	
	jQuery('#<portlet:namespace />buscando').show();
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/ordenar_asientos';
	 url += '&ejercicio=' + escape(ejercicio);
	 url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery('#<portlet:namespace />busquedaAsientosDiv').load(url, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );	
}
</script>
