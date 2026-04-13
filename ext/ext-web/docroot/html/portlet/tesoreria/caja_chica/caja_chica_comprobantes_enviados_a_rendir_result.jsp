<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>


<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();




List<String> headerNames = new ArrayList<String>();
headerNames.add("Fecha");
headerNames.add("Comprobante");
headerNames.add("Concepto");
headerNames.add("Importe");
headerNames.add("Saldo");
headerNames.add("Aprobado");




SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "comprobante-no-encontrado"));
NumberFormat formatter = new DecimalFormat("#0.00");     
List<ComprobanteCajaChica> comprobantes= (List<ComprobanteCajaChica>) request.getSession().getAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION);					
if (comprobantes != null && !comprobantes.isEmpty()){
	int total = comprobantes.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);
	
	List resultRows = searchContainer.getResultRows();

	
	for (int i = 0; i < comprobantes.size(); i++) {	    
		ComprobanteCajaChica liq = (ComprobanteCajaChica) comprobantes.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		row.addText(liq.getFechaEmisionAsString());
		row.addText(liq.getTipoComprobante()+" "+ liq.getLetraComprobante()+" "+liq.getPtoVenta()+"-"+liq.getNroComprobante()+ "  "+
		liq.getAcreedorEmpresa().getCuit()+" "+liq.getAcreedorEmpresa().getDescripcion() );
		row.addText(liq.getConceptos().get(0).getConceptoComprobante().getDescripcion());
		row.addText(liq.getImporteComprobante().toString());
		row.addText(formatter.format(liq.getImporteComprobanteOriginal()));
		
		StringBuilder sb=new StringBuilder();
		sb.append("<input type=\"checkbox\"");
		sb.append("checked=\"checked\"");
		sb.append("name=\"aprobado\"");		
		sb.append("id=\"");
		sb.append("check-"+liq.getId());
	    sb.append("\" value=\"");
		sb.append(liq.getId());
		sb.append("\"/>");		
		
		row.addText(sb.toString());
		
		resultRows.add(row);
	}


}
%>
	
 		
<script type="text/javascript">


</script>
	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

