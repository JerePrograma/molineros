<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />
<%

String portlet_name = "estudio_isidro";

	

DemandaJudicial demanda=(DemandaJudicial)request.getSession().getAttribute(WebKeysEstudioIsidro.DEMANDA_EN_EDICION);

String cmd = (String) request.getAttribute(Constants.CMD);

Integer idPreautAux = demanda!=null && demanda.getId()!=null?demanda.getId():0;

String tabValue = ParamUtil.getString(request, "tab", null); // "datos"

if(tabValue == null || StringUtils.checkEmpty(tabValue)){
	tabValue = (String) request.getAttribute("tab");
}
if(tabValue == null || StringUtils.checkEmpty(tabValue)){
	tabValue = "datos";
}

String tabNames="Datos,datos-imagenes,Estados,Contabilidad" ;
String tabValues="datos,datos-imagenes,estados,contabilidad" ;

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/estudio_isidro/demandas_editar");
portletURL.setParameter("tabs1", tabValue);
portletURL.setParameter("cmd", cmd==null?"move":cmd);
portletURL.setParameter("demanda_id", String.valueOf(idPreautAux));
request.getSession().setAttribute("tab_seleccionada", tabValue);

%>

<liferay-ui-custom:tabs
	names="<%=tabNames%>"
	tabsValues="<%=tabValues%>"
	value="<%=tabValue%>" 
	url="<%= portletURL.toString() %>"
	param="moverATab" 
	/>	

<c:choose>
	<c:when test='<%= tabValue.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_edit.jsp"/>
		
	</c:when>	
	<c:when test='<%= tabValue.equals("datos-imagenes")%>'>		
		<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_imagenes.jsp"/>
	</c:when>
	<c:when test='<%= tabValue.equals("estados")%>'>		
		<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_estados.jsp"/>
	</c:when>
	
	<c:when test='<%= tabValue.equals("contabilidad")%>'>		
		<liferay-util:include page="/html/portlet/estudio_isidro/seguimiento_empresas/gestion_judicial_contabilidad.jsp"/>
	</c:when>
	
</c:choose>

