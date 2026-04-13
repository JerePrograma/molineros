<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@ include file="/html/portlet/tesoreria/init.jsp"%>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.CajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica" %>
<%@ page import="ar.com.ospim.tesoreria.WebKeysCajaChica" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map"%>

<%

CajaChica cajaChica= (CajaChica)request.getSession().getAttribute(WebKeysCajaChica.CAJA_CHICA_EN_EDICION);
List<ComprobanteCajaChica>  comprobantes= cajaChica.getComprobantesPendientesRendicion();
request.getSession().removeAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION);
request.getSession().removeAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO);
request.getSession().removeAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO_COMPROBANTE);

String portlet_name = ParamUtil.getString(request, "portlet_name");
if (portlet_name == null || portlet_name.trim().equals("")){
	portlet_name = "tesoreria";
}


Integer entidad = WebKeysGlobal.OSPIM;
if(renderResponse.getNamespace().equals("_UOM_1_")){
	entidad = WebKeysGlobal.UOMA;
	portlet_name = "uoma";
}

Double saldo =cajaChica.getSaldo();
Map<String, Double> map=new HashMap<String, Double>();

Map<String, Double> mapCpte=new HashMap<String, Double>();

if (comprobantes != null && !comprobantes.isEmpty()){
	for (int i = comprobantes.size()-1; i >= 0; i--) {	    
		comprobantes.get(i).setImporteComprobanteOriginal( BigDecimal.valueOf(saldo));
		if(entidad == WebKeysGlobal.OSPIM){
		  saldo += comprobantes.get(i).getImporteComprobante().doubleValue();
		}  
		if(entidad == WebKeysGlobal.UOMA){
			  saldo -= comprobantes.get(i).getImporteComprobante().doubleValue();
		} 
		Double total = map.get(comprobantes.get(i).getConceptos().get(0).getConceptoComprobante().getDescripcion());
		if(total==null){
			map.put(comprobantes.get(i).getConceptos().get(0).getConceptoComprobante().getDescripcion(), comprobantes.get(i).getImporteComprobante().doubleValue());
		}else{
			map.put(comprobantes.get(i).getConceptos().get(0).getConceptoComprobante().getDescripcion(), comprobantes.get(i).getImporteComprobante().doubleValue()+total);
		}
		
		Double ipte = mapCpte.get(comprobantes.get(i).getTipoComprobante()+" "+ comprobantes.get(i).getLetraComprobante()+" "+comprobantes.get(i).getPtoVenta()+"-"+comprobantes.get(i).getNroComprobante()+ "  "+
				comprobantes.get(i).getAcreedorEmpresa().getCuit()+" "+comprobantes.get(i).getAcreedorEmpresa().getDescripcion());
		if(ipte==null){
			mapCpte.put(comprobantes.get(i).getTipoComprobante()+" "+ comprobantes.get(i).getLetraComprobante()+" "+comprobantes.get(i).getPtoVenta()+"-"+comprobantes.get(i).getNroComprobante()+ "  "+
					comprobantes.get(i).getAcreedorEmpresa().getCuit()+" "+comprobantes.get(i).getAcreedorEmpresa().getDescripcion(), comprobantes.get(i).getImporteComprobante().doubleValue());
		}else{
			mapCpte.put(comprobantes.get(i).getTipoComprobante()+" "+ comprobantes.get(i).getLetraComprobante()+" "+comprobantes.get(i).getPtoVenta()+"-"+comprobantes.get(i).getNroComprobante()+ "  "+
					comprobantes.get(i).getAcreedorEmpresa().getCuit()+" "+comprobantes.get(i).getAcreedorEmpresa().getDescripcion(), comprobantes.get(i).getImporteComprobante().doubleValue()+ipte);
		}
		
	}
	request.getSession().setAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION, comprobantes);
	request.getSession().setAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO, map);
	request.getSession().setAttribute(WebKeysCajaChica.COMPROBANTES_CAJA_CHICA_PENDIENTES_RENDICION_AGRUPADO_COMPROBANTE, mapCpte);
}
%>

<script type="text/javascript"
	src="/html/jqueryui/ui/ui.datepicker-es.js"></script>

