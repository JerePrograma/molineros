<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%
    Nomenclador nomenclador=(Nomenclador)request.getSession().getAttribute(WebKeysAutorizaciones.NOMENCLADOR_EN_EDICION);
    String portlet_name = ParamUtil.getString(request, "portlet_name");
    
	String tabsA = ParamUtil.getString(request, "tabs1", "datos");
	StringBuilder tabsAValues = new StringBuilder("datos");
	
	if(tabsA==null || "".equalsIgnoreCase(tabsA)){
		tabsA="datos";
	}
		
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setParameter("struts_action","/autorizaciones/editar_nomenclador");
	portletURL.setParameter("tabs1", tabsA);
	portletURL.setParameter("view", "true");

	if (null != nomenclador) {
		portletURL.setParameter("cambioSolapa", "cambioSolapa");
	}	
	
	tabsAValues.append(",datos-contables");
	
	String tabsANames = StringUtil.replace(tabsAValues.toString(),
			StringPool.UNDERLINE, StringPool.DASH);
	
%>

<form action="" method="post" name="<portlet:namespace />nomen">
<input name="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="" />

<liferay-ui:tabs		
		names="<%= tabsANames %>"
		tabsValues="<%= tabsAValues.toString() %>"		
		portletURL="<%= portletURL %>" onClick="submitFormNotSave();"
/>

<c:choose>
	<c:when test='<%= tabsA.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/nomenclador/editar_nomenclador.jsp"/>
	</c:when>	
	<c:when test='<%= tabsA.equals("datos-contables")%>'>		
		<liferay-util:include page="/html/portlet/autorizaciones/nomenclador/editar_nomenclador_datos_contables.jsp"/>
	</c:when>
</c:choose>

</form>

