<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<script type="text/javascript">

function pasarParametrosAParentSeccRein(id, param) {	
    jQuery("#<portlet:namespace />id_seccional_r").val(id);
    jQuery("#<portlet:namespace />seccional_r").val(param);
    jQuery("#<portlet:namespace />secc_seleccionada_r").val("1");    
    jQuery("#<portlet:namespace />btnBuscarSeccional_r").hide();
    <portlet:namespace />cerrarSeccRein();    
 }

</script>
<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	List<Seccional> seccionales=null;	
	
	if(null!=ps){  		
		seccionales=(List<Seccional>)ps.getAttribute(WebKeysLiquidaciones.SECCIONALES_EN_SESSION,PortletSession.APPLICATION_SCOPE);		
		if(null==seccionales || seccionales.size()==0){		  
		  seccionales=TraeListasServiceUtil.getSeccionales();
		  ps.setAttribute(WebKeysLiquidaciones.SECCIONALES_EN_SESSION,seccionales,PortletSession.APPLICATION_SCOPE);		  		
		}	  
	}
	//...
	
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("id");
	headerNames.add("seccional");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-seccionales-were-found"));
	//recupero coincidencias		
	if(null!=seccionales){
		String id_seccional=(String)renderRequest.getParameter("id_seccional");
		String seccionalString=(String)renderRequest.getParameter("seccional");
		seccionales=ListUtils.traeCoincidenciasDeLista(seccionales,seccionalString,id_seccional);
		//Seteo el total de la lista.
	 	int total = seccionales.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			Seccional seccUnica=(Seccional) seccionales.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParentSeccRein("<%=seccUnica.getId()%>", "<%=seccUnica.getDescripcion()%>");
				</script>				
			<%
		//More de una coincidencia	
		}else {
		 	searchContainer.setTotal(total);
		 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < seccionales.size(); i++) {
		 		Seccional seccional = (Seccional) seccionales.get(i);
				ResultRow row = new ResultRow(seccional.getId(),seccional.getDescripcion(), i);			
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParentSeccRein(\"");
				sb.append(seccional.getId());
				sb.append("\",\"");
				sb.append(seccional.getDescripcion());
				sb.append("\")'>");			
				sb.append(seccional.getId());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:pasarParametrosAParentSeccRein(\"");
				sb2.append(seccional.getId());
				sb2.append("\",\"");
				sb2.append(seccional.getDescripcion());
				sb2.append("\")'>");
				sb2.append(seccional.getDescripcion());
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

