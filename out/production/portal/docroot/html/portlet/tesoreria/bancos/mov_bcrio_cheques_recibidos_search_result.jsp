<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.ChequeRechazadoException" %>

<liferay-ui:error exception="<%=ChequeRechazadoException.class %>" message="modif-cheques-ya-rechazados" />
<portlet:defineObjects/>
			<%
			SearchContainer searchContainer =null;
			int total=0;
			int i=0;
			try{
			BigDecimal totalImporte = new BigDecimal("0");
			List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
			MovimientoBancario mov= (MovimientoBancario)request.getSession().getAttribute(WebKeysTesoreria.MOV_BCRIO_EN_EDICION);
					boolean esEdicion = true;
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)|| PermissionUtil.userContainsRole(user,"ABM_Farmacia") || PermissionUtil.userContainsRole(user,"Entidad_Uoma");				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("cheque-nro");
			 		headerNames.add("importe");
			 		headerNames.add("banco");
			 		headerNames.add("fecha-pago");
			 		headerNames.add("nuevo-estado");
			 		headerNames.add("recibo");
			 		headerNames.add("fecha-recibo");
					if(showABMButtons && esEdicion) {
						headerNames.add("marcar-depositado");
						headerNames.add("Sacar de la lista");
					}					
					searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					LanguageUtil.get(pageContext, "no-cheques-were-found"));
				
				 	List<MovimientoBancoCheque> cheques = null;
					if (mov != null){
						cheques = mov.getChequesRecibidos();
					}
					if(null!=cheques){
				 		//Seteo el total de la lista.
					 	total = cheques.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (i = 0; i < cheques.size(); i++) {
					 		MovimientoBancoCheque mbchq = cheques.get(i);
					 		if (mbchq.isBorradoLogico()){
					 			total--;
					 		} else {
						 		Cheque chq = mbchq.getCheque();
			 					ResultRow row = new ResultRow(chq, chq.getNumero().toString(), i);
			 					row.addText(chq.getNumero().toString());
			 					row.addText(chq.getImporte().toString());
			 					totalImporte = totalImporte.add(chq.getImporte());
			 					int index = bancos.indexOf(chq.getBanco());
			 					row.addText(bancos.get(index).getDescripcion_banco());
			 					row.addText(chq.getFechaAsString());
			 					row.addText(chq.getEstado().getDescripcion());
			 					row.addText(chq.getNroRecibo()==null?"":chq.getNroRecibo());
			 					row.addText(chq.getFechaReciboAsString());
								// Action
			 					if (showABMButtons && esEdicion){
			 						StringBuilder sb1= new StringBuilder();
			 						if (chq.getEstado().getId() == Cheque.Estado.RECIBIDO){
/*			 							
					 					sb1.append("&nbsp;<img title=\"Marcar como Depositado\" src=\"");
					 					sb1.append(themeDisplay.getPathThemeImages());
					 					sb1.append("/common/checked.png\" />"); //onClick=\"javascript:marcarDepositado('");
					 					//sb1.append(mbchq.getId());
					 					//sb1.append("');\" />");
*/

                                       sb1.append("&nbsp;<img title=\"Marcar como Depositado\" src=\"");
	                                   sb1.append(themeDisplay.getPathThemeImages());
	                                   sb1.append("/common/checked.png\" onClick=\"javascript:marcarDepositado('");
	                                   sb1.append(mbchq.getId());
	                                   sb1.append("');\" />");

			 						} else {
/*			 							
			 							sb1.append("&nbsp;<img title=\"Desmarcar Depositado\" src=\"");
					 					sb1.append(themeDisplay.getPathThemeImages());
					 					sb1.append("/common/undo.png\"/>");  //onClick=\"javascript:desmarcarDepositado('");
					 					sb1.append(mbchq.getId());
					 					//sb1.append("');\" />");
*/

                                        sb1.append("&nbsp;<img title=\"Desmarcar Depositado\" src=\"");
	                                    sb1.append(themeDisplay.getPathThemeImages());
	                                    sb1.append("/common/undo.png\"/ onClick=\"javascript:desmarcarDepositado('");
	                                    sb1.append(mbchq.getId());
	                                    sb1.append("');\" />"); 
	                                    
			 						}
					 					row.addText(sb1.toString());
				 					
			 						StringBuilder sb= new StringBuilder();
				 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
				 					sb.append(themeDisplay.getPathThemeImages());
				 					sb.append("/common/delete.png\" onClick=\"javascript:borraChequesRecibidos('");
				 					sb.append(mbchq.getId());
				 					sb.append("');\" />");
				 					row.addText(sb.toString());
			 			 		}
		
					 			resultRows.add(row);
					 		}
					 	}
				 	}
				}catch(Exception e){
					System.out.println("TOTAL: "+total+ " I: "+i);
					e.printStackTrace();
				}
			%>

	<liferay-ui:search-iterator  paginate="false" searchContainer="<%= searchContainer %>" />		
