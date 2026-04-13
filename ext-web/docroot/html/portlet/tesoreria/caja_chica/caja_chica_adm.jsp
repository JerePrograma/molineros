<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

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
	
	boolean showABMButtonsADM = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA);
	
	boolean showABMButtonsUSR = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_USUARIO_CAJA_CHICA);
	
	boolean showABMButtonsADMSINOP = PermissionUtil.userContainsRole(user,WebKeysCajaChica.ROL_ADMINISTRADOR_CAJA_CHICA_SIN_OP);
%>

<form action="" method="get" name="<portlet:namespace />fm"> 
   
   <liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

	<fieldset class="block-labels">
		<legend><liferay-ui:message key="caja-chica" /></legend>
		<table class="lfr-table">	
			<tr>
			  <td><label><liferay-ui:message key="descripcion"/>:</label></td>
			  <td><input id="<portlet:namespace />descripcionCChica_filtro" name="<portlet:namespace />descripcionCChica_filtro" size="70" maxlength="200" type="text" value=''/></td>
			
			  <td>Estado <select name="<portlet:namespace />estadoCChica_filtro"
			                   id="<portlet:namespace />estadoCChica_filtro" 
			                   onchange="">
			                <option value="">Seleccione estado</option>
			                <%for(int i = 0; i < WebKeysCajaChica.ESTADO_CAJA_CHICA.length; i++ ) {%>
				            <option value="<%=WebKeysCajaChica.ESTADO_CAJA_CHICA[i][0] %>" 
					          > <%=WebKeysCajaChica.ESTADO_CAJA_CHICA[i][1] %> </option>
				            <% } %>
		             </select>
		        </td>
			  </tr> 
			  
			  <tr><td colspan="9">&nbsp;</td></tr>
			   
			  <tr>	
				<td>
					<liferay-ui:message key="concepto" />
				</td>
				<td>
					<select name="<portlet:namespace/>conceptoCChica_filtro" id="<portlet:namespace/>conceptoCChica_filtro">
						<option value="0">Seleccione un concepto</option>
						<%	for (Concepto tnom : conceptos) { %>
								<option value="<%= tnom.getId() %>"><%=tnom.getDescripcion() %></option>
						<%	} %>
					</select>
				</td>
				
				<tr><td colspan="9">&nbsp;</td></tr>
				
		   </tr>
		</table>
	
		<table>
				 <tr align="left">
				    <td>&nbsp;</td>
					<td align="left" width="100%">	
					  	<% if(showABMButtonsADM || showABMButtonsUSR || showABMButtonsADMSINOP) { %>					
						<input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />"
						onClick="javascript: <portlet:namespace />buscarCajaChica();"
						type="button" />
						    <%if(portlet_name.equalsIgnoreCase("uoma")){%>
						       <a href="javascript:void(0)" onclick="help(event, 'helpBuscar')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
						    <%}%>
						<%}%>
						<% if(showABMButtonsADM || showABMButtonsADMSINOP) { %>	
						<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevaCajaChica();" />
						<%}%>
						<input type="button" value="Limpiar" onClick="<portlet:namespace />initDateFields();" />&nbsp;
					</td>
				 </tr>
		</table>
		
		<div id='divCajaChica' style="float:left;">
		</div>
	</fieldset>
	<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscandoCC">
			<table style="align:center;">
				<tr>
					<td><liferay-ui:message key='buscando'/></td>
					<td align="center">					
					<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>		
		</div>	
		<div id="<portlet:namespace />listado_caja_chica">
  		<jsp:include page='/html/portlet/tesoreria/caja_chica/caja_chica_result.jsp' />
		</div>
	</fieldset>

</form>		

<div id="helpBuscar" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
 Al ejecutar esta búsqueda, se exhibirán en pantalla todas las cajas que el usuario tenga habilitadas para su uso. En el caso de las Seccionales, lo más probable es que sólo aparezca una.
</div>
<div id="helpEjecucion" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  Con esta opción 'Eg/Rend', se ingresa a una nueva pantalla donde se podrán ingresar nuevos comprobantes de una rendición o consultar y/o modificar los ya ingresados.
</div>
<div id="helpReporte" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  Con la opción 'reporte', se accede a una nueva pantalla donde se podrá emitir un detalle de los movimientos ingresados.
</div>


<script type="text/javascript">

	
	jQuery('#<portlet:namespace />buscandoCC').hide();		
	var autorizacionGenerada;
	var popupMD;
	
	
	
	<portlet:namespace />initDateFields();

	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />descripcionCChica_filtro').val("");
		jQuery('#<portlet:namespace />estadoCChica_filtro').val("");
		jQuery('#<portlet:namespace />conceptoCChica_filtro').val("");
	}
	
	function <portlet:namespace />nuevaCajaChica() {
		var params = "&<%= Constants.CMD %>=" + "NEW";
		
		//var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/'<%=portlet_name%>'/editar_caja_chica" /></portlet:renderURL>';

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_caja_chica';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);	
	}
	
	function <portlet:namespace />buscarCajaChica(){
		var descripcion= jQuery('#<portlet:namespace />descripcionCChica_filtro').val();
		var estado=jQuery('#<portlet:namespace />estadoCChica_filtro').val();
		var concepto =jQuery('#<portlet:namespace />conceptoCChica_filtro').val();
		jQuery('#<portlet:namespace />buscandoCC').show();
		
	 	var busquedaCajaChica = {"descripcioncajachica":descripcion,"estadocajachica":estado,"conceptocajachica":concepto,"entidadcajachica":<%=entidad%>};
//	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/<%=portlet_name%>/buscar_caja_chica" /></portlet:renderURL>';
	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_caja_chica';
	 	
		jQuery('#<portlet:namespace />listado_caja_chica').load(url,busquedaCajaChica, function(){
															jQuery('#<portlet:namespace />buscandoCC').hide();      
														  });	
	}
	
</script>

