<%@ include file="/html/portlet/afiliados/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />


<liferay-ui:error
	exception="<%=NoHayMensajeErrorException.class %>"
	message="no-homolog-ws-were-found" />

<%

List<AfiliadoOpe> afiPrevencion = (List<AfiliadoOpe>) request.getAttribute(WebKeysAfiliados.NOVEDADES_PREVENCION_WS);



	PortletURL portletURLPrevenCont = renderResponse.createRenderURL();
	List<String> headerNamesPrevenContac = new ArrayList<String>();

	headerNamesPrevenContac.add("Cuil Titular");
	headerNamesPrevenContac.add("Inte"); 
	headerNamesPrevenContac.add("Parentesco");
	headerNamesPrevenContac.add("Estado Civil");
	headerNamesPrevenContac.add("Cuit");
	headerNamesPrevenContac.add("Razon Social");
	headerNamesPrevenContac.add("Plan Prevención");
	headerNamesPrevenContac.add("Baja Afiliado");
	headerNamesPrevenContac.add("Id Transacción");
	headerNamesPrevenContac.add("Operación");
	headerNamesPrevenContac.add("Fecha Informe");
	headerNamesPrevenContac.add("Procesado");
	headerNamesPrevenContac.add("Mensaje Descripción");
	headerNamesPrevenContac.add("Acción");
	
	
	
	SearchContainer searchContainerPrevenContact = new SearchContainer(
			renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,
			SearchContainer.DEFAULT_DELTA, 
			portletURLPrevenCont,
			headerNamesPrevenContac, 
			LanguageUtil.get(pageContext,"no-preven-ws-were-found"));

	if (null != afiPrevencion) {
		int total = afiPrevencion.size();
		searchContainerPrevenContact.setTotal(total);
		List resultRows = searchContainerPrevenContact.getResultRows();

		for (int i = 0; i < afiPrevencion.size(); i++) {
			AfiliadoOpe afi = (AfiliadoOpe) afiPrevencion.get(i);
			
			ResultRow row = null;
			row = new ResultRow(afi, afi.getCuil_titular(), i);

			row.addText(afi.getCuil_titular());
			row.addText(String.valueOf(afi.getInte()));
			row.addText(afi.getParentesco());
			row.addText(afi.getCivil_esta());
			row.addText(afi.getCuit());
			row.addText(afi.getRazonSoc());
			row.addText(afi.getPlanPrevencion());
			row.addText(afi.getBaja_fechaAsString());
			row.addText(String.valueOf(afi.getIdTransaccion()));
			
			//nombre operacion
			String nombreOperacion = null;
			int operacion = afi.getOperacion();
			if (operacion == 0 ){
				nombreOperacion = "ALTA_TOTAL";
			}else if(operacion == 1){
				nombreOperacion = "ALTA_BENEFICIARIO";
			}else if (operacion == 2){
				nombreOperacion = "MODIF_BENEFICIARIO";
			}else if (operacion == 3){
				nombreOperacion = "BAJA_TOTAL";
			}else if (operacion == 4){
				nombreOperacion = "BAJA_BENEFICIARIO";
			}else if (operacion == 5){
				nombreOperacion = "MODIF_PLAN";
			}
			
			row.addText(nombreOperacion);

			
			row.addText(afi.getFechaInformeToString());
			row.addText(afi.getProcesada() != null ? afi.getProcesada() : "NO");
			row.addText(afi.getMensajeDesc() != null ? afi.getMensajeDesc() : "");
			
			
			row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/afiliados/procesados_novedades_ws_tools.jsp");
			resultRows.add(row);
		}
	}


	
	
%>

<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainerPrevenContact%>" />
