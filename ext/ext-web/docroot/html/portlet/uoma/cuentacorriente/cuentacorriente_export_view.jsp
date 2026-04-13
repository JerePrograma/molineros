<%@ include file="/html/portlet/uoma/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Date" %>
   
<%  
  String saldo_ini = (String)session.getAttribute("CTACTE_RESULT_EXPORT_SALDOINI");
  String titulo_periodo = (String)session.getAttribute("CTACTE_RESULT_TIT_PERIODO");
  String titulo_accion = (String)session.getAttribute("CTACTE_RESULT_TIT_ACCION");  
%>
					
<portlet:defineObjects/>
        <label id="titulo">Periodo Desde: Hasta:</label>
        
			<%					
					List<Informacion> infolist = (ArrayList<Informacion>)session.getAttribute("CTACTE_RESULT_EXPORT");
					
					PortletURL portletURL = renderResponse.createRenderURL();				
			 		
					List resultRows;
					SearchContainer searchContainer;
					
					List<String> headerNames = new ArrayList<String>();
					headerNames.add("Periodo");
					headerNames.add("Acta");
			 		headerNames.add("Debe");
			 		headerNames.add("Haber");
			 		headerNames.add("Saldo");

		 			searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-empresas-were-found"));
		 			
		 			//Seteo el total de la lista.
				 	//int total = cta.getInfo().size();
				 	BigDecimal saldo = BigDecimal.ZERO;
				 	
				 	//searchContainer.setTotal(total);
			 		resultRows = searchContainer.getResultRows();
			 		PortletURL rowURL = renderResponse.createRenderURL();
			 		
			 		int i = 0;
			 		
					if(null!=infolist){
						
						for (Informacion info : infolist) {
					 		
							//for (Informacion info : cta.getInfo()) {
								
								if ((info.getFecha() != null) && (info.getImporte() != null)) {
									
									ResultRow row = new ResultRow(info, info.getIdPago(), i);
									rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
							 		
									if (info.getDebitoCredito().equals("")) {
										row.addText("");
									} else {
								 		StringBuilder sbPer = new StringBuilder();
								 		sbPer.append(info.getFecha());
								 		row.addText(sbPer.toString());										
									}

							 		StringBuilder sbActa = new StringBuilder();
							 		sbActa.append(info.getDescripcion());
							 		row.addText(sbActa.toString());
							 		
									BigDecimal importeDebe = BigDecimal.ZERO;
									BigDecimal importeHaber = BigDecimal.ZERO;
										
									if (info.getDebitoCredito().equals("D")) {
										saldo = saldo.subtract(info.getImporte());
										importeHaber = info.getImporte();
									} else {
										saldo = saldo.add(info.getImporte());
										importeDebe = info.getImporte();
									}

									if (info.getDebitoCredito().equals("")) {
										row.addText("");
								 		row.addText("");
									} else {
										row.addText(String.format("%,.2f", importeDebe));
								 		row.addText(String.format("%,.2f", importeHaber));
									}
							 		row.addText(String.format("%,.2f", saldo));
							 		
						 			resultRows.add(row);
						 			
						 			i++;
								}
																
							// }
					 								 	
					 		
					 	}
				 	}
			%>
			<script type="text/javascript">
			jQuery("#titulo").text('<%=titulo_periodo%>'); 		
			
			jQuery("#<portlet:namespace />buscar").show();
			jQuery("#<portlet:namespace />anterior").hide();
			jQuery("#<portlet:namespace />exportar_v0").show();
			jQuery("#<portlet:namespace />exportar_v1").hide();
			jQuery("#<portlet:namespace />exportar_v2").hide();
			
			//jQuery("#<portlet:namespace />exportar_actas_uoma").hide();	
			//jQuery("#<portlet:namespace />exportar_actas_amtima").hide();	
			jQuery("#<portlet:namespace/>nav").val("1");

			</script>

	<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />	
	

