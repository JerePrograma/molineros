<%@ include file="/html/portlet/administracion/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);
/*if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}
if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}*/
if (tabs1 == null){
	tabs1="reportesautomaticos"; 
}

String tabs1Values = "reportesautomaticos";

	
String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(WindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/administracion/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>

<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm">
<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
/>
<c:choose>
	<c:when test='<%= tabs1.equals("reportesautomaticos") %>'>	
		<liferay-util:include page="/html/portlet/administracion/reportes_automaticos/busqueda_reportes_automaticos.jsp"/>		
	</c:when>	
</c:choose>

</form>

