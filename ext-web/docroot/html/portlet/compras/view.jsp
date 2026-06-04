<%@ include file="/html/portlet/compras/init.jsp" %>

<portlet:defineObjects/>

<%
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

String tabs1 = ParamUtil.getString(request, "tabs1", null);

if (tabs1 == null) {
    tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null) {
    tabs1 = (String) request.getSession().getAttribute("compras_tabs1");
}

String tabs1Values = "requerimientos";
String tabs1Names = "Requerimientos";

if (tabs1 == null || !"requerimientos".equals(tabs1)) {
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

    <c:choose>
        <c:when test='<%= "requerimientos".equals(tabs1) %>'>
            <liferay-util:include page="/html/portlet/compras/requerimientos/requerimientos.jsp" />
        </c:when>

        <c:otherwise>
            <liferay-util:include page="/html/portlet/compras/requerimientos/requerimientos.jsp" />
        </c:otherwise>
    </c:choose>

</form>