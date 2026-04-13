
<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%
Empresa empresa = (Empresa)request.getSession().getAttribute(WebKeysEmpleadores.EMPRESA_EN_EDICION);

String tabsA = ParamUtil.getString(request, "tabs1", "datos");
StringBuilder tabsAValues = new StringBuilder("datos");

if(renderResponse==null || !renderResponse.getNamespace().equals("_EST_1_")) {
	tabsAValues.append(",datos-fiscales");
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/afiliados/view_empleadores_entry");

portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("view", "true");


if(null!=empresa){
	portletURL.setParameter("cambioSolapa","cambioSolapa");

}
String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH); 
%>

<form action="">

<liferay-ui:tabs		
	names="<%= tabsANames %>"
	tabsValues="<%= tabsAValues.toString() %>"		
	portletURL="<%= portletURL %>"
/>

<c:choose>
	<c:when test='<%= tabsA.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/empresas/view_empleador.jsp"/>
		
	</c:when>	
	<c:when test='<%= tabsA.equals("datos-fiscales")%>'>		
		<liferay-util:include page="/html/portlet/empresas/view_datos_fiscales.jsp"/>
	</c:when>	
</c:choose>

</form>
