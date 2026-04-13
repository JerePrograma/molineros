<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@page import="ar.com.ospim.global.beans.AportesMonotributo" %>
<%@page import="ar.com.ospim.util.DateUtils" %>
<%
AportesMonotributo aporte = (AportesMonotributo)session.getAttribute(WebKeysAfiliados.APORTE_EN_EDICION);	

Integer id_aporte=aporte!=null && aporte.getId()!= null ? aporte.getId():0;


Calendar vigenteDesde = CalendarFactoryUtil.getCalendar();
if(aporte.getDesde()!=null){
	vigenteDesde.setTime(aporte.getDesde());
}


Calendar vigenteHasta = CalendarFactoryUtil.getCalendar();


if(aporte.getHasta()!=null){
	vigenteHasta.setTime(aporte.getHasta());
}else{
	vigenteHasta.setTime(DateUtils.getLastDateOfYear(vigenteHasta.getTime(), true));
}


List<AportesMonotributo> categorias =TraeListasServiceUtil.getCategoriasMonotributo();


%>
<form action="" method="post" name="<portlet:namespace />fmS">

<liferay-ui:success key="insertCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:success key="updateCabOk"
		message="<%=(String)request.getAttribute(\"msgCabOk\")  %>" />
	<liferay-ui:error key="errorAfiliadoNull"
		message="<%=(String)request.getAttribute(\"msgError\") %>" />

<fieldset class="block-labels"><legend>Aporte-Categoría</legend>
<table class="lfr-table">
	<tr>
		<td><label>Id:</label></td>
		<td><input readonly="readonly" id="<portlet:namespace />id_aporte"
			name="<portlet:namespace />id_aporte" size="13"
			type="text"
			value='<%= aporte != null && aporte.getId()!=null ? aporte.getId() : "" %>'/>
		</td>
		<td>&nbsp;</td>
		
		<td><label>Categoría:</label></td>
			<td colspan="2"><select  name="<portlet:namespace/>categoria">
				<%
					for (AportesMonotributo categoria : categorias) {
				%>
				<option
					<%= aporte != null && aporte.getCategoria()!=null && aporte.getCategoria()==categoria.getCategoria() ? "selected" : ""  %>
					value="<%= categoria.getCategoria() %>"><%=categoria.getDescripcion()%></option>
				<%
				  }
				%>
			</select>
		</td>
		<td>
		      <label><liferay-ui:message key="Desde" />:</label>
		</td>
		<td>  
		        <liferay-ui:input-date
						 dayParam="fechaDesdeDia"
						 dayValue="<%=aporte.getDesde()!=null?vigenteDesde.get(Calendar.DAY_OF_MONTH ):vigenteDesde.get(Calendar.DAY_OF_MONTH )%>"
						 dayNullable="<%= false %>" monthParam="fechaDesdeMes"
						 monthValue="<%=aporte.getDesde()!=null?vigenteDesde.get(Calendar.MONTH ):vigenteDesde.get(Calendar.MONTH )%>"
						 monthNullable="<%= false %>" yearParam="fechaDesdeAnio"
						 yearValue="<%=aporte.getDesde()!=null?vigenteDesde.get(Calendar.YEAR ):vigenteDesde.get(Calendar.YEAR ) %>"
						 yearNullable="<%= false %>"
						 yearRangeStart="<%= vigenteDesde.get(Calendar.YEAR) - 10 %>"
						 yearRangeEnd="<%= vigenteDesde.get(Calendar.YEAR) + 1 %>"
						 firstDayOfWeek="<%= vigenteDesde.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/>
		</td>
		
		<td>
		      <label><liferay-ui:message key="Hasta" />:</label>
		</td>
		
		<td>  
		        <liferay-ui:input-date
						 dayParam="fechaHastaDia"
						 dayValue="<%=aporte.getHasta()!=null?vigenteHasta.get(Calendar.DAY_OF_MONTH ):vigenteHasta.get(Calendar.DAY_OF_MONTH )%>"
						 dayNullable="<%= false %>" monthParam="fechaHastaMes"
						 monthValue="<%=aporte.getHasta()!=null?vigenteHasta.get(Calendar.MONTH ):vigenteHasta.get(Calendar.MONTH )%>"
						 monthNullable="<%= false %>" yearParam="fechaHastaAnio"
						 yearValue="<%=aporte.getHasta()!=null?vigenteHasta.get(Calendar.YEAR ):vigenteHasta.get(Calendar.YEAR ) %>"
						 yearNullable="<%= false %>"
						 yearRangeStart="<%= vigenteHasta.get(Calendar.YEAR) - 10 %>"
						 yearRangeEnd="<%= vigenteHasta.get(Calendar.YEAR) + 1 %>"
						 firstDayOfWeek="<%= vigenteHasta.getFirstDayOfWeek() - 1 %>"
						 disabled="<%= false %>"/>
		</td>
		
		<td>Aporte:</td> 
		<td><input id="<portlet:namespace />aporte"
					name="<portlet:namespace />aporte" size="15"
					maxlength="20" type="text"
					value='<%=aporte.getAporte() ==null?"0":aporte.getAporte()%>' />
		</td>
	</tr>	
	<tr>
			<td colspan="11">&nbsp;</td>
	</tr>
</table>

</fieldset>

<div id="<portlet:namespace />divClases">
		<fieldset class="block-labels">
			<legend>
				Clases
			</legend>
			<liferay-util:include
					page='/html/portlet/afiliados/aportemonotributo_clases_edit.jsp'>
			</liferay-util:include>
		</fieldset>
</div>
	

<br>
<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />salvarEdicion();" />

</form>

<script>
function <portlet:namespace />salvarEdicion(){
	if (<portlet:namespace />validarCampos()) {
		document.getElementById("<portlet:namespace />id_aporte").disabled=false;
		var url= '<liferay-portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString()%>">'+
		'<liferay-portlet:param name="struts_action" value="/afiliados/abm_categorias_monotributo_action" />'+
		'<liferay-portlet:param name="cmd" value="update"/>'+
		'</liferay-portlet:renderURL>';
		document.<portlet:namespace />fmS.method = 'post';
		submitForm(document.<portlet:namespace />fmS, url);
	}
	return false;		
}


function <portlet:namespace />validarCampos(){
	var result = true;
	/*
	
	if(inte==null || "0"!=inte){
		alert("Debe seleccionar un afiliando titular");
		return false;
	}
	*/
	return true;
}

</script>