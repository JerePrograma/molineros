<%@ include file="/html/portlet/novedades/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
<%
	//obtengo lista de session	
	List<PreAfiliadoTotal> afiliadosList= (ArrayList<PreAfiliadoTotal>)request.getSession().getAttribute(WebKeysAfiliados.BUSQUEDA_PRECARGA_AFILIADO);
	
	PreAfiliado afi = null;
	String cuil=null, inte=null;

	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_PRE_CARGA_AFI);
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
		headerNames.add("Padron");
		headerNames.add("Codigo");
		headerNames.add("cuil");
		headerNames.add("inte");
		headerNames.add("apellido");
		headerNames.add("nombre");
		/* headerNames.add("parentesco"); */
	headerNames.add("tipo-documento");
	headerNames.add("nro-documento");
	/* headerNames.add("seccional"); */
	headerNames.add("vigen-fecha");
	headerNames.add("CUIT Empleador");
	headerNames.add("alta-secc-fecha");
	if(showABMButtons) { 
		headerNames.add("editar-borrar");
	}				
	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext, "no-afiliados-were-found"));

		
	if (null != afiliadosList) {

		//Seteo el total de la lista.
	 	total = afiliadosList.size();
	 	searchContainer.setTotal(total);
	 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
	 	List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < afiliadosList.size(); i++) {
	 		PreAfiliado afiliado = (PreAfiliado) afiliadosList.get(i);
				ResultRow row = new ResultRow(afiliado,afiliado.getCuil_titular(), i);
				PortletURL rowURL = renderResponse.createRenderURL();		 				
				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL.setParameter("struts_action","/afiliados/editar_pre_afiliado");
 				rowURL.setParameter("cuil_titular", afiliado.getCuil_titular());
				rowURL.setParameter("inte", String.valueOf(afiliado.getInte())); 
				rowURL.setParameter("id", String.valueOf(afiliado.getId()));
				rowURL.setParameter("cmd", "view");
				StringBuilder sb = new StringBuilder("");
				if (afiliado.isDe_alta_portal()) {				
					sb.append("<img alt=\"Pre-Carga\" src=\"");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/user_icon.png\"/>");
				}else{
					sb.append("<img alt=\"Pre-Carga\" src=\"");
					sb.append(themeDisplay.getPathThemeImages());
					sb.append("/common/guest_icon.png\"/>");
				}
				row.addText(sb.toString());
				row.addText(String.valueOf(afiliado.getId()),rowURL);
				row.addText(afiliado.getCuil_titularMasked(),rowURL);
				row.addText(String.valueOf(afiliado.getInte()),rowURL);
				row.addText(afiliado.getApellido(),rowURL);
				row.addText(afiliado.getNombre(),rowURL);
/* 		 				row.addText(afiliado.getParentesco(), rowURL);*/						
				row.addText(afiliado.getDocumento_tipo(),rowURL);						
			    row.addText(String.valueOf(afiliado.getDocumento_numero()),rowURL); 					
/* 						row.addText(afiliado.getSeccional().getDescripcion()!=null?afiliado.getSeccional().getDescripcion():"Sin Especificar",rowURL);
*/					
				row.addText(sdf.format(afiliado.getVigen_fecha()),rowURL); 					
				row.addText(afiliado.getCuit(),rowURL); 
				row.addText(sdf.format(afiliado.getAlta_fecha()),rowURL); 
			// Action
			if(showABMButtons) {
 				row.addJSP("left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/novedades/editar_borrar_pre_afiliado.jsp");
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
	<liferay-util:include page="/html/portlet/novedades/paginador_busqueda_pre_afiliados.jsp">
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
	