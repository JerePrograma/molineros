<%@ include file="/html/portlet/cai/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);
if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}


StringBuffer tabs1ValuesBuffer = new StringBuffer("contactos");


tabs1ValuesBuffer.append(",reportes_contactos");
tabs1ValuesBuffer.append(",estadisticas_contactos");

String tabs1Values=tabs1ValuesBuffer.toString();

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
		tabs1="contactos";	
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/cai/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>

<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm">
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
/>
<!-- REPRESENTACIÓN DE LOS TABS DE CAI -->
<c:choose>
	<c:when test='<%= tabs1.equals("contactos") %>'>	
		<liferay-util:include page="/html/portlet/cai/cai_contactos.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("reportes_contactos") %>'>
		<liferay-util:include page="/html/portlet/crm/busqueda_contacto.jsp"/>
	</c:when>
	<c:when test='<%= tabs1.equals("estadisticas_contactos") %>'>
		<liferay-util:include page="/html/portlet/crm/estadistica_contacto.jsp"/>
	</c:when>
	
</c:choose>

</form>

<script type="text/javascript">
	
</script>	

<%
request.getSession().removeAttribute("opciones"); 
if (!tabs1.equals("afiliados")) {
	PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(tabs1, StringPool.UNDERLINE, StringPool.DASH)), request);
}
%>