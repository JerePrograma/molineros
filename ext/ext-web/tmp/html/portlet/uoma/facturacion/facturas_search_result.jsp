<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
	//obtengo resultados
	List<Factura> facturasResults = (ArrayList<Factura>) request.getSession().getAttribute(WebKeysUOMA.BUSQUEDA_FACTURAS_RESULT);

	BusquedaFacturasFiltro filtro = (BusquedaFacturasFiltro) request.getSession().getAttribute(WebKeysUOMA.FILTRO_BUSQUEDA_FACTURAS);

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
	 
	headerNames.add("Fecha");
	headerNames.add("Razón Social");
	headerNames.add("Sucursal");
	headerNames.add("Tipo");
	headerNames.add("Letra");
	headerNames.add("Numero");
	headerNames.add("Importe");
	headerNames.add("CAE");
	

	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-facturas-search-were-found"));

//recupero coincidencias
if (null != facturasResults && facturasResults.size() > 0 ) {
	total = facturasResults.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	for (int i = 0; i < facturasResults.size(); i++) {

		Factura fc = (Factura) facturasResults.get(i);
		
		ResultRow row = new ResultRow(fc, String.valueOf(fc.getId()), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		rowURL.setParameter("struts_action","/uoma/facturacion_editar");	
		rowURL.setParameter("idFactura", String.valueOf(fc.getId())); 
		rowURL.setParameter("cmd", "view");
		row.addText(sdf.format(fc.getFecha()),rowURL);
		row.addText((fc.getCliente().getRazonSocial()!=null?fc.getCliente().getRazonSocial():
			fc.getCliente().getApellido()!=null && fc.getCliente().getNombre()!=null?fc.getCliente().getApellido()+ " "+fc.getCliente().getNombre():
				""),rowURL);
		row.addText(fc.getSucursal(),rowURL);
		row.addText(fc.getTipo(),rowURL);
		row.addText(fc.getLetra(),rowURL);
		row.addText(fc.getNumero(),rowURL);
		row.addText(String.valueOf(fc.getImporteTotalCalculado() ),rowURL);
		row.addText(fc.getCaeDescripcion(),rowURL);
		
		resultRows.add(row);
	}
}

%>
<div class="search-results">
	<c:choose>
		<c:when test="<%= total != 1 %>">
			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
		</c:when>
		<c:otherwise>
			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
		</c:otherwise>
	</c:choose>
	<liferay-util:include page="/html/portlet/uoma/facturacion/paginador_facturas_search_results.jsp">
	</liferay-util:include>
</div>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />
<div class="search-results">
	<c:choose>
		<c:when test="<%= total != 1 %>">
			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
		</c:when>
		<c:otherwise>
			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
		</c:otherwise>
	</c:choose>
</div>

<script type="text/javascript">
</script>	