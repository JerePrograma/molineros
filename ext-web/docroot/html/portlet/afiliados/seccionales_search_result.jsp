<%@ include file="/html/portlet/afiliados/init.jsp" %>

<%
	PortletURL portletURL = renderResponse.createRenderURL();
	portletURL.setWindowState(LiferayWindowState.MAXIMIZED);
	
	portletURL.setParameter("struts_action", "/afiliados/view");
	
	String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	
	if (portlet_name == null && portlet_name.trim().equals("_AFI_1_")){
		portlet_name = "afiliados";
	}
	
	if (portlet_name == null && portlet_name.trim().equals("_SEC_1_")){
		portlet_name = "sec";
	}
	
	if(renderResponse.getNamespace().equals("_SEC_1_")){
		portlet_name = "sec";
	}
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "afiliados";
	} 

	//obtengo lista de session
	String usuario_modi = user.getScreenName();
	List<Seccional> seccionales = (ArrayList<Seccional>) portletSession
    		.getAttribute(WebKeysAfiliados.ABM_SECCIONALES_EN_SESSION,
    				PortletSession.APPLICATION_SCOPE);
	
	
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("id");
	headerNames.add("seccional");
	headerNames.add("provincia");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-seccionales-were-found"));
	
	//recupero coincidencias
	
	if(null!=seccionales){
		int total = seccionales.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		
		if(null!=seccionales && seccionales.size()==1){				 								 	
		 	
			 %>	
			 		<script type="text/javascript">					 			
			 		editarSeccional(<%=seccionales.get(0).getId_seccional()%>,"")
			 		</script>
			 	
		 		<%	
		} 		
		for (int i = 0; i < seccionales.size(); i++) {
		 		Seccional seccional = (Seccional) seccionales.get(i);
				ResultRow row = new ResultRow(seccional.getId(),seccional.getDescripcion(), i);
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:editarSeccional(\"");
				sb.append(seccional.getId());
				sb.append("\",\"");
				sb.append(seccional.getDescripcion());
				sb.append("\")'>");			
				sb.append(seccional.getId());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:editarSeccional(\"");
				sb2.append(seccional.getId());
				sb2.append("\",\"");
				sb2.append(seccional.getDescripcion());
				sb2.append("\")'>");
				sb2.append(seccional.getDescripcion());
				sb2.append("</a>");
				row.addText(sb2.toString());
				
				StringBuilder sb3 = new StringBuilder();
				sb3.append("<a href='javascript:editarSeccional(\"");
				sb3.append(seccional.getId());
				sb3.append("\",\"");
				sb3.append(seccional.getDescripcion());
				sb3.append("\")'>");
				sb3.append(seccional.getDomicilio().getProvinciaAsString());
				sb3.append("</a>");
				row.addText(sb3.toString());
				
				resultRows.add(row);
		}
	}	
%>
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

<script type="text/javascript">
var seccionalEnEdicion;

function editarSeccional(id_Seccional,desc){
    
	<%if(portlet_name.equalsIgnoreCase("sec")){%>
		var params = "&id_seccional=" + id_Seccional;
		params = params + "&<%=Constants.CMD%>=<%=Constants.MANAGE%>";
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/sec/view" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	
	<%}else{%>
	
	
		var params = "&<%=Constants.CMD %>=" + "<%= Constants.EDIT %>"; 
	 	params+="&id_seccional=" + id_Seccional;
	 	params+="&usuario_modi=" +"<%=usuario_modi%>";
	 	params+= "&accion=" + "<%= Constants.EDIT %>";
	 	
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/afiliados/editar_seccional" /></portlet:renderURL>';
		url = url + params;
		document.<portlet:namespace />fm.method = 'post';
		submitForm(document.<portlet:namespace />fm, url);
	
	<%}%>
}	
 

 
</script>
