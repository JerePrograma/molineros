<%@ include file="/html/portlet/correspondencia/init.jsp" %>
<%@ page import="com.liferay.portal.util.WebKeys" %>

<%
if(!themeDisplay.isSignedIn()){	
	request.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.FALSE);
}

String tabs1 = ParamUtil.getString(request, "tabs1", null);

boolean showOpcionesABM = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);

if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}

StringBuffer tabs1ValuesBuffer = new StringBuffer("");
tabs1ValuesBuffer.append("bandeja-de-entrada");

if (showOpcionesABM) {
	tabs1ValuesBuffer.append(",entradas-salidas,correo-interno");
}

String tabs1Values=tabs1ValuesBuffer.toString();

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){
		tabs1="bandeja-de-entrada";
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");



PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(WindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/correspondencia/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>



<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
/>
<!-- REPRESENTACIÓN DE LOS TABS DE CORRESPONDENCIA -->
<c:choose>
	<c:when test='<%= tabs1.equals("bandeja-de-entrada") %>'>
		<liferay-util:include page="/html/portlet/correspondencia/bandeja_de_entrada.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("entradas-salidas") %>'>
		<liferay-util:include page="/html/portlet/correspondencia/entradas_salidas.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("correo-interno") %>'>
		<liferay-util:include page="/html/portlet/correspondencia/correo_interno.jsp">			
		</liferay-util:include>
	</c:when>
</c:choose>
</form>
