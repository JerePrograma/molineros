<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/_runtime_init.jsp" %>

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

<%@ include file="/html/portlet/compras/requerimientos/partials/_mensajes.jsp" %>
