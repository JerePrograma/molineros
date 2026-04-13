<%@ include file="/html/portlet/afiliados/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	
	portletURL.setParameter("struts_action", "/afiliados/view");
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	
	if (portlet_name == null && portlet_name.trim().equals("_AFI_1_")){
		portlet_name = "afiliados";
	}
	
	if (portlet_name == null && portlet_name.trim().equals("_SEC_1_")){
		portlet_name = "sec";
	}
	
	if(renderResponse.getNamespace().equals("_SEC_1_")){
		portlet_name = "sec";
	}
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "afiliados";
	} 
	
	//verificar los calendars
	Calendar current = CalendarFactoryUtil.getCalendar();
	String usuario_modi = user.getScreenName();
	
	
	Calendar fechaInicio = CalendarFactoryUtil.getCalendar();
	fechaInicio.add(Calendar.MONTH, -1);
	
	
	boolean popup=ParamUtil.getBoolean(request, "popup", false);
	boolean rolABMSeccionales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_SECCIONALES);
	boolean rolVIEWSeccionales = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_VIEW_SECCIONALES);
%>

<form action="" method="get"  id="<portlet:namespace />fm" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
    
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="seccionales" /></legend>
		<table class="lfr-table">		 
			 <tr>
			    <td><label><liferay-ui:message key="seccional"/>:</label></td>
				<td><input id="<portlet:namespace />codigoSeccional_filtro" name="<portlet:namespace />codigoSeccional_filtro" size="10" maxlength="20" type="text" value=''/></td>
				<td><input id="<portlet:namespace />descripcionSeccional_filtro" name="<portlet:namespace />descripcionSeccional_filtro" size="80" maxlength="200" type="text" value=''	/></td>
				<td><liferay-ui:message key="provincia" /></td>
			    <td>
		            <select name="<portlet:namespace />provincia_filtro"
			                   id="<portlet:namespace />provincia_filtro" onchange="" >
			            <option value="0">Seleccione Provincia</option>
				        <%	for (Provincia provincia : provincias) { %>
					   <option
						value="<%= provincia.getId() %>"><%=provincia.getDescripcion()%></option>
					<%	} %>    
		            </select>
		         </td>
		         <td>
		         <input id="<portlet:namespace />exportar-busqueda" value="<liferay-ui:message key="export"/>" 
		title="<liferay-ui:message key="exportar-busqueda" />" type="button" />
		         </td>    
			 </tr>
		</table>
		
		
	</fieldset>
		
	<table>
		    <tr align="left">
			    <td>&nbsp;</td>
			</tr>
			<tr align="left">
			    <td>&nbsp;</td>
				<td align="left" width="100%">						
					<input id="<portlet:namespace />buscar"
						value="<liferay-ui:message key="buscar"/>"
						title="<liferay-ui:message key="buscar" />"
						onClick="javascript: <portlet:namespace />buscarSeccionales();"
						type="button" />
						
					<c:if test="<%= rolABMSeccionales %>">
						<input type="button" value="Nuevo" onClick="<portlet:namespace />nuevaSeccional();"/>&nbsp;
					</c:if>
						
				</td>
			</tr>
			 <tr align="left">
			    <td>&nbsp;</td>
			</tr>
	</table>
		
	<div id='divSeccionales' style="float:left;">
	</div>
	
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
		<div id="<portlet:namespace />listado_seccionales">
			<jsp:include page='/html/portlet/afiliados/seccionales_search_result.jsp' />  	
		</div>
		
	</fieldset>
	
	<input id="<portlet:namespace />nom_seleccionado" name="<portlet:namespace />nom_seleccionado" type="hidden" value=""/>
	
</form>		

<script type="text/javascript">
	
	jQuery('#<portlet:namespace />buscando').hide();	
	var popupMD;
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
		
	}
	
	
	function <portlet:namespace />nuevaSeccional() {
		var params = "&<%= Constants.CMD %>=" + "<%= Constants.ADD %>";
		    params += "&accion=" + "<%= Constants.ADD %>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_seccional" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);		
	}
	
	function <portlet:namespace />buscarSeccionales(){
		
		var codigo=jQuery('#<portlet:namespace />codigoSeccional_filtro').val();
		var descripcion=jQuery('#<portlet:namespace />descripcionSeccional_filtro').val();
		var provincia=jQuery('#<portlet:namespace />provincia_filtro').val();
		
		jQuery('#<portlet:namespace />buscando').show();
	 	var busquedaNom = {"codigo":codigo,"descripcion":descripcion,"provincia":provincia};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscarABMSeccionales'
	 	<%-- var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/<%=portlet_name%>/buscarABMSeccionales" /></portlet:renderURL>'; --%>
	 	
		jQuery('#<portlet:namespace />listado_seccionales').load(url,busquedaNom, function(){
															jQuery('#<portlet:namespace />buscando').hide();      
		});	
		
	}
		
	
	jQuery('#<portlet:namespace />exportar-busqueda').click(function exportacionSeccionales(){
		var provinciaSeleccionada=jQuery('#<portlet:namespace />provincia_filtro').val();
		 
			window.location.href ='/xlsservlet/?reporte=REPORTE_RESULT_BUSQUEDA_SECCIONALES'
			+'&provinciaSeleccionada='+provinciaSeleccionada;
		});

	
</script>
