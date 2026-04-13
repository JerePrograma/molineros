<%@ page import="ar.com.ospim.global.services.ChequesReutilizadosException" %>
<%@ page import="ar.com.ospim.global.services.ComprobantesYaPagadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposUtilizadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.ComprobantesAnuladosException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposNoPagadosException" %>
<%@ page import="ar.com.ospim.liquidaciones.FechaBajaMenorQueAltaException" %>
<%@ page import="ar.com.ospim.liquidaciones.AnticiposReUtilizadosException" %>
<%@ page import="ar.com.ospim.tesoreria.OpConChequesCanjeadosException" %>
<%@ page import="ar.com.ospim.tesoreria.OpCreadaEnCanjeException" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>


<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%
	
	String portlet_name=null;
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "liquidaciones";
	}
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}	 	

	Calendar fechaPago = CalendarFactoryUtil.getCalendar();
	fechaPago.setTime(new Date());
	
	Date fechaBaja = (Date)request.getAttribute(WebKeysLiquidaciones.FECHA_BAJA_OP);
	
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	if (fechaBaja != null) {
		fechaPago.setTime(fechaBaja);
	}
	
	String isAmtimaStr = (String)request.getAttribute(WebKeysTesoreria.IS_AMTIMA);
	boolean isOspim = true;
	if ( isAmtimaStr != null &&  isAmtimaStr.equals(WebKeysTesoreria.IS_AMTIMA)){
		isOspim = false;
	}
	
	String isFarmaciaStr = (String) request.getAttribute(WebKeysTesoreria.IS_FARMACIA);
	boolean isFarmacia = false;
	if (isFarmaciaStr != null &&  isFarmaciaStr.equals(WebKeysTesoreria.IS_FARMACIA)){
		isFarmacia = true;
	}
	
%>
<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />
	</c:when>
</c:choose>
	<liferay-ui:error exception="<%= AnticiposUtilizadosException.class %>" message="exception-anticipos_utilizados" />
	<liferay-ui:error exception="<%= ChequesReutilizadosException.class %>" message="exception-cheques-reutilizados" />
	<liferay-ui:error exception="<%= ComprobantesYaPagadosException.class %>" message="exception-comprobantes-ya-pagados" />
	<liferay-ui:error exception="<%= ComprobantesAnuladosException.class %>" message="exception-comprobantes-anulados" />
	<liferay-ui:error exception="<%= AnticiposNoPagadosException.class %>" message="exception-anticipos-no-pagados" />
	<liferay-ui:error exception="<%= FechaBajaMenorQueAltaException.class %>" message="exception-fecha-baja-menor-que-alta" />
	<liferay-ui:error exception="<%= AnticiposReUtilizadosException.class %>" message="exception-anticipos-reutilizados" />
	<liferay-ui:error exception="<%= OpConChequesCanjeadosException.class %>" message="exception-op-con-cheques-canjeados" />
	<liferay-ui:error exception="<%= OpCreadaEnCanjeException.class %>" message="exception-op-creada-en-canje" />
	<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="baja-menor-fecha-contable" />
<% if (request.getAttribute("chequesCanjeados")!= null) {
	List<Cheque> cheques = (List<Cheque>) request.getAttribute("chequesCanjeados");
	for (Cheque cheque : cheques) {
	%>
	<span class="portlet-msg-error">Cheque canjeado: <%=cheque.getNumero().toString()%></span>
<%} 
}%>

