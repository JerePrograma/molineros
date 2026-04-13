<%@ include file="/html/portlet/correspondencia/init.jsp"%>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);	
	CabeceraCorrespondencia correspondencia=(CabeceraCorrespondencia)request.getSession().getAttribute(WebKeysCorrespondencia.SALIDA_EN_EDICION);

	List<Organization> organizaciones = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	Organization orgLocal = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId()).get(0);
	Organization lugarEmision = null;
	
	boolean esView = false;
	if (viewStr != null){
		esView = true;
	}
	
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
	fechaDesde=Calendar.getInstance();
	if(correspondencia!=null && correspondencia.getFecha()!=null){
		fechaDesde.setTime(correspondencia.getFecha());
	} else {
		fechaDesde.setTime(new Date());
	}
	
	String lugarEmis=correspondencia!=null?correspondencia.getLugar():"";
	
	if(/*esView ||*/ lugarEmis == null || lugarEmis == "" ){
		lugarEmision = orgLocal;
	}else{
		lugarEmision = OrganizationLocalServiceUtil.getOrganization(Long.parseLong(lugarEmis));
	}
	long id_correspondencia=correspondencia!=null?(long)correspondencia.getId_correspondencia():0;
	String tipoRegistro=correspondencia!=null?correspondencia.getTipoRegistro():"";
	String tipoEnvio=correspondencia!=null?correspondencia.getTipoEnvio():"";

%>

<form action="" method="post" name="<portlet:namespace />fm">
	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<liferay-ui:success key="insertCabOk"  message="<%=(String)request.getAttribute(\"msgCabOk\")  %>"  />
<liferay-ui:success key="insertItemOk" message="<%=(String)request.getAttribute(\"msgItemOk\") %>"  />
<liferay-ui:success key="updateCabOk"  message="<%=(String)request.getAttribute(\"msgCabOk\")  %>"  />
<liferay-ui:success key="updateItemOk" message="<%=(String)request.getAttribute(\"msgItemOk\") %>"  />
<liferay-ui:success key="deleteItemOk" message="<%=(String)request.getAttribute(\"msgItemOk\") %>"  />

<!-- <liferay-ui:success key="insertOk" message="insert-correspondencia"  /> -->

<fieldset class="block-labels"><legend>Salida</legend>

<table class="lfr-table">
	<tr>
		<td><label>Lugar Emisión:</label></td>
		<td><select name="<portlet:namespace/>edificio"
			id="<portlet:namespace/>edificio"  <% if (esView) { %> disabled="disabled" <%} %> >			
			<option value="<%=lugarEmision.getOrganizationId() %>" ><%=lugarEmision.getName() %></option> 
			</select>
		</td>

		<td><label>Fecha de Emisión:</label></td>
		<td><liferay-ui:input-date dayParam="fechaDesdeDia"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
			dayNullable="<%= false %>" monthParam="fechaDesdeMes"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			monthNullable="<%= false %>" yearParam="fechaDesdeAnio"
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearNullable="<%= false %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="<%= esView %>" /></td>			
		<td><label>Número Correspondencia:</label></td>
		
		<td><input id="<portlet:namespace />numero_correspondencia"
			name="<portlet:namespace />numero_correspondencia" size="20"
			maxlength="100" type="text" onkeydown="allowOnlyDigits(event);" 
			value="<%=id_correspondencia != 0 ? String.valueOf(id_correspondencia) : ""%>" 
			readonly="readonly" /></td>
	</tr>
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>

	<tr>
		<td><label>Tipo Registro:</label></td>
		<td><select name="<portlet:namespace />tipo_registro"
			id="<portlet:namespace />tipo_registro" <% if (esView) { %> disabled="disabled" <%} %>>			
			<option value="SALIDA" <%=tipoRegistro.equals("SALIDA")? "selected":""%>>Salida</option>			
		</select></td>
		<td><label>Tipo Envío:</label></td>
		<td><select name="<portlet:namespace />tipo_envio"
			id="<portlet:namespace />tipo_envio" <% if (esView) { %> disabled="disabled" <%} %> >
			<%for(int i = 0; i < WebKeysCorrespondencia.TIPOS_ENVIOS.length; i++ ) {%>
				<%if(WebKeysCorrespondencia.TIPOS_ENVIOS[i][3].contains("s")) {%>
				<option value="<%=WebKeysCorrespondencia.TIPOS_ENVIOS[i][0] %>" 
					<%=(tipoEnvio.equals(WebKeysCorrespondencia.TIPOS_ENVIOS[i][0]))? "selected":""%> > <%=WebKeysCorrespondencia.TIPOS_ENVIOS[i][1] %> </option>
				<% } %>
			<% } %>
		</select></td>
		
		<td><label>Oblea/Mensajería:</label></td>
		<td><input id="<portlet:namespace />oblea"
			name="<portlet:namespace />oblea" size="50"
			maxlength="50" type="text"  
			value="<%=correspondencia==null || correspondencia.getOblea()==null?"":correspondencia.getOblea()%>" 
			 <% if (esView) { %> disabled="disabled" <%} %> /></td>
		
		
	</tr>		
	<tr>
		<td colspan="10">&nbsp;</td>
	</tr>
	
	</table>
	
	<div id="<portlet:namespace />destinatario_detalle">
		<jsp:include page="editar_salida_destinatario.jsp"></jsp:include>
	</div>

