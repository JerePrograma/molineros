<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<% 
Acta act  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);


boolean esEdicion = false;

if (act.isInspector()){
	if (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || act == null) {
		esEdicion = true;
	}
	
	if (act != null && act.getCierre_fecha() != null){
		esEdicion = false;
	}
}
Calendar fechaDesde = CalendarFactoryUtil.getCalendar();
Calendar fechaHasta = CalendarFactoryUtil.getCalendar();
%>

<table class="lfr-table" width="100%">
<% if (esEdicion) { %>
		<tr>
			<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaDesdeDia"
				dayValue="<%= fechaDesde.get(Calendar.DATE) %>" 
				monthParam="fechaDesdeMes"
				monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"				
				yearParam="fechaDesdeAnio"
				yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 120 %>"
				yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR)%>"
				firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion %>" />
			</td>
			<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
			<td>
				<liferay-ui:input-date
				dayParam="fechaHastaDia"
				dayValue="<%= fechaHasta.get(Calendar.DATE) %>" 
				monthParam="fechaHastaMes"
				monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"				
				yearParam="fechaHastaAnio"
				yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
				yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 120 %>"
				yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR)%>"
				firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
				disabled="<%= !esEdicion %>" />
			</td>
			<td><liferay-ui:message key="capital" />:</td>
			<td>
				<input type="text" value ="0" name="<portlet:namespace />capital" id="<portlet:namespace />capital" <% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%> onchange="calcularTotal()" onkeydown="allowOnlyDigitsAndDecimals(event)"/>
			</td>
			<td><liferay-ui:message key="interes" />:</td>
			<td>
				<input type="text" value ="0"  name="<portlet:namespace />interes" id="<portlet:namespace />interes" <% if (!esEdicion) { %> <%="readonly='readonly'" %> <%}%>  onchange="calcularTotal()" onkeydown="allowOnlyDigitsAndDecimals(event)"/>
			</td>
			<td><liferay-ui:message key="total" />:</td>
			<td>
				<input type="text" readonly='readonly' id="<portlet:namespace />total"/>
			</td>
			<td>
				<input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarDetalleActa();" />
			</td>
		</tr>
<%} %>
		<tr>
			<td colspan="11">
				<div align="center" id="<portlet:namespace />buscandoDetallesActa">
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='buscando'/></td>
							<td align="center">					
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="11">
				<div align="center" id="<portlet:namespace />detallesActa">		
					<jsp:include page='detalle_acta_search_result.jsp' />
				</div>
			</td>
		</tr>
</table>


<% if (request.getAttribute("fromActa")!=null && request.getAttribute("fromActa").equals("fromActa")){ %>
<input type="hidden" name="fromActa" value="fromActa"/>
<%} %>
<br />
<input type="hidden" value="" name="cambioSolapa" id="cambioSolapa"/>
<input type="hidden" value="" name="tabs1" id="tabs1"/>
<input type="hidden" value="" name="view" id="view"/>
<input type="hidden" value="<%= request.getAttribute("accionOriginal")%>" name="accionOriginal" id="accionOriginal"/>

	<script type="text/javascript">
		function <portlet:namespace />agregarDetalleActa(){
				jQuery('#<portlet:namespace />buscandoDetallesActa').show();	
				var fechaDesdeDia  = document.getElementById("<portlet:namespace />fechaDesdeDia");
				var fechaDesdeMes= document.getElementById("<portlet:namespace />fechaDesdeMes");
				var fechaDesdeAnio = document.getElementById("<portlet:namespace />fechaDesdeAnio");

				var fechaHastaDia = document.getElementById("<portlet:namespace />fechaHastaDia");
				var fechaHastaMes = document.getElementById("<portlet:namespace />fechaHastaMes");
				var fechaHastaAnio = document.getElementById("<portlet:namespace />fechaHastaAnio");

				var capital = document.getElementById("<portlet:namespace />capital");
				var intereses = document.getElementById("<portlet:namespace />interes");
 
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/agregar_detalle_acta'
					+'&fechaDesdeDia='+fechaDesdeDia.value
					+'&fechaDesdeMes='+fechaDesdeMes.value
					+'&fechaDesdeAnio='+fechaDesdeAnio.value
					+'&fechaHastaDia='+fechaHastaDia.value
					+'&fechaHastaMes='+fechaHastaMes.value
					+'&fechaHastaAnio='+fechaHastaAnio.value
					+'&capital='+capital.value
					+'&intereses='+intereses.value
					+'&accionOriginal='+ "<%=request.getAttribute("accionOriginal")%>";
				jQuery('#<portlet:namespace />detallesActa').load(url, function() {
																							jQuery('#<portlet:namespace />buscandoDetallesActa').hide();            															
																				   }
																   );	
				intereses.value = 0;
				capital.value = 0;
		}
	
		function borraDetalleActa(idActa){
			if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
				return false;
			}else{		
				var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/sacar_detalle_acta&id=' +idActa 					+'&accionOriginal='+ "<%=request.getAttribute("accionOriginal")%>";;
				jQuery('#<portlet:namespace />detallesActa').load(url, function() {
																							jQuery('#<portlet:namespace />buscandoDetallesActa').hide();            															
																				   }
																   );
			}	
		}
		jQuery('#<portlet:namespace />buscandoDetallesActa').hide();


		function submitFormNotSave(){
				document.<portlet:namespace />act.<portlet:namespace /><%= Constants.CMD %>.value = "CAMBIO_SOLAPA";
				document.getElementById("cambioSolapa").value="cambioSolapa";
				document.getElementById("tabs1").value="datos";
				document.getElementById("view").value="true";
				var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/tesoreria/editar_actas_entry" /></portlet:actionURL>';
				document.<portlet:namespace />act.method = 'post';
				submitForm(document.<portlet:namespace />act, url);
		}

		function calcularTotal(){
			var capital = document.getElementById("<portlet:namespace />capital");
			var interes = document.getElementById("<portlet:namespace />interes");
			if (trim(capital.value) == ""){
				capital.value = "0";
			}
			if (trim(interes.value) == ""){
				interes.value = "0";
			}
			document.getElementById("<portlet:namespace />total").value = parseFloat(capital.value) + parseFloat(interes.value);
			
		}
	</script>