<table>
	<% if (fechaBaja != null) {%>
	<tr>
		<td>
			Fecha de baja actual:&nbsp;<%= formatter.format(fechaBaja) %>
		</td>
	</tr>
	<%} %>
	<tr>
		<td>
			<input type="hidden" id="<portlet:namespace />id_orden_pago" value="<%= (String)request.getAttribute("id_op")%>"/>
			Nueva&nbsp;<liferay-ui:message key="baja-op" />
		</td>
		<td>
			<liferay-ui:input-date
			dayParam="fechaBajaDia"
			dayValue="<%=fechaPago.get(Calendar.DATE)%>" 
			monthParam="fechaBajaMes"
			monthValue="<%=  fechaPago.get(Calendar.MONTH)%>"				
			yearParam="fechaBajaAnio"
			yearValue="<%= fechaPago.get(Calendar.YEAR)%>"
			yearRangeStart="<%= fechaPago.get(Calendar.YEAR) -20 %>"	
			yearRangeEnd="<%= fechaPago.get(Calendar.YEAR) + 20%>"
			firstDayOfWeek="<%= fechaPago.getFirstDayOfWeek() - 1 %>"
			disabled="false" />
		</td>
		<td>
			<%if (fechaBaja == null) { %>
				<input type="button" value="<liferay-ui:message key="anular" />" onClick="<portlet:namespace />grabarAnulacionOP();" />
			<%} %>
			<%if (fechaBaja != null) { %>
						<input type="button" value="<liferay-ui:message key="actualizar-anulacion" />" onClick="<portlet:namespace />grabarAnulacionOP();" />
			&nbsp;&nbsp;<input type="button" value="<liferay-ui:message key="reactivar-op" />" onClick="<portlet:namespace />reactivarOPFromPupup();" />
				<%if(portlet_name.equals("liquidaciones")){%>			
			    &nbsp;&nbsp;<input type="submit" value="<liferay-ui:message key="imprimir-anulacion" />" onClick="<portlet:namespace />imprimirOPAnulada();return false;"/>
			    <%}%>
			<%} %>
			<span align="center" id="<portlet:namespace />guardando_anulacion">
				<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
			</span>	
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
	<tr>
		<td>
			<liferay-ui:message key="anulacion-formas-pago" />
		</td>
	</tr>
</table>
<portlet:defineObjects />
<%
	List<CuentaBancaria> ctas = (List<CuentaBancaria>) request
			.getSession().getAttribute(
					WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);

	List<FormaPago> pagos = (List<FormaPago>) request
			.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_ANULACION_FORMA_PAGO);

	PortletURL portletURLTercerizadora = renderResponse
			.createRenderURL();
	List<String> headerNamesTercerizadora = new ArrayList<String>();
	headerNamesTercerizadora.add("tipo");
	headerNamesTercerizadora.add("numero");
	headerNamesTercerizadora.add("importe");
	headerNamesTercerizadora.add("cuenta-bancaria");
	headerNamesTercerizadora.add("descripcion");
	headerNamesTercerizadora.add("a-nombre-de");
	headerNamesTercerizadora.add("baja-actual");
	headerNamesTercerizadora.add("anular");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURLTercerizadora,
			headerNamesTercerizadora, LanguageUtil.get(pageContext,
					"no-cheques-were-found"));

	int cant = 0;
	if (null != pagos) {
		int total = pagos.size();
		List resultRowsInspector = searchContainer.getResultRows();
		for (int i = 0; i < pagos.size(); i++) {
			FormaPago pago = pagos.get(i);
			if (!pago.getTipo().equals("Anticipo")) {
				ResultRow row = new ResultRow(pago,	pago.getNumeroStr(), i);
				row.addText(pago.getTipo());
				row.addText(pago.getNumeroStr());
				row.addText(pago.getImporte().toString());
				if (pago.getCuentaBancaria() != null && pago.getCuentaBancaria().getId_cuenta_bcria() != 0) {
					int index = ctas.indexOf(pago.getCuentaBancaria());
					row.addText(ctas.get(index).getDescripcion() + " "
							+ ctas.get(index).getNro_cuenta() + "/"
							+ ctas.get(index).getSucursal());
				} else {
					row.addText("");
				}
				row.addText(pago.getDescripcion());
				row.addText(pago.getANombreDe() != null ? pago.getANombreDe() : "");
				StringBuilder fechaBajaStr = new StringBuilder();
				if (pago.getPago() instanceof Cheque){
					fechaBajaStr.append(((Cheque)pago.getPago()).getBaja_fechaAsString());
				} else {
					if (fechaBaja != null){
						fechaBajaStr.append(formatter.format(fechaBaja));
					}
				}
				row.addText(fechaBajaStr.toString());
				
				StringBuilder sb = new StringBuilder();
				String link=null;
				if (pago.getPago() instanceof Cheque){
					cant++;
					if (pago.getOtraOpCheque() != null){
						sb.append("Cheque reutilizado en op: " + pago.getOtraOpCheque().toString());	
					} else {
					
						Cheque ch = (Cheque) pago.getPago();

						sb.append("<input type='checkbox' id='anular_cheque_"+
						ch.getNumeroStr()+"_"+
						ch.getCuentaBancaria().getId_cuenta_bcria()+"_"+
						ch.getBanco().getId_banco() +"_"+
						
						"' name='anular_cheque_' ");
						if (fechaBaja == null || ch.getBaja_fecha() != null){
							sb.append("checked='checked'");
						}
						sb.append(" />");
					}
				} else {
					sb.append("<input type='checkbox' checked='checked' disabled='disabled'/>");
				}
				row.addText(sb.toString());
				resultRowsInspector.add(row);
			}
		}
		searchContainer.setTotal(total);
	}
