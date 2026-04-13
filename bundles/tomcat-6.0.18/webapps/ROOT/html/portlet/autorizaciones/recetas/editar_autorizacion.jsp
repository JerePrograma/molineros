<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
	//verificar los calendars
	Calendar fechaReceta = null!=renderRequest.getAttribute("fechaReceta")?(Calendar)renderRequest.getAttribute("fechaReceta"): CalendarFactoryUtil.getCalendar();
	Calendar current = CalendarFactoryUtil.getCalendar();

	String numReceta=(String)renderRequest.getAttribute("numReceta");
	String observaciones=(String)renderRequest.getAttribute("observaciones");

	String usuario_modi = user.getScreenName();
%>

		<div align="left" id="<portlet:namespace />autorizacion_en_edicion">
		<form action="" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data"> 
			<fieldset class="block-labels">
			<legend><liferay-ui:message key="autorizaciones-recetas-pmi" /></legend>
			<table class="lfr-table">	
				<tr>
					<td colspan="12">
					<fieldset class="block-labels"><legend><liferay-ui:message key="datos-afiliado" /></legend>
					<liferay-util:include page='/html/portlet/farmacia/busqueda_afiliado.jsp'>
						<liferay-util:param value="<%=String.valueOf(false)%>" name="edit_mode" />
						<liferay-util:param value="_auto" name="origen" />
					</liferay-util:include>
					</fieldset>
					</td>
				</tr>
				<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>		
				</tr>
						<table class="lfr-table">	
						<tr>
						<td><label><liferay-ui:message key="num-receta"/>:</label></td> 
						<td><input id="<portlet:namespace />recetaN" name="<portlet:namespace />recetaN" size="10" maxlength="10" type="text" value='<%=numReceta%>' readonly/></td>
						<td><label><liferay-ui:message key="fecha-receta" />:</label></td>
						<td>
							<liferay-ui:input-date
							dayParam="fechaRecetaDiaN"
							dayValue="<%= fechaReceta.get(Calendar.DATE) %>"
							monthParam="fechaRecetaMesN"
							monthValue="<%= fechaReceta.get(Calendar.MONTH) %>"
							yearParam="fechaRecetaAnioN"
							yearValue="<%= fechaReceta.get(Calendar.YEAR) %>"
							yearRangeStart="<%= current.get(Calendar.YEAR) -10 %>"	
							yearRangeEnd="<%= fechaReceta.get(Calendar.YEAR) %>"
							firstDayOfWeek="<%= fechaReceta.getFirstDayOfWeek() - 1 %>"
							disabled="<%= false %>" />
						</td>
						<td colspan="1"><liferay-ui:message key="observaciones"/>:</label></td>
						<td colspan="5"><textarea rows="3"cols="80"maxlength="20000"id="<portlet:namespace/>obs"name="<portlet:namespace />obs"><%=observaciones%></textarea>
						</td>	
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td>						
							<input id="<portlet:namespace />guardar"
							value="<liferay-ui:message key="guardar"/>"
							title="<liferay-ui:message key="guardar" />"
							onClick="javascript: <portlet:namespace />salvarEdicion();"
							type="button" />
							</td>
						</tr>
						</table>
				</table>
		</fieldset>
		</form>
	</div>

<script type="text/javascript">
jQuery('#<portlet:namespace />buscando').hide();

function <portlet:namespace />salvarEdicion(){
 	var fechaRecetaDia=jQuery('#<portlet:namespace/>fechaRecetaDiaN').val();
	var fechaRecetaMes=jQuery('#<portlet:namespace />fechaRecetaMesN').val();		
	var fechaRecetaAnio=jQuery('#<portlet:namespace />fechaRecetaAnioN').val();
	var receta= jQuery('#<portlet:namespace />recetaN').val();
	var cuil_titular=jQuery('#<portlet:namespace />cuil_auto').val();
	var inte=jQuery('#<portlet:namespace />inte_auto').val();
	var obs=jQuery('#<portlet:namespace />obs').val();

	if (isNaN(fechaRecetaDia) || fechaRecetaDia == "" || isNaN(fechaRecetaMes) || fechaRecetaMes == "" || isNaN(fechaRecetaAnio) || fechaRecetaAnio == "" ) {
		alert("Debe ingresar una Fecha de Receta valida. Inténtelo nuevamente.");
		fechaRecetaDia.select();
		fechaRecetaDia.focus();
		fechaRecetaMes.select();
		fechaRecetaMes.focus();
		fechaRecetaAnio.select();
		fechaRecetaAnio.focus();
	}
	else {
	jQuery('#<portlet:namespace />buscando').show();
 	var editarPmi = {"fechaRecetaDia":fechaRecetaDia,"fechaRecetaMes":fechaRecetaMes,"fechaRecetaAnio":fechaRecetaAnio,"receta":receta,"cuil_titular":cuil_titular,"inte":inte,"obs":obs,"usuario_modi":'<%=usuario_modi%>'};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_autorizacion_pmi_action" /></portlet:renderURL>';
	jQuery('#<portlet:namespace />listado_autorizaciones_pmi').load(url,editarPmi, function(){
														jQuery('#<portlet:namespace />buscando').hide();            															
													  });
	<portlet:namespace />cerrarEdicion();
		}
	}
	
	<portlet:namespace />initDateFields();
	
	function <portlet:namespace />initDateFields(){
		jQuery('#<portlet:namespace />fechaRecetaDiaN').hide();
	}
	
</script>

