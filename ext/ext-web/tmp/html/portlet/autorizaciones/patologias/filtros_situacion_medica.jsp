<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ include file="/html/portlet/autorizaciones/patologias/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
 
 <%
	response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
	response.setHeader("Pragma", "no-cache"); //HTTP 1.0
	response.setDateHeader("Expires", 0); //prevents caching at the proxy server
	
	BusquedaSituacionMedicaFiltro  criterioBusquedaSituacionMedica    = (BusquedaSituacionMedicaFiltro)request.getSession().getAttribute(WebKeysAutorizaciones.FILTRO_BUSQUEDA_SITUACIONMEDICA );
			
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ABM_SITUACIONES_MEDICAS);
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	portletURL.setParameter("struts_action", "/autorizaciones/view");
	

	//verificar los calendars
	showABMButtons=true;
	Calendar fechaVigenDesde = CalendarFactoryUtil.getCalendar();
	fechaVigenDesde.setTime(DateUtils.getFirstDateOfYear(new Date(), true));
	Calendar fechaVigenHasta = CalendarFactoryUtil.getCalendar();
	fechaVigenHasta.setTime(DateUtils.getLastDateOfYear(new Date(), true));
	
	Calendar fechaDesde =  Calendar.getInstance();
	Calendar fechaHasta =  Calendar.getInstance();
	
	Date fechaDesdeDate = null;
	Date fechaHastaDate = null;
	
	if(criterioBusquedaSituacionMedica != null  ){
		//Date fechaaux = null;
		fechaDesdeDate = Validator.isNotNull(criterioBusquedaSituacionMedica)? criterioBusquedaSituacionMedica.getFechaDesde() : null;
		 
		if (fechaDesdeDate != null) {
			fechaDesde.setTime (criterioBusquedaSituacionMedica.getFechaDesde());	
		}
		fechaHastaDate = Validator.isNotNull(criterioBusquedaSituacionMedica)? criterioBusquedaSituacionMedica.getFechaHasta() : null;
		if (fechaHastaDate != null) {
			fechaHasta.setTime (criterioBusquedaSituacionMedica.getFechaHasta());	
		}
	}	
%>
 
 
 
 <form action="<%=portletURL%>" method="get"
	name="<portlet:namespace />fm"
	onSubmit="submitForm(this); return false;"><liferay-portlet:renderURLParams
	varImpl="portletURL" /> <%
 	boolean showOspim = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ENTIDAD_OSPIM);
 	boolean showAmtima = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ENTIDAD_AMTIMA);
 	boolean showUoma = PermissionUtil.userContainsRole(user,WebKeysAutorizaciones.ROL_ENTIDAD_UOMA);
 %> 
	<fieldset class="block-labels">
	<legend> <liferay-ui:message key="situacion-medica" /> 
	</legend>
<table>		
	<tr> <td colspan="10">&nbsp;</td> </tr>
		<tr>			
		<%if(criterioBusquedaSituacionMedica == null ||  fechaDesdeDate ==null) {%>
			<td><label><liferay-ui:message key="vigen-fecha-desde" />:</label> </td>			
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaVigenDesde.get(Calendar.YEAR)  -10%>"
			yearRangeEnd="<%= fechaVigenDesde.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaVigenDesde.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /> </td>
		<%}else{ %>
			<td><label><liferay-ui:message key="vigen-fecha-desde" />:</label> </td>			
			<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaDesdeMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="fechaDesdeAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaVigenDesde.get(Calendar.YEAR)  -10%>"
			yearRangeEnd="<%= fechaVigenDesde.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaVigenDesde.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /> </td>	
		<%} %>	
		<%if(criterioBusquedaSituacionMedica == null ||  fechaHastaDate ==null) {%>					
			<td><label>&nbsp;&nbsp;&nbsp;&nbsp;<liferay-ui:message key="vigen-fecha-hasta" />:</label> </td>
			<td> <liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue=""
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="-1"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue=""
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaVigenHasta.get(Calendar.YEAR) -10%>"
			yearRangeEnd="<%= fechaVigenHasta.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaVigenHasta.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /></td>
		<%}else{ %>	
			<td><label>&nbsp;&nbsp;&nbsp;&nbsp;<liferay-ui:message key="vigen-fecha-hasta" />:</label> </td>
			<td> <liferay-ui:input-date dayParam="fechaHastaDia"
			dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
			dayNullable="<%= true %>" monthParam="fechaHastaMes"
			monthValue="<%= fechaHasta.get(Calendar.MONTH)%>"
			monthNullable="<%= true %>" yearParam="fechaHastaAnio"
			yearValue="<%= fechaHasta.get(Calendar.YEAR)%>"
			yearNullable="<%= true %>"
			yearRangeStart="<%= fechaVigenHasta.get(Calendar.YEAR) -10%>"
			yearRangeEnd="<%= fechaVigenHasta.get(Calendar.YEAR)  %>"
			firstDayOfWeek="<%= fechaVigenHasta.getFirstDayOfWeek()  %>"
			disabled="<%= false %>" /></td>
		<%} %>	
			
		</tr>	
