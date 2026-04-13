<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			BigDecimal totalImporte = new BigDecimal("0");
			List<Concepto> conceptos = (List<Concepto>)request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_INGRESO);
			
				List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
				Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);
					boolean esEdicion = recibo == null || recibo.getId() == 0;
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA);				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("concepto");
			 		headerNames.add("importe");
					if(showABMButtons && esEdicion) { 
						headerNames.add("Borrar");
					}							
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-conceptos-were-found"));
				
				 	List<ReciboOtroConcepto> ocs = null;
					if (recibo != null){
						ocs = recibo.getOtrosConceptos();
					}
					if(null!=ocs){
				 		//Seteo el total de la lista.
					 	int total = ocs.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < ocs.size(); i++) {
					 		ReciboOtroConcepto oc =  ocs.get(i);
		 					ResultRow row = new ResultRow(oc, oc.getId(), i);
		 					row.addText(conceptos.get(conceptos.indexOf(oc.getConcepto())).getDescripcion());
		 					row.addText(ocs.get(i).getImporte().toString());
		 					totalImporte = totalImporte.add(ocs.get(i).getImporte());
							// Action
		 					if (showABMButtons && esEdicion){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borrarOtroConcepto('");
			 					sb.append(oc.getId());
			 					sb.append("');\" />");
			 					row.addText(sb.toString());
		 			 		}

				 			resultRows.add(row);
					 	}
				 	}
			%>

	<liferay-ui:search-iterator  paginate="false" searchContainer="<%= searchContainer %>" />

<script type="text/javascript">
	document.getElementById("total_otros").value = "<%= totalImporte.toString()%>";
</script>		
