<%
/**
 */
%>

<%@ include file="/html/portlet/uoma/init.jsp" %>

<liferay-ui:tabs names="error" backURL="javascript: history.go(-1);" />

<liferay-ui:error exception="<%= ImposibleBorrarConvenioException.class %>" message="convenio-con-recibo-exception" />
