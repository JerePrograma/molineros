<%@ page contentType="application/json; charset=UTF-8" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ar.com.ospim.compras.beans.CompraArticulo" %>

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
            .replace("\t", " ")
            .replace("\b", " ")
            .replace("\f", " ")
            .replace("<", "\\u003C")
            .replace(">", "\\u003E");
}
%>

<%
response.setContentType("application/json; charset=UTF-8");
response.setCharacterEncoding("UTF-8");
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0L);

List<CompraArticulo> articulos =
        (List<CompraArticulo>) request.getAttribute("ARTICULOS_COMPRA");

if (articulos == null) {
    articulos = new ArrayList<CompraArticulo>();
}
%>{
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
%>,
<%
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