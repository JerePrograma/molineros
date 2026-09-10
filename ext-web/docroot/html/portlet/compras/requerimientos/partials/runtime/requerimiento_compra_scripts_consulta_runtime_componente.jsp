<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%--
Responsabilidad:
    Prepara el contexto e incluye scripts de consulta en orden estable.
Incluido desde:
    requerimiento_compra_ensamblado.jsp, requerimiento_compra_ensamblado.jsp
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
boolean mostrarPanelAfiliadoEnVista = Boolean.TRUE.equals(request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_MOSTRAR_PANEL_AFILIADO));
String afiliadoCuilVisible = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_CUIL);
String afiliadoIntVisible = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_INT);
String afiliadoTipoDocumento = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_TIPO_DOCUMENTO);
String afiliadoNumeroDocumento = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO_DOCUMENTO);
String afiliadoApellido = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_APELLIDO);
String afiliadoNombre = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NOMBRE);
String afiliadoIdSeccional = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_ID_SECCIONAL);
String afiliadoSeccional = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_SECCIONAL);
String afiliadoBajaFecha = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_BAJA_FECHA);
String afiliadoFechaAlta = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_FECHA_ALTA);
String afiliadoIdTercerizadora = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_ID_TERCERIZADORA);
String afiliadoIncapacidad = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_INCAPACIDAD);
String afiliadoNombrePlan = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NOMBRE_PLAN);
String afiliadoIdPlan = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_ID_PLAN);
String afiliadoAfiTercerizadora = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_TERCERIZADORA);
String afiliadoNumeroAfiliado = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO);
String afiliadoNumeroOspim = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO_OSPIM);
String afiliadoNumeroUoma = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO_UOMA);
String afiliadoNumeroAmtima = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_NUMERO_AMTIMA);
String afiliadoAntecedentes = (String) request.getAttribute(WebKeysCompras.ATTR_REQUERIMIENTO_AFILIADO_ANTECEDENTES);
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_consulta_componente.jsp" %>
