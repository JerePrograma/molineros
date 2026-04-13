<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<%
/* 	boolean showAutorizaciones = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.GENERAR_AUTORIZACIONES_PMI);
 */	
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(WindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");

	//verificar los calendars
	Calendar fechaReceta = CalendarFactoryUtil.getCalendar();
	fechaReceta.setTime(new Date()); 
	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();
%>

<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="autorizaciones-recetas-pmi" /></legend>
		<table class="lfr-table">	
			<tr>
				<td colspan="12">
				<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
				<liferay-util:include page='/html/portlet/farmacia/busqueda_afiliado.jsp'>
				<liferay-util:param value="<%=String.valueOf(true)%>" name="edit_mode" />
				</liferay-util:include>
				</fieldset>
				</td>
				</tr>
					<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>		
					</tr>
					
				<tr>
				<td><label><liferay-ui:message key="num-receta"/>:</label></td>
				<td><input id="<portlet:namespace />receta" name="<portlet:namespace />receta" size="10" maxlength="10" type="text" value=''/></td>
				<td><label><liferay-ui:message key="periodo-receta" />:</label></td>
				<td>
					<liferay-ui:input-date
					dayParam="fechaRecetaDia"
					dayValue="1" 
					dayNullable="<%= false %>"	
					monthParam="fechaRecetaMes"
					monthValue="<%= fechaReceta.get(Calendar.MONTH) %>"
					monthNullable="<%= true %>"				
					yearParam="fechaRecetaAnio"
					yearValue="<%= fechaReceta.get(Calendar.YEAR) %>"
					yearNullable="<%= true %>"
					yearRangeStart="<%= 2014 %>"	
					yearRangeEnd="<%= fechaReceta.get(Calendar.YEAR) +1 %>"
					firstDayOfWeek="<%= fechaReceta.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" /> 
				</td>
				<td colspan="1"><liferay-ui:message key="observaciones"/>:</label></td>
				<td colspan="5"><textarea rows="3" cols="100" maxlength="20000" 
						id="<portlet:namespace />observaciones" 
						name="<portlet:namespace />observaciones"
						style="resize: none;"></textarea>
				</td>		
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
<%-- 				<%if(showAutorizaciones) { %>
 --%>					<tr>
						<td>						
						<input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />"
						onClick="javascript: <portlet:namespace />buscarAutorizacion();"
						type="button" />
						</td>
						<td>						
						<input id="<portlet:namespace />generar"
						value="<liferay-ui:message key="generar"/>"
						title="<liferay-ui:message key="generar" />"
						onClick="javascript: <portlet:namespace />generarAutorizacion();"
						type="button" />
						</td>
					</tr>
<%-- 				<% } %>	
 --%>		</table>
	</fieldset>
	<fieldset class="block-labels">
		<div align="center" id="<portlet:namespace />buscando">
			<table style="align:center;">
				<tr>
					<td><liferay-ui:message key='buscando'/></td>
					<td align="center">					
					<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>		
		</div>	
		<div id="<portlet:namespace />listado_autorizaciones_pmi">
		<jsp:include page='/html/portlet/autorizaciones/recetas/autorizaciones_result.jsp' />  
		</div>
		<table class="lfr-table">
<%-- 					<%if(showAutorizaciones) { %>
 --%>						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td>						
							<input id="<portlet:namespace />reporte"
							value="<liferay-ui:message key="reporte"/>"
							title="<liferay-ui:message key="reporte" />"
							onClick="javascript: <portlet:namespace />reporteAutorizacion();"
							type="button" />
							</td>
						</tr>
<%-- 					<% } %>	
 --%>			</table>
	</fieldset>
</form>		

<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();		
	var autorizacionGenerada;

	function <portlet:namespace />generarAutorizacion(){
		var fechaRecetaDia = jQuery('#<portlet:namespace/>fechaRecetaDia').val();
		var fechaRecetaMes=jQuery('#<portlet:namespace />fechaRecetaMes').val();		
		var fechaRecetaAnio=jQuery('#<portlet:namespace />fechaRecetaAnio').val();
		var receta = jQuery('#<portlet:namespace />receta').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val(); 	
		var observaciones = jQuery('#<portlet:namespace />observaciones').val();
		
		var actual = new Date();
		
		if(fechaRecetaAnio < actual.getFullYear()){
			alert('No se pueden generar recetas con esa fecha');
			fechaRecetaAnio.select();
			fechaRecetaAnio.focus();
		}
		if (isNaN(fechaRecetaDia) || fechaRecetaDia == "" || isNaN(fechaRecetaMes) || fechaRecetaMes == "" || isNaN(fechaRecetaAnio) || fechaRecetaAnio == "" ) {
			alert("Debe ingresar una Fecha de Receta valida. Inténtelo nuevamente.");
			fechaRecetaDia.select();
			fechaRecetaDia.focus();
			fechaRecetaMes.select();
			fechaRecetaMes.focus();
			fechaRecetaAnio.select();
			fechaRecetaAnio.focus();
		}
		else { 
		jQuery('#<portlet:namespace />buscando').show();
	 	var generarPmi = {"fechaRecetaDia":fechaRecetaDia,"fechaRecetaMes":fechaRecetaMes,"fechaRecetaAnio":fechaRecetaAnio, "receta":receta,"cuil":cuil,"inte":inte,"observaciones":observaciones,"usuario_modi":'<%=usuario_modi%>'};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/generarAutorizacionPmi" /></portlet:renderURL>';
 	 	autorizacionGenerada = Liferay.Popup({title:"<liferay-ui:message key="Se han generados las recetas:" />",modal:true,width:1300});
	 	jQuery(autorizacionGenerada).load(url,generarPmi, function(){
															jQuery('#<portlet:namespace />buscando').hide();
														  });		
		}
	}
	
	function <portlet:namespace />buscarAutorizacion(){
		var fechaRecetaDia = jQuery('#<portlet:namespace/>fechaRecetaDia').val();
		var fechaRecetaMes=jQuery('#<portlet:namespace />fechaRecetaMes').val();		
		var fechaRecetaAnio=jQuery('#<portlet:namespace />fechaRecetaAnio').val();
		var receta = jQuery('#<portlet:namespace />receta').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val(); 	
	
		jQuery('#<portlet:namespace />buscando').show();
	 	var busquedaPmi = {"fechaRecetaDia":fechaRecetaDia,"fechaRecetaMes":fechaRecetaMes,"fechaRecetaAnio":fechaRecetaAnio,"receta":receta,"cuil":cuil,"inte":inte,"usuario_modi":'<%=usuario_modi%>'};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/buscarAutorizacionPmi" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />listado_autorizaciones_pmi').load(url,busquedaPmi, function(){
															jQuery('#<portlet:namespace />buscando').hide();      
														  });	
	}
	
	function <portlet:namespace />reporteAutorizacion(){
		var fechaRecetaDia = jQuery('#<portlet:namespace/>fechaRecetaDia').val();
		var fechaRecetaMes=jQuery('#<portlet:namespace />fechaRecetaMes').val();		
		var fechaRecetaAnio=jQuery('#<portlet:namespace />fechaRecetaAnio').val();
		var receta = jQuery('#<portlet:namespace />receta').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val(); 	
	
		window.location.href ='/xlsservlet/?reporte=REPORTE_PMI'+'&fechaRecetaDia='+fechaRecetaDia+
			'&fechaRecetaMes='+fechaRecetaMes+'&fechaRecetaAnio='+fechaRecetaAnio+'&receta='+receta+
			'&cuil='+cuil+'&inte='+inte;
	}
	
	<portlet:namespace />initDateFields();

	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />fechaRecetaDia').hide();
		jQuery('#<portlet:namespace />fechaRecetaMes').val("");		
		jQuery('#<portlet:namespace />fechaRecetaAnio').val("");
		jQuery('#<portlet:namespace />receta').val("");
		jQuery('#<portlet:namespace />observaciones').val("");
	}
	
</script>

