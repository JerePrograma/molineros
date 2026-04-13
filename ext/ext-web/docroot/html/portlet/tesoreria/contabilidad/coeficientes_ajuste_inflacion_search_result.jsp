<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="java.util.List"%>
<%@ page import="ar.com.ospim.tesoreria.beans.contabilidad.CoeficienteAjusteInflacion"%>
<%@ page import="ar.com.ospim.global.FechaMenorACierreContableException"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>
<liferay-ui:error
	exception="<%= FechaMenorACierreContableException.class %>"
	message="asiento-menor-fecha-contable" />

<portlet:defineObjects />
<%
NumberFormat formatter = new DecimalFormat("#0.000000");
String portlet_name = ParamUtil.getString(request, "portlet_name");
	
	if (portlet_name == null || portlet_name.trim().equals("")){
		portlet_name = "tesoreria";
	}
	if(renderResponse.getNamespace().equals("_FAR_1_")){
		portlet_name = "farmacia";
	}  
	if(renderResponse.getNamespace().equals("_UOM_1_")){
		portlet_name = "uoma";
	}  
	
%>
<%
		List<CoeficienteAjusteInflacion> coeficientes=(List<CoeficienteAjusteInflacion>) portletSession
				.getAttribute(WebKeysTesoreria.COEFICIENTES_AJUSTE_INFLACION_EN_SESSION,
					PortletSession.APPLICATION_SCOPE);

        if(coeficientes!=null){
           Collections.sort(coeficientes, new Comparator<Object>() {
	           public int compare(Object o1, Object o2) {
		       return ((Comparable<Integer>) ((CoeficienteAjusteInflacion) (o1)).getPeriodo())
				    .compareTo(((CoeficienteAjusteInflacion) (o2)).getPeriodo());
	            }});
        }
		
		PortletURL portletURL = renderResponse.createRenderURL();				
		String orderByCol = ParamUtil.getString(request, "orderByCol");
		String orderByType = ParamUtil.getString(request, "orderByType");
 		List<String> headerNames = new ArrayList<String>();
 		headerNames.add("Período");
 		headerNames.add("Coeficiente");
 		headerNames.add("Editar/Borrar");
		SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
		SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
		LanguageUtil.get(pageContext, "no-coeficientes-were-found"));
	
		if(null!=coeficientes){
	 								 	
	 		//Seteo el total de la lista.
		 	int total = coeficientes.size();
		 	searchContainer.setTotal(total);

		 	
		 	List resultRows = searchContainer.getResultRows();
		 	for (int i = 0; i < coeficientes.size(); i++) {
		 		CoeficienteAjusteInflacion coeficiente = (CoeficienteAjusteInflacion) coeficientes.get(i);
				ResultRow row = new ResultRow(coeficiente,coeficiente.getPeriodo() * 100+ coeficiente.getEntidad(), i);
 				PortletURL rowURL = renderResponse.createRenderURL();					 						 				
 				rowURL.setWindowState(WindowState.MAXIMIZED);		 
 				String struts_action="/"+portlet_name+"/coeficientes_ajuste_inflacion";
 				rowURL.setParameter("struts_action",struts_action);
				rowURL.setParameter("entidad", String.valueOf(coeficiente.getEntidad()));
				rowURL.setParameter("periodo", String.valueOf(coeficiente.getPeriodo()));
				rowURL.setParameter("coeficiente", formatter.format(coeficiente.getCoeficiente()));
				rowURL.setParameter("cmd","edit");
				
 				row.addText(coeficiente.getPeriodo().toString(), rowURL);
 				row.addText(String.valueOf(coeficiente.getCoeficiente()), rowURL);
 				row.addJSP( "left", SearchEntry.DEFAULT_VALIGN, "/html/portlet/tesoreria/contabilidad/editar_borrar_coeficiente_ajuste_inflacion.jsp");
 				resultRows.add(row);
		 	}
	 	}
%>

<liferay-ui:search-iterator paginate="false"
	searchContainer="<%= searchContainer %>" />

<script type="text/javascript">
</script>
