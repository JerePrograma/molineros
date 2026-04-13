<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
	List<ReporteAmtimaPMI> historico = (List<ReporteAmtimaPMI>) request
			.getAttribute(WebKeysAfiliados.REPORTE_AMTIMA_PMI);

	PortletURL portletURLAmtPMI = renderResponse.createRenderURL();
	List<String> headerNamesHistoMov = new ArrayList<String>();
	headerNamesHistoMov.add("vto-fecha");
	headerNamesHistoMov.add("nro-socio");
	headerNamesHistoMov.add("inte");
	headerNamesHistoMov.add("apellido-y-nombre");
	headerNamesHistoMov.add("seccional");
	headerNamesHistoMov.add("titular");
	headerNamesHistoMov.add("empresa");
	headerNamesHistoMov.add("genera-carta");		

	String view = "";

	SearchContainer searchContainerHistoMov = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, portletURLAmtPMI,
			headerNamesHistoMov, LanguageUtil.get(pageContext,
					"no-se-encontraron-vtos-certificados"));

	if (null != historico) {
		int total = historico.size();
		searchContainerHistoMov.setTotal(total);
		List resultRows = searchContainerHistoMov.getResultRows();

		for (int i = 0; i < historico.size(); i++) {
			ReporteAmtimaPMI histoMov = (ReporteAmtimaPMI) historico
					.get(i);
			ResultRow row = null;
			row = new ResultRow(histoMov, histoMov.getId_amtima(), i);

			
				row.addText(histoMov.getFechaVtoAsString());
			
				row.addText(String.valueOf(histoMov.getId_amtima()));
						
				row.addText(String.valueOf(histoMov.getInte()));
						
				row.addText(histoMov.getApe_nom());
						
				row.addText(histoMov.getSeccional());
						
				row.addText(histoMov.getTitular());
				
				row.addText(histoMov.getEmpresa());
				
				StringBuffer sb = new StringBuffer();
				
				sb.append("<input type=\"checkbox\"");
				sb.append("name=\"cartas\"");				
				sb.append("id=\"");
				sb.append("formu-"+histoMov.getId_amtima()+"-"+histoMov.getInte());
		        sb.append("\" value=\"");
				sb.append(histoMov.getId_amtima()+"-"+histoMov.getInte());									
				sb.append("\"/>");
				
				row.addText(sb.toString());
							
				resultRows.add(row);
		}
	}
	
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainerHistoMov%>" />

<c:if test="<%= headerNamesHistoMov.size() > 0 %>">
		<table>
			<tr>
				<td align="left"><input type="button" value="<liferay-ui:message key="genera-cartas" />" onClick="<portlet:namespace />generarCartas();" /></td>				
			</tr>
		</table>
</c:if>


<script type="text/javascript">
		function <portlet:namespace />generarCartas() {
			var cartas = document.getElementsByName('cartas');
			var cartasValue = "";
			var i = 0;			
			for (i = 0; i<cartas.length; i++){
				if (cartas[i].checked) {					
					cartasValue= cartasValue+cartas[i].value+";"; 
				}
			}
			window.location.href ="/odtservlet/?accion=cartasAjuar&cartas="+cartasValue;	
		}
</script>	