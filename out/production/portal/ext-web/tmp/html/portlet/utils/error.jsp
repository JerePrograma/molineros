<%
/**
 */
%>

<%@ include file="/html/portlet/farmacia/init.jsp" %>

<liferay-ui:tabs names="error" backURL="javascript: history.go(-1);" />

<liferay-ui:error exception="<%= NoSuchReintegroEntryException.class %>" message="the-reintegro-could-not-be-found" />
<liferay-ui:error exception="<%= DuplicateReintegroIdException.class %>" message="the-reintegro-key-already-exists" />
<liferay-ui:error exception="<%= SystemException.class %>" message="sistema-no-disponible" />
<liferay-ui:error exception="<%= PrincipalException.class %>" message="you-do-not-have-the-required-permissions" />
