<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();

NumberFormat formatter = new DecimalFormat("#0.00");


List<String> headerNames = new ArrayList<String>();
headerNames.add("Concepto");
headerNames.add("Importe");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "comprobante-no-encontrado"));

Map<String,Double> comprobantes= (Map<String,Double>) request.getSession().getAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO_COMPROBANTE);					
if (comprobantes != null && !comprobantes.isEmpty() ){
	int total = comprobantes.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);
	
	List resultRows = searchContainer.getResultRows();

	Iterator it = comprobantes.entrySet().iterator();
	Integer i=0;
	while (it.hasNext()) {
	   Map.Entry e = (Map.Entry)it.next();
	   ResultRow row = new ResultRow(e,new Integer(1+i), i);
	   PortletURL rowURL = renderResponse.createRenderURL();
	   rowURL.setWindowState(WindowState.MAXIMIZED);
	
	   row.addText(e.getKey().toString());
	   row.addText(formatter.format(e.getValue()));
	   resultRows.add(row);
	}
	
}
%>
	
 		
<script type="text/javascript">

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

