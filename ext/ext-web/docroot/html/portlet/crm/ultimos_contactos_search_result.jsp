<%@ include file="/html/portlet/crm/init.jsp" %>
<%
	//obtengo lista del request	
	List<ContactoCRM> contactos = null;
	contactos = (List<ContactoCRM>) request.getAttribute(WebKeysCrm.CRM_ULTIMOS_CONTACTOS);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	int total = 0;
	/* boolean esView = (Boolean)request.getAttribute("view"); */
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("N° contacto");
	headerNames.add("Fecha");
	headerNames.add("Tipo");
	headerNames.add("Categoría");
	headerNames.add("Motivo");
	headerNames.add("Estado");
	headerNames.add("Usuario Alta");
	headerNames.add("Ver");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-contactos-crm-were-found"));
		
	//recupero coincidencias		
	if (null != contactos && contactos.size() > 0) {
		total = contactos.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < contactos.size(); i++) {
			ContactoCRM c = (ContactoCRM) contactos.get(i);
			ResultRow row = new ResultRow(c, String.valueOf(c.getId()), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(WindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/afiliados/editar_contacto_entry");
 			rowURL.setParameter("id_serialcontacto", String.valueOf(c.getId()));
 			rowURL.setParameter("view", "true"); 
			row.addText(String.valueOf(c.getIdContacto())); 
			row.addText(sdf.format(c.getAltaFecha()));
			row.addText(c.getTipo().getDescripcion()); 
			row.addText(c.getCategoria().getDescripcion());
			row.addText(c.getMotivo().getDescripcion());
			row.addText(c.getEstado().name());
			row.addText(c.getAltaUsr());
			
			
			StringBuilder sb= new StringBuilder();		
			sb.append("&nbsp;<img alt=\"<liferay-ui:message key='ver-contacto'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/common/view.png\" onClick=\"javascript:verCrmContacto('");
			sb.append(c.getId());
			sb.append("');\" />");
			row.addText(sb.toString());	

			resultRows.add(row);
		}
	}
%>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />

