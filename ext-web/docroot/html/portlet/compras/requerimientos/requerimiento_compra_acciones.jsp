<%--
Responsabilidad:
    Renderiza las acciones de fila del listado sin la acción Ver redundante.
Incluido desde:
    requerimiento_compra_busqueda_resultado.jsp mediante row.addJSP.
Pantallas o estados de uso:
    Búsqueda, selección o popup según el forward indicado.
Entradas requeridas:
    Atributos preparados por el Action asociado al forward.
Atributos de request consumidos:
    Los atributos enumerados en el scriptlet inicial del archivo.
Parámetros consumidos:
    Sólo parámetros de render ya validados por el Action; no persiste datos.
IDs o funciones JavaScript expuestos:
    id_requerimiento_compra, estado_nuevo, motivo_baja, solicitarMotivoBaja
Efectos secundarios:
    Sólo renderiza presentación; las operaciones se delegan al Action.
--%>
<%@ page pageEncoding="ISO-8859-1" %>
<%@ include file="/html/portlet/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.compras.WebKeysCompras" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra" %>
<%@ page import="ar.com.ospim.util.PermissionUtil" %>
<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="java.util.Map" %>
<%@ page import="ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional" %>
 
<% 
ResultRow row = 
        (ResultRow) request.getAttribute( 
                WebKeys.SEARCH_CONTAINER_RESULT_ROW 
        ); 
 
RequerimientoCompra req = 
        (RequerimientoCompra) row.getObject(); 

Map<Integer, RequerimientoCompraReclamoPrestacional> relacionesRp =
        (Map<Integer, RequerimientoCompraReclamoPrestacional>)
                renderRequest.getAttribute(
                        WebKeysCompras.RELACIONES_RECLAMO_PRESTACIONAL_COMPRA
                );

RequerimientoCompraReclamoPrestacional relacionRp =
        relacionesRp != null
                ? relacionesRp.get(Integer.valueOf(req.getIdRequerimientoCompra()))
                : null;

boolean puedeAnularRequerimiento = req.puedeAnular(relacionRp);
 
boolean showABMButtons = 
        user != null 
        && PermissionUtil.userContainsRole( 
                user, 
                WebKeysCompras.ROL_ABM_COMPRAS 
        ); 
 
boolean showCotizarButtons = 
        user != null 
        && PermissionUtil.userContainsRole( 
                user, 
                WebKeysCompras.ROL_COTIZAR_COMPRAS 
        ); 
 
boolean showAutorizarButton =
        req.isActivo()
        && WebKeysCompras.esParaAutorizar(req.getEstado())
        && user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_AUTORIZAR_COMPRAS
        );

boolean showAnularButtons = 
        user != null 
        && PermissionUtil.userContainsRole( 
                user, 
                WebKeysCompras.ROL_ABM_COMPRAS 
        ); 
 
boolean showEditarButton =
        (showABMButtons
                && (
                        req.puedeEditarEstructura()
                        || req.puedeEditarSurge()
                        || req.puedeEliminarDetalle()
                ))
        || (showCotizarButtons
                && (req.puedeEditarCotizacion()
                        || (req.esSectorSinCotizacionPrestador()
                                && req.isActivo()
                                && req.isOrdenCompra())));
 
String idRequerimiento = 
        req.getIdRequerimientoCompraString(); 
 
String idRequerimientoForm = 
        req.getIdString(); 
 
String ns = 
        renderResponse.getNamespace(); 
 
String anularFormId = 
        ns + "anular_" + idRequerimientoForm;

String motivoBajaInputId =
        ns + "motivo_baja_" + idRequerimientoForm;
 
String anularURL = 
        "javascript:" + ns + "solicitarMotivoBaja('" 
        + anularFormId 
        + "','"
        + motivoBajaInputId
        + "');"; 
%> 
 
<portlet:renderURL 
        windowState="<%= WindowState.MAXIMIZED.toString() %>" 
        var="editarURL"> 
 
    <portlet:param 
            name="struts_action" 
            value="/compras/editar_requerimiento" /> 
 
    <portlet:param 
            name="id_requerimiento_compra" 
            value="<%= idRequerimiento %>" /> 
</portlet:renderURL> 
 
<portlet:renderURL
        windowState="<%= WindowState.MAXIMIZED.toString() %>"
        var="autorizarURL">
    <portlet:param name="struts_action" value="/compras/ver_requerimiento" />
    <portlet:param name="id_requerimiento_compra" value="<%= idRequerimiento %>" />
</portlet:renderURL>

<portlet:actionURL 
        windowState="<%= WindowState.MAXIMIZED.toString() %>" 
        var="cambiarEstadoURL"> 
 
    <portlet:param 
            name="struts_action" 
            value="/compras/cambiar_estado_requerimiento" /> 
</portlet:actionURL> 
 
<c:if test="<%= showAnularButtons && puedeAnularRequerimiento %>">
    <form action="<%= cambiarEstadoURL %>" 
          method="post" 
          id="<%= anularFormId %>" 
          style="display:none;"> 
 
        <input type="hidden" 
               name="<portlet:namespace />id_requerimiento_compra" 
               value="<%= idRequerimientoForm %>" /> 
 
        <input type="hidden" 
               name="<portlet:namespace />estado_nuevo" 
               value="<%= WebKeysCompras.ESTADO_ANULADO %>" />

        <input type="hidden"
               id="<%= motivoBajaInputId %>"
               name="<portlet:namespace />motivo_baja"
               value="" /> 
    </form> 
</c:if> 
 
<liferay-ui:icon-menu>

    <c:if test="<%= showAutorizarButton %>">
        <liferay-ui:icon
                image="submit"
                message="Revisar para autorizar"
                url="<%= autorizarURL %>" />
    </c:if>

<c:if test="<%= showEditarButton %>"> 
        <liferay-ui:icon 
                image="edit" 
                message="Editar" 
                url="<%= editarURL %>" /> 
    </c:if> 
 
    <c:if test="<%= showAnularButtons && puedeAnularRequerimiento %>">
        <liferay-ui:icon 
                image="delete" 
                message="Eliminar" 
                url="<%= anularURL %>" /> 
    </c:if> 
 
</liferay-ui:icon-menu>

<script type="text/javascript">
    function <portlet:namespace />solicitarMotivoBaja(
            formId,
            motivoInputId) {

        var motivo = window.prompt(
                'Ingrese el motivo de baja (opcional). '
                + 'Presione Aceptar para confirmar.',
                ''
        );

        /*
         * Cancelar aborta la operacion.
         * Aceptar con texto vacio es valido.
         */
        if (motivo === null) {
            return false;
        }

        var form = document.getElementById(formId);
        var motivoInput = document.getElementById(motivoInputId);

        if (!form || !motivoInput) {
            alert('No se pudo preparar la baja del requerimiento.');
            return false;
        }

        motivoInput.value = motivo;
        submitForm(form);

        return false;
    }
</script>
