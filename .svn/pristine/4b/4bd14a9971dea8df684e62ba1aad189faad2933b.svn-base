<%@ include file="/html/portlet/afiliados/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<portlet:defineObjects/>
			<%
			        NumberFormat format2D = new DecimalFormat("#0.00");
  					List<AporteAfiliado> aportesList= (List<AporteAfiliado>)portletSession.getAttribute(WebKeysAfiliados.APORTES_AFIP,
							PortletSession.APPLICATION_SCOPE);  
					boolean showSueldos = PermissionUtil.userContainsRole(user,WebKeysAfiliados.VER_SUELDOS);
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
					String cuil_titular=request.getParameter("cuil_titular");
					String paramDesdeMesAnio=request.getParameter("periodoDesdeMesAnio");
					String paramMes=null;
					String paramAnio=null;			
					String[] mesAnio=null;
					if(null!=paramDesdeMesAnio && paramDesdeMesAnio.indexOf("_")!=-1){
						mesAnio=paramDesdeMesAnio.split("_");
					    paramMes=mesAnio[0];
						paramAnio=mesAnio[1];					
					}
					
					 		List<String> headerNames = new ArrayList<String>();
					 		
					 		Calendar periodoDesde = CalendarFactoryUtil.getCalendar(); 		
					 		periodoDesde.setTime(new Date());
					 		int anioDesde=periodoDesde.getInstance().get(Calendar.YEAR);
					 		
					 		headerNames.add("tipo-aporte");
					 		headerNames.add("cuil");
					 		headerNames.add("nombre");
					 		headerNames.add("ultima-alta-afiliado");
					 		headerNames.add("baja-fecha");
					 		headerNames.add("cuit");
					 		headerNames.add("razon-social");
					 		headerNames.add("periodo");
					 		if(showSueldos){
					 			headerNames.add("remuneracion");
					 		}
					 		headerNames.add("fecha-transf");
					 		headerNames.add("fecha-recauda");
					 		if(seccionalFijada==0){
						 		headerNames.add("aporte-estimado");	
						 		headerNames.add("contrib-estimada");
					 		}
					 		headerNames.add("pago-por-actas");
					 		if(seccionalFijada==0){
						 		headerNames.add("comision-os");					 		
						 		headerNames.add("total-terc");	
					 		}
					 		headerNames.add("fecha-liq-terce");
					 		headerNames.add("id-terc");		
								
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-aportes-were-found"));
					
					if(null!=aportesList){
				 								 	
				 		//Seteo el total de la lista.
					 	int total = aportesList.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 		List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < aportesList.size(); i++) {
					 		AporteAfiliado aporte = (AporteAfiliado) aportesList.get(i);
				 					ResultRow row = new ResultRow(aporte,aporte.getAfiliado().getCuil_titular(), i);
				 					boolean deriva=aporte.getAfiliado().getApeNombre().contains("DERIVACION");
				 					if(null==aporte.getIdTerc() || (null!=aporte.getIdTerc()&& deriva && !aporte.getIdTerc().trim().equals("MOLINEROS POR CONSOLIDAR SALUD"))){
					 					if(deriva){
						 					row.addText("<b>"+aporte.getTipoAporteDeno()+"</b>");
					 					}else{
					 						row.addText(aporte.getTipoAporteDeno());
					 					}
					 					
					 					if(deriva){
						 					row.addText("<b>"+aporte.getAfiliado().getCuil_titular()+"</b>");
					 					}else{
					 						row.addText(aporte.getAfiliado().getCuil_titular());
					 					}
						 				if(deriva){
						 					row.addText("<b>"+aporte.getAfiliado().getApeNombre()+"</b>");
						 				}else{
						 					row.addText(aporte.getAfiliado().getApeNombre());
						 				}
						 				if(deriva){
						 					row.addText("<b>"+aporte.getAfiliado().getIngre_fechaAsString()+"</b>");
						 				}else{
						 					row.addText(aporte.getAfiliado().getIngre_fechaAsString());
						 				}
						 				if(deriva){
						 					row.addText("<b>"+aporte.getAfiliado().getBaja_fechaAsString()+"</b>");
						 				}else{
						 					row.addText(aporte.getAfiliado().getBaja_fechaAsString());
						 				}
						 				if(deriva){		 					
						 					row.addText(aporte.getEmpleador().getCuit()!=null?aporte.getEmpleador().getCuit():"");
						 				}else{
						 					row.addText(aporte.getEmpleador().getCuit()!=null?aporte.getEmpleador().getCuit():"");
						 				}
						 				if(deriva){
						 					row.addText(aporte.getEmpleador().getRazon_soc()!=null?aporte.getEmpleador().getRazon_soc():"");
						 				}else{
						 					row.addText(aporte.getEmpleador().getRazon_soc()!=null?aporte.getEmpleador().getRazon_soc():"");
						 				}
						 				if(deriva){
						 					row.addText("<b>"+aporte.getPeriodoAsString()+"</b>");
						 				}else{
						 					row.addText(aporte.getPeriodoAsString());
						 				}
						 				if(showSueldos){
							 				if(deriva){
							 					row.addText("<b>"+aporte.getRemuneracionAsString()+"</b>");
							 				}else{
							 					row.addText(aporte.getRemuneracionAsString());
							 				}
						 				}
						 				if(deriva){
						 					row.addText("<b>"+aporte.getFechaTransfAsString()!=null?aporte.getFechaTransfAsString():""+"</b>");
						 				}else{
						 					row.addText(aporte.getFechaTransfAsString()!=null?aporte.getFechaTransfAsString():"");
						 				}
						 				if(deriva){
						 					row.addText("<b>"+aporte.getFechaRecaudaAsString()!=null?aporte.getFechaRecaudaAsString():""+"</b>");
						 				}else{
						 					row.addText(aporte.getFechaRecaudaAsString()!=null?aporte.getFechaRecaudaAsString():"");
						 				}
						 				if(seccionalFijada==0){
							 				if(deriva){
							 					row.addText("<b>"+aporte.getImporteAsString()+"</b>");
							 				}else{
							 					row.addText(aporte.getImporteAsString());
							 				}
						 				
							 				if(deriva){
							 					row.addText("<b>"+aporte.getContribucionEstimada() != null ? aporte.getContribucionEstimada().toString() : ""+"</b>");
							 				}else{
							 					row.addText(aporte.getContribucionEstimada() != null ? aporte.getContribucionEstimada().toString() : "");
							 				}
						 				}
						 				
						 				if(seccionalFijada==0){							 				
							 				row.addText(aporte.getLiqActasAsString()!=null?aporte.getLiqActasAsString():"");							 				 
						 				}else{
						 					row.addText(aporte.getLiqActasAsString()!=null && !aporte.getLiqActasAsString().equals("0,00") ?"SI":"NO");
						 				}
						 				
						 				if(seccionalFijada==0){
							 				if(deriva){
							 					row.addText("<b>"+aporte.getComisionOSAsString() != null ? aporte.getComisionOSAsString().toString() : ""+"</b>");
							 				}else{
							 					row.addText(aporte.getComisionOSAsString() != null ? aporte.getComisionOSAsString().toString() : "");
							 				}
							 				if(deriva){
							 					row.addText("<b>"+aporte.getTotalLiqTercerizadora() != null ? format2D.format(aporte.getTotalLiqTercerizadora()) : ""+"</b>");
							 				}else{
							 					row.addText(aporte.getTotalLiqTercerizadora() != null ? format2D.format(aporte.getTotalLiqTercerizadora()) : "");
							 				}
						 				}
							 			if(deriva){
							 				row.addText("<b>"+aporte.getFechaLiqTercerizadoraString()+"</b>");
							 			}else{
							 				row.addText(aporte.getFechaLiqTercerizadoraString());
							 			}
						 				
						 				if(deriva){
						 					row.addText("<b>"+null!=aporte.getIdTerc()?aporte.getIdTerc():""+"</b>");
						 				}else{
						 					row.addText(null!=aporte.getIdTerc()?aporte.getIdTerc():"");
						 				}
								 		if(aporte.isMostrar()){		
							 				resultRows.add(row);
								 		}
								 	}
					 		}
					 	}
			%>
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />