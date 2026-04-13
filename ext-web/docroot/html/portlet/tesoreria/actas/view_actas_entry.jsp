
<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%
Acta acta = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

String tabsA = ParamUtil.getString(request, "tabs1", "datos");
StringBuilder tabsAValues = new StringBuilder("datos");
//	tabsAValues.append(",detalle-acta-inspectores");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/tesoreria/view_actas_entry");
portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("view", "true");

if(null!=acta){
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
		<liferay-util:include page="/html/portlet/tesoreria/actas/view_acta.jsp"/>
	</c:when>	
</c:choose>
<!--  <when test='<tabsA.equals("detalle-acta-inspectores")>'>		
		<clude page="/html/portlet/tesoreria/actas/view_detalle_acta_inspectores.jsp"/>
	</when>	-->

</form>
