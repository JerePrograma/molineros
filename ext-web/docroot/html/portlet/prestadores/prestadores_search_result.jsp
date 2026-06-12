<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ page import="com.liferay.portal.model.Role" %>
<%@ page import="com.liferay.portal.service.RoleLocalServiceUtil" %>

<portlet:defineObjects/>

<%
	// Si debe mostrarse el btn de agregar afiliado
	boolean showABMButtons =
		PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_ADMINISTRACION);

	String rolSolicitarCotizacion =
		WebKeysPrestadores.ROL_SOLICITAR_COTIZACION_PRESTADOR;

	boolean puedeModificarSolicitarCotizacion = false;

	try {
		puedeModificarSolicitarCotizacion =
			RoleLocalServiceUtil.hasUserRole(
				user.getUserId(),
				user.getCompanyId(),
				rolSolicitarCotizacion,
				true
			);
	} catch (Exception e) {
		puedeModificarSolicitarCotizacion = false;
	}

	if (!puedeModificarSolicitarCotizacion && user != null) {
		try {
			List<Role> rolesUsuario = user.getRoles();

			if (rolesUsuario != null) {
				for (Role role : rolesUsuario) {
					if (role != null &&
						role.getName() != null &&
						rolSolicitarCotizacion.equals(role.getName().trim())) {

						puedeModificarSolicitarCotizacion = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			puedeModificarSolicitarCotizacion = false;
		}
	}

	List<Prestador> prestadores =
		(ArrayList<Prestador>) renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_PRESTADORES);

	PortletURL portletURL = renderResponse.createRenderURL();

	PortletURL actualizarSolicitarCotizacionURL = renderResponse.createActionURL();
	actualizarSolicitarCotizacionURL.setParameter(
		"struts_action",
		"/prestadores/actualizar_solicitar_cotizacion_prestador"
	);

	String actualizarSolicitarCotizacionURLString =
		actualizarSolicitarCotizacionURL.toString().replace("&amp;", "&");

	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");

	List<String> headerNames = new ArrayList<String>();

	headerNames.add("cod-prestador");
	headerNames.add("cuit");
	headerNames.add("descripcion");
	headerNames.add("tipo");
	headerNames.add("Cod.Hospital");
	headerNames.add("baja-fecha");

	if (showABMButtons) {
		headerNames.add("editar-borrar");
	}

	headerNames.add("solicitar-cotizacion");

	SearchContainer searchContainer = new SearchContainer(
		renderRequest,
		null,
		null,
		SearchContainer.DEFAULT_CUR_PARAM,
		SearchContainer.MAX_DELTA,
		portletURL,
		headerNames,
		LanguageUtil.get(pageContext, "no-prestadores-were-found")
	);

	if (prestadores != null) {

		int total = prestadores.size();

		searchContainer.setTotal(total);

		List resultRows = searchContainer.getResultRows();

		for (int i = 0; i < prestadores.size(); i++) {

			Prestador prestador = (Prestador) prestadores.get(i);

			ResultRow row = new ResultRow(
				prestador,
				prestador.getId_prestador(),
				i
			);

			PortletURL rowURL = renderResponse.createRenderURL();

			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
			rowURL.setParameter("struts_action", "/prestadores/editar_prestadores_entry");
			rowURL.setParameter("prestador_id", String.valueOf(prestador.getId_prestador()));
			rowURL.setParameter("cmd", "view");

			row.addText(prestador.getId_prestadorString(), rowURL);
			row.addText(prestador.getCuit(), rowURL);
			row.addText(prestador.getDescripcion(), rowURL);
			row.addText(prestador.getTipo().getDescripcion(), rowURL);
			row.addText(prestador.getCodigoHospital(), rowURL);
			row.addText(prestador.getBaja_fechaAsString(), rowURL);

			if (showABMButtons) {
				row.addJSP(
					"left",
					SearchEntry.DEFAULT_VALIGN,
					"/html/portlet/prestadores/editar_borrar_prestador.jsp"
				);
			}

			String solicitarCotizacionCheckId =
				renderResponse.getNamespace() +
					"solicitarCotizacion_" +
					prestador.getId_prestador();

			boolean solicitarCotizacion = prestador.isSolicitarCotizacion();

			String checkedSolicitarCotizacion =
				solicitarCotizacion ? " checked=\"checked\" " : "";

			String disabledSolicitarCotizacion =
				puedeModificarSolicitarCotizacion ? "" : " disabled=\"disabled\" ";

			String titleSolicitarCotizacion =
				puedeModificarSolicitarCotizacion
					? " title=\"Solicitar Cotización\" "
					: " title=\"No posee permisos para modificar Solicitar Cotización\" ";

			String solicitarCotizacionHtml =
				"<input type=\"checkbox\" " +
					"id=\"" + solicitarCotizacionCheckId + "\" " +
					"name=\"" + solicitarCotizacionCheckId + "\" " +
					"class=\"solicitar-cotizacion-check\" " +
					"value=\"" + prestador.getId_prestador() + "\" " +
					checkedSolicitarCotizacion +
					disabledSolicitarCotizacion +
					titleSolicitarCotizacion +
					"onclick=\"return actualizarSolicitarCotizacionPrestador(this, event);\" />";

			row.addText(
				"center",
				SearchEntry.DEFAULT_VALIGN,
				solicitarCotizacionHtml
			);

			resultRows.add(row);
		}
	}
%>

<script type="text/javascript">
var puedeModificarSolicitarCotizacion =
	<%= puedeModificarSolicitarCotizacion ? "true" : "false" %>;

var actualizarSolicitarCotizacionURL =
	'<%= actualizarSolicitarCotizacionURLString %>';

function setSolicitarCotizacionCheckedNative(check, checked) {
	check.checked = checked;

	if (checked) {
		check.setAttribute('checked', 'checked');
	} else {
		check.removeAttribute('checked');
	}
}

function actualizarSolicitarCotizacionPrestador(check, event) {
	if (event) {
		if (event.stopPropagation) {
			event.stopPropagation();
		}

		event.cancelBubble = true;
	}

	if (!puedeModificarSolicitarCotizacion) {
		setSolicitarCotizacionCheckedNative(check, !check.checked);
		return false;
	}

	var prestadorId = check.value;
	var solicitarCotizacion = check.checked;
	var estadoAnterior = !solicitarCotizacion;

	check.disabled = true;

	var params =
		'idPrestador=' + encodeURIComponent(prestadorId) +
		'&solicitarCotizacion=' + encodeURIComponent(solicitarCotizacion ? 'true' : 'false') +
		'&<portlet:namespace />idPrestador=' + encodeURIComponent(prestadorId) +
		'&<portlet:namespace />solicitarCotizacion=' + encodeURIComponent(solicitarCotizacion ? 'true' : 'false');

	var xhr = null;

	if (window.XMLHttpRequest) {
		xhr = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		xhr = new ActiveXObject('Microsoft.XMLHTTP');
	}

	if (xhr == null) {
		setSolicitarCotizacionCheckedNative(check, estadoAnterior);
		check.disabled = false;
		alert('El navegador no permite ejecutar la actualización.');
		return false;
	}

	xhr.open('POST', actualizarSolicitarCotizacionURL, true);
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');

	xhr.onreadystatechange = function() {
		if (xhr.readyState != 4) {
			return;
		}

		if (xhr.status >= 200 && xhr.status < 300) {
			setSolicitarCotizacionCheckedNative(check, solicitarCotizacion);
			check.disabled = false;
			return;
		}

		setSolicitarCotizacionCheckedNative(check, estadoAnterior);
		check.disabled = false;

		alert('No se pudo actualizar Solicitar Cotización.');
	};

	xhr.send(params);

	return true;
}
</script>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />