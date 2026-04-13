<%@ include file="/html/portlet/cgt_ddhh/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);


boolean showOrganismosDDHH = true;
boolean showBoletinDDHH = true;

if (tabs1 == null){
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null){
	tabs1 = (String) request.getSession().getAttribute("tabs1"); 
}


StringBuffer tabs1ValuesBuffer = new StringBuffer("");
if(showOrganismosDDHH){
 tabs1ValuesBuffer.append("organismos-ddhh");
 tabs1ValuesBuffer.append(",normas-ddhh");
}
if(showBoletinDDHH){
 tabs1ValuesBuffer.append(",lista-correo");
 tabs1ValuesBuffer.append(",destinatarios");
 tabs1ValuesBuffer.append(",boletin"); 
}

String tabs1Values=tabs1ValuesBuffer.toString();

if (tabs1 == null || (tabs1 != null && tabs1Values.indexOf(tabs1) < 0)){	
		tabs1="organismos-ddhh";	
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(WindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/cgt_ddhh/view");
portletURL.setParameter("tabs1", tabs1);
currentURL = PortalUtil.getCurrentURL(request);
%>


<%@page import="com.liferay.portal.kernel.util.Constants"%>
<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm">
<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

<liferay-portlet:renderURLParams varImpl="portletURL" />

<liferay-ui-custom:tabs
	names="<%= tabs1Names %>"
	tabsValues="<%= tabs1Values %>"
	portletURL="<%= portletURL %>"
/>
<!-- REPRESENTACIÓN DE LOS TABS DE AFILIACIONES -->
<c:choose>
	<c:when test='<%= tabs1.equals("organismos-ddhh") %>'>
		<liferay-util:include page="/html/portlet/cgt_ddhh/organismos/busqueda_organismos.jsp">			
		</liferay-util:include>
	</c:when>	
	<c:when test='<%= tabs1.equals("normas-ddhh") %>'>
		<liferay-util:include page="/html/portlet/cgt_ddhh/normasDDHH/busqueda_normas_ddhh.jsp">			
		</liferay-util:include>
	</c:when>	
	<c:when test='<%= tabs1.equals("lista-correo") %>'>
		<liferay-util:include page="/html/portlet/utils/mailing/busqueda_mailing.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("destinatarios") %>'>
		<liferay-util:include page="/html/portlet/utils/mailing/busqueda_subscriber.jsp">			
		</liferay-util:include>
	</c:when>
	<c:when test='<%= tabs1.equals("boletin") %>'>
		<liferay-util:include page="/html/portlet/utils/mailing/busqueda_boletin.jsp">			
		</liferay-util:include>
	</c:when>
</c:choose>

</form>
