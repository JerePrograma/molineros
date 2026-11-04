<%@ include file="/html/portlet/compras/init.jsp" %>

<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

String tabs1 = ParamUtil.getString(request, "tabs1", null);

boolean mostrarRequerimientos =
        ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper.puedeConsultar(user);
boolean mostrarOrdenesCompra =
        ar.com.ospim.compras.requerimientos.action.RequerimientoCompraOrdenAction.puedeConsultar(user);

boolean mostrarParaAutorizar =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getRolAutorizarCompras()
        );

boolean mostrarConfiguracionCorreos =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getRolCotizarCompras()
        );

if (tabs1 == null) {
    tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null) {
    tabs1 = (String) request.getSession().getAttribute("compras_tabs1");
}

String tabs1Values = mostrarRequerimientos ? "requerimientos" : "";
String tabs1Names = mostrarRequerimientos ? "Requerimientos" : "";

if (mostrarParaAutorizar) {
    tabs1Values += (tabs1Values.length() > 0 ? "," : "") + "para-autorizar";
    tabs1Names += (tabs1Names.length() > 0 ? "," : "") + "Para Autorizar";
}

if (mostrarOrdenesCompra) {
    tabs1Values += (tabs1Values.length() > 0 ? "," : "") + "orden-de-compra";
    tabs1Names += (tabs1Names.length() > 0 ? "," : "") + "Orden de Compra";
}

if (mostrarConfiguracionCorreos) {
    tabs1Values += (tabs1Values.length() > 0 ? "," : "") + "configuracion-de-correos";
    tabs1Names += (tabs1Names.length() > 0 ? "," : "") + "Configuraci\u00f3n de Correos";
}

boolean tabValida =
        (mostrarRequerimientos && "requerimientos".equals(tabs1))
        || (mostrarOrdenesCompra && "orden-de-compra".equals(tabs1))
        || (mostrarParaAutorizar && "para-autorizar".equals(tabs1))
        || (
                mostrarConfiguracionCorreos
                && "configuracion-de-correos".equals(tabs1)
        );

if (tabs1 == null || !tabValida) {
    tabs1 = mostrarRequerimientos ? "requerimientos"
            : (mostrarOrdenesCompra ? "orden-de-compra" : "");
}

request.getSession().setAttribute("compras_tabs1", tabs1);

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/compras/view");
portletURL.setParameter("tabs1", tabs1);

currentURL = PortalUtil.getCurrentURL(request);
%>
<form action="<%= portletURL %>"
      method="get"
      name="<portlet:namespace />fm"
      onSubmit="submitForm(this); return false;">

    <liferay-portlet:renderURLParams varImpl="portletURL" />

    <liferay-ui-custom:tabs
            names="<%= tabs1Names %>"
            tabsValues="<%= tabs1Values %>"
            portletURL="<%= portletURL %>"
            value="<%= tabs1 %>"
    />
</form>

<c:choose>
    <c:when test='<%= mostrarRequerimientos && "requerimientos".equals(tabs1) %>'>
        <liferay-util:include
                page="/html/portlet/compras/requerimientos/requerimiento_compra_listado.jsp" />
    </c:when>

    <c:when test='<%= mostrarOrdenesCompra && "orden-de-compra".equals(tabs1) %>'>
        <liferay-util:include
                page="/html/portlet/compras/requerimientos/requerimiento_compra_ordenes.jsp" />
    </c:when>

    <c:when test='<%= mostrarParaAutorizar && "para-autorizar".equals(tabs1) %>'>
        <liferay-util:include
                page="/html/portlet/compras/requerimientos/requerimiento_compra_para_autorizar.jsp" />
    </c:when>

    <c:when test='<%= "configuracion-de-correos".equals(tabs1) %>'>
        <liferay-util:include
                page="/html/portlet/compras/requerimientos/requerimiento_compra_configuracion_correos.jsp" />
    </c:when>

    <c:otherwise>
        <div class="portlet-msg-error">No posee permisos para consultar Compras.</div>
    </c:otherwise>
</c:choose>

<jsp:include page="/html/portlet/compras/requerimientos/partials/requerimiento_compra_scripts_envio_componente.jsp" />
