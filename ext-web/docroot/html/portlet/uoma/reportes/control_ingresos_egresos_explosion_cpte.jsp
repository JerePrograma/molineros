<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="ar.com.ospim.global.beans.ItemSubdiarioIngreso" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<portlet:defineObjects/>

<%
PortletURL portletURL = renderResponse.createRenderURL();
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
List<ItemSubdiarioIngreso> cuentas = (List<ItemSubdiarioIngreso>) session.getAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_CPTE");
String leyenda= (String)session.getAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO_CPTE");
Double totalImporte = 0D;
DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
List<String> headerNames = new ArrayList<String>();
headerNames.add("Fecha");
headerNames.add("CUIT");
headerNames.add("Razón Social");
headerNames.add("Descripción");

headerNames.add("Importe");



SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "cuentas-no-encontrado"));
					
if (cuentas != null && !cuentas.isEmpty()){
	int total = cuentas.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	int i=0;
	for (ItemSubdiarioIngreso liq : cuentas) {
		ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(LiferayWindowState.MAXIMIZED);
		row.addText(sdf.format(liq.getFecha()));
		row.addText(liq.getCuit());
		row.addText(liq.getRazonSocial());
        row.addText(liq.getComprobante());
        String str=String.format("%1$20s",df.format(liq.getImporte().doubleValue()).trim());
        row.addText(str);
        
       
		resultRows.add(row);
		i++;
		totalImporte += liq.getImporte().doubleValue();
	}
	
}
%>
	
 		
<script type="text/javascript">
function <portlet:namespace />explosionExcelN2(leyenda){
 	window.location.href ='/xlsservlet/?reporte=CONTROL_INGRESOS_EGRESOS_NIVEL_2'
		+'&leyenda='+leyenda;		
}
</script>
<br>	
<label style="color:blue; background-color: #D1F2EB;font-weight:bold;font-size:20px"><%=leyenda + ": "+df.format(totalImporte) %></label>
<input id="<portlet:namespace />planilla" value="Planilla" 
					              title="<liferay-ui:message key="Excel" />" type="button" 
					              style="background: #AECFC8"
					              onclick="<portlet:namespace />explosionExcelN2('<%=leyenda%>')"/>

<br>

<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>