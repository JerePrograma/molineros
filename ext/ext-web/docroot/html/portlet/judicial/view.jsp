<%@ include file="/html/portlet/afiliados/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);
//String altaContactoTab = (String) request.getAttribute(WebKeysCrm.PORTLET_TAB_CRM_CONTACTO);


boolean showABMCrmLegal = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM_LEGALES); 
boolean showCrmLegalBusqueda = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_CONSULTA_CRM_LEGALES); 

if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}


StringBuffer tabs1ValuesBuffer = new StringBuffer("");

if(showABMCrmLegal || showCrmLegalBusqueda){
	tabs1ValuesBuffer.append("reportes_reclamos");
}

String tabs1Values=tabs1ValuesBuffer.toString();

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
		tabs1="reportes_reclamos";	
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/judicial/view");
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
<!-- REPRESENTACIÓN DE LOS TABS DE JUDICIAL -->
<c:choose>
	<c:when test='<%= tabs1.equals("reportes_reclamos") %>'>
		<liferay-util:include page="/html/portlet/crm/busqueda_docum_legal.jsp"/>
	</c:when>
</c:choose>

</form>

<script type="text/javascript">

</script>	

