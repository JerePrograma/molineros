<%@ include file="/html/portlet/crm/init.jsp"%>
<%
	String accion = (String) request.getAttribute(Constants.CMD);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	ContactoCRM contacto = null;
	
	if(esView){
		contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
	}else{
		contacto = (ContactoCRM) request.getSession().getAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
	}

	NoAfiliado crmNoAfi = contacto!=null?contacto.getNoAfiliado():null; /* (NoAfiliado) request.getAttribute(WebKeysCrm.CRM_AFILIADO); */
%>

<%-- <%if(crmNoAfi!=null) {%> --%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key='no-afiliado' /></legend>
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >	
		<tr>
			<td><label><liferay-ui:message key="tipo-documento" />:</label></td>
			<td>
				<select id="<portlet:namespace/>noafi_documento_tipo"
					    name="<portlet:namespace/>noafi_documento_tipo" >
					<%	for (String tipoDoc : WebKeysAfiliados.TIPOS_DOCUMENTO) {	
					
						if(crmNoAfi!=null && crmNoAfi.getDocumentoTipo().equals(tipoDoc)){ %>
							<option selected="selected" value="<%= tipoDoc %>"> <%=tipoDoc%></option> 
						<%} else if(crmNoAfi==null && tipoDoc.equalsIgnoreCase(WebKeysAfiliados.TIPO_DOCUMENTO_DEFAULT)){ %>
							<option selected="selected" value="<%= tipoDoc %>"> <%=tipoDoc%></option>
						<%} else {%>
							<option value="<%= tipoDoc %>"> <%=tipoDoc%></option>
						<%}	
						} //for
						%>
				</select>
			</td>
			<td><label><liferay-ui:message key="nro-documento" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />noafi_documento_nro"
				name="<portlet:namespace />noafi_documento_nro" size="9" maxlength="8"
				value="<%=crmNoAfi!=null?crmNoAfi.getDocumentoNumero():""%>" 
				onblur="javascript:<portlet:namespace />validarTipoDocumento();"/></td>
	<!-- 	</tr>
		<tr> -->
			<td><label><liferay-ui:message key="apellido" />:</label></td>
			<td><input id="<portlet:namespace />noafi_apellido"
				name="<portlet:namespace />noafi_apellido" size="20" maxlength="100"
				type="text"
				value="<%=crmNoAfi!=null?crmNoAfi.getApellido():""%>" />
			</td>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td><input id="<portlet:namespace />noafi_nombre"
				name="<portlet:namespace />noafi_nombre" maxlength="100" type="text"
				value="<%=crmNoAfi!=null?crmNoAfi.getNombre():""%>"  />
			</td>			
		</tr>
		<tr>
			<td colspan="4">&nbsp;</td>
			<td colspan="1"><label><liferay-ui:message key="telefono" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />noafi_telefono"
				name="<portlet:namespace />noafi_telefono" size="15" maxlength="15"
				type="text" value="<%=crmNoAfi!=null?crmNoAfi.getTelefono():""%>" />
			<td colspan="1"><label><liferay-ui:message key="email-short" />:</label></td>
			<td colspan="1"><input id="<portlet:namespace />noafi_email"
			name="<portlet:namespace />noafi_email" size="50" maxlength="50" onblur="javascript:<portlet:namespace />validarEmail();"
			type="text" value="<%=crmNoAfi!=null?crmNoAfi.getEmail():""%>" /></td>	
		</tr>
	</table>
</fieldset>	
<%-- <%} %> --%>

<script type="text/javascript">
function validarEmail() {
	var email = jQuery('#<portlet:namespace/>noafi_email').val();
	var emailReg = /^([\da-z_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
	
	if(trim(email).length > 0){	
		if( !emailReg.test( email ) ) {
			return false;
		} else {
			return true;
		}
	}else{
		return true;
	}	
}

function <portlet:namespace />validarTipoDocumento(){		
    var tipoDoc = jQuery('#<portlet:namespace />noafi_documento_tipo').val();
    var numeroDoc = jQuery('#<portlet:namespace />noafi_documento_nro').val();
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/afiliados/validar_tipo_documento&tipodoc='+tipoDoc+'&nrodoc='+numeroDoc;
			
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			if(obj.validado=="1"){
				alert("<liferay-ui:message key='cuil-invalido'/>");
				jQuery('#<portlet:namespace />cuil_validado').val('false');
			}else if(obj.validado=="2"){
				alert("<liferay-ui:message key='cuil-titular-existente'/>");
				jQuery('#<portlet:namespace />cuil_validado').val('false');
			}else{
				jQuery('#<portlet:namespace />cuil_validado').val('true');
			}					
		}				                                                                                                                                                                                                                                                            
		
	});
	
}
</script>