%>

<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainer%>" />
<input type="hidden" id="cantidad_cheques" value="<%=String.valueOf(cant)%>"/>
<script type="text/javascript">
 function <portlet:namespace />grabarAnulacionOP(){
	 jQuery('#<portlet:namespace />guardando_anulacion').show();
	var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaBajaDia");
	var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaBajaMes");
	var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaBajaAnio");
	var cantidad_cheques = document.getElementById("cantidad_cheques");
	
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/anular_orden_pago'
			+'&fechaBajaDia='+fechaDesdeDia.value
			+'&fechaBajaMes='+fechaDesdeMes.value
			+'&fechaBajaAnio='+fechaDesdeAnio.value;
	 
	 
	 var elementos = document.getElementsByName("anular_cheque_");
	 for (var i = 0;  i < elementos.length; i++){
		 if (elementos[i].checked){
			 url+="&anular_cheque_" + i + "=";
			 url+=elementos[i].id;
		 }
	 }
	 
	url += '&orden_pago_id=' + document.getElementById("<portlet:namespace />id_orden_pago").value;
	url += '&accion=borrar';
	<% if (!isOspim) { %>
	url += '&isAmtima=isAmtima';
	<% } %>
	url += '&cantidad_cheques=' + cantidad_cheques.value;
	url += '&rnd=' + Math.floor(Math.random()*100);
	jQuery(popup).load(url);
		
 }
 
 function <portlet:namespace />reactivarOPFromPupup(){
	 var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/anular_orden_pago'
		 jQuery('#<portlet:namespace />guardando_anulacion').show();
	url += '&orden_pago_id=' + document.getElementById("<portlet:namespace />id_orden_pago").value;
	url += '&accion=reactivar';
	url += '&rnd=' + Math.floor(Math.random()*100);
	<% if (!isOspim) { %>
	url += '&isAmtima=isAmtima';
	<% } %>
	jQuery(popup).load(url);
 }
 
 function <portlet:namespace />imprimirOPAnulada(){
	 	var id = document.getElementById("<portlet:namespace />id_orden_pago").value;
		<% if (!isOspim) { %> 
				window.location.href ="/pdfservlet/?accion=<%= isFarmacia ? "ordenPagoFarmacia" : "ordenPago"%>&id_orden_pago_ini=" +  id + "&id_orden_pago_fin=" + id ;
		<% } else {%>
			window.location.href ="/pdfservlet/?accion=ordenPagoOspim&id_ini=" + id;
		<% }%>
	}
 
 jQuery('#<portlet:namespace />guardando_anulacion').hide();
</script>