<%@ include file="/html/portlet/tesoreria/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects/>

<%		
	
 		List<CuentaBancaria> ctas=null;
 		ctas=TraeListasServiceUtil.getCtasBcrias();
 		
%>
		<fieldset class="block-labels">
				<legend><liferay-ui:message key="cheques-pendientes-cobro"/></legend>
				<table class="lfr-table">			
					<tr>
					
                    <td><label><liferay-ui:message key="cta-bcria" />:</label></td>
					  <td>
						<select name="<portlet:namespace/>cta_bancaria_ch" id="<portlet:namespace/>cta_bancaria_ch" onchange="<portlet:namespace />buscaSaldoInicial()">
								<%
								  for (CuentaBancaria ctaBcria : ctas) {
								%>
									<option value="<%= ctaBcria.getId_cuenta_bcria()%>"><%=ctaBcria.getCtaBcriaAsString()%></option>											
								<% }%>
						</select>
					</td>		
					
					<td><label id="<portlet:namespace />saldoinicial_lb">Saldo Inicial</label></td>
					<td>
						<input id="<portlet:namespace />saldoinicial" name="<portlet:namespace />saldoinicial" size="15" maxlength="40" type="text" value="" />
					</td>	
					<td><label>Facturas a Pagar:</label></td>
					<td>
							<input id="<portlet:namespace />facturasapagar" name="<portlet:namespace />facturasapagar" size="15" maxlength="40" type="text" value="" />
					</td>
					</tr>	
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
					<tr>					
						<td>
							<input id="<portlet:namespace />excel" value="<liferay-ui:message key="excel"/>" title="<liferay-ui:message key="excel" />" type="button" onClick="javascript:<portlet:namespace />chequesPendientesCobroExcel();"/>
						</td>
				
					</tr>
					<tr>
						<td colspan="5">&nbsp;</td>
					</tr>
				</table>	      	  
		</fieldset>		
		
<script type="text/javascript">
	
function <portlet:namespace />chequesPendientesCobroExcel(){
	var saldoInicial = jQuery("#<portlet:namespace />saldoinicial").val();
	var saldoInicialLb = jQuery("#<portlet:namespace />saldoinicial_lb").html();
	
	var facturasAPagar = jQuery("#<portlet:namespace />facturasapagar").val();
	var ctaid=jQuery("#<portlet:namespace/>cta_bancaria_ch").val();	
	
	
	var url = '/xlsservlet/?reporte=REPORTE_CHEQUES_PENDIENTES_COBRO';
	
	url += "&saldo=" + saldoInicial;
	url += "&saldolb=" + encodeURI(saldoInicialLb);
	url += "&facturas=" + facturasAPagar;
	url += '&cuenta=' +ctaid;
	url += '&rnd=' + Math.floor(Math.random()*100);

	window.location.href =url;
}

function <portlet:namespace />buscaSaldoInicial(){
	
	var ctaid=jQuery("#<portlet:namespace/>cta_bancaria_ch").val();	
	
	var url = '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"/>&struts_action=/tesoreria/traer_saldo_inicial_cta_bcaria'
	    + '&cuenta=' +ctaid;
	url += '&rnd=' + Math.floor(Math.random()*100);
	
	jQuery.ajax({   
		url: url,
		success: function(data){
			var obj = jQuery.parseJSON(data);
			
			if(obj.fecha != null){
			   jQuery('#<portlet:namespace />saldoinicial_lb').html('Saldo al '+obj.fecha);
			   jQuery('#<portlet:namespace />saldoinicial').val(obj.saldo);
			}
					
		}
	});
	
}


<portlet:namespace />buscaSaldoInicial();

</script>