<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

/*
 * Permisos migrados desde Liquidaciones.
 *
 * En el JSP original de Liquidaciones, el acceso al tab "prestador"
 * dependía exclusivamente de:
 *
 * - ABM_PRESTADOR
 * - VIEW_PRESTADOR
 *
 * Como no había roles específicos para convenios en ese JSP,
 * se usa el mismo permiso base para Prestadores, Convenios y Cartilla.
 */
boolean showPrestador =
	PermissionUtil.userContainsRole(user, "ABM_PRESTADOR") ||
	PermissionUtil.userContainsRole(user, "VIEW_PRESTADOR");

boolean showConveniosPrestacionales = showPrestador;
boolean showCartillaConveniosPrestadores = showPrestador;

boolean showAumentoNomenclador =
	PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_AUMENTO_NOMENCLADOR);

StringBuffer tabs1ValuesBuffer = new StringBuffer();
StringBuffer tabs1NamesBuffer = new StringBuffer();

boolean tieneTabs = false;

if (showPrestador) {
	tabs1ValuesBuffer.append("prestadores");
	tabs1NamesBuffer.append("Prestadores");
	tieneTabs = true;
}

if (showConveniosPrestacionales) {
	if (tieneTabs) {
		tabs1ValuesBuffer.append(",");
		tabs1NamesBuffer.append(",");
	}

	tabs1ValuesBuffer.append("convenios-prestacionales");
	tabs1NamesBuffer.append("Convenios Prestacionales");
	tieneTabs = true;
}

if (showCartillaConveniosPrestadores) {
	if (tieneTabs) {
		tabs1ValuesBuffer.append(",");
		tabs1NamesBuffer.append(",");
	}

	tabs1ValuesBuffer.append("cartilla-convenios-prestadores");
	tabs1NamesBuffer.append("Cartilla de Convenios de Prestadores");
	tieneTabs = true;
}

if (showAumentoNomenclador) {
	if (tieneTabs) {
		tabs1ValuesBuffer.append(",");
		tabs1NamesBuffer.append(",");
	}

	tabs1ValuesBuffer.append("aumento-prestaciones");
	tabs1NamesBuffer.append("Aumento de Prestaciones");
	tieneTabs = true;
}

String tabs1Values = tabs1ValuesBuffer.toString();
String tabs1Names = tabs1NamesBuffer.toString();

if (tabs1Values.length() > 0) {
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

	request.getSession().setAttribute("tabs1", tabs1);
} else {
	tabs1 = null;
	request.getSession().removeAttribute("tabs1");
}

String subtitle = "Prestadores";

if ("convenios-prestacionales".equals(tabs1)) {
	subtitle = "Convenios Prestacionales";
} else if ("cartilla-convenios-prestadores".equals(tabs1)) {
	subtitle = "Cartilla de Convenios de Prestadores";
} else if ("aumento-prestaciones".equals(tabs1)) {
	subtitle = "Aumento de Prestaciones";
} else if (tabs1 == null) {
	subtitle = "Sin permisos";
}

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/prestadores/view");

if (tabs1 != null) {
	portletURL.setParameter("tabs1", tabs1);
}

currentURL = PortalUtil.getCurrentURL(request);
%>

<liferay-portlet:renderURLParams varImpl="portletURL" />

<c:choose>
	<c:when test='<%= tabs1Values.length() == 0 %>'>
		<div class="portlet-msg-alert">
			No tiene permisos para acceder a las opciones de prestadores o convenios.
		</div>
	</c:when>

	<c:otherwise>
		<liferay-ui-custom:tabs
			names="<%= tabs1Names %>"
			tabsValues="<%= tabs1Values %>"
			portletURL="<%= portletURL %>"
			value="<%= tabs1 %>"
		/>

		<c:choose>
			<c:when test='<%= "prestadores".equals(tabs1) %>'>
				<liferay-util:include page="/html/portlet/prestadores/busqueda_prestadores.jsp" />
			</c:when>

			<c:when test='<%= "convenios-prestacionales".equals(tabs1) %>'>
				<liferay-util:include page="/html/portlet/prestadores/convenios_prest/busqueda_convenios_prestacionales.jsp" />
			</c:when>

			<c:when test='<%= "cartilla-convenios-prestadores".equals(tabs1) %>'>
				<liferay-util:include page="/html/portlet/prestadores/convenios_prest/cartilla_convenio_por_plan.jsp" />
			</c:when>

			<c:when test='<%= "aumento-prestaciones".equals(tabs1) %>'>
				<liferay-util:include page="/html/portlet/prestadores/convenios_prest/aumento_prestaciones.jsp" />
			</c:when>
		</c:choose>
	</c:otherwise>
</c:choose>

<%
request.getSession().removeAttribute("opciones");
PortalUtil.setPageSubtitle(subtitle, request);
%>
