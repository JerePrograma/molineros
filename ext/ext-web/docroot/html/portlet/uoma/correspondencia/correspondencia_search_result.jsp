<%@ include file="/html/portlet/uoma/init.jsp" %>
<%
	//obtengo lista de session	
	List<Correspondencia> correspondencia=null;	
	correspondencia=(List<Correspondencia>) request.getSession().getAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA);	
	//...
	int total=0;
	PortletURL portletURL = renderResponse.createRenderURL();				
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("id-correspondencia");
	headerNames.add("destino");
	//headerNames.add("tipo-correspondencia");
	headerNames.add("fecha-recepcion");
	headerNames.add("fecha-envio");
	headerNames.add("receptor");
	headerNames.add("edificio-recepcion");
	/*headerNames.add("apellido-rtte");
	headerNames.add("nombre-rtte");
	headerNames.add("apellido-dst");
	headerNames.add("nombre-dst");*/
	headerNames.add("razon-prestador-remitente");
	headerNames.add("razon-prestador-destinatario");
	headerNames.add("editar");	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,20, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-correspondencia-were-found"));
	//recupero coincidencias		
	if(null!=correspondencia){
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		for (int i = 0; i < correspondencia.size(); i++) {
		 		Correspondencia corr = (Correspondencia) correspondencia.get(i);
				ResultRow row = new ResultRow(correspondencia,String.valueOf(corr.getIdCorrespondencia()), i);			
				// Name and short description				
				row.addText(String.valueOf(corr.getIdCorrespondencia()));
				row.addText(corr.getDestino());
				//row.addText(corr.getTipo().getDescripcion());
				if(corr.getDestino().equals("ENTRANTE")){
					row.addText(corr.getFechaEnvioRecepcionAsString());
					row.addText("");
				}else{
					row.addText("");
					row.addText(corr.getFechaEnvioRecepcionAsString());
				}				
				row.addText(corr.getAltaUsr());
				row.addText(corr.getLugarRecepcion()!=null?corr.getLugarRecepcion():"");
				/*row.addText(corr.getNombreRemitente());
				row.addText(corr.getApellidoDestinatario());
				row.addText(corr.getNombreDestinatario());*/
				if(corr.getDestino().equals("ENTRANTE")){
					row.addText(corr.getRazonPrestadorRemitente());
					row.addText("");
				}else{
					row.addText("");
					row.addText(corr.getRazonPrestadorRemitente());					
				}				
				StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='editar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/portlet/edit_guest.png\" onClick=\"javascript:editaCorrespondencia('");
		 					sb.append(corr.getIdCorrespondencia());		 					
		 					sb.append("');\" />");
				row.addText(sb.toString());		 					
				resultRows.add(row);
	 	}
	}
		%>
<input type="button" value="<liferay-ui:message key="print" />" onClick="<portlet:namespace />reporte();" />		
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>" />

