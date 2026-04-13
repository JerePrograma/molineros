<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>
<%@ page import="ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones" %>


<%@ page import="java.util.Comparator"%>
<portlet:defineObjects/>
<%



    boolean flag = ParamUtil.getBoolean(request, "flagOcultar"); 
    Integer idLote = ParamUtil.getInteger(request, "nrolote");
    if(idLote==null ||idLote==0){
       idLote = (Integer)request.getAttribute("nrolote");
    }   

    Integer opMin=99999999;
    Integer opMax=0;
    
    String conAviso="";
    String sinAviso="";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    List<IntegracionDetalleDS> list= IntegracionServiceUtil.detalleLiquidacionByIdLote(idLote);
    
		


    if(!list.isEmpty()) {
    	
     	
      Collections.sort(list, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((Comparable<Integer>) ((IntegracionDetalleDS) (o1)).getOrdenPago())
						.compareTo(((IntegracionDetalleDS) (o2)).getOrdenPago());
			}
		});
    	
  
      
	  for(IntegracionDetalleDS i:list) {
		  if(i.getOrdenPago()!=null){
			 OrdenPagoOspim op = OrdenPagoServiceUtil.getOrdenPagoOspim(i.getOrdenPago());
			 
			 if(op.getBaja_fecha()==null){ 
		        if(i.getOrdenPago()< opMin && i.getOrdenPago()>0) opMin=i.getOrdenPago();
		        if(i.getOrdenPago()> opMax) opMax=i.getOrdenPago();
		     
		        if(flag == false){
			        if(i.getFechaAvisoTransferencia()==null){
			    	
			           if( !sinAviso.contains(i.getOrdenPago().toString()) ){ 
			    	      sinAviso += i.getOrdenPago()+"<br>";
			           }   
			        }else{
			    	   if(!conAviso.contains(i.getOrdenPago().toString())){ 
			    		 conAviso += i.getOrdenPago()+"  "+ sdf.format(i.getFechaAvisoTransferencia()) + "<br>";
				       }
			        }
		        }else{
		        	 if(i.getFechaExportacionInterbanking()==null){
					    	
				           if( !sinAviso.contains(i.getOrdenPago().toString()) ){ 
				    	      sinAviso += i.getOrdenPago()+"<br>";
				           }   
				        }else{
				    	   if(!conAviso.contains(i.getOrdenPago().toString())){ 
				    		 conAviso += i.getOrdenPago()+"  "+ sdf.format(i.getFechaExportacionInterbanking()) + "<br>";
					       }
				        }
		        }
		     }
		     
		  }   
	   }
    }
   String detalleOP = "Rango de OP del Lote; " +opMin + " al " + opMax;
   

   
%>

<form action="" method="get" name="<portlet:namespace />fm2" enctype="multipart/form-data">

<fieldset class="block-labels">
		<legend>Ordenes de Pago - Aviso de Transferencia</legend>
		
		
<fieldset class="block-labels">		
		    	<table class="lfr-table" border="1">
					<tr>
					   <td colspan="2">
					      <span style="font-size: 12pt; color:blue;"><label id="lbOp"> <%= detalleOP %> </label></span>
					   </td>
				    </tr>
				    <tr>
				      <td>
				          <span style="font-size: 10pt; color:blue;"><label><%=flag == true ? "Procesadas Interbanking" : "Con Aviso" %>  </label></span>
				      </td>
				      
				      <td>
				          <span style="font-size: 10pt; color:blue;"><label>Pendientes </label></span>
				      </td>
				    </tr>
				     <tr>
				      <td><div style="height:100px;overflow:auto;">
				          <%=conAviso%>
				          </div>	
				      </td>
				      
				      <td> <div style="height:100px;overflow:auto;">
				          <%=sinAviso%>
				          </div>
				      </td>
				    </tr>
				    
				</table>
</fieldset>
<br>
<fieldset class="block-labels">		
   <legend>Generar Aviso</legend>		    			
		<table class="lfr-table">
		   	<tr>
				<td><label>Desde: </label></td>
				<td><input id="<portlet:namespace />op_desde" name="<portlet:namespace />op_desde" size="8" maxlength="15" type="text" value="<%=opMin%>" /></td>
				<td><label>Hasta:</label></td>
				<td colspan="2"><input id="<portlet:namespace />op_hasta" name="<portlet:namespace />op_hasta" size="8" maxlength="15" type="text" value="<%=opMax%>" /></td>												
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
<br>
 <%if (flag == false){ %>
	  <input id="<portlet:namespace />generarAviso" value="Generar" title="Generar Aviso Ordenes Pago" type="button" onClick="javascript:generarAvisoIntegracion();"/>
   <%}else{ %>
	   <input id="<portlet:namespace />generarAviso" value="Generar" title="Aviso Archivo de Ordenes Pago" type="button" onClick="javascript:exportarCuentasInterbanking();"/>
 
<%} %>
		
<input id="<portlet:namespace />idLote" name="<portlet:namespace />idLote" type="hidden" value="<%= idLote %>" />	

</form>
<script type="text/javascript">
	var popup;
	jQuery('#<portlet:namespace />buscandoIMP').hide();
	
	function generarAvisoIntegracion(){
		var op_desde=	jQuery("#<portlet:namespace/>op_desde").val();
		var op_hasta=	jQuery("#<portlet:namespace/>op_hasta").val();
		var idLote =	jQuery("#<portlet:namespace/>idLote").val();
		jQuery('#<portlet:namespace />buscandoIMP').show();
		var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
	   	url = url+'&<%= Constants.CMD %>'+'='+'genera_aviso_transferencia_lote'+'&op_desde='+op_desde + '&op_hasta='+op_hasta+'&id_lote='+idLote;
	   	
	   	
	   	popup.load(url,null, function(){
			jQuery('#<portlet:namespace />buscandoIMP').hide();

	    });	
	   	
	}

	

	jQuery("#<portlet:namespace />buscando").hide();

	function exportarCuentasInterbanking() {
		var opDesde= jQuery("#<portlet:namespace/>op_desde").val();
		var opHasta= jQuery("#<portlet:namespace/>op_hasta").val();
	
		window.location.href ='/txtservlet/?reporte=EXPORTAR_CUENTAS_INTERBANKING'
			+'&op_desde='+opDesde
			+'&op_hasta='+opHasta;
		 Liferay.Popup.close(popup);
		 /* popup.close(); */

	}
	
	
</script>
