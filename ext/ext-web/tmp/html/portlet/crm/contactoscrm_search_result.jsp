<%@ include file="/html/portlet/crm/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%
	//obtengo resultados
	List<ContactoCRM> contactoCRMResults = (ArrayList<ContactoCRM>) request.getSession().getAttribute(WebKeysCrm.BUSQUEDA_CONTACTOS_RESULT);

	BusquedaContactoFiltro filtro = (BusquedaContactoFiltro) request.getSession().getAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS);

	boolean showCrmAuditoria = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_CRM_Auditoria); 

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
	 
	headerNames.add("N° contacto");
	headerNames.add("Fecha");
	headerNames.add("Datos Contacto");
	headerNames.add("Apellido y Nombre Contacto");
	headerNames.add("Plan");
	/* headerNames.add("Plan Omint"); */
	headerNames.add("Tipo");
	headerNames.add("Categoría");
	headerNames.add("Motivo");
	headerNames.add("Estado");
	headerNames.add("Usuario Alta");
	headerNames.add("Resolución");
	headerNames.add("Ver");
	if(showCrmAuditoria){
		headerNames.add("Eficacia");
	}

	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-contactoscrm-search-were-found"));

//recupero coincidencias
if (null != contactoCRMResults && contactoCRMResults.size() > 0 ) {
	total = contactoCRMResults.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	for (int i = 0; i < contactoCRMResults.size(); i++) {

		ContactoCRM ccrm = (ContactoCRM) contactoCRMResults.get(i);
		
		ResultRow row = new ResultRow(ccrm, String.valueOf(ccrm.getId()), i);
		PortletURL rowURL = renderResponse.createRenderURL();
			rowURL.setWindowState(WindowState.MAXIMIZED);
			if(renderResponse.getNamespace().equals("_CAI_1_")){
			  rowURL.setParameter("struts_action","/cai/editar_contacto_entry");
			}else{
			  rowURL.setParameter("struts_action","/afiliados/editar_contacto_entry");	
			}
			rowURL.setParameter("id_serialcontacto", String.valueOf(ccrm.getId()));
			rowURL.setParameter("view", "true"); 
		row.addText(String.valueOf(ccrm.getIdContacto())); 
		row.addText(sdf.format(ccrm.getAltaFecha()));
		if(ccrm.getAfiliado() != null){
			row.addText(ccrm.getAfiliado().getCuil_titular()+" / "+ccrm.getAfiliado().getInteAsString());
			row.addText(ccrm.getAfiliado().getApeNombre());
			row.addText(ccrm.getAfiliado().getUltimo_plan().getDescripcion());
			/* if(ccrm.getAfiliado().getUltimo_plan().getDescripcionOmint()!=null){
				row.addText(ccrm.getAfiliado().getUltimo_plan().getDescripcionOmint());
			}else{
				row.addText("-");
			} */
		}else if(ccrm.getContactoSeccional() != null){
			
			row.addText(ccrm.getContactoSeccional().getSeccional().getDescripcion());
			row.addText(ccrm.getContactoSeccional().getNombreApe());
			row.addText(ccrm.getContactoSeccional().getCargoDescripcion() );
			row.addText("-");
			
		}else if(ccrm.getNoAfiliado() != null){
			row.addText(ccrm.getNoAfiliado().getDocumentoTipo() + " " + ccrm.getNoAfiliado().getDocumentoNumero());
			row.addText(ccrm.getNoAfiliado().getApellido().trim() + " " + ccrm.getNoAfiliado().getNombre().trim());
			row.addText("-");
			
		}else{
			row.addText("-");
			row.addText("-");
			row.addText("- a definir -");
		}
		row.addText(ccrm.getTipo().getDescripcion()); 
		row.addText(ccrm.getCategoria().getDescripcion());
		row.addText(ccrm.getMotivo().getDescripcion());
		row.addText(ccrm.getEstado().name());
		row.addText(ccrm.getAltaUsr());
		if(ccrm.getEstado().compareTo(ContactoCRM.ESTADOS.CERRADO) == 0 ){
			row.addText(String.valueOf(ccrm.getTiempoResolucion()));
		}else{
			row.addText("-");
		}
		
		
		StringBuilder sb= new StringBuilder();		
		sb.append("<img alt=\"<liferay-ui:message key='ver-contacto'/>\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/view.png\" onClick=\"javascript:verCrmContacto('");
		sb.append(ccrm.getId());
		sb.append("');\" />");
		row.addText(sb.toString());	

		if(showCrmAuditoria){ 
			
			if((ccrm.getEstado().compareTo(ContactoCRM.ESTADOS.CERRADO) == 0)  && ccrm.getEficacia().getId() == 0 ){ 
		
				StringBuilder sb1= new StringBuilder();		
				sb1.append("<img alt=\"<liferay-ui:message key='verifica-efic'/>\" src=\"");
				sb1.append(themeDisplay.getPathThemeImages());
				sb1.append("/common/add_article.png\" onClick=\"javascript:cargaVerificacionEficacia('");
				sb1.append(ccrm.getIdContacto());
				sb1.append("');\" />");
				row.addText(sb1.toString());
			}else if((ccrm.getEstado().compareTo(ContactoCRM.ESTADOS.CERRADO) == 0)  && ccrm.getEficacia().getId() > 0 ){ 
				StringBuilder sb2= new StringBuilder();		
				sb2.append("<img alt=\"<liferay-ui:message key='verifica-efic'/>\" src=\"");
				sb2.append(themeDisplay.getPathThemeImages());
				sb2.append("/common/tag.png\" />");
				row.addText(sb2.toString());	
			}else{
				row.addText("");
			}
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
	<liferay-util:include page="/html/portlet/crm/paginador_contactos_search_results.jsp">
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