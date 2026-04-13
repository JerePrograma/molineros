<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	
	List<Prestador> prestadores=null;	
	
	if(null!=ps){  		
		prestadores=(List<Prestador>)ps.getAttribute(WebKeysLiquidaciones.PRESTADORES_EN_SESSION,PortletSession.APPLICATION_SCOPE);		
		if(null==prestadores || prestadores.size()==0){		  
		  prestadores=TraeListasServiceUtil.getPrestadores();
		  ps.setAttribute(WebKeysLiquidaciones.PRESTADORES_EN_SESSION,prestadores,PortletSession.APPLICATION_SCOPE);		  		
		}
	}
	//...
	
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("cuit");
	headerNames.add("prestador");				
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-prestadores-were-found"));
	//recupero coincidencias		
	if(null!=prestadores){
		String cuit=(String)renderRequest.getParameter("cuit");
		String id_prestador=(String)renderRequest.getParameter("id_prestador");
		String prestadorString=(String)renderRequest.getParameter("prestador");
		prestadores=ListUtils.traeCoincidenciasDeLista(prestadores,prestadorString,cuit);
		//Seteo el total de la lista.
	 	int total = prestadores.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			Prestador presUnica=(Prestador) prestadores.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParentPrestd("<%=presUnica.getCuit()%>", "<%=presUnica.getId_prestador()%>", "<%=presUnica.getDescripcion()%>");
				</script>				
			<%
		//More de una coincidencia	
		}else {
		 	searchContainer.setTotal(total);
		 	//prestadores = ListUtil.subList(prestadores, searchContainer.getStart(),searchContainer.getEnd());
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < prestadores.size(); i++) {
		 		Prestador prestador = (Prestador) prestadores.get(i);
				ResultRow row = new ResultRow(prestador.getCuit(),prestador.getDescripcion(), i);			
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParentPrestd(\"");
				sb.append(prestador.getCuit());
				sb.append("\",\"");
				sb.append(prestador.getId_prestador());				
				sb.append("\",\"");
				sb.append(prestador.getDescripcion());
				sb.append("\")'>");			
				sb.append(prestador.getCuit());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:pasarParametrosAParentPrestd(\"");
				sb2.append(prestador.getCuit());
				sb2.append("\",\"");
				sb2.append(prestador.getId_prestador());
				sb2.append("\",\"");
				sb2.append(prestador.getDescripcion());
				sb2.append("\")'>");
				sb2.append(prestador.getDescripcion());
				sb2.append("</a>");
				row.addText(sb2.toString());
				resultRows.add(row);
		 	}
		%>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
		}
	}
%>