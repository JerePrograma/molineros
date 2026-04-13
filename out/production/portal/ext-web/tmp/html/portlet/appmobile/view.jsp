<%@ include file="/html/portlet/appmobile/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);
if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}


StringBuffer tabs1ValuesBuffer = new StringBuffer("gestion-appmobile");

boolean showAdministracion=PermissionUtil.userContainsRole(user,WebKeysAppMobile.ROL_ADMINISTRACION );




if (showAdministracion ) {
	if(tabs1ValuesBuffer.length()>0){
	  tabs1ValuesBuffer.append(",integracion");
	}else{
	  tabs1ValuesBuffer.append("integracion");	
	}
}
	

String tabs1Values=tabs1ValuesBuffer.toString();

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
		tabs1="gestion-appmobile";	
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/appmobile/view");
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
<!-- REPRESENTACIÓN DE LOS TABS DE COMPROBANTES -->
<c:choose>
	<c:when test='<%= tabs1.equals("gestion-appmobile") %>'>	
		<liferay-util:include page="/html/portlet/appmobile/appmobile_admin_list.jsp"/>
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