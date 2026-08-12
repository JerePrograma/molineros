<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/_runtime_init.jsp" %>

<%--
Requiere el requerimiento, permisos de pantalla y las URLs de volver e imprimir.
--%>
<%
RequerimientoCompra req = (RequerimientoCompra) request.getAttribute("compras.requerimiento.req");
boolean puedeABM = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.puedeABM"));
boolean modoEditable = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.modoEditable"));
boolean puedeEditarEstructuraPantalla = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.puedeEditarEstructura"));
boolean puedeEditarCotizacionPantalla = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.puedeEditarCotizacion"));
PortletURL volverURL = (PortletURL) request.getAttribute("compras.requerimiento.volverURL");
PortletURL imprimirURL = (PortletURL) request.getAttribute("compras.requerimiento.imprimirURL");
String namespaceCompra = renderResponse.getNamespace();
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/_botonera.jsp" %>
