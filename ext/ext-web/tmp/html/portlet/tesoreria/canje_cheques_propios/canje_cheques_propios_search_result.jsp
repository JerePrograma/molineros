<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio" %>


<liferay-ui:error exception="<%= DuplicateNumeroChequeException.class %>" message="duplicate-cheque" />
	
<portlet:defineObjects />
<%

	String portlet_name = ParamUtil.getString(request, "portlet_name");
		
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}
	
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}
	List<CanjeChequePropio> canjes = (List<CanjeChequePropio>) request
		.getAttribute(WebKeysTesoreria.CANJE_CHEQUES_RESULT);

	PortletURL portletURLTercerizadora = renderResponse
			.createRenderURL();
	List<String> headerNamesTercerizadora = new ArrayList<String>();
	headerNamesTercerizadora.add("numero");
	headerNamesTercerizadora.add("fecha");
	headerNamesTercerizadora.add("cheques-pertenecientes-op");
	headerNamesTercerizadora.add("op-generada");
	headerNamesTercerizadora.add("");

	SearchContainer searchContainer = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURLTercerizadora,
			headerNamesTercerizadora, LanguageUtil.get(pageContext,
					"no-canjes-were-found"));

	
	int total = 0;
	BigDecimal importe = BigDecimal.ZERO;
	if (null != canjes) {
		 total = canjes.size();
		List resultRowsInspector = searchContainer.getResultRows();
		for (int i = 0; i < canjes.size(); i++) {
			CanjeChequePropio canje = canjes.get(i);
				PortletURL rowURL = renderResponse.createRenderURL();		 				
				rowURL.setWindowState(WindowState.MAXIMIZED);		 				
				rowURL.setParameter("struts_action","/"+portlet_name+"/canje_cheques_propios");
				rowURL.setParameter("canje_id", String.valueOf(canje.getId()));
				ResultRow row = new ResultRow(canje, canje.getId(), i);
				row.addText(String.valueOf(canje.getId()), rowURL);
				row.addText(canje.getAlta_fechaAsString(), rowURL);
				row.addText(canje.getOrdenPago().getNumeroOP(), rowURL);
				row.addText(canje.getOrdenPagoNueva().getNumeroOP(), rowURL);
				row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/tesoreria/canje_cheques_propios/boton_editar_canje_cheque.jsp");
				resultRowsInspector.add(row);
		}
	}
		searchContainer.setTotal(total);
%>


<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainer%>" />
