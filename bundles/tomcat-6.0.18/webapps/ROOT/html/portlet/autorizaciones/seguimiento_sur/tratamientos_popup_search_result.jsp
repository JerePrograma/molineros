<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ page import="ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad" %>

<portlet:defineObjects/>
			<%
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "autorizaciones";
			}
			 
			List<TratamientoDiscapacidad> tratamientos = new ArrayList<TratamientoDiscapacidad> ();
			tratamientos= (ArrayList<TratamientoDiscapacidad>)renderRequest.getAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD);
			if (tratamientos == null || tratamientos.size() == 0) {
				tratamientos = (ArrayList<TratamientoDiscapacidad>) portletSession.getAttribute(WebKeysAutorizaciones.BUSQUEDA_TRATAMIENTOS_DISCAPACIDAD, PortletSession.PORTLET_SCOPE);
			}
			
			SeguimientoSur seguimiento=(SeguimientoSur)request.getSession().getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			
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
	 		headerNamesTercerizadora.add("Cantidad");
	 		headerNamesTercerizadora.add("Importe");
	 		headerNamesTercerizadora.add("Frecuencia");
	 		headerNamesTercerizadora.add("Período Dde");
	 		headerNamesTercerizadora.add("Período Hta");
	 		headerNamesTercerizadora.add("Prestador");
	 		
	 		headerNamesTercerizadora.add("");
	 		
			if(showABMButtons && esEdicion) { 
				headerNamesTercerizadora.add("Borrar");
			}				
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
			LanguageUtil.get(pageContext, "no-modalidadesAtencion-were-found"));
		
			
			if(null!=tratamientos){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < tratamientos.size(); i++) {
 			 		TratamientoDiscapacidad tratamiento = tratamientos.get(i);
 			 		if (tratamiento.getBaja_fecha()!=null ){
 			 			continue;
 			 		}
	 					ResultRow row = new ResultRow(tratamiento, tratamiento.getId_tratamiento() , i);
	 					row.addText(tratamiento.getPrestacion().getCodigo());
	 					row.addText(tratamiento.getPrestacion().getDescripcion());
	 					row.addText(tratamiento.getCantidad().toString());
	 					row.addText(tratamiento.getImporte_total().toString());
	 					row.addText(tratamiento.getPeriodicidad());
	 					row.addText(tratamiento.getPeriodo_desde().toString());
	 					row.addText(tratamiento.getPeriodo_hasta().toString());
	 					row.addText(tratamiento.getAcreedor().getDescripcion()==null?"":tratamiento.getAcreedor().getDescripcion());
	 					
	 					Boolean marcar=false;
	 					if(seguimiento.getTratamientos()!=null && seguimiento.getTratamientos().size()>0){
	 						for(int xi=0;xi<seguimiento.getTratamientos().size();xi++){
	 							if(tratamiento.getId_tratamiento()==seguimiento.getTratamientos().get(xi).getId_tratamiento()){
	 								marcar=true;
	 								break;
	 							}
	 						}
	 					}
	 					
	 					StringBuffer sb0 = new StringBuffer();
	 					sb0.append("<input type=\"checkbox\"");
	 					sb0.append("name=\"tratam\"");
	 					if(marcar){
	 						sb0.append("\" checked=\"checked");
	 					}
/*	 					
	 					if(seguimiento.getId()!=null && seguimiento.getId()!=0){
	 						sb0.append("\" disabled=\"disabled");
	 					}
*/
	 					sb0.append("id=\"");
	 					sb0.append("formu-"+tratamiento.getId_tratamiento());
	 			        sb0.append("\" value=\"");
	 					sb0.append(tratamiento.getId_tratamiento());									
	 					sb0.append("\"/>");
	 					
	 					row.addText(sb0.toString());
	 					
	 					resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<div align="center" id="<portlet:namespace />selecciontratamientodiscapacidaddiv">
	</div>
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
		
   	<table>
		<tr>
			<td align="left"><input type="button" value="<liferay-ui:message key="Seleccionar" />"
			onClick="<portlet:namespace />seleccionTratamientos();" /></td>				
		</tr>
	</table>


<script type="text/javascript">
		function <portlet:namespace />seleccionTratamientos() {
			var trat = document.getElementsByName('tratam');
			var tratValue = "";
			var i = 0;			
			for (i = 0; i<trat.length; i++){
				if (trat[i].checked) {					
					tratValue= tratValue+trat[i].value+";"; 
				}
			}
			
			<portlet:namespace />seleccionarTratamientos(tratValue);
			
            

		}
</script>			