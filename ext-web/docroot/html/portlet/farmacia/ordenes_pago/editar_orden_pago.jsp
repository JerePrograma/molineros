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
	<liferay-ui:message key="alta-orden-pago" />
</legend>

 <div id="<portlet:namespace />retEsp"  <%if(!portlet_name.equals("uoma")){%>hidden='hidden' style="visibility: hidden;"<%}%>>
	  <fieldset class="block-labels">
         <legend>Retenciones Especiales</legend>
	     <table>
	     <tr>
	     <td><label>Retención IVA </label></td>
	     <td><input type="checkbox" id="<portlet:namespace />retIvaChk" name="<portlet:namespace />retIvaChk"/></td>
	     <td colspan="6">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	     <td><label>Retención Ganancias </label></td>
	     <td><input type="checkbox" id="<portlet:namespace />retGanChk" name="<portlet:namespace />retGanChk"/></td>
	     </tr>
	     </table>
	  </fieldset>   
 </div>
 <br>
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
		<td colspan="5" align="left">
			<input type="submit" value="<liferay-ui:message key="reintegros-asoc"/>" onClick="<portlet:namespace />verReintAsociados();return false;"/>
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
			<textarea rows="5" cols="80" <%if (!esEdicion) {%> disabled="disabled"  <%}%> id="<portlet:namespace />obs" name="<portlet:namespace />obs" ><%=ordenPago != null && ordenPago.getObservaciones() != null ? ordenPago.getObservaciones()  : ""%></textarea>
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
			disabled="<%= !esEdicion %>" />
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
			disabled="<%= !esEdicion %>" />
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
								<liferay-util:param name="esEdicion" value="<%=String.valueOf(esEdicion) %>"/>
								<liferay-util:param name="esAmtima" value="true"/>
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
		<td colspan="6" align="left">
	<% if (esEdicion){ %> 
			<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveOP();return false;"/>&nbsp;&nbsp;
	<%}%>
	<% if (ordenPago != null && ordenPago.getId() != null && ordenPago.getId().intValue() != 0){ %>
			<input type="submit" value="<liferay-ui:message key="print" />" onClick="<portlet:namespace />imprimirOP();return false;"/>
	<%} 	
	   if (ordenPago != null && ordenPago.isTieneRetencion()) {
	%>
			<input type="submit" value="<liferay-ui:message key="imprimir-comprobante-retencion" />" onClick="<portlet:namespace />imprimirRetencionGanancias();return false;"/>
    <%} 	
	   if (ordenPago != null && ordenPago.isTieneRetencionIIBB()) {
	%>
			<input type="submit" value="<liferay-ui:message key="imprimir-comprobante-retencion-iibb" />" onClick="<portlet:namespace />imprimirRetencionIIBB();return false;"/>
				
	<%
		}
	%>
	
	<%
	 if (ordenPago != null && ordenPago.isTieneRetencionIVA()) {
	%>
			<input type="submit" value="Imprimir Ret.IVA" onClick="<portlet:namespace />imprimirRetencionIVA();return false;"/>
				
	<%
		}
	%>
		</td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	</table>
