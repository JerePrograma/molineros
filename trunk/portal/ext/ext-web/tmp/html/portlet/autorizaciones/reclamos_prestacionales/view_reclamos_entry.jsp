
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%

String tabsA = ParamUtil.getString(request, "tabs1", "");
StringBuilder tabsAValues = new StringBuilder("datos");
tabsAValues.append(",lugar-at");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/autorizaciones/editar_reclamosprestaciones_entry");
portletURL.setParameter("tabs1", tabsA);
portletURL.setParameter("view", "true");

String tabsANames = StringUtil.replace(tabsAValues.toString(), StringPool.UNDERLINE, StringPool.DASH); 
%>

<liferay-ui-custom:tabs 
		names="<%= tabsANames %>" 
		tabsValues="<%= tabsAValues.toString() %>" 
		portletURL="<%= portletURL %>"  
		value="<%= tabsA%>"/> 

<c:choose>
	<c:when test='<%= tabsA.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/view_reclamo.jsp"/>
	</c:when>		
	<c:when test='<%= tabsA.equals("archivos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/reclamo_prestacional_imagen.jsp"/>
	</c:when>	
</c:choose>

<!-- </form> -->
