<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="ar.com.ospim.global.beans.ItemSubdiarioIngreso" %>
<%@ page import="java.text.DecimalFormat" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

Map<String, ItemSubdiarioIngreso> cuentas = (TreeMap<String, ItemSubdiarioIngreso>) session.getAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION");
String leyenda= (String)session.getAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO");
Double totalImporte = 0D;
DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
List<String> headerNames = new ArrayList<String>();
headerNames.add("Cuenta");
headerNames.add("Descripción");
headerNames.add("Importe");
if(!"DECLARADO".equalsIgnoreCase(leyenda)){
 headerNames.add("Cpte");
}


SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "cuentas-no-encontrado"));
					
if (cuentas != null && !cuentas.isEmpty()){
	int total = cuentas.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	int i=0;
	for (ItemSubdiarioIngreso liq : cuentas.values()) {
		ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		
        row.addText(liq.getNumeroCuenta());
        row.addText(liq.getCuenta());
        String str=String.format("%1$20s",df.format(liq.getImporte().doubleValue()).trim());
        row.addText(str);
        
        if(!"DECLARADO".equalsIgnoreCase(leyenda)){
           StringBuilder sb=new StringBuilder();
           sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Explorar cuenta\" src=\"");
           sb.append(themeDisplay.getPathThemeImages());
	       sb.append("/common/search.png\" onClick=\"javascript:explosionCpte('");
	       sb.append(liq.getNumeroCuenta() );
	       sb.append("','");
	       sb.append(leyenda);
	       sb.append("');\"");
           sb.append(" title=\"Explorar\"");
	       sb.append("/>");
	       row.addText(sb.toString());
        }
	    
		resultRows.add(row);
		i++;
		totalImporte += liq.getImporte().doubleValue();
	}
	
}
%>
	
 		
<script type="text/javascript">
function explosionCpte(cuenta,leyenda){

	jQuery('#<portlet:namespace />buscando').show();
	var busquedaNom = {"cuenta":cuenta,"leyenda":leyenda,"cmd":"nivel_2"};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/uoma/controlIngresosEgresosExplosion" /></portlet:renderURL>';
 	jQuery('#<portlet:namespace />controlIngresosEgresosSDA').show();
	jQuery('#<portlet:namespace />controlIngresosEgresosSDA').load(url,busquedaNom, function(){
														jQuery('#<portlet:namespace />buscando').hide();      
	});	
	
}


function <portlet:namespace />explosionExcelN1(leyenda){
 	window.location.href ='/xlsservlet/?reporte=CONTROL_INGRESOS_EGRESOS_NIVEL_1'
		+'&leyenda='+leyenda;		
}
</script>
<br>	
<label style="color:blue; background-color: #D1F2EB;font-weight:bold;font-size:20px"><%=leyenda + ": "+df.format(totalImporte) %></label>
<input id="<portlet:namespace />planilla" value="Planilla" 
					              title="<liferay-ui:message key="Excel" />" type="button" 
					              style="background: #AECFC8"
					              onclick="<portlet:namespace />explosionExcelN1('<%=leyenda%>')"/>
<br>

<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>