<%if (total >1){ %>
<b>Total de resultados: <%=total%><b>
<%@ include file="/html/portlet/utils/paginator/paginator.jsp" %>
<%} %>
<script type="text/javascript"> 
 function <portlet:namespace />paginar(cur){
 		var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
	    var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
	    var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();	    
	    var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();
	    var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;
	    var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
		var cuil=jQuery('#<portlet:namespace />cuil').val();
		var inte=jQuery('#<portlet:namespace />inte').val();
		var tipoDoc=jQuery('#<portlet:namespace />tipoDoc').val();
		var nroDoc=jQuery('#<portlet:namespace />nroDoc').val();
		var seccional=jQuery('#<portlet:namespace />id_seccional_r').val();
		var seccional_afiliado=jQuery('#<portlet:namespace />id_seccional_afiliado').val();				
		var apellido=jQuery('#<portlet:namespace />apellido').val();
		var nombre=jQuery('#<portlet:namespace />nombre').val();
		var entidad=jQuery('#<portlet:namespace />entidad').val();
		var numero_afi=jQuery('#<portlet:namespace />numero_afi').val();
		
		var desde_final=diaDesde+'/'+mesDesde+'/'+anioDesde;		
		var hasta_final=diaHasta+'/'+mesHasta+'/'+anioHasta;		
			
		jQuery('#<portlet:namespace />buscando').show();
		//Si la seccional no fue obtenida la borro...
		if(jQuery("#<portlet:namespace />secc_seleccionada").val()!="1"){
			jQuery("#<portlet:namespace />seccional").val("");
			jQuery("#<portlet:namespace />id_seccional").val("");
		}		
    	var cuit=jQuery('#<portlet:namespace />cuit_entidad').val();    	
		var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/uoma/buscar_incidentes&cuil='+cuil+
		'&inte='+inte+'&tipoDoc='+tipoDoc+'&nroDoc='+escape(nroDoc)+'&seccional='+seccional+'&nombre='+escape(nombre)+'&apellido='+escape(apellido)+
		'&entidad='+entidad+'&numero_afi='+numero_afi+'&fecha_desde='+desde_final+'&fecha_hasta='+hasta_final+'&seccional_afiliado='+seccional_afiliado;
		url += '&cur=' + cur;
		url += '&rnd=' + Math.floor(Math.random()*100);
		jQuery('#<portlet:namespace />busquedaIncidenteDiv').load(url, function() {
        																jQuery('#<portlet:namespace />buscando').hide();            															
        															  }
        );	
 }
 
 function <portlet:namespace />reporte(){
 	var destino=jQuery('#<portlet:namespace/>destino').val();
 	var edificio=jQuery('#<portlet:namespace/>edificio').val();
	var diaDesde=jQuery('#<portlet:namespace />fechaDesdeDia').val();	    
	var mesDesde=parseInt(jQuery('#<portlet:namespace />fechaDesdeMes').val())+1;	    
	var anioDesde=jQuery('#<portlet:namespace />fechaDesdeAnio').val();
	var diaHasta=jQuery('#<portlet:namespace />fechaHastaDia').val();
	var mesHasta=parseInt(jQuery('#<portlet:namespace />fechaHastaMes').val())+1;
	var anioHasta=jQuery('#<portlet:namespace />fechaHastaAnio').val();
	var desde_final=diaDesde+'/'+mesDesde+'/'+anioDesde;		
	var hasta_final=diaHasta+'/'+mesHasta+'/'+anioHasta;
	var idCorrDesde=jQuery('#<portlet:namespace />idCorrespondenciaDesde').val();
	var idCorrHasta=jQuery('#<portlet:namespace />idCorrespondenciaHasta').val();
	var tipoCorr=jQuery('#<portlet:namespace/>tipoCorr').val();
	var remitente=jQuery('#<portlet:namespace/>remitente').val();
	var destinatario=jQuery('#<portlet:namespace/>destinatario').val();
	var receptor=jQuery('#<portlet:namespace/>receptor').val();
	var razon_prestador=jQuery('#<portlet:namespace />razon_prestador').val();
	var provincia= jQuery('#<portlet:namespace />provinciaremi').val();
	var localidad= jQuery('#<portlet:namespace />localidadremi').val();
	var seccional= jQuery('#<portlet:namespace />id_seccional_r').val(); 	
 	var busquedaCorr = { "destino": destino, "edificio": edificio, "desde_final": desde_final, "hasta_final": hasta_final, "id_corr_desde": idCorrDesde, 
							 "id_corr_hasta": idCorrHasta, "tipoCorr": tipoCorr, "remitente": remitente, "destinatario": destinatario, "receptor": receptor,
							 "razon_prestador": razon_prestador, "provinciaremi": provincia, "localidadremi": localidad, "id_seccional_r": seccional};
 	window.location.href ='/xlsservlet/?reporte=REPORTE_CORRESPONDENCIA&destino='+destino+
 						  '&edificio='+edificio+
 						  '&desde_final='+desde_final+
 						  '&hasta_final='+hasta_final+
 						  '&razon_prestador='+razon_prestador+
 						  '&provinciaremi='+provincia+
 						  '&localidadremi='+localidad+
 						  '&id_seccional_r='+seccional;
 }
</script>