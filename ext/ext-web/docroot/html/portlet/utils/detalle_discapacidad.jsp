<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%

String cuil_titular = request.getParameter("cuil_titular");
String inte = request.getParameter("inte");
String view = (String)request.getParameter("view");
String searchURL = (String)request.getParameter("path");

boolean esView = true;
if(null==view || !view.equals("true")){
	esView = false;
}

boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_ABM_DISCAPACIDAD);
DetalleDiscapacidad detalleDiscapacidad = EditarAfiliadoServiceUtil.getDetalleDiscapacidad(cuil_titular, Integer.valueOf(inte)); 

//obtengo lista de session
List<CieDiez> cieDiez=(ArrayList<CieDiez>) portletSession.getAttribute(WebKeysGlobal.DOCUMENTOS_CIE,PortletSession.APPLICATION_SCOPE);
if (cieDiez == null) {
	cieDiez=TraeListasServiceUtil.getListadoCieDiez();
	portletSession.setAttribute(WebKeysGlobal.DOCUMENTOS_CIE,cieDiez,PortletSession.APPLICATION_SCOPE);	
}
List<TipoDiscapacidad> tiposDisc=(ArrayList<TipoDiscapacidad>) portletSession.getAttribute(WebKeysGlobal.TIPOS_DISCAPACIDAD,PortletSession.APPLICATION_SCOPE);
if (tiposDisc == null) {
	tiposDisc=TraeListasServiceUtil.getTiposDiscapacidad();
	portletSession.setAttribute(WebKeysGlobal.TIPOS_DISCAPACIDAD,tiposDisc,PortletSession.APPLICATION_SCOPE);	
}

%>

<%@page import="ar.com.ospim.administracion.WebKeysAdministracion"%>
<%@page import="ar.com.ospim.afiliados.WebKeysAfiliados"%><portlet:defineObjects />

<fieldset class="block-labels"><legend><liferay-ui:message key="det-discap" /></legend> 		
<table class="lfr-table">
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="observaciones-diagnostico"/>:</label></td>
		<td colspan="9"><textarea rows="2" cols="80" id="<portlet:namespace />diagnostico"
			name="<portlet:namespace />diagnostico" <% if (esView) { %> <%="readonly='readonly'" %> <%}%>  ><%= detalleDiscapacidad != null && detalleDiscapacidad.getDiagnostico() != null? detalleDiscapacidad.getDiagnostico() : "" %></textarea>
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="dependencia"/>:</label></td>
		<td><select name="<portlet:namespace/>dependencia" id="<portlet:namespace/>dependencia"
			<% if (esView) { %>
				disabled="disabled"
			<%} %> size="1">
			<option value="false" <%=Validator.isNotNull(detalleDiscapacidad) ? (!detalleDiscapacidad.isDependencia() ? "selected" : "") : ""%>>No</option>
			<option value="true" <%=Validator.isNotNull(detalleDiscapacidad) ? (detalleDiscapacidad.isDependencia() ? "selected" : "") : ""%>>Si</option>			
			</select>			 
			&nbsp;&nbsp;&nbsp;
			<label><liferay-ui:message key="telef-contacto"/>:</label>&nbsp;&nbsp;
			<input id="<portlet:namespace />telefono_contacto"
				name="<portlet:namespace />telefono_contacto" size="60" maxlength="60"
				type="text"
				value="<%= Validator.isNotNull(detalleDiscapacidad) ? detalleDiscapacidad.getTelefono_contacto() : "" %>" 
				<% if (esView) { %>
				readonly="readonly"
				<%} %>/>
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="ciex"/>:</label></td>
		<td><select name="<portlet:namespace/>cie_diez" id="<portlet:namespace/>cie_diez"
			<% if (esView) { %>
				disabled="disabled"
			<%} %>>
				<option value=""></option>
				<%
					for (CieDiez cd : cieDiez) {
				%>				
				<option value="<%=cd.getCodigo()%>"
				 <% if (Validator.isNotNull(detalleDiscapacidad) && Validator.isNotNull(detalleDiscapacidad.getCie_diez()) && detalleDiscapacidad.getCie_diez().equalsIgnoreCase(cd.getCodigo())) {%>
				 	selected="selected"  
				<%					
					}
				%>
				 					 	
				 ><%=cd.getDescripcion()%></option>
				<%					
					}
				%>
			</select>			 
		</td>
	</tr>
    <tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="tipo-discapacidad"/>:</label></td>
		<td><select name="<portlet:namespace/>tipo_discapacidad" id="<portlet:namespace/>tipo_discapacidad"
			<% if (esView) { %>
				disabled="disabled"
			<%} %> multiple="multiple">
				<option value=""></option>
				<%for (TipoDiscapacidad td : tiposDisc) {%>				
					<option value="<%=td.getId()%>"> <%=td.getDescripcion()%></option>
				<%}%>
			</select>			 
		</td>
	</tr>
	<tr>
		<td colspan="12">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="1">
<%	if (!esView && showABMButtons) {
%>
		<input type="button" value="<liferay-ui:message key="save" />" onClick="<portlet:namespace />grabarDetalle();" />
<%  }%>
		</td>
	</tr>
	
</table>
<div align="center" id="<portlet:namespace />buscandoDetalle">
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

<div align="center" id="<portlet:namespace />detalle_resultado">


</fieldset>

<script type="text/javascript">



<%if(detalleDiscapacidad!=null && StringUtils.checkNotEmpty(detalleDiscapacidad.getTiposDiscapacidadDelAfiliado()) ){ %>
	var values='<%=detalleDiscapacidad.getTiposDiscapacidadDelAfiliado()%>';

	jQuery.each(values.split(","), function(i,e){
		jQuery("#<portlet:namespace />tipo_discapacidad option[value='" + e + "']").attr("selected", true);
	});
	
<%}%>
	
	jQuery('#<portlet:namespace />buscandoDetalle').hide();

	function <portlet:namespace />grabarDetalle(){
		if(!<portlet:namespace />validarDatosDetalle()){
			return false;
		}else{
			jQuery('#<portlet:namespace />buscandoDetalle').show();

			var cuil_titular = <%=cuil_titular%>;
			var inte = <%=inte%>;
			var diagnostico = document.getElementById("<portlet:namespace />diagnostico").value;
			var dependencia = document.getElementById("<portlet:namespace />dependencia").value;
			var telefono_contacto = document.getElementById("<portlet:namespace />telefono_contacto").value;
			var cie_diez = document.getElementById("<portlet:namespace />cie_diez").value;
/* 			var tipos_disc = document.getElementById("<portlet:namespace />tipo_discapacidad").value; */
 			
			var selectedValues = [];    
		    jQuery("#<portlet:namespace />tipo_discapacidad :selected").each(function(){
		        selectedValues.push(jQuery(this).val()); 
		    });
			
	 		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=<%=searchURL%>&cuil_titular='+cuil_titular+'&inte='+inte+
				'&diagnostico='+encodeURI(diagnostico)+'&dependencia='+dependencia+'&telefono_contacto='+encodeURI(telefono_contacto)+'&cie_diez='+cie_diez+'&tiposDiscSel='+encodeURI(selectedValues);								
			
	 		jQuery('#<portlet:namespace />detalle_resultado').load(url, function() {
																			jQuery('#<portlet:namespace />buscandoDetalle').hide();            															
																			   } ); 
		}
	}
	
	function <portlet:namespace />validarDatosDetalle(){
		return true;
		//no hace nada, lo dejo por si piden algo		
	}
</script>