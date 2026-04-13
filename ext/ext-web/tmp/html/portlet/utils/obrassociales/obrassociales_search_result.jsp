<%@ include file="/html/portlet/utils/obrassociales/init.jsp" %>
<script type="text/javascript">

function pasarParametrosAParentSecc(id, param) {
    jQuery("#<portlet:namespace />obra_social_ant").val(id);
    jQuery("#<portlet:namespace />obrasocial").val(param);
    jQuery("#<portlet:namespace />btnBuscarOS").hide();
    <portlet:namespace />cerrarOS();    
 }

</script>
<%
	//obtengo lista de session

	List<ObraSocialCampo> obrasSocialesAnteriores = (ArrayList<ObraSocialCampo>) portletSession
		.getAttribute(WebKeysAfiliados.OBRAS_SOCIALES_EN_SESSION,
			PortletSession.APPLICATION_SCOPE);
	
	if (obrasSocialesAnteriores == null) {
		obrasSocialesAnteriores = TraeListasServiceUtil.getObrasSocialesAnteriores();
	portletSession.setAttribute(
		WebKeysAfiliados.OBRAS_SOCIALES_EN_SESSION,
		obrasSocialesAnteriores,
		PortletSession.APPLICATION_SCOPE);
	}

	
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("obra-social-ant");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-obra-social-were-found"));
	//recupero coincidencias		
	if(null!=obrasSocialesAnteriores){
		String obrasocial=(String)renderRequest.getParameter("obrasocial");
		String idObrasocial=(String)renderRequest.getParameter("idObrasocial");
		if (idObrasocial != null && !idObrasocial.trim().equals("")){
			obrasocial = null;
		} else {
			idObrasocial  = null;
		}
		obrasSocialesAnteriores=ListUtils.traeCoincidenciasDeLista(obrasSocialesAnteriores,obrasocial,idObrasocial);
		//Seteo el total de la lista.
	 	int total = obrasSocialesAnteriores.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			ObraSocialCampo obraSocialCampo=(ObraSocialCampo) obrasSocialesAnteriores.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParentSecc("<%=obraSocialCampo.getId()%>", "<%=obraSocialCampo.getDescripcion()%>");
				</script>				
			<%
		//More de una coincidencia	
		}else {
		 	searchContainer.setTotal(total);
		 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < obrasSocialesAnteriores.size(); i++) {
		 		ObraSocialCampo os = (ObraSocialCampo) obrasSocialesAnteriores.get(i);
				ResultRow row = new ResultRow(os.getId(),os.getDescripcion(), i);			
				// Name and short description
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:pasarParametrosAParentSecc(\"");
				sb2.append(os.getId());
				sb2.append("\",\"");
				sb2.append(os.getDescripcion());
				sb2.append("\")'>");
				sb2.append(os.getDescripcion());
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

