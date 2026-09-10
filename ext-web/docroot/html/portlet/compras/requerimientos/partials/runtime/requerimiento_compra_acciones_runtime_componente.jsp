<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%--
Responsabilidad:
    Recupera atributos request e incluye la botonera en un contexto runtime aislado.
Incluido desde:
    requerimiento_compra_ensamblado.jsp, requerimiento_compra_ensamblado.jsp
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
RequerimientoCompra req = (RequerimientoCompra) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_MODELO);
boolean puedeABM = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_ABM));
boolean modoEditable = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_MODO_EDITABLE));
boolean puedeEditarEstructuraPantalla = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_EDITAR_ESTRUCTURA));
boolean puedeEditarCotizacionPantalla = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_EDITAR_COTIZACION));
PortletURL volverURL = (PortletURL) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_VOLVER_URL);
PortletURL imprimirURL = (PortletURL) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_IMPRIMIR_URL);
String namespaceCompra = renderResponse.getNamespace();
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_acciones_componente.jsp" %>
