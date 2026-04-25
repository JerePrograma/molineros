<%@ include file="/html/portlet/prestadores/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
 	<c:choose>		
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
			<liferay-ui:success key="request_processed" message="grabar-exitoso" />
		</c:when>		
	</c:choose>