<%@ include file="/html/portlet/utils/medicamentos/init.jsp" %>
<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();
	List<Medicamento> medicamentos=null;
	String troquel=renderRequest.getParameter("troquel");
	String nombre_medicamento=renderRequest.getParameter("nombre_medicamento");		
	//...	
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.POP_UP);
	portletURL.setParameter(Constants.CMD,"PopUp");

	List<String> headerNames = new ArrayList<String>();
	headerNames.add("Id");
	headerNames.add("Troquel");
	headerNames.add("Nombre");
	headerNames.add("Presentacion");
	headerNames.add("Cod Barras");
	headerNames.add("Precio");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.MAX_DELTA, portletURL
				, headerNames,
				LanguageUtil.get(pageContext, "no-medicamentos-were-found"));
	
	medicamentos = BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos(Integer.parseInt(troquel),nombre_medicamento);	
	//recupero coincidencias

	//Seteo el total de la lista.
	int total = medicamentos.size();
	//Si existe una sola coincidencia la plancho en los campos del parent
	if(total==1){
		Medicamento medicamento=(Medicamento) medicamentos.get(0);
		%>
			<script type="text/javascript">
				pasarParametrosAParentMd_edit("<%=medicamento.getTroquel()%>", "<%=medicamento.getNombre().trim()%>", "<%=medicamento.getId_medicamentoAsString()%>", "<%=medicamento.getPresentacion()%>");
			</script>
		<%
	//More de una coincidencia	
	}else {
	 	searchContainer.setTotal(total);
	 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
		List resultRows = searchContainer.getResultRows();
	 	for (int i = 0; i < medicamentos.size(); i++) {
	 		Medicamento medicamento=(Medicamento) medicamentos.get(i);
			ResultRow row = new ResultRow(medicamento.getTroquel(),medicamento.getNombre(), i);			
			// Name and short description
			StringBuilder s = new StringBuilder();
			s.append("<a href='javascript:pasarParametrosAParentMd_edit(\"");
			s.append(medicamento.getTroquel());
			s.append("\",\"");
			s.append(medicamento.getNombre().trim());
			s.append("\",\"");
			s.append(medicamento.getId_medicamentoAsString());
			s.append("\",\"");
			s.append(medicamento.getPresentacion()!=null?medicamento.getPresentacion().trim():"");
			s.append("\")'>");		
			s.append(medicamento.getId_medicamentoAsString());
			s.append("</a>");
			row.addText(s.toString());
			StringBuilder sb = new StringBuilder();
			sb.append("<a href='javascript:pasarParametrosAParentMd_edit(\"");
			sb.append(String.valueOf(medicamento.getTroquel()));
			sb.append("\",\"");
			sb.append(medicamento.getNombre().trim());
			sb.append("\",\"");
			sb.append(medicamento.getId_medicamentoAsString());
			sb.append("\",\"");
			sb.append(medicamento.getPresentacion()!=null?medicamento.getPresentacion().trim():"");
			sb.append("\")'>");			
			sb.append(String.valueOf(medicamento.getTroquel()));
			sb.append("</a>");
			row.addText(sb.toString());
			StringBuilder sb2 = new StringBuilder();
			sb2.append("<a href='javascript:pasarParametrosAParentMd_edit(\"");
			sb2.append(String.valueOf(medicamento.getTroquel()));
			sb2.append("\",\"");
			sb2.append(medicamento.getNombre().trim());
			sb2.append("\",\"");
			sb2.append(medicamento.getId_medicamentoAsString());
			sb2.append("\",\"");
			sb2.append(medicamento.getPresentacion()!=null?medicamento.getPresentacion().trim():"");
			sb2.append("\")'>");
			sb2.append(medicamento.getNombre().trim());
			sb2.append("</a>");
			row.addText(sb2.toString());
			StringBuilder sb3 = new StringBuilder();
			sb3.append("<a href='javascript:pasarParametrosAParentMd_edit(\"");
			sb3.append(String.valueOf(medicamento.getTroquel()));
			sb3.append("\",\"");
			sb3.append(medicamento.getNombre().trim());
			sb3.append("\",\"");
			sb3.append(medicamento.getId_medicamentoAsString());
			sb3.append("\",\"");
			sb3.append(medicamento.getPresentacion()!=null?medicamento.getPresentacion():"");
			sb3.append("\")'>");
			sb3.append(medicamento.getCod_barra()!=null?medicamento.getCod_barra().trim():"");
			sb3.append("</a>");
			row.addText(sb3.toString());
			StringBuilder sb4 = new StringBuilder();
			sb4.append("<a href='javascript:pasarParametrosAParentMd_edit(\"");
			sb4.append(String.valueOf(medicamento.getTroquel()));
			sb4.append("\",\"");
			sb4.append(medicamento.getNombre().trim());
			sb4.append("\",\"");
			sb4.append(medicamento.getId_medicamentoAsString());
			sb4.append("\",\"");
			sb4.append(medicamento.getPresentacion()!=null?medicamento.getPresentacion():"");
			sb4.append("\")'>");
			sb4.append(medicamento.getCod_barra()!=null?medicamento.getCod_barra().trim():"");
			sb4.append("</a>");
			row.addText(sb4.toString());
			StringBuilder sb5 = new StringBuilder();
			sb5.append("<a href='javascript:pasarParametrosAParentMd_edit(\"");
			sb5.append(String.valueOf(medicamento.getTroquel()));
			sb5.append("\",\"");
			sb5.append(medicamento.getNombre().trim());
			sb5.append("\",\"");
			sb5.append(medicamento.getId_medicamentoAsString());
			sb5.append("\",\"");
			sb5.append(medicamento.getPresentacion()!=null?medicamento.getPresentacion():"");
			sb5.append("\")'>");
			sb5.append(medicamento.getPrecio()!=null?medicamento.getPrecio().toString():"0");
			sb5.append("</a>");
			row.addText(sb5.toString());
			resultRows.add(row);
	 	}
		%>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
	}
%>