</fieldset>
<table>
	<tr>
		<td colspan="8">
		<div align="center" id="<portlet:namespace />buscandoDetalles">
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
		<div align="center" id="<portlet:namespace />correspondencia_detalle">
				<liferay-util:include page="/html/portlet/correspondencia/correspondencia_salida_detalles_search_result.jsp">
					<liferay-util:param name="esViewStr" value="<%=viewStr%>"/>
				</liferay-util:include>
		</div>
		</td>
	</tr>
</table>

	<table>
		<tr>
			<td colspan="10">&nbsp;</td>
		</tr>
		<tr>			
			<td colspan="10" align="center"><c:if test="<%=showABMButtons && !esView%>">			
				<input type="button" value="<liferay-ui:message key="save" />" 
					   onClick="javascript:<portlet:namespace />saveCorrespondencia();" />&nbsp;
				<input type="button" value="Nueva Salida" onClick="<portlet:namespace />nuevaSalida();" />&nbsp;	   
				</c:if>
			</td>
		</tr>		
	</table>
	
	<input type="hidden" name="<portlet:namespace />id_correspondencia" id="<portlet:namespace />id_correspondencia" value="<%=id_correspondencia%>" />


	</form>

<script type="text/javascript">

jQuery('#<portlet:namespace />buscandoDetalles').hide();

function <portlet:namespace />saveItemCorrespondencia() {	
	if (<portlet:namespace />validarCamposDetalle()) {
<%-- 		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/correspondencia/editar_salida_entry';
		url = url + "&<%= Constants.CMD %>=" + "<%= Constants.ADD %>"; --%>
		var strutsUrl ='/correspondencia/editar_salida_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="cmd" value="add"/>'+
	    '</liferay-portlet:renderURL>';
	    url = url.replace("__strutsUrl",strutsUrl);
	    
		submitForm(document.<portlet:namespace />fm, url);
	}
	return false;
}

/* solo actualiza la cabecera, el primer padre se inserta con el primer hijo */
function <portlet:namespace />saveCorrespondencia() {	
	if (<portlet:namespace />validarCampos()) { 

		<%-- var params = "&<%= Constants.CMD %>=" + "<%= Constants.UPDATE %>";
		url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/correspondencia/editar_salida_entry';		
		url = url + params; --%>
		var strutsUrl ='/correspondencia/editar_entrada_entry';
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
	    '</liferay-portlet:renderURL>';
	    url = url.replace("__strutsUrl",strutsUrl);
	    
		submitForm(document.<portlet:namespace />fm, url);
	} 
	return false;
}

function <portlet:namespace />validarCampos(){
	// debe haber al menos un item de correspondencia
	var result = true;
	var cantItems = <%=(null!=correspondencia &&  null!=correspondencia.getItemsCorrespondencia())?correspondencia.getItemsCorrespondencia().size():0 %>
	
	var itemEnEdicion = jQuery('#<portlet:namespace />id_item_correspondencia').val();	 
	
	if(itemEnEdicion > 0){
		result=false;
		alert("Existe un ítem en edición, debe presionar Actualizar o Cancelar para continuar");
	}
	
	if(cantItems < 1){
		result=false;
		alert("Debe al menos cargar un ítem");
	}
	return result;
} 

function <portlet:namespace />nuevaSalida() {

<%-- 	var params = "&<%= Constants.CMD %>=" + "<%= Constants.WRITE %>";
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/correspondencia/editar_salida_entry" /></portlet:renderURL>';
	url = url + params; --%>
	var strutsUrl ='/correspondencia/editar_salida_entry';
	var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
	'<liferay-portlet:param name="struts_action" value="__strutsUrl" />'+
	'<liferay-portlet:param name="cmd" value="write"/>'+
    '</liferay-portlet:renderURL>';
    url = url.replace("__strutsUrl",strutsUrl);
	
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);
}
</script>