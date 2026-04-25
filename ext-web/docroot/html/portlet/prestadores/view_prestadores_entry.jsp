<%@ include file="/html/portlet/prestadores/init.jsp" %>
<%
Prestador prestador = (Prestador)request.getSession().getAttribute(WebKeysLiquidaciones.PRESTADOR_EN_EDICION);

String tabsA = ParamUtil.getString(request, "tabs1", "");
StringBuilder tabsAValues = new StringBuilder("datos");
tabsAValues.append(",lugar-at");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/liquidaciones/editar_prestadores_entry");
portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("view", "true");

String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH); 
%>

<%-- <form action="EditarPrestadoresEntryAction">
 	<input type="hidden" name="<%=Constants.CMD%>" value="<%=request.getAttribute(Constants.CMD)%>"/> --%>

<liferay-ui-custom:tabs 
		names="<%= tabsANames %>" 
		tabsValues="<%= tabsAValues.toString() %>" 
		portletURL="<%= portletURL %>"  
		value="<%= tabsA%>"/> 

<c:choose>
	<c:when test='<%= tabsA.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/prestadores/view_prestador.jsp"/>
	</c:when>
	<c:when test='<%= tabsA.equals("lugar_atencion") %>'>
		<liferay-util:include page="/html/portlet/prestadores/prestador_lugar_atencion.jsp"/>
	</c:when>
		
</c:choose>

<!-- </form> -->
