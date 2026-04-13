<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	List<HistoricoMovimientoAfiliado> historico = (List<HistoricoMovimientoAfiliado>) request
			.getAttribute(WebKeysAfiliados.HISTORICO_MOVIMIENTOS);

	PortletURL portletURLHistoMov = renderResponse.createRenderURL();
	List<String> headerNamesHistoMov = new ArrayList<String>();
	headerNamesHistoMov.add("cuil-titular");
	headerNamesHistoMov.add("inte");
	headerNamesHistoMov.add("parentesco");
	headerNamesHistoMov.add("nro-documento");
	headerNamesHistoMov.add("apellido");
	headerNamesHistoMov.add("nombre");
	headerNamesHistoMov.add("Cambio");
	headerNamesHistoMov.add("Anterior");
	headerNamesHistoMov.add("actual");
	headerNamesHistoMov.add("usuario");
	headerNamesHistoMov.add("process-fecha");

	String view = "";

	SearchContainer searchContainerHistoMov = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURLHistoMov,
			headerNamesHistoMov, LanguageUtil.get(pageContext,
					"no-histo-mov-were-found"));

	if (null != historico) {
		int total = historico.size();
		searchContainerHistoMov.setTotal(total);
		List resultRows = searchContainerHistoMov.getResultRows();

		for (int i = 0; i < historico.size(); i++) {
			HistoricoMovimientoAfiliado histoMov = (HistoricoMovimientoAfiliado) historico
					.get(i);
			ResultRow row = null;
			row = new ResultRow(histoMov, histoMov.getCuil_titular(), i);

			if (histoMov.getCuil_titularMasked() != null) {
				row.addText(histoMov.getCuil_titularMasked());
			} else {
				row.addText("");
			}
			if (histoMov.getInteAsString() != null) {
				row.addText(histoMov.getInteAsString());
			} else {
				row.addText("");
			}
			if (histoMov.getParentesco() != null) {
				row.addText(histoMov.getParentesco().toUpperCase());
			} else {
				row.addText("");
			}
			if (histoMov.getNro_documento() != null) {
				row.addText(histoMov.getNro_documento());
			} else {
				row.addText("");
			}
			if (histoMov.getApellido() != null) {
				row.addText(histoMov.getApellido().toUpperCase());
			} else {
				row.addText("");
			}
			if (histoMov.getNombre() != null) {
				row.addText(histoMov.getNombre().toUpperCase());
			} else {
				row.addText("");
			}
			if (histoMov.getModificacion() != null) {
				row.addText(histoMov.getModificacion().toUpperCase());
			} else {
				row.addText("");
			}
			if (histoMov.getValor_anterior() != null) {
				row.addText(histoMov.getValor_anterior().toUpperCase());
			} else {
				row.addText("");
			}
			if (histoMov.getValor_actual() != null) {
				row.addText(histoMov.getValor_actual().toUpperCase());
			} else {
				row.addText("");
			}
			if (histoMov.getUsuario() != null) {
				row.addText(histoMov.getUsuario());
			} else {
				row.addText("");
			}
			if (histoMov.getFecha_modificacionAsString() != null) {
				row.addText(histoMov.getFecha_modificacionAsString());
			} else {
				row.addText("");
			}
			resultRows.add(row);
		}
	}
	
%>

<c:choose>
	<c:when
		test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<liferay-ui:success key="request_processed" message="grabar-exitoso" />		
	</c:when>
</c:choose>
<liferay-ui:error exception="<%= Exception.class %>"
	message="error-al-grabar" />
<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainerHistoMov%>" />