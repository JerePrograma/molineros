<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%
Calendar fechaHoy = CalendarFactoryUtil.getCalendar();
fechaHoy.setTime(new Date());

Calendar prestacionFecha = CalendarFactoryUtil.getCalendar();
prestacionFecha.setTime(new Date());

String view = (String)request.getParameter("view");
String cuil_titular = request.getParameter("cuil");
String inte = request.getParameter("inte");
%>
<portlet:defineObjects />

<fieldset class="block-labels"><legend><liferay-ui:message
	key="Catastral" /></legend> 	
	<%if(null==view || !view.equals("true")){ %>
<table class="lfr-table">
	<tr>
		<td colspan="1"><label><liferay-ui:message key="date" />:</label></td>
		<td colspan="3"><liferay-ui:input-date dayParam="presentacionFechaDia"
			dayValue="<%= prestacionFecha.get(Calendar.DATE)%>"
			monthParam="presentacionFechaMes"
			monthValue="<%= prestacionFecha.get(Calendar.MONTH) %>"
			yearParam="presentacionFechaAnio"
			yearValue="<%= prestacionFecha.get(Calendar.YEAR) %>"
			yearRangeStart="<%= prestacionFecha.get(Calendar.YEAR) - 120 %>"
			yearRangeEnd="<%= prestacionFecha.get(Calendar.YEAR) + 120 %>"
			firstDayOfWeek="<%= prestacionFecha.getFirstDayOfWeek() - 1 %>"
			disabled="false" />
		</td>
		<td colspan="8">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label>NN:</label></td>		
		<td colspan="1"><label>Pieza</label></td>
		<td colspan="1"><label>Cara</label></td>
		<td colspan="1"><label>Pieza</label></td>
		<td colspan="1"><label>Cara</label></td>
		<td colspan="1"><label>Pieza</label></td>
		<td colspan="1"><label>Cara</label></td>
		<td colspan="1"><label>Pieza</label></td>
		<td colspan="1"><label>Cara</label></td>
		<td colspan="1"><label>Pieza</label></td>
		<td colspan="1"><label>Cara</label></td>		
	</tr>
	<tr>	
		<td colspan="1">
		<input id="<portlet:namespace />codigo_p"
				name="<portlet:namespace />codigo_p" size="6" maxlength="8"
				type="text"
				value="<%= WebKeysLiquidaciones.CODIGO_DEFECTO_CATASTRO %>"
				readonly="readonly" 
				/>
		<input id="<portlet:namespace />id_codigo_p"
				name="<portlet:namespace />id_codigo_p"
				type="hidden"
				value="<%= WebKeysLiquidaciones.ID_CODIGO_DEFECTO_CATASTRO %>"				
				/>
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza1"
				name="<portlet:namespace />pieza1" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigitsAndDecimals(event);" />
			</td>
		<td colspan="1"><select name="<portlet:namespace/>cara1" id="<portlet:namespace/>cara1"
			<% if(null==view || view.equals("true")){ %> 
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>					
			</select> 
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza2"
				name="<portlet:namespace />pieza2" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigits(event);" />
			</td>
		<td colspan="1"><select name="<portlet:namespace/>cara2" id="<portlet:namespace/>cara2"
			<% if(null==view || view.equals("true")){ %>
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>
			</select> 
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza3"
				name="<portlet:namespace />pieza3" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigits(event);" />
		</td>
		<td colspan="1"><select name="<portlet:namespace/>cara3" id="<portlet:namespace/>cara3"
			<% if(null==view || view.equals("true")){ %> 
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>					
			</select> 
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza4"
				name="<portlet:namespace />pieza4" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigits(event);"/>
		</td>
		<td colspan="1"><select name="<portlet:namespace/>cara4" id="<portlet:namespace/>cara4"
			<% if(null==view || view.equals("true")){ %> 
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>					
			</select> 
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza5"
				name="<portlet:namespace />pieza5" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigits(event);"/>
		</td>
		<td colspan="1"><select name="<portlet:namespace/>cara5" id="<portlet:namespace/>cara5"
			<% if(null==view || view.equals("true")){ %> 
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>					
			</select> 
		</td>		
		<td colspan="1">&nbsp;</td>		
	</tr>	
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
		<tr>	
		<td colspan="1">
			&nbsp;
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza6"
				name="<portlet:namespace />pieza6" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigitsAndDecimals(event);" />
			</td>
		<td colspan="1"><select name="<portlet:namespace/>cara6" id="<portlet:namespace/>cara6"
			<% if(null==view || view.equals("true")){ %> 
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>					
			</select> 
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza7"
				name="<portlet:namespace />pieza7" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigits(event);" />
			</td>
		<td colspan="1"><select name="<portlet:namespace/>cara7" id="<portlet:namespace/>cara7"
			<% if(null==view || view.equals("true")){ %>
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>
			</select> 
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza8"
				name="<portlet:namespace />pieza8" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigits(event);" />
		</td>
		<td colspan="1"><select name="<portlet:namespace/>cara8" id="<portlet:namespace/>cara8"
			<% if(null==view || view.equals("true")){ %> 
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>					
			</select> 
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza9"
				name="<portlet:namespace />pieza9" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigits(event);"/>
		</td>
		<td colspan="1"><select name="<portlet:namespace/>cara9" id="<portlet:namespace/>cara9"
			<% if(null==view || view.equals("true")){ %> 
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>					
			</select> 
		</td>
		<td colspan="1"><input id="<portlet:namespace />pieza10"
				name="<portlet:namespace />pieza10" size="5" maxlength="2"
				type="text"
				value=""
				onkeydown="allowOnlyDigits(event);"/>
		</td>
		<td colspan="1"><select name="<portlet:namespace/>cara10" id="<portlet:namespace/>cara10"
			<% if(null==view || view.equals("true")){ %> 
				disabled="disabled"
			<%} %>
			disabled="disabled">
			<option value=""></option>
			<option value="SUP">SUP</option>
			<option value="INF">INF</option>					
			</select> 
		</td>
		<td colspan="1">&nbsp;</td>		
	</tr>

	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	
	<tr>
		<td colspan="1">
			<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />grabarCatastro();" />
			<input type="hidden" id="<portlet:namespace />item" name="<portlet:namespace />item" value="0"/>			
		</td>
		<td colspan="5">
			<input type="button" value="Marcar todas las piezas como faltantes" onClick="<portlet:namespace />grabarTodos();" />					
		</td>		
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	
</table>
<div align="center" id="<portlet:namespace />buscandoCatastro">
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
<div align="center" id="<portlet:namespace />catastro_resultado">
	<jsp:include
		page='catastro_search_result.jsp'>
		<jsp:param name="view" value="<%=view%>" />
	</jsp:include></div>
</fieldset>

<script type="text/javascript">
	jQuery('#<portlet:namespace />buscandoCatastro').hide();
	function <portlet:namespace />grabarCatastro(){
		var cuil_titular = <%=cuil_titular%>;
		var inte = <%=inte%>;
		var diaPer = document.getElementById("<portlet:namespace />presentacionFechaDia").value;
		var mesPer = document.getElementById("<portlet:namespace />presentacionFechaMes").value; 
		var anioPer = document.getElementById("<portlet:namespace />presentacionFechaAnio").value;
		var codigo = document.getElementById("<portlet:namespace />codigo_p").value;
		var id_codigo = document.getElementById("<portlet:namespace />id_codigo_p").value;
		var pieza1=jQuery('#<portlet:namespace />pieza1').val();
		var pieza2=jQuery('#<portlet:namespace />pieza2').val();
		var pieza3=jQuery('#<portlet:namespace />pieza3').val();
		var pieza4=jQuery('#<portlet:namespace />pieza4').val();
		var pieza5=jQuery('#<portlet:namespace />pieza5').val();
		var pieza6=jQuery('#<portlet:namespace />pieza6').val();
		var pieza7=jQuery('#<portlet:namespace />pieza7').val();
		var pieza8=jQuery('#<portlet:namespace />pieza8').val();		
		var pieza9=jQuery('#<portlet:namespace />pieza9').val();
		var pieza10=jQuery('#<portlet:namespace />pieza10').val();
				
		var cara1=jQuery('#<portlet:namespace />cara1').val();
		var cara2=jQuery('#<portlet:namespace />cara2').val();		
		var cara3=jQuery('#<portlet:namespace />cara3').val();
		var cara4=jQuery('#<portlet:namespace />cara4').val();
		var cara5=jQuery('#<portlet:namespace />cara5').val();
		var cara6=jQuery('#<portlet:namespace />cara6').val();		
		var cara7=jQuery('#<portlet:namespace />cara7').val();
		var cara8=jQuery('#<portlet:namespace />cara8').val();
		var cara9=jQuery('#<portlet:namespace />cara9').val();
		var cara10=jQuery('#<portlet:namespace />cara10').val();		

		
		if(!<portlet:namespace />validarDatosCatastro()){
			return false;
		}else{
			jQuery('#<portlet:namespace />buscandoCatastro').show();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/grabar_catastro&diaPer='+diaPer+'&mesPer='+mesPer+'&anioPer='+anioPer+
								'&codigo='+codigo+'&id_codigo='+id_codigo+'&pieza1='+pieza1+'&pieza2='+pieza2+'&pieza3='+pieza3+'&pieza4='+pieza4+'&pieza5='+pieza5+
								'&pieza6='+pieza6+'&pieza7='+pieza7+'&pieza8='+pieza8+'&pieza9='+pieza9+'&pieza10='+pieza10+'&cara1='+cara1+'&cara2='+cara2+'&cara3='+cara3+'&cara4='+cara4+'&cara5='+cara5
								+'&cara6='+cara6+'&cara7='+cara7+'&cara8='+cara8+'&cara9='+cara9+'&cara10='+cara10+'&cuil_titular='+cuil_titular+'&inte='+inte;
			jQuery('#<portlet:namespace />catastro_resultado').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoCatastro').hide();            															
																			   }
															   );
		}
	}

	function <portlet:namespace />grabarTodos(){
		if(!confirm("¿Está seguro de que desea marcar todas las piezas como faltantes?")){
			return false;
		}else{
			jQuery('#<portlet:namespace />buscandoCatastro').show();
			var diaPer = document.getElementById("<portlet:namespace />presentacionFechaDia").value;
			var mesPer = document.getElementById("<portlet:namespace />presentacionFechaMes").value; 
			var anioPer = document.getElementById("<portlet:namespace />presentacionFechaAnio").value;
			var codigo = document.getElementById("<portlet:namespace />codigo_p").value;
			var id_codigo = document.getElementById("<portlet:namespace />id_codigo_p").value;
			var cuil_titular = <%=cuil_titular%>;
			var inte = <%=inte%>;							
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/grabar_catastro&diaPer='+diaPer+'&mesPer='+mesPer+'&anioPer='+anioPer+'&codigo='+codigo+'&id_codigo='+id_codigo+'&todas=1'+'&cuil_titular='+cuil_titular+'&inte='+inte;
			jQuery('#<portlet:namespace />catastro_resultado').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoCatastro').hide();
																			   }
															   );
		}
	}
	
	function borraCatastro(id, cuil, inte){
		var cuil_titular = <%=cuil_titular%>;
		var inte = <%=inte%>;
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/liquidaciones/grabar_catastro&id='+id+'&borrarCat='+'true'+'&cuil_titular='+cuil_titular+'&inte='+inte;
			jQuery('#<portlet:namespace />catastro_resultado').load(url, function() {
																			jQuery('#<portlet:namespace />buscandoCatastro').hide();            															
																		}
															   );
		}
	}

	function <portlet:namespace />validarDatosCatastro(){
		
		var pieza1=jQuery('#<portlet:namespace />pieza1').val();
		var pieza2=jQuery('#<portlet:namespace />pieza2').val();
		var pieza3=jQuery('#<portlet:namespace />pieza3').val();
		var pieza4=jQuery('#<portlet:namespace />pieza4').val();
		var pieza5=jQuery('#<portlet:namespace />pieza5').val();
		var pieza6=jQuery('#<portlet:namespace />pieza6').val();
		var pieza7=jQuery('#<portlet:namespace />pieza7').val();
		var pieza8=jQuery('#<portlet:namespace />pieza8').val();
		var pieza9=jQuery('#<portlet:namespace />pieza9').val();
		var pieza10=jQuery('#<portlet:namespace />pieza10').val();
		
		var mensaje="Para guardar debe cargar al menos una pieza, que debe ser un número entero válido";
		var sinError=true;
		if(trim(pieza1) == "" && trim(pieza2) == "" && trim(pieza3) == "" && trim(pieza4) == "" && trim(pieza5) == ""
			&& trim(pieza6) == "" && trim(pieza7) == "" && trim(pieza8) == "" && trim(pieza9) == "" && trim(pieza10) == ""){
			sinError=false;
		}
		if(trim(pieza1) != "" && !isPositiveInteger(trim(pieza1)) && !((trim(pieza1) >= 11 && trim(pieza1) <= 18) || 
				  (trim(pieza1) >= 21 && trim(pieza1) <= 28) || 
				  (trim(pieza1) >= 31 && trim(pieza1) <= 38) ||
				  (trim(pieza1) >= 41 && trim(pieza1) <= 48) ||
				  (trim(pieza1) >= 51 && trim(pieza1) <= 55) ||
				  (trim(pieza1) >= 61 && trim(pieza1) <= 65) ||
				  (trim(pieza1) >= 71 && trim(pieza1) <= 75) ||
				  (trim(pieza1) >= 81 && trim(pieza1) <= 85))) {
			sinError=false;
		}
		if(trim(pieza2) != "" && !isPositiveInteger(trim(pieza2)) && !((trim(pieza2) >= 11 && trim(pieza2) <= 18) || 
				  (trim(pieza2) >= 21 && trim(pieza2) <= 28) || 
				  (trim(pieza2) >= 31 && trim(pieza2) <= 38) ||
				  (trim(pieza2) >= 41 && trim(pieza2) <= 48) ||
				  (trim(pieza2) >= 51 && trim(pieza2) <= 55) ||
				  (trim(pieza2) >= 61 && trim(pieza2) <= 65) ||
				  (trim(pieza2) >= 71 && trim(pieza2) <= 75) ||
				  (trim(pieza2) >= 81 && trim(pieza2) <= 85))) {
			sinError=false;
		}
		if(trim(pieza4) != "" && !isPositiveInteger(trim(pieza4)) && !((trim(pieza4) >= 11 && trim(pieza4) <= 18) || 
				  (trim(pieza4) >= 21 && trim(pieza4) <= 28) || 
				  (trim(pieza4) >= 31 && trim(pieza4) <= 38) ||
				  (trim(pieza4) >= 41 && trim(pieza4) <= 48) ||
				  (trim(pieza4) >= 51 && trim(pieza4) <= 55) ||
				  (trim(pieza4) >= 61 && trim(pieza4) <= 65) ||
				  (trim(pieza4) >= 71 && trim(pieza4) <= 75) ||
				  (trim(pieza4) >= 81 && trim(pieza4) <= 85))) {
			sinError=false;
		}
		if(trim(pieza3) != "" && !isPositiveInteger(trim(pieza3)) && !((trim(pieza3) >= 11 && trim(pieza3) <= 18) || 
				  (trim(pieza3) >= 21 && trim(pieza3) <= 28) || 
				  (trim(pieza3) >= 31 && trim(pieza3) <= 38) ||
				  (trim(pieza3) >= 41 && trim(pieza3) <= 48) ||
				  (trim(pieza3) >= 51 && trim(pieza3) <= 55) ||
				  (trim(pieza3) >= 61 && trim(pieza3) <= 65) ||
				  (trim(pieza3) >= 71 && trim(pieza3) <= 75) ||
				  (trim(pieza3) >= 81 && trim(pieza3) <= 85))) {
			sinError=false;
		}
		if(trim(pieza5) != "" && !isPositiveInteger(trim(pieza5)) && !((trim(pieza5) >= 11 && trim(pieza5) <= 18) || 
				  (trim(pieza5) >= 21 && trim(pieza5) <= 28) || 
				  (trim(pieza5) >= 31 && trim(pieza5) <= 38) ||
				  (trim(pieza5) >= 41 && trim(pieza5) <= 48) ||
				  (trim(pieza5) >= 51 && trim(pieza5) <= 55) ||
				  (trim(pieza5) >= 61 && trim(pieza5) <= 65) ||
				  (trim(pieza5) >= 71 && trim(pieza5) <= 75) ||
				  (trim(pieza5) >= 81 && trim(pieza5) <= 85))) {
			sinError=false;
		}
		if(trim(pieza6) != "" && !isPositiveInteger(trim(pieza6)) && !((trim(pieza6) >= 11 && trim(pieza6) <= 18) || 
				  (trim(pieza6) >= 21 && trim(pieza6) <= 28) || 
				  (trim(pieza6) >= 31 && trim(pieza6) <= 38) ||
				  (trim(pieza6) >= 41 && trim(pieza6) <= 48) ||
				  (trim(pieza6) >= 51 && trim(pieza6) <= 55) ||
				  (trim(pieza6) >= 61 && trim(pieza6) <= 65) ||
				  (trim(pieza6) >= 71 && trim(pieza6) <= 75) ||
				  (trim(pieza6) >= 81 && trim(pieza6) <= 85))) {
			sinError=false;
		}
		if(trim(pieza7) != "" && !isPositiveInteger(trim(pieza7)) && !((trim(pieza7) >= 11 && trim(pieza7) <= 18) || 
				  (trim(pieza7) >= 21 && trim(pieza7) <= 28) || 
				  (trim(pieza7) >= 31 && trim(pieza7) <= 38) ||
				  (trim(pieza7) >= 41 && trim(pieza7) <= 48) ||
				  (trim(pieza7) >= 51 && trim(pieza7) <= 55) ||
				  (trim(pieza7) >= 61 && trim(pieza7) <= 65) ||
				  (trim(pieza7) >= 71 && trim(pieza7) <= 75) ||
				  (trim(pieza7) >= 81 && trim(pieza7) <= 85))) {
			sinError=false;
		}
		if(trim(pieza8) != "" && !isPositiveInteger(trim(pieza8)) && !((trim(pieza8) >= 11 && trim(pieza8) <= 18) || 
				  (trim(pieza8) >= 21 && trim(pieza8) <= 28) || 
				  (trim(pieza8) >= 31 && trim(pieza8) <= 38) ||
				  (trim(pieza8) >= 41 && trim(pieza8) <= 48) ||
				  (trim(pieza8) >= 51 && trim(pieza8) <= 55) ||
				  (trim(pieza8) >= 61 && trim(pieza8) <= 65) ||
				  (trim(pieza8) >= 71 && trim(pieza8) <= 75) ||
				  (trim(pieza8) >= 81 && trim(pieza8) <= 85))) {
			sinError=false;
		}
		if(trim(pieza9) != "" && !isPositiveInteger(trim(pieza9)) && !((trim(pieza9) >= 11 && trim(pieza9) <= 18) || 
				  (trim(pieza9) >= 21 && trim(pieza9) <= 28) || 
				  (trim(pieza9) >= 31 && trim(pieza9) <= 38) ||
				  (trim(pieza9) >= 41 && trim(pieza9) <= 48) ||
				  (trim(pieza9) >= 51 && trim(pieza9) <= 55) ||
				  (trim(pieza9) >= 61 && trim(pieza9) <= 65) ||
				  (trim(pieza9) >= 71 && trim(pieza9) <= 75) ||
				  (trim(pieza9) >= 81 && trim(pieza9) <= 85))) {
			sinError=false;
		}
		if(trim(pieza10) != "" && !isPositiveInteger(trim(pieza10)) && !((trim(pieza10) >= 11 && trim(pieza10) <= 18) || 
				  (trim(pieza10) >= 21 && trim(pieza10) <= 28) || 
				  (trim(pieza10) >= 31 && trim(pieza10) <= 38) ||
				  (trim(pieza10) >= 41 && trim(pieza10) <= 48) ||
				  (trim(pieza10) >= 51 && trim(pieza10) <= 55) ||
				  (trim(pieza10) >= 61 && trim(pieza10) <= 65) ||
				  (trim(pieza10) >= 71 && trim(pieza10) <= 75) ||
				  (trim(pieza10) >= 81 && trim(pieza10) <= 85))) {
			sinError=false;
		}
		
		if(!sinError){
			alert(mensaje);
		}
		return sinError;
	}
	
</script>