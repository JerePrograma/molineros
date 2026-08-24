<%--
Responsabilidad:
    Punto de entrada de edición del requerimiento de compra.
Incluido desde:
    requerimiento_compra_edicion_compatibilidad_wrapper.jsp
Pantallas o estados de uso:
    PENDIENTE y ENVIADO A COTIZAR con capacidades diferenciadas.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    Ninguno.
Efectos secundarios:
    Sólo renderiza presentación; las operaciones se delegan al Action.
--%>
<%@ include file="/html/portlet/compras/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>



<%
request.setAttribute("MODO_REQUERIMIENTO_COMPRA", "EDICION");
%>

<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_modelo_vista_componente.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_contexto_publicacion_componente.jsp" %>
<%@ include file="/html/portlet/compras/requerimientos/partials/requerimiento_compra_edicion_ensamblado.jsp" %>
