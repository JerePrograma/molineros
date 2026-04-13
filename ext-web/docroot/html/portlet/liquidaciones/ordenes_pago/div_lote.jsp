<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%int lote_actual=OrdenPagoServiceUtil.getLoteOrdenPago();%>
<liferay-ui:message key="lote-actual" />: <%=lote_actual%>