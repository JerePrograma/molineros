<%@ include file="/html/portlet/requerimientos_compras/init.jsp" %>

<%
String error = (String) renderRequest.getAttribute(WebKeysRequerimientosCompras.ERROR_PARA_ALERT);
%>

<div class="portlet-msg-error">
    Ocurrió un error en Requerimientos de Compras.
    <c:if test="<%= error != null && error.length() > 0 %>">
        <br /><%= error %>
    </c:if>
</div>
