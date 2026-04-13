<%@ include file="/html/portlet/uoma/init.jsp"%>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute(WebKeysUnidadOperativa.VIEW_UNIDAD_OPERATIVA);
	
	Incidente incidente=(Incidente)request.getAttribute(WebKeysUnidadOperativa.INCIDENTE_EN_EDICION);
	
	if(incidente!=null){
		request.setAttribute("afiliado_edit", incidente.getAfiliado());
		request.setAttribute("domicilio_edit", incidente.getLugarIncidente());
	}
	
	boolean editar=incidente!=null?true:false;
	
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysUnidadOperativa.ROL_ABM_UNIDAD_OPERATIVA);
	
	boolean esView = false;
	if (viewStr != null){
		esView = true;
	}
	Calendar fechaInci=null;
	Calendar fechaRecepcion=null;
	if(incidente!=null){
		fechaRecepcion=Calendar.getInstance();
		fechaRecepcion.setTime(incidente.getFechaRecepcion());
	}
	
	if(incidente!=null){
		fechaInci=Calendar.getInstance();
		fechaInci.setTime(incidente.getFecha());
	}
	Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
%>
<form name="formulariox" id="formulariox" method="post">

	<fieldset class="block-labels"><legend><liferay-ui:message key="datos-incidente" /></legend>
	<table class="lfr-table">
		<tr>
			<td width="150"><label><liferay-ui:message key="seccional-incidente" />:</label></td>
			<td colspan="1" style="vertical-align:top" >			
				<liferay-util:include page='/html/portlet/uoma/busqueda_seccional_incidente.jsp'>
					<liferay-util:param name="id_seccional_r" value='<%= incidente != null && incidente.getIdSeccional() != 0 ? String.valueOf(incidente.getIdSeccional()) : ""%>'/>
		  			<liferay-util:param name="seccional_r" value='<%= incidente != null && incidente.getDescripcionSeccional() != null ? incidente.getDescripcionSeccional() : ""%>'/>									  		
				</liferay-util:include>
			</td>
			<td><label><liferay-ui:message key="fecha-recepcion" />:</label></td>
			<td colspan="6"><liferay-ui:input-date dayParam="fechaDiaRecepcion"
				dayValue="<%= incidente!=null?fechaRecepcion.get(Calendar.DATE):fechaHoy.get(Calendar.DATE)%>" monthParam="fechaMesRecepcion"
				monthValue="<%= incidente!=null?fechaRecepcion.get(Calendar.MONTH):fechaHoy.get(Calendar.MONTH) %>"
				yearParam="fechaAnioRecepcion" yearValue="<%= incidente!=null?fechaRecepcion.get(Calendar.YEAR):fechaHoy.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 25 %>"
				yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 25 %>"
				firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
				disabled="<%= esView %>" /></td>
		</tr>		
		<tr><td colspan="9">&nbsp;</td></tr>
		<tr>
			<td colspan="9">
				<fieldset class="block-labels"><legend>
					<liferay-ui:message
					key="datos-afiliado" /></legend>
					<div id="loadAfiliado">
						<liferay-util:include page='/html/portlet/uoma/busqueda_afiliado.jsp'>
							<liferay-util:param value="true" name="edit_mode" />							
						</liferay-util:include>
			  		</div>	  		
				</fieldset>
			</td>
		</tr>
		<tr><td colspan="9">&nbsp;</td></tr>
		<tr>
			<td colspan="9">
				<fieldset class="block-labels"><legend><liferay-ui:message key="lugar-incidente" /></legend>
					<div>
							<liferay-util:include page='/html/portlet/uoma/editar_domicilio.jsp'>
									<liferay-util:param value="true" name="edit_mode" />
							</liferay-util:include>
					</div>
				</fieldset>
			</td>
		</tr>
		<tr><td colspan="9">&nbsp;</td></tr>
		<tr>
			<td>
				<legend><liferay-ui:message key="fecha-incidente" /></legend>
			</td>
			<td colspan="8">
				<liferay-ui:input-date dayParam="fechaDia"
				dayValue="<%= incidente!=null?fechaInci.get(Calendar.DATE):fechaHoy.get(Calendar.DATE)%>" monthParam="fechaMes"
				monthValue="<%= incidente!=null?fechaInci.get(Calendar.MONTH):fechaHoy.get(Calendar.MONTH) %>"
				yearParam="fechaAnio" yearValue="<%= incidente!=null?fechaInci.get(Calendar.YEAR):fechaHoy.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 25 %>"
				yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 25 %>"
				firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
				disabled="<%= esView %>" />
			</td>
		</tr>
		<tr><td colspan="9">&nbsp;</td></tr>
		<tr>
			<td valign="top">
				<legend><liferay-ui:message key="detalle-incidente" /></legend>
			</td>
			<td colspan="8">
				<textarea id="detalle" name="detalle" cols="140" rows="5" style="resize: none; width: 700px;" ><%=incidente!=null?incidente.getDetalleIncidente():""%></textarea>
			</td>
		</tr>
		<tr>
			<td colspan="9">&nbsp;</td>
		</tr>
		<tr>			
			<td>
				<legend><liferay-ui:message key="seguimiento-incidente" /></legend>
			</td>
			<%if(incidente!=null){%>
			<td colspan="8">
				<textarea id="seguimientoViejo" name="seguimientoViejo" cols="140" rows="5" style="resize: none; width: 700px;" readonly><%if(null!=incidente && null!=incidente.getSeguimientoIncidente()){for(int i=0;i<incidente.getSeguimientoIncidente().size();i++){SeguimientoIncidente seg=incidente.getSeguimientoIncidente().get(i);%><%=seg.getFechaAsString()%> - <%=seg.getDetalle()%>
<%}}%></textarea>
			</td>		
			<%}%>
		</tr>
		<tr>			
			<td>
				&nbsp;
			</td>
			<td colspan="8">
				<textarea id="seguimiento" name="seguimiento" cols="140" rows="5" style="resize: none; width: 700px;"></textarea>
			</td>		
		</tr>
		<tr>
			<td colspan="9">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="9" align="center">
				<c:if test="<%=showABMButtons%>">
					<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveIncidente();" />
				</c:if>
			</td>		
		</tr>
		
	</table>
	<input type="hidden" name="<portlet:namespace />id_incidente" id="<portlet:namespace />id_incidente" value="<%=incidente!=null?incidente.getIdIncidente():""%>"/>
	<input type="hidden" name="<portlet:namespace />id_domicilio" id="<portlet:namespace />id_domicilio" value="<%=incidente!=null?incidente.getLugarIncidente().getId_domicilio():""%>"/>
	<input type="hidden" name="<portlet:namespace /><%= Constants.CMD %>" id="<portlet:namespace /><%= Constants.CMD %>"/>
	
	</fieldset>
</form>
<script type="text/javascript">
function <portlet:namespace />saveIncidente() {
			<%if(null!=incidente){%>
				document.formulariox.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE%>";
			<%}else{%>	
				document.formulariox.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.ADD %>";
			<%}%>
			if(!validar()){
				return false;
			}	
			url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/uoma/editar_incidente_entry'/></portlet:actionURL>";
									
			document.formulariox.action=url;
			submitForm(document.formulariox, url);
}

function validar(){
	
	var respOk = true;
	var idDeleg = jQuery("#<portlet:namespace />id_seccional_r").val();
	var cuilInc= jQuery("#<portlet:namespace />cuil").val();
	var detalleInc= jQuery("#detalle").val();
	
	if(idDeleg == ''){
		alert('Debe seleccionar la Delegación');
		respOk = false;
	}
	if(cuilInc == ''){
		alert('Debe seleccionar el Beneficiario');
		respOk = false;
	}
	if(detalleInc == ''){
		alert('Debe completar el detalle del incidente');
		respOk = false;
	}
	
	return respOk;
}
</script>