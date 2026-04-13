<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects/>
	<script type="text/javascript">
 		 function imprimir( nro){
			window.location.href ="/odtservlet/?accion=cheque&numero="+nro ;
 		 }
		</script>
			<%
				List<Cheque> cheques = (ArrayList<Cheque>)renderRequest.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CHEQUES);
			
				PortletURL portletURL = renderResponse.createRenderURL();
				String orderByCol = ParamUtil.getString(request, "orderByCol");
				String orderByType = ParamUtil.getString(request, "orderByType");
		 		List<String> headerNames = new ArrayList<String>();
		 		headerNames.add("cuit");
		 		headerNames.add("nro");
		 		headerNames.add("cta-bcria");
		 		headerNames.add("importe");
		 		headerNames.add("a-nombre-de");
		 		headerNames.add("debito-credito");
		 		headerNames.add("fecha");
		 		headerNames.add("estado");
		 		headerNames.add("id-op");
		 		headerNames.add("baja-fecha");
				SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-cheques-were-found"));			
			
				if(null!=cheques){
	 				//Seteo el total de la lista.
				 	int total = cheques.size();
				 	searchContainer.setTotal(total);
				 	
	 				List resultRows = searchContainer.getResultRows();
				 	for (int i = 0; i < cheques.size(); i++) {
				 		Cheque cheque = (Cheque) cheques.get(i);
	 					ResultRow row = new ResultRow(cheque,cheque.getNumero().intValue(), i);
		 				row.addText(cheque.getCuit()!=null?cheque.getCuit():"");
		 				row.addText(cheque.getNumero().toString());
		 				row.addText(null!=cheque.getCuentaBancaria()&&null!=cheque.getCuentaBancaria().getDescripcion()?cheque.getCuentaBancaria().getDescripcion():"");		
		 				row.addText(cheque.getImporte().toString());
		 				row.addText(cheque.getANombreDe());
		 				row.addText(cheque.getDebitoCredito().toString());
		 				row.addText(cheque.getFechaAsString());
		 				row.addText(cheque.getEstado().getDescripcion());
		 				row.addText(cheque.getIdOp()>0?String.valueOf(cheque.getIdOp()):"");
		 				row.addText(cheque.getBaja_fechaAsString());
			 			resultRows.add(row);
				 	}
	 			}
 		%>
 		
	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />		
	
	
