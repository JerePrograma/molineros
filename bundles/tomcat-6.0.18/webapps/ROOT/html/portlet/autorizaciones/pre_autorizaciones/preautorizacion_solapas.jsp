<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />
<%

String portlet_name = "autorizaciones";

	

PreAutorizacion preautorizacion=(PreAutorizacion)request.getSession().getAttribute(WebKeysAutorizaciones.PREAUTORIZACION_EN_EDICION);

String cmd = (String) request.getAttribute(Constants.CMD);

Integer idPreautAux = preautorizacion!=null && preautorizacion.getId()!=null?preautorizacion.getId():0;

String tabValue = ParamUtil.getString(request, "tab", null); // "datos"

if(tabValue == null || StringUtils.checkEmpty(tabValue)){
	tabValue = (String) request.getAttribute("tab");
}
if(tabValue == null || StringUtils.checkEmpty(tabValue)){
	tabValue = "datos";
}

String tabNames="Datos,datos-imagenes" ;
String tabValues="datos,datos-imagenes" ;

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/autorizaciones/preautorizacion_editar");
portletURL.setParameter("tabs1", tabValue);
//portletURL.setParameter("cmd", cmd);
portletURL.setParameter("preautorizacion_id", String.valueOf(idPreautAux));
request.getSession().setAttribute("tab_seleccionada", tabValue);

%>


<liferay-ui-custom:tabs
	names="<%=tabNames%>"
	tabsValues="<%=tabValues%>"
	value="<%=tabValue%>" 
	url="<%= portletURL.toString() %>"
	param="tab" 
	onClick='return false;' 
	/>	

<c:choose>
	<c:when test='<%= tabValue.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/pre_autorizaciones/preautorizacion_edit.jsp"/>
		
	</c:when>	
	<c:when test='<%= tabValue.equals("datos-imagenes")%>'>		
		<liferay-util:include page="/html/portlet/autorizaciones/pre_autorizaciones/preautorizacion_imagenes.jsp"/>
	</c:when>
</c:choose>

