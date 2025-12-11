<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%
String portlet_name = "liquidaciones";

if(renderResponse.getNamespace().equals("_COR_1_")){
	portlet_name = "correspondencia";
}else if(renderResponse.getNamespace().equals("_COM_1_")){
	portlet_name = "comprobantes";
}else{
	portlet_name = "liquidaciones";
}

Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
fechaHoy.setTime(new Date());
String view=(String)request.getParameter("view");
String id_liquidacion=request.getParameter("id_liquidacion");
String id_prestador=request.getParameter("id_prestador");

List<Motivo> motivosDebito = (ArrayList<Motivo>) portletSession.getAttribute(WebKeysLiquidaciones.MOTIVOS_DEBITO_EN_SESSION,PortletSession.APPLICATION_SCOPE);

if (motivosDebito == null) {
	motivosDebito = TraeListasServiceUtil.getMotivosDebito();
	portletSession.setAttribute(WebKeysLiquidaciones.MOTIVOS_DEBITO_EN_SESSION,motivosDebito,PortletSession.APPLICATION_SCOPE);	
}
%>
<portlet:defineObjects />
<fieldset class="block-labels"><legend><liferay-ui:message
	key="debitos" /></legend> <%if(null==view || !view.equals("true")){ %>
<table class="lfr-table">
	<tr>
		<td><liferay-ui:message key="motivo-debito" /></td>
		<td colspan="5"><select name="<portlet:namespace/>motivo_debito"
			id="<portlet:namespace/>motivo_debito">
			<%
								for (Motivo motivoDebito : motivosDebito) {
			%>
			<option value="<%= motivoDebito.getId_motivo()%>"><%=motivoDebito.getDescripcion()%></option>
			<%
								}
			%>
		</select></td>
		<td><label><liferay-ui:message key="importe" /></label></td>
		<td>
			<input id="<portlet:namespace />importe_debito"
			name="<portlet:namespace />importe_debito" size="10" maxlength="15"
			type="text"
			value=""
			onkeydown="allowOnlyDigitsAndDecimals(event); limitDecimals(2,document.getElementById('<portlet:namespace />importe_debito'),event);" />		 	
		</td>
	</tr>	
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="9"><textarea rows="5" cols="100" id="<portlet:namespace />observaciones_debito"
				name="<portlet:namespace />observaciones_debito" maxlength="3000" onkeyup="return ismaxlength(this);"></textarea>			
		</td>
	</tr>
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1">
			<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />grabarDebito();" />
			<input type="hidden" id="<portlet:namespace />item" name="<portlet:namespace />item" value="0"/>			
		</td>
		<td colspan="1">
			<input type="button" value="<liferay-ui:message key="limpiar-campos" />" onClick="<portlet:namespace />limpiarCamposDebito();" />		
		</td>
	</tr>
	<tr>
		<td colspan="5">&nbsp;</td>
	</tr>
	
</table>
<div align="center" id="<portlet:namespace />buscandoDebitos">
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
<%} %>
<div align="center" id="<portlet:namespace />debitos_resultado"><jsp:include
	page='debitos_search_result.jsp'>
	<jsp:param name="view" value="<%=view%>" />
</jsp:include></div>
</fieldset>

<script type="text/javascript">
	jQuery('#<portlet:namespace />buscandoDebitos').hide();
	function <portlet:namespace />grabarDebito(){	
		if(!<portlet:namespace />validarDatosDebitos()){					
			return false;
		}else{
			jQuery('#<portlet:namespace />buscandoDebitos').show();
			var id_liquidacion = <%=id_liquidacion%>;
			var id_prestador = <%=id_prestador%>;
			var motivo_debito=jQuery('#<portlet:namespace />motivo_debito').val();
			var observaciones_debito=jQuery('#<portlet:namespace />observaciones_debito').val();
			var importe_debito=jQuery('#<portlet:namespace />importe_debito').val();			
			var item=jQuery('#<portlet:namespace />item').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/grabar_debito&id_liquidacion='+id_liquidacion+'&motivo_debito='+motivo_debito+
			'&observaciones_debito='+encodeURI(observaciones_debito)+'&importe_debito='+importe_debito+'&item='+item+'&id_prestador='+id_prestador;
			jQuery('#<portlet:namespace />debitos_resultado').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoDebitos').hide();            															
																			   }
															   );
		}
	}	
	
	function borraDebito(item){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{
			var id_liquidacion = <%=id_liquidacion%>;
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/grabar_debito&id_liquidacion='+id_liquidacion+
			'&item='+item+'&borrarDeb='+'true';
			jQuery('#<portlet:namespace />debitos_resultado').load(url, function() {
																			jQuery('#<portlet:namespace />buscandoDebitos').hide();            															
																		}
															   );
		}
	}

	function <portlet:namespace />validarDatosDebitos(){
		var observaciones_debito=jQuery('#<portlet:namespace />observaciones_debito').val();
		var importe_debito=jQuery('#<portlet:namespace />importe_debito').val();
		var mensaje="Debe completar todos los campos";		
		var sinError=true;
		if(trim(importe_debito) == ""){
			sinError=false;
		}
		if(trim(observaciones_debito) == ""){			
			sinError=false;
		}	
		if(!sinError){		
			alert(mensaje);
		}
		return sinError;
	}

	function <portlet:namespace />limpiarCamposDebito(){
		//enable
		jQuery('#<portlet:namespace />motivo_debito').attr("disabled",false);
		jQuery('#<portlet:namespace />observaciones_debito').val("");
		jQuery('#<portlet:namespace />importe_debito').val("");			
		jQuery('#<portlet:namespace />item').val("");
	}

	function editaDebito(item,motivo_debito,observaciones_debito,importe_debito){	
		jQuery('#<portlet:namespace />item').val(item);			
		jQuery('#<portlet:namespace />motivo_debito').val(motivo_debito);
		jQuery('#<portlet:namespace />importe_debito').val(importe_debito);
		jQuery('#<portlet:namespace />observaciones_debito').val(observaciones_debito);				
	}
	
</script>