<%@page import="ar.com.ospim.liquidaciones.beans.ReintegroList"%>
<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%
	//obtengo lista de session	

	List<ReintegroList> reintegrosPrestaciones = (ArrayList<ReintegroList>) request.getAttribute(WebKeysLiquidaciones.LISTAS_PRESTACIONES_REINTEGROS_RESULTADOS);
	List<ReintegroList> reintegrosFarmacias    = (ArrayList<ReintegroList>) request.getAttribute(WebKeysLiquidaciones.LISTAS_FARMACIAS_REINTEGROS_RESULTADOS);

	int total = 0;
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("Tipo");
	headerNames.add("Nro. Lista");
	headerNames.add("Seleccionar");
	
	SearchContainer searchContainerPrest = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"lista-reintegros-no-were-found"));

		
	//recupero coincidencias		
	if (null != reintegrosPrestaciones || null != reintegrosFarmacias) {
		
		total = reintegrosPrestaciones!=null?reintegrosPrestaciones.size():0;
		if(reintegrosFarmacias!=null){
			total += reintegrosFarmacias.size();
		
			reintegrosPrestaciones.addAll(reintegrosFarmacias);
		}
		
		searchContainerPrest.setTotal(total);
		List resultRows = searchContainerPrest.getResultRows();
		
		for (int i = 0; i < reintegrosPrestaciones.size(); i++) {
			ReintegroList reintPrest = (ReintegroList) reintegrosPrestaciones.get(i);
			ResultRow row = new ResultRow(reintPrest, String.valueOf(reintPrest.getNroLista()), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","liquidaciones/editar_orden_pago_entry");
 			rowURL.setParameter("id", String.valueOf(reintPrest.getNroLista()));
 			
 			row.addText(reintPrest.getTipo());									
 			row.addText(String.valueOf(reintPrest.getNroLista()));
 			
 			StringBuffer sb = new StringBuffer();
			sb.append("<input type='checkbox' name='reint_"+reintPrest.getTipo()+"-"+reintPrest.getNroLista());
			sb.append("' ");
			sb.append("id='reint_"+reintPrest.getTipo()+"-"+reintPrest.getNroLista());
			sb.append("' />"); //checked='checked'
			row.addText(sb.toString());
		
			resultRows.add(row);
		}
		
	}
	
%>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainerPrest %>" />

<%if (null != reintegrosPrestaciones || null != reintegrosFarmacias) { %>
<input type="button" value="<liferay-ui:message key="alta-orden-pago-from-lista" />" onClick="<portlet:namespace />selecOPFromLista();" /></td>
<input type="button" value="<liferay-ui:message key="crear-op-from-lista-pago-cuenta-tranf" />" onClick="<portlet:namespace />selecOPFromListaPagoAfiliado();" /></td>

<%} %>
<br/>

<%-- <%
SearchContainer searchContainerFarm = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE,
			portletURL, headerNames, LanguageUtil.get(pageContext,
					"lista-reintegros-no-were-found"));

		
	//recupero coincidencias		
	if (null != reintegrosFarmacias) {
		total = reintegrosFarmacias.size();
		searchContainerFarm.setTotal(total);
		List resultRows = searchContainerFarm.getResultRows();
		
		for (int i = 0; i < reintegrosFarmacias.size(); i++) {
			ReintegroList reintFarm = (ReintegroList) reintegrosFarmacias.get(i);
			ResultRow row = new ResultRow(reintFarm, String.valueOf(reintFarm.getNroLista()), i);			
			PortletURL rowURL = renderResponse.createRenderURL();
 			rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
 			rowURL.setParameter("struts_action","/liquidaciones/editar_orden_pago_entry");
 			rowURL.setParameter("id", String.valueOf(reintFarm.getNroLista()));
 			
 			row.addText(reintFarm.getTipo());									
 			row.addText(String.valueOf(reintFarm.getNroLista()));
 			
 			StringBuffer sb = new StringBuffer();
			sb.append("<input type='checkbox' name='reint_"+reintFarm.getTipo()+"-"+reintFarm.getNroLista());
			sb.append("' ");
			sb.append("id='reint_"+reintFarm.getTipo()+"-"+reintFarm.getNroLista());
			sb.append("' />"); //checked='checked'
			row.addText(sb.toString());
		
			resultRows.add(row);
		}
		
	}

%>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainerFarm %>" />
<br/> --%>
	

