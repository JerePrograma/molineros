<%@ include file="/html/portlet/crm/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	List<ContactoCRM> historico = (List<ContactoCRM>) request.getAttribute(WebKeysAfiliados.HISTORICO_CONTACTOS);

	PortletURL portletURLHistoCont = renderResponse.createRenderURL();
	List<String> headerNamesHistoContac = new ArrayList<String>();

	headerNamesHistoContac.add("crm-contacto-nro");
	headerNamesHistoContac.add("crm-contacto-tipo"); 
	headerNamesHistoContac.add("crm-contacto-motivo");
	headerNamesHistoContac.add("crm-contacto-categoria");
	headerNamesHistoContac.add("crm-contacto-descripcion");
	headerNamesHistoContac.add("crm-contacto-estado");
	headerNamesHistoContac.add("crm-contacto-relac");
	headerNamesHistoContac.add("seguimiento-contacto");

	SearchContainer searchContainerHistoContact = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, 
			portletURLHistoCont,
			headerNamesHistoContac, 
			LanguageUtil.get(pageContext,"no-histo-cont-were-found"));

	if (null != historico) {
		int total = historico.size();
		searchContainerHistoContact.setTotal(total);
		List resultRows = searchContainerHistoContact.getResultRows();

		for (int i = 0; i < historico.size(); i++) {
			ContactoCRM histo = (ContactoCRM) historico.get(i);
			
			ResultRow row = null;
			row = new ResultRow(histo, String.valueOf(histo.getIdContacto()), i);

			row.addText(String.valueOf(histo.getIdContacto()));
			row.addText(histo.getTipo().getDescripcion());
			row.addText(histo.getMotivo().getDescripcion());
			row.addText(histo.getCategoria().getDescripcion());
			row.addText(histo.getDescripcion() );
			row.addText(histo.getEstado().name());
			row.addText(String.valueOf(histo.getIdCrmRelacionado()) );
			if(histo.getSeguimiento() != null && histo.getSeguimiento().size() >0 ){
				row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/crm/historico_contacto_seguim_search_result.jsp");
			}else{
				row.addText("");
			}
			
			resultRows.add(row);
		}
	}
	
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainerHistoContact%>" />
