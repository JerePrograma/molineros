<%@ include file="/html/portlet/compras/init.jsp" %>

<%
String idRequerimientoCompra =
        (String) renderRequest.getAttribute("ID_REQUERIMIENTO_COMPRA_PDF");

if (WebKeysCompras.isEmpty(idRequerimientoCompra)) {
    idRequerimientoCompra =
            ParamUtil.getString(renderRequest, "id_requerimiento_compra", "");
}
%>

<c:choose>
    <c:when test="<%= !WebKeysCompras.isEmpty(idRequerimientoCompra) %>">
        <script type="text/javascript">
            window.location.href =
                    "/pdfservlet/?accion=requerimientoCompra&id_requerimiento=<%= HtmlUtil.escapeURL(idRequerimientoCompra) %>";
        </script>
    </c:when>
    <c:otherwise>
        <div class="portlet-msg-error">
            No se pudo determinar el requerimiento de compra a imprimir.
        </div>
    </c:otherwise>
</c:choose>
