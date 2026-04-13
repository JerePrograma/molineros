<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<portlet:defineObjects/>
<liferay-ui:error exception="<%= FechaMenorACierreContableException.class %>" message="movimiento-menor-fecha-contable" />

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
	MovimientoBancario movSess= (MovimientoBancario)request.getSession().getAttribute(WebKeysTesoreria.MOV_BCRIO_EN_EDICION);
	List<CuentaBancaria> ctas=(ArrayList<CuentaBancaria>) portletSession.getAttribute(WebKeysTesoreria.CUENTAS_BCRIAS,PortletSession.APPLICATION_SCOPE);
	List<Chequera> chequeras= (ArrayList<Chequera>)portletSession.getAttribute(WebKeysTesoreria.CHEQUERAS_EN_SESSION,PortletSession.APPLICATION_SCOPE);
	List<TipoMovBcrio> tiposMov=null;
	List<TipoTrxBancaria> trxs=null;
	if(portlet_name.equals("farmacia")){
		tiposMov= (ArrayList<TipoMovBcrio>)request.getAttribute(WebKeysTesoreria.TIPOS_MOV_BCRIO_AMTIMA_EN_REQUEST);		
		trxs=(ArrayList<TipoTrxBancaria>) portletSession.getAttribute(WebKeysTesoreria.TIPOS_TRX_BCRIA_AMTIMA_EN_SESSION,PortletSession.APPLICATION_SCOPE);
	}else{
		tiposMov= (ArrayList<TipoMovBcrio>)request.getAttribute(WebKeysTesoreria.TIPOS_MOV_BCRIO_EN_REQUEST);		
		trxs=(ArrayList<TipoTrxBancaria>) portletSession.getAttribute(WebKeysTesoreria.TIPOS_TRX_BCRIA_EN_SESSION,PortletSession.APPLICATION_SCOPE);		
	}
	
	
	
	Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
	fechaDesde.setTime(new Date());
	Calendar fecha_compro= null;
	Calendar fecha_mov= null;
	Calendar fechaSur= null;
	
	
	if(null!=movSess && null!=movSess.getFecha_comprobante()){
		fecha_compro=CalendarFactoryUtil.getCalendar();
		fecha_compro.setTime(movSess.getFecha_comprobante());
	}
	
	if(null!=movSess && null!=movSess.getFecha_movimiento()){
		fecha_mov=CalendarFactoryUtil.getCalendar();
		fecha_mov.setTime(movSess.getFecha_movimiento());
	}
	
	if(null!=movSess && null!=movSess.getFechaSur()){
		fechaSur=CalendarFactoryUtil.getCalendar();
		fechaSur.setTime(movSess.getFechaSur());
	}
	
	String deb_cred=null;
	if(movSess!=null && movSess.isDeb_cred()){
		deb_cred="DEBITO";
	}else if(movSess!=null && !movSess.isDeb_cred()){
		deb_cred="CREDITO";
	}
	
	boolean esEdicion = true;
	if (movSess != null && movSess.getChequesCanjeados() != null && movSess.getChequesCanjeados().size()>0){
		esEdicion = false;	
	}
	
