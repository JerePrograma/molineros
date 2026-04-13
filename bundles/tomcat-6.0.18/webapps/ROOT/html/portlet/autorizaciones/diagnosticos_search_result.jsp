<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%
	String prefijo = ParamUtil.getString(request, "prefijo","");
%>
<script type="text/javascript">
function pasarParametrosAParentDiagP<%=prefijo%>(id, param) {	
    jQuery("#<portlet:namespace />id_diagnostico<%=prefijo%>").val(id);
    jQuery("#<portlet:namespace />descripDiag<%=prefijo%>").val(param);
    jQuery("#<portlet:namespace />diag_seleccionada<%=prefijo%>").val("1");
    try {    	
        if (jQuery("#<portlet:namespace />id_diagnostico_r").val() == "") {
    		jQuery("#<portlet:namespace />id_diagnostico_r").val(id);    	
    		jQuery("#<portlet:namespace />descripDiag_r").val(param);
    		jQuery("#<portlet:namespace />diag_seleccionada_r").val("1");
        }
    } catch (err) {
    }
    jQuery("#<portlet:namespace />btnBuscarDiagPreaut<%=prefijo%>").hide();
    <portlet:namespace />cerrarDiagP<%=prefijo%>();
 }

</script>
<%
	//obtengo lista de session
/* 	PortletSession ps= renderRequest.getPortletSession();	
 	List<ClaseBase> diagnosticos=null;*/	
 	List<ClaseBase>diagnosticos = (List<ClaseBase>)request.getSession().getAttribute(WebKeysAutorizaciones.DIAGNOSTICOS);
 	/* if(null!=ps){  		
		diagnosticos=(List<ClaseBase>)ps.getAttribute(WebKeysLiquidaciones.SECCIONALES_EN_SESSION,PortletSession.APPLICATION_SCOPE);		
		if(null==diagnosticos || diagnosticos.size()==0){		  
		  diagnosticos=TraeListasServiceUtil.getSeccionales();
		  ps.setAttribute(WebKeysLiquidaciones.SECCIONALES_EN_SESSION,diagnosticos,PortletSession.APPLICATION_SCOPE);		  		
		}	  
	}  */
	//...
	
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("id");
	headerNames.add("descripcion");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,1000, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-diagnosticos-were-found"));
	//recupero coincidencias		
	if(null!=diagnosticos){
		String id_diagnostico=(String)renderRequest.getParameter("id_diagnostico");
		String descripDiagString=(String)renderRequest.getParameter("descripDiag");
		diagnosticos=ListUtils.traeCoincidenciasDeListaMayusculas(diagnosticos,descripDiagString,id_diagnostico);
		//Seteo el total de la lista.
	 	int total = diagnosticos.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			ClaseBase diagUnico=(ClaseBase) diagnosticos.get(0);
			%>
				<script type="text/javascript">
					pasarParametrosAParentDiagP<%=prefijo%>("<%=diagUnico.getId()%>", "<%=diagUnico.getDescripcion()%>");
				</script>
			<%
		//More de una coincidencia
		}else {
		 	searchContainer.setTotal(total);

		 	List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < diagnosticos.size(); i++) {
		 		ClaseBase diagP = (ClaseBase) diagnosticos.get(i);
				ResultRow row = new ResultRow(diagP.getId(),diagP.getDescripcion(), i);
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:");
				sb.append("pasarParametrosAParentDiagP").append(prefijo).append("(\"");
				sb.append(diagP.getId());
				sb.append("\",\"");
				sb.append(diagP.getDescripcion());
				sb.append("\")'>");			
				sb.append(diagP.getId());
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				sb2.append("<a href='javascript:");
				sb2.append("pasarParametrosAParentDiagP").append(prefijo).append("(\"");
				sb2.append(diagP.getId());
				sb2.append("\",\"");
				sb2.append(diagP.getDescripcion());
				sb2.append("\")'>");
				sb2.append(diagP.getDescripcion());
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

