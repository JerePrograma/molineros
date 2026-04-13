<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%

	Cheque cheque = (Cheque)request.getAttribute(WebKeysLiquidaciones.CHEQUE_EN_EDICION);
	

	Cheque chequeAImprimir = (Cheque)request.getAttribute(WebKeysLiquidaciones.CHEQUE_A_IMPRIMIR);

	Calendar fechaHoy= CalendarFactoryUtil.getCalendar();
	
	Date fecha = cheque != null ? cheque.getFecha() : null; 
	if (fecha == null) {
		fechaHoy.setTime(new Date());
	}
	else{
		fechaHoy.setTime(fecha);
	}

	List<CuentaBancaria> ctas = (List<CuentaBancaria>) request.getSession().getAttribute(WebKeysAfiliados.CTAS_BCRIAS_EN_SESSION);
%>

<liferay-ui:error exception="<%= DuplicateNumeroChequeException.class %>" message="numero-cheque-duplicado" />
<form action="" method="post" name="<portlet:namespace />fm">

<fieldset class="block-labels">
<legend>
	<liferay-ui:message key="alta-cheque" />
</legend>
	
<table class="lfr-table">
	<tr>
		<td>
			<label><liferay-ui:message key="cuit" />:</label>
		</td>
		<td>
			<input id="<portlet:namespace />cuit" name="<portlet:namespace />cuit" size="13" maxlength="11" type="text"	onkeydown="allowOnlyDigits(event)" value="<%=cheque!=null &&  cheque.getCuit() != null? cheque.getCuit() : "" %>" />
		</td>
		<td>
			<label><liferay-ui:message key="numero" />:</label>
		</td>
		<td>
			<input id="<portlet:namespace />numero" name="<portlet:namespace />numero" size="8" maxlength="8" type="text"	onkeydown="allowOnlyDigits(event)" value="<%=cheque != null &&  cheque.getNumero() != null?  cheque.getNumero() : "" %>" />
		</td>
		<td>
			&nbsp;
		</td>
		<td>
			&nbsp;
		</td>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="8">&nbsp;</td>
	</tr>
	<tr>
		<td>
			<label><liferay-ui:message key="importe" />:</label>
		</td>
		<td>
			<input size="15" id="<portlet:namespace />importe" name="<portlet:namespace />importe" onkeydown="allowOnlyDigitsAndDecimals(event)" maxlength="10" type="text"	value="<%= cheque != null&& cheque.getImporte() != null ?  cheque.getImporte() : "" %>" />
		</td>
		<td>
			<label><liferay-ui:message key="a-nombre-de" />:</label>
		</td>
		<td>
			<input id="<portlet:namespace />aNombreDe" name="<portlet:namespace />aNombreDe"  size="80" maxlength="80" type="text"	value="<%= cheque != null&& cheque.getANombreDe() != null ?  cheque.getANombreDe() : "" %>" />
		</td>
		<td>
			<label><liferay-ui:message key="concepto" />:</label>
		</td>
		<td colspan="2">
			<input id="<portlet:namespace />concepto" name="<portlet:namespace />concepto"  size="80" maxlength="80" type="text"	value="<%= cheque != null&& cheque.getConcepto() != null ?  cheque.getConcepto() : "" %>" />
		</td>				
	</tr>	
	<tr>
		<td>
			<label><liferay-ui:message key="date" />:</label>
		</td>
		<td colspan="7">			
				<liferay-ui:input-date 
				dayParam="fechaDia"
				dayValue="<%= fechaHoy.get(Calendar.DATE)%>" 
				monthParam="fechaMes"
				monthValue="<%= fechaHoy.get(Calendar.MONTH) %>"
				yearParam="fechaAnio" 
				yearValue="<%= fechaHoy.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaHoy.get(Calendar.YEAR) - 120 %>"
				yearRangeEnd="<%= fechaHoy.get(Calendar.YEAR) + 120 %>"
				firstDayOfWeek="<%= fechaHoy.getFirstDayOfWeek() - 1 %>"
				disabled="<%= false %>" />			
		</td>
	</tr>	
	<tr>
		<td>
			Prestador?:
		</td>
		<td colspan="7">
			<input type="checkbox" id="<portlet:namespace />prestador" name="<portlet:namespace />prestador" value="prestador" <% if (cheque!= null && cheque.isPrestador()){ %> checked="checked"<%} %>/>
		</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="account" />:</label></td>
		<td colspan="7">
			<select name="<portlet:namespace />cta_bcria" id="<portlet:namespace />cta_bcria" >
				<option value="0"></option>
				<% for (CuentaBancaria cta: ctas ){%>
					<option value="<%=cta.getId_cuenta_bcria() %>"><%=cta.getDescripcion() %>/<%=String.valueOf(cta.getSucursal()) %></option>
				<%} %>
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			&nbsp;
		</td>
		<td colspan="4">
			<input type="submit" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />saveCheque();return false;"/>
		</td>
	</tr>
</table>

</fieldset>
</form>

<script>

		function <portlet:namespace />validarCampos() {
				var cuit=jQuery('#<portlet:namespace />cuit').val();
				var importe=jQuery('#<portlet:namespace />importe').val();
				var numero=jQuery('#<portlet:namespace />numero').val();
				var aNombreDe=jQuery('#<portlet:namespace />aNombreDe').val();

				
				if(trim(cuit).length>0){
					if(!validarCuil(cuit,"<liferay-ui:message key='valida-cuit'/>")){
						jQuery('#<portlet:namespace />cuit').focus();
						return false;
					}
				}		

				if (trim(importe).length == 0){
					alert("<liferay-ui:message key='importe-obligatorio' />");
					jQuery('#<portlet:namespace />importe').focus();
					return false;
				}
				
				if (trim(importe).length == 0 || !isFloat(trim(importe))){
					alert("<liferay-ui:message key='importe-invalido' />");
					jQuery('#<portlet:namespace />importe').focus();
					return false;
				}

				if (trim(numero).length == 0){
					alert("<liferay-ui:message key='numero-obligatorio' />");
					jQuery('#<portlet:namespace />numero').focus();
					return false;
				}

				if (document.getElementById("<portlet:namespace />cta_bcria").value == 0){
					alert("Por favor, elija una cuenta bancaria");
					jQuery('#<portlet:namespace />cta_bcria').focus();
					return false;
				}

			return true;
		}

		function <portlet:namespace />saveCheque() {			
			if (<portlet:namespace />validarCampos()) {
				url = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/liquidaciones/alta_cheque_entry' /></portlet:actionURL>";			
				submitForm(document.<portlet:namespace />fm, url);				
				return true;
			}
			return false;
		}


		<%if ( chequeAImprimir != null) { %> 
			window.location.href ="/pdfservlet/?accion=cheque&numero="+<%=chequeAImprimir.getNumero()%> ;
		<%}%>
</script>
	