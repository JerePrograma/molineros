
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%
Convenio convenio= (Convenio)request.getSession().getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);

String tabsA = ParamUtil.getString(request, "tabs1", "convenio");
StringBuilder tabsAValues = new StringBuilder("convenio");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/tesoreria/editar_convenios_entry");
portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("view", "true");

if(null!=convenio){
	portletURL.setParameter("cambioSolapa","cambioSolapa");
}
String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH); 
%>
<form action="" method="post" name="<portlet:namespace />conv" >

<liferay-ui:tabs		
	names="<%= tabsANames %>"
	tabsValues="<%= tabsAValues.toString() %>"		
	portletURL="<%= portletURL %>" onClick="submitFormNotSave();"
/>

<c:choose>
	<c:when test='<%= tabsA.equals("convenio") %>'>
		<liferay-util:include page="/html/portlet/tesoreria/convenios/view_convenio.jsp"/>
	</c:when>	
</c:choose>

</form>
