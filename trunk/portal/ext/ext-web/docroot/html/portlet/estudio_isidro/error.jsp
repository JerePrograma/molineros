<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<liferay-ui:tabs names="error" backURL="javascript: history.go(-1);" />

<liferay-ui:error exception="<%= ImposibleBorrarActaException.class %>" message="imposible-borrar-acta" />
<liferay-ui:error exception="<%= LiquidarActaConvenioException.class %>" message="error-liquidar-acta-convenio" />

