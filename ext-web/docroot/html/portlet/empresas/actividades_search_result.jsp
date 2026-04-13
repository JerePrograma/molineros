<%@ include file="/html/portlet/empresas/init.jsp" %>
<script type="text/javascript">
<%String prefijo = request.getParameter("prefijo");%>
function pasarParametrosAParent(id, param, tipo) {	
    jQuery("#<portlet:namespace />cod_actividad"+tipo+"<%=prefijo!=null?prefijo:""%>").val(id);
    jQuery("#<portlet:namespace />actividad"+tipo+"<%=prefijo!=null?prefijo:""%>").val(param);
    jQuery("#<portlet:namespace />act_seleccionada"+tipo+"<%=prefijo!=null?prefijo:""%>").val("1");
    jQuery("#<portlet:namespace />btnBuscarActividad"+tipo).hide();    
    <portlet:namespace />cerrarAct<%=prefijo%>(tipo);
//    <portlet:namespace />cerrar();    
 }

</script>
<%	
	List<Actividad> actividades = TraeListasServiceUtil.getActividades(renderRequest);
	
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("cod");
	headerNames.add("actividad");				
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-actividades-were-found"));
	//recupero coincidencias		
	if(null!=actividades){
		String tipo=(String)renderRequest.getParameter("tipo");
		if(tipo==null || tipo.equals("null")){
			tipo="";
		}
		String cod_actividad=(String)renderRequest.getParameter("cod_actividad");
		String actividadString=(String)renderRequest.getParameter("actividad");
		
		actividades=ListUtils.traeCoincidenciasDeLista(actividades,actividadString,cod_actividad);
		//Seteo el total de la lista.
	 	int total = actividades.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			Actividad actUnica=(Actividad) actividades.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParent("<%=actUnica.getCodigo()%>", "<%=actUnica.getDescripcion()%>","<%=tipo%>");
				</script>				
			<%
		//More de una coincidencia	
		}else {
		 	searchContainer.setTotal(total);		 	
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < actividades.size(); i++) {
		 		Actividad actividad = (Actividad) actividades.get(i);
				ResultRow row = new ResultRow(actividad.getCodigo(),actividad.getDescripcion(), i);			
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParent(\"");
				sb.append(actividad.getCodigo());
				sb.append("\",\"");
				sb.append(actividad.getDescripcion());
				sb.append("\",\"");
				sb.append(tipo);
				sb.append("\")'>");			
				sb.append(actividad.getCodigo());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:pasarParametrosAParent(\"");
				sb2.append(actividad.getCodigo());
				sb2.append("\",\"");
				sb2.append(actividad.getDescripcion());
				sb2.append("\",\"");
				sb2.append(tipo);
				sb2.append("\")'>");
				sb2.append(actividad.getDescripcion());
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

