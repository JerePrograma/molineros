<%--
Responsabilidad:
    Prepara el contexto e incluye scripts de consulta en orden estable.
Incluido desde:
    requerimiento_compra_consulta_ensamblado.jsp, requerimiento_compra_edicion_ensamblado.jsp
Pantallas o estados de uso:
    Consulta y estados de solo lectura.
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
<%@ include file="/html/portlet/compras/requerimientos/partials/runtime/requerimiento_compra_runtime_javascript_helper_componente.jsp" %>

<%-- Requiere visibilidad y valores visibles del afiliado. --%>
<%
boolean mostrarPanelAfiliadoEnVista = Boolean.TRUE.equals(request.getAttribute("compras.requerimiento.mostrarPanelAfiliado"));
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

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_consulta_componente.jsp" %>
