<%@ include file="/html/portlet/novedades/init.jsp"%>
<%
boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_AFILIADO);

NovedadEmpleadorTotal nove = (NovedadEmpleadorTotal) request.getAttribute(WebKeysAfiliados.BUSQUEDA_DETALLE_NOVEDAD);

SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy"); 

%>

<fieldset class="block-labels">
<legend><liferay-ui:message key="detalle-novedad" /></legend>
<table class="lfr-table">
	<tr>
		<td><label><liferay-ui:message key="tipo-novedad" />:</label></td>
		<td colspan="3" >
			<input type="text" readonly="readonly" value="<%=nove.getNovedad_desc() %>">
		</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<%-- <td><label><liferay-ui:message key="periodo" />:</label></td>
		<td colspan="1">
			<input type="text" readonly="readonly" value="<%=sdf.format(nove.getPeriodo()) %>" >
		</td> --%>
		<td><label><b><liferay-ui:message key="detalle-novedad" />:</label></b></td>
		<td><input readonly="readonly" id="<portlet:namespace />det_nove"
			name="<portlet:namespace />det_nove" size="13"
			type="text"
			value="<%= nove.getPlan_que_corresponde_desc()%>" 
			style="font-weight: bold;"  />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="cuil-titular" />:</label></td>
		<td><input readonly="readonly" id="<portlet:namespace />cuil_titular"
			name="<portlet:namespace />cuil_titular" size="13" type="text"
			value="<%=nove.getCuil_titular() %>" />
		</td>
		<td><label><liferay-ui:message key="inte" />:</label></td>
		<td><input readonly="readonly" id="<portlet:namespace />inte"
			name="<portlet:namespace />inte" size="5"
			type="text"
			value="<%= nove.getInte()%>" />
		</td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />apellido"
				name="<portlet:namespace />apellido" size="20" maxlength="100"
				type="text" readonly="readonly"
				value="<%=nove.getApellido()%>" /></td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />nombre"
				name="<portlet:namespace />nombre" maxlength="100" 
				type="text" readonly="readonly"
				value="<%=nove.getNombre()%>" /></td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>			
	<tr>
		<td><label><liferay-ui:message key="plan" />:</label></td>
		<td><input readonly="readonly" id="<portlet:namespace />plan_actual"
			name="<portlet:namespace />plan_actual" size="13" type="text"
			value="<%=nove.getPlan_actual_desc() %>" />
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>	
	<tr><td colspan="4">&nbsp;</td></tr>
	</table>
</fieldset>
<br/>
<br/>

