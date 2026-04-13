<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%

String view = (String)request.getParameter("view");
String cuil_titular = request.getParameter("cuil");
String inte = request.getParameter("inte");
String tipo_reintegro = request.getParameter("tipo_reintegro");

%>
<portlet:defineObjects />
		<%
			//Si debe mostrarse el btn de agregar afiliado
			
			ArrayList<Reintegro> reintegros = new ArrayList<Reintegro> ();
			boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysLiquidaciones.ROL_ABM_ODONTOLOGIA);
			if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS))	{
				reintegros = (ArrayList<Reintegro>)ReintegroServiceUtil.buscarHistoricoPrestacionesOdoProtesis(cuil_titular, Integer.parseInt(inte));
			} else if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
				reintegros = (ArrayList<Reintegro>)ReintegroServiceUtil.buscarHistoricoPrestacionesOdoOrto(cuil_titular, Integer.parseInt(inte));
			}
			
			PortletURL portletURLReintegroPrestacion = renderResponse.createRenderURL();
	 		List<String> headerNamesReintegroPrestacion = new ArrayList<String>();
	 		headerNamesReintegroPrestacion.add("orden-de-pago-num-cheque-fecha");
	 		headerNamesReintegroPrestacion.add("numero");
	 		headerNamesReintegroPrestacion.add("fecha");
	 		headerNamesReintegroPrestacion.add("cod-prest");
			//headerNamesReintegroPrestacion.add("descripcion");
			if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
				headerNamesReintegroPrestacion.add("Pieza");
			}
		
			SearchContainer searchContainerReintegroPrestacion= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM, Integer.MAX_VALUE, portletURLReintegroPrestacion, headerNamesReintegroPrestacion,
			"No hay registros históricos");
			
			int total = 0;
			if(null!=reintegros){
				
				for (int j = 0; j < reintegros.size(); j++) {
					Reintegro reintegro = null; 			 		
			 		reintegro = (Reintegro)reintegros.get(j);
					ArrayList<ReintegroPrestacion> reintegroPrestaciones = null; 			 		
				 	reintegroPrestaciones = (ArrayList<ReintegroPrestacion>)reintegro.getReintegroPrestacion();
					
				 	total= total + reintegroPrestaciones.size();
	 			 	//seccionales = ListUtil.subList(seccionales, searchContainer.getStart(),searchContainer.getEnd());
	 				List resultRowsReintegroPrestacion = searchContainerReintegroPrestacion.getResultRows();
	 			 	for (int i = 0; i < reintegroPrestaciones.size(); i++) {
	 			 		
	 			 		ReintegroPrestacion reintegroPrestacion = null;
	 			 		
	 			 		reintegroPrestacion = reintegroPrestaciones.get(i);
	 			 		
	 					ResultRow rowReintegroPrestacion = new ResultRow(reintegroPrestacion,reintegroPrestacion.getId_reintegroString()+reintegroPrestacion.getId_prestacionString()+reintegroPrestacion.getId_planString()+reintegroPrestacion.getAltaFechaAsString(), i);	 					
	 					rowReintegroPrestacion.addText(reintegro.getOPReintegro());
	 					if (!tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA))	{
	 						rowReintegroPrestacion.addText(reintegro.getId_reintegro_userString());
	 					} else {
	 						rowReintegroPrestacion.addText(String.valueOf(reintegro.getDetalleCuota().get(0).getId_reintegro_user()));
	 					} 
	 					
	 					rowReintegroPrestacion.addText(reintegro.getFechaAsString());
	 					rowReintegroPrestacion.addText(reintegroPrestacion.getCodigo());
	 					//rowReintegroPrestacion.addText(reintegroPrestacion.getPlan_prestacion().getNomenclador().getDescripcion());
	 					if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
	 						rowReintegroPrestacion.addText(String.valueOf(((ReintegroPrestacionOdoProtesis)reintegroPrestacion).getCara()));
	 					}
	 					resultRowsReintegroPrestacion.add(rowReintegroPrestacion);
	 			 	}
				}
 			}
			searchContainerReintegroPrestacion.setTotal(total);
 	
		%>
		
<liferay-ui:search-iterator searchContainer="<%=searchContainerReintegroPrestacion%>" />
				
	<script type="text/javascript">	
	</script>