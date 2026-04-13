<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimSinPagos" %>
<%@ page import="ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago"%>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimSinComprobantes" %>
<%@ page import="ar.com.ospim.liquidaciones.PagoMayorQueComprobanteException" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimSinPagos" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimTotalPagosMenorQueComprobantesException" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimCreacionNuevoAnticipoException" %>
<%@ page import="ar.com.ospim.liquidaciones.OrdenPagoOspimAnticiposNoUsadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.ChequeSinChequeraException" %>



<%

	String portlet_name=null;
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "farmacia";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}

	OrdenPago ordenPago = (OrdenPago)request.getSession().getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);

	String esEdicionStr = (String)request.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EDICION);
	boolean esEdicion = false;
	if (ordenPago == null || ordenPago.getId() == null || ordenPago.getId().equals(0) || esEdicionStr != null){
		esEdicion = true;
	}
	
	Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
	
	Date fecha = ordenPago != null ? ordenPago.getAlta_fecha() : null; 
	if (fecha == null) {
		fechaHoy.setTime(new Date());
	}
	else{
		fechaHoy.setTime(fecha);
	}
	
	
	Calendar fechaDesde= CalendarFactoryUtil.getCalendar();
	
	Date fechadd = ordenPago != null ? ordenPago.getFechaDesde() : null; 
	if (fechadd == null) {
		fechaDesde.setTime(new Date());
	}
	else{
		fechaDesde.setTime(fechadd);
	}
	
	
	Calendar fechaHasta= CalendarFactoryUtil.getCalendar();
	
	Date fechahta = ordenPago != null ? ordenPago.getFechaHasta() : null; 
	if (fechahta == null) {
		fechaHasta.setTime(new Date());
	}
	else{
		fechaHasta.setTime(fechahta);
	}
	
	
	
	boolean esFarmacia = (ordenPago!=null && ordenPago.getId() != null && ordenPago.getId().intValue() != 0 && ordenPago.getItems() != null && ordenPago.getItems().size() >0);

	boolean mostrarBusquedaComp = (ordenPago == null || ordenPago.getId() == null || ordenPago.getId().intValue() == 0);
	String noMostrar = (String) request.getAttribute(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES);
	if (noMostrar != null && noMostrar.equals(WebKeysLiquidaciones.NO_MOSTRAR_BUSQUEDA_COMPROBANTES)){
		mostrarBusquedaComp = false;
	}
	
%>

<liferay-ui:error exception="<%= java.io.IOException.class %>" message="exception-archivo-zip" />
<liferay-ui:error exception="<%=ar.com.ospim.liquidaciones.DuplicateNumeroChequeException.class %>" message="duplicate-cheque" />
<liferay-ui:error exception="<%= ar.com.ospim.liquidaciones.DuplicateNumeroComprobanteException.class %>" message="duplicate-comprobante" />
<liferay-ui:error exception="<%= PagoMayorQueComprobanteException.class %>" message="pago-mayor-comprobante-exception" />
<liferay-ui:error exception="<%= OrdenPagoOspimSinComprobantes.class %>" message="op-ospim-sin-comprobantes" />
<liferay-ui:error exception="<%= OrdenPagoOspimSinPagos.class %>" message="op-ospim-sin-pagos" />
<liferay-ui:error exception="<%= OrdenPagoOspimTotalPagosMenorQueComprobantesException.class %>" message="op-ospim-pagos-menor-comprobantes" />
<liferay-ui:error exception="<%= OrdenPagoOspimTotalPagosNoAnticipoMenorIgualQueComprobantesException.class %>" message="op-ospim-pagos-no-antic-menor-comprobantes" />
<liferay-ui:error exception="<%= OrdenPagoOspimCreacionNuevoAnticipoException.class%>" message="op-ospim-pagos-nuevo-anticipo" />
<liferay-ui:error exception="<%= OrdenPagoOspimAnticiposNoUsadosException.class %>" message="op-ospim-pagos-anticipos-sin-usar" />
<liferay-ui:error exception="<%= ChequeSinChequeraException.class %>" message="cheque-sin-chequera" />
<liferay-ui:error key="cpteError" message="<%=(String)request.getAttribute(\"msgError1\") %>"  />

