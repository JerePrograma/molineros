<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.global.beans.PlanCuentasSSS"%>
<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String usuario_modi = user.getScreenName();

PlanCuentasSSS cuenta = (PlanCuentasSSS)request.getSession().getAttribute("cuentaSSS");
List<PlanCuentas>  pcuentas= new ArrayList<PlanCuentas>();

if(cuenta.getEquivalencias() !=null && cuenta.getEquivalencias().size()>0 ){
	pcuentas=cuenta.getEquivalencias();
}



List<String> headerNames = new ArrayList<String>();
headerNames.add("Cuenta");
headerNames.add("Eliminar");


SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "cuenta-no-encontrado"));

					
if (pcuentas != null && !pcuentas.isEmpty()){
	int total = pcuentas.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	for (int i = 0; i < pcuentas.size(); i++) {	    
		
		PlanCuentas liq = (PlanCuentas) pcuentas.get(i);
		
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		row.addText(liq.getNumero()+ " -- " + liq.getCuenta());
		
		
		StringBuilder sb= new StringBuilder();
			
		sb.append("<img alt=\"<liferay-ui:message key='eliminar'/>\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/delete.png\" onClick=\"javascript:borraCuentaAsociada('");
		sb.append(liq.getNumero());
		sb.append("');\" />");
		row.addText(sb.toString());
		
		resultRows.add(row);
	}

}
%>
	
 		
<script type="text/javascript">

</script>	
<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>

