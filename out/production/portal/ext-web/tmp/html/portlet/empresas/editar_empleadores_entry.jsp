<%@ include file="/html/portlet/empresas/init.jsp" %>
<%
	Empresa empresa = (Empresa) request.getSession().getAttribute(
			WebKeysEmpresas.EMPRESA_EN_EDICION);	
	
	String tabsA = ParamUtil.getString(request, "tabs1", "datos");
	StringBuilder tabsAValues = new StringBuilder("datos");
		
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setParameter("struts_action","/"+portlet_name+"/editar_empleadores_entry");
	portletURL.setParameter("tabs1", tabsA);
	portletURL.setParameter("view", "true");

	if (null != empresa) {
		portletURL.setParameter("cambioSolapa", "cambioSolapa");
	}	
	
	if(!portlet_name.equals("estudio_isidro")){
		tabsAValues.append(",datos-fiscales");
	}
	
	String tabsANames = StringUtil.replace(tabsAValues.toString(),
			StringPool.UNDERLINE, StringPool.DASH);
	
	String javaString="submitFormNotSavePOP('"+tabsA+"');";
	
	
%>
<form action="" method="post" name="<portlet:namespace />emple" id="<portlet:namespace />emple" >
<input name="<portlet:namespace /><%=Constants.CMD%>" type="hidden" value="" />

<%if(portlet_name.equals("liquidaciones")){ %>
	<liferay-ui:tabs		
		names="<%= tabsANames %>"
		tabsValues="<%= tabsAValues.toString() %>"		
		portletURL="<%= portletURL %>" onClick="<%=javaString%>" 
	/>
<%}else if(!portlet_name.equals("estudio_isidro")){ %>
	<liferay-ui:tabs		
		names="<%= tabsANames %>"
		tabsValues="<%= tabsAValues.toString() %>"		
		portletURL="<%= portletURL %>" onClick="submitFormNotSave();"
	/>
<%} %>
<%if(portlet_name.equals("liquidaciones")){ %>
	<liferay-util:include page="/html/portlet/empresas/view_empleador.jsp"/>
<%}else{ %>
<c:choose>
	<c:when test='<%= tabsA.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/empresas/view_empleador.jsp"/>
	</c:when>	
	
	<c:when test='<%= tabsA.equals("datos-fiscales")%>'>		
		<liferay-util:include page="/html/portlet/empresas/view_datos_fiscales.jsp"/>
	</c:when>	
	
</c:choose>
<%}%>

</form>
