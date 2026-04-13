<%@ include file="/html/portlet/liquidaciones/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS" %>
<%@ page import="ar.com.ospim.autorizaciones.services.IntegracionServiceUtil" %>

<%@ page import="java.util.Comparator"%>
<portlet:defineObjects/>
<%
 
    Integer idLote = ParamUtil.getInteger(request, "nrolote");
    if(idLote==null ||idLote==0){
       idLote = (Integer)request.getAttribute("nrolote");
    }   

    Integer opMin=99999999;
    Integer opMax=0;
    
    List<IntegracionDetalleDS> sinRecibo = new ArrayList<IntegracionDetalleDS>();
    String sinReciboStr="";
    String conReciboStr="";
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    List<IntegracionDetalleDS> list=IntegracionServiceUtil.detalleLiquidacionByIdLote(idLote);
    
    
    if(!list.isEmpty()) {
    	
     	
      Collections.sort(list, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((Comparable<Integer>) ((IntegracionDetalleDS) (o1)).getOrdenPago())
						.compareTo(((IntegracionDetalleDS) (o2)).getOrdenPago());
			}
		});
    	
      
	  for(IntegracionDetalleDS i:list) {
		  if(i.getOrdenPago()!=null){
		     if(i.getOrdenPago()< opMin && i.getOrdenPago()>0) opMin=i.getOrdenPago();
		     if(i.getOrdenPago()> opMax) opMax=i.getOrdenPago();
		     
		     IntegracionDetalleDS item = new IntegracionDetalleDS();
		     
		     if(i.getNroRecibo() ==null || "".equalsIgnoreCase(i.getNroRecibo())){
		    	if(!sinReciboStr.contains(i.getOrdenPago().toString())){
		    		sinRecibo.add(i);
		    		sinReciboStr += i.getOrdenPago()+"<br>";
		    	}
		    	 
		     }else{
		    	 
		    	 if(!conReciboStr.contains(i.getOrdenPago().toString())){ 
		    		 conReciboStr += i.getOrdenPago()+"  "+ i.getNroRecibo() + "<br>";
			     }
		     }
		  }   
	   }
    }
   String detalleOP = "Rango de OP del Lote; " +opMin + " al " + opMax;
   
%>
<fieldset class="block-labels">
		<legend>Ordenes de Pago - Ingreso de Recibos</legend>
		
		
<fieldset class="block-labels">		
		    	<table class="lfr-table" border="1">
					<tr>
					   <td colspan="2">
					      <span style="font-size: 12pt; color:blue;"><label id="lbOp"> <%= detalleOP %> </label></span>
					   </td>
				    </tr>
				    <tr>
				      <td>
				          <span style="font-size: 10pt; color:blue;"><label>Con Recibo </label></span>
				      </td>
				      
				      <td>
				          <span style="font-size: 10pt; color:blue;"><label>Pendientes </label></span>
				      </td>
				    </tr>
				     <tr>
				      <td><div style="height:100px;overflow:auto;">
				            <%=conReciboStr %>
				          </div>	
				      </td>
				      
				      <td> <div style="height:100px;overflow:auto;">
				            <%=sinReciboStr%>
				          </div>
				      </td>
				    </tr>
				    
				</table>
</fieldset>
<br>
<fieldset class="block-labels">		
   <legend>Recibo</legend>		    			
		<table class="lfr-table">
		   	<tr>
		   	   <td><label>OP sin Recibo: </label></td>
		   	   <td>
		   	      
 			         <select  id="<portlet:namespace/>op_desde">
                             <%for (IntegracionDetalleDS e : sinRecibo){ %>
                              <option value="<%=e.getOrdenPago() %>"><%=e.getOrdenPago() %></option>
                             <%} %>
                     </select>
                </td>          
		   	
		   	
				<td><label>Nro Recibo:</label></td>
				<td colspan="2"><input id="<portlet:namespace />nro_recibo" name="<portlet:namespace />nro_recibo" size="15" maxlength="15" type="text" value="" /></td>												
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
<input id="<portlet:namespace />generarRecibo" value="Asociar" title="Asociar Recibo" type="button" onClick="javascript:asociarReciboIntegracion();"/>
<input id="<portlet:namespace />idLote" name="<portlet:namespace />idLote" type="hidden" value="<%= idLote %>" />	
<script type="text/javascript">
	var popup;
	jQuery('#<portlet:namespace />buscandoIMP').hide();
	
	function asociarReciboIntegracion(){
		var op_desde=	jQuery("#<portlet:namespace/>op_desde").val();
		var idLote =	jQuery("#<portlet:namespace/>idLote").val();
		var nroRecibo=	jQuery("#<portlet:namespace/>nro_recibo").val();
		if(nroRecibo!=null && nroRecibo!=""){
		  jQuery('#<portlet:namespace />buscandoIMP').show();
		  var url = '<portlet:renderURL windowState="<%=LiferayWindowState.EXCLUSIVE.toString()%>"/>&struts_action=/autorizaciones/integracion_editar';
	   	  url = url+'&<%= Constants.CMD %>'+'='+'asociar_recibo_integracion'+'&op_desde='+op_desde +'&id_lote='+idLote+'&nro_recibo='+nroRecibo;
	   	  popup.load(url,null, function(){
			jQuery('#<portlet:namespace />buscandoIMP').hide();

	      });
		}else{
			alert('Debe ingresar un número de recibo')
		}  
	   	
	}

	
</script>
