<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="ar.com.uoma.beans.EmpresaSituacionFinanciera" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="ar.com.ospim.tesoreria.beans.CuentaCorriente"%>
<%@ page import="ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente"%>
<%@ page import="ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente.SaldoInicial"%>
<%@ page import="ar.com.ospim.util.DateUtils"%>
<%@ page import="ar.com.ospim.tesoreria.beans.CuentaCorriente.Informacion"%>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collections" %>

<portlet:defineObjects/>
<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>
<%
PortletURL portletURL = renderResponse.createRenderURL();

String leyenda= (String)session.getAttribute("CONTROL_DECLARADOS_N1_LEYENDA");
List<EstadoInicialCuentaCorriente> saldoIni=(List<EstadoInicialCuentaCorriente>)session.getAttribute("CONTROL_DECLARADOS_N1_SALDO_INICIAL");

List<CuentaCorriente> ctas=(List<CuentaCorriente>)session.getAttribute("CONTROL_DECLARADOS_N1_MOVIMIENTOS");
Date fIni= (Date)session.getAttribute("CONTROL_DECLARADOS_N1_FECHA_INICIAL");
Date fFin= (Date)session.getAttribute("CONTROL_DECLARADOS_N1_FECHA_FINAL");

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


//Calculo saldo Inicial
BigDecimal saldoInicial = BigDecimal.ZERO;
List<SaldoInicial> saldosIniciales = new ArrayList<SaldoInicial>();
for (EstadoInicialCuentaCorriente estado : saldoIni) {
	if (estado.getEmpresa().getCuit()
			.equals(ctas.get(0).getEmpresa().getCuit())) {
		for (SaldoInicial saldoInicialEmpresa : estado
				.getSaldosIniciales()) {
			saldosIniciales.add(saldoInicialEmpresa);
		}
	}
	Collections.sort(saldosIniciales);
}
boolean calcularSaldoIni = false;
if (saldosIniciales != null && saldosIniciales.size() > 0) {
	SaldoInicial min = (SaldoInicial) Collections
			.min(saldosIniciales);
	if (DateUtils.compararFechasTruncarEnDia(min.getFecha(),
			fIni) < 0) {
		calcularSaldoIni = true;
	}
}
if (calcularSaldoIni) {

 
  SaldoInicial minSaldoIni = null;
  if (saldosIniciales != null && saldosIniciales.size() > 0) {
	minSaldoIni = (SaldoInicial) Collections.min(saldosIniciales);
	if (DateUtils.compararFechasTruncarEnDia(minSaldoIni.getFecha(),
			fIni) < 0) {
		saldoInicial = minSaldoIni.getImporte();
	}
  }

  Iterator<Informacion> it = ctas.get(0).getInfo().iterator();
  boolean stop = false;
  while (it.hasNext() && !stop) {
	Informacion l = it.next();
	// el saldo inicial a la fecha XX es al ppio de ese dia.
	// (el saldo inicial no incluye los movimientos del dia XX)
	if (DateUtils.compararFechasTruncarEnDia(l.getFecha(), fIni) < 0) {
		if (l.getDebitoCredito().equals("D")) {
			saldoInicial = saldoInicial.subtract(l.getImporte());
		} else {
			saldoInicial = saldoInicial.add(l.getImporte());
		}
	} else {
		stop = true;
	}
  }

}
%>
	
 		
<script type="text/javascript">
var popupMD1;
function <portlet:namespace />explosionExcelEmpresa(leyenda){
 	window.location.href ='/xlsservlet/?reporte=CONTROL_INGRESOS_EGRESOS_EMPRESAS_1'
		+'&leyenda='+leyenda;		
}
</script>
<br>	
<label style="color:blue; background-color: #D1F2EB;font-weight:bold;font-size:20px"><%=leyenda==null || "null".equalsIgnoreCase(leyenda)?"":leyenda%></label>
<input id="<portlet:namespace />planilla" value="Planilla" 
					              title="<liferay-ui:message key="Excel" />" type="button" 
					              style="background: #AECFC8"
					              onclick="<portlet:namespace />explosionExcelEmpresa('<%=leyenda%>')"/>
<br>
<br>
<label style="font-weight:bold;font-size:12px">Saldo Inicial: <%=String.format("%1$20s",df.format(saldoInicial.doubleValue()).trim()) %></label>	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainerD %>"/>