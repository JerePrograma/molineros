<%@ include file="/html/portlet/rrhh/init.jsp" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />
<%

String tabNames="" ;
String tabValues="" ;
String cmd = (String) request.getAttribute(Constants.CMD);
String tabValue = ParamUtil.getString(request, "tab", null);
 
String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}

if(tabValue == null || StringUtils.checkEmpty(tabValue)){	
	tabValue = (String) request.getAttribute("tab");
}


tabNames="Datos Generales" ;
tabValues="datos" ;

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/rrhh/editar_borrar_tarjetas_entry");
portletURL.setParameter("tab", tabValue);
portletURL.setParameter("cmd", cmd);


%>
	
<liferay-ui-custom:tabs
	names="<%=tabNames%>"
	tabsValues="<%=tabValues%>"
	value="<%=tabValue%>" 
	url="<%= portletURL.toString() %>"
	param="tab" />
		
<c:choose>
	<c:when test='<%= tabValue.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/rrhh/view_tarjeta.jsp"/>
	</c:when>
</c:choose>


