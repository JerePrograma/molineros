<%@ include file="/html/portlet/liquidaciones/init.jsp" %>

<% 
ResultRow row2 = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
Reintegro reintegro2=(Reintegro)row2.getObject();
String id_reintegro2 = reintegro2.getId_reintegroString();
List<ReintegroPrestacion> reintegrosList2 = reintegro2.getReintegroPrestacion();

				PortletURL portletURL2 = renderResponse.createRenderURL();
		 		List<String> headerNames2 = new ArrayList<String>();
		 		String tipo_reintegro = ParamUtil.getString(request, "tipo_reintegro", "pre");		 		
	 			SearchContainer searchContainer2 = new SearchContainer(renderRequest, null, null,
	 					SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL2, headerNames2,
	 					LanguageUtil.get(pageContext, "no-prestaciones-were-found"));
			    
				if(null!=reintegrosList2){
					%>
					<table>					
					<%
		 			List resultRows2 = searchContainer2.getResultRows();
				 	for (ReintegroPrestacion reintegroP : reintegrosList2) {
				 		%>
				 		<tr align="left">
				 		<td>
			 				<%=tipo_reintegro.equals(WebKeysLiquidaciones.REINTEGRO_PRE) ? DateUtils.format(reintegroP.getFecha_prestacion(),DateUtils.PERIODO) : reintegroP.getFecha_prestacionAsString()%>
			 			</td>
			 			<td align="left">
			 				<%=tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS) ?
			 					(reintegroP.getCodigo() == null ? "" : reintegroP.getCodigo()) : reintegroP.getPlan_prestacion().getNomenclador().getCodigo()%>
			 			</td>
			 			<td align="left">
			 				<%=reintegroP.getComprobanteString()%>
			 			</td>
				 		</tr>
				 		<%
				 	}
				 	%>
				 	</table>
				 	<%
		 		}
 		%>	