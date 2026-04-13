<%@ include file="/html/portlet/liquidaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>

<%							
	List<PrestadorLugarAtencion> lugarAtPrestador = null;

	lugarAtPrestador =  (ArrayList<PrestadorLugarAtencion>) request.getSession().
			getAttribute(WebKeysLiquidaciones.LUGARES_ATENCION_PRESTADOR_INDIRECTO_EN_SESSION);

	if(lugarAtPrestador == null){
		lugarAtPrestador = new ArrayList<PrestadorLugarAtencion>();
	}
	PortletURL portletURL = renderResponse.createRenderURL();				
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	 		List<String> headerNames = new ArrayList<String>();
	 		headerNames.add("Seleccionar");	
	 		headerNames.add("Nombre");
	 		headerNames.add("Provincia");
	 		headerNames.add("Localidad");
	 		headerNames.add("Calle");
	 		headerNames.add("Nro");
	 		headerNames.add("Piso");
	 		headerNames.add("Dpto");
	 		
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
	SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.MAX_DELTA, portletURL, headerNames,
	LanguageUtil.get(pageContext, "no-lugarat-were-found"));

 		//Seteo el total de la lista.
	 	int total = lugarAtPrestador.size();
	 	searchContainer.setTotal(total);
 		List resultRows = searchContainer.getResultRows();
 		for (int i = 0; i < lugarAtPrestador.size(); i++) {	    
 			 		
		 		PrestadorLugarAtencion la = (PrestadorLugarAtencion) lugarAtPrestador.get(i);
		 		
				ResultRow row = new ResultRow(la,la.getId_domicilio(), i);
				/* PortletURL rowURL = renderResponse.createRenderURL();		 				
				rowURL.setWindowState(LiferayWindowState.MAXIMIZED);		 				
				rowURL.setParameter("struts_action","/liquidaciones/lista_lugares_atencion_prestador_indirecto");
				rowURL.setParameter("domicilio_id", String.valueOf(la.getId_domicilio()));
				rowURL.setParameter("cmd","view"); */
				row.addText("<input type='radio' id='id_domicilio_indirecto' name='id_domicilio_indirecto' value1='"+la.getId_prestador()+"' value2='"+la.getId_domicilio()+"' onclick=\"mostrarDomIndirecto(this);\" onselect=\"mostrarDomIndirecto(this); \"/>" ); 
				/* row.addText(la.getNombre(), rowURL); */
				row.addText(la.getNombre());
				row.addText(la.getDomicilio().getProvincia().getDescripcion());
				row.addText(la.getDomicilio().getLocalidad().getDescripcion());
				row.addText(la.getDomicilio().getCalle());
				row.addText(la.getDomicilio().getNumero());
				row.addText(la.getDomicilio().getPiso());
				row.addText(la.getDomicilio().getDepto());
				
			/* row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/liquidaciones/administracion/prestadores/editar_borrar_lugar_at_prestador.jsp"); */
		
 			resultRows.add(row);
	 	}
 
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script>
function mostrarDomIndirecto(lugarAtencion){
	var prestador_id = lugarAtencion.getAttribute("value1");
	var domicilio_id = lugarAtencion.getAttribute("value2");
	var accionEnCurso = document.<portlet:namespace />prestador_lugarat_fm.<portlet:namespace /><%= Constants.CMD %>.value;
	
	var url = '<portlet:actionURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/liquidaciones/lista_lugares_atencion_prestador" /></portlet:actionURL>';
	url = url + '&<%=Constants.CMD %>='+'<%=Constants.COPY%>';
	url = url + '&prestador_id='+prestador_id+'&domicilio_id='+domicilio_id;
	url = url + '&accionEnCurso=' + accionEnCurso;
	
	if(popupInd){
		Liferay.Popup.close(popupInd);
	}
	
	document.<portlet:namespace />prestador_lugarat_fm.method = 'post';
	submitForm(document.<portlet:namespace />prestador_lugarat_fm, url);
	
	

} 
</script>

	
		

