<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat"%>
<portlet:defineObjects/>
			<%
			DecimalFormat fm = new DecimalFormat("###0.00");
			
			String portlet_name="";
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "tesoreria";
			}
			if(renderResponse.getNamespace().equals("_FAR_1_")){
				portlet_name = "farmacia";
			}
			if(renderResponse.getNamespace().equals("_UOM_1_")){
				portlet_name = "uoma";
			}
			BigDecimal totalImporte = new BigDecimal("0");
			List<Concepto> conceptos = (List<Concepto>)request.getSession().getAttribute(WebKeysLiquidaciones.CONCEPTOS_INGRESO);
			
				List<Banco> bancos = (List<Banco>)request.getSession().getAttribute(WebKeysTesoreria.BANCOS_EN_SESSION);
				Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);					
			
				//Si debe mostrarse el btn de agregar afiliado								
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)||PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA_UOMA);				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");					
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("concepto");
			 		headerNames.add("importe");
			 		
			 		if(!portlet_name.equals("tesoreria")){			 		
				 		headerNames.add("total-remuneracion");
				 		headerNames.add("total-cant-afiliados");
				 		headerNames.add("periodo");
				 		headerNames.add("Nro.Boleta");
			 		}			 		
					if(showABMButtons) { 
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
		 					String antic=null!=oc.getComproNroAntic()?" "+oc.getComproNroAntic():"";
		 					row.addText(conceptos.get(conceptos.indexOf(oc.getConcepto())).getDescripcion()+antic);
		 					row.addText(fm.format(oc.getImporte()));
		 					if(!portlet_name.equals("tesoreria")){
		 						//HACER UNA MARCA EN EL BEAN PARA EVITAR ESTO...
		 						if((portlet_name.equals("farmacia") && oc.getConcepto().getId()==224) ||(portlet_name.equals("uoma")&& oc.getConcepto().getId()==915 || oc.getConcepto().getId()==82 ||oc.getConcepto().getId()==988 || oc.getConcepto().getId()==891 || oc.getConcepto().getId()==962 || 
		 								oc.getConcepto().getId()==964 || oc.getConcepto().getId()==972 || oc.getConcepto().getId()==976 || oc.getConcepto().getId()==894 || oc.getConcepto().getId()==892)){
				 					row.addText(oc.getRemuneracionTotal()!=null?oc.getRemuneracionTotal().toString():"");
				 					row.addText(oc.getCantidadEmpleados()!=null?oc.getCantidadEmpleados().toString():"");
				 					row.addText(oc.getPeriodo()!=null?oc.getPeriodoAsString():"");
				 					row.addText(oc.getBoletaNro()!=null?String.valueOf(oc.getBoletaNro()):"");
		 						}else{
		 							row.addText("");
				 					row.addText("");
				 					row.addText("");
				 					row.addText("");
		 						}
		 					}
		 					totalImporte = totalImporte.add(ocs.get(i).getImporte());
							// Action
		 					if (showABMButtons){
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