<fieldset class="block-labels">
		<legend>Ultimos Movimientos</legend>

		<table class="lfr-table" width="100%" >
		    <tr>
		      <td>
		         <input type="button" value="Reporte" onClick="<portlet:namespace />reporteUltimosMovimientosCajaChica(<%=cajaChica.getId()%>,<%=entidad%>);" />
		         <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		             <a href="javascript:void(0)" onclick="help(event, 'helpUltMovReporte')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		        <%}%>
		      </td>
		      <td>
                <%if("uoma".equalsIgnoreCase(portlet_name)){%>
                   <input id="<portlet:namespace />exportar-imagenes" value="Exportar Imágenes" 
					title="Exportar Imágenes" type="button"
					onClick="<portlet:namespace />exportarImagenes(<%=cajaChica.getId()%>,<%=entidad%>);" /> 
                <%}%>					
              </td>
		    </tr>
			<tr>
			   <td>
			    <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		             <a href="javascript:void(0)" onclick="help(event, 'helpUltMov')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		        <%}%>
			    <liferay-util:include page="/html/portlet/tesoreria/caja_chica/caja_chica_ultimos_movimientos_result.jsp"></liferay-util:include>
			   </td>
			</tr>   
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
		
</fieldset>

<table>
<tr>
<td>

<fieldset class="block-labels">
		<legend>Agrupados por Comprobante</legend>

		<table class="lfr-table">
			<tr>
			   <td>
			    <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		             <a href="javascript:void(0)" onclick="help(event, 'helpUltMovAgrup')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		        <%}%>
			     <liferay-util:include page="/html/portlet/tesoreria/caja_chica/caja_chica_ultimos_movimientos_comprobantes_agrupados_result.jsp"></liferay-util:include>
			   </td>
			</tr>   
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
		
</fieldset>
</td>

<td colspan="3">&nbsp;</td>

<td align="right">

<fieldset class="block-labels">
		<legend>Agrupados por Concepto</legend>

		<table class="lfr-table">
			<tr>
			   <td>
			    <%if("uoma".equalsIgnoreCase(portlet_name)){%>
		             <a href="javascript:void(0)" onclick="help(event, 'helpUltMovAgrup')"><img style="height: 16px; width: 16px" src="/html/images/help.png" title="Ayuda" alt="Ayuda"/></a>&nbsp;
		        <%}%>
			     <liferay-util:include page="/html/portlet/tesoreria/caja_chica/caja_chica_ultimos_movimientos_agrupados_result.jsp"></liferay-util:include>
			   </td>
			</tr>   
			<tr>
				<td>&nbsp;</td>
			</tr>
		</table>	
		
</fieldset>
</td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

</tr>
</table>



		

<div id="helpUltMov" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
 En esta lista, se mostrarán todos los comprobantes previamente ingresados y pendientes de reposición por parte de la Tesorería Central.
</div>
<div id="helpUltMovAgrup" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 100px; left: 300px">
 En esta lista, se mostrará un resumen de los conceptos de gasto utilizados y la sumatoria total de cada uno para todos los comprobantes pendientes.
</div>
<div id="helpUltMovReporte" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 100px; left: 300px">
 Mediante el botón reporte, se podrá generar una planilla de cálculo con los datos de los comprobantes ingresados y pendientes.
</div>
<div id="helpUltMovEdit" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  Mediante este acceso, se podrán efectuar todo tipo de modificaciones sobre el comprobante previamente cargado.
</div>
<div id="helpUltMovDelete" class="containerPlus draggable {buttons:'c', skin:'default', width:'500',title:'Ayuda',closed:'true'}" style="top: 200px; left: 300px">
  Mediante este acceso, se podrá eliminar el comprobante previamente cargado.
</div>


<script type="text/javascript">

	function <portlet:namespace />reporteUltimosMovimientosCajaChica(idCajaChica,entidad){
		window.location.href ='/xlsservlet/?reporte=REPORTE_ULTIMOS_COMPROBANTES_CAJA_CHICA'			
			+'&id_caja_chica='+idCajaChica
			+'&entidad='+entidad;	
		
	}	
	
	

	function <portlet:namespace />exportarImagenes(idCajaChica,entidad){
		window.location.href ='/txtservlet/?reporte=CAJA_CHICA_EXPORTAR_IMAGENES'			
			+'&id_caja_chica='+idCajaChica
			+'&entidad='+entidad;	
		
	}	
	
</script>
