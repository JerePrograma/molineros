<%@ include file="/html/portlet/compras/init.jsp" %>

<%
String error = (String) renderRequest.getAttribute(WebKeysCompras.ERROR_PARA_ALERT);
%>

<div class="portlet-msg-error">
    Ocurrió un error en Compras.
    <c:if test="<%= error != null && error.length() > 0 %>">
        <br /><%= HtmlUtil.escape(error) %>
    </c:if>
</div>