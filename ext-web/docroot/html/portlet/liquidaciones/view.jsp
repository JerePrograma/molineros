<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<%
boolean rolVEROP = PermissionUtil.userContainsRole(user, WebKeysTesoreria.ROL_VER_OP);
boolean rolABMOP = PermissionUtil.userContainsRole(user, WebKeysTesoreria.ROL_ABM_OP);

if (rolABMOP) {
	rolVEROP = true;
}

boolean showCheques = true;
boolean showOrdenPagoAmtima = true;
boolean showOspim = true;
boolean showOpcionesFarma = PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_FARMACIA);
boolean showOpcionesOdo =
	PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA) ||
	PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO);

boolean showOpcionesAuditor =
	!PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA) &&
	PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_AUDITOR_ODO);

boolean showComprobantesGral = PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_BUSQUEDA_GENERAL_COMPROBANTES);
boolean esLiquidadorExterno = PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_LIQUIDACIONES_HOSPITALES);
boolean esLiquidadorOSPIM = PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_LIQUIDACIONES);

/*
 * COMENTADO: lógica de permisos de prestador/prestadores/convenios.
 * Esto se migra al JSP /html/portlet/prestadores/view.jsp.
 *
boolean showPrestador =
	PermissionUtil.userContainsRole(user, "ABM_PRESTADOR") ||
	PermissionUtil.userContainsRole(user, "VIEW_PRESTADOR");
 */

String tabs1 = ParamUtil.getString(request, "tabs1", null);

if (tabs1 == null) {
	tabs1 = (String) request.getAttribute("tabs1");
}

if (tabs1 == null) {
	tabs1 = (String) request.getSession().getAttribute("tabs1");
}

/*
if (tabs1 == null || tabs1.equals("bandeja-de-entrada")) {
	tabs1 = "liquidaciones";
}
*/

String tabs1Values = null;

if (esLiquidadorExterno) {
	tabs1Values = "liquidaciones";

	if (tabs1 == null || tabs1.equals("bandeja-de-entrada")) {
		tabs1 = "liquidaciones";
	}
}

if (esLiquidadorOSPIM) {
	tabs1Values = "liquidaciones,reintegros,reintegro-farmacia";
	tabs1Values += ",cheques,liquidacion-debitos-terceros,consulta-lista-reintegro,consulta-lista-reintegro-farmacia,reportes";

	if (tabs1 == null || tabs1.equals("bandeja-de-entrada")) {
		tabs1 = "liquidaciones";
	}
}

if (showOpcionesOdo || showOpcionesAuditor) {
	if (tabs1Values == null) {
		tabs1Values = "";

		if (tabs1 == null || tabs1.equals("bandeja-de-entrada")) {
			tabs1 = "protesis";
		}
	} else {
		tabs1Values += ",";
	}

	tabs1Values += "protesis,ortodoncia-ortopedia";
}

if (showOspim && rolVEROP) {
	if (tabs1Values == null) {
		tabs1Values = "";

		if (tabs1 == null || tabs1.equals("bandeja-de-entrada")) {
			tabs1 = "comprobantes";
		}
	} else {
		tabs1Values += ",";
	}

	tabs1Values += "comprobantes";

	if (showComprobantesGral) {
		tabs1Values += ",comprobantes-consulta-general";
	}

	tabs1Values += ",ordenes-pago-ospim";
} else if (showOspim && showComprobantesGral) {
	if (tabs1Values == null) {
		tabs1Values = "";

		if (tabs1 == null || tabs1.equals("bandeja-de-entrada")) {
			tabs1 = "comprobantes-consulta-general";
		}
	} else {
		tabs1Values += ",";
	}

	tabs1Values += "comprobantes-consulta-general";
}

/*
 * COMENTADO: agregado de tab prestador/prestadores/convenios dentro de Liquidaciones.
 * Esto queda en el JSP de Prestadores.
 *
if (showPrestador) {
	if (tabs1Values != null) {
		tabs1Values += (tabs1Values.length() > 0 ? "," : "") + "prestador";
	} else {
		tabs1Values = "prestador";
		tabs1 = "prestador";
	}
}
 */

if (showOpcionesAuditor) {
	if ("reintegros".equals(tabs1)) {
		if (tabs1 == null || tabs1.equals("bandeja-de-entrada")) {
			tabs1 = "protesis";
		}
	}
}

if (tabs1Values == null) {
	tabs1Values = "";
}

if (tabs1Values.length() > 0) {
	String[] vTab = tabs1Values.split(",");

	if (vTab.length > 0) {
		if (tabs1 == null) {
			tabs1 = vTab[0];
		} else {
			if (!tabs1Values.contains(tabs1)) {
				tabs1 = vTab[0];
			}
		}
	}
}

