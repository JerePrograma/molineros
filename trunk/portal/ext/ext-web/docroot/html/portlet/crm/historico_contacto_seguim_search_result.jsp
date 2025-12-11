<%@ include file="/html/portlet/crm/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	ResultRow row1 = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
	ContactoCRM crmContacto =(ContactoCRM) row1.getObject();
	ArrayList<DerivacionSeguimiento> seguimientos = (ArrayList<DerivacionSeguimiento>) crmContacto.getSeguimiento();

	PortletURL portletURLContSeg = renderResponse.createRenderURL();
	List<String> headerNamesContacSeg = new ArrayList<String>();

	headerNamesContacSeg.add("crm-seguim-contacto-alta-edi-sec-usu");
	headerNamesContacSeg.add("crm-seguim-contacto-alta-fec");

	SearchContainer searchContainerContactSeguim = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, 
			portletURLContSeg,
			null, /*headerNamesContacSeg*/ 
			LanguageUtil.get(pageContext,"no-histo-cont-were-found"));

	if (null != seguimientos) {
		int total = seguimientos.size();
		searchContainerContactSeguim.setTotal(total);
		List resultRowsS = searchContainerContactSeguim.getResultRows();

		for (int i = 0; i < seguimientos.size(); i++) {
			DerivacionSeguimiento seg = (DerivacionSeguimiento) seguimientos.get(i);
			
			ResultRow rowS = null;
			rowS = new ResultRow(seg, String.valueOf(seg.getId()), i);

			rowS.addText(seg.getDerivacionEdificio()+"/"+seg.getDerivacionSector()+"/"+seg.getDerivacionUsr() );
			rowS.addText(sdf3.format(seg.getAltaFecha()));
			
			resultRowsS.add(rowS);
		}
	}
	
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainerContactSeguim%>" />
