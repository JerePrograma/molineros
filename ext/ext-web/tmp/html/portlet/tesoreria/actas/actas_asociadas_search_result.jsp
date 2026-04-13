<%@ include file="/html/portlet/tesoreria/init.jsp" %>

<liferay-ui:error exception="<%= ActaNoExisteException.class%>" message="acta-no-existe" />
<liferay-ui:error exception="<%= ActaYaRelacionadaException.class%>" message="acta-se-encuentra-relacionada-o-cerrada" />
<liferay-ui:error exception="<%= ActaPerteneceAOtraEmpresaException.class%>" message="acta-pertenece-a-otra-empresa" />

<portlet:defineObjects/>
			<% 
			String ids  ="";
			Acta acta  = (Acta)request.getSession().getAttribute(WebKeysTesoreria.ACTA_EN_EDICION);

			
			boolean esEdicion = false;

			if (request.getAttribute(WebKeysTesoreria.ACTAS_ACTION_EDICION) != null || acta == null) {
				esEdicion = true;
			}
			esEdicion= Boolean.parseBoolean(ParamUtil.getString(request, "esEdicion"));

			if (acta != null && acta.isActaCerrada()){
				esEdicion = false;
			}
			
				//Si debe mostrarse el btn de agregar afiliado
				boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
				List<Acta.ActaRelacionada> actas= null;
				if (acta != null ){
					actas =acta.getActasRelacionadas();
				}
 
				PortletURL portletURLTercerizadora = renderResponse.createRenderURL();
		 		List<String> headerNamesTercerizadora = new ArrayList<String>();
		 		headerNamesTercerizadora.add("Acta");
		 		headerNamesTercerizadora.add("fecha-pago");
		 		headerNamesTercerizadora.add("Importe");
		 		headerNamesTercerizadora.add("Saldo");
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
	 					rowActaRelacionada.addText("<input type='hidden' name='saldo_"+actas.get(i).getActaRelacionada().getId()
	 							+"' id='saldo_"+actas.get(i).getActaRelacionada().getId()
	 							+"' value='"+ actas.get(i).getSaldo().toString() +"'/>" + actas.get(i).getSaldo().toString());
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
	
	<liferay-ui:search-iterator searchContainer="<%=searchContainer%>" />

<script type="text/javascript">
setearTempValueActas("<%=ids%>");
sumarTodo();

</script>
		
