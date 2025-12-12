<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
 		Empresa llest=((LlamadosEstudio)portletSession.getAttribute(WebKeysEstudioIsidro.LLAMADOS_ESTUDIO)).getEmpresa();
%>

<table border="1">
	<tr>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="cuit"/><%=request.getContextPath()%></b>
		</td >
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="razon-social"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="contacto"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="telefono"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="email"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="domicilio"/></b>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #808080; color: #ffffff ">
			<b><liferay-ui:message key="molinera"/></b>
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
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<%=llest.getContactosEConcat("PERSONAL").trim()%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<%=llest.getContactosEConcat("TELEFONO").trim() %>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<%=llest.getContactosEConcat("EMAIL").trim()%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<%=llest.getDomicilioAsString()%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<%=llest.isMolinera()?"SI":"NO"%>
		</td>
		<td style="text-align: center;padding-right: 10px; padding-left: 10px; background-color: #fbfbf0"">
			<img alt="<liferay-ui:message key='edit'/>" src="<%=themeDisplay.getPathThemeImages()%>/common/edit.png" 
			   onClick="javascript:verInfoEmpresa('<%=llest.getCuit()%>','000')" />
		</td>
	</tr>				
</table>	