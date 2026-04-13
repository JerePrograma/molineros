<%@ include file="/html/portlet/novedades/init.jsp" %>
<%
	//obtengo lista de session	
	List<NovedadTotal> novedades = null;
	novedades = (List<NovedadTotal>) request.getSession().getAttribute(WebKeysAfiliados.BUSQUEDA_NOVEDADES_EN_SESSION);
	
	List<Afiliado>afiliados = null;
	Afiliado afi = null;
	String cuil=null, inte=null;

	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_NOVEDADES_SSS);

	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("cuil-titular");
/* 	headerNames.add("inte"); */
	headerNames.add("cuil");
 	headerNames.add("apeynom");
 	headerNames.add("parentesco");
 	headerNames.add("tipo-novedad");
 	headerNames.add("detalle-novedad");

/* 	headerNames.add("fecha-importacion"); */
 	
	if (showABMButtons) {
		headerNames.add("Ver Novedad");
		headerNames.add("Afiliado en Padrón");
	}

	headerNames.add("Marcar como inconsistente");

	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-novedades-were-found"));
		
	if (null != novedades) {
		total = novedades.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();

		
		for (int i = 0; i < novedades.size(); i++) {
			NovedadTotal nove = (NovedadTotal) novedades.get(i);
			
/* 			afiliados = EditarAfiliadoServiceUtil.getAfiliadosPorDocumento(String.valueOf(nove.getDocumento_numero()),nove.getDocumento_tipo()) ;
			if(afiliados!= null && afiliados.size()==1){
				afi = afiliados.get(0);
				cuil = afi.getCuil_titular();
				inte = afi.getInteAsString();
				
				nove.setCuil(cuil+"-"+inte);
			}else{
				afi = null;
				cuil = null;
				inte = null;
			} */
			
			ResultRow row = new ResultRow(nove, String.valueOf(nove.getId()), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(WindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/afiliados/ver_detalle_novedad");
 			rowURL.setParameter("id_novedad", String.valueOf(nove.getId()));
/*  			rowURL.setParameter("view", "true"); */
  			
			row.addText(nove.getCuil_titular());
			row.addText(nove.getCuil(),rowURL);			
			row.addText(nove.getApellido_nombre());
			row.addText(nove.getParentescoDesc());
			row.addText(nove.getCodigo_movimiento());
			row.addText(nove.getDetalle_novedad());
 						
			if (showABMButtons){
				/* row.addText(""); */
				StringBuffer sb1 = new StringBuffer();			
				sb1.append("&nbsp;<img alt=\"<liferay-ui:message key='dar-alta'/>\" src=\"");
		 		sb1.append(themeDisplay.getPathThemeImages());
		 		sb1.append("/common/view.png\" onClick=\"javascript:mostrarDetalleNovedad('");
		 		sb1.append(String.valueOf(nove.getId()));
		 		sb1.append("');\" />");
		 		row.addText(sb1.toString());	
				/* row.addText(""); */
				/* StringBuffer sb2 = new StringBuffer();			
				sb2.append("&nbsp;<img alt=\"<liferay-ui:message key='editar-afiliado'/>\" src=\"");
		 		sb2.append(themeDisplay.getPathThemeImages());
		 		sb2.append("/common/edit.png\" onClick=\"javascript:seleccionaAfiliadoDocumento('");
		 		sb2.append(nove.getDocumento_tipo() + "'"+ ", '" + String.valueOf(nove.getDocumento_numero()) );
		 		sb2.append("');\" />");
		 		row.addText(sb2.toString());	 */
	/* 	 		StringBuffer sb2 = new StringBuffer();			
				sb2.append("&nbsp;<img alt=\"<liferay-ui:message key='editar-afiliado'/>\" src=\"");
		 		sb2.append(themeDisplay.getPathThemeImages());
		 		sb2.append("/common/edit.png\"  onClick=\"javascript:editarAfiliado('" );
				sb2.append("20290489157','0')");
				sb2.append("\"/>");
		 		row.addText(sb2.toString()); */
		 		/* if(afi != null){
					row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/novedades/editar_afi_nov.jsp");
		 		}else{
		 			StringBuffer sb2 = new StringBuffer();			
					sb2.append("&nbsp;<img alt=\"<liferay-ui:message key='editar-afiliado'/>\" src=\"");
			 		sb2.append(themeDisplay.getPathThemeImages());
			 		sb2.append("/common/edit.png\" onClick=\"javascript:seleccionaAfiliadoDocumento('");
			 		sb2.append(nove.getDocumento_tipo() + "'"+ ", '" + String.valueOf(nove.getDocumento_numero()) );
			 		sb2.append("');\" />");
			 		row.addText(sb2.toString());
		 		} */
/* 		 		row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/novedades/editar_afi_nov.jsp"); */
				/*Cambio de cuil*/
				if(nove.getCodigo_movimiento().equalsIgnoreCase("CC") ||
	 			   nove.getCodigo_movimiento().equalsIgnoreCase("MC") || 
		 		   nove.getCodigo_movimiento().equalsIgnoreCase("AP")){ 
				
					StringBuffer sb2 = new StringBuffer();			
					sb2.append("&nbsp;<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 			sb2.append(themeDisplay.getPathThemeImages());
		 			sb2.append("/common/edit.png\" onClick=\"javascript:mostrarCambioCuil('");
		 			sb2.append(String.valueOf(nove.getId()));
		 			sb2.append("');\" />");
		 			row.addText(sb2.toString());
				}else{
 					row.addJSP("left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/novedades/editar_afi_nov.jsp");
				}
 				row.addJSP("left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/novedades/editar_afi_nov_inconsistencia.jsp");
				
		 		 
				
		/* 		StringBuffer sb = new StringBuffer();
				sb.append("<input type='checkbox' name='itempaq_"+corr.getListaPaquete().getId_paquete()+"-"+corr.getId());
				sb.append("' ");
				sb.append("id='itempaq_"+corr.getListaPaquete().getId_paquete()+"-"+corr.getId() );
				sb.append("' />"); //checked='checked'
				row.addText(sb.toString()); */
				
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
	<liferay-util:include page="/html/portlet/novedades/paginador_busqueda_novedades.jsp">
<!-- 		<liferay-util:param name="llamada" value="paquetes"/> -->
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
	

