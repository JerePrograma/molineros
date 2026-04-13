<%@page import="ar.com.ospim.autorizaciones.exceptions.PeriodoNoConsecutivoException"%>
<%@ include file="/html/portlet/autorizaciones/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();

List<AutorizacionesPmi> archivos=(List<AutorizacionesPmi>)renderRequest.getAttribute("AutorizacionesPmi");
List<String> headerNames = new ArrayList<String>();
headerNames.add("N° Aut");
headerNames.add("N° Receta");
headerNames.add("Nombre Afiliado");
headerNames.add("Inte");
headerNames.add("Nro. Afiliado");
headerNames.add("Fecha Receta");
headerNames.add("Fecha Baja");
headerNames.add("Observaciones");
headerNames.add("Imprimir|Editar|Baja");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "no-autorizaciones-were-found"));
					
if (archivos != null && !archivos.isEmpty()){
	int total = archivos.size();
	searchContainer.setTotal(total);
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < archivos.size(); i++) {	    
		AutorizacionesPmi liq = (AutorizacionesPmi) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);	

		row.addText(liq.getId_autorizacion_string());
	 	row.addText(liq.getReceta_string(), rowURL);
	 	row.addText(liq.getNombre()+" "+liq.getApellido());
	 	row.addText(liq.getInte_string());
	 	row.addText(liq.getId_ospimToString());
	 	row.addText(liq.getFecha_string());
	 	row.addText(liq.getBaja_Fecha_string());
	 	row.addText(liq.getObservaciones());

	 	StringBuilder sb=new StringBuilder();
	 	if(liq.getBaja_fecha()!=null){
		 	
	 		row.addText(sb.toString());  
	 	}else {	
						
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Imprimir autorizacion\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
			sb.append("/common/print.png\" onClick=\"javascript:imprimirAutorizacion('");
	  		sb.append(liq.getId_autorizacion_string());
	  		sb.append("','");
	 		sb.append(liq.getBaja_fecha());
	 		sb.append("','");
	 		sb.append(liq.getNro_receta());
	 		sb.append("');\" />");	 	
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Editar autorizacion\" src=\"");
			sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/common/edit.png\" onClick=\"javascript:editarAutorizacion('");
	 		sb.append(liq.getReceta_string());
	 		sb.append("','");
	 		sb.append(liq.getCuil_titular());
	 		sb.append("','");
	 		sb.append(liq.getInte());
	 		sb.append("','");
			sb.append(liq.getId_ospimToString());
	 		sb.append("','");
	 		sb.append(liq.getApellido());
	 		sb.append("','");
	 		sb.append(liq.getNombre());
	 		sb.append("','");
	 		sb.append(liq.getDocu_numero());
	 		sb.append("','");
	 		sb.append(liq.getDocumento_tipo());
	 		sb.append("','");
	 		sb.append(liq.getBaja_afi_string());
	 		sb.append("','");
	 		sb.append(liq.getId_seccional());
	 		sb.append("','");
	 		sb.append(liq.getDescSecc());
	 		sb.append("','");
	 		sb.append(liq.getObservaciones());
	 		sb.append("','");
	 		sb.append(liq.getFecha_string());
	 		sb.append("');\" />");
	 	 	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Baja autorizacion\" src=\"");
	 		sb.append(themeDisplay.getPathThemeImages());
	 		sb.append("/application/close.png\" onClick=\"javascript:bajaAutorizacion('");
	 		sb.append(liq.getId_autorizacion_string());
	 		sb.append("','");
	 		sb.append(liq.getReceta_string());
	 		sb.append("','");
	 		sb.append(liq.getCuil_titular());
	 		sb.append("','");
	 		sb.append(liq.getInte());
	 		sb.append("');\" />");
	 		row.addText(sb.toString());  
	 	}
		resultRows.add(row);
	}
}
%>
	<liferay-ui:success key="AutoGenerada" message="autorizacion-generada" />
	<liferay-ui:error exception="<%=AfiliadoNoEsBebeException.class %>"message="error-benef-mayor-seis-meses" /> 
	<liferay-ui:error exception="<%=NoEsPlanMolineroException.class %>"message="no-es-plan-molinero" />
	<liferay-ui:error exception="<%=ExcedeCantAutoException.class %>"message="excede-cant-auto" />
 	<liferay-ui:error key="avisoPeriodoNoConsecutivo" message="<%=(String)request.getAttribute(\"periodoNoConsecutivo\") %>" />
 		
	<script type="text/javascript">
	<% if(SessionErrors.isEmpty(renderRequest)){%>
	<portlet:namespace />initDateFields();
	<%}%>
	
	jQuery('#<portlet:namespace />buscando').hide();
	var autorizacionEnEdicion;

	function imprimirAutorizacion(idAutorizacion,bajaFecha){
			window.location.href ="/pdfservlet/?accion=autorizacionRecetaPmi&id_autorizacion_pmi="+idAutorizacion;
	}
			
	function editarAutorizacion(receta,cuil,inte,nroAfi,apellido,nombre,nroDoc,docu_tipo, baja_afi, id_seccional,seccional, observaciones,fechaReceta){		
		jQuery('#<portlet:namespace />buscando').show();

	 	var editarPmi = {"numReceta":receta,"cuil":cuil,"inte":inte,"nroAfi":nroAfi,"apellido":apellido,"nombre":nombre,"nroDoc":nroDoc,
	 			"tipoDoc":docu_tipo,"bajaFecha":baja_afi,"id_seccional":id_seccional,"seccional":seccional,"fechaReceta":fechaReceta,
	 			"observaciones":observaciones,"usuario_modi":'<%=usuario_modi%>'};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/editar_autorizacion_pmi" /></portlet:renderURL>';
	 	autorizacionEnEdicion = Liferay.Popup({title:"<liferay-ui:message key="Edicion de la autorizacion:" />",modal:true,width:1100});
	 	jQuery(autorizacionEnEdicion).load(url,editarPmi, function(){
															jQuery('#<portlet:namespace />buscando').hide();            															
														  });		 
		}	
	
	function bajaAutorizacion(idAutorizacion,receta,cuil,inte){
		
		var fechaRecetaDia = jQuery('#<portlet:namespace/>fechaRecetaDia').val();
		var fechaRecetaMes=jQuery('#<portlet:namespace />fechaRecetaMes').val();		
		var fechaRecetaAnio=jQuery('#<portlet:namespace />fechaRecetaAnio').val();
				
		jQuery('#<portlet:namespace />buscando').show();
	 	var busquedaPmi = {"idAutorizacion":idAutorizacion,"receta":receta,"fechaRecetaDia":fechaRecetaDia,"fechaRecetaMes":fechaRecetaMes,"fechaRecetaAnio":fechaRecetaAnio,"cuil_titular":cuil,"inte":inte,"usuario_modi":'<%=usuario_modi%>'};
	 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/autorizaciones/baja_autorizacion_pmi" /></portlet:renderURL>';
		jQuery('#<portlet:namespace />listado_autorizaciones_pmi').load(url,busquedaPmi, function(){
															jQuery('#<portlet:namespace />buscando').hide();            															
														  });	
		}
	
	function <portlet:namespace />cerrarEdicion(){
				Liferay.Popup.close(autorizacionEnEdicion);
		} 
	
	</script>	
	
<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />