<%@ include file="/html/portlet/autorizaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<portlet:defineObjects/>
<%
 
    Integer idLote = ParamUtil.getInteger(request, "nrolote");
    List<TercerizadoraServicio> tercServList=TraeListasServiceUtil.getTercerizadoraServicio();
    String tercValidas = TraeListasServiceUtil.getSystemConfig("INTEGRACION_TERCERIZADORAS");
   
%>
<fieldset class="block-labels">
		<legend>Impresión Detalle Liquidación</legend>			
				<table class="lfr-table">
					<tr>
						<td><label>Lote: </label></td>
						<td><input id="<portlet:namespace />idLote" name="<portlet:namespace />idLote" size="8" maxlength="8" type="text" value="<%=idLote%>" readonly="readonly" /></td>
						
						<td>
						  <liferay-ui:message key="tercerizadora-servicio" />
					   </td>
					   
					   <td>
					   <select name="<portlet:namespace />tercerizadoraLiq"
						id="<portlet:namespace />tercerizadoraLiq" onchange="">
						    <option value="0" selected="selected">Seleccione una tercerizadora</option>
							<%for(int i = 0; i < WebKeysAutorizaciones.INTEGRACION_ENTIDADES.length; i++ ) {%>
							<option
								value="<%=WebKeysAutorizaciones.INTEGRACION_ENTIDADES[i][0] %>">
								<%=WebKeysAutorizaciones.INTEGRACION_ENTIDADES[i][1] %>
							</option>
							<% } %>
			           </select>
					   </td>
					   
					</tr>									
				</table>
								
				<div align="center" id="<portlet:namespace />buscandoIMPDet">
					<table style="align:center;">
						<tr>
							<td><liferay-ui:message key='buscando'/></td>
							<td align="center">					
								<img alt="<liferay-ui:message key='buscando'/>" src="<%= themeDisplay.getPathThemeImages() %>/progress_bar/loading_animation.gif" />
							</td>
						</tr>
					</table>		
				</div>
</fieldset>								
			


<input id="<portlet:namespace />imprimirDetalleLiquidacion" value="<liferay-ui:message key="imprimir"/>" title="Imprimir Detalle Liquidación" type="button" onClick="javascript:imprimirDetalleLiquidacionIntegracion();"/>
	
<script type="text/javascript">
jQuery('#<portlet:namespace />buscandoIMPDet').hide();

function imprimirDetalleLiquidacionIntegracion() {
	var id=	jQuery("#<portlet:namespace/>idLote").val();
	var tercerizadora=jQuery("#<portlet:namespace/>tercerizadoraLiq").val();
	window.location.href ='/xlsservlet/?reporte=REPORTE_DETALLE_LIQUIDACION_INTEGRACION&id='+id+
	'&tercerizadora=' + tercerizadora +		
    '&rnd=' + Math.floor(Math.random()*100);
	
	jQuery('#<portlet:namespace />buscandoIMPDet').hide();	
}


</script>
