<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<portlet:defineObjects/>
	<br/>	
 	<c:choose>		
		<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>			
			<liferay-ui:success key="request_processed" message="grabar-exitoso" />
		</c:when>		
	</c:choose>

	<script type="text/javascript">	

 	<c:choose>
	<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>		
		<portlet:namespace />reloadPopupDetalle();
	</c:when>	
	</c:choose>
	</script>