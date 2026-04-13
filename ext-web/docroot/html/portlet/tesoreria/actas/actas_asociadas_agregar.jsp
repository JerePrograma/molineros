<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%
Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);
List<Acta.ActaRelacionada> actasasociadas= null;

if (acta!=null){
	actasasociadas = acta.getActasRelacionadas();
}

String act = (String)request.getAttribute("accionOriginal");
//String dinámico que se le debe pasar a esta pagina para que sepa si es edicion o no
boolean esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));


%>


<table class="lfr-table" width="100%">
	<tr> 
		<td>
		<% if (esEdicion) { %>
			 <input type="text" value="" name="<portlet:namespace />acta_asociada_nro" id="<portlet:namespace />acta_asociada_nro" />&nbsp;
			 <input type="button" value="<liferay-ui:message key="agregar" />" onClick="<portlet:namespace />agregarActaAsociada();" /> 
		<%} %>
		</td>
	</tr>
	<tr>
		<td>
		<div align="center" id="<portlet:namespace />buscandoActas">
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
		<div align="center" id="<portlet:namespace />actasasociadas">
		<jsp:include page='actas_asociadas_search_result.jsp' /></div>
		</td>
	</tr>
</table>
<div align="center" id="<portlet:namespace />hiddendiv">
</div>
<script type="text/javascript">
	function <portlet:namespace />agregarActaAsociada(){
			if (trim(document.getElementById("<portlet:namespace />cuit_entidad").value)==""){
				alert("Primero debe seleccionar una empresa");
				return false;
			}
		
			jQuery('#<portlet:namespace />buscandoActas').show();	
			var nroActa=jQuery('#<portlet:namespace />acta_asociada_nro').val();
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/agregar_acta_relacionada&acta_asociada_nro=' +nroActa + '&esEdicion=' + "<%=esEdicion%>" 	+'&accionOriginal='+ "<%=act%>"
			 	+'&cuit='+trim(document.getElementById("<portlet:namespace />cuit_entidad").value)
			 	+'&sucursal='+trim(document.getElementById("<portlet:namespace />sucur").value)
				+'&fechaObligDia='+document.getElementById("<portlet:namespace />fechaPagoDia").value
				+'&fechaObligMes='+document.getElementById("<portlet:namespace />fechaPagoMes").value
				+'&fechaObligAnio='+document.getElementById("<portlet:namespace />fechaPagoAnio").value;
			jQuery('#<portlet:namespace />actasasociadas').load(url, function() {
														jQuery('#<portlet:namespace />buscandoActas').hide();
														 sumarTodo();            															
										   }
			 );	
			document.getElementById("<portlet:namespace/>inspector").selectedIndex = 0;
	}

	function borraActaAsociada(id){
		if(!confirm("<liferay-ui:message key='are-you-sure-you-want-to-delete-this-entry'/>")){
			return false;
		}else{		
			var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/sacar_acta_relacionada&acta_asociada_id=' +id+ '&esEdicion=' + "<%=esEdicion%>" 	+'&accionOriginal='+ "<%=act%>";
			jQuery('#<portlet:namespace />actasasociadas').load(url, function() {
																						jQuery('#<portlet:namespace />buscandoActas').hide();            															
																			   }
															   );
		}	
	}

	jQuery('#<portlet:namespace />buscandoActas').hide();
</script>