<%@ include file="/html/portlet/comprobantes/init.jsp" %>
<%
	String prefijo = ParamUtil.getString(request, "prefijo","");
	boolean farmaciaop = ParamUtil.getBoolean(request, "farmaciaop");
	int idOP=ParamUtil.getInteger(request,"idOP");
%>
<script type="text/javascript">
function pasarParametrosAParentSecc<%=prefijo%>(id, param, destino) {	
    jQuery("#<portlet:namespace />id_seccional<%=prefijo%>").val(id);
    jQuery("#<portlet:namespace />seccional<%=prefijo%>").val(param);
    jQuery("#<portlet:namespace />secc_seleccionada<%=prefijo%>").val("1");
    try {    	
        if (jQuery("#<portlet:namespace />id_seccional_r").val() == "") {
    		jQuery("#<portlet:namespace />id_seccional_r").val(id);    	
    		jQuery("#<portlet:namespace />seccional_r").val(param);
    		jQuery("#<portlet:namespace />destino_r").val(destino);    	    	
    		jQuery("#<portlet:namespace />secc_seleccionada_r").val("1");
        }
        jQuery("#<portlet:namespace />destino").val(destino);
    } catch (err) {
    }
    jQuery("#<portlet:namespace />btnBuscarSeccional<%=prefijo%>").hide();
    <portlet:namespace />cerrarSecc<%=prefijo%>();
 }

function pasarDestinoFarm<%=prefijo%>(id, param, destino,idOP) {	
	jQuery("#destino_"+idOP).val(destino);
	<portlet:namespace />cerrarSecc<%=prefijo%>();	    
}

</script>
<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	
	List<Seccional> seccionales=null;	
	
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
				SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-seccionales-were-found"));
	//recupero coincidencias		
	if(null!=seccionales){
		String id_seccional=(String)renderRequest.getParameter("id_seccional");
		//String seccionalString=(String)renderRequest.getParameter("seccional");
		seccionalString=(String)renderRequest.getParameter("seccional");
		seccionales=ListUtils.traeCoincidenciasDeLista(seccionales,seccionalString,id_seccional);
		//Seteo el total de la lista.
	 	int total = seccionales.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			Seccional seccUnica=(Seccional) seccionales.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParentSecc<%=prefijo%>("<%=seccUnica.getId()%>", "<%=seccUnica.getDescripcion()%>","<%=seccUnica.getDestino()%>");
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
				sb.append("<a href='javascript:");
				if(farmaciaop){
					sb.append("pasarDestinoFarm").append(prefijo).append("(\"");
				}else{
					sb.append("pasarParametrosAParentSecc").append(prefijo).append("(\"");
				}
				sb.append(seccional.getId());
				sb.append("\",\"");
				sb.append(seccional.getDescripcion());
				sb.append("\",\"");
				sb.append(seccional.getDestino());
				if(farmaciaop){
					sb.append("\",\"");
					sb.append(idOP);
				}
				sb.append("\")'>");			
				sb.append(seccional.getId());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:");
				if(farmaciaop){
					sb2.append("pasarDestinoFarm").append(prefijo).append("(\"");
				}else{
					sb2.append("pasarParametrosAParentSecc").append(prefijo).append("(\"");
				}
				sb2.append(seccional.getId());
				sb2.append("\",\"");
				sb2.append(seccional.getDescripcion());
				sb2.append("\",\"");
				sb2.append(seccional.getDestino());
				if(farmaciaop){
					sb2.append("\",\"");
					sb2.append(idOP);
				}
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

