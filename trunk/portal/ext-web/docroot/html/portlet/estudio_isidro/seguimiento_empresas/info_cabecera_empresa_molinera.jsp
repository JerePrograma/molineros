<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.beans.RamoEmpresa" %>
<%@ page import="ar.com.empresas.WebKeysEmpresas" %>
<portlet:defineObjects/>

<%
 		Empresa llest=((LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO)).getEmpresa();
		List<RamoEmpresa> ramos = (ArrayList<RamoEmpresa>) portletSession.getAttribute(WebKeysEmpresas.RAMOS_EMPRESA_EN_SESSION,
				PortletSession.APPLICATION_SCOPE);
%>

<table border="1" width="100%">
	<tr>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cuit"/></b>
		</td >
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="razon-social"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="seccional"/></b>
		</td>	
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="actividad-principal"/></b>
		</td>		
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="a-encuadrar"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="F.U.M."/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="usuario"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="edit"/></b>
		</td>																											
	</tr>			
	<tr>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0">
			<%=llest.getCuit()%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
			<%=llest.getRazon_soc().trim()%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"" >
			<%=llest.getDescripcionSeccional().trim()%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<% for (RamoEmpresa ramo: ramos) { %>			
				<%= llest != null && llest.getRamoEmpresa()!= null && llest.getRamoEmpresa().equals(ramo) ? ramo.getDescripcion() : ""  %>				
			<% } %>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<%=llest.getRamoEmpresa()!= null && (llest.getRamoEmpresa().getId_ramo_empresa()>0 &&
					llest.getRamoEmpresa().getId_ramo_empresa()!=99 ||
					llest.getRamoEmpresa().getId_ramo_empresa()!=90)?"NO":"SI"%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<%=llest.getModi_fechaAsString()%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<%=llest.getModi_usr()%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<img alt="<liferay-ui:message key='edit'/>" src="<%=themeDisplay.getPathThemeImages()%>/common/edit.png" 
			   onClick="javascript:verInfoEmpresa('<%=llest.getCuit()%>','000')" />
		</td>
		
	</tr>				
</table>	