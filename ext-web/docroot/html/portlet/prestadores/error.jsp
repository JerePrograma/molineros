<%
/**
 */
%>

<%@ include file="/html/portlet/prestadores/init.jsp" %>

<liferay-ui:tabs names="error" backURL="javascript: history.go(-1);" />

<liferay-ui:error exception="<%= SystemException.class %>" message="sistema-no-disponible" />
<liferay-ui:error exception="<%= PrincipalException.class %>" message="you-do-not-have-the-required-permissions" />
