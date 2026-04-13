<%@page import="ar.com.ospim.crm.beans.EdificioSectorUsuarioLiferay"%>
<%@ include file="/html/portlet/cai/init.jsp"%>
<%@ page import="ar.com.ospim.crm.beans.ContactoCRM"%>
<%
	String accion = (String) request.getAttribute(Constants.CMD);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	EdificioSectorUsuarioLiferay esu = (EdificioSectorUsuarioLiferay) request.getAttribute(WebKeysCrm.CRM_COMPANIERO);
	ContactoCRM contacto = null;
	if(esView){
		contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
	}else{
		contacto = (ContactoCRM) request.getSession().getAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
	}

	if(contacto!=null){
		esu = contacto.getCompaniero();
	}
%>

<%if(esu!=null) {%>

<fieldset class="block-labels">
	<legend><liferay-ui:message key="crm-usu" /></legend>	
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >	
		<input type="hidden"
			id="<portlet:namespace />edificio"
			name="<portlet:namespace />edificio" 
			value='<%=esu.getEdificio()  %>' />
		<input type="hidden"
			id="<portlet:namespace />sector"
			name="<portlet:namespace />sector" 
			value='<%=esu.getGrupo()%>' />
		<input type="hidden"
			id="<portlet:namespace />usuario"
			name="<portlet:namespace />usuario" 
			value='<%=esu.getUsuario()  %>' />		
 		<tr>
			<td><label><liferay-ui:message key="edificio" />:</label></td>
			<td><input type="text"
					   id="<portlet:namespace />edificio_desc"
					   name="<portlet:namespace />edificio_desc" size="15" 
					   value='<%=esu.getEmpresaDescripcion()  %>'
					   readonly='readonly'/>
			</td>
			<td><label><liferay-ui:message key="sector" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />sector_desc"
				name="<portlet:namespace />sector_desc" size="20" 
				value="<%=esu.getSectorDescripcion() %>" 
				readonly="readonly" />
			</td>
			<td><label><liferay-ui:message key="usuario" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />usuario_apaynom"
				name="<portlet:namespace />usuario_apeynom" size="50" 
				value="<%=esu.getUsuarioApeyNom() %>" 
				readonly="readonly" />
			</td>
		</tr> 
	</table>
</fieldset>	
<%} %>
