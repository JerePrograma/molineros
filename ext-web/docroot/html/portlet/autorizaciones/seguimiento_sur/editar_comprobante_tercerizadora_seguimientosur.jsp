<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>
<%
	PortletURL portletURL = renderResponse.createRenderURL();
	String viewStr = (String)request.getAttribute("view");
	SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
	
	boolean esEdicion = true;
	if (viewStr != null && viewStr.trim().equals("VIEW")){
		esEdicion = false;
	}
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "autorizaciones";
	}
	
	int id_seguimiento=seguimiento!=null && seguimiento.getId()!= null ?(int)seguimiento.getId():0;
	
	Calendar fechaHasta = CalendarFactoryUtil.getCalendar(); 		
	fechaHasta.setTime(new Date());
	Calendar fechaPresentacion = CalendarFactoryUtil.getCalendar();
	if(seguimiento.getComprobanteFecha()==null){
		fechaPresentacion.setTime(new Date());
	}else{
	  fechaPresentacion.setTime(seguimiento.getComprobanteFecha());
	}  
%>

<form action="" method="post" name="<portlet:namespace />fmSC">
	
<fieldset class="block-labels"><legend>Comprobante Expediente S.U.R</legend>


<table class="lfr-table">
			<tr>
			  <td><label><liferay-ui:message key="comprobante" />:</label></td>
				<td colspan="5">
				<select
					id="<portlet:namespace/>comprobante_tipoSUR"
					name="<portlet:namespace/>comprobante_tipoSUR" <% if (!esEdicion) { %>
					disabled="disabled" <%} %>>
						<option value="FCP"
							<%=Validator.isNotNull(seguimiento.getComprobanteTipo()) && seguimiento.getComprobanteTipo().equals("FCP") ? "selected" : ""  %>>FCP</option>
						<option value="RCB"
							<%=Validator.isNotNull(seguimiento.getComprobanteTipo()) && seguimiento.getComprobanteTipo().equals("RCB") ? "selected" : ""  %>>RCB</option>
						<option value="NCR"
							<%=Validator.isNotNull(seguimiento.getComprobanteTipo()) && seguimiento.getComprobanteTipo().equals("NCR") ? "selected" : ""  %>>NCR</option>
				</select> &nbsp; 
				<select name="<portlet:namespace/>comprobante_letraSUR"
					id="<portlet:namespace/>comprobante_letraSUR" <% if (!esEdicion) { %>
					disabled="disabled" <%} %>>
						<option value="B"
							<%=Validator.isNotNull(seguimiento.getComprobanteLetra()) && seguimiento.getComprobanteLetra().equals("B") ? "selected" : ""  %>>B</option>
						<option value="C"
							<%=Validator.isNotNull(seguimiento.getComprobanteLetra()) && seguimiento.getComprobanteLetra().equals("C") ? "selected" : ""  %>>C</option>
				</select> &nbsp; 
				<input id="<portlet:namespace />comprobante_sucursalSUR"
					name="<portlet:namespace />comprobante_sucursalSUR" size="5" maxlength="4" type="text"
					value="<%=Validator.isNotNull(seguimiento) ? seguimiento.getComprobanteSucursal() : "" %>"
					<% if (!esEdicion) { %> readonly="readonly" <%} %> />&nbsp;
				 <input
					id="<portlet:namespace />comprobante_nroSUR"
					name="<portlet:namespace />comprobante_nroSUR" size="11"
					maxlength="15" type="text"
					value="<%=Validator.isNotNull(seguimiento.getComprobanteNumero()) ? seguimiento.getComprobanteNumero() : "" %>"
					<% if (!esEdicion) { %> readonly="readonly" <%} %> />
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
			  <td><label><liferay-ui:message key="fecha" />:</label></td>
				<td colspan="2"><liferay-ui:input-date
						dayParam="fechaComprobanteSurDia"
						dayValue="<%=seguimiento.getComprobanteFecha() !=null?fechaPresentacion.get(Calendar.DAY_OF_MONTH ):fechaHasta.get(Calendar.DAY_OF_MONTH )%>"
						dayNullable="<%= true %>" monthParam="fechaComprobanteSurMes"
						monthValue="<%=seguimiento.getComprobanteFecha()!=null?fechaPresentacion.get(Calendar.MONTH ):fechaHasta.get(Calendar.MONTH )%>"
						monthNullable="<%= true %>" yearParam="fechaComprobanteSurAnio"
						yearValue="<%=seguimiento.getComprobanteFecha()!=null?fechaPresentacion.get(Calendar.YEAR ):fechaHasta.get(Calendar.YEAR ) %>"
						yearNullable="<%= true %>"
						yearRangeStart="<%= fechaHasta.get(Calendar.YEAR) - 5 %>"
						yearRangeEnd="<%= fechaHasta.get(Calendar.YEAR) %>"
						firstDayOfWeek="<%= fechaHasta.getFirstDayOfWeek() - 1 %>"
						disabled="<%= false %>" />
				</td>
				<td><label><liferay-ui:message key="importe" />:</label></td>
				
				<td><input id="<portlet:namespace />comprobante_importeSUR"
					name="<portlet:namespace />comprobante_importeSUR" size="20"
					maxlength="20" type="text"
					value='<%=seguimiento.getComprobanteImporte() ==null?"":seguimiento.getComprobanteImporte() %>' /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>
