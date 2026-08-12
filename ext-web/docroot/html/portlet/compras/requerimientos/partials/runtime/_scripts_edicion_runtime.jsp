<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/_runtime_init.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/_runtime_js_helper.jsp" %>

<%--
Requiere sectores, estado de alta y los valores visibles del afiliado.
--%>
<%
boolean esNuevo = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.esNuevo"));
List<RequerimientoCompraSector> sectores = (List<RequerimientoCompraSector>) request.getAttribute("compras.requerimiento.sectores");
String afiliadoCuilVisible = (String) request.getAttribute("compras.requerimiento.afiliadoCuil");
String afiliadoIntVisible = (String) request.getAttribute("compras.requerimiento.afiliadoInt");
String afiliadoTipoDocumento = (String) request.getAttribute("compras.requerimiento.afiliadoTipoDocumento");
String afiliadoNumeroDocumento = (String) request.getAttribute("compras.requerimiento.afiliadoNumeroDocumento");
String afiliadoApellido = (String) request.getAttribute("compras.requerimiento.afiliadoApellido");
String afiliadoNombre = (String) request.getAttribute("compras.requerimiento.afiliadoNombre");
String afiliadoIdSeccional = (String) request.getAttribute("compras.requerimiento.afiliadoIdSeccional");
String afiliadoSeccional = (String) request.getAttribute("compras.requerimiento.afiliadoSeccional");
String afiliadoBajaFecha = (String) request.getAttribute("compras.requerimiento.afiliadoBajaFecha");
String afiliadoFechaAlta = (String) request.getAttribute("compras.requerimiento.afiliadoFechaAlta");
String afiliadoIdTercerizadora = (String) request.getAttribute("compras.requerimiento.afiliadoIdTercerizadora");
String afiliadoIncapacidad = (String) request.getAttribute("compras.requerimiento.afiliadoIncapacidad");
String afiliadoNombrePlan = (String) request.getAttribute("compras.requerimiento.afiliadoNombrePlan");
String afiliadoIdPlan = (String) request.getAttribute("compras.requerimiento.afiliadoIdPlan");
String afiliadoAfiTercerizadora = (String) request.getAttribute("compras.requerimiento.afiliadoTercerizadora");
String afiliadoNumeroAfiliado = (String) request.getAttribute("compras.requerimiento.afiliadoNumero");
String afiliadoNumeroOspim = (String) request.getAttribute("compras.requerimiento.afiliadoNumeroOspim");
String afiliadoNumeroUoma = (String) request.getAttribute("compras.requerimiento.afiliadoNumeroUoma");
String afiliadoNumeroAmtima = (String) request.getAttribute("compras.requerimiento.afiliadoNumeroAmtima");
String afiliadoAntecedentes = (String) request.getAttribute("compras.requerimiento.afiliadoAntecedentes");
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/_scripts_edicion.jsp" %>
