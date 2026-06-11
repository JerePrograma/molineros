<%@ page import="java.util.List" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>
<%@ include file="/html/portlet/compras/init.jsp" %>

<%!
private String jsonArticuloCompra(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("<", "\\u003C")
            .replace(">", "\\u003E");
}
%>

<%
response.setContentType("application/json; charset=UTF-8");

List<CompraArticulo> articulos =
        (List<CompraArticulo>) renderRequest.getAttribute("ARTICULOS_COMPRA");

if (articulos == null) {
    articulos = new java.util.ArrayList<CompraArticulo>();
}
%>
{
    "articulos": [
        <%
        boolean primero = true;

        for (int i = 0; i < articulos.size(); i++) {
            CompraArticulo articulo = articulos.get(i);

            if (articulo == null
                    || articulo.getId() == null
                    || articulo.getDescripcion() == null
                    || articulo.getDescripcion().trim().length() == 0) {
                continue;
            }

            String idArticulo = String.valueOf(articulo.getId().intValue());
            String idSector = articulo.getIdSector() != null
                    ? String.valueOf(articulo.getIdSector().intValue())
                    : "";
            String descripcion = articulo.getDescripcion();

            if (!primero) {
        %>,<%
            }

            primero = false;
        %>
        {
            "id": "<%= jsonArticuloCompra(idArticulo) %>",
            "sector": "<%= jsonArticuloCompra(idSector) %>",
            "descripcion": "<%= jsonArticuloCompra(descripcion) %>"
        }
        <%
        }
        %>
    ]
}