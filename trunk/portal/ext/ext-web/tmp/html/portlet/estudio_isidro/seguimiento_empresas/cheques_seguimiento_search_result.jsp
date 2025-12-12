<%@ include file="/html/portlet/estudio_isidro/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			

	 		boolean showABMButtons = PermissionUtil.userContainsRole(user,"ABM_CalculoDeuda");

			NumberFormat formatter = new DecimalFormat("$#0.00");
			SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
			Boolean fromBusquedaDeuda = (Boolean) renderRequest.getAttribute("fromBusquedaDeuda");
			//Si debe mostrarse el btn de agregar afiliado
			List<ReporteListadoValores> rechazos= (ArrayList<ReporteListadoValores>)portletSession.getAttribute(WebKeysEstudioIsidro.CHEQUES_RECHAZADOS);
			PortletURL portletURL = renderResponse.createRenderURL();				
			String orderByCol = ParamUtil.getString(request, "orderByCol");
			String orderByType = ParamUtil.getString(request, "orderByType");
			List<String> headerNames = new ArrayList<String>();
			headerNames.add("entidad");
			headerNames.add("banco");
			headerNames.add("numero");
			headerNames.add("importe");
			headerNames.add("fecha-recibido");
			headerNames.add("recibo");
			headerNames.add("fecha-deposito");
			headerNames.add("fecha-rechazo");
			headerNames.add("ver-recibo");		
			 		
			 						
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,200, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-cheques-were-found"));
				
					if(null!=rechazos){
				 								 	
				 				//Seteo el total de la lista.
					 	int total = rechazos.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < rechazos.size(); i++) {
					 		String javascriptString=null;
					 		ReporteListadoValores cheque = (ReporteListadoValores) rechazos.get(i);
					 		
				 			ResultRow row = new ResultRow(cheque, cheque.getIdOrdenPago(), i);
					 		row.addText(cheque.getEntidad()==WebKeysGlobal.OSPIM?"OSPIM":cheque.getEntidad()==WebKeysGlobal.UOMA?"UOMA":"AMTIMA");
					 		row.addText(cheque.getBanco());
					 		row.addText(cheque.getNroCheque().toString());
					 		row.addText(null!=cheque.getImporteCheque()?formatter.format(cheque.getImporteCheque()):"");
					 		row.addText(null!=cheque.getFechaRecibo()?sdf.format(cheque.getFechaRecibo()):"");					 		
					 		row.addText(null!=cheque.getNumero()?cheque.getNumero():"");
					 		row.addText(null!=cheque.getFechaDeposito()?sdf.format(cheque.getFechaDeposito()):"");
					 		row.addText(null!=cheque.getFechaRechazado()?sdf.format(cheque.getFechaRechazado()):"");
					 		
					 		StringBuilder sb01 = new StringBuilder();
				 			sb01.append("<a href='javascript:popupRecibo(\"");
				 			sb01.append(String.valueOf(String.valueOf(cheque.getIdRecibo()))).append("\",\",").append(cheque.getEntidad());										
							sb01.append("\")'>Ver Recibo</a>");
					 		row.addText(sb01.toString());
					 		resultRows.add(row);
					 	}
					 	
				 	}
if(null!=rechazos && rechazos.size()>0){
			%>
<fieldset class="block-labels">			
<legend><liferay-ui:message key="cheques-rechazados" /></legend>
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
</fieldset>
<%
}
			//Si debe mostrarse el btn de agregar afiliado
			List<ReporteListadoValores> reemplaRecha= (ArrayList<ReporteListadoValores>)portletSession.getAttribute(WebKeysEstudioIsidro.CHEQUES_REEMP_RECHAZADOS);
									
					searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,200, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-cheques-were-found"));
				
					if(null!=reemplaRecha){				 								 	
				 				//Seteo el total de la lista.
					 	int total = reemplaRecha.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < reemplaRecha.size(); i++) {
					 		String javascriptString=null;
					 		ReporteListadoValores cheque = (ReporteListadoValores) reemplaRecha.get(i);
					 		
				 			ResultRow row = new ResultRow(cheque, cheque.getIdOrdenPago(), i);
					 		row.addText(cheque.getEntidad()==WebKeysGlobal.OSPIM?"OSPIM":cheque.getEntidad()==WebKeysGlobal.UOMA?"UOMA":"AMTIMA");
					 		row.addText(cheque.getBanco());
					 		row.addText(cheque.getNroCheque().toString());
					 		row.addText(null!=cheque.getImporteCheque()?formatter.format(cheque.getImporteCheque()):"");
					 		row.addText(null!=cheque.getFechaRecibo()?sdf.format(cheque.getFechaRecibo()):"");					 		
					 		row.addText(null!=cheque.getNumero()?cheque.getNumero():"");
					 		row.addText(null!=cheque.getFechaDeposito()?sdf.format(cheque.getFechaDeposito()):"");
					 		row.addText(null!=cheque.getFechaRechazado()?sdf.format(cheque.getFechaRechazado()):"");
					 		StringBuilder sb01 = new StringBuilder();
				 			sb01.append("<a href='javascript:popupRecibo(\"");
				 			sb01.append(String.valueOf(String.valueOf(cheque.getIdRecibo()))).append("\",\",").append(cheque.getEntidad());										
							sb01.append("\")'>Ver Recibo</a>");
					 		row.addText(sb01.toString());
					 		resultRows.add(row);
					 	}
					 	
				 	}
