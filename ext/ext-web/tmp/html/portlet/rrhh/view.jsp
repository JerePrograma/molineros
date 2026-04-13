<%@ include file="/html/portlet/rrhh/init.jsp" %>

<%
boolean showTarjetas  =PermissionUtil.userContainsRole(user,WebKeysGlobal.ROL_ABM_RRHH);


String tabs1 = ParamUtil.getString(request, "tabs1", null);

if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}

StringBuffer tabs1ValuesBuffer = new StringBuffer("");
tabs1ValuesBuffer.append("carga-de-archivos,lecturas-de-acceso,informacion-de-personas");

if (showTarjetas) {
	tabs1ValuesBuffer.append(",tarjetas-de-personas");
}

String tabs1Values=tabs1ValuesBuffer.toString();

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
		tabs1="carga-de-archivos";	
}


String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/rrhh/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>



<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" enctype="multipart/form-data">
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
/>
<!-- REPRESENTACIÓN DE LOS TABS DE RRHH -->
<c:choose>
	<c:when test='<%= tabs1.equals("carga-de-archivos") %>'>
		<liferay-util:include page="/html/portlet/rrhh/carga_registros_rrhh.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("lecturas-de-acceso") %>'>
		<liferay-util:include page="/html/portlet/rrhh/lecturas_de_acceso.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("informacion-de-personas") %>'>
		<liferay-util:include page="/html/portlet/rrhh/informacion_de_personas.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("tarjetas-de-personas") %>'>
		<liferay-util:include page="/html/portlet/rrhh/busqueda_tarjetas.jsp">			
		</liferay-util:include>
	</c:when>
</c:choose>
</form>
