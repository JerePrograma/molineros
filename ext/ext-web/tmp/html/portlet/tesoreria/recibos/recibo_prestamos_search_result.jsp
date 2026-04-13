<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="ar.com.ospim.hoteles.beans.Prestamo" %>

<portlet:defineObjects/>
			<%
			DecimalFormat fm = new DecimalFormat("###0.00");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
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
			Double totalPrestamos = 0D;
				Recibo recibo= (Recibo)request.getSession().getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);					
			
					boolean showABMButtons = PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA)||PermissionUtil.userContainsRole(user,WebKeysTesoreria.ROL_ABM_TESORERIA_UOMA);				
					PortletURL portletURL = renderResponse.createRenderURL();				
					String orderByCol = ParamUtil.getString(request, "orderByCol");
					String orderByType = ParamUtil.getString(request, "orderByType");					
			 		List<String> headerNames = new ArrayList<String>();
			 		headerNames.add("Número");
			 		headerNames.add("Total");
			 		headerNames.add("Fecha");
			 		headerNames.add("Importe");
			 		if(showABMButtons) { 
						headerNames.add("Borrar");
					}							
					SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
					SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
					"Préstamos no encontrados");
				
				 	List<ReciboPrestamo> ocs = null;
					if (recibo != null){
						ocs = recibo.getReciboPrestamos();
					}
					if(null!=ocs){
				 		//Seteo el total de la lista.
					 	int total = ocs.size();
					 	searchContainer.setTotal(total);
					 	//resultsPrueba2 = ListUtil.subList(resultsPrueba2, searchContainer.getStart(),searchContainer.getEnd());
				 				List resultRows = searchContainer.getResultRows();
					 	for (int i = 0; i < ocs.size(); i++) {
					 		Prestamo oc =  ocs.get(i).getPrestamo();
		 					ResultRow row = new ResultRow(oc, oc.getId(), i);
		 					row.addText(oc.getId()!=null?oc.getId().toString():"");
		 					row.addText(oc.getTotal()!=null?fm.format(oc.getTotal()):"");
		 					row.addText(oc.getAcuerdoFecha() !=null?sdf.format(oc.getAcuerdoFecha()):"");
		 					row.addText(oc.getMonto()!=null?fm.format(oc.getMonto()):"");
		 					
		 					
		 					totalPrestamos += ocs.get(i).getPrestamo().getMonto();
							// Action
		 					if (showABMButtons){
		 						StringBuilder sb= new StringBuilder();
			 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
			 					sb.append(themeDisplay.getPathThemeImages());
			 					sb.append("/common/delete.png\" onClick=\"javascript:borrarPrestamo('");
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
	document.getElementById("total_prestamos").value = "<%= totalPrestamos.toString()%>";
</script>		
