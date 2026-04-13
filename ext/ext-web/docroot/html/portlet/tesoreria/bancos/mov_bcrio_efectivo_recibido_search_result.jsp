<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>
			<%
			BigDecimal totalImporte = new BigDecimal("0");
			MovimientoBancario mov= (MovimientoBancario)request.getSession().getAttribute(WebKeysTesoreria.MOV_BCRIO_EN_EDICION);
					boolean esEdicion = true;
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)|| PermissionUtil.userContainsRole(user,"ABM_Farmacia") || PermissionUtil.userContainsRole(user,"Entidad_Uoma");				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("importe");
			 		headerNames.add("fecha-pago");
			 		headerNames.add("nuevo-estado");
			 		headerNames.add("recibo");
			 		headerNames.add("fecha-recibo");
					if(showABMButtons && esEdicion) {
						headerNames.add("marcar-depositado");
						headerNames.add("Sacar de la lista");
					}					
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-cheques-were-found"));
				
				 	List<MovimientoBancoReciboIngreso> efectivos = null;
					if (mov != null){
						efectivos = mov.getEfectivoRecibido();
					}
					if(null!=efectivos){
				 		//Seteo el total de la lista.
					 	int total = efectivos.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < efectivos.size(); i++) {
					 		MovimientoBancoReciboIngreso mbri = efectivos.get(i);
					 		if (mbri.isBorradoLogico()){
					 			total--;
					 		} else {
						 		Efectivo ef = (Efectivo)mbri.getReciboIngreso().getIngreso();
			 					ResultRow row = new ResultRow(ef, ef.getNumeroStr(), i);
			 					row.addText(ef.getImporte().toString());
			 					totalImporte = totalImporte.add(ef.getImporte());
			 					row.addText(ef.getFechaAsString());
			 					row.addText(ef.getEstado().getDescripcion());
			 					row.addText(ef.getNroRecibo());
			 					row.addText(ef.getFechaReciboAsString());
								// Action
			 					if (showABMButtons && esEdicion){
			 						StringBuilder sb1= new StringBuilder();
			 						if (ef.getEstado().getId() == Efectivo.Estado.RECIBIDO){
					 					sb1.append("&nbsp;<img title=\"Marcar como Depositado\" src=\"");
					 					sb1.append(themeDisplay.getPathThemeImages());
					 					sb1.append("/common/checked.png\" onClick=\"javascript:marcarEfectivoDepositado('");
					 					sb1.append(mbri.getId());
					 					sb1.append("');\" />");
			 						} else {
			 							sb1.append("&nbsp;<img title=\"Desmarcar Depositado\" src=\"");
					 					sb1.append(themeDisplay.getPathThemeImages());
					 					sb1.append("/common/undo.png\" onClick=\"javascript:desmarcarEfectivoDepositado('");
					 					sb1.append(mbri.getId());
					 					sb1.append("');\" />");
			 						}
					 					row.addText(sb1.toString());
				 					
			 						StringBuilder sb= new StringBuilder();
				 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
				 					sb.append(themeDisplay.getPathThemeImages());
				 					sb.append("/common/delete.png\" onClick=\"javascript:borraEfectivoRecibido('");
				 					sb.append(mbri.getId());
				 					sb.append("');\" />");
				 					row.addText(sb.toString());
			 			 		}
	
					 			resultRows.add(row);
					 		}
					 	}
				 	}
			%>

	<liferay-ui:search-iterator  paginate="false" searchContainer="<%= searchContainer %>" />		
