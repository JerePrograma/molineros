<%@ include file="/html/portlet/uoma/init.jsp" %>
<%
	//obtengo lista de session	
	List<IncidenteTotal> incidentes= (ArrayList<IncidenteTotal>)request.getSession().getAttribute(WebKeysUnidadOperativa.BUSQUEDA_INCIDENTES_RESULT);	
	
	BusquedaIncidentesUnidadOpeFiltro filtro = (BusquedaIncidentesUnidadOpeFiltro) request.getSession().getAttribute(WebKeysUnidadOperativa.BUSQUEDA_INCIDENTES);
	//...
	int total = 0;
	
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("fecha");
	headerNames.add("cuil");
	headerNames.add("apellido");
	headerNames.add("nombre");
	headerNames.add("seccional");
	headerNames.add("detalle");
	headerNames.add("edit");	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,20, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-incidentes-were-found"));
	//recupero coincidencias		
	if(null!=incidentes){
		
		total = incidentes.size();
		
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < incidentes.size(); i++) {
		 		IncidenteTotal incidente = (IncidenteTotal) incidentes.get(i);
				ResultRow row = new ResultRow(incidente,incidente.getAfiliado().getCuil_titular(), i);			
				// Name and short description				
				row.addText(incidente.getFechaAsString());
				row.addText(incidente.getAfiliado().getCuil_titular());
				row.addText(incidente.getAfiliado().getApellido());
				row.addText(incidente.getAfiliado().getNombre());
				row.addText(incidente.getDescripcionSeccional());
				row.addText(incidente.getDetalleIncidente());
				StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/portlet/edit_guest.png\" onClick=\"javascript:editaIncidente('");
		 					sb.append(incidente.getIdIncidente());		 					
		 					sb.append("');\" />");
				row.addText(sb.toString());		 					
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
	<liferay-util:include page="/html/portlet/uoma/unidad_operativa/paginador_incidentes.jsp" />
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
