<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%--
Responsabilidad:
    Recupera atributos request e incluye mensajes en contexto runtime.
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
Requiere atributos compras.requerimiento.* de mensajes, permisos y errores.
--%>
<%
boolean soloLecturaSolicitada = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_SOLO_LECTURA_SOLICITADA));
boolean puedeABM = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_ABM));
boolean puedeCotizar = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_PUEDE_COTIZAR));
String errorParaAlert = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_ERROR_ALERT);
String errorCampoCompra = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_ERROR_CAMPO);
String comprasOperacion = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_OPERACION);
boolean mostrarMensajeRequerimientoGuardado = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_MOSTRAR_MENSAJE_GUARDADO));
boolean mostrarErrorGenericoCompra = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_MOSTRAR_ERROR_GENERICO));
String idRequerimientoMensaje = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_ID_MENSAJE);
boolean msgDetalleGuardado = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_MSG_DETALLE_GUARDADO));
boolean msgDetalleBorrado = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_MSG_DETALLE_BORRADO));
boolean msgRequerimientoAnulado = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_MSG_ANULADO));
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_mensajes_componente.jsp" %>
