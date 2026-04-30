<%@page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/html/portlet/prestadores/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	PortletURL portletURL = renderResponse.createRenderURL();

	List<PrestadorPlan> planesPrestador = (ArrayList<PrestadorPlan>) 
												request.getSession().getAttribute(WebKeysLiquidaciones.PLANES_PRESTADOR_EN_SESSION);
	
	List<String> headerNames = new ArrayList<String>();
	headerNames.add("Plan");
	headerNames.add("Vigencia Desde");
	headerNames.add("Vigencia Hasta");
	headerNames.add("Eliminar");
	
	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
			LanguageUtil.get(pageContext, "no-prest-plan-were-found"));
	
	
	if (planesPrestador != null && !planesPrestador.isEmpty()){
		int total = planesPrestador.size();
		searchContainer.setTotal(total);
		List resultRows = searchContainer.getResultRows();
		
	 	for (int i = 0; i < planesPrestador.size(); i++) {
	 		PrestadorPlan planDelPrestador = (PrestadorPlan) planesPrestador.get(i);
		 	
		 	ResultRow row = new ResultRow(planDelPrestador,new Integer(1+i), i);
		  		
			row.addText(planDelPrestador.getPlan().getDescripcion());
			row.addText(sdf.format(planDelPrestador.getVigencia_desde()));
			if(planDelPrestador.getVigencia_hasta() != null){
				row.addText(sdf.format(planDelPrestador.getVigencia_hasta()));
			}else{
				row.addText("");
			}
		 	StringBuilder sb=new StringBuilder(); 
	  		if(planDelPrestador.getEstado() == null || !planDelPrestador.getEstado().equals(PrestadorPlan.ESTADOS.BAJA)){
			 	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"borrar plan\" src=\"");
		 		sb.append(themeDisplay.getPathThemeImages());
		 		sb.append("/common/delete.png\" onClick=\"javascript:borrarPlan('");
		 		sb.append(String.valueOf(planDelPrestador.getId()));
		 		sb.append("');\" />");
	  		}else{
	  			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img height='16'  width='16' src='/html/themes/classic/images/common/close.png'/>");
	  		}
	 		
	 		row.addText(sb.toString()); 
	 		
			resultRows.add(row);
	 	}
	}
%>

<liferay-ui:error exception="<%=PlanPrestadorDuplicadoException.class %>" message="prest-plan-vig-duplicada" />

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />	

<script>
function borrarPlan(idPrestPlan){
	var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/prestadores/borrar_prestador_plan';
	url = url+'&idPrestPlan='+idPrestPlan;
		jQuery("#<portlet:namespace />lista_planes").load(url);   
	}
</script>
