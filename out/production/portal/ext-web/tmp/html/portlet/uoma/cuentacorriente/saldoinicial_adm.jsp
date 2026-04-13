<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<portlet:defineObjects />

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(WindowState.MAXIMIZED);
	//portletURL.setParameter("struts_action", "/tesoreria/view");
	portletURL.setParameter("struts_action", "/uoma/cuentacorriente/view");

	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();
	
	String portlet_name="uoma";
	
		
	boolean popup=ParamUtil.getBoolean(request, "popup", false);
	
	//boolean showABMButtonsADM = PermissionUtil.userContainsRole(user,WebKeysSaldoInicial.ROL_ADMINISTRADOR_SaldoInicial);
    boolean showABMButtonsADM = true; 
%>

<form action="" method="get" name="<portlet:namespace />fm">

	<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

	<fieldset class="block-labels">
		<legend>
			<liferay-ui:message key="ctacte-saldoinicial" />			
		</legend>
		<table class="lfr-table">
			<tr>
				<td><label><liferay-ui:message key="empresa" />:</label></td>
				<td colspan="6">
					<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
				  		<liferay-util:param name="esEditable" value='true'/>
				  		<liferay-util:param name="portlet_name" value='tesoreria'/>
				  		<liferay-util:param name="cuit" value=''/>
					</liferay-util:include>
				</td>
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
					onClick="javascript: <portlet:namespace />buscarSaldoInicial();"
					type="button" /> <%if(portlet_name.equalsIgnoreCase("uoma")){%> <a
					href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img
						style="height: 16px; width: 16px" src="/html/images/help.png"
						title="Ayuda" alt="Ayuda" /></a>&nbsp; <%}%> <%}%> <% if(showABMButtonsADM) { %>
					<input type="button" value="Nuevo"
					onClick="<portlet:namespace />nuevoSaldoInicial();" /> <%}%> <input
					type="button" value="Limpiar"
					onClick="<portlet:namespace />Limpiar();" />&nbsp;
				</td>
			</tr>
		</table>

		<div id='divCajaChica' style="float: left;"></div>
	</fieldset>
	<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscandoSaldoInicial">
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
		<div id="<portlet:namespace />listado_SaldoInicial">
			<jsp:include
				page='/html/portlet/tesoreria/caja_chica/caja_chica_result.jsp' />
		</div>
	</fieldset>

</form>


<script type="text/javascript">

	
	jQuery('#<portlet:namespace />buscandoSaldoInicial').hide();		
	var autorizacionGenerada;
	var popupMD;
		
	<portlet:namespace />buscarSaldoInicial();
	
	function <portlet:namespace />Limpiar() {
		jQuery('#<portlet:namespace />cuit_entidad').val("");			
		jQuery('#<portlet:namespace />sucursal_entidad').val("");
	}
	
	function <portlet:namespace />nuevoSaldoInicial() {
		
		var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
		var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
		
		if ((sucursal_entidad.length == 0) || (cuit_entidad.length == 0)) {
			alert("Debe ingresar Empresa para Alta de Saldo Inicial");
			return;
		}
	 	
		var params = "&<%= Constants.CMD %>=" + "NEW";		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_saldoinicial';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	}

		
	function <portlet:namespace />buscarSaldoInicial(){
				
		var cuit_entidad=jQuery("#<portlet:namespace/>cuit_entidad").val();	
		var sucursal_entidad=jQuery("#<portlet:namespace/>sucursal_entidad").val();
		
		// Pacificador Show
		jQuery('#<portlet:namespace />buscandoSaldoInicial').show();		
		// Json params busqueda
	 	var busquedaSaldoInicial = {"cuit":cuit_entidad,"sucursal":sucursal_entidad};	 	
	 	// Arma URL
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_saldoinicial';
	 	
	 	// alert(url);
	 	
	 	// Ajax Call
		jQuery('#<portlet:namespace />listado_SaldoInicial').load(url,busquedaSaldoInicial, function(){
															jQuery('#<portlet:namespace />buscandoSaldoInicial').hide();      
														  });	
	}
</script>

