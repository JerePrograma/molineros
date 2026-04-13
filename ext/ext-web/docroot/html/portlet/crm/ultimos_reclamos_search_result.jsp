<%@ include file="/html/portlet/crm/init.jsp" %>
<%
	//obtengo lista del request	
	List<DocumentoLegalCRM> reclamos = null;
	reclamos = (List<DocumentoLegalCRM>) request.getAttribute(WebKeysCrm.CRM_ULTIMOS_DOCUM_LEGAL);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	int total = 0;
	/* boolean esView = (Boolean)request.getAttribute("view"); */
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("N° contacto");
	headerNames.add("Fecha Notificación");
	headerNames.add("Tipo");
	headerNames.add("Motivo");
	headerNames.add("Expediente");
	headerNames.add("Fecha Vencimiento");
	headerNames.add("Usuario Alta");
	headerNames.add("Fecha Alta");
	headerNames.add("Ver");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"no-doc-legal-crm-were-found"));
		
	//recupero coincidencias		
	if (null != reclamos && reclamos.size() > 0) {
		total = reclamos.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < reclamos.size(); i++) {
			DocumentoLegalCRM dl = (DocumentoLegalCRM) reclamos.get(i);
			ResultRow row = new ResultRow(dl, String.valueOf(dl.getId()), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/afiliados/editar_crm_legales_entry");
 			rowURL.setParameter("id", String.valueOf(dl.getId()));
 			rowURL.setParameter("view", "true"); 
 			rowURL.setParameter("esPopup", "si"); 
			row.addText(String.valueOf(dl.getId())); 
			row.addText(dl.getFechaNotificacion()!=null?sdf.format(dl.getFechaNotificacion()):"");
			row.addText(dl.getTipo().getDescripcion()); 
			row.addText(dl.getMotivo().getDescripcion());
			row.addText(dl.getExpediente());
			row.addText(dl.getFechaVencimiento()!=null?sdf.format(dl.getFechaVencimiento()):"");
			row.addText(dl.getAltaUsr());
			row.addText(dl.getAltaFecha()!=null?sdf1.format(dl.getAltaFecha()):"");
			
			StringBuilder sb= new StringBuilder();		
			sb.append("&nbsp;<img alt=\"<liferay-ui:message key='ver-reclamo'/>\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/common/view.png\" onClick=\"javascript:verCrmDocumentoLegal('");
			sb.append(dl.getId());
			sb.append("');\" />");
			row.addText(sb.toString());	

			resultRows.add(row);
		}
	}
%>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />

