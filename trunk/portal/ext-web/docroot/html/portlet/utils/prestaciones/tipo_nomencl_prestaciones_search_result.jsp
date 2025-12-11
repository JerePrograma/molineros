<%@ include file="/html/portlet/utils/prestaciones/init.jsp" %>
<%
	PortletSession ps= renderRequest.getPortletSession();
	List<Prestacion> prestaciones=null;	
	
	PortletURL portletURL = renderResponse.createRenderURL();
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("Código");
	headerNames.add("Prestación");
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-prestaciones-were-found"));	
	
	String idTipoNomStr=(String)renderRequest.getParameter("id_tipo_nomenclador");
	Integer idTipoNomenclador=0;
	if(idTipoNomStr!=null && idTipoNomStr.length() >0){
		idTipoNomenclador = Integer.parseInt(idTipoNomStr);
	}
	String codigoPrestacion=(String)renderRequest.getParameter("codigo");
	String descripcionPrestacion=(String)renderRequest.getParameter("prestacion");
	String suf = (String)renderRequest.getParameter("suf");
	
	prestaciones=PlanPrestacionServiceUtil.traeTipoNomencladorPrestaciones(idTipoNomenclador, codigoPrestacion, descripcionPrestacion);
		
	if(null!=prestaciones){
		//Seteo el total de la lista.
	 	int total = prestaciones.size();
		//Si existe una sola coincidencia la plancho en los campos del parent
		if(total==1){
			Prestacion presUnica=(Prestacion) prestaciones.get(0); //.getNomenclador();
			/* PlanPrestacion planPrestacion = (PlanPrestacion) prestaciones.get(0); */
			%>
				<script type="text/javascript">					
					<%-- pasarParametrosAParentPresc<%=suf%>("<%=presUnica.getId()%>","<%=presUnica.getCodigo()%>","<%=presUnica.getDescripcion()%>",
							"<%= String.valueOf(presUnica.getId_tipo_nomenclador())%>","<%= presUnica.getImporte() == null ? 0 : presUnica.getImporte().toString()%>",
							"<%= presUnica.getHonorarios().toString()%>","<%= presUnica.getGastos().toString()%>");		 --%>
					pasarParametrosAParentPresc<%=suf%>("<%=presUnica.getId()%>","<%=presUnica.getCodigo()%>","<%=presUnica.getDescripcion()%>",
							"<%= String.valueOf(presUnica.getId_tipo_nomenclador())%>","<%=0%>","","");				
				</script>
			<%
		//More de una coincidencia
		}else {
		 	searchContainer.setTotal(total);
		 	//prestaciones = ListUtil.subList(prestaciones, searchContainer.getStart(),searchContainer.getEnd());
			List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < prestaciones.size(); i++) {
		 		Prestacion prestacion = (Prestacion) prestaciones.get(i); //.getNomenclador();
		 		/* PlanPrestacion planPrestacion = (PlanPrestacion) prestaciones.get(i); */
				ResultRow row = new ResultRow(prestacion.getCodigo(),prestacion.getDescripcion(), i);			
				// Name and short description
				StringBuilder sb = new StringBuilder();
				sb.append("<a href='javascript:pasarParametrosAParentPresc"+suf+"(\"");
				sb.append(prestacion.getId());
				sb.append("\",\"");
				sb.append(prestacion.getCodigo());
				sb.append("\",\"");
				sb.append(prestacion.getDescripcion());
				sb.append("\",\"");
				sb.append(prestacion.getId_tipo_nomenclador());
				/*sb.append("\",\"");
				sb.append(prestacion.getImporte());
				sb.append("\",\"");
				sb.append(prestacion.getHonorarios().toString());
				sb.append("\",\"");
				sb.append(prestacion.getGastos().toString()); */
				sb.append("\");"); 
				
				//sb.append("pasarParametroImporte"+suf+"(\"");
				//	sb.append(prestacion.getImporte());
				//	sb.append("\");");
				//}
				sb.append("'>");
				sb.append(prestacion.getCodigo());
				
				sb.append("</a>");
				row.addText(sb.toString());
				StringBuilder sb2 = new StringBuilder();
				
				sb2.append("<a href='javascript:pasarParametrosAParentPresc"+suf+"(\"");
				sb2.append(prestacion.getId());
				sb2.append("\",\"");
				sb2.append(prestacion.getCodigo());
				sb2.append("\",\"");
				sb2.append(prestacion.getDescripcion());
				sb2.append("\",\"");
				sb2.append(prestacion.getId_tipo_nomenclador());
				/*sb2.append("\",\"");
				sb2.append("\",\"");
				sb2.append(prestacion.getImporte());
				sb2.append("\",\"");
				sb2.append(prestacion.getHonorarios().toString());
				sb2.append("\",\"");
				sb2.append(prestacion.getGastos().toString());*/
				sb2.append("\");"); 
				//if (Integer.valueOf(protesisString) == 1 || Integer.valueOf(protesisString) == 2 || Integer.valueOf(protesisString) == 3) {
				//	sb2.append("pasarParametroImporte"+suf+"(\"");
				//	sb2.append(prestacion.getImporte());
				//	sb2.append("\");");
				//}
				sb2.append("'>");
				sb2.append(prestacion.getDescripcion());
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