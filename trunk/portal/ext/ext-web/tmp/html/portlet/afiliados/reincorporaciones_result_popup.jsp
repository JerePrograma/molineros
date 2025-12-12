<%@ include file="/html/portlet/afiliados/init.jsp" %>
<portlet:defineObjects/>
 	<c:choose>
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
			<liferay-ui:success key="request_processed" message="grabar-exitoso" />
		</c:when>
	</c:choose>
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />