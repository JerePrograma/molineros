<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%
    String portlet_name = ParamUtil.getString(request, "portlet_name");
    Seccional seccional=(Seccional)request.getSession().getAttribute(WebKeysAfiliados.ABM_SECCIONAL_EN_SESSION);
	String tabsA = ParamUtil.getString(request, "tabs1", "datos");
	StringBuilder tabsAValues = new StringBuilder("datos");
	
	if(tabsA==null || "".equalsIgnoreCase(tabsA)){
		tabsA="datos";
	}
		
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setParameter("struts_action","/afiliados/editar_seccional");
	portletURL.setParameter("tabs1", tabsA);
	portletURL.setParameter("view", "true");

	if (null != seccional) {
		portletURL.setParameter("cambioSolapa", "cambioSolapa");
	}	
	
	tabsAValues.append(",datos-contactos");
	
	String tabsANames = StringUtil.replace(tabsAValues.toString(),
			StringPool.UNDERLINE, StringPool.DASH);
	
%>

<form action="" method="post" name="<portlet:namespace />fmSecc">
<input name="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="" />

<liferay-ui:tabs		
		names="<%= tabsANames %>"
		tabsValues="<%= tabsAValues.toString() %>"		
		portletURL="<%= portletURL %>" onClick="submitFormNotSave();"
/>

<c:choose>
	<c:when test='<%= tabsA.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/afiliados/editar_seccional.jsp"/>
	</c:when>	
	<c:when test='<%= tabsA.equals("datos-contactos")%>'>		
		<liferay-util:include page="/html/portlet/afiliados/editar_seccional_contactos.jsp"/>
	</c:when>
</c:choose>

</form>

