<%@ include file="/html/portlet/utils/prestadores/init.jsp" %>

<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	
	List<Prestador> prestadores=null;
	int id=ParamUtil.getInteger(renderRequest, "id_prestador", 0);
	String cuit=renderRequest.getParameter("cuit_prestador");
	String razon=renderRequest.getParameter("nombre_prestador");
	String ext=renderRequest.getParameter("ext");
	boolean solo_vigentes = ParamUtil.getBoolean(request, "solo_vigentes");
	boolean solo_hospitales = ParamUtil.getBoolean(request, "solo_hospitales");
	if (ext == null || (ext != null && ext.length() == 0)) {
		ext = "";
	}
	//...	
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.POP_UP);
	portletURL.setParameter(Constants.CMD,"PopUp");

	List<String> headerNames = new ArrayList<String>();
	headerNames.add("cod-prestador");
	headerNames.add("cuit");
	headerNames.add("prestador");
	headerNames.add("Cód.Hospital");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.MAX_DELTA, portletURL
				, headerNames,
				LanguageUtil.get(pageContext, "no-prestadores-were-found"));
	
	prestadores=PrestadorServiceUtil.getPrestadores(id,cuit,razon, solo_vigentes,solo_hospitales);
	//recupero coincidencias

	//Seteo el total de la lista.
	int total = prestadores.size();
	//Si existe una sola coincidencia la plancho en los campos del parent
	if(total==1){
		Prestador prestador=(Prestador) prestadores.get(0);
		%>
			<script type="text/javascript">				
				pasarParametrosAParentPd<%=ext%>("<%=prestador.getCuit().trim()%>", "<%=prestador.getDescripcion().trim()%>", "<%=prestador.getId_prestadorString()%>");
			</script>				
		<%
	//More de una coincidencia	
	}else {
	 	searchContainer.setTotal(total);
	 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
		List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < prestadores.size(); i++) {
	 		Prestador prestador=(Prestador) prestadores.get(i);
			ResultRow row = new ResultRow(prestador.getCuit(),prestador.getDescripcion(), i);			
			// Name and short description
			StringBuilder s = new StringBuilder();
			s.append("<a href='javascript:pasarParametrosAParentPd"+ext+"(\"");
			s.append(prestador.getCuit());
			s.append("\",\"");
			s.append(prestador.getDescripcion());
			s.append("\",\"");
			s.append(prestador.getId_prestador());
			s.append("\")'>");			
			s.append(prestador.getId_prestadorString());
			s.append("</a>");
			row.addText(s.toString());
			StringBuilder sb = new StringBuilder();
			sb.append("<a href='javascript:pasarParametrosAParentPd"+ext+"(\"");
			sb.append(prestador.getCuit());
			sb.append("\",\"");
			sb.append(prestador.getDescripcion());
			sb.append("\",\"");
			sb.append(prestador.getId_prestador());
			sb.append("\")'>");			
			sb.append(prestador.getCuit());
			sb.append("</a>");
			row.addText(sb.toString());
			StringBuilder sb2 = new StringBuilder();
			sb2.append("<a href='javascript:pasarParametrosAParentPd"+ext+"(\"");
			sb2.append(prestador.getCuit());
			sb2.append("\",\"");
			sb2.append(prestador.getDescripcion());
			sb2.append("\",\"");
			sb2.append(prestador.getId_prestador());
			sb2.append("\")'>");			
			sb2.append(prestador.getDescripcion());
			sb2.append("</a>");
			row.addText(sb2.toString());
			
			StringBuilder sb3 = new StringBuilder();
			sb3.append("<a href='javascript:pasarParametrosAParentPd"+ext+"(\"");
			sb3.append(prestador.getCuit());
			sb3.append("\",\"");
			sb3.append(prestador.getDescripcion());
			sb3.append("\",\"");
			sb3.append(prestador.getId_prestador());
			sb3.append("\")'>");			
			sb3.append(prestador.getCodigoHospital());
			sb3.append("</a>");
			row.addText(sb3.toString());
			resultRows.add(row);
	 	}
		%>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
	}
	
%>

