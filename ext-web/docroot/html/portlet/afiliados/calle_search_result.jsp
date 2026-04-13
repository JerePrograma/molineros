<%@ include file="/html/portlet/afiliados/init.jsp" %>
<script type="text/javascript">

function pasarParametrosAParentCalle(param) {
    jQuery("#<portlet:namespace />calle").val(param);
    jQuery("#<portlet:namespace />calle_seleccionada").val(param);    
    <portlet:namespace />cerrarCalle();
 }

</script>
<%
	//obtengo lista de session
	PortletSession ps = renderRequest.getPortletSession();
	List<Direccion> direcciones = null;	
	String calle = (String)renderRequest.getParameter("calle");
	if (ps != null) {  		  
		direcciones = TraeListasServiceUtil.getDirecciones(calle);
		ps.setAttribute(WebKeysAfiliados.DIRECCIONES_EN_SESSION,direcciones,PortletSession.APPLICATION_SCOPE);		 
	}
	
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("calle");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-calles-were-found"));
	//recupero coincidencias		
	if (direcciones != null) {
		//Seteo el total de la lista.
	 	int total = direcciones.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if (total == 1) {
			Direccion direUnica = (Direccion) direcciones.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParentCalle("<%=direUnica.getCalle()%>");
				</script>
			<%
		//More de una coincidencia
		} else {
		 	searchContainer.setTotal(total);
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < direcciones.size(); i++) {
		 		Direccion direccion = (Direccion) direcciones.get(i);
				ResultRow row = new ResultRow(direcciones, direccion.getCalle(), i);
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParentCalle(\"");
				sb.append(direccion.getCalle());
				sb.append("\")'>");
				sb.append(direccion.getCalle());
				sb.append("</a>");
				row.addText(sb.toString());
				resultRows.add(row);
		 	}
		%>
		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
<%
		}
	}
%>