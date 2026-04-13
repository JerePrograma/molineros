<%@ include file="/html/portlet/afiliados/init.jsp" %>
<script type="text/javascript">

function pasarParametrosAParent(id, param, libro_p, tomo_p) {
    jQuery("#<portlet:namespace />id_delegacion").val(id);
    jQuery("#<portlet:namespace />delegacion").val(param);
    jQuery("#<portlet:namespace />deleg_seleccionada").val("1");
    jQuery("#<portlet:namespace />btnBuscarDelegacion").hide();
/*     var libro_p = jQuery("#<portlet:namespace />deleg_libro").val();
    var tomo_p = jQuery("#<portlet:namespace />deleg_tomo").val(); */
    jQuery("#<portlet:namespace />libro").val(libro_p);
    jQuery("#<portlet:namespace />tomo").val(tomo_p);
    <portlet:namespace />cerrar();    
 }

</script>
<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	List<Delegacion> delegaciones=null;	
	
	if(null!=ps){  		
		delegaciones=(List<Delegacion>)ps.getAttribute(WebKeysAfiliados.DELEGACIONES_EN_SESSION,PortletSession.APPLICATION_SCOPE);		
		if(null==delegaciones || delegaciones.size()==0){		  
		  delegaciones=TraeListasServiceUtil.getDelegaciones();
		  ps.setAttribute(WebKeysAfiliados.DELEGACIONES_EN_SESSION,delegaciones,PortletSession.APPLICATION_SCOPE);		  		
		}	  
	}
	//...
	
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("id");
	headerNames.add("delegacion");				
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-delegaciones-were-found"));
	//recupero coincidencias		
	if(null!=delegaciones){
		String id_delegacion=(String)renderRequest.getParameter("id_delegacion");
		String delegacionString=(String)renderRequest.getParameter("delegacion");
		delegaciones=ListUtils.traeCoincidenciasDeLista(delegaciones,delegacionString,id_delegacion);
		//Seteo el total de la lista.
	 	int total = delegaciones.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			Delegacion delegUnica=(Delegacion) delegaciones.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParent("<%=delegUnica.getId()%>", "<%=delegUnica.getDescripcion()%>","<%=delegUnica.getLibro()%>","<%=delegUnica.getTomo()%>");
				</script>				
			<%
		//More de una coincidencia	
		}else {
		 	searchContainer.setTotal(total);
		 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < delegaciones.size(); i++) {
		 		Delegacion delegacion = (Delegacion) delegaciones.get(i);
				ResultRow row = new ResultRow(delegacion.getId(),delegacion.getDescripcion(), i);			
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParent(\"");
				sb.append(delegacion.getId());
				sb.append("\",\"");
				sb.append(delegacion.getDescripcion());
				sb.append("\",\"");
				sb.append(delegacion.getLibro());
				sb.append("\",\"");
				sb.append(delegacion.getTomo());
				sb.append("\")'>");			
				sb.append(delegacion.getId());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:pasarParametrosAParent(\"");
				sb2.append(delegacion.getId());
				sb2.append("\",\"");
				sb2.append(delegacion.getDescripcion());
				sb2.append("\",\"");
				sb2.append(delegacion.getLibro());
				sb2.append("\",\"");
				sb2.append(delegacion.getTomo());
				sb2.append("\")'>");
				sb2.append(delegacion.getDescripcion());
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

