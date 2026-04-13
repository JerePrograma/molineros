<%@ include file="/html/portlet/farmacia_ospim/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);

if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}

StringBuffer tabs1ValuesBuffer = new StringBuffer("");
tabs1ValuesBuffer.append("subir-archivo,medicacion-ospim,farmacia-ospim,subir-archivo-vademecum,vademecum");

String tabs1Values=tabs1ValuesBuffer.toString();


if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
		tabs1="subir-archivo";	
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/farmaciaospim/view");
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

<!-- REPRESENTACIÓN DE LOS TABS DE FARMACIA  -->
<c:choose>
	<c:when test='<%= tabs1.equals("subir-archivo") %>'>
		<liferay-util:include page="/html/portlet/farmacia_ospim/upload_archivos_farm_ospim.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("medicacion-ospim") %>'>
		<liferay-util:include page="/html/portlet/farmacia_ospim/medicamentos/busqueda_medicamentos.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("farmacia-ospim") %>'>
		<liferay-util:include page="/html/portlet/farmacia_ospim/busqueda_farmacia.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("subir-archivo-vademecum") %>'>	
		<liferay-util:include page="/html/portlet/farmacia_ospim/upload_archivos_farmacia.jsp">
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("vademecum") %>'>
		<liferay-util:include page="/html/portlet/farmacia_ospim/medicamentos/busqueda_vademecum.jsp">			
		</liferay-util:include>
	</c:when>
	
	
</c:choose>
</form>





