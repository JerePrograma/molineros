<%@ include file="/html/portlet/uoma/init.jsp" %>
<script type="text/javascript">

function pasarParametrosAParent(id, param, serial) {
    jQuery("#<portlet:namespace />id_farmacia").val(id);
    jQuery("#<portlet:namespace />farmacia").val(param);
    jQuery("#<portlet:namespace />id_farmacia_serial").val(serial);
    jQuery("#<portlet:namespace />farmacia_seleccionada").val("1");
    jQuery("#<portlet:namespace />btnBuscarFarmacia").hide();
    <portlet:namespace />cerrarFarmacia();    
 }

</script>
<%
	//obtengo lista de session
	PortletSession ps= renderRequest.getPortletSession();	
	List<Farmacia> farmacias=null;	
	

    farmacias=TraeListasServiceUtil.getFarmacias(renderRequest);
	
	
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("id");
	headerNames.add("farmacia");				
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-farmacias-were-found"));
	//recupero coincidencias		
	if(null!=farmacias){
		String id_farmacia=(String)renderRequest.getParameter("id_farmacia");
		String farmaciaString=(String)renderRequest.getParameter("farmacia");
		farmacias=ListUtils.traeCoincidenciasDeLista(farmacias,farmaciaString,id_farmacia);
		//Seteo el total de la lista.
	 	int total = farmacias.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			Farmacia farmaciaUnica=(Farmacia) farmacias.get(0);
			%>
				<script type="text/javascript">
					<%-- pasarParametrosAParent("<%=farmaciaUnica.getCodigo()%>", "<%=farmaciaUnica.getFarmacia()%>"); --%>
				 pasarParametrosAParent("<%=farmaciaUnica.getCodigo()%>", "<%=farmaciaUnica.getFarmacia()%>", "<%=farmaciaUnica.getId_farmacia() %>");   
				</script>				
			<%
		//More de una coincidencia	
		}else {
		 	searchContainer.setTotal(total);
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < farmacias.size(); i++) {
		 		Farmacia farmacia = (Farmacia) farmacias.get(i);
				/* ResultRow row = new ResultRow(farmacia.getId_farmacia(),farmacia.getFarmacia(), i);	 */
				ResultRow row = new ResultRow(farmacia.getCodigo(),farmacia.getFarmacia(), i);	
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParent(\"");
				sb.append(farmacia.getCodigo());
				/* sb.append(farmacia.getId_farmacia()); */
				sb.append("\",\"");
				sb.append(farmacia.getFarmacia());
				sb.append("\")'>");
				sb.append(farmacia.getCodigo()); 
				/*sb.append(farmacia.getId_farmacia());*/
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:pasarParametrosAParent(\"");
				sb2.append(farmacia.getCodigo()); 
				/* sb2.append(farmacia.getId_farmacia()); */
				sb2.append("\",\"");
				sb2.append(farmacia.getFarmacia());
				sb2.append("\",\"");
				sb2.append(farmacia.getId_farmacia());
				sb2.append("\")'>");
				sb2.append(farmacia.getFarmacia());
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

