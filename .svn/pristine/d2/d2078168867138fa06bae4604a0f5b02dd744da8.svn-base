<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<portlet:defineObjects />
<liferay-theme:defineObjects />
<%
String tabNames="" ;
String tabValues="" ;
String cmd = (String) request.getAttribute(Constants.CMD);


 
int idTratamiento = ParamUtil.getInteger(request,
		"id_tratamiento", 0);
if(idTratamiento==0){
  try{	
    idTratamiento= (Integer) request.getSession().getAttribute("id_tratamiento");
  }catch(Exception e){
	idTratamiento=0;  
  }
}

String tabValue = ParamUtil.getString(request, "tab", null);
 
String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "autorizaciones";
}

if(tabValue == null || StringUtils.checkEmpty(tabValue)){	
	tabValue = (String) request.getAttribute("tab");
}
if(tabValue == null || StringUtils.checkEmpty(tabValue)){
	tabValue = "datos";
}


	tabNames="Datos Generales" ;
	 tabValues="archivos" ;
	 
	 if (idTratamiento != 0){
		 tabNames="Datos Generales,Imágenes,Historial" ;
		 tabValues="datos,archivos,historial" ;
	 }

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/autorizaciones/editar_autorizacionprestacional_entry");
portletURL.setParameter("tab", tabValue);
portletURL.setParameter("id_tratamiento", String.valueOf(idTratamiento));

%>

<liferay-ui-custom:tabs
	names="<%=tabNames%>"
	tabsValues="<%=tabValues%>"
	value="<%=tabValue%>" 
	url="<%= portletURL.toString() %>"
	param="tab" />
		
<c:choose>
	<c:when test='<%= tabValue.equals("datos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/autorizaciones_prestacionales/view_autorizacion_prestacional.jsp"/>
	</c:when>
	<c:when test='<%= tabValue.equals("archivos") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/autorizaciones_prestacionales/view_autorizacion_prestacional_imagen.jsp"/>
	</c:when>	
	<c:when test='<%= tabValue.equals("historial") %>'>
		<liferay-util:include page="/html/portlet/autorizaciones/autorizaciones_prestacionales/historial_autorizacion_prestacional.jsp"/>
	</c:when>	
</c:choose>


	
