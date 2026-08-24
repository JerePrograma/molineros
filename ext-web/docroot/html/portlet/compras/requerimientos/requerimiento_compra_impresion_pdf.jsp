<%--
Responsabilidad:
    Adapta la navegación legacy hacia la impresión PDF del requerimiento.
Incluido desde:
    Forward, Action o entry point directo en: tiles-defs.xml.
Pantallas o estados de uso:
    Consulta y estados de solo lectura.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo modifica el DOM o comunica la selección al callback namespaced.
--%>
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