%>
<form action="" method="post" name="<portlet:namespace />mov" >
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />					
	<fieldset class="block-labels">
		<legend><liferay-ui:message key="grupo-alta-movimiento-bancario" /></legend>				
		<table class="lfr-table">
			<% if (movSess!= null && movSess.getId_movimiento() != 0){ %>
			<tr>
				<td><label><liferay-ui:message key="numero" />:</label></td>
				<td><%=movSess.getId_movimiento()%></td>
				<td>&nbsp;</td>
			</tr>
			<% } %>
			<tr><td colspan="3">&nbsp;</td></tr>
			<tr>
				<td><label><liferay-ui:message key="descripcion" />:</label></td>
				<td colspan="2">						
					<input id="<portlet:namespace />descripcion" name="<portlet:namespace />descripcion" size="40" maxlength="40" type="text" value="<%=movSess!=null?movSess.getDescripcion():""%>" <% if (!esEdicion) {%> readonly="readonly" <%} %>/>
				</td>
			</tr>
			<tr><td colspan="3">&nbsp;</td>
			</tr>
			<tr>
				<td><label><liferay-ui:message key="cta-bcria" />:</label></td>
				<td colspan="2">
					<select name="<portlet:namespace/>cta_bancaria" id="<portlet:namespace/>cta_bancaria" <% if (!esEdicion) {%> disabled="disabled" <%} %>>									
						<% for (CuentaBancaria ctaBcria : ctas) {
							if(portlet_name.equals("farmacia") && ctaBcria.getEntidad().equals("A")){
								%>										
								 <option <%= movSess != null && movSess.getCta_bcria().getId_cuenta_bcria()==ctaBcria.getId_cuenta_bcria() ? "selected" : ""  %> value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>											
								<%  }else if(portlet_name.equals("tesoreria") && ctaBcria.getEntidad().equals("O")){%>
								 <option <%= movSess != null && movSess.getCta_bcria().getId_cuenta_bcria()==ctaBcria.getId_cuenta_bcria() ? "selected" : ""  %> value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>											
								<%  }else if(portlet_name.equals("uoma") && ctaBcria.getEntidad().equals("U")){%>
								 <option <%= movSess != null && movSess.getCta_bcria().getId_cuenta_bcria()==ctaBcria.getId_cuenta_bcria() ? "selected" : ""  %> value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>
								<%  }
							}%>
					</select>
				</td>
			</tr>
			<tr><td colspan="3">&nbsp;</td>
			<tr>				
				<td><label><liferay-ui:message key="fecha-mov" />:</label></td>
				<td colspan="2">
					<liferay-ui:input-date
						dayParam="fechaMovDia"
						dayValue="<%= fecha_mov!=null?fecha_mov.get(Calendar.DATE):fechaDesde.get(Calendar.DATE)%>"
						monthParam="fechaMovMes"
						monthValue="<%= fecha_mov!=null?fecha_mov.get(Calendar.MONTH):fechaDesde.get(Calendar.MONTH) %>"
						yearParam="fechaMovAnio"
						yearValue="<%= fecha_mov!=null?fecha_mov.get(Calendar.YEAR):fechaDesde.get(Calendar.YEAR) %>"
						yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
						yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
						firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek()- 1 %>"
						disabled="<%= !esEdicion %>" />
				</td>
			</tr>
			<tr><td colspan="3">&nbsp;</td>
			<tr>
				<td><label><liferay-ui:message key="movimiento" />:</label></td>
				<td colspan="2">
						<select name="<portlet:namespace/>tipo_mov" id="<portlet:namespace/>tipo_mov" 
						onchange="cambiarMovimiento();" 
						<%-- <%= movSess != null && movSess.getId_movimiento() != 0 ? new String("disabled='disabled'") : new String("") %> --%>
						>									
							<% for (TipoMovBcrio mov : tiposMov) { %>
									<option <%= movSess != null && movSess.getTipo_mov().getId_tipo_mov()==mov.getId_tipo_mov() ? "selected" : ""  %> value="<%= mov.getId_tipo_mov()%>"><%=mov.getDescripcion()%></option>											
							<% } %>
						</select>
				</td>
			</tr>
			<tr><td colspan="3">&nbsp;</td>
			<tr>
				<td><label><liferay-ui:message key="debito-credito" />:</label></td>
				<td colspan="2">
					<select name="<portlet:namespace/>debcred" id="<portlet:namespace/>debcred" <% if (!esEdicion) {%> disabled="disabled" <%} %>>									
							<% for (String debcred : WebKeysTesoreria.DEBITO_CREDITO) { %>
								<option <%= deb_cred!=null && deb_cred.trim().equals(debcred.trim()) ? "selected" : ""  %> value="<%= debcred %>"><%=debcred%></option>										
							<% } %>
					</select>
				</td>
			</tr>
			<tr><td colspan="3">&nbsp;</td>
			<tr>
			<tr>
			<td valign="top" colspan="3">
			  <div id="<portlet:namespace />divSUR">
			  <table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="nro-expediente-sur" />:</label></td>												
						<td><input id="<portlet:namespace />nro_exp_sur" name="<portlet:namespace />nro_exp_sur" size="20" maxlength="20" type="text" value="<%=movSess!=null?movSess.getNroExpedienteSur():""%>" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="fecha-pago-sur" />:</label></td>
						<td>
									<liferay-ui:input-date
										dayParam="fechaSurDia"
										dayValue="<%=fechaSur!=null?fechaSur.get(Calendar.DATE):fechaDesde.get(Calendar.DATE)%>"
										monthParam="fechaSurMes"
										monthValue="<%= fechaSur!=null?fechaSur.get(Calendar.MONTH):fechaDesde.get(Calendar.MONTH)%>"
										yearParam="fechaSurAnio"
										yearValue="<%= fechaSur!=null?fechaSur.get(Calendar.YEAR):fechaDesde.get(Calendar.YEAR)%>"
										yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
										yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
										firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
										disabled="<%= !esEdicion %>" />
						</td>	
					</tr>
				</table>
				</div>
			</td>
		</tr>		
			
			<!-- <tr><td colspan="3">&nbsp;</td>
			<tr>
				<td><label><liferay-ui:message key="tipo-trx" />:</label></td>
				<td>
					<select name="<portlet:namespace/>tipotrxbcria" id="<portlet:namespace/>tipotrxbcria" onChange="javascript=<portlet:namespace/>cambioTrx();">									
								<for (TipoTrxBancaria trx : trxs) { >
									<option < movSess != null && movSess.getTipo_trx_bcria().getId_tipo_trx()==trx.getId_tipo_trx() ? "selected" : ""  > value="<trx.getId_tipo_trx()>"><trx.getDescripcion()></option>
								<>
					</select>							
				</td>
				<td>
						<div id="<portlet:namespace />chequera" name="<portlet:namespace />chequera">
							<label><liferay-ui:message key="chequera" />:</label> &nbsp;
							<select name="<portlet:namespace/>id_chequera" id="<portlet:namespace/>id_chequera">									
									<for (Chequera chequera : chequeras) { >
										<option < movSess != null && movSess.getChequera().getId_chequera()==chequera.getId_chequera() ? "selected" : ""  > value="< chequera.getId_chequera()>"><=chequera.getDescripcion()></option>										
									<>
							</select>
							<input < movSess != null && movSess.isImprime_cheque() ? "checked" : ""  > type="checkbox" id="<portlet:namespace />imprimech" name="<portlet:namespace />imprimech"/>
							<liferay-ui:message key="imprime-cheque" />
							<input < movSess != null && movSess.isNo_a_la_orden() ? "checked" : ""  > type="checkbox" id="<portlet:namespace />noorden" name="<portlet:namespace />noorden"/>
							<liferay-ui:message key="no-a-la-orden" />
						</div>
				</td>
				
			</tr> -->
		<tr><td colspan="3">&nbsp;</td>
		<tr>
		  <td colspan="3">
		      <div id="<portlet:namespace />divManual">
			<table  class="lfr-table">
				<tr>				
				<td><label><liferay-ui:message key="fecha-comprobante" />:</label></td>
				<td colspan="2">
					<liferay-ui:input-date
						dayParam="fechaCompDia"
						dayValue="<%=fecha_compro!=null?fecha_compro.get(Calendar.DATE):fechaDesde.get(Calendar.DATE)%>"
						monthParam="fechaCompMes"
						monthValue="<%= fecha_compro!=null?fecha_compro.get(Calendar.MONTH):fechaDesde.get(Calendar.MONTH)%>"
						yearParam="fechaCompAnio"
						yearValue="<%= fecha_compro!=null?fecha_compro.get(Calendar.YEAR):fechaDesde.get(Calendar.YEAR)%>"
						yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
						yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 120 %>"
						firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
						disabled="<%= !esEdicion %>" />
						<a href="javascript:void(0)" onclick="copiarFecha()">copiar fecha de movimiento</a>
				</td>
				</tr>
				<tr><td colspan="3">&nbsp;</td>
				<tr>
					<td><label><liferay-ui:message key="nro-comprobante" />:</label></td>
					<td colspan="2">
						<input id="<portlet:namespace />nroComp" name="<portlet:namespace />nroComp" size="15" maxlength="15" type="text" value="<%=movSess!=null && movSess.getNro_comprobante() != null ?movSess.getNro_comprobante():"" %>" <% if (!esEdicion) {%> readonly="readonly" <%} %>/>
					</td>
				</tr>	
				<tr><td colspan="3">&nbsp;</td></tr>
				<tr>				
					<td><label><liferay-ui:message key="importe-movimiento" />:</label></td>
					<td colspan="1">
						<input id="<portlet:namespace />importe" name="<portlet:namespace />importe" size="15" maxlength="15" type="text" value="<%=movSess!=null && movSess.getImporte() != null ? movSess.getImporte():""%>" <% if (!esEdicion) {%> readonly="readonly" <%} %>
						 onkeyup="<portlet:namespace />formatearNro();"/>							
					</td>
					<td><span style="font-size: 13pt; color: black; ">
						<label id="<portlet:namespace />importe_lb"></label>
						</span>
					</td>
				</tr>
			</table>
			</div>
		  </td>
		</tr>
		<tr><td colspan="3">&nbsp;</td>
		<tr>
			<td valign="top" colspan="3">
			  <div id="<portlet:namespace />divCheque">
			  <table class="lfr-table">
					<tr>
						<td><label><liferay-ui:message key="cuit" />:</label></td>
						<td><input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td><label><liferay-ui:message key="numero" />:</label></td>
						<td><input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="10" maxlength="10" type="text" value="" /></td>
						<td>&nbsp;</td>
						<td>							
							<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="<portlet:namespace />buscarChequesRecibidosParametrico();"/>							
					 	    &nbsp;<input type="button"  value="<liferay-ui:message key="buscar-todos-cheques-recibidos" />" onClick="<portlet:namespace />buscarChequesRecibidos();" ><br/>
						</td>
					</tr>
				</table>	   
					<div align="center" id="<portlet:namespace />agregandoCheque">
					<table style="align: center;">
						<tr>
							<td><liferay-ui:message key='buscando' /></td>
							<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>
					</div>
					<div align="center" id="<portlet:namespace />cheques_recibidos"> 
					<liferay-util:include page="/html/portlet/tesoreria/bancos/mov_bcrio_cheques_recibidos_search_result.jsp"/>
					</div>
				</div>
			</td>
		</tr>		
		<tr>
			<td valign="top" colspan="3">
			  <div id="<portlet:namespace />divChequeDepo">
				   <table class="lfr-table">
						<tr>
							<td><label><liferay-ui:message key="cuit" />:</label></td>
							<td><input id="<portlet:namespace />cuit_depo" name="<portlet:namespace />cuit_depo" size="13" maxlength="11" type="text" value="" /></td>
							<td>&nbsp;</td>
							<td><label><liferay-ui:message key="numero" />:</label></td>
							<td><input id="<portlet:namespace />numero_depo" name="<portlet:namespace />numero_depo" size="10" maxlength="10" type="text" value="" /></td>
							<td>&nbsp;</td>
							<td>							
								<input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onClick="<portlet:namespace />buscarChequesDepositadosParametrico();"/>							
						 	    &nbsp;<input type="button"  value="<liferay-ui:message key="buscar-todos-cheques-depositados" />" onClick="<portlet:namespace />buscarChequesDepositados();" ><br/>
							</td>
						</tr>
					</table>	   
					<div align="center" id="<portlet:namespace />agregandoChequeDepo">
					<table style="align: center;">
						<tr>
							<td><liferay-ui:message key='buscando' /></td>
							<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>
					</div>
					<div align="center" id="<portlet:namespace />cheques_depositados"> 
					<liferay-util:include page="/html/portlet/tesoreria/bancos/mov_bcrio_cheques_depositados_search_result.jsp"/>
					</div>
				</div>
			</td>
		</tr>			
		<tr>
		<td valign="top" colspan="3">
				<div align="center" id="<portlet:namespace />cheques_canjeados"> 
					<liferay-util:include page="/html/portlet/tesoreria/bancos/mov_bcrio_cheques_canjeados_search_result.jsp"/>
				</div>	
			</td>
		</tr>	
		<tr>
			<td valign="top" colspan="3">
			  <div id="<portlet:namespace />divEfectivo">
			  		<input type="button"  value="<liferay-ui:message key="buscar-todos-efectivos-ingresados" />" onClick="<portlet:namespace />buscarEfectivoIngresado();" ><br/>
					<div align="center" id="<portlet:namespace />agregandoEfectivo">
					<table style="align: center;">
						<tr>
							<td><liferay-ui:message key='buscando' /></td>
							<td align="center"><img alt="<liferay-ui:message key='buscando'/>"	src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>
					</div>
					<div align="center" id="<portlet:namespace />efectivo_recibido"> 
					<liferay-util:include page="/html/portlet/tesoreria/bancos/mov_bcrio_efectivo_recibido_search_result.jsp"/>
					</div>
				</div>
			</td>
		</tr>						
		<tr><td colspan="2"><input type="hidden" id="<portlet:namespace />id_mov" name="<portlet:namespace />id_mov" value="<%= movSess!=null?movSess.getId_movimiento():""%>"/>&nbsp;</td></tr>
		<% if (esEdicion) {%> 
		<tr>						
			<td colspan="3" style="align:center;">
				<input id="<portlet:namespace />save" value="<liferay-ui:message key="save"/>" title="<liferay-ui:message key="save" />" type="button" onClick="javascript:<portlet:namespace/>guardaMov();"/>
				<% if (movSess != null && movSess.getId_movimiento() != 0) { %>							
					&nbsp;<input id="<portlet:namespace />nuevoMov" value="<liferay-ui:message key="nuevo-movimiento"/>" title="<liferay-ui:message key="nuevo-movimiento" />" type="button" onClick="javascript:<portlet:namespace />nuevoMovimiento();"/>
				<%} %>
			</td>
		</tr>	
		<%} %>								
	  </table>	      	  
  </fieldset>		
  <input type="hidden" name="fromRedireccion" id="fromRedireccion" value=""/>
</form>			
<script type="text/javascript">	
	function cambiarMovimiento(){
		var ocultarImporte = false; 
		
		if (document.getElementById("<portlet:namespace/>tipo_mov").value == '<%= TipoMovBcrio.DEPOSITAR_CHEQUES %>') {
			jQuery('#<portlet:namespace />divCheque').show();
			document.getElementById("<portlet:namespace/>debcred").selectedIndex = 1;
			document.getElementById("<portlet:namespace/>debcred").disabled = true;
			ocultarImporte = true;
		} else {
			jQuery('#<portlet:namespace />divCheque').hide();
		}

		if (document.getElementById("<portlet:namespace/>tipo_mov").value == '<%= TipoMovBcrio.DEPOSITAR_EFECTIVO %>') {
			jQuery('#<portlet:namespace />divEfectivo').show();
			document.getElementById("<portlet:namespace/>debcred").selectedIndex = 1;
			document.getElementById("<portlet:namespace/>debcred").disabled = true;
			ocultarImporte = true;
		} else {
			jQuery('#<portlet:namespace />divEfectivo').hide();
		}

		if (document.getElementById("<portlet:namespace/>tipo_mov").value == '<%= TipoMovBcrio.RECHAZAR_CHEQUES %>') {
			jQuery('#<portlet:namespace />divChequeDepo').show();
			document.getElementById("<portlet:namespace/>debcred").selectedIndex = 0;
			document.getElementById("<portlet:namespace/>debcred").disabled = true;
			ocultarImporte = true;
		}else{
			jQuery('#<portlet:namespace />divChequeDepo').hide();
		}
		
		if (document.getElementById("<portlet:namespace/>tipo_mov").value == '<%= TipoMovBcrio.CANJE_CHEQUE %>') {
			document.getElementById("<portlet:namespace/>debcred").selectedIndex = 1;
			document.getElementById("<portlet:namespace/>debcred").disabled = true;
			ocultarImporte = true;
		}

		if (!ocultarImporte) {
			document.getElementById("<portlet:namespace/>debcred").disabled = false;
			jQuery('#<portlet:namespace />divManual').show();
		} else {
			jQuery('#<portlet:namespace />divManual').hide();
		}
		
		if (document.getElementById("<portlet:namespace/>tipo_mov").value == '<%= TipoMovBcrio.SUBSIDIOS_APE %>') {					
			jQuery('#<portlet:namespace />divSUR').show();
		}else{			
			jQuery('#<portlet:namespace />divSUR').hide();			
		}
	}
	
	function <portlet:namespace/>cambioTrx(){
		var trx=jQuery("#<portlet:namespace/>tipotrxbcria").val();		
		if(trx==7){
			jQuery('#<portlet:namespace />chequera').show();
		}else{
			jQuery('#<portlet:namespace />chequera').hide();
		}
	}
	
	function <portlet:namespace/>guardaMov(){
		var descripcion=jQuery("#<portlet:namespace />descripcion").val();
		var importe=jQuery("#<portlet:namespace />importe").val();
		
		if(!validarForm(descripcion,importe)){
			return false;
		}

		document.<portlet:namespace />mov.<portlet:namespace /><%= Constants.CMD %>.value = "<%=(movSess == null ? Constants.ADD : Constants.UPDATE) %>";
		var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_movimiento_bcrio';		
		url += '&rnd=' + Math.floor(Math.random()*100);
		document.<portlet:namespace />mov.method = 'post';
		submitForm(document.<portlet:namespace />mov, url);
	}
	 	
	function validarForm(descripcion,importe){
		var validarImporte = true;
		
		if (document.getElementById("<portlet:namespace/>tipo_mov").value == '<%= TipoMovBcrio.DEPOSITAR_CHEQUES %>') {
			validarImporte = false;
		}

		if (document.getElementById("<portlet:namespace/>tipo_mov").value == '<%= TipoMovBcrio.DEPOSITAR_EFECTIVO %>') {
			validarImporte = false;
		}

		if (document.getElementById("<portlet:namespace/>tipo_mov").value == '<%= TipoMovBcrio.RECHAZAR_CHEQUES %>') {
			validarImporte = false;
		}

	  if (validarImporte) {
		  if(trim(descripcion).length<=0){
			   alert('<liferay-ui:message key="ingrese-descripcion"/>');
			   return false;
		   }
		   if(!isNumeric(importe)){
			   alert('<liferay-ui:message key="importe-numerico"/>');
			   return false;
		   }
	  }
      return true;
	}
	
	function isNumeric(input){ 
	   return (input - 0) == input && input.length > 0; 
	}
	
	function <portlet:namespace />nuevoMovimiento(){
		var descripcion=jQuery("#<portlet:namespace />descripcion").val('');		
		var nrocompro=jQuery("#<portlet:namespace />nroComp").val('');		
		var importe=jQuery("#<portlet:namespace />importe").val('');
		
	}  

	function cambiarEstado(divElement){
		divElement.slideToggle('slow');
	}

	function <portlet:namespace />buscarChequesRecibidosParametrico() {
		var cuit=jQuery('#<portlet:namespace />cuit').val();
		var numero=jQuery('#<portlet:namespace />numero').val();
		
		if(cuit.length>0){
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuit'/>")){
				jQuery('#<portlet:namespace />cuit').focus();
				return false;
			}
		}		
		jQuery('#<portlet:namespace />agregandoCheque').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_recibidos&cuit='+cuit+'&numero='+numero;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_recibidos').load(url, function() {
        																jQuery('#<portlet:namespace />agregandoCheque').hide();            															
        															  }
        );	
	}
	
	function <portlet:namespace />buscarChequesRecibidos(){
		jQuery('#<portlet:namespace />agregandoCheque').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_recibidos';
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_recibidos').load(url, function() {
			jQuery('#<portlet:namespace />agregandoCheque').hide();
			});
	}
	
	function borraChequesRecibidos(id){
		jQuery('#<portlet:namespace />agregandoCheque').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_recibidos'
			+'&borrar=borrar'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_recibidos').load(url, function() {
					jQuery('#<portlet:namespace />agregandoCheque').hide();
		}); 
	}


	function marcarDepositado(id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_recibidos'
			+'&depositar=depositar'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_recibidos').load(url, function() {
		}); 
	}

	function desmarcarDepositado(id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_recibidos'
			+'&depositar=deshacer'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_recibidos').load(url, function() {
		}); 
	}

	
	function <portlet:namespace />buscarChequesDepositadosParametrico() {
		var cuit=jQuery('#<portlet:namespace />cuit_depo').val();
		var numero=jQuery('#<portlet:namespace />numero_depo').val();
		
		if(cuit.length>0){
			if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuit'/>")){
				jQuery('#<portlet:namespace />cuit_depo').focus();
				return false;
			}
		}		
		jQuery('#<portlet:namespace />agregandoChequeDepo').show();
		
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_depositados&cuit='+cuit+'&numero='+numero;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_depositados').load(url, function() {
	    																jQuery('#<portlet:namespace />agregandoChequeDepo').hide();            															
	    															  }
	    );	
	}


	function <portlet:namespace />buscarChequesDepositados(){
		jQuery('#<portlet:namespace />agregandoChequeDepo').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_depositados';
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_depositados').load(url, function() {
			jQuery('#<portlet:namespace />agregandoChequeDepo').hide();
			});
	}
	
	function borraChequesDepositados(id){
		jQuery('#<portlet:namespace />agregandoChequeDepo').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_depositados'
			+'&borrar=borrar'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_depositados').load(url, function() {
					jQuery('#<portlet:namespace />agregandoChequeDepo').hide();
		}); 
	}

	function marcarRechazado(id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_depositados'
			+'&rechazar=rechazar'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_depositados').load(url, function() {
		}); 
	}

	function desmarcarRechazado(id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_cheques_depositados'
			+'&rechazar=deshacer'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />cheques_depositados').load(url, function() {
		}); 
	}

	function <portlet:namespace />buscarEfectivoIngresado(){
		jQuery('#<portlet:namespace />agregandoEfectivo').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_efectivo_recibido';
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace  />efectivo_recibido').load(url, function() {
			jQuery('#<portlet:namespace />agregandoEfectivo').hide();
			});
	}

	function borraEfectivoRecibido(id){
		jQuery('#<portlet:namespace />agregandoEfectivo').show();
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_efectivo_recibido'
			+'&borrar=borrar'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />efectivo_recibido').load(url, function() {
					jQuery('#<portlet:namespace />agregandoEfectivo').hide();
		}); 
	}

	function marcarEfectivoDepositado(id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_efectivo_recibido'
			+'&depositar=depositar'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />efectivo_recibido').load(url, function() {
		}); 
	}

	function desmarcarEfectivoDepositado(id){
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/abm_mov_bcrio_efectivo_recibido'
			+'&depositar=deshacer'
			+'&id=' + id;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />efectivo_recibido').load(url, function() {alert('entro');	
		}); 
	}

		
	<%if (movSess != null && movSess.getNro_comprobante() != null) {%>
		jQuery('#<portlet:namespace />divManual').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divManual').hide();
	<%}%>

	<%if (movSess != null && movSess.getChequesRecibidos() != null && movSess.getChequesRecibidos().size() > 0) {%>
		jQuery('#<portlet:namespace />divCheque').show();
	<%} else {%>
	jQuery('#<portlet:namespace />divCheque').hide();
	<%}%>

	<%if (movSess != null && movSess.getChequesDepositados() != null && movSess.getChequesDepositados().size() >0) {%>
		jQuery('#<portlet:namespace />divChequeDepo').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divChequeDepo').hide();
	<%}%>
	
	<%if (movSess != null && movSess.getEfectivoRecibido() != null && movSess.getEfectivoRecibido().size() > 0) {%>
		jQuery('#<portlet:namespace />divEfectivo').show();
	<%} else {%>
		jQuery('#<portlet:namespace />divEfectivo').hide();
	<%}%>
	
	<%if (movSess != null && movSess.getChequesCanjeados() != null && movSess.getChequesCanjeados().size() > 0) {%>
		jQuery('#<portlet:namespace />cheques_canjeados').show();
	<%} else {%>
		jQuery('#<portlet:namespace />cheques_canjeados').hide();
	<%}%>


	function <portlet:namespace />nuevoMovimiento(){
		var url = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/editar_movimiento_bcrio';		
		document.<portlet:namespace />mov.method = 'post';
		jQuery('#fromRedireccion').val("fromRedireccion");
		submitForm(document.<portlet:namespace />mov, url);
	}     

	function copiarFecha(){
		var hasta_dia=jQuery("#<portlet:namespace/>fechaMovDia").val();	
		var hasta_mes=jQuery("#<portlet:namespace/>fechaMovMes").val();
		var hasta_anio=jQuery("#<portlet:namespace/>fechaMovAnio").val();

		var desde_dia=jQuery("#<portlet:namespace/>fechaCompDia").val(hasta_dia);	
		var desde_mes=jQuery("#<portlet:namespace/>fechaCompMes").val(hasta_mes);
		var desde_anio=jQuery("#<portlet:namespace/>fechaCompAnio").val(hasta_anio);
	}
	cambiarMovimiento();
	 
	jQuery('#<portlet:namespace />agregandoCheque').hide();
	jQuery('#<portlet:namespace />agregandoChequeDepo').hide();
	jQuery('#<portlet:namespace />agregandoEfectivo').hide();
	jQuery('#<portlet:namespace />chequera').hide();
	
	
	function <portlet:namespace />formatearNro(){
	
	  var importe=jQuery("#<portlet:namespace />importe").val();
	  jQuery("#<portlet:namespace />importe_lb").html(formatNumber.format(importe)); 
	}   
	
	var formatNumber = {
			 separador: ".", // separador para los miles
			 sepDecimal: ',', // separador para los decimales
			 formatear:function (num){
			  num +='';
			  var splitStr = num.split('.');
			  var splitLeft = splitStr[0];
			  var splitRight = splitStr.length > 1 ? this.sepDecimal + splitStr[1] : '';
			  var regx = /(\d+)(\d{3})/;
			  while (regx.test(splitLeft)) {
			  splitLeft = splitLeft.replace(regx, '$1' + this.separador + '$2');
			  }
			  return this.simbol + splitLeft  +splitRight;
			 },
			 format:function(num, simbol){
			  this.simbol = simbol ||'';
			  return this.formatear(num);
			 }
	}
</script>
