<%@ include file="/html/portlet/crm/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%

    String portlet_name = ParamUtil.getString(request, "portlet_name");
    if(renderResponse.getNamespace().equals("_JUD_1_")){
	   portlet_name = "judicial";
    }else{
	   portlet_name = "afiliados";
    }

	//obtengo resultados
	List<DocumentoLegalCRM> reclamoCRMResults = (ArrayList<DocumentoLegalCRM>) request.getSession().getAttribute(WebKeysCrm.BUSQUEDA_DOC_LEGAL_RESULT);

	BusquedaContactoFiltro filtro = (BusquedaContactoFiltro) request.getSession().getAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS);

	boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysCrm.ROL_ABM_CRM_LEGALES);
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
	 
	headerNames.add("N° reclamo");
	headerNames.add("Fecha Alta");
	headerNames.add("Datos Contacto");
	headerNames.add("Apellido y Nombre Contacto");
	headerNames.add("Plan");
	headerNames.add("Plan Omint");
	headerNames.add("Tipo");
	headerNames.add("Motivo");
	headerNames.add("Usuario Alta");
	headerNames.add("Ver");
	if(showABMButtons){
		headerNames.add("Editar");
	}

	
	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-doc-legalcrm-search-were-found"));

//recupero coincidencias
if (null != reclamoCRMResults && reclamoCRMResults.size() > 0 ) {
	total = reclamoCRMResults.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	for (int i = 0; i < reclamoCRMResults.size(); i++) {

		DocumentoLegalCRM dlcrm = (DocumentoLegalCRM) reclamoCRMResults.get(i);
		
		ResultRow row = new ResultRow(dlcrm, String.valueOf(dlcrm.getId()), i);
		PortletURL rowURL = renderResponse.createRenderURL();
			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
//			rowURL.setParameter("struts_action","/afiliados/editar_crm_legales_entry");
			rowURL.setParameter("struts_action","/"+portlet_name+"/editar_crm_legales_entry");
			rowURL.setParameter("id", String.valueOf(dlcrm.getId()));
			rowURL.setParameter("view", "true"); 
		row.addText(String.valueOf(dlcrm.getId())); 
		row.addText(sdf.format(dlcrm.getAltaFecha()));
		if(dlcrm.getAfiliado() != null){
			row.addText(dlcrm.getAfiliado().getCuil_titular()+" / "+dlcrm.getAfiliado().getInteAsString());
			row.addText(dlcrm.getAfiliado().getApeNombre());
			row.addText(dlcrm.getAfiliado().getUltimo_plan().getDescripcion());
			if(dlcrm.getAfiliado().getUltimo_plan().getDescripcionOmint()!=null){
				row.addText(dlcrm.getAfiliado().getUltimo_plan().getDescripcionOmint());
			}else{
				row.addText("-");
			}
		}else{
			row.addText(dlcrm.getNoAfiliado().getDocumentoTipo() + " " + dlcrm.getNoAfiliado().getDocumentoNumero());
			row.addText(dlcrm.getNoAfiliado().getApellido().trim() + " " + dlcrm.getNoAfiliado().getNombre().trim());
			row.addText("-");
			row.addText("-");
		}
		row.addText(dlcrm.getTipo().getDescripcion()); 
		row.addText(dlcrm.getMotivo().getDescripcion());
		row.addText(dlcrm.getAltaUsr());
		
		StringBuilder sb= new StringBuilder();		
		sb.append("<img alt=\"<liferay-ui:message key='ver-reclamo'/>\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/view.png\" onClick=\"javascript:verCrmReclamo('");
		sb.append(dlcrm.getId());
		sb.append("');\" />");
		row.addText(sb.toString());	

		if(showABMButtons){ 
			
			StringBuilder sb1= new StringBuilder();		
			sb1.append("<img alt=\"<liferay-ui:message key='editar-reclamo'/>\" src=\"");
			sb1.append(themeDisplay.getPathThemeImages());
			sb1.append("/common/edit.png\" onClick=\"javascript:editaCrmDocumentoLegal('");
			sb1.append(dlcrm.getId());
			sb1.append("','");
			sb1.append(dlcrm.getAfiliado()!=null?dlcrm.getAfiliado().getCuil_titular():"");
			sb1.append("','");
			sb1.append(dlcrm.getAfiliado()!=null?dlcrm.getAfiliado().getInte():"");
			sb1.append("');\" />");
			row.addText(sb1.toString());
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
	<liferay-util:include page="/html/portlet/crm/paginador_reclamos_search_results.jsp">
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