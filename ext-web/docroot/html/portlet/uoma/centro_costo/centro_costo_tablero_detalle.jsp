<%@ include file="/html/portlet/uoma/init.jsp"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="ar.com.ospim.global.WebKeysGlobal" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto" %>
<%@ page import="ar.com.ospim.global.beans.Concepto" %>

<portlet:defineObjects/>
<%
PortletURL portletURL = renderResponse.createRenderURL();
CentroCosto centroCosto=(CentroCosto)request.getSession().getAttribute(WebKeysUOMA.CENTRO_COSTO_EN_EDICION);

String usuario_modi = user.getScreenName();
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//Double ejecucion = 0D;
String portlet_name=null;
Integer entidad = WebKeysGlobal.OSPIM;
portlet_name = "tesoreria";
if(renderResponse.getNamespace().equals("_UOM_1_")){
	portlet_name = "uoma";
	entidad = WebKeysGlobal.UOMA;
}

List<ComprobanteCajaChica> archivos=(List<ComprobanteCajaChica>)session.getAttribute(WebKeysUOMA.CENTRO_COSTO_COMPROBANTES);

List<String> headerNames = new ArrayList<String>();
headerNames.add("Fecha");
headerNames.add("Tipo");
headerNames.add("Letra");
headerNames.add("Pto.Venta");
headerNames.add("Nro");
headerNames.add("Cuit");
headerNames.add("Razon Social");
headerNames.add("Concepto");
headerNames.add("Importe");
headerNames.add("OP");

NumberFormat formatter = new DecimalFormat("###,###,###,###,##0.00");     

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null,
				SearchContainer.DEFAULT_CUR_PARAM,Integer.MAX_VALUE, portletURL, headerNames,
				LanguageUtil.get(pageContext, "centrocostodetalle-no-encontrado"));
int total=0;					
if (archivos != null && !archivos.isEmpty()){
	total = archivos.size();
	searchContainer.setTotal(total);
	
	pageContext.setAttribute("total", total);	
	
	List resultRows = searchContainer.getResultRows();
	
	
	
	for (int i = 0; i < archivos.size(); i++) {	    
		ComprobanteCajaChica liq = (ComprobanteCajaChica) archivos.get(i);
	 	ResultRow row = new ResultRow(liq,new Integer(1+i), i);
		PortletURL rowURL = renderResponse.createRenderURL();
		
		rowURL.setWindowState(WindowState.MAXIMIZED);
		row.addText(liq.getFechaEmisionAsString());
		row.addText(liq.getTipoComprobante());
		row.addText(liq.getLetraComprobante());
		row.addText(String.valueOf(liq.getPtoVenta()));
		row.addText(liq.getNroComprobante());
		row.addText(liq.getCuit());
		row.addText(liq.getAcreedorEmpresa().getRazon_soc());
		row.addText(liq.getConceptos().get(0).getConceptoComprobante().getDescripcion());
		row.addText(formatter.format(liq.getImporteComprobante().doubleValue()));
		row.addText(liq.getOrdenPago().toString());
		resultRows.add(row);
	}
}
%>
	
