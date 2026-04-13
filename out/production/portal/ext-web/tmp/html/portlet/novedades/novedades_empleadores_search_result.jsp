<%@page import="ar.com.ospim.novedades.beans.NovedadEmpleadorTotal"%>
<%@ include file="/html/portlet/novedades/init.jsp" %>
<%
	//obtengo lista de session	
	SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
	List<NovedadEmpleadorTotal> novedades = null;
	novedades = (List<NovedadEmpleadorTotal>) request.getSession().getAttribute(WebKeysAfiliados.BUSQUEDA_NOVEDADES_EN_SESSION);
	
	List<Afiliado>afiliados = null;
	Afiliado afi = null;
	String cuil=null, inte=null;

	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysAfiliados.ROL_ABM_NOVEDADES_SSS);

	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("cuil-titular");
 	headerNames.add("inte"); 
 	headerNames.add("apellido y nombre"); 
	headerNames.add("Seccional"); 
 	/* headerNames.add("periodo"); */
 	headerNames.add("cuit");
 	headerNames.add("Plan Actual");
 	headerNames.add("tipo-novedad");
 	headerNames.add("detalle-novedad");

 	
	if (showABMButtons) {
		headerNames.add("Ver Novedad");
		headerNames.add("Afiliado en Padrón");
	}
	
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
			NovedadEmpleadorTotal nove = (NovedadEmpleadorTotal) novedades.get(i);
			
			ResultRow row = new ResultRow(nove, nove.getCuil_titular(), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(WindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/afiliados/ver_detalle_novedad_empl");
  			rowURL.setParameter("cuil_titular", String.valueOf(nove.getCuil_titular() )); 
  			rowURL.setParameter("inte", String.valueOf(nove.getInte())); 
  			/*rowURL.setParameter("periodo", String.valueOf(nove.getPeriodo_As_Str())); */

  			
			row.addText(nove.getCuil_titular(),rowURL);
			row.addText(String.valueOf(nove.getInte()));	
			row.addText(nove.getApellido().trim() + ", " +nove.getNombre());
			row.addText(nove.getDescSeccional() != null ? nove.getDescSeccional() : "");			
			/* row.addText(sdf.format(nove.getPeriodo())); */
			row.addText(nove.getEmpresa_cuit());
			row.addText(nove.getPlan_actual_desc());
			row.addText(nove.getNovedad_desc());
			row.addText(nove.getPlan_que_corresponde_desc());
 						
			if (showABMButtons){
				StringBuffer sb1 = new StringBuffer();			
				sb1.append("&nbsp;<img alt=\"<liferay-ui:message key='dar-alta'/>\" src=\"");
		 		sb1.append(themeDisplay.getPathThemeImages());
		 		sb1.append("/common/view.png\" onClick=\"javascript:mostrarDetalleNovedadEmpl('");
		 		/* sb1.append(nove.getCuil_titular()+"','"+String.valueOf(nove.getInte()) +"','"+ nove.getPeriodo_As_Str());
		 		sb1.append("');\" />"); */
		 		sb1.append(nove.getCuil_titular()+"','"+String.valueOf(nove.getInte()) +"');\" />");
		 		row.addText(sb1.toString());
		 		
		 		/*StringBuffer sb2 = new StringBuffer();			
				sb2.append("&nbsp;<img alt=\"<liferay-ui:message key='dar-alta'/>\" src=\"");
		 		sb2.append(themeDisplay.getPathThemeImages());
		 		sb2.append("/common/edit.png\" onClick=\"javascript:editarAfiliado('");
		 		sb2.append(nove.getCuil_titular()+"','"+String.valueOf(nove.getInte()));
		 		sb2.append("');\" />");
		 		row.addText(sb2.toString());*/
		 		
		 		row.addJSP("left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/novedades/editar_afi_nov_empl.jsp");

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
	

