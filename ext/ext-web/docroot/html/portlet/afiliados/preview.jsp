<%@ include file="/html/portlet/init.jsp" %>
<%@ page import="javax.servlet.ServletContext" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>

<%
String menuUrl = PortalUtil.getCurrentCompleteURL(request);
String display = "";
if(menuUrl.contains("/web/guest/afiliaciones")){
	display = "afi";
}else{ // novedades sss
	display = "nov";
}
%>

<!-- REPRESENTACIÓN DE LAS PAGINAS DE AFILIACIONES/NOVEDADES -->
<c:choose>
	<c:when test='<%= display.equals("afi")%>'>	
		<liferay-util:include page="/html/portlet/afiliados/view.jsp"/>		
	</c:when>
	<c:when test='<%= display.equals("nov") %>'>
		<liferay-util:include page="/html/portlet/novedades/view.jsp"/>	
	</c:when>
	
</c:choose>
