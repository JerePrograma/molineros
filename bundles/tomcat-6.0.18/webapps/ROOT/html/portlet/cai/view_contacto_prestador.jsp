<%@ include file="/html/portlet/cai/init.jsp"%>
<%@ page import="ar.com.ospim.crm.beans.ContactoCRM"%>
<%
	String accion = (String) request.getAttribute(Constants.CMD);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	Prestador prestador = (Prestador) request.getAttribute(WebKeysCrm.CRM_PRESTADOR);
	
	ContactoCRM contacto = null;
	if(esView){
		contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
	}else{
		contacto = (ContactoCRM) request.getSession().getAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
	}
	
	if(contacto != null){
		prestador = contacto.getPrestador();
	}

%>

<%if(prestador!=null) {%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key="prestador" /></legend>	
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >	
 		<tr>
			<td><label><liferay-ui:message key="cod-prestador" />:</label></td>
			<td><input type="text"
					   id="<portlet:namespace />id_prestador_"
					   name="<portlet:namespace />id_prestador_" size="6" 
					   value='<%=prestador.getId_prestador()  %>'
					   readonly='readonly'/>
				<input type="hidden"
					   id="<portlet:namespace />idPrestador"
					   name="<portlet:namespace />idPrestador" 
					   value='<%=prestador.getId_prestador()  %>' />	   
			</td>
			<td><label><liferay-ui:message key="cuit" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />cuit_prestador"
				name="<portlet:namespace />cuit_prestador" size="13" 
				value="<%=prestador.getCuit() %>" 
				readonly="readonly" />
			</td>
			<td><label><liferay-ui:message key="descripcion" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />descripcion_prestador"
				name="<portlet:namespace />descripcion_prestador" size="50" 
				value="<%=prestador.getDescripcion() %>" 
				readonly="readonly" />
			</td>
		</tr> 
	</table>
</fieldset>	
<%} %>