</fieldset>
</form>
<%
	if (ordenPago!=null && ordenPago.getItems() != null && ordenPago.getItems().size() >0){%>
		<table class="lfr-table">
		<tr>
			<td colspan="6" align="center">
			
	<%
					List <OrdenPago.ItemOrdenPago> items = ordenPago.getItems();
										
						PortletURL portletURLReintegroPrestacion = renderResponse.createRenderURL();
						 		List<String> headerNamesReintegroPrestacion = new ArrayList<String>();
						 		headerNamesReintegroPrestacion.add("codigo");	
						 		headerNamesReintegroPrestacion.add("id-ospim");	
						 		headerNamesReintegroPrestacion.add("inte");	
						 		headerNamesReintegroPrestacion.add("beneficiario");		 		
						headerNamesReintegroPrestacion.add("nro-recetario");
						headerNamesReintegroPrestacion.add("troquel");
						headerNamesReintegroPrestacion.add("medicamento");
						headerNamesReintegroPrestacion.add("pvp");
						headerNamesReintegroPrestacion.add("total-ospim");
						headerNamesReintegroPrestacion.add("total-amtima");
						headerNamesReintegroPrestacion.add("debito");
						headerNamesReintegroPrestacion.add("dif-ospim");
						headerNamesReintegroPrestacion.add("dif-amtima");
						SearchContainer searchC= new SearchContainer(renderRequest, null, null,
						SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLReintegroPrestacion, headerNamesReintegroPrestacion,null);
					
						if(null!=items){
							int total=items.size();
							searchC.setTotal(total);
					 			 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
					 				List resultRows = searchC.getResultRows();
					 			 	for (int i = 0; i < items.size(); i++) {
					 			 		OrdenPago.ItemOrdenPago item = (OrdenPagoAmtima.ItemOrdenPago) items.get(i);
					 					ResultRow row = new ResultRow(item,item.getCodigoPrestador(), i);	 					
					 					row.addText(item.getCodigoPrestador());
					 					row.addText(String.valueOf(item.getAfiliado().getId_ospim()));
					 					row.addText(String.valueOf(item.getAfiliado().getInte()));
					 					row.addText(item.getAfiliado().getNombre());
					 					row.addText(item.getNroRecetario());
					 					row.addText(item.getTroquel());
					 					row.addText(item.getMedicamento());
					 					row.addText(item.getPvp().toString());
					 					row.addText(item.getTotalOspim().toString());
					 					row.addText(item.getTotalAmtima().toString());
					 					row.addText(item.getDebito());
					 					row.addText(item.getDifOspim().toString());
					 					row.addText(item.getDifAmtima().toString());
					 					resultRows.add(row);
					 			 	}
					 			}
				%>
 		
			<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchC%>" />
	

			</td>
		</tr>
		</table>
<%} %>

