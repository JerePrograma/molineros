<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>

<%@ page import="ar.com.ospim.tesoreria.WebKeysTesoreria" %>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="ar.com.ospim.global.beans.Parentesco" %>
<%@ page import="ar.com.ospim.global.beans.Plan" %>
<%@ page import="ar.com.ospim.tesoreria.beans.PrecioPlanSuperador" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="ar.com.uoma.facturacion.Producto" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.NumberFormat" %>

<script type="text/javascript" src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<portlet:defineObjects/>
			<%
			String portlet_name = ParamUtil.getString(request, "portlet_name");
			Integer entidad = WebKeysGlobal.OSPIM;
			if(renderResponse.getNamespace().equals("_UOM_1_")){
				entidad = WebKeysGlobal.UOMA;
				portlet_name = "uoma";
			}
			
			if(renderResponse.getNamespace().equals("_FAR_1_")){
				entidad = WebKeysGlobal.UOMA;
				portlet_name = "farmacia";
			}
			
			if(renderResponse.getNamespace().equals("_TES_1_")){
				entidad = WebKeysGlobal.UOMA;
				portlet_name = "tesoreria";
			}
			
			PrecioPlanSuperador precio=(PrecioPlanSuperador)request.getSession().getAttribute(WebKeysTesoreria.PRECIO_EN_SESSION);
			
			Collections.sort(precio.getValores(), new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					return ((Comparable<Integer>) ((Producto) (o1)).getId())
							.compareTo(((Producto) (o2)).getId());
				}
			});
		
			String esEditableStr = ParamUtil.getString(request, "esEdicion");
			if (esEditableStr == null || esEditableStr.equals("false")){
				esEditableStr ="false";
			}
			boolean esEdicion = Boolean.parseBoolean(esEditableStr);
		

			PortletURL portletURLPreAutMed = renderResponse.createRenderURL();
	 		List<String> headerNamesPreAutMed = new ArrayList<String>();
	 		headerNamesPreAutMed.add("Orden de aplicación");
	 		headerNamesPreAutMed.add("Importe");
	 		headerNamesPreAutMed.add("Borrar");
			SearchContainer searchContainer= new SearchContainer(renderRequest, null, null,
			SearchContainer.DEFAULT_CUR_PARAM,SearchContainer.DEFAULT_DELTA, portletURLPreAutMed, headerNamesPreAutMed,
			LanguageUtil.get(pageContext, "no hay valores cargados"));
		
			NumberFormat format2D = new DecimalFormat("#0.00");
			NumberFormat format0D = new DecimalFormat("#0");
			
			if(null!=precio.getValores()){
 				List resultRowsInspector = searchContainer.getResultRows();
 			 	for (int i = 0; i < precio.getValores().size(); i++) {
 			 		Producto detalle = precio.getValores().get(i);
	 					ResultRow row = new ResultRow(detalle, detalle.getId() , i);
//	 					row.addText(detalle.getNomenclador().getId_prestacion_string() );
	 					row.addText(String.valueOf(detalle.getId()) );
	 					row.addText(format2D.format(detalle.getPrecioUnitario()));
	 					
	 						StringBuilder sb= new StringBuilder();
		 					sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		 					sb.append(themeDisplay.getPathThemeImages());
		 					sb.append("/common/delete.png\" onClick=\"javascript:sacarValor('");
		 					sb.append(detalle.getId());
		 					sb.append("');\" />");
		 					row.addText(sb.toString());
		 					
	 					resultRowsInspector.add(row);
 			 		}
	 		}
 		%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<liferay-ui:search-iterator paginate="false"  searchContainer="<%=searchContainer%>" />
    <input type="hidden" name="<portlet:namespace />q_precio"
		id="<portlet:namespace />q_precio" value="<%=precio.getValores().size()%>" />

<script type="text/javascript">
function sacarValor(id){
		
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/<%=portlet_name%>/facturacion_precios'
		+	'&<%= Constants.CMD%>=' + 'sacarValor'
		+ '&idValor=' + id; 	
	
		jQuery('#<portlet:namespace />divValores').load(url, function() {});
	
return false;	
	
}	

</script>