<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>

<%
	// Si debe mostrarse el btn de agregar afiliado
	boolean showABMButtons =
		PermissionUtil.userContainsRole(user, WebKeysLiquidaciones.ROL_ABM_ADMINISTRACION);

	// Permiso específico para modificar el check Solicitar Cotización
	boolean puedeModificarSolicitarCotizacion = false;

    try {
    	puedeModificarSolicitarCotizacion =
    		RoleLocalServiceUtil.hasUserRole(
    			user.getUserId(),
    			user.getCompanyId(),
    			WebKeysPrestadores.ROL_ABM_COTIZACION,
    			true
    		);
    } catch (Exception e) {
    	puedeModificarSolicitarCotizacion = false;
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

			String solicitarCotizacionCheckId =
				renderResponse.getNamespace() + "solicitarCotizacion_" + prestador.getId_prestador();

			boolean solicitarCotizacion = prestador.isSolicitarCotizacion();

			String checkedSolicitarCotizacion =
				solicitarCotizacion ? " checked=\"checked\" " : "";

			String disabledSolicitarCotizacion =
            	puedeModificarSolicitarCotizacion ? "" : " disabled=\"disabled\" ";

			String titleSolicitarCotizacion =
				puedeModificarSolicitarCotizacion
					? ""
					: " title=\"No posee permisos para modificar Solicitar Cotización\" ";

			// Action
			if (showABMButtons) {
				row.addJSP(
					"left",
					SearchEntry.DEFAULT_VALIGN,
					"/html/portlet/prestadores/editar_borrar_prestador.jsp"
				);
			}

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

			resultRows.add(row);
		}
	}
%>

<script type="text/javascript">
var puedeModificarSolicitarCotizacion =
	<%= puedeModificarSolicitarCotizacion ? "true" : "false" %>;

function setSolicitarCotizacionChecked(check, checked) {
	if (checked) {
		check.attr('checked', 'checked');
		check[0].checked = true;
	} else {
		check.removeAttr('checked');
		check[0].checked = false;
	}
}

jQuery(document).on('change', '.solicitar-cotizacion-check', function(event) {
	event.stopPropagation();

	var check = jQuery(this);

	if (!puedeModificarSolicitarCotizacion) {
		event.preventDefault();
		setSolicitarCotizacionChecked(check, !check.is(':checked'));
		return false;
	}

	var prestadorId = check.val();
	var solicitarCotizacion = check.is(':checked');
	var estadoAnterior = !solicitarCotizacion;

	check.attr('disabled', 'disabled');

	var data = {};

	data['idPrestador'] = prestadorId;
	data['solicitarCotizacion'] = solicitarCotizacion ? 'true' : 'false';

	data['<portlet:namespace />idPrestador'] = prestadorId;
	data['<portlet:namespace />solicitarCotizacion'] = solicitarCotizacion ? 'true' : 'false';

	jQuery.ajax({
		type: 'POST',
		url: '<%= actualizarSolicitarCotizacionURLString %>',
		data: data,
		cache: false,
		success: function() {
			if (puedeModificarSolicitarCotizacion) {
				check.removeAttr('disabled');
			}
		},
		error: function() {
			setSolicitarCotizacionChecked(check, estadoAnterior);

			if (puedeModificarSolicitarCotizacion) {
				check.removeAttr('disabled');
			}

			alert('No se pudo actualizar Solicitar Cotización.');
		}
	});
});
</script>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />