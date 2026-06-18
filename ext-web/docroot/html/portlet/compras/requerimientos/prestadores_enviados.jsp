<%@ page contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion" %>

<%!
private String jsonPrestadorCompra(String value) {
    if (value == null) {
        return "";
    }

    return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("\t", " ")
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

List<PrestadorCotizacion> prestadores =
        (List<PrestadorCotizacion>) request.getAttribute(
                WebKeysCompras.PRESTADORES_ENVIADOS_COTIZACION
        );

if (prestadores == null) {
    prestadores = new ArrayList<PrestadorCotizacion>();
}
%>{
    "prestadores": [
<%
boolean primero = true;

for (int i = 0; i < prestadores.size(); i++) {
    PrestadorCotizacion prestador = prestadores.get(i);

    if (prestador == null || prestador.getIdPrestador() <= 0) {
        continue;
    }

    if (!primero) {
%>,
<%
    }

    primero = false;
%>
        {
            "id": "<%= prestador.getIdPrestador() %>",
            "cuit": "<%= jsonPrestadorCompra(prestador.getCuitVisible()) %>",
            "razonSocial": "<%= jsonPrestadorCompra(prestador.getDescripcionVisible()) %>",
            "label": "<%= jsonPrestadorCompra(prestador.getEtiquetaVisible()) %>"
        }
<%
}
%>
    ]
}
