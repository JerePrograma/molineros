<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<script type="text/javascript">
<%String prefijo = request.getParameter("prefijo");%>
function pasarParametrosAParent(id, param) {	
//    jQuery("#<portlet:namespace />id_patologia<%=prefijo!=null?prefijo:""%>").val(id);
    jQuery("#<portlet:namespace />patologiaSeguimiento<%=prefijo!=null?prefijo:""%>").val(id);
    jQuery("#<portlet:namespace />patologia<%=prefijo!=null?prefijo:""%>").val(param);
    jQuery("#<portlet:namespace />patologia_seleccionada<%=prefijo!=null?prefijo:""%>").val("1");
    jQuery("#<portlet:namespace />btnBuscarPatologia").hide();
    <portlet:namespace />cerrarPatologia<%=prefijo%>();
    
    try{
    	<portlet:namespace />actualizaDrogas();
    }catch(err){}
 }

</script>
<%
	
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("id");
	headerNames.add("patología");				
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-patologia-were-found"));
	List<DrogaPatologia> patologias=SeguimientoSurServiceUtil.traePatologias(0);
	//recupero coincidencias		
	if(null!=patologias){
		String id_patologia=(String)renderRequest.getParameter("id_patologia");
		String patologiaString=(String)renderRequest.getParameter("patologia");
		patologias=ListUtils.traeCoincidenciasDeLista(patologias,patologiaString,id_patologia);
		//Seteo el total de la lista.
	 	int total = patologias.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			DrogaPatologia patologiaUnica=(DrogaPatologia) patologias.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParent("<%=patologiaUnica.getId()%>", "<%=patologiaUnica.getPatologia()%>");
				</script>				
			<%
		//More de una coincidencia	
		}else {
		 	searchContainer.setTotal(total);
		 	patologias = ListUtil.subList(patologias, searchContainer.getStart(),searchContainer.getEnd());
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < patologias.size(); i++) {
		 		DrogaPatologia patologia = (DrogaPatologia) patologias.get(i);
				ResultRow row = new ResultRow(patologia.getId(),patologia.getPatologia(), i);			
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParent(\"");
				sb.append(patologia.getId());
				sb.append("\",\"");
				sb.append(patologia.getPatologia());
				sb.append("\")'>");			
				sb.append(patologia.getId());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:pasarParametrosAParent(\"");
				sb2.append(patologia.getId());
				sb2.append("\",\"");
				sb2.append(patologia.getPatologia());
				sb2.append("\")'>");
				sb2.append(patologia.getPatologia());
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

