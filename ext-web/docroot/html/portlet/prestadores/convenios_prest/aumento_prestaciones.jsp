<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
    
<%
 		boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ADMINISTRACION);
		boolean showAumentoNomenclador = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_AUMENTO_NOMENCLADOR);
		PortletURL portletURL = renderResponse.createRenderURL();
		portletURL.setWindowState(WindowState.MAXIMIZED);
		portletURL.setParameter("struts_action", "/prestadores/view");
		
 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar();
 		periodoDesde.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
 		String usuario_modi = themeDisplay.getUser().getFullName();

if(showAumentoNomenclador) { %>

<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" /> 
<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
		<fieldset class="block-labels">		
				<legend><liferay-ui:message key="aumento-prestaciones" /></legend>
				<table class="lfr-table">
					<tr>
						<td ><label><liferay-ui:message key="vig-aumento" />:</label></td>
						<td>
							<liferay-ui:input-date
								dayParam="vigAumentoDia"
								dayNullable="<%= true %>"
								dayValue="1"
								monthAndYearParam="vigAumentoMesAnio"
								monthValue="<%= periodoDesde.get(Calendar.MONTH) %>"
								monthAndYearNullable="<%= true %>"
								yearValue="<%= periodoDesde.get(Calendar.YEAR) %>"							
								yearRangeStart="<%= periodoDesde.get(Calendar.YEAR) - 1 %>"
								yearRangeEnd="<%= periodoDesde.get(Calendar.YEAR) %>"
								firstDayOfWeek="<%= periodoDesde.getFirstDayOfWeek() - 1 %>"
								disabled="<%= false %>" />
						</td>						
						<td><label><liferay-ui:message key="porc-aumento"/>:</label></td>
						<td><input id="<portlet:namespace />porcentaje" name="<portlet:namespace />porcentaje" size="2" maxlength="2" type="text" value=""/></td>						
						<td><label><liferay-ui:message key="resolucion"/>:</label></td>
						<td><input id="<portlet:namespace />resolucion" name="<portlet:namespace />resolucion" size="20" maxlength="20" type="text" value=""/></td>
						<td><label><liferay-ui:message key="ttos"/></label></td>
						<td><input type="checkbox" id="<portlet:namespace />ttos" name="<portlet:namespace />ttos" value="false"/></td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>								
					</tr>
					<tr>
						<td><label><liferay-ui:message key="nomenclador" />:</label></td>
							<td><select id="<portlet:namespace/>nomenclador" name="<portlet:namespace/>nomenclador">
								<option value="8" >Discapacidad</option>
								<option value="3">Propio</option>
							</select>
						</td>
						<td><label><liferay-ui:message key="cod-desde"/>:</label></td>
						<td><input id="<portlet:namespace />cod_desde" name="<portlet:namespace />cod_desde" size="7" maxlength="7" type="text" value="" /></td>
						<td><label><liferay-ui:message key="cod-hasta"/>:</label></td>
						<td><input id="<portlet:namespace />cod_hasta" name="<portlet:namespace />cod_hasta" size="7" maxlength="7" type="text" value=""/></td>
					</tr>
					<tr>
						<td colspan="8">&nbsp;</td>								
					</tr>			
					<tr>
 					<td><input type="button" id="<portlet:namespace />guardar" value="<liferay-ui:message key="guardar"/>" title="<liferay-ui:message key="guardar" />" /></td>
 					</tr>
				</table>
		</fieldset>
</form>

<script type="text/javascript">
	
jQuery('#<portlet:namespace />guardando').hide();
jQuery('#<portlet:namespace />guardar').click(function(){
	
	var porcentaje = jQuery('#<portlet:namespace/>porcentaje').val();
	var vigAumentoDia=jQuery('#<portlet:namespace />vigAumentoDia').val();		
	var vigAumentoMesAnio=jQuery('#<portlet:namespace />vigAumentoMesAnio').val();
	var resolucion = jQuery('#<portlet:namespace />resolucion').val();
	var ttos=jQuery('#<portlet:namespace />ttos').is(':checked');
	var nomenclador = jQuery('#<portlet:namespace />nomenclador').val();
	var cod_desde = jQuery('#<portlet:namespace />cod_desde').val();
	var cod_hasta = jQuery('#<portlet:namespace />cod_hasta').val();	

	if (isNaN(porcentaje) || porcentaje == "") {
		alert("El Porcentaje no es válido. Prueba de nuevo.");
		porcentaje.select();
		porcentaje.focus();
	}
	
	if (resolucion == "") {
    	alert("Debes cargar la Resolución, Prueba de nuevo.");
    	resolucion.select();
    	resolucion.focus();
    }	
	
	if (isNaN(cod_desde) || cod_desde == "") {
	    alert("El Codigo Desde no es válido. Prueba de nuevo.");
	    cod_desde.select();
	    cod_desde.focus();
	}	
	if (isNaN(cod_hasta) || cod_hasta == "") {
	    	alert("El Codigo Hasta no es válido. Prueba de nuevo.");
	    	cod_hasta.select();
	    	cod_hasta.focus();
	}
	 
	 else{ 
		 
		// Formateo por_aumento 
		while (porcentaje.length<2) {
			porcentaje = '0'+porcentaje;
		} 
		var porciento = "1.";
		var porc_aumento = porciento.concat(porcentaje);
		
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/prestadores/incrementarNomenclador&vigAumentoDia='+vigAumentoDia+'&vigAumentoMesAnio='+vigAumentoMesAnio+'&porc_aumento='+porc_aumento+'&resolucion='+resolucion+'&ttos='+ttos+'&nomenclador='+nomenclador+'&cod_desde='+cod_desde+'&cod_hasta='+cod_hasta+'&usuario_modi='+'<%=usuario_modi%>';
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
});

</script>

<%}%>