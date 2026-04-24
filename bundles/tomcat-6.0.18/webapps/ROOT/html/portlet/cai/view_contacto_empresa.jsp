<%@ include file="/html/portlet/cai/init.jsp"%>
<%@ page import="ar.com.ospim.crm.beans.ContactoCRM"%>
<%
	String accion = (String) request.getAttribute(Constants.CMD);
	boolean esView = false;
	
	if (accion != null && accion.equalsIgnoreCase(Constants.VIEW)){
		esView = true;
	}
	
	Empresa empresa = (Empresa) request.getAttribute(WebKeysCrm.CRM_EMPRESA);
	ContactoCRM contacto = null;
	if(esView){
		contacto = (ContactoCRM) request.getAttribute(WebKeysCrm.CRM_CONTACTO_EN_VIEW);
	}else{
		contacto = (ContactoCRM) request.getSession().getAttribute(WebKeysCrm.CRM_CONTACTO_EN_EDICION);
	}
	if(contacto != null){
		empresa = contacto.getEmpresa();
	}

%>

<%if(empresa!=null) {%>
<fieldset class="block-labels">
	<legend><liferay-ui:message key="empresa" /></legend>	
	<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;" >	
 		<tr>
			<td><label><liferay-ui:message key="cuit" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />cuit_empresa_"
				name="<portlet:namespace />cuit_empresa_" size="13" 
				value="<%=empresa.getCuit() %>" 
				readonly="readonly" />
				<input type="hidden" 
			    id="<portlet:namespace />cuit"
				name="<portlet:namespace />cuit"
				value="<%=empresa.getCuit() %>" />
			</td>
			<td><label><liferay-ui:message key="sucursal" />:</label></td>
			<td><input type="text"
					   id="<portlet:namespace />sucursal_empresa_"
					   name="<portlet:namespace />sucursal_empresa_" size="6" 
					   value='<%=empresa.getSucursal()  %>'
					   readonly='readonly'/>
				<input type="hidden"
					   id="<portlet:namespace />sucursal"
					   name="<portlet:namespace />sucursal" 
					   value='<%=empresa.getSucursal()  %>' />	   
			</td>
			<td><label><liferay-ui:message key="razon-social" />:</label></td>
			<td><input type="text" 
			    id="<portlet:namespace />razon_soc_empresa"
				name="<portlet:namespace />razon_soc_empresa" size="50" 
				value="<%=empresa.getRazon_soc() %>" 
				readonly="readonly" />
			</td>
		</tr> 
	</table>
</fieldset>	
<%} %>
