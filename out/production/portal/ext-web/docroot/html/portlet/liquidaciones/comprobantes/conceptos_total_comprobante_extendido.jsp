<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ page import="ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat"%> 
<%
            NumberFormat formatter = new DecimalFormat("#0.00");
			
			String portlet_name=null;
			if (portlet_name == null || portlet_name.trim().equals("")){
				portlet_name = "liquidaciones";
			}
			if(renderResponse.getNamespace().equals("_UOM_1_")){
				portlet_name = "uoma";
			}
/*			
			Comprobante cAcumulado = (Comprobante) request.getSession().getAttribute(WebKeysLiquidaciones.COMPROBANTE_EXTENDIDO_ACUMULADO);
            if(cAcumulado==null){
				cAcumulado=new Comprobante();
				cAcumulado.setGravadoIVA27(BigDecimal.ZERO);
				cAcumulado.setGravadoIVA21(BigDecimal.ZERO);
				cAcumulado.setGravadoIVA105(BigDecimal.ZERO);
				cAcumulado.setExento(BigDecimal.ZERO);
			}
*/			
			
			List<ComprobanteConcepto> lista = (List<ComprobanteConcepto>) request.getSession()
					.getAttribute(WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS);
			
			Comprobante cAcumulado = new Comprobante();
			cAcumulado.setGravadoIVA27(BigDecimal.ZERO);
			cAcumulado.setGravadoIVA21(BigDecimal.ZERO);
			cAcumulado.setGravadoIVA105(BigDecimal.ZERO);
			cAcumulado.setIva27(BigDecimal.ZERO);
			cAcumulado.setIva21(BigDecimal.ZERO);
			cAcumulado.setIva105(BigDecimal.ZERO);
			cAcumulado.setExento(BigDecimal.ZERO);
			cAcumulado.setPercepcionIVA(BigDecimal.ZERO);
			cAcumulado.setPercepcionIIBB(BigDecimal.ZERO);
			cAcumulado.setOtrosTributos(BigDecimal.ZERO);
			cAcumulado.setImporteComprobante(BigDecimal.ZERO);
			if(lista!=null){
			for(ComprobanteConcepto x:lista) {
			  if(!x.isBorradoLogicamente()) {
				if(x.getTasaIva()==.27D) {
					cAcumulado.setGravadoIVA27(cAcumulado.getGravadoIVA27().add(x.getGravadoIVA()));
					cAcumulado.setIva27(cAcumulado.getIva27().add(x.getIva()));
				}
				if(x.getTasaIva()==.21D) {
					cAcumulado.setGravadoIVA21(cAcumulado.getGravadoIVA21().add(x.getGravadoIVA()));
					cAcumulado.setIva21(cAcumulado.getIva21().add(x.getIva()));
				}
				if(x.getTasaIva()==.105D) {
					cAcumulado.setGravadoIVA105(cAcumulado.getGravadoIVA105().add(x.getGravadoIVA()));
					cAcumulado.setIva105(cAcumulado.getIva105().add(x.getIva()));
				}
				if(x.getTasaIva()==.0D) {
					cAcumulado.setExento(cAcumulado.getExento().add(x.getExento()!=null?x.getExento():BigDecimal.ZERO));
				}

				cAcumulado.setPercepcionIVA(cAcumulado.getPercepcionIVA().add(x.getPercepcionIVA()));
				cAcumulado.setPercepcionIIBB(cAcumulado.getPercepcionIIBB().add(x.getPercepcionIIBB()));
				cAcumulado.setOtrosTributos(cAcumulado.getOtrosTributos().add(x.getOtrosTributos()));
				cAcumulado.setImporteComprobante(cAcumulado.getImporteComprobante().add(x.getImporte()));
			  }	
			}
			}
			
			
			
			
			
%>
 		
	<liferay-ui:error exception="<%= Exception.class %>" message="error-al-grabar" />
	
	<fieldset class="block-labels">
		<legend>Resúmen Conceptos</legend>
	   <table>
	     <tr>
	       <td>Grav. 27%:</td>
	       <td><input type="text" value="<%= formatter.format( cAcumulado.getGravadoIVA27().doubleValue() ) %>" name="<portlet:namespace />total_gravado_27" id="<portlet:namespace />total_gravado_27" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right" /></td>
	     </tr>
	     <tr>
	       <td>Grav. 21%:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getGravadoIVA21().doubleValue()) %>" name="<portlet:namespace />total_gravado_21" id="<portlet:namespace />total_gravado_21" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right"  /></td>
	     </tr>
	     <tr>
	       <td>Grav. 10.5%:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getGravadoIVA105().doubleValue()) %>" name="<portlet:namespace />total_gravado_105" id="<portlet:namespace />total_gravado_105" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right"/></td>
	     </tr>
	     <tr>
	       <td>Exento:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getExento().doubleValue()) %>" name="<portlet:namespace />total_exento" id="<portlet:namespace />total_exento" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly"  style="text-align:right"/></td>
	     </tr>
	     <tr>
	       <td>IVA 27%:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getIva27().doubleValue()) %>" name="<portlet:namespace />total_iva_27" id="<portlet:namespace />total_iva_27" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right"/></td>
	     </tr>
	     <tr>
	       <td>IVA 21%:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getIva21().doubleValue()) %>" name="<portlet:namespace />total_iva_21" id="<portlet:namespace />total_iva_21" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right"/></td>
	     </tr>
	     <tr>
	       <td>IVA 10.5%:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getIva105().doubleValue()) %>" name="<portlet:namespace />total_iva_105" id="<portlet:namespace />total_iva_105" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right"/></td>
	     </tr>
	     
	      <tr>
	       <td>Perc.IVA:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getPercepcionIVA().doubleValue()) %>" name="<portlet:namespace />total_percep_iva" id="<portlet:namespace />total_percep_iva" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right"/></td>
	     </tr>
	     
	     <tr>
	       <td>Perc.IIBB:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getPercepcionIIBB().doubleValue()) %>" name="<portlet:namespace />total_percep_iibb" id="<portlet:namespace />total_percep_iibb" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right"/></td>
	     </tr>
	     <tr>
	       <td>Otros Tributos:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getOtrosTributos().doubleValue()) %>" name="<portlet:namespace />total_otros_tributos" id="<portlet:namespace />total_otros_tributos" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="text-align:right"/></td>
	     </tr>
	     <tr>
	       <td>Total Comprobante:</td>
	       <td><input type="text" value="<%=formatter.format(cAcumulado.getImporteComprobante().doubleValue())%>" name="<portlet:namespace />total_comprobante" id="<portlet:namespace />total_comprobante" onkeydown="allowOnlyDigitsAndDecimals(event)" onchange="agregarCeros(this);"
	       readonly="readonly" style="background:#AEB6BF;text-align:right"/></td>
	     </tr>
	     
	   </table>
	  </fieldset>	
