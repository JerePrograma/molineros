<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="ar.com.uoma.beans.EmpresaSituacionFinanciera" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

Map<String, EmpresaSituacionFinanciera> cuentas = (TreeMap<String, EmpresaSituacionFinanciera>) session.getAttribute("CONTROL_DECLARADOS");
String leyenda= (String)session.getAttribute("CONTROL_DECLARADOS_TIPO");
Date fechaInicial=(Date)session.getAttribute("CONTROL_DECLARADOS_PERIODO");
Double totalImporte = 0D;
DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

List<String> headerNames = new ArrayList<String>();
headerNames.add("CUIT");
headerNames.add("Descripción");
headerNames.add("Declarado");
headerNames.add("Pagado");
headerNames.add("Pendiente de Pago");
headerNames.add("Estimado");
headerNames.add("");


SearchContainer searchContainerD = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "empresas-no-encontrado"));
					
if (cuentas != null && !cuentas.isEmpty()){
	int total = cuentas.size();
	searchContainerD.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainerD.getResultRows();
	
	int i=0;
	for (EmpresaSituacionFinanciera liq : cuentas.values()) {
		ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		rowURL.setWindowState(WindowState.MAXIMIZED);
		
		
        row.addText(liq.getCuit());
        row.addText(liq.getRazonSoc());
        String str=String.format("%1$20s",df.format(liq.getTotal().doubleValue()).trim());
        row.addText(str);
        String strPagado=String.format("%1$20s",df.format(liq.getTotalPagado().doubleValue()).trim());
        row.addText(strPagado);
        String strPendientePago=String.format("%1$20s",df.format( liq.getTotal().doubleValue() - liq.getTotalPagado().doubleValue()>0D?liq.getTotal().doubleValue() - liq.getTotalPagado().doubleValue():0D ).trim());
        row.addText(strPendientePago);
        String strEstimado=String.format("%1$20s",df.format(liq.getEstimado().doubleValue()).trim());
        row.addText(strEstimado);
        
        StringBuilder sb=new StringBuilder();
        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;<img alt=\"Cuenta Corriente\" src=\"");
        sb.append(themeDisplay.getPathThemeImages());
	    sb.append("/common/search.png\" onClick=\"javascript:explosionEmpresaCtaCte('");
	    sb.append(liq.getCuit() );
	    sb.append("','");
	    sb.append(sdf.format(fechaInicial));
	    sb.append("');\"");
        sb.append(" title=\"Cta.Cte\"");
	    sb.append("/>");
	    row.addText(sb.toString());
        
		resultRows.add(row);
		i++;
	}
	
}
%>
	
 		
<script type="text/javascript">
var popupMD1;
function <portlet:namespace />explosionExcelEmpresa(leyenda){
 	window.location.href ='/xlsservlet/?reporte=CONTROL_INGRESOS_EGRESOS_EMPRESAS_1'
		+'&leyenda='+leyenda;		
}

function explosionEmpresaCtaCte(cuit,fechaIni){

	var busquedaNom = {"cuit":cuit,"fechaini":encodeURI(fechaIni),"cmd":"declarados_nivel_1"};
 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/uoma/controlIngresosEgresosExplosion" /></portlet:renderURL>';
 	/*
 	jQuery('#<portlet:namespace />/>divControlIngresosEgresosCtaCte').show();
 	
	jQuery('#<portlet:namespace />/>divControlIngresosEgresosCtaCte').load(url,busquedaNom, function(){
	});
	*/
	
	if(popupMD1==null)
	    popupMD1 = Liferay.Popup({title:"Cuenta Corriente",modal:true,width:900,position:[0,5],onClose: function() { popupMD1 = null;}});
	
	jQuery(popupMD1).load(url,busquedaNom,function(){
								jQuery('#<portlet:namespace />buscando').hide();
	});
   
	
}
</script>
<br>	
<label style="color:blue; background-color: #D1F2EB;font-weight:bold;font-size:20px"><%=leyenda==null || "null".equalsIgnoreCase(leyenda)?"":leyenda%></label>
<input id="<portlet:namespace />planilla" value="Planilla" 
					              title="<liferay-ui:message key="Excel" />" type="button" 
					              style="background: #AECFC8"
					              onclick="<portlet:namespace />explosionExcelEmpresa('<%=leyenda%>')"/>
<br>

<%=pageContext.getAttribute("total")!= null?"Total Filas encontradas " + pageContext.getAttribute("total"):""%>
	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainerD %>"/>