<script type="text/javascript">
		function <portlet:namespace />saveOP() {	
			var importe=jQuery('#<portlet:namespace />importe').val();
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= ordenPago== null || ordenPago.getId() == null || ordenPago.getId().equals(0) ? Constants.ADD : Constants.UPDATE %>";
			url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_orden_pago_entry';			
			submitForm(document.<portlet:namespace />fm, url);				
			return true;
		}

		function <portlet:namespace />imprimirOP(){
			<%if(portlet_name.equals("uoma")){%>
				window.location.href ="/pdfservlet/?accion=ordenPagoUoma&id_orden_pago_ini=<%=ordenPago!=null && ordenPago.getId() != null ? ordenPago.getId().toString() : ""%>&id_orden_pago_fin=<%=ordenPago!=null && ordenPago.getId() != null ? ordenPago.getId().toString() : ""%>" ;
			<%}else{%>
				window.location.href ="/pdfservlet/?accion=<%= esFarmacia ? "ordenPagoFarmacia" : "ordenPago"%>&id_orden_pago_ini=<%=ordenPago!=null && ordenPago.getId() != null ? ordenPago.getId().toString() : ""%>&id_orden_pago_fin=<%=ordenPago!=null && ordenPago.getId() != null ? ordenPago.getId().toString() : ""%>" ;
			<%}%>		
		}

		 function cambiaCuit(){
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
		 function <portlet:namespace />verReintAsociados(){
			 window.location.href ='/xlsservlet/?reporte=OP_REINTEGRO_FARMACIA&idLista=<%=ordenPago != null &&  ordenPago.getReintegrosList() != null && !ordenPago.getReintegrosList().isEmpty() ? ordenPago.getReintegrosList().toString() : new String("")%>';
		}
		 
		 function utilizarObservaciones(){
				var obs = jQuery("#obs_comprobantes").val();				
				jQuery("#<portlet:namespace />obs").val(obs);
		}
		 
		 recalcularTotales();
		 
		 function buscarAnticipos(){
		 		
				var cuitEntidad = jQuery("#<portlet:namespace />cuit_entidad").val();
				var sucuEntidad = jQuery("#<portlet:namespace />sucursal_entidad").val();
				var idSeccional = jQuery("#<portlet:namespace />id_seccional").val();

				jQuery('#<portlet:namespace />agregandoAnticipo').show();	

				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_op_pago';
				url += '&cuit_entidad=' + cuitEntidad;
				url += '&sucu_entidad=' + sucuEntidad;
				url += '&id_seccional=' + idSeccional;
				url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
				url += '&buscar_anticipos=buscar_anticipos';
				url += '&esEdicion=<%=esEdicion%>';
				url += '&rnd=' + Math.floor(Math.random()*100);
				
				jQuery('#<portlet:namespace />anticipos').load(url, function() {
															jQuery('#<portlet:namespace />agregandoAnticipo').hide();	
															 jQuery('#<portlet:namespace />nro_anticipo').val("");
															 recalcularTotales();
											   }
				 );	
				
		}
		
		function sugerirNumero(){			
			buscarAnticipos();
			recalcularTotales()
		}
		
		function actualizarValorAnticipos(anticipo){
				var importe=anticipo.value;
				var anticipo=anticipo.id;				
				var cuitEntidad = jQuery("#<portlet:namespace />cuit_entidad").val();
				var sucuEntidad = jQuery("#<portlet:namespace />sucursal_entidad").val();
				var idSeccional = jQuery("#<portlet:namespace />id_seccional").val();
				jQuery('#<portlet:namespace />agregandoAnticipo').show();	

				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/agregar_op_pago';
				url += '&cuit_entidad=' + cuitEntidad;
				url += '&sucu_entidad=' + sucuEntidad;
				url += '&id_seccional=' + idSeccional;
				url += '&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>'
				url += '&buscar_anticipos=modificar_anticipo';
				url += '&anticipo='+encodeURI(anticipo);
				url += '&importeAnticipo='+Math.abs(importe);
				url += '&rnd=' + Math.floor(Math.random()*100);
				jQuery('#<portlet:namespace />anticipos').load(url, function() {
															jQuery('#<portlet:namespace />agregandoAnticipo').hide();	
															 jQuery('#<portlet:namespace />nro_anticipo').val("");
															 recalcularTotales();
											   }
				 );				
		}
		
		function sugerirRazonSocialChequeYDestino(){
			<% if (request.getAttribute("cheque_a_favor_de") != null && !request.getAttribute("cheque_a_favor_de").equals("")){ %>
			jQuery("#<portlet:namespace />a_favor_de").val("<%=request.getAttribute("cheque_a_favor_de") %>");
			<%} else { %>
			var cuitEntidad = jQuery("#<portlet:namespace />cuit_entidad").val();
			var sucuEntidad = jQuery("#<portlet:namespace />sucursal_entidad").val();
			var idSeccional = jQuery("#<portlet:namespace />id_seccional").val();
			
			 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/buscar_ultima_razon_social_cheque_op';
				url += '&cuit_entidad=' + cuitEntidad;
				url += '&sucu_entidad=' + sucuEntidad;
				url += '&id_seccional=' + idSeccional;				
				url += '&rnd=' + Math.floor(Math.random()*100);				
				url +='&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>';				
				
				
			jQuery.ajax({   
				url: url,
				success: function(data){	
					var obj = jQuery.parseJSON(data);
					jQuery("#<portlet:namespace />a_favor_de").val(obj.razon=null || 'null'==obj.razon?"":obj.razon);
					jQuery("#<portlet:namespace />destino").val(obj.destino);
					jQuery("#<portlet:namespace />cbu_sugerido").val(obj.cbu==null || 'null'==obj.cbu?"":obj.cbu);
//					jQuery("#<portlet:namespace />email_cbu").val(obj.email);
				}
			});
			<%}%>
		}
		
		<% if (request.getAttribute("cheque_a_favor_de") != null && !request.getAttribute("cheque_a_favor_de").equals("")){ %>
			jQuery("#<portlet:namespace />a_favor_de").val("<%=request.getAttribute("cheque_a_favor_de") %>");
		<%}%>
		
		function filtrarConceptosUOMA(){		
		}
		function <portlet:namespace />imprimirRetencionGanancias(){			
			window.location.href ="/pdfservlet/?accion=comproRetenGanancias&id_ini=<%=ordenPago != null && ordenPago.getId() != null ? ordenPago
						.getId().toString()
						: ""%>&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>";							
		}
			
		
		function <portlet:namespace />imprimirRetencionIIBB(){			
			window.location.href ="/pdfservlet/?accion=comproRetenIIBB&id_ini=<%=ordenPago != null && ordenPago.getId() != null ? ordenPago
						.getId().toString()
						: ""%>&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>&tipo=IIBB";							
		}
		
		function <portlet:namespace />imprimirRetencionIVA(){			
			window.location.href ="/pdfservlet/?accion=comproRetenIVA&id_ini=<%=ordenPago != null && ordenPago.getId() != null ? ordenPago
						.getId().toString()
						: ""%>&entidad=<%=portlet_name.equals("farmacia")?WebKeysGlobal.AMTIMA:(portlet_name.equals("uoma")?WebKeysGlobal.UOMA:WebKeysGlobal.OSPIM)%>&tipo=RIVA";							
		}
		
		sugiereDatosPorDefecto();
		function sugiereDatosPorDefecto(){
			try{
				sugerirNumero();
			}catch(err){}
		}
</script>
