<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<liferay-ui:error exception="<%= ActaNoExisteException.class%>" message="acta-no-existe" />
<liferay-ui:error exception="<%= ActaYaRelacionadaException.class%>" message="acta-se-encuentra-relacionada-o-cerrada" />

<portlet:defineObjects/>
			<% 
			String ids  ="";
			Convenio convenio= (Convenio)request.getSession().getAttribute(WebKeysTesoreria.CONVENIO_EN_EDICION);

			
			boolean esEdicion = false;

			if (request.getAttribute(WebKeysTesoreria.CONVENIOS_ACTION_EDICION) != null || convenio == null) {
				esEdicion = true;
			}
			esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

			if (convenio != null && convenio.getId() != 0){
				esEdicion = false;
			}
			
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
				List<Convenio.ActaRelacionada> actas= null;
				if (convenio != null ){
					actas =convenio.getActasRelacionadas();
				}
 
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("Acta");
		 		headerNamesTercerizadora.add("fecha-pago");
		 		headerNamesTercerizadora.add("Importe");
		 		headerNamesTercerizadora.add("Interes");
		 		headerNamesTercerizadora.add("Total");
				if(showABMButtons && esEdicion) { 
					headerNamesTercerizadora.add("Borrar");
				}				
				SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLTercerizadora, headerNamesTercerizadora,
				LanguageUtil.get(pageContext, "no-actas-asociadas-were-found"));
			
				if(null!=actas){
					int total=actas.size();	 				
	 				List resultRowsInspector = searchContainer.getResultRows();
	 			 	for (int i = 0; i < actas.size(); i++) {
	 			 		if (actas.get(i).getId() < 0){
	 			 			total--;
	 			 			continue;
	 			 		}
	 			 		ids += actas.get(i).getActaRelacionada().getId() + ";";
	 					ResultRow rowActaRelacionada = new ResultRow(actas.get(i),actas.get(i).getId(), i);			
	 					rowActaRelacionada.addText(actas.get(i).getActaRelacionada().getNumero());
	 					rowActaRelacionada.addText(actas.get(i).getActaRelacionada().getFechaPagoAsString());
	 					rowActaRelacionada.addText(actas.get(i).getImporte().toString());
	 					int id = actas.get(i).getActaRelacionada().getId();
	 					BigDecimal ajuste = BigDecimal.ZERO;
	 					if (actas.get(i).getSaldo() != null){
	 						ajuste = actas.get(i).getSaldo().subtract(actas.get(i).getImporte());
	 					}
	 					String ajusteStr = "<input type='hidden' name='importe_"+id
							+"' id='importe_"+id
 							+"' value='"+ actas.get(i).getImporte().toString() +"'/>" 
 							+"  <input type='text' name='ajuste_capital_" + id + "' id='ajuste_capital_" + id + "' value='"+ajuste.toString()+"' ";
 						if (!esEdicion){
 							ajusteStr += " disabled='disabled' ";	
 						}
 						ajusteStr +=" onchange='javascript:sumarTodo()'/>";
	 					rowActaRelacionada.addText(ajusteStr);
	 					rowActaRelacionada.addText("<input type='text' name='total_" + id + "' id='total_" + id + "' disabled='disabled'/>");
	 					
	 					resultRowsInspector.add(rowActaRelacionada);
	 					if (showABMButtons && esEdicion){
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:borraActaAsociada('");
		 					sb.append(actas.get(i).getActaRelacionada().getId());
		 					sb.append("');\" />");
		 					rowActaRelacionada.addText(sb.toString());
	 			 		}
	 				searchContainer.setTotal(total);
		 			}
				}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false" searchContainer="<%=searchContainer%>" />

<script type="text/javascript">
jQuery(document).ready(function(){
	setearTempValueActas("<%=ids%>");
	actualizarDetalleCuotas();
});
</script>
<table width="100%">
<tr>
	<td width="20%"><liferay-ui:message key="dedua-actas" />:</td>
	<td width="80%" align="left">
		<input type="text" name="<portlet:namespace />deuda" id="<portlet:namespace />deuda" disabled="disabled" onChange=" sumarTodo()" 
			value="<%= convenio != null &&  convenio.getDeudaActasRelacionadas() != null ? convenio.getDeudaActasRelacionadas().toString() : "" %>"/>
	</td>
</tr>
</table>