<%@ include file="/html/portlet/correspondencia/init.jsp" %>
<%
	//obtengo lista de session	
	List<ItemCorrespondencia> correspondencia = null;
	correspondencia = (List<ItemCorrespondencia>) request.getSession().getAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA_RESULT);
	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);

	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("Paquete");
	headerNames.add("Estado Paquete");
	headerNames.add("Fecha Paquete");
	headerNames.add("Emision Item");
	headerNames.add("Nro. Corr.");	
	//headerNames.add("tipo-correspondencia");		
/* 	headerNames.add("Edificio Destinatario");
	headerNames.add("Sector Destinatario");
	headerNames.add("Usuario Destinatario"); */
	headerNames.add("Tipo");
	headerNames.add("Remitente");
/*  headerNames.add("Cód");
	headerNames.add("Descripción"); */
	headerNames.add("Contenido");
	headerNames.add("Destinatario");
	headerNames.add("Estado Item");	
	if (showABMButtons) {
		headerNames.add("Desempaquetar");		
	}
	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"No se encontraron paquetes"));
	
	boolean hayEmpaquetables = false;
	
	//recupero coincidencias		
	if (null != correspondencia) {
		total = correspondencia.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < correspondencia.size(); i++) {
			ItemCorrespondencia corr = (ItemCorrespondencia) correspondencia.get(i);
			ResultRow row = new ResultRow(corr, String.valueOf(corr.getId()), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(WindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/correspondencia/view_correspondencia_entry");
 			rowURL.setParameter("id_correspondencia", String.valueOf(corr.getId_correspondencia()));
 			rowURL.setParameter("id_item_correspondencia", String.valueOf(corr.getId()));
 			rowURL.setParameter("tipo_registro", corr.getCabecera().getTipoRegistro());
 			rowURL.setParameter("view", "true"); 
 			
 			row.addText(String.valueOf(corr.getListaPaquete().getId_paquete()));									
			row.addText(corr.getPaquete().getEstado());
			row.addText(String.valueOf(corr.getListaPaquete().getAlta_fechaString()));
			/*row.addText(corr.getCabecera().getLugar());  */
			row.addText(corr.getCabecera().getLugarDescription());
			row.addText(String.valueOf(corr.getCabecera().getId_correspondencia()),rowURL);			
/* 			row.addText(corr.getEdificioDescripcion());
			row.addText(corr.getSectorDescripcion()); 
			row.addText(corr.getUsuario()); */
			row.addText(corr.getTipoRemitenteDestinatario());
			row.addText(corr.getRemitente());
/* 			row.addText(corr.getCodRemitenteDestinatario());
			row.addText(corr.getDescRemitenteDestinatario()); */
			row.addText(corr.getContenido() + (corr.getComprobanteString().isEmpty()?"":"<br/>"+corr.getComprobanteString()) );
			row.addText(corr.getDestinatario());
 			row.addText(corr.getEstado());			
			if ( corr.getPaquete().getEstado().equalsIgnoreCase("ENVIADO") &&
					Long.parseLong(corr.getEdificio()) == Long.valueOf(user.getOrganizations().get(0).getOrganizationId()) )
				 {
				StringBuffer sb = new StringBuffer();
				sb.append("<input type='checkbox' name='itempaq_"+corr.getListaPaquete().getId_paquete()+"-"+corr.getId());
				sb.append("' ");
				sb.append("id='itempaq_"+corr.getListaPaquete().getId_paquete()+"-"+corr.getId() );
				sb.append("' />"); //checked='checked'
				row.addText(sb.toString());
				
			}else{
				row.addText("");		
			}
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
	<liferay-util:include page="/html/portlet/correspondencia/paginador_entradas_salidas.jsp">
		<liferay-util:param name="llamada" value="paquetes"/>
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

<br/>
	

