<%--
Responsabilidad:
    Recupera atributos request e incluye mensajes en contexto runtime.
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
Requiere atributos compras.requerimiento.* de mensajes, permisos y errores.
--%>
<%
boolean soloLecturaSolicitada = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.soloLecturaSolicitada"));
boolean puedeABM = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.puedeABM"));
boolean puedeCotizar = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.puedeCotizar"));
String errorParaAlert = (String) request.getAttribute("compras.requerimiento.errorParaAlert");
String errorCampoCompra = (String) request.getAttribute("compras.requerimiento.errorCampo");
String comprasOperacion = (String) request.getAttribute("compras.requerimiento.operacion");
boolean mostrarMensajeRequerimientoGuardado = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.mostrarMensajeGuardado"));
boolean mostrarErrorGenericoCompra = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.mostrarErrorGenerico"));
String idRequerimientoMensaje = (String) request.getAttribute("compras.requerimiento.idMensaje");
boolean msgDetalleGuardado = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.msgDetalleGuardado"));
boolean msgDetalleBorrado = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.msgDetalleBorrado"));
boolean msgRequerimientoAnulado = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.msgAnulado"));
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_mensajes_componente.jsp" %>