<form action="" method="post" name="<portlet:namespace />fm" >
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<fieldset class="block-labels">
<legend>
	Modificación Forma de Pago
</legend>
<table class="lfr-table">
	<tr>
		<td ><label><liferay-ui:message key="numero" />:</label></td>
		<td><input type="text" readonly="readonly" name="orden_pago_id" size = "15" value="<%= ordenPago != null && ordenPago.getId() != null ? ordenPago.getId().toString() : "" %>"/></td>
		<td colspan="4">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="acreedor" />:</label></td>
		<td colspan="5">
			<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_padron_entidades.jsp">
		  		<liferay-util:param name="esEditable" value='<%=String.valueOf(esEdicion && mostrarBusquedaComp) %>'/>
		  		<liferay-util:param name="cuit" value='<%= ordenPago != null && ordenPago.getAcreedor() != null ? ordenPago.getAcreedor().getCuit() : new String("")%>'/>
		  		<liferay-util:param name="sucu" value='<%=ordenPago != null && ordenPago.getAcreedor() != null ? ordenPago.getAcreedor().getSucursal() : new String("") %>'/>
		  		<liferay-util:param name="razon" value='<%=ordenPago != null && ordenPago.getAcreedor() != null ? ordenPago.getAcreedor().getRazon_soc() : new String("") %>'/>
		  		<liferay-util:param name="id_seccional" value='<%=ordenPago != null && ordenPago.getSeccional() != null ? String.valueOf(ordenPago.getSeccional().getId()) : new String("") %>'/>
		  		<liferay-util:param name="portlet_name" value='<%=portlet_name%>'/>
		  		<liferay-util:param name="buscar_destino" value='true'/>
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<% if (ordenPago  != null
							&& ordenPago.getReintegrosList()!=null && !ordenPago.getReintegrosList().isEmpty()) {
			%>
	<tr>
		<td><label><liferay-ui:message key="numero-lista" />:</label></td>
		<td>		
		<%=String.valueOf(ordenPago.getReintegrosList()) %>	
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<%
		}
	%>
	<tr>
		<td><label><liferay-ui:message key="observaciones" />:</label></td>
		<td colspan="5">
			<textarea rows="5" cols="80"  id="<portlet:namespace />obs" name="<portlet:namespace />obs" ><%=ordenPago != null && ordenPago.getObservaciones() != null ? ordenPago.getObservaciones()  : ""%></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">
			<label><liferay-ui:message key="corresp-desde" />:</label>
			<liferay-ui:input-date 
			dayParam="fechaDiaDesde"
			dayValue="<%= fechaDesde.get(Calendar.DATE)%>" 
			monthParam="fechaMesDesde"
			monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
			yearParam="fechaAnioDesde" 
			yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
			disabled="false" />
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label><liferay-ui:message key="corresp-hasta" />:</label>
			<liferay-ui:input-date 
			dayParam="fechaDiaHasta"
			dayValue="<%= fechaHasta.get(Calendar.DATE)%>" 
			monthParam="fechaMesHasta"
			monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
			yearParam="fechaAnioHasta" 
			yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
			yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
			disabled="false" />
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="3"  width="50%">
			<table>
				<% if (ordenPago!=null && ordenPago.getItems() != null && ordenPago.getItems().size() >0){%>
				<tr>
					<td>
						<liferay-ui:message key="beneficio-porcent" />:</label>
					</td>
					<td >
						<input size="15" id="<portlet:namespace />desc" name="<portlet:namespace />desc" onkeydown="allowOnlyDigitsAndDecimals(event)" size="8" maxlength="8" type="text" 
						<% if (!esEdicion){ %> readonly="readonly" <%}%> value="<%=ordenPago!=null && ordenPago.getDescuento() != null ? ordenPago.getDescuento().toString() : "0"%>"/>
					</td>
				</tr>
					<tr>
					<td>
						<liferay-ui:message key="descuento-por-drog" />:</label>
					</td>
					<td >
						<input size="15" id="<portlet:namespace />desc_drog" name="<portlet:namespace />desc_drog" onkeydown="allowOnlyDigitsAndDecimals(event)" size="8" maxlength="8" type="text"	
						<% if (!esEdicion){ %> readonly="readonly" <%}%> value="<%=ordenPago!=null && ordenPago.getDescuentoDrogueria() != null ? ordenPago.getDescuentoDrogueria().toString() : "0"%>"/>
					</td>
				</tr>
				<% }%>
			</table>
		</td>
		<td colspan="3"  width="50%">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">
			<liferay-util:include page="/html/portlet/utils/comprobantes/busqueda_comprobantes.jsp">
				<liferay-util:param name="esEditable" value="<%=String.valueOf(mostrarBusquedaComp)%>" />
				<liferay-util:param name="portlet_name" value='<%=portlet_name%>'/>
			</liferay-util:include>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
		<tr>
	<td colspan="6">
	<fieldset>
		<legend>
				<label><liferay-ui:message key="anticipos" />:</label>
		</legend>
			<table width="100%">
				<tr>
					<td width="100%">
						<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/anticipos_agregar.jsp">
							<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
							<liferay-util:param name="portlet_name" value='<%=portlet_name%>'/>
						</liferay-util:include>
					</td>
				</tr>
			</table>
	</fieldset>
	</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	
		<tr>
	<td colspan="6">
		<fieldset>
			<legend>
					<label><liferay-ui:message key="formas-de-pago" />:</label>
			</legend>
				<table width="100%">
					<tr>
						<td width="100%">
							<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/formas_pago_agregar.jsp">
								<liferay-util:param name="esEdicion" value="true"/>
								<liferay-util:param name="esAmtima" value="true"/>
								<liferay-util:param name="portlet_name" value='<%=portlet_name%>'/>
								<liferay-util:param name="modificaFormaPago" value="true"/>
							</liferay-util:include>
						</td>
					</tr>
				</table>
		</fieldset>
	</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6" align="left">
			<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveOP();return false;"/>&nbsp;&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	</table>