<form action="" method="get" name="<portlet:namespace />fm"> 
<fieldset class="block-labels">
		<legend><liferay-ui:message key="centro-costo" /></legend>
		
		<table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
			<tr>
			   <td><label><liferay-ui:message key="caja-chica-nombre" />:</label></td>
				<td><input id="<portlet:namespace />descripcionCentroCosto"
					name="<portlet:namespace />descripcionCentroCosto" size="145"
					maxlength="145" type="text"
					value='<%=centroCosto.getDescripcion()==null?"":centroCosto.getDescripcion() %>' />
				</td>
			</tr>	
			<tr>
			 <table class="lfr-table" style="border-collapse: separate; border-spacing: 5px;">
			  <tr>
			  <td><label><liferay-ui:message key="presupuesto" />:</label></td>
				<td><input id="<portlet:namespace />presupuestoCentroCosto"
					name="<portlet:namespace />presupuestoCentroCosto" size="20"
					maxlength="20" type="text" readonly="readonly"  style="background: #FFFFE0;text-align:right;font-weight:bold"
					value='<%=centroCosto.getPresupuesto()==null?"": formatter.format(centroCosto.getPresupuesto())%>' /></td>
			   
			    <td><label><liferay-ui:message key="ejecutado" />:</label></td>
				<td><input id="<portlet:namespace />ejecucionCentroCosto"
					name="<portlet:namespace />ejecucionCentroCosto" size="20"
					maxlength="20" type="text" readonly="readonly"  style="background: #FFFFE0;text-align:right;font-weight:bold;"
					value='<%=centroCosto.getEjecutado() ==null?"": formatter.format(centroCosto.getEjecutado())%>' /></td>
			   
			   <td><label><liferay-ui:message key="saldo" />:</label></td>
				<td><input id="<portlet:namespace />saldoCentroCosto"
					name="<portlet:namespace />saldoCentroCosto" size="20"  style="background: #FFFFE0;text-align:right;font-weight:bold;"
					maxlength="20" type="text" readonly="readonly"
					value='<%=formatter.format( (centroCosto.getPresupuesto() ==null?0: centroCosto.getPresupuesto() )
					          - (centroCosto.getEjecutado() ==null?0: centroCosto.getEjecutado() ) )   %>' /></td>
			   
			   </tr>
			   
			  </table> 
			</tr>
		</table>	
		
		
		<table>
			 <tr align="left">
			 <td>&nbsp;</td>
			 <td align="left">	
					    <input id="<portlet:namespace />atrasDetalle"
						value="<liferay-ui:message key="atras"/>"
						title="<liferay-ui:message key="atras" />"
						onClick="javascript: <portlet:namespace />atrasDetalleCentro();"
						type="button" />
			 </td>
			  <td>&nbsp;</td>
			  <td align="left">	
					    <input id="<portlet:namespace />exportarDetalle"
						value="Exportar"
						title="Exportar"
						onClick="javascript: <portlet:namespace />exportarDetalleCentro();"
						type="button" />
			 </td>
			 </tr>
		</table>
</fieldset>		

</form>

<div class="search-results">
	<c:choose>
		<c:when test="<%= total != 1 %>">
			<%= LanguageUtil.format(pageContext, "showing-x-results", total) %>
		</c:when>
		<c:otherwise>
			<%= LanguageUtil.format(pageContext, "showing-x-result", total) %>
		</c:otherwise>
	</c:choose>
	<liferay-util:include page="/html/portlet/uoma/centro_costo/paginador_centro_costo_detalle.jsp">
	</liferay-util:include>

</div>

	
<liferay-ui:search-iterator paginate="false" searchContainer="<%= searchContainer %>"/>
 		
<script type="text/javascript">

function <portlet:namespace />atrasDetalleCentro(){
	
	var params = "&<%= Constants.CMD %>=" + "atrasDetalle";
	url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"/>&struts_action=/<%=portlet_name%>/centro_costo_edicion';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);	
	
}

function <portlet:namespace />buscarComprobantes(){

	var offset_reg = jQuery('#<portlet:namespace />pagina_sel').val();
	
	var params = "&<%= Constants.CMD %>=" + "detalleComprobantes";
 	params+="&id_centro_costo=" + "<%=centroCosto.getId()%>";
 	params+="&usuario_modi=" +"<%=usuario_modi%>";
 	params+= "&entidad_centro="+"<%=entidad%>";
 	params+= "&pagina="+offset_reg;

 	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/uoma/centro_costo_edicion" /></portlet:renderURL>';
	url = url + params;
	document.<portlet:namespace />fm.method = 'post';
	submitForm(document.<portlet:namespace />fm, url);	
}

function <portlet:namespace />exportarDetalleCentro(){
		window.location.href ='/xlsservlet/?reporte=REPORTE_CENTROS_COSTOS_DETALLE&entidad_centro='+"<%=entidad%>";
}

</script>	


