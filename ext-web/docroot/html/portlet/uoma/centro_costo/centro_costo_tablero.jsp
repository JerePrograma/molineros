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
	
	boolean popup=ParamUtil.getBoolean(request, "popup", false);
	
%>

<form action="" method="get" name="<portlet:namespace />fm"> 
   
   <liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />

	<fieldset class="block-labels">
		<legend><liferay-ui:message key="centro-costo" /></legend>
		<table class="lfr-table">	
			<tr>
			  <td><label><liferay-ui:message key="id"/>:</label></td>
			  <td><input id="<portlet:namespace />idCentro_filtro" name="<portlet:namespace />idCentro_filtro" size="10" maxlength="10" type="text" value=''/></td>
			  <td><label><liferay-ui:message key="descripcion"/>:</label></td>
			  <td><input id="<portlet:namespace />descripcionCentro_filtro" name="<portlet:namespace />descripcionCentro_filtro" size="70" maxlength="200" type="text" value=''/></td>
			
		    </tr> 
			  
			<tr><td colspan="9">&nbsp;</td></tr>
			   
		</table>
	
		<table>
				 <tr align="left">
				    <td>&nbsp;</td>
					<td align="left" width="100%">	
					    <input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />"
						onClick="javascript: <portlet:namespace />buscarCentro();"
						type="button" />&nbsp;
						<input type="button" value="Limpiar" onClick="<portlet:namespace />initDateFields();" />&nbsp;
						<input type="button" value="Exportar" onClick="<portlet:namespace />exportarCentrosCosto();" />&nbsp;
					</td>
				 </tr>
		</table>
		
		<div id='divCentro' style="float:left;">
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
		<div id="<portlet:namespace />listado_centro">
  		<jsp:include page='/html/portlet/uoma/centro_costo/centro_costo_tablero_result.jsp' />
		</div>
	</fieldset>

</form>		



<script type="text/javascript">

	
	jQuery('#<portlet:namespace />buscandoCC').hide();		
	var autorizacionGenerada;
	var popupMD;
	
	
	
	<portlet:namespace />initDateFields();

	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />descripcionCentro_filtro').val("");
	}
	
	function <portlet:namespace />buscarCentro(){
		var descripcion= jQuery('#<portlet:namespace />descripcionCentro_filtro').val();
		var id= jQuery('#<portlet:namespace />idCentro_filtro').val();
		jQuery('#<portlet:namespace />buscandoCC').show();
		
	 	var busquedaCentroCosto = {"descripcioncentro":descripcion,"entidadcentro":<%=entidad%>,"id":id};
	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/centro_costo_edicion';
	 	url += "&<%= Constants.CMD %>=" + "filtrarTablero";
	 	
		jQuery('#<portlet:namespace />listado_centro').load(url,busquedaCentroCosto, function(){
															jQuery('#<portlet:namespace />buscandoCC').hide();      
														  });	
	}
	
	function <portlet:namespace />exportarCentrosCosto(){
 		window.location.href ='/xlsservlet/?reporte=REPORTE_CENTROS_COSTOS';
	}
	
</script>

