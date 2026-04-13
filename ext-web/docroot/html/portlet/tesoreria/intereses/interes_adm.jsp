<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.WebKeysInteres"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<portlet:defineObjects />

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(WindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/tesoreria/view");

	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();
	
	Integer entidad = WebKeysGlobal.OSPIM;
	String portlet_name=null;
	portlet_name = "tesoreria";
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		entidad = WebKeysGlobal.UOMA;
		portlet_name = "uoma";
	}
	
		
	
	List<Concepto> conceptos = TraeListasServiceUtil.getConceptos(DateUtils.getDesdeEjercicioActual().getTime(), entidad);
	
	boolean popup=ParamUtil.getBoolean(request, "popup", false);
	
	boolean showABMButtonsADM = PermissionUtil.userContainsRole(user,WebKeysInteres.ROL_ADMINISTRADOR_INTERES);

%>

<form action="" method="get" name="<portlet:namespace />fm">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

	<fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="interes" />
		</legend>
		<table class="lfr-table">
			<tr>			
				<td><label><liferay-ui:message key="fecha-inicio" />:</label> <input
					id="<portlet:namespace />fechaDesdeInteres_filtro"
					name="<portlet:namespace />fechaDesdeInteres_filtro" size="10"
					maxlength="10" type="text" value='' /></td>
				<td><label><liferay-ui:message key="fecha-fin" />:</label> <input
					id="<portlet:namespace />fechaHastaInteres_filtro"
					name="<portlet:namespace />fechaHastaInteres_filtro" size="10"
					maxlength="10" type="text" value='' /></td>
			</tr>
			<tr>
				<td colspan="9">&nbsp;</td>
			</tr>

		</table>

		<table>
			<tr align="left">
				<td>&nbsp;</td>
				<td align="left" width="100%">
					<% if(showABMButtonsADM) { %>
					<input id="<portlet:namespace />buscar"
					value="<liferay-ui:message key="buscar"/>"
					title="<liferay-ui:message key="buscar" />"
					onClick="javascript: <portlet:namespace />buscarInteres();"
					type="button" /> <%if(portlet_name.equalsIgnoreCase("uoma")){%> <a
					href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img
						style="height: 16px; width: 16px" src="/html/images/help.png"
						title="Ayuda" alt="Ayuda" /></a>&nbsp; <%}%> <%}%> <% if(showABMButtonsADM) { %>
					<input type="button" value="Nuevo"
					onClick="<portlet:namespace />nuevoInteres();" /> <%}%> <input
					type="button" value="Limpiar"
					onClick="<portlet:namespace />initDateFields();" />&nbsp;
				</td>
			</tr>
		</table>

		<div id='divCajaChica' style="float: left;"></div>
	</fieldset>
	<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscandoInteres">
			<table style="align: center;">
				<tr>
					<td><liferay-ui:message key='buscando' /></td>
					<td align="center"><img
						alt="<liferay-ui:message key='buscando'/>"
						src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>
		</div>
		<div id="<portlet:namespace />listado_interes">
			<jsp:include
				page='/html/portlet/tesoreria/caja_chica/caja_chica_result.jsp' />
		</div>
	</fieldset>

</form>

<div id="helpBuscar"
	class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}"
	style="top: 200px; left: 300px">Al ejecutar esta búsqueda, se
	exhibirán en pantalla todas las cajas que el usuario tenga habilitadas
	para su uso. En el caso de las Seccionales, lo más probable es que sólo
	aparezca una.</div>
<div id="helpEjecucion"
	class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}"
	style="top: 200px; left: 300px">Con esta opción 'Eg/Rend', se
	ingresa a una nueva pantalla donde se podrán ingresar nuevos
	comprobantes de una rendición o consultar y/o modificar los ya
	ingresados.</div>
<div id="helpReporte"
	class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}"
	style="top: 200px; left: 300px">Con la opción 'reporte', se
	accede a una nueva pantalla donde se podrá emitir un detalle de los
	movimientos ingresados.</div>


<script type="text/javascript">

	
	jQuery('#<portlet:namespace />buscandoInteres').hide();		
	var autorizacionGenerada;
	var popupMD;
	
	jQuery('#<portlet:namespace />fechaDesdeInteres_filtro').datepicker(jQuery.datepicker.regional['es']);
	jQuery('#<portlet:namespace />fechaHastaInteres_filtro').datepicker(jQuery.datepicker.regional['es']);
	
	<portlet:namespace />initDateFields();
	<portlet:namespace />buscarInteres();

	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />fechaDesdeInteres_filtro').val("");
		jQuery('#<portlet:namespace />fechaHastaInteres_filtro').val("");
	}
	
	function <portlet:namespace />nuevoInteres() {
		var params = "&<%= Constants.CMD %>=" + "NEW";		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_interes';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	}

		
	function <portlet:namespace />buscarInteres(){
		var fechaDesde= jQuery('#<portlet:namespace />fechaDesdeInteres_filtro').val();
		var fechaHasta= jQuery('#<portlet:namespace />fechaHastaInteres_filtro').val();
		
		// Pacificador Show
		jQuery('#<portlet:namespace />buscandoInteres').show();		
		// Json params busqueda
	 	var busquedaInteres = {"fechaDesde":fechaDesde,"fechaHasta":fechaHasta,"entidadInteres":<%=entidad%>};	 	
	 	// Arma URL
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_interes';
	 	// Ajax Call
		jQuery('#<portlet:namespace />listado_interes').load(url,busquedaInteres, function(){
															jQuery('#<portlet:namespace />buscandoInteres').hide();      
														  });	
	}
</script>

