<%@ include file="/html/portlet/compras/init.jsp" %>

<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

String tabs1 = ParamUtil.getString(request, "tabs1", null);

boolean mostrarConfiguracionCorreos =
        user != null
        && PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        );

if (tabs1 == null) {
    tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null) {
    tabs1 = (String) request.getSession().getAttribute("compras_tabs1");
}

String tabs1Values =
        "requerimientos,cotizados";

String tabs1Names =
        "Requerimientos,Cotizados";

if (mostrarConfiguracionCorreos) {
    tabs1Values += ",configuracion-de-correos";
    tabs1Names += ",Configuraci\u00f3n de Correos";
}

boolean tabValida =
        "requerimientos".equals(tabs1)
        || "cotizados".equals(tabs1)
        || (
                mostrarConfiguracionCorreos
                && "configuracion-de-correos".equals(tabs1)
        );

if (tabs1 == null || !tabValida) {
    tabs1 = "requerimientos";
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
    <c:when test='<%= "requerimientos".equals(tabs1) %>'>
        <liferay-util:include
                page="/html/portlet/compras/requerimientos/requerimiento_compra_listado.jsp" />
    </c:when>

    <c:when test='<%= "cotizados".equals(tabs1) %>'>
        <liferay-util:include
                page="/html/portlet/compras/requerimientos/requerimiento_compra_cotizados.jsp" />
    </c:when>

    <c:when test='<%= "configuracion-de-correos".equals(tabs1) %>'>
        <liferay-util:include
                page="/html/portlet/compras/requerimientos/requerimiento_compra_configuracion_correos.jsp" />
    </c:when>

    <c:otherwise>
        <liferay-util:include
                page="/html/portlet/compras/requerimientos/requerimiento_compra_listado.jsp" />
    </c:otherwise>
</c:choose>
