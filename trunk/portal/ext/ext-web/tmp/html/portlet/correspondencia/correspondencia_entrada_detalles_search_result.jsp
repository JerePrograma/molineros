<%@ include file="/html/portlet/correspondencia/init.jsp" %>
<%
	//obtengo lista de session
	List<ItemCorrespondencia> itemsCorresp = null ;
	CabeceraCorrespondencia correspondenciaE=(CabeceraCorrespondencia)request.getSession().getAttribute(WebKeysCorrespondencia.ENTRADA_EN_EDICION);

	if(correspondenciaE != null){
		itemsCorresp = correspondenciaE.getItemsCorrespondencia();
	}
	
	boolean showABMButtons = PermissionUtil.userContainsRole(user,
			WebKeysCorrespondencia.ROL_ABM_CORRESPONDENCIA);

	String viewStr = ParamUtil.getString(request, "esViewStr");
	boolean esView = false;
	if (viewStr != null && viewStr != ""){
		esView = true;
	}
	
	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("Item");
	headerNames.add("Paquete");
	headerNames.add("Remitente");
	headerNames.add("Contenido");
	headerNames.add("Tipo");
	headerNames.add("Destinatario");
	headerNames.add("Comprobante");
	headerNames.add("Importe");
	headerNames.add("Fecha Emisión");

	
	if (showABMButtons) {
		headerNames.add("Editar/Borrar");	
	}
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-correspondencia-were-found"));
			
	//recupero coincidencias
	if (null != itemsCorresp) {
		total = itemsCorresp.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = itemsCorresp.size()-1; i >= 0; i--) {  // lo doy vuelta para mostrar los mas recientes al principio
			ItemCorrespondencia corr = (ItemCorrespondencia) itemsCorresp.get(i);
			
			/* mostramos todo excepto los eliminados */
			if(corr.getBaja_fecha() == null){
				ResultRow row = new ResultRow(corr, String.valueOf(corr.getId()), i);
				PortletURL rowURL = renderResponse.createRenderURL();
	 			rowURL.setWindowState(WindowState.MAXIMIZED);
				
	 			row.addText(String.valueOf(corr.getId()));
	 			row.addText(String.valueOf(corr.getListaPaquete() != null ? corr.getListaPaquete().getId_paquete() : ""));
				row.addText(corr.getRemitente());
				row.addText(corr.getContenido() == null ? "" : corr.getContenido());
				row.addText(corr.getTipoRemitenteDestinatario());
				row.addText(corr.getDestinatario());
				row.addText(corr.getComprobanteString());
				row.addText(corr.getImporte() != null && corr.getImporte().compareTo(BigDecimal.ZERO) == 1 ? corr.getImporte().toPlainString() : "");
				if(corr.getComprobanteString() != null && corr.getComprobanteString().length() >0){
					row.addText(corr.getFecha_emision() != null ? DateUtils.format(corr.getFecha_emision(), DateUtils.SHORT) : "");
				} else { 
					row.addText("");
				}
				StringBuilder sb = new StringBuilder("");
				if (Validator.isNotNull(corr.getBaja_fecha()) ) {				
						sb.append("<img alt=\"Baja\" src=\"");
						sb.append(themeDisplay.getPathThemeImages());
						sb.append("/common/close.png\"/>");
						
						row.addText(sb.toString());
				}else if (showABMButtons && !esView && !Validator.isNotNull(corr.getBaja_fecha())) {
					row.addJSP("right", SearchEntry.DEFAULT_VALIGN,
							"/html/portlet/correspondencia/editar_borrar_item_correspondencia.jsp");
					  }else{
							row.addText("");
				}
				
				resultRows.add(row);
			}
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