if(null!=reemplaRecha && reemplaRecha.size()>0){
			%>
<fieldset class="block-labels">			
<legend><liferay-ui:message key="cheques-reempla-rechazo" /></legend>
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
</fieldset>

<%
}
			//Si debe mostrarse el btn de agregar afiliado
			List<ReporteListadoValores> canjeSinDepo= (ArrayList<ReporteListadoValores>)portletSession.getAttribute(WebKeysEstudioIsidro.CHEQUES_CANJEADOS_SIN_DEPO);
									
					searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,200, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-cheques-were-found"));
				
					if(null!=canjeSinDepo){				 								 	
				 				//Seteo el total de la lista.
					 	int total = canjeSinDepo.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < canjeSinDepo.size(); i++) {
					 		String javascriptString=null;
					 		ReporteListadoValores cheque = (ReporteListadoValores) canjeSinDepo.get(i);
					 		
				 			ResultRow row = new ResultRow(cheque, cheque.getIdOrdenPago(), i);
					 		row.addText(cheque.getEntidad()==WebKeysGlobal.OSPIM?"OSPIM":cheque.getEntidad()==WebKeysGlobal.UOMA?"UOMA":"AMTIMA");
					 		row.addText(cheque.getBanco());
					 		row.addText(cheque.getNroCheque().toString());
					 		row.addText(null!=cheque.getImporteCheque()?formatter.format(cheque.getImporteCheque()):"");
					 		row.addText(null!=cheque.getFechaRecibo()?sdf.format(cheque.getFechaRecibo()):"");					 		
					 		row.addText(null!=cheque.getNumero()?cheque.getNumero():"");
					 		row.addText(null!=cheque.getFechaDeposito()?sdf.format(cheque.getFechaDeposito()):"");
					 		row.addText(null!=cheque.getFechaRechazado()?sdf.format(cheque.getFechaRechazado()):"");
					 		StringBuilder sb01 = new StringBuilder();
				 			sb01.append("<a href='javascript:popupRecibo(\"");
				 			sb01.append(String.valueOf(String.valueOf(cheque.getIdRecibo()))).append("\",\",").append(cheque.getEntidad());										
							sb01.append("\")'>Ver Recibo</a>");
					 		row.addText(sb01.toString());
					 		resultRows.add(row);
					 	}
					 	
				 	}
if(null!=canjeSinDepo && canjeSinDepo.size()>0){					
			%>
<fieldset class="block-labels">			
<legend><liferay-ui:message key="cheques-canje-sin-depo" /></legend>
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
</fieldset>

<%
}
			//Si debe mostrarse el btn de agregar afiliado
			List<ReporteListadoValores> chequesCartera= (ArrayList<ReporteListadoValores>)portletSession.getAttribute(WebKeysEstudioIsidro.CHEQUES_CARTERA);
									
					searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,200, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-cheques-were-found"));
				
					if(null!=chequesCartera){				 								 	
				 				//Seteo el total de la lista.
					 	int total = chequesCartera.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < chequesCartera.size(); i++) {
					 		String javascriptString=null;
					 		ReporteListadoValores cheque = (ReporteListadoValores) chequesCartera.get(i);
					 		
				 			ResultRow row = new ResultRow(cheque, cheque.getIdOrdenPago(), i);
					 		row.addText(cheque.getEntidad()==WebKeysGlobal.OSPIM?"OSPIM":cheque.getEntidad()==WebKeysGlobal.UOMA?"UOMA":"AMTIMA");
					 		row.addText(cheque.getBanco());
					 		row.addText(cheque.getNroCheque().toString());
					 		row.addText(null!=cheque.getImporteCheque()?formatter.format(cheque.getImporteCheque()):"");
					 		row.addText(null!=cheque.getFechaRecibo()?sdf.format(cheque.getFechaRecibo()):"");
					 		row.addText(null!=cheque.getNumero()?cheque.getNumero():"");
					 		row.addText(null!=cheque.getFechaDeposito()?sdf.format(cheque.getFechaDeposito()):"");
					 		row.addText(null!=cheque.getFechaRechazado()?sdf.format(cheque.getFechaRechazado()):"");
					 							 							 		
					 		StringBuilder sb01 = new StringBuilder();
				 			sb01.append("<a href='javascript:popupRecibo(\"");
				 			sb01.append(String.valueOf(String.valueOf(cheque.getIdRecibo()))).append("\",\",").append(cheque.getEntidad());										
							sb01.append("\")'>Ver Recibo</a>");
					 		row.addText(sb01.toString());
					 		resultRows.add(row);
					 	}
					 	
				 	}
if(null!=chequesCartera && chequesCartera.size()>0){					
			%>
<fieldset class="block-labels">			
<legend><liferay-ui:message key="cheques-cartera" /></legend>
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
</fieldset>
<%}%>

<%if(null==rechazos && null==reemplaRecha && null==canjeSinDepo && null==chequesCartera){%>
No existen cheques para esta empresa
<%} %>