</fieldset>

<input type="hidden" name="<portlet:namespace />id_seguimiento" id="<portlet:namespace />id_seguimiento" value="<%=id_seguimiento%>" />

<input id="<portlet:namespace />close"
				value="<liferay-ui:message key="guardar"/>"
				title="<liferay-ui:message key="guardar" />"
				onClick="javascript: <portlet:namespace />salvarComprobanteSeguimiento();"
				type="button" /> 
				
<input type="hidden" name="<portlet:namespace/>validado" id="<portlet:namespace/>validado" value="" />
</form>

<script type="text/javascript">
function <portlet:namespace />salvarComprobanteSeguimiento(){
  if (<portlet:namespace />validarCampos()) {	
	var params = "&<%= Constants.CMD %>=" + "comprobante_save";
	 	params += "&id_seguimiento="+<%=id_seguimiento%>;
		params += "&comprobante_tiposur="+jQuery('#<portlet:namespace />comprobante_tipoSUR').val();
		params += "&comprobante_letrasur="+jQuery('#<portlet:namespace />comprobante_letraSUR').val();
		params += "&comprobante_sucursalsur="+jQuery('#<portlet:namespace />comprobante_sucursalSUR').val();
		params += "&comprobante_nrosur="+jQuery('#<portlet:namespace />comprobante_nroSUR').val();
		params += "&fechaComprobantesurdia="+jQuery('#<portlet:namespace />fechaComprobanteSurDia').val();
		params += "&fechaComprobantesurmes="+jQuery('#<portlet:namespace />fechaComprobanteSurMes').val();
		params += "&fechaComprobantesuranio="+jQuery('#<portlet:namespace />fechaComprobanteSurAnio').val();
		params += "&comprobante_importesur="+jQuery('#<portlet:namespace />comprobante_importeSUR').val();
			
	url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/autorizaciones/editar_seguimientosur';
	url = url + params;
	jQuery('#<portlet:namespace />listado_seguimientoSur').load(url);
			
	<portlet:namespace />cerrarPopComprobanteSeguimientoSur();
  }	  
  return false;		
}

function <portlet:namespace />validarCampos(){
	var result = true;
	
	if (jQuery("#<portlet:namespace />comprobante_sucursalSUR").val==""){
		result=false;
		alert("Debe ingresar la sucursal");
	}else{
		if (jQuery('#<portlet:namespace />comprobante_nroSUR').val()=="" ){
			result=false;
			alert("Debe ingresar un número de comprobante");
		}else{	
			if (jQuery("#<portlet:namespace />fechaComprobanteSurDia").val()=="" ||
					jQuery("#<portlet:namespace />fechaComprobanteSurMes").val()=="" ||
					jQuery("#<portlet:namespace />fechaComprobanteSurAnio").val()==""){
				result=false;
				alert("Debe ingresar una fecha.");
			}else{
				if (jQuery("#<portlet:namespace />comprobante_importeSUR").val()==""){
					result=false;
					alert("Debe ingresar un importe.");
				}			    
		    }   
	    }
	}	
	
	return result;
}

</script>

