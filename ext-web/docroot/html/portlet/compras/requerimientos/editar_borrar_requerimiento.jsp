<%@ include file="/html/portlet/compras/init.jsp" %>

<%
ResultRow row =
        (ResultRow) request.getAttribute(
                WebKeys.SEARCH_CONTAINER_RESULT_ROW
        );

RequerimientoCompra req =
        (RequerimientoCompra) row.getObject();

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

boolean showAnularButtons =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        );

boolean showEditarButton =
        (showABMButtons
                && req.puedeEditarEstructura())
        || (showCotizarButtons
                && req.puedeEditarCotizacion());

String idRequerimiento =
        req.getIdRequerimientoCompraString();

String idRequerimientoForm =
        req.getIdString();

String ns =
        renderResponse.getNamespace();

String anularFormId =
        ns + "anular_" + idRequerimientoForm;

String anularURL =
        "javascript:if(confirm('¿Confirma eliminar el requerimiento?')) "
        + "submitForm(document.getElementById('"
        + anularFormId
        + "'));";
%>

<portlet:renderURL
        windowState="<%= WindowState.MAXIMIZED.toString() %>"
        var="verURL">

    <portlet:param
            name="struts_action"
            value="/compras/ver_requerimiento" />

    <portlet:param
            name="id_requerimiento_compra"
            value="<%= idRequerimiento %>" />
</portlet:renderURL>

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

<portlet:actionURL
        windowState="<%= WindowState.MAXIMIZED.toString() %>"
        var="cambiarEstadoURL">

    <portlet:param
            name="struts_action"
            value="/compras/cambiar_estado_requerimiento" />
</portlet:actionURL>

<c:if test="<%= showAnularButtons && req.puedeAnular() %>">
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
    </form>
</c:if>

<liferay-ui:icon-menu>

    <liferay-ui:icon
            image="../common/view"
            message="Ver"
            url="<%= verURL %>" />

    <c:if test="<%= showEditarButton %>">
        <liferay-ui:icon
                image="edit"
                message="Editar"
                url="<%= editarURL %>" />
    </c:if>

    <c:if test="<%= showAnularButtons && req.puedeAnular() %>">
        <liferay-ui:icon
                image="delete"
                message="Eliminar"
                url="<%= anularURL %>" />
    </c:if>

</liferay-ui:icon-menu>