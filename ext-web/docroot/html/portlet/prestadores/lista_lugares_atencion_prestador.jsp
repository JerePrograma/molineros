<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<%!
	private String nvl(Object value) {
		return value == null ? "" : String.valueOf(value);
	}
%>

<%
	List<PrestadorLugarAtencion> lugarAtPrestador =
		(List<PrestadorLugarAtencion>) request.getSession().getAttribute(
			WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_EN_SESSION
		);

	if (lugarAtPrestador == null) {
		lugarAtPrestador = new ArrayList<PrestadorLugarAtencion>();
	}

	PortletURL portletURL = renderResponse.createRenderURL();

	List<String> headerNames = new ArrayList<String>();
	headerNames.add("Vigencia Desde");
	headerNames.add("Factura");
	headerNames.add("Nombre");
	headerNames.add("Provincia");
	headerNames.add("Localidad");
	headerNames.add("Direccion");
	headerNames.add("email");
	headerNames.add("Telefono");
	headerNames.add("Editar/Eliminar");

	SearchContainer searchContainer = new SearchContainer(
		renderRequest,
		null,
		null,
		SearchContainer.DEFAULT_CUR_PARAM,
		SearchContainer.MAX_DELTA,
		portletURL,
		headerNames,
		LanguageUtil.get(pageContext, "no-lugarat-were-found")
	);

	searchContainer.setTotal(lugarAtPrestador.size());

	List resultRows = searchContainer.getResultRows();

	for (int i = 0; i < lugarAtPrestador.size(); i++) {

		PrestadorLugarAtencion la = lugarAtPrestador.get(i);

		ResultRow row = new ResultRow(la, la.getId_domicilio(), i);

		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		rowURL.setParameter("struts_action", "/prestadores/lista_lugares_atencion_prestador");
		rowURL.setParameter("domicilio_id", String.valueOf(la.getId_domicilio()));
		rowURL.setParameter("prestador_id", String.valueOf(la.getId_prestador()));
		rowURL.setParameter("cmd", "edit");

		Domicilio domicilio = la.getDomicilio();

		String provincia = "";
		String localidad = "";
		String direccion = "";

		if (domicilio != null) {
			if (domicilio.getProvincia() != null) {
				provincia = nvl(domicilio.getProvincia().getDescripcion());
			}

			if (domicilio.getLocalidad() != null) {
				localidad = nvl(domicilio.getLocalidad().getDescripcion());
			}

			StringBuffer direccionBuffer = new StringBuffer();

			if (nvl(domicilio.getNumero()).length() > 0) {
				direccionBuffer.append("Nro: ").append(nvl(domicilio.getNumero()));
			}

			if (nvl(domicilio.getPiso()).length() > 0) {
				if (direccionBuffer.length() > 0) {
					direccionBuffer.append(" ");
				}
				direccionBuffer.append("Piso: ").append(nvl(domicilio.getPiso()));
			}

			if (nvl(domicilio.getDepto()).length() > 0) {
				if (direccionBuffer.length() > 0) {
					direccionBuffer.append(" ");
				}
				direccionBuffer.append("Dto: ").append(nvl(domicilio.getDepto()));
			}

			if (nvl(domicilio.getPostal_codi()).length() > 0) {
				if (direccionBuffer.length() > 0) {
					direccionBuffer.append(" ");
				}
				direccionBuffer.append("CP: ").append(nvl(domicilio.getPostal_codi()));
			}

			direccion = direccionBuffer.toString();
		}

		String vigenciaDesde = "";

		if (la.getVigen_desde() != null) {
			vigenciaDesde = DateUtils.format(la.getVigen_desde(), DateUtils.SHORT);
		}

		row.addText(vigenciaDesde, rowURL);
		row.addText(nvl(la.getFactura()), rowURL);
		row.addText(nvl(la.getNombre()), rowURL);
		row.addText(provincia, rowURL);
		row.addText(localidad, rowURL);
		row.addText(direccion, rowURL);
		row.addText(nvl(la.getCorreoElectronico()), rowURL);
		row.addText(nvl(la.getTelefonosConcatenados()), rowURL);

		row.addJSP(
			"left",
			SearchEntry.DEFAULT_VALIGN,
			"/html/portlet/prestadores/editar_borrar_lugar_at_prestador.jsp"
		);

		resultRows.add(row);
	}
%>

<liferay-ui:error exception="<%= LugarAtencionPrestadorException.class %>" message="lugar-at-duplicado" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
