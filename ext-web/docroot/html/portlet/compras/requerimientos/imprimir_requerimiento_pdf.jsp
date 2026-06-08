<%@ include file="/html/portlet/compras/init.jsp" %>

<%
String idRequerimientoCompra =
        (String) renderRequest.getAttribute("ID_REQUERIMIENTO_COMPRA_PDF");

if (WebKeysCompras.isEmpty(idRequerimientoCompra)) {
    idRequerimientoCompra =
            ParamUtil.getString(renderRequest, "id_requerimiento_compra", "");
}

if (idRequerimientoCompra != null) {
    idRequerimientoCompra = idRequerimientoCompra.trim();
}

boolean idValido =
        !WebKeysCompras.isEmpty(idRequerimientoCompra)
        && idRequerimientoCompra.matches("^[0-9]+$");
%>

<c:choose>
    <c:when test="<%= idValido %>">
        <script type="text/javascript">
            window.location.href =
                    "/pdfservlet/?accion=requerimientoCompra&id_requerimiento=<%= idRequerimientoCompra %>";
        </script>
    </c:when>
    <c:otherwise>
        <div class="portlet-msg-error">
            No se pudo determinar el requerimiento de compra a imprimir.
        </div>
    </c:otherwise>
</c:choose>