</table>
</fieldset>
	
<table align="center">
	<tr>	<td colspan="12">&nbsp;</td>	</tr>	
	<tr>
		<td colspan="12">
		<fieldset class="block-labels"><legend><liferay-ui:message
			key="datos-afiliado" /></legend> <liferay-util:include
			page='/html/portlet/autorizaciones/busqueda_afiliado.jsp'>
			<liferay-util:param value="<%=String.valueOf(true)%>"
				name="edit_mode" />
			<liferay-util:param value="<%=String.valueOf(true)%>"
				name="discapacidad" />
			<liferay-util:param name="pag_reintegro" value='1' />
			
			<% if ( criterioBusquedaSituacionMedica!=null   ) { %>
			        <liferay-util:param name="cuil" value="<%=criterioBusquedaSituacionMedica!=null?criterioBusquedaSituacionMedica.getCuilTitular() :null%>" />
			        <liferay-util:param name="inte" value="<%=criterioBusquedaSituacionMedica!=null?String.valueOf(criterioBusquedaSituacionMedica.getInte()):null%>" />
			        <liferay-util:param value="" name="origen" />	
		    <%} %>
								 				
		</liferay-util:include></fieldset>
		</td>
	</tr>
	</table>
	<table>
	<tr>	<td colspan="12">&nbsp;</td>	</tr>
	<tr>	<td colspan="12">&nbsp;</td>	</tr>
	<tr>
	<td><label><liferay-ui:message key="tipo-situacion-medica" />:</label> </td>
	<td colspan="12">&nbsp;</td>
			<td>
			<select name="<portlet:namespace/>situacionMedica" 				
				id="<portlet:namespace/>situacionMedica">
					<option  value="0">SELECCIONE</option>
					<% for (TiposDeSituacionesMedicas situaciones  : listatipodesituacionesmedicas) { %>
					<option
					   <%=Validator.isNotNull(criterioBusquedaSituacionMedica)  && Validator.isNotNull(criterioBusquedaSituacionMedica.gettipoSituMedica())  &&  criterioBusquedaSituacionMedica.gettipoSituMedica() == situaciones.getId() ? "selected" : ""  %>					
						value="<%= situaciones.getId() %>"><%=situaciones.getDescripcion()%>
					</option>
					<% } %>								
			</select>
			 </td>
	</tr>
		<tr>	<td colspan="12">&nbsp;</td>	</tr>
		<tr>	<td colspan="12">&nbsp;</td>	</tr>
</table>
	    
<table>
	<tr>
		<td coslpan="1"  ><input id="<portlet:namespace />buscar" name="<portlet:namespace />buscar"
			value="<liferay-ui:message key="buscar"/>"
			title="<liferay-ui:message key="buscar-situacion-medica" />" type="button" /></td>
		<td colspan="12">&nbsp;&nbsp;&nbsp;</td>	
		<td colspan="2"  >
		<c:if test="<%=showABMButtons%>">
			<input type="button" value="<liferay-ui:message key="compose"/>" id="<portlet:namespace />nueva" name="<portlet:namespace />nueva"
				title="<liferay-ui:message key="nueva-situ-medica" />" 
				onClick="<portlet:namespace />altaRegistroSituacionMedica();" /></c:if></td>
        <td colspan="12">&nbsp;&nbsp;&nbsp;</td>				
		<td colspan="2"  >
		<c:if test="<%=showABMButtons%>">
		<input id="<portlet:namespace />exportar-busqueda" value="<liferay-ui:message key="exportar-busqueda"/>" 
		title="<liferay-ui:message key="exportar-busqueda" />" type="button" />
		</c:if></td>
	</tr>
	<tr>	<td colspan="12">&nbsp;</td>	</tr>
</table>

<fieldset class="block-labels">
<div align="center" id="<portlet:namespace />buscando">
<table style="align: center;">
	<tr>
		<td><liferay-ui:message key='buscando' /></td>
		<td align="center"><img
			alt="<liferay-ui:message key='buscando'/>"
			src="<%=themeDisplay.getPathThemeImages()%>/progress_bar/loading_animation.gif" />
		</td>
	</tr>
</table>
</div>
<div align="center" id="<portlet:namespace />busquedaSituacionMedicaDiv">
</div>
</fieldset>
</form>
 