String tabs1Names = StringUtil.replace(tabs1Values, StringPool.UNDERLINE, StringPool.DASH);
String keywords = ParamUtil.getString(request, "keywords");

PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
portletURL.setParameter("struts_action", "/liquidaciones/view");

if (tabs1 != null) {
	portletURL.setParameter("tabs1", tabs1);
}

currentURL = PortalUtil.getCurrentURL(request);
%>

<form action="<%= portletURL %>" method="get" name="<portlet:namespace />fm" onSubmit="submitForm(this); return false;">
	<liferay-portlet:renderURLParams varImpl="portletURL" />

	<c:choose>
		<c:when test='<%= tabs1Values.length() == 0 %>'>
			<div class="portlet-msg-alert">
				No tiene permisos para acceder a las opciones de liquidaciones.
			</div>
		</c:when>

		<c:otherwise>
			<liferay-ui-custom:tabs
				names="<%= tabs1Names %>"
				tabsValues="<%= tabs1Values %>"
				portletURL="<%= portletURL %>"
				value="<%= tabs1 %>"
			/>

			<!-- REPRESENTACION DE LOS TABS DE Liquidaciones -->
			<c:choose>
				<c:when test='<%= "liquidaciones".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/busqueda_liquidaciones.jsp">
						<liferay-util:param name="tipo_liquidacion" value="<%= WebKeysLiquidaciones.LIQUIDACION_PRE %>" />
					</liferay-util:include>
				</c:when>

				<c:when test='<%= "reintegros".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/reintegros/busqueda_reintegro.jsp">
						<liferay-util:param name="tipo_reintegro" value="<%= WebKeysLiquidaciones.REINTEGRO_PRE %>" />
					</liferay-util:include>
				</c:when>

				<c:when test='<%= "reintegro-farmacia".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/farmacia/reintegros/busqueda_reintegro.jsp" />
				</c:when>

				<c:when test='<%= "protesis".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/reintegros/busqueda_reintegro.jsp">
						<liferay-util:param name="tipo_reintegro" value="<%= WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS %>" />
					</liferay-util:include>
				</c:when>

				<c:when test='<%= "ortodoncia-ortopedia".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/reintegros/busqueda_reintegro.jsp">
						<liferay-util:param name="tipo_reintegro" value="<%= WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA %>" />
					</liferay-util:include>
				</c:when>

				<c:when test='<%= "comprobantes".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/comprobantes/busqueda_comprobantes.jsp" />
				</c:when>

				<c:when test='<%= "comprobantes-consulta-general".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/comprobantes/comprobantes_consulta_general.jsp" />
				</c:when>

				<c:when test='<%= "ordenes-pago-ospim".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/ordenes_pago/busqueda_ordenes_pago_ospim.jsp" />
				</c:when>

				<c:when test='<%= "cheques".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/cheques/busqueda_cheques.jsp" />
				</c:when>

				<c:when test='<%= "liquidacion-debitos-terceros".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/busqueda_nota_debito.jsp" />
				</c:when>

				<%--
				COMENTADO: include del tab prestador/prestadores/convenios dentro de Liquidaciones.
				Esto se mueve al JSP /html/portlet/prestadores/view.jsp.

				<c:when test='<%= "prestador".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/prestadores/busqueda_prestadores.jsp" />
				</c:when>
				--%>

				<c:when test='<%= "consulta-lista-reintegro".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/consulta_listas_reintegros/reporte_listas_reintegros.jsp" />
				</c:when>

				<c:when test='<%= "consulta-lista-reintegro-farmacia".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/farmacia/consulta_listas_reintegros/reporte_listas_reintegros.jsp" />
				</c:when>

				<c:when test='<%= "reportes".equals(tabs1) %>'>
					<liferay-util:include page="/html/portlet/liquidaciones/reportes/reportes.jsp" />
				</c:when>
			</c:choose>
		</c:otherwise>
	</c:choose>
</form>

<script type="text/javascript">
	function <portlet:namespace />altaReintegroFarmacia() {
		<portlet:namespace />limpiarCamposAfiliado();

		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_reintegro_farmacia_entry" /></portlet:renderURL>';

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

	function <portlet:namespace />altaLiquidacion() {
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_liquidacion_entry" /></portlet:renderURL>';

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}

	function <portlet:namespace />altaReintegro() {
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/editar_reintegro_entry" /></portlet:renderURL>';

		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	}
</script>

<%--
<%
if (!tabs1.equals("liquidaciones")) {
	PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, StringUtil.replace(tabs1, StringPool.UNDERLINE, StringPool.DASH)), request);
}
%>
--%>