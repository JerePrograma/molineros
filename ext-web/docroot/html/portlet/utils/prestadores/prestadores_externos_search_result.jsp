<%@ include file="/html/portlet/utils/prestadores/init.jsp" %>

<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	
	List<PrestadorExterno> prestadores=null;
	int id=ParamUtil.getInteger(renderRequest, "id_prestador", 0);
	String razon = ParamUtil.getString(request, "nombre_prestador");
	String mat_tipo = ParamUtil.getString(request, "mat_tipo");
	String mat_numero = ParamUtil.getString(request, "mat_numero");
	String cuit = ParamUtil.getString(request, "cuit");
	
//...	
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.POP_UP);
	portletURL.setParameter(Constants.CMD,"PopUp");

	List<String> headerNames = new ArrayList<String>();	
	headerNames.add("Matrícula Tipo");
	headerNames.add("Matrícula N�mero");
	headerNames.add("Cuit");
	headerNames.add("prestador");
	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.MAX_DELTA, portletURL
				, headerNames,
				LanguageUtil.get(pageContext, "no-prestadores-were-found"));
	
	prestadores=PrestadorExternoServiceUtil.getPrestadores(id,mat_tipo,mat_numero,razon, cuit);
	//recupero coincidencias

	//Seteo el total de la lista.
	int total = prestadores.size();
	//Si existe una sola coincidencia la plancho en los campos del parent
	if(total==1){
		PrestadorExterno prestador=(PrestadorExterno) prestadores.get(0);
		%>
			<script type="text/javascript">
				pasarParametrosAParentPd("<%=prestador.getTipo_matricula()%>", "<%=prestador.getNro_matricula()%>", "<%=prestador.getCuit()%>", "<%=prestador.getDescripcion()%>", "<%=prestador.getId_prestadorString()%>");
			</script>				
		<%
	//More de una coincidencia	
	}else {
	 	searchContainer.setTotal(total);
	 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
		List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < prestadores.size(); i++) {
	 		PrestadorExterno prestador=(PrestadorExterno) prestadores.get(i);
			ResultRow row = new ResultRow(prestador.getId_prestadorString(),prestador.getDescripcion(), i);			
			// Name and short description
			StringBuilder s = new StringBuilder();
			s.append("<a href='javascript:pasarParametrosAParentPd(\"");
			s.append(prestador.getTipo_matricula());
			s.append("\",\"");
			s.append(prestador.getNro_matricula());
			s.append("\",\"");			
			s.append(prestador.getCuit());
			s.append("\",\"");			
			s.append(prestador.getDescripcion());
			s.append("\",\"");			
			s.append(prestador.getId_prestadorString());
			s.append("\")'>");			
			s.append(prestador.getTipo_matricula());
			s.append("</a>");
			row.addText(s.toString());
			StringBuilder sb = new StringBuilder();
			sb.append("<a href='javascript:pasarParametrosAParentPd(\"");
			sb.append(prestador.getTipo_matricula());
			sb.append("\",\"");
			sb.append(prestador.getNro_matricula());
			sb.append("\",\"");			
			sb.append(prestador.getCuit());
			sb.append("\",\"");			
			sb.append(prestador.getDescripcion());
			sb.append("\",\"");			
			sb.append(prestador.getId_prestadorString());			
			sb.append("\")'>");
			sb.append(prestador.getNro_matricula());
			sb.append("</a>");
			row.addText(sb.toString());
			StringBuilder sb2 = new StringBuilder();
			sb2.append("<a href='javascript:pasarParametrosAParentPd(\"");
			sb2.append(prestador.getTipo_matricula());
			sb2.append("\",\"");
			sb2.append(prestador.getNro_matricula());
			sb2.append("\",\"");			
			sb2.append(prestador.getCuit());
			sb2.append("\",\"");			
			sb2.append(prestador.getDescripcion());
			sb2.append("\",\"");			
			sb2.append(prestador.getId_prestadorString());
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
	
%>