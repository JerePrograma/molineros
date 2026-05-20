<%@ include file="/html/portlet/compras/init.jsp" %>

<%
boolean puedeVer = PermissionUtil.userContainsRole(user, WebKeysRequerimientosCompras.ROL_VIEW_COMPRAS)
        || PermissionUtil.userContainsRole(user, WebKeysRequerimientosCompras.ROL_ABM_COMPRAS)
        || PermissionUtil.userContainsRole(user, WebKeysRequerimientosCompras.ROL_APROBAR_COMPRAS);

boolean puedeABM = PermissionUtil.userContainsRole(user, WebKeysRequerimientosCompras.ROL_ABM_COMPRAS);

String tabs1 = ParamUtil.getString(request, "tabs1", "requerimientos");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/compras/view");
portletURL.setParameter("tabs1", tabs1);
%>

<c:if test="<%= !puedeVer %>">
    <div class="portlet-msg-error">No posee permisos para visualizar compras.</div>
</c:if>

<c:if test="<%= puedeVer %>">
    <form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm">
        <liferay-portlet:renderURLParams varImpl="portletURL" />

        <liferay-ui-custom:tabs
            names="requerimientos"
            tabsValues="requerimientos"
            portletURL="<%= portletURL %>"
            value="<%= tabs1 %>"
        />

        <c:choose>
            <c:when test='<%= "requerimientos".equals(tabs1) %>'>
                <liferay-util:include page="/html/portlet/compras/busqueda_requerimientos.jsp" />
            </c:when>
            <c:otherwise>
                <liferay-util:include page="/html/portlet/compras/busqueda_requerimientos.jsp" />
            </c:otherwise>
        </c:choose>
    </form>
</c:if>
