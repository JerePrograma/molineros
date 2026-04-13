<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.CanjeChequesTotalesDiferentesException" %>
<%@ page import="ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio" %>

<% 

	String portlet_name = ParamUtil.getString(request, "portlet_name");
		
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	CanjeChequePropio cpp = (CanjeChequePropio)request.getSession().getAttribute(WebKeysTesoreria.CANJE_CHEQUES_EN_SESSION);
%>

<liferay-ui:error exception="<%= CanjeChequesTotalesDiferentesException.class %>" message="canje-cheques-totales-diferentes-exception" />

<form action="" name="<portlet:namespace />form_canje" id="<portlet:namespace />form_canje" method="post" >
<fieldset class="block-labels"><legend><liferay-ui:message
	key="canje-cheque-propio" /></legend>
<table class="lfr-table" width="100%" style="border-spacing: 2px; border-collapse: separate;" cellspacing="2px;">
		<tr>
			<td><liferay-ui:message	key="cheque-a-canjear" />:</td>
		</tr>
		<tr>
			<td>
				<liferay-ui:message key="op-nro"/>:
				<input type="text" name="<portlet:namespace />nro_op" id="<portlet:namespace />nro_op" <% if (cpp != null && cpp.getId()!=0) { %> disabled="disabled" <%} %> value="<%=(cpp != null && cpp.getOrdenPago() !=null) ? cpp.getOrdenPago().getNumeroOP() : new String()%>"/>
				<% if (cpp == null || cpp.getId()==0) {%>
				&nbsp;<input type="button" value="<liferay-ui:message key="buscar" />" onClick="<portlet:namespace />buscarChequesOP();" />
				<% } else { %>
					<input type="submit" value="<liferay-ui:message key="print" />" onClick="<portlet:namespace />imprimirOP('<%= cpp.getOrdenPago().getNumeroOP() %>');return false;"/>
				<% } %>
				<input type="hidden" id="<portlet:namespace />nro_op_final" name="<portlet:namespace />nro_op_final"  value="<%= cpp != null && cpp.getOrdenPago() != null ? cpp.getOrdenPago().getNumeroOP() : new String() %>"/>
			</td>
		</tr>
		<tr>
			<td>
			<div align="center" id="<portlet:namespace />buscandoChequesOp">
			<table style="align: center;">
				<tr>
					<td><liferay-ui:message key='buscando' /></td>
					<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td>
			<div align="center" id="<portlet:namespace />cheques_op">
				<jsp:include page='canje_cheques_propios_cheques_op.jsp' /></div>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td><liferay-ui:message	key="cheque-nuevo" />:</td>
		</tr>
		<tr>	
			<td>
				</legend>
					<table class="lfr-table" style="border-spacing: 2px; border-collapse: separate;" cellspacing="2px;">
						<tr>
							<td width="100%">
								<liferay-util:include page="/html/portlet/tesoreria/canje_cheques_propios/canje_cheques_agregar.jsp">
									<liferay-util:param name="esEdicion" value='<%= (cpp != null && cpp.getId()!=0) ? new String("false") : new String("true") %>'/>
								</liferay-util:include>
							</td>
						</tr>
					</table>
			</td>
		</tr>
		<% if (cpp != null && cpp.getOrdenPagoNueva() != null && cpp.getId()!=0){ %>
		<tr>
			<td>Orden de pago generada:&nbsp;<%= cpp.getOrdenPagoNueva().getNumeroOP() %>&nbsp;&nbsp;
			<input type="submit" value="<liferay-ui:message key="print" />" onClick="<portlet:namespace />imprimirOP('<%= cpp.getOrdenPagoNueva().getNumeroOP() %>');return false;"/></td>
		</tr>
		<%} %>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<% if (cpp == null || cpp.getId()==0) { %> 
		<tr>
			<td><input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />agregarCanje();" /></td>
		</tr>
		<%} %>
</table>
</form>
<script type="text/javascript">
	jQuery('#<portlet:namespace />buscandoChequesOp').hide();
	function <portlet:namespace />buscarChequesOP(){
		var idOp = jQuery('#<portlet:namespace />nro_op').val();
		
		if (trim(idOp)==""){
			alert("Debe completar el numero.");
			jQuery('#<portlet:namespace />nro_op').focus();
			return false;
		} 
		var idOpFinal = jQuery('#<portlet:namespace />nro_op_final').val(idOp);

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/canje_cheque_propio_busqueda_op&nro=' +idOp
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />buscandoChequesOp').show();
		jQuery('#<portlet:namespace />cheques_op').load(url, function() {
											jQuery('#<portlet:namespace />buscandoChequesOp').hide();
									   }
		 );	
	}
	
	function <portlet:namespace />agregarCanje(){
		var idOp=jQuery('#<portlet:namespace />nro_op_final').val();
		if (trim(idOp)==""){
			alert("Debe completar el numero.");
			jQuery('#<portlet:namespace />nro_op').focus();
			return false;
		}
		
		var url = '<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>"/>&struts_action=/<%=portlet_name%>/canje_cheques_propios';
		submitForm(document.<portlet:namespace />form_canje, url);				
		return true;
	}
	
	function <portlet:namespace />imprimirOP(nro){
		<%if(portlet_name.equals("farmacia")){%>
			window.location.href ="/pdfservlet/?accion=ordenPago&id_orden_pago_ini=" + nro;
		<%}else if(portlet_name.equals("uoma")){%>
			window.location.href ="/pdfservlet/?accion=ordenPagoUoma&id_orden_pago_ini=" + nro;
		<%}else{%>
			window.location.href ="/pdfservlet/?accion=ordenPagoOspim&id_ini=" + nro;
		<%}%>		
	}
	
</script>