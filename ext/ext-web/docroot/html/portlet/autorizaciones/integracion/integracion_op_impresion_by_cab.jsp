<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<portlet:defineObjects/>
<%
 
    Integer idLote = ParamUtil.getInteger(request, "nrolote");

    Integer opMin=99999999;
    Integer opMax=0;
    List<IntegracionDetalleDS> list=IntegracionServiceUtil.detalleLiquidacionByIdCab(idLote);
    if(!list.isEmpty()) {
    	 Collections.sort(list,
    	 new Comparator<IntegracionDetalleDS>() {
    		    @Override
    		    public int compare(IntegracionDetalleDS o1, IntegracionDetalleDS o2) {
    		        return o1.getOrdenPago().compareTo(o2.getOrdenPago());
    		    }
    		}
    			 );
    	 
    	 for(IntegracionDetalleDS d:list){
    		 opMin=d.getOrdenPago();
    		 if(opMin>0) break;
    	 }
    	 
    	 //opMin=list.get(0).getOrdenPago();
    	 opMax=list.get(list.size()-1).getOrdenPago();
    	 
/*    	
	  for(IntegracionDetalleDS i:list) {
		  if(i.getOrdenPago()!=null){
		     if(i.getOrdenPago()< opMin && i.getOrdenPago()>0) opMin=i.getOrdenPago();
		     if(i.getOrdenPago()> opMax) opMax=i.getOrdenPago();
		  }   
	   }
*/
    }
   
%>
<fieldset class="block-labels">
		<legend>Impresión de Ordenes de Pago</legend>			
				<table class="lfr-table">
					<tr>
						<td><label>Desde: </label></td>
						<td><input id="<portlet:namespace />op_desde" name="<portlet:namespace />op_desde" size="8" maxlength="8" type="text" value="<%=opMin%>" /></td>
						<td><label>Hasta:</label></td>
						<td colspan="2"><input id="<portlet:namespace />op_hasta" name="<portlet:namespace />op_hasta" size="8" maxlength="8" type="text" value="<%=opMax%>" /></td>												
					</tr>									
				</table>				
				<div align="center" id="<portlet:namespace />buscandoIMP">
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
			
<div align="center" id="<portlet:namespace />busquedaOPDiv"></div>

<input id="<portlet:namespace />imprimirOP" value="<liferay-ui:message key="imprimir"/>" title="Imprimir Ordenes Pago" type="button" onClick="javascript:imprimirOPIntegracion();"/>
	
<script type="text/javascript">
	var popupAfill;
	jQuery('#<portlet:namespace />buscandoIMP').hide();
	
		

	function imprimirOPIntegracion() {
		var op_desde=	jQuery("#<portlet:namespace/>op_desde").val();
		var op_hasta=	jQuery("#<portlet:namespace/>op_hasta").val();
		
		window.location.href ='/pdfservlet/?accion=ordenPagoOspimIntegracion'
			+'&op_desde='+op_desde
			+'&op_hasta='+op_hasta;			
		
		
		jQuery('#<portlet:namespace />buscandoIMP').hide();	
	}
	
</script>
