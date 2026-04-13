<%@ include file="/html/portlet/cai/init.jsp"%>
<%@ page import="ar.com.ospim.crm.beans.ContactoCRM"%>
<%
	String accion = (String) request.getAttribute(Constants.CMD);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}

	
	ContactoCRM contacto = null;
	Contacto crmafi = (Contacto) request.getAttribute(WebKeysCrm.CRM_PERSONAL_SECCIONAL );
	if(esView){
		contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
		if(crmafi==null && contacto!=null){
			crmafi=contacto.getContactoSeccional();
		}
//	}else{
//		contacto = (ContactoCRM) request.getSession().getAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
	}

	
	
%>

<%if(crmafi!=null) {%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key="crm-sec-pers" /></legend>
	<input name="<portlet:namespace />id_contactoSeccional" type="hidden" value="<%=crmafi.getIdContacto()%>" />
	
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >	
		<tr>
			<td><label><liferay-ui:message key="nombre" />:</label></td>
			<td><input type="text"
					   id="<portlet:namespace />nombre_personal_seccional"
					   name="<portlet:namespace />nombre_personal_seccional" size="30" 
					   value='<%=crmafi.getNombreApe()  %>'
					   readonly='readonly'/>
			</td>
			<td><label><liferay-ui:message key="cargo" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />cargo_personal_seccional"
				name="<portlet:namespace />cargo_personal_seccional" size="30" 
				value="<%=crmafi.getCargoDescripcion() %>" 
				readonly="readonly" />
			</td>
			<td><label><liferay-ui:message key="seccional" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />descripcion_personal_seccional"
				name="<portlet:namespace />descripcion_personal_seccional" size="30" 
				value="<%=crmafi.getSeccional().getDescripcion() %>" 
				readonly="readonly" />
			</td>
			<td><label><liferay-ui:message key="ID" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />id_contactoSeccional_1"
				name="<portlet:namespace />id_contactoSeccional_1" size="10" 
				value="<%=crmafi.getIdContacto()%>" 
				readonly="readonly" />
				<input type="hidden" 
			    id="<portlet:namespace />id_contactoSeccional"
				name="<portlet:namespace />id_contactoSeccional" 
				value="<%=crmafi.getIdContacto()%>" />
			</td>
			
		</tr>
		
			
	</table>
</fieldset>	
<%} %>
