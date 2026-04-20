<%@ include file="/html/portlet/prestadores/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />

<%
String tabs1 = ParamUtil.getString(request, "tabs1", null);

if (tabs1 == null) {
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null) {
	tabs1 = (String) request.getSession().getAttribute("tabs1");
}

boolean showAumentoNomenclador = PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_AUMENTO_NOMENCLADOR);

StringBuffer tabs1ValuesBuffer = new StringBuffer("prestadores,convenios-prestacionales,cartilla-convenios-prestadores");
StringBuffer tabs1NamesBuffer = new StringBuffer("Prestadores,Convenios Prestacionales,Cartilla de Convenios de Prestadores");

if (showAumentoNomenclador) {
	tabs1ValuesBuffer.append(",aumento-prestaciones");
	tabs1NamesBuffer.append(",Aumento de Prestaciones");
}

String tabs1Values = tabs1ValuesBuffer.toString();
String tabs1Names = tabs1NamesBuffer.toString();

String[] vTab = tabs1Values.split(",");
boolean tabValida = false;

if (tabs1 != null) {
	for (int i = 0; i < vTab.length; i++) {
		if (vTab[i].equals(tabs1)) {
			tabValida = true;
			break;
		}
	}
}

if (tabs1 == null || !tabValida) {
	tabs1 = vTab[0];
}

String subtitle = "Prestadores";

if ("convenios-prestacionales".equals(tabs1)) {
	subtitle = "Convenios Prestacionales";
} else if ("cartilla-convenios-prestadores".equals(tabs1)) {
	subtitle = "Cartilla de Convenios de Prestadores";
} else if ("aumento-prestaciones".equals(tabs1)) {
	subtitle = "Aumento de Prestaciones";
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/prestadores/view");
portletURL.setParameter("tabs1", tabs1);

currentURL = PortalUtil.getCurrentURL(request);
%>

<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm">
	<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="" />

	<liferay-portlet:renderURLParams varImpl="portletURL" />

	<liferay-ui-custom:tabs
		names="<%= tabs1Names %>"
		tabsValues="<%= tabs1Values %>"
		portletURL="<%= portletURL %>"
		value="<%= tabs1 %>"
	/>

	<c:choose>
		<c:when test='<%= "prestadores".equals(tabs1) %>'>
			<liferay-util:include page="/html/portlet/liquidaciones/administracion/prestadores/busqueda_prestadores.jsp" />
		</c:when>

		<c:when test='<%= "convenios-prestacionales".equals(tabs1) %>'>
			<liferay-util:include page="/html/portlet/liquidaciones/administracion/convenios_prest/busqueda_convenios_prestacionales.jsp" />
		</c:when>

		<c:when test='<%= "cartilla-convenios-prestadores".equals(tabs1) %>'>
			<liferay-util:include page="/html/portlet/liquidaciones/administracion/convenios_prest/cartilla_convenio_por_plan.jsp" />
		</c:when>

		<c:when test='<%= "aumento-prestaciones".equals(tabs1) %>'>
			<liferay-util:include page="/html/portlet/liquidaciones/administracion/prestadores/aumento_prestaciones.jsp" />
		</c:when>
	</c:choose>
</form>

<script type="text/javascript">
</script>

<%
request.getSession().removeAttribute("opciones");
PortalUtil.setPageSubtitle(subtitle, request);
%>