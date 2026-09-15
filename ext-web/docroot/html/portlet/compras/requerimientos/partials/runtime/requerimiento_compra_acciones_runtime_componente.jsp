<%--
Responsabilidad:
    Recupera atributos request e incluye la botonera en un contexto runtime aislado.
Incluido desde:
    requerimiento_compra_consulta_ensamblado.jsp, requerimiento_compra_edicion_ensamblado.jsp
Pantallas o estados de uso:
    Alta, edición o consulta según el caller indicado.
Entradas requeridas:
    Atributos request publicados por requerimiento_compra_contexto_publicacion_componente.jsp.
Atributos de request consumidos:
    Claves compras.requerimiento.* leídas por requerimiento_compra_runtime_inicializacion_componente.jsp.
Parámetros consumidos:
    Ninguno directamente; sólo renderiza names y valores del contrato legacy cuando corresponde.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza o incluye presentación; no ejecuta persistencia.
--%>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_inicializacion_componente.jsp" %>

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

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_acciones_componente.jsp" %>