</fieldset>
</form>

<script type="text/javascript">

		function <portlet:namespace />saveOP() {	
			var importe=jQuery('#<portlet:namespace />importe').val();
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE %>";
			url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_forma_pago_orden_pago';			
			submitForm(document.<portlet:namespace />fm, url);				
			return true;
		}

		 
		 function recalcularTotales(){		 	 
			 var totalConceptos = jQuery("#total_conceptos").val();
				var totalPagos = jQuery("#total_formas_pago").val();
				var totalAnticipos = jQuery("#total_anticipos").val();
				jQuery("#<portlet:namespace />importe_pago").val(
						Math.round(
							(Math.round(parseFloat(totalConceptos)*100)/100 - 
							Math.round(parseFloat(totalAnticipos)*100)/100 - 
							Math.round(parseFloat(totalPagos)*100)/100)
						     *100)/100);
				jQuery("#total_pagar").val(
						Math.round(
									(Math.round(parseFloat(totalConceptos)*100)/100 - 
								     Math.round(parseFloat(totalAnticipos)*100)/100)
								     *100)/100);
		 }
		 
		 
		recalcularTotales();
		
		<% if (request.getAttribute("cheque_a_favor_de") != null && !request.getAttribute("cheque_a_favor_de").equals("")){ %>
			jQuery("#<portlet:namespace />a_favor_de").val("<%=request.getAttribute("cheque_a_favor_de") %>");
		<%}%>
		
		
</script>
