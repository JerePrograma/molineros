<%@ include file="/html/portlet/correspondencia/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<script type="text/javascript">
jQuery("#<portlet:namespace />fm" ).ready(function() { <portlet:namespace />buscarItemsCorrespondencia(); });
</script>

<%
	boolean showItemsRecepcionista = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);

	boolean showParaLiquidar = UserLocalServiceUtil.hasUserGroupUser(99214, user.getUserId() );

	String estadoParaLiquidar = "PARA_LIQUIDAR";
	
	BusquedaBandejaCorreoFiltro filtro = (BusquedaBandejaCorreoFiltro) request.getSession().getAttribute(WebKeysCorrespondencia.BUSQUEDA_BANDEJA_CORRESPONDENCIA);
/* 	Calendar fechaDesde = CalendarFactoryUtil.getCalendar(); 		
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); */
	Calendar fechaDesde = Calendar.getInstance(); 		
	Calendar fechaHasta = Calendar.getInstance();
	String estadoSelec = WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[0];
	if(filtro != null){
		fechaDesde.setTime(filtro.getFechaDesde());
		fechaHasta.setTime(filtro.getFechaHasta());
		estadoSelec=filtro.getEstado();
	}else{
		fechaDesde.add(Calendar.DATE, -7);
		fechaDesde.setTime(fechaDesde.getTime());
		fechaHasta.setTime(new Date());
	}

%>	
<fieldset class="block-labels">
<legend>Criterios de Búsqueda de Correspondencia</legend>

	<table>	
		<tr>	
			<td><label><liferay-ui:message key="fecha-desde" />:</label></td>
			<td>
				<liferay-ui:input-date
					dayParam="fechaDesdeDia"
					dayValue="<%= fechaDesde.get(Calendar.DATE)%>"
					dayNullable="<%= true %>" 
					monthParam="fechaDesdeMes"
					monthValue="<%= fechaDesde.get(Calendar.MONTH) %>"
					monthNullable="<%= true %>"				
					yearParam="fechaDesdeAnio"
					yearValue="<%= fechaDesde.get(Calendar.YEAR) %>"
					yearNullable="<%= true %>"
					yearRangeStart="<%= fechaDesde.get(Calendar.YEAR) - 10 %>"
					yearRangeEnd="<%= fechaDesde.get(Calendar.YEAR) + 10 %>"
					firstDayOfWeek="<%= fechaDesde.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" />
			</td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td><label><liferay-ui:message key="fecha-hasta" />:</label></td>
			<td>
				<liferay-ui:input-date
					dayParam="fechaHastaDia"																					
					dayValue="<%= fechaHasta.get(Calendar.DATE)%>"
					dayNullable="<%= true %>"
					monthParam="fechaHastaMes"
					monthValue="<%= fechaHasta.get(Calendar.MONTH) %>"
					monthNullable="<%= true %>"
					yearParam="fechaHastaAnio"
					yearValue="<%= fechaHasta.get(Calendar.YEAR) %>"
					yearNullable="<%= true %>"
					yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 10 %>"
					yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) + 10 %>"
					firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
					disabled="<%= false %>" />
			</td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td><label>Estado:</label></td>
			<td><select name="<portlet:namespace/>estado" id="<portlet:namespace/>estado" >
					<option value="<%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[0] %>" <% if(estadoSelec.equals(WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[0])){ %> selected="selected" <%} %> ><%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[0] %></option>
					<option value="<%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[2] %>" <% if(estadoSelec.equals(WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[2])){ %> selected="selected" <%} %> ><%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[2] %></option>
					<%if(showItemsRecepcionista) {%>
						<option value="<%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[1] %>" <% if(estadoSelec.equals(WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[1])){ %> selected="selected" <%} %> ><%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[1] %></option>
						<option value="<%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[3] %>" <% if(estadoSelec.equals(WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[3])){ %> selected="selected" <%} %> ><%=WebKeysCorrespondencia.ESTADOS_ITEM_CORRESPONDENCIA[3] %></option>
					<%} %>
					<%if(showParaLiquidar) {%>
						<option value="<%=estadoParaLiquidar%>" <% if(estadoSelec.equals(estadoParaLiquidar)){ %> selected="selected" <%} %> ><%=estadoParaLiquidar%></option>
					<% } %>	
				</select>
			</td>
		</tr>
		<%if(showParaLiquidar) {%>
		<tr><td><label>Prestador/Proveedor:</label></td>
			<td><input id="<portlet:namespace />prest_prov"
				name="<portlet:namespace />prest_prov" size="13" maxlength="11"
				type="text" onkeydown="allowOnlyDigits(event);"  /> </td>
			<td colspan="3" >&nbsp;</td>	
		</tr>
		<% } else { %>
			<tr><td><input id="<portlet:namespace />prest_prov"
				name="<portlet:namespace />prest_prov"  type="hidden" value=""/>
				</td>
			</tr>	
		<% } %>
		<tr><td colspan="5">&nbsp;</td></tr>
		<tr><td colspan="5"><input id="<portlet:namespace />buscar" value="<liferay-ui:message key="buscar"/>" title="<liferay-ui:message key="buscar" />" type="button" onclick="<portlet:namespace />buscarItemsCorrespondencia();" /></td></tr>
	</table>


	<div align="center" id="<portlet:namespace />buscando">
	<table style="align: center;">
		<tr>
			<td><liferay-ui:message key='buscando'/></td>
			<td align="center"><img
				alt="<liferay-ui:message key='buscando'/>"
				src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
			</td>
		</tr>
	</table>
	</div>
	<div align="center" id="<portlet:namespace />busquedaCorrespondenciaInboxDiv">
		<liferay-util:include page="/html/portlet/correspondencia/bandeja_de_entrada_detalle.jsp">
		</liferay-util:include>
	</div>

</fieldset>	
<script type="text/javascript">

jQuery('#<portlet:namespace />buscando').hide();

function <portlet:namespace />buscarItemsCorrespondencia(){

	jQuery('#<portlet:namespace />buscando').show();
	
	var estado=jQuery('#<portlet:namespace/>estado').val();
	var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
    var fechaDesdeFinal = diaDesde+'/'+mesDesde+'/'+anioDesde;
	var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();	    
    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;	    
    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
    var fechaHastaFinal = diaHasta+'/'+mesHasta+'/'+anioHasta;
	var prestProv=jQuery('#<portlet:namespace/>prest_prov').val();
	
	/* var total_reg = jQuery('#<portlet:namespace />total_registros').val(); */
	var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
	var viene_de = jQuery('#<portlet:namespace />viene_de').val();
	
	var busquedaCorr = { "estado": estado, "fechaDesdeFinal": fechaDesdeFinal, "fechaHastaFinal": fechaHastaFinal, "pagina" : offset_reg, "viene_de" : viene_de, "prest_prov" : prestProv };

	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/correspondencia/buscar_bandeja_entrada" /></portlet:renderURL>';
	
    jQuery('#<portlet:namespace />busquedaCorrespondenciaInboxDiv').load(url,busquedaCorr, function() {
    																jQuery('#<portlet:namespace />buscando').hide();            															
    															  }
    );
}

function marcaRecibido(id_item_corr) {
	var url = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/correspondencia/marcar_recibido" /></portlet:actionURL>';
	url= url + "&item_corr="+id_item_corr;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);				
}
function liquidarFC(id_item_corr) {
	var url = '<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>"><portlet:param name="struts_action" value="/correspondencia/liquidar_FC" /></portlet:renderURL>';
	url= url + "&item_corr="+id_item_corr;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);				
}
</script>	
