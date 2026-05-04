<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento" %>
<%@page import="java.math.BigDecimal"%>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<portlet:defineObjects/>
			<%
			NumberFormat formatter = new DecimalFormat("#0.00");
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			 
			List<TratamientoDiscapacidadSeguimiento> tratamientos = null;
			SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			tratamientos= seguimiento.getTratamientos();
			
			/*
			 tratamientos= (ArrayList<TratamientoDiscapacidad>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD);
			 if (tratamientos == null || tratamientos.size() == 0) {
				tratamientos = (ArrayList<TratamientoDiscapacidad>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD, PortletSession.PORTLET_SCOPE);
			 }
			*/
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);

			//Si debe mostrarse el btn de agregar afiliado
			boolean showABMButtons = true;			

			PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
	 		List<String> headerNamesTercerizadora = new ArrayList<String>();
	 		headerNamesTercerizadora.add("Código");
	 		headerNamesTercerizadora.add("Prestación");
	 		headerNamesTercerizadora.add("Prestador");
	 		headerNamesTercerizadora.add("Cantidad");
	 		headerNamesTercerizadora.add("Importe");
	 		headerNamesTercerizadora.add("Total");
	 		headerNamesTercerizadora.add("Frecuencia");
	 		headerNamesTercerizadora.add("Período Dde");
	 		headerNamesTercerizadora.add("Período Hta");
	 		headerNamesTercerizadora.add("Recupera SUR");
	 		headerNamesTercerizadora.add("Comprobantes");
	 		headerNamesTercerizadora.add("Elimina");
	 		
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-modalidadesAtencion-were-found"));
		
			
			if(null!=tratamientos){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < tratamientos.size(); i++) {
 			 		TratamientoDiscapacidadSeguimiento tratamiento = tratamientos.get(i);
 			 		if (tratamiento.getBaja_fecha()!=null ){
 			 			continue;
 			 		}
	 					ResultRow row = new ResultRow(tratamiento, tratamiento.getId_tratamiento() , i);
	 					row.addText(tratamiento.getPrestacion().getCodigo());
	 					row.addText(tratamiento.getPrestacion().getDescripcion());
	 					row.addText(tratamiento.getAcreedor()==null || tratamiento.getAcreedor().getDescripcion()==null?"":tratamiento.getAcreedor().getDescripcion());
	 					row.addText(tratamiento.getCantidad().toString());
	 					row.addText(tratamiento.getImporte_total().toString());
	 					row.addText(tratamiento.getImporte_tercerizado()!=null && tratamiento.getImporte_tercerizado().compareTo(BigDecimal.ZERO) != 0 ?
	 							formatter.format(tratamiento.getImporte_tercerizado()):
	 							formatter.format(tratamiento.getCantidad().multiply(tratamiento.getImporte_total())));
	 					row.addText(tratamiento.getPeriodicidad());
	 					row.addText(tratamiento.getPeriodo_desde().toString());
	 					row.addText(tratamiento.getPeriodo_hasta().toString());
	 					row.addText("");
//	 					if (showABMButtons && esEdicion){
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='ver'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/view.png\" onClick=\"javascript:verComprobantes('");
		 					sb.append(tratamiento.getId_tratamiento());
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
//	 			 		} else {
//	 			 			row.addText("");
//	 			 		}

//	 			 		if (seguimiento.getId()==null || seguimiento.getId()==0){
		 				 	StringBuilder sb1= new StringBuilder();
			 					sb1.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb1.append(themeDisplay.getPathThemeImages());
			 					sb1.append("/common/delete.png\" onClick=\"javascript:borraTratamiento('");
			 					sb1.append(tratamiento.getId_tratamiento());
			 					sb1.append("');\" />");
			 					row.addText(sb1.toString());
//		 			 	} else {
//		 			 		row.addText("");
//		 			 	}
	 					resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
		
<script type="text/javascript">
var importeSS=jQuery('#<portlet:namespace />importeSeguimientoSUR').val();	
//if(importeSS==null || Number(importeSS)==0 ){
	jQuery('#<portlet:namespace />importeSeguimientoSUR').val('<%=seguimiento.getImportePresentado()==null?0: (new DecimalFormat("#.00")).format(seguimiento.getImportePresentado())%>'.replace(",","."));
	
//}
</script>		
		