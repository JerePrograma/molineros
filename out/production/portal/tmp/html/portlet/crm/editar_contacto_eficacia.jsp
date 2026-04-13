<%@ include file="/html/portlet/crm/init.jsp"%>

<%
String accion = (String) request.getAttribute(Constants.CMD);

CRMEficacia crmEficacia = (CRMEficacia) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EFICACIA);

Integer idContacto = (Integer) request.getAttribute(WebKeysCrm.CRM_ID_CONTACTO);

boolean esView = false;
%>
<form action="" method="post" name="<portlet:namespace />fm">

	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="<%=accion%>" />

<liferay-ui:success key="insertEficaciaOk"  message="<%=(String)request.getAttribute(\"msgEficaciaOk\")  %>"  />
<liferay-ui:success key="updateEficaciaOk"  message="<%=(String)request.getAttribute(\"msgEficaciaOk\")  %>"  />
<liferay-ui:success key="deleteEficaciaOk"  message="<%=(String)request.getAttribute(\"msgEficaciaOk\")  %>"  />

<fieldset class="block-labels">
<legend><liferay-ui:message key="carga-verifica-efic" /></legend>
<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
	<tr>
		<td colspan="1"><label><liferay-ui:message key="crm-contacto-nro" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />crm_efi_id_contacto"
			name="<portlet:namespace />crm_efi_id_contacto" size="10" maxlength="6"
			type="text" value="<%=crmEficacia!=null?crmEficacia.getIdContacto():idContacto %>" 
			readonly="readonly"/></td>	
		<td colspan="1"><label><liferay-ui:message key="crm-eficacia-id" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />id_eficacia"
			name="<portlet:namespace />id_contacto" size="10" maxlength="6"
			type="text" value="<%if( crmEficacia != null ){ crmEficacia.getId(); } %>" 
			readonly="readonly"/></td>
	</tr>
	<tr>
		<td colspan="1"><label><liferay-ui:message key="crm-eficacia-contacto_a" />:</label></td>
		<td colspan="1"><input id="<portlet:namespace />crm_efi_contacto_a"
			name="<portlet:namespace />crm_efi_contacto_a" size="100" maxlength="100"
			type="text" value="<%= crmEficacia != null ? crmEficacia.getContacto_a() : new String("")  %>" /></td>
		<td colspan="1"><label><liferay-ui:message key="crm-eficacia-conforme" />:</label></td>
		<td colspan="1"><select id="<portlet:namespace />crm_efi_conforme" name="<portlet:namespace />crm_efi_conforme">
							<option <%if(crmEficacia!=null && crmEficacia.isConforme()) {%> selected="selected" <% } %> >SI</option>
							<option <%if(crmEficacia!=null && !crmEficacia.isConforme()) {%> selected="selected" <% } %> >NO</option>
						</select>
		</td>
	</tr>
	<tr>
		<td><label><liferay-ui:message key="crm-eficacia-obs" />:</label></td>
		<td colspan="3"><textarea rows="6" cols="100" maxlength="20000" 
					id="<portlet:namespace />crm_efi_observaciones" 
					name="<portlet:namespace />crm_efi_observaciones"
					style="resize: none;" ><%= crmEficacia !=null ? crmEficacia.getObservaciones() : new String("") %></textarea>
		</td>			
	</tr>
	<tr>
		<td>
			<label><liferay-ui:message key="crm-eficacia-reapertura" />:</label>
			<input type="checkbox"  name="<portlet:namespace/>crm_efi_reapertura" id="<portlet:namespace/>crm_efi_reapertura" 
					onclick="javascript:<portlet:namespace />activaUsuarioDerivacion();" >
		</td>
		<td colspan="3">
			<div align="center" id="<portlet:namespace />divUsuarioDerivacion" >
				<liferay-util:include page="/html/portlet/crm/derivacion_liferay.jsp">
					<liferay-util:param name="view" value="<%=esView?new String(\"true\"):new String(\"false\") %>"/>
				</liferay-util:include>
			</div>
		</td>
	</tr>		
</table>
</fieldset>
<br/>
<table class="lfr-table">
	<tr>
		<td>
			<input type="button" name="grabarEfi" value="Aceptar" onClick='javascript:grabarEficacia();'  >
		</td>
		<!-- <td>&nbsp;</td>
		<td>
			<input type="button" name="cancelarCuil" value="Cancelar" >
		</td> -->
	</tr>
</table>
</form>

<script type="text/javascript" >
jQuery('#<portlet:namespace />divUsuarioDerivacion').hide();

function grabarEficacia(){
 	if (<portlet:namespace />validarCamposEficacia()) {
 		
 		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
				   	<portlet:param name="struts_action" value="/afiliados/editar_eficacia_entry" />
					<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.SAVE %>" />
				   </portlet:renderURL>';
	    <c:if test='<%=(renderResponse!=null && renderResponse.getNamespace()!=null && renderResponse.getNamespace().equals("_CAI_1_"))%>'>
	        url = '<portlet:renderURL windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>">
		   	<portlet:param name="struts_action" value="/cai/editar_eficacia_entry" />
			<portlet:param name="<%=Constants.CMD%>" value="<%=Constants.SAVE %>" />
		   </portlet:renderURL>';
	    </c:if>
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);

	} 
	
	
 
}

function <portlet:namespace />validarCamposEficacia() {
	
	var contactoa=jQuery("#<portlet:namespace />crm_efi_contacto_a").val();
	var conforme=jQuery("#<portlet:namespace />crm_efi_conforme").val();
	var obs=jQuery("#<portlet:namespace />crm_efi_observaciones").val();
		
 	if (trim(contactoa).length == 0){
		alert("Debe ingresar a quien contactó");
		jQuery('#<portlet:namespace />crm_efi_contacto_a').focus();
		return false;
	} 
	if (trim(conforme).length == 0){
		alert("Seleccionar si hay conformidad");
		jQuery('#<portlet:namespace/>crm_efi_conforme').focus();
		return false;
	}
	if (trim(obs).length == 0){
		alert("Ingrese alguna observación");
		jQuery('#<portlet:namespace/>crm_efi_observaciones').focus();
		return false;
	}
	
	var reApert = document.getElementById("<portlet:namespace/>crm_efi_reapertura");
	var esReApert = reApert.checked ? 'true' : 'false';
	
	if( esReApert == 'true'){
		var sec_dest = jQuery('#<portlet:namespace />sector_destino').val();
		var usu_dest = jQuery('#<portlet:namespace />usuario_destino').val();
		
		if(sec_dest==''){
			alert("Debe seleccionar el Sector derivación");
			return false;
		}
		if(usu_dest==''){
			alert("Debe seleccionar el Usuario derivación");
			return false;
		}
	}
	return true;
}

function <portlet:namespace />activaUsuarioDerivacion(){
	var check = jQuery("#<portlet:namespace />crm_efi_reapertura").is(':checked');
	if (check){
    	jQuery('#<portlet:namespace />divUsuarioDerivacion').show();
    	jQuery('#<portlet:namespace />crm_efi_conforme').find('option[value="NO"]').attr("selected",true);
    }else{
    	jQuery('#<portlet:namespace />divUsuarioDerivacion').hide();
    	jQuery('#<portlet:namespace />crm_efi_conforme').find('option[value="SI"]').attr("selected",true);
    }
} 


</script>