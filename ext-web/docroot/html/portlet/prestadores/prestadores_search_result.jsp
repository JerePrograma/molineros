<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<%
	// Si debe mostrarse el btn de agregar afiliado
	boolean showABMButtons =
		PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_ADMINISTRACION);

	// Permiso específico para modificar el check Solicitar Cotización
	boolean puedeModificarSolicitarCotizacion =
		PermissionUtil.userContainsRole(user, WebKeysPrestadores.ROL_ABM_COTAZACION);

	List<Prestador> prestadores =
		(ArrayList<Prestador>) renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_PRESTADORES);

	PortletURL portletURL = renderResponse.createRenderURL();

	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");

	List<String> headerNames = new ArrayList<String>();

	headerNames.add("cod-prestador");
	headerNames.add("cuit");
	headerNames.add("descripcion");
	headerNames.add("tipo");
	headerNames.add("Cod.Hospital");
	headerNames.add("baja-fecha");
	headerNames.add("solicitar-cotizacion");

	if (showABMButtons) {
		headerNames.add("editar-borrar");
	}

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

			String solicitarCotizacionCheckId =
				renderResponse.getNamespace() + "solicitarCotizacion_" + prestador.getId_prestador();

			boolean solicitarCotizacion = prestador.isSolicitarCotizacion();

			// Futuro:
			// boolean solicitarCotizacion = prestador.isSolicitarCotizacion();

			String checkedSolicitarCotizacion =
				solicitarCotizacion ? " checked=\"checked\" " : "";

			String disabledSolicitarCotizacion =
				puedeModificarSolicitarCotizacion ? "" : " disabled=\"disabled\" ";

			String titleSolicitarCotizacion =
				puedeModificarSolicitarCotizacion
					? ""
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
					"onclick=\"event.stopPropagation();\" />";

			row.addText(
				"center",
				SearchEntry.DEFAULT_VALIGN,
				solicitarCotizacionHtml
			);

			// Action
			if (showABMButtons) {
				row.addJSP(
					"left",
					SearchEntry.DEFAULT_VALIGN,
					"/html/portlet/prestadores/editar_borrar_prestador.jsp"
				);
			}

			resultRows.add(row);
		}
	}
%>

<script type="text/javascript">
var puedeModificarSolicitarCotizacion =
	<%= puedeModificarSolicitarCotizacion ? "true" : "false" %>;

jQuery(document).on('change', '.solicitar-cotizacion-check', function(event) {
	event.stopPropagation();

	if (!puedeModificarSolicitarCotizacion) {
		event.preventDefault();
		return false;
	}

	var prestadorId = jQuery(this).val();
	var solicitarCotizacion = jQuery(this).is(':checked');

	// Futuro:
	// llamar action/renderURL con prestadorId + solicitarCotizacion
});
</script>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />